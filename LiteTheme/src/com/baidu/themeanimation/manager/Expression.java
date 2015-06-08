
package com.baidu.themeanimation.manager;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;

/*
 * used to calculate the expression value and set the new value to the specify object attribute
 */
public class Expression {
    private final static String TAG = "Expression";

    private Object mObject; // the object which will use this expression to set
                            // its attribute
    private Method mMethod; // the method of the object which used to set the
                            // attribute
    private String mExpressionString; // the expression
    private boolean mIsValid = true;
    private ArrayList<String> mVariables = new ArrayList<String>();
    private ExpressionManager mExpressionManager = ExpressionManager.getInstance();
    private Evaluator mEvaluator = Evaluator.getInstance();
    private boolean hasEvaluated = false;

    public Expression(String expressionString, Object object, Method method) {
        if(object == null || method == null || expressionString == null){
            return;
        }

        mObject = object;
        mMethod = method;
        setExpressionString(expressionString);
    }
    
    public String getExpressionString() {
        return mExpressionString;
    }

    private void setExpressionString(String exp) {
        /*
         * the variable is always start with #, then concat the variable name
         * example: #a: variable a's value, #b.x_1, the object b's x_1 value for
         * match jeval's standard, we will change the format like #{variable's
         * name} use {} to contain the vaiable's name if variable is start with
         * @, this means the variable has a string type value. if a expression
         * contain both # and @ variable, this expression is invalid for
         * example, expression like this "#stringVariable+@stringVariable" is
         * invalid
         */

        // check the expression validation
        if (exp == null) {
            mIsValid = false;
            return;
        }

        if (exp.indexOf('#') >= 0 && exp.indexOf(',') > 0 &&
                exp.indexOf('(') < 0) {
            // this expression is paras expression, which format like #xxx1,#xxx2
            // we change the format to paras(#xxx1, #xxx2)
            exp = "paras(" + exp + ")";
        }

        mExpressionString = replaceString(exp);
        mExpressionString = fomateExpression(mExpressionString, '#');
        mExpressionString = fomateExpression(mExpressionString, '@');
        mExpressionString = mExpressionString.replace('@', '#');
        addNullVariable(mExpressionString);
    }

    private String replaceString(String exp) {
        String result = new String(exp);
        int index = exp.indexOf('@');
        if (index < 0) {
            return result;
        }

        int length = exp.length();
        int start = 0;
        while (index >= 0) {
            index++;
            start = index;
            while (index < length) {
                char c = exp.charAt(index);
                if (c < '0' && c != '.') {
                    break;
                }
                index++;
            }

            String variableName = exp.substring(start, index);
            if (variableName.length() > 0 && mEvaluator.getVariables().containsKey(variableName)) {
                String value = (String) mEvaluator.getVariables().get(variableName);
                String name = '@'+variableName;
                result = result.replaceAll(name, value);
            }

            start = index;
            index = exp.indexOf('@', start);
        }
        return result;
    }

    private String fomateExpression(String exp, char symbol) {
        int index = exp.indexOf(symbol);
        if (index < 0) {
            return exp;
        }

        String result = "";
        int length = exp.length();
        int start = 0;
        while (index >= 0) {
            result += exp.substring(start, index + 1) + "{";
            index++;
            start = index;
            while (index < length) {
                char c = exp.charAt(index);
                if (c < '0' && c != '.') {
                    break;
                }
                index++;
            }

            String variableName = exp.substring(start, index);
            result += variableName + "}";
            if (variableName.length() > 0 && symbol == '#') {
                mVariables.add(variableName);
                mExpressionManager.monitorVariable(variableName, this);
            }

            start = index;
            index = exp.indexOf(symbol, start);
        }
        result += exp.substring(start);
        return result;
    }

    private void addNullVariable(String exp) {
        int pos = exp.indexOf("isnull(");
        while (pos >= 0) {
            int start = exp.indexOf("{", pos + 1);
            int index = exp.indexOf("}", pos + 1);
            if (start >= 0 && index >= 0 && index > start) {
                String variableName = exp.substring(start + 1, index);
                if (!mEvaluator.getVariables().containsKey(variableName)) {
                    mEvaluator.putVariable(variableName, Evaluator.DEFAULT_NULL_STR);
                }
            }
            pos = exp.indexOf("isnull(", pos + 1);
        }
    }

    public void setObject(Object object) {
        mObject = object;
    }

    public void setMethod(Method method) {
        // the method prototype should be function(int value)
        mMethod = method;
    }

    private boolean isExpressionValid() {
        if (!mIsValid || mExpressionString == null || mObject == null || mMethod == null) {
            return false;
        }

        for (int i = 0; i < mVariables.size(); i++) {
            if (mEvaluator.getVariables().get(mVariables.get(i)) == null) {
                return false;
            }
        }
        return true;
    }

    public static final int EXPRESSION_TYPE_INT = 0;
    public static final int EXPRESSION_TYPE_STRING = 1;

    public boolean exec(int type) {
        boolean result = true;

        if (!isExpressionValid()) {
            return false;
        }
        String value = null;
        try {
            if (type == EXPRESSION_TYPE_INT) {
                try {
                    value = mEvaluator.evaluate(mExpressionString, true, false);
                } catch (Exception e) {
                    result = false;
                    mIsValid = false;
                }
                if (value != null && value.indexOf("#") < 0) {
                    try {
                        mMethod.invoke(mObject, value);
                    } catch (Exception e) {
                        DecimalFormat decimalFormat = new DecimalFormat("#");
                        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
                        String realValue = decimalFormat.format(Double.parseDouble(value));
                        mMethod.invoke(mObject, realValue);
                    }
                }
            } else {
                value = mEvaluator.replaceVariables(mExpressionString);
                if (null == value || value.equals(""))
                    return false;
                try {
                    if(value != null && value.indexOf("#") > 0){
                       value = mEvaluator.evaluate(value);
                    }
                } catch (Exception e) {
                    mIsValid = false;
                }
                if (value != null && value.indexOf("#") < 0)
                    mMethod.invoke(mObject, value);
            }
        } catch (Exception e) {
             e.printStackTrace();
        }
        hasEvaluated = true;
        return result;
    }

    public boolean hasEvaluated() {
        return hasEvaluated;
    }
    
    public String getValue() {
        String value = null;

        if(!isExpressionValid())
            return null;

        try {
            value = mEvaluator.replaceVariables(mExpressionString);
            if (value != null && value.indexOf("#") > 0) {
                value = mEvaluator.evaluate(value);
            }
        } catch (Exception e) {
            mIsValid = false;
        }

        return value;
    }

    public boolean exec() {
        return exec(EXPRESSION_TYPE_INT);
    }

    public Boolean setValue(String variableName, long value) {
        // Logger.v(TAG, "Set "+variableName+" to "+value);
        // mEvaluator.putVariable(variableName, String.valueOf(value));
        return exec(EXPRESSION_TYPE_INT);
    }

    public boolean setValue(String variableName, String value) {
        // mEvaluator.putVariable(variableName, value);
        return exec(EXPRESSION_TYPE_STRING);
    }
}
