package com.baidu.widgets.lists;


import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.Window;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;
/**
 * This example shows how to use choice mode on a list. This list is 
 * in CHOICE_MODE_MULTIPLE mode, which means the items behave like
 * checkboxes.
 */
public class MultiChoiceList extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, GENRES));
        
        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setSmoothScrollbarEnabled(true);

    }


    private static final String[] GENRES = new String[] {
        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
}
