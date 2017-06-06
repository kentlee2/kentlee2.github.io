package com.haoqi.yungou.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinRecordActivity extends FragmentActivity implements XListView.IXListViewListener {

    private XListView mList;
    private String goodId;
    private ArrayList<Map<String,String>> recordList =new ArrayList<>();
    private SimpleAdapter adapter;
    private ImageLoader imageLoader;
    private int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_record);
        imageLoader =ImageLoader.getInstance();
        mList = (XListView)findViewById(R.id.join_list);
        mList.setXListViewListener(this);
        mList.setPullRefreshEnable(false);
        mList.setPullLoadEnable(true);
        goodId = getIntent().getStringExtra("goodsId");
         adapter = new SimpleAdapter(this,recordList,R.layout.item_join_record,
                       new String[]{"username","address","joinCount","headImg","time"},new int[]{R.id.user_name,R.id.address,R.id.join_count,R.id.user_header,R.id.buy_time});
        mList.setAdapter(adapter);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView){
                    imageLoader.displayImage(data.toString(),(ImageView) view, CommonUtils.circleImageOptions);
                    return true;
                }
                return false;
            }
        });

        getList(page);
    }

    private void getList(int num) {
        RequestParams params = new RequestParams();
        params.put("goodId", goodId);
        params.put("firstIndex", num+"");
        VolleyRequest.post(this, Uriconfig.join_record, params,null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)) {
                        JSONArray array = inf.optJSONArray("joinData");
                        String isPageEnd = inf.optString("isPageEnd");
                        if(isPageEnd.equals("1")){
                            page=1;
                            mList.setPullLoadEnable(false);
                        }
                        for(int i=0;i<array.length();i++) {
                            JSONObject jso = array.optJSONObject(i);
                            Map map = new HashMap();
                            String joinCount = "参与了" + jso.optString("joinCount") + "人次";
                            map.put("username", jso.optString("username"));
                            map.put("address", "(" + jso.optString("address") + ")");
                            map.put("joinCount", joinCount);
                            map.put("headImg", Uriconfig.baseUrl + jso.optString("headImg"));
                            map.put("time", jso.optString("createTime"));
                            recordList.add(map);
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
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = (NavigationView)findViewById(R.id.join_bar);
        navigationView.getBackView().setOnClickListener(new View.OnClickListener() {
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
        onLoad();
        page++;
        getList(page);
    }
    private void onLoad() {
        mList.stopRefresh();
        mList.setRefreshTime(CommonUtils.getTime());
    }
}
