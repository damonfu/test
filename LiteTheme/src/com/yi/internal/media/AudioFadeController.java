package com.yi.internal.media;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

public class AudioFadeController {
    private static final String TAG = "AudioFadeController";

    private static AudioFadeController sInstance;
    private static Object sLock = new Object();

    public  static final int FADE_IN = 0;
    public  static final int FADE_OUT = 1;

    private static final int MIN_VOLUME_INDEX = 1;

    private static final int DEFAULT_INTERVAL = 1000;
    private static final int VOLUME_FADE_STEP = 1;

    private static final int STREAM_FLAG = 0;

    private static HashMap<Integer, Integer> mVolumeMap;
    private static HashMap<Integer, OnFadeCompleteListener> mListenerMap;

    private AudioManager mAudioManager;

    public static AudioFadeController instance(Context context) {
        if (sInstance == null) {
            synchronized(sLock) {
                if (sInstance == null) {
                    sInstance = new AudioFadeController(context);
                }
            }
        }

        return sInstance;
    }

    private AudioFadeController(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void fadeStream(int streamType, int fadeDirection,
            OnFadeCompleteListener listener) {
        fadeStream(streamType, fadeDirection, DEFAULT_INTERVAL, listener);
    }

    public void fadeStream(int streamType, int fadeDirection, int fadeInterval,
            OnFadeCompleteListener listener) {
        initMaps(streamType, listener);

        if (fadeDirection == FADE_OUT) {
            mAudioManager.setStreamVolume(streamType, MIN_VOLUME_INDEX, STREAM_FLAG);
        }

        Message msg = mFadeInOutHandler.obtainMessage(fadeDirection, streamType, fadeInterval);
        mFadeInOutHandler.sendMessage(msg);
    }

    public void cancelFade(int streamType) {
        int volume = -1;
        synchronized(sLock) {
            if (mVolumeMap != null && mVolumeMap.containsKey(streamType))
                volume = mVolumeMap.remove(streamType);

            if (mListenerMap != null && mListenerMap.containsKey(streamType))
                mListenerMap.remove(streamType);

            if (volume != -1)
                mAudioManager.setStreamVolume(streamType, volume, STREAM_FLAG);
        }
    }

    private void initMaps(int streamType, OnFadeCompleteListener listener) {
        synchronized(sLock) {
            if (mVolumeMap == null) {
                mVolumeMap = new HashMap<Integer, Integer>();
            }
            if (mListenerMap == null) {
                mListenerMap = new HashMap<Integer, OnFadeCompleteListener>();
            }

            if (mVolumeMap.containsKey(streamType)) {
                mVolumeMap.remove(streamType);
            }
            if (mListenerMap.containsKey(streamType)) {
                mListenerMap.remove(streamType);
            }
            int volume = mAudioManager.getStreamVolume(streamType);
            Log.i(TAG, "Stream type is :" + streamType + " ,volume is :" + volume);

            mVolumeMap.put(streamType, volume);
            mListenerMap.put(streamType, listener);
        }
    }

    private Handler mFadeInOutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized(sLock) {
                int streamType = msg.arg1;
                int fadeInterval = msg.arg2;
                if (mVolumeMap == null || !mVolumeMap.containsKey(streamType))
                    return;

                int currentVolume = mAudioManager.getStreamVolume(streamType);
                int maxVolume = mVolumeMap.get(streamType);
                OnFadeCompleteListener listener = mListenerMap.get(streamType);
                switch (msg.what) {
                case FADE_IN:
                    currentVolume -= VOLUME_FADE_STEP;
                    if (currentVolume >= MIN_VOLUME_INDEX) {
                        mFadeInOutHandler.sendMessageDelayed(mFadeInOutHandler.
                                obtainMessage(msg.what, streamType, fadeInterval), fadeInterval);
                    } else {
                        currentVolume = mVolumeMap.get(streamType);
                        if (listener != null)
                           listener.onFadeComplete();
                    }
                    mAudioManager.setStreamVolume(streamType, currentVolume, STREAM_FLAG);
                    break;
                case FADE_OUT:
                    currentVolume += VOLUME_FADE_STEP;
                    if (currentVolume <= maxVolume) {
                        mFadeInOutHandler.sendMessageDelayed(mFadeInOutHandler.
                                obtainMessage(msg.what, streamType, fadeInterval), fadeInterval);
                    } else {
                        currentVolume = mVolumeMap.get(streamType);
                        if (listener != null)
                           listener.onFadeComplete();
                    }
                    mAudioManager.setStreamVolume(streamType, currentVolume, STREAM_FLAG);
                    break;
                }
            }
        }
    };

    public interface OnFadeCompleteListener {
        public void onFadeComplete();
    }
}
