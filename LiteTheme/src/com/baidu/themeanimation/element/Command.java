
package com.baidu.themeanimation.element;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;

/**
 * Command
 * 
 * @author luqingyuan Class deal RingMode/wifi/Data/Bluetooth/UsbStorage ...
 *         need here
 *         Trigger和Command
 */
public class Command extends CommandElement {
    private String mTarget; // RingMode/wifi/Data/Bluetooth/UsbStorage not
                            // support now
    private String mProperty;
    private String mValue; 

    public Command() {
        super();
        setCommandType(CommandElement.TYPE_COMMAND);
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_COMMAND.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new Command();
    }

    public void setTarget(String target) {
        int idx = target.indexOf(".");
        if (idx > 0) {
            mTarget = target.substring(0, idx);
            mProperty = target.substring(idx + 1);
        } else {
            mTarget = target;
        }
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public void exec() {
        ExpressionManager.getInstance().execElement(mTarget, mProperty, mValue);
    }
}
