package com.wulian.wlcamera.customview;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;



/**
 * created by huxc  on 2018/4/3.
 * func：摄像机亮度调节、单击隐藏全屏中的控件手势
 * email: hxc242313@qq.com
 */

public class CameraGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = "BrightnessGesture";
    private float startY;//记录手指按下时的Y坐标
    private float startX;//记录手指按下时的x坐标
    private MyGestureListener listener;

    public interface MyGestureListener {
        void OnBrightChanged(float brightness);

        void onSingleTouchConfirmed();
    }

    public CameraGestureListener(Context context, MyGestureListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.i(TAG, "onDown");
        startY = event.getY();
        startX = event.getX();
        return true;//false 会忽略掉手势
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final double FLING_MIN_DISTANCE = 0.5;
        final double FLING_MIN_VELOCITY = 0.5;
        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(e1.getY() - e2.getY()) > FLING_MIN_VELOCITY) {
            listener.OnBrightChanged(20);
        }
        if (e1.getY() - e2.getY() < FLING_MIN_DISTANCE
                && Math.abs(e1.getY() - e2.getY()) > FLING_MIN_VELOCITY) {
            listener.OnBrightChanged(-20);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i(TAG, "onFling" + "------>" + velocityX);
        if (velocityX > 0) {
        } else if (velocityX < 0) {
        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        listener.onSingleTouchConfirmed();
        return super.onSingleTapConfirmed(e);
    }
}