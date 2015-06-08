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

package yi.app;

import android.app.Activity;
//import android.content.Context;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.baidu.lite.R;

public class BaiduAppTitle {
	private static final String TAG = "BaiduAppTitle";
	final static public  int RICE_MAX_TITLE_BTN_LEVEL = 2;
	final static private int RICE_INIT_TITLE_BTN_PADDING = 0; /* dips */
	final static private int RICE_INIT_TITLE_BTN_PADDING_BOTTOM = 11; /* dips */
	final static private int RICE_DELTA_TITLE_BTN_PADDING = 5; /* dips */
	final static private int RICE_INIT_TITLE_BTN_WIDTH = 90; /* dips */

	private Activity mActivity;
	private int mResourceId = R.layout.yi_internal_app_title;
	private int mBtnGrpResourceId = R.layout.yi_internal_app_title_2_button;
	private boolean useBtnGrpTitle = false;
	private Button mleftBtn;
	private Button mRightBtn;
	private TextView mTitleLabel;
	private View mTitleRoot;
	private ImageView mTitleCorner;

	/**
     * Listener used to dispatch left button click events.
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected OnLeftClickedListener mOnLeftClickedListener;
	protected OnYiLeftClickedListener mOnYiLeftClickedListener;

    /**
     * Listener used to dispatch right button click events.
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected OnRightClickedListener mOnRightClickedListener;
	protected OnYiRightClickedListener mOnYiRightClickedListener;

	/**
     * @param activity
     */
	public BaiduAppTitle (Activity activity) {
		mActivity = activity;

	}
	
	public final void installYiAppTitle() {
		installRiceAppTitle();
	}

	public final void installRiceAppTitle() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "installRiceAppTitle");

        if(mActivity == null)
        	return;

        mleftBtn  = (Button)mActivity.findViewById(R.id.left_label);
        mRightBtn = (Button)mActivity.findViewById(R.id.right_label);
        mTitleLabel  = (TextView)mActivity.findViewById(android.R.id.title);
        mTitleRoot = mActivity.findViewById(R.id.title_root);
        mTitleCorner = (ImageView)mActivity.findViewById(R.id.title_corner);

        ComponentName componentName = new ComponentName(mActivity, mActivity.getClass());
        ActivityInfo activityInfo = null;
        try {
        	activityInfo = mActivity.getPackageManager().getActivityInfo(componentName, 0);
        } catch (NameNotFoundException e) {
        	Log.w(TAG, "ComponentName params null", e);
        }

        if(mleftBtn  != null) {
            mleftBtn.setOnClickListener(new android.view.View.OnClickListener()
            {
                public void onClick(View v) {
                	if(mOnLeftClickedListener != null) {
                		mOnLeftClickedListener.onRiceTitleLeftButtonClicked(v);
                	}
                	
                	if (mOnYiLeftClickedListener != null) {
                		mOnYiLeftClickedListener.onYiTitleLeftButtonClicked(v);
                	}
                }
            }
            );
        }

        if(mRightBtn != null) {
            mRightBtn.setOnClickListener(new android.view.View.OnClickListener()
            {
                public void onClick(View v) {
                	if(mOnRightClickedListener != null) {
                		mOnRightClickedListener.onRiceTitleRightButtonClicked(v);
                	}
                	
                	if (mOnYiRightClickedListener != null) {
                		mOnYiRightClickedListener.onYiTitleRightButtonClicked(v);
                	}
                }
            });
        }

        if(mleftBtn != null) {
            setLabelPaddingDip(mleftBtn, RICE_INIT_TITLE_BTN_PADDING, 9, 0, RICE_INIT_TITLE_BTN_PADDING_BOTTOM);
            mleftBtn.setVisibility(View.INVISIBLE);
        }
        if(mRightBtn != null) {
            setLabelPaddingDip(mRightBtn, 0, 9, RICE_INIT_TITLE_BTN_PADDING, RICE_INIT_TITLE_BTN_PADDING_BOTTOM);
            mRightBtn.setVisibility(View.INVISIBLE);
        }

		if (mTitleLabel != null) {
		    mTitleLabel.setVisibility(android.view.View.VISIBLE);
		    if(activityInfo != null && activityInfo.loadLabel(mActivity.getPackageManager()) != null){
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG,"activityInfo.label " + activityInfo.loadLabel(mActivity.getPackageManager()));
		        setYiTitleCenterLabel(activityInfo.loadLabel(mActivity.getPackageManager()));
            }
		}
	}

    /**
     * set whether use buttonGroup title .
     *
     * @param isBtnGrp  when it is true,  BtnGrp title style enabled
     *
     */
    public void setBtnGrpStyle(boolean isBtnGrp) {
        useBtnGrpTitle = isBtnGrp;
    }

	/**
	 * set app title visibility.
	 * 
	 * @param visibility
	 *            which represent title's visibility
	 * 
	 */
	public void setYiTitleVisibility(int visibility) {
		setRiceTitleVisibility(visibility);
	}
	
    /**
     * set app title visibility.
     *
     * @param visibility   which represent title's visibility
     *
     */
    public void setRiceTitleVisibility(int visibility) {

    	View top = mActivity.findViewById(R.id.title_root);
    	if (top != null) {		
    		ViewGroup title_container = (ViewGroup)top.getParent().getParent();
    		if (title_container != null) {
    		    title_container.setVisibility(visibility);
    	    }
    	    top.setVisibility(visibility);
    	}

    	View text = mActivity.findViewById(R.id.title);
    	if (text != null) {
    		text.setVisibility(visibility);
    	}

    	View corner = mActivity.findViewById(R.id.title_corner);
    	if (corner != null) {
    		corner.setVisibility(visibility);
    	}

    }

    private void setLabelPaddingDip(View v, int left, int top, int right, int bottom) {
        Resources r = mActivity.getResources();
        v.setPadding(
            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, left, r.getDisplayMetrics()),
            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, top, r.getDisplayMetrics()),
            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, right, r.getDisplayMetrics()),
            (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottom, r.getDisplayMetrics()));
    }

    /**
     * Register a callback to be invoked when this left button is clicked.
     *
     * @param l The callback that will run
     *
     */
    public void setOnLeftClickedListener(OnLeftClickedListener l) {
        if (mleftBtn  == null) {
            return;
        }
        mOnLeftClickedListener = l;
    }
    
	public void setOnLeftClickedListener(OnYiLeftClickedListener l) {
        if (mleftBtn  == null) {
            return;
        }
        mOnYiLeftClickedListener = l;
    }
	
    /**
     * Register a callback to be invoked when this left button is clicked.
     *
     * @param l The callback that will run
     *
     */
    public void setOnRightClickedListener(OnRightClickedListener l) {
        if (mRightBtn == null) {
            return;
        }
        mOnRightClickedListener = l;
    }

    /**
     * Register a callback to be invoked when this left button is clicked.
     *
     * @param l The callback that will run
     *
     */
    public void setOnRightClickedListener(OnYiRightClickedListener l) {
        if (mRightBtn == null) {
            return;
        }
        mOnYiRightClickedListener = l;
    }

	/**
	 * Set left label on app title
	 * 
	 * 
	 * @param label
	 *            The Text to be set
	 * 
	 */
	public final void setYiTitleLeftLabel(CharSequence label) {
		setRiceTitleLeftLabel(label);
	}
	
    /**
     * Set left label on app title
     *
     *
     * @param label The Text to be set
     *
     */
    public final  void setRiceTitleLeftLabel(CharSequence label) {
    	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "setRiceTitleLeftLabel");

    	if (TextUtils.isEmpty(label)) {
    		mleftBtn.setVisibility(View.INVISIBLE);
    		return;
    	}

    	if(mleftBtn != null) {
            mleftBtn.setText(label);
            mleftBtn.setVisibility(View.VISIBLE);
        }
    }

	/**
	 * Set right label on app title
	 * 
	 * 
	 * @param label
	 *            The Text to be set
	 * 
	 */
	final public void setYiTitleRightLabel(CharSequence label) {
		setRiceTitleRightLabel(label);
	}
	
    /**
     * Set right label on app title
     *
     *
     * @param label The Text to be set
     *
     */
    final public void setRiceTitleRightLabel(CharSequence label) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "setRiceTitleRightLabel");

    	if (TextUtils.isEmpty(label)) {
    		mRightBtn.setVisibility(View.INVISIBLE);
    		return;
    	}

    	if(mRightBtn != null) {
    		mRightBtn.setText(label);
            mRightBtn.setVisibility(View.VISIBLE);
        }
    }

	/**
	 * Set center label on app title
	 * 
	 * 
	 * @param label
	 *            The Text to be set
	 * 
	 */
	final public void setYiTitleCenterLabel(CharSequence label) {
		setRiceTitleCenterLabel(label);
	}

    /**
     * Set center label on app title
     *
     *
     * @param label The Text to be set
     *
     */
    final public void setRiceTitleCenterLabel(CharSequence label) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "setRiceTitleCenterLabel");
        if(mTitleLabel!=null) {
                mTitleLabel.setText(label);
        }
        mActivity.setTitle(label);
    }

    /**
     * Set app title background, including title_root and title_corner
     *
     * @param root   drawable for title_root
     * @param corner  drawable for title_corner
     *
     */
     public void setTitleBackground(Drawable root, Drawable corner) {
         if(mTitleRoot != null) {
            mTitleRoot.setBackgroundDrawable(root);
         }

         if(mTitleCorner != null) {
            mTitleCorner.setBackgroundDrawable(corner);
         }
     }

     /**
     * Set app title background Id, including title_root and title_corner
     *
     * @param rootResId   res id for title_root
     * @param cornerResId  res id for title_corner
     *
     */
     public void setTitleBackground(int rootResId, int cornerResId) {
         if(mTitleRoot != null) {
            mTitleRoot.setBackgroundResource(rootResId);
         }

         if(mTitleCorner != null) {
            mTitleCorner.setBackgroundResource(cornerResId);
         }
     }
     
    /**
     * Get Rice App Title layout res
     *
     * @return layout res ID, if using BtnGrp title sytle, @setBtnGrpStyle should be called first
     *
     */
	public int getLayoutId () {
	    if(useBtnGrpTitle)
	        return mBtnGrpResourceId;
	    else
		    return mResourceId;
	}

	/**
	 * Get left title Button
	 * 
	 * @return left title button
	 * 
	 */
	public Button getTitleLeftButton() {
		return mleftBtn;
	}
	
    /**
     * Get left title Button
     *
     * @return left title button
     *
     */
    public Button getRiceTitleLeftButton() {
          return  mleftBtn;
    }


	/**
	 * Get right title Button
	 * 
	 * @return right title button
	 * 
	 */
	public Button getTitleRightButton() {
		return mRightBtn;
	}
	
    /**
     * Get right title Button
     *
     * @return right title button
     *
     */
    public Button getRiceTitleRightButton() {
          return  mRightBtn;
    }

	/**
	 * Set the enabled state of left title button
	 * @param enabled the left button enabled status
	 */

	final public void setYiTitleLeftButtonEnable(boolean enabled) {
		setRiceTitleLeftButtonEnable(enabled);
	}
	
    /**
     * Set the enabled state of left title button
     *
     *
     * @param enabled  the left button enabled status
     *
     */
    final public void setRiceTitleLeftButtonEnable(boolean enabled) {
    	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "setRiceTitleLeftButtonEnable");
        if(mleftBtn != null) {
            mleftBtn.setEnabled(enabled);
        }
    }

	/**
	 * Set the enabled state of right title button
	 * 
	 * 
	 * @param enabled
	 *            the right button enabled status
	 * 
	 */
	final public void setYiTitleRightButtonEnable(boolean enabled) {
		setRiceTitleRightButtonEnable(enabled);
	}
	
    /**
     * Set the enabled state of right title button
     *
     *
     * @param enabled  the right button enabled status
     *
     */
    final public void setRiceTitleRightButtonEnable(boolean enabled) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "setRiceTitleLeftButtonEnable");
        if(mRightBtn!= null) {
            mRightBtn.setEnabled(enabled);
        }
    }

	/**
	 * Interface definition for a callback to be invoked when a BaiduAppTitle's
	 * left button is clicked.
	 */
	public interface OnYiLeftClickedListener {
		/**
		 * Called when a view has been clicked.
		 * 
		 * @param v
		 *            The view that was clicked.
		 */
		void onYiTitleLeftButtonClicked(View v);
	}
	
    /**
     * Interface definition for a callback to be invoked when a BaiduAppTitle's left button is clicked.
     */
    public interface OnLeftClickedListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onRiceTitleLeftButtonClicked(View v);
    }

	/**
	 * Interface definition for a callback to be invoked when a BaiduAppTitle's
	 * left button is clicked.
	 */
	public interface OnYiRightClickedListener {
		/**
		 * Called when a view has been clicked.
		 * 
		 * @param v
		 *            The view that was clicked.
		 */
		void onYiTitleRightButtonClicked(View v);
	}
	
    /**
     * Interface definition for a callback to be invoked when a BaiduAppTitle's left button is clicked.
     */
    public interface OnRightClickedListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onRiceTitleRightButtonClicked(View v);
    }
}
