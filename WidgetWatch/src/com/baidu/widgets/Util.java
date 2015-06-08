package com.baidu.widgets;

import android.app.Activity;

public class Util {

    public static void setAppTitle(Activity activity) {
        String settingValue = System.getProperty(activity.getResources().getString(R.string.change_theme_property));
        if(null == settingValue) {
            activity.setTheme(R.style.Yi_Theme_AppTitle);
        } else {
            if(settingValue.equals("dark")) {
                activity.setTheme(R.style.Yi_Theme_AppTitle);
            } else {
                activity.setTheme(R.style.Yi_Theme_Light_AppTitle);
            }
        }
    }

    public static void setYiTheme(Activity activity) {
        String settingValue = System.getProperty(activity.getResources().getString(R.string.change_theme_property));
        if(null == settingValue) {
            activity.setTheme(R.style.Yi_Theme);
        } else {
            if(settingValue.equals("dark")) {
                activity.setTheme(R.style.Yi_Theme);
            } else {
                activity.setTheme(R.style.Yi_Theme_Light);
            }
        }
    }

    public static void setDeviceDefaultLightTheme(Activity activity) {
//        String settingValue = System.getProperty(activity.getResources().getString(R.string.change_theme_property));
//        if(null == settingValue) {
//            activity.setTheme(R.style.Theme_DeviceDefault_Light); 
//        } else {
//            if(settingValue.equals("dark")) {
//                activity.setTheme(R.style.Theme_DeviceDefault_Light); 
//            } else if(settingValue.equals("light")){
//                activity.setTheme(R.style.Theme_DeviceDefault_Light);
//            }else {
//                activity.setTheme(R.style.Theme_DeviceDefault_Light_DarkActionBar);
//            }
//        }
    }
}
