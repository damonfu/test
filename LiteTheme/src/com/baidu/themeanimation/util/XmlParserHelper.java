
package com.baidu.themeanimation.util;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;

import com.baidu.themeanimation.manager.Expression;
import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;

public class XmlParserHelper {
    private final static String TAG = "XmlParserHelper";

    public static void setElementAttr(XmlPullParser parser, Object receiver) {
        setElementAttr(parser, receiver, Expression.EXPRESSION_TYPE_INT);
    }

    /*
     * type specify the type of all the attributes's value Expreesion
     */
    public static void setElementAttr(XmlPullParser parser, Object receiver, int type) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            String attrValue = parser.getAttributeValue(i);

            if (attrName == null || attrValue == null) {
                continue;
            }

            attrValue = replaceConsts(attrValue);
            boolean hasVariable = (attrValue.indexOf('#') + attrValue.indexOf('@') > -2) &&
                    (!attrName.equals(Constants.TAG_TEXT_COLOR));

            if (attrName.equals(Constants.TAG_COMMON_NAME)) {
                ExpressionManager.getInstance().monitorElement(attrValue, receiver);
            }

            try {
                String methodName = Utils.getMethodName(attrName);
                Method method = receiver.getClass().getMethod(methodName, String.class);
                if (!hasVariable) {
                    try {
                        if (attrValue.indexOf("+") + attrValue.indexOf("(") >= 0) {
                            attrValue = Evaluator.getInstance().evaluate(attrValue);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        method.invoke(receiver, attrValue);
                    } catch (Exception e) {
                        try {
                            if (Utils.getStringType(attrValue) == Utils.STRING_NONE) {
                                attrValue = Evaluator.getInstance().evaluate(attrValue);
                            }
                            DecimalFormat decimalFormat = new DecimalFormat("#");
                            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
                            String realValue = decimalFormat.format(Double.parseDouble(attrValue));
                            method.invoke(receiver, realValue);
                        } catch (Exception e2) {
                            try {
                                method.invoke(receiver, "0");
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        }
                    }
                } else {
                    Expression expression = new Expression(attrValue, receiver, method);
                    ExpressionManager.getInstance().addExpression(expression);
                }
            } catch (Exception e) {
                Log.w("unsupported", "attrName = " + attrName + ", element = " + receiver.getClass().getSimpleName());
            }
        }
    }

    private static String replaceConsts(String str){
        if (str != null && str.indexOf('#') >= 0) {
            String result = str.replaceAll("#" + VariableConstants.getGlobalVariableTag(VariableConstants.VAI_SCREEN_WIDTH),
                    String.valueOf(FileUtil.DESIGH_SCREEN_WIDTH));
            result = result.replaceAll("#" + VariableConstants.getGlobalVariableTag(VariableConstants.VAI_SCREEN_HEIGHT),
                    String.valueOf(FileUtil.DESIGN_SCREEN_HEIGHT));
            return result;
        }
        return str;
    }
}
