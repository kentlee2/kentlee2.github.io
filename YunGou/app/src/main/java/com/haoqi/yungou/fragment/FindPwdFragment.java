package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kentlee on 2016/11/23.
 */
public class FindPwdFragment extends Fragment {

    private View view;
    private Button btn_get;
    private EditText et_phone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_findpwd,null);
        initView();
        return view;
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
        btn_get = (Button)view.findViewById(R.id.btn_get_verify_code);
        et_phone = (EditText)view.findViewById(R.id.et_find_pwd_telephone);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_phone.getText())){
                    Toast toast =  Toast.makeText(getActivity(),R.string.inputnewnum,Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }
                getCode(et_phone.getText().toString());
            }
        });
    }

    private void getCode(String s) {
        RequestParams params = new RequestParams("tel",s);
        VolleyRequest.post(getActivity(), Uriconfig.find_pwd_getCode, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        VerifyFragment veriFragment = new VerifyFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("phone",et_phone.getText().toString());
                        bundle.putBoolean("modify",true);
                        veriFragment.setArguments(bundle);
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.register_content, veriFragment,"0");
                        transaction.commit();
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
