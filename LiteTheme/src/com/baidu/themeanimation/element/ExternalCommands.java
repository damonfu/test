package com.baidu.themeanimation.element;

import com.baidu.themeanimation.util.Constants;

public class ExternalCommands extends Element {

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_EXTERNAL_COMMAND.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new ExternalCommands();
    }
}
