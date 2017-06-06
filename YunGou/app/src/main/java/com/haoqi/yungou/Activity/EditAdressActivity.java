package com.haoqi.yungou.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.AreaPopupWindow;
import com.haoqi.yungou.widget.SlideSwitch;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加或新增地址
 * Created by Kentlee on 2016/9/26.
 */
public class EditAdressActivity extends FragmentActivity implements SlideSwitch.SlideListener{
    private EditText et_receiptor,et_phone,et_detail_address,et_mail;
    private NavigationView titleView;
    private SlideSwitch slide;
    private String isDefault;
    private TextView tv_area;
    private String id;
    private Map addressMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_address);
        titleView = (NavigationView)findViewById(R.id.titlebar_edit_address);
        et_receiptor =(EditText)findViewById(R.id.et_receiptor);
        et_phone =(EditText)findViewById(R.id.et_phone);
        tv_area =(TextView)findViewById(R.id.et_area);
        et_detail_address =(EditText)findViewById(R.id.et_detail_address);
        et_mail =(EditText)findViewById(R.id.et_mail);
        slide =(SlideSwitch)findViewById(R.id.address_defualt);
        Button btn = (Button)findViewById(R.id.bth_submit_address);
        tv_area.setOnClickListener(areSelectListener);
        btn.setOnClickListener(submitClickListener);
        slide.setSlideListener(this);
        addressMap = (HashMap)getIntent().getSerializableExtra("map");
        get();
    }

    private AreaPopupWindow popupWindow;
    View.OnClickListener submitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(null==addressMap){
                add();
            }else {
                edit();
            }
        }
    };


    View.OnClickListener areSelectListener = new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(et_phone.getWindowToken(),0);
                  popupWindow = new AreaPopupWindow(EditAdressActivity.this);
                  popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                  onPopClick();
              }
          };

    private void onPopClick() {
        popupWindow.getSelect_Ok().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_area.setText(popupWindow.getAdress());
                popupWindow.dismiss();
            }
        });
        popupWindow.getSelect_Cancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        titleView.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void get() {
        if(addressMap!=null) {
            id = addressMap.get("id").toString();
            et_receiptor.setText(addressMap.get("receiver").toString());
            et_phone.setText(addressMap.get("tel").toString());
            tv_area.setText(addressMap.get("area_nodetail").toString());
            et_detail_address.setText(addressMap.get("detail_aress").toString());
            et_mail.setText(addressMap.get("mail").toString());
            String isDefault = addressMap.get("isDefault").toString();
            if(TextUtils.isEmpty(isDefault)){
                slide.setState(false);
            }else{
                slide.setState(true);
            }
        }
    }

    private void add() {
        RequestParams params = new RequestParams();
        params.put("tel",et_phone.getText().toString());
        params.put("userId", UserUtils.getUserId());
        params.put("isDefault",isDefault);
        params.put("consignee",et_receiptor.getText().toString());
        params.put("address",et_detail_address.getText().toString());
        params.put("province",tv_area.getText().toString());
        params.put("postCode",et_mail.getText().toString());
        VolleyRequest.post(this, Uriconfig.add_adress, params, null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.optJSONObject("inf");
                    JSONObject	res = jsonobject.optJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        ToastUtils.showShort(EditAdressActivity.this,res.toString());
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

    private void edit() {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("id", id);
        params.put("tel",et_phone.getText().toString());
        params.put("isDefault",isDefault);
        params.put("consignee",et_receiptor.getText().toString());
        params.put("address",et_detail_address.getText().toString());
        params.put("province",tv_area.getText().toString());
        params.put("postCode",et_mail.getText().toString());
        VolleyRequest.post(this, Uriconfig.edit_adress, params, null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.optJSONObject("inf");
                    JSONObject	res = jsonobject.optJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        ToastUtils.showShort(EditAdressActivity.this,res.toString());
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

    @Override
    public void open() {
        isDefault ="1";
    }

    @Override
    public void close() {
        isDefault ="0";
    }
}
