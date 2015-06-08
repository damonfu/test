package yi.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class QualcommPhoneUtil implements IPhoneUtil {
    private final static String TAG = QualcommPhoneUtil.class.getName();

    private Context mContext;

    public QualcommPhoneUtil(Context context) {
        mContext = context;
    }

    public String getIMEI(int slot) {
        try {
            Class<?> yidmc = Class.forName("android.telephony.MSimTelephonyManager");

            Method getDefaultMethod = yidmc.getDeclaredMethod("getDefault", new Class[0]);
            Object telephonyManager = getDefaultMethod.invoke(null, new Object[0]);
            Method getIMEIMethod = yidmc.getDeclaredMethod("getDeviceId", new Class[] { int.class });

            return (String) getIMEIMethod.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public boolean isSlotEnabled(int smsNumber) {

        boolean result = false;
        if (0 != smsNumber && 1 != smsNumber) {
            return false;
        }

        try {
            Class<?> yidmc;
            yidmc = Class.forName("android.telephony.MSimTelephonyManager");

            Method getDefaultMethod;
            getDefaultMethod = yidmc.getDeclaredMethod("getDefault", new Class[0]);
            Object telephonyManager = getDefaultMethod.invoke(null, new Object[0]);

            Method getPhoneStateMethod;
            getPhoneStateMethod = yidmc.getDeclaredMethod("getSimState", new Class[] { int.class });
            int simState = (Integer) getPhoneStateMethod.invoke(telephonyManager, new Object[] { smsNumber });
            if (simState == TelephonyManager.SIM_STATE_READY) {
                result = true;
            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }

        return result;
    }

    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int smscardNumber) {

        boolean result = true;

        try {
            Class<?> yidmc;
            yidmc = Class.forName("android.telephony.MSimSmsManager");
            Method method_getSmsManager = yidmc.getMethod("getDefault", new Class[0]);
            Object smssender = method_getSmsManager.invoke(null, new Object[0]);

            Method method = smssender.getClass().getDeclaredMethod(
                    "sendTextMessage",
                    new Class[] { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class,
                            int.class });
            method.invoke(smssender, new Object[] { destinationAddress, scAddress, text, sentIntent, deliveryIntent,
                    smscardNumber });
        } catch (NoSuchMethodException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
        return result;
    }

    @Override
    public String getIMSI(int slotNumber) {
        try {
            Class<?> yidmc = Class.forName("android.telephony.MSimTelephonyManager");

            Method getDefaultMethod = yidmc.getDeclaredMethod("getDefault", new Class[0]);
            Object telephonyManager = getDefaultMethod.invoke(null, new Object[0]);
            Method getIMSIMethod = yidmc.getDeclaredMethod("getSubscriberId", new Class[] { int.class });
            return (String) getIMSIMethod.invoke(telephonyManager, new Object[] { slotNumber });

        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public boolean isCompatable() {

        Class<?> yidmc = null;
        Class<?> yidmc2 = null;
        Boolean isMultiSimEnable = false;
        try {
            yidmc = Class.forName("android.telephony.MSimSmsManager");
            yidmc2 = Class.forName("android.telephony.MSimTelephonyManager");

            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> cls = tm.getClass();
            Method method = cls.getMethod("isMultiSimEnabled");
            isMultiSimEnable = (Boolean) method.invoke(tm);
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }

        return (null != yidmc && null != yidmc2 && isMultiSimEnable);
    }

    @Override
    public String getOperatior(int slot) {
        try {
            Class<?> yidmc = Class.forName("android.telephony.MSimTelephonyManager");
            Method getDefaultMethod = yidmc.getDeclaredMethod("getDefault", new Class[0]);
            Object telephonyManager = getDefaultMethod.invoke(null, new Object[0]);
            Method getPhoneOperatorMethod = yidmc.getDeclaredMethod("getSimOperator", new Class[] { int.class });

            return (String) getPhoneOperatorMethod.invoke(telephonyManager, new Object[] { slot });

        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    @Override
    public String getPhoneNumber(int slot) {
        try {
            Class<?> yidmc = Class.forName("android.telephony.MSimTelephonyManager");

            Method getDefaultMethod = yidmc.getDeclaredMethod("getDefault", new Class[0]);
            Object telephonyManager = getDefaultMethod.invoke(null, new Object[0]);
            Method getPhoneNumberMethod = yidmc.getDeclaredMethod("getLine1Number", new Class[] { int.class });
            return (String) getPhoneNumberMethod.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    @Override
    public String getNetOperaterName(int slot) {
        String result = null;
        if (0 != slot && 1 != slot) {
            return null;
        }

        try {
            Class<?> yidmc;
            yidmc = Class.forName("android.telephony.MSimTelephonyManager");

            Method getDefaultMethod;
            getDefaultMethod = yidmc.getDeclaredMethod("getDefault", new Class[0]);
            Object telephonyManager = getDefaultMethod.invoke(null, new Object[0]);

            Method getPhoneStateMethod;
            getPhoneStateMethod = yidmc.getDeclaredMethod("getNetworkOperatorName", new Class[] { int.class });
            result = (String) getPhoneStateMethod.invoke(telephonyManager, new Object[] { slot });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        }

        Log.d(TAG, result);
        return result;
    }
}
