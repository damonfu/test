package com.baidu.widgets.basic;

import java.util.Calendar;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTime extends Activity implements OnClickListener {

	private Button btnEnable;
	private Button btnDisable;
	private Button btnTime;
	private Button btnDate;
	
	private TimePicker tp;
	private DatePicker dp;	

    static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 1;
    
    // date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setDeviceDefaultLightTheme(this);

        setContentView(R.layout.datetime);
        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this);  
        
        btnTime = (Button)findViewById(R.id.btn_time);
        btnTime.setOnClickListener(this);
        
        btnDate = (Button)findViewById(R.id.btn_date);
        btnDate.setOnClickListener(this);        
        
        tp = (TimePicker)findViewById(R.id.TimePicker01);
        dp = (DatePicker)findViewById(R.id.DatePicker01);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }
	
	public void onClick(View v) {
		if(v.getId() == R.id.btn_enable) {
			Log.i("enable","enable");
			tp.setEnabled(true);
			dp.setEnabled(true);
		} else if(v.getId() == R.id.btn_disable) {
			Log.i("disable","disable");
			tp.setEnabled(false);
			dp.setEnabled(false);
		} else if(v.getId() == R.id.btn_time) {
			showDialog(TIME_DIALOG_ID);
		} else if(v.getId() == R.id.btn_date) {
			showDialog(DATE_DIALOG_ID);
		}
	}
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        null, mHour, mMinute, false);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            null,
                            mYear, mMonth, mDay);
        }
        return null;
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }   
}
