
package com.baidu.themeanimation.element;

import java.util.ArrayList;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.view.AnimationViewFactory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class AnimatorDrawableElement {
    private ArrayList<AnimatorDrawableKeyframe> mKeyframes;
    private int mCurrentPvhCount;
    private boolean oneShot;
    private SourceAnimation mAniDrawable;
    private int timeType;

    public boolean isOneShot() {
        return oneShot;
    }

    public void setOneShot(boolean _oneshot) {
        this.oneShot = _oneshot;
    }

    public void setOneShot(String _oneshot) {
        if (_oneshot.equalsIgnoreCase("true")) {
            this.oneShot = true;
        } else {
            this.oneShot = false;
        }
    }

    public AnimatorDrawableElement() {
        mCurrentPvhCount = 0;
        oneShot = false;
        timeType = 1;
    }

    public void setTimeType(String type) {
        setTimeType(Integer.valueOf(type));
    }

    public void setTimeType(int type) {
        this.timeType = type;
    }

    public SourceAnimation getAnimationDrawable() {
        return mAniDrawable;
    }

    public void addKeyframes(final String src, int Fraction) {
        if (mKeyframes == null) {
            mKeyframes = new ArrayList<AnimatorDrawableKeyframe>();
        }
        AnimatorDrawableKeyframe keyframe = new AnimatorDrawableKeyframe(src, Fraction);
        mKeyframes.add(keyframe);
        mCurrentPvhCount += 1;
    }

    public SourceAnimation generateAnimatorDrawable(final View target) {
        if (mKeyframes != null) {
            mAniDrawable = new SourceAnimation();
            Bitmap bitmap;
            Drawable drawable;
            formatKeyframeTime(mKeyframes);
            Resources res = target.getContext().getResources();
            for (int i = 0; i < mKeyframes.size(); i++) {
                bitmap = FileUtil.getInstance().getElementBitmap(
                        mKeyframes.get(i).getSrc());

                drawable = new BitmapDrawable(res, bitmap);
                mAniDrawable.addFrame(drawable, mKeyframes.get(i).getFraction());
                bitmap = null;
            }
            res = null;
            mAniDrawable.setOneShot(oneShot);
            return mAniDrawable;
        }
        return null;
    }

    public final void formatKeyframeTime(final ArrayList<AnimatorDrawableKeyframe> keyframes) {
        if (keyframes != null) {
            final int total_time = keyframes.get(keyframes.size() - 1).getFraction();
            if (total_time > Constants.MAX_DURATION) {
                oneShot = true;
            }
            if (timeType == 1 && total_time != 0) {
                for (int i = 1; i < keyframes.size(); i++) {
                    keyframes.get(i).setFraction(
                            keyframes.get(i).getFraction() - keyframes.get(i - 1).getFraction());
                }
            }
        }
    }

    public void startAnimations() {
        if (mAniDrawable != null) {
            mAniDrawable.start();
        }
    }

    public void stopAnimations() {
        if (mAniDrawable != null) {
            mAniDrawable.stop();
        }
    }

    public class AnimatorDrawableKeyframe {
        String src;
        int Fraction;

        public AnimatorDrawableKeyframe(String _src, int _Fraction) {
            src = _src;
            Fraction = _Fraction;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public int getFraction() {
            return Fraction;
        }

        public void setFraction(int Fraction) {
            this.Fraction = Fraction;
        }
    }

    public class SourceAnimation extends AnimationDrawable {

        AnimationViewFactory.AnimationListener mAnimatorListener;
        boolean mIsFinish = false;
        int mSelectNums = 0;

        public void setAnimatorListener(AnimationViewFactory.AnimationListener animatorListener) {
            mAnimatorListener = animatorListener;
        }

        @Override
        public boolean selectDrawable(int idx) {
            boolean ret = super.selectDrawable(idx);

            if (mAnimatorListener != null) {
                if (mSelectNums == 0) {
                    mAnimatorListener.onAnimationStart();
                } else if (mSelectNums % getNumberOfFrames() == 0) {
                    mAnimatorListener.onAnimationRepeat();
                }
                mSelectNums++;
                if (idx != 0 && idx == getNumberOfFrames() - 1) {
                    mIsFinish = true;
                    mAnimatorListener.onAnimationEnd();
                }
            }
            return ret;
        }
    }

    public int getAnimationStatus() {
        if (mAniDrawable != null && mAniDrawable.isRunning()) {
            return Constants.Animation_Status_Running;
        }
        return Constants.Animation_Status_Stop;
    }

    public void setAnimatorListener(AnimationViewFactory.AnimationListener listener) {
        if (mAniDrawable != null) {
            mAniDrawable.setAnimatorListener(listener);
        }
    }
}
