package com.baidu.widgets.rice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.view.Window;

public class Indicators extends Activity implements OnClickListener{

	private Button btnEnable;
	private Button btnDisable;	

	private ImageView indDown;	
	private ImageView indRight;
	//private ImageView indPlus;	
	private ImageView indInfo;
	private ImageView indCancel;
	private ImageView indBlackCancel;
	//private ImageView indNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setYiTheme(this);
        setContentView(R.layout.indicators);
       
        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this);    
        
        indDown = (ImageView)findViewById(R.id.ind_down);
        indRight = (ImageView)findViewById(R.id.ind_right);
        //indPlus = (ImageView)findViewById(R.id.ind_plus);
        indInfo = (ImageView)findViewById(R.id.ind_info); 
        indCancel = (ImageView)findViewById(R.id.ind_cancel); 
        indBlackCancel = (ImageView)findViewById(R.id.ind_black_cancel); 
        //indNoti = (ImageView)findViewById(R.id.ind_noti); 
    }

	public void onClick(View v) {
		if(v.getId() == R.id.btn_enable) {
			indDown.setEnabled(true);
			indRight.setEnabled(true);
			//indPlus.setEnabled(true);
			indInfo.setEnabled(true);
			indCancel.setEnabled(true);
			indBlackCancel.setEnabled(true);
			//indNoti.setEnabled(true);
		} else if(v.getId() == R.id.btn_disable) {
			indDown.setEnabled(false);
			indRight.setEnabled(false);
			//indPlus.setEnabled(false);
			indInfo.setEnabled(false);
			indCancel.setEnabled(false);
			indBlackCancel.setEnabled(false);
			//indNoti.setEnabled(false);
		}
		
	}
    
    
}
