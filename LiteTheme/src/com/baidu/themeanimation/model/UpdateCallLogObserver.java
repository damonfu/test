
package com.baidu.themeanimation.model;

import android.content.Context;
import android.os.Handler;

import android.database.ContentObserver;

public class UpdateCallLogObserver extends ContentObserver {
    private final static String TAG = "UpdateCallLogObserver";
    private UpdateCallLogTask mUpdateTask = null;
    Context mContext;
    Handler mHandler;

    public UpdateCallLogObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
        updateCallLog();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        updateCallLog();
    }

    private void updateCallLog() {
        if (mUpdateTask != null) {
            mUpdateTask.cancel(true);
            mUpdateTask = null;
        }
        mUpdateTask = new UpdateCallLogTask(mContext, mHandler);
        if (mUpdateTask != null) {
            mUpdateTask.execute();
        }
    }

}
