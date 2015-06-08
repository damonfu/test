package com.baidu.themeanimation.util;

import android.text.TextUtils;

public class Utils {

    public static final int STRING_NONE   = -1;
    public static final int STRING_INT    = 1;
    public static final int STRING_DOUBLE = 2;
    
    public static int getStringType(String str) {
        if (str == null || str.length() == 0) {
            return STRING_NONE;
        }
        int chr = str.charAt(0);
        int result = STRING_INT;

        if (!((chr > 47 && chr < 58) || chr == '-' || chr == '+')) {
            return STRING_NONE;
        }

        for (int i = 1; i < str.length(); ++i) {
            chr = str.charAt(i);
            if (chr > 47 && chr < 58)
                continue;
            if (chr == '.') {
                result = STRING_DOUBLE;
            } else {
                return STRING_NONE;
            }
        }
        return result;
    }

    public static boolean isInt(String str) {
        return getStringType(str) == STRING_INT;
    }

    public static boolean isDouble(String str) {
        return getStringType(str) == STRING_DOUBLE;
    }

    public static String removeQuote(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int length = str.length();
        if (length > 2 && str.startsWith("'") && str.endsWith("'")) {
            return str.substring(1, length - 1);
        }
        return str;
    }

    public static boolean getBoolean(String value) {
        return !(value == null || "".equals(value) ||
                "false".equalsIgnoreCase(value) ||
                "0".equals(value) || "0.0".equals(value));
    }

    public static String getMethodName(String name) {
        if (TextUtils.isEmpty(name)) {
            return "setError";
        }
        return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
