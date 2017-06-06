package com.haoqi.yungou.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.CustomListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressActivity extends CommonActivity implements AdapterView.OnItemClickListener,View.OnClickListener {
    private ArrayList<Map<String,String>> adressList = new ArrayList();
    private MySimpleAdapter adapter;
    private CustomListView mListView;
    private ImageLoader imageLoader;
    private String addressId;
    private Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        imageLoader = ImageLoader.getInstance();
        adapter = new MySimpleAdapter(AddAddressActivity.this,adressList,R.layout.item_address2,new String[]{"receiver","tel","area","isDefault"},
                new int[]{R.id.tv_recriptor_name_address,R.id.tv_recriptor_phone_address,R.id.tv_recriptor_addr_address,R.id.tv_default_address});
        mListView = (CustomListView)findViewById(R.id.address_select);
        RelativeLayout rl_add = (RelativeLayout) findViewById(R.id.rl_add_address);
        Button btn = (Button) findViewById(R.id.bth_submit_address);
        rl_add.setOnClickListener(this);
        btn.setOnClickListener(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        getGoodsInfo();
        getAdressList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_add_address:
                startActivityForResult(new Intent(AddAddressActivity.this,EditAdressActivity.class),1);
                break;
            case R.id.bth_submit_address:
                submitData();
                break;
        }
    }

    private void submitData() {
        RequestParams params = new RequestParams();
        if(UserUtils.isLogined()) {
            params.put("userId", UserUtils.getUserId());
        }
        params.put("itemId",map.get("id"));
        params.put("addressId",addressId);
        VolleyRequest.post(this, Uriconfig.goods_address, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)) {
                        ToastUtils.showShort(AddAddressActivity.this,R.string.submit_success);
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        ToastUtils.showShort(AddAddressActivity.this,res.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(AddAddressActivity.this,e.toString());
            }
        });
    }

    private void getGoodsInfo() {
        ImageView iv = (ImageView)findViewById(R.id.iv_goodspic);
        TextView goodsname = (TextView)findViewById(R.id.tv_goodsname);
        TextView cloudNo = (TextView)findViewById(R.id.tv_cloudNo);
        TextView price = (TextView)findViewById(R.id.tv_price);
        map = (Map<String, String>) getIntent().getSerializableExtra("map");
        imageLoader.displayImage(map.get("pic"),iv, CommonUtils.displayImageOptions);
        goodsname.setText(map.get("name"));
        cloudNo.setText(map.get("winCode"));
        price.setText(map.get("price"));
    }

    private void getAdressList() {
        RequestParams params = new RequestParams();
        if(UserUtils.isLogined()) {
            params.put("userId", UserUtils.getUserId());
        }
        VolleyRequest.post(this, Uriconfig.my_adress, params, null,new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)) {
                        JSONArray list = inf.getJSONArray("address");
                        for(int i=0;i<list.length();i++){
                            JSONObject object = list.getJSONObject(i);
                            Map map = new HashMap();
                            String province = object.getString("province");
                            String city = object.getString("city");
                            String area = object.getString("area");
                            String address = object.getString("address");
                            map.put("area",province+" "+city+" "+area+" "+address);
                            map.put("area_nodetail",province+" "+city+" "+area);
                            map.put("detail_aress",address);
                            map.put("receiver",object.getString("consignee"));
                            map.put("tel",object.getString("tel"));
                            map.put("mail",object.getString("postCode"));
                            map.put("id",object.getString("id"));
                            String isDefault = object.getString("isDefault");
                            if("0".equals(isDefault)){
                                map.put("isDefault","");//是否默认地址0否1是
                                map.put("cb","0");
                            }else{
                                map.put("isDefault","默认");
                                map.put("cb","1");
                                addressId = object.getString("id");
                            }
                            adressList.add(map);
                        }
                        adapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Map map = (Map) parent.getItemAtPosition(position);
        for(Map hasMap : adressList){//全部设为未选中
            hasMap.put("cb","0");
        }
        map.put("cb","1");
         addressId = (String) map.get("id");
        adressList.set(position,map);
        adapter.notifyDataSetChanged();
    }

    private class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ImageView btn=(ImageView) v.findViewById(R.id.ibtn_recriptor_edit_address);
            TextView tv=(TextView) v.findViewById(R.id.tv_default_address);
            CheckBox cb = (CheckBox)v.findViewById(R.id.address_cb);
            Map map = (Map) getItem(position);
            if(TextUtils.isEmpty(map.get("isDefault").toString())){
                tv.setVisibility(View.GONE);
            }else{
                tv.setVisibility(View.VISIBLE);
            }
            if(map.get("cb").equals("1")){
                cb.setChecked(true);
                cb.setVisibility(View.VISIBLE);
            }else{
                cb.setChecked(false);
                cb.setVisibility(View.INVISIBLE);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddAddressActivity.this,EditAdressActivity.class);
                    HashMap<String,Object> map = (HashMap<String,Object>) adapter.getItem(position);
                    intent.putExtra("map",map);
                    startActivityForResult(intent,1);
                }
            });
            return v;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            adressList.clear();
            getAdressList();
        }
    }
}
