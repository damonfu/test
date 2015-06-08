package yi.support.v1.menu;

import com.yi.internal.view.menu.MenuBuilder;
import com.yi.internal.view.menu.MenuItemImpl;
import com.yi.internal.view.menu.MenuPresenter;
import com.yi.internal.view.menu.MenuView;
import com.yi.internal.view.menu.SubMenuBuilder;

import android.content.Context;
import android.os.Parcelable;
import android.view.ViewGroup;

public class HybridMenuPresenter implements MenuPresenter {

    @Override
    public void initForMenu(Context context, MenuBuilder menu) {
        // TODO Auto-generated method stub

    }

    @Override
    public MenuView getMenuView(ViewGroup root) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateMenuView(boolean cleared) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCallback(Callback cb) {
        // TODO Auto-generated method stub

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
        return false;
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
        return 0;
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

}
