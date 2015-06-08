package yi.support.v1.utils;

import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.view.View;


public class ViewCapturer {

    public static Bitmap snapshot(View view) {
        final boolean isDrawingCacheEnabled = view.isDrawingCacheEnabled();
        if (!isDrawingCacheEnabled) {
            view.setDrawingCacheEnabled(true);
        }

        final Bitmap snapshot = view.getDrawingCache();

        if (!isDrawingCacheEnabled) {
            try {
                final Field field = View.class.getDeclaredField("mUnscaledDrawingCache");
                final boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(view, null);
                field.setAccessible(accessible);
            } catch (Exception e) {}

            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(false);
        }

        return snapshot;
    }

}