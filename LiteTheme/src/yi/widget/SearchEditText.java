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

import com.baidu.lite.R;

public final class SearchEditText extends BaiduEditText {
    public static final int LEFT_ICON_NOMAL = 0;
    public static final int LEFT_ICON_ONLINE = 1;

	/**
	 * @param context
	 */
	public SearchEditText(Context context) {
		//super(context);
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SearchEditText(Context context, AttributeSet attrs) {
		//super(context, attrs);
		this(context, attrs, R.attr.searchEditTextStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	/*
	protected void setIcon(Drawable icon) {
		super.setIcon(icon);
		
		if (icon == null) {			
			Drawable searchIcon = this.getResources().getDrawable(android.R.drawable.yi_ic_element_text_area_magnifier);
			this.setCompoundDrawablesWithIntrinsicBounds(null, null, searchIcon, null);
			setGravity(Gravity.CENTER_VERTICAL);
			requestLayout();
		}
	}     */
	/**
	*@param level
	*/
	public void setLeftIcon(int level){
	    Drawable[] drawables;
	    drawables = this.getCompoundDrawables();

	    if(drawables[0] != null)
	        drawables[0].setLevel(level);
	}
}
