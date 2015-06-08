
package com.baidu.themeanimation.element;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.baidu.themeanimation.element.ImageElement.ImageElementView;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.XmlParserHelper;

public class SourceAnimationElement extends AnimationElement {
    public final static String TAG = "SourceAnimation";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_SOURCE_ANIMATION.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new SourceAnimationElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        SourceKeyFrame sourceKeyFrame = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_SOURCE_ANIMATION_SOURCE)) {
                    sourceKeyFrame = new SourceKeyFrame();
                    XmlParserHelper.setElementAttr(parser, sourceKeyFrame);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return sourceKeyFrame;
    }

    private LockSourceAnimation mLockSourceAnimation = null;

    @Override
    public Animation generateAnimations(VisibleElement element) {
        // Logger.v(TAG, "source Generate Animation!");
        if (mLockSourceAnimation == null || (!mLockSourceAnimation.getElement().equals(element))) {
            int count = mKeyFrames.size();
            if (count > 0 && element != null) {
                // if the first key frame's time is zero, add the current state
                // as the first key frame
                SourceKeyFrame keyFrame = (SourceKeyFrame) mKeyFrames.get(0);
                if (keyFrame.getTime() != 0) {
                    SourceKeyFrame sourceKeyFrame = new SourceKeyFrame();
                    sourceKeyFrame.setTime(0);
                    sourceKeyFrame.setSrc(keyFrame.getSrc());
                    if (sourceKeyFrame.getX() ==0 && sourceKeyFrame.getY() == 0) {
                        sourceKeyFrame.setX(keyFrame.getX());
                        sourceKeyFrame.setY(keyFrame.getY());
                    }
                    mKeyFrames.add(0, sourceKeyFrame);
                }

                mLockSourceAnimation = new LockSourceAnimation(element);

                setStartTime(0);
                long endTime = 0;
                Boolean isOnceAnimationBoolean = false;
                for (int i = mKeyFrames.size() - 1; i >= 0; i--) {
                    if (mKeyFrames.get(i).getTime() < 100000) {
                        endTime = mKeyFrames.get(i).getTime();
                        if (i == mKeyFrames.size() - 1) {
                            mLockSourceAnimation.setRepeatCount(Animation.INFINITE);
                            mLockSourceAnimation.setRepeatMode(Animation.RESTART);
                        }
                        break;
                    } else {
                        // this is a once animation
                        // Logger.v(TAG, "This is a once animation!");
                        mLockSourceAnimation.setFillAfter(true);
                        isOnceAnimationBoolean = true;
                    }
                }

                if (isOnceAnimationBoolean) {
                    endTime++;
                }

                setEndTime(endTime);

                // Logger.v(TAG,
                // "  generate source animation startTime="+getStartTime()+", endTime="+getEndTime());

                mLockSourceAnimation.setDuration(getEndTime() - getStartTime());
            }
        }

        // Logger.v(TAG, "  start a source animation:"+element);
        return mLockSourceAnimation;
    }

    public void cacheAnimation() {
        for (int i = 0; i < mKeyFrames.size(); i++) {
            String src = ((SourceKeyFrame) mKeyFrames.get(i)).getSrc();
            FileUtil.getInstance().cacheBitmap(src, FileUtil.PRIORITY_LOW);
        }
    }

    public static class SourceKeyFrame extends BaseKeyFrame {
        private String mSrc;

        public String getSrc() {
            return mSrc;
        }

        public void setSrc(String src) {
            this.mSrc = src;
        }
    }

    // private static byte devodeBytes[] = new byte[64*1024];

    public class LockSourceAnimation extends Animation {
        private VisibleElement mElement;
        private int mCurrentStage = -1;
        private List<String> mBitmapNames;

        public LockSourceAnimation(VisibleElement element) {
            super();
            this.mElement = element;
            mBitmapNames = new ArrayList<String>();

            String src;
            for (int i = 0; i < mKeyFrames.size(); i++) {
                src = ((SourceKeyFrame) mKeyFrames.get(i)).getSrc();
                FileUtil.getInstance().cacheBitmap(src, FileUtil.PRIORITY_HIGH);
                mBitmapNames.add(src);
            }
        }

        public VisibleElement getElement() {
            return mElement;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (mElement != null) {
                ImageElementView view = (ImageElementView) mElement.getView();
                if (view != null) {
                    float time = interpolatedTime * getEndTime();
                    if(Constants.DBG_ANIMATION){
                        Logger.i(TAG, "LockSourceAnimation in CurrentStage="+ mCurrentStage);
                    }
                    int i;
                    if (mCurrentStage == -1) {
                        mCurrentStage = 1;
                    }
                    if (time <= mKeyFrames.get(mCurrentStage).getTime()) {
                        for (i = mCurrentStage; i > 0; i--) {
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

                        Bitmap bitmap = FileUtil.getInstance().getCachedBitmap(mBitmapNames.get(i));
                        if (bitmap != null) {
                            view.setImage(bitmap);
                            // Logger.v(TAG,
                            // "==set to ,"+i+", x="+mElement.getX()+", Y="+mElement.getY()+", time="+time);
                            view.getLayoutParams().width = bitmap.getWidth();
                            view.getLayoutParams().height = bitmap.getHeight();
                            if (mElement.getX() == 0 && mElement.getY() == 0) {
                                mElement.setX(mKeyFrames.get(i).getX());
                                mElement.setY(mKeyFrames.get(i).getY());
                            }
                            if (Constants.DBG_ANIMATION) {
                                Logger.v(
                                        TAG,
                                        "x=" + view.getLeft() + ", y=" + view.getTop() + ", width="
                                                + bitmap.getWidth() + ", height="
                                                + bitmap.getHeight());
                            }
                        }
                        if(Constants.DBG_ANIMATION){
                            Logger.i(TAG,"==set bitmap " + bitmap + ",i= " + i + ", X="
                                            + mElement.getX() + ", Y=" + mElement.getY() + ", time=" + time + 
                                            ", width =" + view.getLayoutParams().width + ", height = "+view.getLayoutParams().height);
                        }
                    }
                }
            }
        }
    }
}
