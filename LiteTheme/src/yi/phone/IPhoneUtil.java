package yi.phone;

import android.app.PendingIntent;
import android.content.Context;

public interface IPhoneUtil {

    public static int SLOT_1 = 0;
    public static int SLOT_2 = 1;

    public boolean isCompatable();

    public String getIMEI(int slot);

    public boolean isSlotEnabled(int slot);

    public String getIMSI(int slot);

    public String getOperatior(int slot);

    public String getPhoneNumber(int slot);

    public boolean sendMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent,
            PendingIntent deliveryIntent, int slot);

    public String getNetOperaterName(int slot);

    public class Creator {
        private static IPhoneUtil mInstance = null;
        private static Object msync = new Object();

        public static IPhoneUtil getInstance(Context context) {
            if (null == mInstance) {
                synchronized (msync) {
                    IPhoneUtil mPhoneUtilImpl = new DefaultPhoneUtil(context);

                    // Check Compatable
                    IPhoneUtil[] mPhones = new IPhoneUtil[] { new MTKTCLPhoneUtil(context),
                            new MTKZXPhoneUtil(context), new MTKPhoneUtil(context), new QualcommPhoneUtil(context),
                            new QualcommZXPhoneUtil(context) };
                    for (int i = 0; i < mPhones.length; i++) {
                        if (mPhones[i].isCompatable()) {
                            mPhoneUtilImpl = mPhones[i];
                            break;
                        }
                    }
                    mInstance = mPhoneUtilImpl;
                }
            }
            return mInstance;
        }
    }
}
