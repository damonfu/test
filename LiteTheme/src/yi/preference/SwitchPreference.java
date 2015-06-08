/*
 * Copyright (C) 2010 The Android Open Source Project
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

package yi.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.baidu.lite.R;

import yi.util.IDHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
 * @author rendayun
 *
 */
public class SwitchPreference extends TwoStatePreference {
    // Switch text for on and off states
    private CharSequence mSwitchOn;
    private CharSequence mSwitchOff;
    private final Listener mListener = new Listener();

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                // Listener didn't like it, change it back.
                // CompoundButton will make sure we don't recurse.
                buttonView.setChecked(!isChecked);
                return;
            }

            SwitchPreference.this.setChecked(isChecked);
        }
    }

    /**
     * Construct a new SwitchPreference with the given style options.
     * 
     * @param context The Context that will style this preference
     * @param attrs Style attributes that differ from the default
     * @param defStyle Theme attribute defining the default style options
     */
    public SwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                IDHelper.getStyleableArrByName("SwitchPreference"), defStyle, 0);
        setSummaryOn(a.getString(IDHelper.getStyleableByName("SwitchPreference_summaryOn")));
        setSummaryOff(a.getString(IDHelper.getStyleableByName("SwitchPreference_summaryOff")));
        setSwitchTextOn(a.getString(
                IDHelper.getStyleableByName("SwitchPreference_switchTextOn")));
        setSwitchTextOff(a.getString(
                IDHelper.getStyleableByName("SwitchPreference_switchTextOff")));
        setDisableDependentsState(a.getBoolean(
                IDHelper.getStyleableByName("SwitchPreference_disableDependentsState"), false));
        a.recycle();
    }

    /**
     * Construct a new SwitchPreference with the given style options.
     * 
     * @param context The Context that will style this preference
     * @param attrs Style attributes that differ from the default
     */
    public SwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchPreferenceStyle);
    }

    /**
     * Construct a new SwitchPreference with default style options.
     * 
     * @param context The Context that will style this preference
     */
    public SwitchPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        Switch checkableView = (Switch) view.findViewById(R.id.switchWidget);
        if (checkableView != null && checkableView instanceof Checkable) {
            checkableView.setTextAppearance(this.getContext(), R.style.Widget_DeviceDefault_Light_CompoundButton_Switch);
            boolean checked = false;
            final Field field = getParentField(this, "mChecked");
            final boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                checked = field.getBoolean(this);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(accessible);
            ((Checkable) checkableView).setChecked(checked);

            // sendAccessibilityEvent(checkableView);
            Object[] objs = {
                (Object) checkableView
            };
            invoke(this, "sendAccessibilityEvent", objs, View.class);

            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setTextOn(mSwitchOn);
                switchView.setTextOff(mSwitchOff);
                switchView.setOnCheckedChangeListener(mListener);
            }
        }

        // syncSummaryView(view);
        Object[] objs = {
            (Object) view
        };
        invoke(this, "syncSummaryView", objs, View.class);
    }

    Field getParentField(Object object, String fieldName) {
        Field field = null;
        try {
            field = object.getClass().getSuperclass()
                    .getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    Method getParentMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = object.getClass().getSuperclass()
                    .getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    public Object invoke(final Object obj, final String methodName, Object[] parameters,
            Class<?>... parameterTypes) {
        try {
            Method method = getParentMethod(obj, methodName, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(obj, parameters);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the text displayed on the switch widget in the on state. This should
     * be a very short string; one word if possible.
     * 
     * @param onText Text to display in the on state
     */
    public void setSwitchTextOn(CharSequence onText) {
        mSwitchOn = onText;
        notifyChanged();
    }

    /**
     * Set the text displayed on the switch widget in the off state. This should
     * be a very short string; one word if possible.
     * 
     * @param offText Text to display in the off state
     */
    public void setSwitchTextOff(CharSequence offText) {
        mSwitchOff = offText;
        notifyChanged();
    }

    /**
     * Set the text displayed on the switch widget in the on state. This should
     * be a very short string; one word if possible.
     * 
     * @param resId The text as a string resource ID
     */
    public void setSwitchTextOn(int resId) {
        setSwitchTextOn(getContext().getString(resId));
    }

    /**
     * Set the text displayed on the switch widget in the off state. This should
     * be a very short string; one word if possible.
     * 
     * @param resId The text as a string resource ID
     */
    public void setSwitchTextOff(int resId) {
        setSwitchTextOff(getContext().getString(resId));
    }

    /**
     * @return The text that will be displayed on the switch widget in the on
     *         state
     */
    public CharSequence getSwitchTextOn() {
        return mSwitchOn;
    }

    /**
     * @return The text that will be displayed on the switch widget in the off
     *         state
     */
    public CharSequence getSwitchTextOff() {
        return mSwitchOff;
    }
}
