
package com.baidu.themeanimation.element;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.util.FloatMath;
import android.os.Handler;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.Utils;

public class TextElement extends VisibleElement {

    public static final int DEFAULT_TEXT_SIZE = 10;
    public static final int DEFAULT_TEXT_COLOR = 0x000000;
    public static final int DEFAULT_MARQUEEN_SPEED = 30;

    private int mColor = DEFAULT_TEXT_COLOR;
    private int mSize = DEFAULT_TEXT_SIZE;
    private String mFormat = null;
    private String mParas = null;

    // the speed of the text scrolling when the text
    // is exceed the width of display area
    private int mMarqueeSpeed = DEFAULT_MARQUEEN_SPEED;
    private String mText;
    private boolean mBoldText = false;
    private boolean mStartAnimationOnTextChange = false;

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_TEXT.equals(tagName)
                || Constants.TAG_TEXT_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new TextElement();
    }

    public void setTextExp(String textExp) {
        setText(textExp);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public void setColor(String color) {
        try {
            setColor(Color.parseColor(color));
        } catch (Exception e) {
            Logger.w("TextElement", "Color+" + e.toString());
        }
    }

    public boolean getBold() {
       return this.mBoldText;
    }

    public void setBold(boolean bold) {
        this.mBoldText = bold;
    }

    public void setBold(String bold) {
        setVisibility(Utils.getBoolean(bold));
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        this.mSize = (int)(size * FileUtil.Image_X_SCALE);
    }

    public void setSize(String size) {
        if (size != null) {
            setSize(Integer.valueOf(size));
        }
    }

    public String getFormat() {
        return mFormat;
    }

    public void setFormat(String format) {
        this.mFormat = format;
    }

    public void setParas(String paras) {
        if (paras == null) {
            return;
        }
        mParas = paras;
        if (mFormat != null) {
            String[] params = paras.split(",");
            Object[] targets = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                try {
                    if (Utils.getStringType(param) > 0) {
                        targets[i] = (int) Double.parseDouble(param);
                    } else {
                        targets[i] = param;
                    }
                } catch (Exception e) {
                    // not a number
                    targets[i] = param;
                }
            }
            setText(String.format(mFormat, targets));
        }
    }

    public int getMarqueeSpeed() {
        return mMarqueeSpeed;
    }

    public void setMarqueeSpeed(int marqueeSpeed) {
        this.mMarqueeSpeed = marqueeSpeed;
    }

    public void setMarqueeSpeed(String marqueeSpeed) {
        if (marqueeSpeed != null) {
            setMarqueeSpeed(Integer.valueOf(marqueeSpeed));
        }
    }

    public String getText() {
        return mText;
    }

    /*
     * the text may contain the @variable_name to construct a string in current
     * expression manager, we make the #{variable} as variable, so we need
     * handle the case which will use @variable_name. the priority is text >
     * format format will use params to construct a string. the handle flow is
     * 1. check whether text have @ type expression 2. if does't have, use
     * format instead 3. otherwise use text as the string constructor.
     */
    public void setText(String text) {
        if (text != null && text.startsWith("'") && text.endsWith("'")) {
            text = text.substring(1, text.length() - 1);
        }
        this.mText = text;
        if (mTextElementView != null) {
            mTextElementView.updateText(mText);
            if (mStartAnimationOnTextChange) {
                startAnimations();
            }
        }
    }

    /*
     * set whether restart the animation when the text content has been changed
     */
    public void setAnimationOnTextChange(boolean change) {
        mStartAnimationOnTextChange = change;
    }

    public void setAnimationOnTextChange(String change) {
        setAnimationOnTextChange(Boolean.valueOf(change));
    }

    @Override
    protected void onCategoryChange(int category) {
        super.onCategoryChange(category);
    }

    private TextElementView mTextElementView;

    public void clearView() {
        super.clearView();
        if (mTextElementView != null) {
            ViewGroup viewGroup = (ViewGroup) mTextElementView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mTextElementView);
            }
            mTextElementView = null;
        }
    }

    @Override
    public View generateView(Context context, Handler handler) {
        if (mTextElementView == null) {
            mTextElementView = new TextElementView(context);
        }

        setView(mTextElementView);
        return mTextElementView;
    }

    public class TextElementView extends View {
        private int mTextWidth = LayoutParams.WRAP_CONTENT;
        private int mTextHeight = LayoutParams.WRAP_CONTENT;

        public TextElementView(Context context) {
            super(context);

            mPaint.setColor(getColor());
            mPaint.setTextSize(getSize());

            FontMetrics fm = mPaint.getFontMetrics();
            mTextHeight = (int) FloatMath.ceil(fm.descent - fm.ascent);
            if (getText() != null) {
                mPaint.setTypeface(getBold() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
                mTextWidth = (int) mPaint.measureText(getText());
            }
            setRealW(mTextWidth + 10);
            setRealH(mTextHeight);
            setLayoutParams(genLayoutParams());

            if (hasW()) {
                truncateText(getW());
            }
        }

        private int mEndIndex = -1;
        private final static String mTruncate = "...";

        private void truncateText(int length) {
            mEndIndex = -1;
            if (length < mTextWidth) {
                String text = getText();
                if (text != null) {
                    int start = 0;
                    int end = text.length();
                    int middle = 0;
                    int tempLength = 0;
                    String tempTextString;
                    while (start < end) {
                        middle = (start + end) / 2;
                        tempTextString = text.substring(0, middle) + mTruncate;
                        tempLength = (int) mPaint.measureText(tempTextString);
                        // Logger.v("truncate", "start=" + start + ", end=" +
                        // end + ", middle="
                        // + middle + ", tempLength=" + tempLength);
                        if (tempLength <= length && tempLength > length - 10) {
                            // ok
                            mEndIndex = middle;
                            break;
                        } else if (tempLength > length) {
                            end = middle;
                        } else {
                            mEndIndex = middle;
                            if (start == middle) {
                                start = middle + 1;
                            } else {
                                start = middle;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (getText() != null) {
                // the alpha may change before the view has generated
                mPaint.setAlpha(TextElement.this.getAlpha());
                if (mEndIndex >= 0) {
                    canvas.drawText(getText().substring(0, mEndIndex) + mTruncate, 0,
                            (int) (canvas.getClipBounds().bottom * 0.8), mPaint);
                } else {
                    canvas.drawText(getText(), 0, (int) (canvas.getClipBounds().bottom * 0.8),
                            mPaint);
                }
            }
        }

        public void updateText(CharSequence text) {
            mEndIndex = -1;
            if (text != null) {
                mPaint.setTypeface(getBold() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
                mTextWidth = (int) mPaint.measureText(text.toString());
                setRealW(mTextWidth);
                setLayoutParams(genLayoutParams());
            }
        }
    }
}
