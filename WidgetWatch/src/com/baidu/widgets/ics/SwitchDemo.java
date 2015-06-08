package com.baidu.widgets.ics;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class SwitchDemo extends PreferenceActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    Util.setDeviceDefaultLightTheme(this);
        super.onCreate(savedInstanceState);       
        addPreferencesFromResource(R.xml.switch_preference);
        
        Preference cPreference = findPreference("switch_with_icon");
        cPreference.setIcon(R.drawable.ic_launcher);
        
        /* how to custom the thumb of the switch button
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        getListView().addHeaderView(linearLayout);
        
        Switch switch1 = new Switch(this);
        linearLayout.addView(switch1);
        
        Switch switch2 = new Switch(this);
        switch2.setBackgroundResource(R.drawable.ic_clock_alarmclock_switch_off_background);
        switch2.setButtonDrawable(R.drawable.ic_clock_alarmclock_switch_btn);
        linearLayout.addView(switch2);
        */
    }
}
