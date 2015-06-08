package com.baidu.widgets.rice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import yi.widget.Switcher;
//import yi.widget.Switcher.OnSwitcherChangeListener;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.view.Window;

public class RiceButtons extends Activity implements OnClickListener { //, OnSwitcherChangeListener{

	private Button btnEnable;
	private Button btnDisable;	

    private Button btnGreenAdd;
    private Button btnRedDel;	
		
	//private Switcher btnSwitcher;
	//private TextView txtSwitcher;
	
	private EditText edit0, edit1;
	
	private LinearLayout mButtons;
	
	private LinearLayout mSearchLayout;
	private EditText mSearchText;
	private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setYiTheme(this);

        setContentView(R.layout.yi_buttons);
        
        btnEnable = (Button)findViewById(R.id.btn_enable);
        btnEnable.setOnClickListener(this);
        
        btnDisable = (Button)findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(this);    
        
        mButtons = (LinearLayout)findViewById(R.id.buttons);
        
        /*btnSwitcher   = (Switcher)findViewById(R.id.btn_switcher);
        btnSwitcher.setOnSwitcherChangeListener(this);
        txtSwitcher   = (TextView)findViewById(R.id.txt_switcher); 
        txtSwitcher.setText(btnSwitcher.isToggled()?"--ON--":"--OFF-");*/
	
	    btnGreenAdd = (Button)findViewById(R.id.btn_green_add);
	    btnRedDel = (Button)findViewById(R.id.btn_red_del);
	    
	    edit0  = (EditText)findViewById(R.id.edit0);
	    edit1  = (EditText)findViewById(R.id.edit1);
	    
	    mSearchLayout = (LinearLayout)findViewById(R.id.search_layout);
	    mSearchText = (EditText)findViewById(R.id.search_text);
	    mSearchButton = (Button)findViewById(R.id.search_button);
	    mSearchButton.setOnClickListener(this);
    }

	public void onClick(View v) {
		if(v.getId() == R.id.btn_enable) {
			
			int buttonCount = mButtons.getChildCount();
			for (int i = 0; i < buttonCount; i++) {
				mButtons.getChildAt(i).setEnabled(true);
			}
			
			//btnSwitcher.setEnabled(true);
			btnGreenAdd.setEnabled(true);
			btnRedDel.setEnabled(true);
			edit0.setEnabled(true);
			edit1.setEnabled(true);
		} else if(v.getId() == R.id.btn_disable) {

			int buttonCount = mButtons.getChildCount();
			for (int i = 0; i < buttonCount; i++) {
				mButtons.getChildAt(i).setEnabled(false);
			}
			
			//btnSwitcher.setEnabled(false);
            btnGreenAdd.setEnabled(false);
            btnRedDel.setEnabled(false);
            edit0.setEnabled(false);
            edit1.setEnabled(false);
		} else if(v.getId() == R.id.search_button) {
			Toast.makeText(this, "This is The Search Button", Toast.LENGTH_SHORT).show();
			Log.d("SYGTC", "mSearchText = " + mSearchText.toString());
		}
		
	}
	/*public void onSwitcherToggleChanged(Switcher arg0, boolean arg1) {
		txtSwitcher.setText(arg1?"--ON--":"--OFF-");
	}*/
}
