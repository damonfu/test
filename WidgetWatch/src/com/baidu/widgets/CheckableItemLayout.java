package com.baidu.widgets;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.RemoteViews.RemoteView;

/**
 *
 */
@RemoteView
public class CheckableItemLayout extends RelativeLayout implements Checkable {
	private boolean mChecked;
	private int mCheckMarkResource;
	private Drawable mCheckMarkDrawable = null;
	private int mBasePaddingRight = 12;
	private int mCheckMarkWidth;
	private int mLayoutMode = LAYOUT_MODE_LIST;
	private int mPaddingRight;

	/**
	 * The extra padding for check box
	 */
	protected int mCBExtraPaddingLeft = 0;
	protected int mCBExtraPaddingRight = 0;
	protected int mCBExtraPaddingTop = 0;
	protected int mCBExtraPaddingBottom = 0;

	private int mChoiceMode = CHOICE_MODE_NONE;

	/**
	 * Normal Item that does not indicate choices
	 */
	public static final int CHOICE_MODE_NONE = 0;

	/**
	 * The Item with a Radio Button 
	 */
	public static final int CHOICE_MODE_SINGLE = 1;

	/**
	 * The Item with a Check Button 
	 */
	public static final int CHOICE_MODE_MULTIPLE = 2;
	
	/**
	 * Layout in a ListView
	 */
	public static final int LAYOUT_MODE_LIST = 3;
	
	/**
	 * Layout in a GridView
	 */
	public static final int LAYOUT_MODE_GRID = 4;

	private static final int[] CHECKED_STATE_SET = {
		R.attr.state_checked
	};

	public CheckableItemLayout(Context context) {
		this(context, null);
	}

	public CheckableItemLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckableItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setWillNotDraw(false);
	}

	/**
	 * @see #setChoiceMode(int)
	 *
	 * @return The current choice mode
	 */
	public int getChoiceMode() {
		return mChoiceMode;
	}

	/**
	 * Defines the choice behavior for the Item . By default, Item is {@link #CHOICE_MODE_NONE} mode.
	 * By setting the choiceMode to {@link #CHOICE_MODE_SINGLE}, the Item appears a Radio Button in the right side,  
	 * By setting the choiceMode to {@link #CHOICE_MODE_MULTIPLE}, the Item appears a Check Button in the right side. 
	 *
	 * @param choiceMode One of {@link #CHOICE_MODE_NONE}, {@link #CHOICE_MODE_SINGLE}, or
	 * {@link #CHOICE_MODE_MULTIPLE}
	 */
	public void setChoiceMode(int choiceMode) {

		mChoiceMode = choiceMode;

		Drawable d = null;
		if (mChoiceMode == CHOICE_MODE_SINGLE) {
			d = getResources().getDrawable(getContext().getResources().getIdentifier("yi_btn_radio", "drawable", "yi"));
		} else if (mChoiceMode == CHOICE_MODE_MULTIPLE ) {
			d = getResources().getDrawable(getContext().getResources().getIdentifier("yi_btn_check", "drawable", "yi"));
		} else if ( mChoiceMode == CHOICE_MODE_NONE ) {
			d = null;
		}

		setCheckMarkDrawable(d);
	}
	
	/**
	 * Defines which layout style this Checkable item want to use.
	 * By default ,Layout mode is {@link #LAYOUT_MODE_LIST}, if you set it to {@link #LAYOUT_MODE_GRID}, 
	 * the checkmark will show on the top|right position.
	 * @param layoutMode
	 */
//	public void setLayoutMode(int layoutMode){
//		if(layoutMode == LAYOUT_MODE_GRID){
//			mLayoutMode = LAYOUT_MODE_GRID;
//		}else {
//			mLayoutMode = LAYOUT_MODE_LIST;
//		}
//	}

	public void toggle() {
		setChecked(!mChecked);
	}

	@ViewDebug.ExportedProperty
		public boolean isChecked() {
			return mChecked;
		}

	/**
	 * <p>Changes the checked state of this text view.</p>
	 *
	 * @param checked true to check the text, false to uncheck it
	 */
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
	}


	/**
	 * Set the checkmark to a given Drawable, identified by its resourece id. This will be drawn
	 * when {@link #isChecked()} is true.
	 * 
	 * @param resid The Drawable to use for the checkmark.
	 */
	public void setCheckMarkDrawable(int resid) {
		if (resid != 0 && resid == mCheckMarkResource) {
			return;
		}

		mCheckMarkResource = resid;

		Drawable d = null;
		if (mCheckMarkResource != 0) {
			d = getResources().getDrawable(mCheckMarkResource);
		}
		setCheckMarkDrawable(d);
	}

	/**
	 * Set the checkmark to a given Drawable. This will be drawn when {@link #isChecked()} is true.
	 *
	 * @param d The Drawable to use for the checkmark.
	 */
	public void setCheckMarkDrawable(Drawable d) {
		if (mCheckMarkDrawable != null) {
			mCheckMarkDrawable.setCallback(null);
			unscheduleDrawable(mCheckMarkDrawable);
		}
		if (d != null) {
			d.setCallback(this);
			d.setVisible(getVisibility() == VISIBLE, false);
			d.setState(CHECKED_STATE_SET);
			//setMinHeight(d.getIntrinsicHeight());

			mCheckMarkWidth = d.getIntrinsicWidth();
			mPaddingRight = mCheckMarkWidth + mBasePaddingRight;
			d.setState(getDrawableState());
		} else {
			mPaddingRight = mBasePaddingRight;
		}
		mCheckMarkDrawable = d;
		requestLayout();
		invalidate();
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, bottom);
		mBasePaddingRight = mPaddingRight;
	}

	/**
	* set the extra padding for checkbox
	* @param left the extra left padding in pixels
	* @param top the extra top padding in pixels
	* @param right the extra right padding in pixels
	* @param bottom the extra bottom padding in pixels
	* Note: the extra left padding does not affect the check box's position
	*/
	public void setCheckBoxExtraPadding(int left, int top, int right, int bottom) {
		boolean changed = false;

		if (mCBExtraPaddingLeft != left) {
			changed = true;
			mCBExtraPaddingLeft = left;
		}
		if (mCBExtraPaddingTop != top) {
			changed = true;
			mCBExtraPaddingTop = top;
		}
		if (mCBExtraPaddingRight != right) {
			changed = true;
			mCBExtraPaddingRight = right;
		}
		if (mCBExtraPaddingBottom != bottom) {
			changed = true;
			mCBExtraPaddingBottom = bottom;
		}

		if (changed) {
			requestLayout();
		}
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    final Drawable checkMarkDrawable = mCheckMarkDrawable;
        if (checkMarkDrawable != null && mLayoutMode == LAYOUT_MODE_LIST) {
            mCheckMarkWidth = checkMarkDrawable.getIntrinsicWidth();
            mPaddingRight = mCheckMarkWidth + mBasePaddingRight;
        } else {
            mPaddingRight = mBasePaddingRight;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		}

	@Override
		protected int[] onCreateDrawableState(int extraSpace) {
			final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
			if (isChecked()) {
				mergeDrawableStates(drawableState, CHECKED_STATE_SET);
			}
			return drawableState;
		}

	@Override
		protected void drawableStateChanged() {
			super.drawableStateChanged();
			if (mCheckMarkDrawable != null) {
				int[] myDrawableState = getDrawableState();

				// Set the state of the Drawable
				mCheckMarkDrawable.setState(myDrawableState);

				invalidate();
			}
		}

	@Override
		public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
			boolean populated = super.dispatchPopulateAccessibilityEvent(event);
			if (!populated) {
				event.setChecked(mChecked);
			}
			return populated;
		}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		
		final Drawable checkMarkDrawable = mCheckMarkDrawable;
		if (checkMarkDrawable != null) {
			final int height = checkMarkDrawable.getIntrinsicHeight();
			if (mLayoutMode == LAYOUT_MODE_LIST) {
				int top = mCBExtraPaddingTop
						+ (getHeight() - height - mCBExtraPaddingTop - mCBExtraPaddingBottom)
						/ 2;
				int right = getWidth();

				checkMarkDrawable.setBounds(
						right - mCheckMarkWidth - mBasePaddingRight - mCBExtraPaddingRight,
						top, 
						right - mBasePaddingRight - mCBExtraPaddingRight, 
						top + height);
				checkMarkDrawable.draw(canvas);
			} else if(mChecked == true){
				Drawable mask = new ColorDrawable(0x88000000);
				mask.setBounds(0, 0, getWidth(), getHeight());
				mask.draw(canvas);
				checkMarkDrawable.setBounds(
						getWidth() - mCheckMarkWidth, 
						0,
						getWidth(), 
						height);
				checkMarkDrawable.draw(canvas);
			}
			
			
		}
	}
}



