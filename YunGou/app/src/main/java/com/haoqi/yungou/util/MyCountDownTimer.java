package com.haoqi.yungou.util;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/9/18.
 */
public class MyCountDownTimer extends CountDownTimer {
    private  TextView tv_min,tv_sec,tv_msec;
    private TextView tv_time;
    private OnFinished finish;

    /**
     *  @param millisInFuture
     *      表示以毫秒为单位 倒计时的总数
     *
     *      例如 millisInFuture=1000 表示1秒
     *
     * @param countDownInterval
     *      表示 间隔 多少微秒 调用一次 onTick 方法
     *
     */
    public MyCountDownTimer(long millisInFuture, long countDownInterval, TextView time) {
        super(millisInFuture, countDownInterval);
        this.tv_time = time;
    }

    public MyCountDownTimer(long millisInFuture, int countDownInterval, TextView tv_min, TextView tv_sec, TextView tv_msec) {
        super(millisInFuture,countDownInterval);
        this.tv_min = tv_min;
        this.tv_sec = tv_sec;
        this.tv_msec = tv_msec;
    }


    @Override
    public void onFinish() {
        if(tv_time!=null)
        tv_time.setText("正在计算中...");
        if(finish!=null)
            finish.onfinish();
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
        if(tv_time!=null) {
            tv_time.setText(strMinute + " : " + strSecond + ":" + strMilliSecond);
        }else{
            tv_min.setText(strMinute);
            tv_sec.setText(strSecond);
            tv_msec.setText(strMilliSecond);
        }
    }
    public void setOnFinishListener(OnFinished onfinish){
        this.finish = onfinish;
    }
    public interface OnFinished{
        void onfinish();
    }
}
