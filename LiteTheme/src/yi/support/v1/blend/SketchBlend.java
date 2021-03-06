package yi.support.v1.blend;

import yi.support.v1.blend.BlendService.BlendTask;
import yi.support.v1.blend.BlendService.Observer;
import android.graphics.Bitmap;



public class SketchBlend {

    private SketchBlend() {
        
    }

    public static Bitmap sketch(Bitmap bitmap, int shade) {
        Task sketchTask = new Task(bitmap, shade, null);
        sketchTask.run();
        return sketchTask.mSrcBitmap;
    }

    public static class Task extends BlendTask {
        private final int mShade;

        public Task(Bitmap bitmap, int shade, Observer observer) {
            super(bitmap, observer);
            mShade = shade;
        }

        @Override
        protected boolean blend() {
            return sketch(mSrcBitmap);
        }
        
        boolean sketch(Bitmap src) {
            if (src==null) {
                return false;
            }

            final int w = src.getWidth();
            final int h = src.getHeight();
            if (w==0 || h==0) {
                return false;
            }

            final int wh = w * h;
            int[] pixels = new int[wh];
            byte[] grays = new byte[wh];
            src.getPixels(pixels, 0, w, 0, 0, w, h);

            grayAndInverse(pixels, grays);
            
            if (isInterrupted()) return false;

            pixels = monoFastBlur(pixels, w, h, mShade);
            
            if (pixels == null) return false;
            
            deceaseColorCompound(pixels, grays);

            src.setPixels(pixels, 0, w, 0, 0, w, h);
            return true;
        }
        
        private static void grayAndInverse(int[] pixels, byte[] grays) {
            for (int i=0; i<pixels.length; i++) {
                final int color = pixels[i];
                final int gray = (((color & 0xFF0000) >> 16) * 3 + ((color & 0x00FF00) >> 8) * 6 + ((color & 0x0000FF)) * 1) / 10;
                grays[i] = (byte) gray;
                final int inverse = 255 - gray;
                pixels[i] = (pixels[i] & 0xFF000000) | inverse;
            }
        }
        
        public static void deceaseColorCompound(int[] pixels, byte[] grays) {
            for (int i=0; i<pixels.length; i++) {
                final int color = pixels[i];
                int blue = color & 0xFF;
                int gray = (int) (grays[i] & 0xFF);

                int temp = gray + gray * blue / (256 - blue);
                float ex = temp * temp * 1.0f/255/255;
                temp = (int) (temp * ex);

                gray = Math.min(temp, 255);
                pixels[i] = (pixels[i] & 0xFF000000) | (gray << 16) | (gray << 8) | gray;
            }
        }

        /*
         * (alpha) 0xFF000000 | (blue) 0xFF
         */
        private int[] monoFastBlur(final int[] pixels, final int w, final int h, final int radius) {
            if (pixels==null || radius<1) {
                return null;
            }

            if (w==0 || h==0) {
                return null;
            }
            
            final int wh = w * h;
            final int wm = w - 1;
            final int hm = h - 1;
            final int div = radius + radius + 1;
            
            int yp, yi, yw;
            yw = yi = 0;
            
            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int[] dvtable = new int[256 * divsum];
            for (int i = 0; i < 256 * divsum; i++) {
                dvtable[i] = (i / divsum);
            }

            int[] dvcolor = new int[wh];
            int[] vmin = new int[Math.max(w, h)];

            int[] rgbStack = new int[div];
            int stackpointer;
            int r1 = radius + 1;
            int /*rsum, gsum,*/ bsum;
            int /*rinsum, ginsum,*/ binsum;
            int /*routsum, goutsum,*/ boutsum;

            for (int y = 0; y < h; y++) {
                /*rsum = gsum =*/ bsum = 
                /*rinsum = ginsum =*/ binsum = 
                /*routsum = goutsum =*/ boutsum = 0;
                
                // sum all colors in radius
                for (int i = -radius; i <= radius; i++) {
                    // pick up RGB and store to array
                    final int color = rgbStack[i + radius] = pixels[yi + Math.min(wm, Math.max(i, 0))];
                    //final int r = (color & 0xFF0000) >> 16;
                    //final int g = (color & 0x00FF00) >> 8;
                    final int b = (color & 0x0000FF);

                    final int rbs = r1 - Math.abs(i);
                    //rsum += (r * rbs);
                    //gsum += (g * rbs);
                    bsum += (b * rbs);

                    if (i > 0) {
                        //rinsum += r;
                        //ginsum += g;
                        binsum += b;
                    } else {
                        //routsum += r;
                        //goutsum += g;
                        boutsum += b;
                    }
                }
                stackpointer = radius;

                // 
                for (int x = 0; x < w; x++) {
                    // set RGB priority
                    dvcolor[yi] = /*(dvtable[rsum] << 16) | (dvtable[gsum] << 8) |*/ dvtable[bsum];

                    //rsum -= routsum;
                    //gsum -= goutsum;
                    bsum -= boutsum;

                    int pos = stackpointer - radius + div;
                    int rgb = rgbStack[pos % div];
                    //routsum -= (rgb & 0xFF0000) >> 16;
                    //goutsum -= (rgb & 0x00FF00) >> 8;
                    boutsum -= (rgb & 0x0000FF);

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    int color = pixels[yw + vmin[x]];
                    rgbStack[pos % div] = color;
                    //int r = (color & 0xFF0000) >> 16;
                    //int g = (color & 0x00FF00) >> 8;
                    int b = (color & 0x0000FF);

                    //rinsum += r;
                    //ginsum += g;
                    binsum += b;

                    //rsum += rinsum;
                    //gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    color = rgbStack[(stackpointer) % div];
                    //r = (color & 0xFF0000) >> 16;
                    //g = (color & 0x00FF00) >> 8;
                    b = (color & 0x0000FF);

                    //routsum += r;
                    //goutsum += g;
                    boutsum += b;

                    //rinsum -= r;
                    //ginsum -= g;
                    binsum -= b;

                    yi++;
                }
                yw += w;

                if (isInterrupted()) return null;
            }

            for (int x = 0; x < w; x++) {
                /*rsum = gsum =*/ bsum = 
                /*rinsum = ginsum =*/ binsum = 
                /*routsum = goutsum =*/ boutsum = 0;

                yp = -radius * w;

                for (int i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    final int color = rgbStack[i + radius] = dvcolor[yi];
                    //final int r = (color & 0xFF0000) >> 16;
                    //final int g = (color & 0x00FF00) >> 8;
                    final int b = (color & 0x0000FF);

                    final int rbs = r1 - Math.abs(i);
                    //rsum += (r * rbs);
                    //gsum += (g * rbs);
                    bsum += (b * rbs);

                    if (i > 0) {
                        //rinsum += r;
                        //ginsum += g;
                        binsum += b;
                    } else {
                        //routsum += r;
                        //goutsum += g;
                        boutsum += b;
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;

                for (int y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xFF000000 & pix[yi] )
                    pixels[yi] = (0xFF000000 & pixels[yi]) | /*(dvtable[rsum] << 16) | (dvtable[gsum] << 8) |*/ dvtable[bsum];

                    //rsum -= routsum;
                    //gsum -= goutsum;
                    bsum -= boutsum;

                    int pos = stackpointer - radius + div;
                    int rgb = rgbStack[pos % div];
                    //routsum -= (rgb & 0xFF0000) >> 16;
                    //goutsum -= (rgb & 0x00FF00) >> 8;
                    boutsum -= (rgb & 0x0000FF);

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    final int p = x + vmin[y];
                    int color = rgbStack[pos % div] = dvcolor[p];
                    //int r = (color & 0xFF0000) >> 16;
                    //int g = (color & 0x00FF00) >> 8;
                    int b = (color & 0x0000FF);

                    //rinsum += r;
                    //ginsum += g;
                    binsum += b;

                    //rsum += rinsum;
                    //gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    color = rgbStack[stackpointer];
                    //r = (color & 0xFF0000) >> 16;
                    //g = (color & 0x00FF00) >> 8;
                    b = (color & 0x0000FF);

                    //routsum += r;
                    //goutsum += g;
                    boutsum += b;

                    //rinsum -= r;
                    //ginsum -= g;
                    binsum -= b;

                    yi += w;
                }

                if (isInterrupted()) return null;
            }

            return pixels;
        }
    }

}
