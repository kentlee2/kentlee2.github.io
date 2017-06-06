package com.haoqi.yungou.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.domain.JsonResult;
import com.haoqi.yungou.domain.ShaidanData;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyShaidanListActivity extends FragmentActivity implements XListView.IXListViewListener {

    private XListView mListView;
    private ArrayList<Map<String,Object>> dataList;
    private SimpleAdapter adapter;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_shaidan_list);
        imageLoader = ImageLoader.getInstance();
        mListView = (XListView)findViewById(R.id.shaidan_list);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setXListViewListener(this);
        getData();
        adapter = new SimpleAdapter(this,dataList,R.layout.item_shaidan,new String[]{"iv_shaidanpic","shaidan_title","shandan_detail",
                                  "shandan_time","shandan_status"},
                                new int[]{R.id.iv_shaidanpic,R.id.shaidan_title,R.id.shandan_detail,R.id.shandan_time,R.id.shandan_status});
        mListView.setAdapter(adapter);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView){
                    imageLoader.displayImage(data.toString(),(ImageView)view, CommonUtils.displayImageOptions);
                    return true;
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Map map = (Map) parent.getItemAtPosition(position);
                startActivity(new Intent(MyShaidanListActivity.this,ShaidanDetailActivity.class).putExtra("map",(Serializable) map));
            }
        });
    }

    private void getData() {
        dataList = new ArrayList<>();
        RequestParams params = new RequestParams("userId", UserUtils.getUserId());
        VolleyRequest.post(this, Uriconfig.my_shaidan, params, null, new RequestListener() {

            @Override
            public void requestSuccess(String json) {
                Gson gson = new Gson();
                Type type = new TypeToken<JsonResult<ShaidanData>>(){}.getType();
                JsonResult<ShaidanData> data = gson.fromJson(json, type);
                ShaidanData inf = data.getInf();
                Map<String, String> res = data.getRes();
                if(res.get("status").equals("0")){
                    String isPageEnd = inf.getIsPageEnd();
                    if(isPageEnd.equals("1")){
                        mListView.setPullLoadEnable(false);
                    }
                    List<ShaidanData.Shaidan> share = inf.getShareOrder();
                    for(ShaidanData.Shaidan shai : share){
                        String pic = Uriconfig.baseUrl + shai.getImg();
                        String createTime = shai.getCreateTime();
                        String id = shai.getId();
                        String detail = shai.getDetail();
                        String title = shai.getTitle();
                        String statusCode = shai.getStatus();//晒单审核状态，0未审核1审核通过2审核失败
                        Map map = new HashMap();
                        map.put("iv_shaidanpic", pic);
                        map.put("shandan_time", createTime);
                        map.put("shareId", id);
                        map.put("shandan_detail", detail);
                        map.put("shaidan_title", title);
                        if (statusCode.equals("0")) {
                            map.put("shandan_status", "未审核");
                        } else if (statusCode.equals("1")) {
                            map.put("shandan_status", "审核通过");
                        } else {
                            map.put("shandan_status", "审核失败");
                        }
                        dataList.add(map);
                    }
                    adapter.notifyDataSetChanged();
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
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        getData();
        onLoad();
    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.setRefreshTime(CommonUtils.getTime());
    }
}
