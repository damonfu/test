
package com.baidu.themeanimation.util;

public class Constants {
    public final static String LOCK_SCREEN_THEME = "lockscreen"; //

    public final static String PATH_LOCKWALLPAPER = "/data/data/com.baidu.thememanager.ui/files/wallpaper/default_lock_wallpaper.jpg";
    public final static String PATH_LOCKSCREEN_DIR = "/data/data/com.baidu.thememanager.ui/files/";
    public final static int DEFAULT_SCREEN_WIDTH = 480;
    public final static int DEFAULT_SCREEN_HEIGHT = 800;
    
    public final static int DEFAULT_SCREEN_WIDTH_XH = 720;
    public final static int DEFAULT_SCREEN_HEIGHT_XH = 1280;

    public final static int DEFAULT_SCREEN_WIDTH_XXH = 1080;
    public final static int DEFAULT_SCREEN_HEIGHT_XXH = 1920;

    public final static int MSG_BATTERY = 0x200;
    public final static int CHARGING = 0;
    public final static int BATTERY_LOW = 1;
    public final static int BATTERY_FULL = 2;
    public final static int BATTERY_NORMAL = 3;
    public final static int ALWAYS_DISPLAY = 4; // will be displayed in any
                                                // state
    public final static boolean DBG_ANIMATION = false;
    public final static boolean DBG_BATTERY = false;

    public final static int MSG_TIME_TICK = 0x201;
    public final static int MSG_TIME_REFRESH = 0x202;
    public final static int MSG_LOCK_SCREEN_ACTIVITY_CLOSE = 0x203;
    public static final int MSG_TIMEOUT = 0x204;
    public static final int MSG_LOCK_SCREEN_INIT = 0x205;
    public static final int MSG_LOCK_SCREEN_INIT2 = 0x206;
    public static final int MSG_LOCK_SCREEN_UNINT = 0x207;
    public static final int MSG_UPDATE_WALLPAPER = 0x208;
    public static final int MSG_UPDATE_IMAGE = 0x209;
    public final static int MSG_UNLOCK_INTENT = 0x210;

    public final static int MSG_UPDATE_CALLLOG = 0x211;
    public final static int MSG_UPDATE_SMSLOG = 0x212;
    public final static int MSG_UPDATE_ALARM = 0x213;
    public final static int MSG_CONFIGURATION_CHANGED = 0x214;


    public final static int MAX_DURATION = 1000000;

    public static final int IMAGE_TYPE_COMMON = 1;
    public static final int IMAGE_TYPE_WALLPAPER = 2;
    // text related
    public final static String LEFT_TAG = "left";
    public final static String CENTER_TAG = "center";
    public final static String RIGHT_TAG = "right";

    // touch state
    public final static int NONE_STATE = -1;
    public final static int NORMAL_STATE = 0;
    public final static int PRESSED_STATE = 1;
    public final static int REACHED_STATE = 2;

    public final static int LOW_BATTERY_THRESHOLD = 20;
    public final static int FULL_BATTERY_LEVEL = 100;

    public final static String CURRENT_THEME_DIR = "current_theme";
    public final static String LOCKSCREEN_CACHE_DIR = "cache";

    public final static String SEARCH_BAIDU_WEB = "http://m.baidu.com/s?word=%s";
    public final static String HOTWORD_URL = "http://box.os.baidu.com/hitspot?num=50";

    public final static String LOCKSCREEN_PHONE = "theme.lockscreen.phone";
    public final static String LOCKSCREEN_SHOW = "theme.lockscreen.show";
    // current lock type: 0 : system
    // 1 : app
    public final static String MULTI_THEME_LOCKTYPE = "current_lock_type";
    public final static int MULTI_THEME_LOCK = 1;
    public final static int MULTI_THEME_LOCK_SYSTEM = 0;

    public final static String SMS_URI = "content://mms-sms/";

    public final static String ACTION_REDUCE_THEME = "theme.lockscreen.action.reduce_theme";

    public final static String ACTION_REDUCE_THEME_SETTINGS = "com.baidu.lockscreen.action.reduce_theme_settings";
    public final static String IS_NEED_CLEAR_LOCK = "needClearLock";

    public final static String ACTION_APPLY_THEME = "theme.lockscreen.action.apply_theme";
    public final static String INTENT_IS_APPLY_WALLPAPER = "isApplyWallPaper";
    public final static String INTENT_IS_STARTMESSAGE = "StartMessage";
    public final static String INTENT_IS_STARTMESSAGE_START = "start";
    
    public final static String ACTION_APPLY_THEME_WATER = "theme.lockscreen.action.apply_theme_water";
    public final static int APPLY_TYPE_WATER = 4;

    public final static int APPLY_TYPE_REDUCE = 0;
    public final static int APPLY_TYPE_All = 1;
    public final static int APPLY_TYPE_WALLPAPER = 2;
    public final static int APPLY_TYPE_REDUCE_SETTINGS = 3;

    public final static String ACTION_GO_SLEEP = "theme.lockscreen.action.goSleep";
    public final static int TICK_PER_SECOND = 1000;
    public final static int TICK_TEND_SECOND = 10000;
    public final static int TICK_TEND_MINUTE = 600000;

    public final static String ACTION_NEXT_ALARM_BOOT = "com.baidu.yi.baiduclock.NEXT_ALARM_AFTER_BOOT";

    public final static String ACTION_UNLOCK_INTENT = "theme.lockscreen.action.Unlock";
    public final static String JUST_UNLOCK = "just_unlock";
    public final static String INTENT_PACKAGE_NAME = "packageName";
    public final static String INTENT_CLASS_NAME = "className";
    public final static String INTENT_COMPONENT_NAME = "componentName";
    public final static String INTENT_ACTION = "action";
    public final static String INTENT_TYPE = "type";
    public final static String INTENT_CATEGORY = "category";
    public final static String INTENT_URI = "uri";

    public final static String STRING_NULL = "null";

    /*
     * LockScreen's attribute name is version, frameRate, displayDesktop
     */
    // below is tag name in manifest.xml
    public final static String TAG_LOCKSCREEN = "Lockscreen";
    public final static String TAG_LOCKSCREEN_BAIDU = "BaiduLockscreen";
    // common
    public final static String TAG_COMMON_NAME = "name";
    public final static String TAG_COMMON_TYPE = "type";
    // category
    public final static String TAG_CHARGING = "Charging";
    public final static String TAG_BATTERY_LOW = "BatteryLow";
    public final static String TAG_BATTERY_FULL = "BatteryFull";
    public final static String TAG_BATTERY_NORMAL = "Normal";

    public final static String TAG_VERSION = "version";
    public final static String TAG_FRAMERATE = "frameRate";
    public final static String TAG_DISPLAY_DESKTOP = "displayDesktop";
    public final static String TAG_SCREENWIDTH = "screenWidth";

    /* the widget element common tag */
    public final static String TAG_POS_X = "x";
    public final static String TAG_POS_Y = "y";
    public final static String TAG_POS_CENTERX = "centerX";
    public final static String TAG_POS_CENTERY = "centerY";
    public final static String TAG_WIDTH = "w";
    public final static String TAG_HEIGHT = "h";
    public final static String TAG_ANGLE = "angle";
    public final static String TAG_ALPHA = "alpha";

    // image element tag
    public final static String TAG_IMAGE = "Image";
    public final static String TAG_IMAGE_BAIDU = "ImageElement";
    public final static String TAG_IMAGE_SRC = "src";
    public final static String TAG_IMAGE_SRCID = "srcid";
    public final static String TAG_IMAGE_ANTIALIAS = "antiAlias";

    // MASK
    public final static String TAG_MASK = "Mask";
    public final static String TAG_MASK_BAIDU = "ImageFilter";
    // wall paper related, it maybe useless
    public final static String TAG_WALLPAPER = "Wallpaper";
    public final static String TAG_WALLPAPER_BAIDU = "WallpaperElement";

    public final static String TAG_VARIABLE_BINDERS = "VariableBinders";
    public final static String TAG_VARIABLE_BINDERS_BAIDU = "VariableBinders";

    public final static String TAG_ANIMATION_IMAGE = "AnimationImage";
    // content
    public final static String TAG_CONTENT = "ContentProviderBinder";
    public final static String TAG_CONTENT_BAIDU = "ContentProviderBinder";
    public final static String TAG_CONTENT_VARIABLE = "Variable";
    public final static String TAG_CONTENT_VARIABLE_BAIDU = "Variable";

    public final static String TAG_DATE = "DateTime";
    public final static String TAG_DATE_BAIDU = "DateElement";

    // music control element related
    public final static String TAG_MUSIC_CONTROL = "MusicControl";
    public final static String TAG_MUSIC_CONTROL_BAIDU = "MusicControlElement";

    // button element related
    public final static String TAG_BUTTON = "Button";
    public final static String TAG_BUTTON_BAIDU = "ButtonElement";
    public final static String TAG_BUTTON_NORMAL = "Normal";
    public final static String TAG_BUTTON_PRESSED = "Pressed";
    public final static String TAG_TRIGGER = "Trigger";

    // text element tag
    public final static String TAG_TEXT = "Text";
    public final static String TAG_TEXT_BAIDU = "TextElement";
    public final static String TAG_TEXT_CONTENT = "text";
    public final static String TAG_TEXT_COLOR = "color";
    public final static String TAG_TEXT_SIZE = "size";
    public final static String TAG_TEXT_FORMAT = "format";
    public final static String TAG_TEXT_PARAS = "paras";
    public final static String TAG_TEXT_ALIGN = "align";
    public final static String TAG_TEXT_MARQUEE_SPEED = "marqueeSpeed";

    // time related
    public final static String TAG_TIME = "Time";
    public final static String TAG_TIME_BAIDU = "TimeElement";

    // unlock element
    public final static String TAG_UNLOCKER = "Unlocker";
    public final static String TAG_UNLOCKER_BAIDU = "UnlockerElement";
    public final static String TAG_GROUP = "Group";
    public final static String TAG_GROUP_BAIDU = "GroupElement";
    public final static String TAG_UNLOCKER_START_POINTER = "StartPoint";
    public final static String TAG_UNLOCKER_END_POINTER = "EndPoint";
    public final static String TAG_UNLOCKER_NORMAL_STATE = "NormalState";
    public final static String TAG_UNLOCKER_PRESSED_STATE = "PressedState";
    public final static String TAG_UNLOCKER_REACHED_STATE = "ReachedState";
    public final static String TAG_UNLOCKER_INTENT = "Intent";
    // unlock path
    public final static String TAG_UNLOCKER_PATH = "Path";
    public final static String TAG_POSITION = "Position";

    // Var
    public final static String TAG_VAR = "Var";
    public final static String TAG_VAR_BAIDU = "Variable";
    public final static String TAG_VAR_NAME = Constants.TAG_COMMON_NAME;
    public final static String TAG_VAR_EXPRESSION = "expression";
    public final static String TAG_VAR_TYPE = Constants.TAG_COMMON_TYPE;
    public final static String TAG_VAR_CONST = "const";

    //
    public final static String TAG_ALPHA_ANIMATION = "AlphaAnimation";
    public final static String TAG_ALPHA_ANIMATION_ALPHA = "Alpha";

    public final static String TAG_POSITION_ANIMATION = "PositionAnimation";
    public final static String TAG_POSITION_ANIMATION_POSITION = "Position";

    public final static String TAG_ROTATE_ANIMATION = "RotationAnimation";
    public final static String TAG_ROTATE_ANIMATION_ROTATE = "Rotation";
    public final static String TAG_SIZE_ANIMATION = "SizeAnimation";
    public final static String TAG_SIZE_ANIMATION_SIZE = "Size";

    public final static String TAG_VAR_ANIMATION = "VariableAnimation";
    public final static String TAG_VAR_ANIMATION_ANIFRAME = "AniFrame";

    public final static String TAG_SOURCE_ANIMATION = "SourcesAnimation";
    public final static String TAG_SOURCE_ANIMATION_SOURCE = "Source";

    public final static String TAG_ANIMATION_SET = "AnimationSet";

    //command triggers
    public final static String TAG_COMMAND = "Command";
    public final static String TAG_VAR_COMMAND = "VariableCommand";
    public final static String TAG_INTENT_COMMAND = "IntentCommand";
    public final static String TAG_TRIGGERS = "Triggers";
    public final static String TAG_EXTERNAL_COMMAND = "ExternalCommands";
    
    // config value tag
    public final static String TAG_CHECKBOX = "CheckBox";
    public final static String TAG_STRING_INPUT = "StringInput";
    public final static String TAG_NUMBER_INPUT = "NumberInput";
    public final static String TAG_STRING_CHOICE = "StringChoice";
    public final static String TAG_CHOICE_ITEM = "Item";
    public final static String TAG_APP_PICKER = "AppPicker";

    public final static int Animation_Status_Running = 1;
    public final static int Animation_Status_Stop = 2;

}
