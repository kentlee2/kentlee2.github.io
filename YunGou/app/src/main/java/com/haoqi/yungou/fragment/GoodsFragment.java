package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.GoodsDetailActivity;
import com.haoqi.yungou.Activity.MainActivity;
import com.haoqi.yungou.Activity.SearchActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.GoodsAdapter;
import com.haoqi.yungou.adapter.GoodsTypeAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有商品
 */
public class GoodsFragment extends Fragment implements AdapterView.OnItemClickListener, XListView.IXListViewListener, View.OnClickListener {
	private View view;
	private NavigationView navigationView;
	private ListView listView;
	private GoodsAdapter adapter;
	public static int mPosition;
    private ArrayList<Map> typeList;
	private XListView mListView;
	private int page=1;
	private ArrayList<Map<String,Object>> dataList = new ArrayList<>() ;
	private GoodsTypeAdapter goodsAdapter;
	private TextView tv_anounce,tv_pop,tv_newest,tv_price;
	private ImageView classify_price;
	private LinearLayout ll_price;
	private String goodsId;
	private int index=0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 super.onCreateView(inflater, container, savedInstanceState);
		 view = inflater.inflate(R.layout.fragment_goods, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		navigationView = (NavigationView) view.findViewById(R.id.nav_main);
		initTypeList();
		getData();
		initTopText();
	}
	boolean isPressed = true;
	@Override
	public void onClick(View v) {
		setDeafault();
		page=1;
		dataList.clear();
		goodsAdapter.notifyDataSetChanged();
		switch (v.getId()){
			case R.id.tv_anounce:
				index=0;
				getAnnounceGoods();
				tv_anounce.setTextColor(getResources().getColor(R.color.red_text));
				break;
			case R.id.tv_pop:
				index=1;
				getAnnounceGoods();
				tv_pop.setTextColor(getResources().getColor(R.color.red_text));
				break;
			case R.id.tv_newest:
				index=2;
				getAnnounceGoods();
				tv_newest.setTextColor(getResources().getColor(R.color.red_text));
				break;
			case R.id.ll_price:
				tv_price.setTextColor(getResources().getColor(R.color.red_text));
				if(isPressed){
					classify_price.setBackgroundResource(R.drawable.classify_price_up);
					index=3;
					getAnnounceGoods();//价值升序
					isPressed =false;
				}else {
					classify_price.setBackgroundResource(R.drawable.classify_price_down);
					index=4;
					getAnnounceGoods();//价值降序
					isPressed =true;
				}
				break;
		}

	}
	private void setDeafault(){
		tv_anounce.setTextColor(getResources().getColor(R.color.black_text));
		tv_pop.setTextColor(getResources().getColor(R.color.black_text));
		tv_newest.setTextColor(getResources().getColor(R.color.black_text));
		tv_price.setTextColor(getResources().getColor(R.color.black_text));
		classify_price.setBackgroundResource(R.drawable.classify_price_default);
	}

	private void initTopText() {
		tv_anounce = (TextView) view.findViewById(R.id.tv_anounce);
		tv_pop = (TextView) view.findViewById(R.id.tv_pop);
		tv_newest = (TextView) view.findViewById(R.id.tv_newest);
		ll_price = (LinearLayout) view.findViewById(R.id.ll_price);
		tv_price = (TextView)view.findViewById(R.id.tv_price);
		classify_price = (ImageView) view.findViewById(R.id.classify_price);
		classify_price.setOnClickListener(this);
		ll_price.setOnClickListener(this);
		tv_anounce.setOnClickListener(this);
		tv_newest.setOnClickListener(this);
		tv_pop.setOnClickListener(this);

	}

	//类别列表
	private void initTypeList() {
		listView = (ListView)view.findViewById(R.id.goods_typeList);
		typeList = new ArrayList<>();
		adapter = new GoodsAdapter(getActivity(), typeList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		initGoodsList();
	}
   //商品列表
	private void initGoodsList() {
		mListView = (XListView)view.findViewById(R.id.goodsList);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setAutoLoadEnable(true);
		goodsAdapter = new GoodsTypeAdapter(getActivity(),dataList);
		mListView.setAdapter(goodsAdapter);
		getAnnounceGoods();
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
				Map map = (Map) parent.getItemAtPosition(position);
				intent.putExtra("goodsId",map.get("cloudGoodId").toString());
				startActivityForResult(intent, MainActivity.GoodsDetail);
			}
		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == getActivity().RESULT_OK && requestCode == MainActivity.GoodsDetail){
			((MainActivity)getActivity()).setTabSelection(3);
		}
	}
	private void getData() {
		VolleyRequest.get(getActivity(), Uriconfig.GoodsTypeList, new RequestListener() {
			@Override
			public void requestSuccess(String json) {

				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject	res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)){
						JSONArray list = inf.getJSONArray("typeList");
						for(int i=0;i<list.length();i++){
							Map map = new HashMap();
							JSONObject jsobject = (JSONObject) list.get(i);
							String typeName =jsobject.getString("typeName");
							String goodsId = jsobject.getString("id");
							map.put("typeName",typeName);
							map.put("goodsId",goodsId);
							typeList.add(map);
						}
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void requestError(VolleyError e) {
				ToastUtils.showShort(getActivity(),e.toString());
			}
		});
	}
	public void getAnnounceGoods() {//默认开始显示即将揭晓
		String msg ="加载中...";
		RequestParams params = new RequestParams();
		params.put("showType", index + "");
		params.put("goodsTypeId", goodsId);
		params.put("firstIndex", page+"");
		VolleyRequest.post(getActivity(), Uriconfig.getGoodsList, params,msg, new RequestListener() {
			@Override
			public void requestSuccess(String json) {
				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject	res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)) {
						onLoad();
						if(inf.optString("isPageEnd").equals("1")){
							mListView.setPullLoadEnable(false);
						}else{
							mListView.setPullLoadEnable(true);
						}
						JSONArray list = inf.getJSONArray("goodList");
						for(int i=0;i<list.length();i++){
							Map map = new HashMap();
							JSONObject jsobject = list.optJSONObject(i);
							map.put("sumCount",jsobject.getString("sumCount"));
							map.put("cloudGoodId",jsobject.getString("cloudGoodId"));
							map.put("joinCount", jsobject.getString("joinCount"));
//                            map.put("leftCount",jsobject.getString("leftCount"));
							map.put("price",jsobject.getString("price"));
							map.put("goodId",jsobject.getString("goodId"));
							map.put("cloudNo",jsobject.getString("cloudNo"));
							map.put("pic",Uriconfig.baseUrl+jsobject.getString("pic"));
							map.put("goodsName",jsobject.getString("goodsName"));
							map.put("limitBuy",jsobject.getString("limitBuy"));//是否限购   0否  1是
							dataList.add(map);
						}

						goodsAdapter.notifyDataSetChanged();
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
	public void onStart() {
		super.onStart();
		navigationView.setTitle("所有商品");
		navigationView.getRightView().setImageResource(R.drawable.title_search);
		navigationView.getRightView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//拿到当前位置
		mPosition = position;
		//当前页置1
		page=1;
		//即时刷新adapter
		adapter.notifyDataSetChanged();
//		for (int i = 0; i < typeList.size(); i++) {
//			GoodsTypListFragment typFragment = new GoodsTypListFragment();
//			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//			fragmentTransaction.replace(R.id.fragment_container, typFragment);
//			Bundle bundle = new Bundle();
//			bundle.putString(GoodsTypListFragment.TAG, typeList.get(position).get("goodsId").toString());
//			typFragment.setArguments(bundle);
//			fragmentTransaction.commit();
//		}
		 goodsId=typeList.get(position).get("goodsId").toString();
		dataList.clear();

		goodsAdapter.notifyDataSetChanged();
		getAnnounceGoods();
		navigationView.getTitleView().setText(typeList.get(position).get("typeName").toString());
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		page++;
		getAnnounceGoods();
	}
	private void onLoad() {
		mListView.stopRefresh();
		mListView.setRefreshTime(CommonUtils.getTime());
	}


}
