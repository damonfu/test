package com.baidu.widgets.ics;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class ListPopupWindowDemo extends Activity implements OnClickListener{
	ListPopupWindow mListPopupWindow;
	LayoutInflater mLayoutInflater;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		setContentView(R.layout.listpopupwindow_demo);
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);
		
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(this);
		
		button = (Button) findViewById(R.id.button3);
		button.setOnClickListener(this);
		
		button = (Button) findViewById(R.id.button4);
		button.setOnClickListener(this);
		
		mListPopupWindow = new ListPopupWindow(this);
		mListPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				Toast.makeText(ListPopupWindowDemo.this, "List popup window dismissed.", Toast.LENGTH_SHORT).show();
			}
		});
		
		mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(ListPopupWindowDemo.this, "Clicked at Position="+arg2+", id="+arg3, Toast.LENGTH_SHORT).show();
			}
		});
		
		mListPopupWindow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(ListPopupWindowDemo.this, "Selected at Position="+arg2+", id="+arg3, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(ListPopupWindowDemo.this, arg0+" nothing selected.", Toast.LENGTH_SHORT).show();
			}
		});
		
		mLayoutInflater = getLayoutInflater();
	}
	
	private int TYPE_NORMAL 		= 0;
	private int TYPE_WITH_ICON 		= 1;
	private int TYPE_WITH_INDICATOR = 2;
	private int TYPE_CW				= 4;
	
	private class ListAdapter1 implements ListAdapter {
		int mCount = 0;
		int mType = 0;
		
		public ListAdapter1(int count, int type){
			mCount = count;
			mType = type;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if ((mType & TYPE_CW) == TYPE_CW) {
				view = mLayoutInflater.inflate(R.layout.listpopupwindow_other, null);
			} else {
				view = mLayoutInflater.inflate(R.layout.listpopupwindow_item1, null);
			}
			
			if (view != null) {
				boolean isEnable = true;
				if (position > 0 && position % 4 == 0){
					view.setEnabled(false);
					isEnable = false;
				}
				TextView textView = (TextView) view.findViewById(R.id.title);
				if (textView != null) {
					textView.setText("Title "+position);
					textView.setEnabled(isEnable);
				}
				
				textView = (TextView) view.findViewById(R.id.description);
				if (textView != null) {
					textView.setText("Descrition of Title "+position);
					textView.setEnabled(isEnable);
				}
				
				ImageView imageView = null;
				if ((mType & TYPE_CW) == TYPE_CW) {
					FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.widget);
					frameLayout.removeAllViews();
					
					if (frameLayout != null){
						if (position % 2 == 0) {
							Switch switch1 = new Switch(ListPopupWindowDemo.this);
							switch1.setEnabled(isEnable);
							frameLayout.addView(switch1);
							switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
									Toast.makeText(getApplicationContext(), buttonView+" is "+isChecked, Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							CheckBox checkBox = new CheckBox(ListPopupWindowDemo.this);
							checkBox.setEnabled(isEnable);
							frameLayout.addView(checkBox);
						}
					}
					
				} else {
					if ((mType & TYPE_WITH_ICON) == TYPE_WITH_ICON) {
						imageView = (ImageView) view.findViewById(R.id.icon);
						if (imageView != null) {
							imageView.setImageResource(R.drawable.ic_launcher);
						}
					}
					
					if ((mType & TYPE_WITH_INDICATOR) == TYPE_WITH_INDICATOR) {
						imageView = (ImageView) view.findViewById(R.id.indicator);
						if (imageView != null) {
							imageView.setImageResource(R.drawable.ic_clock_alarmclock_switch_btn);
						}
					}
				}
			}
			
			return view;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			if (position > 0 && position % 4 == 0){
				return false;
			} else {
				return true;
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		ListAdapter1 listAdapter1 = null;
		mListPopupWindow.setAnchorView(v);
		
		switch (v.getId()) {
		case R.id.button1:
			listAdapter1 = new ListAdapter1(5, TYPE_NORMAL);
			break;
			
		case R.id.button2:
			listAdapter1 = new ListAdapter1(10, TYPE_NORMAL | TYPE_WITH_ICON);
			break;
			
		case R.id.button3:
			listAdapter1 = new ListAdapter1(15, TYPE_NORMAL | TYPE_WITH_ICON | TYPE_WITH_INDICATOR);
			break;
			
		case R.id.button4:
			listAdapter1 = new ListAdapter1(3, TYPE_CW);
			break;

		default:
			break;
		}
		
		mListPopupWindow.setAdapter(listAdapter1);
		mListPopupWindow.show();
	}
}
