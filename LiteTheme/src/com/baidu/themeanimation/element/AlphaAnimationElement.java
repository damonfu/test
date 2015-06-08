
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.XmlParserHelper;

public class AlphaAnimationElement extends AnimationElement {

    public final static String TAG = "AlphaAnimationElement";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_ALPHA_ANIMATION.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new AlphaAnimationElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        AlphaKeyFrame alphaKeyFrame = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_ALPHA_ANIMATION_ALPHA)) {
                    alphaKeyFrame = new AlphaKeyFrame();
                    XmlParserHelper.setElementAttr(parser, alphaKeyFrame);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return alphaKeyFrame;
    }

    @Override
    public Animation generateAnimations(VisibleElement element) {
        LockAlphaAnimation lockAlphaAnimation = null;
        // Logger.v(TAG, "Alpha Generate Animation!");

        int count = mKeyFrames.size();
        if (count > 0 && element != null) {
            // if the first key frame's time is zero, add the first frame state
            // as the start value
            AlphaKeyFrame keyFrame = (AlphaKeyFrame) mKeyFrames.get(0);
            if (keyFrame.getTime() != 0) {
                AlphaKeyFrame alphaKeyFrame = new AlphaKeyFrame();
                alphaKeyFrame.setTime(0);
                alphaKeyFrame.setA(255);
                mKeyFrames.add(0, alphaKeyFrame);
            }

            lockAlphaAnimation = new LockAlphaAnimation(element);

            setStartTime(0);
            long endTime = 0;
            for (int i = mKeyFrames.size() - 1; i >= 0; i--) {
                if (mKeyFrames.get(i).getTime() < 100000) {
                    endTime = mKeyFrames.get(i).getTime();
                    if (i == mKeyFrames.size() - 1) {
                        lockAlphaAnimation.setRepeatCount(Animation.INFINITE);
                        lockAlphaAnimation.setRepeatMode(Animation.RESTART);
                    }
                    break;
                } else {
                    // this is a once animation
                    // Logger.v(TAG, "This is a once animation!");
                    lockAlphaAnimation.setFillAfter(true);
                }
            }
            setEndTime(endTime);

            // Logger.v(TAG,
            // "  generate alpha animation startTime="+getStartTime()+", endTime="+getEndTime());

            lockAlphaAnimation.setDuration(getEndTime() - getStartTime());
        }
        return lockAlphaAnimation;
    }

    public static class AlphaKeyFrame extends BaseKeyFrame {
        private float mA;

        public float getA() {
            return mA;
        }

        public void setA(float a) {
            this.mA = a / 255.0f;
        }

        public void setA(String a) {
            setA(Float.valueOf(a));
        }

    }

    public class LockAlphaAnimation extends Animation {
        public LockAlphaAnimation(VisibleElement element) {
            super();
        }

        private int mCurrentStage = -1;

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float time = interpolatedTime * getEndTime();
            int i;
            
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

            float toAlpha, fromAlpha;
            float factor;
            AlphaKeyFrame alphaKeyFrame = (AlphaKeyFrame) mKeyFrames.get(i - 1);
            fromAlpha = alphaKeyFrame.getA();

            alphaKeyFrame = (AlphaKeyFrame) mKeyFrames.get(i);
            toAlpha = alphaKeyFrame.getA();

            factor = (time - mKeyFrames.get(i - 1).getTime())
                    / (mKeyFrames.get(i).getTime() - mKeyFrames.get(i - 1).getTime());

            toAlpha = fromAlpha + (toAlpha - fromAlpha) * factor;
            if (Constants.DBG_ANIMATION) {
                Logger.i(TAG, "Time=" + time + ", fromAlpha=" + fromAlpha + ", toAlpha="
                        + toAlpha + ", factor=" + factor);
            }
            t.setAlpha(toAlpha);
        }
    }
}
