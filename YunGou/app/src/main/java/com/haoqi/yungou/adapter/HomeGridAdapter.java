package com.haoqi.yungou.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.countDown.CountDownTask;
import com.haoqi.yungou.countDown.CountDownTimers;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Kentlee on 2016/10/8.
 */
public class HomeGridAdapter extends BaseAdapter {
    private final ArrayList<Map<String, Object>> list;
    private final LayoutInflater inflate;
    private final ImageLoader imageLoader;
    private CountDownTask mCountDownTask;
    private NewHolder holder;

    public HomeGridAdapter(Context context, ArrayList<Map<String, Object>> list) {
        this.list = list;
        inflate = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return 4;
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
            convertView = inflate.inflate(R.layout.item_newest_grid,null);
            holder.iv_pic = (ImageView)convertView.findViewById(R.id.iv_goods_img);
            holder.tv_goods_name = (TextView)convertView.findViewById(R.id.tv_goods_name);
            holder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else{
            holder = (NewHolder)convertView.getTag();
        }
//        VolleyLog.e("getView"+"   position="+position);
        if(!list.isEmpty() && position<list.size()) {
            Map map = list.get(position);
            String time = map.get("time").toString();
            holder.tv_time.setBackgroundResource(R.drawable.btn_radius_all_red_normal);
            holder.tv_goods_name.setText(map.get("name").toString());
            imageLoader.displayImage(map.get("img").toString(), holder.iv_pic);
            long targetMillis = CountDownTask.elapsedRealtime() + comparedata(time);
            startCountDown(position, targetMillis, convertView);
        }
        return convertView;
    }
    private void startCountDown(final int position, final long millis, View convertView) {
        if (mCountDownTask != null) {
            mCountDownTask.until(convertView, millis, 60, new CountDownTimers.OnCountDownListener() {
                @Override
                public void onTick(View view, long millisUntilFinished) {
                    doOnTick(position, view, millisUntilFinished, 60);
                }

                @Override
                public void onFinish(View view) {
                    doOnFinish(position, view);
                }
            });
        }
    }
    private void doOnTick(int position, View view, long millisUntilFinished, long countDownInterval) {
        int ss = 1000;
        int mi = ss * 60;
        long minute = millisUntilFinished/ mi;
        long second = (millisUntilFinished- minute * mi) / ss;
        long milliSecond = millisUntilFinished  - minute * mi - second * ss;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond >100 ? strMilliSecond.substring(0,strMilliSecond.length()-1) : "" + strMilliSecond;
        TextView textView2 = (TextView) view.findViewById(R.id.tv_time);
        textView2.setText(strMinute + " : " + strSecond + ":" + strMilliSecond);
    }

    private void doOnFinish(int position, View view) {
        TextView textView2 = (TextView) view.findViewById(R.id.tv_time);
        textView2.setText("正在计算...");
        notifyDataSetChanged();
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setCountDownTask(CountDownTask countDownTask) {
        if (!Objects.equals(mCountDownTask, countDownTask)) {
            mCountDownTask = countDownTask;
            notifyDataSetChanged();
        }
    }
    class NewHolder{
        public ImageView iv_pic;
        public TextView tv_goods_name;
        public TextView tv_time;
    }
    public  long comparedata(String time){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(time);
            Date d2 = df.parse(getCurrentTime());
            long diff = d1.getTime() - d2.getTime();
            return  diff;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public  String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }
}
