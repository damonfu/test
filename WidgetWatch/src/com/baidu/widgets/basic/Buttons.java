package com.baidu.widgets.basic;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class Buttons extends Activity implements OnClickListener {

	private Button btnEnable;
	private Button btnDisable;	
	
	private Button btnNormal;
	private Button btnSmall;
	private CheckedTextView ctv;
	private ToggleButton btnToggle;
	private CheckBox btnCheck0;
	private CheckBox btnCheck1;	
	private RadioButton btnRatio0;
	private RadioButton btnRatio1;	
	private RadioButton btnRatio3;	
	
	private ImageButton btnImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setDeviceDefaultLightTheme(this);
        setContentView(R.layout.buttons);

        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this);    
        
    	btnNormal = (Button)findViewById(R.id.Button01);
    	btnSmall  = (Button)findViewById(R.id.Button02);
        ctv = (CheckedTextView)findViewById(R.id.CheckedTextView01);
    	btnToggle = (ToggleButton)findViewById(R.id.ToggleButton01);
    	
    	btnCheck0 = (CheckBox)findViewById(R.id.CheckBox01);
    	btnCheck0.setText(android.R.string.cancel);
    	
    	btnCheck1 = (CheckBox)findViewById(R.id.CheckBox02);
    	btnRatio0 = (RadioButton)findViewById(R.id.RadioButton01);
    	btnRatio1 = (RadioButton)findViewById(R.id.RadioButton02);
    	btnRatio3 = (RadioButton)findViewById(R.id.RadioButton03);
    	
    	btnImage  = (ImageButton)findViewById(R.id.ImageButton01);
    	
    	int padding = btnRatio3.getCompoundDrawablePadding();
    	Log.d("SYGTC", "getCompoundDrawablePadding = " + padding);
    	padding = btnRatio0.getCompoundDrawablePadding();
    	Log.d("SYGTC", "getCompoundDrawablePadding = " + padding);
    	padding = btnRatio1.getCompoundDrawablePadding();
    	Log.d("SYGTC", "getCompoundDrawablePadding = " + padding);
    	ctv.setOnClickListener(this);
    	
    }
    
	public void onClick(View v) {
		if(v.getId() == R.id.CheckedTextView01) {
			ctv.setChecked(!ctv.isChecked());
		} else if(v.getId() == R.id.btn_enable) {
			btnNormal.setEnabled(true);
			btnSmall.setEnabled(true);
			ctv.setEnabled(true);
			btnToggle.setEnabled(true);
			btnCheck0.setEnabled(true);
			btnCheck1.setEnabled(true);
			btnRatio0.setEnabled(true);
			btnRatio1.setEnabled(true);
			btnImage.setEnabled(true);
			test();
		} else if(v.getId() == R.id.btn_disable) {
			btnNormal.setEnabled(false);
			btnSmall.setEnabled(false);
			ctv.setEnabled(false);
			btnToggle.setEnabled(false);
			btnCheck0.setEnabled(false);
			btnCheck1.setEnabled(false);
			btnRatio0.setEnabled(false);
			btnRatio1.setEnabled(false);
			btnImage.setEnabled(false);
		}
		
	}
    
	
    private void test() {
    	NotificationManager nm = (NotificationManager) this.getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification n = new Notification();
    	n.icon = R.drawable.sygtc;
    	n.setLatestEventInfo(this, "sygtc", "sygtc", PendingIntent.getActivity(this, 0, new Intent(), 0));
    	nm.notify(0, n);
    	
    	Notification n1 = new Notification();
    	n1.icon = R.drawable.stat_sys_data_connected_h;
    	n1.setLatestEventInfo(this, "stat_sys_data_connected_h", "stat_sys_data_connected_h", PendingIntent.getActivity(this, 0, new Intent(), 0));
    	nm.notify(1, n1);

    	Notification n2 = new Notification();
    	n2.icon = R.drawable.stat_sys_data_connected_n;
    	n2.setLatestEventInfo(this, "stat_sys_data_connected_n", "stat_sys_data_connected_n", PendingIntent.getActivity(this, 0, new Intent(), 0));
    	nm.notify(2, n2);

    	/*
		android:adjustViewBounds="true"
    	android:scaleType="centerInside" 
    	 */
    	LinearLayout parent = (LinearLayout)findViewById(R.id.LinearLayout01);
    	
    	ImageView imageView = new ImageView(this);
    	imageView.setAdjustViewBounds(true);
    	imageView.setScaleType(ScaleType.CENTER_INSIDE);
    	imageView.setImageResource(R.drawable.sygtc);
    	parent.addView(imageView);

    	ImageView imageView2 = new ImageView(this);
    	imageView2.setAdjustViewBounds(true);
    	imageView2.setScaleType(ScaleType.CENTER_INSIDE);
    	imageView2.setImageResource(R.drawable.stat_sys_data_connected_h);
    	parent.addView(imageView2);
    	
    	ImageView imageView3 = new ImageView(this);
    	imageView3.setAdjustViewBounds(true);
    	imageView3.setScaleType(ScaleType.CENTER_INSIDE);
    	imageView3.setImageResource(R.drawable.stat_sys_data_connected_n);
    	parent.addView(imageView3);
    }
}
