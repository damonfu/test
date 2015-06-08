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

import android.graphics.drawable.Drawable;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.LayoutInflater;
import java.util.HashMap;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.lite.R;

import yi.util.IDHelper;

final public class ButtonGroup extends LinearLayout {
    private static final String TAG = "ButtonGroup";
	private int mCheckedId = -1;
	private int mButtonNumber = -1;
	private int mBackgroundType = -1;

	private static int BUTTON_NUMBER_MIN = 2;
	private static int BUTTON_NUMBER_MAX = 5;
	private static int GROUND_TYPE_TOP = 0;
	private static int GROUND_TYPE_BOTTOM = 1;

	private final static int BUTTON_WIDTH = LinearLayout.LayoutParams.WRAP_CONTENT;
	private final static int BUTTON_HEIGHT = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	private int mBackground = R.drawable.yi_button_group_background;
	
	private HashMap<Integer, TextView> mButtons = new HashMap<Integer, TextView>();
	private HashMap<Integer, String> mTitles = new HashMap<Integer, String>();

	protected OnButtonCheckedListener mOnButtonCheckedListener;

	public ButtonGroup(Context context, int buttonNumber, int backgroundType) {
		super(context);

		mButtonNumber = buttonNumber;
		mBackgroundType = backgroundType;

		//check ButtonNumber
		if (mButtonNumber < BUTTON_NUMBER_MIN) {
			mButtonNumber = BUTTON_NUMBER_MIN;
		}
		if (mButtonNumber > BUTTON_NUMBER_MAX) {
			mButtonNumber = BUTTON_NUMBER_MAX;
		}

		setupView(context);
		setCheckedButton(0);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	public ButtonGroup(Context context) {
		super(context);

		mButtonNumber = BUTTON_NUMBER_MIN;
		setupView(context);
		setCheckedButton(0);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	public ButtonGroup(Context context, AttributeSet attrs) {
		super(context, attrs);

		loadAttrs(context, attrs);
		setupView(context);
		setCheckedButton(0);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
	}

	/**
	 * Set Title for the Button with ID id
	 * 
	 * @param id The Button id
	 * @param title The title will be set
	 * 
	 * @return True if set success
	 */
	public boolean setTitle(int id, String title) {
		TextView bt = mButtons.get(id);
		if (bt == null) {
			return false;
		}
		bt.setText(title);
		mTitles.put(id, title);
		return true;
	}

	public String getTitle(int id) {
		return mTitles.get(id);
	}

	public void setBackgroundResource(int resid) {
		super.setBackgroundResource(resid);
		
		super.setPadding(1, 0, 1, 0);
		
		int size = mButtons.size();
		for (int i = 0; i < size; i++) {
			TextView bt = mButtons.get(i);
			if (bt != null) {
				bt.setBackgroundResource(resid);
				bt.setPadding(12, 0, 12, 0);
			}
		}
        initCheckedState(mCheckedId);
	}
	
    private void initCheckedState(int checkedId) {
        int size = mButtons.size();
		for (int i = 0; i < size; i++) {
			TextView bt = mButtons.get(i);
			if (bt == null) {
                continue;
            }

            Drawable d = bt.getBackground();
            if (d == null) {
                continue;
            }

            if (i != checkedId) {
                d.setLevel(4);
            } else {
                if (i == 0) {
                    d.setLevel(1);
                } else if (i == size-1) {
                    d.setLevel(2);
                } else {
                    d.setLevel(3);
                }
            }
		}
    }

	private void loadAttrs(Context context, AttributeSet attrs) {

		String title = null;
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ButtonGroup, 0, 0);

		int n = attributes.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = attributes.getIndex(i);
			if (attr == R.styleable.ButtonGroup_backgroundType) {
				mBackgroundType = attributes.getInt(attr, 1);

			} else if (attr == R.styleable.ButtonGroup_background) {
				mBackground = attributes.getResourceId(attr, R.drawable.yi_button_group_background);
				if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "mBackground = " + mBackground);

			} else if (attr == R.styleable.ButtonGroup_buttonNumber) {
				mButtonNumber = attributes.getInt(attr, 1);

			} else if (attr == R.styleable.ButtonGroup_buttonText0) {
				title = attributes.getString(attr);
				mTitles.put(0, title);

			} else if (attr == R.styleable.ButtonGroup_buttonText1) {
				title = attributes.getString(attr);
				mTitles.put(1, title);

			} else if (attr == R.styleable.ButtonGroup_buttonText2) {
				title = attributes.getString(attr);
				mTitles.put(2, title);

			} else if (attr == R.styleable.ButtonGroup_buttonText3) {
				title = attributes.getString(attr);
				mTitles.put(3, title);

			} else if (attr == R.styleable.ButtonGroup_buttonText4) {
				title = attributes.getString(attr);
				mTitles.put(4, title);

			}
		}

		//check Width and Height
		attributes = context.obtainStyledAttributes(attrs, IDHelper.getStyleableArrByName("LinearLayout_Layout"));
		int width = attributes.getLayoutDimension(IDHelper.getStyleableByName("LinearLayout_Layout_layout_width"),
			LinearLayout.LayoutParams.WRAP_CONTENT);
		int height = attributes.getLayoutDimension(IDHelper.getStyleableByName("LinearLayout_Layout_layout_height"),
			LinearLayout.LayoutParams.WRAP_CONTENT);

		if (width != LayoutParams.WRAP_CONTENT) {
			throw new IllegalArgumentException("ButtonGroup: layout_width should be WRAP_CONTENT");
		}
		if (height != LayoutParams.WRAP_CONTENT) {
			throw new IllegalArgumentException("ButtonGroup: layout_height should be WRAP_CONTENT");
		}

		attributes.recycle();

		//check ButtonNumber
		if (mButtonNumber < BUTTON_NUMBER_MIN) {
			mButtonNumber = BUTTON_NUMBER_MIN;
		}
		if (mButtonNumber > BUTTON_NUMBER_MAX) {
			mButtonNumber = BUTTON_NUMBER_MAX;
		}
		
	}

	private void setupView(Context context) {

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(BUTTON_WIDTH, BUTTON_HEIGHT);
		setLayoutParams(lp);
		
		if (mBackgroundType == GROUND_TYPE_TOP) {
			mBackground = R.drawable.yi_title_button_group_background;
		} else if (mBackgroundType == GROUND_TYPE_BOTTOM) {
			mBackground = R.drawable.yi_button_group_background;
		}

		this.setBackgroundResource(mBackground);
		Drawable d;
        d = this.getBackground();
		if (d != null) {
			d.setLevel(0);
		}

		TextView bt = null;
		String title = null;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		bt = (TextView)inflater.inflate(R.layout.yi_group_button_left, this, false);
		if (bt != null) {
			title = mTitles.get(0);
			if (title != null) {
				bt.setText(title);
			}
			bt.setTag(0);
			bt.setOnClickListener(mClickListener);
			bt.setOnTouchListener(mTouchListener);
			bt.setBackgroundResource(mBackground);
			bt.setPadding(12, 0, 12, 0);
            d = bt.getBackground();
            if (d != null) {
                d.setLevel(4);
            }
			
			mButtons.put(0, bt);
			addButton(bt);
		}

		for (int i = 1; i < mButtonNumber - 1; i++) {
			bt = (TextView)inflater.inflate(R.layout.yi_group_button_middle, this, false);
			if (bt != null) {
				title = mTitles.get(i);
				if (title != null) {
					bt.setText(title);
				}
				bt.setTag(i);
				bt.setOnClickListener(mClickListener);
				bt.setOnTouchListener(mTouchListener);
				bt.setBackgroundResource(mBackground);
				bt.setPadding(12, 0, 12, 0);
	            d = bt.getBackground();
                if (d != null) {
                    d.setLevel(4);
                }
			
				mButtons.put(i, bt);
				addButton(bt);
			}
		}

		bt = (TextView)inflater.inflate(R.layout.yi_group_button_right, this, false);
		if (bt != null) {
			title = mTitles.get(mButtonNumber-1);
			if (title != null) {
				bt.setText(title);
			}
			bt.setTag(mButtonNumber-1);
			bt.setOnClickListener(mClickListener);
			bt.setOnTouchListener(mTouchListener);
			bt.setBackgroundResource(mBackground);
			bt.setPadding(12, 0, 12, 0);
			d = bt.getBackground();
            if (d != null) {
                d.setLevel(4);
            }

			mButtons.put(mButtonNumber-1, bt);
			addButton(bt);
		}
	}

	/**
	 * Set the CheckedState for the Button with ID id
	 * 
	 * @param id The Button id
	 * 
	 * @return True if set success
	 */
	public boolean setCheckedButton(int id) {
		
		if (id < 0 || id >= mButtonNumber) {
			return false;
		}
		
		//clear old checked Button
		TextView button = mButtons.get(mCheckedId);
		if (button != null) {
			Drawable d = button.getBackground();
			if (d != null) {
				d.setLevel(4);
			}
		}

		//set new checked Button
		button = mButtons.get(id);
		if (button == null) {
			return false;
		}
		
		Drawable d = button.getBackground();
		if (d!= null) {
			if (id == 0) {
				d.setLevel(1);
			} else if (id == mButtonNumber - 1) {
				d.setLevel(2);
			} else {
				d.setLevel(3);
			}
		}
		
		mCheckedId = id;
		return true;
	}

	public int getCheckedButtonId() {
		return mCheckedId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	/**
	 * A callback that notifies clients when a Button in Group has been Checked
	 */
	public interface OnButtonCheckedListener {
		public void onButtonChecked(int id);
	}

	public void setOnButtonCheckedListener (OnButtonCheckedListener l) {
		mOnButtonCheckedListener = l;
	}

	// no use
	View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int id = (Integer)v.getTag();
			TextView bt = mButtons.get(id);
			if (bt == null) {
				return false;
			}
			
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					//setCheckedButton(id);
					break;
				}
				case MotionEvent.ACTION_UP: {
					//setCheckedButton(-1);
					break;
				}
			}
			return false;
		}
	};
	
	View.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			int id = (Integer)v.getTag();
			TextView bt = mButtons.get(id);
			if (bt == null) {
				//error
				return;
			}
			setCheckedButton(id);
			if (mOnButtonCheckedListener != null) {
				mOnButtonCheckedListener.onButtonChecked(id);
			}
		}
	};
	
	private void addButton(TextView bt) {
		super.addView(bt);
	}

	@Override
	public void addView(View child) {
		super.addView(child);
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
	}

	@Override
	public void addView(View child, int width, int height) {
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(0, 0, 0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof ButtonGroup.LayoutParams;
	}

	@Override
	protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public static class LayoutParams extends LinearLayout.LayoutParams {
		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(int w, int h) {
			super(w, h);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(int w, int h, float initWeight) {
			super(w, h, initWeight);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams p) {
			super(p);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}

		@Override
		protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
			if (a.hasValue(widthAttr)) {
				width = a.getLayoutDimension(widthAttr, "layout_width");
			}

			if (a.hasValue(heightAttr)) {
				height = a.getLayoutDimension(heightAttr, "layout_height");
			}
		}
	}
}
