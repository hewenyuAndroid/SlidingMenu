package com.hwy.slidingmenu_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hwy.slidingmenu.SlidingMode;
import com.hwy.slidingmenu.widget.SlidingMenu;

public class SlidingMenuActivity extends AppCompatActivity {

    private SlidingMenu mSlidingMenu;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu);
        mContext = this;
        getSupportActionBar().hide();

        mSlidingMenu = findViewById(R.id.sliding_menu);

        mSlidingMenu.getContentView().findViewById(R.id.btn_full).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.setSlidingMode(SlidingMode.FROM_FULL_SCREEN);
                Toast.makeText(mContext, "全屏有效", Toast.LENGTH_SHORT).show();
            }
        });

        mSlidingMenu.getContentView().findViewById(R.id.btn_side).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.setSlidingMode(SlidingMode.FROM_SIDE);
                Toast.makeText(mContext, "侧边有效", Toast.LENGTH_SHORT).show();
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
