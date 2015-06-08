package com.baidu.widgets.ics;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class ActionBarSplit extends Activity {
	ActionBar mActionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		TextView textView = new TextView(this);
		textView.setText("Split Action Bar: add uiOptions=\"splitActionBarNarrow\" to <activity> or <application> in manifest.xml ");
		
		setContentView(textView);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mActionBar = getActionBar();
		mActionBar.setTitle("Split Action Bar");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_demo, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "menu "+item.getTitle()+" selected!", Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}
}
