/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Configuration;

import android.provider.Settings;
import java.util.Calendar;
import android.util.Log;

import com.baidu.lite.R;

/**
 * A native SimpleDateFormat, which import and use native format resource.
 */
public class YiSimpleDateFormat extends SimpleDateFormat {
    public static final String[] TIME_CHINA =
    //"0,   1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12,  13,  14,  15,  16,  17,  18,  19,  20,  21,  22,  23" ---- 24-hour
    {" 0"," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9","10","11","12"," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9","10","11"};
    public static final char[] TIME_SECTION_FLAG = 
    /*
     * 0-------->morning            //00:00~10:59
     * 1-------->noon               //11:00~12:59
     * 2-------->afternoon,         //13:00~23:59(01:00~11:59)
     */
    { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   1,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2 };

    private String[] mTimeSectionMarker;
    private boolean mUseNativeMarker = false;

    private Locale mLocale;
    
    /**
     * Constructs a new {@code YiSimpleDateFormat} using the 
     * {@code Resources} for the specified native resources and the 
     * default non-localized pattern and default Locale.
     *
     * @param res
     *            the native resources.
     * @throws NullPointerException
     *            if the pattern is {@code null}.
     * @throws IllegalArgumentException
     *            if the pattern is invalid.
     */
    public YiSimpleDateFormat(Resources res) {
        super();
        init(res);
    }
    
    /**
     * Constructs a new {@code YiSimpleDateFormat} using the 
     * {@code Resources} for the specified native resources and the specified
     * non-localized pattern and default Locale.
     *
     * @param res
     *            the native resources.
     * @param template
     *            the pattern.
     * @throws NullPointerException
     *            if the pattern is {@code null}.
     * @throws IllegalArgumentException
     *            if the pattern is invalid.
     */
    public YiSimpleDateFormat(Resources res, String template) {
        super(template);
        init(res);
    }

    /**
     * Constructs a new {@code YiSimpleDateFormat} using the 
     * {@code Resources} for the specified native resources and the 
     * specified non-localized pattern and {@code locale} for the specified 
     * locale.
     *
     * @param res
     *            the native resources.
     * @param template
     *            the pattern.
     * @param locale
     *            the locale.
     * @throws NullPointerException
     *            if the pattern is {@code null}.
     * @throws IllegalArgumentException
     *            if the pattern is invalid.
     */
    public YiSimpleDateFormat(Resources res, String template, Locale locale) {
        super(template, locale);
        mLocale = locale;
        if(res != null) {
            importNativeResStr(res);
        }
    }
    
    /**
     * initialize
     */
    private void init(Resources res) {
    	if(res != null) {
            //use Resources's Locale of the inputed parameter as default Locale
            mLocale = res.getConfiguration().locale;
            setDateFormatSymbols(new DateFormatSymbols(mLocale));

            importNativeResStr(res);
        }
        else {
            mLocale = Locale.getDefault();
        }
    }

    /**
     * get time section marker string from native resource
     */
    private void importNativeResStr(Resources res) {
        Configuration cfg = res.getConfiguration();
        Locale locale = cfg.locale;
        //Log.d("lihouchen", "importNativeResStr(), locale="+locale.toString());
        //if(locale.equals(Locale.CHINA)) {
        if(locale.getLanguage().equals("zh")) {
            mTimeSectionMarker = res.getStringArray(R.array.chineseTimeSection);
            if(mTimeSectionMarker.length > 0) {     //mTimeSectionMarker.length == 3
                mUseNativeMarker = true;
            }
        }
    }
    
    /**
     * Formats the specified date as a string with a specific template.
     * It's a native version of java.text.SimpleDateFormat.format(...)
     *
     * @param date
     *            the date to format.
     * @param newTemplate
     *            the template for formatting, override the template has 
     *            been registered in YiSimpleDateFormat's constructor.
     * @return the formated string.
     * @throws IllegalArgumentException
     *             if there are invalid characters in the pattern.
     */
    public String nativeFormat(Date date, String newTemplate) {
    	return format(date, newTemplate);
    }
    
    /**
     * Formats the specified date as a string using the template has been 
     * registered in YiSimpleDateFormat's constructor.
     * It's a native version of java.text.SimpleDateFormat.format(...)
     *
     * @param date
     *            the date to format.
     * @return the formated string.
     * @throws IllegalArgumentException
     *             if there are invalid characters in the pattern.
     */
    public String nativeFormat(Date date) {
    	return format(date, null);
    }

    /**
     * Formats the specified date as a string with a specific template.
     * It's a native version of java.text.SimpleDateFormat.format(...)
     *
     * @param date
     *            the date to format.
     * @param newTemplate
     *            the template for formatting, override the template has 
     *            been registered in YiSimpleDateFormat's constructor.
     * @return the formated string.
     * @throws IllegalArgumentException
     *             if there are invalid characters in the pattern.
     */
    private String format(Date date, String newTemplate) {
        StringBuffer resultStrBuffer;
        int hour = date.getHours();
        int timeSectionFlag = TIME_SECTION_FLAG[hour];

        String pattern = super.toPattern();
        String template = pattern;
        if(newTemplate != null) {
            if(newTemplate.length() > 0) {
            	template = newTemplate;
                super.applyPattern(template.toString());  //use user defined temporary pattern
            }
        }
        
        if(mUseNativeMarker){
            FieldPosition filePos_hour0 = new FieldPosition(DateFormat.Field.HOUR0);
            FieldPosition filePos_hour1 = new FieldPosition(DateFormat.Field.HOUR1);
            FieldPosition filePos_ampm = new FieldPosition(DateFormat.Field.AM_PM);

            StringBuffer formatStrBuf = new StringBuffer();

            int count_K = 0;
            int count_h = 0;
            int index_K = template.indexOf('K');
            int index_h = template.indexOf('h');
            if(index_K > -1) {    //hour at AM/PM (0~11)
            	count_K ++;
                formatStrBuf = super.format(date, formatStrBuf, filePos_hour0);
                while(template.charAt(++ index_K) == 'K') {
                	count_K ++;
                }
            }
            if(index_h > -1) {    //hour at AM/PM (1~12)
            	count_h ++;
                formatStrBuf = super.format(date, new StringBuffer(), filePos_hour1);
                while(template.charAt(++ index_h) == 'h') {
                	count_h ++;
                }
            }
            if(template.indexOf('a') > -1) {    //AM/PM marker
                formatStrBuf = super.format(date, new StringBuffer(), filePos_ampm);
            }
            //Log.d("lihouchen", "format(Date date, String tempTemplate), filePos_hour0.getEndIndex()=" + filePos_hour0.getEndIndex() + ",filePos_hour1.getEndIndex()=" + filePos_hour1.getEndIndex() + ",filePos_ampm.getEndIndex()=" + filePos_ampm.getEndIndex());
            if(formatStrBuf.length() > 0) {
            	Calendar c = Calendar.getInstance();
            	c.setTime(date);
            	int hour_num = c.get(java.util.Calendar.HOUR_OF_DAY);
			
                if(filePos_hour0.getEndIndex() > 0) {   //template has 'K', change the hour string
                	String replaceStr = TIME_CHINA[hour];
                	if(count_K == 1) {
                		if(replaceStr.charAt(0) == ' ') {
                			replaceStr = replaceStr.substring(1, 2);
                		}
                	}
                	formatStrBuf = formatStrBuf.replace(filePos_hour0.getBeginIndex(), filePos_hour0.getEndIndex(), replaceStr);
                }
                if(filePos_hour1.getEndIndex() > 0) {   //template has 'h', change the hour string
                	String replaceStr = TIME_CHINA[hour];
                	if(count_h == 1) {
                		if(replaceStr.charAt(0) == ' ') {
                			replaceStr = replaceStr.substring(1, 2);
                		}
                	}
                	int offset = 0;
                	if(count_K == 1 && hour_num == 12 && filePos_hour0.getEndIndex() < filePos_hour1.getEndIndex()) {
                		offset ++;
                	}
                	formatStrBuf = formatStrBuf.replace(filePos_hour1.getBeginIndex() + offset, filePos_hour1.getEndIndex() + offset, replaceStr);
                }
                if(filePos_ampm.getEndIndex() > 0) {    //template has 'a', change the time section marker
                	int offset = 0;
                	if(count_K == 1 && hour_num == 12 && filePos_hour0.getEndIndex() < filePos_ampm.getEndIndex()) {
                		offset ++;
                	}
                	if(count_h == 1 && hour_num == 0 && filePos_hour1.getEndIndex() < filePos_ampm.getEndIndex()) {
                		offset --;
                	}
                	formatStrBuf = formatStrBuf.replace(filePos_ampm.getBeginIndex() + offset, filePos_ampm.getEndIndex() + offset, mTimeSectionMarker[timeSectionFlag]);
                }
                
                resultStrBuffer = formatStrBuf;
            }
            else {
                resultStrBuffer = super.format(date, formatStrBuf, filePos_hour0);
            }

        }
        else {
        	FieldPosition filePos_hour0 = new FieldPosition(DateFormat.Field.HOUR0);
            resultStrBuffer = super.format(date, new StringBuffer(), filePos_hour0);
        }

        if(newTemplate != null) {
            if(newTemplate.length() > 0) {
                super.applyPattern(pattern);        //restore the original pattern
            }
        }
        
        return resultStrBuffer.toString();
    }

    /**
     * Parses a date from the specified native string.
     *
     * @param string
     *            the string to parse using the pattern of this simple date
     *            format.
     * @return the date resulting from the parse, or {@code null} if there is an
     *         error.
     * @throws IllegalArgumentException
     *             if there are invalid characters in the pattern.
     */
    @Override   //Override java.text.DateFormat.parse(String string)
    public Date parse(String string) {
        String strInput = string;
        String strPattern = super.toPattern();
        Date retDate;
        /* TODO:
         * change the native string to the canonical string Compliance with JAVA's ICU
         */
        //DateFormatSymbols formatSymbols = new DateFormatSymbols(mLocale);
        //formatSymbols.getAmPmStrings();

        //First step, handles only one special case: the inputed string contain "noon" of chinese.
        int index_noon = string.indexOf(mTimeSectionMarker[1]);
        int index_K = strPattern.indexOf('K');
        int index_h = strPattern.indexOf('h');
        int index_a = strPattern.indexOf('a');
        if(index_noon >= 0 && ((index_K >= 0 || index_h >= 0) && index_a >= 0)) {
            //generate a de-native string
            StringBuffer stringBuf = new StringBuffer(string);
            stringBuf = stringBuf.delete(index_noon, index_noon + mTimeSectionMarker[1].length());
            strInput = stringBuf.toString();

            //generate a de-native pattern
            StringBuffer patternBuf = new StringBuffer(strPattern);
            if(index_K >= 0) {
                while(patternBuf.charAt(index_K) == 'K') {
                    patternBuf.setCharAt(index_K, 'H');
                    index_K ++;
                }
            }
            if(index_h >= 0) {
                while(patternBuf.charAt(index_h) == 'h') {
                    patternBuf.setCharAt(index_h, 'k');
                    index_h ++;
                }
            }
            if(index_a >= 0) {
                while(patternBuf.charAt(index_a) == 'a') {
                    patternBuf = patternBuf.deleteCharAt(index_a);
                    index_a ++;
                }
            }
            super.applyPattern(patternBuf.toString());  //use the new and de-native pattern
            retDate = super.parse(strInput, new ParsePosition(0));
            super.applyPattern(strPattern);             //restore the original pattern
        }
        else {
            retDate = super.parse(strInput, new ParsePosition(0));
        }

        return retDate;
    }
    
    /**
     * Returns true if user preference is set to 24-hour format.
     * 
     * @param context 
     *            the context to use for the content resolver
     * @return true if 24 hour time format is selected, false otherwise.
     */
    public static boolean is24HourFormat(Context context) {
    	return android.text.format.DateFormat.is24HourFormat(context);
    }
    
    /**
     * Returns a {@link yi.util.YiSimpleDateFormat} object that can format
     * the time according to the current locale and the user's 12-/24-hour
     * clock preference.
     * 
     * @param context 
     *            the application context
     * @return the {@link yi.util.YiSimpleDateFormat} object that properly
     *         formats the time.
     */
    public static final YiSimpleDateFormat getTimeFormat(Context context) {
        boolean b24 = is24HourFormat(context);
        int res;

        if (b24) {
            res = IDHelper.STR_24_HOUR_FORMAT;
        } else {
            res = IDHelper.STR_12_HOUR_FORMAT;
        }

        return new YiSimpleDateFormat(context.getResources(), context.getString(res));
    }
    
    /**
     * Returns a {@link yi.util.YiSimpleDateFormat} object that can format
     * the date in short form (such as 12/31/1999) according
     * to the current locale and the user's date-order preference.
     * 
     * @param context 
     *            the application context
     * @return the {@link yi.util.YiSimpleDateFormat} object that properly
     *         formats the date.
     */
    public static final YiSimpleDateFormat getDateFormat(Context context) {
        String value = Settings.System.getString(context.getContentResolver(),
                Settings.System.DATE_FORMAT);

        return getDateFormatForSetting(context, value);
    }
    
    /**
     * Returns a {@link yi.util.YiSimpleDateFormat} object to format the 
     * date as if the date format setting were set to <code>value</code>,
     * including null to use the locale's default format.
     * 
     * @param context 
     *            the application context
     * @param value 
     *            the date format setting string to interpret for the current
     *            locale
     * @hide
     */
    public static YiSimpleDateFormat getDateFormatForSetting(Context context,
                                                               String value) {
        String format = getDateFormatStringForSetting(context, value);

        return new YiSimpleDateFormat(context.getResources(), format);
    }
    
    private static String getDateFormatStringForSetting(Context context, String value) {
        if (value != null) {
            int month = value.indexOf('M');
            int day = value.indexOf('d');
            int year = value.indexOf('y');

            if (month >= 0 && day >= 0 && year >= 0) {
                String template = context.getString(IDHelper.STR_NUMERIC_DATA_TEMP);
                if (year < month && year < day) {
                    if (month < day) {
                        value = String.format(template, "yyyy", "MM", "dd");
                    } else {
                        value = String.format(template, "yyyy", "dd", "MM");
                    }
                } else if (month < day) {
                    if (day < year) {
                        value = String.format(template, "MM", "dd", "yyyy");
                    } else { // unlikely
                        value = String.format(template, "MM", "yyyy", "dd");
                    }
                } else { // day < month
                    if (month < year) {
                        value = String.format(template, "dd", "MM", "yyyy");
                    } else { // unlikely
                        value = String.format(template, "dd", "yyyy", "MM");
                    }
                }

                return value;
            }
        }

        /*
         * The setting is not set; use the default.
         * We use a resource string here instead of just DateFormat.SHORT
         * so that we get a four-digit year instead a two-digit year.
         */
        value = context.getString(IDHelper.STR_NUMERIC_DATA_FORMAT);
        return value;
    }

    /**
     * return the member variable---String array "mTimeSectionMarker".
     */
    public String[] getNativeTSMarker() {
        if(mUseNativeMarker) {
            return mTimeSectionMarker;
        }
        else {
            return null;
        }
    }
    
    /**
     * return the member variable---mUseNativeMarker,whether use the native marker.
     */
    public boolean getIsUseNative() {
    	return mUseNativeMarker;
    }
}
