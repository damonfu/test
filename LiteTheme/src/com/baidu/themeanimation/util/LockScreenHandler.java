
package com.baidu.themeanimation.util;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.content.Context;
import android.provider.Settings;
import android.provider.MediaStore;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.model.BatteryStatus;

public class LockScreenHandler extends Handler {
    private final static String TAG = "LockScreenHandler";

    public interface HandlerCallback {
        void unLock();

        void updateWallpaper();

        void setCategory(int category);

        void dispatchTimeTick(Time time);
    }

    private Boolean mPhoneing = false;
    private Boolean mScreenOff = false;

    private long mLastSecond = -1;
    private ExpressionManager mExpressionManager;
    private Context mContext;
    private HandlerCallback mCallback;

    public boolean mTimeOut = true;

    public boolean isTimeOut() {
        return mTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        mTimeOut = timeOut;
    }

    public LockScreenHandler(Context context, HandlerCallback callback) {
        mExpressionManager = ExpressionManager.getInstance();
        mContext = context;
        mCallback = callback;
    }

    public void startIntent(Intent intent) {
        String packageName = intent.getPackage();
        String className = intent.getClass().getName();
        String componentName = null;

        if (!intent.getBooleanExtra(Constants.JUST_UNLOCK, false)) {
            try {

                if (intent.getComponent() != null) {
                    componentName = intent.getComponent().getClassName();
                }

                Logger.i(TAG, "intent: startIntent: packageName=" + packageName +
                        " className= " + className + "componentName=" + componentName);

                if ((packageName != null && packageName.toLowerCase().endsWith(
                        "camera"))
                        || (className != null && className.toLowerCase().endsWith(
                                "camera"))
                        || (componentName != null && componentName.toLowerCase()
                                .endsWith("camera"))) {

                    intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                Logger.d(TAG, "intent: " + intent + " successful!");
            } catch (Exception e) {

                Logger.w(TAG, "intent: " + intent + " exception:" + e.toString());
                try {
                    if (componentName != null) {
                        componentName = componentName.toLowerCase();
                        Logger.v("intent", "componentName=" + componentName);
                        if (componentName.contains("player")) {

                            intent.setClassName("com.baidu.musicplayer",
                                    "com.baidu.musicplayer.activity.MainFragmentActivity");

                        } else if (componentName.contains("deskclock")) {

                            intent.setClassName("com.baidu.baiduclock",
                                    "com.baidu.baiduclock.BaiduClock");

                        } else if (componentName.contains("fileexplorer")) {

                            intent.setClassName("com.baidu.resmanager.filemanager",
                                    "com.baidu.resmanager.filemanager.ui.ProfileActivity");

                        } else if (componentName.contains("android.gm")) {

                            intent.setClassName("com.android.email",
                                    "com.android.email.activity.Welcome");

                        } else if (componentName.contains("gallery")) {

                            intent.setClassName("com.baidu.gallery3d",
                                    "com.baidu.gallery3d.app.Gallery");

                        } else if (componentName.contains("soundrecorder")) {

                            intent.setClassName("com.baidu.soundrecorder",
                                    "com.baidu.soundrecorder.SoundRecorder");

                        } else if (componentName.contains("notes")) {

                            intent.setClassName("com.baidu.notepad",
                                    "com.baidu.notepad.NotesList");

                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                } catch (Exception ee) {
                    e.printStackTrace();
                }
            }
        }
        mCallback.unLock();
    }

    public void setScreenOff(Boolean isScreenOff) {
        mScreenOff = isScreenOff;
    }

    public boolean getScreenState() {
        return mScreenOff;
    }

    public void refrashBattery(int category, int level, int pluged) {

        int state = 0;

        switch (category) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                category = Constants.CHARGING;
                state = 1;
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                if (level > Constants.LOW_BATTERY_THRESHOLD) {
                    category = Constants.BATTERY_NORMAL;
                    state = 0;
                } else {
                    category = Constants.BATTERY_LOW;
                    state = 2;
                }
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                if (pluged > 0 && level < Constants.FULL_BATTERY_LEVEL) {
                    category = Constants.CHARGING;
                    state = 1;
                } else {
                    category = Constants.BATTERY_FULL;
                    state = 3;
                }
                break;

            default:
                category = Constants.BATTERY_NORMAL;
                state = 0;
                break;
        }

        mCallback.setCategory(category);

        mExpressionManager.setVariableValue(
                VariableConstants.VAI_BATTERY_LEVEL, level);
        mExpressionManager.setVariableValue(
                VariableConstants.VAI_BATTERY_STATE, state);

        // Logger.d(TAG, "category Change: category=" + category + ", level="
        // + level + ", state = " + state + ", pluged: " + pluged);
    }

    public void refreshTime() {
        Time time = new Time();
        time.setToNow();
        mCallback.dispatchTimeTick(time);
        // Logger.d(TAG, "Tick: time = " + time);

        int hour = time.hour;
        mExpressionManager.setVariableValue(VariableConstants.VAI_TIME_HOUR12,
                hour % 12);
        mExpressionManager.setVariableValue(VariableConstants.VAI_TIME_HOUR24, hour);
        int minute = time.minute;
        mExpressionManager.setVariableValue(VariableConstants.VAI_TIME_MINUTE, minute);
        int year = time.year;
        mExpressionManager.setVariableValue(VariableConstants.VAI_DATE_YEAR, year);
        int month = time.month;
        mExpressionManager.setVariableValue(VariableConstants.VAI_DATE_MONTH, month);
        int day = time.monthDay;
        mExpressionManager.setVariableValue(VariableConstants.VAI_DATE_DAY, day);
        int dayofweek = time.weekDay + 1;
        mExpressionManager
                .setVariableValue(VariableConstants.VAI_DATE_WEEK, dayofweek);

    }

    @Override
    public void handleMessage(Message msg) {
        //Logger.i(TAG, "handleMessage" + msg);
        switch (msg.what) {
            case Constants.MSG_BATTERY:
                BatteryStatus bs = (BatteryStatus) msg.obj;
                refrashBattery(bs.status, bs.level, bs.plugged);

                break;
            case Constants.MSG_TIME_TICK:

                refreshTime();
                break;
            case Constants.MSG_TIME_REFRESH:

                // if(mScreenOff) break;

                long currentTime = System.currentTimeMillis();
                mExpressionManager.setVariableValue(VariableConstants.VAI_TIME,
                        (int) (currentTime % Integer.MAX_VALUE));

                Time time2 = new Time();
                time2.setToNow();

                int second = time2.second;
                if (mLastSecond != second) {
                    mLastSecond = second;
                    mExpressionManager.setVariableValue(VariableConstants.VAI_TIME_SECONE, second);
                }

                break;
            case Constants.MSG_UPDATE_WALLPAPER:
                Logger.i(TAG, "handleMessage " + msg);
                mCallback.updateWallpaper();
                break;
            case Constants.MSG_UNLOCK_INTENT:
                Logger.i(TAG, "handleMessage " + msg);
                Intent intent = (Intent) msg.obj;
                startIntent(intent);
                break;
            case Constants.MSG_UPDATE_CALLLOG:
                Integer callLogCount = (Integer) msg.obj;
                mExpressionManager.setVariableValue(VariableConstants.VAI_CALL_MISSED_COUNT,
                        callLogCount);
                Logger.d("Calllog", "New Missed Count=" + callLogCount);
                break;
            case Constants.MSG_UPDATE_SMSLOG:
                Integer smsCount = (Integer) msg.obj;
                mExpressionManager.setVariableValue(VariableConstants.VAI_SMS_UNREAD_COUNT,
                        smsCount);
                Logger.d("Calllog", "Unread SMS Count=" + smsCount);
                break;
            case Constants.MSG_UPDATE_ALARM:
                Logger.i(TAG, "handleMessage " + msg);
                String nextAlarm = null;
                nextAlarm = Settings.System.getString(mContext.getContentResolver(),
                        Settings.System.NEXT_ALARM_FORMATTED);

                mExpressionManager.setVariableValue(VariableConstants.ALARM_COUNT,
                        ((nextAlarm.length() == 0) ? 0 : 1));
                mExpressionManager.setVariableValue(VariableConstants.ALARM_FORMATE, nextAlarm);
                break;
            case Constants.MSG_CONFIGURATION_CHANGED:

                break;
            default:
                break;
        }
    }

    public Boolean getmPhoneing() {
        return mPhoneing;
    }

    public void setmPhoneing(Boolean phoneing) {
        mPhoneing = phoneing;
    }
}
