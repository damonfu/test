/*
 * Copyright (C) 2010 Baidu Inc.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yi.phone.IPhoneUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;

//import com.android.internal.telephony.Phone;

/**
 * Utility Device tool.
 * 
 * 
 */
public class DeviceManager {

    private static final String REGINFO_URL = "content://com.baidu.register.provider/";
    private static final String CHUNLEI_ID = "chunleiUid";
    private static final String CHUNLEI_CODE = "chunleiCode";

    private static final String FILE_NAME = "/register_info.xml";
    private static final String DATA_PATH = "/data/data";
    private static final String DATA_SUB_PATH = "/data/data/register";
    private static final String SETTINGS_KEY_DIGEST_CHUNLEI_UID = "digest_chunleiUid";
    private static final int DEVICE_SCREEN = 0;
    private static final int DEVICE_BACKPANEL = 1;


    private String destFilePath;
    private Context mContext;

    public DeviceManager(Context context) {
        mContext = context;
    }

    public static int getMotionEventSource(MotionEvent event) {
        return event != null && event.getPressure() == 0 ? DEVICE_BACKPANEL : DEVICE_SCREEN;
    }

    public static boolean isScreenMotionEvent(MotionEvent event) {
        return getMotionEventSource(event) == DEVICE_SCREEN;
    }
    /**
     * Save the IMEI and password as xml file.
     * 
     * @hide
     * @param IMEI
     *            The new IMEI receive from server.
     * @param password
     *            The password receive from server.
     */
    public void setDeviceInfo(String IMEI, String password) {
        try {
            File dataDir = new File(DATA_PATH);
            if (dataDir != null && dataDir.exists() && dataDir.canRead()) {
                File dataSubDir = new File(DATA_SUB_PATH);
                dataSubDir.mkdir();
                File dataFile = new File(DATA_SUB_PATH + FILE_NAME);
                createNewFile(dataFile, IMEI, password);
                return;
            }
        } catch (Exception e) {
            System.out.println("file not found exception");
        }
    }

    private void createNewFile(File privateFile, String IMEI, String password) {
        try {
            if (privateFile.exists() && privateFile.canWrite()) {
                FileOutputStream out = new FileOutputStream(privateFile.getAbsolutePath());
                out.write("".getBytes());
                out.flush();
                out.close();
            } else {
                privateFile.createNewFile();
                FileOutputStream out = new FileOutputStream(privateFile.getAbsolutePath());
                StringBuffer sf = new StringBuffer();
                sf.append("<?xml version=\"1.0\" encoding=\"utf-8 \"?>");
                sf.append("<response>");
                sf.append("<ChunleiUid>" + IMEI + "</ChunleiUid");
                sf.append("<ChunleiCode>" + password + "</ChunleiCode");
                sf.append("</response>");
                ByteArrayInputStream bin = new ByteArrayInputStream(sf.toString().getBytes());
                byte[] buf = new byte[1024];
                int n = 0;
                while ((n = bin.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
                out.flush();
                bin.close();
                out.close();
            }
        } catch (IOException e) {

        }
    }

    /**
     * Return ChunleiUid.If there has no ChunleiUid file which receive from
     * server, then return null.
     */
    public String getChunleiUid() {
        String info = null;
        try {
            Uri queryurl = Uri.parse(REGINFO_URL + CHUNLEI_ID);
            ContentResolver resolver = mContext.getContentResolver();
            info = resolver.getType(queryurl);
        } catch (Exception e) {
            System.out.println("in or out strean exception");
        }
        return info;
    }

    /**
     *Return Digest-ChunleiUid.If there has no ChunleiUid ,return null
     */
    public String getDigestChunleiUid(){
         String digestChunleiUid = Settings.System.getString(mContext.getContentResolver(), SETTINGS_KEY_DIGEST_CHUNLEI_UID);
         if (digestChunleiUid == null) {
             return null;
         }
         else{
             return digestChunleiUid;
         }
    }

    /**
     * Return IMEI.If there has no IMEI which receive from server, then return
     * the original IMEI.
     */
    public String getDeviceId() {
        String info = "";
        try {
            Uri queryurl = Uri.parse(REGINFO_URL + CHUNLEI_ID);
            ContentResolver resolver = mContext.getContentResolver();
            info = resolver.getType(queryurl);
            if (null == info && null != mContext) {
                info = getIMEI();
            }
            if (null == info) {
                info = "";
            }
        } catch (Exception e) {
            System.out.println("in or out strean exception");
        }
        return info;
    }

    /**
     * Get device key
     * 
     * @hide which will be used to encode user infomation. If there has no
     *       device key will return space.
     */
    public String getDeviceKey() {
        String info = "";
        try {
            Uri queryurl = Uri.parse(REGINFO_URL + CHUNLEI_CODE);
            ContentResolver resolver = mContext.getContentResolver();
            info = resolver.getType(queryurl);
            if (null == info) {
                info = "";
            }
        } catch (Exception e) {
            System.out.println("in or out strean exception");
        }
        return info;
    }

    /**
     * Get Local phone number
     * 
     * @hide which will be used to encode user infomation. If there has no
     *       line1number will return space.
     */
    public String getLine1Number() {
        String line1Number = "";
        try {
            line1Number = Settings.Secure.getString(mContext.getContentResolver(), "Line1Number");
            if (line1Number == null) {
                line1Number = "";
            }
        } catch (Exception e) {
            line1Number = "";
            System.out.println("in or out strean exception");
        }
        return line1Number;
    }

    /**
     * @hide
     */
    public static boolean isBmsDevice() {
        File file = new File("/system/app/BaiduCamera.apk");
        return !file.exists();
    }

    /**
     * @hide
     */
    public String getDestFilePath() {
        return destFilePath;
    }

    private String getIMEI() {
        IPhoneUtil phoneUtil = IPhoneUtil.Creator.getInstance(mContext);
        return phoneUtil.getIMEI(IPhoneUtil.SLOT_1);
    }

}
