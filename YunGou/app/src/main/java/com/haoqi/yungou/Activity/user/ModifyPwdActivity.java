package com.haoqi.yungou.Activity.user;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ModifyPwdActivity extends Activity {

    private EditText edit_one;
    private EditText edit_two;
    private EditText edit_old;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        edit_old =(EditText)findViewById(R.id.edit_old);
        edit_one =(EditText)findViewById(R.id.edit_one);
        edit_two =(EditText)findViewById(R.id.edit_two);

    }
    public void modify(View view){
        if(TextUtils.isEmpty(edit_old.getText())){
            Toast.makeText(this,R.string.pwd_not_null,Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edit_one.getText())){
            Toast.makeText(this,R.string.pwd_not_null,Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(edit_two.getText())){
            Toast.makeText(this,R.string.pwd_not_null,Toast.LENGTH_SHORT).show();
            return;
        }
        if(!edit_one.getText().toString().equals(edit_two.getText().toString())){
            Toast.makeText(this,R.string.pwd_not_match,Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("oldPassword", edit_old.getText().toString());
        params.put("password", edit_two.getText().toString());
        VolleyRequest.post(this, Uriconfig.resetPwd, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject inf = jsonObject.getJSONObject("inf");
                    JSONObject res = jsonObject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        GlobalApplication.getInstance().toastShortMsg(R.string.modify_pwd_success);
                       finish();
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
