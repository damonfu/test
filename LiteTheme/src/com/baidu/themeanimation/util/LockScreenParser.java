
package com.baidu.themeanimation.util;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.baidu.themeanimation.element.LockScreenElement;
import com.baidu.themeanimation.manager.ElementManager;
import com.baidu.themeanimation.manager.ExpressionManager;

/*
 * this class used to parser manifest.xml file, to generate the lock screen layout, animation, etc
 * and this class will store the lock screen information, it just update when the lock screen theme has been changed
 * xml parser use XMLPullParser to parse
 */
public class LockScreenParser {
    private final static String TAG = "LockScreenParser";

    public LockScreenParser() {
    }

    static LockScreenParser mInstance;
    static final Object mInstanceSync = new Object();

    static public LockScreenParser getInstance() {
        synchronized (mInstanceSync) {
            if (mInstance != null) {
                return mInstance;
            }

            mInstance = new LockScreenParser();
        }
        return mInstance;
    }

    public LockScreenElement inflate(InputStream manifestStream)
            throws XmlPullParserException, IOException, Exception {

        // should clear all ExpressionManager, remove all the old information
        ExpressionManager.getInstance().reset();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(manifestStream, null);

        LockScreenElement lockElement = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                if (Constants.TAG_LOCKSCREEN.equals(tagName)
                        || Constants.TAG_LOCKSCREEN_BAIDU.equals(tagName)) {
                    lockElement = (LockScreenElement) ElementManager.parseElement(parser);
                }
            }
            eventType = parser.next();
        }
        ExpressionManager.getInstance().execAll();
        return lockElement;
    }
}
