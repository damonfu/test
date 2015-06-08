
package com.baidu.themeanimation.element;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.themeanimation.util.Constants;

public class ButtonElement extends VisibleElement {
    private final static String TAG = "button";
    private List<VisibleElement> mNormalState;
    private List<VisibleElement> mPressedState;
    private ClickListener mClickListener;

    public interface ClickListener{
        public void onClick();
        public void onDobuleClick();
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_BUTTON.equals(tagName)
                || Constants.TAG_BUTTON_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new ButtonElement();
    }

    @Override
    public void addElement(Element element) {
        super.addElement(element);
        if (element instanceof StateElement) {
            int state = ((StateElement) element).getState();
            if (state == Constants.NORMAL_STATE) {
                setNormalState(element.getVisibleElements());
            } else if (state == Constants.PRESSED_STATE) {
                setPressedState(element.getVisibleElements());
            }
        }
    }

    public void setClickListener(ButtonElement.ClickListener listener){
        mClickListener = listener;
    }

    public List<VisibleElement> getNormalState() {
        return mNormalState;
    }

    public void setNormalState(List<VisibleElement> normalElements) {
        mNormalState = normalElements;
        if (mNormalState != null) {
            for (int i = 0; i < mNormalState.size(); i++) {
                mNormalState.get(i).setParentElement(this);
                mNormalState.get(i).addFlag(VisibleElement.RELATIVE_TO_PARENT);
            }
        }
    }

    public List<VisibleElement> getPressedState() {
        return mPressedState;
    }

    public void setPressedState(List<VisibleElement> pressedElements) {
        mPressedState = pressedElements;
        if (mPressedState != null) {
            for (int i = 0; i < mPressedState.size(); i++) {
                mPressedState.get(i).setParentElement(this);
                mPressedState.get(i).addFlag(VisibleElement.RELATIVE_TO_PARENT);
            }
        }
    }

    /*
     * In buttonElement, Triggers has a Trigger contains many command, 
     * and Trigger means only a Trigger for old version
     */
    public void invokeTrigger(String action, Context context) {
        execTrigger(action);

        if (mClickListener != null) {
            if (TriggerElement.ACTION_DOWN.equals(action)) {
                mClickListener.onClick();
            } else if (TriggerElement.ACTION_UP.equals(action)) {
                mClickListener.onClick();
            } else if (TriggerElement.ACTION_DOUBLE.equals(action)) {
                mClickListener.onDobuleClick();
            }
        }

//        if (TriggerElement.ACTION_DOUBLE.equals(action)) {
//            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
//        }
    }

    @Override
    public void startAnimations() {
        List<VisibleElement> elements = null;

        switch (mState) {
            case Constants.NORMAL_STATE:
                elements = mNormalState;
                break;

            case Constants.PRESSED_STATE:
                elements = mPressedState;
                break;

            default:
                break;
        }

        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).startAnimations();
            }
        }
    }

    private ButtonElementView mButtonElementView = null;

    public void clearView() {
        super.clearView();
        if (mButtonElementView != null) {
            ViewGroup viewGroup = (ViewGroup) mButtonElementView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mButtonElementView);
            }
            if (mNormalState != null) {
                for (int i = 0; i < mNormalState.size(); i++) {
                    mNormalState.get(i).clearView();
                }
            }
            if (mPressedState != null) {
                for (int i = 0; i < mPressedState.size(); i++) {
                    mNormalState.get(i).clearView();
                }
            }
            mButtonElementView.removeAllViews();
            // mView.clearAnimation();
            mButtonElementView = null;
        }
    }

    @Override
    public View generateView(Context context, Handler handler) {
        if (mButtonElementView == null) {
            mButtonElementView = new ButtonElementView(context, handler);
        }

        // Logger.v(TAG, "generate button view!");

        setView(mButtonElementView);
        return mButtonElementView;
    }

    private long mLastDownTime = -1;
    private final static long DOUBLE_CLICK_THREHOLD = 250;
    private int mState = -1;

    public class ButtonElementView extends RelativeLayout {
        Context mContext;
        Handler mHandler;

        public ButtonElementView(Context context, Handler handler) {
            super(context);

            mContext = context;
            mHandler = handler;

            setLayoutParams(genLayoutParams());

            setState(Constants.NORMAL_STATE);
        }

        private void setState(int state) {
            if (state == Constants.NORMAL_STATE || state == Constants.PRESSED_STATE) {
                // Logger.v(TAG, "setState " + state);
                if (state != mState) {
                    List<VisibleElement> elements = null;
                    switch (state) {
                        case Constants.NORMAL_STATE:
                            elements = mNormalState;
                            break;

                        case Constants.PRESSED_STATE:
                            elements = mPressedState;
                            break;

                        default:
                            break;
                    }

                    mState = state;

                    // stop the current animation
                    for (int i = 0; i < this.getChildCount(); i++) {
                        this.getChildAt(i).clearAnimation();
                    }

                    // remove all the view
                    this.removeAllViews();

                    // set the state view and start animation
                    if (elements != null) {
                        View view = null;
                        for (int i = 0; i < elements.size(); i++) {
                            view = elements.get(i).generateView(mContext, mHandler);
                            addView(view);
                            // Logger.v(TAG, "add view " + view);
                            startAnimations();
                        }
                    } else {
                        // this.setBackgroundColor(Color.argb(50, 111, 111,
                        // 111));
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            int action = event.getAction();
            Boolean result = false;
            // need check whether have touch down on the unlock start area
            // if not touch down on the unlock area, consume the down event
            if (action == MotionEvent.ACTION_DOWN) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - mLastDownTime > DOUBLE_CLICK_THREHOLD) {
                    invokeTrigger(TriggerElement.ACTION_DOWN, mContext);
                    result = false;
                } else {
                    invokeTrigger(TriggerElement.ACTION_DOUBLE, mContext);
                    if (!LockScreenElement.mIsInStartArea) {
                        result = true;
                    }
                }
                mLastDownTime = currentTime;
                setState(Constants.PRESSED_STATE);
            } else if (action == MotionEvent.ACTION_UP) {
                setState(Constants.NORMAL_STATE);
            }

            return result;
        }
    }
}
