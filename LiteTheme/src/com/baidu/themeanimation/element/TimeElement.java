
package com.baidu.themeanimation.element;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;

import com.baidu.themeanimation.manager.ExpressionManager;
import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;

/*
 * display a image time components, each time digit is use picture to display, 
 * it has attributes x, y, src, the src specify the picture to display the digit, the format is src+"_"+digit+".png"
 */
public class TimeElement extends VisibleElement {
    private String mSrc;
    private TimeElementView mTimeElementView;

    private final static String TAG = "TimeElement";

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_TIME.equals(tagName) || Constants.TAG_TIME_BAIDU.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new TimeElement();
    }

    @Override
    public void setX(int posX) {
        super.setX(posX);
        TimeElementView view = (TimeElementView) getView();
        if (view != null) {
            view.updateLayoutParams();
        }

        ExpressionManager.getInstance().setVariableValue(this.getName() + ".actual_w", posX);
    }

    @Override
    public void setY(int posY) {
        super.setY(posY);
        ExpressionManager.getInstance().setVariableValue(this.getName() + ".actual_h", posY);
    }

    public String getSrc() {
        return mSrc;
    }

    public void setSrc(String src) {
        this.mSrc = src;
    }

    public void clearView() {
        super.clearView();
        if (mTimeElementView != null) {
            ViewGroup viewGroup = (ViewGroup) mTimeElementView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mTimeElementView);
            }
            // mView.clearAnimation();
            mTimeElementView = null;
        }
    }

    @Override
    public View generateView(Context context, Handler handler) {
        if (mTimeElementView == null) {
            mTimeElementView = new TimeElementView(this, context);
        }

        setView(mTimeElementView);
        return mTimeElementView;
    }

    public class TimeElementView extends LinearLayout implements OnTimeTick {
        private TimeElement mTimeElement;
        private int mHour;
        private int mMinute;

        private boolean checkIs24HoursMode() {
            android.content.ContentResolver cv = this.getContext().getContentResolver();
            // 获取当前系统设置
            String strTimeFormat = android.provider.Settings.System.getString(cv,
                    android.provider.Settings.System.TIME_12_24);
            if (strTimeFormat.equals("24")) {
                return true;
            }
            return false;
        }

        public TimeElementView(TimeElement timeElement, Context context) {
            super(context);

            mTimeElement = timeElement;
            setOrientation(LinearLayout.HORIZONTAL);

            // get the current time
            Time time = new Time();
            time.setToNow();
            mHour = time.hour;
            mMinute = time.minute;

            if (!checkIs24HoursMode()) {
                mHour = (mHour >= 12) ? (mHour - 12) : mHour;
            }

            // current only support 24-hours format, display HH:MM
            addView(createDigitView(mHour / 10, context));
            addView(createDigitView(mHour % 10, context));
            addView(createDigitView(':', context));
            addView(createDigitView(mMinute / 10, context));
            addView(createDigitView(mMinute % 10, context));

            updateLayoutParams();

            mTimeElement.setOnTimeTick(this);
        }

        private void updateLayoutParams() {
            int width = 0;
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                ImageView view = (ImageView) getChildAt(i);
                width += view.getDrawable().getIntrinsicWidth();
                if (height < view.getDrawable().getIntrinsicHeight()) {
                    height = view.getDrawable().getIntrinsicHeight();
                }
            }

            setRealW(width);
            setRealH(height);
            setLayoutParams(genLayoutParams());
        }

        public void refreshTime() {
            Time time = new Time();
            time.setToNow();
            int hour = time.hour;
            int minute = time.minute;
            ImageView imageView;

            if (!checkIs24HoursMode()) {
                hour = (hour >= 12) ? (hour - 12) : hour;
            }

            if (mHour != hour) {
                if (mHour / 10 != hour / 10) {
                    imageView = (ImageView) getChildAt(0);
                    FileUtil.getInstance().setBitmap(getFilePath(hour / 10), imageView);
                }
                if ((mHour % 10) != (hour % 10)) {
                    imageView = (ImageView) getChildAt(1);
                    FileUtil.getInstance().setBitmap(getFilePath(hour % 10), imageView);
                }
                mHour = hour;
            }

            if (mMinute != minute) {
                if (mMinute / 10 != minute / 10) {
                    imageView = (ImageView) getChildAt(3);
                    FileUtil.getInstance().setBitmap(getFilePath(minute / 10), imageView);
                }
                if ((mMinute % 10) != (minute % 10)) {
                    imageView = (ImageView) getChildAt(4);
                    FileUtil.getInstance().setBitmap(getFilePath(minute % 10), imageView);
                }
                mMinute = minute;
            }
            updateLayoutParams();
        }

        private String getFilePath(int digit) {
            String filename = mTimeElement.getSrc();
            int dotIndex = filename.lastIndexOf('.');
            if (digit >= 0 && digit <= 9) {
                if (dotIndex > 0) {
                    filename = filename.substring(0, dotIndex) + "_" + digit
                            + filename.substring(dotIndex);
                }
            } else if (digit == ':') {
                if (dotIndex > 0) {
                    filename = filename.substring(0, dotIndex) + "_dot"
                            + filename.substring(dotIndex);
                }
            }
            return filename;
        }

        private ImageView createDigitView(int digit, Context context) {
            ImageView imageView = new ImageView(context);
            String srcPath = getFilePath(digit);
            Bitmap bitmap = FileUtil.getInstance().getElementBitmap(srcPath);
            imageView.setImageBitmap(bitmap);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            return imageView;
        }

        public void onTimeTick(Time time) {
            refreshTime();
        }
    }
}
