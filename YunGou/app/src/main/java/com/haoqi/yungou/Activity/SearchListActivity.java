package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.CartUtils;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.UserUtils;
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

public class SearchListActivity extends Activity implements View.OnClickListener, XListView.IXListViewListener {

    private XListView listView;
    private ArrayList<Map<String,String>> list= new ArrayList<>();
    private SearchAdapter adapter;
    private LayoutInflater inflate;
    private ImageLoader imageLoader;
    private EditText et_search;
    private ImageView iv_cart;
    private TextView tv_cart_count;
    private int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        listView = (XListView)findViewById(R.id.lv_search);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);
        listView.setXListViewListener(this);
        listView.setAutoLoadEnable(true);
        inflate = getLayoutInflater();
        registerReceiver(cartReceiver,new IntentFilter(Constant.ADD_CART));
        ImageView iv_search_back =(ImageView)findViewById(R.id.iv_search_back);
        TextView tv_search =(TextView)findViewById(R.id.tv_goods_search);
         tv_cart_count =(TextView)findViewById(R.id.tv_cart_counter);
        et_search =(EditText)findViewById(R.id.et_search_content);
        iv_cart =(ImageView)findViewById(R.id.iv_cart);
        iv_search_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        imageLoader = ImageLoader.getInstance();
         adapter = new SearchAdapter();
        listView.setAdapter(adapter);
        String str = getIntent().getStringExtra("str");
        et_search.setText(str);
        getData(str);
        getCartCount();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = (Map) parent.getItemAtPosition(position);
                String cloudGoodId = map.get("cloudGoodId").toString();
                startActivity(new Intent(SearchListActivity.this,GoodsDetailActivity.class).putExtra("goodsId",cloudGoodId));
            }
        });
    }
    private void getData(String str) {
        RequestParams params = new RequestParams();
        params.put("content",str);
        params.put("firstIndex", page+"");
        if(UserUtils.isLogined()) {
            params.put("userId", UserUtils.getUserId());
        }
        VolleyRequest.post(this, Uriconfig.search_goods, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                       if( inf.optString("isPageEnd").equals("1")){
                           listView.setPullLoadEnable(false);
                       }
                        JSONArray goodArray = inf.optJSONArray("goodList");
                        for(int i=0;i<goodArray.length();i++){
                            JSONObject object =goodArray.optJSONObject(i);
                            Map map = new HashMap();
                            map.put("sumCount",object.optInt("sumCount"));
                            map.put("cloudGoodId",object.optString("cloudGoodId"));
                            map.put( "joinCount", object.optInt("joinCount"));
                            map.put( "price",object.optString("price"));
                            map.put( "goodId", object.optString("goodId"));
                            String img = Uriconfig.baseUrl+object.optString("pic");
                            map.put("img",img);
                            map.put( "goodsName",object.optString("goodsName"));
                            map.put( "leftCount",object.optInt("leftCount"));
                            list.add(map);
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

    }

    @Override
    public void onLoadMore() {
        page++;
        getData(et_search.getText().toString());
        onLoad();
    }
    private void onLoad() {
        listView.stopRefresh();
        listView.setRefreshTime(CommonUtils.getTime());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search_back:
                finish();
                break;
            case R.id.tv_goods_search:
                if(adapter!=null) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }
                getData(et_search.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_search.getWindowToken(),InputMethodManager.SHOW_FORCED);
                break;
        }
    }
    public void getCartCount() {
        String count = DBManager.getInstance().getGoodsCount();
        if(!count.equals("0")){
            tv_cart_count.setText(count);
            tv_cart_count.setVisibility(View.VISIBLE);
        }else{
            tv_cart_count.setVisibility(View.INVISIBLE);
        }
    }
    BroadcastReceiver cartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.ADD_CART.equals(intent.getAction())){
                    getCartCount();
            }
        }
    };
    private class SearchAdapter extends BaseAdapter{

        private Viewholder holder;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                holder = new Viewholder();
                convertView = inflate.inflate(R.layout.item_searchgoods,null);
                holder.image =(ImageView)convertView.findViewById(R.id.goods_pic);
                holder.add_to_cart =(ImageView)convertView.findViewById(R.id.iv_put_in_cart);
                holder.goodsName =(TextView)convertView.findViewById(R.id.tv_goodsname);
                holder.goodsPrice =(TextView)convertView.findViewById(R.id.tv_goodsprice);
                holder.goodspb =(ProgressBar)convertView.findViewById(R.id.progressBar);

                holder.hasJoin_num =(TextView)convertView.findViewById(R.id.hasJoin_num);
                holder.total_num =(TextView)convertView.findViewById(R.id.total_num);
                holder.remain_num =(TextView)convertView.findViewById(R.id.remain_num);
                convertView.setTag(holder);
            }else{
                holder = (Viewholder)convertView.getTag();
            }
              Map map = (Map) getItem(position);
            final String pic = map.get("img").toString();
            imageLoader.displayImage(pic,holder.image, CommonUtils.displayImageOptions);
            holder.goodsName.setText(map.get("goodsName").toString());
            holder.goodsPrice.setText(map.get("price").toString());
            int progress = (int)map.get("joinCount");
            holder.goodspb.setProgress(progress);
            holder.hasJoin_num.setText((int)map.get("joinCount")+"");
            holder.total_num.setText((int)map.get("sumCount")+"");
            holder.remain_num.setText((int)map.get("leftCount")+"");
            final String goodsId = map.get("cloudGoodId").toString();
            holder.add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] start_location = new int[2];
                    RelativeLayout rl = (RelativeLayout) v.getParent().getParent();
                    ImageView iv = (ImageView) rl.getChildAt(0);
                    iv.getLocationInWindow(start_location);
                    ImageView buyImg = new ImageView(SearchListActivity.this);// buyImg是动画的图片
                    buyImg.setMaxWidth(200);
                    buyImg.setMaxHeight(200);
                    imageLoader.displayImage(pic,buyImg, CommonUtils.displayImageOptions);
                    int[] end_loc =new int[2];
                    iv_cart.getLocationInWindow(end_loc);
                    CartUtils.setAnim(SearchListActivity.this,buyImg, start_location,end_loc,goodsId);
                }
            });
            return convertView;
        }
        private class Viewholder{

            public ImageView image,add_to_cart;
            public TextView goodsName;
            public TextView goodsPrice;
            public ProgressBar goodspb;
            public TextView hasJoin_num,total_num,remain_num;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cartReceiver);
    }
}
