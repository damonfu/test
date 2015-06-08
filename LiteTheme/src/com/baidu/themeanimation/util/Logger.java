
package com.baidu.themeanimation.util;

import android.util.Log;

public class Logger {
    /** component tag for debugging */
    private static final String TAG = "BaiduLockScreen";

    private static final String SPACE = ": ";

    public static void v(String tag, String msg) {
        // Log.v(TAG, tag + SPACE + msg);
    }

    public static void d(String tag, String msg) {
        // if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, tag + SPACE + msg);
        // }
    }

    public static void i(String tag, String msg) {
        Log.i(TAG, tag + SPACE + msg);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG, tag + SPACE + msg);
    }

    public static void w(String tag, String msg, Exception e) {
        Log.w(TAG, tag + SPACE + msg, e);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, tag + SPACE + msg);
    }
}
