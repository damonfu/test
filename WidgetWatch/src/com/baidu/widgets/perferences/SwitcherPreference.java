/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.baidu.widgets.perferences;

import yi.util.IDHelper;
import yi.widget.Switcher;
import yi.widget.Switcher.OnSwitcherChangeListener;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.TextView;

public class SwitcherPreference extends Preference {

    private CharSequence mSummaryOn;
    private CharSequence mSummaryOff;
    
    private boolean mChecked;
    
    private Switcher mSwitcher;
    
    private boolean mSendAccessibilityEventViewClickedType;

    private AccessibilityManager mAccessibilityManager;
    
    private boolean mDisableDependentsState;


    private final Listener mListener = new Listener();

    private class Listener implements OnSwitcherChangeListener {
        @Override
        public void onSwitcherToggleChanged(Switcher switcherView, boolean toggled) {
            mSendAccessibilityEventViewClickedType = true;

            if (!callChangeListener(toggled)) {
                switcherView.setChecked(!toggled);
                return;
            }

            SwitcherPreference.this.setChecked(toggled);
        }
    }
    
    public SwitcherPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        TypedArray a = context.obtainStyledAttributes(attrs, IDHelper.getStyleableArrByName("CheckBoxPreference"), defStyle, 0);
        mSummaryOn = a.getString(IDHelper.getStyleableByName("CheckBoxPreference_summaryOn"));
        mSummaryOff = a.getString(IDHelper.getStyleableByName("CheckBoxPreference_summaryOff"));
        mDisableDependentsState = a.getBoolean(IDHelper.getStyleableByName("CheckBoxPreference_disableDependentsState"), false);
        a.recycle();
        
        mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(Service.ACCESSIBILITY_SERVICE);
    }

    public SwitcherPreference(Context context, AttributeSet attrs) {
        this(context, attrs, context.getResources().getIdentifier("switcherPreferenceStyle", "attr", "yi"));
    }

    public SwitcherPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mSwitcher = (Switcher)view.findViewById(getContext().getResources().getIdentifier("switcher", "id", "yi"));
        if (mSwitcher != null && mSwitcher instanceof Checkable) {
            ((Checkable) mSwitcher).setChecked(mChecked);
            mSwitcher.setOnSwitcherChangeListener(mListener);
            
            // send an event to announce the value change of the CheckBox and is done here
            // because clicking a preference does not immediately change the checked state
            // for example when enabling the WiFi
            if (mSendAccessibilityEventViewClickedType &&
                    mAccessibilityManager.isEnabled() &&
                    mSwitcher.isEnabled()) {
                mSendAccessibilityEventViewClickedType = false;

                int eventType = AccessibilityEvent.TYPE_VIEW_CLICKED;
                mSwitcher.sendAccessibilityEventUnchecked(AccessibilityEvent.obtain(eventType));
            }
            
        }

        // Sync the summary view
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
            boolean useDefaultSummary = true;
            if (mChecked && mSummaryOn != null) {
                summaryView.setText(mSummaryOn);
                useDefaultSummary = false;
            } else if (!mChecked && mSummaryOff != null) {
                summaryView.setText(mSummaryOff);
                useDefaultSummary = false;
            }

            if (useDefaultSummary) {
                final CharSequence summary = getSummary();
                if (summary != null) {
                    summaryView.setText(summary);
                    useDefaultSummary = false;
                }
            }
            
            int newVisibility = View.GONE;
            if (!useDefaultSummary) {
                // Someone has written to it
                newVisibility = View.VISIBLE;
            }
            if (newVisibility != summaryView.getVisibility()) {
                summaryView.setVisibility(newVisibility);
            }
        }
    }

    /**
     * Sets the checked state and saves it to the {@link SharedPreferences}.
     * 
     * @param checked The checked state.
     */
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            persistBoolean(checked);
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    /**
     * Returns the checked state.
     * 
     * @return The checked state.
     */
    public boolean isChecked() {
        return mChecked;
    }
    
    @Override
    public boolean shouldDisableDependents() {
        boolean shouldDisable = mDisableDependentsState ? mChecked : !mChecked;
        return shouldDisable || super.shouldDisableDependents();
    }

    /**
     * Sets the summary to be shown when checked.
     * 
     * @param summary The summary to be shown when checked.
     */
    public void setSummaryOn(CharSequence summary) {
        mSummaryOn = summary;
        if (isChecked()) {
            notifyChanged();
        }
    }

    /**
     * @see #setSummaryOn(CharSequence)
     * @param summaryResId The summary as a resource.
     */
    public void setSummaryOn(int summaryResId) {
        setSummaryOn(getContext().getString(summaryResId));
    }
    
    /**
     * Returns the summary to be shown when checked.
     * @return The summary.
     */
    public CharSequence getSummaryOn() {
        return mSummaryOn;
    }
    
    /**
     * Sets the summary to be shown when unchecked.
     * 
     * @param summary The summary to be shown when unchecked.
     */
    public void setSummaryOff(CharSequence summary) {
        mSummaryOff = summary;
        if (!isChecked()) {
            notifyChanged();
        }
    }

    /**
     * @see #setSummaryOff(CharSequence)
     * @param summaryResId The summary as a resource.
     */
    public void setSummaryOff(int summaryResId) {
        setSummaryOff(getContext().getString(summaryResId));
    }
    
    /**
     * Returns the summary to be shown when unchecked.
     * @return The summary.
     */
    public CharSequence getSummaryOff() {
        return mSummaryOff;
    }

    /**
     * Returns whether dependents are disabled when this preference is on ({@code true})
     * or when this preference is off ({@code false}).
     * 
     * @return Whether dependents are disabled when this preference is on ({@code true})
     *         or when this preference is off ({@code false}).
     */
    public boolean getDisableDependentsState() {
        return mDisableDependentsState;
    }

    /**
     * Sets whether dependents are disabled when this preference is on ({@code true})
     * or when this preference is off ({@code false}).
     * 
     * @param disableDependentsState The preference state that should disable dependents.
     */
    public void setDisableDependentsState(boolean disableDependentsState) {
        mDisableDependentsState = disableDependentsState;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, false);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setChecked(restoreValue ? getPersistedBoolean(mChecked)
                : (Boolean) defaultValue);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }
        
        final SavedState myState = new SavedState(superState);
        myState.checked = isChecked();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setChecked(myState.checked);
    }
    
    private static class SavedState extends BaseSavedState {
        boolean checked;
        
        public SavedState(Parcel source) {
            super(source);
            checked = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(checked ? 1 : 0);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    
}
