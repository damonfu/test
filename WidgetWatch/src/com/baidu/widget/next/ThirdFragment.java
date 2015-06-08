/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.baidu.widget.next;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import yi.support.v1.YiLaf;

import com.baidu.widgets.R;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class ThirdFragment extends Fragment implements OnClickListener {
    int mNum;

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static ThirdFragment newInstance(int num) {
        ThirdFragment f = new ThirdFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        YiLaf.current().invalidateOptionsMenu();
    }

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TextView v = new TextView(getActivity());
        // v.setText("Third Fragment");
        View v = inflater.inflate(R.layout.third, container, false);
        v.findViewById(R.id.datePickerDialog).setOnClickListener(this);
        v.findViewById(R.id.timePickerDialog).setOnClickListener(this);
        v.findViewById(R.id.Expandable).setOnClickListener(this);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState.isEmpty()) {
            outState.putBoolean("bug:fix", true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.third, menu);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.datePickerDialog:
                new DatePickerDialog(getActivity(), new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub

                    }
                }, 1900, 0, 1).show();
                break;
            case R.id.timePickerDialog:
                new TimePickerDialog(getActivity(), new OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub

                    }
                }, 0, 0, false).show();
                break;
            case R.id.Expandable:
                Intent ckilintent = new Intent();
                // ckilintent.putExtra("Theme", "tabtop");
                ckilintent.setClass(getActivity(), com.baidu.widgets.lists.Expandable2.class);
                startActivity(ckilintent);
            default:
                break;
        }
    }
}
