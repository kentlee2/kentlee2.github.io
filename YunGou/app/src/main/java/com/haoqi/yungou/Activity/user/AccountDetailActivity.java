package com.haoqi.yungou.Activity.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountDetailActivity extends Activity implements View.OnClickListener {
    private ArrayList<String> rechargeList = new ArrayList<>();
    private ArrayList<String> comsumeList = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Button btn_recharge,btn_consume;
    private ImageView iv_cursor,iv_cursor2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        listView = (ListView)findViewById(R.id.listView);
        btn_recharge = (Button)findViewById(R.id.btn_recharge);
        btn_consume = (Button)findViewById(R.id.btn_consume);
        iv_cursor = (ImageView)findViewById(R.id.cursor);
        iv_cursor2 = (ImageView)findViewById(R.id.cursor2);
        NavigationView title = (NavigationView) findViewById(R.id.title);
        title.getBackView().setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
        btn_consume.setOnClickListener(this);
        getDetails(0);
    }

    private void getDetails(int type) {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("accountType",type+"");
        VolleyRequest.post(this, Uriconfig.Accnount_detail, params,new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        if(adapter!=null) {
                            rechargeList.clear();
                            comsumeList.clear();
                            adapter.notifyDataSetChanged();
                            adapter =null;
                        }
                        JSONArray accountArray = inf.optJSONArray("accountRecord");
                        for(int i=0;i<accountArray.length();i++){
                            JSONObject jsonObject = accountArray.optJSONObject(i);
                           String payType = jsonObject.optString("payType");
                            String payTime =  jsonObject.optString("payTime");
                            String type =  jsonObject.optString("type");////0充值，1消费
                            String changeAmount =  jsonObject.optString("changeAmount");
                            if(type.equals("0")){
                                rechargeList.add(payTime+"       "+payType+"                  ￥"+changeAmount);
                                adapter = new ArrayAdapter<String>(AccountDetailActivity.this,R.layout.item_account_detail,rechargeList);
                                listView.setAdapter(adapter);
                            }else{
                                comsumeList.add(payTime+"       "+payType+"                  ￥"+changeAmount);
                                adapter = new ArrayAdapter<String>(AccountDetailActivity.this,R.layout.item_account_detail,comsumeList);
                                listView.setAdapter(adapter);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(AccountDetailActivity.this,e.toString());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_recharge:
                btn_recharge.setBackgroundResource(R.drawable.all_stroke_pressed);
                btn_consume.setBackgroundResource(R.drawable.all_stroke_normal);
                iv_cursor.setVisibility(View.VISIBLE);
                iv_cursor2.setVisibility(View.INVISIBLE);
                getDetails(0);
                break;
            case R.id.btn_consume:
                btn_consume.setBackgroundResource(R.drawable.all_stroke_pressed);
                btn_recharge.setBackgroundResource(R.drawable.all_stroke_normal);
                iv_cursor.setVisibility(View.INVISIBLE);
                iv_cursor2.setVisibility(View.VISIBLE);
                getDetails(1);
                break;
            case R.id.iv_nav_back:
                finish();
                break;
        }
    }
}
