
package com.baidu.themeanimation.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.VariableConstants;

import com.baidu.themeanimation.net.sourceforge.jeval.Evaluator;

/*
 * 1. manage all the global variables and user customer variables.
 * 2. each variable response to a list which contain all the object which will use the variable to set its attribute.
 * 3. when the variable changed, all the object will related to it will also update its attribute.
 */
public class ExpressionManager {
    private final static String TAG = "ExpressionManager";
    /*
     * key: is the variable value: is the list of all the expression which will
     * use the key
     */
    private HashMap<String, List<Expression>> mHashMap = new HashMap<String, List<Expression>>();

    /*
     * key: string, variable's name value: int, the variable's value it will
     * contain the value of all the variable which has been passed to the
     * ExpressionManager
     */

    private HashMap<String, Long> mVariablesValue = new HashMap<String, Long>();
    /*
     * key: string, means the element's name value: list, the elements objects
     * which name is the key it is used button will trigger some elements's
     * methods
     */
    private HashMap<String, List<Object>> mElementsMap;

    private List<Expression> mExpressionList = new ArrayList<Expression>();

    // The previous stack of parsed operators

    private HashMap<String, Stack> mOperatorStackCache = new HashMap<String, Stack>();

    // The previous stack of parsed operands.
    private HashMap<String, Stack> mOperandStackCache = new HashMap<String, Stack>();

    static ExpressionManager mInstance;
    static final Object mInstanceSync = new Object();

    static public ExpressionManager getInstance() {

        synchronized (mInstanceSync) {
            if (mInstance != null) {
                return mInstance;
            }

            mInstance = new ExpressionManager();
        }
        return mInstance;
    }

    public void addExpression(Expression expression) {
        if (expression != null) {
            mExpressionList.add(expression);
        }
    }

    public void execAll() {
        for (Expression expression : mExpressionList) {
            if (!expression.hasEvaluated()) {
                expression.exec();
            }
        }
    }

    public void setOperatorStack(String _expression, Stack _stack) {
        if (mOperatorStackCache == null) {
            mOperatorStackCache = new HashMap<String, Stack>();
        }
        mOperatorStackCache.put(_expression, _stack);

    }

    public Stack getOperatorStack(String _expression) {
        Stack localStack = null;
        if (mOperatorStackCache != null && mOperatorStackCache.containsKey(_expression)) {
            localStack = mOperatorStackCache.get(_expression);
        }
        return localStack;
    }

    public void setOperandStack(String _expression, Stack _stack) {
        if (mOperandStackCache == null) {
            mOperandStackCache = new HashMap<String, Stack>();
        }
        mOperandStackCache.put(_expression, _stack);
    }

    public Stack getOperandStack(String _expression) {
        Stack localStack = null;
        if (mOperandStackCache != null && mOperandStackCache.containsKey(_expression)) {
            localStack = mOperandStackCache.get(_expression);
        }
        return localStack;
    }

    /*
     * clear all the information in the ExpressionManager
     */
    public void reset() {
        if (mHashMap != null) {
            mHashMap.clear();
        }
        if (mOperatorStackCache != null) {
            mOperatorStackCache.clear();
        }

        if (mOperandStackCache != null) {
            mOperandStackCache.clear();
        }
        if (mVariablesValue != null) {
            mVariablesValue.clear();
        }

        if (mElementsMap != null) {
            mElementsMap.clear();
        }
    }

    /*
     * monitor variableName which expression will use it value to set attribute
     */
    public void monitorVariable(String variableName, Expression expression) {
        List<Expression> expressions = mHashMap.get(variableName);
        if (expressions == null) {
            expressions = new ArrayList<Expression>();
        }
        if (!expressions.contains(expression)) {
            expressions.add(expression);
            mHashMap.put(variableName, expressions);
        }
    }

    public void monitorElement(String name, Object element) {
        if (mElementsMap == null) {
            mElementsMap = new HashMap<String, List<Object>>();
        }

        List<Object> elements = mElementsMap.get(name);
        if (elements == null) {
            elements = new ArrayList<Object>();
        }

        elements.add(element);
        mElementsMap.put(name, elements);
    }

    /**
     * get elements array by name
     * @param name
     * @return
     */
    public List<Object> getElementArrayByName(String name) {
        if (mElementsMap != null) {
            return mElementsMap.get(name);
        }
        return null;
    }

    /*
     * name: the element name, methodName: the method of the element this
     * function will do name.setMethodName(value)
     */
    public void execElement(String name, String methodName, String value) {
        List<Object> elements = mElementsMap.get(name);
        Object element = null;
        Method method = null;
        try {
            if (methodName != null && elements != null && value != null) {
                String tMethodName = "set" + Character.toUpperCase(methodName.charAt(0))
                        + methodName.substring(1);
                 Logger.i("command", "execElement "+name+"."+tMethodName);

                for (int i = 0; i < elements.size(); i++) {
                    element = elements.get(i);
                    method = element.getClass().getMethod(tMethodName, String.class);
                    method.invoke(element, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
             Logger.w("command", "execElement: error "+name+"."+ methodName +":"+e.getMessage());
        }

    }

    /*
     * set string value
     */
    public void setVariableValue(String variableName, String value) {
        if (variableName == null) {
            return;
        }
        List<Expression> expressions = mHashMap.get(variableName);
        Evaluator.getInstance().putVariable(variableName, value);
        if (expressions != null) {
            for (int i = 0; i < expressions.size(); i++) {
                expressions.get(i).setValue(variableName, value);
            }
        }
    }

    public void setVariableValue(String variableName, long value) {
        if (variableName == null) {
            return;
        }
        long currentValue = Long.MAX_VALUE;
        if (mVariablesValue.containsKey(variableName)) {
            currentValue = mVariablesValue.get(variableName);
        }

        if (currentValue == value)
            return;

        List<Expression> expressions = mHashMap.get(variableName);
        mVariablesValue.put(variableName, value);
        Evaluator.getInstance().putVariable(variableName, String.valueOf(value));
        if (expressions != null) {
            for (int i = 0; i < expressions.size(); i++) {
                expressions.get(i).setValue(variableName, value);
            }
        }
    }

    /*
     * set the variable value, and update all attribute which related to it
     */
    public void setVariableValue(int va_index, long value) {
        long currentValue = Long.MAX_VALUE;
        if (mVariablesValue.containsKey(VariableConstants.getGlobalVariableTag(va_index))) {
            currentValue = mVariablesValue.get(VariableConstants.getGlobalVariableTag(va_index));
        }

        if (currentValue == value)
            return;

        mVariablesValue.put(VariableConstants.getGlobalVariableTag(va_index), value);
        List<Expression> expressions = mHashMap.get(VariableConstants
                .getGlobalVariableTag(va_index));
        Evaluator.getInstance().putVariable(VariableConstants.getGlobalVariableTag(va_index),
                String.valueOf(value));
        if (expressions != null) {
            for (int i = 0; i < expressions.size(); i++) {
                expressions.get(i)
                        .setValue(VariableConstants.getGlobalVariableTag(va_index), value);
            }
        }
    }

    public int getCategoryValue() {
        long category = Constants.BATTERY_NORMAL;

        if (mVariablesValue.containsKey(VariableConstants
                .getGlobalVariableTag(VariableConstants.VAI_CATEGORY))) {
            category = Long.valueOf(mVariablesValue.get(VariableConstants
                    .getGlobalVariableTag(VariableConstants.VAI_CATEGORY)));
        }
        return ((int) category);
    }
}
