
package yi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import com.baidu.lite.R;

public class BadgeView extends TextView {
    public static final int POSITION_TOP_LEFT = 1;
    public static final int POSITION_TOP_RIGHT = 2;
    public static final int POSITION_BOTTOM_LEFT = 3;
    public static final int POSITION_BOTTOM_RIGHT = 4;
    public static final int POSITION_LEFT_CENTER = 5;

    private static final int DEFAULT_MARGIN_DIP = 0;
    private static final int DEFAULT_LR_PADDING_DIP = 0;
    private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
    private static final int DEFAULT_POSITION = POSITION_LEFT_CENTER;
    private static final int DEFAULT_BADGE_COLOR = Color.RED;
    private static final int DEFAULT_TEXT_COLOR = 0xff1884bf;
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_SIZE = 24; //dip

    private static Animation mFadeIn;
    private static Animation mFadeOut;

    private View mTarget;

    private int mBadgePosition;
    private int mBadgeMarginLeft;
    private int mBadgeMarginTop;
    private int mBadgeMarginRight;
    private int mBadgeMarginBottom;
    private int mBadgeColor;

    private boolean mIsShown;

    private ShapeDrawable mBadgeBg;

    private int mTargetTabIndex;
    private int mTargetResId;
    private boolean mIsMatchParent = false;

    public BadgeView(Context context) {
        this(context, (AttributeSet) null, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, View target, int tabIndex, int targetResId) {
        this(context, null, android.R.attr.textViewStyle, target, tabIndex, targetResId);
    }

    public BadgeView(Context context, TabWidget target, int index) {
        this(context, null, android.R.attr.textViewStyle, target, index, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, 0, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex, int targetResId) {
        super(context, attrs, defStyle);
        init(context, target, tabIndex, targetResId);
    }

    private void init(Context context, View target, int tabIndex, int targetResId) {

        mTarget = target;
        mTargetTabIndex = tabIndex;
        mTargetResId = targetResId;
        // apply defaults
        mBadgePosition = DEFAULT_POSITION;
        mBadgeMarginLeft = mBadgeMarginBottom = 0;
        mBadgeMarginTop = 0;
        mBadgeMarginRight = dipToPixels(DEFAULT_MARGIN_DIP);
        mBadgeColor = DEFAULT_BADGE_COLOR;

        int padding = dipToPixels(DEFAULT_LR_PADDING_DIP);
        setPadding(padding, 0, padding, 0);
        setTypeface(Typeface.DEFAULT);
        setTextColor(DEFAULT_TEXT_COLOR);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_SIZE);
        setGravity(Gravity.CENTER);
        Drawable d = getContext().getResources().getDrawable(R.drawable.cld_ic_tab_point);
        int size = dipToPixels(DEFAULT_SIZE);
        setMaxHeight(size);
        setBackgroundDrawable(d);

        mFadeIn = new AlphaAnimation(0, 1);
        mFadeIn.setInterpolator(new DecelerateInterpolator());
        mFadeIn.setDuration(200);

        mFadeOut = new AlphaAnimation(1, 0);
        mFadeOut.setInterpolator(new AccelerateInterpolator());
        mFadeOut.setDuration(200);

        mIsShown = false;
        if (mTarget != null) {
            applyTo(mTarget);
        } else {
            show();
        }

    }

    void applyTo(View target) {

        LayoutParams lp = target.getLayoutParams();
        ViewParent parent = target.getParent();
        RelativeLayout container = new RelativeLayout(getContext());
        container.setDuplicateParentStateEnabled(true);

        if (target instanceof TabWidget) {

            // set target to the relevant tab child container
            target = ((TabWidget) target).getChildTabViewAt(mTargetTabIndex);
            mTarget = target;

            ((ViewGroup) target).addView(container,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            setVisibility(View.GONE);
            container.addView(this);

        } else {

            // TODO verify that parent is indeed a ViewGroup
            ViewGroup group = (ViewGroup) parent;
            int index = group.indexOfChild(target);

            group.removeView(target);
            group.addView(container, index, lp);

            target.setDuplicateParentStateEnabled(true);
            container.addView(target);
            RelativeLayout.LayoutParams lparam = (RelativeLayout.LayoutParams) target.getLayoutParams();
            lparam.addRule(RelativeLayout.CENTER_IN_PARENT);

            setVisibility(View.GONE);
            container.addView(this);

            group.invalidate();

        }

    }
    
    public void setContainerMatchParent(boolean isMatchParent){
        mIsMatchParent = isMatchParent;
        if(isMatchParent) {
            ViewGroup vp = (ViewGroup)getParent();
            ViewGroup.LayoutParams lp = vp.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            vp.invalidate();
        } 
    }

    public boolean isContainerMatchParent(){
        return mIsMatchParent;
    }
    
    public void show() {
        show(false, null);
    }

    public void show(boolean animate) {
        show(animate, mFadeIn);
    }

    public void show(Animation anim) {
        show(true, anim);
    }

    public void hide() {
        hide(false, null);
    }

    public void hide(boolean animate) {
        hide(animate, mFadeOut);
    }

    public void hide(Animation anim) {
        hide(true, anim);
    }

    public void toggle() {
        toggle(false, null, null);
    }

    public void toggle(boolean animate) {
        toggle(animate, mFadeIn, mFadeOut);
    }

    public void toggle(Animation animIn, Animation animOut) {
        toggle(true, animIn, animOut);
    }

    private void show(boolean animate, Animation anim) {
        if (getBackground() == null) {
            if (mBadgeBg == null) {
                mBadgeBg = getDefaultBackground();
            }
            setBackgroundDrawable(mBadgeBg);
        }
        applyLayoutParams();

        if (animate) {
            startAnimation(anim);
        }
        setVisibility(View.VISIBLE);
        mIsShown = true;
    }

    private void hide(boolean animate, Animation anim) {
        setVisibility(View.GONE);
        if (animate) {
            startAnimation(anim);
        }
        mIsShown = false;
    }

    private void toggle(boolean animate, Animation animIn, Animation animOut) {
        if (mIsShown) {
            hide(animate && (animOut != null), animOut);
        } else {
            show(animate && (animIn != null), animIn);
        }
    }

    private ShapeDrawable getDefaultBackground() {

        int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
        float[] outerR = new float[] {
                r, r, r, r, r, r, r, r
        };

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(mBadgeColor);

        return drawable;

    }

    private void applyLayoutParams() {

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        switch (mBadgePosition) {
            case POSITION_TOP_LEFT:
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case POSITION_TOP_RIGHT:
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case POSITION_BOTTOM_LEFT:
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case POSITION_BOTTOM_RIGHT:
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case POSITION_LEFT_CENTER:
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                break;
            default:
                break;
        }

        lp.setMargins(mBadgeMarginLeft, mBadgeMarginTop, mBadgeMarginRight, mBadgeMarginBottom);
        setLayoutParams(lp);

    }

    public View getTarget() {
        return mTarget;
    }

    @Override
    public boolean isShown() {
        return mIsShown;
    }

    public int getBadgePosition() {
        return mBadgePosition;
    }

    public void setBadgePosition(int layoutPosition) {
        mBadgePosition = layoutPosition;
    }

    public Rect getBadgeMargin() {
        return new Rect(mBadgeMarginLeft,mBadgeMarginTop, mBadgeMarginRight, mBadgeMarginBottom);
    }

    public void setBadgeMargin(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        mBadgeMarginLeft = leftMargin;
        mBadgeMarginTop = topMargin;
        mBadgeMarginRight = rightMargin;
        mBadgeMarginBottom = bottomMargin;
    }

    public int getBadgeBackgroundColor() {
        return mBadgeColor;
    }

    public void setBadgeBackgroundColor(int badgeColor) {
        mBadgeColor = badgeColor;
        mBadgeBg = getDefaultBackground();
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                r.getDisplayMetrics());
        return (int) px;
    }
    
    public int getTargetTabIndex(){
        return mTargetTabIndex;
    }
    
    public int getTargetResId(){
        return mTargetResId;
    }
}
