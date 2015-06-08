package com.baidu.widgets.rice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

//import com.baidu.widgets.CheckableItemLayout;
import yi.widget.CheckableItemLayout;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class GridViewLayoutActivity extends Activity implements OnItemClickListener, OnClickListener{
	
	private int mCheckMode = CheckableItemLayout.CHOICE_MODE_NONE;
	private Button btn_none , btn_single, btn_muti;
	private View action_cancel, action_done;
	private View multi_mode_action_bar;
	private GridView grid;
	private MyAdapter gridAdapter;
	
	private Integer[] mImageIds = { R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09,R.drawable.w01, R.drawable.w02,
			R.drawable.w03, R.drawable.w04, R.drawable.w05, R.drawable.w06,
			R.drawable.w07, R.drawable.w08, R.drawable.w09 };
	
	private List<Integer> imagesId;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Util.setDeviceDefaultLightTheme(this);
        
        setContentView(R.layout.grid_checkable);
        
        imagesId = new ArrayList<Integer>(Arrays.asList(mImageIds));
        
        btn_none = (Button)findViewById(R.id.btn_mode_none);
        btn_single = (Button)findViewById(R.id.btn_mode_single);
        btn_muti = (Button)findViewById(R.id.btn_mode_multiple);
        action_cancel = findViewById(R.id.action_cancel);
        action_done = findViewById(R.id.action_done);
        btn_none.setOnClickListener(this);
        btn_single.setOnClickListener(this);
        btn_muti.setOnClickListener(this);
        action_cancel.setOnClickListener(this);
        action_done.setOnClickListener(this);

        
        
        multi_mode_action_bar = findViewById(R.id.multi_mode_action_bar);
        
        grid = (GridView)findViewById(R.id.grid);
        //grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridAdapter = new MyAdapter(this);
        grid.setAdapter(gridAdapter);
        grid.setOnItemClickListener(this);
        
        grid.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        /*grid.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return true;
			}
        	
        });*/
    }
    
	class MyAdapter extends BaseAdapter {
		
		private Context mContext;
		
		public MyAdapter(Context context){
			this.mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagesId.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return imagesId.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			android.util.Log.i("lg","getView:"+position);
			// TODO Auto-generated method stub
			View view;
			ImageView imageView ;
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
				view = inflater.inflate(R.layout.grid_item, null);
				((CheckableItemLayout)view).setChoiceMode(mCheckMode);
//				((CheckableItemLayout)view).setLayoutMode(CheckableItemLayout.LAYOUT_MODE_GRID);
				
			}else {
				view = convertView;
			}
			imageView = (ImageView)view.findViewById(R.id.image);
			imageView.setScaleType(ScaleType.MATRIX);
			imageView.setImageResource(imagesId.get(position));
			return view;
		}
    	
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
//		((CheckableItemLayout)view).setChecked(!((CheckableItemLayout)view).isChecked());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_mode_none:
			mCheckMode = CheckableItemLayout.CHOICE_MODE_NONE;
			multi_mode_action_bar.setVisibility(View.GONE);
			grid.setChoiceMode(GridView.CHOICE_MODE_NONE);
			grid.setAdapter(gridAdapter);
			break;
		case R.id.btn_mode_single:
			mCheckMode = CheckableItemLayout.CHOICE_MODE_SINGLE;
			multi_mode_action_bar.setVisibility(View.GONE);
			grid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
			grid.setAdapter(gridAdapter);
			break;
		case R.id.btn_mode_multiple:
			mCheckMode = CheckableItemLayout.CHOICE_MODE_MULTIPLE;
			multi_mode_action_bar.setVisibility(View.VISIBLE);
			grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
			grid.setAdapter(gridAdapter);
			break;
		case R.id.action_cancel:
			grid.clearChoices();
			grid.setAdapter(gridAdapter);
			mCheckMode = CheckableItemLayout.CHOICE_MODE_NONE;
			multi_mode_action_bar.setVisibility(View.GONE);
			break;
		case R.id.action_done:
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle(getString(R.string.delete_dialog_title))
            	.setMessage(getString(R.string.delete_prompt, grid.getCheckedItemCount()))
            	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int whichButton) {
            			confirmDelete();
            			grid.clearChoices();
            			grid.setAdapter(gridAdapter);
            			//mDisplayInfoView.setText(GridViewLayoutActivity.this.getString(R.string.select_item_title));
            		}
             })
             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                     /* User clicked No so do some stuff */
                 }
             }).create().show();
			break;
		}
	}
	
	public void confirmDelete(){
		SparseBooleanArray array = grid.getCheckedItemPositions();
		if (array == null) {
            return ;
        }
		
		List<Integer> temp = new ArrayList<Integer>();
		
		for(int i = 0; i<grid.getCount(); i++){
			if(array.get(i) == false){
				temp.add(imagesId.get(i));
			}
		}
		
		imagesId.clear();
		imagesId.addAll(temp);
		((MyAdapter)grid.getAdapter()).notifyDataSetChanged();
	}
}