
package com.baidu.themeanimation.element;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.baidu.themeanimation.manager.ElementManager;
import com.baidu.themeanimation.util.XmlParserHelper;

public abstract class Element {

    private String mName;

    private MaskElement mMaskElement;

    private List<VisibleElement> mVisibleElements;
    private List<AnimationElement> mAnimationElements;
    private List<TriggerElement> mTriggerElements;
    private List<TriggersElement> mTriggersElements;
    private List<CommandElement> mCommandElements;

    private static List<SourceAnimationElement> mSourceAnimations =
        new ArrayList<SourceAnimationElement>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public boolean hasView() {
        return false;
    }

    public abstract boolean matchTag(String tagName);

    public abstract Element createElement(String tagName);

    public Element parseChild(XmlPullParser parser) throws Exception {
        return ElementManager.parseElement(parser);
    }

    public Element parse(XmlPullParser parser) throws Exception {
        int eventType = parser.getEventType();
        if (!(eventType == XmlPullParser.START_TAG)) {
            return null;
        }

        String tagName = parser.getName();
        Element element = createElement(tagName);
        XmlParserHelper.setElementAttr(parser, element);
        while (eventType != XmlPullParser.END_DOCUMENT) {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                Element child = element.parseChild(parser);
                element.addElement(child);
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
        }
        return element;
    }

    public List<VisibleElement> getVisibleElements() {
        return mVisibleElements;
    }

    public void addVisibleElement(VisibleElement element) {
        if (mVisibleElements == null) {
            mVisibleElements = new ArrayList<VisibleElement>();
        }
        mVisibleElements.add(element);
    }

    public List<TriggerElement> getTriggerElements() {
        return mTriggerElements;
    }

    public void addTriggerElement(TriggerElement element) {
        if (mTriggerElements == null) {
            mTriggerElements = new ArrayList<TriggerElement>();
        }
        mTriggerElements.add(element);
    }

    public List<TriggersElement> getTriggersElements() {
        return mTriggersElements;
    }

    public void addTriggersElement(TriggersElement element) {
        if (mTriggersElements == null) {
            mTriggersElements = new ArrayList<TriggersElement>();
        }
        mTriggersElements.add(element);
    }

    public List<AnimationElement> getAnimationElements() {
        return mAnimationElements;
    }

    public void addAnimationElement(AnimationElement element) {
        if (mAnimationElements == null) {
            mAnimationElements = new ArrayList<AnimationElement>();
        }
        mAnimationElements.add(element);
        if (element instanceof SourceAnimationElement) {
            mSourceAnimations.add((SourceAnimationElement) element);
        }
    }

    public List<CommandElement> getCommandElements() {
        return mCommandElements;
    }

    public void addCommandElement(CommandElement element) {
        if (mCommandElements == null) {
            mCommandElements = new ArrayList<CommandElement>();
        }
        mCommandElements.add(element);
    }

    public List<SourceAnimationElement> getmSourceAnimationss() {
        return mSourceAnimations;
    }

    public MaskElement getMask() {
        return mMaskElement;
    }

    public void setMask(MaskElement mask) {
        mMaskElement = mask;
    }

    public void addElement(Element element) {
        if (element != null) {
            if (element instanceof MaskElement) {
                setMask((MaskElement) element);
            } else if (element instanceof VisibleElement) {
                addVisibleElement((VisibleElement) element);
            } else if (element instanceof TriggerElement) {
                addTriggerElement((TriggerElement) element);
            } else if (element instanceof TriggersElement) {
                addTriggersElement((TriggersElement) element);
            } else if (element instanceof AnimationElement) {
                addAnimationElement((AnimationElement) element);
            } else if (element instanceof CommandElement) {
                addCommandElement((CommandElement) element);
            }
        }
    }

    public void execTrigger(){
        if(getTriggerElements() != null){
            for(TriggerElement trigger : getTriggerElements()){
                trigger.exec();
            }
        }
        if(getTriggersElements() != null){
            for(TriggersElement triggers : getTriggersElements()){
                triggers.exec();
            }
        }
    }

    public void execTrigger(String action) {
        if(getTriggerElements() != null){
            for(TriggerElement trigger : getTriggerElements()){
                trigger.exec(action);
            }
        }
        if(getTriggersElements() != null){
            for(TriggersElement triggers : getTriggersElements()){
                triggers.exec(action);
            }
        }
    }
}
