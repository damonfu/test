package com.baidu.widgets.ics;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class CalendarViewDemo extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		setContentView(R.layout.calendarview_demo);
		
		showDialog(0);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while loading...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
	}
}
