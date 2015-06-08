
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.XmlParserHelper;

public class SizeAnimationElement extends AnimationElement {
    public final static String TAG = "SizeAnimation";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_SIZE_ANIMATION.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new SizeAnimationElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        SizeKeyFrame sizeKeyFrame = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_SIZE_ANIMATION_SIZE)) {
                    sizeKeyFrame = new SizeKeyFrame();
                    XmlParserHelper.setElementAttr(parser, sizeKeyFrame);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return sizeKeyFrame;
    }

    @Override
    public Animation generateAnimations(VisibleElement element) {
        LockSizeAnimation lockSizeAnimation = null;
        // Logger.v(TAG, "Size Generate Animation!");

        int count = mKeyFrames.size();
        if (count > 0 && element != null) {
            // if the first key frame's time is zero, add the current state as
            // the first key frame
            int index = 0;
            SizeKeyFrame keyFrame = (SizeKeyFrame) mKeyFrames.get(index);
            if (keyFrame.getTime() != 0) {
                SizeKeyFrame sizeKeyFrame = new SizeKeyFrame();
                sizeKeyFrame.setTime(0);
                // Logger.v(TAG,
                // "start w="+element.getView().getLayoutParams().width+", h="+element.getView().getLayoutParams().height);
                sizeKeyFrame.setW(element.getW()); // TODO
                sizeKeyFrame.setH(element.getH());
                mKeyFrames.add(0, sizeKeyFrame);
            }

            lockSizeAnimation = new LockSizeAnimation();
            lockSizeAnimation.setWidth(element.getView().getLayoutParams().width);
            lockSizeAnimation.setHeight(element.getView().getLayoutParams().height);

            setStartTime(0);
            long endTime = 0;
            for (int i = mKeyFrames.size() - 1; i >= 0; i--) {
                if (mKeyFrames.get(i).getTime() < 100000) {
                    endTime = mKeyFrames.get(i).getTime();
                    if (i == mKeyFrames.size() - 1) {
                        lockSizeAnimation.setRepeatCount(Animation.INFINITE);
                        lockSizeAnimation.setRepeatMode(Animation.RESTART);
                    }
                    break;
                } else {
                    // this is a once animation
                    // Logger.v(TAG, "This is a once animation!");
                    lockSizeAnimation.setFillAfter(true);
                }
            }
            setEndTime(endTime);

            // Logger.v(TAG,
            // "  generate size animation startTime="+getStartTime()+", endTime="+getEndTime());

            lockSizeAnimation.setDuration(getEndTime() - getStartTime());

            if (element.getAlign() == VisibleElement.ALIGN_CENTER) {
                lockSizeAnimation.setPivotX(0.5f);
            } else if (element.getAlign() == VisibleElement.ALIGN_RIGHT) {
                lockSizeAnimation.setPivotX(1.0f);
            }

            if (element.getAlignV() == VisibleElement.ALIGN_CENTER) {
                lockSizeAnimation.setPivotY(0.5f);
            } else if (element.getAlignV() == VisibleElement.ALIGN_BOTTOM) {
                lockSizeAnimation.setPivotY(1.0f);
            }
        }

        return lockSizeAnimation;
    }

    public static class SizeKeyFrame extends BaseKeyFrame {
        private int mW = 0;
        private int mH = 0;

        public int getW() {
            return mW;
        }

        public void setW(int w) {
            // jianglin:scale
            w *= FileUtil.X_SCALE;

            this.mW = w;
        }

        public void setW(String w) {
            setW(Integer.valueOf(w));
        }

        public int getH() {
            return mH;
        }

        public void setH(int h) {
            // jianglin:scale
            h *= FileUtil.Y_SCALE;

            this.mH = h;
        }

        public void setH(String h) {
            setH(Integer.valueOf(h));
        }
    }

    public class LockSizeAnimation extends Animation {
        private int mCenterX = 0;
        private int mCenterY = 0;
        private int mWidth;
        private int mHeight;
        private int mCurrentStage = -1;

        public int getWidth() {
            return mWidth;
        }

        public void setWidth(int width) {
            this.mWidth = width;
        }

        public int getHeight() {
            return mHeight;
        }

        public void setHeight(int height) {
            this.mHeight = height;
        }

        public int getCenterX() {
            return mCenterX;
        }

        public void setCenterX(int centerX) {
            this.mCenterX = centerX;
        }

        public int getCenterY() {
            return mCenterY;
        }

        public void setCenterY(int centerY) {
            this.mCenterY = centerY;
        }

        /*
         * 0.0f -- 1.0f or more
         */
        private float mPivotXValue = 0.0f;
        private float mPivotX = 0.0f;

        public void setPivotX(float pivotX) {
            mPivotXValue = pivotX;
        }

        /*
         * 0.0f -- 1.0f or more
         */
        private float mPivotYValue = 0.0f;
        private float mPivotY = 0.0f;

        public void setPivotY(float pivotY) {
            mPivotYValue = pivotY;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float time = interpolatedTime * getEndTime();
            int i;
            // find the animation stage

            if (mCurrentStage == -1) {
                mCurrentStage = 1;
            }
            if (time <= mKeyFrames.get(mCurrentStage).getTime()) {
                for (i = mCurrentStage; i > 1; i--) {
                    if (time > mKeyFrames.get(i - 1).getTime()) {
                        break;
                    }
                }
            } else {
                for (i = mCurrentStage + 1; i < mKeyFrames.size(); i++) {
                    if (time <= mKeyFrames.get(i).getTime()) {
                        break;
                    }
                }
            }

            if (mCurrentStage != i) {
                mCurrentStage = i;
            }
            SizeKeyFrame sizeKeyFrame = (SizeKeyFrame) mKeyFrames.get(i - 1);

            int fromW = sizeKeyFrame.getW();
            int fromH = sizeKeyFrame.getH();

            sizeKeyFrame = (SizeKeyFrame) mKeyFrames.get(i);

            float factor = (time - mKeyFrames.get(i - 1).getTime())
                    / (mKeyFrames.get(i).getTime() - mKeyFrames.get(i - 1).getTime());

            int toW = (int) (fromW + (sizeKeyFrame.getW() - fromW) * factor);
            int toH = (int) (fromH + (sizeKeyFrame.getH() - fromH) * factor);

            float scaleW = ((float) toW) / mWidth;
            float scaleH = ((float) toH) / mHeight;

            if (Constants.DBG_ANIMATION) {
                Logger.i(TAG, "Time=" + time + ", fromW=" + fromW + ", toW="
                        + toW + ", fromH=" + fromH + ", toH="
                        + toH + ", factor=" + factor + ",pivotx=" + mPivotX + ",pivoty=" + mPivotY);
            }
            t.getMatrix().setScale(scaleW, scaleH, mPivotX, mPivotY);
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            mPivotX = width * mPivotXValue;
            mPivotY = height * mPivotYValue;
        }
    }
}
