package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.EmptyView;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
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

public class MyCloundHistoryActivity extends Activity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private EmptyView empty;
    private ArrayList<Map<String,Object>> list =new ArrayList<>();
    private RecordAdapter adapter;
    private XListView mListView;
    private ImageLoader imageLoader;
    private int page =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clound_history);
        mListView = (XListView)findViewById(R.id.record_list);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        imageLoader = ImageLoader.getInstance();
        adapter = new RecordAdapter();
        mListView.setAdapter(adapter);
        getCloundHistory();
    }

    private void getCloundHistory() {

        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("firstIndex", page+"");
        final Context context = GlobalApplication.getContext();
        VolleyRequest.post(context, Uriconfig.my_record, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        JSONArray array = inf.optJSONArray("UserCloudOrderItem");
                        if(inf.optString("isPageEnd").equals("1")){
                            mListView.setPullLoadEnable(false);
                        }
                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.optJSONObject(i);
                            String cloundNo = object.optString("cloudNo");
                            String img = Uriconfig.baseUrl+object.optString("img");
                            String publicTime = object.optString("publicTime");//揭晓时间
                            String goodsName = object.optString("goodsName");
                            String stat = object.optString("status");//揭晓状态（0进行中1待揭晓2已揭晓）
                            String isWin = object.optString("isWin");
                            String id = object.optString("id");
                            String owner = object.optString("owner");//获得者
                            Map map = new HashMap();
                            map.put("id",id);
                            map.put("goodsPic",img);
                            if(TextUtils.isEmpty(owner)){
                                map.put("getter","获得者:");
                            }else {
                                map.put("getter", "获得者:" + owner);
                            }
                            map.put("goodsName","(第"+cloundNo+"云)"+goodsName);
                            map.put("publicTime","揭晓时间:"+publicTime);
                            if("0".equals(stat)){
                                map.put("stat","进行中");
                            }else if("1".equals(stat)){
                                map.put("stat","待揭晓");
                            }else{
                                map.put("stat","已揭晓");
                                if(TextUtils.isEmpty(owner)){
                                    map.put("getter","获得者:无");
                                }else {
                                    map.put("getter", "获得者:" + owner);
                                }
                            }
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

    public void setEmpty(){
        empty = (EmptyView)findViewById(R.id.emptyview);
        empty.setNoDataView();
    }

    @Override
    public void onRefresh() {
        getCloundHistory();
        onLoad();
    }

    @Override
    public void onLoadMore() {
        onLoad();
        page++;
        getCloundHistory();
    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.setRefreshTime(CommonUtils.getTime());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Map map = (Map) parent.getItemAtPosition(position);
                 startActivity(new Intent(this,CloundsDetailActivity.class).putExtra("goodsId",map.get("id").toString()));

    }

    class RecordAdapter extends BaseAdapter{

            private ViewHolder holder;

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
            public View getView(int position, android.view.View convertView, ViewGroup parent) {
                if(convertView==null){
                    holder = new ViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.item_shopping_record,null);
                    holder.img = (ImageView)convertView.findViewById(R.id.iv_goodspic);
                    holder.iv_lable = (ImageView)convertView.findViewById(R.id.iv_goods_label_shopping_record);
                    holder.tv_goodslabel = (TextView)convertView.findViewById(R.id.tv_goodslabel);
                    holder.tv_goodsname = (TextView)convertView.findViewById(R.id.tv_goodsname);
                    holder.tv_goodsprice = (TextView)convertView.findViewById(R.id.tv_goodsprice);
                    holder.tv_getter = (TextView)convertView.findViewById(R.id.tv_getter);
                    holder.tv_code = (TextView)convertView.findViewById(R.id.tv_code);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder)convertView.getTag();
                }
                Map map = (Map) getItem(position);
                imageLoader.displayImage(map.get("goodsPic").toString(),holder.img,CommonUtils.displayImageOptions);
                holder.tv_goodslabel.setText(map.get("stat").toString());
                holder.tv_goodsname.setText(map.get("goodsName").toString());
                String owner = map.get("getter").toString();
                holder.tv_getter.setText(owner);
                CommonUtils.setSpanableText(holder.tv_getter,owner,getResources().getColor(R.color.blue),4,owner.length());
                holder.tv_code.setText(map.get("publicTime").toString());
                return convertView;
            }
        }
    private  class ViewHolder{

        public ImageView img;
        public ImageView iv_lable;
        public TextView tv_goodslabel;
        public TextView tv_goodsname;
        public TextView tv_goodsprice;
        public TextView tv_getter;
        public TextView tv_code;
    }
    @Override
    protected void onStart() {
        super.onStart();
        NavigationView nav = (NavigationView)findViewById(R.id.nav);
        nav.setClickCallback(new NavigationView.ClickCallback() {
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
