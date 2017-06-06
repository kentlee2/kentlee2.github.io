package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.haoqi.yungou.Activity.user.ModifyPwdActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.customView.NavigationView;

public class SafeSetActivity extends Activity implements View.OnClickListener {

    private RelativeLayout rl_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_set);
        rl_password = (RelativeLayout)findViewById(R.id.rl_password);
        rl_password.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((NavigationView)findViewById(R.id.title_bar)).getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_password:
               startActivity(new Intent(this,ModifyPwdActivity.class));
                break;
        }
    }

}
