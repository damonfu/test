
package com.baidu.themeanimation.element;

import com.baidu.themeanimation.manager.Expression;
import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Utils;

public class CommandVariable extends CommandElement{
//    private String mName; // RingMode/wifi/Data/Bluetooth/UsbStorage not
//                            // support now
    private String mExpressionString;
    private Expression mExpression;
    private int mType;
    private boolean mConst;

//    private long updateCount = 0;
    private final static int TYPE_NUM = 0;
    private final static int TYPE_STR = 1;

    public CommandVariable() {
        mType = TYPE_NUM;
        mConst = false;
        mExpressionString = "";
        setCommandType(CommandElement.TYPE_VARIABLE_COMMAND);
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_VAR_COMMAND.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new CommandVariable();
    }

    public void setExpression(String expression) {
        mExpressionString = expression;
    }

    public String getExpression() {
        return mExpressionString;
    }

    public void setType(String type) {
        // not support
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

    public boolean getConst() {
        return mConst;
    }

    @Override
    public void exec() {
       //not support condition & delay
        String value = null;
        if (!mExpressionString.contains("#")) {
            value = mExpressionString;
        } else {
            value = mExpression.getValue();
        }

        if (mType == TYPE_NUM) {
            if (Utils.isInt(mExpressionString)) {
                ExpressionManager.getInstance().setVariableValue(getName(), Integer.valueOf(mExpressionString));
            }
        } else {
            ExpressionManager.getInstance().setVariableValue(getName(), mExpressionString);
        }

    }

}
