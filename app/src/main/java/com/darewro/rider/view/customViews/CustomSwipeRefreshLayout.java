package com.darewro.rider.view.customViews;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    private float mPrevX;
    private float mPrevY;
    private boolean paused;

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                setEnabled(true);
                break;
            default:
                if (paused) {
                    setEnabled(false);
                    return false;
                } else {
                    setEnabled(true);
                    return super.onInterceptTouchEvent(ev);
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

/*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                mPrevY = MotionEvent.obtain(event).getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                final float eventY = event.getY();
                float xDiff = eventX - mPrevX;
                float yDiff = eventY - mPrevY;

                Log.i("CSRL xDiff = ", xDiff + "");
                Log.i("CSRL yDiff = ", yDiff + "");
                Log.i("CSRL mTouchSlop = ", mTouchSlop + "");
                if (Math.abs(xDiff) > mTouchSlop) {
                    return false;
                } else {
                    this.setRefreshing(true);
                }
                */
/*if (yDiff > mTouchSlop) {
                    return true;
                }*//*

        }

        return super.onInterceptTouchEvent(event);
    }
*/


}
