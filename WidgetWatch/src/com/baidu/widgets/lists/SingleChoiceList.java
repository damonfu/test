/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.baidu.widgets.lists;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.view.Window;
/**
 * This example shows how to use choice mode on a list. This list is 
 * in CHOICE_MODE_SINGLE mode, which means the items behave like
 * checkboxes.
 */
public class SingleChoiceList extends ListActivity {    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, GENRES));
	    /*
        if(mBaiduAppTitle == null) {
            mBaiduAppTitle = new BaiduAppTitle(this);
        }

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, mBaiduAppTitle.getLayoutId());
        mBaiduAppTitle.installYiAppTitle();
		*/
        
        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDividerHeight(5);
        listView.setFadingEdgeLength(0);
        listView.setSmoothScrollbarEnabled(true);
    }


    private static final String[] GENRES = new String[] {
        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
}
