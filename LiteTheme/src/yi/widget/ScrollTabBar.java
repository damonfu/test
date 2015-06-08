
package yi.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.lite.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import yi.support.v1.utils.Logger;


/**
 * @author rendayun
 */
public class ScrollTabBar {

    private static final String TAG = "SrollTabBar";
    private static final int DEFAULT_SLID_DRAWABLE_RES_PORT = R.drawable.yi_tab_top_selected_baidu;
    private static final int DEFAULT_SLID_DRAWABLE_RES_LAND = R.drawable.yi_tab_top_selected_baidu;

    public interface Interface {
        public void onScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void setTabOnClickListener(int positon, View scrollView, OnClickListener l);
        public void showBadgeView(Activity activity, int tabIndex);
        public void showBadgeView(Activity activity, int tabIndex, CharSequence text);
        public void hideBadgeView(int tabIndex);
    }
    
    /**
     * @param activity
     */
    public static ScrollTabBar.Interface create(Activity activity) {
        Object viewPageObj = activity.findViewById(R.id.viewPager);

        if (viewPageObj != null) {
            return new ScrollTabBarImp(activity, viewPageObj);
        } else {
            return new ScrollTabBarStub();
        }
    }
    
    private static class ScrollTabBarStub implements Interface {
        @Override
        public void showBadgeView(Activity activity, int tabIndex, CharSequence text) {}
        @Override
        public void showBadgeView(Activity activity, int tabIndex) {}
        @Override
        public void setTabOnClickListener(int positon, View scrollView, OnClickListener l) {}
        @Override
        public void onScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void hideBadgeView(int tabIndex) {}
    }
    
    private static class ScrollTabBarImp implements Interface {

        private static final String SCROLLING_HORIZONTAL_VIEW_NAME = "com.android.internal.widget.ScrollingTabContainerView";
        private boolean mUserDefaultSlid = true;
        private boolean mIsActionBarVisible = false;   //actionbarview's visibility
//        private int mDividerWidth = 0;

        ActionBar mActionbar;
        Object mViewPagerObj;
        int mAdapterCount;
        FrameLayout mCursorParent;
        ImageView mCursor;
        int mCursorWidth;
        Map<String, BadgeView> mBadgeList;
        int mTabContainerWidth;
        Drawable mSlidDrawableLand = null;
        Drawable mSlidDrawablePort = null;
        HorizontalScrollView mScrollView;

        private TabClickListener mTabClickListener;
        LinearLayout mTabLayout;
        HashMap<String, View> mScrollContentViews = new HashMap<String, View>();
        HashMap<String, OnClickListener> mOnActionTabClickListeners = new HashMap<String, OnClickListener>();

        /**
         * 
         * @param activity
         * @param viewPagerObj
         */
        private ScrollTabBarImp(Activity activity, Object viewPagerObj) {
            mActionbar = activity.getActionBar();
            mViewPagerObj = viewPagerObj;
            initSlidDrawable(activity);
            mBadgeList = new HashMap<String, BadgeView>();
            init(activity);
        }

        private void initSlidDrawable(Context context) {
            final Resources.Theme theme = context.getTheme();
            TypedArray a = theme.obtainStyledAttributes(R.styleable.ActionTabBarSlid);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ActionTabBarSlid_actionTabBarPortSlid) {
                    mSlidDrawableLand = a.getDrawable(attr);

                } else if (attr == R.styleable.ActionTabBarSlid_actionTabBarLandSlid) {
                    mSlidDrawablePort = a.getDrawable(attr);

                }
            }
            a.recycle();
            if(mSlidDrawableLand == null) {
                mSlidDrawableLand = context.getResources().getDrawable(R.drawable.yi_tab_top_selected_baidu);
            }
            
            if(mSlidDrawablePort == null) {
                mSlidDrawablePort = context.getResources().getDrawable(R.drawable.yi_tab_selected_baidu);
            }
        }
        
        /**
         * @param activity
         */
        public void init(final Activity activity) {
            final FrameLayout containerView = getActionContainerView(activity);
            if (containerView != null) {
                mScrollView = getHorizontalScrollView(activity, containerView);

                if (mScrollView != null) {
                    ViewGroup view = (ViewGroup) mScrollView.getChildAt(0);
                    ViewGroup.LayoutParams params = null;
                    if (view != null) {
                        if (mCursorParent == view) {
                            View tabView = mCursorParent.getChildAt(0);
                            params = mCursorParent.getLayoutParams();
                            mCursorParent.removeAllViews();
                            mScrollView.removeView(mCursorParent);
                            mScrollView.addView(tabView, params);
                        }
                        
                        final ViewGroup childView = (ViewGroup) mScrollView.getChildAt(0);
                        if (childView != null && childView instanceof LinearLayout) {
                            params = childView.getLayoutParams();
                            mScrollView.removeView(childView);

                            if (mCursorParent == null) {
                                mCursorParent = new FrameLayout(activity);
                                mCursorParent.setBackgroundColor(Color.TRANSPARENT);
                            } else {
                                mCursorParent.removeAllViews();
                            }

                            ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(
                                    params);
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            mScrollView.addView(mCursorParent, params1);

                            mCursorParent.addView(childView, 0, params);
                            childView.getViewTreeObserver().addOnPreDrawListener(
                                    new ViewTreeObserver.OnPreDrawListener() {

                                        public boolean onPreDraw() {
                                            // TODO
                                            // Auto-generated
                                            // method stub
                                            if (mCursorParent == ((FrameLayout) childView
                                                    .getParent())) {
                                                mTabContainerWidth = childView
                                                        .getMeasuredWidth();
                                                mAdapterCount = childView.getChildCount();//getAdapterCount(viewPagerObj);
                                                //mIsActionBarVisible = isActionBarViewVisible(activity);
                                                //mDividerWidth = getDividerWidth((LinearLayout)childView);
                                                if(mAdapterCount != 0) {
                                                    addCursorView(activity,
                                                            mCursorParent,
                                                            mTabContainerWidth,
                                                            mActionbar);
                                                }
                                            }
                                            childView.getViewTreeObserver()
                                                    .removeOnPreDrawListener(
                                                            this);
                                            return false;
                                        }
                                    });
                            childView.requestLayout();
                            childView.invalidate();
                            rebuildClickEventOnActionTab((LinearLayout) childView);
                        }

//                        mScrollView.getViewTreeObserver().addOnPreDrawListener(
//                                new ViewTreeObserver.OnPreDrawListener() {
//
//                                    public boolean onPreDraw() {
//                                        // TODO Auto-generated method stub
//                                        final ViewGroup view = (ViewGroup) mScrollView.getChildAt(0);
//                                        ViewGroup.LayoutParams params = null;
//                                        if (view != null && view instanceof LinearLayout) {
//                                            params = view.getLayoutParams();
//                                            mScrollView.removeView(view);
//
//                                            if (mCursorParent == null) {
//                                                mCursorParent = new FrameLayout(activity);
//                                                mCursorParent.setBackgroundColor(Color.TRANSPARENT);
//                                            } else {
//                                                mCursorParent.removeAllViews();
//                                            }
//
//                                            ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(
//                                                    params);
//                                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                                            mScrollView.addView(mCursorParent, params1);
//
//                                            mCursorParent.addView(view, 0, params);
//                                            view.getViewTreeObserver().addOnPreDrawListener(
//                                                    new ViewTreeObserver.OnPreDrawListener() {
//
//                                                        public boolean onPreDraw() {
//                                                            // TODO
//                                                            // Auto-generated
//                                                            // method stub
//                                                            if (mCursorParent == ((FrameLayout) view
//                                                                    .getParent())) {
//                                                                mTabContainerWidth = view
//                                                                        .getMeasuredWidth();
//                                                                addCursorView(activity,
//                                                                        mCursorParent,
//                                                                        mTabContainerWidth,
//                                                                        mViewPagerObj);
//                                                            }
//                                                            view.getViewTreeObserver()
//                                                                    .removeOnPreDrawListener(
//                                                                            this);
//                                                            return false;
//                                                        }
//                                                    });
//                                            view.requestLayout();
//                                            view.invalidate();
//                                            rebuildClickEventOnActionTab((LinearLayout) view);
//
//                                        }
//                                        mScrollView.getViewTreeObserver().removeOnPreDrawListener(this);
//                                        return false;
//                                    }
//
//                                });
                    }
                }
                containerView.requestLayout();
                containerView.invalidate();
            }
        }

        private int getDividerWidth(LinearLayout ll) {
            int width = 0;
            int showDividers = ll.getShowDividers();
            Drawable divider = null;
            try{
                Field field = ll.getClass().getDeclaredField("mDivider");
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Object object = field.get(ll);
                String className = object.getClass().getName();
                if(object instanceof Drawable) {
                    divider = (Drawable) object;
                }
                field.setAccessible(accessible);
            } catch(Exception e){
                e.printStackTrace();
            }

            if(divider != null) {
                switch(showDividers) {
                    case LinearLayout.SHOW_DIVIDER_MIDDLE:
                        if(ll.getOrientation() == LinearLayout.HORIZONTAL) {
                            width = divider.getIntrinsicWidth();
                        } else {
                            width = divider.getIntrinsicHeight();
                        }
                        break;
                    default:
                        break;
                }
            }
            return width;
        }

        /**
         * @param context
         * @return the orientation of this device
         */
        int getOrientation(Context context) {
            return context.getResources().getConfiguration().orientation;
        }

        /**
         * @param context
         * @return
         */
        boolean hasEmbeddedTabs(Context context) {
            int id = context.getResources().getIdentifier("action_bar_embed_tabs", "bool", "android");
            return context.getResources().getBoolean(id);
        }

        /**
         * @param context
         * @param containerView
         * @return
         */
        HorizontalScrollView getHorizontalScrollView(Activity activity, FrameLayout containerView) {
            HorizontalScrollView hScrollView = null;
            ActionBar actionBar = activity.getActionBar();
            if(actionBar != null) {
                try{
                    Field field = actionBar.getClass().getDeclaredField("mTabScrollView");
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    Object object = field.get(actionBar);
                    String className = object.getClass().getName();
                    if(className.equals(SCROLLING_HORIZONTAL_VIEW_NAME)) {
                        hScrollView = (HorizontalScrollView) object;
                    }
                    field.setAccessible(accessible);
                    android.util.Log.d("getHorizontalScrollView", "rendayun getHorizontalScrollView");
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            
//            boolean hasEmbeddedTabs = hasEmbeddedTabs(context);
//            if (containerView != null) {
//                if (hasEmbeddedTabs) {
//                    int actionBarId = context.getResources().getIdentifier("action_bar", "id",
//                            "android");
//                    ViewGroup actionBarView = (ViewGroup) containerView.findViewById(actionBarId);
//                    for (int i = 0; i < actionBarView.getChildCount(); i++) {
//                        View childView = actionBarView.getChildAt(i);
//                        String className = childView.getClass().getName();
//                        if (className.equals(SCROLLING_HORIZONTAL_VIEW_NAME)) {
//                            hScrollView = (HorizontalScrollView) childView;
//                            break;
//                        }
//                    }
//                } else {
//                    for (int i = 0; i < containerView.getChildCount(); i++) {
//                        View childView = containerView.getChildAt(i);
//                        String className = childView.getClass().getName();
//                        Logger.d("rendayun", " " + childView.getClass().getName());
//                        if (className.equals(SCROLLING_HORIZONTAL_VIEW_NAME)) {
//                            hScrollView = (HorizontalScrollView) childView;
//                            break;
//                        }
//                    }
//                }
//            }

            return hScrollView;
        }

        private int getAdapterCount(Object viewPagerObj) {
            int count = 0;
            Class<?> viewPagerClass = viewPagerObj.getClass();
            try {
                Method getAdapterM = viewPagerClass.getMethod("getAdapter", (Class[]) null);
                if(getAdapterM != null) {
                    Object adapterObj = getAdapterM.invoke(viewPagerObj, (Object[]) null);                
                    if (adapterObj != null) {
                        Class<?> adapterClass = adapterObj.getClass();
                        Method getCountM = adapterClass.getMethod("getCount", (Class[]) null);
                        if(getCountM != null) {
                            Integer countObj = (Integer)getCountM.invoke(adapterObj, (Object[])null);
                            if(countObj != null) {
                                count = countObj.intValue();                            
                            }
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return count;
        }

        
        private int getViewPagerCurrentItem(Object viewPagerObj) {
            int currentItem = 0;
            Class<?> viewPagerClass = viewPagerObj.getClass();
            try {
                Method getCurrentItemM = viewPagerClass.getMethod("getCurrentItem", (Class[]) null);
                if(getCurrentItemM != null) {
                    Object currentItemObj = getCurrentItemM.invoke(viewPagerObj, (Object[]) null);                
                    if (currentItemObj != null) {
                        Integer currentItemI = (Integer)currentItemObj;
                        currentItem = currentItemI.intValue();
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return currentItem;
        }

        /**
         * 
         * @param context
         * @param cursorParent
         * @param tabContainerWidth
         * @param viewPagerObj
         */
        void addCursorView(Context context, ViewGroup cursorParent, int tabContainerWidth,
                ActionBar actionBar) {
            if (mCursor == null)
                mCursor = new ImageView(context);
            mCursor.setScaleType(ImageView.ScaleType.FIT_XY);

            int orientation = getOrientation(context);
            switch (orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    mCursor.setImageDrawable(mSlidDrawableLand);
                    break;
                default:
                    mCursor.setImageDrawable(mSlidDrawablePort);
                    break;
            }

            int tabCount = mAdapterCount;
            ActionBar.Tab selectTab = actionBar.getSelectedTab();
            int selectedTabIndex = 0;//getViewPagerCurrentItem(viewPagerObj);
            if(selectTab != null) {
                selectedTabIndex = selectTab.getPosition();
            }
            mCursorWidth = tabContainerWidth / tabCount;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mCursorWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

            if (selectedTabIndex == tabCount - 1) {
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            } else if (selectedTabIndex > 0 && selectedTabIndex < tabCount - 1) {
                params.gravity = Gravity.BOTTOM;
                params.leftMargin = selectedTabIndex * mCursorWidth;
            } else {
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                params.leftMargin = 0;
            }

            cursorParent.addView(mCursor, params);
        }

        /**
         * @param activity
         * @return
         */
        FrameLayout getActionContainerView(Activity activity) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            final int resId = activity.getResources().getIdentifier("action_bar_container", "id",
                    "android");
            return (FrameLayout) decorView.findViewById(resId);
        }

        boolean isActionBarViewVisible(Activity activity) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int resId = activity.getResources().getIdentifier("action_bar", "id",
                        "android");
            if(resId != 0) {
                View view = decorView.findViewById(resId);
                if(view != null) {
                    return (view.getHeight() != 0);
                }
            }
            return false;
        }
        
        private void rebuildClickEventOnActionTab(LinearLayout tabLayout) {
            mTabLayout = tabLayout;
            if (mTabLayout != null) {
                if (mTabClickListener == null) {
                    mTabClickListener = new TabClickListener();
                }
                final int tabCount = mTabLayout.getChildCount();
                for (int i = 0; i < tabCount; i++) {
                    final View child = mTabLayout.getChildAt(i);
                    child.setOnClickListener(mTabClickListener);
                    //change tabView Background
                    //setTabViewBackground(child);
                }
            }
        }

        public void setTabOnClickListener(int positon, View scrollView, OnClickListener l) {
            if (mScrollContentViews == null) {
                mScrollContentViews = new HashMap<String, View>();
            }

            if (mOnActionTabClickListeners == null) {
                mOnActionTabClickListeners = new HashMap<String, OnClickListener>();
            }

            String key = String.valueOf(positon);
            if (mScrollContentViews.containsKey(key)) {
                mScrollContentViews.remove(key);
            }

            if (mOnActionTabClickListeners.containsKey(key)) {
                mOnActionTabClickListeners.remove(key);
            }
            mScrollContentViews.put(String.valueOf(positon), scrollView);
            mOnActionTabClickListeners.put(String.valueOf(positon), l);
        }

        private class TabClickListener implements View.OnClickListener {
            public void onClick(View view) {
                int index = mTabLayout.indexOfChild(view);
                boolean oldSelected = view.isSelected();
                mActionbar.getTabAt(index).select();
                final int tabCount = mTabLayout.getChildCount();
                for (int i = 0; i < tabCount; i++) {
                    final View child = mTabLayout.getChildAt(i);
                    child.setSelected(child == view);
                }

                if (oldSelected) {
                    if (mOnActionTabClickListeners != null) {
                        OnClickListener onActionTabClickListener = mOnActionTabClickListeners
                                .get(String.valueOf(index));
                        if (onActionTabClickListener != null) {
                            onActionTabClickListener.onClick(view);
                            return;
                        }
                    }

                    View scrollContentView = mScrollContentViews.get(String.valueOf(index));
                    if (scrollContentView != null) {
                        if (scrollContentView instanceof ScrollView) {
                            ((ScrollView) scrollContentView).smoothScrollTo(0, 0);
                        } else if (scrollContentView instanceof AbsListView) {
                            AbsListView listView = ((AbsListView) scrollContentView);
                            listView.smoothScrollToPosition(0);
                            // if (!listView.isStackFromBottom()) {
                            // listView.setStackFromBottom(true);
                            // }
                            // listView.setStackFromBottom(false);
                        }
                    }
                }
            }
        }

        /**
         * 
         * @param position
         * @param positionOffset
         * @param positionOffsetPixels
         */
        public void onScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mCursor != null && mAdapterCount != 0) {
                Context context = mCursor.getContext();
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                int screenWidth = dm.widthPixels;
                int end = position * mCursorWidth + mCursorWidth * positionOffsetPixels / screenWidth;
//                int end = position * (mCursorWidth + mDividerWidth) + mCursorWidth * positionOffsetPixels / screenWidth;

                int tabCount = mAdapterCount;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mCursor.getLayoutParams();
                Logger.d("ViewPager", "Viewpager:position = " + position
                        + " positionOffset = " + positionOffset + " positionOffsetPixels = "
                        + positionOffsetPixels + " end = " + end + " screenWidth = " + dm.widthPixels);

                if(position == tabCount - 1) {
                    params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                } else {
                    if (Float.compare(positionOffset, 0.0f) != 0 || positionOffsetPixels != 0) {
                        params.gravity = Gravity.BOTTOM;
                        params.leftMargin = end;
                    } else if (Float.compare(positionOffset, 0.0f) == 0 && positionOffsetPixels == 0) {
                        if (position < tabCount - 1) {
                            params.gravity = Gravity.BOTTOM;
                            params.leftMargin = position * mCursorWidth;
//                            params.leftMargin = position * (mCursorWidth + mDividerWidth);
                        }
                    }
                }

//                ViewGroup parent = (ViewGroup)mCursor.getParent();
//                View ll = parent.getChildAt(0);
//                if(ll instanceof LinearLayout) {
//                    FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) ll.getLayoutParams();
//                    params1.width = parent.getWidth();
//                }

                mCursor.requestLayout();
            }
        }

        /**
         * @param activity
         * @param tabIndex
         */
        public void showBadgeView(Activity activity, int tabIndex) {
            showBadgeView(activity, tabIndex, 0, null);
        }

        /**
         * @param activity
         * @param tabIndex
         * @param text
         */
        public void showBadgeView(Activity activity, int tabIndex, CharSequence text) {
            showBadgeView(activity, tabIndex, 0, text);
        }

        /**
         * 
         * @param activity
         * @param position
         * @param targetResId
         * @param text
         */
        private void showBadgeView(Activity activity, int position, int targetResId, CharSequence text) {
            BadgeView badge = getBadgeView(activity, position, targetResId, text);

            if (badge != null && !badge.isShown())
                badge.show(true);
        }

        /**
         * 
         * @param tabIndex
         */
        public void hideBadgeView(int tabIndex) {
            BadgeView badge = mBadgeList.get(String.valueOf(tabIndex));
            if (badge != null && badge.isShown()) {
                badge.hide(true);
            }
        }

        /**
         * @param context
         * @param position
         * @param targetResId
         * @return
         */
        View getTargetView(Activity activity, int position, int targetResId) {
            ViewGroup tabParent = null;
            HorizontalScrollView scrollView = getHorizontalScrollView(activity,
                    getActionContainerView(activity));
            if (scrollView != null) {
                View view = scrollView.getChildAt(0);
                if (view != null) {
                    if (view instanceof LinearLayout) {
                        tabParent = (ViewGroup) view;
                    } else if (view instanceof FrameLayout) {
                        ViewGroup vp = (ViewGroup) view;
                        int childCount = vp.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View childView = vp.getChildAt(i);
                            if (childView instanceof LinearLayout) {
                                tabParent = (ViewGroup) childView;
                                break;
                            }
                        }
                    }
                }
            }

            return getTabView(tabParent, position, targetResId);
        }

        /**
         * @param tabParent
         * @param position
         * @param targetResId
         * @return
         */
        View getTabView(ViewGroup tabParent, int position, int targetResId) {
            View tabView = null;
            if (tabParent != null) {
                ViewGroup tabLayout = (ViewGroup) tabParent.getChildAt(position);
                if (tabLayout != null) {
                    if (targetResId != 0) {
                        tabView = tabLayout.findViewById(targetResId);
                    } else {
                        for (int i = 0; i < tabLayout.getChildCount(); i++) {
                            View childView = tabLayout.getChildAt(i);
                            if (childView instanceof TextView) {
                                tabView = childView;
                                break;
                            }
                        }
                    }
                }
            }
            return tabView;
        }

        /**
         * create a badge view on assign position
         * 
         * @param activity
         * @param position
         * @param targetResId
         * @param text
         * @return
         */
        public BadgeView getBadgeView(Activity activity, int position, int targetResId,
                CharSequence text) {
            BadgeView badge = mBadgeList.get(String.valueOf(position));
            if (badge == null) {
                View target = getTargetView(activity, position, targetResId);

                if (target != null) {
                    badge = new BadgeView(activity, target, position, targetResId);
                    badge.setContainerMatchParent(true);
                    mBadgeList.put(String.valueOf(position), badge);
                }
            }

            if (badge != null) {
                badge.setText(text);
            }
            return badge;
        }

        // void updateBadges(Context context) {
        // int tabCount = mViewPager.getAdapter().getCount();
        // BadgeView badge = null;
        // for (int i = 0; i < tabCount; i++) {
        // badge = mBadgeList.get(String.valueOf(i));
        // if (badge != null) {
        // View target = getTargetView(context, badge.getTargetTabIndex(),
        // badge.getTargetResId());
        // if (target != null) {
        // boolean isShown = badge.isShown();
        // badge.applyTo(target);
        // if (isShown)
        // badge.show(true);
        // } else {
        // mBadgeList.remove(String.valueOf(i));
        // }
        // }
        // }
        // }

    }


}
