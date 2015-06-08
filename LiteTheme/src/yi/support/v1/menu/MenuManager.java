
package yi.support.v1.menu;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import com.yi.internal.view.menu.HybridMenuInflater;
import com.yi.internal.view.menu.MenuBuilder;
import com.yi.internal.view.menu.MenuPresenter;

import yi.util.ReflectUtil.ContextReflect;
import yi.util.ReflectUtil.WindowReflect;

public class MenuManager implements MenuBuilder.Callback {

    HybridMenuInflater mHybridMenuInflater;
    PanelFeatureState mPanelState;
    PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    Window mWindow;
    Window.Callback mCallback;
    MenuViewContainer mMenuViewContainer = new MenuViewContainer();

    public class MenuViewContainer {
        private final ViewGroup mActionMenuView;
        private final ViewGroup mListMenuView;
        
        private MenuViewContainer() {
            mActionMenuView = null;
            mListMenuView = null;
        }
        
        private MenuViewContainer(ViewGroup actionMenu, ViewGroup listMenu) {
            mActionMenuView = actionMenu;
            mListMenuView = listMenu;
        }
        
        public ViewGroup getActionMenu() {
            if (mActionMenuView != null) {
                return mActionMenuView.getChildCount() > 0 ? mActionMenuView : null;
            } else {
                return null;
            }
        }
        
        public ViewGroup getListMenu() {
            if (mListMenuView != null) {
                return ((ListView) mListMenuView).getAdapter().getCount() > 0 ? mListMenuView : null;
            } else {
                return null;
            }
        }
    }

    public MenuManager(Activity activity) {
        mWindow = activity.getWindow();
        mCallback = mWindow.getCallback();
        setMenuInflater(activity);
    }

    private void setMenuInflater(Activity activity) {
        try {
            final Field field = Activity.class.getDeclaredField("mMenuInflater");
            final boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(activity, getMenuInflater());
            field.setAccessible(accessible);
        } catch (Exception e) {
        }
    }

    /**
     * @param featureId
     * @return
     */
    public MenuViewContainer onCreatePanelView(int featureId) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            PanelFeatureState st = getPanelState(featureId);
            if (!preparePanel(st, null)) {
                return null;
            }

            if (mPanelState.menu == null) {
                return null;
            }

            if (mPanelMenuPresenterCallback == null) {
                mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
            }

            ViewGroup actionMenuView = (ViewGroup) st.getActionMenuView(mWindow.getContext(),
                    mPanelMenuPresenterCallback);

            ViewGroup listMenuView = (ViewGroup) st.getListMenuView(mWindow.getContext(),
                    mPanelMenuPresenterCallback);

            return mMenuViewContainer = new MenuViewContainer(actionMenuView, listMenuView);
        }
        return null;
    }

    public void addOtherPresenters(MenuPresenter presenter) {
        PanelFeatureState st = getPanelState(Window.FEATURE_OPTIONS_PANEL);
        if (st.menu != null) {
            st.menu.removeMenuPresenter(presenter);
            st.menu.addMenuPresenter(presenter);
        }
    }

    /**
     * @param event
     */
    public boolean onPreparePanel(KeyEvent event) {
        PanelFeatureState st = getPanelState(Window.FEATURE_OPTIONS_PANEL);
        if (st.menu == null) {
            if (!initializePanelMenu(mWindow.getContext(), st) || (st.menu == null)) {
                return false;
            }
        }

        st.menu.stopDispatchingItemsChanged();
        if (st.frozenActionViewState != null) {
            st.menu.restoreActionViewStates(st.frozenActionViewState);
            st.frozenActionViewState = null;
        }

        if (!mCallback.onPreparePanel(st.featureId, null, st.menu)) {
            st.menu.startDispatchingItemsChanged();
            return false;
        }

        // Set the proper keymap
        KeyCharacterMap kmap = KeyCharacterMap.load(
                event != null ? event.getDeviceId() : KeyCharacterMap.VIRTUAL_KEYBOARD);
        st.qwertyMode = kmap.getKeyboardType() != KeyCharacterMap.NUMERIC;
        st.menu.setQwertyMode(st.qwertyMode);
        st.menu.startDispatchingItemsChanged();
        return true;
    }

    public MenuViewContainer getMenuViewContainer() {
        return mMenuViewContainer;
    }

    /**
     * Prepares the panel to either be opened or chorded. This creates the Menu
     * instance for the panel and populates it via the Activity callbacks.
     *
     * @param st The panel state to prepare.
     * @param event The event that triggered the preparing of the panel.
     * @return Whether the panel was prepared. If the panel should not be shown,
     *         returns false.
     */
    final boolean preparePanel(PanelFeatureState st, KeyEvent event) {
        if (st.menu == null) {
            if (!initializePanelMenu(mWindow.getContext(), st) || (st.menu == null)) {
                return false;
            }
        }
        // function bar menu
        // call window.callback
        st.menu.stopDispatchingItemsChanged();
        st.menu.clear();
        if (!mCallback.onCreatePanelMenu(st.featureId, st.menu)) {
            // Ditch the menu created above
            st.setMenu(null);
            // function bar menu
            return false;
        }

        // st.refreshMenuContent = false;
        if (st.frozenActionViewState != null) {
            st.menu.restoreActionViewStates(st.frozenActionViewState);
            st.frozenActionViewState = null;
        }

        if (!mCallback.onPreparePanel(st.featureId, null, st.menu)) {
            // function bar menu
            st.menu.startDispatchingItemsChanged();
            return false;
        }

        // Set the proper keymap
        KeyCharacterMap kmap = KeyCharacterMap.load(
                event != null ? event.getDeviceId() : KeyCharacterMap.VIRTUAL_KEYBOARD);
        st.qwertyMode = kmap.getKeyboardType() != KeyCharacterMap.NUMERIC;
        st.menu.setQwertyMode(st.qwertyMode);
        st.menu.startDispatchingItemsChanged();

        return true;
    }

    /**
     * @param st The panel whose menu is being initialized.
     * @return Whether the initialization was successful.
     */
    protected boolean initializePanelMenu(Context context, final PanelFeatureState st) {
        TypedValue outValue = new TypedValue();
        Resources.Theme currentTheme = context.getTheme();
        currentTheme.resolveAttribute(android.R.attr.actionBarWidgetTheme,
                outValue, true);
        final int targetThemeRes = outValue.resourceId;

        if (targetThemeRes != 0 && ContextReflect.getThemeResId(context) != targetThemeRes) {
            context = new ContextThemeWrapper(context, targetThemeRes);
        }

        final MenuBuilder menu = new MenuBuilder(context);

        menu.setCallback(this);
        st.setMenu(menu);
        return true;
    }

    /**
     * Gets a panel's state based on its feature ID.
     *
     * @param featureId The feature ID of the panel.
     * @param required Whether the panel is required (if it is required and it
     *            isn't in our features, this throws an exception).
     * @return The panel state.
     */
    private PanelFeatureState getPanelState(int featureId) {
        if (mPanelState != null)
            return mPanelState;
        mPanelState = new PanelFeatureState(featureId);
        return mPanelState;
    }

    public MenuInflater getMenuInflater() {
        // Make sure that action views can get an appropriate theme.
        if (mHybridMenuInflater == null) {
            mHybridMenuInflater = new HybridMenuInflater(mWindow.getContext());
        }
        return mHybridMenuInflater;
    }

    @Override
    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        // TODO Auto-generated method stub
        if (!WindowReflect.isDestroyed(mWindow)) {
            if (mPanelState != null) {
                return mCallback.onMenuItemSelected(mPanelState.featureId, item);
            }
        }
        return false;
    }

    @Override
    public void onMenuModeChange(MenuBuilder menu) {
        // TODO Auto-generated method stub

    }

    private class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        @Override
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            if (mPanelState != null) {
                mWindow.closePanel(Window.FEATURE_OPTIONS_PANEL);
            }
            // final Menu parentMenu = menu.getRootMenu();
            // final boolean isSubMenu = parentMenu != menu;
            // final PanelFeatureState panel = findMenuPanel(isSubMenu ?
            // parentMenu : menu);
            // if (panel != null) {
            // if (isSubMenu) {
            // callOnPanelClosed(panel.featureId, panel, parentMenu);
            // closePanel(panel, true);
            // } else {
            // // Close the panel and only do the callback if the menu is being
            // // closed completely, not if opening a sub menu
            // closePanel(panel, allMenusAreClosing);
            // }
            // }
        }

        @Override
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            if (subMenu == null && mWindow.hasFeature(Window.FEATURE_ACTION_BAR)) {
                if (!WindowReflect.isDestroyed(mWindow)) {
                    mCallback.onMenuOpened(Window.FEATURE_ACTION_BAR, subMenu);
                }
            }

            return true;
        }
    }
}
