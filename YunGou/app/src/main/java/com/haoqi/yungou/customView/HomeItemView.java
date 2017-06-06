package com.haoqi.yungou.customView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.GoodsDetailActivity;
import com.haoqi.yungou.Activity.MainActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.HomeGridAdapter;
import com.haoqi.yungou.countDown.CountDownTask;
import com.haoqi.yungou.util.MyCountDownTimer;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.VolleyRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LiGuang on 2016/9/13.
 */
public class HomeItemView extends LinearLayout  {
    private String TAG ="homeView";
    private ImageLoader imageLoader;
    private Context context;
    private MyCountDownTimer mc;
    private ArrayList<Map<String,Object>> dataList = new ArrayList<>();
    private CountDownTask mCountDownTask;
    private HomeGridAdapter adapter;
    private GridView grid;

    public HomeItemView(Context context) {
        this(context, null);
    }

    public HomeItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) { return; }
        initView(context);
    }

    /**
     * find 控件，初始化
     * @param context
     */
    private void initView(final Context context) {
        imageLoader = ImageLoader.getInstance();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.home_item_announce,this);
        this.context = context;
         grid = (GridView) view.findViewById(R.id.grid);
        adapter = new HomeGridAdapter(context,dataList);
        grid.setAdapter(adapter);
        Timer timer = new Timer();
        MyTimerTask timerTask = new MyTimerTask();
        timer.schedule(timerTask, 1000, 10000); // 延迟10秒钟,执行1次
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<dataList.size()) {
                    Map map = (Map) parent.getItemAtPosition(position);
                    String goodsId = map.get("cloudGoodsId").toString();
                    Intent intent = new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra("goodsId", goodsId);
                    ((MainActivity)context).startActivityForResult(intent, MainActivity.GoodsDetail);
                }
            }
        });
    }

    class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            //do
            Log.i(TAG,"定时任务正在执行");
            getAnnounceList(context);
        }
    }
    public void getAnnounceList(Context context){
        VolleyRequest.get(context, Uriconfig.getAnounceList,new RequestListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        dataList.clear();
                        cancelCountDown();
                        JSONArray list = inf.getJSONArray("CloudGoods");
                        if(list.length()>0) {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject obj = (JSONObject) list.get(i);
                                String time = obj.getString("publicTime");//揭晓时间 2016-09-19 12:35:27.0
                                String goodsId = obj.getString("goodsId");// 商品id
                                String img = Uriconfig.baseUrl + obj.getString("pic");
                                String goodsName = obj.getString("goodsName");
                                if (i < 4) {
                                    Map map = new HashMap();
                                    map.put("cloudGoodsId",obj.getString("cloudGoodsId"));//云购商品id
                                    map.put("name",goodsName);
                                    map.put("time",time);
                                    map.put("img",img);
                                    dataList.add(map);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            startCountDown();
                        }else{
                            grid.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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
    public void startCountDown() {
        mCountDownTask = CountDownTask.create();
        adapter.setCountDownTask(mCountDownTask);
    }
    public void cancelCountDown() {
        adapter.setCountDownTask(null);
        if(mCountDownTask!=null)
            mCountDownTask.cancel();
    }

}
