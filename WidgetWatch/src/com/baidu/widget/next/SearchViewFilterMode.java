/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.baidu.widget.next;

import yi.support.v1.YiLaf;
import yi.widget.SearchView;

import com.baidu.widgets.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Shows a list that can be filtered in-place with a SearchView in non-iconified mode.
 */
public class SearchViewFilterMode extends Activity implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchViewFilterMode";

    private SearchView mSearchView;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private final String[] mStrings = Cheeses.sCheeseStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setTheme(R.style.Yi_Theme_Light_ActionbarTabTop);
        setContentView(R.layout.searchview_filter);

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mStrings));
        mListView.setTextFilterEnabled(true);
        YiLaf.enable(this);
        setupSearchView();
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.cheese_hunt_hint));
        mSearchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(YiLaf.current().isContentViewExclusive())
				{
					mSearchView.collapseSearchViewDown();
				}
				else
				{
			        mSearchView.expandSearchViewUp(new OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mSearchView.clearFocus();
                        }
			        });
				}
			}
        });
        
        mSearchView.setVisibility(View.INVISIBLE);
        mSearchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchView.setVisibility(View.VISIBLE);
            }
        }, 100);
       
    }

    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
