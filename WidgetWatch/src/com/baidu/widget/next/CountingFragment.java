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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import yi.support.v1.YiLaf;
import yi.support.v1.utils.FpsCounter;
import yi.widget.SearchView;

import com.baidu.widgets.R;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.util.Calendar;

public class CountingFragment extends Fragment implements View.OnClickListener {
    int mNum;
    private Spinner mSpinner;
    private static final String sSpinnerExampleNames[] = {
            "Title only", "Title and Icon", "Submenu", "Groups",
            "Checkable", "Shortcuts", "Order", "Category and Order",
            "Visible", "Disabled"
    };
    EditText mEditText;
    private static final String TAG = "CountingFragment";

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static CountingFragment newInstance(int num) {
        CountingFragment f = new CountingFragment();

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
        final View v = inflater.inflate(R.layout.main, container, false);

        Button btn = (Button) v.findViewById(R.id.btn_show_dialog);
        // show dialogs
        btn.setOnClickListener(this);
        registerForContextMenu(btn);

        v.findViewById(R.id.btn_fps).setOnClickListener(this);
        v.findViewById(R.id.btn_menu_transparent).setOnClickListener(this);
        v.findViewById(R.id.btn_menu_visibility).setOnClickListener(this);
        v.findViewById(R.id.btn_trigger_menu).setOnClickListener(this);
        v.findViewById(R.id.btn_ckil).setOnClickListener(this);
        v.findViewById(R.id.btn_preference).setOnClickListener(this);
        v.findViewById(R.id.btn_title).setOnClickListener(this);
        v.findViewById(R.id.btn_custom_view).setOnClickListener(this);
        v.findViewById(R.id.btn_full_screen).setOnClickListener(this);
        v.findViewById(R.id.btn_dyn_menu).setOnClickListener(this);

        mEditText = (EditText) v.findViewById(R.id.editor);
        mEditText.setOnClickListener(this);

        mSpinner = (Spinner) v.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, sSpinnerExampleNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        YiLaf.current().getActionBar().setTabOnClickListener(0, new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (v instanceof ScrollView) {
                    ((ScrollView) v).smoothScrollTo(0, 0);
                }
            }
        });
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState.isEmpty()) {
            outState.putBoolean("bug:fix", true);
        }
    }

    private boolean mIsMenuPanelShown = true;
    private boolean mIsPanelTransparent = false;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.btn_show_dialog:
                final ViewGroup vg = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                        R.layout.dialog_content_demo, null);
                ViewGroup parent = ((ViewGroup) vg.getParent());
                if (parent != null) {
                    parent.removeView(vg);
                }
                // show list dialog
                vg.findViewById(R.id.list_btn).setOnClickListener(this);
                // show message dialog
                vg.findViewById(R.id.message).setOnClickListener(this);
                // show progress dialog
                vg.findViewById(R.id.progress_btn).setOnClickListener(this);
                // show date picker dialog
                vg.findViewById(R.id.date_picker_dialog).setOnClickListener(this);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Test")
                        .setView(vg)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).create().show();
                break;
            case R.id.btn_full_screen:
                YiLaf.current().setContentViewExclusive(!YiLaf.current().isContentViewExclusive());
                break;
            case R.id.btn_menu_visibility:
                YiLaf.current().getMenu().setPanelVisibility(mIsMenuPanelShown ? View.GONE : View.VISIBLE);
                mIsMenuPanelShown = !mIsMenuPanelShown;
                break;
            case R.id.btn_fps:
                if (FpsCounter.isEnabled(getActivity())) {
                    FpsCounter.disable(getActivity());
                } else {
                    FpsCounter.enable(getActivity());
                }
                break;
            case R.id.message:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select item")
                        .setMessage("message")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).create().show();
                break;
            case R.id.list_btn:
                String[] content = {
                        "test1", "test2"
                };
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select item")
                        .setItems(content, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).create().show();
                break;
            case R.id.progress_btn:
                final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setTitle("Title");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMax(100);
                mProgressDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Hide",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked Yes so do some stuff */
                            }
                        });
                mProgressDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked No so do some stuff */
                            }
                        });
                mProgressDialog.show();
                final Handler mProgressHandler = new Handler() {
                    int mProgress = 0;

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (mProgress >= 100) {
                            mProgressDialog.dismiss();
                        } else {
                            mProgress++;
                            mProgressDialog
                                    .setProgressNumberFormat(getString(R.string.exporting_contact_list_progress));
                            mProgressDialog.incrementProgressBy(1);
                            sendEmptyMessageDelayed(0, 100);
                        }
                    }
                };
                mProgressDialog.setProgress(0);
                mProgressHandler.sendEmptyMessage(0);
                break;
            case R.id.date_picker_dialog:
                final Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), null, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.editor:
                mEditText.setFocusableInTouchMode(true);
                mEditText.requestFocus();
                break;
            case R.id.btn_title:
                intent = new Intent();
                intent.setClass(getActivity(), ActionBarTitleActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_ckil:
                Intent ckilintent = new Intent();
                ckilintent.setClass(getActivity(), com.baidu.widgets.rice.CheckableListView.class);
                startActivity(ckilintent);
                break;
            case R.id.btn_preference:
                intent = new Intent();
                intent.setClass(getActivity(), PerferencesXML.class);
                startActivity(intent);
                break;
            case R.id.btn_menu_transparent:
                mIsPanelTransparent = !mIsPanelTransparent;
                YiLaf.current().getMenu().setPanelTransparency(mIsPanelTransparent);
                break;

            case R.id.btn_trigger_menu:
                YiLaf.current().openOptionsMenu();
                break;

            case R.id.btn_custom_view:
                showSearchView();
                break;

            case R.id.btn_dyn_menu:
                showOrHideMenuItem();
                break;

            default:
                break;

        }
    }
    
    boolean menuMode = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.dyn_menu) {
            menuMode = !menuMode;
            YiLaf.current().invalidateOptionsMenu();
            return true;
        } else {
          return false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //showOrHideMenuItem();
    }

    private void showSearchView() {
        final SearchView searchView = new SearchView(this.getActivity());

        searchView.showSearchBackIcon(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                YiLaf.current().getActionBar().removeCustomView();
                searchView.clearFocus();
            }

        }, false);

        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getString(R.string.cheese_hunt_hint));

        YiLaf.current().getActionBar().setCustomView(searchView);
        searchView.requestFocus();
        searchView.setIconified(false);
    }

    public void showOrHideMenuItem() {
        if (mMenu.getItem(0).isVisible()) {
            mMenu.getItem(0).setVisible(false);
        } else if (mMenu.getItem(1).isVisible()) {
            mMenu.getItem(1).setVisible(false);
        } else if (mMenu.getItem(2).isVisible()) {
            mMenu.getItem(2).setVisible(false);
        } else {
            mMenu.getItem(0).setVisible(true);
            mMenu.getItem(1).setVisible(true);
            mMenu.getItem(2).setVisible(true);
            
            mMenu.getItem(1).setEnabled(false);
        }
    }

    private Menu mMenu;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        
        if (menuMode) {
            inflater.inflate(R.menu.second, menu);
        } else {
            inflater.inflate(R.menu.counting, menu);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        // set context menu title
        menu.setHeaderTitle("files manager");
        // add context menu item
        menu.add(0, 1, Menu.NONE, "send");
        menu.add(0, 3, Menu.NONE, "rename");
        menu.add(0, 4, Menu.NONE, "delete");
    }
}
