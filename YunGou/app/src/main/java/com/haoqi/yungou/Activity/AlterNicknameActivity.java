package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 修改昵称
 */
public class AlterNicknameActivity extends Activity implements NavigationView.ClickCallback {

    private EditText et_nick;
    private ImageView iv_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_nickname);
        et_nick = (EditText)findViewById(R.id.et_nickname);
        et_nick.setText(UserUtils.getNickName());
        NavigationView navBar = (NavigationView) findViewById(R.id.title_bar);
        iv_clear = (ImageView)findViewById(R.id.iv_clear);
        navBar.setClickCallback(this);
        et_nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>1){
                    iv_clear.setVisibility(View.VISIBLE);
                }else{
                    iv_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_nick.setText("");
            }
        });
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {
        if(TextUtils.isEmpty(et_nick.getText())){
            GlobalApplication.getInstance().toastCenterMsg(R.string.nick_name_null);
            return;
        }
        setProfile(2,et_nick.getText().toString());
    }
    private void setProfile(int type, final String content) {
        RequestParams params = new RequestParams();
        params.put("editUserType",type+"");
        params.put("userId", UserUtils.getUserId());
        params.put("content", content);
        VolleyRequest.post(this, Uriconfig.edit_info, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        GlobalApplication.getInstance().toastShortMsg(R.string.set_success);
                        DBManager.getInstance().updateUserInfo(Constant.COLUMN_NAME_NICK,content);
                        finish();
                    }else{
                        GlobalApplication.getInstance().toastShortMsg(R.string.set_fail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                Log.e("tag",e.toString());
            }
        });
    }
}
