package com.baidu.widgets.rice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.Contacts.PeopleColumns;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class SecondTab extends Activity { 
	
	private Instrumentation mInstrumentation;
	private ListView mListView;
	
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        Util.setYiTheme(this);
        //setGravity(Gravity.BOTTOM|Gravity.CENTER);
        Button bt = new Button(this.getBaseContext());
        View.OnClickListener l = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showDialog();
				testList();
			}
		};
		
		bt.setOnClickListener(l);
        
        setContentView(R.layout.list_content); 
        mListView = (ListView)findViewById(android.R.id.list);
        mInstrumentation = new Instrumentation();
        testList();
    } 
    
    private void testList() {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String [] {"a", "b", "c", "d"});
		mListView.setAdapter(adapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = (String)parent.getSelectedItem();
				Log.d("SYGTC", "onItemClick() item = " + item);
			}
    	};
    	
    	mListView.setOnItemClickListener(listener);
		OnItemSelectedListener listener2 = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = (String)parent.getSelectedItem();
				Log.d("SYGTC", "onItemSelected() item = " + item);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		};
		mListView.setOnItemSelectedListener(listener2 );
    	
    	mListView.performItemClick(null, 1, 1);
    	mListView.setSelection(1);
    	
    	String item = (String)mListView.getSelectedItem();
    	Log.d("SYGTC", "item = " + item);    	
    }
    
    private void showDialog() {
        
        String [] projection = new String[] {
        		People._ID, People.NAME
        };
        
        preparePeople();
        
        final Cursor c = this.getBaseContext().getContentResolver().query(People.CONTENT_URI, projection, null, null, null);
        final android.content.DialogInterface.OnClickListener mOnClickListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("SYGTC", "which = " + which);
			}
        	
        };
        
        final ListView listView;
        
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
		        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SecondTab.this);                
				//mBuilder.setCursor(c, mOnClickListener, People.NAME);	
				mBuilder.setMultiChoiceItems(c, People.NAME, People.NAME, null);
		        AlertDialog mDialog = mBuilder.show();
		        mListView = mDialog.getListView();		        
		        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		        mListView.performItemClick(null, 0, 0);
			}
		});
      
		//this.mInstrumentation.waitForIdleSync();
		
        CursorWrapper selected = (CursorWrapper)mListView.getSelectedItem();
        Log.d("SYGTC", "selected.getString(1)" + selected.getString(1));
        Log.d("SYGTC", "c.getString(1) = " + c.getString(1));
    }
    
    public void onResume() { 
        super.onResume();
        Toast.makeText(this, "This is The Second Tab", Toast.LENGTH_SHORT).show();
        
    }
    
    private void preparePeople() {
    	ContentResolver resolver = this.getBaseContext().getContentResolver();
    	//resolver.delete(People.CONTENT_URI, null, null);
    	ContentValues values = new ContentValues();
    	//values.put(People._ID, "1");
    	//values.put(People.NAME, "name");
    	values.put(People._ID, "1");
    	values.put(People.NAME, "name");
    	Uri uri = resolver.insert(People.CONTENT_URI, values);
    	
    	Log.d("SYGTC", "uri = " + uri.toString());
    }
}
