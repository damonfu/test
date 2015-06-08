
package com.baidu.themeanimation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.view.WindowManager;

import com.baidu.themeanimation.element.ImageElement.ImageElementView;

public class FileUtil {
    private final static String TAG = "FileUtil";

    public static final int PRIORITY_LOW    = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH   = 2;

    private boolean mIsWallpaperChanged = false;

    private String mWallpaperFilePath;
    private String mLockScreenFilePath;
    private Bitmap mLockWallpaperBitmap;

    public static float X_SCALE = 1;
    public static float Y_SCALE = 1;
    public static float Image_X_SCALE = 1;
    public static float Image_Y_SCALE = 1;
    public static float WALLPAPER_SCALE = 1;
    public static int REAL_SCREEN_WIDTH = 0;
    public static int REAL_SCREEN_HEIGHT = 0;
    public static int DESIGH_SCREEN_WIDTH = Constants.DEFAULT_SCREEN_WIDTH;
    public static int DESIGN_SCREEN_HEIGHT = Constants.DEFAULT_SCREEN_HEIGHT;
    public int mDensityDpi = DisplayMetrics.DENSITY_HIGH;

    private boolean mIsThemeChange;
    private long mStartTime;

    private boolean isTactileFeedbackEnabled = true;

    HashMap<String, Bitmap> mBitmaps = new HashMap<String, Bitmap>();

    static FileUtil mInstance;
    static final Object mInstanceSync = new Object();
    static final Object mExecuteServiceSync = new Object();
    private ExecutorService mExecutorService;
    private ExecutorService mExecutorService2;
    private List<LoadBitmapTask> mLoadTasks = new ArrayList<LoadBitmapTask>();
    private boolean mLoadImmediate = false;

    static public FileUtil getInstance() {
        synchronized (mInstanceSync) {
            if (mInstance != null) {
                return mInstance;
            }

            mInstance = new FileUtil();
        }
        return mInstance;
    }

    public boolean isTactileFeedbackEnabled() {
        return isTactileFeedbackEnabled;
    }

    public void setTactileFeedbackEnabled(boolean isTactileFeedbackEnabled) {
        this.isTactileFeedbackEnabled = isTactileFeedbackEnabled;
    }

    public void setDensity(DisplayMetrics dm, String rootPath, String lockDir, String wallpaperDir) {
        mDensityDpi = dm.densityDpi;
        DESIGH_SCREEN_WIDTH = Constants.DEFAULT_SCREEN_WIDTH;
        DESIGN_SCREEN_HEIGHT = Constants.DEFAULT_SCREEN_HEIGHT;
        if (dm.widthPixels < dm.heightPixels) {
            REAL_SCREEN_WIDTH = dm.widthPixels;
            REAL_SCREEN_HEIGHT = dm.heightPixels;
        } else {
            REAL_SCREEN_WIDTH = dm.heightPixels;
            REAL_SCREEN_HEIGHT = dm.widthPixels;
        }
        //REAL_SCREEN_WIDTH = 480;
        //REAL_SCREEN_HEIGHT = 800;
        dm = null;
        String advance = lockDir + "/advance";
        Image_X_SCALE = 1;
        Image_Y_SCALE = 1;
        File file = null;
        String localPath;
        
        if (mDensityDpi == 480) {
            if (REAL_SCREEN_WIDTH != Constants.DEFAULT_SCREEN_WIDTH_XXH
                    || REAL_SCREEN_HEIGHT != Constants.DEFAULT_SCREEN_HEIGHT_XXH) {
                localPath = lockDir + "/advance-xxhdpi-" + REAL_SCREEN_WIDTH + "x" + REAL_SCREEN_HEIGHT;
                file = new File(rootPath + localPath);
                if (file.exists() && file.isDirectory()) {
                    advance = localPath;
                }
            }
            if (file == null || !(file.exists() && file.isDirectory())) {
                localPath = lockDir + "/advance-xxhdpi";
                file = new File(rootPath + localPath);
                if (file.exists() && file.isDirectory()) {
                    advance = localPath;
                    Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH_XXH;
                    Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT_XXH;
                } else {
                    Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH;
                    Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT;
                }
            }
            if (file == null || !(file.exists() && file.isDirectory())) {
                localPath = lockDir + "/advance-xhdpi";
                file = new File(rootPath + localPath);
                if (file.exists() && file.isDirectory()) {
                    advance = localPath;
                    Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH_XH;
                    Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT_XH;
                } else {
                    Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH;
                    Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT;
                }
            }
        } else if (mDensityDpi == DisplayMetrics.DENSITY_XHIGH) {
            if (REAL_SCREEN_WIDTH != Constants.DEFAULT_SCREEN_WIDTH_XH
                    || REAL_SCREEN_HEIGHT != Constants.DEFAULT_SCREEN_HEIGHT_XH) {
                localPath = lockDir + "/advance-xhdpi-" + REAL_SCREEN_WIDTH + "x" + REAL_SCREEN_HEIGHT;
                file = new File(rootPath + localPath);
                if (file.exists() && file.isDirectory()) {
                    advance = localPath;
                }
            }
            if (file == null || !(file.exists() && file.isDirectory())) {
                localPath = lockDir + "/advance-xhdpi";
                file = new File(rootPath + localPath);
                if (file.exists() && file.isDirectory()) {
                    advance = localPath;
                    Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH_XH;
                    Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT_XH;
                } else {
                    Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH;
                    Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT;
                }
            }
        } else if (mDensityDpi == DisplayMetrics.DENSITY_HIGH) {
            if (REAL_SCREEN_WIDTH != Constants.DEFAULT_SCREEN_WIDTH
                    || REAL_SCREEN_HEIGHT != Constants.DEFAULT_SCREEN_HEIGHT) {
                localPath = lockDir + "/advance-hdpi-" + REAL_SCREEN_WIDTH + "x" + REAL_SCREEN_HEIGHT;
                file = new File(localPath);
                if (file.exists() && file.isDirectory()) {
                    advance = localPath;
                }
            }
            if (file == null || !(file.exists() && file.isDirectory())) {
                Image_X_SCALE = ((float) REAL_SCREEN_WIDTH) / Constants.DEFAULT_SCREEN_WIDTH;
                Image_Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / Constants.DEFAULT_SCREEN_HEIGHT;
            }
        }

        setLockScreenFilePath(rootPath + advance);
        setLockWallpaperFilePath(rootPath + wallpaperDir + "/default_lock_wallpaper.jpg");

        X_SCALE = ((float) REAL_SCREEN_WIDTH) / DESIGH_SCREEN_WIDTH;
        Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / DESIGN_SCREEN_HEIGHT;

        WALLPAPER_SCALE = 1;
    }

    public void updateScale(int designWidth, int designHeight) {
        DESIGH_SCREEN_WIDTH = designWidth;
        DESIGN_SCREEN_HEIGHT = designHeight;
        X_SCALE = ((float) REAL_SCREEN_WIDTH) / DESIGH_SCREEN_WIDTH;
        Y_SCALE = ((float) REAL_SCREEN_HEIGHT) / DESIGN_SCREEN_HEIGHT;
        Image_X_SCALE = X_SCALE;
        Image_Y_SCALE = Y_SCALE;
    }

    public void init(Context context, String rootPath,  String lockDir, String wallpaperDir) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(dm);
        FileUtil.getInstance().setDensity(dm, rootPath, lockDir, wallpaperDir);

        InputStream configStream = FileUtil.getInstance().getElementToInputStream("config.xml");
        if (configStream != null) {
            try {
                ConfigParser.parse(configStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPath(String rootPath) {
        setLockScreenFilePath(rootPath);
        setLockWallpaperFilePath(rootPath + File.separator + "default_lock_wallpaper.jpg");
    }

    public boolean getWallpaperChanged() {
        return mIsWallpaperChanged;
    }

    public void setWallpaperChanged(boolean _wallpaperChanged) {
        mIsWallpaperChanged = _wallpaperChanged;
    }

    public String getLockScreenFilePath() {
        return mLockScreenFilePath;
    }

    public void setLockScreenFilePath(String lockWallpaperPath) {
        mLockScreenFilePath = lockWallpaperPath;
    }

    public String getLockWallpaperFilePath() {
        return mWallpaperFilePath;
    }

    public void setLockWallpaperFilePath(String lockWallpaperPath) {
        mWallpaperFilePath = lockWallpaperPath;
    }

    public InputStream openFile(Handler handler) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(2);
        }
        if (mExecutorService2 == null) {
            mExecutorService2 = Executors.newFixedThreadPool(2);
        }

        File lockscreenThemeFile = new File(mLockScreenFilePath);
        if (!(lockscreenThemeFile.exists() && lockscreenThemeFile.isDirectory())) {
            Logger.w(TAG, "cannot found " + mLockScreenFilePath);
            return null;
        }

        InputStream manifestStream = null;
        try {
            manifestStream = getElementToInputStream("manifest.xml");
            if (manifestStream == null) {
                Logger.w(TAG, "cannot found manifestStream.xml!!");
                return null;
            }

            File wallpaperFile = new File(mWallpaperFilePath);
            if (!wallpaperFile.exists()) {
                mWallpaperFilePath = mWallpaperFilePath.replace(".jpg", ".png");
                wallpaperFile = new File(mWallpaperFilePath);
            }
            if (wallpaperFile.exists() && wallpaperFile.isFile()) {
                setWallpaperChanged(true);
            }
            // new LoadBitmapFileThead2(mLockScreenFilePath,handler).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manifestStream;
    }

    class LoadWallpaperTask implements Runnable {
        String mLocalPath;
        Handler mHandler;

        public LoadWallpaperTask(String path, Handler handler) {
            mLocalPath = path;
            mHandler = handler;
        }

        public void run() {
            todo();
        }

        void todo() {
            try {
                synchronized (mLockWallPaperLock) {
                    mLockWallpaperBitmap = loadNextLockWallPaper();
                }
                mHandler.sendMessageDelayed(mHandler.obtainMessage(Constants.MSG_UPDATE_WALLPAPER),
                        300);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadWallpaper(Handler handler) {
        if (FileUtil.getInstance().getWallpaperChanged()) {
            try {
                if (mExecutorService != null){
                    mExecutorService.execute(new LoadWallpaperTask(mWallpaperFilePath, handler));
                }
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
            }
        } else {
            handler.sendMessageDelayed(handler.obtainMessage(Constants.MSG_UPDATE_WALLPAPER), 300);
        }
    }

    public static boolean isPic(String file)
    {
        return (file.endsWith("jpeg") || file.endsWith("jpg")
                || file.endsWith("png") || file.endsWith("PNG")
                || file.endsWith("JPEG") || file.endsWith("JPG")
                || file.endsWith("bmp") || file.endsWith("BMP"));
    }

    private final byte[] mLockWallPaperLock = new byte[1];
    private Bitmap loadNextLockWallPaper() {
        if (getWallpaperChanged()) {
            try {
                if (mLockWallpaperBitmap != null) {
                    //mLockWallpaperBitmap.recycle();
                    mLockWallpaperBitmap = null;
                }

                File wallpaperFile = new File(getLockWallpaperFilePath());
                if (wallpaperFile.exists() && wallpaperFile.isFile()) {
                    mLockWallpaperBitmap = BitmapFactory.decodeFile(getLockWallpaperFilePath());
                    if (mLockWallpaperBitmap != null) {
                        mLockWallpaperBitmap = Bitmap.createScaledBitmap(mLockWallpaperBitmap,
                                (int) (FileUtil.REAL_SCREEN_WIDTH * FileUtil.WALLPAPER_SCALE),
                                (int) (FileUtil.REAL_SCREEN_HEIGHT * FileUtil.WALLPAPER_SCALE), false);
                    }
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setWallpaperChanged(false);
            }
        }
        return mLockWallpaperBitmap;
    }

    public Bitmap getCurrentLockWallpaper() {
        synchronized (mLockWallPaperLock) {
            return mLockWallpaperBitmap;
        }
    }

    public boolean checkManifest() {
        File file = new File(getLockScreenFilePath() + File.separator + "manifest.xml");
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    public InputStream getElementToInputStream(String element) {
        InputStream inputStream = null;
        String path;
        path = getLockScreenFilePath() + File.separator + element;
        File file = new File(path);
        try {
            if (file.exists() && file.isFile()) {
                // Logger.v(TAG, "Fetch the " + element + " from " + path);
                inputStream = new FileInputStream(path);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public void clearBitmap() {
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
            mExecutorService = null;
        }
        if (mExecutorService2 != null) {
            mExecutorService2.shutdownNow();
            mExecutorService2 = null;
        }
        synchronized (mBitmaps) {
            Set<String> keySet = mBitmaps.keySet();
            Bitmap bitmap;
            Iterator<String> keySetIterator = keySet.iterator();
            while (keySetIterator.hasNext()) {
                bitmap = mBitmaps.get(keySetIterator.next());
                if (bitmap != null && !bitmap.isRecycled()) {
                    //bitmap.recycle();
                    bitmap = null;
                }
            }
            mBitmaps.clear();
        }
        synchronized (mLockWallPaperLock) {
            if (null != mLockWallpaperBitmap && !mLockWallpaperBitmap.isRecycled()) {
                //mLockWallpaperBitmap.recycle();
                mLockWallpaperBitmap = null;
            }
        }
    }

    private void load(String path, Object target, boolean isMask) {
        try {
            if (mExecutorService != null) {
                mExecutorService.execute(new LoadBitmapTask(path, target, isMask));
            }
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }
    }

    private class LoadBitmapTask implements Runnable {
        private String mPath;
        private Object mTarget;
        private boolean mIsMask;
        private int mPriority;

        public LoadBitmapTask(String path, Object target, boolean isMask) {
            this(path, target, isMask, PRIORITY_NORMAL);
        }

        public LoadBitmapTask(String path, Object target, boolean isMask, int priority) {
            super();
            mPath = path;
            mTarget = target;
            mIsMask = isMask;
            mPriority = priority;
        }

        public int getPriority() {
            return mPriority;
        }

        @Override
        public void run() {
            synchronized (mBitmaps) {
                final Bitmap bmp = getElementBitmap(mPath);
                if (bmp == null || bmp.isRecycled()) {
                    return;
                }
                if (mTarget instanceof ImageElementView) {
                    final ImageElementView elementview = (ImageElementView) mTarget;
                    if (mIsMask) {
                        elementview.post(new Runnable() {
                            @Override
                            public void run() {
                                elementview.setMask(bmp);
                                elementview.requestLayout();
                            }
                        });
                    } else {
                        elementview.post(new Runnable() {
                            @Override
                            public void run() {
                                elementview.setImage(bmp);
                            }
                        });
                    }
                } else if (mTarget instanceof ImageView) {
                    final ImageView imageview = (ImageView) mTarget;
                    imageview.post(new Runnable() {
                        @Override
                        public void run() {
                            imageview.setImageBitmap(bmp);
                        }
                    });
                }
            }
        }
    }

    private class TaskComparator implements Comparator<LoadBitmapTask>{

        @Override
        public int compare(LoadBitmapTask lhs, LoadBitmapTask rhs) {
            return rhs.getPriority() - lhs.getPriority();
        }

    }

    /*
     * get the bitmap of the specify element path
     */
    public Bitmap getElementBitmap(String element) {
        Bitmap bitmap = null;
        bitmap = mBitmaps.get(element);
        if (bitmap == null && element != null) {
            InputStream is = null;
            try {
                is = getElementToInputStream(element);
                bitmap = BitmapFactory.decodeStream(is);
                if (mBitmaps.get(element) == null && bitmap != null
                        && (FileUtil.Image_X_SCALE != 1 || FileUtil.Image_Y_SCALE != 1)) {
                    int width = (int) (bitmap.getWidth() * FileUtil.Image_X_SCALE);
                    int height = (int) (bitmap.getHeight() * FileUtil.Image_Y_SCALE);
                    if (width > 0 && height > 0) {
                        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        //bitmap.recycle();
                        bitmap = null;
                        bitmap = bmp;
                    }
                }

                if (mBitmaps.get(element) != null) {
                    if (null != bitmap && !bitmap.isRecycled()) {
                        //bitmap.recycle();
                    }
                    bitmap = null;
                    bitmap = mBitmaps.get(element);
                } else {
                    mBitmaps.put(element, bitmap);
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }

    /*
     * get the bitmap of the specify element path
     */
    public BitmapFactory.Options getBitmapOptions(String element) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        String path = getLockScreenFilePath() + File.separator + element;
        if (element != null) {
            try {
                BitmapFactory.decodeFile(path, options);
                options.outWidth *= FileUtil.Image_X_SCALE;
                options.outHeight *= FileUtil.Image_Y_SCALE;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return options;
    }

    public void cacheBitmap(String name, int priority) {
        LoadBitmapTask task = new LoadBitmapTask(name, null, false, priority);
        if (mLoadImmediate) {
            if (mExecutorService2 != null) {
                mExecutorService2.execute(task);
            }
        } else {
            mLoadTasks.add(task);
        }
    }

    public void excutuLoadTasks() {
        if (mExecutorService2 != null) {
            Collections.sort(mLoadTasks, new TaskComparator());
            for (int i = 0; i < mLoadTasks.size(); i++) {
                mExecutorService2.execute(mLoadTasks.get(i));
            }
        }
        mLoadImmediate = true;
    }

    public void setBitmap(String name, ImageView view) {
        setBitmap(name, view, false);
    }

    public void setBitmap(String name, Object target, boolean isMask) {
        synchronized (mBitmaps) {
            if (name != null && mBitmaps.get(name) != null && !mBitmaps.get(name).isRecycled()) {
                if (target instanceof ImageElementView) {
                    ImageElementView elementView = (ImageElementView) target;
                    if (isMask) {
                        elementView.setMask(mBitmaps.get(name));
                        elementView.requestLayout();
                    } else {
                        elementView.setImage(mBitmaps.get(name));
                    }
                } else if (target instanceof ImageView) {
                    ((ImageView) target).setImageBitmap(mBitmaps.get(name));
                }
            } else {
                load(name, target, isMask);
            }
        }
    }

    public Bitmap getCachedBitmap(String element) {
        synchronized (mBitmaps) {
            return mBitmaps.get(element);
        }
    }

    public void printRuntime(String msg) {
        Logger.d(TAG, "time_cast: " + msg + " " + (System.currentTimeMillis() - mStartTime) + "ms");
        mStartTime = System.currentTimeMillis();
    }

    public boolean getThemeChange() {
        return mIsThemeChange;
    }

    public void setThemeChange(boolean _value) {
        mIsThemeChange = _value;
    }
}
