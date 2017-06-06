package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class RegisterFragment extends Fragment {
    private View view;
    private Button btn_next;
    private EditText et_phone;
    private RelativeLayout rl_check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        NavigationView nav = (NavigationView)view.findViewById(R.id.title_bar);
        nav.setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                getActivity().finish();
            }
            @Override
            public void onRightClick() {
            }
        });
        btn_next = (Button)view. findViewById(R.id.btn_next);
        et_phone = (EditText)view. findViewById(R.id.et_telephone);
        rl_check = (RelativeLayout)view.findViewById(R.id.rl_check);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);
        TextView tv_agreement = (TextView) view.findViewById(R.id.tv_agreement);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btn_next.setEnabled(true);
                }else{
                    btn_next.setEnabled(false);
                }
            }
        });
        btn_next.setOnClickListener(new CheckPhoneClickListener());
        tv_agreement.setOnClickListener(new View.OnClickListener() {//条款点击事件
            @Override
            public void onClick(View v) {
            }
        });
    }
    class CheckPhoneClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String phone = et_phone.getText().toString();
            if(TextUtils.isEmpty(phone)){
              Toast toast =  Toast.makeText(getActivity(),R.string.inputnewnum,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
            getCode();
        }
    }
    public void getCode(){
        String submit =getString(R.string.submit_ing);
        RequestParams params = new RequestParams("tel",et_phone.getText().toString());
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
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            VerifyFragment veriFragment = new VerifyFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("phone",et_phone.getText().toString());
                            veriFragment.setArguments(bundle);
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.register_content, veriFragment,"0");
                            transaction.commit();
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

}
