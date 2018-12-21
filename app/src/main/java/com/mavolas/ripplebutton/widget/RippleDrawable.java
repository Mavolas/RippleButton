package com.mavolas.ripplebutton.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;


/**
 * Author by Andy
 * Date on 2018/12/20.
 */
public class RippleDrawable extends Drawable {

    //最大的透明度
    private static final int MAX_ALPHA_BG = 172;
    private static final int MAX_ALPHA_CIRCLE = 255;

    //drawable 0~255 透明度
    private int mAlpha = 255;
    private int mRippleColor = 0;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //圆心坐标、半径
    private float mRipplepointX, mRipplePointY;
    private float mRippleRadius = 0;

    //背景透明度，圆形透明度
    private int mBgAlpha = 0, mCircleAlpha = MAX_ALPHA_CIRCLE;

    //点击点
    private float mDownPointX, mDownPointY;
    //控件的中心区域
    private float mCenterPointX, mCenterPointY;
    //开始和结束的半径
    private float mStartRadius, mEndRadius;

    //进入动画完成flag
    private boolean mEnterDone;
    //进入动画进度
    private float mEnterProgress = 0;
    //进入动画插值器，实现从快到慢
    private Interpolator mEnterInterpolator = new DecelerateInterpolator(2);
    //每次递增的进度值
    private float mEnterIncrement = 16f / 360;

    //退出动画进度
    private float mExitProgress = 0;
    //退出动画插值器，实现从快到慢
    private Interpolator mExitInterpolator = new AccelerateInterpolator(2);
    //每次递减的进度值
    private float mExitIncrement = 16f / 280;

    //标识用户手是否抬起
    private boolean mTouchRelease;


    public RippleDrawable(int color) {

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        //设置涟漪颜色
        setRippleColor(color);
    }

    /**
     * 通过两块玻璃叠加后颜色更深，
     * 光线透过更少算法,反向推出其中一块玻璃值
     * @param preAlpha
     * @param bgAlpha
     * @return
     */
    private int getCircleAlpha(int preAlpha, int bgAlpha) {
        int dAlpha = preAlpha - bgAlpha;
        return (int) ((dAlpha * 255f) / (255f - bgAlpha));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        int preAlpha = mPaint.getAlpha();
        int bgAlpha = (int) (preAlpha * (mBgAlpha / 255f));
        int maxCircleAlpha = getCircleAlpha(preAlpha, bgAlpha);
        int circleAlpha = (int) (maxCircleAlpha * (mCircleAlpha / 255f));

        mPaint.setAlpha(bgAlpha);
        //绘制背景区域颜色
        canvas.drawColor(mPaint.getColor());
        mPaint.setAlpha(circleAlpha);
        //绘制一个圆
        canvas.drawCircle(mRipplepointX, mRipplePointY, mRippleRadius, mPaint);
        mPaint.setAlpha(preAlpha);
    }


    private Runnable mEnterRunable = new Runnable() {
        @Override
        public void run() {

            mEnterProgress = mEnterProgress + mEnterIncrement;

            if (mEnterProgress > 1) {
                onEnterProgress(1);
                onEnterDone();
                return;
            }
            float realProgress = mEnterInterpolator.getInterpolation(mEnterProgress);

            onEnterProgress(realProgress);
            //延迟16 毫秒，刷新频率接近60FPS
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };


    private Runnable mExitRunable = new Runnable() {
        @Override
        public void run() {

            //进入时，首先判断进入动画是否有
            if (!mEnterDone)
                return;

            mExitProgress = mExitProgress + mExitIncrement;

            if (mExitProgress > 1) {
                onExitProgress(1);
                onExitDone();
                return;
            }
            float realProgress = mExitInterpolator.getInterpolation(mExitProgress);

            onExitProgress(realProgress);
            //延迟16 毫秒，刷新频率接近60FPS
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    };


    private void onEnterProgress(float progress) {

        mRippleRadius = getProgressValue(mStartRadius, mEndRadius, progress);
        mRipplepointX = getProgressValue(mDownPointX, mCenterPointX, progress);
        mRipplePointY = getProgressValue(mDownPointY, mCenterPointY, progress);

        mBgAlpha = (int) getProgressValue(0, 182, progress);

        invalidateSelf();

    }


    private void onExitProgress(float progress) {

        //背景减淡
        mBgAlpha = (int) getProgressValue(MAX_ALPHA_BG, 0, progress);
        //圆形减淡
        mCircleAlpha = (int) getProgressValue(MAX_ALPHA_CIRCLE, 0, progress);
        invalidateSelf();
    }

    private float getProgressValue(float start, float end, float progress) {

        return start + (end - start) * progress;
    }


    public void onTouch(MotionEvent event) {

        event.getAction();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event.getX(), event.getY());
                break;
        }
    }

    /**
     * 界面大小改变使触发
     * @param bounds
     */
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        //获取中心点
        mCenterPointX = bounds.centerX();
        mCenterPointY = bounds.centerY();
        //得到圆的最大半径
        float maxRadius = Math.max(mCenterPointX, mCenterPointY);
        mStartRadius = maxRadius * 0f;
        mEndRadius = maxRadius * 0.9f;
    }

    private void onTouchCancel(float x, float y) {
        mTouchRelease = true;
        if (mEnterDone) {
            startExitRunable();
        }
    }

    private void onTouchUp(float x, float y) {

        mTouchRelease = true;
        if (mEnterDone) {
            startExitRunable();
        }

    }

    private void onTouchMove(float x, float y) {
    }

    private void onTouchDown(float x, float y) {

        mDownPointX = x;
        mDownPointY = y;

        mTouchRelease = false;
        startEnterRunable();
    }

    private void onEnterDone() {

        mEnterDone = true;
        //当用户手放开时候，启动退出动画
        if (mTouchRelease) {
            startExitRunable();
        }

    }

    private void onExitDone() {

    }

    private void startEnterRunable() {

        mCircleAlpha = MAX_ALPHA_CIRCLE;

        mEnterProgress = 0;
        mEnterDone = false;
        //取消事物
        unscheduleSelf(mExitRunable);
        unscheduleSelf(mEnterRunable);
        //注入一个进入动画
        scheduleSelf(mEnterRunable, SystemClock.uptimeMillis());
    }

    private void startExitRunable() {

        mExitProgress = 0;
        //取消事物
        unscheduleSelf(mEnterRunable);
        unscheduleSelf(mExitRunable);
        //注入一个退出动画
        scheduleSelf(mExitRunable, SystemClock.uptimeMillis());
    }

    public void setRippleColor(int color) {
        mRippleColor = color;
        onColorOrAlphaChange();
        invalidateSelf();
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
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        int alpha = mPaint.getAlpha();
        if (alpha == 255) {
            //不透明
            return PixelFormat.OPAQUE;
        } else if (alpha == 0) {
            //全透明
            return PixelFormat.TRANSPARENT;
        } else {
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
