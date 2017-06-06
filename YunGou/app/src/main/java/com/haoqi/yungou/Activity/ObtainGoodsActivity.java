package com.haoqi.yungou.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.Bimp;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.CustomDialog;
import com.haoqi.yungou.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObtainGoodsActivity extends CommonActivity implements XListView.IXListViewListener {

    private static final int ADDADDRESS = 0;
    private XListView listView;
    private ArrayList<Map<String,Object>> datalist = new ArrayList<>();
    private MySimpleAdapter adapter;
    private int firstIndex =1;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_goods);
        imageLoader = ImageLoader.getInstance();
        listView = (XListView)findViewById(R.id.obtain_list);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        adapter = new MySimpleAdapter(this,datalist,R.layout.item_obtain_goods,
                new String[]{"pic","name","winCode","price","status"},
                new int[]{R.id.iv_goodspic,R.id.tv_goodsname,R.id.tv_cloudNo,R.id.tv_price,R.id.tv_goods_status});
        listView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("firstIndex", firstIndex+"");
        VolleyRequest.post(this, Uriconfig.obtain_goods, params, null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        if(inf.optString("isPageEnd").equals("1")){
                         listView.setPullLoadEnable(false);
                        }
                        JSONArray array = inf.optJSONArray("myGoods");
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.optJSONObject(i);
                            Map<String,Object> map = new HashMap();
                            map.put("id",object.optString("id"));
                            map.put("pic",Uriconfig.baseUrl+object.optString("img"));
                            map.put("winCode",object.optString("winCode"));
                            map.put("name","(第"+object.optString("cloudNo")+"云)"+object.optString("goodsName"));
                            map.put("goodsName",object.optString("goodsName"));
                            map.put("price","￥"+object.optDouble("price"));
                            String goodsStatus = object.optString("status");//1:未接单  2:已接单 3：已发货 4：成功（确认收货） 5：失败
                            String isAdr = object.optString("isAddr");//是否已经填写收货地址  0否  1是
                            if(goodsStatus.equals("1")){//1:未接单
                                if(isAdr.equals("0")){ //是否已经填写收货地址  0否  1是
                                    map.put("status","未填写收货地址");
                                    map.put("wuliu",false);
                                    map.put("shaidan",false);
                                    map.put("shouhuo",false);
                                    map.put("dizhi",true);
                                }else{
                                    map.put("status","等待卖家发货");
                                    map.put("wuliu",true);
                                    map.put("shaidan",false);
                                    map.put("shouhuo",false);
                                    map.put("dizhi",false);
                                }
                            }else if (goodsStatus.equals("2")){
                                map.put("status","等待卖家发货");
                                map.put("wuliu",true);
                                map.put("shaidan",false);
                                map.put("shouhuo",true);
                                map.put("dizhi",false);
                            }else if (goodsStatus.equals("3")){
                                map.put("status","已发货");
                                map.put("wuliu",true);
                                map.put("shaidan",false);
                                map.put("shouhuo",true);
                                map.put("dizhi",false);
                            }else if (goodsStatus.equals("4")){
                                map.put("status","交易已完成");
                                map.put("wuliu",true);
                                map.put("shaidan",true);
                                map.put("shouhuo",false);
                                map.put("dizhi",false);
                            }else if (goodsStatus.equals("5")){
                                map.put("status","交易失败");
                                map.put("wuliu",false);
                                map.put("shaidan",false);
                                map.put("shouhuo",false);
                                map.put("dizhi",false);
                            }

                            datalist.add(map);
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
    public void onRefresh() {
        firstIndex=1;
        datalist.clear();
        adapter.notifyDataSetChanged();
        getData();
        onLoad();
    }

    @Override
    public void onLoadMore() {
        firstIndex++;
        getData();
        onLoad();
    }
    private void onLoad() {
        listView.stopRefresh();
        listView.setRefreshTime(CommonUtils.getTime());
    }
    private class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ImageView iv=(ImageView) v.findViewById(R.id.iv_goodspic);
            Button btn_shaidan=(Button) v.findViewById(R.id.btn_shaidan);//去晒单
            Button btn_wuliu=(Button) v.findViewById(R.id.btn_tranfor);//查看物流
            Button btn_dizhi=(Button) v.findViewById(R.id.add_address);//添加地址
            Button btn_shouhuo=(Button) v.findViewById(R.id.btn_comfirm);//确认收货
            final Map<String,Object> map = (Map<String, Object>) getItem(position);
            boolean wuliu = (boolean) map.get("wuliu");
            boolean shaidan = (boolean) map.get("shaidan");
            boolean shouhuo = (boolean)map.get("shouhuo");
            boolean dizhi = (boolean)map.get("dizhi");

             if(shaidan){
                 btn_shaidan.setOnClickListener(new MyOnclickListener(0,map));
                 btn_shaidan.setVisibility(View.VISIBLE);
             }else{
                 btn_shaidan.setVisibility(View.GONE);
             }
            if(wuliu){
                btn_wuliu.setOnClickListener(new MyOnclickListener(1,map));
                btn_wuliu.setVisibility(View.VISIBLE);
            }else{
                btn_wuliu.setVisibility(View.GONE);
            }
            if(dizhi){
                btn_dizhi.setOnClickListener(new MyOnclickListener(2,map));
                btn_dizhi.setVisibility(View.VISIBLE);
            }else{
                btn_dizhi.setVisibility(View.GONE);
            }
             if(shouhuo){
                 btn_shouhuo.setOnClickListener(new MyOnclickListener(3,map));
                 btn_shouhuo.setVisibility(View.VISIBLE);
             }else{
                 btn_shouhuo.setVisibility(View.GONE);
             }

            imageLoader.displayImage(map.get("pic").toString(),iv, CommonUtils.displayImageOptions);
            return v ;
        }
        class MyOnclickListener implements View.OnClickListener{

            private final int intext;
            private final Map<String, Object> map;

            public MyOnclickListener(int i, Map<String, Object> map) {
                intext = i;
                this.map = map;
            }

            @Override
            public void onClick(View v) {
                switch (intext){
                    case 0:
                        Intent intent = new Intent(ObtainGoodsActivity.this,ShaidanActivity.class);
                        intent.putExtra("id",map.get("id").toString());
                        intent.putExtra("map", (Serializable) map);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(ObtainGoodsActivity.this,ChecklogisticsActivity.class);
                        intent2.putExtra("id",map.get("id").toString());
                        intent2.putExtra("map", (Serializable) map);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent addIntent = new Intent(ObtainGoodsActivity.this,AddAddressActivity.class);
                        addIntent.putExtra("map", (Serializable) map);
                        startActivityForResult(addIntent,ADDADDRESS);
                        break;
                    case 3:
                        CustomDialog.Builder dialog=new CustomDialog.Builder(ObtainGoodsActivity.this);
                        dialog.setMessage(R.string.receive_sure)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        receiveGoods(map.get("id").toString());//确认收货
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();

                        break;
                }
            }
        }
    }

    private void receiveGoods(String id) {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("itemId", id);
        params.put("firstIndex", firstIndex+"");
        VolleyRequest.post(this, Uriconfig.receive_goods, params, null, new RequestListener() {

            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        ToastUtils.showShort(ObtainGoodsActivity.this,"确认收货成功");
                        refresh();
                    }else{
                        ToastUtils.showShort(ObtainGoodsActivity.this,res.toString());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK ){
            switch (requestCode){
                case ADDADDRESS:
                    firstIndex=1;
                    refresh();
                    break;
            }
        }
    }
   public void refresh(){
       datalist.clear();
       adapter.notifyDataSetChanged();
       getData();
   }
    @Override
    protected void onResume() {
        super.onResume();
        Bimp.tempSelectBitmap.clear();
    }
}
