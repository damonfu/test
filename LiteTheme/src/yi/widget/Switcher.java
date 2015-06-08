/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yi.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.Checkable;
import android.widget.ProgressBar;

import com.baidu.lite.R;

/**
 * A Switcher is an extension of SeekBar that mimic two states switch. The user can touch
 * the thumb and drag left or right to flip the switch.
 * <p>
 * Clients of the Switcher can attach a {@link Switcher.OnSwitcherChangeListener} to
 * be notified of the user's actions.
 *
 */
public class Switcher extends SeekBar 
	implements android.widget.SeekBar.OnSeekBarChangeListener,android.widget.Checkable {
	private final int THUMB_OFFSET = 0; // 15 pix
	private final int START_TRACKING_OFFSET = 10; // 15 pix	
	private boolean mToggle = false;
	private boolean mTouched = false;
	private boolean mInTracking = false;	
	private float mInitX = 0;
	private float mInitY = 0;
	private OnSwitcherChangeListener mToggleListener;
	private OnClickListener mClickListener;

	private boolean mTouchable = true;

	public interface OnSwitcherChangeListener {
        /**
         * Notification that the toggle state has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         * 
         * @param switcher The switcher whose toggle state has changed
         * @param toggled  The toggle state. 
         */
		  public void onSwitcherToggleChanged(Switcher switcher, boolean toggled);	  
	}

    /**
     * Sets a listener to receive notifications of changes to the Switcher's toggle state.
     * 
     * @param listener The notification listener
     * 
     * @see Switcher.OnSwitcherChangeListener
     */
	public void setOnSwitcherChangeListener(OnSwitcherChangeListener listener) {
		mToggleListener = listener;
	}

    /**
     * Sets a listener to receive notifications of changes to the Switcher's toggle state.
     * 
     * @param listener The notification listener
     * 
     * @see Switcher.OnSwitcherChangeListener
     */
	public void setOnClickListener(OnClickListener listener) {
		mClickListener = listener;
	}

	private void init() {
		setMax(100);
		setOnSeekBarChangeListener(this);
	}
	
	public Switcher(Context context) {
		this(context, null);
		init();
	}
	
	public Switcher(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.switcherStyle);
		init();
	}
	
	public Switcher(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
	}
	
	public void onStartTrackingTouch(SeekBar seekBar) {

	}
	
	public void onStopTrackingTouch(SeekBar seekBar) {
		mInTracking = false;
		mTouched = false;
		if(mClickListener != null) {
		//	mClickListener.onClick(this);
		}
		toggleOnProgress();
	}
	
	public boolean onTouchEvent(MotionEvent event) {	
		if (!isEnabled())
			return true;
		
		if (!mTouchable) {
			return false;
		}

		if (mInTracking) {
			super.onTouchEvent(event);
		} else {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				mTouched = true;
				mInitX = event.getX();
				mInitY = event.getY();
			} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				float  dist = (float)Math.sqrt((mInitX - event.getX()) * (mInitX - event.getX()) +
						(mInitY - event.getY()) * (mInitY - event.getY()));
				if(dist > START_TRACKING_OFFSET) {
					MotionEvent eventNew = MotionEvent.obtain(event);
					eventNew.setAction(MotionEvent.ACTION_DOWN);
					eventNew.setLocation(mInitX, mInitY);
					super.onTouchEvent(eventNew);
					mInTracking = true;
				}
			} else {
				if(!mInTracking) {
					setToggle(!mToggle);
					if(mClickListener != null) {
						//mClickListener.onClick(this);
					}
				}
				mInTracking = false;
				mTouched = false;
			}
		}
		return true;
	}
    /**
     * Get toggle state.
     * 
     * @return toggle state
     * 
     * @see Switcher.isToggled
     */		
	public boolean isToggled() {
		return mToggle;
	}

	public void setToggle(boolean toggle) {
		boolean prev = mToggle;
		mToggle = toggle;
		if(mToggle) {
			setProgress(100);
		}
		else {
			setProgress(0);
		}
		
		if(mToggleListener != null && prev!= toggle) {
			mToggleListener.onSwitcherToggleChanged(this, isToggled());
		}
	}
	
	public boolean isChecked() {
		return isToggled();
	}

	public void setChecked(boolean checked) {
		setToggle(checked);
	}

	public void toggle() {
		setToggle(!isToggled());
	}
    
	private void toggleOnProgress() {
		if(!mToggle) {
			if(getProgress() >= 50) {
				setProgress(100);
				mToggle = true;
				if(mToggleListener != null) {
					mToggleListener.onSwitcherToggleChanged(this, isToggled());
				}
			} else {
				setProgress(0);
			}
		}
		else {
			if(getProgress() < 50) {
				setProgress(0);
				mToggle = false;
				if(mToggleListener != null) {
					mToggleListener.onSwitcherToggleChanged(this, isToggled());
				}
			} else {
				setProgress(100);
			}
		}
	}

	/**
	 * 
	 * @param touchable
	 * 
	 * @hide
	 */
   public void setTouchable(boolean touchable) {
       mTouchable = touchable;
   }
}
