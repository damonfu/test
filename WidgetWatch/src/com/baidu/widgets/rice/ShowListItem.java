package com.baidu.widgets.rice;

//import android.widget.CheckableItemLayout;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowListItem extends ListActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener, OnLongClickListener {
    
    private ListAdapter mAdapter;
    static boolean showImg1;
    static boolean showImg2;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setYiTheme(this);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_list_item_layouts);
        
        Intent intent = getIntent();
        Integer layoutId = intent.getExtras().getInt("LAYOUT_ID");
        showImg1 = intent.getExtras().getBoolean("SHOW_IMG_1");
        showImg2 = intent.getExtras().getBoolean("SHOW_IMG_2");
        
        final String[] matrix  = { "_id", "text1", "text2", "text3", "text4" };
        MatrixCursor  cursor = new MatrixCursor(matrix);

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        StringBuilder sb4 = new StringBuilder();
        
        String string1 = this.getResources().getString(android.R.string.ok);
        String string2 = this.getResources().getString(android.R.string.copy);
        String string3 = this.getResources().getString(android.R.string.cut);
        String string4 = this.getResources().getString(android.R.string.cancel);
        
        for(int i = 0 ; i < 20 ; i ++) {
        	sb1.append(string1);
        	sb2.append(string2);
        	sb3.append(string3);
        	sb4.append(string4);
        	cursor.addRow(new Object[] { i, sb1.toString(), sb2.toString(), sb3.toString(), sb4.toString() });
        }
        
        mAdapter = new ListAdapter(this,
                // Use a template that displays a text view
                        layoutId,
                        // Give the cursor to the list adatper
                        cursor,
                        // Map the NAME column in the people database to...
                        new String[] { "text1", "text2", "text3", "text4" },
                        // The "text1" view defined in the XML template
                        new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.text4 });
        
        setListAdapter(mAdapter);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setOnItemLongClickListener(this);
    }
    
    public void onYiTitleRightButtonClicked(View v) {
    	ListView listView = this.getListView();
    	if (listView.getChoiceMode() != ListView.CHOICE_MODE_NONE) {
        	listView.setChoiceMode(ListView.CHOICE_MODE_NONE);    	    		
    	} else {
    		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	}
    }
    
    class ViewHolder {
        TextView txt1;
        TextView txt2;
        TextView txt3;
        TextView txt4;            

        ImageView img1;
        ImageView img2;
        
        RatingBar rat1;
        
        ProgressBar pro1;
    }
    
    class ListAdapter extends SimpleCursorAdapter {
        public ListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            mContext = context;
        }

        private Context mContext = null;

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = super.newView(context, cursor, parent);

            ViewHolder vh = new ViewHolder();
            /*
            vh.txt1 = (TextView) v.findViewById(android.R.id.text1);            
            vh.txt2 = (TextView) v.findViewById(android.R.id.text2);
            vh.txt3 = (TextView) v.findViewById(R.id.text3);
            vh.txt4 = (TextView) v.findViewById(R.id.text4);
            */
            vh.img1 = (ImageView) v.findViewById(R.id.image1);
            vh.img2 = (ImageView) v.findViewById(R.id.image2);
            //vh.rat1 = (RatingBar) v.findViewById(android.R.id.ratingbar);
            //vh.pro1 = (ProgressBar) v.findViewById(android.R.id.progressbar);
            v.setTag(vh);
            //v.setFocusable(true);
            
            vh.txt1 = (TextView) v.findViewById(R.id.text1);
            //vh.txt1.setOnClickListener(ShowListItem.this);
            //vh.txt1.setOnLongClickListener(ShowListItem.this);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        	super.bindView(view, context, cursor);
        	
            ViewHolder vh = (ViewHolder) view.getTag();

            if(null != view) {
                //view.setBackgroundResource(R.drawable.background);
            }

            if( null != vh.img1 ) {
                if(showImg1) {
                    vh.img1.setImageResource(R.drawable.ic_appstore_picture);
                    //vh.img1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yi_indicator_noti));
                    vh.img1.setVisibility(View.VISIBLE);
                    vh.img1.setOnClickListener(ShowListItem.this);
                } else {
                    vh.img1.setVisibility(View.GONE);
                }
            }

            if( null != vh.img2 ) {
                if(showImg2) {
                    vh.img2.setImageResource(R.drawable.yi_indicator_arrow_right);
                	//vh.img2.setImageResource(R.drawable.icon);
                    vh.img2.setVisibility(View.VISIBLE);
                    vh.img2.setOnClickListener(ShowListItem.this);
                } else {
                    vh.img2.setVisibility(View.GONE);
                }
            }

            
            if( null != vh.txt1 ) {
            	Log.d("SYGTC", "null != vh.txt1");
                //vh.txt1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yi_indicator_arrow_right));
            }
            
            /*
            if( null != vh.txt2 ) {
                vh.txt2.setText(context.getText(R.string.txt2));
            }

            if( null != vh.txt3 ) {
                vh.txt3.setText(context.getText(R.string.txt3));
            }

            if( null != vh.txt4 ) {
                vh.txt4.setText(context.getText(R.string.txt4));
            }
			*/
            
            if( null != vh.rat1 ) {           
                vh.rat1.setRating(4);
            }

            if( null != vh.pro1 ) {         
                vh.pro1.setProgress(80);
            }            

        }
    }

	@Override
	public void onClick(View v) {
		Toast.makeText(this, "The ImageView onClick was called", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(this, "The position " + position + " onItemClick was clicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (view instanceof TextView) {
			//((TextView)view).startQuickSearch();
		//} else if (view instanceof CheckableItemLayout) {
			ViewHolder vh = (ViewHolder)view.getTag();
			//vh.txt1.startQuickSearch();
		}
		return true;
	}

	@Override
	public boolean onLongClick(View v) {
		Toast.makeText(this, "The onLongClick was called", Toast.LENGTH_SHORT).show();
		return false;
	}
}
