
package com.baidu.themeanimation.element;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.themeanimation.element.StartPointElement.EndPointElement;
import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.LockScreenHandler;
import com.baidu.themeanimation.util.Logger;

public class UnlockerElement extends VisibleElement {
    private static List<UnlockerElement> mUnlockerElements = null;

    public static void addUnlockerElement(UnlockerElement unlockerElement) {
        if (mUnlockerElements == null) {
            mUnlockerElements = new ArrayList<UnlockerElement>();
        }
        mUnlockerElements.add(unlockerElement);
    }

    /*
     * some one unlock element state has changed, dispatch this information to
     * other unlock other unlock element can do some specify action, for
     * example, if there are 3 unlock element A, B C if A become press state,
     * then B and C will be invisible, when A become normal state back, then B
     * and C become visible again
     */
    public static void dispatchStateChange(UnlockerElement unlockerElement, int state) {
        if (unlockerElement != null && !unlockerElement.getStandAloneMode()) {
            boolean visible = state != Constants.PRESSED_STATE;
            for (int i = 0; i < mUnlockerElements.size(); i++) {
                UnlockerElement element = mUnlockerElements.get(i);
                if (element != unlockerElement && !element.getStandAloneMode()
                        && !element.getAlwaysShow()) {
                    element.changeVisibility(visible);
                    element.clearAnimations();
                }
            }
        }
    }

    public void releaseUnlockerElement() {
        if (null != mUnlockerElements) {
            for (int i = 0; i < mUnlockerElements.size(); i++) {
                if (mUnlockerElements.get(i) != mUnlockerElements) {
                    mUnlockerElements.get(i).releaseAttributAnimation();
                }
            }
            mUnlockerElements.clear();
        }
    }

    private boolean mStandAloneMode = false;
    private StartPointElement mStartPoint;
    private List<EndPointElement> mEndPoints = new ArrayList<EndPointElement>();

    public UnlockerElement() {
        super();
        UnlockerElement.addUnlockerElement(this);
    }

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_UNLOCKER.equals(tagName)
                || Constants.TAG_UNLOCKER_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new UnlockerElement();
    }

    @Override
    public void addElement(Element element) {
        super.addElement(element);
        if (element instanceof EndPointElement) {
            addEndPoint((EndPointElement) element);
        } else if (element instanceof StartPointElement) {
            setStartPoint((StartPointElement) element);
        }
    }

    /*
     * if a unlock is in standalone mode, then when press on it, the other
     * unlock element will not hide, otherwise the others unlock elements will
     * be hide
     */
    public boolean getStandAloneMode() {
        return mStandAloneMode;
    }

    public void setStandAloneMode(boolean mode) {
        mStandAloneMode = mode;
    }

    public void setStandAloneMode(String mode) {
        mStandAloneMode = Boolean.valueOf(mode);
    }

    public void setBounceInitSpeed(String bounceInitSpeed) {
    }

    public void setBounceAcceleration(String bounceInitSpeed) {

    }

    private boolean hasChanged = false;

    public void changeVisibility(boolean visible) {
        if (!visible && getVisibility()) {
            hasChanged = true;
            setVisibility(false);
            return;
        }
        if (visible && hasChanged) {
            hasChanged = false;
            setVisibility(true);
        }
    }

    public StartPointElement getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(StartPointElement startPoint) {
        if (startPoint != null) {
            this.mStartPoint = startPoint;
        }
    }

    public void addEndPoint(EndPointElement endpoint) {
        if (endpoint != null) {
            mEndPoints.add(endpoint);
        }
    }

    public List<EndPointElement> getEndPoints() {
        return mEndPoints;
    }

    public EndPointElement getEndPoint(int index) {
        if (index >= 0 && index < mEndPoints.size()) {
            return mEndPoints.get(index);
        }
        return null;
    }

    /*
     * check whether the point is in the start pointer area
     */
    public Boolean inStartPoint(float posX, float posY) {
        Boolean result = false;
        StartPointElement startPoint = getStartPoint();
        if (startPoint != null) {
            if (posX >= startPoint.getX() &&
                    posX < startPoint.getX() + startPoint.getW() &&
                    posY >= startPoint.getY() &&
                    posY < startPoint.getY() + startPoint.getH()) {
                result = true;
            }
        }

        // Logger.v("startPoint", "===============================");
        // Logger.v("startPoint", "posX=" + posX + ", posY=" + posY +
        // ", result=" + result);
        // Logger.v("startPoint", "x=" + startPoint.getX() + ", y=" +
        // startPoint.getY() + ", w="
        // + startPoint.getW() + ", h=" + startPoint.getH());

        return result;
    }

    /*
     * check whether the point is in the end pointer area return the index of
     * the end pointer which touch point is in its area posX, posY is the touch
     * point position, preIndex means the index of the previous end point we
     * enter
     */
    public int inEndPoint(float posX, float posY) {
        int index = -1;

        List<EndPointElement> endPoints = getEndPoints();
        if (endPoints != null) {
            EndPointElement endPoint;
            // Logger.v("end", "===============posX=" + posX + ", posY=" +
            // posY);
            for (int i = 0; i < endPoints.size(); i++) {
                endPoint = endPoints.get(i);
                // Logger.v("end", "x=" + endPoint.getX() + ", y=" +
                // endPoint.getY() + ", w="
                // + endPoint.getW() + ", h=" + endPoint.getH());
                // Logger.v("end", "getStartPoint().x=" + getStartPoint().getX()
                // + ", getStartPoint().y=" + getStartPoint().getY());
                if (posX >= (endPoint.getX() - 2) &&
                        posX <= endPoint.getX() + endPoint.getW() &&
                        posY >= (endPoint.getY() - 2) &&
                        posY <= endPoint.getY() + endPoint.getH()) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    @Override
    protected void onCategoryChange(int category) {
        // Logger.v("category",
        // "unlock element dispatch category change "+category);
        getStartPoint().dispatchCategoryChange(category);

        List<EndPointElement> endPoints = getEndPoints();
        // Logger.v("category", "the number of endpoints="+endPoints.size());
        for (int i = 0; i < endPoints.size() - 1; i++) {
            endPoints.get(i).dispatchCategoryChange(category);
        }
    }

    @Override
    public void onTimeTick(Time time) {
        getStartPoint().onTimeTick(time);

        List<EndPointElement> endPoints = getEndPoints();
        for (int i = 0; i < endPoints.size() - 1; i++) {
            endPoints.get(i).onTimeTick(time);
        }
    }

    // deltaPoint is the delta distance in x/y direction
    public void updateAttributes(Point downEvent, Point deltaPoint, int state) {
        /*
         * update the value of the unlock, then update others elements which
         * relate to it move_x move_y actual_x actual_y move_dist state
         */
        float move_x = 0;
        float move_y = 0;
        float actual_x = 0;
        float actual_y = 0;
        ExpressionManager expressionManager = ExpressionManager.getInstance();

        if (deltaPoint != null) {
            move_x = deltaPoint.x / FileUtil.X_SCALE;
            move_y = deltaPoint.y / FileUtil.Y_SCALE;

            // in expression (#screen_width - 120 *#unlock.move_x), current
            // algorithm will multiply the scale radio
            // but the #unlock.move_x should not be multiply scale radio, so in
            // this place, divide the scale radio first to make sure the result
            // is right
        }

        // actual_x actual_y are the start pointer's position
        actual_x = move_x + getStartPoint().getX();

        actual_y = move_y + getStartPoint().getY();
        // Logger.v("set", "=====start update unlock attributes==========");
        // Logger.v("end", getName()+", move_x="+move_x+", move_y="+move_y);
        expressionManager.setVariableValue(getName() + ".move_x", (int) move_x);
        expressionManager.setVariableValue(getName() + ".move_y", (int) move_y);
        expressionManager.setVariableValue(getName() + ".actual_x", (int) actual_x);
        expressionManager.setVariableValue(getName() + ".actual_y", (int) actual_y);

        // move_dist
        double move_dist = android.util.FloatMath.sqrt(move_x * move_x + move_y * move_y);
        expressionManager.setVariableValue(getName() + ".move_dist", (int) move_dist);

        if (state == 0 || state == 1 || state == 2) {
            // 0:normal, 1:pressed, 2:reached
            expressionManager.setVariableValue(getName() + ".state", state);
        }
    }

    private UnlockerElementView mUnlockerElementView;

    public void clearView() {
        super.clearView();
        if (mUnlockerElementView != null) {
            ViewGroup viewGroup = (ViewGroup) mUnlockerElementView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mUnlockerElementView);
            }
            // mView.clearAnimation();
            mUnlockerElementView = null;
            if (mStartPoint != null) {
                mStartPoint.clearView();
            }
        }
        if (mEndPoints != null) {
            for (int i = 0; i < mEndPoints.size(); i++) {
                mEndPoints.get(i).clearView();
            }
        }
    }

    @Override
    public View generateView(Context context, Handler handler) {
        if (mUnlockerElementView == null) {
            mUnlockerElementView = new UnlockerElementView(context, this, handler);
        }

        setView(mUnlockerElementView);
        updateAttributes(null, null, Constants.NORMAL_STATE);
        return mUnlockerElementView;
    }

    /*
     * reset the Unlock element's parameters to original value, for when touch
     * up if it is reached to the end point, we will not change it value to
     * normal state
     */
    public void reset() {
        if (mUnlockerElementView != null) {
            mUnlockerElementView.resetState();
        }
    }

    private int mTouchState;

    public class UnlockerElementView extends RelativeLayout {
        private final static String TAG = "UnlockerElementView";

        private UnlockerElement mUnlockerElement;
        private Context mContext;
        private LockScreenHandler mHandler;
        private Vibrator mVibrator;

        public UnlockerElementView(Context context, UnlockerElement unlockerElement, Handler handler) {
            super(context);

            mUnlockerElement = unlockerElement;
            mTouchState = Constants.NONE_STATE;
            mContext = context;
            mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            mHandler = (LockScreenHandler) handler;

            // preview mode, lock screen is not full screen
            setLayoutParams(new LayoutParams(FileUtil.REAL_SCREEN_WIDTH,
                    FileUtil.REAL_SCREEN_HEIGHT));
            // TODO current all the unlock element is full screen size
            setStateView(Constants.NORMAL_STATE, -1);// -1 means all the end
                                                     // point should change to
                                                     // normal state
        }

        private void setStateView(int state, int endPointIndex) {
            setStateViewInternal(state, mUnlockerElement.getStartPoint());

            // add end point views
            List<EndPointElement> endPoints = mUnlockerElement.getEndPoints();
            // Logger.v("state1",
            // "=====count of end points="+endPoints.size()+", state="+state);
            if (endPointIndex >= 0 && endPointIndex < endPoints.size()) {
                // Logger.v("state2",
                // "set end point "+endPointIndex+" to reached state");
                setStateViewInternal(state, endPoints.get(endPointIndex));

                // for case, change from reached state to other reached state
                // the other not in reached state end point should be in normal
                // state
                if (state == Constants.REACHED_STATE) {
                    for (int i = 0; i < endPoints.size(); i++) {
                        if (i != endPointIndex) {
                            setStateViewInternal(Constants.PRESSED_STATE, endPoints.get(i));
                        }
                    }
                }
            } else {
                for (int i = 0; i < endPoints.size(); i++) {
                    // Logger.v("state1",
                    // "=====set "+endPoints.get(i)+" to state "+state);
                    setStateViewInternal(state, endPoints.get(i));
                }
            }

            mUnlockerElement.getStartPoint().bringToFront();// make sure the
                                                            // start point view
                                                            // at the top level

            mTouchState = state;
        }

        private View setStateViewInternal(int state, StartPointElement startPoint) {
            // check whether need to change view
            View view = null;
            if ((mTouchState != state && state >= Constants.NORMAL_STATE && state <= Constants.REACHED_STATE)
                    ||
                    (state == Constants.REACHED_STATE && mTouchState == Constants.REACHED_STATE)) {
                Boolean needChangeViewBoolean = true;
                switch (state) {
                    case Constants.PRESSED_STATE:
                        if (startPoint.getPressedElements() == null
                                && mTouchState == Constants.NORMAL_STATE) {
                            needChangeViewBoolean = false;
                        }

                        if (mTouchState == Constants.REACHED_STATE
                                && startPoint.getReachedElements() == null) {
                            needChangeViewBoolean = false;
                        }
                        break;

                    case Constants.REACHED_STATE:
                        if (startPoint.getReachedElements() == null) {
                            needChangeViewBoolean = false;
                        }
                        break;

                    default:
                        break;
                }

                // Logger.v("back",
                // "prestate="+mTouchState+", to state="+state+", need change="+needChangeViewBoolean+", startPoint="+startPoint+", startPoint.x="+startPoint.getY());

                if (needChangeViewBoolean) {
                    // change the start point to state view
                    if (startPoint != null) {
                        view = startPoint.getView(mContext, mTouchState, mHandler);
                        if (view != null) {
                            removeView(view);
                        }

                        view = startPoint.getView(mContext, state, mHandler);
                        addView(view);

                        startPoint.startAnimations(state);
                    }
                }
            }

            return view;
        }

        private MediaPlayer mp;// = new MediaPlayer();

        public void setTouchState(int state, int endPointIndex) {
            // Logger.v("state3", "setTouchState:"+state+", pre="+mTouchState);
            // play sound, get the sound file form cache directory
            String soundDirString = FileUtil.getInstance().getLockScreenFilePath() + File.separator;
            String soundFileName = null;
            switch (state) {
                case Constants.NORMAL_STATE:
                    soundFileName = mStartPoint.getNormalSound();
                    break;

                case Constants.PRESSED_STATE:
                    soundFileName = mStartPoint.getPressedSound();
                    break;

                case Constants.REACHED_STATE:
                    soundFileName = mStartPoint.getReachedSound();
                    break;

            }

            if (soundFileName != null
            /*
             * && (Settings.System.getInt(mContext.getContentResolver(),
             * Settings.System.LOCKSCREEN_SOUNDS_ENABLED, 1) == 1)
             */) {
                try {
                    // Logger.v("audio", "try to play sound " + soundDirString +
                    // soundFileName);
                    File soundFile = new File(soundDirString + soundFileName);
                    // Logger.v("audio", "try to play sound " + soundDirString +
                    // soundFileName
                    // + ", exsit=" + soundFile.exists());
                    FileInputStream fis = new FileInputStream(soundFile);
                    if (mp != null) {
                        mp.release();
                        mp = null;
                    }

                    if (mp == null) {
                        mp = new MediaPlayer();
                    }

                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                    mp.start();
                    mp.setOnCompletionListener(new OnCompletionListener() {
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            // Logger.v("audio", "sound player end!");
                            mediaPlayer.release();
                            mp = null;
                        }
                    });
                } catch (Exception e) {
                    Logger.w("audio", "play sound exception:" + e.toString());
                    if (mp != null) {
                        mp.release();
                    }
                }
            }

            // it exists a situation that may from reached state to reached
            // state
            if ((mTouchState != state && state >= Constants.NORMAL_STATE && state <= Constants.REACHED_STATE)
                    ||
                    (mTouchState == Constants.REACHED_STATE && state == Constants.REACHED_STATE)) {
                if (!(state == Constants.PRESSED_STATE && mTouchState == Constants.REACHED_STATE)
                        && (state != Constants.NORMAL_STATE)
                        && (FileUtil.getInstance().isTactileFeedbackEnabled())) {
                    mVibrator.vibrate(50);
                }
                setStateView(state, endPointIndex);
            }
        }

        private void resetState() {
            UnlockerElement.dispatchStateChange(mUnlockerElement, Constants.NORMAL_STATE);
            updateAttributes(null, null, Constants.NORMAL_STATE);// this line
                                                                 // must before
                                                                 // setTouchState,
                                                                 // for some
                                                                 // element will
                                                                 // use the
                                                                 // attribute
                                                                 // value to
                                                                 // update
            setTouchState(Constants.NORMAL_STATE, -1);
            getStartPoint().reset();
        }

        private float preDownPosX;
        private float preDownPosY;
        private Point mDownPoint = new Point();// current down position

        public double getRadio(int startX, int startY, int endX, int endY) {
            double radio = 0;

            int x = endX - startX;
            int y = endY - startY;

            double angle;

            if (x > 0) {
                radio = Math.PI / 2;
                angle = Math.atan2(Math.abs(y), Math.abs(x));
                if (y > 0) {
                    radio -= angle;
                } else if (y < 0) {
                    radio += angle;
                }
            } else if (x < 0) {
                radio = 3 * Math.PI / 2;
                angle = Math.atan2(Math.abs(y), Math.abs(x));
                if (y > 0) {
                    radio += angle;
                } else if (y < 0) {
                    radio -= angle;
                }
            } else {
                if (y > 0) {
                    radio = 0;
                } else if (y < 0) {
                    radio = Math.PI;
                } else {
                    radio = Double.MAX_VALUE;
                }
            }

            return radio;
        }

        private Point getValidDeltaXY(MotionEvent event) {
            Point point = new Point(0, 0);
            int indexOfEndPoint = 0;
            double minRadio = Math.PI * 4;
            double currRadio;
            int x3 = (int) event.getX();
            int y3 = (int) event.getY();
            List<EndPointElement> endPoints = getEndPoints();

            double moveRadio = getRadio(x3, y3, (int) preDownPosX, (int) preDownPosY);

            // 1. check which end point is nearest
            LockPathElement lockPath = null;
            for (int i = 0; i < endPoints.size(); i++) {
                lockPath = endPoints.get(i).getLockPath();
                if (lockPath != null) {
                    currRadio = getRadio(lockPath.getEndPoint().x, lockPath.getEndPoint().y,
                            lockPath.getStartPoint().x, lockPath.getStartPoint().y);
                    if (Math.abs(currRadio - moveRadio) < minRadio) {
                        minRadio = Math.abs(currRadio - moveRadio);
                        indexOfEndPoint = i;
                    }
                }
            }

            // 2. calculate the deltaX, deltaY
            int tempDeltaX0 = 0;
            int tempDeltaX1 = 0;
            int tempDeltaY0 = 0;
            int tempDeltaY1 = 0;

            StartPointElement startPoint = getStartPoint();
            EndPointElement endPoint = endPoints.get(indexOfEndPoint);
            lockPath = endPoint.getLockPath();
            if (lockPath != null) {
                tempDeltaX0 = endPoint.getLockPath().getMinX() - startPoint.getX();
                tempDeltaX1 = endPoint.getLockPath().getMaxX() - startPoint.getX();
                tempDeltaY0 = endPoint.getLockPath().getMinY() - startPoint.getY();
                tempDeltaY1 = endPoint.getLockPath().getMaxY() - startPoint.getY();

                if (endPoint.getLockPath().getMaxX() == endPoint.getLockPath().getMinX()) {
                    tempDeltaX0 = 0;
                    tempDeltaX1 = 0;
                }

                if (endPoint.getLockPath().getMaxY() == endPoint.getLockPath().getMinY()) {
                    tempDeltaY0 = 0;
                    tempDeltaY1 = 0;
                }
            } else {
                tempDeltaX0 = -480; // TODO: there should be the screen size
                tempDeltaX1 = 480;
                tempDeltaY0 = -800;
                tempDeltaY1 = 800;
            }

            // X0, Y0 contain the smaller number
            if (tempDeltaX0 > tempDeltaX1) {
                tempDeltaX0 = tempDeltaX0 + tempDeltaX1;
                tempDeltaX1 = tempDeltaX0 - tempDeltaX1;
                tempDeltaX0 = tempDeltaX0 - tempDeltaX1;
            }

            if (tempDeltaY0 > tempDeltaY1) {
                tempDeltaY0 = tempDeltaY0 + tempDeltaY1;
                tempDeltaY1 = tempDeltaY0 - tempDeltaY1;
                tempDeltaY0 = tempDeltaY0 - tempDeltaY1;
            }

            int deltaX = (int) (x3 - preDownPosX);
            int deltaY = (int) (y3 - preDownPosY);

            // Logger.v("end", "tempDX0="+tempDeltaX0+", tempDX1="+tempDeltaX1);
            // Logger.v("end", "tempDY0="+tempDeltaY0+", tempDY1="+tempDeltaY1);
            // Logger.v("end", "deltaX="+deltaX+", deltaY="+deltaY);

            if (deltaX < tempDeltaX0) {
                deltaX = tempDeltaX0;
            } else if (deltaX > tempDeltaX1) {
                deltaX = tempDeltaX1;
            }

            if (deltaY < tempDeltaY0) {
                deltaY = tempDeltaY0;
            } else if (deltaY > tempDeltaY1) {
                deltaY = tempDeltaY1;
            }

            // 3. check whether bigger than tolerance

            // 4. adjust the deltaX, deltaY

            point.x = deltaX;
            point.y = deltaY;
            return point;
        }

        private int mReachedIndex = -1;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Boolean result = false;
            float posX = event.getX();
            float posY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // debug_test_for_expression();

                    // check whether in start pointer
                    // Logger.v(
                    // TAG,
                    // this + ":Touch Event: type=" + event.getAction() + ", x="
                    // + event.getX() + ", y=" + event.getY());
                    mDownPoint.set((int) posX, (int) posY);

                    if (mUnlockerElement.inStartPoint(posX, posY)) {
                        // change current unlock element to press state, set
                        // other unlock element to invisible state
                        // Logger.v("end", "in start point:" + posX + ", " +
                        // posY);
                        setTouchState(Constants.PRESSED_STATE, -1);
                        UnlockerElement.dispatchStateChange(mUnlockerElement,
                                Constants.PRESSED_STATE);
                        updateAttributes(mDownPoint, null, Constants.PRESSED_STATE);

                        result = true;
                    } else {
                        // not press on start pointer, do nothing
                        // Logger.v(TAG, "not in start point:" + posX + ", " +
                        // posY);
                        result = false;
                    }

                    preDownPosX = posX;
                    preDownPosY = posY;

                    mReachedIndex = -1;
                    break;

                case MotionEvent.ACTION_MOVE:
                    // enter press and move stage, change the position of the
                    // unlock element
                    Point point = getValidDeltaXY(event);

                    getStartPoint().moveBy(point.x, point.y);// TODO

                    invalidate(0, 0, 480, 800);

                    // check whether move into end point area
                    // int currReachedIndex =
                    // mUnlockerElement.inEndPoint(getStartPoint().getX() +
                    // point.x*FileUtil.getInstance().X_SCALE,
                    // getStartPoint().getY() +
                    // point.y*FileUtil.getInstance().Y_SCALE);
                    int currReachedIndex = mUnlockerElement.inEndPoint(getStartPoint().getX()
                            + point.x, getStartPoint().getY() + point.y);

                    if (currReachedIndex < 0) {
                        // not move into end point area, if in reach state,
                        // change to press state
                        if (mTouchState == Constants.REACHED_STATE) {
                            // Logger.v("back",
                            // "deltaX="+point.x+", deltaY="+point.y);
                            // Logger.v("back", "back to press state");
                            setTouchState(Constants.PRESSED_STATE, -1);
                        }
                    } else {
                        if (mTouchState == Constants.PRESSED_STATE
                                || mTouchState == Constants.REACHED_STATE) {
                            // Logger.v("reach", "enter to reach state " +
                            // currReachedIndex);
                            if (mReachedIndex != currReachedIndex) {
                                setTouchState(Constants.REACHED_STATE, currReachedIndex);
                            }
                        }
                    }
                    mReachedIndex = currReachedIndex;

                    // adjust the touch point
                    updateAttributes(mDownPoint, point, mTouchState);
                    break;

                case MotionEvent.ACTION_UP:

                    if (mTouchState != Constants.REACHED_STATE) {
                        UnlockerElement.dispatchStateChange(mUnlockerElement,
                                Constants.NORMAL_STATE);
                        resetState();
                    } else {
                        try {
                            Intent intent;
                            EndPointElement endPoint = mUnlockerElement.getEndPoint(mReachedIndex);
                            if (endPoint != null && endPoint.getIntent() != null) {
                                intent = endPoint.getIntent();
                                intent.putExtra(Constants.JUST_UNLOCK, false);
                            } else {
                                intent = new Intent();
                                intent.putExtra(Constants.JUST_UNLOCK, true);
                            }

                            mHandler.startIntent(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mReachedIndex = -1;
                    break;

                case MotionEvent.ACTION_CANCEL:
                    resetState();
                    break;

                default:
                    break;
            }

            return result;
        }
    }

    @Override
    public void startAnimations() {
        getStartPoint().startAnimations(mTouchState);

        List<EndPointElement> endPoints = getEndPoints();
        if (endPoints != null) {
            EndPointElement endPoint = null;
            for (int i = 0; i < endPoints.size(); i++) {
                endPoint = endPoints.get(i);
                endPoint.startAnimations(mTouchState);
            }
        }
    }
}
