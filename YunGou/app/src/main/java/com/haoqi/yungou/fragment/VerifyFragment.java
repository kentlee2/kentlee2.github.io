package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kentlee on 2016/9/21.
 */
public class VerifyFragment extends Fragment implements View.OnClickListener {

    private  final int STARTCOUNT = 0;
    private  final int CANCELCOUNT = 1;
    private View view;
    private int time=150;
    private Button btn_resend;
    private TextView tv_time;
    private Timer timer;
    private Button btn_next;
    private EditText edit_text;
    private String phone;
    private boolean isModiFy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_register_verify, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        phone = getArguments().getString("phone");
        isModiFy = getArguments().getBoolean("modify",false);
        initView();
    }

    private void initView() {
         btn_next = (Button) view.findViewById(R.id.btn_next);
        edit_text = (EditText) view.findViewById(R.id.edit_text);
        tv_time = (TextView)view.findViewById(R.id.on_time);
        btn_resend = (Button)view.findViewById(R.id.btn_resend);
        btn_next.setOnClickListener(this);
        btn_next.setEnabled(false);
        btn_resend.setOnClickListener(this);
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
        edit_text.addTextChangedListener(new MyTextWatcher());
        timer = new Timer();
        MyTimerTask task = new MyTimerTask();
        timer.schedule(task,0,1000);
    }
    class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
              if(!TextUtils.isEmpty(s)){
                  btn_next.setEnabled(true);
              }else
                  btn_next.setEnabled(false);
        }
    }
    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            time--;
            if(time>0) {
                handler.sendEmptyMessage(STARTCOUNT);
            }else{
                cancel();
                handler.sendEmptyMessage(CANCELCOUNT);
            }
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STARTCOUNT:
                    SpannableString spanString = new SpannableString("如未收到验证短信，请"+time+" 后重新发送");
                    ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                    spanString.setSpan(span, 10, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_time.setText(spanString);
                    break;
                case CANCELCOUNT:
                    tv_time.setText("如未收到验证短信，请点击重新发送");
                    btn_resend.setEnabled(true);
                    break;
            }
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            getFragmentManager().popBackStack();//关闭当前fragment
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                checkVeryfyCode();
                break;
            case R.id.btn_resend:
                getCode();
                break;
        }
    }
    public void getCode(){
        String submit =getString(R.string.submit_ing);
        RequestParams params = new RequestParams("tel", phone);
        VolleyRequest.post(getActivity(), Uriconfig.register_code, params,submit, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject inf = jsonObject.getJSONObject("inf");
                    JSONObject res = jsonObject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        String code = inf.getString("status");
                        if("0".equals(code)){
                            GlobalApplication.getInstance().saveValue(Constant.PHONE,UserUtils.getPhone());
                        }else{
                            GlobalApplication.getInstance().toastCenterMsg(R.string.phone_alreay_regist);
                        }
                    }else{
                        GlobalApplication.getInstance().toastCenterMsg(res.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                GlobalApplication.getInstance().toastShortMsg(e.toString());
            }
        });
    }

    /**
     * 检查验证码
     */
    private void checkVeryfyCode() {
        String code =edit_text.getText().toString();
        RequestParams params = new RequestParams();
        params.put("tel",phone);
        params.put("code",code);
        VolleyRequest.post(getActivity(), Uriconfig.checkCode, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject inf = jsonObject.getJSONObject("inf");
                    JSONObject  res = jsonObject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){//进入设置密码界面
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        SetPwdFragment set = new SetPwdFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("phone",phone);
                        bundle.putBoolean("modify",isModiFy);
                        set.setArguments(bundle);
                        if(isModiFy){
                            transaction.replace(R.id.safe_content, set,"1");
                        }else{
                            transaction.replace(R.id.register_content, set,"1");
                        }
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }else{
                        GlobalApplication.getInstance().toastShortMsg(res.toString());
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
