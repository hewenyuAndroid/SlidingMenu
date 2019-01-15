package com.hwy.slidingmenu_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startZoomSlidingMenu(View view) {
        startActivity(new Intent(this, ZoomSlidingMenuActivity.class));
    }

    public void startSlidingMenu(View view) {
        startActivity(new Intent(this, SlidingMenuActivity.class));
    }
}
