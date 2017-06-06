package com.haoqi.yungou.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.haoqi.yungou.R;
import com.haoqi.yungou.fragment.FindPwdFragment;
import com.haoqi.yungou.fragment.SetPwdFragment;
import com.haoqi.yungou.fragment.VerifyFragment;

public class ForgetPwdActivity extends FragmentActivity {

    private FindPwdFragment findPwdFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        findPwdFragment = new FindPwdFragment();
        transaction.addToBackStack(null);
        transaction.replace(R.id.register_content, findPwdFragment);
        transaction.commit();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment veriFragment =getFragmentManager().findFragmentByTag("0");
        Fragment setFragment =getFragmentManager().findFragmentByTag("1");
        if(veriFragment!=null ) {
            if (veriFragment instanceof VerifyFragment) {
                ((VerifyFragment) veriFragment).onKeyDown(keyCode, event);
                return false;
            }
        }
        if(setFragment!=null ) {
            if (setFragment instanceof SetPwdFragment) {
                ((SetPwdFragment) veriFragment).onKeyDown(keyCode, event);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
