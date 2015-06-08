package com.baidu.widgets.ics;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class NumberPickerDemo extends Activity {
	NumberPicker mNumberPicker;
	EditText mMaxEditText;
	EditText mMinEditText;
	EditText mEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		setContentView(R.layout.numberpicker_demo);
		
		mNumberPicker = (NumberPicker) findViewById(R.id.numberPicker1);
		
		Button button = (Button) findViewById(R.id.enable_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNumberPicker.setEnabled(true);
			}
		});
		
		button = (Button) findViewById(R.id.disable_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNumberPicker.setEnabled(false);
			}
		});
		
		mMaxEditText = (EditText) findViewById(R.id.max_number);
		button = (Button) findViewById(R.id.max_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNumberPicker.setMaxValue(Integer.valueOf(mMaxEditText.getText().toString()));
			}
		});
		
		mMinEditText = (EditText) findViewById(R.id.min_number);
		button = (Button) findViewById(R.id.min_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNumberPicker.setMinValue(Integer.valueOf(mMinEditText.getText().toString()));
			}
		});
		
		mEditText = (EditText) findViewById(R.id.set_value);
		button = (Button) findViewById(R.id.set_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNumberPicker.setValue(Integer.valueOf(mEditText.getText().toString()));
			}
		});
		
		mNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			}
		});
		mNumberPicker.setMaxValue(20);
		mNumberPicker.setMinimumHeight(1);
		mNumberPicker.setValue(5);
		
		mNumberPicker.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				return "Formatter value "+value;
			}
		});
	}
}
