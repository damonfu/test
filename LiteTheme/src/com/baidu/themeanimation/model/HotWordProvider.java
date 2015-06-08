
package com.baidu.themeanimation.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.themeanimation.util.Logger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class HotWordProvider extends ContentProvider {
    public static final String DB_NAME = "hotworddb";
    public static final String TABLE_NAME = "hotwordtable";
    // column name
    public static final String INFO = "info";
    public static final String ID = "id";
    public static final String AUTOHORITY = "com.baidu.themeanimation.hotwords";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_NAME);
    public static final int VERSION = 1;

    public class DBlite extends SQLiteOpenHelper {
        public DBlite(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + "(" + ID + " int, " + INFO
                    + " text not null);");
            Logger.v("http", "create table");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void add(String hotword) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(INFO, hotword);
            db.insert(TABLE_NAME, "", values);
        }
    }

    DBlite dBlite;
    SQLiteDatabase db;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = dBlite.getWritableDatabase();
        long rowId;

        rowId = db.insert(TABLE_NAME, "", values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new IllegalArgumentException("Unknown URI" + uri);
    }

    @Override
    public boolean onCreate() {
        this.dBlite = new DBlite(getContext());
        /*
         * fetch the hot words in handler
         */
        mHandler.sendMessage(mHandler.obtainMessage());
        return true;
    }

    private void refresh(ArrayList<String> keywords) {
        // delete all the hot words in the table
        db = dBlite.getWritableDatabase();
        db.beginTransaction();
        String sql = "Delete from " + HotWordProvider.TABLE_NAME;
        db.execSQL(sql);
        ContentValues values = new ContentValues();
        int i = 0;
        for (String keyword : keywords) {
            values.put(ID, i++);
            values.put(HotWordProvider.INFO, keyword);
            db.insert(HotWordProvider.TABLE_NAME, "", values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    // Default timeouts
    private final static int HTTP_CONN_TIMEOUT = 7000;
    private final static int HTTP_TIMEOUT = 7000;

    // Protocol constants
    private final static String HTTP_METHOD = "POST";
    private final static boolean DBG = true;
    private final static String HTTP_TAG = "http";

    private final static long REFREASH_INTERVAL = 15 * 60 * 1000;

    private ArrayList<String> mKeywordList = new ArrayList<String>();
    private long mLastUpdateKeywordTime = 0;

    private Handler mHandler = new H();

    private class H extends Handler {
        public void handleMessage(Message m) {
            new Thread() {
                public void run() {
                    getKeyWord();
                    postDelayed(new Runnable() {

                        public void run() {
                            sendMessage(obtainMessage());
                        }
                    }, REFREASH_INTERVAL);
                }
            }.start();
        }
    }

    private void getKeyWord() {
        long currentTime = System.currentTimeMillis();
        long intervals = currentTime - mLastUpdateKeywordTime;
        Logger.v(HTTP_TAG, "getKeyWord at " + currentTime + ", last update time="
                + mLastUpdateKeywordTime);

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isAvailable()) {
            return;
        }

        if (intervals < 0 || intervals >= REFREASH_INTERVAL) {
            String url = "http://box.os.baidu.com/hitspot?num=49";

            for (int i = 0; i < 3; i++) {
                if (sugQuery(url)) {
                    refresh(mKeywordList);
                    return;
                }
            }
        }
    }

    private boolean sugQuery(String userQuery) {
        HttpURLConnection conn = null;
        boolean isPostSuccess = false;
        OutputStream out = null;
        try {
            URL queryURL = new URL(userQuery);
            conn = (HttpURLConnection) queryURL.openConnection();

            conn.setRequestMethod(HTTP_METHOD);
            conn.setUseCaches(false);
            conn.setConnectTimeout(HTTP_CONN_TIMEOUT);
            conn.setReadTimeout(HTTP_TIMEOUT);
            conn.setRequestProperty("Content-Length", "0");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();
            out = conn.getOutputStream();
            out.flush();
            int resCode = conn.getResponseCode();
            Logger.d(HTTP_TAG, "resCode : " + resCode);
            if (resCode != HttpURLConnection.HTTP_OK) {
                isPostSuccess = false;
            }
        } catch (MalformedURLException e) {
            // e.printStackTrace();
            Logger.w("Exception HotWordProvider: sugQuery",
                    "MalformedURLException " + e.getMessage());
            isPostSuccess = false;
        } catch (ProtocolException e) {
            // e.printStackTrace();
            Logger.w("Exception HotWordProvider: sugQuery", "ProtocolException " + e.getMessage());
            isPostSuccess = false;
        } catch (IOException ex) {
            isPostSuccess = false;
            // ex.printStackTrace();
            Logger.w("Exception HotWordProvider: sugQuery", "IOException " + ex.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ex.printStackTrace();
                Logger.w("Exception HotWordProvider: sugQuery",
                        "finally IOException " + ex.getMessage());
            }
        }

        FileOutputStream fout = null;
        try {
            InputStream inputStream = conn.getInputStream();
            fout = new FileOutputStream(getContext().getFilesDir().getPath() + "/keywords");
            byte[] buffer = new byte[1024];
            int length = 0;
            String content = "";

            while (true)
            {
                length = inputStream.read(buffer);
                if (length < 0)
                    break;
                content += new String(buffer, 0, length, Charset.forName("UTF-8"));
            }
            ;

            Logger.v(HTTP_TAG, content);

            if (content.length() < 1) {
                isPostSuccess = false;
                Logger.v(HTTP_TAG, "Nothing is got from server.");
            } else {
                JSONObject data = new JSONObject(content);
                int status = data.getInt("status");
                if (status == 1) {
                    isPostSuccess = true;
                    JSONArray arrayJson = data.getJSONArray("words");
                    int key_length = arrayJson.length();
                    for (int i = 0; i < key_length; i++) {
                        mKeywordList.add(i, arrayJson.getString(i));
                        Logger.v(HTTP_TAG, "fetch " + i + "=" + arrayJson.getString(i));
                    }

                    mLastUpdateKeywordTime = System.currentTimeMillis();
                    if (DBG) {
                        Logger.v(HTTP_TAG, "mLastUpdateKeywordTime : " + mLastUpdateKeywordTime);
                    }
                    fout.write(content.getBytes(Charset.forName("UTF-8")));
                    fout.getFD().sync();
                } else {
                    isPostSuccess = false;
                    Logger.v(HTTP_TAG, "The status is error. No data is got from server.");
                }
            }
        } catch (IOException ex) {
            isPostSuccess = false;
            ex.printStackTrace();
        } catch (JSONException e) {
            isPostSuccess = false;
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return isPostSuccess;
    }

    int mStartIndex = 0;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        db = dBlite.getWritableDatabase();
        Cursor c;
        Logger.v("http", "mStartIndex=" + mStartIndex);
        c = db.query(TABLE_NAME, projection, "id >= ?", new String[] {
            String.valueOf(mStartIndex)
        }, null, null, null);
        mStartIndex += 7;
        if (mStartIndex >= 49) {
            mStartIndex = 0;
        } else {
            if (49 - mStartIndex < 7) {
                mStartIndex = 49 - 7;
            }
        }
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }

}
