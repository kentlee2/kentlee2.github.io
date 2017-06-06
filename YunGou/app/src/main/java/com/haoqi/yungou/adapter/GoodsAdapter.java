package com.haoqi.yungou.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoqi.yungou.fragment.GoodsFragment;
import com.haoqi.yungou.R;

import java.util.ArrayList;
import java.util.Map;

public class GoodsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Map> strings;
	public static int mPosition;
	
	public GoodsAdapter(Context context, ArrayList<Map> strings){
		this.context =context;
		this.strings = strings;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return strings.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return strings.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
		TextView tv = (TextView) convertView.findViewById(R.id.tv);
		mPosition = position;
		tv.setText(strings.get(position).get("typeName").toString());
		if (position == GoodsFragment.mPosition) {
			convertView.setBackgroundResource(R.drawable.tongcheng_all_bg01);
		} else {
			convertView.setBackgroundColor(Color.parseColor("#f4f4f4"));
		}
		return convertView;
	}
}
