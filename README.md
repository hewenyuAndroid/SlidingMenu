# SlidingMenu
### 介绍
Android侧滑框架，本项目基于HorizontalScrollView开发，主要实现了SlidingMenu,ZoomSlidingMenu两个类，基本实现了对手势速度的监测，Menu打开时内容页面的触摸事件的拦截，具体效果见效果图:

![Zoom全屏有效](https://github.com/hewenyuAndroid/SlidingMenu/blob/master/screen/zoom_01.gif?raw=true)
![Zoom侧边有效](https://github.com/hewenyuAndroid/SlidingMenu/blob/master/screen/zoom_02.gif?raw=true)
![Zoom绑定ViewPager](https://github.com/hewenyuAndroid/SlidingMenu/blob/master/screen/zoom_03.gif?raw=true)
![SlidingMenu](https://github.com/hewenyuAndroid/SlidingMenu/blob/master/screen/sliding_01.gif?raw=true)

### 包含控件
1. SlidingMenu
2. ZoomSlidingMenu

本项目中将侧滑事件抽取出来封装到一个类中 `BaseSlidingMenu` ,如果想扩展更多的自定义需求，可以子接继承自 `BaseSlidingMenu` 具体的写法参考上面两个类；

### 使用方式
> compile 'com.hewenyu:SlidingMenu:1.0'

### 使用步骤
1. 按照如下代码放置布局文件
```XML
<com.hwy.slidingmenu.widget.SlidingMenu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SlidingMenuActivity">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal">
        <!-- 菜单视图 -->
        <include layout="@layout/layout_sliding_menu" />
        <!-- 内容视图 -->
        <include layout="@layout/layout_sliding_content" />
    </LinearLayout>

</com.hwy.slidingmenu.widget.SlidingMenu>
```

2. 如果ContentView里面有ViewPager，ViewPager需要对事件进行反拦截处理，这里我们将此代码进行封装,只需调用 `mSlidingMenu.bindViewPager(mViewPager);` 方法绑定ViewPager即可，具体的事件处理代码如下：
```Java
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
```

3. 可以重写Activity页面的返回键来关闭菜单
```Java
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mSlidingMenu.closeMenu()) {
        return true;
    }
    return super.onKeyDown(keyCode, event);
}
```

### 自定义属性
> 通用属性
```XML
<!-- 侧滑打开时与右侧的间距 -->
<attr name="slidingMargin" format="dimension" />
<!-- 侧滑动画的延时 -->
<attr name="slidingDuration" format="integer" />
<!-- 侧滑的模式 -->
<attr name="slidingMode" format="enum">
    <!-- 全屏 -->
    <enum name="from_full_screen" value="0" />
    <!-- 侧边 -->
    <enum name="from_side" value="1" />
</attr>
<!-- 侧边侧滑时有效宽度 -->
<attr name="slidingSideWidth" format="dimension" />
<!-- 是否允许侧滑 -->
<attr name="isAllowScroll" format="boolean" />
<!-- 认为是快速滑动的阈值 -->
<attr name="velocityThreshold" format="integer" />
<!-- 菜单页面平移动画的百分比 -->
<attr name="menuTranslation" format="float" />
```
> SlidingMenu
```XML
 <!-- 菜单打开时主界面内容的遮罩颜色 -->
<attr name="contentMaskColor" format="color" />
```
> ZoomSlidingMenu
```XML
<!-- 菜单页面打开时内容页面缩放的比例 (0.0f - 1.0f) -->
<attr name="contentScale" format="float" />
<!-- 菜单页面关闭时的缩放 (0.0f - 1.0f) -->
<attr name="menuScale" format="float" />
<!-- 菜单页面关闭时的透明度 (0.0f - 1.0f) -->
<attr name="menuAlpha" format="float" />
```

