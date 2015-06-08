
package com.baidu.themeanimation.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;

import com.baidu.themeanimation.manager.ContentManager;
import com.baidu.themeanimation.manager.ContentManager.Content;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.VariableConstants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.view.AnimationViewFactory;

public class LockScreenElement extends Element {
    private final static String TAG = "LockScreenElement";

    private int mVersion = 1; // the version of the lock screen theme
    private int mFrameRate = 30;// the frame rate of the animation in the lock
                                // screen
    private Boolean mDisplayDesktop = false; // whether should opacity display
                                             // the previous screen before the
                                             // lock screen displayed
    private int mDesignWidth = Constants.DEFAULT_SCREEN_WIDTH;     //screenwidth define by manifest.xml
    private int mDesignHeight = Constants.DEFAULT_SCREEN_HEIGHT;

    private int mCategory = Constants.BATTERY_NORMAL; // default in normal state
    private ExpressionManager mExpressionManager;
    private Evaluator mEvaluator;

    public LockScreenElement(int version, int frameRate, Boolean displayDesktop) {
        mVersion = version;
        mFrameRate = frameRate;
        mDisplayDesktop = displayDesktop;
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_LOCKSCREEN.equals(tagName)
                || Constants.TAG_LOCKSCREEN_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new LockScreenElement();
    }

    public LockScreenElement() {
        mNormalStateViews = new ArrayList<View>();
        mChargingStateViews = new ArrayList<View>();
        mBatteryFullStateViews = new ArrayList<View>();
        mBatteryLowStateViews = new ArrayList<View>();
        mExpressionManager = ExpressionManager.getInstance();
        mEvaluator = Evaluator.getInstance();
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
    }

    public void setVersion(String version) {
        setVersion(Integer.valueOf(version));
    }

    public int getFrameRate() {
        return mFrameRate;
    }

    public void setFrameRate(int frameRate) {
        mFrameRate = frameRate;
    }

    public void setFrameRate(String frameRate) {
        setFrameRate(Integer.valueOf(frameRate));
    }

    public Boolean getDisplayDesktop() {
        return mDisplayDesktop;
    }

    public void setDisplayDesktop(Boolean displayDesktop) {
        this.mDisplayDesktop = displayDesktop;
    }

    public void setDisplayDesktop(String displayDesktop) {
        setDisplayDesktop(displayDesktop.equals("true"));
    }

    public void setScreenWidth(int screenWidth){
        mDesignWidth = screenWidth;
        if (mDesignWidth == Constants.DEFAULT_SCREEN_WIDTH_XH) {
            setScreenHeight(Constants.DEFAULT_SCREEN_HEIGHT_XH);
            FileUtil.getInstance().updateScale(mDesignWidth, mDesignHeight);
        }
    }

    public void setScreenWidth(String screenWidth){
        setScreenWidth(Integer.valueOf(screenWidth));
    }
    
    public int getScreenWidth(){
        return this.mDesignWidth;
    }

    public void setScreenHeight(int height){
        this.mDesignHeight = height;
    }

    public void setScreenHeight(String height){
        setScreenHeight(Integer.valueOf(height));
    }

    public int getScreenHeight(){
        return this.mDesignHeight;
    }

    private ContentManager mContentManager;

    public void addContent(Content content) {
        if (mContentManager == null) {
            mContentManager = ContentManager.getInstance();
        }
        mContentManager.addContent(content);
    }

    public void updateContent(Context context, Handler handler) {
        ContentManager.getInstance().update(context);
    }

    /*
     * for the case that button and unlock start area will be in the same place,
     * the unlock start point have priority to process touch down event the
     * button in the same place can consume the double click event
     */
    public static Boolean mIsInStartArea = false;

    public class LockScreenElementView extends RelativeLayout {
        Handler mHandler;
        Context mContext;

        public LockScreenElementView(Context context, Handler handler) {
            super(context);
            mContext = context;
            mHandler = handler;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            // check whether in unlock start area
            mIsInStartArea = false;
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                for (int i = 0; i < getVisibleElements().size(); i++) {
                    Element element = getVisibleElements().get(i);
                    if (element instanceof UnlockerElement) {
                        if (((UnlockerElement) element).inStartPoint(ev.getX(), ev.getY())) {
                            mIsInStartArea = true;
                        }
                    }
                }
            }
            return false;
        }
    }

    private WallpaperElement mWallpaperElement = null;
    private ImageView mWallpaperView = null;

    public void updateWallpaper() {
        if (mWallpaperElement != null) {
            mWallpaperElement.changeWallPaper();
        }
        if (mWallpaperView != null) {
            Bitmap b = FileUtil.getInstance().getCurrentLockWallpaper();
            if (null != b && !b.isRecycled()) {
                mWallpaperView.setImageBitmap(FileUtil.getInstance().getCurrentLockWallpaper());
            }
        }
    }

    private LockScreenElementView mLockScreenView;
    private List<View> mNormalStateViews;
    private List<View> mChargingStateViews;
    private List<View> mBatteryFullStateViews;
    private List<View> mBatteryLowStateViews;

    public View generateView(Context context, int width, int height, Handler handler) {
        if (mLockScreenView == null) {
            mLockScreenView = new LockScreenElementView(context, handler);

            ImageView imageView = new ImageView(context);
            LayoutParams layoutParams = new LayoutParams(width, height);
            layoutParams.setMargins(0, 0, 0, 0);
            imageView.setAdjustViewBounds(true);// restretch the wall paper
                                                // image
            imageView.setScaleType(ScaleType.MATRIX);
            imageView.setLayoutParams(layoutParams);
            mWallpaperView = imageView;
            mLockScreenView.addView(imageView);
            FileUtil.getInstance().loadWallpaper(handler);

            VisibleElement element;

            for (int i = 0; i < getVisibleElements().size(); i++) {
                View view = null;
                element = getVisibleElements().get(i);
                if (element instanceof WallpaperElement) {
                    // wallpaperElement just get once
                    mWallpaperElement = (WallpaperElement) element;
                    view = mWallpaperElement.generateView(context, handler);
                } else {
                    view = element.generateView(context, handler);
                }
                if (view == null) {
                    continue;
                }

                int category = element.getCategory();
                if (category != mCategory && category != Constants.ALWAYS_DISPLAY) {
                    element.setVisibility(false);
                }
                switch (category) {
                    case Constants.CHARGING:
                        mChargingStateViews.add(view);
                        break;
                    case Constants.BATTERY_FULL:
                        mBatteryFullStateViews.add(view);
                        break;
                    case Constants.BATTERY_LOW:
                        mBatteryLowStateViews.add(view);
                        break;
                    case Constants.BATTERY_NORMAL:
                        mNormalStateViews.add(view);
                        break;
                }

                mLockScreenView.addView(view);
                if (element instanceof UnlockerElement) {
                    view.bringToFront();
                }
            }
        } else {
            // remove the lock screen view and then add it to the layout
            ViewGroup viewGroup = (ViewGroup) mLockScreenView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mLockScreenView);
            }
        }
        return mLockScreenView;
    }

    // release UnlockerElement's view
    public void releaseView() {
        if (mLockScreenView != null) {
            ViewGroup viewGroup = (ViewGroup) mLockScreenView.getParent();
            Logger.i(TAG, "generateView: removeView");
            if (viewGroup != null) {
                viewGroup.removeView(mLockScreenView);
            }
            mLockScreenView.removeAllViews();
        }
        VisibleElement element;
        for (int i = 0; i < getVisibleElements().size(); i++) {
            element = getVisibleElements().get(i);
            if (element instanceof UnlockerElement) {
                ((UnlockerElement) element).reset();
            }
            element.clearView();
        }
        if (mNormalStateViews != null) {
            mNormalStateViews.clear();
            mBatteryFullStateViews.clear();
            mChargingStateViews.clear();
            mBatteryLowStateViews.clear();
        }

        mLockScreenView = null;
        if (mWallpaperView != null)
            mWallpaperView = null;
    }

    public void clearElement() {
        FileUtil.getInstance().clearBitmap();
        ExpressionManager.getInstance().reset();
        ContentManager.getInstance().clear();
        mEvaluator.clearVariables();
    }

    public void startAnimations() {
        VisibleElement element;

        for (int i = 0; i < getVisibleElements().size(); i++) {
            element = getVisibleElements().get(i);
            int category = element.getCategory();

            if (category == Constants.ALWAYS_DISPLAY
                    || category == mCategory) {
                element.startAnimations();
            }

            if (element instanceof UnlockerElement) {
                ((UnlockerElement) element).reset();
            }
        }
        for (int i = 0; i < getmSourceAnimationss().size(); i++) {
            SourceAnimationElement animation = getmSourceAnimationss().get(i);
            animation.cacheAnimation();
        }
        FileUtil.getInstance().excutuLoadTasks();
    }

    public void stopAnimations() {
        Logger.i(TAG, "stopAnimations");
        for (int i = 0; i < getVisibleElements().size(); i++) {
            getVisibleElements().get(i).clearAnimations();
        }
    }

    public void setAnimationsListener(AnimationViewFactory.AnimationListener listener) {
        for (int i = 0; i < getVisibleElements().size(); i++) {
            getVisibleElements().get(i).setAnimationListener(listener);
        }
    }

    public int getAnimationsStatus() {
        for (int i = 0; i < getVisibleElements().size(); i++) {
            if (getVisibleElements().get(i).getAnimationsStatus() == Constants.Animation_Status_Running) {
                return Constants.Animation_Status_Running;
            }
        }
        return Constants.Animation_Status_Stop;
    }

    public void dispatchCategoryChange(int category) {
        for (int i = 0; i < getVisibleElements().size(); i++) {
            getVisibleElements().get(i).onCategoryChange(category);
        }
        mExpressionManager.setVariableValue(VariableConstants.VAI_CATEGORY,
                mCategory);
    }

    public void dispatchTimeTick(Time time) {
        for (int i = 0; i < getVisibleElements().size(); i++) {
            getVisibleElements().get(i).onTimeTick(time);
        }
    }
}
