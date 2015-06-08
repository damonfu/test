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

package com.baidu.widgets.rice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import yi.widget.ButtonGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.Window;
import yi.app.BaiduAppTitle;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class ButtonGroups extends Activity {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Util.setYiTheme(this);
        setContentView(R.layout.button_groups);
        
        final ButtonGroup buttonGroup = (ButtonGroup)findViewById(R.id.button_group_2);
        ButtonGroup.OnButtonCheckedListener l =  new ButtonGroup.OnButtonCheckedListener () {
            public void onButtonChecked (int id) {
                switch (id) {
                    case 0:
                        Toast.makeText(ButtonGroups.this, "Button 0 was Checked", Toast.LENGTH_SHORT).show();                       
                        break;
                    case 1:
                        Toast.makeText(ButtonGroups.this, "Button 1 was Checked", Toast.LENGTH_SHORT).show();
                        //buttonGroup.setBackgroundResource(R.drawable.button_group_background);
                        break;
                    case 2:
                        Toast.makeText(ButtonGroups.this, "Button 2 was Checked", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(ButtonGroups.this, "Button 3 was Checked", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(ButtonGroups.this, "Button 4 was Checked", Toast.LENGTH_SHORT).show();                        
                        break;
                }
            } 
        };

        buttonGroup.setOnButtonCheckedListener(l);
		
        
        ButtonGroup bg = new ButtonGroup(this, 3, 0);
        bg.setTitle(0, "abc");
        bg.setTitle(1, "ABC");
        bg.setTitle(2, "123");
        bg.setCheckedButton(1);
        //bg.setBackgroundResource(R.drawable.button_group_background);
        
        LinearLayout parent = (LinearLayout)findViewById(R.id.parent);
        parent.addView(bg);
       
    }
} 
