package com.baidu.widgets.ics;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.widgets.R;

public class ActionBarTabTop extends Activity {

	private ActionBar mActionbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionbar_tab_top);
		mActionbar = getActionBar();
		
		for (int i = 0; i < 3; i++) {
			ActionBar.Tab tab = mActionbar.newTab().setText("Tab "+i);
			tab.setTabListener(new MyTabListener(i));
			mActionbar.addTab(tab);
		}
		
		mActionbar.setDisplayShowTitleEnabled(false);
		mActionbar.setDisplayShowHomeEnabled(false);
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	public static class MyTabListener implements ActionBar.TabListener {
    

        public MyTabListener(int i) {
        }


    	public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	}

    	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	}

    	public void onTabReselected(Tab tab, FragmentTransaction ft) {
    	}
    }

}
