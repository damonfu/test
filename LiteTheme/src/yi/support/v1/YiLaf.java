/*
 * Yi Look And Feel
 * 
 */

package yi.support.v1;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import yi.support.v1.ContentDemensionSwitcher.Demension;
import yi.support.v1.menu.HybridMenu;
import yi.support.v1.utils.Logger;
import yi.support.v1.utils.Reflection;
import yi.widget.ScrollTabBar;


public class YiLaf {
    
    private final static String TAG = YiLaf.class.getSimpleName();

    private YiLaf() {

    }

    /*
     * wrapper
     */

    public interface ActionBarWrapper {
        public void onScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void setTabOnClickListener(int positon, OnClickListener l);
        public void setTabOnClickScrollView(int positon, View scrollView);
        public void showBadgeView(int tabIndex);
        public void showBadgeView(int tabIndex, CharSequence text);
        public void hideBadgeView(int tabIndex);
        public View setCustomView(int resId);
        public View setCustomView(View view);
        public void removeCustomView();
        public boolean hasCustomView();
        public void setTitle(int resId);
        public void setTitle(CharSequence title);
        public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp);
        public void setDisplayShowCustomEnabled(boolean showCustom);
        public void setDisplayActionButtonEnabled(boolean showActionButton, Drawable res);
        public void setDisplayActionSpinerEnabled(boolean showActionSpiner);
        public void setAcitonSpinerAdapter(SpinnerAdapter adapter, OnSpinerItemListener callback);
    }
    
    public interface MenuWrapper {
        public void onScrolled(int currentPosition, int scollPosition, float positionOffset);
        public void setPanelVisibility(int visibility);
        public void enablePanelWhenSoftInputShown(boolean enabled);
        public void setPanelTransparency(boolean transparent);
    }
    
    public interface OnSpinerItemListener {
        public boolean onSpinerItemSelected(int itemPosition, long itemId);
    }

    public interface Interface {
        public void setContentViewExclusive(boolean enable);
        public boolean isContentViewExclusive();
        public void enableActionBarStyle();
        public ActionBarWrapper getActionBar();
        public MenuWrapper getMenu();
        public void invalidateOptionsMenu();
        public void openOptionsMenu();
        public void closeOptionsMenu();
    }

    public static void enable(Activity activity) {
        Logger.Performance.begin();

        // register activity life cycle observer
        ActivityLifecycleObserver.enable(activity.getApplication());

        Logger.Performance.elapse();

        // create decorator 
        Interface decorator = Decorator.get(activity);
        if (decorator == null) {
            decorator = new ActivityDecorator(activity);
        }

        Decorator.setCurrent(decorator);

        Logger.Performance.end();
    }
    
    public static Interface current() { 
        Interface decorator = Decorator.current();
        return (decorator == null) ? mActivityDecoratorStub : decorator;
    }

    public static Interface get(Activity activity) {
        Interface decorator = Decorator.get(activity);
        return (decorator == null) ? mActivityDecoratorStub : decorator;
    }
    
    public static void removePreferencePadding(Activity activity) {
        ListView lv = (ListView) activity.findViewById(android.R.id.list);
        if(lv != null){
            lv.setPadding(0, lv.getPaddingTop(), 0, lv.getPaddingBottom());
        }
    }

    /*
     * Activity Decorator
     */
    
    private static class Decorator {

        private static WeakReference<Interface> mCurrentDecorator;

        private static Interface current() {
            return (mCurrentDecorator == null) ? null : mCurrentDecorator.get();
        }

        private static Interface get(Activity activity) {
            Window.Callback callback = activity.getWindow().getCallback();
            if (callback instanceof Interface) {
                return (Interface) callback;
            } else {
                return null;
            }
        }

        private static void setCurrent(Interface decorator) {
            if (current() != decorator) {
                mCurrentDecorator = new WeakReference<Interface>(decorator);
            }
        }

        private static void setCurrent(Activity activity) {
            setCurrent(get(activity));
        }
    }
    
    private static Interface mActivityDecoratorStub = new Interface() {
        public void setContentViewExclusive(boolean enable) {}
        public boolean isContentViewExclusive() { return false; }
        public void enableActionBarStyle() {}
        public ActionBarWrapper getActionBar() { return mActionBarWrapperStub; }
        public MenuWrapper getMenu() { return mMenuWrapperStub; }
        public void invalidateOptionsMenu() {}
        public void openOptionsMenu() {}
        public void closeOptionsMenu() {}
    };
    
    private static ActionBarWrapper mActionBarWrapperStub = new ActionBarWrapper() {
        public void onScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        public void setTabOnClickListener(int positon, OnClickListener l) {}
        public void setTabOnClickScrollView(int positon, View scrollView) {}
        public void showBadgeView(int tabIndex) {}
        public void showBadgeView(int tabIndex, CharSequence text) {}
        public void hideBadgeView(int tabIndex) {}
        public View setCustomView(int resId) { return null; }
        public View setCustomView(View view) { return null; }
        public void removeCustomView() {}
        public boolean hasCustomView() { return false; }
        public void setTitle(int resId) {}
        public void setTitle(CharSequence title) {}
        public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {}
        public void setDisplayShowCustomEnabled(boolean showCustom) {}
        public void setDisplayActionButtonEnabled(boolean showActionButton, Drawable res) {}
        public void setDisplayActionSpinerEnabled(boolean showActionSpiner) {}
        public void setAcitonSpinerAdapter(SpinnerAdapter adapter, OnSpinerItemListener callback) {}
    };
    
    private static MenuWrapper mMenuWrapperStub = new MenuWrapper() {
        public void onScrolled(int currentPosition, int scollPosition, float positionOffset) {}
        public void setPanelVisibility(int visibility) {}
        public void enablePanelWhenSoftInputShown(boolean enabled) {}
        public void setPanelTransparency(boolean transparent) {}
    };

    private static class ActivityDecorator implements Interface, Window.Callback {

        private final WeakReference<Activity> mActivity;
        private final HybridMenu mHybridMenu;
        private final ContentDemensionSwitcher mContentDemensionSwitcher = new ContentDemensionSwitcher();

        private ScrollTabBar.Interface mScrollTabBar;
        private final ActionBarCustomViewContainer mActionBarCustomViewContainer = new ActionBarCustomViewContainer();
        private final ActionBarTitleViewContainer  mActionBarTitleViewContainer;

        private boolean mInvalidateOptionsMenu;

        public ActivityDecorator(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
            mHybridMenu = new HybridMenu(activity);
            activity.getWindow().setCallback(this);
            mActionBarTitleViewContainer = new ActionBarTitleViewContainer(activity);
        }

        private Activity getActivity() {
            Activity activity = mActivity.get();
            if (activity == null) {
                Logger.e(TAG, "activity is null!");
                Logger.printStackTrace(TAG);
            }
            return activity;
        }

        @Override
        public void setContentViewExclusive(boolean enable) {
            mContentDemensionSwitcher.setDemension(getActivity(), 
                    enable ? Demension.EXCLUSIVE : Demension.STANDARD);
        }

        @Override
        public boolean isContentViewExclusive() {
            return (mContentDemensionSwitcher.getDemension() == Demension.EXCLUSIVE);
        }
        
        @Override
        public void enableActionBarStyle() {
            getActionBar();
        }

        @Override
        public ActionBarWrapper getActionBar() {
            if (mScrollTabBar == null) {
                mScrollTabBar = ScrollTabBar.create(getActivity());
            }

            return mActionBarWrapper;
        }

        @Override
        public MenuWrapper getMenu() {
            return mHybridMenu;
        }

        @Override
        public void invalidateOptionsMenu() {
            mInvalidateOptionsMenu = true;
            getActivity().invalidateOptionsMenu();

            if (mInvalidateOptionsMenu) {
                mInvalidateOptionsMenu = false;

                Object st = Reflection.invokeMethod(getActivity().getWindow(), "getPanelState", Window.FEATURE_OPTIONS_PANEL, true);
                if (st != null) {
                    Reflection.setFieldValue(st, "isPrepared", false);
                    Reflection.invokeMethod(getActivity().getWindow(), "preparePanel", st, null);
                }
            }
        }

        @Override
        public void openOptionsMenu() {
            mHybridMenu.openOptionsMenu();
        }

        @Override
        public void closeOptionsMenu() {
            mHybridMenu.closeOptionsMenu();
        }


        private ActionBarWrapper mActionBarWrapper = new ActionBarWrapper() {
            @Override
            public void onScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mScrollTabBar.onScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void setTabOnClickListener(int positon, OnClickListener l) {
                mScrollTabBar.setTabOnClickListener(positon, null, l);
            }

            @Override
            public void setTabOnClickScrollView(int positon, View scrollView) {
                mScrollTabBar.setTabOnClickListener(positon, scrollView, null);
            }

            @Override
            public void showBadgeView(int positon) {
                mScrollTabBar.showBadgeView(getActivity(), positon);
            }
            
            @Override
            public void showBadgeView(int tabIndex, CharSequence text) {
                mScrollTabBar.showBadgeView(getActivity(), tabIndex, text);
            }

            @Override
            public void hideBadgeView(int positon) {
                mScrollTabBar.hideBadgeView(positon);
            }

            @Override
            public View setCustomView(int resId) {
                View customView = LayoutInflater.from(getActivity()).inflate(resId, null);
                return setCustomView(customView);
            }

            @Override
            public View setCustomView(View view) {
                mActionBarCustomViewContainer.show(getActivity(), view);
                return view;
            }

            @Override
            public void removeCustomView() {
                mActionBarCustomViewContainer.hide();
            }

            @Override
            public boolean hasCustomView() {
                return mActionBarCustomViewContainer.isShown();
            }

            @Override
            public void setDisplayShowCustomEnabled(boolean showCustom){
                if(showCustom) {
                    mActionBarTitleViewContainer.show();
                }
            }

            @Override
            public void setTitle(int resId){
                mActionBarTitleViewContainer.setTitle(getActivity().getResources().getText(resId));
            }

            @Override
            public void setTitle(CharSequence title){
                mActionBarTitleViewContainer.setTitle(title);
            }

            @Override
            public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
                mActionBarTitleViewContainer.setDisplayHomeAsUpEnabled(showHomeAsUp); 
            }

            @Override
            public void setDisplayActionButtonEnabled(boolean showActionButton, Drawable res) {
                mActionBarTitleViewContainer.setDisplayActionButtonEnabled(showActionButton, res); 
                
            }

            @Override
            public void setDisplayActionSpinerEnabled(boolean showActionSpiner) {
                mActionBarTitleViewContainer.setDisplayActionSpinerEnabled(showActionSpiner);
            }

            @Override
            public void setAcitonSpinerAdapter(SpinnerAdapter adapter, OnSpinerItemListener callback) {
                mActionBarTitleViewContainer.setDropdownAdapter(adapter);
                mActionBarTitleViewContainer.setCallback(callback);
            }
        };

        /*
         * override for Window.Callback
         */

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (mActionBarCustomViewContainer.dispatchKeyEvent(event) || 
                mHybridMenu.dispatchKeyEvent(event)) {
                return true;
            } else {
                return getActivity().dispatchKeyEvent(event);
            }
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            return getActivity().dispatchKeyShortcutEvent(event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            return getActivity().dispatchTouchEvent(event);
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event) {
            return getActivity().dispatchTrackballEvent(event);
        }

        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event) {
            return getActivity().dispatchGenericMotionEvent(event);
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            return getActivity().dispatchPopulateAccessibilityEvent(event);
        }

        @Override
        public View onCreatePanelView(int featureId) {
            mInvalidateOptionsMenu = false;
            View panelView = getActivity().onCreatePanelView(featureId);
            if (panelView == null) {
                panelView = mHybridMenu.onCreatePanelView(featureId);
            }
            return panelView;
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            return getActivity().onCreatePanelMenu(featureId, menu);
        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            return getActivity().onPreparePanel(featureId, view, menu);
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu) {
            return getActivity().onMenuOpened(featureId, menu);
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            return getActivity().onMenuItemSelected(featureId, item);
        }

        @Override
        public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
            getActivity().onWindowAttributesChanged(attrs);
        }

        @Override
        public void onContentChanged() {
            getActivity().onContentChanged();
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            if (!hasFocus) {
                mHybridMenu.closeOptionsMenu();
            }
            getActivity().onWindowFocusChanged(hasFocus);
        }

        @Override
        public void onAttachedToWindow() {
            getActivity().onAttachedToWindow();
        }

        @Override
        public void onDetachedFromWindow() {
            getActivity().onDetachedFromWindow();
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu) {
            getActivity().onPanelClosed(featureId, menu);
        }

        @Override
        public boolean onSearchRequested() {
            return getActivity().onSearchRequested();
        }

        @Override
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            return getActivity().onWindowStartingActionMode(callback);
        }

        @Override
        public void onActionModeStarted(ActionMode mode) {
            getActivity().onActionModeStarted(mode);
        }

        @Override
        public void onActionModeFinished(ActionMode mode) {
            getActivity().onActionModeFinished(mode);
        }
    }

    
    /*
     * for monitoring activity switching event
     */
    private static class ActivityLifecycleObserver implements ActivityLifecycleCallbacks {

        private static ActivityLifecycleObserver mActivityLifecycleObserver;

        public static void enable(Application application) {
            if (mActivityLifecycleObserver == null) {
                mActivityLifecycleObserver = new ActivityLifecycleObserver();
                application.registerActivityLifecycleCallbacks(mActivityLifecycleObserver);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Decorator.setCurrent(activity);
        }
        
        @Override
        public void onActivityResumed(Activity activity) {
            Decorator.setCurrent(activity);
        }
        
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    };
}
