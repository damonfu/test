package yi.phone;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class DefaultPhoneUtil implements IPhoneUtil {

    private Context mContext;

    public DefaultPhoneUtil(Context context) {
        mContext = context;
    }

    @Override
    public boolean isCompatable() {
        return true;
    }

    @Override
    public String getIMEI(int slot) {

        String imei = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();

        return imei;
    }

    @Override
    public boolean isSlotEnabled(int slot) {

        boolean result = false;
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (((TelephonyManager) telephonyManager).getSimState() == TelephonyManager.SIM_STATE_READY) {
            result = true;
        }

        return result;
    }

    @Override
    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int slot) {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);

        return true;
    }

    @Override
    public String getIMSI(int slot) {

        if (isSlotEnabled(slot)) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return null;
    }

    @Override
    public String getOperatior(int slot) {
        if (isSlotEnabled(slot)) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimOperator();
        }

        return null;
    }

    @Override
    public String getPhoneNumber(int slot) {
        if (isSlotEnabled(slot)) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        }
        return null;
    }

    @Override
    public String getNetOperaterName(int slot) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }
}
