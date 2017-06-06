package com.haoqi.yungou.Activity.user;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HelpCenterActivity extends Activity {

    private TextView tv_title;
    private TextView tv_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        tv_title = (TextView)findViewById(R.id.title);
        tv_detail = (TextView)findViewById(R.id.detail);
        VolleyRequest.get(this, Uriconfig.help_center, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        JSONArray jsonArray =inf.getJSONArray("about");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String createTime =object.getString("createTime");
                            String title =object.getString("title");
                            String detail =object.getString("Detail");
                            tv_title.setText(title);
                            tv_detail.setText(detail);
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
}
