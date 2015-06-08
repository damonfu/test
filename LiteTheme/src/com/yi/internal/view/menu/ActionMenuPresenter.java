
package com.yi.internal.view.menu;

import android.content.Context;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ActionMenuPresenter implements MenuPresenter {
    private static final String TAG = "ActionMenuPresenter";

    Context mContext;
    LayoutInflater mInflater;
    MenuBuilder mMenu;

    ActionMenuView mMenuView;

    private int mItemIndexOffset;
    int mThemeRes;
    int mMenuLayoutRes;
    int mItemLayoutRes;

    private Callback mCallback;

    private int mId;

    public static final String VIEWS_TAG = "android:menu:action";

    /**
     * Construct a new ActionMenuPresenter.
     * 
     * @param context Context to use for theming. This will supersede the
     *            context provided to initForMenu when this presenter is added.
     * @param menuLayoutRes Layout resource ID for the menu container view
     * @param itemLayoutRes Layout resource for individual item views.
     */
    public ActionMenuPresenter(Context context, int menuLayoutRes, int itemLayoutRes) {
        this(menuLayoutRes, itemLayoutRes, 0);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * Construct a new ActionMenuPresenter.
     * 
     * @param menuLayoutRes Layout resource ID for the menu container view
     * @param itemLayoutRes Layout resource for individual item views.
     * @param themeRes Resource ID of a theme to use for views.
     */
    public ActionMenuPresenter(int menuLayoutRes, int itemLayoutRes, int themeRes) {
        mMenuLayoutRes = menuLayoutRes;
        mItemLayoutRes = itemLayoutRes;
        mThemeRes = themeRes;
    }

    @Override
    public void initForMenu(Context context, MenuBuilder menu) {
        // TODO Auto-generated method stub
        if (mThemeRes != 0) {
            mContext = new ContextThemeWrapper(context, mThemeRes);
            mInflater = LayoutInflater.from(mContext);
        } else if (mContext != null) {
            mContext = context;
            if (mInflater == null) {
                mInflater = LayoutInflater.from(mContext);
            }
        }
        mMenu = menu;
    }

    @Override
    public MenuView getMenuView(ViewGroup root) {
        // TODO Auto-generated method stub
        if (mMenuView == null) {
            mMenuView = (ActionMenuView) mInflater.inflate(mMenuLayoutRes, root, false);
            mMenuView.initialize(mMenu);
            updateMenuView(true);
            
            mMenuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        return mMenuView;
    }
    
    private final static float ACTION_MENU_ALPHA_NORMAL = 1;
    private final static float ACTION_MENU_ALPHA_DIMMED = 0.4f;

    @Override
    public void updateMenuView(boolean cleared) {
        // TODO Auto-generated method stub
        final ViewGroup parent = (ViewGroup) mMenuView;
        if (parent == null)
            return;
        int childIndex = 0;
        if (mMenu != null) {
            mMenu.flagActionItems();
            ArrayList<MenuItemImpl> actionItems = mMenu.getActionItems();
            final int itemCount = actionItems.size();
            for (int i = 0; i < itemCount; i++) {
                MenuItemImpl item = actionItems.get(i);
                final View convertView = parent.getChildAt(childIndex);
                final MenuItemImpl oldItem = convertView instanceof MenuView.ItemView ?
                        ((MenuView.ItemView) convertView).getItemData() : null;
                final View itemView = getItemView(item, convertView, parent);
                if (item != oldItem) {
                    // Don't let old states linger with new data.
                    itemView.setPressed(false);
                    itemView.jumpDrawablesToCurrentState();
                }
                if (itemView != convertView) {
                    addItemView(itemView, childIndex);
                }

                itemView.setClickable(item.isEnabled());
                itemView.setAlpha(item.isEnabled() ? ACTION_MENU_ALPHA_NORMAL : ACTION_MENU_ALPHA_DIMMED);

                childIndex++;
            }
        }

        // Remove leftover views.
        while (childIndex < parent.getChildCount()) {
            if (!filterLeftoverView(parent, childIndex)) {
                childIndex++;
            }
        }
    }

    boolean filterLeftoverView(ViewGroup parent, int childIndex) {
        parent.removeViewAt(childIndex);
        return true;
    }
    /**
     * Add an item view at the given index.
     * 
     * @param itemView View to add
     * @param childIndex Index within the parent to insert at
     */
    protected void addItemView(View itemView, int childIndex) {
        final ViewGroup currentParent = (ViewGroup) itemView.getParent();
        if (currentParent != null) {
            currentParent.removeView(itemView);
        }
        ((ViewGroup) mMenuView).addView(itemView, childIndex);
    }

    @Override
    public void setCallback(Callback cb) {
        // TODO Auto-generated method stub
        mCallback = cb;
    }

    @Override
    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean flagActionItems() {
        // TODO Auto-generated method stub
        final ArrayList<MenuItemImpl> visibleItems = mMenu.getVisibleItems();
        final int itemsSize = visibleItems.size();
        for (int i = 0; i < itemsSize; i++) {
            MenuItemImpl item = visibleItems.get(i);
            if ((item.requiresActionButton() || item.requestsActionButton())) {
                item.setIsActionButton(true);
            }
        }
        return true;
    }

    @Override
    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getId() {
        // TODO Auto-generated method stub
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        // TODO Auto-generated method stub

    }

    /**
     * @param parent
     * @return
     */
    public MenuView.ItemView createItemView(ViewGroup parent) {
        return (MenuView.ItemView) mInflater.inflate(mItemLayoutRes, parent, false);
    }

    /**
     * @param item
     * @param convertView
     * @param parent
     * @return
     */
    public View getItemView(MenuItemImpl item, View convertView, ViewGroup parent) {
        MenuView.ItemView itemView;
        if (convertView instanceof MenuView.ItemView) {
            itemView = (MenuView.ItemView) convertView;
        } else {
            itemView = createItemView(parent);
        }
        bindItemView(item, itemView);
        return (View) itemView;
    }

    /**
     * @param item Item to bind
     * @param itemView
     */
    public void bindItemView(MenuItemImpl item, MenuView.ItemView itemView) {
        itemView.initialize(item, 1);

        final ActionMenuView menuView = (ActionMenuView) mMenuView;
        ActionMenuItemView actionItemView = (ActionMenuItemView) itemView;
        actionItemView.setItemInvoker(menuView);
    }
}
