package com.baidu.widgets.perferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

public class PerferencesXML extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setDeviceDefaultLightTheme(this);
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference lp = (ListPreference)findPreference("list_preference");
    }

}

