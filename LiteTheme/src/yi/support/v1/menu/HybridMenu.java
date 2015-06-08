package yi.support.v1.menu;

import java.lang.ref.WeakReference;

import yi.support.v1.YiLaf;
import yi.support.v1.ActionBarCustomViewContainer;
import yi.support.v1.blend.BlendService;
import yi.support.v1.utils.Animatable;
import yi.support.v1.utils.Logger;
import yi.support.v1.utils.Reflection;
import yi.support.v1.utils.ViewCapturer;
import yi.widget.SearchView;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcelable;
import android.view.CollapsibleActionView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.baidu.lite.R;

import com.yi.internal.view.menu.MenuBuilder;
import com.yi.internal.view.menu.MenuItemImpl;
import com.yi.internal.view.menu.MenuPresenter;
import com.yi.internal.view.menu.MenuView;
import com.yi.internal.view.menu.SubMenuBuilder;


public class HybridMenu extends HybridMenuPresenter implements YiLaf.MenuWrapper {

    private final WeakReference<Activity> mActivity;
    private final ContentMask mContentMask;
    private final HybridMenuPanel mMenuPanel;
    private final MenuManager mMenuManager;

    private int mPanelUserVisibility = View.VISIBLE;

    public HybridMenu(Activity activity) {
        Logger.Performance.begin();

        mActivity = new WeakReference<Activity>(activity);
        View hybridMenu = (View) activity.findViewById(R.id.baidu_hybrid_menu);

        Logger.Performance.elapse();
        
        if (hybridMenu == null) {
            hybridMenu = LayoutInflater.from(activity).inflate(R.layout.hybrid_menu, null);
            final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(hybridMenu);
        }

        Logger.Performance.elapse();

        mContentMask = new ContentMask(hybridMenu);

        mMenuPanel = (HybridMenuPanel) hybridMenu.findViewById(R.id.baidu_menu_panel);
        mMenuPanel.setObserver(mMenuPanelObserver);
        initBackground(activity, mMenuPanel);

        mMenuManager = new MenuManager(activity);

        Logger.Performance.end();
    }

    private void initBackground(Context context, ViewGroup menuPanel) {
        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(null, R.styleable.HybridMenu, 
                     R.attr.hybridMenuStyle, 0);
        Drawable shadow = null;
        Drawable background = null;
        Drawable menuMoreBackground = null;
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.HybridMenu_hybridMenuShadow) {
                shadow = a.getDrawable(attr);

            } else if (attr == R.styleable.HybridMenu_hybridMenuBackground) {
                background = a.getDrawable(attr);

            } else if (attr == R.styleable.HybridMenu_hybridMenuMore) {
                menuMoreBackground = a.getDrawable(attr);

            }
        }
        a.recycle();

        if(shadow == null) {
            shadow = context.getResources().getDrawable(R.drawable.cld_ab_bottom_shadow_light_baidu);
        }
        menuPanel.findViewById(R.id.baidu_menu_panel_shadow).setBackgroundDrawable(shadow);

        if(background == null) {
            background = context.getResources().getDrawable(R.drawable.cld_ab_bottom_solid_light_baidu);
        }
        menuPanel.findViewById(R.id.baidu_menu_panel_view).setBackgroundDrawable(background);

        if(menuMoreBackground == null) {
            menuMoreBackground = context.getResources().getDrawable(R.drawable.cld_ic_more);
        }
        menuPanel.findViewById(R.id.baidu_menu_more).setBackgroundDrawable(menuMoreBackground);

    }

    public View onCreatePanelView(final int featureId) {
        mMenuManager.onCreatePanelView(featureId);
        mMenuManager.addOtherPresenters(HybridMenu.this);
        setupConnectionWithActionBar();
        doUpdateMenuView();
        return mMenuPanel;
    }

    public void openOptionsMenu() {
        openOptionsMenu(null);
    }

    public void closeOptionsMenu() {
        mMenuPanel.closeMenu();
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        closeOptionsMenu();
    }
    
    @Override
    public void updateMenuView(boolean cleared) {
        doUpdateMenuView();
    }
    
    private void doUpdateMenuView() {
        MenuManager.MenuViewContainer container = mMenuManager.getMenuViewContainer();
        mMenuPanel.setContent(container.getActionMenu(), container.getListMenu());
        refreshPanelVisibility();
    }

    @Override
    public void enablePanelWhenSoftInputShown(boolean enabled) {
        mMenuPanel.enablePanelWhenSoftInputShown(enabled);
    }

    @Override
    public void onScrolled(int currentPosition, int scollPosition, float positionOffset) {
        mMenuPanel.fadeActionMenu(positionOffset, currentPosition != scollPosition);
    }

    @Override
    public void setPanelVisibility(int visibility) {
        mPanelUserVisibility = visibility;
        refreshPanelVisibility();
    }

    @Override
    public void setPanelTransparency(boolean transparent) {
        mMenuPanel.setPanelTransparency(transparent);
    }

    public void refreshPanelVisibility() {
        if (!mMenuPanel.isAvailable() || 
            mPanelUserVisibility == View.GONE || 
            YiLaf.get(mActivity.get()).getActionBar().hasCustomView()) {
            mMenuPanel.hidePanel();
        } else {
            mMenuPanel.showPanel();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MENU:
                    if (mMenuPanel.isAvailable()) {
                        if (mMenuPanel.isMenuOpened()) {
                            closeOptionsMenu();
                        } else {
                            openOptionsMenu(event);
                        }
                        mMenuPanel.playSoundEffect(SoundEffectConstants.CLICK);
                    }
                    
                    // must consume this key event to avoiding framework to handle it
                    // or there will be crash here
                    return true;

                case KeyEvent.KEYCODE_BACK:
                    if (hasExpandedActionView()) {
                        collapseActionView();
                        return true;
                    } else if (mMenuPanel.isMenuOpened()) {
                        closeOptionsMenu();
                        return true;
                    }
             }
        }

        return false;
    }

    private void openOptionsMenu(KeyEvent event) {
        if (mMenuPanel.getPermission().isReadyToPrepareMenuPanel()) {
            mMenuManager.onPreparePanel(event);
        }
        mMenuPanel.openMenu();
    }

    private HybridMenuPanel.Observer mMenuPanelObserver = new HybridMenuPanel.Observer() {
        @Override
        public void onOpenOptionsMenu() {
            openOptionsMenu();
        }
        
        @Override
        public void onMenuChangeStart(boolean visible) {
            if (visible) {
                mContentMask.fadeIn();
            } else {
                mContentMask.fadeOut();
                //mContentMask.hideBlurMask();
            }
        }
        
        @Override
        public void onMenuChangeEnd(boolean visible) {
            if (visible) {
                //mContentMask.showBlurMask();
            }
        }
    };

    private void setupConnectionWithActionBar() {
        PanelFeatureState st = mMenuManager.mPanelState;
        if (st.menu!=null && mExpandedMenuPresenter==null) {
            mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            st.menu.addMenuPresenter(mExpandedMenuPresenter);
        }
    }

    private void onExpandItemActionView() {
        setPanelVisibility(View.GONE);
    }

    private void onCollapseItemActionView() {
        setPanelVisibility(View.VISIBLE);
    }

    public boolean hasExpandedActionView() {
        return mExpandedMenuPresenter != null &&
                mExpandedMenuPresenter.mCurrentExpandedItem != null;
    }

    public void collapseActionView() {
        final MenuItemImpl item = mExpandedMenuPresenter == null ? null :
                mExpandedMenuPresenter.mCurrentExpandedItem;
        if (item != null) {
            item.collapseActionView();
            onCollapseItemActionView();
        }
    }

    /*
     * DimmedMask & BlurMask
     */
    private class ContentMask {

        private final ImageView mBlurMask;
        private final ImageView mDimmedMask;

        ContentMask(View menu) {
            mBlurMask = (ImageView) menu.findViewById(R.id.baidu_blur_mask);
            mDimmedMask = (ImageView) menu.findViewById(R.id.baidu_dimmed_mask);
            mDimmedMask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeOptionsMenu();
                }
            });
        }

        void fadeIn() {
            mDimmedMask.setVisibility(View.VISIBLE);
            mDimmedMask.startAnimation(new Animatable.Alpha(0, 1, 
                    getAnimationAlpha(mDimmedMask,0), Animatable.DURATION.MASK.ALPHA) {
                @Override
                protected void onEnd() {
                    mDimmedMask.clearAnimation();
                }
            });
        }

        void fadeOut() {
            mDimmedMask.setVisibility(View.VISIBLE);
            mDimmedMask.startAnimation(new Animatable.Alpha(1, 0, 
                    getAnimationAlpha(mDimmedMask,1), Animatable.DURATION.MASK.ALPHA) {
                @Override
                protected void onEnd() {
                    mDimmedMask.clearAnimation();
                    mDimmedMask.setVisibility(View.GONE);
                }
            });
        }

        private float getAnimationAlpha(View view, float defValue) {
            return Animatable.getCurrent(view, defValue);
        }

        private void showBlurMask() {
            startBlurMask(new BlendService.Observer(new Handler()) {
                private final static float SCALE_FACTOR = 0.5f;

                @Override
                public float getScaleFactor() {
                    return SCALE_FACTOR;
                }

                @Override
                public void onBlendFinished(Bitmap bmp) {
                    if (bmp != null) {
                        mBlurMask.setVisibility(View.VISIBLE);
                        mBlurMask.setImageBitmap(bmp);
                        mBlurMask.startAnimation(new Animatable.Alpha(0, 1, 
                                getAnimationAlpha(mBlurMask,0), Animatable.DURATION.MASK.BLUR));
                    }
                }
            });
        }

        private void hideBlurMask() {
            cancelBlurMask();

            if (mBlurMask.getVisibility() == View.VISIBLE) {
                mBlurMask.startAnimation(new Animatable.Alpha(1, 0, 
                        getAnimationAlpha(mBlurMask,1), Animatable.DURATION.MASK.ALPHA) {
                    @Override
                    protected void onEnd() {
                        mBlurMask.clearAnimation();
                        mBlurMask.setImageBitmap(null);
                        mBlurMask.setVisibility(View.GONE);
                    }
                });
            }
        }

        private final static int BLUR_RADIUS = 3;

        private void startBlurMask(BlendService.Observer observer) {
            cancelBlurMask();
            mDimmedMask.setVisibility(View.GONE);
            Bitmap snapshot = ViewCapturer.snapshot(mBlurMask.getRootView());
            mDimmedMask.setVisibility(View.VISIBLE);
            BlendService.blur(snapshot, BLUR_RADIUS, observer);
        }

        private void cancelBlurMask() {
            BlendService.interrupt();
        }

    }

    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    
    private class ExpandedActionViewMenuPresenterVolatile {
        protected ViewGroup mActionBarViewThis;

        protected FrameLayout mHomeLayout;
        protected View mExpandedActionView;
        protected FrameLayout mExpandedHomeLayout;
        protected Drawable mIcon;
        protected LinearLayout mTitleLayout;
        protected View mCustomNavView;
        protected HorizontalScrollView mTabScrollView;
        protected Spinner mSpinner;
        
        protected int mNavigationMode;
        protected int mDisplayOptions = -1;
        
        static final int GONE = View.GONE;
        static final int VISIBLE = View.VISIBLE;
        
        private static final String ACTION_BAR_MEMBER_NAME = "mActionBar";
        
        ExpandedActionViewMenuPresenterVolatile() {
            Activity activity = (Activity) mMenuPanel.getContext();
            mActionBarViewThis = Reflection.getFieldValue(activity.getWindow(), ACTION_BAR_MEMBER_NAME, ViewGroup.class);
        }

        protected void reloadVolatile() {            
            mHomeLayout = Reflection.getFieldValue(mActionBarViewThis, "mHomeLayout", FrameLayout.class);
            mExpandedActionView = Reflection.getFieldValue(mActionBarViewThis, "mExpandedActionView", View.class);
            mExpandedHomeLayout = Reflection.getFieldValue(mActionBarViewThis, "mExpandedHomeLayout", FrameLayout.class);
            mIcon = Reflection.getFieldValue(mActionBarViewThis, "mIcon", Drawable.class);
            mTitleLayout = Reflection.getFieldValue(mActionBarViewThis, "mTitleLayout", LinearLayout.class);
            mCustomNavView = Reflection.getFieldValue(mActionBarViewThis, "mCustomNavView", View.class);
            mTabScrollView = Reflection.getFieldValue(mActionBarViewThis, "mTabScrollView", HorizontalScrollView.class);
            mSpinner = Reflection.getFieldValue(mActionBarViewThis, "mSpinner", Spinner.class);
            mNavigationMode = Reflection.getFieldValue(mActionBarViewThis, "mNavigationMode", Integer.class);
            mDisplayOptions = Reflection.getFieldValue(mActionBarViewThis, "mDisplayOptions", Integer.class);
        }

        protected void addView(View view) {
            mActionBarViewThis.addView(view);
        }

        protected void removeView(View view) {
            mActionBarViewThis.removeView(view);
        }

        protected void requestLayout() {
            mActionBarViewThis.requestLayout();
        }

        protected Resources getResources() {
            return mActionBarViewThis.getResources();
        }
        
        protected void initTitle() {
            Reflection.invokeMethod(mActionBarViewThis, "initTitle");
        }

    }

    private class ExpandedActionViewMenuPresenter extends ExpandedActionViewMenuPresenterVolatile implements MenuPresenter {
        MenuBuilder mMenu;
        MenuItemImpl mCurrentExpandedItem;

        protected final boolean USE_ANIMAION = true;
        protected Animation mInAnimationFuture;
        protected Animation mOutAnimationFuture;

        private final OnClickListener mExpandedActionViewUpListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final MenuItemImpl item = mCurrentExpandedItem;
                if (item != null) {
                    item.collapseActionView();
                    onCollapseItemActionView();
                }
            }
        };

        @Override
        public void initForMenu(Context context, MenuBuilder menu) {
            // Clear the expanded action view when menus change.
            if (mMenu != null && mCurrentExpandedItem != null) {
                mMenu.collapseItemActionView(mCurrentExpandedItem);
            }
            mMenu = menu;
        }

        @Override
        public MenuView getMenuView(ViewGroup root) {
            return null;
        }

        @Override
        public void updateMenuView(boolean cleared) {
            // Make sure the expanded item we have is still there.
            if (mCurrentExpandedItem != null) {
                boolean found = false;

                if (mMenu != null) {
                    final int count = mMenu.size();
                    for (int i = 0; i < count; i++) {
                        final MenuItem item = mMenu.getItem(i);
                        if (item == mCurrentExpandedItem) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    // The item we had expanded disappeared. Collapse.
                    collapseItemActionView(mMenu, mCurrentExpandedItem);
                }
            }
        }

        @Override
        public void setCallback(Callback cb) {
        }

        @Override
        public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
            return false;
        }

        @Override
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override
        public boolean flagActionItems() {
            return false;
        }

        @Override
        public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        	mInAnimationFuture = AnimationUtils.loadAnimation(mMenuPanel.getContext(), R.anim.cld_view_top_in);
            reloadVolatile();
            mExpandedHomeLayout.setOnClickListener(mExpandedActionViewUpListener);
            mExpandedActionView = item.getActionView();
            boolean bSearchView = false;
            if (mExpandedActionView instanceof SearchView)
            {
            	bSearchView = true;
            	((SearchView) mExpandedActionView).showSearchBackIcon(mExpandedActionViewUpListener, false);
            }
            if(bSearchView){
            	mExpandedHomeLayout.setVisibility(GONE);
            }
            Reflection.setFieldValue(mActionBarViewThis, "mExpandedActionView", mExpandedActionView);
            //mExpandedHomeLayout.setIcon(mIcon.getConstantState().newDrawable(getResources()));
            if(!bSearchView)
            	Reflection.invokeMethod(mExpandedHomeLayout, "setIcon", mIcon.getConstantState().newDrawable(getResources()));
            mCurrentExpandedItem = item;
            if (mExpandedActionView.getParent() != mActionBarViewThis) {
                addView(mExpandedActionView);
            }
            if (!bSearchView && mExpandedHomeLayout.getParent() != mActionBarViewThis) {
                addView(mExpandedHomeLayout);
            }
            if(!bSearchView){
                mHomeLayout.setVisibility(GONE);
	            if (mTitleLayout != null) mTitleLayout.setVisibility(GONE);
	            if (mTabScrollView != null) mTabScrollView.setVisibility(GONE);
	            if (mSpinner != null) mSpinner.setVisibility(GONE);
	            if (mCustomNavView != null) mCustomNavView.setVisibility(GONE);
	            requestLayout();
            }
            item.setActionViewExpanded(true);

            if (mExpandedActionView instanceof CollapsibleActionView) {
            	if(bSearchView){
            		mExpandedActionView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cld_search_view_background));
            		startAnimation(mExpandedActionView, mInAnimationFuture);
            	}
                ((CollapsibleActionView) mExpandedActionView).onActionViewExpanded();
            }
            onExpandItemActionView();
            return true;

        }
        

        @Override
        public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
            reloadVolatile();
            
            mOutAnimationFuture = AnimationUtils.loadAnimation(mMenuPanel.getContext(), R.anim.cld_view_top_out);
            mOutAnimationFuture.setAnimationListener(new AnimationListener(){ 
            	public void onAnimationStart(Animation animation) {
            		// Do this before detaching the actionview from the hierarchy, in case
    	            // it needs to dismiss the soft keyboard, etc.
            		if (mExpandedActionView instanceof SearchView) {
            			((SearchView) mExpandedActionView).clearFocus();
            		}
            	}
            	public void onAnimationEnd(Animation animation) { 
            		// Do this before detaching the actionview from the hierarchy, in case
    	            // it needs to dismiss the soft keyboard, etc.
    	            if (mExpandedActionView instanceof CollapsibleActionView) {
    	                ((CollapsibleActionView) mExpandedActionView).onActionViewCollapsed();
    	            }
    	            removeView(mExpandedActionView);
    	            removeView(mExpandedHomeLayout);
    	            mExpandedActionView = null;
    	            if ((mDisplayOptions & ActionBar.DISPLAY_SHOW_HOME) != 0) {
    	                mHomeLayout.setVisibility(VISIBLE);
    	            }
    	            if ((mDisplayOptions & ActionBar.DISPLAY_SHOW_TITLE) != 0) {
    	                if (mTitleLayout == null) {
    	                    initTitle();
    	                } else {
    	                    mTitleLayout.setVisibility(VISIBLE);
    	                }
    	            }
    	            if (mTabScrollView != null && mNavigationMode == ActionBar.NAVIGATION_MODE_TABS) {
    	                mTabScrollView.setVisibility(VISIBLE);
    	            }
    	            if (mSpinner != null && mNavigationMode == ActionBar.NAVIGATION_MODE_LIST) {
    	                mSpinner.setVisibility(VISIBLE);
    	            }
    	            if (mCustomNavView != null && (mDisplayOptions & ActionBar.DISPLAY_SHOW_CUSTOM) != 0) {
    	                mCustomNavView.setVisibility(VISIBLE);
    	            }
    	            //mExpandedHomeLayout.setIcon(null);
    	            Reflection.invokeMethod(mExpandedHomeLayout, "setIcon", (Drawable) null);
    	            mCurrentExpandedItem = null;
    	            requestLayout();
            	}
            	public void onAnimationRepeat(Animation animation){
            		
            	}
	            
            });
            startAnimation(mExpandedActionView, mOutAnimationFuture);
            item.setActionViewExpanded(false);

            return true;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public Parcelable onSaveInstanceState() {
            return null;
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
        }
        
    	private void startAnimation(View view, Animation animation) {
    		if ((animation == null) || (view == null))
    			return;

    		if (USE_ANIMAION)
    			view.startAnimation(animation);
    	}
    }

}
