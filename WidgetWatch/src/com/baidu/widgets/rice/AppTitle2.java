package com.baidu.widgets.rice;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import yi.widget.ButtonGroup;
import yi.widget.ButtonGroup.OnButtonCheckedListener;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import yi.app.BaiduAppTitle;


public class AppTitle2 extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	Button btn_l0;
	Button btn_l1;
	Button btn_l2;
	Button btn_l3;
	Button btn_l4;
	Button btn_r0;
	Button btn_r1;
	Button btn_r2;
	Button btn_r3;
	Button btn_r4;	
	Button btn_r5;
	Button btn_r6;	
	ButtonGroup bg;    
    Button btn_set_left;
    Button btn_set_center;  
    Button btn_set_right;   
    
    EditText edt_left;
    EditText edt_center;    
    EditText edt_right; 

    private boolean left_enable;
    private boolean right_enable;
    private BaiduAppTitle mBaiduAppTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setAppTitle(this);
        /* step1. request feature first */
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        /* step2. set content view */
        setContentView(R.layout.yi_app_title);

        if(mBaiduAppTitle == null) {
            mBaiduAppTitle = new BaiduAppTitle(this);
        }

        mBaiduAppTitle.setBtnGrpStyle(true);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, mBaiduAppTitle.getLayoutId());
        mBaiduAppTitle.installYiAppTitle();

        // use a Animation
        //this.getWindow().setWindowAnimations(android.R.style.Animation_Activity_Rotate);
        
        mBaiduAppTitle.setYiTitleLeftLabel("Left");
        mBaiduAppTitle.setYiTitleRightLabel("Right");

        btn_l0 = (Button) findViewById(R.id.btnl0);
        btn_l0.setOnClickListener(this);
        btn_l1 = (Button) findViewById(R.id.btnl1);
        btn_l1.setOnClickListener(this);
        btn_l2 = (Button) findViewById(R.id.btnl2);
        btn_l2.setOnClickListener(this);
        btn_l3 = (Button) findViewById(R.id.btnl3);
        btn_l3.setOnClickListener(this);

        btn_r0 = (Button) findViewById(R.id.btnr0);
        btn_r0.setOnClickListener(this);        
        btn_r1 = (Button) findViewById(R.id.btnr1);
        btn_r1.setOnClickListener(this);
        btn_r2 = (Button) findViewById(R.id.btnr2);
        btn_r2.setOnClickListener(this);
        btn_r3 = (Button) findViewById(R.id.btnr3);
        btn_r3.setOnClickListener(this);
        btn_r5 = (Button) findViewById(R.id.btnr5);
        btn_r5.setOnClickListener(this);
        btn_r6 = (Button) findViewById(R.id.btnr6);
        btn_r6.setOnClickListener(this);

        btn_set_left = (Button) findViewById(R.id.btn_set_left);
        btn_set_left.setOnClickListener(this);
        btn_set_center = (Button) findViewById(R.id.btn_set_center);
        btn_set_center.setOnClickListener(this);
        btn_set_right = (Button) findViewById(R.id.btn_set_right);
        btn_set_right.setOnClickListener(this);
        
        edt_left = (EditText) findViewById(R.id.edt_left);
        edt_center = (EditText) findViewById(R.id.edt_center);
        edt_right = (EditText) findViewById(R.id.edt_right);  
        
        left_enable = false;
        right_enable = false;

        bg     = (ButtonGroup) findViewById(R.id.title_button_group);
        if (bg != null) {
			bg.setTitle(0, "abc");
			bg.setTitle(1, "ABC");

			bg.setOnButtonCheckedListener(new OnButtonCheckedListener() {
				public void onButtonChecked(int id) {
					switch (id) {
					case 0:
						showToast("BG0 clicked");
						break;
					case 1:
						showToast("BG1 clicked");
						break;
					}
				}
			});
		}
    }
    
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	public void onYiTitleLeftButtonClicked(View v) {
		showToast("Left Clicked");
	}

	public void onYiTitleRightButtonClicked(View v) {
		showToast("Right Clicked");
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.btnl0) {
			//setRiceTitleLeftLevel(0);
		}else if(v.getId() == R.id.btnl1) {
			//setRiceTitleLeftLevel(1);
		} else if(v.getId() == R.id.btnl2) {
			//setRiceTitleLeftLevel(2);
		} else if(v.getId() == R.id.btnl3) {
			//setRiceTitleLeftLevel(3);
		} else if(v.getId() == R.id.btnr0) {
			//setRiceTitleRightLevel(0);
		} else if(v.getId() == R.id.btnr1) {
			//setRiceTitleRightLevel(1);
		} else if(v.getId() == R.id.btnr2) {
			//setRiceTitleRightLevel(2);
		} else if(v.getId() == R.id.btnr3) {
			//setRiceTitleRightLevel(3);
		}else if(v.getId() == R.id.btn_set_left) {
            mBaiduAppTitle.setYiTitleLeftLabel(edt_left.getText().toString()); 

        } else if(v.getId() == R.id.btn_set_center) {
            mBaiduAppTitle.setYiTitleCenterLabel(edt_center.getText().toString());

        } else if(v.getId() == R.id.btn_set_right) {
            mBaiduAppTitle.setYiTitleRightLabel(edt_right.getText().toString());

        } else if (v.getId() == R.id.btnr5) {
            left_enable = !left_enable;
            mBaiduAppTitle.setYiTitleLeftButtonEnable(left_enable);

        } else if (v.getId() == R.id.btnr6) {
            right_enable = !right_enable;
            mBaiduAppTitle.setYiTitleRightButtonEnable(right_enable);
        }
	}
}
