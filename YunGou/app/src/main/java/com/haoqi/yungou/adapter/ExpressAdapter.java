package com.haoqi.yungou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoqi.yungou.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Kentlee on 2016/10/25.
 */
public class ExpressAdapter extends ArrayAdapter<Map<String,String>> {
    private final LayoutInflater inflater;
    private final int mResourceId;
    private final Context context;

    public ExpressAdapter(Context context, int resource, ArrayList<Map<String, String>> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        mResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(mResourceId, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        Map<String,String> map = (Map) getItem(position);
        if(position==0){
            tv_title.setTextColor(context.getResources().getColor(R.color.cyan_text));
            tv_time.setTextColor(context.getResources().getColor(R.color.cyan_text));
        }else{
            tv_title.setTextColor(context.getResources().getColor(R.color.black_text));
            tv_time.setTextColor(context.getResources().getColor(R.color.black_text));
        }
        tv_title.setText(map.get("title"));
        tv_time.setText(map.get("time"));
        return view;
    }
}
