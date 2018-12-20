package com.mavolas.ripplebutton.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Author by Andy
 * Date on 2018/12/20.
 */
public class RippleButton extends AppCompatButton {


    private RippleDrewable mRippleDrewable;


    public RippleButton(Context context) {
        this(context,null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippleDrewable = new RippleDrewable();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRippleDrewable.draw(canvas);
        super.onDraw(canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return super.onTouchEvent(event);
    }
}
