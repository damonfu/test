package yi.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class PushHelper {

    private Context mContext = null;

    private final String URI_APP_CONFIG = "content://com.baidu.push/config/apponoff/";
    private final String URI_REG_CONFIG = "content://com.baidu.push/config/appreg/";
    private final String URI_REG_APPLY = "content://com.baidu.push/config/regapply/";

    public PushHelper(Context context) {
        mContext = context;
    }

    /**
     * @return true if push is enabled for app, false if disabled
     */
    public boolean isEnabled() {
        String appname = mContext.getApplicationContext().getApplicationInfo().packageName;
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(URI_APP_CONFIG + appname);
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) != 0;
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    /**
     * @param isEnabled
     *            : true if app want enble push, false if not
     */
    public void setEnabled(boolean isEnabled) {
        String appname = mContext.getApplicationContext().getApplicationInfo().packageName;
        Uri uri = Uri.parse(URI_APP_CONFIG + appname);
        ContentValues cv = new ContentValues();
        cv.put("enable", isEnabled);
        mContext.getContentResolver().update(uri, cv, null, null);
    }

    /**
     * apply for register id
     */
    public void register() {
        String appname = mContext.getApplicationContext().getApplicationInfo().packageName;
        Uri uri = Uri.parse(URI_REG_APPLY + appname);
        mContext.getContentResolver().query(uri, null, null, null, null);
    }

    /**
     * 
     * @return register id if push has registered on server, null if not
     */
    public String getRegisterId() {
        String appname = mContext.getApplicationContext().getApplicationInfo().packageName;

        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(URI_REG_CONFIG + appname);
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
