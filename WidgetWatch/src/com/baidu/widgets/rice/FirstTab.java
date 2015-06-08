package com.baidu.widgets.rice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import android.widget.Toast;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class FirstTab extends Activity { 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        Util.setYiTheme(this);
        setContentView(R.layout.tab1);   
    } 
    public void onResume() {
    	super.onResume();
                
        Toast.makeText(this, "This is The First Tab", Toast.LENGTH_SHORT).show();
         
    } 
}
