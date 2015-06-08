
package com.baidu.themeanimation.manager;

import java.util.ArrayList;

import com.baidu.themeanimation.util.Logger;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;

public class ContentManager {
    private ArrayList<Content> mContents;

    static ContentManager mInstance;
    static final Object mInstanceSync = new Object();

    static public ContentManager getInstance() {
        synchronized (mInstanceSync) {
            if (mInstance != null) {
                return mInstance;
            }

            mInstance = new ContentManager();
        }
        return mInstance;
    }

    public ContentManager() {
        mContents = new ArrayList<Content>();
    }

    public void addContent(Content content) {
        if (content != null) {
            mContents.add(content);
        }
    }

    public void clear() {
        mContents.clear();
    }

    public void update(Context context) {
        for (Content content : mContents) {
            content.update(context);
        }
    }

    public static class Content {
        private String mName; // can be null, the content provider name
        private Uri mUri; // the uri of content provider, it must not be null
        private String[] mProjection; // the projection of query command, null
                                      // means fetch all the columns
        private String mOrder; // the order parameters of query command
        private String mSelection; // the selection format of query command
        private String[] mSelectionParas; // the parameters for the mSelection
        private String mCountName; // the variable which represent the number of
                                   // the query results
        private String mDependency; // not use now
        private final static String TAG = "content";

        private ArrayList<ContentVariable> mVariables = new ArrayList<ContentVariable>();

        /*
         * the query like this query(uri, projection, selection, seletionArgs,
         * order)
         */
        public Content() {
        }

        public void setDependency(String dependency) {
            mDependency = dependency;
        }

        public String getDependency() {
            return null;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            Logger.v(TAG, "Content.setName(" + name + ")");
            this.mName = name;
        }

        public Uri getUri() {
            return mUri;
        }

        public void setUri(Uri uri) {
            Logger.v(TAG, "Content.setUri(" + uri + ")");
            this.mUri = uri;
        }

        public void setUri(String uri) {
            setUri(Uri.parse(uri));
        }

        private String mUriFormat;

        /*
         * if has set the uri format, then the content will use the uri format
         * to construct the content uri address support %d:numerical variable,
         * %s:string variable, can only contain 1 variable
         */
        public void setUriFormat(String uriFormat) {
            if (uriFormat != null) {
                mUriFormat = uriFormat.replace("%d", "%s");
            }
        }

        public void setUriParas(String paras) {
            setUri(String.format(mUriFormat, paras));
        }

        public String[] getProjection() {
            return mProjection;
        }

        public void setProjection(String[] projection) {
            this.mProjection = projection;
            for (String column : projection) {
                Logger.v(TAG, "Content.setProjection(" + column + ")");
            }
        }

        public String getOrder() {
            return mOrder;
        }

        public void setOrder(String order) {
            this.mOrder = order;
            Logger.v(TAG, "Content.setOrder(" + order + ")");
        }

        public String getSelection() {
            return mSelection;
        }

        public void setSelection(String selection) {
            this.mSelection = selection;
            Logger.v(TAG, "Content.setSelection(" + selection + ")");
        }

        /*
         * START: For xml parser function
         */
        public void setWhere(String where) {
            setSelection(where);
        }

        private String mWhereFormat;

        public void setWhereFormat(String whereFormat) {
            if (whereFormat != null) {
                mWhereFormat = whereFormat.replace("%d", "%s");
            }
        }

        public void setWhereParas(String whereParas) {
            setWhere(String.format(mWhereFormat, whereParas));
        }

        // format: column1,column2,...,columnX
        public void setColumns(String columns) {
            if (columns != null) {
                setProjection(columns.split(","));
                Logger.v(TAG, "Content.setColumns(" + columns + ")");
            }
        }

        public void setCountName(String countName) {
            mCountName = countName;
        }

        /*
         * END: For xml parser function
         */

        public String[] getSelectionParas() {
            return mSelectionParas;
        }

        public void setSelectionParas(String[] selectionParas) {
            this.mSelectionParas = selectionParas;
        }

        public void addContentVariable(ContentVariable variable) {
            mVariables.add(variable);
        }

        public void update(Context context) {
            if (context != null) {
                ContentResolver cr = context.getContentResolver();
                Cursor cursor = null;
                int count = 0;
                try {
                    cursor = cr.query(mUri, mProjection, mSelection, mSelectionParas, mOrder);
                    if (cursor != null) {
                        count = cursor.getCount();
                        for (ContentVariable variable : mVariables) {
                            variable.update(cursor);
                        }
                    }
                } catch (Exception e) {
                    // Logger.w(TAG,
                    // "query exception: Uri="+mUri+", Projection="+mProjection+", "
                    // +
                    // "Selection="+mSelection+", SelectionParas="+mSelectionParas+", Order="+mOrder);
                    Logger.w(TAG, "Exception=" + e.toString());
                } finally {
                    if (cursor != null) {
                        if (mCountName != null) {
                            ExpressionManager.getInstance().setVariableValue(mCountName, count);
                        }
                        cursor.close();
                    }
                }
            }
        }

        /*
         * for button trigger function use!
         */
        public void actionUpdate(Context context, String useless) {
            Logger.v(TAG, "Trigger Content action update!");
            update(context);
        }
    }

    public static class ContentVariable {
        private static final int TYPE_INT = 0;
        private static final int TYPE_LONG = 1;
        private static final int TYPE_STRING = 2;
        private static final String TAG = "content";

        private String mName; // must not be null
        private int mType; // the variable's value type, default is TYPE_STRING
        private String mColumn; // the column name of the content provider, must
                                // not be null
        private int mRow; // the row index of the query result, default is 0

        public ContentVariable() {
            mRow = 0;
            mType = TYPE_STRING;
        }

        public void setName(String name) {
            mName = name;
            Logger.v(TAG, "ContentVariable.setName(" + name + ")");
        }

        public void setColumn(String column) {
            mColumn = column;
            Logger.v(TAG, "ContentVariable.setColumn(" + column + ")");
        }

        public void setType(int type) {
            mType = type;
        }

        public void setType(String type) {
            int iType = TYPE_STRING;
            if (type.equals("int")) {
                iType = TYPE_INT;
            } else if (type.equalsIgnoreCase(String.valueOf(TYPE_LONG))) {
                iType = TYPE_LONG;
            }
            setType(iType);
            Logger.v(TAG, "ContentVariable.setType(" + type + ")");
        }

        public void setRow(int row) {
            mRow = row;
            Logger.v(TAG, "ContentVariable.setRow(" + row + ")");
        }

        public void setRow(String row) {
            setRow(Integer.valueOf(row));
        }

        public Object update(Cursor cursor) {
            String result = null;
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(mColumn);
                Logger.v(TAG, "get value:" + mColumn);
                if (columnIndex > -1 && cursor.moveToPosition(mRow)) {
                    switch (mType) {
                        case TYPE_INT:
                            result = String.valueOf(cursor.getInt(columnIndex));
                            break;

                        case TYPE_LONG:
                            result = String.valueOf(cursor.getLong(columnIndex));
                            break;

                        case TYPE_STRING:
                        default:
                            result = cursor.getString(columnIndex);
                            break;
                    }
                }
            }

//            Logger.v(TAG, mName + ": " + mColumn + "=" + result);
            ExpressionManager.getInstance().setVariableValue(mName, (String) result);

            return result;
        }
    }
}
