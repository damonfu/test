package yi.support.v1.utils;

import android.util.Log;

import java.util.Stack;


/**
 * encapsulation Log
 * @author rendayun
 *
 */
public class Logger {

    private static boolean mLoggable = false;
    private static String mCfgTag = "";
    
    public static void enable(String cfgTAG) {
        mLoggable = true;
        mCfgTag = cfgTAG;
    }

    public static void d(String tag, String msg) {
        if (mLoggable && Log.isLoggable(mCfgTag, Log.DEBUG)) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mLoggable && Log.isLoggable(mCfgTag, Log.INFO)) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (mLoggable && Log.isLoggable(mCfgTag, Log.VERBOSE)) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mLoggable && Log.isLoggable(mCfgTag, Log.WARN)) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mLoggable && Log.isLoggable(mCfgTag, Log.ERROR)) {
            Log.e(tag, msg);
        }
    }

    public static void printStackTrace(String tag) {
        if (mLoggable) {
            StackTraceElement[] stes = new Throwable().getStackTrace();
            for (int i=1; i<stes.length; i++) {
                Log.i(tag, stes[i].toString());
            }
        }
    }

    public static class Performance {

        private static String TAG = "PERFORMANCE";
        private static Stack<Long> mTimestampStack = new Stack<Long>();
        
        private static String LEVEL[] = {
            "",
            "", 
            ">", 
            ">>", 
            ">>>", 
            ">>>>", 
            ">>>>>", 
            ">>>>>>", 
            ">>>>>>>", 
            ">>>>>>>>", 
            ">>>>>>>>>", 
            ">>>>>>>>>>", 
            ">>>>>>>>>>>", 
            ">>>>>>>>>>>>", 
            ">>>>>>>>>>>>>", 
            ">>>>>>>>>>>>>>", 
            ">>>>>>>>>>>>>>>", 
        };

        public static void begin() {
            begin(TAG, getCallerInfo());
        }
        
        public static void elapse() {
            elapse(TAG, getCallerInfo());
        }
        
        public static void end() {
            end(TAG, getCallerInfo());
        }

        public static void begin(String tag, String msg) {
            mTimestampStack.push(System.currentTimeMillis());
            print(tag, "[BEGIN]" + msg, 0);
        }

        public static void elapse(String tag, String msg) {
            long current = -1;
            if (!mTimestampStack.isEmpty()) {
                current = System.currentTimeMillis() - mTimestampStack.peek();
            }

            print(tag, "[ELAPSE]" + msg, current);
        }

        public static void end(String tag, String msg) {
            long current = -1;
            if (!mTimestampStack.isEmpty()) {
                current = System.currentTimeMillis() - mTimestampStack.peek();
            }

            print(tag, "[END]" + msg, current);
            
            if (!mTimestampStack.isEmpty()) {
                mTimestampStack.pop();
            }
        }
        
        private static String getCallerInfo() {
            StackTraceElement ste = new Throwable().getStackTrace()[2];
            return (ste == null) ? "" : ste.toString();
        }
        
        private static void print(String tag, String msg, long time) {
            String level = LEVEL[Math.min(LEVEL.length, mTimestampStack.size())];
            i(tag, level + msg + " : " + String.valueOf(time));
        }

    }

}
