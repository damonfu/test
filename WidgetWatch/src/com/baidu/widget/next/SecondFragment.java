/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SecondFragment extends Fragment implements SearchView.OnQueryTextListener, 
    View.OnClickListener, 
    View.OnFocusChangeListener  {

    int mNum;
    private static final String[] GENRES = new String[] {
        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
    private SearchView mSearchView;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static SecondFragment newInstance(int num) {
        SecondFragment f = new SecondFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        YiLaf.current().invalidateOptionsMenu();
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.second, container, false);
        
        mSearchView = (SearchView) v.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextFocusChangeListener(this);
        
        setupSearchView();
        ListView listView  = (ListView)v.findViewById(android.R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_checked, GENRES));
        YiLaf.current().getActionBar().setTabOnClickScrollView(1, listView);

        return v;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState.isEmpty()) {
            outState.putBoolean("bug:fix", true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.second, menu);
    }
    
    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.cheese_hunt_hint));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back_icon:
                Toast.makeText(getActivity(), "click", Toast.LENGTH_LONG).show();
                YiLaf.current().setContentViewExclusive(false);
                mSearchView.hideSearchBackIcon(true);
                break;

            default:
                break;
        }
    }
    
    @Override
    public void onFocusChange(android.view.View arg0, boolean arg1) {
//        if (arg0 == mSearchView) {
//            if (arg1) {
//                YiLaf.setContentViewExclusive(true);
//                mSearchView.showSearchBackIcon(this, true);
//            } else {
//                YiLaf.setContentViewExclusive(false);
//                mSearchView.hideSearchBackIcon(true);
//            }
//        }
        
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }
}
