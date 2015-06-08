
package com.baidu.themeanimation.element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.os.Handler;

import com.baidu.themeanimation.element.ImageElement.ImageElementView;
import com.baidu.themeanimation.receiver.MusicControlReceiver;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Logger;

public class MusicControlElement extends VisibleElement {
    private final static String TAG = "MusicControl";
    /*
     * which contain at least 4 button, 1 image and 1 text 4 button: music_prev,
     * music_next, music_play, music_pause 1 image: music_album_cover i text:
     * music_display
     */

    private Boolean mAutoShow = true; // the music application is running, this
                                      // widget will be display on the lock
                                      // screen
    private List<VisibleElement> mElements;

    private String mDefAlbumCoverSrc;

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_MUSIC_CONTROL.equals(tagName)
                || Constants.TAG_MUSIC_CONTROL_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new MusicControlElement();
    }

    public Boolean getAutoShow() {
        return mAutoShow;
    }

    public void setAutoShow(Boolean autoShow) {
        this.mAutoShow = autoShow;
    }

    public void setAutoShow(String autoShow) {
        setAutoShow(Boolean.valueOf(autoShow));
    }

    public void addElement(VisibleElement element) {
        if (element != null) {
            if (mElements == null) {
                mElements = new ArrayList<VisibleElement>();
            }

            mElements.add(element);
        }
    }

    public void setDefAlbumCover(String src){
        mDefAlbumCoverSrc = src;
    }

    public String getDefAlbumCover(){
        return mDefAlbumCoverSrc;
    }

    @Override
    public void startAnimations() {
        if (mElements != null) {
            for (int i = 0; i < mElements.size(); i++) {
                mElements.get(i).startAnimations();
            }
        }
    }

    private MusicControlView mMusicControlView = null;

    @Override
    public View generateView(Context context, Handler handler) {
        if (mMusicControlView == null) {
            mMusicControlView = new MusicControlView(context, handler);
        }

        Logger.v(TAG, "music control generate view!");
        setView(mMusicControlView);
        return mMusicControlView;
    }

    private static final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
    static {
        mBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mBitmapOptions.inDither = false;
    }

    public class MusicControlView extends RelativeLayout implements OnClickListener {

        Context mContext;
        Handler mHandler;

        public MusicControlView(Context context, Handler handler) {
            super(context);

            mContext = context;
            mHandler = handler;
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(MusicControlElement.this.getX(),
                    MusicControlElement.this.getY(), 0, 0);

            setLayoutParams(layoutParams);

            Logger.v(TAG, "music control x=" + getX() + ", y=" + getY());

            initView();
        }

        private View mPrevBtn;
        private View mNextBtn;
        private View mPlayBtn;
        private View mPauseBtn;

        private VisibleElement mPrevBtnElement;
        private VisibleElement mNextBtnElement;
        private VisibleElement mPlayBtnElement;
        private VisibleElement mPauseBtnElement;
        private VisibleElement mAlbumCoverElement;

        private ImageElementView mAlbumCover;
        private Bitmap mDefaultAlbum;
        private TextElement mAlbumTextElement;

        private static final int PREV_BUTTON = 0;
        private static final int NEXT_BUTTON = 1;
        private static final int PLAY_BUTTON = 2;
        private static final int PAUSE_BUTTON = 3;

        public void initView() {
            if (mElements != null) {
                VisibleElement element;
                View view;
                String name;
                for (int i = 0; i < mElements.size(); i++) {
                    element = mElements.get(i);
                    name = element.getName();
                    view = element.generateView(mContext, mHandler);

                    if (name != null) {
                        if (element instanceof ButtonElement) {
                            view.setOnClickListener(this);
                        }

                        if (element instanceof TextElement) {
                            mAlbumTextElement = (TextElement) element;
                        }

                        if (name.equals("music_prev")) {
                            mPrevBtnElement = element;
                            mPrevBtn = view;
                            mPrevBtn.setId(PREV_BUTTON);
                        } else if (name.equals("music_next")) {
                            mNextBtnElement = element;
                            mNextBtn = view;
                            mNextBtn.setId(NEXT_BUTTON);
                        } else if (name.equals("music_play")) {
                            mPlayBtnElement = element;
                            mPlayBtn = view;
                            mPlayBtn.setId(PLAY_BUTTON);
                        } else if (name.equals("music_pause")) {
                            mPauseBtnElement = element;
                            mPauseBtn = view;
                            mPauseBtn.setId(PAUSE_BUTTON);
                        } else if (name.equals("music_album_cover")) {
                            mAlbumCoverElement = element;
                            mAlbumCover = (ImageElementView) view;
                            mDefaultAlbum = mAlbumCover.getCurrentBitmap();
                        }
                    }

                    addView(view);
                }

                updateView(null);

                try {
                    MusicControlReceiver.registerMusicPlayCB(this,
                            MusicControlView.class.getMethod("updateView", Bundle.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateView(Bundle musicBundle) {
            // get the current music playing state
            boolean isPlay = false;
            long id = -1;
            String albumString = null;
            if (musicBundle != null) {
                id = musicBundle.getLong("id");

                if (musicBundle.getBoolean("playstate")) {
                    isPlay = true;
                }

                albumString = musicBundle.getString("album");

                Set<String> keySet = musicBundle.keySet();
                for (String key : keySet) {
                    Logger.v("music", "3:" + key + "=" + musicBundle.get(key) + ", "
                            + musicBundle.get(key).getClass());
                }
            }

            if (isPlay) {
                // mPlayBtn.setVisibility(View.GONE);
                // mPauseBtn.setVisibility(View.VISIBLE);
                mPlayBtnElement.setVisibility(false);
                mPauseBtnElement.setVisibility(true);
            } else {
                // mPlayBtn.setVisibility(View.VISIBLE);
                // mPauseBtn.setVisibility(View.GONE);
                mPlayBtnElement.setVisibility(true);
                mPauseBtnElement.setVisibility(false);
            }

            // set album title and cover
            if (id >= 0) {
                mAlbumTextElement.setText(albumString);
                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri uri = ContentUris.withAppendedId(sArtworkUri, id);
                InputStream in = null;
                try {
                    in = mContext.getContentResolver().openInputStream(uri);
                } catch (Exception ee) {
                    Logger.w("music", "get album failed!+" + ee.toString());
                }

                try {
                    if (mAlbumCover != null) {
                        Bitmap bitmap = null;
                        if (in != null) {
                            bitmap = BitmapFactory.decodeStream(in, null, mBitmapOptions);
                        }

                        // Drawable bitmapDrawable =
                        // BitmapDrawable.createFromStream(in, null);

                        if (bitmap != null) {
                            Logger.v("music", "set album cover!" + " width=" + bitmap.getWidth()
                                    + ", height=" + getHeight());
                            mAlbumCover.setImage(bitmap);

                        } else {
                            mAlbumCover.setImage(mDefaultAlbum);
                        }
                    }
                } catch (Exception e) {
                    Logger.w("music", "get album cover!+" + e.toString());
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e) {
                        Logger.w("music", "get album cover!+" + e.toString());
                    }
                }
            }
        }

        public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
        public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
        public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
        public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";

        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case PREV_BUTTON:
                    intent.setAction(PREVIOUS_ACTION);
                    break;

                case NEXT_BUTTON:
                    intent.setAction(NEXT_ACTION);
                    break;

                case PLAY_BUTTON:
                    intent.setAction(TOGGLEPAUSE_ACTION);
                    // mPlayBtn.setVisibility(View.GONE);
                    // mPauseBtn.setVisibility(View.VISIBLE);
                    mPlayBtnElement.setVisibility(false);
                    mPauseBtnElement.setVisibility(true);
                    break;

                case PAUSE_BUTTON:
                    intent.setAction(PAUSE_ACTION);
                    // mPlayBtn.setVisibility(View.VISIBLE);
                    // mPauseBtn.setVisibility(View.GONE);
                    mPlayBtnElement.setVisibility(true);
                    mPauseBtnElement.setVisibility(false);
                    break;

                default:
                    break;
            }

            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        String name = TAG;
        return TAG;

        // return super.getName();
    }

}
