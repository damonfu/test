
package com.yi.internal.view.menu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.lite.R;

public class ActionMenuItemView extends LinearLayout implements MenuView.ItemView,
        View.OnClickListener {
    private static final String TAG = "ActionMenuItemView";
    private MenuItemImpl mItemData;
    private CharSequence mTitle;
    private Drawable mIcon;
    private MenuBuilder.ItemInvoker mItemInvoker;
    private TextView mTextView;
    private ImageView mImageView;
    
    private int mShadowColor = 0;
    private ColorStateList mTextColor = null;
    private Drawable mActionMenuBackground = null;

    public ActionMenuItemView(Context context) {
        this(context, null);
    }

    public ActionMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.actionMenuStyle);
    }

    public ActionMenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(
                attrs, R.styleable.ActionMenu, defStyle, 0);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ActionMenu_actionMenuTextColor) {
                mTextColor = a.getColorStateList(attr);

            } else if (attr == R.styleable.ActionMenu_actionMenuTextShadow) {
                mShadowColor = a.getColor(attr, 0);

            } else if (attr == R.styleable.ActionMenu_actionMenuBackground) {
                mActionMenuBackground = a.getDrawable(attr);

            }
        }
        if(mTextColor == null) {
            mTextColor = context.getResources().getColorStateList(R.color.cld_action_menu_item_text_baidu_light);
        }
        
        if(mActionMenuBackground == null) {
            mActionMenuBackground = context.getResources().getDrawable(R.drawable.cld_action_bar_btn_background);
        }

        if(mShadowColor == 0) {
            mShadowColor = context.getResources().getColor(R.color.cld_action_menu_item_text_shadow_baidu_light);
        }

        setBackgroundDrawable(mActionMenuBackground);
        a.recycle();
        setOnClickListener(this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (TextView) findViewById(android.R.id.title);
        mTextView.setTextColor(mTextColor);
        mTextView.setShadowLayer(1.2f, 1.2f, 1.2f, mShadowColor);
        mImageView = (ImageView) findViewById(android.R.id.icon);
    }

    @Override
    public void initialize(MenuItemImpl itemData, int menuType) {
        // TODO Auto-generated method stub
        mItemData = itemData;

        setIcon(itemData.getIcon());
        setTitle(itemData.getTitleForItemView(this)); // Title only takes effect
                                                      // if there is no icon
        setId(itemData.getItemId());

        setVisibility(itemData.isVisible() ? View.VISIBLE : View.GONE);
        setEnabled(itemData.isEnabled());
    }

    @Override
    public MenuItemImpl getItemData() {
        // TODO Auto-generated method stub
        return mItemData;
    }

    @Override
    public void setTitle(CharSequence title) {
        // TODO Auto-generated method stub
        mTitle = title;
        setContentDescription(mTitle);
        mTextView.setText(mTitle);
    }

    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub
        // setEnabled(enabled);
    }

    @Override
    public void setCheckable(boolean checkable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIcon(Drawable icon) {
        // TODO Auto-generated method stub
        mIcon = icon;
        mImageView.setImageDrawable(mIcon);
    }

    @Override
    public boolean prefersCondensedTitle() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean showsIcon() {
        // TODO Auto-generated method stub
        return true;
    }

    public void setItemInvoker(MenuBuilder.ItemInvoker invoker) {
        mItemInvoker = invoker;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mItemInvoker != null) {
            mItemInvoker.invokeItem(mItemData);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        final CharSequence cdesc = getContentDescription();
        if (!TextUtils.isEmpty(cdesc)) {
            event.getText().add(cdesc);
        }
    }
}
