package yi.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class MTKZXPhoneUtil implements IPhoneUtil {
    private Context mContext;

    public MTKZXPhoneUtil(Context context) {
        mContext = context;
    }

    @Override
    public String getIMEI(int slot) {
        String imei = null;
        TelephonyManager telephonyManager;
        try {
            Method getDefaultManager = TelephonyManager.class.getMethod("getDefault", new Class[] { int.class });
            telephonyManager = (TelephonyManager) getDefaultManager.invoke(null, new Object[] { slot });
            imei = telephonyManager.getDeviceId();
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return imei;
    }

    @Override
    public boolean isSlotEnabled(int slotNumber) {

        boolean result = false;
        if (0 != slotNumber && 1 != slotNumber) {
            return false;
        }
        TelephonyManager telephonyManager;
        try {
            Method getDefaultManager = TelephonyManager.class.getMethod("getDefault", new Class[] { int.class });
            telephonyManager = (TelephonyManager) getDefaultManager.invoke(null, new Object[] { slotNumber });
            int simState = telephonyManager.getSimState();
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
    public String getIMSI(int slotNumber) {

        String imsi = null;
        TelephonyManager telephonyManager;
        try {
            Method getDefaultManager = TelephonyManager.class.getMethod("getDefault", new Class[] { int.class });
            telephonyManager = (TelephonyManager) getDefaultManager.invoke(null, new Object[] { slotNumber });
            imsi = telephonyManager.getSubscriberId();
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return imsi;
    }

    @Override
    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int slotNumber) {

        boolean result = true;

        SmsManager smsManger;
        try {
            Method getDefaultManager = SmsManager.class.getMethod("getDefault", new Class[] { int.class });
            smsManger = (SmsManager) getDefaultManager.invoke(null, new Object[] { slotNumber });
            smsManger.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);

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

    public boolean isCompatable() {

        Method getDefaultManager = null;
        Method getDefaultsmsManger = null;
        try {
            getDefaultManager = TelephonyManager.class.getMethod("getDefault", new Class[] { int.class });
            getDefaultsmsManger = SmsManager.class.getMethod("getDefault", new Class[] { int.class });
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        }
        if (null != getDefaultManager && null != getDefaultsmsManger) {
            return true;
        }
        return false;
    }

    @Override
    public String getOperatior(int slot) {
        return null;
    }

    @Override
    public String getPhoneNumber(int slot) {
        return null;
    }

    @Override
    public String getNetOperaterName(int slot) {
        String result = null;
        TelephonyManager telephonyManager;
        try {
            Method getDefaultManager = TelephonyManager.class.getMethod("getDefault", new Class[] { int.class });
            telephonyManager = (TelephonyManager) getDefaultManager.invoke(null, new Object[] { slot });
            result = telephonyManager.getNetworkOperatorName();
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return result;
    }

}
