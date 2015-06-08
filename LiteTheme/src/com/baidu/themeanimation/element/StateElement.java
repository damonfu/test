
package com.baidu.themeanimation.element;

import com.baidu.themeanimation.util.Constants;

public class StateElement extends VisibleElement {

    public int mState = Constants.NORMAL_STATE;

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_UNLOCKER_NORMAL_STATE.equals(tagName)
                || Constants.TAG_UNLOCKER_PRESSED_STATE.equals(tagName)
                || Constants.TAG_UNLOCKER_REACHED_STATE.equals(tagName)
                || Constants.TAG_BUTTON_NORMAL.equals(tagName)
                || Constants.TAG_BUTTON_PRESSED.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        StateElement element = new StateElement();
        if (Constants.TAG_BUTTON_NORMAL.equals(tagName)
                || Constants.TAG_UNLOCKER_NORMAL_STATE.equals(tagName)) {
            element.setState(Constants.NORMAL_STATE);
        } else if (Constants.TAG_BUTTON_PRESSED.equals(tagName)
                || Constants.TAG_UNLOCKER_PRESSED_STATE.equals(tagName)) {
            element.setState(Constants.PRESSED_STATE);
        } else if (Constants.TAG_UNLOCKER_REACHED_STATE.equals(tagName)) {
            element.setState(Constants.REACHED_STATE);
        }
        return element;
    }
}
