package com.haoqi.yungou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoqi.yungou.R;

import java.util.List;

/**
 * Created by Kentlee on 2016/10/24.
 */
public class CodeGridAdapter extends ArrayAdapter<String> {
    private final List<String> list;
    private final int mResourceId;
    private final LayoutInflater inflater;

    public CodeGridAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.mResourceId = resource;
         inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(mResourceId, null);
        TextView tv_code = (TextView) view.findViewById(R.id.tv_code);
        String code = getItem(position);
        tv_code.setText(code);
        return view;
    }
}
