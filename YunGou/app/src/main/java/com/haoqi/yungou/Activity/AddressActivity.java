package com.haoqi.yungou.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收货地址管理
 */
public class AddressActivity extends FragmentActivity implements NavigationView.ClickCallback {
    public static String TAG ="EditAdressActivity";
    private ListView mListView;
    private ArrayList adressList = new ArrayList();
    private MySimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        NavigationView navBar = (NavigationView)findViewById(R.id.nav_bar);
        mListView = (ListView)findViewById(R.id.address_list);
        navBar.setClickCallback(this);
        getAdressList();
        adapter = new MySimpleAdapter(this,adressList,R.layout.item_address,new String[]{"receiver","tel","area","isDefault"},
                              new int[]{R.id.tv_recriptor_name_address,R.id.tv_recriptor_phone_address,R.id.tv_recriptor_addr_address,R.id.tv_default_address});
        mListView.setAdapter(adapter);
        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        final Map map  = (Map) adressList.get(position);

        CustomDialog.Builder dialog=new CustomDialog.Builder(this);
        dialog.setMessage(R.string.delete_sure);
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAdress(map.get("id").toString(),position);
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }


    private void deleteAdress(String id, final int position) {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("streetId", id);
        VolleyRequest.post(this, Uriconfig.delete_adress, params,"删除中...", new RequestListener() {

            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)) {
                       adressList.remove(position);
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

    private class MySimpleAdapter extends SimpleAdapter{
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ImageView btn=(ImageView) v.findViewById(R.id.ibtn_recriptor_edit_address);
            TextView tv=(TextView) v.findViewById(R.id.tv_default_address);
            Map map = (Map) getItem(position);
            if(TextUtils.isEmpty(map.get("isDefault").toString())){
                tv.setVisibility(View.GONE);
            }else{
                tv.setVisibility(View.VISIBLE);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddressActivity.this,EditAdressActivity.class);
                    HashMap<String,Object> map = (HashMap<String,Object>) adapter.getItem(position);
                    intent.putExtra("map",map);
                    startActivityForResult(intent,1);
                }
           });
            return v;
        }
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
                            }else{
                                map.put("isDefault","默认");
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
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {
        startActivityForResult(new Intent(this,EditAdressActivity.class),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            refresh();
        }
    }

    public void refresh(){
        adressList.clear();
        getAdressList();
    }
}
