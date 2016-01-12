package com.cundong.viewdraghelper.douban;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cundong.viewdraghelper.R;

public class DragLayout extends FrameLayout {

    private String TAG = this.getClass().getSimpleName();

    private ViewDragHelper mViewDragHelper;

    //上下滑动的实际高度（总高度-bottomBar）
    private int mDragedHeight;
    private int mHeight;

    //音乐相关信息
    private ViewGroup mMusicContainer;
    private TextView mTypeView, mTitleView, mSingerView;
    private ImageView mCoverImage;

    //底部音乐控制bar
    private ViewGroup mButtomBar;

    //音乐删除、喜欢、忽略按钮
    private ImageView mTrashImgeView, mLikeImgeView, mSkipImgeView;

    // 封面初始X、Y
    private int mCoverInitX = 0;
    private int mCoverInitY = 0;

    // 歌名初始X、Y
    private int mTitleInitX = 0;
    private int mTitleInitY = 0;

    // 歌手初始X、Y
    private int mSingerInitX = 0;
    private int mSingerInitY = 0;

    // 封面最终X、Y
    private int mCoverLastX = 0;
    private int mCoverLastY = 0;

    // 歌名初始X、Y
    private int mTitleLastX = 0;
    private int mTitleLastY = 0;

    // 歌手初始X、Y
    private int mSingerLastX = 0;
    private int mSingerLastY = 0;

    //初始状态Open
    private Status mStatus = Status.Open;

    private OnViewDragListener mOnViewDragListener = null;

    public DragLayout(Context context) {
        this(context, null);

    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnViewDragListener(OnViewDragListener listener) {
        this.mOnViewDragListener = listener;
    }

    public Status getStatus() {
        return mStatus;
    }

    private Status getStatus(float percent) {
        if (percent == 0) {
            return Status.Open;
        } else if (percent == 1.0f) {
            return Status.Close;
        } else {
            return Status.Draging;
        }
    }

    /**
     * onFinishInflate  -> onSizeChanged ->onWindowFocusChanged -> onLayout
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.d(TAG, "onFinishInflate");

        if (getChildCount() < 2) {
            throw new RuntimeException("Content view must contains two child views at least.");
        }

        mMusicContainer = (ViewGroup) getChildAt(1);

        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        Log.d(TAG, "onLayout");

        mHeight = getHeight();
        mDragedHeight = mHeight - mButtomBar.getLayoutParams().height;

        mCoverInitX = mCoverImage.getLeft();
        mCoverInitY = mCoverImage.getTop();

        mTitleInitX = mTitleView.getLeft();
        mTitleInitY = mTitleView.getTop();

        mSingerInitX = mSingerView.getLeft();
        mSingerInitY = mSingerView.getTop();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void init() {

        mCoverImage = (ImageView) findViewById(R.id.image_cover);
        mTypeView = (TextView) findViewById(R.id.type_view);
        mTitleView = (TextView) findViewById(R.id.title_view);
        mSingerView = (TextView) findViewById(R.id.singer_view);

        mButtomBar = (ViewGroup) findViewById(R.id.bottom_bar);

        mCoverLastX = getContext().getResources().getDimensionPixelOffset(R.dimen.cover_last_x);
        mCoverLastY = getContext().getResources().getDimensionPixelOffset(R.dimen.cover_last_y);

        mTitleLastX = getContext().getResources().getDimensionPixelOffset(R.dimen.title_last_x);
        mTitleLastY = getContext().getResources().getDimensionPixelOffset(R.dimen.title_last_y);

        mSingerLastX = getContext().getResources().getDimensionPixelOffset(R.dimen.singer_last_x);
        mSingerLastY = getContext().getResources().getDimensionPixelOffset(R.dimen.singer_last_y);

        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);

        mTrashImgeView = (ImageView) findViewById(R.id.image_trash);
        mLikeImgeView = (ImageView) findViewById(R.id.image_like);
        mSkipImgeView = (ImageView) findViewById(R.id.image_skip);

        mTrashImgeView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "trash", Toast.LENGTH_SHORT).show();
            }
        });

        mLikeImgeView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "like", Toast.LENGTH_SHORT).show();
            }
        });

        mSkipImgeView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "skip", Toast.LENGTH_SHORT).show();
            }
        });

        mMusicContainer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( mStatus == Status.Close ) {
                    open();
                } else if ( mStatus == Status.Open) {
                    close();
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                mViewDragHelper.cancel();
                break;
        }

        /**
         * 检查是否可以拦截touch事件
         * 如果onInterceptTouchEvent可以return true 则这里return true
         */
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //对多点触摸有点问题
        try {
            // There seems to be a bug on certain devices: "pointerindex out of range" in viewdraghelper
            // https://github.com/umano/AndroidSlidingUpPanel/issues/351
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 处理拖拽事件
     *
     * @param top:可滑动的View距顶部的高度
     */
    private void dispatchDragEvent(int top) {

        //percent:0 open
        //percent:1 close
        float percent = top / (mDragedHeight * 1.0f);

        if (mOnViewDragListener != null) {
            if(mStatus != Status.Open && mStatus != Status.Close) {
                mOnViewDragListener.onDraging(percent);
            }
        }

        Status preStatus = mStatus;
        mStatus = getStatus(percent);

        if (mStatus != preStatus) {
            if (mStatus == Status.Close) {
                mStatus = Status.Close;
                if (mOnViewDragListener != null) {
                    mOnViewDragListener.onClose();
                }
            } else if (mStatus == Status.Open) {
                mStatus = Status.Open;
                if (mOnViewDragListener != null) {
                    mOnViewDragListener.onOpen();
                }
            }
        } else {
            if (percent == 0.0) {
                if (mStatus != Status.Open) {
                    if (mOnViewDragListener != null) {
                        mOnViewDragListener.onClose();
                    }
                }
            } else if (percent == 1.0) {
                if (mStatus != Status.Close) {
                    if (mOnViewDragListener != null) {
                        mOnViewDragListener.onOpen();
                    }
                }
            }
        }

        setViewAnimation(top, percent);
    }

    /**
     * 设置动画效果
     *
     * @param top     可滑动View距顶部的高度
     * @param percent 可滑动的View显示的百分比
     */
    private void setViewAnimation(int top, float percent) {
        /**歌曲相关动画*/

        //0.音乐类型 动画
        mTypeView.setAlpha(1 - 1.5f * percent);

        //1.封面移动动画
        mCoverImage.setTranslationX(-1 * mCoverInitX * percent);
        mCoverImage.setTranslationY(-1 * mCoverInitY * percent);

        //2.设置锚点的X坐标值，如果不设置，默认以View的中心为锚点，设置以后，将作为View旋转中心点的X坐标
        mCoverImage.setPivotX(mCoverLastX);
        mCoverImage.setPivotY(mCoverLastY);

        //缩放为原来的0.6倍，160dp->96dp，所以，bottomBar.height = PivotY + PivotY + 96
        float coverScale = 1 - percent * 0.4f;

        //3.缩放
        mCoverImage.setScaleX(coverScale);
        mCoverImage.setScaleY(coverScale);

        //4.歌名动画
        mTitleView.setTranslationX(-1 * (mTitleInitX - mTitleLastX) * percent);
        mTitleView.setTranslationY(-1 * (mTitleInitY - mTitleLastY) * percent);

        //5.歌手View动画
        mSingerView.setTranslationX(-1 * (mSingerInitX - mSingerLastX ) * percent);
        mSingerView.setTranslationY(-1 * (mSingerInitY - mSingerLastY) * percent);

        /**底部 bottomBar 动画*/

        //1.移动动画
        mButtomBar.setTranslationY(-top);

        //2.设置ScaleX动画的锚点，ScaleY就不设置了，直接以中心为锚点
        mButtomBar.setPivotX(getWidth());

        //3.X、Y缩放
        mButtomBar.setScaleX(1 - percent * 0.3f);
        mButtomBar.setScaleY(1 - percent * 0.3f);

        //4.bottomBar Trash ImageView 动画
        mTrashImgeView.setAlpha(1 - 1.5f * percent);

        if(percent == 0f) {
            mTrashImgeView.setClickable(true);
        } else {
            mTrashImgeView.setClickable(false);
        }
    }

    public void open() {

        if (mViewDragHelper.smoothSlideViewTo(mMusicContainer, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }

    public void close() {
        if (mViewDragHelper.smoothSlideViewTo(mMusicContainer, 0, mDragedHeight)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }

    public enum Status {
        Close, Open, Draging
    }

    public interface OnViewDragListener {

        void onClose();

        void onOpen();

        void onDraging(float percent);
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == mMusicContainer;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            Log.i(TAG, "top:" + top);
            dispatchDragEvent(top);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (yvel == 0 && mMusicContainer.getTop() > mHeight / 3.0f) {
                close();
            } else if (yvel > 0) {
                close();
            } else {
                open();
            }
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mHeight;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            final int topBound = getPaddingTop();
            final int bottomBound = mHeight - mButtomBar.getLayoutParams().height;
            return Math.min(Math.max(top, topBound), bottomBound);
        }
    }
}