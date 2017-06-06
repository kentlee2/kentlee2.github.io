package com.haoqi.yungou.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.haoqi.yungou.R;
import com.haoqi.yungou.customView.NavigationView;

public class CommonActivity extends FragmentActivity {

    protected NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Override
        protected void onStart() {
            super.onStart();
            nav = (NavigationView)findViewById(R.id.nav_bar);
            nav.setClickCallback(new NavigationView.ClickCallback() {
                @Override
                public void onBackClick() {
                    finish();
                }
                @Override
                public void onRightClick() {
                }
            });
    }
}
