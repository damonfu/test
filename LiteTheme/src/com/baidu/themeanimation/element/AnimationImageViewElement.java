
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.os.Handler;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.Logger;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.XmlParserHelper;
import com.baidu.themeanimation.view.AnimationViewFactory;

public class AnimationImageViewElement extends VisibleElement {
    private static final String TAG = "AnimationImageViewElement";

    private ImageView imageview;

    private Bitmap mSrcDrawable;
    private boolean mHasSrcDrawable = false;

    private double scale = 1.0f;

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_ANIMATION_IMAGE.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new AnimationImageViewElement();
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                getAnimatorParser().inflateAnimatorElement(parser);
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return null;
    }

    @Override
    public View generateView(Context context, Handler handler) {
        Logger.i(TAG, "generateView");

        if (imageview == null) {
            imageview = new ImageView(context);
        }

        if (mHasSrcDrawable && mSrcDrawable != null) {
            imageview.setImageBitmap(mSrcDrawable);
        }
        setView(imageview);

        if (mAnimatorParser != null) {
            mAnimatorParser.generateAnimatorSet(imageview);
            // ObjectAnimator.ofFloat(imageview, "pivotX", this.getCenterX());
            // ObjectAnimator.ofFloat(imageview, "pivotY", this.getCenterY());
        }

        if (imageview.getLayoutParams() == null && mAnimatorParser.getAnimationDrawable() != null) {
            LayoutParams layoutParams = new LayoutParams(
                    (int) (mAnimatorParser.getAnimationDrawable().getIntrinsicWidth() * scale),
                    (int) (mAnimatorParser.getAnimationDrawable().getIntrinsicHeight() * scale));

            Logger.i(TAG, "w = " + mAnimatorParser.getAnimationDrawable().getIntrinsicWidth());
            Logger.i(TAG, "h = " + mAnimatorParser.getAnimationDrawable().getIntrinsicHeight());

            imageview.setLayoutParams(layoutParams);
        }

        // super.setX(x);
        // super.setY(y);

        updateView();

        return imageview;
    }

    @Override
    public void startAnimations() {
        if (mAnimatorParser != null) {
            mAnimatorParser.startAnimations();
        }
    }

    @Override
    public void clearAnimations() {
        if (mAnimatorParser != null) {
            mAnimatorParser.stopAnimations();
        }
    }

    public void setScale(String s) {
        scale = Double.valueOf(s);
    }

    public void setSrc(String src) {
        if (src != null) {
            mSrcDrawable = FileUtil.getInstance().getElementBitmap(
                    src);
            if (mSrcDrawable != null) {
                mHasSrcDrawable = true;
                if (imageview != null) {
                    imageview.setImageBitmap(mSrcDrawable);
                    mHasSrcDrawable = false;
                }
            }
        }
    }
    @Override
    public void setAnimationListener(AnimationViewFactory.AnimationListener listener){
        mAnimatorParser.setAnimationListener(listener);
    }
}
