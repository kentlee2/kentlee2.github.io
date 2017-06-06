package com.haoqi.yungou.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class WebGoodsInfoActivity extends FragmentActivity {

    private String goodsId;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);
        webView = (WebView)findViewById(R.id.web_info);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.getAllowFileAccess();
        webSettings.setDomStorageEnabled(true);
        goodsId = getIntent().getStringExtra("goodsId");
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView tb = (NavigationView)findViewById(R.id.tb_info);
        tb.setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }
            @Override
            public void onRightClick() {
            }
        });
    }

    private void getData() {

        RequestParams params = new RequestParams("goodId",goodsId);
        VolleyRequest.post(this, Uriconfig.getDetailImg, params,null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.optString("status");
                    if("0".equals(status)) {
                       String detail = inf.getString("detailImg");
                        webView.loadUrl(Uriconfig.baseUrl+detail);
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
}
