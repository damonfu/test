
package com.baidu.themeanimation.model;

import android.content.Context;
import android.os.Handler;

import android.database.ContentObserver;

public class UpdateSmsObserver extends ContentObserver {
    private final static String TAG = "UpdateSmsObserver";
    UpdateSmsTask mUpdateTask = null;
    Context mContext;
    Handler mHandler;

    public UpdateSmsObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
        updateSms();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        updateSms();
    }

    private void updateSms() {
        if (mUpdateTask != null) {
            mUpdateTask.cancel(true);
            mUpdateTask = null;
        }

        mUpdateTask = new UpdateSmsTask(mContext, mHandler);
        if (mUpdateTask != null) {
            mUpdateTask.execute();
        }
    }
}
