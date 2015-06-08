
package com.baidu.themeanimation.element;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Logger;

public class WallpaperElement extends ImageElement {
    private final static String TAG = "WallpaperElement";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_WALLPAPER.equals(tagName)
                || Constants.TAG_WALLPAPER_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new WallpaperElement();
    }

    public void changeWallPaper() {
        Logger.d(TAG, "changeWallPaper");
        Bitmap b = FileUtil.getInstance().getCurrentLockWallpaper();
        if (mImageElementView != null && b != null && !b.isRecycled())
            mImageElementView.setImage(b);
    }

    @Override
    public View generateView(Context context, Handler handler) {
       if (mImageElementView == null) {
            Logger.d(TAG, "generateView WallpaperElement View");

            mImageElementView = new ImageElementView(context, this,
                    Constants.IMAGE_TYPE_WALLPAPER);
            LayoutParams layoutParams = new LayoutParams(
                    (int) (FileUtil.REAL_SCREEN_WIDTH * FileUtil.WALLPAPER_SCALE),
                    (int) (FileUtil.REAL_SCREEN_HEIGHT * FileUtil.WALLPAPER_SCALE));
            layoutParams.setMargins(0, 0, 0, 0);
            mImageElementView.setLayoutParams(layoutParams);

            setView(mImageElementView);
        }
        return mImageElementView;
    }

}
