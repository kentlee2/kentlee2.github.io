package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedBackActivity extends Activity {

    private Spinner sp;
    private EditText et_telephone,et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
         sp = (Spinner)findViewById(R.id.spinner);
        Button bt = (Button) findViewById(R.id.btn_submit);
         et_telephone = (EditText) findViewById(R.id.et_telephone);
        et_content = (EditText) findViewById(R.id.et_content);
        String[] strs = getResources().getStringArray(R.array.feedBack);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,strs);
        sp.setAdapter(adapter);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        RequestParams params = new RequestParams();
        params.put("detail",et_content.getText().toString());
        params.put("type",sp.getSelectedItemPosition()+"");
        params.put("tel",et_telephone.getText().toString());
        VolleyRequest.post(this, Uriconfig.feedback, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        GlobalApplication.getInstance().toastShortMsg(R.string.submit_complete);
                        finish();
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
        ((NavigationView)findViewById(R.id.title_bar)).getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
