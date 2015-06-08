package com.baidu.widget.next;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Window;
import yi.support.v1.YiLaf;

import com.baidu.widgets.R;

public class PerferencesXML extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference lp = (ListPreference)findPreference("list_preference");
        YiLaf.removePreferencePadding(this);
    }

}

