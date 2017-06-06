package com.haoqi.yungou.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.CartUtils;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.CustomGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LimitGoodsActivity extends FragmentActivity {

    private CustomGridView gridView;
    private LimitAdapter adapter;
    private ArrayList<Object> dataList = new ArrayList<>();;
    private LayoutInflater inflate;
    private ImageLoader imageLoader;
    private boolean isAdd =false;
    private ImageView iv_right;
    private TextView tv_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_goods);
        gridView = (CustomGridView)findViewById(R.id.gv_limit_goods);
        iv_right = (ImageView)findViewById(R.id.iv_limit_right);
        tv_count = (TextView)findViewById(R.id.limit_cart_count);
        imageLoader = ImageLoader.getInstance();
        inflate = LayoutInflater.from(this);
        adapter = new LimitAdapter();
        gridView.setAdapter(adapter);
        registerReceiver(cartReceiver,new IntentFilter(Constant.ADD_CART));
        getGoods();
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = (Map) parent.getItemAtPosition(position);
                String goodsId = map.get("cloudGoodId").toString();
                startActivity(new Intent(LimitGoodsActivity.this,GoodsDetailActivity.class).putExtra("goodsId",goodsId));
            }
        });
    }
    public void getGoods() {
        String msg = "加载中...";
        RequestParams params = new RequestParams();
        params.put("limitBuy", 1 + "");
        VolleyRequest.post(this, Uriconfig.getGoodsList, params, msg, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if ("0".equals(status)) {
                        JSONArray list = inf.getJSONArray("goodList");
                        for (int i = 0; i < list.length(); i++) {
                            Map map = new HashMap();
                            JSONObject jsobject = list.optJSONObject(i);
                            map.put("sumCount", jsobject.getString("sumCount"));
                            map.put("cloudGoodId", jsobject.getString("cloudGoodId"));
                            map.put("joinCount", jsobject.getString("joinCount"));
                            map.put("leftCount",jsobject.getString("leftCount"));
                            map.put("price", "价值:"+jsobject.getString("price"));
                            map.put("goodId", jsobject.getString("goodId"));
                            map.put("cloudNo", jsobject.getString("cloudNo"));
                            map.put("pic", Uriconfig.baseUrl + jsobject.getString("pic"));
                            map.put("goodsName", jsobject.getString("goodsName"));
                            map.put("limitBuy", jsobject.getString("limitBuy"));//是否限购   0否  1是
                            map.put("BuyCount",jsobject.optString("BuyCount"));
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
    }
    public void back(View v){
        finish();
    }
    class LimitAdapter extends BaseAdapter{

        private ViewHolder holder;

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflate.inflate(R.layout.item_limit_area_good, null);
                holder.image = (ImageView) convertView.findViewById(R.id.limit_goods_image);
                holder.title = (TextView) convertView.findViewById(R.id.all_goods_title);
                holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                holder.hasJoin_num = (TextView) convertView.findViewById(R.id.hasJoin_num);
                holder.total_num = (TextView) convertView.findViewById(R.id.total_num);
                holder.remain_num = (TextView) convertView.findViewById(R.id.remain_num);
                holder.bubble_limit = (TextView) convertView.findViewById(R.id.bubble_limit_count);
                holder.btn_go = (Button) convertView.findViewById(R.id.go_shopping);
                holder.add = (ImageView) convertView.findViewById(R.id.add_to_cart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Map map = (Map) getItem(position);
            final String pic = map.get("pic").toString();
            final String cloudGoodId = map.get("cloudGoodId").toString();
            imageLoader.displayImage(map.get("pic").toString(),holder.image, CommonUtils.displayImageOptions);
            holder.title.setText(map.get("price").toString());
            holder.hasJoin_num.setText(map.get("joinCount").toString());
            holder.total_num.setText(map.get("sumCount").toString());
            holder.remain_num.setText(map.get("leftCount").toString());
            holder.bubble_limit.setText("限购"+map.get("BuyCount").toString()+"人次");
            holder.btn_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAdd =false;
                    CartUtils.AddtoCart(LimitGoodsActivity.this,1,cloudGoodId);
                }
            });
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isAdd=true;
                    int[] start_location = new int[2];
                    LinearLayout ll = (LinearLayout) v.getParent().getParent();
                    FrameLayout fl = (FrameLayout) ll.getChildAt(0);
                    fl.getLocationInWindow(start_location);
                    ImageView buyImg = new ImageView(LimitGoodsActivity.this);
                    buyImg.setMaxWidth(200);
                    buyImg.setMaxHeight(200);
                    imageLoader.displayImage(pic,buyImg, CommonUtils.displayImageOptions);
                    int[] end_loc =new int[2];
                    iv_right.getLocationOnScreen(end_loc);
                    CartUtils.setAnim(LimitGoodsActivity.this,buyImg, start_location,end_loc,cloudGoodId);
                }
            });
            return  convertView;
        }
        private class ViewHolder{

            public ImageView image;
            public TextView title;
            public ProgressBar pb;
            public TextView hasJoin_num,total_num,remain_num;
            public Button btn_go;
            public ImageView add;
            public TextView bubble_limit;
        }
    }
    BroadcastReceiver cartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.ADD_CART.equals(intent.getAction())){
                if(!isAdd) {
                    LimitGoodsActivity.this.setResult(RESULT_OK);
                    finish();
                }else{
                    CartUtils.getCartCount(tv_count);
                }
            }
        }
    };
}
