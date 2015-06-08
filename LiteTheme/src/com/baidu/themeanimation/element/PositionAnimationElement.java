
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.XmlParserHelper;

public class PositionAnimationElement extends AnimationElement {
    public final static String TAG = "PositionAnimation";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_POSITION_ANIMATION.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new PositionAnimationElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        PositionKeyFrame positionKeyFrame = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_POSITION_ANIMATION_POSITION)) {
                    positionKeyFrame = new PositionKeyFrame();
                    XmlParserHelper.setElementAttr(parser, positionKeyFrame);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return positionKeyFrame;
    }

    Boolean isOnceAnimationBoolean = false;

    public int getAngle() {
        return 0;
    }

    public void setAngle(int angle) {
    }

    public void setAngle(String angle) {
    }

    @Override
    public Animation generateAnimations(VisibleElement element) {
        LockTranslateAnimation translateAnimation = null;
        // Logger.v(TAG, "Position Generate Animation!");
        setListener(element);

        int count = mKeyFrames.size();
        if (count > 0 && element != null) {
            // if the first key frame's time is zero, add the current state as
            // the first key frame
            int index = 0;
            PositionKeyFrame keyFrame = (PositionKeyFrame) mKeyFrames.get(index);
            if (keyFrame.getTime() != 0) {
                PositionKeyFrame positionKeyFrame = new PositionKeyFrame();
                positionKeyFrame.setTime(0);
                positionKeyFrame.setX(0);
                positionKeyFrame.setY(0);
                mKeyFrames.add(0, positionKeyFrame);
            }

            translateAnimation = new LockTranslateAnimation();

            setStartTime(0);
            long endTime = 0;
            for (int i = mKeyFrames.size() - 1; i >= 0; i--) {
                if (mKeyFrames.get(i).getTime() < Constants.MAX_DURATION) {
                    endTime = mKeyFrames.get(i).getTime();
                    if (i == mKeyFrames.size() - 1) {
                        translateAnimation.setRepeatCount(Animation.INFINITE);
                        translateAnimation.setRepeatMode(Animation.RESTART);
                    }
                    break;
                } else {
                    // this is a once animation
                    // Logger.v(TAG, "This is a once animation!");
                    isOnceAnimationBoolean = true;
                    translateAnimation.setFillAfter(true);
                }
            }

            if (isOnceAnimationBoolean) {
                endTime++;
            }
            setEndTime(endTime);

            translateAnimation.setDuration(getEndTime() - getStartTime());
        }

        return translateAnimation;
    }

    public static class PositionKeyFrame extends BaseKeyFrame {
        private int mX;
        private int mY;

        public int getX() {
            return mX;
        }

        public void setX(int x) {
            // jianglin:scale
            x *= FileUtil.X_SCALE;

            this.mX = x;
        }

        public void setX(String x) {
            setX(Integer.valueOf(x));
        }

        public int getY() {
            return mY;
        }

        public void setY(int y) {
            // jianglin:scale
            y *= FileUtil.Y_SCALE;

            this.mY = y;
        }

        public void setY(String y) {
            setY(Integer.valueOf(y));
        }
    }

    public interface LockTranslateAnimationListener {
        public void translateAnimationStage(int deltaX, int deltaY);
    }

    private LockTranslateAnimationListener mListener = null;

    public void setListener(LockTranslateAnimationListener listener) {
        mListener = listener;
    }

    public class LockTranslateAnimation extends Animation {
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
            PositionKeyFrame positionKeyFrame = (PositionKeyFrame) mKeyFrames.get(i - 1);

            int fromXDelta = positionKeyFrame.getX();
            int fromYDelta = positionKeyFrame.getY();

            positionKeyFrame = (PositionKeyFrame) mKeyFrames.get(i);

            int toXDelta = positionKeyFrame.getX();
            int toYDelta = positionKeyFrame.getY();

            float factor = (time - mKeyFrames.get(i - 1).getTime())
                    / (mKeyFrames.get(i).getTime() - mKeyFrames.get(i - 1).getTime());

            if (fromXDelta != toXDelta) {
                fromXDelta = (int) (fromXDelta + (toXDelta - fromXDelta) * factor);
            }
            if (fromYDelta != toYDelta) {
                fromYDelta = (int) (fromYDelta + (toYDelta - fromYDelta) * factor);
            }

            t.getMatrix().setTranslate(fromXDelta, fromYDelta);

            if (Constants.DBG_ANIMATION) {
                Logger.i(TAG, "Time=" + time + ", fromXDelta=" + fromXDelta + ", toXDelta="
                        + toXDelta + ", fromYDelta=" + fromYDelta + ", toYDelta="
                        + toYDelta + ", factor=" + factor);
            }

            if (mListener != null) {
                mListener.translateAnimationStage(fromXDelta, fromYDelta);
            }
        }
    }
}
