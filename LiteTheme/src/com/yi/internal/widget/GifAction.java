package com.yi.internal.widget;

/*define action of animate GIF image display*/

public interface GifAction {
    /*
     * Observer of GIF image decode,whether decode is success or not. in
     * GIFDecoder class,call this function to display GIF image after decode
     * @param parseStatus: Whether GIF decode succeed or not,succeed: true;
     * failed:false
     * @param frameIndex: current index of frame in decoding. If all decode work
     * is done,the index would be -1*
     */
    public void parseOk(boolean parseStatus, int frameIndex);
}
