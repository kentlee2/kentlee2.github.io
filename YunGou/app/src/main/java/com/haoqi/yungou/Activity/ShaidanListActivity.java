package com.haoqi.yungou.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.ShareListAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShaidanListActivity extends FragmentActivity implements XListView.IXListViewListener {

    private int index=1;
    private ImageLoader imageLoader;
    private ShareListAdapter adapter;
    private ArrayList<Map> dataList = new ArrayList<>();
    private String goodsId;
    private XListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsId = getIntent().getStringExtra("goodsId");
        setContentView(R.layout.my_shaidan_list);
        setViews();
        setData();
    }

    private void setViews() {
        imageLoader = ImageLoader.getInstance();
        mListView = (XListView)findViewById(R.id.shaidan_list);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setXListViewListener(this);
        adapter = new ShareListAdapter(this,R.layout.item_shaidan_new,dataList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = (Map) parent.getItemAtPosition(position);
                Intent intent = new Intent(ShaidanListActivity.this,ShaidanDetailActivity.class);
                intent.putExtra("map", (Serializable) map);
                startActivity(intent);
            }
        });
    }

    private void setData() {
        RequestParams params = new RequestParams();
        params.put("goodId",goodsId);
        params.put("firstIndex",index+"");
        VolleyRequest.post(this, Uriconfig.all_shaidan, params, null, new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if (status.equals("0")) {
                        String isPageEnd = inf.optString("isPageEnd");
                        if(isPageEnd.equals("1")){
                            mListView.setPullLoadEnable(false);
                            mListView.getFooterView().setVisibility(View.GONE);
                        }
                        JSONArray shareArr = inf.optJSONArray("shareData");
                        for(int i=0;i<shareArr.length();i++){
                            JSONObject shareData = shareArr.optJSONObject(i);
                            Map map = new HashMap();
                            map.put("imgs",shareData.optString("imgs"));
                            map.put("likely",shareData.optString("likely"));
                            map.put("headImg",Uriconfig.baseUrl+shareData.optString("headImg"));
                            map.put("createTime",shareData.optString("createTime"));
                            map.put("shareId",shareData.optString("shareId"));
                            map.put("detail",shareData.optString("detail"));
                            map.put("title",shareData.optString("title"));
                            map.put("userId",shareData.optString("userId"));
                            map.put("username",shareData.optString("username"));
                            map.put("shareId",shareData.optString("shareId"));
                            map.put("comments",shareData.optString("comments"));
                            shareData.optString("status");
                            dataList.add(map);
                        }

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
    protected void onStart() {
        super.onStart();
        NavigationView nav = (NavigationView)findViewById(R.id.nav_bar);
        nav.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nav.setTitle(R.string.shaidan_share);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        setData();
        onLoad();
    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.setRefreshTime(CommonUtils.getTime());
    }
}
