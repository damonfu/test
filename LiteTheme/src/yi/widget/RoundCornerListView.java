
package yi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.lite.R;

public class RoundCornerListView extends ListView {
    private static final String TAG = "RoundCornerListView";
    public static final int TOP_ROUND_CORNER = 0x1;
    public static final int BOTTOM_ROUND_CORNER = 0x2;
    public static final int TOP_BOTTOM_ROUND_CORNER = 0x3;
    public static final int NO_ROUND_CORNER = 0x0;

    Drawable mTopSelector;
    Drawable mBottomSelector;
    Drawable mSingleSelector;
    Drawable mNormalSelector;

    int mCornerType = NO_ROUND_CORNER;

    public RoundCornerListView(Context context) {
        super(context);
    }

    public RoundCornerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttrs(context, attrs, R.attr.roundCornerListViewStyle);
    }

    public RoundCornerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadAttrs(context, attrs, defStyle);
    }

    /**
     * load other attributes
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    private void loadAttrs(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundCornerListView, defStyle, 0);
        mNormalSelector = a
                .getDrawable(R.styleable.RoundCornerListView_listSelector);
        mTopSelector = a
                .getDrawable(R.styleable.RoundCornerListView_topListSelector);
        mBottomSelector = a
                .getDrawable(R.styleable.RoundCornerListView_bottomListSelector);
        mSingleSelector = a
                .getDrawable(R.styleable.RoundCornerListView_singleListSelector);
        mCornerType = a.getInt(R.styleable.RoundCornerListView_cornerType,
                NO_ROUND_CORNER);
        a.recycle();
        
        if(mNormalSelector == null) {
            mNormalSelector = context.getResources().getDrawable(R.drawable.cld_list_selector_baidu_light);
        }
        
        if(mTopSelector == null) {
            mTopSelector = context.getResources().getDrawable(R.drawable.cld_list_top_selector_baidu_light);
        }
        
        if(mBottomSelector == null) {
            mBottomSelector = context.getResources().getDrawable(R.drawable.cld_list_bottom_selector_baidu_light);
        }
        
        if(mSingleSelector == null) {
            mSingleSelector = context.getResources().getDrawable(R.drawable.cld_list_single_selector_baidu_light);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                int itemnum = pointToPosition(x, y);
                if (itemnum == AdapterView.INVALID_POSITION) {
                    break;
                }

                switch (mCornerType) {
                    case BOTTOM_ROUND_CORNER:
                        if (itemnum == (getAdapter().getCount() - 1)) {
                            if (mBottomSelector != null) {
                                setSelector(mBottomSelector);
                            }
                        } else {
                            if (mNormalSelector != null) {
                                setSelector(mNormalSelector);
                            }
                        }
                        break;
                    case TOP_ROUND_CORNER:
                        if (itemnum == 0) {
                            if (mTopSelector != null) {
                                setSelector(mTopSelector);
                            }
                        } else {
                            if (mNormalSelector != null) {
                                setSelector(mNormalSelector);
                            }
                        }
                        break;
                    case TOP_BOTTOM_ROUND_CORNER:
                        if (itemnum == 0) {
                            if (itemnum == (getAdapter().getCount() - 1)) {
                                if (mSingleSelector != null) {
                                    setSelector(mSingleSelector);
                                }
                            } else {
                                if (mTopSelector != null) {
                                    setSelector(mTopSelector);
                                }
                            }
                        } else if (itemnum == (getAdapter().getCount() - 1)) {
                            if (mBottomSelector != null) {
                                setSelector(mBottomSelector);
                            }
                        } else {
                            if (mNormalSelector != null) {
                                setSelector(mNormalSelector);
                            }
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * @param cornerType
     */
    public void setCornerType(int cornerType) {
        mCornerType = cornerType;

        if (mCornerType == NO_ROUND_CORNER && mNormalSelector != null) {
            setSelector(mNormalSelector);
        }
    }

    public int getCornerType() {
        return mCornerType;
    }

    public void setCornerSelector(int topResID, int bottomResID, int singleResID) {
        Resources res = getResources();
        setCornerSelector(res.getDrawable(topResID), res.getDrawable(bottomResID),
                res.getDrawable(singleResID));
    }

    public void setCornerSelector(Drawable topSel, Drawable bottomSel, Drawable singleSel) {
        mTopSelector = topSel;
        mBottomSelector = bottomSel;
        mSingleSelector = singleSel;
    }

    public Drawable getTopSelector() {
        return mTopSelector;
    }

    public Drawable getBottomSelector() {
        return mBottomSelector;
    }

    public Drawable getSingleSelector() {
        return mSingleSelector;
    }
}
