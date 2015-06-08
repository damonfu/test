
package com.baidu.themeanimation.receiver;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.themeanimation.util.Logger;

public class MusicControlReceiver extends BroadcastReceiver {
    private final static String TAG = "MusicControlReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d(TAG, "onReceive: " + action);
        if (true) {

            Bundle bundle = intent.getExtras();
            for (MusicCallback callback : mMusicMethods) {
                try {
                    callback.mMethod.invoke(callback.mObject, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MusicCallback {
        Object mObject;
        Method mMethod;

        public MusicCallback(Object object, Method method) {
            mObject = object;
            mMethod = method;
        }
    }

    static ArrayList<MusicCallback> mMusicMethods = new ArrayList<MusicCallback>();

    public static void registerMusicPlayCB(Object object, Method method) {
        mMusicMethods.add(new MusicCallback(object, method));
    }

    public static void clearAllMusicPlayCB() {
        mMusicMethods.clear();
    }
}
