
package com.baidu.themeanimation.element;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.themeanimation.util.Constants;

public class GroupElement extends VisibleElement {

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_GROUP.equals(tagName)
                || Constants.TAG_GROUP_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new GroupElement();
    }

    @Override
    public void startAnimations() {
        List<VisibleElement> elements = getVisibleElements();

        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).startAnimations();
            }
        }
    }

    private GroupElementView mGroupElementView = null;

    public void clearView() {
        super.clearView();
        if (mGroupElementView != null) {
            ViewGroup viewGroup = (ViewGroup) mGroupElementView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mGroupElementView);
            }
            if (getVisibleElements() != null) {
                for (int i = 0; i < getVisibleElements().size(); i++) {
                    getVisibleElements().get(i).clearView();
                }
            }
            mGroupElementView.removeAllViews();
            mGroupElementView = null;
        }
    }

    @Override
    public View generateView(Context context, Handler handler) {
        if (mGroupElementView == null) {
            mGroupElementView = new GroupElementView(context, handler);
        }
        setView(mGroupElementView);
        return mGroupElementView;
    }

    public class GroupElementView extends RelativeLayout {

        public GroupElementView(Context context, Handler handler) {
            super(context);

            setLayoutParams(genLayoutParams());
        }
    }
}
