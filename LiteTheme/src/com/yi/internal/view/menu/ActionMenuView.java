
package com.yi.internal.view.menu;

import yi.support.v1.utils.Animatable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yi.internal.view.menu.MenuBuilder.ItemInvoker;

public class ActionMenuView extends LinearLayout implements ItemInvoker, MenuView {
    
    enum Motion {
        STANDARD,
        FADE_IN,
        FADE_OUT,
        HIDEN,
    }
    
    private Motion mMotion = Motion.STANDARD;
    private MenuBuilder mMenu;
    private Runnable mFadeCallback;

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initialize(MenuBuilder menu) {
        mMenu = menu;
    }

    @Override
    public int getWindowAnimations() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        clearAllAnimation();
        switch (mMotion) {
            case FADE_IN:
                fadeIn(mFadeCallback);
                break;
            case FADE_OUT:
                fadeOut(mFadeCallback);
                break;
            case HIDEN:
                hide();
                break;
        }
    }

    public void fadeIn() {
        fadeIn(null);
    }

    public void fadeIn(Runnable callback) {
        mMotion = Motion.FADE_IN;
        mFadeCallback = callback;

        final int middle = getWidth() / 2;
        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            float from = middle - (view.getLeft() + view.getWidth() / 2);
            if (from != 0) {
                float current = Animatable.getCurrent(view, from);
                view.startAnimation(new Animatable.Horizontal(from, 0, current, 
                        Animatable.DURATION.MENU.ACTION.FADE_OUT));
            } else {
                view.clearAnimation();
            }
        }

        float current = Animatable.getCurrent(this, 0);
        startAnimation(new Animatable.Alpha(0, 1, current, 
                Animatable.DURATION.MENU.ACTION.FADE_OUT) {
            @Override
            protected void onEnd() {
                onFadeEnd();
            }
        });

        clearDisappearingChildren();
    }

    public void fadeOut() {
        fadeOut(null);
    }

    public void fadeOut(Runnable callback) {
        mMotion = Motion.FADE_OUT;
        mFadeCallback = callback;

        final int middle = getWidth() / 2;
        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            float to = middle - (view.getLeft() + view.getWidth() / 2);
            if (to != 0) {
                float current = Animatable.getCurrent(view, 0);
                view.startAnimation(new Animatable.Horizontal(0, to, current, 
                        Animatable.DURATION.MENU.ACTION.FADE_OUT));
            } else {
                view.clearAnimation();
            }
        }

        float current = Animatable.getCurrent(this, 1);
        startAnimation(new Animatable.Alpha(1, 0, current, 
                Animatable.DURATION.MENU.ACTION.FADE_OUT) {
            @Override
            protected void onEnd() {
                onFadeEnd();
            }
        });

        clearDisappearingChildren();
    }

    private void onFadeEnd() {
        mMotion = Motion.STANDARD;
        if (mFadeCallback != null) {
            mFadeCallback.run();
            mFadeCallback = null;
        }
    }

    public void hide() {
        mMotion = Motion.HIDEN;
        doFade(1);
    }

    public void fade(float percent) {
        if (mMotion == Motion.STANDARD) {
            doFade(percent);
        }
    }

    private void doFade(float percent) {
        final int middle = getWidth() / 2;

        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            float value = (middle - (view.getLeft() + view.getWidth() / 2)) * percent;
            view.startAnimation(new Animatable.Horizontal(value, value, value, 0));
        }

        float value = 1 - percent;
        startAnimation(new Animatable.Alpha(value, value, value, 0));
        clearDisappearingChildren();
    }

    public void reset() {
        mMotion = Motion.STANDARD;
        mFadeCallback = null;

        clearAllAnimation();
    }

    private void clearAllAnimation() {
        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            view.clearAnimation();
        }

        clearAnimation();
        clearDisappearingChildren();
    }

    @Override
    public boolean invokeItem(MenuItemImpl item) {
        // TODO Auto-generated method stub
        return mMenu.performItemAction(item, 0);
    }

}
