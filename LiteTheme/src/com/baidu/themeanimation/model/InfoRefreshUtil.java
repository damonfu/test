
package com.baidu.themeanimation.model;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.os.PowerManager;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.receiver.BatteryReceiver;
import com.baidu.themeanimation.receiver.VariableMonitorReceiver;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.LockScreenHandler;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.VariableConstants;

public class InfoRefreshUtil {
    public static final String TAG = "InfoRefreshUtil";

    private VariableMonitorReceiver mVariableMonitorReceiver;
    private IntentFilter mVariableIntentFilter;
    private BatteryReceiver mBatteryReceiver;
    //private IntentFilter mMusicPlayerIntentFilter;
    //private MusicControlReceiver mMusicControlReceiver;
    private IntentFilter mBatteryIntentFilter;
    private UpdateCallLogObserver mUpdateCallLogObserver;
    private UpdateSmsObserver mUpdateSmsObserver;
    private AlarmContentObserve mAlarmContentObserve;

    private ExpressionManager mExpressionManager;

    private LockScreenHandler mHandler;
    private Context mContext;

    public InfoRefreshUtil(Context context, LockScreenHandler handler) {
        mHandler = handler;
        mContext = context;
        mExpressionManager = ExpressionManager.getInstance();
    }

    public InfoRefreshUtil(Context context, String rootPath, String lockDir, String wallpaperDir) {
        mContext = context;
        mExpressionManager = ExpressionManager.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(dm);
        FileUtil.getInstance().setDensity(dm, rootPath, lockDir, wallpaperDir);
        dm = null;
    }

    public void setGlobalVariable() {
        mExpressionManager.setVariableValue(VariableConstants.VAI_SCREEN_WIDTH,
                FileUtil.DESIGH_SCREEN_WIDTH);
        mExpressionManager.setVariableValue(VariableConstants.VAI_SCREEN_HEIGHT,
                FileUtil.DESIGN_SCREEN_HEIGHT);
        mExpressionManager.setVariableValue(VariableConstants.VAI_TIME_SYS,
                System.currentTimeMillis());
        mExpressionManager.setVariableValue(VariableConstants.MUSIC_CONTROL_VISIBILITY, 0);
        mExpressionManager.setVariableValue(VariableConstants.DATE_POSITION_Y, 0);
        mExpressionManager.setVariableValue(VariableConstants.TEXT_SIZE_DATE, 14);
        mExpressionManager.setVariableValue(VariableConstants.SHOW_DATE, 1);
        mExpressionManager.setVariableValue(VariableConstants.DATE_FOMATE, "yyyy/M/d EEEE");
        mExpressionManager.setVariableValue(VariableConstants.VAI_CALL_MISSED_COUNT, 0);
        mExpressionManager.setVariableValue(VariableConstants.VAI_SMS_UNREAD_COUNT, 0);
    }

    public void unregister_Receiver_destory() {
        Logger.i(TAG, "unregister_Receiver_destory");
        try {
            mContext.unregisterReceiver(mVariableMonitorReceiver);
            mContext.getContentResolver().unregisterContentObserver(
                    mUpdateCallLogObserver);
            mContext.getContentResolver().unregisterContentObserver(mUpdateSmsObserver);
            mContext.getContentResolver().unregisterContentObserver(mAlarmContentObserve);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register_Receiver_create() {
        Logger.i(TAG, "register_Receiver_create");

        mVariableIntentFilter = new IntentFilter();
        mVariableIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        mVariableIntentFilter.addAction(Constants.ACTION_UNLOCK_INTENT);
        mBatteryIntentFilter = new IntentFilter();
        mBatteryIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        //mMusicPlayerIntentFilter = new IntentFilter();
        //mMusicPlayerIntentFilter.addAction("com.android.music.metachanged");

        mBatteryReceiver = new BatteryReceiver(mHandler);
        mVariableMonitorReceiver = new VariableMonitorReceiver(mHandler);
        mUpdateCallLogObserver = new UpdateCallLogObserver(mContext, mHandler);
        mUpdateSmsObserver = new UpdateSmsObserver(mContext, mHandler);
        mAlarmContentObserve = new AlarmContentObserve(mHandler);
        //mMusicControlReceiver = new MusicControlReceiver();

        mContext.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
                false, mUpdateCallLogObserver);
        mContext.getContentResolver().registerContentObserver(
                Uri.parse(Constants.SMS_URI), true, mUpdateSmsObserver);
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.NEXT_ALARM_FORMATTED), false,
                mAlarmContentObserve);

        mContext.registerReceiver(mVariableMonitorReceiver, mVariableIntentFilter);
    }

    public void unregister_Receiver_pause() {
        Logger.i(TAG, "unregister_Receiver_pause");
        try {
            mContext.unregisterReceiver(mBatteryReceiver);
            //mMusicControlReceiver.clearAllMusicPlayCB();
            //mContext.unregisterReceiver(mMusicControlReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register_Receiver_resume() {
        Logger.i(TAG, "register_Receiver_resume");

        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_TIME_TICK,
                    null));
            mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_UPDATE_ALARM,
                    null));
        }
        if(mBatteryReceiver!=null){
            mContext.registerReceiver(mBatteryReceiver, mBatteryIntentFilter);
        }
        //mContext.registerReceiver(mMusicControlReceiver, mMusicPlayerIntentFilter);
    }

    public void dispatchTouch(float x, float y) {
        mExpressionManager.setVariableValue(VariableConstants.VAI_TOUCH_X,
                (int) (x / FileUtil.X_SCALE));
        mExpressionManager.setVariableValue(VariableConstants.VAI_TOUCH_Y,
                (int) (y / FileUtil.Y_SCALE));
    }

    public static class AlarmContentObserve extends ContentObserver {
        Handler mHandler;

        public AlarmContentObserve(Handler handler) {
            super(handler);
            mHandler = handler;
            updateAlarm();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateAlarm();
        }

        private void updateAlarm() {
            if (mHandler != null) {
                mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_UPDATE_ALARM,
                        null));
            }
        }
    }
}
