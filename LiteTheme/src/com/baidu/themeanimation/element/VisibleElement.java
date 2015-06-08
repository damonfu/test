
package com.baidu.themeanimation.element;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.RelativeLayout.LayoutParams;
import android.os.Handler;

import com.baidu.themeanimation.element.PositionAnimationElement.LockTranslateAnimationListener;
import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Utils;
import com.baidu.themeanimation.view.AnimationViewFactory;

public abstract class VisibleElement extends Element implements LockTranslateAnimationListener {
    private static final String TAG = "BaseElement";

    public final static int ALIGN_LEFT   = 0;
    public final static int ALIGN_CENTER = 1;
    public final static int ALIGN_RIGHT  = 2;
    public final static int ALIGN_TOP    = 3;
    public final static int ALIGN_BOTTOM = 4;

    private int mX = 0;
    private int mY = 0;
    private int mWidth = FileUtil.REAL_SCREEN_WIDTH;
    private int mHeight = FileUtil.REAL_SCREEN_HEIGHT;
    private boolean mHasX = false;
    private boolean mHasY = false;
    private boolean mHasW = false;
    private boolean mHasH = false;
    private int mCenterX = 0;
    private int mCenterY = 0;
    private int mAngle = 0;
    private int mAngleY = 0;
    private int mAlpha;
    private int mCategory;
    public Boolean mVisibility;
    private int mAlign = ALIGN_LEFT;
    private int mAlignV = ALIGN_TOP;
    private boolean mAwalysShow;
    public AnimatorParser mAnimatorParser;

    public Paint mPaint;// just for internal use

    // for rotate issue, if some call setAngle to rotate custom view, use this
    // animation to set the view's angle
    // caused by if rotate the image or bitmap in the view's canvas, it will
    // exceed the view's bounds
    private class AttributAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (mAngle != 0) {
                t.getMatrix().setRotate(mAngle, getCenterX(), getCenterY());
            }
        }
    }

    private AttributAnimation mAttributAnimation = null;

    public void releaseAttributAnimation() {
        if (null != mAttributAnimation) {
            mAttributAnimation = null;
        }
    }

    public VisibleElement() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        setAngle(0);
        setAlpha(255);
        setCategory(Constants.ALWAYS_DISPLAY);
        setVisibility(true);
        //setName(null);
        mAnimatorParser = new AnimatorParser();
        // mAttributAnimation = new AttributAnimation();
        // mAttributAnimation.setDuration(0);
        // mAttributAnimation.setRepeatCount(Animation.INFINITE);
    }
    
    public boolean hasView() {
        return true;
    }

    public int getX() {
        return mX;
    }

    public void setX(int posX) {
        mHasX = true;

        posX *= FileUtil.X_SCALE;

        if ((mFlag & RELATIVE_TO_PARENT) == RELATIVE_TO_PARENT) {
            if (mParentElement != null) {
                posX -= mParentElement.getX();
            }
        }

        if (this.mX == posX) {
            return;
        }

        this.mX = posX;
        updateView();
    }
    
    public void setX(String posX) {
        if (posX != null) {
            setX(Integer.valueOf(posX));
        }
    }

    public int getY() {
        return mY;
    }

    public void setY(int posY) {
        mHasY = true;

        posY *= FileUtil.Y_SCALE;

        if ((mFlag & RELATIVE_TO_PARENT) == RELATIVE_TO_PARENT) {
            if (mParentElement != null) {
                posY -= mParentElement.getY();
            }
        }

        if (this.mY == posY) {
            return;
        }

        this.mY = posY;
        updateView();
    }

    public void setY(String posY) {
        if (posY != null) {
            setY(Integer.valueOf(posY));
        }
    }

    public void moveXY(int x, int y) {
        View view = getView();
        if (view != null) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = getY() + y;
            layoutParams.leftMargin = getX() + x;
            view.setLayoutParams(layoutParams);
        }
    }

    public int getW() {
        return mWidth;
    }
    
    public void setRealW(int width) {
        width /= FileUtil.X_SCALE;
        setW(width);
    }

    public void setW(int width) {
        mHasW = true;
        width *= FileUtil.X_SCALE;

        if (this.mWidth == width) {
            return;
        }

        this.mWidth = width;
        updateView();
    }

    public void setW(String width) {
        if (width != null) {
            setW(Integer.valueOf(width));
        }
    }

    public int getH() {
        return mHeight;
    }

    public void setRealH(int height) {
        height /= FileUtil.Y_SCALE;
        setH(height);
    }

    public void setH(int height) {
        mHasH = true;
        height *= FileUtil.Y_SCALE;

        if (this.mHeight == height) {
            return;
        }

        this.mHeight = height;
        updateView();
    }

    public void setH(String height) {
        if (height != null) {
            setH(Integer.valueOf(height));
        }
    }

    public boolean hasX() {
        return mHasX;
    }

    public boolean hasY() {
        return mHasY;
    }

    public boolean hasW() {
        return mHasW;
    }

    public boolean hasH() {
        return mHasH;
    }

    public int getCenterX() {
        return mCenterX;
    }

    public void setCenterX(int centerX) {
        // jianglin:scale
        centerX *= FileUtil.X_SCALE;

        this.mCenterX = centerX;
    }

    public void setCenterX(String centerX) {
        if (centerX != null) {
            setCenterX(Integer.valueOf(centerX));
        }
    }

    public int getCenterY() {
        return mCenterY;
    }

    public void setCenterY(int centerY) {
        // jianglin:scale
        centerY *= FileUtil.Y_SCALE;

        this.mCenterY = centerY;
    }

    public void setCenterY(String centerY) {
        if (centerY != null) {
            setCenterY(Integer.valueOf(centerY));
        }
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        if (mAngle != angle) {
            mAngle = angle;
            View view = getView();
            if (view != null && view.isShown()) {
                view.invalidate();
            }
        }
    }

    public void setAngle(String angle) {
        if (angle != null) {
            setAngle(Integer.valueOf(angle));
        }
    }
    
    public int getAngleY() {
        return mAngleY;
    }

    public void setAngleY(int angleY) {
        if (mAngleY != angleY) {
            mAngleY = angleY;
            View view = getView();
            if (view != null && view.isShown()) {
                view.invalidate();
            }
        }
    }

    public void setAngleY(String angleY) {
        if (angleY != null) {
            setAngleY(Integer.valueOf(angleY));
        }
    }

    public int getAlpha() {
        return mAlpha;
    }

    public void setAlpha(int alpha) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }

        if (mAlpha != alpha) {
            mPaint.setAlpha(alpha);

            View view = getView();
            if (view != null && view.getVisibility() != View.INVISIBLE) {
                view.invalidate();
            }
            mAlpha = alpha;
        }
    }

    Runnable mAnimationRunnable = new Runnable() {

        public void run() {
            if (mAnimationSet != null) {
                View view = getView();
                if (view != null) {
                    view.startAnimation(mAnimationSet);
                }
            }
        }
    };

    public void setAlpha(String alpha) {
        if (alpha != null) {
            setAlpha(Integer.valueOf(alpha));
        }
    }

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        this.mCategory = category;
    }

    public void setCategory(String category) {
        int c = mCategory;
        if (category != null) {
            if (category.equals(Constants.TAG_CHARGING)) {
                c = Constants.CHARGING;
            } else if (category.equals(Constants.TAG_BATTERY_LOW)) {
                c = Constants.BATTERY_LOW;
            } else if (category.equals(Constants.TAG_BATTERY_FULL)) {
                c = Constants.BATTERY_FULL;
            } else if (category.equals(Constants.TAG_BATTERY_NORMAL)) {
                c = Constants.BATTERY_NORMAL;
            }
            setCategory(c);
        }
    }

    public Boolean getVisibility() {
        return mVisibility;
    }

    public void setAlwaysShow(Boolean alwaysShow){
        this.mAwalysShow = alwaysShow;
    }

    public void setAlwaysShow(String alwaysShow){
        this.mAwalysShow = Boolean.valueOf(alwaysShow);
    }

    public boolean getAlwaysShow(){
        return this.mAwalysShow;
    }

    public void setVisibility(Boolean visibility) {
        this.mVisibility = visibility;
        View view = getView();
        if (view != null) {
            if (visibility) {
                view.setVisibility(View.VISIBLE);
                startAnimations();
            } else {
                view.setVisibility(View.GONE);
                clearAnimations();
            }
        }

        if (getName() != null) {
            ExpressionManager.getInstance().setVariableValue(getName() + ".visibility",
                    visibility ? 1 : 0);
        }
    }

    public void setVisibility(String visibility) {
        if ("toggle".equalsIgnoreCase(visibility)) {
            setVisibility(!mVisibility);
            return;
        }

        setVisibility(Utils.getBoolean(visibility));
    }

    public void actionVisibility(String visibility) {
        if (visibility.equals("toggle")) {
            setVisibility(!mVisibility);
            return;
        }
    }

    public int getAlign() {
        return mAlign;
    }

    public void setAlign(int align) {
        this.mAlign = align;
    }

    public void setAlign(String align) {
        if (align.equals("center")) {
            setAlign(ALIGN_CENTER);
        } else if (align.equals("right")) {
            setAlign(ALIGN_RIGHT);
        } else {
            setAlign(ALIGN_LEFT);
        }
    }

    public int getAlignV() {
        return mAlignV;
    }

    public void setAlignV(int alignV) {
        this.mAlignV = alignV;
    }

    public void setAlignV(String alignV) {
        if (alignV.equals("center")) {
            setAlignV(ALIGN_CENTER);
        } else if (alignV.equals("bottom")) {
            setAlignV(ALIGN_BOTTOM);
        } else {
            setAlignV(ALIGN_TOP);
        }
    }

    private int mFlag = 0;
    public final static int RELATIVE_TO_PARENT = 1;

    public void addFlag(int flag) {
        if ((mFlag & RELATIVE_TO_PARENT) != RELATIVE_TO_PARENT &&
                (flag & RELATIVE_TO_PARENT) == RELATIVE_TO_PARENT) {
            if (mParentElement != null) {
                mX -= mParentElement.getX();
                mY -= mParentElement.getY();
            }
        }

        mFlag |= flag;
    }

    private VisibleElement mParentElement = null;

    public VisibleElement getParentElement() {
        return mParentElement;
    }

    public void setParentElement(VisibleElement element) {
        mParentElement = element;
    }

    private AnimationSet mAnimationSet = null;

    /*
     * for this case, if a element have scale animation and translate animation
     * in the same time, if the scale animation add to AnimationSet first, then
     * the scale and translate animation works well but if the translate
     * animation add to AnimationSet first, then the scale animation is well,
     * but the the translate animation will multiply the scale animation factor,
     * it will have wrong behavior so we sort the animations, make sure the
     * scale animation always in the first place
     */
    public void sortAnimations() {
        if (getAnimationElements() != null) {
            AnimationElement animationElement = null;
            List<AnimationElement> tempAnimationElements = new ArrayList<AnimationElement>();
            for (int i = 0; i < getAnimationElements().size(); i++) {
                animationElement = getAnimationElements().get(i);
                if (animationElement.getClass().getName()
                        .equals(PositionAnimationElement.class.getName())) {
                    tempAnimationElements.add(animationElement);
                }
            }

            for (int i = 0; i < tempAnimationElements.size(); i++) {
                animationElement = tempAnimationElements.get(i);
                getAnimationElements().remove(animationElement);
                getAnimationElements().add(animationElement);
            }
        }
    }

    public void setAnimation(String command) {
        if ("play".equalsIgnoreCase(command)) {
            startAnimations();
        }
    }

    public void startAnimations() {
        // initAnimation();

        View view = getView();
        if (view != null && view.getVisibility() == View.VISIBLE) {
            if (getAnimationElements() != null) {
                sortAnimations();

                view.clearAnimation();
                if (mAnimationSet == null) {
                    mAnimationSet = new AnimationSet(false);
                    mAnimationSet.setFillAfter(true);// if child animation use
                                                     // setFillAfter(true), it
                                                     // not work, must set in
                                                     // AnimationSet
                    for (int i = 0; i < getAnimationElements().size(); i++) {
                        AnimationElement animationElement = getAnimationElements().get(i);
                        // Logger.v("animation", i+":"+animationElement);
                        Animation animation = animationElement.generateAnimations(this);
                        if (animation != null) {
                            // Logger.v("animation", "add animation"+animation);
                            mAnimationSet.addAnimation(animation);
                        }
                    }
                }

                view.startAnimation(mAnimationSet);
            }
            if(mAnimatorParser !=null){
                mAnimatorParser.generateAnimatorSet(view);
                mAnimatorParser.startAnimations();
            }
        }
    }

    public void clearAnimations() {
        View view = getView();
        if (view != null) {
            view.clearAnimation();
        }
        if (mAttributAnimation != null) {
            mAttributAnimation.cancel();
        }
        if(mAnimatorParser != null){
            mAnimatorParser.stopAnimations();
        }
    }

    public int getAnimationsStatus() {
        if (mAnimatorParser != null) {
            return mAnimatorParser.getAnimationStatus();
        }
        return Constants.Animation_Status_Stop;
    }

    public AnimatorParser getAnimatorParser() {
        if (mAnimatorParser == null) {
            mAnimatorParser = new AnimatorParser();
        }
        return mAnimatorParser;
    }

    protected void onCategoryChange(int category) {
        if (category >= Constants.CHARGING && category <= Constants.ALWAYS_DISPLAY) {
            if (mCategory != Constants.ALWAYS_DISPLAY) {
                // View view = getView();
                // if (view != null){
                if (category == mCategory) {
                    // view.setVisibility(View.VISIBLE);
                    // startAnimations();
                    this.setVisibility(true);
                } else {
                    // view.setVisibility(View.GONE);
                    // clearAnimations();
                    this.setVisibility(false);
                }
                // }
            }
        }
    }

    /*
     * this method should be override by subclass, otherwise the subclass
     * doesn't have visible display
     */
    public View generateView(Context context, Handler handler) {
        return null;
    }

    private View mView;

    /*
     * subclass must call this method to set view to element, otherwise the
     * element will not have visible view
     */
    public void setView(View view) {
        if (view instanceof ViewGroup && getVisibleElements() != null) {
            for (int i = 0; i < getVisibleElements().size(); i++) {
                VisibleElement elememt = getVisibleElements().get(i);
                View v = elememt.generateView(view.getContext(), view.getHandler());
                if (v != null) {
                    if (v.getParent() != null && v.getParent() instanceof ViewGroup) {
                        ((ViewGroup) v.getParent()).removeView(v);
                    }
                    ((ViewGroup) view).addView(v);
                }
            }
        }
        mView = view;
        // the first time to initial the view's visibility
        if (mView != null && mVisibility == false) {
            this.setVisibility(false);
        }
    }

    public View getView() {
        if (mView != null && mVisibility == false) {
            mView.setVisibility(View.GONE);
            mView.clearAnimation();
        }
        return mView;
    }

    public void clearView() {
        if (mView != null) {
            ViewGroup viewGroup = (ViewGroup) mView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mView);
            }
            // mView.clearAnimation();
            mView = null;
        }
        mOnTimeTick = null;
    }

    /*
     * update the element view to what the parameter defines
     */
    public void updateView() {
        View view = getView();
        if (view == null) {
            return;
        }
        view.setLayoutParams(genLayoutParams());
    }

    public LayoutParams genLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(getW(), getH());
        layoutParams.width = getW();
        layoutParams.height = getH();
        layoutParams.leftMargin = getX();
        layoutParams.topMargin = getY();

        if (hasW()) {
            if (getAlign() == ALIGN_CENTER) {
                layoutParams.leftMargin -= layoutParams.width / 2;
            } else if (getAlign() == ALIGN_RIGHT) {
                layoutParams.leftMargin -= layoutParams.width;
            }
        }

        if (hasH()) {
            if (getAlignV() == ALIGN_CENTER) {
                layoutParams.topMargin -= layoutParams.height / 2;
            } else if (getAlignV() == ALIGN_BOTTOM) {
                layoutParams.topMargin -= layoutParams.height;
            }
        }

        return layoutParams;
    }

    private OnTimeTick mOnTimeTick;

    public void onTimeTick(Time time) {
        if (mOnTimeTick != null) {
            mOnTimeTick.onTimeTick(time);
        }
    }

    public void setOnTimeTick(OnTimeTick onTimeTick) {
        mOnTimeTick = onTimeTick;
    }

    interface OnTimeTick {
        public void onTimeTick(Time time);
    }

    private static ExpressionManager mExpressionManager = ExpressionManager.getInstance();

    public void translateAnimationStage(int deltaX, int deltaY) {
        if (getName() != null) {
            mExpressionManager.setVariableValue(getName() + ".actual_x",
                    (int) ((getX() + deltaX) / FileUtil.X_SCALE));
            mExpressionManager.setVariableValue(getName() + ".actual_y",
                    (int) ((getY() + deltaY) / FileUtil.Y_SCALE));
        }
    }
    
    public void setAnimationListener(AnimationViewFactory.AnimationListener listener){
        
    }
}
