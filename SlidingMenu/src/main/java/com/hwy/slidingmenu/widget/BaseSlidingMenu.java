package com.hwy.slidingmenu.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

import com.hwy.slidingmenu.SlidingMode;

/**
 * 作者: hewenyu
 * 日期: 2018/11/15 19:40
 * 说明: 侧滑菜单的基类
 */
public abstract class BaseSlidingMenu extends HorizontalScrollView {

    /**
     * 内容视图
     */
    protected View mContentView;

    /**
     * 菜单视图
     */
    protected View mMenuView;

    /**
     * 容器视图
     */
    protected ViewGroup mContainer;

    /**
     * 菜单的宽度
     */
    protected int mMenuWidth;

    /**
     * 速度检测
     */
    protected GestureDetector mGestureDetector;

    /**
     * 滚动工具类
     */
    protected Scroller mScroller;

    /**
     * 系统认为的最小侧滑距离
     */
    protected int mTouchSlop;

    /**
     * 屏幕宽度
     */
    protected int mScreenWidth;

    protected Context mContext;

    /**
     * 是否打开状态
     */
    protected boolean mIsOpen = false;

    /**
     * 需要处理的ViewPager
     */
    private ViewPager mViewPager;

    // region ---------- 自定义属性 ----------

    protected int mSlidingMargin = 50;

    protected int mSlidingDuration = 200;

    /**
     * 是否允许侧滑
     */
    protected boolean mIsAllowSliding = true;

    /**
     * 侧滑的模式
     */
    protected SlidingMode mSlidingMode = SlidingMode.FROM_FULL_SCREEN;

    /**
     * 侧边侧滑时的有效宽度
     */
    protected int mSlidingSideWidth = 20;

    /**
     * 默认是快速滑动的阈值
     */
    protected int mVelocityThreshold = 1500;

    // endregion ------------------------------

    public BaseSlidingMenu(Context context) {
        this(context, null);
    }

    public BaseSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContext = getContext();
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mScroller = new Scroller(mContext, new DecelerateInterpolator());
        mScreenWidth = getScreenWidth();
        initGestureDetector();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mMenuWidth = mScreenWidth - mSlidingMargin;

        // 获取ScrollView唯一的子布局
        mContainer = (ViewGroup) getChildAt(0);

        int childCount = mContainer.getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("SlidingMenu only two children are allowed!");
        }

        // 设置菜单视图的宽度
        mMenuView = mContainer.getChildAt(0);
        ViewGroup.LayoutParams menuParams = mMenuView.getLayoutParams();
        menuParams.width = mMenuWidth;
        mMenuView.setLayoutParams(menuParams);

        // 设置内容视图的宽度
        mContentView = mContainer.getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 默认显示内容视图
        scrollTo(mMenuWidth, 0);
    }

    // region ---------- ViewPager ----------

    public void bindViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        if (mViewPager != null) {
            initViewPager();
        }
    }

    public ViewPager getViewPager() {
        return this.mViewPager;
    }

    private void initViewPager() {
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSlidingMode == SlidingMode.FROM_FULL_SCREEN    // 全屏滑动
                        || (mSlidingMode == SlidingMode.FROM_SIDE && !mIsAllowSliding)) {   // 侧边滑动，同时不允许滑动的情况
                    mViewPager.requestDisallowInterceptTouchEvent(true);
                    if (mViewPager.getAdapter() != null
                            && mViewPager.getAdapter().getCount() > 0) {

                        if (mViewPager.getCurrentItem() == 0) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    mTouchStartX = (int) event.getX();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    int distance = (int) (mTouchStartX - event.getX());
                                    if (distance < 0 && Math.abs(distance) >= mTouchSlop) {
                                        requestDisallowInterceptTouchEvent(false);
                                    }
                                    break;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    // endregion ------------------------------

    // region ---------- 快速滑动 ----------

    /**
     * 处理快滑动
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > mVelocityThreshold) {
                    Log.e("TAG", "velocityX = " + velocityX);
                    // 横向滑动
                    if (mIsOpen && velocityX < 0) {
                        // 当前是打开状态
                        closeMenu();
                        return true;
                    } else if (!mIsOpen && velocityX > 0) {
                        // 当前是关闭状态
                        openMenu();
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    // endregion ------------------------------

    // region ---------- 事件处理 ----------

    /**
     * 触摸的起始位置
     */
    private int mTouchStartX, mTouchStartY;

    /**
     * 用于记录菜单外部的点击事件
     */
    private boolean isOutsideClick;

    /**
     * 侧滑时的超出了侧滑的有效宽度
     */
    private boolean isSideOutsideClick;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mScroller.isFinished()) {
            // 执行滚动的过程中不再接收外部事件
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = (int) ev.getX();
                mTouchStartY = (int) ev.getY();
                isSideOutsideClick = false;
                if (!isOpen() && mSlidingMode == SlidingMode.FROM_SIDE) {
                    // 侧边滑动有效
                    if (mTouchStartX > mSlidingSideWidth) {
                        isSideOutsideClick = true;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsOpen) {
                    if (mTouchStartX > mMenuWidth) {
                        isOutsideClick = true;
                        return true;
                    } else {
                        isOutsideClick = false;
                    }
                }
                break;
        }

        if (isSideOutsideClick) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!mIsAllowSliding) {
            return true;
        }

        if (isSideOutsideClick) {
            return true;
        }

        // 如果是快速滑动事件则消费掉
        if (mGestureDetector.onTouchEvent(ev)) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 如果滑动了则不关闭菜单
                if (Math.abs(ev.getX() - mTouchStartX) > mTouchSlop
                        || Math.abs(ev.getY() - mTouchStartY) > mTouchSlop) {
                    isOutsideClick = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isOutsideClick) {
                    // 点击了外部
                    closeMenu();
                    return true;
                }
                if (getScrollX() > mMenuWidth / 2) {
                    // 关闭菜单
                    closeMenu();
                } else {
                    // 打开菜单
                    openMenu();
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }

    // endregion ------------------------------

    // region ---------- 工具方法 ----------

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    protected int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    protected int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 单位转换: dp 2 px
     *
     * @param dpValue
     * @return
     */
    protected int dp2px(int dpValue) {
        return (int) (getDensity(mContext) * dpValue + 0.5);
    }

    private float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    // endregion ------------------------------

    // region ---------- get/set ----------

    public void setSlidingMargin(int slidingMargin) {
        this.mSlidingMargin = slidingMargin;
    }

    public int getSlidingMargin() {
        return this.mSlidingMargin;
    }

    /**
     * 设置是否允许侧滑
     *
     * @param allowSliding
     */
    public void setAllowSliding(boolean allowSliding) {
        this.mIsAllowSliding = allowSliding;
        if (!mIsAllowSliding) {
            closeMenu();
        }
    }

    public boolean isAllowSliding() {
        return mIsAllowSliding;
    }

    public void setSlidingDuration(int duration) {
        this.mSlidingDuration = duration;
    }

    public int getSlidingDuration() {
        return this.mSlidingDuration;
    }

    public void setSlidingMode(SlidingMode mode) {
        this.mSlidingMode = mode;
    }

    public SlidingMode getSlidingMode() {
        return this.mSlidingMode;
    }

    public void setSlidingSideWidth(int slidingSideWidth) {
        this.mSlidingSideWidth = slidingSideWidth;
    }

    public int getSlidingSideWidth() {
        return this.mSlidingSideWidth;
    }

    public boolean isOpen() {
        return this.mIsOpen;
    }

    public void setVelocityThreshold(int velocityThreshold) {
        this.mVelocityThreshold = velocityThreshold;
    }

    public int getVelocityThreshold() {
        return this.mVelocityThreshold;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getMenuView() {
        return mMenuView;
    }

    public void setScroller(Scroller scroller) {
        this.mScroller = scroller;
    }

    // endregion ------------------------------

    // region ---------- Scroller ----------

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    // endregion ------------------------------

    // region ---------- 关闭/打开 ----------

    public boolean openMenu() {
        if (getScrollX() == 0 && mIsOpen) {
            return false;
        }
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, mSlidingDuration);
        invalidate();
        mIsOpen = true;
        return true;
    }

    public boolean closeMenu() {
        if (getScrollX() == mMenuWidth && !mIsOpen) {
            return false;
        }
        mScroller.startScroll(getScrollX(), 0, mMenuWidth - getScrollX(), 0, mSlidingDuration);
        invalidate();
        mIsOpen = false;
        return true;
    }

    // endregion ------------------------------

}
