package com.baidu.widgets;

import android.app.Activity;
import android.os.Bundle;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
//import yi.app.BaiduAppTitle;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.System;

import com.baidu.widgets.R;

public class WidgetWatch extends ListActivity {

    private static final int CHANGE_THEME_ID = 0x7;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setDeviceDefaultLightTheme(this);

        Intent intent = getIntent();
        String path = intent.getStringExtra("com.baidu.widgets.Path");
        
        if (path == null) {
            path = "";
        }

        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
        getListView().setTextFilterEnabled(true);
        getListView().setPadding(15,0,15,0); 

    }

    protected List getData(String prefix) {
        List<Map> myData = new ArrayList<Map>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory("com.baidu.widgets.CATEGORY_TEST");

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        String[] prefixPath;
        
        if (prefix.equals("")) {
            prefixPath = null;
        } else {
            prefixPath = prefix.split("/");
        }
        
        int len = list.size();
        
        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null
                    ? labelSeq.toString()
                    : info.activityInfo.name;
            
            if (prefix.length() == 0 || label.startsWith(prefix)) {
                
                String[] labelPath = label.split("/");

                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];

                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                    addItem(myData, nextLabel, activityIntent(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        Collections.sort(myData, sDisplayNameComparator);
        
        return myData;
    }

    private final static Comparator<Map> sDisplayNameComparator = new Comparator<Map>() {
        private final Collator   collator = Collator.getInstance();

        public int compare(Map map1, Map map2) {
            return collator.compare(map1.get("title"), map2.get("title"));
        }
    };

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }
    
    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, WidgetWatch.class);
        result.putExtra("com.baidu.widgets.Path", path);
        return result;
    }

    protected void addItem(List<Map> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        temp.put("intent", intent);
        data.add(temp);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map map = (Map) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0, 0, 0, android.R.string.selectAll).setIcon(R.drawable.ic_menubar_contact);
        menu.add(0, 1, 0, android.R.string.copy);
        menu.add(0, 2, 1, android.R.string.cut).setIcon(R.drawable.ic_menubar_contact);
        menu.add(0, 3, 2, android.R.string.cancel).setIcon(R.drawable.ic_menubar_contact);
        menu.add(1, 4, 0, android.R.string.paste).setIcon(R.drawable.ic_menubar_contact);
        menu.add(1, 5, 1, android.R.string.no);//.setIcon(R.drawable.yi_ic_menubar_settings);
        menu.add(1, 6, 3, android.R.string.yes).setIcon(R.drawable.ic_menubar_contact);
        menu.add(1, 7, 3, "dark theme");
        menu.add(1, 8, 3, "light theme");
        menu.add(1, 9, 3, "light-darkActionbar theme");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String settingValue = null;
        // Handle item selection
        switch (item.getItemId()) {
            case CHANGE_THEME_ID:
                settingValue = System.getProperty(getResources().getString(R.string.change_theme_property));
                if(null == settingValue) {
                    settingValue = "light"; 
                } else {
                    if(settingValue.equals("dark")) {
                        settingValue = "light";
                    } else {
                        settingValue = "dark";
                    }
                }
                System.setProperty(getResources().getString(R.string.change_theme_property), "dark");
                return true;
            case 8:
            	System.setProperty(getResources().getString(R.string.change_theme_property), "light");
                return true;
            case 9:
            	System.setProperty(getResources().getString(R.string.change_theme_property), "light_darkactionbar");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    @Override
    public void onBackPressed() {
    	this.moveTaskToBack(true);
    }
    */
    
}

