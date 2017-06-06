package com.haoqi.yungou.customView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.haoqi.yungou.Activity.GoodsDetailActivity;
import com.haoqi.yungou.Activity.MainActivity;
import com.haoqi.yungou.adapter.HomeHotGoodsAdapter;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 */
public class GoodsLayout extends LinearLayout {

    private HomeHotGoodsAdapter adapter;
    private Context context;

    public GoodsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setAdapter(HomeHotGoodsAdapter adapter) {
        this.adapter = adapter;
        removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            final Map<String, Object> map = adapter.getItem(i);
            View view = adapter.getView(i, null, null);
            view.setPadding(10, 0, 10, 0);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  =new Intent(context, GoodsDetailActivity.class);
                    intent.putExtra("goodsId",map.get("goodId").toString());
                    ((Activity)context).startActivityForResult(intent, MainActivity.GoodsDetail);
                }
            });
            this.setOrientation(HORIZONTAL);
            this.addView(view, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }
}