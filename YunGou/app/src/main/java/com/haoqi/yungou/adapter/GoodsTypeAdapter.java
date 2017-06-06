package com.haoqi.yungou.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.CartUtils;
import com.haoqi.yungou.util.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/20.
 */
public class GoodsTypeAdapter extends BaseAdapter {
    private final Activity context;
    private final ArrayList<Map<String, Object>> dataList;
    private final LayoutInflater inflate;
    private TypeHolder holder;
    private ImageLoader imageLoader;
    private ImageView buyImg;// 这是在界面上跑的小图片
    public GoodsTypeAdapter(Activity conext, ArrayList<Map<String, Object>> dataList) {
        imageLoader = ImageLoader.getInstance();
        this.context = conext;
        this.dataList = dataList;
        inflate = LayoutInflater.from(conext);
    }

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
        holder = new TypeHolder();
        if(convertView==null){
            convertView = inflate.inflate(R.layout.item_all_goods_type,null);
            holder.rl = (RelativeLayout)convertView.findViewById(R.id.rl_root);
            holder.img = (ImageView)convertView.findViewById(R.id.goods_image);
            holder.lable = (ImageView)convertView.findViewById(R.id.iv_goods_label);
            holder.add = (ImageView)convertView.findViewById(R.id.add_to_cart);
            holder.title = (TextView)convertView.findViewById(R.id.all_goods_title);
            holder.value = (TextView)convertView.findViewById(R.id.goods_value);
            holder.pb = (ProgressBar)convertView.findViewById(R.id.progressBar);
            holder.lable.setVisibility(View.GONE);
            convertView.setTag(holder);
        }else{
            holder = (TypeHolder)convertView.getTag();
        }
        final Map map = dataList.get(position);
        imageLoader.displayImage(map.get("pic").toString(),holder.img, CommonUtils.displayImageOptions);
        holder.title.setText("(第"+map.get("cloudNo")+"云)"+map.get("goodsName").toString());
        holder.value.setText("价值：￥"+map.get("price").toString());
        String sumCount = map.get("sumCount").toString();
        String joinCount = map.get("joinCount").toString();
        int sum = Integer.valueOf(sumCount);
        int join = Integer.valueOf(joinCount);
        holder.pb.setMax(sum);
        holder.pb.setProgress(join);
        String islimit =map.get("limitBuy").toString();
        if("1".equals(islimit)){
            holder.lable.setVisibility(View.VISIBLE);
        }else{
            holder.lable.setVisibility(View.GONE);
        }
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
                RelativeLayout rl = (RelativeLayout) v.getParent();
                ImageView iv_goods = (ImageView) rl.getChildAt(0);
                iv_goods.getLocationInWindow(start_location);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
                buyImg = new ImageView(context);// buyImg是动画的图片（R.drawable.sign）
                buyImg.setMaxWidth(200);
                buyImg.setMaxHeight(200);
                imageLoader.displayImage(map.get("pic").toString(),buyImg, CommonUtils.displayImageOptions);
                int[] end_loc ={CartUtils.getImg_X(), CartUtils.getImg_Y()};
                CartUtils.setAnim(context,buyImg, start_location,end_loc,map.get("cloudGoodId").toString());// 开始执行动画
            }
        });
        return convertView;
    }


    class TypeHolder{

        public ImageView img;
        public TextView title;
        public TextView value;
        public ProgressBar pb;
        public ImageView add;
        public ImageView lable;
        public RelativeLayout rl;
    }
}
