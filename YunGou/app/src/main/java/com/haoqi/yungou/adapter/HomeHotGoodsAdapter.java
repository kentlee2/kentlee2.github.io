package com.haoqi.yungou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 */
public class HomeHotGoodsAdapter extends BaseAdapter {

    private final ImageLoader imagLoader;
    private List<Map<String,Object>> list;
    private Context context;
    private HotHolder holder;

    public HomeHotGoodsAdapter(Context context){
        this.context=context;
        this.list=new ArrayList<Map<String,Object>>();
        imagLoader = ImageLoader.getInstance();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map<String,Object> getItem(int location) {
        return list.get(location);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void addObject(Map<String,Object> map){
        list.add(map);
        notifyDataSetChanged();
    }
    @Override
    public View getView(int location, View arg1, ViewGroup arg2) {
        if(arg1==null){
            holder = new HotHolder();
            arg1 = LayoutInflater.from(context).inflate(R.layout.item_hot_goods,null);
            holder.image=(ImageView)arg1.findViewById(R.id.goods_image); //商品图片
            holder.price_text=(TextView)arg1.findViewById(R.id.price_text);//商品价格
            holder.hasJoin_text=(TextView)arg1.findViewById(R.id.hasJoin_num);//已参与
            holder.total_text=(TextView)arg1.findViewById(R.id.total_num);//总需人数
            holder.remain_text=(TextView)arg1.findViewById(R.id.remain_num);//剩余人数
            holder.pb=(ProgressBar)arg1.findViewById(R.id.progressBar);//进度条
            arg1.setTag(holder);
        }else{
            holder = (HotHolder)arg1.getTag();
        }
        Map<String,Object> map=getItem(location);
        String imgUrl = map.get("pic").toString();
        String total = map.get("sumCount").toString();
        String join = map.get("joinCount").toString();
        holder.pb.setMax(Integer.valueOf(total));
        holder.pb.setProgress(Integer.valueOf(join));
        holder.price_text.setText("价值:￥"+Double.valueOf(map.get("price").toString()));
        holder.hasJoin_text.setText(join);
        holder.total_text.setText(total);
        holder.remain_text.setText(map.get("leftCount").toString());
        imagLoader.displayImage(imgUrl,holder.image, CommonUtils.displayImageOptions);
        return arg1;
    }

    public void clear() {
        if(!list.isEmpty()){
            list.clear();
            notifyDataSetChanged();
        }
    }

    class HotHolder {
             ImageView image;
             TextView price_text,hasJoin_text,total_text,remain_text;
             public ProgressBar pb;
         }
}
