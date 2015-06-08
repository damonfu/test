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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

import com.baidu.lite.R;

import java.lang.Math;

public class BaiduEditText extends EditText {
	
	private int mDeleteIcon = R.drawable.yi_edit_text_cancel;
	private Drawable mDelete;

	private int mIconWidth;
	private int mIconHeight;
	
	private boolean mClean = false;

	private static final int		VERY_WIDE = 16384;

	/**
	 * @param context
	 */
	public BaiduEditText(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public BaiduEditText(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.editTextStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BaiduEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
        this.setSingleLine(true);
        //this.setCompoundDrawablePadding(0);
		
		this.setFilters(new InputFilter[]{new InputFilter.LengthFilter((int)Math.floor(VERY_WIDE/this.getTextSize()))});

        this.addTextChangedListener(mTextWatcher);
        
		if (mDelete == null) {
			mDelete = this.getResources().getDrawable(mDeleteIcon);
		}
		this.setIcon(mDelete);
        this.updateDeleteIcon();
	}

	/*
	private void doDelete() {
		Editable editable = this.getEditableText();
		int st = this.getSelectionStart();
		int en = this.getSelectionEnd();
		
		int start;
		int end;
		
		if (st == en) {
			start = 0;
			end = st;
		} else if (st > en) {
			start = en;
			end = st;
		} else {
			start = st;
			end = en;
		}
		editable.delete(start, end);
		this.setSelection(start);
	}
	*/

	private void doDelete() {
		Editable editable = this.getEditableText();
		if (editable != null) {
			editable.clear();
		}
	}
	
	private boolean isOnDelete(int x, int y) {
		this.requestFocus();
		int left = this.getWidth() - mIconWidth - this.getPaddingRight();
		int right = left + mIconWidth;
		int top = (this.getHeight() - mIconHeight) / 2;
		int bottom = top + mIconHeight;
		Rect rect = new Rect(left, top, right, bottom);

		if (rect.contains(x, y)) {
			return true;
		}
		return false;
	}

	protected void setIcon(Drawable icon) {
	    Drawable[] drawables;
	    drawables = getCompoundDrawables();
		if (icon != null) {
			mIconWidth = icon.getIntrinsicWidth();
			mIconHeight = icon.getIntrinsicHeight();
			icon.setBounds(0, 0, mIconWidth, mIconHeight);
			setCompoundDrawables(drawables[0], null, icon, null);
			setGravity(Gravity.CENTER_VERTICAL);
			requestLayout();
		} else {
			mIconWidth = -1;
			mIconHeight = -1;
			setCompoundDrawables(drawables[0], null, null, null);
		}
	}
    
    private boolean isEmpty() {
        return TextUtils.isEmpty(getText());
    }
    
	private void updateDeleteIcon() {
		if (this.isEmpty()) {
			this.setIconLevel(3);
			return;
		}

		this.setIconLevel(0);
	}
	
	private boolean setIconLevel(int level) {
		if (this.mDelete == null) {
			return false;
		}
	    
		if (this.mDelete.getLevel() == level) {
			return false;
		}
		
		this.mDelete.setLevel(level);
		return true;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.isEmpty()) {
		    this.setIconLevel(3);
		}else {
		    this.setIconLevel(enabled ? 0 : 2);
		}
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled() == false) {
        	return super.onTouchEvent(event);
        }
        
        final int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();
        
        if (!this.isOnDelete(x, y)) {
        	mClean = false;
        	if(this.mDelete.getLevel() == 1)
        	    setIconLevel(0);
    	    return super.onTouchEvent(event);
        }

        if (this.isEmpty()) {
        	mClean = false;
        	setIconLevel(3);
    	    return super.onTouchEvent(event);
        }

        switch (action) {
        	case MotionEvent.ACTION_DOWN:
        	case MotionEvent.ACTION_MOVE: {
        	    mClean = true;
        	    setIconLevel(1);
        	    break;
        	}
        	case MotionEvent.ACTION_UP: {
	        	if (mClean) {
	        		mClean = false;
	            	setIconLevel(0);
	        		doDelete();
	        	}
	        	break;
        	}
        	case MotionEvent.ACTION_CANCEL: {
                mClean = false;
                setIconLevel(0);
                break;
        	}
        }
        return true;
    }
    

    /**
     * Callback to watch the EditText field for empty/non-empty
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int before, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int after) {
            updateDeleteIcon();
        }

        public void afterTextChanged(Editable s) { }
    };
}
