package com.baidu.widgets.rice;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import java.util.Calendar;
import android.util.Log;
import android.os.SystemClock;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.view.Window;

public class DateAndTimeDemo extends Activity  implements TimePickerDialog.OnTimeSetListener , DatePickerDialog.OnDateSetListener {

	private final int ID1 = 0;
	private final int ID2 = 1;
	private final int ID3 = 2;
	private final int ID4 = 3;
	private final int ID5 = 4;
	private final int ID6 = 5;

    private static final int DIALOG_DATEPICKER = 0;
    private static final int DIALOG_TIMEPICKER = 1;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Util.setYiTheme(this);
        setContentView(R.layout.date_time);

        Button btnShow1 = (Button) findViewById(R.id.showTimeSetting);
		btnShow1.setText("show TimeSetting");
		btnShow1.setOnClickListener(showTime);

        Button btnShow2 = (Button) findViewById(R.id.showDateSetting);
		btnShow2.setText("show DateSetting");
		btnShow2.setOnClickListener(showDate);
    }

    private View.OnClickListener showTime= new View.OnClickListener() {

	    public void onClick(View v) {
                    showDialog(DIALOG_TIMEPICKER);
		    Log.d("samlog","TimePickerDialog will be created ");
	    }                	
    	                     	
    };                       	
                             	
    @Override
    public Dialog onCreateDialog(int id) {
        Dialog d= null;
	Log.d("samlog","onCreateDialog");

        switch (id) {
        case DIALOG_DATEPICKER: {
            final Calendar calendar = Calendar.getInstance();
            d = new DatePickerDialog(
                DateAndTimeDemo.this,
                DateAndTimeDemo.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
            d.setTitle("set Date");
	    	
            break;
        }
        case DIALOG_TIMEPICKER: {
            final Calendar calendar = Calendar.getInstance();
            Log.d("samlog","TimePickerDialog will be created ");
            d = new TimePickerDialog(
			    DateAndTimeDemo.this,
			    DateAndTimeDemo.this,
			    calendar.get(Calendar.HOUR_OF_DAY),
			    calendar.get(Calendar.MINUTE),
			    false);
	    	d.setTitle("Set Time");
            break;
        }
        default:
            d = null;
            break;
        }

        return d;
    }
    @Override
    public void onPrepareDialog(int id, Dialog d) {
		    Log.d("samlog","onPrepareDialog");
        switch (id) {
        case DIALOG_DATEPICKER: {
           // DatePickerDialog datePicker = (DatePickerDialog)d;
           // final Calendar calendar = Calendar.getInstance();
           // datePicker.updateDate(
           //         calendar.get(Calendar.YEAR),
           //         calendar.get(Calendar.MONTH),
           //         calendar.get(Calendar.DAY_OF_MONTH));
            break;
        }
        case DIALOG_TIMEPICKER: {
            Log.d("samlog","TimePickerDialog will be created ");
            TimePickerDialog timePicker = (TimePickerDialog)d;
            final Calendar calendar = Calendar.getInstance();
            timePicker.updateTime(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE));
            break;
        }
        default:
            break;
        }
    }

    private View.OnClickListener showDate= new View.OnClickListener() {

		public void onClick(View v) {
                    showDialog(DIALOG_DATEPICKER);
		}
    	
    };

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        //updateTimeAndDateDisplay();

        // We don't need to call timeUpdated() here because the TIME_CHANGED
        // broadcast is sent by the AlarmManager as a side effect of setting the
        // SystemClock time.
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        Log.d("SYGTC", "year = " + year);
        Log.d("SYGTC", "month = " + month);
        Log.d("SYGTC", "day = " + day);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
    //    updateTimeAndDateDisplay();
    }
} 
