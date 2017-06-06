package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kentlee on 2016/9/21.
 */
public class SetPwdFragment extends Fragment {
    private View view;
    private EditText et_password;
    private ToggleButton tb_show;
    private Button btn_submit;
    private String phone;
    private boolean isModiFy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_register_setpwd, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle argument = getArguments();
        if(argument!=null) {
            phone = argument.getString("phone");
            isModiFy = argument.getBoolean("modify", false);
        }
        et_password = (EditText)view.findViewById(R.id.et_password);
        tb_show = (ToggleButton)view.findViewById(R.id.tb_show_password);
        btn_submit = (Button)view.findViewById(R.id.btn_submit_complete);
        tb_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        }else{
                            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isModiFy){
                    modify();
                }else {
                    setPwd();
                }
            }
        });
        NavigationView nav = (NavigationView)view.findViewById(R.id.title_bar);
        nav.setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                getFragmentManager().popBackStack();
            }
            @Override
            public void onRightClick() {
            }
        });
    }

    /**
     * 找回密码 -修改密码
     */
    private void modify() {
        if(TextUtils.isEmpty(et_password.getText()) && et_password.getText().length()==11){
            Toast.makeText(getActivity(),R.string.pwd_notice,Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("tel", phone);
        params.put("password", et_password.getText().toString());
        VolleyRequest.post(getActivity(), Uriconfig.find_pwd_resetPwd, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject inf = jsonObject.getJSONObject("inf");
                    JSONObject res = jsonObject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        GlobalApplication.getInstance().toastShortMsg(R.string.modify_pwd_success);
                        getActivity().finish();
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

    /**
     * 用户注册设置密码
     */
    private void setPwd() {
        if(TextUtils.isEmpty(et_password.getText()) && et_password.getText().length()==11){
            Toast.makeText(getActivity(),R.string.pwd_notice,Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("tel", phone);
        params.put("password", et_password.getText().toString());
        VolleyRequest.post(getActivity(), Uriconfig.register_setpwd, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject inf = jsonObject.getJSONObject("inf");
                    JSONObject res = jsonObject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        GlobalApplication.getInstance().toastShortMsg(R.string.register_success);
                        getActivity().finish();
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            getFragmentManager().popBackStack();//关闭当前fragment
        }
        return true;
    }
}
