package com.baidu.themeanimation.manager;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.baidu.themeanimation.element.AlphaAnimationElement;
import com.baidu.themeanimation.element.AnimationImageViewElement;
import com.baidu.themeanimation.element.ButtonElement;
import com.baidu.themeanimation.element.Command;
import com.baidu.themeanimation.element.CommandIntent;
import com.baidu.themeanimation.element.CommandVariable;
import com.baidu.themeanimation.element.DateElement;
import com.baidu.themeanimation.element.Element;
import com.baidu.themeanimation.element.ExternalCommands;
import com.baidu.themeanimation.element.GroupElement;
import com.baidu.themeanimation.element.ImageElement;
import com.baidu.themeanimation.element.LockPathElement;
import com.baidu.themeanimation.element.LockScreenElement;
import com.baidu.themeanimation.element.MaskElement;
import com.baidu.themeanimation.element.PositionAnimationElement;
import com.baidu.themeanimation.element.RotateAnimationElement;
import com.baidu.themeanimation.element.SizeAnimationElement;
import com.baidu.themeanimation.element.SourceAnimationElement;
import com.baidu.themeanimation.element.StartPointElement;
import com.baidu.themeanimation.element.StartPointElement.EndPointElement;
import com.baidu.themeanimation.element.StateElement;
import com.baidu.themeanimation.element.TextElement;
import com.baidu.themeanimation.element.TimeElement;
import com.baidu.themeanimation.element.TriggerElement;
import com.baidu.themeanimation.element.TriggersElement;
import com.baidu.themeanimation.element.UnlockerElement;
import com.baidu.themeanimation.element.VarElement;
import com.baidu.themeanimation.element.VariableAnimationElement;
import com.baidu.themeanimation.element.WallpaperElement;

public class ElementManager {

    private static List<Element> mElementList = new ArrayList<Element>();
    static {
        mElementList.add(new LockScreenElement());
        mElementList.add(new TriggerElement());
        mElementList.add(new TriggersElement());
        mElementList.add(new LockPathElement());
        mElementList.add(new ExternalCommands());

        mElementList.add(new AnimationImageViewElement());
        mElementList.add(new ButtonElement());
        mElementList.add(new GroupElement());
        mElementList.add(new ImageElement());
        mElementList.add(new WallpaperElement());
        mElementList.add(new MaskElement());
        mElementList.add(new StartPointElement());
        mElementList.add(new EndPointElement());
        mElementList.add(new StateElement());
        mElementList.add(new TextElement());
        mElementList.add(new DateElement());
        mElementList.add(new TimeElement());
        mElementList.add(new UnlockerElement());
        mElementList.add(new VarElement());

        mElementList.add(new AlphaAnimationElement());
        mElementList.add(new PositionAnimationElement());
        mElementList.add(new RotateAnimationElement());
        mElementList.add(new SizeAnimationElement());
        mElementList.add(new SourceAnimationElement());
        mElementList.add(new VariableAnimationElement());

        mElementList.add(new Command());
        mElementList.add(new CommandIntent());
        mElementList.add(new CommandVariable());
    }

    public static Element parseElement(XmlPullParser parser) {
        String tagName = parser.getName();
        try {
            for (int i = 0; i < mElementList.size(); i++) {
                Element element = mElementList.get(i);
                if (element.matchTag(tagName)) {
                    return element.parse(parser);
                }
            }
            skipElement(parser, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * If we do not support one tag, skip it and all it's child tags.
     */
    public static void skipElement(XmlPullParser parser, boolean report)
            throws Exception {
        String tagName = parser.getName();
        if (report) {
            Log.w("unsupported", "tag name = " + tagName);
        }

        int eventType = parser.getEventType();
        if (eventType == XmlPullParser.START_TAG) {
            eventType = parser.next();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.getEventType();
                if (eventType == XmlPullParser.START_TAG) {
                    skipElement(parser, false);
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals(tagName)) {
                        break;
                    }
                }
                eventType = parser.next();
            }
        }
    }
}
