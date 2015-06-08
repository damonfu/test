package yi.support.v1;

import yi.support.v1.utils.Animatable;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;


class ContentDemensionSwitcher {
    
    enum Demension {
        STANDARD,
        EXCLUSIVE,
    }

    private Demension mDemension = Demension.STANDARD;
    
    void setDemension(Activity activity, Demension demension) {
        if (getDemension() != demension) {
            final View content = activity.findViewById(android.R.id.content);
            if (content == null) return;

            final View actionBar = getActionBar(content);

            if (demension == Demension.EXCLUSIVE) {
                if (actionBar.getVisibility() != View.VISIBLE) return;

                View decorView = content.getRootView();
                Drawable background = decorView.getBackground();
                if (background != null) {
                    Drawable clone = background.getConstantState().newDrawable();
                    content.setBackgroundDrawable(clone);
                }
                content.addOnLayoutChangeListener(mOnLayoutChangeListener);
            } else {
                actionBar.setVisibility(View.VISIBLE);
            }
            
            content.forceLayout();
            content.requestLayout();
            mDemension = demension;
        }
    }

    Demension getDemension() {
        return mDemension;
    }

    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        boolean mSelfTriggered;

        @Override
        public void onLayoutChange(View content, int left, int top, int right,
                int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (!mSelfTriggered) {
                mSelfTriggered = true;

                final View actionBar = getActionBar(content);
                if (getDemension() == Demension.EXCLUSIVE) {
                    if (layoutContentToTop(content, actionBar)) {
                        showScrollUpAnimation(content, actionBar);
                    }
                } else {
                    content.removeOnLayoutChangeListener(mOnLayoutChangeListener);
                    showScrollDownAnimation(content, actionBar);
                }

                mSelfTriggered = false;
            }
        }
    };
    
    private boolean layoutContentToTop(View content, View actionBar) {
        if (content.getTop() != actionBar.getTop()) {
            final int widthSpec = getExactlyMeasureSpec(content.getMeasuredWidth(), 0);
            final int heightSpec = getExactlyMeasureSpec(content.getMeasuredHeight(), actionBar.getHeight());
            content.measure(widthSpec, heightSpec);
            content.layout(content.getLeft(), actionBar.getTop(), content.getRight(), content.getBottom());
            return true;
        } else {
            return false;
        }
    }
    
    private void showScrollUpAnimation(final View content, final View actionBar) {
        // start animation
        float from = actionBar.getHeight();

        float current = from;
        float residual = Animatable.getCurrent(content, 0);
        if (residual > 0) { // scroll up
            current = residual;
        } else if (residual < 0) { // scroll down
            current += residual;
        }

        content.startAnimation(new Animatable.Vertical(from, 0, current,
                Animatable.DURATION.CONTENT.TRANSLATE, Animatable.INTERPOLATER.DECELERATE) {
            @Override
            public void onEnd() {
                content.clearAnimation();
                actionBar.setVisibility(View.GONE);
            }
        });
    }
    
    private void showScrollDownAnimation(final View content, final View actionBar) {
        float from = -actionBar.getHeight();

        float current = from;
        float residual = Animatable.getCurrent(content, 0);
        if (residual > 0) { // scroll up
            current += residual;
        } else if (residual < 0) { // scroll down
            current = residual;
        }

        content.startAnimation(new Animatable.Vertical(from, 0, current, 
                Animatable.DURATION.CONTENT.TRANSLATE, Animatable.INTERPOLATER.DECELERATE) {
            @Override
            public void onEnd() {
                content.clearAnimation();
                content.setBackgroundDrawable(null);
            }
        });
    }

    private int getExactlyMeasureSpec(int measureSpec, int offset) {
        final int size = MeasureSpec.getSize(measureSpec);
        return MeasureSpec.makeMeasureSpec(size + offset, MeasureSpec.EXACTLY);
    }

    private View getActionBar(View view) {
        final int resId = view.getResources().getIdentifier("action_bar_container", "id", "android");
        return view.getRootView().findViewById(resId);
    }

}