package yi.util;
import java.lang.reflect.Field;
import android.util.Log;
public class IDHelper{
	private static final String INTERNALR = "com.android.internal.R";
	private static final String ID = "id";
	private static final String LAYOUT = "layout";
	private static final String STRING = "string";
	private static final String DRAWABLE = "drawable";
	private static final String STYLEABLE = "styleable";
	private static final String BOOLEAN = "bool";
	private static final String ATTR = "attr";
	private static final String TAG = "IDHelper";
	
	public static final int ID_OK_BUTTON = getIdByName("ok_button");
	public static final int ID_BTN_PLAY = getIdByName("btn_play");
	public static final int ID_VOICE_LIST = getIdByName("voice_list");
	public static final int ID_EMPTY_TEXT = getIdByName("empty_text");
	public static final int ID_CUSTOM_LIST = getIdByName("custom_list");
	public static final int ID_ALBUM = getIdByName("album");
	public static final int ID_BTN_PREV = getIdByName("btn_prev");
	public static final int ID_CANCEL_BUTTON = getIdByName("cancel_button");
	public static final int ID_BTN_NEXT = getIdByName("btn_next");
	
	public static final int LAYOUT_YI_RINGTONE = getLayoutByName("yi_ringtone");
	public static final int LAYOUT_SELDLG_SINGLE_HOLO = getLayoutByName("select_dialog_singlechoice_holo");
	public static final int LAYOUT_RINGTONE_CUSTOM_ITEM = getLayoutByName("ringtone_custom_item");
	
	public static final int STR_RING_PICK_NO_SEL = getStringByName("ringtone_picker_nothing_selectecd");
	public static final int STR_DESP_TARGET_UNLOCK = getStringByName("description_target_unlock");
	public static final int STR_RING_PICK_CUST_TITLE = getStringByName("ringtone_picker_custom_title");
	public static final int STR_RING_PICK_SD_UNMOUNT = getStringByName("ringtone_picker_sdcard_unmount");
	public static final int STR_RING_DEFAULT = getStringByName("ringtone_default");
	public static final int STR_RING_SILENT = getStringByName("ringtone_silent");
	public static final int STR_RING_PICK = getStringByName("ringtone_picker_title");
	public static final int STR_NUMERIC_DATA_TEMP = getStringByName("numeric_date_template");
	public static final int STR_24_HOUR_FORMAT = getStringByName("twenty_four_hour_time_format");
	public static final int STR_12_HOUR_FORMAT = getStringByName("twelve_hour_time_format");
	public static final int STR_NUMERIC_DATA_FORMAT = getStringByName("numeric_date_format");
	public static final int STR_LOCK_PAUSE = getStringByName("lockscreen_transport_pause_description");
	public static final int STR_LOCK_PLAY = getStringByName("lockscreen_transport_play_description");
	public static final int STR_LOCK_STOP = getStringByName("lockscreen_transport_stop_description");

	public static final int DR_LOCK_MUSIC_PLAY = getDrawableByName("zz_lockscreen_music_play");
	public static final int DR_MEDIA_STOP = getDrawableByName("ic_media_stop");
	public static final int DR_LOCK_MUSIC_DEFAULT = getDrawableByName("zz_lockscreen_music_default");
	public static final int DR_LOCK_MUSIC_PAUSE = getDrawableByName("zz_lockscreen_music_pause");
	
	public static int getId(String cls, String subcls, String field){
        int id = -1;
        try {
            Class<?> c = Class.forName(cls);
            Class<?>[] subClsList = c.getClasses();
            if(null != subClsList && subClsList.length > 0){
                for(int i=0;i<subClsList.length;i++){
                	String clsName = cls + "$" + subcls;
                	Log.e(TAG, "clsName is " + clsName);
                    if(subClsList[i].getName().equals(clsName)){
                        Field f = subClsList[i].getField(field);
                        Object obj = f.get(null);
                        if(null != obj && obj instanceof Integer){
                        	id = (Integer)obj;
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

	public static int[] getIdArr(String cls, String subcls, String field){
        int[] id = null;
        try {
            Class<?> c = Class.forName(cls);
            Class<?>[] subClsList = c.getClasses();
            if(null != subClsList && subClsList.length > 0){
                for(int i=0;i<subClsList.length;i++){
                    String clsName = cls + "$" + subcls;
                    Log.e(TAG, "clsName is " + clsName);
                    if(subClsList[i].getName().equals(clsName)){
                        Field f = subClsList[i].getField(field);
                        Object obj = f.get(null);
                        if(null != obj && obj instanceof int[]){
                            id = (int[])obj;
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

    public static int getIdByName(String field){
    	return getId(INTERNALR, ID, field);
    }
    public static int getLayoutByName(String field){
    	return getId(INTERNALR, LAYOUT, field);
    }
    public static int getStringByName(String field){
    	return getId(INTERNALR, STRING, field);
    }
    public static int getDrawableByName(String field){
    	return getId(INTERNALR, DRAWABLE, field);
    }

    public static class MenuGroup {
        public static final String self = "MenuGroup";
        public static final String id = "MenuGroup_id";
        public static final String menuCategory = "MenuGroup_menuCategory";
        public static final String orderInCategory = "MenuGroup_orderInCategory";
        public static final String checkableBehavior = "MenuGroup_checkableBehavior";
        public static final String visible = "MenuGroup_visible";
        public static final String enabled = "MenuGroup_enabled";
    }

    public static class MenuItem {
        public static final String self = "MenuItem";
        public static final String id = "MenuItem_id";
        public static final String menuCategory = "MenuItem_menuCategory";
        public static final String orderInCategory = "MenuItem_orderInCategory";
        public static final String title = "MenuItem_title";
        public static final String titleCondensed = "MenuItem_titleCondensed";
        public static final String icon = "MenuItem_icon";
        public static final String alphabeticShortcut = "MenuItem_alphabeticShortcut";
        public static final String numericShortcut = "MenuItem_numericShortcut";
        public static final String checkable = "MenuItem_checkable";
        public static final String checked = "MenuItem_checked";
        public static final String visible = "MenuItem_visible";
        public static final String enabled = "MenuItem_enabled";
        public static final String showAsAction = "MenuItem_showAsAction";
        public static final String onClick = "MenuItem_onClick";
        public static final String actionLayout = "MenuItem_actionLayout";
        public static final String actionViewClass = "MenuItem_actionViewClass";
        public static final String actionProviderClass = "MenuItem_actionProviderClass";
    }

    public static class MenuView {
        public static final String self = "MenuView";
        public static final String itemBackground = "MenuView_itemBackground";
        public static final String itemTextAppearance = "MenuView_itemTextAppearance";
        public static final String preserveIconSpacing = "MenuView_preserveIconSpacing";
        public static final String windowAnimationStyle = "MenuView_windowAnimationStyle";
    }

    public static class SearchView {
        public static final String self = "SearchView";
        public static final String iconifiedByDefault = "SearchView_iconifiedByDefault";
        public static final String maxWidth = "SearchView_maxWidth";
        public static final String queryHint = "SearchView_queryHint";
        public static final String imeOptions = "SearchView_imeOptions";
        public static final String inputType = "SearchView_inputType";
    }

    public static int getStyleableByName(String field){
        return getId(INTERNALR, STYLEABLE, field);
    }

    public static int[] getStyleableArrByName(String field){
        return getIdArr(INTERNALR, STYLEABLE, field);
    }

    public static int getBoolByName(String field){
        return getId(INTERNALR, BOOLEAN, field);
    }

    public static int getAttrByName(String field){
        return getId(INTERNALR, ATTR, field);
    }
}
