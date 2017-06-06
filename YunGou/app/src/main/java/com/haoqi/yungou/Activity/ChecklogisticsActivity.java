package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.ExpressAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.CustomListView;
import com.haoqi.yungou.widget.TimeLineView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChecklogisticsActivity extends Activity {

    private TextView express_status;
    private TextView expresser;
    private TextView expressNo;
    private ArrayList<Map<String,String>> dataList = new ArrayList<>();
    private CustomListView mListView;
    private ExpressAdapter adapter;
    private TimeLineView timeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklogistics);
        getGoodsInfo();
        getState();
    }


    private void getState() {
        timeLine = (TimeLineView) findViewById(R.id.tv_timelines);
        mListView = (CustomListView)findViewById(R.id.list_order_logistics);
        adapter = new ExpressAdapter(this,R.layout.item_express_progress,dataList);
        mListView.setAdapter(adapter);
        express_status =(TextView)findViewById(R.id.express_status);
        expresser =(TextView)findViewById(R.id.expresser);
        expressNo =(TextView)findViewById(R.id.express_no);
        final String str1 ="物流状态:待打单出库";
        final String str2 ="物流状态:出库打包中";
        final String str3 ="物流状态:运输中";
        final String str4 ="物流状态:已确认收货";
        final String str5 ="物流状态:交易失败";
        final String id = getIntent().getStringExtra("id");
        RequestParams params = new RequestParams();
        params.put("goodId",id);
        params.put("userId", UserUtils.getUserId());
        VolleyRequest.post(this, Uriconfig.check_express, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");

                    inf.getJSONArray("logs");
                   String num=  inf.optString("status");//物流状态  1：待打单出库2：出库打包中 3：运输中 4：确认收货5：交易失败
                    if(num.equals("1")){
                        CommonUtils.setSpanableText(express_status, str1,getResources().getColor(R.color.cyan_text),5,str1.length());
                    }else if(num.equals("2")){
                        CommonUtils.setSpanableText(express_status, str2,getResources().getColor(R.color.cyan_text),5,str2.length());
                    }else if(num.equals("3")){
                        CommonUtils.setSpanableText(express_status, str3,getResources().getColor(R.color.cyan_text),5,str3.length());
                    }else if(num.equals("4")){
                        CommonUtils.setSpanableText(express_status, str4,getResources().getColor(R.color.cyan_text),5,str4.length());
                    }else if(num.equals("5")){
                        CommonUtils.setSpanableText(express_status, str5,getResources().getColor(R.color.cyan_text),5,str5.length());
                    }
                    expresser.setText( "承运来源:"+inf.getString("expresser"));
                    expressNo.setText("运单编号:"+inf.getString("expressno"));
                    JSONArray arrays = inf.optJSONArray("logs");
                    for(int i=0;i<arrays.length();i++){
                        JSONObject object = arrays.optJSONObject(i);
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("title",object.optString("note"));
                        map.put("time",object.optString("createTime"));
                        dataList.add(map);
                    }
                     adapter.notifyDataSetChanged();
                    timeLine.setTimelineCount(arrays.length());
                    timeLine.setTimelineRadius(10);//设置下面那些轴的圆点直径
                    timeLine.setTimelineWidth(5);//设置时间轴的宽度
                    timeLine.setTimeLineViewHeight(getTotalHeightofListView(mListView));//设置时间轴的高度
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {

            }
        });
    }
    public int getTotalHeightofListView(ListView listView) {
        ListAdapter mAdapter = (ListAdapter) listView.getAdapter();
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mView.measure(0, 0);
            totalHeight += mView.getMeasuredHeight();
              Log.w("HEIGHT" + i, String.valueOf(totalHeight));
            timeLine.setTimelineRadiusDistance(mView.getMeasuredHeight());
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        int listviewHeight = params.height;

        listView.setLayoutParams(params);
        listView.requestLayout();
        return listviewHeight;
    }
    private void getGoodsInfo() {
        ImageView iv = (ImageView)findViewById(R.id.iv_goodspic);
        TextView goodsname = (TextView)findViewById(R.id.tv_goodsname);
        TextView cloudNo = (TextView)findViewById(R.id.tv_cloudNo);
        TextView price = (TextView)findViewById(R.id.tv_price);
        Map<String,String> map = (Map<String, String>) getIntent().getSerializableExtra("map");
        ImageLoader.getInstance().displayImage(map.get("pic"),iv, CommonUtils.displayImageOptions);
        goodsname.setText(map.get("name"));
        cloudNo.setText(map.get("winCode"));
        price.setText(map.get("price"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((NavigationView)findViewById(R.id.titlebar)).setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
    }
}
