package com.baidu.widgets.lists;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.Window;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class SimpleTwoLine1 extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);

        // Get a cursor with all phones
        Cursor c = getContentResolver().query(Phones.CONTENT_URI, null, null, null, null);
        startManagingCursor(c);

        // Map Cursor columns to views defined in simple_list_item_2.xml
        ListAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, c, 
                        new String[] { Phones.NAME, Phones.NUMBER }, 
                        new int[] { android.R.id.text1, android.R.id.text2 });
        setListAdapter(adapter);
    }



    private static final String[] GENRES = new String[] {
        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
}
