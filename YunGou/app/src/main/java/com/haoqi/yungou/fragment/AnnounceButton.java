package com.haoqi.yungou.fragment;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.CommonUtils;

/**
 * Created by LiGuang on 2016/9/13.
 */
public class AnnounceButton extends LinearLayout {
    private TextView tv_status;
    private MyCountDownTimer mc;
    private String awardee;
    private OnFinished finish;
    private AnnounceButton view;

    public AnnounceButton(Context context) {
        this(context, null);
    }

    public AnnounceButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnnounceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * find 控件，初始化
     * @param context
     */
    private void initView(Context context) {
        view = this;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.view_commodity_buying,this);
        tv_status= (TextView) view.findViewById(R.id.jx_time);

    }
    public void setTime(String time){
        long date = CommonUtils.comparedata(time);
        mc = new MyCountDownTimer(date, 60);
    }
    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture
         *      表示以毫秒为单位 倒计时的总数
         *
         *      例如 millisInFuture=1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *      例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            //sendBroadcast
            if(finish!=null)
            finish.onfinish(view);
        }

        /**
         * 处理时间倒计时进行页面刷新
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {

            int ss = 1000;
            int mi = ss * 60;
            long minute = millisUntilFinished/ mi;
            long second = (millisUntilFinished- minute * mi) / ss;
            long milliSecond = millisUntilFinished  - minute * mi - second * ss;
            String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
            String strSecond = second < 10 ? "0" + second : "" + second;//秒
            String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
            strMilliSecond = milliSecond >100 ? strMilliSecond.substring(0,strMilliSecond.length()-1) : "" + strMilliSecond;
            tv_status.setText("揭晓倒计时:"+strMinute + " 分 "+strSecond+"秒"+strMilliSecond);
        }
    }

    /**
     * 设置获奖人
     * @param awardee
     */
    public void setAwardee(String awardee){
        this.awardee= awardee;
    }
    public void start(){
        mc.start();
    }

    public void setOnFinishListener(OnFinished onfinish){
        this.finish = onfinish;
    }
    interface OnFinished{
        void onfinish(View v);
    }
}
