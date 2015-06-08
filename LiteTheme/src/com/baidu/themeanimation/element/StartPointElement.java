package com.baidu.themeanimation.element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.XmlParserHelper;

public class StartPointElement extends VisibleElement {
    private int mX = 0;
    private int mY = 0;
    private int mW = 0;
    private int mH = 0;
    private List<VisibleElement> mNormalElements = null;
    private List<VisibleElement> mPressedElements = null;
    private List<VisibleElement> mReachedElements = null;

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_UNLOCKER_START_POINTER.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new StartPointElement();
    }

    @Override
    public void addElement(Element element) {
        super.addElement(element);
        if (element instanceof StateElement) {
            int state = ((StateElement) element).getState();
            if (state == Constants.NORMAL_STATE) {
                setNormalElements(element.getVisibleElements());
            } else if (state == Constants.PRESSED_STATE) {
                setPressedElements(element.getVisibleElements());
            } else if (state == Constants.REACHED_STATE) {
                setReachedElements(element.getVisibleElements());
            }
        }
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        // jianglin:scale
        x *= FileUtil.X_SCALE;

        this.mX = x;
        // Logger.v("startPoint", "setX=" + x);
    }

    public void setX(String x) {
        setX(Integer.valueOf(x));
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        // jianglin:scale
        y *= FileUtil.Y_SCALE;

        this.mY = y;
        // Logger.v("startPoint", "setY=" + y);
    }

    public void setY(String y) {
        setY(Integer.valueOf(y));
    }

    public int getW() {
        return mW;
    }

    private final int MIN_ENDPOINT_DIM = 35;

    public void setW(int w) {
        w *= FileUtil.X_SCALE;

        int tw = w;
        if (tw < MIN_ENDPOINT_DIM) {
            tw = MIN_ENDPOINT_DIM;
        }
        this.mW = tw;
    }

    public void setW(String w) {
        setW(Integer.valueOf(w));
    }

    public int getH() {
        return mH;
    }

    public void setH(int h) {
        // jianglin:scale
        h *= FileUtil.Y_SCALE;

        int th = h;
        if (th < MIN_ENDPOINT_DIM) {
            th = MIN_ENDPOINT_DIM;
        }
        this.mH = th;
    }

    public void setH(String h) {
        setH(Integer.valueOf(h));
    }

    private String mNormalSound;

    public void setNormalSound(String normalSound) {
        mNormalSound = normalSound;
    }

    public String getNormalSound() {
        return mNormalSound;
    }

    private String mPressedSound;

    public void setPressedSound(String pressedSound) {
        // Logger.v("audio", "setPressedSound:" + pressedSound);
        mPressedSound = pressedSound;
    }

    public String getPressedSound() {
        return mPressedSound;
    }

    private String mReachedSound;

    public void setReachedSound(String reachedSound) {
        mReachedSound = reachedSound;
    }

    public String getReachedSound() {
        return mReachedSound;
    }

    public List<VisibleElement> getNormalElements() {
        return mNormalElements;
    }

    public void setNormalElements(List<VisibleElement> normalElements) {
        this.mNormalElements = normalElements;
    }

    public List<VisibleElement> getPressedElements() {
        return mPressedElements;
    }

    public void setPressedElements(List<VisibleElement> pressedElements) {
        this.mPressedElements = pressedElements;
    }

    public List<VisibleElement> getReachedElements() {
        return mReachedElements;
    }

    public void setReachedElements(List<VisibleElement> reachedElements) {
        this.mReachedElements = reachedElements;
    }

    public void dispatchCategoryChange(int category) {
        VisibleElement element;
        int i;
        if (mNormalElements != null) {
            for (i = 0; i < mNormalElements.size(); i++) {
                element = mNormalElements.get(i);
                element.onCategoryChange(category);
                // Logger.v("category",
                // element+" set to category "+category);
            }
        }

        if (mPressedElements != null) {
            for (i = 0; i < mPressedElements.size(); i++) {
                element = mPressedElements.get(i);
                element.onCategoryChange(category);
            }
        }

        if (mReachedElements != null) {
            for (i = 0; i < mReachedElements.size(); i++) {
                element = mReachedElements.get(i);
                element.onCategoryChange(category);
            }
        }
    }

    public void onTimeTick(Time time) {
        VisibleElement element;
        int i;
        if (mNormalElements != null) {
            for (i = 0; i < mNormalElements.size(); i++) {
                element = mNormalElements.get(i);
                element.onTimeTick(time);
            }
        }

        if (mPressedElements != null) {
            for (i = 0; i < mPressedElements.size(); i++) {
                element = mPressedElements.get(i);
                element.onTimeTick(time);
            }
        }

        if (mReachedElements != null) {
            for (i = 0; i < mReachedElements.size(); i++) {
                element = mReachedElements.get(i);
                element.onTimeTick(time);
            }
        }
    }

    private RelativeLayout mStartPointView = null;
    private int mState = -1;

    public void bringToFront() {
        if (mStartPointView != null) {
            mStartPointView.bringToFront();
        }
    }

    public void clearView() {
        if (mStartPointView != null) {
            ViewGroup viewGroup = (ViewGroup) mStartPointView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mStartPointView);
            }
            for (int i = 0; i < mStartPointView.getChildCount(); i++) {
                mStartPointView.getChildAt(i).clearAnimation();
            }

            mStartPointView.removeAllViews();
            mStartPointView = null;
        }
        VisibleElement element;
        if (mNormalElements != null) {
            for (int i = 0; i < mNormalElements.size(); i++) {
                element = mNormalElements.get(i);
                element.clearView();
                // Logger.v("category",
                // element+" set to category "+category);
            }
        }

        if (mPressedElements != null) {
            for (int i = 0; i < mPressedElements.size(); i++) {
                element = mPressedElements.get(i);
                element.clearView();
            }
        }

        if (mReachedElements != null) {
            for (int i = 0; i < mReachedElements.size(); i++) {
                element = mReachedElements.get(i);
                element.clearView();
            }
        }
    }

    public View getView(Context context, int state, Handler handler) {
        if (mStartPointView == null) {
            mStartPointView = new RelativeLayout(context);
            mStartPointView.setLayoutParams(new LayoutParams(
                    FileUtil.REAL_SCREEN_WIDTH, FileUtil.REAL_SCREEN_HEIGHT));
            // for case: one view in start point x coordinate is < 0 and
            // can't display in the screen, but it translate animation can
            // make the view in the screen
            // but the result is the start point which contain the view's
            // dimension is zero, so it can't be displayed
        }

        List<VisibleElement> elements = null;
        switch (state) {
            case Constants.NORMAL_STATE:
                elements = mNormalElements;
                // when back to normal state, the point view return the
                // original position
                mDeltaX = 0;
                mDeltaY = 0;
                break;

            case Constants.PRESSED_STATE:
                elements = mPressedElements;
                if (elements == null && mNormalElements != null) {
                    elements = mNormalElements;
                }
                break;

            case Constants.REACHED_STATE:
                elements = mReachedElements;
                break;

            default:
                break;
        }

        if (true) {// (elements != null || state == Constants.NORMAL_STATE){
            for (int i = 0; i < mStartPointView.getChildCount(); i++) {
                mStartPointView.getChildAt(i).clearAnimation();
            }

            mStartPointView.removeAllViews();
        }

        mState = state;

        if (elements != null) {
            View view = null;
            int category = ExpressionManager.getInstance().getCategoryValue();
            for (int i = 0; i < elements.size(); i++) {
                view = elements.get(i).generateView(context, handler);
                elements.get(i).onCategoryChange(category);
                mStartPointView.addView(view);
            }
        }

        moveBy(mDeltaX, mDeltaY);

        mStartPointView.requestLayout();

        return mStartPointView;
    }

    public void startAnimations(int state) {
        List<VisibleElement> elements = null;
        switch (state) {
            case Constants.NORMAL_STATE:
                elements = mNormalElements;
                break;

            case Constants.PRESSED_STATE:
                elements = mPressedElements;
                break;

            case Constants.REACHED_STATE:
                elements = mReachedElements;
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

    public void reset() {
        // reset all the start point view to initial state
        List<List<VisibleElement>> elements = new ArrayList<List<VisibleElement>>();
        elements.add(mNormalElements);
        elements.add(mPressedElements);
        elements.add(mReachedElements);

        VisibleElement element;

        List<VisibleElement> baseElements;
        for (int i = 0; i < elements.size(); i++) {
            baseElements = elements.get(i);

            if (baseElements != null) {
                for (int j = 0; j < baseElements.size(); j++) {
                    element = baseElements.get(j);

                    element.updateView();
                }
            }
        }
    }

    // move the start point view in x/y coordinate by deltaX/deltaY
    public void moveBy(int deltaX, int deltaY, int state) {
        View view;

        List<VisibleElement> elements = null;
        switch (state) {
            case Constants.NORMAL_STATE:
                elements = mNormalElements;
                break;

            case Constants.PRESSED_STATE:
                if (mPressedElements == null) {
                    elements = mNormalElements;
                } else {
                    elements = mPressedElements;
                }

                break;

            case Constants.REACHED_STATE:
                if (mReachedElements != null) {
                    elements = mReachedElements;
                } else {
                    if (mPressedElements != null) {
                        elements = mPressedElements;
                    } else {
                        elements = mNormalElements;
                    }
                }
                break;

            default:
                break;
        }

        VisibleElement element;
        LayoutParams layoutParams = null;
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                element = elements.get(i);
                view = element.getView();
                layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.leftMargin = element.getX() + deltaX;
                    layoutParams.topMargin = element.getY() + deltaY;

                    // Logger.v("end",
                    // "top="+layoutParams.topMargin+", left="+layoutParams.leftMargin);

                    if (element.getAlign() == ALIGN_CENTER) {
                        layoutParams.leftMargin -= layoutParams.width / 2;
                    } else if (element.getAlign() == ALIGN_RIGHT) {
                        layoutParams.leftMargin -= layoutParams.width;
                    }

                    if (element.getAlignV() == ALIGN_CENTER) {
                        layoutParams.topMargin -= layoutParams.height / 2;
                    } else if (element.getAlignV() == ALIGN_BOTTOM) {
                        layoutParams.topMargin -= layoutParams.height;
                    }
                }
                view.requestLayout();
            }
        }
    }

    // the distance in x and y coordinate direction from current position to
    // the original position
    private int mDeltaX;
    private int mDeltaY;

    public void moveBy(int deltaX, int deltaY) {
        mDeltaX = deltaX;
        mDeltaY = deltaY;
        moveBy(deltaX, deltaY, mState);
    }

    public static class EndPointElement extends StartPointElement {
        private IntentWrapper mIntentWrapper;
        private LockPathElement mLockPathElement;

        @Override
        public boolean matchTag(String tagName) {
            return Constants.TAG_UNLOCKER_END_POINTER.equals(tagName);
        }

        @Override
        public Element createElement(String tagName) {
            return new EndPointElement();
        }

        @Override
        public void addElement(Element element) {
            super.addElement(element);
            if (element instanceof IntentWrapper) {
                setIntent((IntentWrapper) element);
            } else if (element instanceof LockPathElement) {
                setLockPath((LockPathElement) element);
            }
        }

        @Override
        public Element parseChild(XmlPullParser parser) throws Exception {
            String tagName = parser.getName();
            if (Constants.TAG_UNLOCKER_INTENT.equals(tagName)) {
                IntentWrapper intentWrapper = null;
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        intentWrapper = new IntentWrapper();
                        XmlParserHelper.setElementAttr(parser, intentWrapper);
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (parser.getName().equals(tagName)) {
                            break;
                        }
                    }
                    eventType = parser.next();
                }
                return intentWrapper;
            } else {
                return super.parseChild(parser);
            }
        }

        public Intent getIntent() {
            Intent intent = null;

            if (mIntentWrapper != null) {
                intent = mIntentWrapper.getIntent();
            }

            return intent;
        }

        public void setIntent(IntentWrapper intentWrapper) {
            mIntentWrapper = intentWrapper;
        }

        public void setLockPath(LockPathElement path) {
            mLockPathElement = path;
        }

        public LockPathElement getLockPath() {
            return mLockPathElement;
        }
    }

    public static class IntentWrapper extends BottomElement {
        private Intent mIntent;
        private String mPackageName;
        private String mClass;

        public IntentWrapper() {
            mIntent = new Intent();
        }

        public void setAction(String action) {
            mIntent.setAction(action);
        }

        public void setType(String type) {
            mIntent.setType(type);
        }

        public void setCategory(String category) {
            mIntent.addCategory(category);
        }

        public void setUri(String uri) {
            try {
                mIntent.setData(Uri.parse(String.format(Constants.SEARCH_BAIDU_WEB,
                        URLEncoder.encode(uri, "gb2312"))));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public void setPackage(String packageName) {
            mPackageName = packageName;
        }

        public void setClass(String className) {
            mClass = className;
        }

        public Intent getIntent() {
            if (mPackageName != null && mClass != null) {
                ComponentName componentName = new ComponentName(mPackageName, mClass);
                mIntent.setComponent(componentName);
            }

            return mIntent;
        }
    }
}
