
package com.baidu.widget.next;

import java.util.ArrayList;

import yi.support.v1.YiLaf;
import yi.support.v1.utils.Logger;
import yi.widget.SearchView;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Toast;

import com.baidu.widgets.R;

public class NextActivity extends Activity  implements SearchView.OnQueryTextListener {
    private static final String META_DATA_KEY_ACTIONBAR_TAB = "actionbar.tab";
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;
    SearchView mSearchView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DeviceDefault_Light_DarkActionBar_ActionbarTabTop);
        String msg = "";
        try{
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), 
                    PackageManager.GET_META_DATA);
            msg = info.metaData.getString(META_DATA_KEY_ACTIONBAR_TAB);
            if(msg.equals("secondary")) {
//                setTheme(android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
            }            
        }catch(NameNotFoundException ex){
            ex.printStackTrace();
        }
        
        Logger.enable("NextActivity");

        YiLaf.enable(this);

        // setContentView(R.layout.main);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        if(msg.equals("first")) {
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayShowTitleEnabled(false);            
        }
        //bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText("简单"),
                CountingFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("第二"),
                SecondFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("Third"),
                ThirdFragment.class, null);

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
        
      mSearchView = new SearchView(this);
       // setupSearchView();

        YiLaf.current().enableActionBarStyle();
        
        //YiLaf.current().getMenu().setPanelVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "menu " + item.getTitle() + " selected!", Toast.LENGTH_SHORT).show();

        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_add:
                mSearchView.showSearchBackIcon(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        YiLaf.current().getActionBar().removeCustomView();
                        mSearchView.clearFocus();
                    }
                    
                }, false);

                mSearchView.setIconifiedByDefault(false);
                mSearchView.setSubmitButtonEnabled(false);
                mSearchView.setQueryHint(getString(R.string.cheese_hunt_hint));
                
                YiLaf.current().getActionBar().setCustomView(mSearchView);
                mSearchView.requestFocus();
                mSearchView.setIconified(false);
                return true;
            case R.id.menu_add_activity:
                intent = new Intent(this, SearchViewActionBar.class);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                intent = new Intent(this, SearchViewFilterMode.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    } 

    /**
     * 
     * @author rendayun
     *
     */
    final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
    }

    /**
     * 
     * @author rendayun
     *
     */
    public class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private final Fragment[] mFragments = new Fragment[10];

        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return mFragments[position] = Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            YiLaf.current().getActionBar().onScrolled(position, positionOffset, positionOffsetPixels);
            YiLaf.current().getMenu().onScrolled(mViewPager.getCurrentItem(), position, positionOffset);
        }

        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
            android.util.Log.d("rendayun", "Viewpager:position = " + position);

            if (position == 1) {
                YiLaf.current().getActionBar().showBadgeView(1, "1");
            } else if(position == 2) {
                YiLaf.current().getActionBar().showBadgeView(1, "12");
            } else {
                YiLaf.current().getActionBar().hideBadgeView(1);                
            }
            
            for (int i=0; i<mFragments.length; i++) {
                if (mFragments[i] != null) {
                    mFragments[i].setMenuVisibility(i == position);
                }
            }
            
            YiLaf.current().invalidateOptionsMenu();
            
            YiLaf.current().getMenu().setPanelVisibility(position==1 ? View.GONE : View.VISIBLE);
        }

        public void onPageScrollStateChanged(int state) {
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

    private void setupSearchView() {
        mSearchView.showSearchBackIcon(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                YiLaf.current().getActionBar().removeCustomView();
                mSearchView.clearFocus();
            }
        }, false);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint(getString(R.string.cheese_hunt_hint));
        mSearchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(YiLaf.current().isContentViewExclusive())
                {
                    mSearchView.collapseSearchViewDown();
                }
                else
                {
                    mSearchView.expandSearchViewUp(new OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mSearchView.clearFocus();
                        }
                    });
                }
            }
        });
        
        mSearchView.setVisibility(View.INVISIBLE);
        mSearchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchView.setVisibility(View.VISIBLE);
            }
        }, 100);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public static class SecondaryActionTabActivity extends NextActivity { /* empty */ }
}
