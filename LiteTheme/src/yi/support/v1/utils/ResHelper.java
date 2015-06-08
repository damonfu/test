/**
 * 
 */

package yi.support.v1.utils;

import java.lang.reflect.Field;

public class ResHelper {
    private static final String INTERNALR = "com.android.internal.R";
    private static final String ID = "id";
    private static final String LAYOUT = "layout";
    private static final String BOOL = "bool";
    private static final String DIMEN = "dimen";
    private static final String ATTR = "attr";
    private static final String DRAWABLE = "drawable";
    private static final String STYLEABLE = "styleable";
    private static final String TAG = "ResHelper";

    public static int getResId(String cls, String subcls, String field) {
        int id = -1;
        try {
            Class<?> c = Class.forName(cls);
            Class<?>[] subClsList = c.getClasses();
            if (null != subClsList && subClsList.length > 0) {
                for (int i = 0; i < subClsList.length; i++) {
                    String clsName = cls + "$" + subcls;
                    Logger.e(TAG, "clsName is " + clsName);
                    if (subClsList[i].getName().equals(clsName)) {
                        Field f = subClsList[i].getField(field);
                        Object obj = f.get(null);
                        if (null != obj && obj instanceof Integer) {
                            id = (Integer) obj;
                        }
                        break;
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static int[] getResArrayId(String cls, String subcls, String field) {
        try {
            Class<?> c = Class.forName(cls);
            Class<?>[] subClsList = c.getClasses();
            if (null != subClsList && subClsList.length > 0) {
                for (int i = 0; i < subClsList.length; i++) {
                    String clsName = cls + "$" + subcls;
                    Logger.e(TAG, "clsName is " + clsName);
                    if (subClsList[i].getName().equals(clsName)) {
                        Field f = subClsList[i].getField(field);
                        Object obj = f.get(null);
                        if (obj.getClass().isArray()
                                && obj.getClass().getComponentType().newInstance() instanceof Integer) {
                            int[] id = (int[]) obj;
                            return id;
                        }
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static int getIdByName(String field) {
        return getResId(INTERNALR, ID, field);
    }

    public static int getLayoutByName(String field) {
        return getResId(INTERNALR, LAYOUT, field);
    }

    public static int getBoolByName(String field) {
        return getResId(INTERNALR, BOOL, field);
    }

    public static int getDimenByName(String field) {
        return getResId(INTERNALR, DIMEN, field);
    }

    public static int getAttrByName(String field) {
        return getResId(INTERNALR, ATTR, field);
    }

    public static int getDrawableByName(String field) {
        return getResId(INTERNALR, DRAWABLE, field);
    }

    public static int getStyleableByName(String field) {
        return getResId(INTERNALR, STYLEABLE, field);
    }

    public static int[] getStyleableArrayByName(String field) {
        return getResArrayId(INTERNALR, STYLEABLE, field);
    }
}
