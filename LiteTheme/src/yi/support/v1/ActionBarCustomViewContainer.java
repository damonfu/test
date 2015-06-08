package yi.support.v1;

import java.lang.ref.WeakReference;

import yi.widget.SearchView;
import yi.support.v1.menu.HybridMenu;

import android.app.Activity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.baidu.lite.R;

public class ActionBarCustomViewContainer {

    private WeakReference<Activity> mActivity = new WeakReference<Activity>(null);
    private boolean mShown = false;

    public void show(Activity activity, View view) {
        mActivity = new WeakReference<Activity>(activity);
        if (mActivity.get() != null) {
            ViewGroup actionbar = getActionBar(mActivity.get());
            if (actionbar != null) {
                // remove before add
                View remove = actionbar.findViewById(R.id.action_bar_custom_view);
                actionbar.removeView(remove);

                // add new one 
                view.setBackgroundDrawable(mActivity.get().getResources()
                        .getDrawable(R.drawable.cld_search_view_background));
                
                view.setId(R.id.action_bar_custom_view);

                actionbar.addView(view, new FrameLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL));

                view.startAnimation(AnimationUtils.loadAnimation(mActivity.get(),
                        R.anim.cld_view_top_in));
                
                mShown = true;
                ((HybridMenu) YiLaf.get(mActivity.get()).getMenu()).refreshPanelVisibility();
            }
        }
    }

    public void hide() {
        if (mActivity.get() != null) {
            ViewGroup actionbar = getActionBar(mActivity.get());
            if (actionbar != null) {
                View customView = actionbar.findViewById(R.id.action_bar_custom_view);
                if (customView != null) {
                    Animation fadeout = AnimationUtils.loadAnimation(mActivity.get(), R.anim.cld_view_top_out);
                    customView.startAnimation(fadeout);
                    actionbar.removeView(customView);
                    if(customView instanceof SearchView){
                        ((SearchView) customView).isViewFadeOut();
                    }
                }
                
                mShown = false;
                ((HybridMenu) YiLaf.get(mActivity.get()).getMenu()).refreshPanelVisibility();
            }
        }
    }

    private static ViewGroup getActionBar(Activity activity) {
        return getActionBar(activity.getWindow().getDecorView());
    }
    
    private static ViewGroup getActionBar(View view) {
        final int resId = view.getResources().getIdentifier("action_bar_container", "id", "android");
        return (ViewGroup) view.getRootView().findViewById(resId);
    }
    
    public boolean isShown() {
        return mShown;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isShown()) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    hide();
                    return true;
            }
        }
        return false;
    }

}

