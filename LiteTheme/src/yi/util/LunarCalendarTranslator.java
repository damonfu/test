package yi.util;

import java.util.*;

public class LunarCalendarTranslator implements Cloneable {
private static String TAG = "LunarCalendarTranslator";
	
    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    
    private static final int FIELD_COUNT = 3;
    
    protected int[] mFields = new int[FIELD_COUNT];
    
    protected boolean mIsLeap = false;
    
    //High 12 bits represent days of 12 months, 1 means 30 days, 0 means 29 days.
    private final static long[] sLunarInfo = new long[] {
        0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0,
        0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970,
        0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
        0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0,
        0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0,
        0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6,
        0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0,
        0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6, 0x095b0,
        0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5,
        0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
        0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950,
        0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954,
        0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0,
        0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0,
        0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0, 0x14b63};
    
    //number of days from New Year's day to first day of the lunar year
    private static final char[] sSolarLunarOffsetTable = { 
        49, 38, 28, 46, 34, 24, 43, 32, 21, 40, // 1910
        29, 48, 36, 25, 44, 33, 22, 41, 31, 50, // 1920
        38, 27, 46, 35, 23, 43, 32, 22, 40, 29, // 1930
        47, 36, 25, 44, 34, 23, 41, 30, 49, 38, // 1940
        26, 45, 35, 24, 43, 32, 21, 40, 28, 47, // 1950
        36, 26, 44, 33, 23, 42, 30, 48, 38, 27, // 1960
        45, 35, 24, 43, 32, 20, 39, 29, 47, 36, // 1970
        26, 45, 33, 22, 41, 30, 48, 37, 27, 46, // 1980
        35, 24, 43, 32, 50, 39, 28, 47, 36, 26, // 1990
        45, 34, 22, 40, 30, 49, 37, 27, 46, 35, // 2000
        23, 42, 31, 21, 39, 28, 48, 37, 25, 44, // 2010
        33, 22, 40, 30, 49, 38, 27, 46, 35, 24, // 2020
        42, 31, 21, 40, 28, 47, 36, 25, 43, 33, // 2030
        22, 41, 30, 49, 38, 27, 45, 34, 23, 42, // 2040
        31, 21, 40, 29, 47, 36, 25, 44, 32, 22, // 2050
    };
    
   /* private static final long[] sTermInfo = new long[] { 
        0, 21208, 42467, 63836, 85337,107014, 128867, 150921, 173149, 
        195551, 218072, 240693, 263343,285989, 308563, 331033, 353350, 
        375494, 397447, 419210, 440795,462224, 483532, 504758 };*/

    final static float[] sTermC20 = new float[] {
        6.11f, 20.84f, 4.6295f, 19.4599f, 6.3826f, 21.4155f,
        5.59f, 20.888f, 6.318f, 21.86f, 6.5f, 22.20f,
        7.928f, 23.65f, 8.35f, 23.95f, 8.44f, 23.822f,
        9.098f, 24.218f, 8.218f, 23.08f, 7.9f, 22.6f
    };
    
    final static float[] sTermC21 = new float[] {
        5.4055f, 20.12f, 3.87f, 18.73f, 5.63f, 20.646f,
        4.81f, 20.1f, 5.52f, 21.04f, 5.678f, 21.37f,
        7.108f, 22.83f, 7.5f, 23.13f, 7.646f, 23.042f,
        8.318f, 23.438f, 7.438f, 22.36f, 7.18f, 21.94f 
    };
    
    public LunarCalendarTranslator() {
        Calendar calendar = Calendar.getInstance();
        clear();
        solarCalendarToLundar(calendar);
    }
    
    public LunarCalendarTranslator(Calendar calendar) {
        clear();
        solarCalendarToLundar(calendar);
    }
    
    
    private final void clear() {
        int[] tempFields = {1970, 1, 1};
        mFields = tempFields;
        mIsLeap = false;
    }
    
    public Object clone() {
        try {
            LunarCalendarTranslator lunarCalendar = (LunarCalendarTranslator) super.clone();
            lunarCalendar.mFields = (int[]) mFields.clone();
            lunarCalendar.mIsLeap = mIsLeap;
            return lunarCalendar;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    protected void complete() {
        while (true) {
        	/*Log.d(TAG, String.format("complete Year: %d, Month: %d, Day: %d", 
        			mFields[YEAR], mFields[MONTH], mFields[DAY]));*/
        	
            if (mFields[MONTH] > 11) {
                mFields[YEAR] += mFields[MONTH]/12;
                mFields[MONTH] = mFields[MONTH]%12;
            }
            
            try {
                if (mFields[YEAR] < 1901 || mFields[YEAR] > 2050) {
                    throw new OutOfLunarRangeException();
                }
            }
            catch(OutOfLunarRangeException e) {
                System.out.println(e);
            }
            
            int daysOfMonth = getLunarMonthDays(mFields[YEAR], mFields[MONTH]);
            if (mIsLeap && mFields[MONTH] == getLeapMonth(mFields[YEAR])) {
                daysOfMonth = getLeapDays(mFields[YEAR]);
            }
            if (mFields[DAY]- daysOfMonth <= 0) {
                break;
            }
            mFields[DAY] -= daysOfMonth;
            mFields[MONTH]++;
        }        
    }

    private void solarCalendarToLundar(Calendar calendar) {
        int iYear = calendar.get(Calendar.YEAR);
        int iMonth = calendar.get(Calendar.MONTH);
        int iDay = calendar.get(Calendar.DAY_OF_MONTH);
       
        int iLDay, iLMonth, iLYear;
        int iOffsetDays = getSolarNewYearOffsetDays(iYear, iMonth, iDay);
        int iLeapMonth = getLeapMonth(iYear);

        if (iOffsetDays < sSolarLunarOffsetTable[iYear - 1901]) {
            iLYear = iYear - 1;
            try {
                if (iLYear < 1901 || iLYear > 2050) {
                    throw new OutOfLunarRangeException();
                }
            }
            catch(OutOfLunarRangeException e) {
                System.out.println(e);
            }
            iOffsetDays = sSolarLunarOffsetTable[iYear - 1901] - iOffsetDays;
            iLDay = iOffsetDays;

            for (iLMonth = 11; iOffsetDays > getLunarMonthDays(iLYear, iLMonth); iLMonth--) {
                iLDay = iOffsetDays;
                iOffsetDays -= getLunarMonthDays(iLYear, iLMonth);
            }
            if (0 == iLDay)
                iLDay = 1;
            else
                iLDay = getLunarMonthDays(iLYear, iLMonth) - iOffsetDays + 1;
        } else {
            iLYear = iYear;
            iOffsetDays -= sSolarLunarOffsetTable[iYear - 1901];
            iLDay = iOffsetDays + 1;

            for (iLMonth = 0; iOffsetDays >= 0; iLMonth++) {
                iLDay = iOffsetDays + 1;
                iOffsetDays -= getLunarMonthDays(iLYear, iLMonth);
                if ((iLeapMonth == iLMonth) && (iOffsetDays >= 0)) {
                    iLDay = iOffsetDays + 1;
                    iOffsetDays -= getLeapDays(iLYear);
                    if (iOffsetDays <= -1) {
                        setLeap(true);
                        iLMonth += 1;
                        break;
                    }
                }
            }
            iLMonth--;
        }
        mFields[YEAR] = iLYear;
        mFields[MONTH] = iLMonth;
        mFields[DAY] = iLDay;
    }

    final public static int getLeapMonth(int y) {
        return (int) ((sLunarInfo[y - 1901] & 0xf) - 1);
    }

    final private static int getLeapDays(int y) {
        if (getLeapMonth(y) != 0) {
            if ((sLunarInfo[y - 1901] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }
    
    final private static int getSolarNewYearOffsetDays(int iYear, int iMonth, int iDay) {
        int iOffsetDays = 0;

        for (int i = 0; i < iMonth; i++) {
            iOffsetDays += getSolarYearMonthDays(iYear, i);
        }
        iOffsetDays += iDay - 1;

        return iOffsetDays;
    }

    final public static int getLunarMonthDays(int y, int m) {
    	//Log.d(TAG, String.format("getLunarMonthDays Year: %d, Month: %d", y, m));
               
        if ((sLunarInfo[y - 1901] & (0x10000 >> (m + 1))) == 0)
            return 29;
        else
            return 30;
    }

    public Calendar toSolarCalendar() {
        return translateLunarToSolar(get(YEAR), get(MONTH), get(DAY));   
    }
    
    //translate from lunar calendar to solar calendar
    public static Calendar translateLunarToSolar(int year, int month, int day) {
        int iSYear, iSMonth, iSDay;
        int iOffsetDays = getLunarNewYearOffsetDays(year, month, day)
                + sSolarLunarOffsetTable[year - 1901];
        
        int iYearDays = isSolarLeapYear(year) ? 366 : 365;

        if (iOffsetDays >= iYearDays) {
            iSYear = year + 1;
            iOffsetDays -= iYearDays;
        } else {
            iSYear = year;
        }
        iSDay = iOffsetDays + 1;
        for (iSMonth = 0; iOffsetDays >= 0; iSMonth++) {
            iSDay = iOffsetDays + 1;
            iOffsetDays -= getSolarYearMonthDays(iSYear, iSMonth);
        }
        iSMonth--;
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(iSYear, iSMonth, iSDay);
        
        return calendar;
    }
    
    //Calculate days from the lunar day to lunar New Year. 
    private static int getLunarNewYearOffsetDays(int iYear, int iMonth, int iDay) {
        int iOffsetDays = 0;
        int iLeapMonth = getLeapMonth(iYear);
        //System.out.println("iLeapMonth: " + iYear + " " + iLeapMonth);

        /*if ((iLeapMonth > 0) && (iLeapMonth == iMonth - 12)) {
            iMonth = iLeapMonth;
            iOffsetDays += getLeapDays(iYear);
        }*/

        for (int i = 0; i < iMonth; i++) {
            iOffsetDays += getLunarMonthDays(iYear, i);
            if (i == iLeapMonth)
                iOffsetDays += getLeapDays(iYear);
        }
        iOffsetDays += iDay - 1;

        return iOffsetDays;
    }
    
    //Judge whether the year is a leap year
    private static boolean isSolarLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0) || year % 400 == 0);
    }
    
    //Return days of solar calendar in month/year
    private static int getSolarYearMonthDays(int year, int month) {
        if ((month == 0) || (month == 2) || (month == 4) || (month == 6)
                || (month == 7) || (month == 9) || (month == 11))
            return 31;
        else if ((month == 3) || (month == 5) || (month == 8)
                || (month == 10))
            return 30;
        else if (month == 1) {
            if (isSolarLeapYear(year))
                return 29;
            else
                return 28;
        } else
            return 0;
    }
    
    
    public void set(int year, int month, int day) {
        set(YEAR, year);
        set(MONTH, month);
        set(DAY, day);
    }
    
    public void set(int field, int value) {
        switch(field) {
            case YEAR:
                mFields[YEAR] = value;
                mIsLeap = false;
                break;
            case MONTH:
                mFields[MONTH] = value;
                mIsLeap = false;
                break;
            case DAY:
                mFields[DAY] = value;
                mIsLeap = false;
                break;
        }
    }
    
    public int get(int field) {
        complete();
        /*try {
            if (mFields[YEAR] < 1901 || mFields[YEAR] > 2050) {
                throw new OutOfLunarRangeException();
            }
        }
        catch(OutOfLunarRangeException e) {
            System.out.println(e);
        }*/
        return mFields[field];
    }
    
    public boolean getLeap() {
        return mIsLeap;      
    }
    
    public boolean setLeap(boolean leap) {
        mIsLeap = leap;
        return true;   
    } 
    
    public void setTime(Date date) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }
    
    public Date getTime() {
    	Calendar calendar = Calendar.getInstance();
    	calendar.clear();
    	calendar.set(get(YEAR), get(MONTH), get(DAY));
        return calendar.getTime();
    }  
    
    public class OutOfLunarRangeException extends IllegalStateException {
		private static final long serialVersionUID = 1L;

		public OutOfLunarRangeException() {
            super("The year is out of range (1901-2050).");
        }
    }
    
    public static Calendar getDateOfTerm(int y, int n) {
        Calendar cal = Calendar.getInstance();
        /*cal.set(1900, 0, 6, 2, 5, 0);
        long temp = cal.getTime().getTime();
        cal.setTime(new Date(
           (long) ((31556925974.7 * (y - 1900) + sTermInfo[n] * 60000L) + temp)));*/
        int day = getDayOfTerm(y, n);
        cal.set(y, n/2, day);

        return cal;
    }
    
    // Return day of solarTerm in month/year
    public static int getDayOfTerm(int year, int n) {
        //Calendar cal = getDateOfTerm(y, n);
        //return cal.get(Calendar.DAY_OF_MONTH);

        float c = 0;
        int y = year % 100;
        if (year >= 1900 && year < 2000) {
            c = sTermC20[n];
        } else if (year >= 2000 && year < 2100) {
            c = sTermC21[n];
        }
        int ydc = (int) (y * 0.2422 + c);
        if (n < 4 && isSolarLeapYear(y)) {
            y--;
        }
        int L = y / 4;
        int day = ydc - L;

        do {
            if ((year == 2026 && n == 3) || (year == 1918 && n == 9)) {
                day--;
                break;
            } else if ((year == 2084 && n == 5) || (year == 1911 && n == 8)
                    || (year == 2008 && n == 9) || (year == 1902 && n == 10)
                    || (year == 1928 && n == 11) || (year == 1925 && n == 12)
                    || (year == 2016 && n == 12) || (year == 1922 && n == 13)
                    || (year == 2002 && n == 14) || (year == 1927 && n == 16)
                    || (year == 1942 && n == 17) || (year == 2089 && n == 19)
                    || (year == 2089 && n == 20) || (year == 1978 && n == 21)
                    || (year == 1954 && n == 22)) {
                day++;
                break;
            }
        } while (false);

        return day;
    }
}
