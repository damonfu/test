package com.baidu.widgets.rice;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import yi.util.ViewHelper;
import android.view.Window;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public final class TabBar extends TabActivity implements
		TabHost.TabContentFactory, OnItemClickListener,
		DialogInterface.OnClickListener {
	public final static byte ALERT_DIALOG = 1;
	public final static String KEY_TMP = "KEY_TMP";
	public final static String KEY_IDX = "KEY_IDX";

	private ArrayList<String> items1 = new ArrayList<String>();
	private ArrayList<String> items2 = new ArrayList<String>();

	private ListView lv1;
	private ListView lv2;

	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setYiTheme(this);

		items1.add("a");
		items2.add("b");

 		setContentView(R.layout.yi_tab_content);
		load();
	}

	private final void load() {
		TabHost tabHost = getTabHost();
		TabWidget widget = getTabWidget();
		
		View view1 = ViewHelper.createIndicatorView(widget, "test1", R.drawable.yi_ic_tabbar_contact_callhistory);
		tabHost.addTab(tabHost.newTabSpec("1").setIndicator(view1).setContent(this));
		
		View view2 = ViewHelper.createIndicatorView(widget, "test2", R.drawable.yi_ic_tabbar_contact_contacts);
		tabHost.addTab(tabHost.newTabSpec("2").setIndicator(view2).setContent(this));
		
		View view3 = ViewHelper.createIndicatorView(widget, "test3", R.drawable.yi_ic_tabbar_contact_dialer);
		tabHost.addTab(tabHost.newTabSpec("3").setIndicator(view3).setContent(this));

		View view4 = ViewHelper.createIndicatorView(widget, "test4", R.drawable.yi_ic_tabbar_contact_merge);
		tabHost.addTab(tabHost.newTabSpec("4").setIndicator(view4).setContent(this));

		tabHost = null;
	}
	
	public final View createTabContent(String tag) {
		// System.out.println("createTabContent 1 :: " + tag);
		ArrayAdapter<String> adapter;
		if (tag.equals("1")) {
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, items1);
			lv1 = new ListView(this);
			lv1.setAdapter(adapter);
			lv1.setOnItemClickListener(this);
			return lv1;

		} else {
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, items2);
			lv2 = new ListView(this);
			lv2.setAdapter(adapter);
			lv2.setOnItemClickListener(this);
			return lv2;
		}

	}

	public final void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}
}
