package com.hwy.slidingmenu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hwy.slidingmenu.R;
import com.hwy.slidingmenu.SlidingMode;

/**
 * 作者: hewenyu
 * 日期: 2018/11/15 21:13
 * 说明: 侧滑菜单
 */
public class SlidingMenu extends BaseSlidingMenu {

    /**
     * 主界面遮罩的颜色
     */
    private int mContentMaskColor = Color.parseColor("#4D000000");

    /**
     * 遮罩的控件
     */
    private View mMaskView;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
        mSlidingMargin = array.getDimensionPixelSize(R.styleable.SlidingMenu_slidingMargin, dp2px(mSlidingMargin));
        mSlidingDuration = array.getInt(R.styleable.SlidingMenu_slidingDuration, mSlidingDuration);
        mSlidingMode = SlidingMode.parseMode(array.getInteger(R.styleable.SlidingMenu_slidingMode, SlidingMode.FROM_FULL_SCREEN.getMode()));
        mSlidingSideWidth = array.getDimensionPixelSize(R.styleable.SlidingMenu_slidingSideWidth, dp2px(mSlidingSideWidth));
        mIsAllowSliding = array.getBoolean(R.styleable.SlidingMenu_isAllowScroll, mIsAllowSliding);
        mVelocityThreshold = array.getInteger(R.styleable.SlidingMenu_velocityThreshold, mVelocityThreshold);
        mContentMaskColor = array.getColor(R.styleable.SlidingMenu_contentMaskColor, mContentMaskColor);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ViewGroup.LayoutParams contentParams = mContentView.getLayoutParams();
        mContainer.removeView(mContentView);
        contentParams.width = mScreenWidth;

        RelativeLayout rlContent = new RelativeLayout(mContext);
        rlContent.setLayoutParams(contentParams);
        rlContent.addView(mContentView);

        mMaskView = new View(mContext);
        mMaskView.setBackgroundColor(mContentMaskColor);
        rlContent.addView(mMaskView);

        // 将包裹的视图放入到默认的容器中
        mContainer.addView(rlContent);
        // 设置遮罩层不可见
        mMaskView.setAlpha(0.0f);

    }

    // region ---------- 监听滚动 ----------

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        float scale = 1f * l / mMenuWidth;

        float maskAlpha = 1 - scale;
        mMaskView.setAlpha(maskAlpha);
    }

    // endregion --------------------------

    // region -------- get/set -------------

    public void setContentMaskColor(int maskColor) {
        this.mContentMaskColor = maskColor;
        mMaskView.setBackgroundColor(mContentMaskColor);
    }

    public int getContentMaskColor() {
        return this.mContentMaskColor;
    }

    // endregion ---------------------------

}
