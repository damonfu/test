package com.baidu.widgets.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.Dialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
//import yi.app.BaiduAppTitle;
import com.baidu.widgets.R;
import com.baidu.widgets.Util;


public class AlertDialogs extends Activity {
	private static final int DIALOG_YES_MESSAGE = 0;
    private static final int DIALOG_YES_NO_MESSAGE = 1;
    private static final int DIALOG_YES_NO_LONG_MESSAGE = 2;
    private static final int DIALOG_LIST = 3;
    private static final int DIALOG_PROGRESS = 4;
    private static final int DIALOG_SINGLE_CHOICE = 5;
    private static final int DIALOG_MULTIPLE_CHOICE = 6;
    private static final int DIALOG_TEXT_ENTRY = 7;

    private static final int MAX_PROGRESS = 100;
    
    private ProgressDialog mProgressDialog;
    private int mProgress;
    private Handler mProgressHandler;
    //private BaiduAppTitle mBaiduAppTitle;
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_YES_MESSAGE:
            return new AlertDialog.Builder(AlertDialogs.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("One Button")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked OK so do some stuff */
                    	Toast.makeText(AlertDialogs.this, "DIALOG_YES_MESSAGE", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        case DIALOG_YES_NO_MESSAGE:
            return new AlertDialog.Builder(AlertDialogs.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Two Buttons")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked OK so do some stuff */
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
        case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(AlertDialogs.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    }
                })
                .setNeutralButton("Any", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Something so do some stuff */
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
        case DIALOG_LIST:
            return new AlertDialog.Builder(AlertDialogs.this)
                .setTitle("Select item")
                .setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                        String[] items = getResources().getStringArray(R.array.select_dialog_items);
                        new AlertDialog.Builder(AlertDialogs.this)
                                .setMessage("You selected: " + which + " , " + items[which])
                                .show();
                    }
                })
                .create();
        case DIALOG_PROGRESS:
            mProgressDialog = new ProgressDialog(AlertDialogs.this);
            mProgressDialog.setIcon(R.drawable.alert_dialog_icon);
            mProgressDialog.setTitle("Title");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMax(MAX_PROGRESS);
            mProgressDialog.setButton("Hide", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked Yes so do some stuff */
                }
            });
            mProgressDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked No so do some stuff */
                }
            });
            return mProgressDialog;
        case DIALOG_SINGLE_CHOICE:
            return new AlertDialog.Builder(AlertDialogs.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Title")
                .setSingleChoiceItems(R.array.select_dialog_items2, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked on a radio button do some stuff */
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Yes so do some stuff */
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                    }
                })
               .create();
        case DIALOG_MULTIPLE_CHOICE:
            return new AlertDialog.Builder(AlertDialogs.this)
                .setIcon(R.drawable.ic_popup_reminder)
                .setTitle("Title")
                .setMultiChoiceItems(R.array.select_dialog_items3,
                        new boolean[]{false, true, false, true, false, false, false},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton,
                                    boolean isChecked) {

                                /* User clicked on a check box do some stuff */
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Yes so do some stuff */
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                    }
                })
               .create();
        case DIALOG_TEXT_ENTRY:
            // This example shows how to add a custom layout to an AlertDialog
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
            AlertDialog alertDialog =  new AlertDialog.Builder(AlertDialogs.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Title")
                .setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
                alertDialog.setCancelable(false);
                return alertDialog;
        }
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView(int)} to
     * describe what is to be displayed in the screen.
     */
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setDeviceDefaultLightTheme(this);
        setContentView(R.layout.alert_dialog);
 
        Button oneButtonTitle = (Button) findViewById(R.id.one_button);
        oneButtonTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_YES_MESSAGE);
            }
        });
        
        /* Display a text message with yes/no buttons and handle each message as well as the cancel action */
        Button twoButtonsTitle = (Button) findViewById(R.id.two_buttons);
        twoButtonsTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_MESSAGE);
            }
        });
        
        /* Display a long text message with yes/no buttons and handle each message as well as the cancel action */
        Button twoButtons2Title = (Button) findViewById(R.id.two_buttons2);
        twoButtons2Title.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_LONG_MESSAGE);
            }
        });
        
        
        /* Display a list of items */
        Button selectButton = (Button) findViewById(R.id.select_button);
        selectButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_LIST);
            }
        });
        
        /* Display a custom progress bar */
        Button progressButton = (Button) findViewById(R.id.progress_button);
        progressButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_PROGRESS);
                mProgress = 0;
                mProgressDialog.setProgress(0);
                mProgressHandler.sendEmptyMessage(0);
            }
        });
        
        /* Display a radio button group */
        Button radioButton = (Button) findViewById(R.id.radio_button);
        radioButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SINGLE_CHOICE);
            }
        });
        
        /* Display a list of checkboxes */
        Button checkBox = (Button) findViewById(R.id.checkbox_button);
        checkBox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_MULTIPLE_CHOICE);
            }
        });

        /* Display a text entry dialog */
        Button textEntry = (Button) findViewById(R.id.text_entry_button);
        textEntry.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_TEXT_ENTRY);
            }
        });
        
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgress >= MAX_PROGRESS) {
                    mProgressDialog.dismiss();
                } else {
                    mProgress++;
                    mProgressDialog.setProgressNumberFormat(getString(R.string.exporting_contact_list_progress));
                    mProgressDialog.incrementProgressBy(1);
                    mProgressHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };
    }

	@Override
	public void startSearch(String initialQuery, boolean selectInitialQuery,
			Bundle appSearchData, boolean globalSearch) {
		super.startSearch(initialQuery, selectInitialQuery, appSearchData, globalSearch);
		Log.d("SYGTC", "startSearch()");
	}
    
}
