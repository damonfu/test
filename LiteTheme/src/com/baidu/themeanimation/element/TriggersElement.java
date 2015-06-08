package com.baidu.themeanimation.element;

import com.baidu.themeanimation.util.Constants;

public class TriggersElement extends Element {

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_TRIGGERS.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new TriggersElement();
    }

    public void exec(){
        if (getTriggerElements() != null) {
            for(TriggerElement trigger: getTriggerElements()){
                trigger.exec();
            }
        }
    }

    public void exec(String action){
        if (getTriggerElements() != null) {
            for(TriggerElement trigger : getTriggerElements()){
                trigger.exec(action);
            }
        }
    }

}
