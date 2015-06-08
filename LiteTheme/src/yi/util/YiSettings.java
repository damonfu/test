package yi.util;

/**
 * @hide
 * The Yi internal static instance contains global system-level device preferences.
 */
public final class YiSettings {
    
    /**@hide*/
    public static final String ACTION_RINGTONE_PICKER_SYSTEM = "yi.intent.action.RINGTONE_PICKER_SYSTEM";
    /**@hide*/
    public static final String ACTION_RINGTONE_PICKER_CUSTOM = "yi.intent.action.RINGTONE_PICKER_CUSTOM";
    /**@hide*/
    public static final int ERROR_SDUNMOUNT = 1;
    /**@hide*/
    public static final int ERROR_SDEMPTY = 2;
    /**@hide*/
    public static final int ERROR_SDBUSY = 3;
    /**@hide*/
    public static final int TYPE_MESSAGE = 8;
    /**@hide*/
    public static final String EXTRA_RINGTONE_ERROR_FLAG = "android.intent.extra.ringtone.ERROR_FLAG"; 
    
    /**@hide*/
    public static final class System {
        
        /**@hide*/
        public static final String NEXT_ALARM_SNOOZE_NUMBER = "next_alram_snooze";
        
        /**
         * URI for the plug headset sound file.
         * @hide
         */
        public static final String PLUG_HEADSET_SOUND = "plug_headset_sound";
        
        /**
         * Whether IP Call is enabled? 0:disabled 1:enabled
         * @hide
         */
        public static final String IP_CALL_ENABLED= "ip_call_enabled";
        
        /**
         * IP Call prefix
         * @hide
         */
        public static final String IP_CALL_PREFIX= "ip_call_prefix";
        
        /**
         * Whether cloud report is enabled? 0:disabled 1:enabled
         * @hide
         */
        public static final String CLOUD_REPORT_ENABLED= "cloud_report_enabled";

        // ==== VOIP ====
        /**
         * Whether SIP Call is enabled? 0:disabled 1:enabled
         * @hide
         */
        public static final String SIP_CALL_ENABLED= "sip_call_enabled";
        // ==== VOIP end ====
    }

    /**
     * See CL-57786,
     * Allow user to filter system app's notification, except the following packages.
     */
    public static String PKG_NOT_FLITER_NOTIFICATION[] = {
        "com.android.phone",
        "com.android.contacts",
        "com.baidu.android.ota",
        "com.baidu.yi.userfeedback",
        "com.baidu.bsf.service"
    };
}
