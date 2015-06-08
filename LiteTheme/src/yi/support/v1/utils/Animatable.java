package yi.support.v1.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;


public class Animatable {

    /*
     * definition
     */

    public static class DURATION {

        private static final long SCALE = 3;

        public static class MENU {

            public static class ACTION {
                public static final long FADE_OUT = SCALE * 50;
                public static final long HEIGHT = SCALE * 100;
            }

            public static class INDICATOR {
                public static final long ALPHA = SCALE * 50;
            }

            public static class LIST {
                public static final long TRANSLATE = SCALE * 75;
                public static final long TRANSLATE_DEC = SCALE * 150;
                public static final long ALPHA = SCALE * 50;
                public static final long ALPHA_START_OFFSET = TRANSLATE - ALPHA;
            }

            public static class PANEL {
                public static final long TRANSLATE = SCALE * 50;
            }
        }
        
        public static class MASK {
            public static final long ALPHA = MENU.LIST.TRANSLATE;
            public static final long BLUR = SCALE * 250;
        }
        
        public static class CONTENT {
            public static final long TRANSLATE = SCALE * 100;
        }

        public static class VIEW {
            public static final long TRANSLATE = SCALE * 100;
        }

    }
    
    public static class INTERPOLATER {
        public static final float DECELERATE = 3;
    }


    /*
     * implement start
     */

    private interface Current {
        public float getCurrent();
    }

    private static class Translate extends TranslateAnimation implements Current {
        protected float mCurrent;
            
        public Translate(float fromXDelta, float toXDelta, float fromYDelta, float toYDelt, float factor) {
            super(fromXDelta, toXDelta, fromYDelta, toYDelt);
            setInterpolator(new DecelerateInterpolator(factor));
            setFillAfter(true);
            setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                
                @Override
                public void onAnimationRepeat(Animation animation) { }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    onEnd();
                }
            });
        }
        
        @Override
        public float getCurrent() {
            return mCurrent;
        }
        
        protected void onEnd() {
            
        }
    }

    public static class Horizontal extends Translate {

        public Horizontal(float fromDelta, float toDelta, float currentDelta, long duration) {
            this(fromDelta, toDelta, currentDelta, duration, 1);
        }
        
        public Horizontal(float fromDelta, float toDelta, float currentDelta, long duration, float factor) {
            super(currentDelta, toDelta, 0, 0, factor);
            mCurrent = currentDelta;
            setDuration(getRemainingDuration(fromDelta, toDelta, currentDelta, duration));
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            float[] values = new float[9];
            t.getMatrix().getValues(values);
            mCurrent = values[2];
        }

    }

    public static class Vertical extends Translate {

        public Vertical(float fromDelta, float toDelta, float currentDelta, long duration) {
            this(fromDelta, toDelta, currentDelta, duration, 1);
        }

        public Vertical(float fromDelta, float toDelta, float currentDelta, long duration, float factor) {
            super(0, 0, currentDelta, toDelta, factor);
            mCurrent = currentDelta;
            setDuration(getRemainingDuration(fromDelta, toDelta, currentDelta, duration));
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            float[] values = new float[9];
            t.getMatrix().getValues(values);
            mCurrent = values[5];
        }

    }
        
    public static class Alpha extends AlphaAnimation implements Current {
        private float mCurrent;

        public Alpha(float fromAlpha, float toAlpha, float currentAlpha, long duration) {
            this(fromAlpha, toAlpha, currentAlpha, duration, 0);
        }

        public Alpha(float fromAlpha, float toAlpha, float currentAlpha, long duration, long startOffset) {
            super(currentAlpha, toAlpha);
            mCurrent = currentAlpha;
            setDuration(getRemainingDuration(fromAlpha, toAlpha, currentAlpha, duration));
            setFillAfter(true);
            setStartOffset(startOffset);
            setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }
                
                @Override
                public void onAnimationRepeat(Animation animation) { }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    onEnd();
                }
            });
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mCurrent = t.getAlpha();
        }

        @Override
        public float getCurrent() {
            return mCurrent;
        }

        protected void onEnd() {
            
        }
    }

    public static long getRemainingDuration(float from, float to, float current, long duration) {
        duration = (long) (duration * (to - current) / (to - from));
        return Math.max(0, duration);
    }

    public static float getCurrent(View view, float defValue) {
        Animation animation = view.getAnimation();
        if (animation instanceof Current) {
            return ((Current) animation).getCurrent();
        } else {
            return defValue;
        }
    }

}