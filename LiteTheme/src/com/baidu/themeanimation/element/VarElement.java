
package com.baidu.themeanimation.element;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Utils;

public class VarElement extends VisibleElement{

    private String mExpression;
    private int mType;
    private boolean mConst;
    private boolean mHasValue = false;
    private final static int TYPE_NUM = 0;
    private final static int TYPE_STR = 1;
    private int mThreshold = Integer.MAX_VALUE;

    public VarElement() {
        mType = TYPE_NUM;
        mConst = false;
        mExpression = "";
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_VAR.equals(tagName)
                || Constants.TAG_VAR_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new VarElement();
    }

    public void setExpression(String expression) {
        if (mConst && mHasValue) {
            return;
        }
        mExpression = expression;
        if (expression != null && expression.startsWith("'")) {
            mType = TYPE_STR;
        }
        mHasValue = true;
        update(expression);
    }

    public String getExpression() {
        return mExpression;
    }

    public void setType(String type) {
        if (type.equalsIgnoreCase("number")) {
            mType = TYPE_NUM;
        } else if (type.equalsIgnoreCase("string")) {
            mType = TYPE_STR;
        }
    }

    public int getType() {
        return mType;
    }

    public void setConst(String isConst) {
        mConst = Utils.getBoolean(isConst);
    }

    public void setThreshold(String value) {
        setThreshold(Integer.valueOf(value));
    }

    public void setThreshold(int value){
        mThreshold = value;
    }

    public int getThreshold(){
        return mThreshold;
    }

    public void update(String value) {
        if (mType == TYPE_NUM) {
            int number = Integer.valueOf(value);
            ExpressionManager.getInstance().setVariableValue(getName(), number);
            if (mThreshold != Integer.MAX_VALUE && number >= mThreshold) {
                execTrigger();
            }
        } else {
            ExpressionManager.getInstance().setVariableValue(getName(), value);
        }
    }
}
