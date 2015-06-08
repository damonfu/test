package com.baidu.widgets.basic;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Misc extends Activity  implements OnClickListener {

	private Button btnEnable;
	private Button btnDisable;
	private Spinner spinner1;
	private SeekBar seek1;
	private RatingBar rb1;
	private RatingBar rb2;	
	private RatingBar rb3;	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        Util.setDeviceDefaultLightTheme(this);
        setContentView(R.layout.misc);

        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this); 
        
        spinner1 = (Spinner) findViewById(R.id.Spinner01);
        
        seek1    = (SeekBar) findViewById(R.id.SeekBar01);
        
        rb1      = (RatingBar) findViewById(R.id.RatingBar01);
        
        rb2      = (RatingBar) findViewById(R.id.RatingBar02); 
        
        rb3      = (RatingBar) findViewById(R.id.RatingBar03);         
 
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);        
        spinner1.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        showToast("Spinner1: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        showToast("Spinner1: unselected");
                    }
                });        
    }
    
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
	public void onClick(View v) {
		if(v.getId() == R.id.btn_enable) {
			Log.i("enable","enable");
			spinner1.setEnabled(true);
			seek1.setEnabled(true);
			rb1.setEnabled(true);
			rb2.setEnabled(true);
			rb3.setEnabled(true);
		} else if(v.getId() == R.id.btn_disable) {
			Log.i("disable","disable");
			spinner1.setEnabled(false);
			seek1.setEnabled(false);
			rb1.setEnabled(false);
			rb2.setEnabled(false);
			rb3.setEnabled(false);
		}
	}

}
