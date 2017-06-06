package com.haoqi.yungou.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoqi.yungou.R;

/**
 * Created by Kentlee on 2016/9/21.
 */
public class EmptyView extends LinearLayout {
    private RetryListener retryListener;
    private ImageView iv_reload;
    private TextView tv_reload1,tv_reload2;
    private Button btn_login;

    public EmptyView(Context context) {
        super(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) { return; }
        initView(context);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void initView(final Context context) {
        View view=LayoutInflater.from(context).inflate(R.layout.empty_view,this);
        iv_reload = (ImageView)view.findViewById(R.id.iv_reload);
        tv_reload1 = (TextView)view.findViewById(R.id.tv_reload1);
        tv_reload2 = (TextView)view.findViewById(R.id.tv_reload2);
        btn_login = (Button)view.findViewById(R.id.login_now);
        iv_reload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(retryListener!=null) {
                    retryListener.retry();
                }
            }
        });

    }
    public  interface RetryListener{
        void retry();
    }
    public void setRetryListener(RetryListener listener) {
        this.retryListener = listener;
    }
    public void setNoDataView(){
        iv_reload.setImageResource(R.drawable.no_data);
        tv_reload1.setVisibility(View.GONE);
        tv_reload2.setVisibility(View.VISIBLE);
    }
    public void setCartEmptyView(){
        iv_reload.setImageResource(R.drawable.empty_cart);
        tv_reload2.setVisibility(View.VISIBLE);
        tv_reload2.setText(R.string.cart_empty);
        tv_reload1.setVisibility(View.INVISIBLE);
//        if(!UserUtils.isLogined()){
//            tv_reload1.setText(R.string.cart_empty_nologin);
//            tv_reload1.setVisibility(View.VISIBLE);
//            btn_login.setVisibility(View.VISIBLE);
//        }else{
//            btn_login.setVisibility(View.INVISIBLE);
//            tv_reload1.setVisibility(View.INVISIBLE);
//        }
    }

    public Button getBtn_login() {
        return btn_login;
    }
}
