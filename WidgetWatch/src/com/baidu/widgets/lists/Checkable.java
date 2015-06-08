package com.baidu.widgets.lists;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.Window;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;
public class Checkable extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        ListView listView = getListView();

        listView.addHeaderView(new Button(this));

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, GENRES));
        
        listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
    }


    private static final String[] GENRES = new String[] {
        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
}
