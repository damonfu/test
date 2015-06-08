
package com.baidu.themeanimation.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.AsyncTask;
import android.database.sqlite.SQLiteException;

import android.net.Uri;

import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.Constants;

public class UpdateSmsTask extends AsyncTask<Void, Integer, Void> {
    private final static String TAG = "UpdateSmsTask";
    private Context mContext;
    private Handler mHandler;
    private int mCount;

    public UpdateSmsTask(final Context context, final Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Cursor query1 = null;
        Cursor query2 = null;
        int countSms = 0, countMms = 0;
        try {
            query1 = mContext.getContentResolver().query(
                    Uri.parse("content://sms"), null, "read = 0", null, null);
            if (null != query1) {
                countSms = query1.getCount();
            }

            query2 = mContext.getContentResolver().query(
                    Uri.parse("content://mms/inbox"), null, "read = 0", null, null);
            if (null != query2) {
                countMms = query2.getCount();
            }
            Logger.i(TAG, "mms = "+countMms + "sms= " + countSms);
            mCount = countSms + countMms;

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (query1 != null) {
                query1.close();
            }
            if (query2 != null) {
                query2.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Logger.i(TAG, "onPostExecute");
        mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_UPDATE_SMSLOG,
                (Integer) mCount));
    }
}
