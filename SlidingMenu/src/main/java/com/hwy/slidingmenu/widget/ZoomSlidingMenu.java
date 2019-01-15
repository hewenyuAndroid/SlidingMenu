package com.hwy.slidingmenu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.hwy.slidingmenu.R;
import com.hwy.slidingmenu.SlidingMode;

/**
 * 作者: hewenyu
 * 日期: 2019/1/15 10:05
 * 说明: 可以缩放的侧滑菜单
 */
public class ZoomSlidingMenu extends BaseSlidingMenu {

    // region --------- 自定义属性 ------------

    /**
     * 内容视图的缩放
     */
    private float mContentScale = 0.7f;

    /**
     * 菜单视图关闭时的缩放
     */
    private float mMenuScale = 0.7f;

    /**
     * 菜单视图关闭时的透明度
     */
    private float mMenuAlpha = 0.3f;

    /**
     * 菜单视图平移的比例
     */
    private float mMenuTranslation = 0.2f;

    // endregion ------------------------------

    public ZoomSlidingMenu(Context context) {
        this(context, null);
    }

    public ZoomSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZoomSlidingMenu);
        mSlidingMargin = array.getDimensionPixelSize(R.styleable.ZoomSlidingMenu_slidingMargin, dp2px(mSlidingMargin));
        mSlidingDuration = array.getInt(R.styleable.ZoomSlidingMenu_slidingDuration, mSlidingDuration);
        mSlidingMode = SlidingMode.parseMode(array.getInteger(R.styleable.ZoomSlidingMenu_slidingMode, SlidingMode.FROM_FULL_SCREEN.getMode()));
        mSlidingSideWidth = array.getDimensionPixelSize(R.styleable.ZoomSlidingMenu_slidingSideWidth, dp2px(mSlidingSideWidth));
        mIsAllowSliding = array.getBoolean(R.styleable.ZoomSlidingMenu_isAllowScroll, mIsAllowSliding);
        mVelocityThreshold = array.getInteger(R.styleable.ZoomSlidingMenu_velocityThreshold, mVelocityThreshold);
        mContentScale = array.getFloat(R.styleable.ZoomSlidingMenu_contentScale, mContentScale);
        mMenuScale = array.getFloat(R.styleable.ZoomSlidingMenu_menuScale, mMenuScale);
        mMenuAlpha = array.getFloat(R.styleable.ZoomSlidingMenu_menuAlpha, mMenuAlpha);
        mMenuTranslation = array.getFloat(R.styleable.ZoomSlidingMenu_menuTranslation, mMenuTranslation);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 设置内容视图
        ViewGroup.LayoutParams contentParams = mContentView.getLayoutParams();
        contentParams.width = mScreenWidth;
        mContentView.setLayoutParams(contentParams);

    }

    // region ---------- 监听滚动 ----------

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        // 1 --> 0
        float scale = 1f * l / mMenuWidth;

        // 内容视图的缩放
        float contentScale = mContentScale + (1 - mContentScale) * scale;
        // 设置缩放的中心
        ViewCompat.setPivotX(mContentView, 0);
        ViewCompat.setScaleX(mContentView, contentScale);
        ViewCompat.setScaleY(mContentView, contentScale);

        // 菜单视图的缩放
        float menuScale = mMenuScale + (1 - mMenuScale) * (1 - scale);
        ViewCompat.setScaleX(mMenuView, menuScale);
        ViewCompat.setScaleY(mMenuView, menuScale);
        // 菜单视图的透明度
        float menuAlpha = mMenuAlpha + (1 - mMenuAlpha) * (1 - scale);
        ViewCompat.setAlpha(mMenuView, menuAlpha);
        // 菜单平移
        ViewCompat.setTranslationX(mMenuView, mMenuTranslation * l);

    }

    // endregion --------------------

    // region ---------- get/set ----------

    public void setContentScale(float contentScale) {
        this.mContentScale = contentScale;
    }

    public float getContentScale() {
        return this.mContentScale;
    }

    public void setMenuScale(float menuScale) {
        this.mMenuScale = menuScale;
    }

    public float getMenuScale() {
        return this.mMenuScale;
    }

    public void setMenuAlpha(float menuAlpha) {
        this.mMenuAlpha = menuAlpha;
    }

    public float getMenuAlpha() {
        return this.mMenuAlpha;
    }

    public void setMenuTranslation(float menuTranslation) {
        this.mMenuTranslation = menuTranslation;
    }

    public float getMenuTranslation() {
        return this.mMenuTranslation;
    }

    // endregion ------------------------------

}
