package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.GoodsDetailActivity;
import com.haoqi.yungou.Activity.MainActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.GoodsTypeAdapter;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品列表
 * Created by Administrator on 2016/9/8.
 */
public class   GoodsTypListFragment  extends Fragment implements View.OnClickListener, XListView.IXListViewListener, AdapterView.OnItemClickListener {

    public static final String TAG = "MyFragment";
    public String str;
    private View view;
    private XListView listView;
    private ArrayList<Map<String,Object>> dataList = new ArrayList<>() ;
    private GoodsTypeAdapter adapter;
    private TextView tv_anounce,tv_pop,tv_newest,tv_price;
    private ImageView classify_price;
    private LinearLayout ll_price;
    private onWorkFinish work;
    private int index;
    private int page=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.typelist_fragment, null);
        //得到数据
//        str = getArguments().getString(TAG);
        initView();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(work!=null) {
            work.onfinish();
        }
    }

    private void initView() {
        tv_anounce = (TextView) view.findViewById(R.id.tv_anounce);
        tv_pop = (TextView) view.findViewById(R.id.tv_pop);
        tv_newest = (TextView) view.findViewById(R.id.tv_newest);
        ll_price = (LinearLayout) view.findViewById(R.id.ll_price);
        tv_price = (TextView)view.findViewById(R.id.tv_price);
        classify_price = (ImageView) view.findViewById(R.id.classify_price);
        classify_price.setOnClickListener(this);
        ll_price.setOnClickListener(this);
        tv_anounce.setOnClickListener(this);
        tv_newest.setOnClickListener(this);
        tv_pop.setOnClickListener(this);
        listView = (XListView)view.findViewById(R.id.goods_listView);
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(false);
        listView.setXListViewListener(this);
        listView.setAutoLoadEnable(true);
        listView.setOnItemClickListener(this);
        adapter = new GoodsTypeAdapter(getActivity(),dataList);
        listView.setAdapter(adapter);
    }

    public void getAnnounceGoods(int index, Context context) {//默认开始显示即将揭晓
        String msg ="加载中...";
        RequestParams params = new RequestParams();
        params.put("showType", index + "");
        params.put("goodsTypeId", str);
        params.put("firstIndex", page+"");
        VolleyRequest.post(context, Uriconfig.getGoodsList, params,msg, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)) {
                        if(inf.optString("isPageEnd").equals("1")){
                            listView.setPullLoadEnable(false);
                        }else{
                            listView.setPullLoadEnable(true);
                        }
                        JSONArray list = inf.getJSONArray("goodList");
                        for(int i=0;i<list.length();i++){
                            Map map = new HashMap();
                            JSONObject jsobject = list.optJSONObject(i);
                            map.put("sumCount",jsobject.getString("sumCount"));
                            map.put("cloudGoodId",jsobject.getString("cloudGoodId"));
                            map.put("joinCount", jsobject.getString("joinCount"));
//                            map.put("leftCount",jsobject.getString("leftCount"));
                            map.put("price",jsobject.getString("price"));
                            map.put("goodId",jsobject.getString("goodId"));
                            map.put("cloudNo",jsobject.getString("cloudNo"));
                            map.put("pic",Uriconfig.baseUrl+jsobject.getString("pic"));
                            map.put("goodsName",jsobject.getString("goodsName"));
                            map.put("limitBuy",jsobject.getString("limitBuy"));//是否限购   0否  1是
                            dataList.add(map);
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

        switch (index){
            case 0:
                tv_pop.setTextColor(getResources().getColor(R.color.black_text));
                tv_newest.setTextColor(getResources().getColor(R.color.black_text));
                tv_anounce.setTextColor(getResources().getColor(R.color.red_text));
                tv_price.setTextColor(getResources().getColor(R.color.black_text));
                classify_price.setBackgroundResource(R.drawable.classify_price_default);
                break;
            case 1:
                tv_anounce.setTextColor(getResources().getColor(R.color.black_text));
                tv_newest.setTextColor(getResources().getColor(R.color.black_text));
                tv_pop.setTextColor(getResources().getColor(R.color.red_text));
                tv_price.setTextColor(getResources().getColor(R.color.black_text));
                classify_price.setBackgroundResource(R.drawable.classify_price_default);
                break;
            case 2:
                tv_anounce.setTextColor(getResources().getColor(R.color.black_text));
                tv_pop.setTextColor(getResources().getColor(R.color.black_text));
                tv_newest.setTextColor(getResources().getColor(R.color.red_text));
                tv_price.setTextColor(getResources().getColor(R.color.black_text));
                classify_price.setBackgroundResource(R.drawable.classify_price_default);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        setDeafault();
        page=1;
        dataList.clear();
        switch (v.getId()){
            case R.id.tv_anounce:
                index=0;
                getAnnounceGoods(index, getActivity());
                break;
            case R.id.tv_pop:
                index=1;
                getAnnounceGoods(index, getActivity());
                break;
            case R.id.tv_newest:
                index=2;
                getAnnounceGoods(2, getActivity());
                break;
            case R.id.ll_price:
                tv_price.setTextColor(getResources().getColor(R.color.red_text));
                if(isPressed){
                    classify_price.setBackgroundResource(R.drawable.classify_price_up);
                    index=3;
                    getAnnounceGoods(index, getActivity());//价值升序
                    isPressed =false;
                }else {
                    classify_price.setBackgroundResource(R.drawable.classify_price_down);
                    index=4;
                    getAnnounceGoods(index, getActivity());//价值降序
                    isPressed =true;
                }
                break;
        }
        listView.setSelection(0);
    }
    boolean isPressed = true;
    private void setDeafault(){
        tv_anounce.setTextColor(getResources().getColor(R.color.black_text));
        tv_pop.setTextColor(getResources().getColor(R.color.black_text));
        tv_newest.setTextColor(getResources().getColor(R.color.black_text));
        tv_price.setTextColor(getResources().getColor(R.color.black_text));
        classify_price.setBackgroundResource(R.drawable.classify_price_default);
    }

    public void setStr(String str) {
        this.str = str;
        setDeafault();
        page=1;
        dataList.clear();
        adapter.notifyDataSetChanged();
        tv_anounce.setTextColor(getResources().getColor(R.color.red_text));
        getAnnounceGoods(0, getActivity());
    }

    public ArrayList<Map<String, Object>> getDataList() {
        return dataList;
    }

    @Override
    public void onRefresh() {
        page=1;
        getAnnounceGoods(index,getActivity());
        onLoad();
    }

    @Override
    public void onLoadMore() {
        page++;
        getAnnounceGoods(index,getActivity());
        onLoad();
    }
    private void onLoad() {
        listView.stopRefresh();
        listView.setRefreshTime(CommonUtils.getTime());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
        Map map = (Map) parent.getItemAtPosition(position);
        intent.putExtra("goodsId",map.get("cloudGoodId").toString());
        startActivityForResult(intent, MainActivity.GoodsDetail);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK && requestCode == MainActivity.GoodsDetail){
            ((MainActivity)getActivity()).setTabSelection(3);
        }
    }
    public interface onWorkFinish{
        void onfinish();
    }
}