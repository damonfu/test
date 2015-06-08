package com.yi.internal.widget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.os.Message;
import android.util.Log;

import java.nio.IntBuffer;


/*decode GIF image to several frames for display. The decoder is a sub thread to do the decode work*/
public class GifDecoder extends Thread {
    @SuppressWarnings("unused")
    private static final String TAG = "GifDecoder";

    /* static variables */
    public static final int STATUS_PARSING = 0; // in decoding

    public static final int STATUS_FORMAT_ERROR = 1; // invalid image format

    public static final int STATUS_OPEN_ERROR = 2; // open image failed

    public static final int STATUS_OUT_MEMORY = 3;

    public static final int STATUS_FINISHED = -1; // decode succeed

    private static final int MaxStackSize = 4096; // max decoder pixel stack
                                                  // size

    private static final int MAX_FRAME_WAIT = 5; // max frame the view wait to
                                                 // show process bar

    /* automatics variables */
    public int imgWidth = 0; // full image width

    public int imgHeight = 0; // full image height

    private InputStream in = null;

    private Bitmap[] image = new Bitmap[3]; // current frame

    private Bitmap lastImage; // previous frame

    private Context mContext;

    private GifFrame currentFrame;

    private GifFrame gifFrame; // frames read from current file

    private GifAction action = null;
    
    private Handler mExternalHandler;

    private Uri mUri = null;

    private int ix, iy, iw, ih; // current image rectangle

    private int lrx, lry, lrw, lrh;

    private int status;

    private int loopCount = 1; // iterations; 0 : repeat forever

    private int bgIndex; // background color index

    private int bgColor; // background color

    private int lastBgColor; // previous background color

    private int pixelAspect; // pixel aspect ratio

    private int dispose = 0; // last graphic control extension info

    private int lastDispose = 0; // 0: no action; 1: leave in place; 2: restore
                                 // to background; 3: restore to previous

    private int delay = 0; // delay in milliseconds

    private int transIndex; // transparent color index

    private int frameCount; // the number of image frame

    private int frameTotalCount;

    private int blockSize = 0; // block size

    private int gctSize; // size of global color table

    private int lctSize; // local color table size

    private boolean gctFlag; // whether global color table is used

    private boolean lctFlag; // local color table flag

    private boolean interlace; // interlace flag

    private boolean isShow = false;

    private boolean transparency = false; // use transparent color

    private boolean readNotify = true; // wangyan

    private boolean restartNotify = true; // wangyan

    private boolean firstDecodeFlag = true; // wangyan

    private boolean isRun = false;

    private int[] gct; // global color table

    private int[] lct; // local color table

    private int[] act; // active color table

    private byte[] block = new byte[256]; // current data block

    private byte[] gifData = null; // GIF image data

	private short[] prefix;

	private byte[] suffix;

	private byte[] pixels;

	private byte[] pixelStack;

    /* construct function */
    public GifDecoder(byte[] data, GifAction act) { // raw data
        gifData = data;
        action = act;
        initData();
    }

    public GifDecoder(InputStream is, GifAction act, Context context) { // stream
        in = is;
        action = act;
        mContext = context.getApplicationContext();
        initData();
    }

    public GifDecoder(Uri uri, GifAction act, Context context) {
        mUri = uri;
        mContext = context.getApplicationContext();
        action = act;
        initData();
    }
    
    private void initData() {
    	prefix = new short[MaxStackSize];
        suffix = new byte[MaxStackSize];
        pixelStack = new byte[MaxStackSize + 1];
    }

    /* Thread run. read GIF stream or data */
    public void run() {
        // add by wangyan08
        if (null == mUri && in == null)
            return;
        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        } catch (Exception e) {
            Log.e(TAG, "GifDecoder Thread: setThreadPriority " + e);
        }
        
        firstDecodeFlag = true;
        isRun = true;

        ContentResolver cr = mContext.getContentResolver();
        try {
            in = in == null ? cr.openInputStream(mUri) : in;
        } catch (java.io.FileNotFoundException ept) {
            Log.e(TAG, "Error to Gif play: FileNotFoundException " + ept);
            int waitforfileopen = 0;
            while (waitforfileopen++ < 3) {
                try {
                    Thread.sleep(300);
                    in = cr.openInputStream(mUri);
                    if (null != in)
                        break;
                } catch (Exception e) {
                    Log.e(TAG, "Exception " + e);
                }
            }
        }
        if (null != in) {
            readStream();
            Log.d(TAG, "finish the gif decoder thread!!  ");
            if (STATUS_FORMAT_ERROR == status) {
                return;
            }
        } else if (null != gifData) {
            readByte();
        } else {
            return;
        }
    }

    /* free resource */
    public void stopGifDecoder() {
        isRun = false;
        try {
            this.interrupt();
        } catch (SecurityException e) {
            Log.e(TAG, "Error recycle:stop GifDecoder thread " + e);
        }
    }

    public void free() {
        GifFrame gf = gifFrame;
        while (null != gf) {
            /*
             * if (null != gf.image) { gf.image.recycle();
             * Log.d("wangyan*****free", "gf.image.recycle();"); }
             */
            gf.image = null;
            gf = null;
            gifFrame = gifFrame.nextFrame;
            gf = gifFrame;
        }
        if (in != null && mUri != null) {
            try {
                in.close();
            } catch (Exception ex) {
            }
            in = null;
        }
        for (int i = 0; i < 3; i++) {
            if (image[i] != null) {
                image[i].recycle();
                image[i] = null;
            }
        }
        gifData = null;
        mContext = null;
        lastImage = null;
        currentFrame = null;
        gifFrame = null;
        action = null;
        mUri = null;
        gct = null;
        lct = null;
        act = null;
        block = null;
        prefix = null;
        suffix = null;
        pixelStack = null;
        pixels = null;
    }
    
    public short[] getPrefix() {
        return prefix;
    }

    public byte[] getSuffix() {
        return suffix;
    }

    public byte[] getPixelStack() {
        return pixelStack;
    }

    public byte[] getPixels() {
        return pixels;
    }

    public byte[] getPixels(int npix) {
        if (null == pixels) {
            try {
                pixels = new byte[npix];
            } catch (OutOfMemoryError eMem) {
                Log.e(TAG, "getPixels: new pixels buffer out of memory!");
                pixels = null;
            }
        }
        return pixels;
    }

    public void allocatePixels(int npix) {
        pixels = null;
        try {
            pixels = new byte[npix];
        } catch (OutOfMemoryError eMem) {
            Log.e(TAG, "allocatePixels: new pixels buffer out of memory!");
            pixels = null;
        }
    }

    /* return current status */
    public int getStatus() {
        return status;
    }

    /*
     * Whether decode succeed.
     * @return succeed: true; failed: false
     */
    public boolean parseOk() {
        return (STATUS_FINISHED == status);
    }

    /* get the delay time of some frame */
    public int getDelay(int index) {
        delay = -1;
        if ((index >= 0) && (index < /* frameCount */frameTotalCount)) {
            GifFrame f = getFrame(index);
            if (null != f)
                delay = f.delay;
        }

        return delay;
    }

    /* get delay time of all of the frames */
    public int[] getDelays() {
        GifFrame f = gifFrame;
        int[] d = new int[/* frameCount */frameTotalCount];
        int i = 0;

        while ((null != f) && (i < /* frameCount */frameTotalCount)) {
            d[i] = f.delay;
            f = f.nextFrame;
            i++;
        }

        return d;
    }

    public int getFrameCount() {
        return /* frameCount */frameTotalCount;
    }

    /* get the first frame */
    public Bitmap getImage() {
        return getFrameImage(0);
    }

    /* get the frame according to the index parameter */
    public Bitmap getFrameImage(int index) {
        /*
         * GifFrame frame = getFrame(index); if (frame == null) { return null; }
         * else { return frame.image; }
         */
        return image[index % 3];
    }

    public GifFrame getCurrentFrame() {
        return currentFrame;
    }

    /*
     * get the GIF frame according to the indexeach frame includes the image and
     * display delay time
     */
    public GifFrame getFrame(int index) {
        GifFrame frame = gifFrame;
        int i = 0;
        while (frame != null) {
            if (i == index) {
                return frame;
            } else {
                frame = frame.nextFrame;
            }
            i++;
        }
        return null;
    }

    public int getLoopCount() {
        return loopCount;
    }

    /* reset. come back to the first frame */
    public void reset() {
        currentFrame = gifFrame;
    }

    /*
     * get next frame. after done this, can get next frame by calling
     * getCurrentFrame function
     */
    public GifFrame next() {
        if (false == isShow) {
            isShow = true;
            readNotify = true;
            return gifFrame;
        } else {
            synchronized (this) {
                readNotify = true;
                notify();
            }
            if (STATUS_PARSING == status) {
                if (currentFrame == null) {
                    currentFrame = gifFrame; // show again
                } else if (currentFrame.nextFrame != null) {
                    currentFrame = currentFrame.nextFrame;
                }
                if (currentFrame!=null&&currentFrame.nextFrame == null) {
                    // isShow = false;
                    restartNotify = true;
                }
            } else {
                if (currentFrame != null) {
                    currentFrame = currentFrame.nextFrame;
                }
                if (currentFrame == null) {
                    currentFrame = gifFrame; // show again
                }
            }
            return currentFrame;
        }
    }

    /**/
    private void setPixels() {
        int[] dest = new int[imgWidth * imgHeight];
        // fill in starting image contents based on last image's dispose code
        if (lastDispose > 0) {
            if (3 == lastDispose) {
                // use image before last
                int n = frameCount - 2;
                if (n > 0) {
                    lastImage = getFrameImage(n - 1);
                } else {
                    lastImage = null;
                }
            }
            if ((lastImage != null) && (!lastImage.isRecycled())) {
                lastImage.getPixels(dest, 0, imgWidth, 0, 0, imgWidth, imgHeight);
                // copy pixels
                if (2 == lastDispose) {
                    // fill last image rect area with background color
                    int c = 0;
                    if (!transparency) {
                        c = lastBgColor;
                    }
                    for (int i = 0; i < lrh; i++) {
                        int n1 = (lry + i) * imgWidth + lrx;
                        int n2 = n1 + lrw;
                        for (int k = n1; k < n2; k++) {
                            dest[k] = c;
                        }
                    }
                }
            }
        }

        // copy each source line to the appropriate place in the destination
        int pass = 1;
        int inc = 8;
        int iline = 0;
        for (int i = 0; i < ih; i++) {
            int line = i;
            if (interlace) {
                if (iline >= ih) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            line += iy;
            if (line < imgHeight) {
                int k = line * imgWidth;
                int dx = k + ix; // start of line in dest
                int dlim = dx + iw; // end of dest line
                if ((k + imgWidth) < dlim) {
                    dlim = k + imgWidth; // past dest edge
                }
                int sx = i * iw; // start of line in source

                byte[] pixels = getPixels();
                if (null == pixels) {
                    status = STATUS_OUT_MEMORY;
                    return;
                }
                while (dx < dlim) {
                    // map color and insert in destination
                    int index = ((int) pixels[sx++]) & 0xff;
                    int c = act[index];
                    if (c != 0) {
                        dest[dx] = c;
                    }
                    dx++;
                }
            }
        }
        try {
            if (null == image[(frameCount - 1) % 3]) {
                image[(frameCount - 1) % 3] = Bitmap.createBitmap(imgWidth, imgHeight,
                        Config.ARGB_4444/* Config.RGB_565 */);
            }
            image[(frameCount - 1) % 3].setPixels(dest, 0, imgWidth, 0, 0, imgWidth, imgHeight);
            if (gifFrame == null) {
                gifFrame = new GifFrame(image[(frameCount - 1) % 3], delay, frameCount);
                currentFrame = gifFrame;
                // Log.d("GifDecoder****wangyan*****",
                // "get gifFrame index = "+frameCount);
            } else {
                GifFrame f = gifFrame;
                while (f.nextFrame != null) {
                    f = f.nextFrame;
                }
                f.nextFrame = new GifFrame(image[(frameCount - 1) % 3], delay, frameCount);
                // Log.d("GifDecoder****wangyan*****",
                // "get next Frame index = "+frameCount);
            }
        } catch (OutOfMemoryError eMem) {
            Log.e(TAG, "setPixels createBitmap out of memory error!!");
            status = STATUS_OUT_MEMORY;
        }
    }

    private void init() {
        status = STATUS_PARSING;
        frameCount = 0;
        frameTotalCount = 0;
        gifFrame = null;
        gct = null;
        lct = null;
    }

    private int read() {
        int curByte = 0;
        try {
            curByte = in.read();
        } catch (Exception e) {
            status = STATUS_FORMAT_ERROR;
        }
        return curByte;
    }

    private int readByte() {
        in = new ByteArrayInputStream(gifData);
        gifData = null;
        return readStream();
    }

    private int readShort() {
        // read 16-bit value, LSB first
        return read() | (read() << 8);
    }

    private int readStream() {
        while (isRun && (in != null)) {
            while (isRun && !restartNotify) {
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {
                }
            }
            init();
            readHeader();
            if (!err()) {
                readContents();
                if (action == null || status == STATUS_OUT_MEMORY)
                    break;
                if (frameCount < 0) {
                    status = STATUS_FORMAT_ERROR;
                    action.parseOk(false, -1);
                } else if (frameTotalCount <= 3) {
                    status = STATUS_FINISHED;
                    action.parseOk(true, -1);
                    break;
                } else {
                    // status = STATUS_FINISHED;
                    action.parseOk(true, -1);
                }
            } else {
                break;
            }
        }
        if (in == null && action != null) {
            status = STATUS_OPEN_ERROR;
            action.parseOk(false, -1);
        }
        /*
         * Message msg = new Message(); msg.what = GifView.GIF_DECODE_FINISHED;
         * mGifView.getMessageHandle().sendMessage(msg);
         */

        return status;
    }

    private int readBlock() {
        blockSize = read();
        int n = 0;
        if (blockSize > 0) {
            try {
                int count = 0;
                while (n < blockSize) {
                    count = in.read(block, n, blockSize - n);
                    if (count == -1) {
                        break;
                    }
                    n += count;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (n < blockSize) {
                status = STATUS_FORMAT_ERROR;
            }
        }
        return n;
    }

    private void resetFrame() {
        lastDispose = dispose;
        lrx = ix;
        lry = iy;
        lrw = iw;
        lrh = ih;
        lastImage = image[(frameCount - 1) % 3];
        lastBgColor = bgColor;
        dispose = 0;
        transparency = false;
        delay = 0;
        lct = null;
    }

    private int[] readColorTable(int ncolors) {
        int nbytes = 3 * ncolors;
        int[] tab = null;
        byte[] c = new byte[nbytes];
        int n = 0;
        try {
            n = in.read(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (n < nbytes) {
            status = STATUS_FORMAT_ERROR;
        } else {
            tab = new int[256]; // max size to avoid bounds checks
            int i = 0;
            int j = 0;
            while (i < ncolors) {
                int r = ((int) c[j++]) & 0xff;
                int g = ((int) c[j++]) & 0xff;
                int b = ((int) c[j++]) & 0xff;
                tab[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
        return tab;
    }

    private void readContents() {
        // read GIF file content blocks
        boolean done = false;
        while (isRun && !(done || err())) {
            int code = read();
            switch (code) {
                case 0x2C: // image separator
                    synchronized (this) {
                        if (!firstDecodeFlag || (firstDecodeFlag && frameTotalCount >= 3)) {
                            readNotify = false;
                            firstDecodeFlag = false;
                        }
                        while (isRun && !readNotify) {
                            try {
                                wait(30);
                            } catch (InterruptedException ept) {
                            }
                        }
                    }
                    readImage();
                    break;
                case 0x21: // extension
                    code = read();
                    switch (code) {
                        case 0xf9: // graphics control extension
                            readGraphicControlExt();
                            break;
                        case 0xff: // application extension
                            readBlock();
                            String app = "";
                            for (int i = 0; i < 11; i++) {
                                app += (char) block[i];
                            }
                            if (app.equals("NETSCAPE2.0")) {
                                readNetscapeExt();
                            } else {
                                skip(); // don't care
                            }
                            break;
                        default: // uninteresting extension
                            skip();
                    }
                    break;
                case 0x3b: // terminator
                    done = true;
                    frameCount = 0;
                    try {
                        if (null != in && mUri != null) {
                            in.close();
                            in = mContext.getContentResolver().openInputStream(mUri);
                        } else if (null != in) {
                        	in.reset();
                        }
                        
                    } catch (Exception ept) {
                        Log.e(TAG, "Error to gif play: FileNotFoundException " + ept);
                    }
                    restartNotify = false;
                    break;
                case 0x00: // bad byte, but keep going and see what happens
                    break;
                default:
                    status = STATUS_FORMAT_ERROR;
            }
            if (status == STATUS_OUT_MEMORY)
                break;
        }
    }

    private void readGraphicControlExt() {
        read(); // block size
        int packed = read(); // packed fields
        dispose = (packed & 0x1c) >> 2; // disposal method
        if (dispose == 0) {
            dispose = 1; // elect to keep old image if discretionary
        }
        transparency = (packed & 1) != 0;
        delay = readShort() * 10; // delay in milliseconds
        transIndex = read(); // transparent color index
        read(); // block terminator
    }

    private void readNetscapeExt() {
        do {
            readBlock();
            if (block[0] == 1) {
                // loop count sub-block
                int b1 = ((int) block[1]) & 0xff;
                int b2 = ((int) block[2]) & 0xff;
                loopCount = (b2 << 8) | b1;
            }
        } while ((blockSize > 0) && !err());
    }

    private void readHeader() {
        String id = "";
        try {
            in.reset();
        }catch (Exception e) {
        	
        }
        for (int i = 0; i < 6; i++) {
            id += (char) read();
        }
        if (!id.startsWith("GIF")) {
            status = STATUS_FORMAT_ERROR;
            return;
        }
        readLSD();
        if (gctFlag && !err()) {
            gct = readColorTable(gctSize);
            bgColor = gct[bgIndex];
        }
    }

    private void readImage() {
        ix = readShort(); // (sub)image position & size
        iy = readShort();
        iw = readShort();
        ih = readShort();
        int packed = read();
        lctFlag = (packed & 0x80) != 0; // 1 - local color table flag
        interlace = (packed & 0x40) != 0; // 2 - interlace flag
        // 3 - sort flag
        // 4-5 - reserved
        lctSize = 2 << (packed & 7); // 6-8 - local color table size
        if (lctFlag) {
            lct = readColorTable(lctSize); // read table
            act = lct; // make local table active
        } else {
            act = gct; // make global table active
            if (bgIndex == transIndex) {
                bgColor = 0;
            }
        }
        int save = 0;
        if (transparency) {
            save = act[transIndex];
            act[transIndex] = 0; // set transparent color if specified
        }
        if (act == null) {
            status = STATUS_FORMAT_ERROR; // no color table defined
        }
        if (err()) {
            return;
        }
        decodeImageData(); // decode pixel data
        if (status == STATUS_OUT_MEMORY)
            return;
        skip();
        if (err()) {
            return;
        }
        frameCount++;
        frameTotalCount = frameCount;
        // create new image to receive frame data
        // image = Bitmap.createBitmap(imgWidth, imgHeight, Config.ARGB_4444);
        // createImage(width, height);
        setPixels(); // transfer pixel data to image
        if (status == STATUS_OUT_MEMORY)
            return;
        /*
         * if (gifFrame == null) { gifFrame = new GifFrame(image, delay);
         * currentFrame = gifFrame; } else { GifFrame f = gifFrame;
         * while(f.nextFrame != null){ f = f.nextFrame; } f.nextFrame = new
         * GifFrame(image, delay); }
         */
        // frames.addElement(new GifFrame(image, delay)); // add image to frame
        // list
        if (transparency) {
            act[transIndex] = save;
        }
        resetFrame();
        if (null != action) {
            action.parseOk(true, frameTotalCount);
        }
        // add to show processbar
        /*
         * if (frameCount > MAX_FRAME_WAIT) { Message msg = new Message();
         * msg.what = GifView.GIF_PROGRESS_BAR_SHOW;
         * mGifView.getMessageHandle().sendMessage(msg); }
         */
    }

    private void readLSD() {
        // logical screen size
        imgWidth = readShort();
        imgHeight = readShort();
        // packed fields
        int packed = read();
        gctFlag = (packed & 0x80) != 0; // 1 : global color table flag
        // 2-4 : color resolution
        // 5 : gct sort flag
        gctSize = 2 << (packed & 7); // 6-8 : gct size
        bgIndex = read(); // background color index
        pixelAspect = read(); // pixel aspect ratio
    }

    private void decodeImageData() {
        int NullCode = -1;
        int npix = iw * ih;
        int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i, datum, data_size, first, top, bi, pi;
        byte[] pixels = getPixels(npix);
        byte[] suffix = getSuffix();
        byte[] pixelStack = getPixelStack();
        short[] prefix = getPrefix();
        if (null == pixels) {
            status = STATUS_OUT_MEMORY;
            return;
        }
        if (pixels.length < npix) {
            allocatePixels(npix);
        }
        /*
         * if (prefix == null) { prefix = new short[MaxStackSize]; } if (suffix
         * == null) { suffix = new byte[MaxStackSize]; } if (pixelStack == null)
         * { pixelStack = new byte[MaxStackSize + 1]; }
         */
        // Initialize GIF data stream decoder.
        data_size = read();
        clear = 1 << data_size;
        end_of_information = clear + 1;
        available = clear + 2;
        old_code = NullCode;
        code_size = data_size + 1;
        code_mask = (1 << code_size) - 1;
        for (code = 0; code < clear; code++) {
            prefix[code] = 0;
            suffix[code] = (byte) code;
        }

        // Decode GIF pixel stream.
        datum = bits = count = first = top = pi = bi = 0;
        for (i = 0; i < npix;) {
            if (top == 0) {
                if (bits < code_size) {
                    // Load bytes until there are enough bits for a code.
                    if (count == 0) {
                        // Read a new data block.
                        count = readBlock();
                        if (count <= 0) {
                            break;
                        }
                        bi = 0;
                    }
                    datum += (((int) block[bi]) & 0xff) << bits;
                    bits += 8;
                    bi++;
                    count--;
                    continue;
                }
                // Get the next code.
                code = datum & code_mask;
                datum >>= code_size;
                bits -= code_size;
                // Interpret the code
                if ((code > available) || (code == end_of_information)) {
                    break;
                }
                if (code == clear) {
                    // Reset decoder.
                    code_size = data_size + 1;
                    code_mask = (1 << code_size) - 1;
                    available = clear + 2;
                    old_code = NullCode;
                    continue;
                }
                if (old_code == NullCode) {
                    pixelStack[top++] = suffix[code];
                    old_code = code;
                    first = code;
                    continue;
                }
                in_code = code;
                if (code == available) {
                    pixelStack[top++] = (byte) first;
                    code = old_code;
                }
                while (code > clear) {
                    pixelStack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = ((int) suffix[code]) & 0xff;
                // Add a new string to the string table,
                if (available >= MaxStackSize) {
                    break;
                }
                pixelStack[top++] = (byte) first;
                prefix[available] = (short) old_code;
                suffix[available] = (byte) first;
                available++;
                if (((available & code_mask) == 0) && (available < MaxStackSize)) {
                    code_size++;
                    code_mask += available;
                }
                old_code = in_code;
            }

            // Pop a pixel off the pixel stack.
            top--;
            if (pi < pixels.length) {
                pixels[pi++] = pixelStack[top];
            }
            i++;
        }
        for (i = pi; i < npix && i < pixels.length; i++) {
            pixels[i] = 0; // clear missing pixels
        }
    }

    /* Skips variable length blocks up to and including next zero length block. */
    private void skip() {
        do {
            readBlock();
        } while ((blockSize > 0) && !err());
    }

    private boolean err() {
        return (status != STATUS_PARSING);
    }

}


/*define a frame of GIF image and the delay time between image to display*/
class GifFrame {

    public Bitmap image;    // a frame image of GIF
	public int delay;            // display delay time between two frames
    public int index;
	public GifFrame nextFrame = null;       //next frame

	/*GifFrame construct function
	 * @param image   a frame of GIF image
	 * @param delay    display delay time
	 * */
	public GifFrame(Bitmap image, int delay, int index) {
	   this.image = image;
	   this.delay = delay;
       this.index = index;
	}
}


