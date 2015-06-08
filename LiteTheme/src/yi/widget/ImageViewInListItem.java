package yi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Special class to to allow the parent to be pressed/selected without being pressed/selected itself.
 * This way the line of a tab can be pressed/selected, but the image itself is not.
 */
public class ImageViewInListItem extends ImageView {

    public ImageViewInListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        // If the parent is pressed, do not set to pressed.
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }
    
    @Override
    public void setSelected(boolean selected) {
        // If the parent is selected, do not set to selected.
        if (selected && ((View) getParent()).isSelected()) {
            return;
        }
        super.setSelected(selected);
    }
}
