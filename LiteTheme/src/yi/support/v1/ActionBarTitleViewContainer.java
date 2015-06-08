package yi.support.v1;

import java.lang.ref.WeakReference;

import yi.util.ReflectUtil;
import yi.widget.RoundCornerListView;
import yi.widget.SearchView;
import yi.support.v1.YiLaf.OnSpinerItemListener;
import yi.support.v1.menu.HybridMenu;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.baidu.lite.R;


public class ActionBarTitleViewContainer{

    private WeakReference<Activity> mActivity;
    Window.Callback mWindowCallback;
    private Object mBackHomeItem;
    private boolean mShowHomeAsUp = false;
    private boolean mShowActionButton = false;
    private Drawable mActionButtonRes;
    private Object mActionButtonItem;
    private boolean mShowActionSpiner =false;
    private View mSpinerOpen;
    private View mSpinerClose;
    private RoundCornerListView mListView;
    private View mTitleView;
    
    private SpinnerAdapter mSpinnerAdapter;
    private OnSpinerItemListener mCallback;
    private PopupWindow mPopUpWindow;

    ActionBarTitleViewContainer(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
        mWindowCallback = mActivity.get().getWindow().getCallback();
    }

    public void show() {
        if (mActivity.get() != null) {
            ViewGroup actionbar = getActionBar(mActivity.get());
            if (actionbar != null) {
                mTitleView = LayoutInflater.from(mActivity.get()).inflate(R.layout.cld_custom_title_bar, null);
                mTitleView.setId(R.id.action_bar_title_view);
                //set default back ground
                mTitleView.setBackgroundDrawable(mActivity.get().getResources().getDrawable(R.drawable.cld_ab_solid_dark_baidu));
                actionbar.addView(mTitleView);
                
                //set back 
                View back = mTitleView.findViewById(R.id.custom_back_icon);
                if(mShowHomeAsUp){
                    mBackHomeItem = ReflectUtil.ActionMenuItemReflect.getInstance(mActivity.get()
                            .getApplicationContext(), 0, android.R.id.home, 0, 0, null);
                    back.setOnClickListener(mUpClickListener);
                    back.setClickable(true);
                    back.setFocusable(true);
                }else{
                    back.setVisibility(View.GONE);
                }
                
                //set action button
                View actionBt = mTitleView.findViewById(R.id.custom_aciton_button);
                if(mShowActionButton){
                    actionBt.setBackgroundDrawable(mActionButtonRes);
                    mActionButtonItem = ReflectUtil.ActionMenuItemReflect.getInstance(mActivity
                            .get().getApplicationContext(), 0, R.id.action_bar_button, 0, 0, null);
                    actionBt.setOnClickListener(mActionBarButtonClickListener);
                    actionBt.setClickable(true);
                    actionBt.setFocusable(true);
                }else{
                    actionBt.setVisibility(View.GONE);
                }
                
                //set spiner
                mSpinerOpen = mTitleView.findViewById(R.id.custom_title_spiner_open);
                mSpinerClose = mTitleView.findViewById(R.id.custom_title_spiner_close);
                View spiner = LayoutInflater.from(mActivity.get()).inflate(R.layout.cld_spiner_drop_list, null);
                mListView = (RoundCornerListView)spiner.findViewById(R.id.spiner_listview);
                if(mShowActionSpiner){
                    mSpinerOpen.setVisibility(View.VISIBLE);
                    mSpinerClose.setVisibility(View.GONE);
                    mSpinerOpen.setOnClickListener(mActionBarSpinerClickListener);
                    mSpinerClose.setOnClickListener(mActionBarSpinerClickListener);
                    if(mListView != null && mSpinnerAdapter != null){
                        mListView.setAdapter((ListAdapter)mSpinnerAdapter);
                        mListView.setOnItemClickListener(listClickListener);
                        mPopUpWindow = new PopupWindow(spiner, LayoutParams.FILL_PARENT,  
                                LayoutParams.WRAP_CONTENT);
                        mPopUpWindow.setOnDismissListener(dismissListenter);
                    }
                    //enable title click
                    TextView titleText = (TextView)mTitleView.findViewById(R.id.custom_title_text);
                    if(titleText != null){
                        titleText.setOnClickListener(mActionBarSpinerClickListener);
                    }
                }else{
                    mSpinerOpen.setVisibility(View.GONE);
                    mSpinerClose.setVisibility(View.GONE);
                }
            }
        }
    }

    public void hide() {
        if (mActivity.get() != null) {
            ViewGroup actionbar = getActionBar(mActivity.get());
            if (actionbar != null) {
                View titleview = actionbar.findViewById(R.id.action_bar_title_view);
                if (titleview != null) {
                    actionbar.removeView(titleview);
                }
                
            }
        }
    }
    
    public void setTitle(CharSequence title){
        if (mActivity.get() != null) {
            ViewGroup actionbar = getActionBar(mActivity.get());
            if (actionbar != null) {
                TextView text = (TextView)actionbar.findViewById(R.id.custom_title_text);
                if(text != null)
                    text.setText(title);
            }
        }
    }
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp){
        mShowHomeAsUp = showHomeAsUp;
    }
    public void setDisplayActionButtonEnabled(boolean showActionButton,
            Drawable res) {
        mShowActionButton = showActionButton;
        mActionButtonRes = res;
        
    }
    public void setDisplayActionSpinerEnabled(boolean showActionSpiner){
        mShowActionSpiner = showActionSpiner;
    }
    
    public void setDropdownAdapter(SpinnerAdapter adpter){
        mSpinnerAdapter = adpter;
    }
    public void setCallback(OnSpinerItemListener callback) {
        mCallback = callback;
    }
    

    private static ViewGroup getActionBar(Activity activity) {
        return getActionBar(activity.getWindow().getDecorView());
    }
    
    private static ViewGroup getActionBar(View view) {
        final int resId = view.getResources().getIdentifier("action_bar_container", "id", "android");
        return (ViewGroup) view.getRootView().findViewById(resId);
    }
    
    public boolean dispatchKeyEvent(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    hide();
                    return false;
            }
        return false;
    }
    
    private final OnClickListener mUpClickListener = new OnClickListener() {
        public void onClick(View v) {
            mWindowCallback.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, (MenuItem) mBackHomeItem);
        }
    };
    private final OnClickListener mActionBarButtonClickListener = new OnClickListener() {
        public void onClick(View v) {
            mWindowCallback.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, (MenuItem) mActionButtonItem);
        }
    };
    private final OnClickListener mActionBarSpinerClickListener = new OnClickListener() {
        public void onClick(View v) {
            if(mSpinerOpen.getVisibility() == View.VISIBLE){
                mSpinerOpen.setVisibility(View.GONE);
                mSpinerClose.setVisibility(View.VISIBLE);
                if(mPopUpWindow != null){
                    mPopUpWindow.setBackgroundDrawable(mActivity.get().getResources().getDrawable(R.drawable.cld_filter_dropdown_panel_baidu_light));
                    mPopUpWindow.setFocusable(true);
                    mPopUpWindow.setOutsideTouchable(true);
                    mPopUpWindow.showAsDropDown(mTitleView);
                }          
            }else if(mSpinerClose.getVisibility() == View.VISIBLE)
            {
                mPopUpWindow.setFocusable(false);
                if(mPopUpWindow != null && mPopUpWindow.isShowing()){
                    mPopUpWindow.dismiss();
                }
            }
        }
    };
    
    OnItemClickListener listClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // TODO Auto-generated method stub
            if(arg1 instanceof TextView){
                setTitle(((TextView)arg1).getText());
            }
            if(mCallback != null){
                mCallback.onSpinerItemSelected(arg2, arg3);
            }
            mPopUpWindow.dismiss();
        } 
    };
    
    OnDismissListener dismissListenter = new OnDismissListener(){

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            mSpinerOpen.setVisibility(View.VISIBLE);
            mSpinerClose.setVisibility(View.GONE);
        }
        
    };
}

