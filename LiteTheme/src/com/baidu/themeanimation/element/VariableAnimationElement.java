
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.XmlParserHelper;

public class VariableAnimationElement extends AnimationElement {

    public final static String TAG = "VariableAnimationElement";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_VAR_ANIMATION.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new VariableAnimationElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        VarAniFrame varKeyFrame = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_VAR_ANIMATION_ANIFRAME)) {
                    varKeyFrame = new VarAniFrame();
                    XmlParserHelper.setElementAttr(parser, varKeyFrame);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return varKeyFrame;
    }

    @Override
    public Animation generateAnimations(VisibleElement element) {
        LockVarAnimation lockVarAnimation = null;
        // Logger.v(TAG, "Alpha Generate Animation!");

        int count = mKeyFrames.size();
        if (count > 0 && element != null) {
            // if the first key frame's time is zero, add the first frame state
            // as the start value
            VarAniFrame keyFrame = (VarAniFrame) mKeyFrames.get(0);
            if (keyFrame.getTime() != 0) {
                VarAniFrame varKeyFrame = new VarAniFrame();
                varKeyFrame.setTime(0);
                varKeyFrame.setValue(keyFrame.getValue());
                mKeyFrames.add(0, varKeyFrame);
            }

            lockVarAnimation = new LockVarAnimation(element);

            setStartTime(0);
            long endTime = 0;
            for (int i = mKeyFrames.size() - 1; i >= 0; i--) {
                if (mKeyFrames.get(i).getTime() < 100000) {
                    endTime = mKeyFrames.get(i).getTime();
                    if (i == mKeyFrames.size() - 1) {
                        lockVarAnimation.setRepeatCount(Animation.INFINITE);
                        lockVarAnimation.setRepeatMode(Animation.RESTART);
                    }
                    break;
                } else {
                    // this is a once animation
                    // Logger.v(TAG, "This is a once animation!");
                    lockVarAnimation.setFillAfter(true);
                }
            }
            setEndTime(endTime);

            // Logger.v(TAG,
            // "  generate alpha animation startTime="+getStartTime()+", endTime="+getEndTime());

            lockVarAnimation.setDuration(getEndTime() - getStartTime());
        }
        return lockVarAnimation;
    }

    public static class VarAniFrame extends BaseKeyFrame {
        private String mValue;
        private String mName;

        public String getValue() {
            return mValue;
        }

        public void setValue(String a) {
            mValue = a;
        }

        public String getName() {
            return mValue;
        }

        public void setName(String a) {
            mValue = a;
        }
    }

    public class LockVarAnimation extends Animation {
        VarElement mElement;
        public LockVarAnimation(VisibleElement element) {
            super();
            mElement = (VarElement) element;
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

            VarAniFrame varKeyFrame = (VarAniFrame) mKeyFrames.get(i);
            mElement.update(varKeyFrame.getValue());
        }
    }
}
