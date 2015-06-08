package com.yi.internal.widget;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/*GifImageView: Animate GIF image display,functions of which is similar to other Views of Android(such as ImageView)
 * If GIF image is too large,there will be some problems with GIF decoder */
public class GifImageView extends ImageView implements GifAction {
	@SuppressWarnings("unused")
	/* variables */
	private static final String TAG = "GifImageView";

	private GifDecoder gifDecoder = null; // GIF Decoder

	private Context mContext = null;

	private DrawThread drawThread = null;

	private Bitmap currentImage = null; // The current image to draw

	private GifImageType animationType = GifImageType.WAIT_FINISH; // GifImageType.SYNC_DECODER;//GifImageType.WAIT_FINISH;

	private InputStream mIs = null;
	
	private Uri mUri = null;

	private volatile boolean isRun = false; // add volatile

	private boolean pause = false;
//
//                    // Forget the drawable previously used.
//                    mPhoto.setTag(null);
//                    // Show empty screen for a moment.
//                    mPhoto.setVisibility(View.INVISIBLE);
//                    // Load the image with a callback to update the image state.
//                    // When the load is finished, onImageLoadComplete() will be called.
//                    Log.w(LOG_TAG, "onImageLoadComplete. begin ");
//                    ContactsAsyncHelper.startObtainPhotoAsync(TOKEN_UPDATE_PHOTO_FOR_CALL_STATE,
//                        getContext(), personUri, this, new AsyncLoadCookie((ImageView)mCallInfoContainer.findViewById(R.id.ongoing_photo), info, call));
//
//                    // If the image load is too slow, we show a default avatar icon afterward.
//                    // If it is fast enough, this message will be canceled on onImageLoadComplete().
//                    mHandler.removeMessages(MESSAGE_SHOW_UNKNOWN_PHOTO);
//                    mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_UNKNOWN_PHOTO, MESSAGE_DELAY);

	private int mImgWidth = 0;

	private int mImgHeight = 0;

	private boolean mIfAdjustViewBounds;

	private final int MSG_REDRAW = 100;

	/**
	 * The way to display the GIF animation If the GIF image is large, it will
	 * spend more time on decode In the decoding process, how to display the
	 * animation.
	 */
	static public enum GifImageType {
		WAIT_FINISH(0), // Don't show image in the decoding process, show it
						// after all the decode work done
		SYNC_DECODER(1), // Synchronized with decoder to show picture
		COVER(2); // just show first frame of GIF image in the decoding process.

		GifImageType(int i) {
			nativeInt = i;
		}

		final int nativeInt;
	}

	/* construct function */
	public GifImageView(Context context) {
		super(context);
	}

	public GifImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * set image by inputstream and start to decode
	 * 
	 * @param is
	 *            - the GIF animation to display
	 */
	private void setGifDecoderImage(InputStream is, Context context) {
		clear();
		if (checkAndParseImageDimention(is)) {
			this.animationType = GifImageType.COVER;
			showStatic();
		} else {
			this.animationType = this.animationType == GifImageType.COVER ? GifImageType.WAIT_FINISH
					: this.animationType;
			if (gifDecoder == null) {
				gifDecoder = new GifDecoder(is, this, context);
				pause = true;
				gifDecoder.start();
			}
		}
	}

	private void setGifDecoderImage(Uri uri, Context context) {
		clear();
		mUri = uri;
		if (checkAndParseImageDimention(uri)) {
			this.animationType = GifImageType.COVER;
			showStatic();
		} else {
			this.animationType = this.animationType == GifImageType.COVER ? GifImageType.WAIT_FINISH
					: this.animationType;
			if (gifDecoder == null) {
				gifDecoder = new GifDecoder(uri, this, context);
				pause = true;
				gifDecoder.start();
			}
		}
	}

	private void showStatic() {
			int width = GifImageView.this.getWidth();
			int height = GifImageView.this.getHeight();
			if (width <= 0 || height <= 0) {
				return;
			}
			double paraWidth = mImgWidth / width;
			double paraHeight = mImgHeight / height;
			double DoubleWidthRatio = Math.ceil(paraWidth);
			double DoubleHeightRatio = Math.ceil(paraHeight);
			int widthRatio = (int) DoubleWidthRatio;
			int heightRatio = (int) DoubleHeightRatio;
			final int ratio = Math.max(widthRatio, heightRatio);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = ratio;
			opts.inJustDecodeBounds = false;
			try {
				if (currentImage != null && !currentImage.isRecycled())
					currentImage.recycle();
				if (mIs != null)
					mIs.mark(Integer.MAX_VALUE);
					currentImage = BitmapFactory.decodeStream(mIs,
							null, opts);
					mIs.reset();
				this.setImageBitmap(currentImage);
			} catch (Exception eMem) {
				Log.e(TAG, "current image decodeFile out of memory");
			}
	}

	private boolean checkAndParseImageDimention(Uri uri) {
		InputStream is = null;
		boolean result = true;
		try {
			is = this.getContext().getContentResolver().openInputStream(uri);
			result = checkAndParseImageDimention(is);// Clear canvas

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private void resetStream(InputStream in) {
		try {
			if (in != null)
		        in.reset();
		} catch (Exception e) {
			
		}
	}
	
	private boolean checkAndParseImageDimention(InputStream in) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		resetStream(in);
		
		in.mark(0xFFFFFFFF);
		Bitmap gifBmp = BitmapFactory.decodeStream(in, null, options);
		resetStream(in);

		mImgWidth = options.outWidth;
		mImgHeight = options.outHeight;
		mIs = in;
		
		return mImgWidth * mImgHeight > 1000000;
	}

	/**
	 * set image by inputstream
	 * 
	 * @param is
	 *            image stream
	 */
	public void setGifImage(InputStream is, Context context) {
		setGifDecoderImage(is, context);
	}

	/**
	 * set GIF image by resource id
	 * 
	 * @param resId
	 *            - resource id of gif image
	 */
	public void setGifImage(int resId, Context context) {
		Resources r = this.getResources();
		InputStream is = r.openRawResource(resId);
		setGifDecoderImage(is, context);
	}

	/**
	 * set GIF image by uri
	 * 
	 * @param uri
	 *            - uri of gif image
	 */
	public void setGifImage(Uri uri, Context context) {
		setGifDecoderImage(uri, context);
		/*
		 * ContentResolver cr = mContext.getContentResolver(); InputStream is =
		 * null; try { is = cr.openInputStream(uri); } catch
		 * (java.io.FileNotFoundException ept) { Log.e(TAG,
		 * "FileNotFoundException " + ept ); }
		 */
	}
    @Override	
	public void setAdjustViewBounds(boolean adjustViewBounds) {
		 mIfAdjustViewBounds = adjustViewBounds;
         super.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    	if (changed && this.animationType == GifImageType.COVER) {
    		showStatic();
    	}
    }
    

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w;
		int h;

		// Desired aspect ratio of the view's contents (not including padding)
		float desiredAspect = 0.0f;

		// We are allowed to change the view's width
		boolean resizeWidth = false;

		// We are allowed to change the view's height
		boolean resizeHeight = false;

		final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		w = mImgWidth;
		h = mImgHeight;
		if (w <= 0)
			w = 1;
		if (h <= 0)
			h = 1;

		// We are supposed to adjust view bounds to match the aspect
		// ratio of our drawable. See if that is possible.
		if (mIfAdjustViewBounds) {
			resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
			resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

			desiredAspect = (float) w / (float) h;
		}

		int pleft = this.getPaddingLeft();
		int pright = this.getPaddingRight();
		int ptop = this.getPaddingTop();
		int pbottom = this.getPaddingBottom();

		int widthSize;
		int heightSize;

		if (resizeWidth || resizeHeight) {
			/*
			 * If we get here, it means we want to resize to match the drawables
			 * aspect ratio, and we have the freedom to change at least one
			 * dimension.
			 */

			// Get the max possible width given our constraints
			widthSize = resolveAdjustedSize(w + pleft + pright, 2000,
					widthMeasureSpec);

			// Get the max possible height given our constraints
			heightSize = resolveAdjustedSize(h + ptop + pbottom, 2000,
					heightMeasureSpec);

			if (desiredAspect != 0.0f) {
				// See what our actual aspect ratio is
				float actualAspect = (float) (widthSize - pleft - pright)
						/ (heightSize - ptop - pbottom);

				if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

					boolean done = false;

					// Try adjusting width to be proportional to height
					if (resizeWidth) {
						int newWidth = (int) (desiredAspect * (heightSize
								- ptop - pbottom))
								+ pleft + pright;
						if (newWidth <= widthSize) {
							widthSize = newWidth;
							done = true;
						}
					}

					// Try adjusting height to be proportional to width
					if (!done && resizeHeight) {
						int newHeight = (int) ((widthSize - pleft - pright) / desiredAspect)
								+ ptop + pbottom;
						if (newHeight <= heightSize) {
							heightSize = newHeight;
						}
					}
				}
			}
		} else {
			/*
			 * We are either don't want to preserve the drawables aspect ratio,
			 * or we are not allowed to change view dimensions. Just measure in
			 * the normal way.
			 */
			w += pleft + pright;
			h += ptop + pbottom;

			w = Math.max(w, getSuggestedMinimumWidth());
			h = Math.max(h, getSuggestedMinimumHeight());

			widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
			heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
		}

		setMeasuredDimension(widthSize, heightSize);
	}
	
	@Override
	protected void 	onDetachedFromWindow() {
		super.onDetachedFromWindow();
		this.recycle();
	}
	

	private int resolveAdjustedSize(int desiredSize, int maxSize,
			int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = Math.min(desiredSize, maxSize);
			break;
		case MeasureSpec.AT_MOST:
			// Parent says we can be as big as we want, up to specSize.
			// Don't be larger than specSize, and don't be larger than
			// the max size imposed on ourselves.
			result = Math.min(Math.min(desiredSize, specSize), maxSize);
			break;
		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}

	/**
	 * just show the first frame of GIF image call this function,GIF just
	 * display the first image instead of animation
	 */
	public void showCover() {
		if (gifDecoder == null) {
			return;
		}
		pause = true;
		currentImage = gifDecoder.getImage();
		setImageBitmap(currentImage);
		invalidate();
	}

	/**
	 * go on showing animation<br>
	 * this function replay the GIF animation after showCover called If the
	 * showCover function has not been called, there is no effect
	 **/
	public void startGifAnimation() {
		if (pause) {
			pause = false;
		}
	}

	// add by wangyan08
	// stop show gif animation
	public void pauseGifAnimation() {
		if (null != drawThread) {
			pause = true;
		}
	}
	
	public void stopGifAnimation() {
		Uri uri = mUri;
		InputStream in = mIs;
		clear();
		if (null != uri) {
		    setGifDecoderImage(uri, getContext());
		} else if (in != null) {
			setGifDecoderImage(in, getContext());
		}
	}
	


	// recycle:stop thread, recycle bitmap and decoder
	public void clear() {
		if (null != drawThread) {
			isRun = false;
			try {
				drawThread.interrupt();
			} catch (SecurityException e) {
				// Log.e(TAG, "recycle:stop thread err "+e);
			}
			try {
				drawThread.join();
			} catch (InterruptedException e) {
				// Log.e(TAG, "recycle:drawThread jion interruptted "+e);
			}
			drawThread = null;
		}
		if (null != gifDecoder) {
			gifDecoder.stopGifDecoder();
			try {
				gifDecoder.join();
			} catch (InterruptedException e) {
				// Log.e(TAG, "recycle:drawThread jion interruptted "+e);
			}
			gifDecoder.free();
			gifDecoder = null;
		}
		// Log.d(TAG, "GIfImageView recycle!!!!");
		if (currentImage != null && !currentImage.isRecycled())
			currentImage.recycle();
		currentImage = null;
		if (mUri != null && mIs != null) {
			try {
				mIs.close();
			} catch (IOException e) {
				
			}
		}
		mUri = null;
		mIs = null;
		this.setImageBitmap(null);
	}
	
	public void recycle() {
		InputStream in = mIs;
		clear();
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				
			}
		}
	}

	/* parseOK function implement from GIF action */
	public void parseOk(boolean parseStatus, int frameIndex) {
		if (parseStatus) {
			if (gifDecoder != null) {
				switch (animationType) {
				case WAIT_FINISH:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						reDraw();
					}
					if ((frameIndex > 2) || (frameIndex == -1)) {
						if (gifDecoder.getFrameCount() > 1) { // if frame
																// count is
																// more than
																// 1, start
																// drawThread
							if (drawThread == null) {
								drawThread = new DrawThread();
								drawThread.start();
							}
						} else if (gifDecoder.getFrameCount() == 1) {
							isRun = true;
							currentImage = gifDecoder.getImage();
							reDraw();
						} else {
							reDraw();
						}
					}
					break;
				case COVER:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						reDraw();
					} else if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() > 1) {
							if (drawThread == null) {
								drawThread = new DrawThread();
								drawThread.start();
							}
						} else {
							reDraw();
						}
					}
					break;
				case SYNC_DECODER:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						isRun = true;
						reDraw();
					} else if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() == 1) {
							currentImage = gifDecoder.getImage();
						}
						reDraw();
					} else {
						if (drawThread == null) {
							drawThread = new DrawThread();
							drawThread.start();
						}
					}
					break;
				}
			} else {
				Log.e(TAG, "Gif Image: parse error");
			}

		}
	}

	private void reDraw() {
		if (redrawHandler != null) {
			Message msg = redrawHandler.obtainMessage(this.MSG_REDRAW);
			redrawHandler.sendMessage(msg);
		}
	}

	private Handler redrawHandler = new Handler() {
		public void handleMessage(Message msg) {
		    if (msg.what == MSG_REDRAW && currentImage != null) {
			    setImageBitmap(currentImage);
			    invalidate();
		    }
		}
	};

	/**
	 * animation GIF show thread
	 */
	private class DrawThread extends Thread {
		public void run() {
			if (gifDecoder == null) {
				return;
			}
			isRun = true;
			while (isRun) {
				if (gifDecoder == null) {
					return;
				}
				if (pause == false) {
					GifFrame frame = gifDecoder.next();
					if (frame == null) {
						continue;
					}
					currentImage = frame.image;
					long sp = frame.delay;
					if (redrawHandler != null) {
						Message msg = redrawHandler.obtainMessage(MSG_REDRAW);
						redrawHandler.sendMessage(msg);
						// Log.d(TAG,
						// "DrawThread run sleep delay time = "+sp+"  "+System.currentTimeMillis()+" index = "+frame.index);
						try {
							Thread.sleep(sp);
						} catch (InterruptedException e) {
							// Log.e(TAG,
							// "DrawThread run sleep delay interrupt "+e);
						}
					} else {
						break;
					}
				} else {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// Log.e(TAG, "DrawThread pause sleep interrupt "+e);
					}
				}
			}
			// Log.d(TAG, "DrawThread stop^^^^^^^");
		}

	}

}
