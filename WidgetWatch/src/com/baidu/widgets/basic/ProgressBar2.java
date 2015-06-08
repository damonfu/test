/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.widgets.basic;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;


/**
 * Demonstrates the use of indeterminate progress bars as widgets and in the
 * window's title bar. The widgets show the 3 different sizes of circular
 * progress bars that can be used.
 */
public class ProgressBar2 extends Activity  implements OnClickListener {
	private Button btnEnable;
	private Button btnDisable;
	private ProgressBar progress1;
	private ProgressBar progress2;	
	private ProgressBar progress3;
	private ProgressBar progress4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setDeviceDefaultLightTheme(this);

        // Request for the progress bar to be shown in the title
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.progressbar_2);
        
        // Make sure the progress bar is visible
        setProgressBarVisibility(true);
        //btnEnable = (Button)findViewById(R.id.btn_enable);
        //btnEnable.setOnClickListener(this);
        
        //btnDisable = (Button)findViewById(R.id.btn_disable);
        //btnDisable.setOnClickListener(this);  
        
        progress1 = (ProgressBar) findViewById(android.R.id.progress);
        progress2 = (ProgressBar) findViewById(R.id.progress_large);  
        progress3 = (ProgressBar) findViewById(R.id.progress_small);      
        progress4 = (ProgressBar) findViewById(R.id.progress_small_title);         
    }
	public void onClick(View v) {
		if(v.getId() == R.id.btn_enable) {
			Log.i("Progress","Enabled");
			progress1.setEnabled(true);
			progress2.setEnabled(true);
			progress3.setEnabled(true);
			progress4.setEnabled(true);			
		} else if(v.getId() == R.id.btn_disable) {
			Log.i("Progress","Disabled");
			progress1.setEnabled(false);
			progress2.setEnabled(false);
			progress3.setEnabled(false);
			progress4.setEnabled(false);	
		}
	}
}
