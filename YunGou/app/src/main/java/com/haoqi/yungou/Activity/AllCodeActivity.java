package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.adapter.CodeGridAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;

import java.util.ArrayList;

public class AllCodeActivity extends Activity {

    private TextView tv_buy_count,tv_buy_time;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_announced_gainer_codes);
        int joinCount = getIntent().getIntExtra("join",0);
        String buyTime = getIntent().getStringExtra("buyTime");
        String allCodes = GoodsDetailActivity.allCodes;
        TextView tv_show_more = (TextView) findViewById(R.id.tv_show_more);
        tv_show_more.setVisibility(View.GONE);
        tv_buy_count  =(TextView)findViewById(R.id.tv_buy_count);
        tv_buy_time =(TextView)findViewById(R.id.tv_buy_time);
         gridView = (GridView) findViewById(R.id.code_grid);
        String str ="获得者本云总共参与：" + joinCount + "人次";
        tv_buy_count.setText(str);
        tv_buy_time.setText(buyTime);
        CommonUtils.setSpanableText(tv_buy_count, str, getResources().getColor(R.color.red_text), 10, str.length()-2);
        setCodeLayout(allCodes);
        setTitleBar();
    }

    private void setTitleBar() {
        LinearLayout ll_main = (LinearLayout) findViewById(R.id.ll_main);
        NavigationView nav = new NavigationView(this);
        nav.setTitle(R.string.cloundsNo);
        nav.getBackView().setBackgroundResource(R.drawable.common_tab_bg);
        nav.getBackView().setImageResource(R.drawable.previous_pre);
        nav.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_main.addView(nav,0);
    }

    private void setCodeLayout(String codes) {
        ArrayList<String> list = new ArrayList();
        String[] codesArr = codes.split(",");
        for(int i=0;i<codesArr.length;i++){
                String code = codesArr[i];
                list.add(code);
        }
        CodeGridAdapter adapter = new CodeGridAdapter(this,R.layout.item_code,list);
        gridView.setAdapter(adapter);
    }
}
