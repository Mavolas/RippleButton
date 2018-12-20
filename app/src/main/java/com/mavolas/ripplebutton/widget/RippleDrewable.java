package com.mavolas.ripplebutton.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Author by Andy
 * Date on 2018/12/20.
 */
public class RippleDrewable extends Drawable {

    private int mAlpha = 255;
    private int mRippleColor = 0;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mRipplepointX, mRipplePointY;
    private float mRippleRadius = 200;

    public RippleDrewable() {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // canvas.drawColor(0x70FF0000);
        canvas.drawCircle(mRipplepointX, mRipplePointY, mRippleRadius, mPaint);
        mRippleRadius = mRippleRadius + 10;
    }

    public void setRippleColor(int color) {
        mRippleColor = color;
        onColorOrAlphaChange();
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        onColorOrAlphaChange();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mPaint.getColorFilter() != colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        int alpha = mPaint.getAlpha();
        if (alpha == 255){
            //不透明
            return PixelFormat.OPAQUE;
        }else if (alpha == 0){
            //全透明
            return PixelFormat.TRANSPARENT;
        }else {
            //半透明
            return PixelFormat.TRANSLUCENT;
        }
    }

    private void onColorOrAlphaChange() {
        mPaint.setColor(mRippleColor);
        if (mAlpha != 255) {
            int alpha = mPaint.getAlpha();
            int realAlpha = (int) (alpha * (mAlpha / 255f));
            mPaint.setAlpha(realAlpha);
        }
    }
}
