package com.haoqi.yungou.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Kentlee on 2016/10/8.
 */
public class NewestAdapter extends BaseAdapter {
    private final ArrayList<Map<String, Object>> list;
    private final LayoutInflater inflate;
    private final ImageLoader imageLoader;
    private NewHolder holder;

    public NewestAdapter(Activity activity, ArrayList<Map<String, Object>> list) {
        this.list = list;
        inflate = LayoutInflater.from(activity);
        imageLoader = ImageLoader.getInstance();
    }

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
        holder = new NewHolder();
        if(convertView==null){
            convertView = inflate.inflate(R.layout.item_newest,null);
            holder.iv_pic = (ImageView)convertView.findViewById(R.id.iv_goodspic);
            holder.iv_avatar = (ImageView)convertView.findViewById(R.id.sriv_avatar);
            holder.tv_goods = (TextView)convertView.findViewById(R.id.tv_accountname);
            holder.tv_price = (TextView)convertView.findViewById(R.id.tv_price);
            holder.tv_times = (TextView)convertView.findViewById(R.id.tv_times);
            holder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else{
            holder = (NewHolder)convertView.getTag();
        }
        Map  map = (Map) getItem(position);
        String time = map.get("time").toString();
        String joinCount =map.get("joinCount").toString();
        String gainer = map.get("username").toString();
        if(TextUtils.isEmpty(gainer)){
            holder.tv_goods.setText("获得者:无");
        }else
        holder.tv_goods.setText("获得者:"+gainer);
        holder.tv_price.setText("价值:￥"+map.get("price").toString());
        if(TextUtils.isEmpty(joinCount)){
            holder.tv_times.setText("本云参与:0人次");
        }else
        holder.tv_times.setText("本云参与:"+joinCount+"人次");
        holder.tv_time.setText("揭晓时间:"+time);
        imageLoader.displayImage(map.get("goodsImg").toString(),holder.iv_pic, CommonUtils.displayImageOptions);
        imageLoader.displayImage(map.get("headImg").toString(),holder.iv_avatar, CommonUtils.circleImageOptions);
        return convertView;
    }
    class NewHolder{
        public ImageView iv_pic;
        public TextView tv_goods;
        public TextView tv_price;
        public TextView tv_times;
        public TextView tv_time;
        public ImageView iv_avatar;
    }
}
