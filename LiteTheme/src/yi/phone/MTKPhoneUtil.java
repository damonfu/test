package yi.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class MTKPhoneUtil implements IPhoneUtil {
    private Context mContext;

    public MTKPhoneUtil(Context context) {
        mContext = context;
    }

    public String getIMEI(int slot) {

        String imei = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            Class<?> yidmc = tm.getClass();
            Method method = yidmc.getMethod("getDeviceIdGemini", new Class[] { int.class });
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

    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int smscardNumber) {

        boolean result = true;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
        return result;
    }

    @Override
    public String getIMSI(int slotNumber) {

        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Method[] methods = telephonyManager.getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if ("getSubscriberIdGemini".equals(methods[i].getName())) {
                    return (String) methods[i].invoke(telephonyManager, new Object[] { slotNumber });
                }
            }
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return null;
    }

    public boolean isCompatable() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getSimStateGemini = null;
        try {
            method_getSimStateGemini = tm.getClass().getDeclaredMethod("getSimStateGemini", new Class[] { int.class });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        }
        if (null != method_getSimStateGemini) {
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
            Class<?> yidmc = tm.getClass();
            Method method = yidmc.getMethod("getNetworkOperatorNameGemini", new Class[] { int.class });
            result = (String) method.invoke(tm, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return result;
    }
}
