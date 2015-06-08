package com.baidu.widgets.ics;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class GridLayoutDemo extends Activity{
	TextView mTextView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		setContentView(R.layout.gridlayout_demo);
		
		GridLayout gridLayout = (GridLayout) findViewById(R.id.gridlayout);
		
		mTextView = (TextView) findViewById(R.id.formula);
		mTextView.setSingleLine(false);

		Button button = null;
		View childView = null;
		for (int i = 0; i < gridLayout.getChildCount(); i++) {
			childView = gridLayout.getChildAt(i);
			if (childView.getClass().equals(Button.class)) {
				button = (Button) childView;
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CharSequence s = ((Button) v).getText();
						if (s.equals("Cls")){
							mTextView.setText("");
						} else {
							mTextView.append(((Button) v).getText());
						}
					}
				});
			}
		}
	}
}
