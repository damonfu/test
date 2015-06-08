
package com.baidu.themeanimation.element;

import java.util.ArrayList;

import com.baidu.themeanimation.util.Constants;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;

public class AnimatorElement {
    PropertyValuesHolder[] mPvhs;
    private long mDuration;
    private int mRepateMode;
    private int mRepateCount;
    private int mCurrentPvhCount;
    private int mType;
    private ValueAnimator mAnimator;

    public AnimatorElement() {
        mRepateMode = ValueAnimator.RESTART;
        mRepateCount = ValueAnimator.INFINITE;
        mCurrentPvhCount = 0;
    }

    public void initType(final int type) {
        mType = type;
    }

    public int getCurrentPvhCount() {
        return mCurrentPvhCount;
    }

    public void setRepateCount(final int repateCount) {
        this.mRepateCount = repateCount;
        if(mAnimator!=null){
            mAnimator.setRepeatCount(repateCount);
        }
    }

    public void setRepateCount(final String repateCount) throws NumberFormatException{
        setRepateCount(Integer.valueOf(repateCount));
    }

    public void setDuration(final long duration) {
        if(duration > Constants.MAX_DURATION){
            this.mDuration = (long) Constants.MAX_DURATION;
            mRepateMode = ValueAnimator.RESTART;
            mRepateCount = 0;
       }else {
           this.mDuration = duration;
       }
        if(mAnimator!=null){
            mAnimator.setDuration(this.mDuration);
        }
    }

    public void setDuration(final String duration) throws NumberFormatException{
        setDuration(Long.valueOf(duration));
    }

    // RESTART = 1, REVERSE = 2, INFINITE = -1;
    public void setRepeatMode(final int repateMode) {
        this.mRepateMode = repateMode;
        if(mAnimator!=null){
            mAnimator.setRepeatMode(repateMode);
        }
    }

    public void setRepeatMode(final String repateMode) throws NumberFormatException{
        setRepeatMode(Integer.valueOf(repateMode));
    }

    public void setInterpolator(final int interpolator) {
        // this.mInterpolotor = interpolator;
    }
    
    public void setInterpolator(final String interpolator) {
        // this.mInterpolotor = interpolator;
    }

    public void addKeyframes(final String propertyName, final ArrayList<Keyframe> keyframes) {
        if (mPvhs == null) {
            if (mType == 1) {
                mPvhs = new PropertyValuesHolder[1];
            } else if (mType == 2) {
                mPvhs = new PropertyValuesHolder[2];
            }
        }
        formatKeyframeTime(keyframes);
        final Keyframe[] ks = new Keyframe[keyframes.size()];
        keyframes.toArray(ks);
        mPvhs[mCurrentPvhCount] = PropertyValuesHolder.ofKeyframe(propertyName, ks);
        mCurrentPvhCount += 1;
    }

    public Animator generateAnimator(final Object target) {
        if (mPvhs != null) {
            mAnimator = ObjectAnimator.ofPropertyValuesHolder(target, mPvhs);
            mAnimator.setDuration(mDuration);
            mAnimator.setRepeatCount(mRepateCount);
            mAnimator.setRepeatMode(mRepateMode);
            return mAnimator;
        }
        return null;
    }

    public final void formatKeyframeTime(final ArrayList<Keyframe> keyframes) {
        if (keyframes != null) {
            final float total_time = keyframes.get(keyframes.size() - 1).getFraction();
            setDuration((long)total_time);
            if (total_time != 0) {
                for (int i = 0; i < keyframes.size(); i++) {
                    keyframes.get(i).setFraction(keyframes.get(i).getFraction() / total_time);
                }
            }
        }
    }
}
