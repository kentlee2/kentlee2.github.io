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
import android.widget.GridView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.SearchListActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kentlee on 2016/9/23.
 */
public class HotSearchFragment extends Fragment {
    private View view;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private boolean isFirstVisible=true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_grid, null);
        return view;
    }

    private void initView() {
        list = new ArrayList<String>();
        GridView gv_search = (GridView) view.findViewById(R.id.gv_search);
         adapter = new ArrayAdapter<String>(getActivity(),R.layout.item_hot_search,list);
        gv_search.setAdapter(adapter);
        getHotSearch();
        gv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Intent intent = new Intent(getActivity(),SearchListActivity.class);
                intent.putExtra("str",item);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isFirstVisible){
            isFirstVisible=false;
            initView();
        }
    }


    private void getHotSearch() {
        VolleyRequest.get(getActivity(), Uriconfig.hot_search, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                      JSONArray arrays =  inf.getJSONArray("hotSelect");
                        for(int i=0;i<arrays.length();i++){
                            JSONObject object = arrays.getJSONObject(i);
                            list.add(object.getString("content"));
                        }
                        adapter.notifyDataSetChanged();
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
