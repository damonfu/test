package com.baidu.widgets.rice;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import yi.app.BaiduAppTitle;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class AppTitle1 extends Activity implements OnClickListener, BaiduAppTitle.OnYiLeftClickedListener, BaiduAppTitle.OnYiRightClickedListener{
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
	
    Button btn_set_left;
    Button btn_set_center;  
    Button btn_set_right;   
    
    EditText edt_left;
    EditText edt_center;    
    EditText edt_right;

    Button btn_set_left_enble;	
    Button btn_set_right_enble;   
    
    boolean left_enable = true;
    boolean right_enable = true;

    BaiduAppTitle baiduAppTitle;
    int mAppTitleId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setAppTitle(this);
        /* step1. request feature first */
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        // use a Animation
        this.getWindow().setWindowAnimations(R.style.Yi_Animation_Activity_New);
       
        /* step1. set content view */
        setContentView(R.layout.yi_app_title);

        if(baiduAppTitle == null) {
            baiduAppTitle = new BaiduAppTitle(this);
        }

        mAppTitleId = baiduAppTitle.getLayoutId();
        if(mAppTitleId != 0) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, mAppTitleId);
            baiduAppTitle.installYiAppTitle();
 
        	//setTitle("Center");
            baiduAppTitle.setYiTitleLeftLabel("Left");
            baiduAppTitle.setYiTitleRightLabel("Right");
        	baiduAppTitle.setYiTitleCenterLabel("Center");
        	baiduAppTitle.setOnLeftClickedListener(this);
        	baiduAppTitle.setOnRightClickedListener(this);

        }

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
        
        btn_set_left = (Button) findViewById(R.id.btn_set_left);
        btn_set_left.setOnClickListener(this);
        btn_set_center = (Button) findViewById(R.id.btn_set_center);
        btn_set_center.setOnClickListener(this);
        btn_set_right = (Button) findViewById(R.id.btn_set_right);
        btn_set_right.setOnClickListener(this);
        
        btn_set_left_enble = (Button) findViewById(R.id.btnr5);
        btn_set_left_enble.setOnClickListener(this);

        btn_set_right_enble = (Button) findViewById(R.id.btnr6);
        btn_set_right_enble.setOnClickListener(this);
        
        edt_left = (EditText) findViewById(R.id.edt_left);
        edt_center = (EditText) findViewById(R.id.edt_center);
        edt_right = (EditText) findViewById(R.id.edt_right);         

        left_enable = true;
        right_enable = true;
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
		} else if(v.getId() == R.id.btn_set_left) {
            baiduAppTitle.setYiTitleLeftLabel(edt_left.getText().toString()); 
        } else if(v.getId() == R.id.btn_set_center) {
            baiduAppTitle.setYiTitleCenterLabel(edt_center.getText().toString());
        } else if(v.getId() == R.id.btn_set_right) {
            baiduAppTitle.setYiTitleRightLabel(edt_right.getText().toString());
        } else if (v.getId() == R.id.btnr5) {
            left_enable = !left_enable;
			//setYiTitleLeftButtonEnable(left_enable);
			baiduAppTitle.setYiTitleVisibility(left_enable ? View.VISIBLE : View.INVISIBLE);
        } else if (v.getId() == R.id.btnr6) {
            right_enable = !right_enable;
			//setYiTitleRightButtonEnable(right_enable);
			baiduAppTitle.setYiTitleVisibility(right_enable ? View.VISIBLE : View.GONE);
        }
	}
}
