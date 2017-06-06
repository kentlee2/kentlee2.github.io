package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.android.volley.VolleyError;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.EmptyView;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaidanCommontActivity extends Activity implements XListView.IXListViewListener {

    private XListView listView;
    private SimpleAdapter adapter;
    private List<Map<String,String>> list = new ArrayList<>();
    private ImageLoader imageLoader;
    private int page=1;
    private EmptyView emptyview;
    private Button sendBtn;
    private String shareId;
    private EditText et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaidan_commont);
        shareId = getIntent().getStringExtra("shareId");
        listView = (XListView)findViewById(R.id.listview_comment);
        emptyview = (EmptyView)findViewById(R.id.emptyview);
         et_content = (EditText) findViewById(R.id.et_content);
        sendBtn = (Button)findViewById(R.id.comment_send);
        adapter = new SimpleAdapter(this,list,R.layout.item_comment,new String[]{"pic","username","date","content"},
                                           new int[]{R.id.iv_accountpic,R.id.tv_accountname,R.id.tv_date,R.id.tv_content});
        imageLoader = ImageLoader.getInstance();
        listView.setAdapter(adapter);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);
        listView.setAutoLoadEnable(true);
        listView.setXListViewListener(this);
        getComment();
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView){
                    imageLoader.displayImage(data.toString(),(ImageView)view, CommonUtils.circleImageOptions);
                    return true;
                }
                return false;
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendComment(et_content);
            }
        });
        emptyview.setRetryListener(new EmptyView.RetryListener() {
            @Override
            public void retry() {
                page=1;
                emptyview.setVisibility(View.GONE);
                getComment();
            }
        });
    }

    private void sendComment(final EditText et) {
        RequestParams params = new RequestParams();
        params.put("shareId",shareId);
        params.put("userId", UserUtils.getUserId());
        params.put("detail", et.getText().toString());
        VolleyRequest.post(this, Uriconfig.send_comment, params, new RequestListener() {

            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        et.setText("");
                        page=1;
                        list.clear();
                        adapter.notifyDataSetChanged();
                        getComment();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(ShaidanCommontActivity.this,e.toString());
            }
        });
    }

    private void getComment() {
        RequestParams params = new RequestParams();
        params.put("shareId",shareId);
        params.put("firstIndex",page+"");
        VolleyRequest.post(this, Uriconfig.all_comment, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        if(inf.optString("isPageEnd").equals("1")){
                            listView.setPullLoadEnable(false);
                            emptyview.setVisibility(View.GONE);
                        }
                            JSONArray commentList = inf.optJSONArray("commentList");
                        for(int i=0;i<commentList.length();i++){
                            JSONObject object = commentList.optJSONObject(i);
                            Map<String,String> map = new HashMap<String, String>();
                            map.put("pic",Uriconfig.baseUrl+object.optString("headImg"));
                            map.put("username",object.optString("username"));
                            map.put("date",object.optString("createTime"));
                            map.put("content",object.optString("content"));
                            list.add(map);
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        emptyview.setVisibility(View.VISIBLE);
                        emptyview.setNoDataView();
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(ShaidanCommontActivity.this,e.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView nav = (NavigationView)findViewById(R.id.title_bar);
        nav.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        onLoad();
        page++;
        getComment();
    }
    private void onLoad() {
        listView.stopRefresh();
        listView.setRefreshTime(CommonUtils.getTime());
    }
}
