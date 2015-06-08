package com.baidu.widgets.lists;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.Window;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;
/**
 * Demonstrates expandable lists backed by a Simple Map-based adapter
 */
public class Expandable2 extends ExpandableListActivity {
    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";
    
    private ExpandableListAdapter mAdapter;   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get("Theme").equals("tabtop")) {
                setTheme(R.style.Theme_DeviceDefault_Light_DarkActionBar_ActionbarTabTop);
            }
        }
       
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, "Group " + i);
            curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even" : "This group is odd");
            
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < 15; j++) {
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(NAME, "Child " + j);
                curChildMap.put(IS_EVEN, (j % 2 == 0) ? "This child is even" : "This child is odd");
            }
            childData.add(children);
        }
        
        // Set up our adapter
        mAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME, IS_EVEN },
                new int[] { android.R.id.text1, android.R.id.text2 },
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME, IS_EVEN },
                new int[] { android.R.id.text1, android.R.id.text2 }
                );
        setListAdapter(mAdapter);
    }

}
