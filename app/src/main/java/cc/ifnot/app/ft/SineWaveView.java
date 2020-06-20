package cc.ifnot.app.ft;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/20 9:09 AM
 * description:
 */
public class SineWaveView extends View {
    public SineWaveView(Context context) {
        super(context);
    }

    public SineWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SineWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SineWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setColor(Color.MAGENTA);
        canvas.drawPoint(getWidth() / 2, getHeight() / 2, mPaint);
//        mPaint.setTextSize(30);
        float start = 0;
        for (float i = 0; i < 2 * Math.PI * 100; i++) {
//            A——振幅，当物体作轨迹符合正弦曲线的直线往复运动时，其值为行程的1/2。
//            (ωx+φ)——相位，反映变量y所处的状态。
//            φ——初相，x=0时的相位；反映在坐标系上则为图像的左右移动。
//            k——偏距，反映在坐标系上则为图像的上移或下移。
//            ω——角速度， 控制正弦周期(单位弧度内震动的次数)。
            // y=Asin(ωx+φ)+k
            float y = (float) (0.5 * 3 * (Math.sin(1 * i / 100 + start)) + 0);

            canvas.drawPoint(i * 2, y * 200 + getHeight() / 2, mPaint);
            Lg.d("(%s - %s)%s - %s", getWidth(), getHeight(),
                    i * 2, y * 200 + getHeight() / 2);
//            postInvalidateDelayed(1000);
        }


    }
}
