
package com.baidu.themeanimation.util;

public class VariableConstants {

    public static String getGlobalVariableTag(int index) {
        return VA[index];
    }

    private static final String[] VA = {
            "second",
            "minute",
            "hour12",
            "hour24",

            "day",
            "month",
            "year",
            "day_of_week",

            "touch_x",
            "touch_y",

            "category",

            "screen_width",
            "screen_height",

            "time_sys",

            "call_missed_count",
            "sms_unread_count",

            "battery_level",

            "time",

            "battery_state"
    };

    public final static int VAI_TIME_SECONE = 0;
    public final static int VAI_TIME_MINUTE = 1;
    public final static int VAI_TIME_HOUR12 = 2;
    public final static int VAI_TIME_HOUR24 = 3;

    public final static int VAI_DATE_DAY = 4;
    public final static int VAI_DATE_MONTH = 5;
    public final static int VAI_DATE_YEAR = 6;
    public final static int VAI_DATE_WEEK = 7;

    public final static int VAI_TOUCH_X = 8;
    public final static int VAI_TOUCH_Y = 9;

    public final static int VAI_CATEGORY = 10;

    public final static int VAI_SCREEN_WIDTH = 11;
    public final static int VAI_SCREEN_HEIGHT = 12;

    public final static int VAI_TIME_SYS = 13;
    public final static int VAI_CALL_MISSED_COUNT = 14;
    public final static int VAI_SMS_UNREAD_COUNT = 15;
    public final static int VAI_BATTERY_LEVEL = 16;

    // time: current system time
    public final static int VAI_TIME = 17;

    public final static int VAI_BATTERY_STATE = 18;

    public final static String MUSIC_CONTROL_VISIBILITY = "music_control.visibility";
    public final static String DATE_POSITION_Y = "date_position_y";
    public final static String TEXT_SIZE_DATE = "text_size_date";
    public final static String SHOW_DATE = "show_date";
    public final static String DATE_FOMATE = "date_format";
    public final static String ALARM_FORMATE = "alarm_info";
    public final static String ALARM_COUNT = "alarm_count";
}
