package cc.ifnot.app.libs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/21 10:26 AM
 * description:
 */
public class WaterFall extends ViewGroup {
    private SparseArray<Integer> rows = new SparseArray<>();
    private int x, y;

    public WaterFall(Context context) {
        super(context);
    }

    public WaterFall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterFall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaterFall(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // just layout
        // |A|BB|C|
        // |A|D|EE|
        // |FF|HH|
        //
        Lg.d("onLayout");
        if (changed) {
            Lg.d("onLayout: changed %s[%s] - %s[%s]", getWidth(), getMeasuredWidth(),
                    getHeight(), getMeasuredHeight());
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                Lg.d("child: [%s:%s] x: %s y: %s", child.getMeasuredWidth(),
                        child.getMeasuredHeight(), x, y);
//                Lg.d("%s - %s", child.getWidth(), child.getHeight());
                if (child.getMeasuredWidth() + x > getMeasuredWidth()) {
                    x = 0;
                }
                int yy = rows.get(x, 0);
                Lg.d("in line: %s - %s", x, yy);
                child.layout(l + x, t + yy,
                        child.getMeasuredWidth() + x, child.getMeasuredHeight() + yy);
                rows.put(x, yy + child.getMeasuredHeight());
                x += child.getMeasuredWidth();
                Lg.d("done: %s - %s", x, yy + child.getMeasuredHeight());
            }
        }
    }
}
