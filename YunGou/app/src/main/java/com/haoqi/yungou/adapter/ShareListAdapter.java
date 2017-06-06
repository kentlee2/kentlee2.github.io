package com.haoqi.yungou.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.ShaidanCommontActivity;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.JsonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kentlee on 2016/10/18.
 */
public class ShareListAdapter extends BaseAdapter {
    private final ArrayList<Map<String,Object>> praiseList;
    private  Animation animation;
    private  Context context;
    private  ArrayList<Map> dataList;
    private  LayoutInflater inflate;
    private  int res;
    private  ImageLoader imageLoader;
    private ViewHolder holder;

    public ShareListAdapter(Context context, int res, ArrayList<Map> dataList) {
         praiseList = new ArrayList<Map<String,Object>>();
        imageLoader = ImageLoader.getInstance();
        this.context = context;
        this.dataList =dataList;
        this.res = res;
        inflate = LayoutInflater.from(context);
        animation= AnimationUtils.loadAnimation(context,R.anim.praise);
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
        if(convertView==null){
            holder = new ViewHolder();
            convertView = inflate.inflate(res,null);
            holder.avatar = (ImageView)convertView.findViewById(R.id.sriv_avatar);
            holder.summary[0] = (ImageView)convertView.findViewById(R.id.siv_shaidan_summary1);
            holder.summary[1]= (ImageView)convertView.findViewById(R.id.siv_shaidan_summary2);
            holder.summary[2] = (ImageView)convertView.findViewById(R.id.siv_shaidan_summary3);
            holder.tv_nickname = (TextView)convertView.findViewById(R.id.tv_nickname);
            holder.tv_shaidan_time = (TextView)convertView.findViewById(R.id.tv_shaidan_time);
            holder.tv_shaidan_title = (TextView)convertView.findViewById(R.id.tv_shaidan_title);
            holder.tv_shaidan_summary = (TextView)convertView.findViewById(R.id.tv_shaidan_summary);
            holder.tv_envy =(TextView)convertView.findViewById(R.id.tv_envy);
            holder.tv_comment =(TextView)convertView.findViewById(R.id.tv_comment);
            holder.rl_envy =(RelativeLayout)convertView.findViewById(R.id.rl_envy);
            holder.rl_comment =(RelativeLayout)convertView.findViewById(R.id.rl_comment);
            holder.rl_share =(RelativeLayout)convertView.findViewById(R.id.rl_share); 
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Map map = (Map) getItem(position);
        String[] imgArr = map.get("imgs").toString().split(",");
        for(int i=0;i<imgArr.length;i++){
            switch (i){
                case 0:
                    imageLoader.displayImage(Uriconfig.baseUrl+imgArr[0].toString(), holder.summary[0], CommonUtils.displayImageOptions);
                    holder.summary[1].setVisibility(View.INVISIBLE);
                    holder.summary[2].setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    imageLoader.displayImage(Uriconfig.baseUrl+imgArr[0].toString(), holder.summary[0], CommonUtils.displayImageOptions);
                    imageLoader.displayImage(Uriconfig.baseUrl+imgArr[1].toString(), holder.summary[1], CommonUtils.displayImageOptions);
                    holder.summary[1].setVisibility(View.VISIBLE);
                    holder.summary[2].setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    imageLoader.displayImage(Uriconfig.baseUrl+imgArr[0].toString(), holder.summary[0], CommonUtils.displayImageOptions);
                    imageLoader.displayImage(Uriconfig.baseUrl+imgArr[1].toString(), holder.summary[1], CommonUtils.displayImageOptions);
                    imageLoader.displayImage(Uriconfig.baseUrl+imgArr[2].toString(), holder.summary[2], CommonUtils.displayImageOptions);
                    holder.summary[1].setVisibility(View.VISIBLE);
                    holder.summary[2].setVisibility(View.VISIBLE);
                    break;
            }
            if(i<3) {

            }
        }
        imageLoader.displayImage(map.get("headImg").toString(),holder.avatar, CommonUtils.circleImageOptions);
        holder.tv_envy.setText(map.get("likely").toString());
        holder.tv_comment.setText(map.get("comments").toString());
        holder.tv_nickname.setText(map.get("username").toString());
        holder.tv_shaidan_time.setText(map.get("createTime").toString());
        holder.tv_shaidan_title.setText(map.get("title").toString());
        holder.tv_shaidan_summary.setText(map.get("detail").toString());
        holder.rl_envy.setOnClickListener(new MyOnclickListener(0,map));
        holder.rl_comment.setOnClickListener(new MyOnclickListener(1,map));
//        holder.rl_share.setOnClickListener(new MyOnclickListener(2));
        return convertView;
    }
    class MyOnclickListener implements View.OnClickListener{

        private  int intext;
        private  Map map;

        public MyOnclickListener(int i, Map map) {
            intext = i;
            this.map = map;
        }

        @Override
        public void onClick(View v) {
            switch (intext){
                case 0:
                    final TextView tv_praise_anim = (TextView) ((RelativeLayout)v).getChildAt(1);
                    final TextView tv_praise = (TextView) ((RelativeLayout)v).getChildAt(0);
                    boolean isPraise = DBManager.getInstance().isPraise(map.get("shareId").toString());
                    if(!isPraise) {
                        envy(map, tv_praise, tv_praise_anim);
                    }else{
                        ToastUtils.showShort(context,"您已点过赞");
                        return;
                    }
                    break;
                case 1:
                    context.startActivity(new Intent(context, ShaidanCommontActivity.class).putExtra("shareId",map.get("shareId").toString()));
                    break;
                case 2:
                    break;
            }
        }
    }

    private void envy(final Map map, final TextView tv_praise, final TextView tv_praise_anim) {

        RequestParams params = new RequestParams("shareId",map.get("shareId").toString());
        VolleyRequest.post(context, Uriconfig.praise, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if (status.equals("0")) {
                        Map<String,Object> maps = new HashMap();
                        maps.put(map.get("shareId").toString(),true);
                        praiseList.add(maps);
                        DBManager.getInstance().updateUserInfo(Constant.PRAISE, JsonUtils.toJson(praiseList));
                        int praisNum = Integer.parseInt(map.get("likely").toString());
                        tv_praise.setText(praisNum+1+"");
                        map.put("likely",praisNum+1+"");
                        notifyDataSetChanged();
                        tv_praise_anim.setVisibility(View.VISIBLE);
                        tv_praise_anim.startAnimation(animation);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv_praise_anim.setVisibility(View.GONE);
                            }
                        },1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                 ToastUtils.showShort(context,e.toString());
            }
        });
    }

    private class ViewHolder{

        public ImageView avatar;
        public TextView tv_nickname;
        public TextView tv_shaidan_time;
        public TextView tv_shaidan_title;
        public ImageView[] summary = new ImageView[3];
        public RelativeLayout rl_share;
        public RelativeLayout rl_comment;
        public RelativeLayout rl_envy;
        public TextView tv_shaidan_summary;
        public TextView tv_envy;
        public TextView tv_comment;
    }
}
