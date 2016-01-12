package com.cundong.viewdraghelper.qq;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cundong.viewdraghelper.util.EvaluateUtils;

/**
 * Created by cundong on 2016/1/7.
 *
 * 类似QQ那种侧滑
 */
public class DragLayout extends FrameLayout {

    private OnDragStatusChangedListener mOnDragStatusChangedListener;

    private Status mStatus = Status.Close;

    private int mRange;
    private int mHeight;
    private int mWidth;

    private ViewGroup mLeftContent, mMainContent;

    private ViewDragHelper mDragHelper;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
    }

    public void setOnDragStatusListener(OnDragStatusChangedListener listener) {
        this.mOnDragStatusChangedListener = listener;
    }

    public Status getStatus() {
        return mStatus;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

    /**
     * 1.将事件拦截下来,把自定义控件的事件交给ViewDragHelper去处理
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                mDragHelper.cancel(); // 相当于调用 processTouchEvent收到ACTION_CANCEL
                break;
        }

        /**
         * 检查是否可以拦截touch事件
         * 如果onInterceptTouchEvent可以return true 则这里return true
         */
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 2.接着，将拦截下来的事件进行处理，onTouchEvent返回true，持续接收事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //对多点触摸有点问题
        try {
            // There seems to be a bug on certain devices: "pointerindex out of range" in viewdraghelper
            // https://github.com/umano/AndroidSlidingUpPanel/issues/351
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 当尺寸有变化的时候调用，在onMeasure(...)方法后调用
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mRange = (int) (mWidth * 0.6f);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void open() {
        open(true);
    }

    private void open(boolean isSmooth) {
        int finalLeft = mRange;

        if (isSmooth) {
            if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, mWidth + finalLeft, mHeight);
        }
    }

    public void close() {
        close(true);
    }

    private void close(boolean isSmooth) {

        int finalLeft = 0;

        if (isSmooth) {
            if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mMainContent.layout(finalLeft, 0, mWidth + finalLeft, mHeight);
        }
    }

    private void dispatchDragEvent(int newLeft) {

        float percent = newLeft / (mRange * 1.0f);

        if (mOnDragStatusChangedListener != null) {
            if (mStatus != Status.Close && mStatus != Status.Open) {
                mOnDragStatusChangedListener.onDraging(percent);
            }
        }

        Status preStatus = mStatus;
        mStatus = getStatus(percent);

        if (mStatus != preStatus) {
            if (mStatus == Status.Close) {
                mStatus = Status.Close;
                if (mOnDragStatusChangedListener != null) {
                    mOnDragStatusChangedListener.onClose();
                }
            } else if (mStatus == Status.Open) {
                mStatus = Status.Open;
                if (mOnDragStatusChangedListener != null) {
                    mOnDragStatusChangedListener.onOpen();
                }
            }
        } else {
            if (percent == 0.0) {
                if (mStatus != Status.Close) {
                    if (mOnDragStatusChangedListener != null) {
                        mOnDragStatusChangedListener.onClose();
                    }
                }
            } else if (percent == 1.0) {
                if (mStatus != Status.Open) {
                    if (mOnDragStatusChangedListener != null) {
                        mOnDragStatusChangedListener.onOpen();
                    }
                }
            }
        }

        animViews(percent);
    }

    /**
     * 往右滑动：percent:0.0->1.0
     * 往左滑动：percent:1.0->0.0
     *
     * @param percent 左边大间距占可滑动距离的多少
     */
    private void animViews(float percent) {

        /**
         * 左边面板：
         * 1.缩放动画
         * 2.移动动画
         * 2.透明度变化
         */
        //左面板缩放动画 percent:0.0->1.0  0.5->1
        mLeftContent.setScaleX(EvaluateUtils.evaluateFloat(percent, 0.5f, 1.0f));
        mLeftContent.setScaleY(EvaluateUtils.evaluateFloat(percent, 0.5f, 1.0f));

        //左面板平移动画 percent:0.0->1.0  -mWidth/2->0
        mLeftContent.setTranslationX(EvaluateUtils.evaluateFloat(percent, -mWidth / 2.0f, 0f));

        //左面板透明度变化 percent:0.0->1.0  半透明->透明
        mLeftContent.setAlpha(EvaluateUtils.evaluateFloat(percent, 0.5f, 1.0f));

        /**
         * 主面板：
         * 1.缩放动画
         * 2.透明度变化
         */
        //主面板缩放动画 percent:0.0->1.0，1.0->0.8
        mMainContent.setScaleX(EvaluateUtils.evaluateFloat(percent, 1.0f, 0.8f));
        mMainContent.setScaleY(EvaluateUtils.evaluateFloat(percent, 1.0f, 0.8f));

        //主面板透明度变化 percent:0.0->1.0，1.0->0.8
        mMainContent.setAlpha(EvaluateUtils.evaluateFloat(percent, 1.0f, 0.8f));

        /**
         * 整个背景的亮度变化
         */
        // percent:0.0->1.0，BLACK->TRANSPARENT
        getBackground().setColorFilter(EvaluateUtils.evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    private Status getStatus(float percent) {
        if (percent == 0) {
            return Status.Close;
        } else if (percent == 1.0f) {
            return Status.Open;
        } else {
            return Status.Draging;
        }
    }

    /**
     * 根据范围修正左边值
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > mRange) {
            return mRange;
        }
        return left;
    }

    /**
     * 状态枚举
     */
    public enum Status {
        Close, Open, Draging
    }

    public interface OnDragStatusChangedListener {

        void onClose();

        void onOpen();

        void onDraging(float percent);
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        /**
         * 尝试捕获子view，一定要返回true
         *
         * @param view      尝试捕获的view
         * @param pointerId 区分多点触摸的id
         */
        @Override
        public boolean tryCaptureView(View view, int pointerId) {
            return mLeftContent == view || mMainContent == view;
        }

        /**
         * 返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定了动画执行速度
         *
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (child == mMainContent) {
                left = fixLeft(left);
            }

            return left;
        }

        /**
         * 当View位置改变的时候，处理要做的事情(更新状态，伴随动画，重绘界面)
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            int newLeft = left;

            if (changedView == mLeftContent) {
                //把当前的变化量传递给mMainContent
                newLeft = mMainContent.getLeft() + dx;

                newLeft = fixLeft(newLeft);

                //当左面板移动之后，再强制放回去
                mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
                mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
            }

            dispatchDragEvent(newLeft);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }
    }
}