package com.baidu.widgets.ics;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class ActionBarDemo extends Activity {
	ActionBar mActionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setDeviceDefaultLightTheme(this);
		
		//使ActionBar变得透明  
//        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); 
		setContentView(R.layout.actionbar_demo);
		
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.hideButton);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mActionBar.show();
				} else {
					mActionBar.hide();
				}
			}
		});
		
		toggleButton = (ToggleButton) findViewById(R.id.homeButton);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActionBar.setDisplayShowHomeEnabled(isChecked);
			}
		});
		
		toggleButton = (ToggleButton) findViewById(R.id.titleButton);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActionBar.setDisplayShowTitleEnabled(isChecked);
			}
		});
		
		toggleButton = (ToggleButton) findViewById(R.id.upButton);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mActionBar.setDisplayHomeAsUpEnabled(isChecked);
			}
		});
		
		Button button = (Button) findViewById(R.id.standardButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
		});
		
		button = (Button) findViewById(R.id.listButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			}
		});
		
		button = (Button) findViewById(R.id.tabButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			}
		});
		button = (Button) findViewById(R.id.startCustomAction1);
		button.setText("Start ActionBar-Change Button Text Color");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					v.startActionMode(new MyCallback((TextView) v, 0));
				} catch (Exception e) {
				}
			}
		});
		
		button = (Button) findViewById(R.id.startCustomAction2);
		button.setText("Start ActionBar-Change Button BG Color");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					v.startActionMode(new MyCallback((TextView) v, 1));
				} catch (Exception e) {
				}
			}
		});
	}
	
	private class MyCallback implements Callback {
		private TextView mView;
		private int mMode = 0;// 0 means change text color, 1 means change background color
		
		public MyCallback(TextView view, int mode) {
			this.mView = view;
			mMode = mode;
			if (mMode != 0 && mMode != 1) {
				mMode = 0;
			}
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int color = Color.BLACK;
			switch (item.getItemId()) {
			case 0:
				color = Color.RED;
				break;
				
			case 1:
				color = Color.BLUE;
				break;
				
			case 2:
				color = Color.CYAN;
				break;
				
			case 3:
				color = Color.DKGRAY;
				break;
				
			case 4:
				color = Color.GREEN;
				break;
				
			case 5:
				color = Color.LTGRAY;
				break;
				
			case 6:
				color = Color.YELLOW;
				break;
				
			case 7:
				color = Color.MAGENTA;
				break;
				
			case 8:
				color = Color.BLACK;
				break;

			default:
				break;
			}
			
			if (mMode == 0){
				mView.setTextColor(color);
			} else {
				mView.setBackgroundColor(color);
			}
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuItem menuItem;
			menuItem = menu.add(0, 0, Menu.NONE, "Red");
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.RED);
			
			menuItem = menu.add(0, 1, Menu.NONE, "Blue");
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.BLUE);
			
			menuItem = menu.add(0, 2, Menu.NONE, "CYAN");
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.CYAN);
			
			menuItem = menu.add(0, 3, Menu.NONE, "DKGRAY");
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.DKGRAY);
			
			menuItem = menu.add(0, 4, Menu.NONE, "GREEN");
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.GREEN);
			
			menuItem = menu.add(0, 5, Menu.NONE, "LTGRAY");
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.LTGRAY);
			
			menuItem = menu.add(0, 6, Menu.NONE, "YELLOW");
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.YELLOW);
			
			menuItem = menu.add(0, 7, Menu.NONE, "MAGENTA");
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.MAGENTA);
			
			menuItem = menu.add(0, 8, Menu.NONE, "BLACK");
			if (menuItem.getActionView() != null)
				menuItem.getActionView().setBackgroundColor(Color.BLACK);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Toast.makeText(getApplicationContext(), "Custom ActionBar Destroy.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
	}
	
	private class ListNavigationSpinnerAdapter implements SpinnerAdapter {

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position+10;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(android.R.layout.simple_spinner_item, null);
			((TextView)view.findViewById(android.R.id.text1)).setText("List item "+position);
			((TextView)view.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(android.R.color.white));
			/*TextView textView = new TextView(ActionBarDemo.this);
			textView.setTextColor(getResources().getColor(android.R.color.white));  
			textView.setText("List item "+position);*/
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
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
			((TextView)view.findViewById(android.R.id.text1)).setText("Drop down item "+position);
			
			/*TextView textView = new TextView(ActionBarDemo.this);
			textView.setText("Drop down item "+position);*/
			int minScreenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
			int minHeight = (int) (56 * view.getContext().getResources().getDisplayMetrics().density);
			view.setMinimumWidth(minScreenWidth);
			view.setMinimumHeight(minHeight);
			return view;
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mActionBar = getActionBar();
		mActionBar.setTitle("ActionBar");
		
		mActionBar.setListNavigationCallbacks(new ListNavigationSpinnerAdapter(), new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				Toast.makeText(getApplicationContext(), "Press List item "+itemPosition+", itemId="+itemId, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
		for (int i = 0; i < 8; i++) {
			ActionBar.Tab tab = mActionBar.newTab().setText("Tab "+i);
			tab.setTabListener(new MyTabLisener(i));
			mActionBar.addTab(tab);
		}
	}
	
    public static class MyFragment extends Fragment {
    	int mIndex;
    	public MyFragment(int index){
    		mIndex = index;
    	}
    	
        public View onCreateView(LayoutInflater inflater, ViewGroup container,   
                Bundle savedInstanceState) {
        	View view = inflater.inflate(R.layout.fragment_demo, container, false);
        	TextView textView = (TextView) view.findViewById(R.id.msg);
        	textView.setText("Tab Fragment "+mIndex);
            return view;
        }
    }
	
	private static class MyTabLisener implements ActionBar.TabListener {
		int mTabIndex = -1;
		Fragment mFragment;
		public MyTabLisener(int tabIndex){
			mTabIndex = tabIndex;
			mFragment = new MyFragment(mTabIndex);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.add(R.id.fragment_content, mFragment, null);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(mFragment);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_demo, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "menu "+item.getTitle()+" selected!", Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
            	FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
            	ft.detach(mFragment);
            	ft.commit();
            	}
            }

    	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    	if (mFragment == null) {
	    		mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
	    		ft.add(android.R.id.content, mFragment, mTag);
	    	} else {
	    		ft.attach(mFragment);
	    	}
    	}

    	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    		if (mFragment != null) {
    			ft.detach(mFragment);
    		}
    	}

    	public void onTabReselected(Tab tab, FragmentTransaction ft) {
    		Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
    	}
    }
}
