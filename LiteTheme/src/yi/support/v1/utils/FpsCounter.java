
/*
 * FPS Counter on-screen Log
 * Log will be drawn on screen whenever onDraw is triggered.
 * 
 * author : zhaorui@baidu.com
 */

package yi.support.v1.utils;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewParent;
import android.view.WindowManager;

import com.baidu.lite.R;

public class FpsCounter {

    public static void enable(Activity activity) {
        View fps = activity.findViewById(R.id.fps_counter);
        if (fps == null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            fps = new FpsView(activity);
            // add fps to view tree
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            decorView.addView(fps, params);
            decorView.setOnHierarchyChangeListener((FpsView) fps);
            decorView.invalidate();
        }
    }
    
    public static void disable(Activity activity) {
        View fps = activity.findViewById(R.id.fps_counter);
        if (fps != null) {
            // remove fps from view tree
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.removeView(fps);
            decorView.setOnHierarchyChangeListener(null);
        }
    }
    
    public static boolean isEnabled(Activity activity) {
        View fps = activity.findViewById(R.id.fps_counter);
        return (fps != null);
    }

    private static class FpsView extends View implements OnHierarchyChangeListener {

        final static int ONE_SECOND = 1000; // 1s
        final static int TEXT_SIZE_DIP = 16;

        private final static int COMB_CHART_GRANULARITY = 4;
        private final static int COMB_CHART_X = 0;
        private int COMB_CHART_Y;

        private final StringBuilder mText = new StringBuilder();
        private static final Paint mPaint = new Paint();

        private long mLastDrawTime;
        private long mFpsTime;

        private int mAverageFps;
        private int mFpsCounter;

        private float mText_Y;
        private float[] mFpsCombChart;

        FpsView(Context context) {
            super(context);
            init(context);
        }
        
        FpsView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }
        
        FpsView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context);
        }

        private void init(Context context) {
            setId(R.id.fps_counter);

            int widthPixels = getResources().getDisplayMetrics().widthPixels;
            mFpsCombChart = new float[COMB_CHART_GRANULARITY * widthPixels];

            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.setTextSize(dipToPixels(TEXT_SIZE_DIP));

            COMB_CHART_Y = getStatusBarHeight();

            mText_Y = COMB_CHART_Y + mPaint.getTextSize();
            resetFpsChart();
        }
        
        private int getStatusBarHeight() {
            final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            return (resourceId > 0) ? getResources().getDimensionPixelSize(resourceId) : dipToPixels(25);
        }
        
        private int dipToPixels(float dips) {
            float density = getResources().getDisplayMetrics().density;
            return (int) (dips * density + 0.5f);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            calculateFps();
            
            mPaint.setColor(Color.CYAN);
            canvas.drawLines(mFpsCombChart, mPaint);

            mPaint.setColor(Color.BLACK);
            canvas.drawText(mText.toString(), 2+1, mText_Y+1, mPaint);
            
            mPaint.setColor(Color.MAGENTA);
            canvas.drawText(mText.toString(), 2, mText_Y, mPaint);

            setDirty(this);
        }

        private void calculateFps() {
            final long now = System.currentTimeMillis();
            final long interval = now - mLastDrawTime;
            final long elapsed = now - mFpsTime;

            mFpsCounter++;
            mLastDrawTime = now;

            int instantFps = (int) (ONE_SECOND / interval);
            if (elapsed > ONE_SECOND) {
                mFpsTime = now;
                mAverageFps = mFpsCounter;
                mFpsCounter = 0;
            }

            appendFpsToChart(interval);

            mText.setLength(0);
            mText.append("fps:").append(mAverageFps).append("/").append(instantFps).append("/").append(mFpsCounter);
        }

        /*
         * invalidate without redraw
         */
        private static void setDirty(View view) {
            try {
                final ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    final Field field = View.class.getDeclaredField("mParent");
                    final boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(view, null);

                    view.invalidate();

                    field.set(view, parent);
                    field.setAccessible(accessible);
                }
            } catch (Exception e) {
                Log.e("FpsCounter", e.toString());
            }
        }
        
        private void resetFpsChart() {
            float x = COMB_CHART_X;
            for (int i=0; i<mFpsCombChart.length; i+=4) {
                mFpsCombChart[i+0] = x;
                mFpsCombChart[i+1] = COMB_CHART_Y;
                mFpsCombChart[i+2] = x;
                mFpsCombChart[i+3] = COMB_CHART_Y;
                x += 2;
            }
        }
        
        private void appendFpsToChart(long interval) {
            int widthPixels = getResources().getDisplayMetrics().widthPixels;
            final int max_x = COMB_CHART_X + 2 * widthPixels;

            // scroll all to right
            for (int i=0; i<mFpsCombChart.length; i+=4) {
                if (++mFpsCombChart[i+0] > max_x) {
                    // move the most right one to left & set it's value
                    mFpsCombChart[i+0] = COMB_CHART_X;
                    mFpsCombChart[i+2] = COMB_CHART_X;
                    mFpsCombChart[i+3] = COMB_CHART_Y + dipToPixels(interval >> 1);
                } else {
                    ++mFpsCombChart[i+2];
                }
            }
        }

        @Override
        public void onChildViewAdded(View parent, View child) {
            if (getParent() == parent) {
                bringToFront();
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            
        }

    };


}

