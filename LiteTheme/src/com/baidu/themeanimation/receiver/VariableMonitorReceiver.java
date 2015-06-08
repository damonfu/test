
package com.baidu.themeanimation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.LockScreenHandler;
import com.baidu.themeanimation.util.Logger;

public class VariableMonitorReceiver extends BroadcastReceiver {
    private final static String TAG = "VariableMonitorReceiver";

    private LockScreenHandler mLockScreenHandler;

    public VariableMonitorReceiver(LockScreenHandler lockScreenHandler) {
        mLockScreenHandler = lockScreenHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Logger.i(TAG, "action = " + action);

        if (action.equals(Intent.ACTION_TIME_TICK)) {
            // per-minute
            Message message = mLockScreenHandler.obtainMessage();
            message.what = Constants.MSG_TIME_TICK;
            mLockScreenHandler.sendMessage(message);

        }  else if (action.equals(Constants.ACTION_UNLOCK_INTENT)) {
            mLockScreenHandler.sendMessage(mLockScreenHandler.obtainMessage(
                    Constants.MSG_UNLOCK_INTENT,
                    intent));
        }
    }

}
