package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.JsonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShaidanDetailActivity extends Activity {

    private ImageView iv_accountpic;
    private TextView tv_accountname,tv_buytimes,tv_obtained_goods,tv_title,tv_date,tv_content,tv_flag_fufen;
    private String shareId;
    private ImageLoader imageLoader;
    private LinearLayout layout_commentpic;
    private RelativeLayout rl_comment,rl_envy;
    private TextView tv_envy,tv_comment,tv_envy_anim;
    private Map map;
    private Animation animation;
    private int praisNum;
    private  ArrayList<Map<String,Object>> praiseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaidan_detail);
        map = (Map) getIntent().getSerializableExtra("map");
        animation= AnimationUtils.loadAnimation(this,R.anim.praise);
        initView();
        getContent();
    }
    private void initView() {
        praiseList=  new ArrayList<>();
        tv_flag_fufen = (TextView)findViewById(R.id.tv_flag_fufen);
        iv_accountpic = (ImageView)findViewById(R.id.iv_accountpic);
        tv_accountname = (TextView)findViewById(R.id.tv_accountname);
        tv_buytimes = (TextView)findViewById(R.id.tv_buytimes);
        tv_obtained_goods = (TextView)findViewById(R.id.tv_obtained_goods);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_date = (TextView)findViewById(R.id.tv_date);
        tv_content = (TextView)findViewById(R.id.tv_content);
        tv_envy = (TextView)findViewById(R.id.tv_envy);
        tv_comment = (TextView)findViewById(R.id.tv_comment);
        tv_envy_anim = (TextView)findViewById(R.id.tv_envy_anim);
        layout_commentpic = (LinearLayout)findViewById(R.id.layout_commentpic);
        rl_comment = (RelativeLayout)findViewById(R.id.rl_comment);
        rl_envy = (RelativeLayout)findViewById(R.id.rl_envy);
        if(map!=null) {
            shareId = map.get("shareId").toString();
            tv_envy.setText(map.get("likely")+"");
            tv_comment.setText(map.get("comments")+"");
        }else{
            shareId = getIntent().getStringExtra("id");//从首页进来
        }
        rl_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShaidanDetailActivity.this, ShaidanCommontActivity.class);
                intent.putExtra("shareId",shareId);
                startActivityForResult(intent,0);
            }
        });
        rl_envy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPraise = DBManager.getInstance().isPraise(shareId);
                if(UserUtils.isLogined()) {
                    if (!isPraise) {
                        envy();
                    } else {
                        ToastUtils.showShort(ShaidanDetailActivity.this, "您已点过赞");
                    }
                }else{
                    ToastUtils.showShort(ShaidanDetailActivity.this, "请登录后再试");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            getContent();
        }
    }

    private void getContent() {
        imageLoader = ImageLoader.getInstance();
        RequestParams params = new RequestParams();
        params.put("id",shareId);
        VolleyRequest.post(this, Uriconfig.shaidan_detail, params, null,new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        String headImg = Uriconfig.baseUrl+inf.optString("img");//晒单人的头像
                        imageLoader.displayImage(headImg,iv_accountpic, CommonUtils.circleImageOptions);
                        String joindes = "本云参与:"+inf.optString("joinCount")+"人次";
                        tv_accountname.setText(inf.optString("username"));
                        String cloundNo = inf.optString("cloudNo");
                        tv_obtained_goods.setText("(第"+cloundNo+"云)"+inf.optString("goodsName"));
                        tv_flag_fufen.setText("奖励积分\n"+inf.optString("score"));
                        tv_title.setText(inf.optString("title"));
                        tv_date.setText(inf.optString("createTime"));
                        tv_content.setText(inf.optString("detail"));
                        praisNum= inf.optInt("likely");
                        tv_envy.setText(praisNum+"");
                        tv_comment.setText(inf.optString("comment"));
                        CommonUtils.setSpanableText(tv_buytimes,joindes,getResources().getColor(R.color.red_text),5,joindes.length()-2);
                        JSONArray arrays = inf.optJSONArray("imgs");
                        for(int i=0;i<arrays.length();i++){
                            ImageView image = new ImageView(ShaidanDetailActivity.this);
                            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            image.setAdjustViewBounds(true);
                            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                            mParams.bottomMargin=10;
                            image.setLayoutParams(mParams);
                            layout_commentpic.addView(image);
                            String shaidanImg = Uriconfig.baseUrl+arrays.opt(i);
                            imageLoader.displayImage(shaidanImg,image, CommonUtils.displayImageOptions);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {

            }
        });

    }
    private void envy() {

        RequestParams params = new RequestParams("shareId",shareId);
        VolleyRequest.post(this, Uriconfig.praise, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if (status.equals("0")) {
                        tv_envy.setText(praisNum+1+"");
                        tv_envy_anim.setVisibility(View.VISIBLE);
                        tv_envy_anim.startAnimation(animation);
                        Map<String,Object> maps = new HashMap();
                        maps.put(shareId,true);
                        praiseList.add(maps);
                        DBManager.getInstance().updateUserInfo(Constant.PRAISE, JsonUtils.toJson(praiseList));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv_envy_anim.setVisibility(View.GONE);
                            }
                        },1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(ShaidanDetailActivity.this,e.toString());
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        ((NavigationView)findViewById(R.id.title_bar)).getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
