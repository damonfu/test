/*
 * Copyright (C) 2010 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yi.util;

import java.util.Hashtable;

import android.util.Log;

/**
 * Utility log tool.
 * 
 * @author GuoZhen
 *
 */
public class MyLogger {
    
    private static boolean sIsLoggerEnable = true;   
    private static Hashtable<String, MyLogger> sLoggerTable;
    
    static {
        sLoggerTable = new Hashtable<String, MyLogger>();
    }
    
    private String LOG_TAG;    
    
    public static MyLogger getLogger(String tag) {
        MyLogger classLogger = (MyLogger)sLoggerTable.get(tag);
        if(classLogger == null){
            classLogger = new MyLogger(tag);
            sLoggerTable.put(tag, classLogger);            
        }        
        return classLogger;
    }
    
    public static void enableLog(){
    	sIsLoggerEnable = true;
    }
    public static void disableLog(){
    	sIsLoggerEnable = false;
    }
    private MyLogger(String name) {
    	LOG_TAG = name;
    }
    
    public void v(String log) {
        if(sIsLoggerEnable){
            Log.v(LOG_TAG, log);
        }
    }
    
    public void d(String log) {
        if(sIsLoggerEnable){
            Log.d(LOG_TAG, log);
        }
    }
    
    public void i(String log) {
        if(sIsLoggerEnable){
            Log.i(LOG_TAG, log);
        }
    }
    
    public void i(String log, Throwable tr) {
        if(sIsLoggerEnable){
            Log.i(LOG_TAG, log + "\n" + Log.getStackTraceString(tr));
        }
    }
    
    public void w(String log) {
        if(sIsLoggerEnable){
            Log.w(LOG_TAG, log);
        }
    }
    
    public void w(String log, Throwable tr) {
        if(sIsLoggerEnable){
            Log.w(LOG_TAG,log + "\n" + Log.getStackTraceString(tr));
        }
    }
    
    public void e(String log) {
        if(sIsLoggerEnable){
            Log.e(LOG_TAG,log);
        }
    }
    
    public void e(String log, Throwable tr) {
        if(sIsLoggerEnable){
            Log.e(LOG_TAG,log + "\n" + Log.getStackTraceString(tr));
        }
    }
}
