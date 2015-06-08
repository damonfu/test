
package com.baidu.themeanimation.element;

import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.Utils;

import android.os.Handler;

public abstract class CommandElement extends Element {

    public static final int TYPE_COMMAND          = 0;
    public static final int TYPE_VARIABLE_COMMAND = 1;
    public static final int TYPE_INTENT_COMMAND   = 2;

    private boolean mCondition = true;
    private boolean mDelayCondition = true;
    private long mDelay = 0;
    private int mType;

    private Handler mHandler = new Handler();

    public void setCommandType(int type) {
        mType = type;
    }

    public int getCommandType() {
        return mType;
    }

    public boolean getCondition() {
        return mCondition;
    }

    public void setDelay(long delay) {
        mDelay = delay;
    }

    public void setDelay(String delay) {
        setDelay(Long.parseLong(delay));
    }

    public long getDelay() {
        return mDelay;
    }

    public void setCondition(boolean condition) {
        mCondition = condition;
    }

    public void setCondition(String condition) {
        setCondition(Utils.getBoolean(condition));
    }

    public boolean getDelayCondition() {
        return mDelayCondition;
    }

    public void setDelayCondition(boolean condition) {
        mDelayCondition = condition;
    }

    public void setDelayCondition(String condition) {
        setDelayCondition(Utils.getBoolean(condition));
    }

    /**
     * execute the command
     */
    public void execDelay(){
        if (mCondition) {
            if (mDelay > 0 && mDelayCondition) {
                mHandler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        exec();
                    }}, mDelay);
            } else {
                exec();
            }
        }
    }

    /*
     * should override this method
     */
    protected void exec(){

    }
}
