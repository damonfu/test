
package yi.util;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.view.Window;

import yi.widget.SearchView.SearchAutoComplete;

public class ReflectUtil {

    public static final String TAG = "ReflectUtil";

    public static final String CONTEXT = "android.content.Context";
    public static final String CONTEXT_METHOD_getThemeResId = "android.content.Context";

    public static class ContextReflect {
        public static final String className = "android.content.Context";
        public static final String methodGetThemeResId = "getThemeResId";

        public static int getThemeResId(Context context) {
            int resId = -1;
            try {
                Object result = Reflector.invokeMethod(context, methodGetThemeResId, null, null);
                if (result != null) {
                    resId = (Integer) result;
                }
            } catch (Exception e) {
            }
            return resId;
        }
    }

    public static class WindowReflect {
        public static final String className = "android.view.Window";
        public static final String methodIsDestroyed = "isDestroyed";

        public static boolean isDestroyed(Window window) {
            boolean isDestroyed = false;
            try {
                Object result = Reflector.invokeMethod(window, methodIsDestroyed, null, null);
                if (result != null) {
                    isDestroyed = (Boolean) result;
                }
            } catch (Exception e) {
            }
            return isDestroyed;
        }
    }

    public static class ActionMenuItemReflect {
        public static final String className = "com.android.internal.view.menu.ActionMenuItem";

        public static Object getInstance(Context context, int group, int id, int categoryOrder, int ordering,
                CharSequence title) {
            Class [] classTypes = {Context.class, int.class, int.class, int.class, int.class, CharSequence.class};
            Object [] classArgs = {context, group, id, categoryOrder, ordering, title};
            Object result = null;
            try {
                result = Reflector.newInstance(className, classTypes, classArgs);
            } catch (Exception e) {
            }
            return result;
        }
    }

    public static class SearchableInfoReflect {
        public static final String className = "android.app.SearchableInfo";
        public static final String methodFindActionKey = "findActionKey";
        public static final String methodGetActivityContext= "getActivityContext";
        public static final String methodGetProviderContext= "getProviderContext";

        public static Object findActionKey(SearchableInfo searchable, int keyCode) {
            Object actionKey = null;
            Class [] classTypes = {int.class};
            Object [] classArgs = {keyCode};
            try {
                actionKey = Reflector.invokeMethod(searchable, methodFindActionKey, classTypes, classArgs);
            } catch (Exception e) {
            }
            return actionKey;
        }

        public static Context getActivityContext(SearchableInfo searchable, Context context) {
            Context activityContext = null;
            Class [] classTypes = {Context.class};
            Object [] classArgs = {context};
            try {
                Object result = Reflector.invokeMethod(searchable, methodGetActivityContext, classTypes, classArgs);
                if (result != null) {
                    activityContext = (Context) result;
                } 
            } catch (Exception e) {
            }
            return activityContext;
        }

        public static Context getProviderContext(SearchableInfo searchable, Context context) {
            Context providerContext = null;
            Class [] classTypes = {Context.class};
            Object [] classArgs = {context};
            try {
                Object result = Reflector.invokeMethod(searchable, methodGetProviderContext, classTypes, classArgs);
                if (result != null) {
                    providerContext = (Context) result;
                } 
            } catch (Exception e) {
            }
            return providerContext;
        }
    }

    public static class ActionKeyInfoReflect {
        public static final String className = "android.app.SearchableInfo$ActionKeyInfo";
        public static final String methodGetQueryActionMsg = "getQueryActionMsg";
        public static final String methodGetSuggestActionMsgColumn = "getSuggestActionMsgColumn";
        public static final String methodGetSuggestActionMsg = "getSuggestActionMsg";

        public static String getQueryActionMsg(Object actionKey) {
            String msg = null;
            try {
                Object result = Reflector.invokeMethod(actionKey, methodGetQueryActionMsg, null, null);
                if (result != null) {
                    msg = (String) result;
                } 
            } catch (Exception e) {
            }
            return msg;
        }

        public static String getSuggestActionMsgColumn(Object actionKey) {
            String msg = null;
            try {
                Object result = Reflector.invokeMethod(actionKey, methodGetSuggestActionMsgColumn, null, null);
                if (result != null) {
                    msg = (String) result;
                } 
            } catch (Exception e) {
            }
            return msg;
        }

        public static String getSuggestActionMsg(Object actionKey) {
            String msg = null;
            try {
                Object result = Reflector.invokeMethod(actionKey, methodGetSuggestActionMsg, null, null);
                if (result != null) {
                    msg = (String) result;
                } 
            } catch (Exception e) {
            }
            return msg;
        }
    }
    
    public static class SearchAutoCompleteReflect {
        public static final String className = "yi.widget.SearchView$SearchAutoComplete";
        public static final String methodEnsureImeVisible = "ensureImeVisible";
        public static final String methodSetDropDownAnimationStyle = "setDropDownAnimationStyle";
        public static final String methodSetText = "setText";

        public static void ensureImeVisible(SearchAutoComplete searchAutoComplete, boolean visible) {
            Class [] classTypes = {boolean.class};
            Object [] classArgs = {visible};
            try {
                Reflector.invokeMethod(searchAutoComplete, methodEnsureImeVisible, classTypes, classArgs);
            } catch (Exception e) {
            }
        }
        
        public static void setDropDownAnimationStyle(SearchAutoComplete searchAutoComplete, int animationStyle) {
            Class [] classTypes = {int.class};
            Object [] classArgs = {animationStyle};
            try {
                Reflector.invokeMethod(searchAutoComplete, methodSetDropDownAnimationStyle, classTypes, classArgs);
            } catch (Exception e) {
            }
        }
        
        public static void setText(SearchAutoComplete searchAutoComplete, CharSequence text, boolean filter) {
            Class [] classTypes = {CharSequence.class, boolean.class};
            Object [] classArgs = {text, filter};
            try {
                Reflector.invokeMethod(searchAutoComplete, methodSetText, classTypes, classArgs);
            } catch (Exception e) {
            }
        }
    }

    public static class ContentResolverReflect {
        public static final String className = "android.content.ContentResolver";
        public static final String methodGetResourceId = "getResourceId";

        //result is a instance of ContentResolverReflect$OpenResourceIdResult
        public static Object getResourceId(ContentResolver contentResolver, Uri uri) {
            Object result = null;
            Class [] classTypes = {CharSequence.class, boolean.class};
            Object [] classArgs = {contentResolver, uri};
            try {
                result = Reflector.invokeMethod(contentResolver, methodGetResourceId, classTypes, classArgs);
            } catch (Exception e) {
            }
            return result;
        }
    }

    public static class OpenResourceIdResultReflect {
        public static final String className = "android.content.ContentResolver$OpenResourceIdResult";
        public static final String fieldR = "r";
        public static final String fieldId = "id";

        public static Resources getResources(Object openResourceIdResult) {
            Resources resources = null;
            try {
                Object result = Reflector.getValue(openResourceIdResult, fieldR);
                if (result != null) {
                    resources = (Resources) result;
                }
            } catch (Exception e) {
            }
            return resources;
        }

        public static int getId(Object openResourceIdResult) {
            int id = -1;
            try {
                Object result = Reflector.getValue(openResourceIdResult, fieldId);
                if (result != null) {
                    id = (Integer) result;
                } 
            } catch (Exception e) {
            }
            return id;
        }
    }

    public static class SearchManagerResultReflect {
        public static final String className = "android.app.SearchManager";
        public static final String methodGetSuggestions= "getSuggestions";

        public static Cursor getSuggestions(SearchManager searchManager, SearchableInfo searchable, String query, int queryLimit) {
            Cursor cursor = null;
            Class [] classTypes = {SearchableInfo.class, String.class, int.class};
            Object [] classArgs = {searchable, query, queryLimit};
            try {
                Object result = Reflector.invokeMethod(searchManager, methodGetSuggestions, classTypes, classArgs);
                if (result != null) {
                    cursor = (Cursor) result;
                } 
            } catch (Exception e) {
            }
            return cursor;
        }
    }

}
