package com.baidu.widgets.ics;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupMenu;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class PopupMenuDemo extends Activity implements OnClickListener {
	View redView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		setContentView(R.layout.popuplayout_demo);
		Button button = null;
		
		button = (Button) findViewById(R.id.popupmenu_normal);
		button.setOnClickListener(this);
		button.setText("Normal PopupMenu");
		
		button = (Button) findViewById(R.id.popupmenu_group);
		button.setOnClickListener(this);
		button.setText("Group PopupMenu");
		
		button = (Button) findViewById(R.id.popupmenu_submenu);
		button.setOnClickListener(this);
		button.setText("Submenu PopupMenu");
		
		button = (Button) findViewById(R.id.popupmenu_display_in_red_area);
		button.setOnClickListener(this);
		button.setText("PopupMenu display from left red area");
		
		redView = new View(this);
		redView.setBackgroundColor(Color.RED);
		redView.setMinimumHeight(10);
		redView.setMinimumWidth(10);
		redView.setX(100);
		redView.setY(300);
		
		addContentView(redView, new LayoutParams(10, 10));
	}

	@Override
	public void onClick(View v) {
		PopupMenu popupMenu = new PopupMenu(this, v);
		int id = -1;
		switch (v.getId()) {
		case R.id.popupmenu_normal:
			id = R.menu.popupmenu_normal;
			break;
			
		case R.id.popupmenu_group:
			id = R.menu.popupmenu_group;
			break;
			
		case R.id.popupmenu_submenu:
			id = R.menu.popupmenu_submenu;
			break;
			
		case R.id.popupmenu_display_in_red_area:
			popupMenu = new PopupMenu(this, redView);
			id = R.menu.popupmenu_normal;
			popupMenu.inflate(id);
			popupMenu.show();
			return;

		default:
			break;
		}
		if (id > 0) {
			popupMenu.inflate(id);
			popupMenu.show();
		}
	}
}
