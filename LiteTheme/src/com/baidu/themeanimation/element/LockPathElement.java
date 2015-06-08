
package com.baidu.themeanimation.element;

import org.xmlpull.v1.XmlPullParser;

import com.baidu.themeanimation.util.Constants;
import com.baidu.themeanimation.util.FileUtil;
import com.baidu.themeanimation.util.XmlParserHelper;

public class LockPathElement extends Element {
    private int mTolerance = 0;
    private int mX = 0;
    private int mY = 0;
    private LockPosition mStartPoint = null;
    private LockPosition mEndPoint = null;
    private int mPositionIndex = 0;

    @Override
    public boolean matchTag(String tagName) {
        return Constants.TAG_UNLOCKER_PATH.equals(tagName);
    }

    @Override
    public Element createElement(String tagName) {
        return new LockPathElement();
    }

    @Override
    public void addElement(Element element) {
        super.addElement(element);
        if (element instanceof LockPosition) {
            if (mPositionIndex == 0) {
                setStartPoint((LockPosition) element);
            } else if (mPositionIndex == 1) {
                setEndPoint((LockPosition) element);
            }
            mPositionIndex++;
        }
    }

    @Override
    public Element parseChild(XmlPullParser parser) throws Exception {
        String tagName = parser.getName();
        LockPosition position = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals(Constants.TAG_POSITION)) {
                    position = new LockPosition();
                    XmlParserHelper.setElementAttr(parser, position);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    break;
                }
            }
            eventType = parser.next();
        }
        return position;
    }

    public int getTolerance() {
        return mTolerance;
    }

    public void setTolerance(int tolerance) {
        mTolerance = tolerance;
    }

    public void setTolerance(String tolerance) {
        setTolerance(Integer.valueOf(tolerance));
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        // jianglin:scale
        x *= FileUtil.X_SCALE;

        mX = x;
    }

    public void setX(String x) {
        setX(Integer.valueOf(x));
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        // jianglin:scale
        y *= FileUtil.Y_SCALE;

        mY = y;
    }

    public void setY(String y) {
        setY(Integer.valueOf(y));
    }

    public LockPosition getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(int x, int y) {
        // jianglin:scale
        x *= FileUtil.X_SCALE;
        y *= FileUtil.Y_SCALE;

        mStartPoint.x = x;
        mStartPoint.y = y;
    }

    public void setStartPoint(LockPosition position) {
        mStartPoint = position;
    }

    public LockPosition getEndPoint() {
        return mEndPoint;
    }

    public void setEndPoint(int x, int y) {
        // jianglin:scale
        x *= FileUtil.X_SCALE;
        y *= FileUtil.Y_SCALE;

        mEndPoint.x = x;
        mEndPoint.y = y;
    }

    public void setEndPoint(LockPosition position) {
        mEndPoint = position;
    }

    public int getMinX() {
        return mX + mStartPoint.x;
    }

    public int getMaxX() {
        return mX + mEndPoint.x;
    }

    public int getMinY() {
        return mY + mStartPoint.y;
    }

    public int getMaxY() {
        return mY + mEndPoint.y;
    }

    public static class LockPosition extends BottomElement {
        public int x = 0;
        public int y = 0;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            x *= FileUtil.X_SCALE;
            this.x = x;
        }

        public void setX(String x) {
            setX(Integer.valueOf(x));
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            y *= FileUtil.Y_SCALE;

            this.y = y;
        }

        public void setY(String y) {
            setY(Integer.valueOf(y));
        }
    }
}
