package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.haoqi.yungou.Activity.user.AboutActivity;
import com.haoqi.yungou.Activity.user.HelpCenterActivity;
import com.haoqi.yungou.Activity.user.LoginActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.widget.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingActivity extends Activity implements View.OnClickListener{

    private RelativeLayout rl_avatar,rl_addr_manage,rl_safecenter, rl_help_center,rl_feedback_information,rl_customer_service_hotline,
                           rl_msg_setting,rl_clear_cache,rl_version_update,rl_about,rl_service_agreement;
    private LinearLayout ll_no_drawing_mode;
    private Button btn_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        rl_avatar = (RelativeLayout)findViewById(R.id.rl_avatar);
        rl_addr_manage = (RelativeLayout)findViewById(R.id.rl_addr_manage);
        rl_safecenter = (RelativeLayout)findViewById(R.id.rl_safecenter);
        rl_help_center = (RelativeLayout)findViewById(R.id.rl_help_center);
        rl_feedback_information = (RelativeLayout)findViewById(R.id.rl_feedback_information);
        rl_customer_service_hotline = (RelativeLayout)findViewById(R.id.rl_customer_service_hotline);
        ll_no_drawing_mode = (LinearLayout)findViewById(R.id.ll_no_drawing_mode);
        rl_msg_setting = (RelativeLayout)findViewById(R.id.rl_msg_setting);
        rl_clear_cache = (RelativeLayout)findViewById(R.id.rl_clear_cache);
        rl_version_update = (RelativeLayout)findViewById(R.id.rl_version_update);
        rl_about = (RelativeLayout)findViewById(R.id.rl_about);
        rl_service_agreement = (RelativeLayout)findViewById(R.id.rl_service_agreement);
        btn_logout = (Button)findViewById(R.id.btn_logout);
        rl_avatar.setOnClickListener(this);
        rl_addr_manage.setOnClickListener(this);
        rl_safecenter.setOnClickListener(this);
        rl_help_center.setOnClickListener(this);
        rl_feedback_information.setOnClickListener(this);
        rl_customer_service_hotline.setOnClickListener(this);
        ll_no_drawing_mode.setOnClickListener(this);
        rl_msg_setting.setOnClickListener(this);
        rl_clear_cache.setOnClickListener(this);
        rl_version_update.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_service_agreement.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView title = (NavigationView)findViewById(R.id.title_bar);
        title.getRightView().setVisibility(View.GONE);
        title.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.rl_avatar:
                  startActivity(new Intent(this,EditInfoAcitivity.class));
                  break;
              case R.id.rl_addr_manage:
                  startActivity(new Intent(this,AddressActivity.class));
                  break;
              case R.id.rl_safecenter:
                  startActivity(new Intent(this,SafeSetActivity.class));
                  break;
              case R.id.rl_help_center:
                  startActivity(new Intent(this,HelpCenterActivity.class));
                  break;
              case R.id.rl_feedback_information:
                  startActivity(new Intent(this,FeedBackActivity.class));
                  break;
              case R.id.rl_customer_service_hotline:
                  break;
              case R.id.ll_no_drawing_mode:
                  break;
              case R.id.rl_msg_setting:
                  break;
              case R.id.rl_clear_cache://清除图片缓存
                  ImageLoader.getInstance().clearDiskCache();
                  ImageLoader.getInstance().clearMemoryCache();
                  Toast.makeText(this,R.string.clean_cache_success,Toast.LENGTH_SHORT).show();
                  break;
              case R.id.rl_version_update:
                  break;
              case R.id.rl_about:
                  startActivity(new Intent(this,AboutActivity.class).putExtra("aboutus",true));
                  break;
              case R.id.rl_service_agreement:
                  startActivity(new Intent(this,AboutActivity.class));
                  break;
              case R.id.btn_logout:
                  CustomDialog.Builder dialog=new CustomDialog.Builder(this);
                  dialog.setTitle(R.string.logout_sure)
                          .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {
                                  UserUtils.logout();
                                  startActivity(new Intent(SettingActivity.this,LoginActivity.class));
                                  dialogInterface.dismiss();
                                  finish();
                              }
                          })
                          .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {
                                  dialogInterface.dismiss();
                              }
                          }).create().show();

                  break;
          }
    }

}
