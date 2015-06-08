
package com.baidu.themeanimation.element;

import java.util.ArrayList;
import java.util.List;

import android.view.animation.Animation;

public abstract class AnimationElement extends Element {
    protected List<BaseKeyFrame> mKeyFrames;
    private long mStartTime = 0;
    private long mEndTime = 0;

    @Override
    public boolean hasView() {
        return false;
    }

    @Override
    public void addElement(Element element) {
        if (element instanceof BaseKeyFrame) {
            addKeyFrame((BaseKeyFrame) element);
        }
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public int getAngle() {
        return 0;
    }

    public void setAngle(int angle) {
    }

    public void setAngle(String angle) {

    }

    /*
     * add the key frame of the animation
     */
    public final void addKeyFrame(BaseKeyFrame baseKeyFrame) {
        if (mKeyFrames == null) {
            mKeyFrames = new ArrayList<BaseKeyFrame>();
        }
        mKeyFrames.add(baseKeyFrame);
    }

    public List<BaseKeyFrame> getKeyFrames() {
        return mKeyFrames;
    }

    /*
     * this method should be override by subclass, for generate the Animation
     * objects
     */
    public abstract Animation generateAnimations(VisibleElement element);

    public static class BaseKeyFrame extends BottomElement {
        private long mTime;
        private int mX;
        private int mY;
        private boolean mIsUsePos = false;

        public boolean isUsePos(){
            return mIsUsePos;
        }

        public long getTime() {
            return mTime;
        }

        public void setTime(long time) {
            this.mTime = time;
        }

        public void setTime(String time) {
            setTime(Long.valueOf(time));
        }

        public int getX() {
            return mX;
        }

        public int getY() {
            return mY;
        }

        public void setX(int x) {
            mIsUsePos = true;
            mX = (int) x;
        }

        public void setX(String x) {
            setX(Integer.valueOf(x));
        }

        public void setY(int y) {
            mIsUsePos = true;
            mY = y;
        }

        public void setY(String y) {
            setY(Integer.valueOf(y));
        }
    }
}
