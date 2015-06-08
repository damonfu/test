
package yi.support.v1.menu;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import yi.support.v1.utils.Animatable;

import com.baidu.lite.R;
import com.yi.internal.view.menu.ActionMenuView;
import com.yi.internal.view.menu.ExpandedMenuView;


public class HybridMenuPanel extends LinearLayout {

    private final static String TAG = HybridMenuPanel.class.getSimpleName();
    
    private static final int ACTION_BAR_ALPHA_HALF = 220;
    private static final int ACTION_BAR_ALPHA_FULL = 255;

    interface Observer {
        public void onOpenOptionsMenu();
        public void onMenuChangeStart(boolean visible);
        public void onMenuChangeEnd(boolean visible);
    }

    private Observer mObserver;

    private ViewGroup mPanelContainer;
    private ViewGroup mActionContainer;
    private ViewGroup mMenuContainer;

    private final PanelController mPanelController = new PanelController();
    private final PanelPlaceHolder mPanelPlaceHolder = new PanelPlaceHolder();
    private final ActionController mActionController = new ActionController();
    private final MenuController mMenuController = new MenuController();
    private final ActionHeight mActionHeight = new ActionHeight();
    private final MenuHeight mMenuHeight = new MenuHeight();
    private MenuIndicator mMenuIndicator;

    private final Permission mPermission = new Permission();
    private ContentConsistency mViewConsistency;


    public HybridMenuPanel(Context context) {
        super(context);
    }

    public HybridMenuPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HybridMenuPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        
        mPanelContainer = (ViewGroup) findViewById(R.id.baidu_menu_panel_view);
        mActionContainer = (ViewGroup) findViewById(R.id.baidu_action_container);
        mMenuContainer = (ViewGroup) findViewById(R.id.baidu_menu_container);
        mMenuIndicator = new MenuIndicator(findViewById(R.id.baidu_menu_more));

        mPanelContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mObserver.onOpenOptionsMenu();
            }
        });

        mPanelContainer.setClickable(false);
        mMenuContainer.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMenuHeight.calculate(widthMeasureSpec, heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMenuHeight.getHeight(), MeasureSpec.EXACTLY);
        mMenuController.measure(widthMeasureSpec, heightMeasureSpec);
    }


    /*
     * public interface
     */

    public void setObserver(Observer observer) {
        mObserver = observer;
    }
    
    private Runnable mFadeInActionRunnable = new Runnable() {
        @Override
        public void run() {
            mActionController.fadeIn();
        }
    };

    public void setContent(ViewGroup actionMenu, ViewGroup listMenu) {

        ContentConsistency consistency = new ContentConsistency().check(actionMenu).check(listMenu);
        if (consistency.isSame(mViewConsistency)) {
            return;
        } else {
            mViewConsistency = consistency;
        }
        
        mActionMenuChangeTimeStamp = System.currentTimeMillis();
        
        // handle old one
        mActionController.reset();

        // handle new one
        mActionController.set(actionMenu);
        mMenuController.set(listMenu);
        mMenuHeight.reset();

        if (mPanelController.isOpened()) {
            setVisibility(View.VISIBLE);
        }

        refreshMenuIndicatorVisibility();
        mPanelContainer.setClickable(mMenuController.isAvailable());

        // resume previous operation

        if (mMenuController.isVisible()) {
            // menu is visible (pull up / push down/ still)

            if (mMenuController.isAvailable()) {
                // new menu is available
                if (mMenuController.isTransitioning()) {
                    if (mMenuController.isOpened()) {
                        mMenuController.doOpen();
                    } else {
                        mMenuController.doClose();
                    }
                } else {
                    // nothing
                }
            } else {
                // hide menu and show action immediately
                mMenuController.reset();
                mObserver.onMenuChangeStart(false);
                mMenuController.handleMenuEnding(mFadeInActionRunnable);
            }

            mActionHeight.refreshHeight(false, null);
        } else {
            // menu is not visible

            if (mPanelController.isTransitioning()) {
                // panel visibility is changing
                mActionHeight.refreshHeight(false, null);
                if (mPanelController.isOpened()) {
                    mPanelController.doOpen();
                } else {
                    mPanelController.doClose();
                }
            } else {
                // panel is still
                if (mPanelController.isOpened()) {
                    if (mActionHeight.refreshHeight(true, mFadeInActionRunnable)) {
                        // perform height animation firstly and action animation 
                        // will be performed just after it finished
                        mActionController.hide();
                    } else {
                        // perform action animation
                        mActionController.fadeIn();
                    }
                } else {
                    mActionHeight.refreshHeight(false, null);
                }
            }
        }

    }

    public void showPanel() {
        mPanelController.open();
    }

    public void hidePanel() {
        mPanelController.close();
    }

    public void openMenu() {
        mMenuController.open();
    }

    public void closeMenu() {
        mMenuController.close();
    }

    public boolean isMenuOpened() {
        return mMenuController.isOpened();
    }

    public Permission getPermission() {
        return mPermission;
    }

    public void enablePanelWhenSoftInputShown(boolean enabled) {
        mPanelPlaceHolder.enableWhenSoftInputShown(enabled);
    }

    private long mActionMenuChangeTimeStamp;
    private final static int ACTION_MENU_CHANGE_BLOCK_INTERVAL = 500;

    public void fadeActionMenu(float percent, boolean reverse) {
        long elapsed = System.currentTimeMillis() - mActionMenuChangeTimeStamp;
        
        if (elapsed > ACTION_MENU_CHANGE_BLOCK_INTERVAL && mPanelController.isOpenFinished()) {
            percent = (float) Math.sqrt(reverse ? (1 - percent) : percent);
            mActionController.fade(percent);
        }
    }

    public void setPanelTransparency(boolean transparent) {
        mPanelPlaceHolder.enable(!transparent);
        mPanelPlaceHolder.setHeight(mActionHeight.getHeight());
        
        Drawable background = mPanelContainer.getBackground();
        if (background != null) {
            int alpha = transparent ? ACTION_BAR_ALPHA_HALF : ACTION_BAR_ALPHA_FULL;
            background.setAlpha(alpha);
        }
    }
    
    public boolean isAvailable() {
        return mActionController.isAvailable() || mMenuController.isAvailable();
    }
    
    private void refreshMenuIndicatorVisibility() {
        boolean shouldShow = (mMenuController.isAvailable() && !mMenuController.isOpened());
        mMenuIndicator.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
    }

    /*
     * Permission
     */
    public class Permission {

        public boolean isReadyToPrepareMenuPanel() {
            return !mMenuController.isVisible();
        }

    }
    
    /*
     * View Consistency
     */
    private static class ContentConsistency {

        StringBuilder mContentHashCode = new StringBuilder();

        private ContentConsistency check(ViewGroup view) {
            if (view != null) {
                mContentHashCode.append(view.hashCode());
                for (int i=0; i<view.getChildCount(); i++) {
                    mContentHashCode.append(view.getChildAt(i).hashCode());
                }
            }
            return this;
        }

        private boolean isSame(ContentConsistency consistency) {
            if (consistency != null) {
                return mContentHashCode.toString().equals(consistency.mContentHashCode.toString());
            } else {
                return false;
            }
        }

    }

    /*
     * Panel State
     */
    private static class PanelState {

        protected boolean mIsOpened;
        protected boolean mIsTransitioning;
        
        PanelState(boolean opened, boolean transitioning) {
            mIsOpened = opened;
            mIsTransitioning = transitioning;
        }
        
        public boolean isOpened() {
            return mIsOpened;
        }
        
        public boolean isTransitioning() {
            return mIsTransitioning;
        }
        
        public boolean isOpenFinished() {
            return isOpened() && !isTransitioning();
        }
        
        public boolean isVisible() {
            return isOpened() || isTransitioning();
        }
        
        public void reset() {
            mIsOpened = false;
            mIsTransitioning = false;
        }
    }

 
    /*
     * Panel Controller
     */
    private class PanelController extends PanelState {

        PanelController() {
            super(true, false);
        }

        public void open() {
            if (!isOpened()) {
                doOpen();
            }
        }

        public void close() {
            if (isOpened()) {
                doClose();
            }
        }
        
        private boolean isInitializing() {
            return (mActionMenuChangeTimeStamp == 0);
        }

        public void doOpen() {
            if (HybridMenuPanel.this.isAvailable() && !isInitializing()) {
                // show animation
                mIsTransitioning = true;
                setVisibility(View.VISIBLE);
                mActionController.hide();

                float from = mActionHeight.getHeight();
                float current = Animatable.getCurrent(HybridMenuPanel.this, from);
                startAnimation(new Animatable.Vertical(from, 0, current, 
                        Animatable.DURATION.MENU.PANEL.TRANSLATE) {
                    @Override
                    public void onEnd() {
                        mIsTransitioning = false;
                        clearAnimation();
                        mActionController.fadeIn();
                        setVisibility(View.VISIBLE);
                        mPanelPlaceHolder.setHeight(mActionHeight.getHeight());
                    }
                });
            }
            
            mIsOpened = true;
        }

        public void doClose() {
            if (isVisible() && !isInitializing()) {
                mIsTransitioning = true;
                setVisibility(View.VISIBLE);
  
                final float to = mActionHeight.getHeight();
                final float current = Animatable.getCurrent(HybridMenuPanel.this, 0);
                mActionController.fadeOut(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation(new Animatable.Vertical(0, to, current, 
                                Animatable.DURATION.MENU.PANEL.TRANSLATE) {
                            @Override
                            public void onEnd() {
                                mIsTransitioning = false;
                                clearAnimation();
                                setVisibility(View.GONE);
                            }
                        });
                    }
                });
            } else {
                setVisibility(View.GONE);
            }

            mIsOpened = false;
            mPanelPlaceHolder.setHeight(0);
        }
    }

    /*
     * Panel Place Holder
     */
    private class PanelPlaceHolder {

        private boolean mEnabled = true;
        private boolean mEnabledWhenSoftInputShown = false;

        private View mPlaceHolder;
        private AnsycTaskExecutor mAnsycTaskExecutor = new AnsycTaskExecutor();

        public void enable(boolean enabled) {
            if (!enabled) setHeight(0);
            mEnabled = enabled;
        }
        
        public void enableWhenSoftInputShown(boolean enabled) {
            mEnabledWhenSoftInputShown = enabled;
        }

        public void setHeight(int height) {
            if (mEnabled) {
                final int validHeight = mPanelController.isOpened() ? height : 0;
                mAnsycTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (validHeight != 0) {
                            ensurePlaceHolder();
                        }

                        if (mPlaceHolder!=null && mPlaceHolder.getHeight()!=validHeight) {
                            mPlaceHolder.getLayoutParams().height = validHeight;
                            mPlaceHolder.requestLayout();
                        }
                    }
                });
            }
        }

        private void ensurePlaceHolder() {
            if (mPlaceHolder == null) {
                View content = getRootView().findViewById(android.R.id.content);
                if (content != null) {
                    ViewGroup.LayoutParams lp = content.getLayoutParams();
                    if (lp instanceof LinearLayout.LayoutParams) {
                        lp.height = 0;
                        ((LinearLayout.LayoutParams) lp).weight = 1;
                    }
                    
                    ViewParent parent = content.getParent();
                    if (parent instanceof LinearLayout) {
                        LinearLayout container = (LinearLayout) parent;

                        mPlaceHolder = new View(getContext());
                        mPlaceHolder.setId(R.id.action_bar_place_holder);
                        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
                        container.addView(mPlaceHolder, params);
                        container.addOnLayoutChangeListener(mOnLayoutChangeListener);
                    }
                }
            }
        }

        private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, 
                    int oldLeft, int oldTop, int oldRight, int oldBottom) {
                final int paddingBottom = v.getPaddingBottom();
                final boolean fullScreen = (paddingBottom == 0);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEnabledWhenSoftInputShown) {
                            final View hybridMenu = HybridMenuPanel.this;
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) hybridMenu.getLayoutParams();
                            if (params.bottomMargin != paddingBottom) {
                                params.bottomMargin = paddingBottom;
                                hybridMenu.setLayoutParams(params);
                            }
                        } else {
                            mPlaceHolder.setVisibility(fullScreen ? View.VISIBLE : View.GONE);
                        }
                    }
                });
            }
        };

    }
    
    /*
     * Action Controller
     */
    private class ActionController {
        
        private ActionMenuView mActionMenuView;
        
        private void set(ViewGroup actionMenu) {
            mActionMenuView = (ActionMenuView) removeAndAddView(mActionContainer, actionMenu, R.id.action_menu);
        }
        
        private boolean isAvailable() {
            return (mActionMenuView != null);
        }

        private void hide() {
            if (isAvailable()) {
                mActionMenuView.hide();
            }
        }

        private void fadeIn() {
            if (isAvailable() && 
                !mPanelController.isTransitioning() && 
                !mActionHeight.isTransitioning()) {
                mActionMenuView.fadeIn();
            }
        }

        private void fadeOut(Runnable callback) {
            if (isAvailable()) {
                mActionMenuView.fadeOut(callback);
            } else {
                callback.run();
            }
        }

        private void fade(float percent) {
            if (isAvailable()) {
                mActionMenuView.fade(percent);
            }
        }

        private void reset() {
            if (isAvailable()) {
                mActionMenuView.reset();
            }
        }

        private boolean isVisible() {
            return (isAvailable() && mActionContainer.getVisibility()==View.VISIBLE);
        }

    }

    /*
     * Menu Controller
     */
    private class MenuController extends PanelState {

        private ExpandedMenuView mListMenuView;

        MenuController() {
            super(false, false);
        }

        private void set(ViewGroup listMenu) {
            mListMenuView = (ExpandedMenuView) removeAndAddView(mMenuContainer, listMenu, R.id.expanded_menu);
        }

        private boolean isAvailable() {
            return (mListMenuView != null);
        }
        
        private void measure(int widthMeasureSpec, int heightMeasureSpec) {
            if (isAvailable()) {
                mListMenuView.measure(widthMeasureSpec, heightMeasureSpec);
            }
        }
        
        private int getMeasuredHeight() {
            if (mMenuController.isAvailable()) {
                final int itemHeight = MeasureSpec.getSize(mListMenuView.getMeasuredHeight()) + mListMenuView.getDividerHeight();
                final int count = mListMenuView.getAdapter().getCount();
                return itemHeight * count;
            } else {
                return 0;
            }
        }

        private void open() {
            if (!isOpened()) {
                doOpen();
            }
        }

        private void close() {
            if (isOpened()) {
                doClose();
            }
        }
        
        private void doOpen() {
            if (isAvailable()) {
                mIsOpened = true;
                mIsTransitioning = true;
                setVisibility(View.VISIBLE);
                mObserver.onMenuChangeStart(true);
                
                if (mPanelController.isOpened()) {
                    Runnable pullUpMenu = new Runnable() {
                        @Override
                        public void run() {
                            float from = mMenuHeight.getHeight() - mActionHeight.getHeight();
                            pullUpMenu(from, new Runnable() {
                                @Override
                                public void run() {
                                    mIsTransitioning = false;
                                }
                            });
                        }
                    };

                    if (mActionController.isVisible()) {
                        // start from action step
                        mActionController.fadeOut(pullUpMenu);
                    } else {
                        // action step finished
                        pullUpMenu.run();
                    }
                } else { // without panel
                    pullUpMenu(mMenuHeight.getHeight(), new Runnable() {
                        @Override
                        public void run() {
                            mIsTransitioning = false;
                        }
                    });
                }
            }
        }
        
        private void doClose() {
            if (isAvailable()) {
                mIsOpened = false;
                mIsTransitioning = true;
                setVisibility(View.VISIBLE);
                mObserver.onMenuChangeStart(false);
                
                if (mPanelController.isOpened()) {
                    Runnable fadeInAction = new Runnable() {
                        @Override
                        public void run() {
                            mActionController.fadeIn();
                            mIsTransitioning = false;
                        }
                    };

                    if (mActionController.isVisible()) {
                        // menu step finished
                        fadeInAction.run();
                    } else {
                        // start from menu step
                        float to = mMenuHeight.getHeight() - mActionHeight.getHeight();
                        pushDownMenu(to, fadeInAction);
                    }
                } else { // without panel
                    pushDownMenu(mMenuHeight.getHeight(), new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(View.GONE);
                            mIsTransitioning = false;
                        }
                    });
                }
            }
        }

        private void pullUpMenu(float from, final Runnable callback) {
            mActionContainer.setVisibility(View.GONE);
            mMenuIndicator.setVisibility(View.GONE);
            mMenuContainer.setVisibility(View.VISIBLE);

            if (from > 0) {
                float current = from - Animatable.getCurrent(HybridMenuPanel.this,0);
                startAnimation(new Animatable.Vertical(from, 0, current, 
                        Animatable.DURATION.MENU.LIST.TRANSLATE_DEC, 
                        Animatable.INTERPOLATER.DECELERATE));

                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(new Animatable.Alpha(0, 1, (from-current)/from, 
                        Animatable.DURATION.MENU.LIST.ALPHA));
                animationSet.addAnimation(new Animatable.Vertical(-from, 0, -current, 
                        Animatable.DURATION.MENU.LIST.TRANSLATE_DEC, 
                        Animatable.INTERPOLATER.DECELERATE) {
                    @Override
                    protected void onEnd() {
                        handleMenuEnding(callback);
                    }
                });

                mMenuContainer.startAnimation(animationSet);
            } else {
                mMenuContainer.startAnimation(new Animatable.Alpha(0, 1, 0, 
                        Animatable.DURATION.MENU.LIST.ALPHA) {
                    @Override
                    protected void onEnd() {
                        handleMenuEnding(callback);
                    }
                });
            }
        }

        private void pushDownMenu(float to, final Runnable callback) {
            mActionContainer.setVisibility(View.GONE);
            mMenuIndicator.setVisibility(View.GONE);
            mMenuContainer.setVisibility(View.VISIBLE);

            if (to > 0) {
                float current = Animatable.getCurrent(HybridMenuPanel.this,0);
                startAnimation(new Animatable.Vertical(0, to, current, 
                        Animatable.DURATION.MENU.LIST.TRANSLATE));

                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(new Animatable.Alpha(1, 0, (to-current)/to, 
                        Animatable.DURATION.MENU.LIST.ALPHA, 
                        Animatable.DURATION.MENU.LIST.ALPHA_START_OFFSET));
                animationSet.addAnimation(new Animatable.Vertical(0, -to, -current, 
                        Animatable.DURATION.MENU.LIST.TRANSLATE) {
                    @Override
                    protected void onEnd() {
                        handleMenuEnding(callback);
                    }
                });

                mMenuContainer.startAnimation(animationSet);
            } else {
                mMenuContainer.startAnimation(new Animatable.Alpha(1, 0, 1, 
                        Animatable.DURATION.MENU.LIST.ALPHA) {
                    @Override
                    protected void onEnd() {
                        handleMenuEnding(callback);
                    }
                });
            }
        }

        private void handleMenuEnding(Runnable callback) {
            mObserver.onMenuChangeEnd(isOpened());

            clearAnimation();
            mMenuContainer.clearAnimation();
            
            mActionContainer.setVisibility(isOpened() ? View.GONE : View.VISIBLE);
            mMenuContainer.setVisibility(isOpened() ? View.VISIBLE : View.GONE);
            refreshMenuIndicatorVisibility();

            if (callback != null) {
                callback.run();
            }
        }
    }

    /*
     * Action Height Controller
     */
    private class ActionHeight {

        private static final int ACTION_MIN_HEIGHT_HALF = 20;
        private static final int ACTION_MIN_HEIGHT_FULL = 60;

        private ValueAnimator mHeightAnimator;
        private int mSuggestedHeight;
        private int mActualHeight;

        /**
         * 
         * @param animation
         * @return return true if started an animation
         */
        private boolean refreshHeight(boolean animation, Runnable callback) {
            int height = mActionController.isAvailable() ? ACTION_MIN_HEIGHT_FULL : ACTION_MIN_HEIGHT_HALF;
            height = dipToPixels(height);

            if (animation) {
                return changeHeightSmoothly(height, callback);
            } else {
                cancelTransitioning();
                changeHeightImmediately(height);
                if (callback != null) callback.run();
                return false;
            }
        }

        private boolean changeHeightSmoothly(final int height, final Runnable callback) {
            int targetHeight = isTransitioning() ? mSuggestedHeight : mActualHeight;

            if (targetHeight != height) {
                // we will do set height only if the new height does not match current/target height

                cancelTransitioning();

                if (mPanelController.isVisible()) {
                    final boolean shrinked = (mSuggestedHeight > height);
                    mSuggestedHeight = height;
                    if (shrinked) mPanelPlaceHolder.setHeight(height);

                    int from = mActionContainer.getHeight();
                    int to = mSuggestedHeight;
                    doSetHeight(from);

                    mHeightAnimator = ObjectAnimator.ofInt(from, to);
                    mHeightAnimator.setDuration(Animatable.DURATION.MENU.ACTION.HEIGHT);
                    mHeightAnimator.setInterpolator(new DecelerateInterpolator(Animatable.INTERPOLATER.DECELERATE));
                    mHeightAnimator.addUpdateListener(mUpdateListener);
                    mHeightAnimator.start();

                    mHeightAnimator.addListener(new AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}
                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                        @Override
                        public void onAnimationCancel(Animator animation) {}
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mHeightAnimator = null;
                            if (!shrinked) mPanelPlaceHolder.setHeight(getHeight());
                            if (callback != null) callback.run();
                        }
                    });

                    return true;
                } else {
                    changeHeightImmediately(height);
                    return false;
                }
            } else {
                // tell whether there will be an animation and cost some time
                return (mHeightAnimator == null) ? false : mHeightAnimator.isRunning();
            }
        }
        
        private boolean isTransitioning() {
            return (mHeightAnimator != null);
        }

        private void cancelTransitioning() {
            if (isTransitioning()) {
                mHeightAnimator.cancel();
                mHeightAnimator = null;
            }
        }

        private boolean changeHeightImmediately(int height) {
            mSuggestedHeight = height;
            mPanelPlaceHolder.setHeight(getHeight());
            return doSetHeight(height);
        }
        
        private int getHeight() {
            return mSuggestedHeight;
        }

        private AnimatorUpdateListener mUpdateListener = new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                doSetHeight((Integer) animation.getAnimatedValue());
            }
        };

        private boolean doSetHeight(int height) {
            if (mActualHeight != height) {
                ViewGroup.LayoutParams params = mActionContainer.getLayoutParams();
                params.height = mActualHeight = height;
                mActionContainer.setLayoutParams(params);
                forceLayout();
                requestLayout();
                return true;
            } else {
                return false;
            }
        }

        private int dipToPixels(float dips) {
            float density = getResources().getDisplayMetrics().density;
            return (int) (dips * density + 0.5f);
        }

    }

    /*
     * Menu Height
     */
    private class MenuHeight {
        
        private int mHeight;
        
        private void reset() {
            mHeight = 0;
        }

        private int getHeight() {
            if (mHeight == 0) {
                calculate(getMeasuredWidth(), getMeasuredHeight());
            }

            return mHeight;
        }

        private void calculate(int widthMeasureSpec, int heightMeasureSpec) {
            mMenuController.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
            mHeight = mMenuController.getMeasuredHeight();
        }
    }

    private static View removeAndAddView(ViewGroup parent, View view, int id) {
        final View child = parent.findViewById(id);
        if (child != view) {
            if (child != null) parent.removeView(child);

            if (view != null) {
                ViewGroup p = (ViewGroup) view.getParent();
                if(p != null) p.removeView(view);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                parent.addView(view, 0, params);
            }
        }

        return view;
    }
    
    /*
     * Menu Indicator
     */
    private static class MenuIndicator {

        private View mIndicatorView;
        private int mVisibility = View.VISIBLE;

        MenuIndicator(View indicator) {
            mIndicatorView = indicator;
        }

        private void setVisibility(int visibility) {
            if (mVisibility != visibility) {
                mVisibility = visibility;

                float from = isVisible() ? 0 : 1;
                float to = isVisible() ? 1 : 0;
                //float current = Animatable.getCurrent(mIndicatorView, 0);
                mIndicatorView.startAnimation(new Animatable.Alpha(from, to, from, 
                        Animatable.DURATION.MENU.INDICATOR.ALPHA));
            }
        }

        /*private void fade(float percent) {
            if (isVisible()) {
                final float value = 1 - percent;
                mIndicatorView.startAnimation(new Animatable.Alpha(value, value, value, 0));
            }
        }*/

        private boolean isVisible() {
            return (mVisibility == View.VISIBLE);
        }

    }

}
