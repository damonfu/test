package com.baidu.widgets.rice;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class AppTitleUser extends Activity implements OnClickListener{

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
    
    Button btn_set_left_enble;	
    Button btn_set_right_enble;   
    boolean left_enable = true;
    boolean right_enable = true;

    EditText edt_left;
    EditText edt_center;    
    EditText edt_right;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setAppTitle(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.yi_app_title);
        
        btn_l0 = (Button)findViewById(R.id.btnl0);
        btn_l0.setOnClickListener(this);
        btn_l1 = (Button)findViewById(R.id.btnl1);
        btn_l1.setOnClickListener(this);

        // For Animation start
        try {
			mView = (View)(findViewById(android.R.id.content).getParent().getParent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initAnimation();
		// For Animation end 
    }
    
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	public void onClick(View v) {
		if (mView == null) {
			Log.d("SYGTC", "mView == NULL");
			return;
		}
		
		int id = v.getId();
		
		if (id == R.id.btnl0) {
			mView.startAnimation(mAnimationOne);
		} else {
			mView.startAnimation(mAnimationThree);		
		}
	}
	
	// For Animation start
	
	private View mView;
	private Animation mAnimationOne;
	private Animation mAnimationTwo;
	private Animation mAnimationThree;
	private Animation mAnimationFour;
	private static final int mAnimationTime = 400;
	
	private void initAnimation() {
		DisplayMetrics dm = getResources().getDisplayMetrics();

        int centerX = dm.widthPixels / 2;
        int centerY = dm.heightPixels / 2;
        
//		mAnimationOne = new Rotate3dAnimation(0, 90, centerX, centerY);
//		mAnimationOne.setDuration(mAnimationTime);
//		mAnimationOne.setFillAfter(true);
//		mAnimationOne.setAnimationListener(mListener);
//		mAnimationOne.setInterpolator(new DecelerateInterpolator());
//		
//		mAnimationTwo = new Rotate3dAnimation(-90, 0, centerX, centerY);
//		mAnimationTwo.setDuration(mAnimationTime);
//		mAnimationTwo.setInterpolator(new AccelerateInterpolator());
//		
//		mAnimationThree = new Rotate3dAnimation(0, -90, centerX, centerY);
//		mAnimationThree.setDuration(mAnimationTime);
//		mAnimationThree.setFillAfter(true);
//		mAnimationThree.setAnimationListener(mListener);
//		mAnimationThree.setInterpolator(new DecelerateInterpolator());
//		
//		mAnimationFour = new Rotate3dAnimation(90, 0, centerX, centerY);
//		mAnimationFour.setDuration(mAnimationTime);
//		mAnimationFour.setInterpolator(new AccelerateInterpolator());
	}
	
	private AnimationListener mListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// you can use another View here
			if (animation == mAnimationOne) {
				mView.startAnimation(mAnimationTwo);
			} else if (animation == mAnimationThree) {
				mView.startAnimation(mAnimationFour);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	};
	
	// For Animation end
}
