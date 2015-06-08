
package com.baidu.themeanimation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.baidu.themeanimation.model.BatteryStatus;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.LockScreenHandler;
import com.baidu.themeanimation.util.Logger;

public class BatteryReceiver extends BroadcastReceiver {
    private final static String TAG = "BatteryReceiver";

    private LockScreenHandler mLockScreenHandler;

    public BatteryReceiver(LockScreenHandler lockScreenHandler) {
        mLockScreenHandler = lockScreenHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //Logger.i(TAG, "action = " + action);

        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {

            final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            final int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            final int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
            if (Constants.DBG_BATTERY) {
                Logger.i(TAG, "Battery: level = " + level + " status = " + status + " plugged:"
                        + pluged);
            }

            mLockScreenHandler.sendMessage(mLockScreenHandler.obtainMessage(
                    Constants.MSG_BATTERY, new BatteryStatus(status, level, pluged, health)));

        }
    }

}
