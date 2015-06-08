
package com.baidu.themeanimation.element;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.view.AnimationViewFactory;

public class AnimatorParser {
    public final static String TAG = "AnimatorParser";
    private final ArrayList<AnimatorElement> mAnimatorElements = new ArrayList<AnimatorElement>();
    private AnimatorDrawableElement mAnimationDrawableElement;
    private AnimatorSet mAnimatorSet;

    public AnimatorParser() {
    }

    public AnimationDrawable getAnimationDrawable() {
        if (mAnimationDrawableElement != null)
            return mAnimationDrawableElement.getAnimationDrawable();
        return null;
    }

    public final AnimatorSet generateAnimatorSet(final View target) {
        final Collection<Animator> aniCollection = new ArrayList<Animator>();
        for (int i = 0; i < mAnimatorElements.size(); i++) {
            aniCollection.add(mAnimatorElements.get(i).generateAnimator(target));
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(aniCollection);
        if (mAnimationDrawableElement != null) {
            target.setBackgroundDrawable(mAnimationDrawableElement.generateAnimatorDrawable(target));
        }
//         animatorSet.setInterpolator(new LinearInterpolator());
//         animatorSet.setTarget(imageview);

//        mAnimatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                Logger.i(TAG, "onAnimationStart");
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//                Logger.i(TAG, "onAnimationRepeat");
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                Logger.i(TAG, "onAnimationEnd");
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                Logger.i(TAG, "onAnimationCancel");
//            }
//        });

        return mAnimatorSet;
    }

    private void inflateAnimatorPosition(final XmlPullParser parser,
            final AnimatorElement animatorElement)
            throws XmlPullParserException, IOException, NumberFormatException {
        if (parser != null) {
            String attrName;
            String attrValue;
            int eventType = parser.next();
            String name = parser.getName();
            final ArrayList<Keyframe> xkeyframes = new ArrayList<Keyframe>();
            final ArrayList<Keyframe> ykeyframes = new ArrayList<Keyframe>();
            float value = 0f, value_time = 0f, value1 = 0f;
            Keyframe ka = null, kb = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG &&
                        name.equalsIgnoreCase(Constants.TAG_POSITION_ANIMATION_POSITION)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrName = parser.getAttributeName(i);
                        attrValue = parser.getAttributeValue(i);
                        if (attrName.equalsIgnoreCase("x")) {
                            value = Float.valueOf(attrValue);
                        } else if (attrName.equalsIgnoreCase("y")) {
                            value1 = Float.valueOf(attrValue);
                        } else if (attrName.equalsIgnoreCase("time")) {
                            value_time = Float.valueOf(attrValue);
                        }
                    }
                    ka = Keyframe.ofFloat(value_time, value);
                    xkeyframes.add(ka);
                    kb = Keyframe.ofFloat(value_time, value1);
                    ykeyframes.add(kb);
                } else if (eventType == XmlPullParser.END_TAG
                        && name.equalsIgnoreCase(Constants.TAG_POSITION_ANIMATION)) {
                    break;
                }
                eventType = parser.next();
                name = parser.getName();
            }

            animatorElement.addKeyframes("x", xkeyframes);
            animatorElement.addKeyframes("y", ykeyframes);
        }
    }

    private void inflateAnimatorSize(final XmlPullParser parser,
            final AnimatorElement animatorElement)
            throws XmlPullParserException, IOException, NumberFormatException {
        if (parser != null) {
            String attrName;
            String attrValue;
            int eventType = parser.next();
            String name = parser.getName();
            final ArrayList<Keyframe> wkeyframes = new ArrayList<Keyframe>();
            final ArrayList<Keyframe> hkeyframes = new ArrayList<Keyframe>();
            float value = 0f, value_time = 0f, value1 = 0f;
            Keyframe ka = null, kb = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG &&
                        name.equalsIgnoreCase(Constants.TAG_SIZE_ANIMATION_SIZE)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrName = parser.getAttributeName(i);
                        attrValue = parser.getAttributeValue(i);
                        if (attrName.equalsIgnoreCase("w")) {
                            value = Float.valueOf(attrValue);
                        } else if (attrName.equalsIgnoreCase("h")) {
                            value1 = Float.valueOf(attrValue);
                        } else if (attrName.equalsIgnoreCase("time")) {
                            value_time = Float.valueOf(attrValue);
                        }
                    }
                    ka = Keyframe.ofFloat(value_time, value);
                    wkeyframes.add(ka);
                    kb = Keyframe.ofFloat(value_time, value1);
                    hkeyframes.add(kb);
                } else if (eventType == XmlPullParser.END_TAG
                        && name.equalsIgnoreCase(Constants.TAG_SIZE_ANIMATION)) {
                    break;
                }
                eventType = parser.next();
                name = parser.getName();
            }

            animatorElement.addKeyframes("scaleX", wkeyframes);
            animatorElement.addKeyframes("scaleY", hkeyframes);
        }
    }

    private void inflateAnimatorAlpha(final XmlPullParser parser,
            final AnimatorElement animatorElement)
            throws XmlPullParserException, IOException, NumberFormatException {
        if (parser != null) {
            String attrName;
            String attrValue;
            int eventType = parser.next();
            String name = parser.getName();
            final ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();
            float value_time = 0f;
            int value = 0;
            Keyframe ka = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG &&
                        name.equalsIgnoreCase(Constants.TAG_ALPHA_ANIMATION_ALPHA)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrName = parser.getAttributeName(i);
                        attrValue = parser.getAttributeValue(i);
                        if (attrName.equalsIgnoreCase("a")) {
                            value = Integer.valueOf(attrValue);
                        } else if (attrName.equalsIgnoreCase("time")) {
                            value_time = Float.valueOf(attrValue);
                        }
                    }
                    ka = Keyframe.ofFloat(value_time, (float)value/255);
                    keyframes.add(ka);
                } else if (eventType == XmlPullParser.END_TAG
                        && name.equalsIgnoreCase(Constants.TAG_ALPHA_ANIMATION)) {
                    break;
                }
                eventType = parser.next();
                name = parser.getName();
            }

            animatorElement.addKeyframes("alpha", keyframes);
        }
    }

    private final void inflateAnimatorRotate(final XmlPullParser parser,
            final AnimatorElement animatorElement)
            throws XmlPullParserException, IOException, NumberFormatException {
        if (parser != null) {
            String attrName;
            String attrValue;
            int eventType = parser.next();
            String name = parser.getName();
            final ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();
            float value_time = 0f;
            float value = 0;
            Keyframe ka = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG &&
                        name.equalsIgnoreCase(Constants.TAG_ROTATE_ANIMATION_ROTATE)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrName = parser.getAttributeName(i);
                        attrValue = parser.getAttributeValue(i);
                        if (attrName.equalsIgnoreCase("angle")) {
                            value = Float.valueOf(attrValue);
                        } else if (attrName.equalsIgnoreCase("time")) {
                            value_time = Float.valueOf(attrValue);
                            if (keyframes.size() > 0 && keyframes.get(0).getFraction() != 0) {
                                value_time = keyframes.get(0).getFraction();
                            } else if (keyframes.size() == 0) {
                                value_time = 0;
                            }
                        }
                    }
                    ka = Keyframe.ofFloat(value_time, value);
                    keyframes.add(ka);
                } else if (eventType == XmlPullParser.END_TAG
                        && name.equalsIgnoreCase(Constants.TAG_ROTATE_ANIMATION)) {
                    break;
                }
                eventType = parser.next();
                name = parser.getName();
            }

            animatorElement.addKeyframes("rotation", keyframes);
        }
    }

    public final void inflateAnimatorSource(final XmlPullParser parser,
            AnimatorDrawableElement aniDrawableElement)
            throws XmlPullParserException, IOException, NumberFormatException {
        if (parser != null) {
            String attrName;
            String attrValue;
            int eventType = parser.next();
            String name = parser.getName();
            int value_time = 0;
            String value = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG &&
                        name.equalsIgnoreCase(Constants.TAG_SOURCE_ANIMATION_SOURCE)) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrName = parser.getAttributeName(i);
                        attrValue = parser.getAttributeValue(i);
                        if (attrName.equalsIgnoreCase("src")) {
                            value = attrValue;
                        } else if (attrName.equalsIgnoreCase("time")) {
                            value_time = Integer.valueOf(attrValue);
                        }
                    }
                    aniDrawableElement.addKeyframes(value, value_time);
                } else if (eventType == XmlPullParser.END_TAG
                        && name.equalsIgnoreCase(Constants.TAG_SOURCE_ANIMATION)) {
                    break;
                }
                eventType = parser.next();
                name = parser.getName();
            }
        }
    }

    public final void inflateAnimatorElement(final XmlPullParser parser)
            throws XmlPullParserException, IOException, NumberFormatException,
            NoSuchMethodException,
            Exception {
        if (parser != null) {
            String attrName;
            String attrValue;
            String methodName;
            Method method;
            int eventType = parser.getEventType();
            String name = parser.getName();
            // while (eventType != XmlPullParser.END_DOCUMENT) {
            // if (eventType == XmlPullParser.END_TAG
            // && name.equals(end_name)) {
            // break;
            // } else
            if (eventType == XmlPullParser.START_TAG) {
                if (name.equalsIgnoreCase(Constants.TAG_SOURCE_ANIMATION)) {
                    mAnimationDrawableElement = new AnimatorDrawableElement();
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attrName = parser.getAttributeName(i);
                        attrValue = parser.getAttributeValue(i);
                        methodName = "set" + Character.toUpperCase(attrName.charAt(0))
                                + attrName.substring(1);
                        method = mAnimationDrawableElement.getClass().getMethod(methodName,
                                String.class);
                        method.invoke(mAnimationDrawableElement, attrValue);
                    }
                    inflateAnimatorSource(parser, mAnimationDrawableElement);
                } else {
                    int type = 0;
                    if (name.equals(Constants.TAG_ALPHA_ANIMATION)) {
                        type = 1;
                    } else if (name.equalsIgnoreCase(Constants.TAG_ROTATE_ANIMATION)) {
                        type = 1;
                    } else if (name.equalsIgnoreCase(Constants.TAG_POSITION_ANIMATION)) {
                        type = 2;
                    } else if (name.equalsIgnoreCase(Constants.TAG_SIZE_ANIMATION)) {
                        type = 2;
                    }
                    if (type > 0) {
                        final AnimatorElement animatorElement = new AnimatorElement();
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            attrName = parser.getAttributeName(i);
                            attrValue = parser.getAttributeValue(i);
                            methodName = "set" + Character.toUpperCase(attrName.charAt(0))
                                    + attrName.substring(1);
                            method = animatorElement.getClass().getMethod(methodName,
                                    String.class);
                            method.invoke(animatorElement, attrValue);
                        }
                        animatorElement.initType(type);
                        if (name.equals(Constants.TAG_ALPHA_ANIMATION)) {
                            inflateAnimatorAlpha(parser, animatorElement);
                        } else if (name.equalsIgnoreCase(Constants.TAG_ROTATE_ANIMATION)) {
                            inflateAnimatorRotate(parser, animatorElement);
                        } else if (name.equalsIgnoreCase(Constants.TAG_POSITION_ANIMATION)) {
                            inflateAnimatorPosition(parser, animatorElement);
                        } else if (name.equalsIgnoreCase(Constants.TAG_SIZE_ANIMATION)) {
                            inflateAnimatorSize(parser, animatorElement);
                        }
                        mAnimatorElements.add(animatorElement);
                    }
                }
            }
        }
        return;
    }

    public void startAnimations() {
        if (mAnimationDrawableElement != null) {
            mAnimationDrawableElement.startAnimations();
        }
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    public void stopAnimations() {
        if (mAnimationDrawableElement != null) {
            mAnimationDrawableElement.stopAnimations();
        }
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    public int getAnimationStatus(){
        if (mAnimatorSet != null && mAnimatorSet.isStarted()) {
            return Constants.Animation_Status_Running;
        }
        if (mAnimationDrawableElement != null) {
            return mAnimationDrawableElement.getAnimationStatus();
        }
        return Constants.Animation_Status_Stop;
    }
    
    public void setAnimationListener(AnimationViewFactory.AnimationListener listener){
        if(mAnimationDrawableElement!=null){
            mAnimationDrawableElement.setAnimatorListener(listener);
        }
    }
}
