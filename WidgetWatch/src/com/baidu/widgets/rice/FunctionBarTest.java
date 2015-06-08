package com.baidu.widgets.rice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class FunctionBarTest extends Activity {

    private View functionBar;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Util.setYiTheme(this);
        setContentView(R.layout.function_bar);

	Button b = (Button)findViewById(R.id.anim);
	b.setText("show functionBar");
	b.setOnClickListener(showFunc);

	Button hide = (Button)findViewById(R.id.anim1);
	hide.setText("hide functionBar");
	hide.setOnClickListener(hideFunc);
	
	functionBar = findViewById(R.id.functionbar3);
    }

    private View.OnClickListener showFunc = new View.OnClickListener() {
		public void onClick(View v) {
			Animation anim = AnimationUtils.loadAnimation(FunctionBarTest.this, R.anim.yi_function_bar_show);
			functionBar.setVisibility(View.VISIBLE);
			functionBar.startAnimation(anim);	
		}
    	
    };

    private View.OnClickListener hideFunc = new View.OnClickListener() {
		public void onClick(View v) {
			Animation anim = AnimationUtils.loadAnimation(FunctionBarTest.this, R.anim.yi_function_bar_hide);
			functionBar.setVisibility(View.GONE);
			functionBar.startAnimation(anim);
		}
    };

} 
