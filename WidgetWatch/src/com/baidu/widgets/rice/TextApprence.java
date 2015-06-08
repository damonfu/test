package com.baidu.widgets.rice;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Window;
public class TextApprence extends Activity implements OnClickListener{
    
    private Button btnEnable;
    private Button btnDisable;  
    
    private TextView t1;
    private TextView t2;    
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private TextView t7;    
    private TextView t8;
    private TextView t9;
    private TextView t10;
    
    private EditText e1;
    private EditText e2;    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setYiTheme(this);
        setContentView(R.layout.text_apprence);

        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this); 
        
        t1 = (TextView)findViewById(R.id.TextView01);
        t2 = (TextView)findViewById(R.id.TextView11); 
        t3 = (TextView)findViewById(R.id.TextView21);
        t4 = (TextView)findViewById(R.id.TextView31);         
        t5 = (TextView)findViewById(R.id.TextView41);
        t6 = (TextView)findViewById(R.id.TextView01); 
        t7 = (TextView)findViewById(R.id.TextView12);
        t8 = (TextView)findViewById(R.id.TextView22); 
        t9 = (TextView)findViewById(R.id.TextView32);
        t10 = (TextView)findViewById(R.id.TextView42);  
        
        e1 = (EditText)findViewById(R.id.EditText01);  
        e2 = (EditText)findViewById(R.id.EditText02);         
    }
    public void onClick(View v) {
        if(v.getId() == R.id.btn_enable) {
            t1.setEnabled(true);
            t2.setEnabled(true);
            t3.setEnabled(true);
            t4.setEnabled(true);
            t5.setEnabled(true);
            t6.setEnabled(true);
            t7.setEnabled(true);
            t8.setEnabled(true);
            t9.setEnabled(true);
            t10.setEnabled(true);  
            e1.setEnabled(true);  
            e2.setEnabled(true); 
        } else if(v.getId() == R.id.btn_disable){
            t1.setEnabled(false);
            t2.setEnabled(false);
            t3.setEnabled(false);
            t4.setEnabled(false);
            t5.setEnabled(false);
            t6.setEnabled(false);
            t7.setEnabled(false);
            t8.setEnabled(false);
            t9.setEnabled(false);
            t10.setEnabled(false); 
            e1.setEnabled(false);  
            e2.setEnabled(false);             
        }
        
    }
}
