package com.haoqi.yungou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.SearchListActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kentlee on 2016/9/23.
 */
public class RecentSearchFragment extends Fragment {
    private View view;
    private ArrayList list;
    private ArrayAdapter<String> adapter;
    private Button btn_delete;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_histroy_list, null);
        initView();
        return view;
    }

    private void initView() {
        ListView listView = (ListView) view.findViewById(R.id.lv_histroy_search);
         btn_delete = (Button) view.findViewById(R.id.btn_delete_record);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.item_histroy_search,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getActivity(),SearchListActivity.class);
                intent.putExtra("str",item);
                startActivity(intent);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        getData();
    }

    private void delete() {
        RequestParams params = new RequestParams("userId", UserUtils.getUserId());
        VolleyRequest.post(getActivity(), Uriconfig.delete_search, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        if(adapter!=null) {
                            list.clear();
                            adapter.notifyDataSetChanged();
                            btn_delete.setVisibility(View.GONE);
                            ToastUtils.showShort(getActivity(),"清除成功");
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

    private void getData() {
        RequestParams params = new RequestParams("userId", UserUtils.getUserId());
        VolleyRequest.post(getActivity(), Uriconfig.recent_search, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        JSONArray array = inf.getJSONArray("content");
                        if(array.length()>0) {
                            btn_delete.setVisibility(View.VISIBLE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                list.add(object.getString("content"));
                            }
                            adapter.notifyDataSetChanged();
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
