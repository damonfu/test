/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.widgets.rice;

import com.baidu.widgets.R;
import com.baidu.widgets.Util;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

/**
 * Settings for the Alarm Clock.
 */
public class SwitchPreferenceDemo extends PreferenceActivity 
        implements Preference.OnPreferenceChangeListener {


//    private SwitcherPreference mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setYiTheme(this);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.switchpreference);
//        mSwitch = (SwitcherPreference)findPreference("switchkey");
//        mSwitch.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(final Preference p, Object newValue) {
//        if (p == mSwitch) {
//              Toast.makeText(this, "Preference is changed", Toast.LENGTH_SHORT).show();
//        }
        return true;
    }

    public void onYiTitleLeftButtonClicked(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        Toast.makeText(this, "onPreferenceTreeClick", Toast.LENGTH_SHORT).show();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void refresh() {
    }
}
