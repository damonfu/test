
package yi.support.v1.menu;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.lite.R;
import com.yi.internal.view.menu.ActionMenuPresenter;
import com.yi.internal.view.menu.ListMenuPresenter;
import com.yi.internal.view.menu.MenuBuilder;
import com.yi.internal.view.menu.MenuPresenter;
import com.yi.internal.view.menu.MenuView;

public class PanelFeatureState {

    /** Feature ID for this panel. */
    int featureId;

    /** The panel that we are actually showing. */
    View shownPanelView;

    /** Use {@link #setMenu} to set this. */
    MenuBuilder menu;

    ListMenuPresenter listMenuPresenter;
    ActionMenuPresenter actionMenuPresenter;

    boolean isOpen;

    /**
     * True if the menu is in expanded mode, false if the menu is in icon mode
     */
    boolean isInExpandedMode = true;

    public boolean qwertyMode;

    boolean refreshMenuContent;

    boolean wasLastOpen;

    boolean wasLastExpanded;

    /**
     * Contains the state of the menu when told to freeze.
     */
    Bundle frozenMenuState;

    /**
     * Contains the state of associated action views when told to freeze. These
     * are saved across invalidations.
     */
    Bundle frozenActionViewState;

    PanelFeatureState(int featureId) {
        this.featureId = featureId;
    }

    public boolean isInListMode() {
        return isInExpandedMode;
    }

    public boolean isInMixMode() {
        return false;// !menu.getNonActionItems().isEmpty();
    }

    public boolean hasPanelItems() {
        if (shownPanelView == null)
            return false;

        if (isInExpandedMode) {
            return listMenuPresenter.getAdapter().getCount() > 0;
        } else {
            return ((ViewGroup) shownPanelView).getChildCount() > 0;
        }
    }

    /**
     * Unregister and free attached MenuPresenters. They will be recreated as
     * needed.
     */
    public void clearMenuPresenters() {
        if (menu != null) {
            menu.removeMenuPresenter(actionMenuPresenter);
            menu.removeMenuPresenter(listMenuPresenter);
        }
        actionMenuPresenter = null;
        listMenuPresenter = null;
    }

    void setMenu(MenuBuilder menu) {
        if (menu == this.menu)
            return;

        if (this.menu != null) {
            this.menu.removeMenuPresenter(actionMenuPresenter);
            this.menu.removeMenuPresenter(listMenuPresenter);
        }
        this.menu = menu;
        if (menu != null) {
            if (listMenuPresenter != null) {
                menu.addMenuPresenter(actionMenuPresenter);
                menu.addMenuPresenter(listMenuPresenter);
            }
        }
    }

    MenuView getActionMenuView(Context context, MenuPresenter.Callback cb) {
        if (menu == null)
            return null;

        if (actionMenuPresenter == null) {
            actionMenuPresenter = new ActionMenuPresenter(context,R.layout.action_menu_layout,
                    R.layout.action_menu_item_layout);
            actionMenuPresenter.setCallback(cb);
            actionMenuPresenter.setId(R.id.action_menu_presenter);
            menu.addMenuPresenter(actionMenuPresenter);
        }
        MenuView result = actionMenuPresenter.getMenuView(null);
        return result;
    }

    View getMixMenuView(Context context, MenuPresenter.Callback cb) {
        return null;
    }

    MenuView getListMenuView(Context context, MenuPresenter.Callback cb) {
        if (menu == null)
            return null;

        if (listMenuPresenter == null) {
            listMenuPresenter = new ListMenuPresenter(context,
                    R.layout.list_menu_item_layout);
            listMenuPresenter.setCallback(cb);
            listMenuPresenter.setId(R.id.list_menu_presenter);
            menu.addMenuPresenter(listMenuPresenter);
        }
        MenuView result = listMenuPresenter.getMenuView(null);

        return result;
    }

    Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.featureId = featureId;
        savedState.isOpen = isOpen;
        savedState.isInExpandedMode = isInExpandedMode;

        if (menu != null) {
            savedState.menuState = new Bundle();
            menu.savePresenterStates(savedState.menuState);
        }

        return savedState;
    }

    void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        featureId = savedState.featureId;
        wasLastOpen = savedState.isOpen;
        wasLastExpanded = savedState.isInExpandedMode;
        frozenMenuState = savedState.menuState;

        /*
         * A LocalActivityManager keeps the same instance of this class around.
         * The first time the menu is being shown after restoring, the
         * Activity.onCreateOptionsMenu should be called. But, if it is the same
         * instance then menu != null and we won't call that method. We clear
         * any cached views here. The caller should invalidatePanelMenu.
         */
        shownPanelView = null;
    }

    void applyFrozenState() {
        if (menu != null && frozenMenuState != null) {
            menu.restorePresenterStates(frozenMenuState);
            frozenMenuState = null;
        }
    }

    private static class SavedState implements Parcelable {
        int featureId;
        boolean isOpen;
        boolean isInExpandedMode;
        Bundle menuState;

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(featureId);
            dest.writeInt(isOpen ? 1 : 0);
            dest.writeInt(isInExpandedMode ? 1 : 0);

            if (isOpen) {
                dest.writeBundle(menuState);
            }
        }

        private static SavedState readFromParcel(Parcel source) {
            SavedState savedState = new SavedState();
            savedState.featureId = source.readInt();
            savedState.isOpen = source.readInt() == 1;
            savedState.isInExpandedMode = source.readInt() == 1;

            if (savedState.isOpen) {
                savedState.menuState = source.readBundle();
            }

            return savedState;
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return readFromParcel(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
