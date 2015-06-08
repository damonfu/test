
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.XmlParserHelper;

public class RotateAnimationElement extends AnimationElement {
    public final static String TAG = "RotateAnimation";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_ROTATE_ANIMATION.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new RotateAnimationElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        RotateKeyFrame rotateKeyFrame = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_ROTATE_ANIMATION_ROTATE)) {
                    rotateKeyFrame = new RotateKeyFrame();
                    XmlParserHelper.setElementAttr(parser, rotateKeyFrame);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return rotateKeyFrame;
    }

    public static class RotateKeyFrame extends BaseKeyFrame {
        private int mAngle;

        public int getAngle() {
            return mAngle;
        }

        public void setAngle(int angle) {
            this.mAngle = angle;
        }

        public void setAngle(String angle) {
            setAngle(Integer.valueOf(angle));
        }
    }

    @Override
    public Animation generateAnimations(VisibleElement element) {
        LockRotateAnimation lockRotateAnimation = null;
        Logger.v("rotate", "Rotate Generate Animation!");

        int count = mKeyFrames.size();
        if (count > 0 && element != null) {
            // if the first key frame's time is zero, add the current state as
            // the first key frame
            int index = 0;
            RotateKeyFrame keyFrame = (RotateKeyFrame) mKeyFrames.get(index);
            if (keyFrame.getTime() != 0) {
                RotateKeyFrame rotateKeyFrame = new RotateKeyFrame();
                rotateKeyFrame.setTime(0);
                rotateKeyFrame.setAngle(0);
                mKeyFrames.add(0, rotateKeyFrame);
            }

            lockRotateAnimation = new LockRotateAnimation(element.getCenterX(),
                    element.getCenterY());

            setStartTime(0);
            long endTime = 0;
            for (int i = mKeyFrames.size() - 1; i >= 0; i--) {
                if (mKeyFrames.get(i).getTime() < 100000) {
                    endTime = mKeyFrames.get(i).getTime();
                    if (i == mKeyFrames.size() - 1) {
                        lockRotateAnimation.setRepeatCount(Animation.INFINITE);
                        lockRotateAnimation.setRepeatMode(Animation.REVERSE);
                    }
                    break;
                } else {
                    // this is a once animation
                    Logger.v(TAG, "This is a once animation!");
                    lockRotateAnimation.setFillAfter(true);
                }
            }
            setEndTime(endTime);

            Logger.v(TAG, "  generate rotate animation startTime=" + getStartTime() + ", endTime="
                    + getEndTime());

            lockRotateAnimation.setDuration(getEndTime() - getStartTime());
        }
        return lockRotateAnimation;
    }

    public class LockRotateAnimation extends Animation {
        private int mPivotX = 0;
        private int mPivotY = 0;

        private int mPivotXValue = 0;
        private int mPivotYValue = 0;

        private int mCurrentStage = -1;

        public LockRotateAnimation(int pivotX, int pivotY) {
            mPivotX = pivotX;
            mPivotY = pivotY;
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }

        @Override
        public boolean willChangeTransformationMatrix() {
            return true;
        }

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
            RotateKeyFrame rotateKeyFrame = (RotateKeyFrame) mKeyFrames.get(i - 1);
            int fromDegrees = rotateKeyFrame.getAngle();

            rotateKeyFrame = (RotateKeyFrame) mKeyFrames.get(i);
            int toDegrees = rotateKeyFrame.getAngle();

            float factor = (time - mKeyFrames.get(i - 1).getTime())
                    / (mKeyFrames.get(i).getTime() - mKeyFrames.get(i - 1).getTime());

            fromDegrees = (int) (fromDegrees + (toDegrees - fromDegrees) * factor);

            if (Constants.DBG_ANIMATION) {
                Logger.i(TAG, "Time=" + time + ", fromDegrees=" + fromDegrees + ", toDegrees="
                        + toDegrees +  ", factor=" + factor + ",pivotx=" + mPivotXValue + ",pivoty=" + mPivotYValue);
            }
            t.getMatrix().setRotate(fromDegrees, mPivotXValue, mPivotYValue);
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mPivotXValue = (int) resolveSize(0, mPivotX, width, parentWidth);
            mPivotYValue = (int) resolveSize(0, mPivotY, height, parentHeight);
        }
    }
}
