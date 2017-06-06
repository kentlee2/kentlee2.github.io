package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.CodeGridAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CartUtils;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CloundsDetailActivity extends Activity implements View.OnClickListener {

    private ImageView iv_goodspic;
    private TextView tv_goodsname;
    private TextView tv_goodsjoin;
    private TextView tv_getter;
    private TextView tv_anounce_time;
    private TextView tv_news_number;
    private Button btn_add;
    private TextView tv_clounds_time;
    private TextView clounds_times;
    private String goodsId;
    private TextView tv_goodslabel;
    private RelativeLayout layout_detail;
    private String cloudGoodsId;
    private String img;
    private NavigationView nav;
    private String newCloudGoodsId;
    private LinearLayout ll_codes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
        initView();
        getInfo();
    }


    private void initView() {
        goodsId = getIntent().getStringExtra("goodsId");
        iv_goodspic = (ImageView)findViewById(R.id.iv_goodspic);
        layout_detail = (RelativeLayout)findViewById(R.id.layout_detail);
        tv_goodsname = (TextView)findViewById(R.id.tv_goodsname);
        tv_goodsjoin = (TextView)findViewById(R.id.tv_goodsjoin);
        tv_goodslabel = (TextView)findViewById(R.id.tv_goodslabel);
        tv_getter = (TextView)findViewById(R.id.tv_getter);
        tv_anounce_time = (TextView)findViewById(R.id.tv_anounce_time);
        tv_news_number = (TextView)findViewById(R.id.tv_news_number);
        btn_add = (Button)findViewById(R.id.btn_add_to_cart);

        tv_clounds_time = (TextView)findViewById(R.id.tv_clounds_time);
        clounds_times = (TextView)findViewById(R.id.clounds_times);
        ll_codes = (LinearLayout)findViewById(R.id.ll_codes);
        layout_detail.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }
    private void getInfo() {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("goodsId", goodsId);

        VolleyRequest.post(this, Uriconfig.my_record_detail, params, new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        tv_goodsname.setText(inf.optString("goodsName"));
                        String str = "您已参与 "+inf.optString("joinCount")+" 人次";
                        CommonUtils.setSpanableText(tv_goodsjoin,str, Color.RED,4,str.length()-2);
                        String owner = "获得者:"+inf.optString("owner");
                        CommonUtils.setSpanableText(tv_getter,owner, getResources().getColor(R.color.blue),4,owner.length());
                        tv_anounce_time.setText("揭晓时间:"+inf.optString("publicTime"));
                        tv_news_number.setText("第"+inf.optString("newCloudNo")+"正在进行...");
                        tv_clounds_time.setText(inf.optString("buyTime"));
                        clounds_times.setText(inf.optString("joinCount")+"人次");
                        setCodeLayout(inf.optString("payCode"));
//                        tv_clounds_no.setText(inf.optString("payCode"));
                        String code = inf.optString("status");
                        cloudGoodsId = inf.optString("cloudGoodsId");
                        newCloudGoodsId = inf.optString("newCloudGoodsId");
                        if("0".equals(code)){
                            tv_goodslabel.setText("进行中");
                        }else if("1".equals(code)){
                            tv_goodslabel.setText("待揭晓");
                        }else{
                            tv_goodslabel.setText("已揭晓");
                        }
                         img = Uriconfig.baseUrl+inf.optString("pic");
                        ImageLoader.getInstance().displayImage(img,iv_goodspic, CommonUtils.displayImageOptions);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(CloundsDetailActivity.this,e.toString());
            }
        });
    }

    private void setCodeLayout(String codes) {
        ArrayList<String> list = new ArrayList();
        String[] codesArr = codes.split(",");
        for(int i=0;i<codesArr.length;i++){
                String code = codesArr[i];
                list.add(code);
        }
        CodeGridAdapter adapter = new CodeGridAdapter(this,R.layout.item_code,list);
        GridView grid = (GridView)findViewById(R.id.code_grid);
        grid.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
         nav = (NavigationView)findViewById(R.id.nav);
        nav.setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }
            @Override
            public void onRightClick() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_detail:
                if(!TextUtils.isEmpty(newCloudGoodsId)) {
                    Intent intent = new Intent(this, GoodsDetailActivity.class);
                    intent.putExtra("goodsId", newCloudGoodsId);
                    startActivityForResult(intent, MainActivity.GoodsDetail);
                }
                break;
            case R.id.btn_add_to_cart:
                int[] start_location = new int[2];
                iv_goodspic.getLocationInWindow(start_location);
                ImageView buyImg = new ImageView(this);
                ImageLoader.getInstance().displayImage(img,buyImg, CommonUtils.displayImageOptions);
                int[] end_location = new int[2];
                nav.getRightView().getLocationInWindow(end_location);
                CartUtils.setAnim(this,buyImg, start_location,end_location,cloudGoodsId);// 开始执行动画
                break;
        }
    }
}
