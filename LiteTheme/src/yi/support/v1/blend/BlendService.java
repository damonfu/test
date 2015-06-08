package yi.support.v1.blend;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;


public class BlendService {

    public abstract static class Observer {
        final Handler mHandler;

        public Observer(Handler handler) {
            mHandler = handler;
        }

        public float getScaleFactor() { return 1.0f; }

        abstract public void onBlendFinished(Bitmap bmp);
    }

    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private static BlendTask mCurrentTask;

    private BlendService() {
        
    }

    public static void blur(Bitmap bitmap, int radius, Observer observer) {
        interrupt();
        mCurrentTask = new BlurBlend.Task(bitmap, radius, observer);
        mExecutorService.submit(mCurrentTask);
    }

    public static void sketch(Bitmap bitmap, int shade, Observer observer) {
        interrupt();
        mCurrentTask = new SketchBlend.Task(bitmap, shade, observer);
        mExecutorService.submit(mCurrentTask);
    }

    public static void interrupt() {
        if (mCurrentTask != null) {
            mCurrentTask.interrupt();
        }
    }

    abstract static class BlendTask implements Runnable {
        protected Bitmap mSrcBitmap;
        private boolean mInterrupted = false;
        private final Observer mObserver;
        
        BlendTask(Bitmap bitmap, Observer observer) {
            mSrcBitmap = bitmap;
            mObserver = observer;
        }

        @Override
        public void run() {
            boolean result = false;
            try {
                Bitmap origin = Bitmap.createBitmap(mSrcBitmap);

                scaleSrcBmp();
                result = blend();

                Canvas canvas = new Canvas(origin);
                Paint paint = new Paint();
                paint.setAlpha(200);
                final Matrix matrix = new Matrix();
                matrix.postScale(2, 2);
                canvas.drawBitmap(mSrcBitmap, matrix, paint);

                mSrcBitmap.recycle();
                mSrcBitmap = origin;
            } catch (Exception e) {}
            notifyObserver(result ? mSrcBitmap : null);
        }

        private void scaleSrcBmp() {
            final float scaleFactor = mObserver.getScaleFactor();
            if (scaleFactor != 1.0f) {
                final int w = mSrcBitmap.getWidth();
                final int h = mSrcBitmap.getHeight();
                final Matrix matrix = new Matrix();
                matrix.postScale(scaleFactor, scaleFactor);
                Bitmap thumbnail = Bitmap.createBitmap(mSrcBitmap, 0, 0, w, h, matrix, false);
                mSrcBitmap.recycle();
                mSrcBitmap = thumbnail;
            }
        }

        abstract protected boolean blend();

        public void interrupt() {
            mInterrupted = true;
        }

        protected boolean isInterrupted() {
            return mInterrupted;
        }
        
        protected void notifyObserver(final Bitmap bmp) {
            if (mCurrentTask == null || 
                mCurrentTask == BlendTask.this || 
                mCurrentTask.mObserver != mObserver) {
                if (mObserver!=null && mObserver.mHandler!=null) {
                    mObserver.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mObserver.onBlendFinished(bmp);
                        }
                    });
                }
            }
        }
    }

}
