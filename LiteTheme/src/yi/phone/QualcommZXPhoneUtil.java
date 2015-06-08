package yi.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class QualcommZXPhoneUtil implements IPhoneUtil {

    private Context mContext;

    public QualcommZXPhoneUtil(Context context) {
        mContext = context;
    }

    public boolean isCompatable() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> cls = tm.getClass();

        Method method_getDeviceId = null;
        Class<?> MSimSmsManager_class = null;
        boolean isMultiSimEnable = false;

        try {
            Method method = cls.getMethod("isMultiSimEnabled");
            isMultiSimEnable = (Boolean) method.invoke(tm);
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        try {
            method_getDeviceId = tm.getClass().getDeclaredMethod("getDeviceId", new Class[] { int.class });
            MSimSmsManager_class = Class.forName("android.telephony.MSimSmsManager");
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (ClassNotFoundException e) {
        }

        if (isMultiSimEnable && null != method_getDeviceId && null == MSimSmsManager_class) {
            return true;
        }
        return false;
    }

    public String getIMEI(int slot) {
        String imei = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> yidmc = tm.getClass();
            Method method = yidmc.getMethod("getDeviceId", new Class[] { int.class });
            imei = (String) method.invoke(tm, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return imei;
    }

    public boolean isSlotEnabled(int slot) {
        boolean result = false;
        if (0 != slot && 1 != slot) {
            return false;
        }

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Method method;
        try {
            method = telephonyManager.getClass().getDeclaredMethod("getSimState", new Class[] { int.class });
            int simState = (Integer) method.invoke(telephonyManager, new Object[] { slot });
            if (simState == TelephonyManager.SIM_STATE_READY) {
                result = true;
            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return result;
    }

    public String getIMSI(int slot) {
        String imsi = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> yidmc = tm.getClass();
            Method method = yidmc.getMethod("getSubscriberId", new Class[] { int.class });
            imsi = (String) method.invoke(tm, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return imsi;
    }

    public String getOperatior(int slot) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getDeclaredMethod("getSimOperator", new Class[] { int.class });
            return (String) method.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return null;
    }

    public String getPhoneNumber(int slot) {
        // TODO Auto-generated method stub
        return "";
    }

    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int slot) {
        boolean result = true;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
        return result;
    }

    public String getNetOperaterName(int slot) {
        String result = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> yidmc = tm.getClass();
            Method method = yidmc.getMethod("getNetworkOperatorName", new Class[] { int.class });
            result = (String) method.invoke(tm, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return result;
    }

}
