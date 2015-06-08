package com.baidu.widgets.lists;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;
/**
 * Demonstrates expandable lists backed by a Simple Map-based adapter
 */
public class Expandable1 extends ExpandableListActivity {
    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";
    
    private ExpandableListAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        setContentView(R.layout.expandable_list_content);

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
        for (int i = 0; i < 4; i++) {
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, "Group " + i);
            curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even" : "This group is odd");
            
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < 4; j++) {
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
                R.layout.simple_expandable_list_item_group,
                new String[] { NAME, IS_EVEN },
                new int[] { R.id.text1, android.R.id.text2 },
                childData,
                R.layout.simple_expandable_list_item_child,
                new String[] { NAME, IS_EVEN },
                new int[] { R.id.text2, android.R.id.text2 }
                );
        setListAdapter(mAdapter);

        this.getExpandableListView().setSelector(android.R.color.transparent);
        this.getExpandableListView().setDivider(null);
        this.getExpandableListView().setIndicatorBounds(200, 250);
    }
}
