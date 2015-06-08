
package com.baidu.themeanimation.element;

import java.util.List;

import com.baidu.themeanimation.util.Constants;

public class MaskElement extends VisibleElement {
    private String mSrc; // the mask image source
    private int mAlign; // relative to it parent or absolute coordinate
    private List<VisibleElement> mBaseElements = null;

    public final static int RELATIVE = 0;
    public final static int ABSOLUTE = 1;

    public MaskElement() {
        super();
        setSrc(null);
        setAlign(RELATIVE);
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_MASK.equals(tagName)
                || Constants.TAG_MASK_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new MaskElement();
    }

    public String getSrc() {
        return mSrc;
    }

    public void setSrc(String src) {
        this.mSrc = src;
    }

    public int getAlign() {
        return mAlign;
    }

    public void setAlign(int align) {
        if (align == RELATIVE || align == ABSOLUTE) {
            mAlign = align;
        }
    }

    public void setAlign(String align) {
        if (align.equals("absolute")) {
            setAlign(ABSOLUTE);
        } else {
            setAlign(RELATIVE);
        }
    }

    public List<VisibleElement> getBaseElements() {
        return mBaseElements;
    }

    public void setBaseElements(List<VisibleElement> mBaseElements) {
        this.mBaseElements = mBaseElements;
    }
}
