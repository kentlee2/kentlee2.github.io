package com.haoqi.yungou.Activity.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutActivity extends Activity {

    private WebView webView;
    private boolean isAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
         webView = (WebView)findViewById(R.id.about_webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.getAllowFileAccess();
        webSettings.setDomStorageEnabled(true);
         isAbout = getIntent().getBooleanExtra("aboutus",false);
        if(isAbout){
            getData(Uriconfig.about_us);
        }else{
            getData(Uriconfig.service_agreement);
        }
    }

    private void getData(String url) {
        VolleyRequest.get(this, url, new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.optJSONObject("inf");
                    JSONObject	res = jsonobject.optJSONObject("res");
                    String status = res.optString("status");
                    if("0".equals(status)) {
                        JSONArray array = inf.optJSONArray("about");
                        for(int i=0;i<array.length();i++){
                            JSONObject jsonObject = array.optJSONObject(i);
                            String detail = jsonObject.optString("Detail");
                            webView.loadDataWithBaseURL(Uriconfig.baseUrl, detail, "text/html", "UTF-8", null);
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

    @Override
    protected void onStart() {
        super.onStart();
       NavigationView titlBar = (NavigationView)findViewById(R.id.title_bar);
        if(isAbout){
            titlBar.setTitle(R.string.about);
        }else{
            titlBar.setTitle(R.string.service_agreement);
        }
        titlBar.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
