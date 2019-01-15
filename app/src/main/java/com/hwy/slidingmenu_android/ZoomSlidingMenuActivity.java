package com.hwy.slidingmenu_android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hwy.slidingmenu.SlidingMode;
import com.hwy.slidingmenu.widget.ZoomSlidingMenu;

public class ZoomSlidingMenuActivity extends AppCompatActivity {

    private ZoomSlidingMenu mSlidingMenu;

    private ViewPager mViewPager;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_sliding_menu);
        getSupportActionBar().hide();
        mContext = this;
        mSlidingMenu = findViewById(R.id.sliding_menu);

        mSlidingMenu.getContentView().findViewById(R.id.btn_full).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.setSlidingMode(SlidingMode.FROM_FULL_SCREEN);
            }
        });

        mSlidingMenu.getContentView().findViewById(R.id.btn_side).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.setSlidingMode(SlidingMode.FROM_SIDE);
            }
        });

        mViewPager = mSlidingMenu.getContentView().findViewById(R.id.view_pager);

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                TextView textView = new TextView(mContext);
                textView.setText("position = " + position);
                textView.setBackgroundColor(Color.parseColor("#EBEBEB"));
                textView.setGravity(Gravity.CENTER);
                container.addView(textView);
                //最后要返回的是控件本身
                return textView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//                super.destroyItem(container, position, object);
            }
        });

        mSlidingMenu.bindViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 2) {
                    mSlidingMenu.setAllowSliding(false);
                } else {
                    mSlidingMenu.setAllowSliding(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mSlidingMenu.closeMenu()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
