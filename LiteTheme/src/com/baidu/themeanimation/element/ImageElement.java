
package com.baidu.themeanimation.element;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Logger;

public class ImageElement extends VisibleElement {
    private String mSrc;// a image file name
    private int mSrcid; // must >= 0, if less than 0, this one is useless
    private Boolean mAntiAlias; // when turn anti-alias feature for this image
                                // when rotate the image element
    protected ImageElementView mImageElementView;

    public ImageElement() {
        super();
        setSrc(null);
        setSrcid(0);
        setAntiAlias(false);
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_IMAGE.equals(tagName)
                || Constants.TAG_IMAGE_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new ImageElement();
    }

    public String getSrc() {
        return mSrc;
    }

    public void setSrc(String src) {
        this.mSrc = src;
    }

    public int getSrcid() {
        return mSrcid;
    }

    public void setSrcid(int srcid) {
        if (mSrcid != srcid) {
            mSrcid = srcid;
            if (mImageElementView != null) {
                mImageElementView.updateSrc();
            }
        }
    }

    public void setSrcid(String srcid) {
        if (srcid != null) {
            setSrcid(Integer.valueOf(srcid));
        }
    }

    public Boolean getAntiAlias() {
        return mAntiAlias;
    }

    public void setAntiAlias(Boolean antiAlias) {
        this.mAntiAlias = antiAlias;
    }

    public void setAntiAlias(String antiAlias) {
        setAntiAlias(antiAlias.equals("true"));
    }

    public void clearView() {
        super.clearView();
        if (mImageElementView != null) {
            ViewGroup viewGroup = (ViewGroup) mImageElementView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mImageElementView);
            }
            // mView.clearAnimation();
            mImageElementView = null;
        }
    }

    public View getImageElementView() {
        return mImageElementView;
    }

    @Override
    public View generateView(Context context, Handler handler) {
        if (mImageElementView == null) {
            mImageElementView = new ImageElementView(context, this, Constants.IMAGE_TYPE_COMMON);
        }

        setView(mImageElementView);
        return mImageElementView;
    }

    public class ImageElementView extends View {
        private Bitmap mCurrentBitmap = null;
        private Bitmap mMaskBitmap = null;
        private Paint mMaskPaint = null;
        private Matrix mMatrix = new Matrix();
        private int mType;
        private PorterDuffXfermode pdm;

        public ImageElementView(Context context, VisibleElement element, int type) {
            super(context);
            mType = type;
            pdm = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        }

        public void updateImage() {
            updateSrc();
            if (getMask() != null) {
                mMaskPaint = new Paint();
                String filename = getMask().getSrc();
                 //should load in ansytask
                FileUtil.getInstance().setBitmap(filename, this, true);
            }
        }

        public void setMask(Bitmap bmp){
            mMaskBitmap = bmp;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (mType == Constants.IMAGE_TYPE_COMMON) {
                updateImage();
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mCurrentBitmap == null) {
                return;
            }
            try {
                canvas.save();

                int width = mCurrentBitmap.getWidth();
                int height = mCurrentBitmap.getHeight();

                if (getAngle() != 0) {
                    canvas.rotate(getAngle(), getCenterX(), getCenterY());
                } else if (getAngleY() == 180) {
                    canvas.translate(width, 0);
                    canvas.scale(-1, 1);
                }

                if (getMask() != null) {
                    mMaskPaint.setFilterBitmap(false);
                    mMaskPaint.setAlpha(ImageElement.this.getAlpha());
                    mMaskPaint.setAntiAlias(true);
                    // Logger.v("mask1", "alpha="+getAlpha());

                    int x = 0;
                    int y = 0;
                    // draw the src/dst example into our offscreen bitmap
                    int sc = canvas.saveLayer(x, y, x + width, y + height, null,
                            Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
                                    | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                                    | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                                    | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

                    canvas.drawBitmap(mCurrentBitmap, 0, 0, mMaskPaint);
                    // DST_ATOP can work well in most condition
                    mMaskPaint.setXfermode(pdm);
                    // draw the mask

                    // TODO: rotate the mask
                    canvas.rotate(getMask().getAngle(), getMask().getCenterX(), getMask()
                            .getCenterY());
                    // Logger.v("mask", "mask angle:"+getMask().getAngle());

                    if (getMask().getAlign() == MaskElement.RELATIVE) {
                        canvas.drawBitmap(mMaskBitmap, getMask().getX(), getMask().getY(),
                                mMaskPaint);
                    } else {
                        float values[] = new float[9];
                        canvas.getMatrix().getValues(values);
                        int sX = ImageElement.this.getX();
                        int sY = ImageElement.this.getY();
                        canvas.drawBitmap(mMaskBitmap, getMask().getX() - sX,
                                getMask().getY() - sY, mMaskPaint);
                    }
                    mMaskPaint.setXfermode(null);
                    canvas.restoreToCount(sc);
                } else {
                    if (mCurrentBitmap != null) {
                        if (hasW() && hasH()) {
                            canvas.concat(mMatrix);
                        }
                        canvas.drawBitmap(mCurrentBitmap, 0, 0, mPaint);
                    }
                }

                if (getAngleY() == 180) {
                    canvas.scale(-1, 1);
                }

                canvas.restore();// current it is useless
            } catch (Exception e) {
                Logger.w("exception-image", "draw image" + getSrc() + " exception:" + e.toString());
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (mCurrentBitmap == null && mMaskBitmap == null) {
                super.onMeasure(0, 0);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        public Bitmap getCurrentBitmap() {
            return mCurrentBitmap;
        }

        public void setImage(Bitmap bitmap) {
            mCurrentBitmap = bitmap;
            if (mCurrentBitmap != null) {
                if (!hasW() || !hasH()) {
                    setRealW(mCurrentBitmap.getWidth());
                    setRealH(mCurrentBitmap.getHeight());
                }
                float mScaleX = ((float) getW()) / mCurrentBitmap.getWidth();
                float mScaleY = ((float) getH()) / mCurrentBitmap.getHeight();
                mMatrix.setScale(mScaleX, mScaleY);
                LayoutParams layoutParams = genLayoutParams();
                if (getAngleY() == 180) {
                    int posX = ImageElement.this.getX();
                    layoutParams.leftMargin = FileUtil.REAL_SCREEN_WIDTH - posX - getW();
                }
                setLayoutParams(layoutParams);
            }
        }

        Boolean debBoolean = false;

        public void updateSrc() {
            String filename = getSrc();

            if (filename != null && getSrcid() > 0) {
                int dotIndex = filename.lastIndexOf('.');
                if (dotIndex > 0) {
                    filename = filename.substring(0, dotIndex) + "_" + getSrcid()
                            + filename.substring(dotIndex);
                }
            }

            if (filename != null && filename.equals("unlocker_button.png")) {
                debBoolean = true;
            }

            FileUtil.getInstance().setBitmap(filename, this, false);
        }
    }
}
