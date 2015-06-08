package yi.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.baidu.lite.R;

public class ViewHelper {
    private static int mBackground = R.drawable.yi_tab_background;

	static public View createIndicatorView (TabWidget parent, CharSequence label) {
		return createIndicatorView(parent, label, null);
	}

	static public View createIndicatorView (TabWidget parent, CharSequence label, int iconId) {
		return createIndicatorView(parent, label, parent.getResources().getDrawable(iconId));
	}

	static public View createIndicatorView (TabWidget parent, CharSequence label, Drawable icon) {
        final Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabIndicator = inflater.inflate(R.layout.yi_tab_indicator, parent, false);

        // set Label
        final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
        if (tv != null) {
        	tv.setText(label);
        }
        
        // set Icon
        final ImageView iconView = (ImageView) tabIndicator.findViewById(R.id.icon);
        if (icon != null && iconView != null) {
        	iconView.setImageDrawable(icon);
        }

        // set Background
        if (mBackground != R.drawable.yi_tab_background) {
            tabIndicator.setBackgroundResource(mBackground);
        }
            
        Drawable d = tabIndicator.getBackground();

        if(d == null) 
            return tabIndicator;
        
        int childCount = parent.getChildCount();
        
        if (childCount == 0) {
        	d.setLevel(1);
        	return tabIndicator;
        }

        d.setLevel(4);
        
        View child;
        child = parent.getChildAt(0);
        child.getBackground().setLevel(2);

        for(int i = 1; i < childCount; i++) {
            child = parent.getChildAt(i);
            child.getBackground().setLevel(3);
        }
        
        return tabIndicator;
	}

	/**
	* custom tab background
	*
	*@param drawable  tab background res id
     */
    static public void setIndicatorBackground(int drawable) {
  	    mBackground = drawable;
    }
}
