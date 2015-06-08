package yi.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

class MTKTCLPhoneUtil implements IPhoneUtil {
    private Context mContext;

    public MTKTCLPhoneUtil(Context context) {
        mContext = context;
    }

    public String getIMEI(int slot) {

        String imei = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method method = tm.getClass().getMethod("getDeviceIdGemini", new Class[] { int.class });
            imei = (String) method.invoke(tm, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return imei;
    }

    public boolean isSlotEnabled(int smsNumber) {

        boolean result = false;
        if (0 != smsNumber && 1 != smsNumber) {
            return false;
        }

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Method method;
        try {
            method = telephonyManager.getClass().getDeclaredMethod("getSimStateGemini", new Class[] { int.class });
            int simState = (Integer) method.invoke(telephonyManager, new Object[] { smsNumber });
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

    @Override
    public String getIMSI(int slot) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getDeclaredMethod("getSubscriberIdGemini",
                    new Class[] { int.class });
            return (String) method.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return null;
    }

    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int smscardNumber) {
        boolean result = true;

        SmsManager sms = SmsManager.getDefault();
        try {
            Method method = sms.getClass().getDeclaredMethod(
                    "sendTextMessageEx",
                    new Class[] { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class,
                            int.class });
            method.invoke(sms, new Object[] { destinationAddress, scAddress, text, sentIntent, deliveryIntent,
                    smscardNumber });
        } catch (NoSuchMethodException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        }

        return result;
    }

    @Override
    public boolean isCompatable() {

        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        SmsManager sms = SmsManager.getDefault();

        Method method_getDeviceIdGemini = null;
        Method method_getSubscriberIdGemini = null;
        Method method_sendTextMessageEx = null;
        try {
            method_getDeviceIdGemini = tm.getClass().getMethod("getDeviceIdGemini", new Class[] { int.class });
            method_getSubscriberIdGemini = tm.getClass().getDeclaredMethod("getSubscriberIdGemini",
                    new Class[] { int.class });
            method_sendTextMessageEx = sms.getClass().getDeclaredMethod(
                    "sendTextMessageEx",
                    new Class[] { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class,
                            int.class });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        }
        if (null != method_getDeviceIdGemini && null != method_getSubscriberIdGemini
                && null != method_sendTextMessageEx) {
            return true;
        }
        return false;
    }

    @Override
    public String getOperatior(int slot) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getDeclaredMethod("getSimOperatorGemini",
                    new Class[] { int.class });
            return (String) method.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return null;
    }

    @Override
    public String getPhoneNumber(int slot) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = telephonyManager.getClass().getDeclaredMethod("getLine1NumberGemini",
                    new Class[] { int.class });
            return (String) method.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return null;
    }

    @Override
    public String getNetOperaterName(int slot) {
        String result = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            Method method = tm.getClass().getMethod("getNetworkOperatorNameGemini", new Class[] { int.class });
            result = (String) method.invoke(tm, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return result;
    }

}
