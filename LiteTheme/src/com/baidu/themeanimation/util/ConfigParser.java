
package com.baidu.themeanimation.util;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;

public class ConfigParser {

    private static Evaluator mEvaluator = Evaluator.getInstance();

    public static void parse(InputStream configStream) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(configStream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals(Constants.TAG_CHECKBOX)) {
                        parseCheckbox(parser);
                    } else if (name.equals(Constants.TAG_STRING_INPUT)) {
                        parseStringInput(parser);
                    } else if (name.equals(Constants.TAG_NUMBER_INPUT)) {
                        parseNumberInput(parser);
                    } else if (name.equals(Constants.TAG_STRING_CHOICE)) {
                        parseStringChoice(parser);
                    } else if (name.equals(Constants.TAG_APP_PICKER)) {
                        parseAppPicker(parser);
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseCheckbox(XmlPullParser parser) {
        if (parser.getName().equals(Constants.TAG_CHECKBOX)) {
            String key = null;
            String value = null;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                String attrValue = parser.getAttributeValue(i);
                if (attrName.equals("id")) {
                    key = attrValue;
                }
                if (attrName.equals("default")) {
                    value = attrValue;
                }
            }
            addValue(key, value);
        }
    }

    private static void parseStringInput(XmlPullParser parser) {
        if (parser.getName().equals(Constants.TAG_STRING_INPUT)) {
            String key = null;
            String value = null;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                String attrValue = parser.getAttributeValue(i);
                if (attrName.equals("id")) {
                    key = attrValue;
                }
                if (attrName.equals("default")) {
                    value = attrValue;
                }
            }
            addValue(key, value, true);
        }
    }

    private static void parseNumberInput(XmlPullParser parser) {
        if (parser.getName().equals(Constants.TAG_NUMBER_INPUT)) {
            String key = null;
            String value = null;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                String attrValue = parser.getAttributeValue(i);
                if (attrName.equals("id")) {
                    key = attrValue;
                }
                if (attrName.equals("default")) {
                    value = attrValue;
                }
            }
            addValue(key, value);
        }
    }

    private static void parseStringChoice(XmlPullParser parser) {
        if (parser.getName().equals(Constants.TAG_STRING_CHOICE)) {
            String key = null;
            String text = null;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                String attrValue = parser.getAttributeValue(i);
                if (attrName.equals("id")) {
                    key = attrValue;
                }
                if (attrName.equals("default")) {
                    text = attrValue;
                }
            }
            String value = getChoiceValue(parser, text);
            addValue(key, value, true);
        }
    }

    private static String getChoiceValue(XmlPullParser parser, String target) {
        String result = null;
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals(Constants.TAG_CHOICE_ITEM)) {
                        String text = null;
                        String value = null;
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);
                            if (attrName.equals("text")) {
                                text = attrValue;
                            }
                            if (attrName.equals("value")) {
                                value = attrValue;
                            }
                        }
                        if (target.equals(text)) {
                            result = value;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String name = parser.getName();
                    if (name.equals(Constants.TAG_STRING_CHOICE)) {
                        break;
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void parseAppPicker(XmlPullParser parser) {
        if (parser.getName().equals(Constants.TAG_APP_PICKER)) {
            String key = null;
            String value = null;
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                String attrValue = parser.getAttributeValue(i);
                if (attrName.equals("id")) {
                    key = attrValue;
                }
                if (attrName.equals("text")) {
                    value = attrValue;
                }
            }
            addValue(key + ".name", value, true);
        }
    }

    private static void addValue(String key, String value) {
        addValue(key, value, false);
    }

    private static void addValue(String key, String value, boolean addQuote) {
        if (key != null && value != null) {
            if (addQuote && !(value.startsWith("'") && value.endsWith("'"))) {
                value = "'" + value + "'";
            }
            mEvaluator.putVariable(key, value);
        }
    }
}
