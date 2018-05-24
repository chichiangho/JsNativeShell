package com.chichaingho.routor.jsnativeshell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LimitedSlidingPaneLayout extends SlidingPaneLayout {
    private int Touch_Max;
    private int firstX = -1;
    private boolean touchAble = true;
    private boolean localTouchAble = true;//本地代码定义，总控制开关

    public LimitedSlidingPaneLayout(@NonNull Context context) {
        super(context);
    }

    public LimitedSlidingPaneLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitedSlidingPaneLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return touchAble && localTouchAble && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!touchAble || !localTouchAble) {//only here is not enough,override onTouchEvent too
            return false;
        }

        if (Touch_Max <= 0)
            Touch_Max = (int) getResources().getDimension(R.dimen.limitedslidingpanelayout_max_slide_width);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                firstX = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (firstX < 0)
                    firstX = (int) ev.getX();
                break;
        }

        if (firstX < Touch_Max) {
            return super.onInterceptTouchEvent(ev);
        } else {
            MotionEvent cancelEvent = MotionEvent.obtain(ev);
            cancelEvent.setAction(MotionEvent.ACTION_DOWN);
            return super.onInterceptTouchEvent(cancelEvent);
        }
    }

    public void setTouchAble(boolean touchAble) {
        this.touchAble = touchAble;
    }

    public void setLocalTouchAble(boolean localTouchAble) {
        this.localTouchAble = localTouchAble;
    }
}
