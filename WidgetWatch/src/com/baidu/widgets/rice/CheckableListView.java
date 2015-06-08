package com.baidu.widgets.rice;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ListPopupWindow;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import android.view.Window;
import android.util.Log;
import android.util.SparseBooleanArray;
import java.util.List;
import java.util.ArrayList;
import yi.widget.CheckableItemLayout;
import yi.support.v1.YiLaf;

import com.baidu.widgets.R;


public class CheckableListView extends Activity  implements OnClickListener, OnItemClickListener{

    private static final String[] GENRES = new String[] {
        "item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8",
        "item9", "item10", "item11", "item12", "item13", "item14", "item15", "item16",
    };
    private static final String[] SELECT = new String[1];

    private ListView mListView;
    private ArrayList<String> mList;
    ActionBar mActionBar;
    boolean menuVisible = false;
    Button mDisplayInfoView;
    ListPopupWindow mListPopupWindow;
    LinearLayout mMultiModeView, mCancelView, mDoneView;
    boolean mAllBeSelected =false;
    int mDiaplayOptions;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String settingValue = System.getProperty(getResources().getString(R.string.change_theme_property));
        if(null == settingValue) {
            setTheme(R.style.Yi_Theme);
            setContentView(R.layout.yi_checkable_listview);
        } else {
            if(settingValue.equals("dark")) {
                setTheme(R.style.Yi_Theme);
                setContentView(R.layout.yi_checkable_listview);
            } else if(settingValue.equals("light")){
                setTheme(R.style.Yi_Theme_Light);
                setContentView(R.layout.yi_checkable_listview_light);
            } else {
                setTheme(R.style.Yi_Theme_Light_DarkActionBar);
                setContentView(R.layout.yi_checkable_listview_light);
                //requestWindowFeature(Window.FEATURE_NO_TITLE); 
                YiLaf.enable(this); 
                YiLaf.current().enableActionBarStyle();
            }
        }
        
        
        mList = new ArrayList();
        for(int i = 0;i< 1000;i++){
            mList.add("item" + i);
        }

        final ListView listView = (ListView)findViewById(R.id.list);
        //listView.setAdapter(new ArrayAdapter<String>(this,
          //      R.layout.single_choice_item, R.id.text1, GENRES));
        //listView.setAdapter(new ArrayAdapter<String>(this,
                //yi.R.layout.yi_list_item_1, yi.R.id.text3, GENRES));
        listView.setAdapter(new TestAdapter(this, mList));

        listView.setOnItemClickListener(this);
        mListView = listView;

        final Button button1 = (Button)findViewById(R.id.btn_select_all);
        button1.setOnClickListener(this);
        button1.setVisibility(View.GONE);

        final Button button2 = (Button)findViewById(R.id.btn_unselect_all);
        button2.setOnClickListener(this);
        button2.setVisibility(View.GONE);

        final Button button3 = (Button)findViewById(R.id.btn_mode_none);
        button3.setOnClickListener(this);

        final Button button4 = (Button)findViewById(R.id.btn_mode_single);
        button4.setOnClickListener(this);

        final Button button5 = (Button)findViewById(R.id.btn_mode_multiple);
        button5.setOnClickListener(this);

        mActionBar = getActionBar();
        SELECT[0] = getResources().getString(R.string.select_all);
        if (mActionBar != null) {
            mDiaplayOptions = mActionBar.getDisplayOptions();
        }

        mMultiModeView = (LinearLayout)this.findViewById(R.id.multi_mode_action_bar);
        //mCancelView = (LinearLayout)mMultiModeView.findViewById(yi.R.id.action_cancel);
        //mDoneView = (LinearLayout)mMultiModeView.findViewById(yi.R.id.action_done);
        if(mCancelView != null) mCancelView.setOnClickListener(this);
        if(mDoneView != null) mDoneView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mDisplayInfoView != null) {
            if(getCheckedItemCount() != 0) {
                mDisplayInfoView.setText(String.format(CheckableListView.this.getString(R.string.select_item_prompt),
                        getCheckedItemCount()));
            } else {
                mDisplayInfoView.setText(CheckableListView.this.getString(R.string.select_item_title));
            }
        }

    }

    public void onClick(View v) {
        int id = v.getId();
        int count = 0;
        switch(id){
            case R.id.btn_select_all:
                if(mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                    count = ((TestAdapter)mListView.getAdapter()).getCount();
                    for(int i=0; i< count; i++){
                        mListView.setItemChecked(i, true);
                    }
                }
                break;

            case R.id.btn_unselect_all:
                if(mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                    count = ((TestAdapter)mListView.getAdapter()).getCount();
                    for(int i=0; i< count; i++){
                        mListView.setItemChecked(i, false);
                    }
                }
                break;

            case R.id.btn_mode_none:
                mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                ((TestAdapter)mListView.getAdapter()).setChoiseMode(ListView.CHOICE_MODE_NONE);
                count = mListView.getChildCount();
                for(int i=0; i< count; i++){
                    ((CheckableItemLayout)mListView.getChildAt(i)).setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
                //menuVisible = false ;
                setCustomView(false);
                //invalidateOptionsMenu();
                YiLaf.current().getMenu().setPanelVisibility(View.GONE);
                if(mMultiModeView != null) mMultiModeView.setVisibility(View.GONE);
                break;

            case R.id.btn_mode_single:
                mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                ((TestAdapter)mListView.getAdapter()).setChoiseMode(ListView.CHOICE_MODE_SINGLE);
                count = mListView.getChildCount();

                for(int i=0; i< count; i++){
                    ((CheckableItemLayout)mListView.getChildAt(i)).setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                }
                //menuVisible = false ;
                setCustomView(false);
                //invalidateOptionsMenu();
                YiLaf.current().getMenu().setPanelVisibility(View.VISIBLE);
                if(mMultiModeView != null) mMultiModeView.setVisibility(View.GONE);
                break;

            case R.id.btn_mode_multiple:
                mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                ((TestAdapter)mListView.getAdapter()).setChoiseMode(ListView.CHOICE_MODE_MULTIPLE);
                count = mListView.getChildCount();
                for(int i=0; i< count; i++){
                    ((CheckableItemLayout)mListView.getChildAt(i)).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                }
                //menuVisible = true ;
                setCustomView(true);
                //invalidateOptionsMenu();
                YiLaf.current().getMenu().setPanelVisibility(View.VISIBLE);
                if(mMultiModeView != null) mMultiModeView.setVisibility(View.VISIBLE);
                break;
            case android.R.id.selectAll:
                if(mListPopupWindow == null) {
                    mListPopupWindow = new ListPopupWindow(this);
                    mListPopupWindow.setAnchorView(mDisplayInfoView);
                    final ListPopupWindow popup = mListPopupWindow;
                    mListPopupWindow.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            android.util.Log.d("-----", " " + ((TextView)arg1).getText());
                            TextView textView =(TextView)arg1;
                            int number = 0;
                            if(textView.getText().equals("Select All")) {
                                if(mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                                    number = ((TestAdapter)mListView.getAdapter()).getCount();
                                    for(int i=0; i< number; i++){
                                        mListView.setItemChecked(i, true);
                                    }
                                }
                                mAllBeSelected = true;
                            } else {
                                if(mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                                    number = ((TestAdapter)mListView.getAdapter()).getCount();
                                    for(int i=0; i< number; i++){
                                        mListView.setItemChecked(i, false);
                                    }
                                }
                                mAllBeSelected = false;
                            }

                            if(mDisplayInfoView != null) {
                                if(getCheckedItemCount() != 0) {
                                    mDisplayInfoView.setText(String.format(CheckableListView.this.getString(R.string.select_item_prompt),
                                            getCheckedItemCount()));
                                } else {
                                    mDisplayInfoView.setText(CheckableListView.this.getString(R.string.select_item_title));
                                }
                            }
                            //CheckableListView.this.invalidateOptionsMenu();
                            popup.dismiss();
                        }
                    });
                    mListPopupWindow.setAdapter(new selectAdapter(this,SELECT));
                }

                mListPopupWindow.show();
                break;

            case R.id.action_cancel:
                mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                ((TestAdapter)mListView.getAdapter()).setChoiseMode(ListView.CHOICE_MODE_NONE);
                count = mListView.getChildCount();
                for(int i=0; i< count; i++){
                    ((CheckableItemLayout)mListView.getChildAt(i)).setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
                //menuVisible = false ;
                setCustomView(false);
                //invalidateOptionsMenu();
                if(mMultiModeView != null) mMultiModeView.setVisibility(View.GONE);
                break;

            case R.id.action_done:
                if(mListView.getCheckedItemCount() == 0) {
                    Toast.makeText(this, "None item(s) be selected", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(getString(R.string.delete_dialog_title))
                            .setMessage(getString(R.string.delete_prompt, getCheckedItemCount()))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    confirmDelete();
                                    mListView.clearChoices();
                                    mDisplayInfoView.setText(CheckableListView.this.getString(R.string.select_item_title));
                                }
                             })
                             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface dialog, int whichButton) {
                                     /* User clicked No so do some stuff */
                                 }
                             }).create().show();
                break;
            default:break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkablelistmenu, menu);
        return true;
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_edit_mode, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setMenuVisible(menu, menuVisible);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setMenuVisible(Menu menu, boolean visible) {
        menuVisible = visible;
        MenuItem cancel;
        MenuItem done;
        cancel = menu.findItem(R.id.cancel);
        if(cancel != null) {
            cancel.setVisible(visible);
        }
        done = menu.findItem(R.id.ok);
        if(done != null) {
            done.setVisible(visible);
        }
    }*/

    private void setCustomView (boolean visible) {
        if(visible) {
            View view = null;
            if(null == mDisplayInfoView) {
                view = getLayoutInflater().inflate(R.layout.actionbar_custom_title, null);
                mDisplayInfoView = (Button)view.findViewById(android.R.id.selectAll);
                mDisplayInfoView.setOnClickListener(this);
            }

            if(getCheckedItemCount() != 0) {
                mDisplayInfoView.setText(String.format(getString(R.string.select_item_prompt),
                        getCheckedItemCount()));
            } else {
                mDisplayInfoView.setText(getString(R.string.select_item_title));
            }

            if(mActionBar.getCustomView() == null)
                mActionBar.setCustomView(view);
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        } else {
            mActionBar.setDisplayOptions(mDiaplayOptions);
        }
    }

    /*
     * confirm delete list items
     * */
    private void confirmDelete() {
        SparseBooleanArray array = mListView.getCheckedItemPositions();
        if (array == null) {
            return;
        }

        TestAdapter itla = (TestAdapter) mListView.getAdapter();
        if (itla == null) {
            return;
        }

        int itemCount = itla.getCount();
        if (itemCount == 0) {
            return;
        }

        ArrayList<String> tmp = new ArrayList();
        int j = 0;
        for (int i = 0; i < itemCount; i++) {
            if (array.get(i) == false) {
                tmp.add(mList.get(i));
            }
        }

        mList.clear();
        mList.addAll(tmp);
        itla.notifyDataSetChanged();
    }

    /**
     * get checked item count
     * @return
     */
    private int getCheckedItemCount() {
        SparseBooleanArray array = mListView.getCheckedItemPositions();
        if (array == null) {
            return 0;
        }

        TestAdapter itla = (TestAdapter) mListView.getAdapter();
        if (itla == null) {
            return 0;
        }

        int itemCount = itla.getCount();
        if (itemCount == 0) {
            return 0;
        }

        int checkedItemCount = 0;
        for (int i = 0; i < itemCount; i++) {
            if (array.get(i) == true) {
                checkedItemCount++;
            }
        }

        return checkedItemCount;
    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.cancel:
                mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                ((TestAdapter)mListView.getAdapter()).setChoiseMode(ListView.CHOICE_MODE_NONE);
                int count = mListView.getChildCount();
                for(int i=0; i< count; i++){
                    ((CheckableItemLayout)mListView.getChildAt(i)).setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
                //menuVisible = false ;
                setCustomView(false);
                //invalidateOptionsMenu();
                if(mMultiModeView != null) mMultiModeView.setVisibility(View.GONE);
                break;

            case R.id.ok:
                if(mListView.getCheckedItemCount() == 0) {
                    Toast.makeText(this, "None item(s) be selected", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(getString(R.string.delete_dialog_title))
                            .setMessage(getString(R.string.delete_prompt, getCheckedItemCount()))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    confirmDelete();
                                    mListView.clearChoices();
                                    mDisplayInfoView.setText(CheckableListView.this.getString(R.string.select_item_title));
                                }
                             })
                             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                 public void onClick(DialogInterface dialog, int whichButton) {
                                     // User clicked No so do some stuff
                                 }
                             }).create().show();
                break;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    private class TestAdapter extends ArrayAdapter<String> {
        private int mChoiseMode = ListView.CHOICE_MODE_NONE;

        TestAdapter(Context context, List<String> objects) {
            super(context,0,0,objects);
        }

        public void setChoiseMode(int choiseMode) {
            mChoiseMode = choiseMode;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView text;
            ImageView image;
            LayoutInflater mInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                android.util.Log.d("TestAdapter", "position " + position + " convertView is null");
                view = mInflater.inflate(R.layout.yi_checkable_list_item, parent, false);
            } else {
                view = convertView;
            }



            try {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(R.id.text1);
                image = (ImageView) view.findViewById(R.id.image2);

            } catch (ClassCastException e) {
                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException(
                        "ArrayAdapter requires the resource ID to be a TextView", e);
            }

            String item = getItem(position);
            if (item instanceof CharSequence) {
                text.setText((CharSequence)item);
            } else {
                text.setText(item.toString());
            }

            if(view instanceof CheckableItemLayout) {
                // if(((CheckableItemLayout)view).getChoiceMode() != mChoiseMode)
                ((CheckableItemLayout)view).setChoiceMode(mChoiseMode);
             }

            return view;
        }
    }

    private class selectAdapter extends ArrayAdapter<String> {

        selectAdapter(Context context, String[] objects) {
            super(context,0,0,objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView text;
            LayoutInflater mInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);

            try {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(android.R.id.text1);

            } catch (ClassCastException e) {
                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException(
                        "ArrayAdapter requires the resource ID to be a TextView", e);
            }

            String item = getItem(position);
            if (CheckableListView.this.mAllBeSelected) {
                text.setText("UnSelect All");
            } else {
                text.setText("Select All");
            }

            return view;
        }
    }
}













