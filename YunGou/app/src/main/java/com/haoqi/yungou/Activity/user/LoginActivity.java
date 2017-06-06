package com.haoqi.yungou.Activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.ForgetPwdActivity;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements View.OnClickListener{

    private NavigationView nav_login;
    private AutoCompleteTextView tel_email;
    private EditText pass_number;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nav_login =(NavigationView)findViewById(R.id.nav_login);
        Button btn = (Button) findViewById(R.id.shopping_login);
        Button bt_forgetpassword = (Button) findViewById(R.id.bt_forgetpassword);
        Button bt_register = (Button) findViewById(R.id.bt_register);
        ImageView iv_login_qq = (ImageView) findViewById(R.id.iv_login_qq);
        ImageView iv_login_wx = (ImageView) findViewById(R.id.iv_login_wx);
         tel_email = (AutoCompleteTextView) findViewById(R.id.tel_email);
        pass_number = (EditText) findViewById(R.id.pass_number);
        btn.setOnClickListener(this);
        bt_forgetpassword.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        iv_login_qq.setOnClickListener(this);
        iv_login_wx.setOnClickListener(this);
        nav_login.getBackView().setImageResource(R.drawable.previous_pre);
        nav_login.getBackView().setOnClickListener(this);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
        api.registerApp(Constant.APP_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nav_login.setTitle("登录");
        String userName =UserUtils.getUserName();
        if(!TextUtils.isEmpty(userName)){
            tel_email.setText(userName);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            finish();
            overridePendingTransition(0,R.anim.activity_down);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_nav_back:
                finish();
                overridePendingTransition(0,R.anim.activity_down);
                break;
            case R.id.shopping_login:
                login();
                break;
            case R.id.bt_forgetpassword:
                startActivity(new Intent(this,ForgetPwdActivity.class));
                break;
            case R.id.bt_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.iv_login_qq:
                break;
            case R.id.iv_login_wx:
                // send oauth request
               if( api.isWXAppInstalled()) {
                   SendAuth.Req req = new SendAuth.Req();
                   req.scope = "snsapi_userinfo";
                   req.state = "wechat_sdk_demo_test";
                   api.sendReq(req);
               }else{
                   GlobalApplication.getInstance().toastCenterMsg(R.string.wx_not_installed);
               }
                break;
        }

    }

    private void login() {
        final String accountName =tel_email.getText().toString();
        final String password =pass_number.getText().toString();
        if(TextUtils.isEmpty(accountName)){
            GlobalApplication.getInstance().toastCenterMsg(R.string.inputPass);
            return;
        }
        if(TextUtils.isEmpty(password)){
            GlobalApplication.getInstance().toastCenterMsg(R.string.inputnum);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("tel",accountName);
        params.put("password",password);
        VolleyRequest.post(this, Uriconfig.user_login, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                      String code = inf.getString("status");
                        if("0".equals(code)){
                            String userId =inf.getString("userId");
                            GlobalApplication.getInstance().saveValue(Constant.USERID,userId);
                            GlobalApplication.getInstance().saveValue(Constant.USERNAME,accountName);
                            GlobalApplication.getInstance().saveValue(Constant.USERPWD,password);
                            finish();
                            overridePendingTransition(0,R.anim.activity_down);
                        }else if("1".equals(code)){
                            GlobalApplication.getInstance().toastShortMsg(R.string.account_null);
                        }else{
                            GlobalApplication.getInstance().toastShortMsg(R.string.pass_error);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void requestError(VolleyError e) {

            }
        });
    }
}
