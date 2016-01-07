package com.cundong.viewdraghelper.qq;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by liucundong on 2016/1/7.
 *
 * 正文使用的容器，为了处理DragLayout打开状态时右面的Touch事件
 */
public class MainContainer extends LinearLayout {

    private DragLayout mDragLayout;

    public MainContainer(Context context) {
        super(context);
    }

    public MainContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(DragLayout dragLayout) {
        this.mDragLayout = dragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragLayout != null && mDragLayout.getStatus() == DragLayout.Status.Open) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mDragLayout != null) {
            if (mDragLayout.getStatus() == DragLayout.Status.Open) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mDragLayout.close();
                }
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }
}