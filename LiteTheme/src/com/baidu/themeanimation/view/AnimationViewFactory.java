
package com.baidu.themeanimation.view;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;
import android.text.format.Time;
import android.view.View;

import com.baidu.themeanimation.element.LockScreenElement;
import com.baidu.themeanimation.model.InfoRefreshUtil;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.LockScreenHandler;
import com.baidu.themeanimation.util.LockScreenParser;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.FileUtil;

public class AnimationViewFactory {
    private static final String TAG = "AnimationViewFactory";

    private View mLockScreenView;
    public LockScreenElement mLockScreenElement;

    private int mCategory = -1;

    private Boolean mIsInitSuccess = false;

    public LockScreenHandler mHandler;

    private HandlerCallBack mHandlerCallback = new HandlerCallBack();
    private Context mContext;
    public AnimationListener mAnimationListener;
    private InfoRefreshUtil mInfoRefreshUtil;
    private FileUtil mFileUtil;

    public AnimationViewFactory(Context context) {
        mContext = context;
        mInfoRefreshUtil = new InfoRefreshUtil(mContext,"","","");
        mFileUtil = FileUtil.getInstance();
    }

    public void initPara(String path, AnimationListener listener) {
        mFileUtil.setPath(path);
        mAnimationListener = listener;
    }

    public View generateView() {
        Logger.i(TAG, "AnimationView onCreate");

        initlayout();

        if (mLockScreenView != null) {
            mInfoRefreshUtil.setGlobalVariable();
            mLockScreenElement.startAnimations();
            Logger.i(TAG, "create AnimationView ok");
        }
        return mLockScreenView;
    }

    public void initlayout() {
        Logger.i(TAG, "initlayout ");
        mIsInitSuccess = false;
        if (mLockScreenElement == null) {
            Logger.i(TAG, "create LockScreenElement");
            InputStream manifestStream = null;
            manifestStream = mFileUtil.openFile(mHandler);
            if (manifestStream != null) {
                try {
                    mLockScreenElement = LockScreenParser.getInstance().inflate(manifestStream);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        manifestStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (mLockScreenElement != null) {
            Logger.i(TAG, "generate LockScreenView");
            try {
                mLockScreenView = mLockScreenElement.generateView(mContext,
                        FileUtil.REAL_SCREEN_WIDTH,
                        FileUtil.REAL_SCREEN_HEIGHT, mHandler);
                if (mAnimationListener != null) {
                    mLockScreenElement.setAnimationsListener(mAnimationListener);
                }
            } catch (IllegalStateException e) {
                mLockScreenView = null;
                e.printStackTrace();
            } catch (Exception e1) {
                mLockScreenView = null;
                e1.printStackTrace();
            }
        }
        if (mLockScreenView != null) {
            mIsInitSuccess = true;
        } else {
            mIsInitSuccess = false;
        }
    }

    private class HandlerCallBack implements LockScreenHandler.HandlerCallback {
        public void unLock() {
            if (mLockScreenElement != null) {
                mLockScreenElement.startAnimations();
            }
        }

        public void updateWallpaper() {
            if (mLockScreenElement != null) {
                mLockScreenElement.updateWallpaper();
            }
        }

        public void setCategory(int category) {
            if (mLockScreenElement != null && category != mCategory) {
                mCategory = category;
                Logger.d("category", "Change category to " + mCategory);
                mLockScreenElement.dispatchCategoryChange(category);
            }
        }

        public void dispatchTimeTick(Time time) {
            if (mLockScreenElement != null) {
                mLockScreenElement.dispatchTimeTick(time);
            }
        }
    }

    public interface AnimationListener {
        public void onAnimationStart();

        public void onAnimationRepeat();

        public void onAnimationEnd();
        // public void onAnimationCancel();
    }

    public void startAnimation() {
        if (mLockScreenElement != null) {
            mLockScreenElement.startAnimations();
        }
    }

    public void stopAnimation() {
        if (mLockScreenElement != null) {
            mLockScreenElement.stopAnimations();
        }
    }

    public int getAnimationStatus() {
        if (mLockScreenElement != null) {
            return mLockScreenElement.getAnimationsStatus();
        }
        return Constants.Animation_Status_Stop;
    }
}
