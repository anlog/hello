package cc.ifnot.app.libs.glide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: dp
 * created on: 2020/7/20 6:18 PM
 * description:
 */
class VG extends ViewGroup {
    private int x = 0;
    private int y = 0;

    public VG(Context context) {
        super(context);
    }

    public VG(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VG(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public VG(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();

        if (changed) {
            getParent().requestLayout();
        } else {
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
//                child.measure(getMeasuredWidth(), getMeasuredHeight());
                if (child.getMeasuredWidth() + x > getWidth()) {
                    x = 0;
                    y += child.getWidth();
                }
                x = child.getWidth();
                child.layout(l + x, t + y,
                        r + x, b + y);
            }
        }

    }
}
