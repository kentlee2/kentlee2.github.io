package com.haoqi.yungou.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoqi.yungou.R;

/**
 * 导航栏组件,目前包括返回键,标题,右侧按钮.其中:
 * </br>返回键已经实现按键监听
 * </br>右侧按钮已实现按键监听
 * </br>标题默认不可点击
 * @author Asia
 *
 */
public class NavigationView extends RelativeLayout implements View.OnClickListener {



    public NavigationView(Context context){
        this(context, null);
    }
    private ImageView backView;
    private TextView titleView;
    private ImageView rightView;
    private  TextView rightText;
    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.navigation_view, this, true);
        backView = (ImageView) view.findViewById(R.id.iv_nav_back);
        backView.setOnClickListener(this);
        titleView = (TextView) view.findViewById(R.id.tv_nav_title);
        rightText = (TextView) view.findViewById(R.id.tv_nav_right);
        rightView = (ImageView) view.findViewById(R.id.iv_nav_right);
        rightView.setOnClickListener(this);
        rightText.setOnClickListener(this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.navBar);
        int left = ta.getResourceId(R.styleable.navBar_leftBackgroud, 0);
        int right = ta.getResourceId(R.styleable.navBar_rightBackgroud, 0);
        String tile = ta.getString(R.styleable.navBar_title);
        String rightStr = ta.getString(R.styleable.navBar_rightText);
        titleView.setText(tile);
        rightText.setText(rightStr);
        backView.setImageResource(left);
        rightView.setImageResource(right);
        ta.recycle();
    }

    /**
     * 获取返回按钮
     * @return
     */
    public ImageView getBackView() {
        return backView;
    }

    /**
     * 获取标题控件
     * @return
     */
    public TextView getTitleView() {
        return titleView;
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        titleView.setText(title);
    }
    public void setTitle(int title){
        titleView.setText(title);
    }

    /**
     * 获取右侧按钮,默认不显示
     * @return
     */
    public ImageView getRightView() {
        return rightView;
    }

    public TextView getRightText(){
        return  rightText;
    }
    private ClickCallback callback;
    /**
     * 设置按钮点击回调接口
     * @param callback
     */
    public void setClickCallback(ClickCallback callback) {
        this.callback = callback;
    }

    /**
     * 导航栏点击回调接口
     * </br>如若需要标题可点击,可再添加
     * @author Asia
     *
     */
    public static interface ClickCallback{
        /**
         * 点击返回按钮回调
         */
        void onBackClick();

        void onRightClick();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_nav_back:
                if(callback!=null) {
                    callback.onBackClick();
                }
                break;
            case R.id.iv_nav_right:
                if(callback!=null) {
                    callback.onRightClick();
                }
                break;
            case R.id.tv_nav_right:
                if(callback!=null) {
                    callback.onRightClick();
                }
                break;
        }
    }
}
