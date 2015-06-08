package com.baidu.themeanimation.element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.baidu.themeanimation.util.Constants;

public class CommandIntent extends CommandElement {
    private Intent mIntent;
    private String mPackageName;
    private String mClass;

    public CommandIntent() {
        super();
        setCommandType(CommandElement.TYPE_INTENT_COMMAND);
        mIntent = new Intent();
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_INTENT_COMMAND.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new CommandIntent();
    }

    public void setAction(String action) {
        mIntent.setAction(action);
        // Logger.v("IntentWrapper", "Intent set Action=" + action);
    }

    public void setType(String type) {
        mIntent.setType(type);
        // Logger.v("IntentWrapper", "Intent set type=" + type);
    }

    public void setCategory(String category) {
        mIntent.addCategory(category);
        // Logger.v("IntentWrapper", "Intent set category=" + category);
    }

    public void setUri(String uri) {
        try {
            mIntent.setData(Uri.parse(String.format(Constants.SEARCH_BAIDU_WEB,
                    URLEncoder.encode(uri, "gb2312"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Logger.v("IntentWrapper", "Intent set uri=" + uri);
    }

    public void setPackage(String packageName) {
        // Logger.v("IntentWrapper", "Intent set packageName=" +
        // packageName);
        mPackageName = packageName;
    }

    public void setClass(String className) {
        // Logger.v("IntentWrapper", "Intent set className=" + className);
        mClass = className;
    }

    public Intent getIntent() {
        if (mPackageName != null && mClass != null) {
            ComponentName componentName = new ComponentName(mPackageName, mClass);
            mIntent.setComponent(componentName);
        }

        return mIntent;
    }
}
