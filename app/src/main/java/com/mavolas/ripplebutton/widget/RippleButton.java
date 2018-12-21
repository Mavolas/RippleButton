package com.mavolas.ripplebutton.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Author by Andy
 * Date on 2018/12/20.
 */
public class RippleButton extends AppCompatButton {

    private RippleDrawable mRippleDrawable;

    public RippleButton(Context context) {
        this(context, null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippleDrawable = new RippleDrawable(0x30000000);
        //设置刷新接口，View 中已经实现
        mRippleDrawable.setCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRippleDrawable.draw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置Drawable 绘制和刷新区域
        mRippleDrawable.setBounds(0, 0, getWidth(), getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRippleDrawable.onTouch(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        //验证draw able 是否OK
        return who == mRippleDrawable || super.verifyDrawable(who);
    }


}
