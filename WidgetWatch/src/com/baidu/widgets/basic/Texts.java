package com.baidu.widgets.basic;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Window;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.text.TextPaint;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import yi.app.BaiduAppTitle;
public class Texts extends Activity  implements OnClickListener, OnLongClickListener {

	private Button btnEnable;
	private Button btnDisable;
	
	private EditText edtNormal;
	private EditText edtPassword;
	private TextView txtPrimary;
	private TextView txtSecondary;
	private TextView txtTertiary;
	private TextView txtSearch;
	//private BaiduAppTitle mBaiduAppTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        /* step1. request feature first */
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.texts);
		/*
        if(mBaiduAppTitle == null) {
            mBaiduAppTitle = new BaiduAppTitle(this);
        }

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, mBaiduAppTitle.getLayoutId());
        mBaiduAppTitle.installYiAppTitle();
        */
        
        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this); 
      
        edtNormal   = (EditText)findViewById(R.id.EditText01);        

        TextPaint p = edtNormal.getPaint();

        edtPassword = (EditText)findViewById(R.id.EditText02);
        txtPrimary = (TextView)findViewById(R.id.TextView01);  
        txtSecondary = (TextView)findViewById(R.id.TextView02);
        txtTertiary = (TextView)findViewById(R.id.TextView03);
        
        txtSearch = (TextView)findViewById(R.id.TextView04);
        //txtSearch.setOnClickListener(this);
        txtSearch.setTextIsSelectable(true);
    }

	public void onClick(View v) {
		int id = v.getId();
		
		Log.d("SYGTC", "onClick!");
		
		if (id == R.id.TextView04) {
			Log.d("SYGTC", "txtSearch clicked!");
			return;
		}
		
		if(id == R.id.btn_enable) {
			Log.i("enable","enable");
			edtNormal.setEnabled(true);
			edtPassword.setEnabled(true);
			txtPrimary.setEnabled(true);
			txtSecondary.setEnabled(true);
			txtTertiary.setEnabled(true);
		} else if(id == R.id.btn_disable) {
			Log.i("disable","disable");
			edtNormal.setEnabled(false);
			edtPassword.setEnabled(false);
			txtPrimary.setEnabled(false);
			txtSecondary.setEnabled(false);
			txtTertiary.setEnabled(false);
		}
		
		//test();
	}
	
	private void test() {
		Context context = this.getApplicationContext();		
		String path = context.getPackageResourcePath();
		Log.d("SYGTC", "getPackageResourcePath() = " + path);
		
		String s = this.getCustomResource().getString(R.string.txt1);
		Log.d("SYGTC", "getString() = " + s);
		
	}

	private Resources getCustomResource() {
		Context context = this.getApplicationContext();
		Resources resource = context.getResources();
		
		Configuration configuration = resource.getConfiguration();
		DisplayMetrics metrics = resource.getDisplayMetrics();		
		AssetManager asset = this.getAssets();
//
//		asset.addAssetPath("/data/data/WidgetWatch.apk");
		
		Resources r = new Resources(asset, metrics, configuration);
		return r;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return true;
	}
}
