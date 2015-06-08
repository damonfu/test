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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.View;
import android.widget.Button;
//import yi.app.BaiduAppTitle;
/**
 * Demonstrates the use of progress dialogs.  Uses {@link Activity#onCreateDialog}
 * and {@link Activity#showDialog} to ensure the dialogs will be properly saved
 * and restored.
 */
public class ProgressBar3 extends Activity {

	private ProgressDialog mDialog1;
	private ProgressDialog mDialog2;
	private ProgressDialog mDialog3;
    private Handler mProgressHandler;
    private int mProgress = 0;
    private int MAX_PROGRESS = 100;

    private static final int DIALOG1_KEY = 0;
    private static final int DIALOG2_KEY = 1;
    private static final int DIALOG3_KEY = 2;    
    //private BaiduAppTitle mBaiduAppTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        /* step1. request feature first */
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.progressbar_3);
		/*
        if(mBaiduAppTitle == null) {
            mBaiduAppTitle = new BaiduAppTitle(this);
        }

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, mBaiduAppTitle.getLayoutId());
        mBaiduAppTitle.installYiAppTitle();
		*/

        Button button = (Button) findViewById(R.id.showIndeterminate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG1_KEY);
            }
        });

        button = (Button) findViewById(R.id.showIndeterminateNoTitle);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG2_KEY);
            }
        });
        button = (Button) findViewById(R.id.showAnother);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG3_KEY);
            }
        }); 
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgress >= MAX_PROGRESS) {
                	mDialog3.dismiss();
                	mDialog3.setProgress(0);
                } else {
                    mProgress++;
                    mDialog3.incrementProgressBy(1);
                    mProgressHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };        
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG1_KEY: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Indeterminate");
                dialog.setMessage("Please wait while loading...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
            case DIALOG2_KEY: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Please wait while loading...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
            case DIALOG3_KEY: {
            	mDialog3 = new ProgressDialog(ProgressBar3.this);
            	mDialog3.setIcon(R.drawable.icon);
            	mDialog3.setTitle("Title");
            	mDialog3.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            	mDialog3.setMax(MAX_PROGRESS);
            	mDialog3.setButton("Hide", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Yes so do some stuff */
                    }
                });
            	mDialog3.setButton2("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                    }
                });
            	mProgressHandler.sendEmptyMessage(0);
            	mDialog3.setProgress(0);
                return mDialog3;
            }
        }
        return null;
    }
}
