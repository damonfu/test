
package com.baidu.themeanimation.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.database.sqlite.SQLiteException;

import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.Constants;

/*
 * get the number of missed call which have not read
 */

public class UpdateCallLogTask extends AsyncTask<Void, Integer, Void> {
    private final static String TAG = "UpdateCallLogTask";
    private Context mContext;
    private Handler mHandler;
    private int mCount;

    public UpdateCallLogTask(final Context context, final Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String[] projection = new String[] {
                CallLog.Calls._ID
        };

        String select = CallLog.Calls.TYPE + "=? and " + CallLog.Calls.NEW + "=1";
        String selectArgs[] = new String[] {
                Integer.toString(CallLog.Calls.MISSED_TYPE)
        };
        mCount = 0;
        Cursor query = null;
        try {
            query = mContext.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, projection, select, selectArgs, null);
            if (null != query) {
                mCount = query.getCount();
                query.close();
            } else {
                Logger.e(TAG, "getContentResolver for CallLog return null");
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (query != null)
                query.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_UPDATE_CALLLOG,
                (Integer) mCount));
    }
}
