package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.MyCloundHistoryActivity;
import com.haoqi.yungou.Activity.RechargeActivity;
import com.haoqi.yungou.Activity.ShaidanDetailActivity;
import com.haoqi.yungou.Activity.ShaidanListActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.HomeHotGoodsAdapter;
import com.haoqi.yungou.customView.GoodsLayout;
import com.haoqi.yungou.customView.HomeItemView;
import com.haoqi.yungou.customView.SlideShowView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ImageItem;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.MyImageView;
import com.haoqi.yungou.widget.XScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements XScrollView.IXScrollViewListener, View.OnClickListener {
	private View view;
	private XScrollView mScrollView;
	private HomeItemView homview;
	private SlideShowView slidView;
	private GoodsLayout goodsLayout;
	private HomeHotGoodsAdapter adapter;
	private ArrayList<ImageItem> imgList; //轮播图url List
	private ArrayList<String> idList; //轮播图id List
	private ImageView[] iv_limitGoods;
	private ImageView[] iv_shaidan;
	private TextView[] tv_shaidan;
    private ImageLoader imageLoader;
	private OnHomeItemClickListener listener;
	private ImageView iv_limit_main;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mScrollView = (XScrollView)view.findViewById(R.id.scroll_view);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);//是否加载更多
		mScrollView.setIXScrollViewListener(this);
		mScrollView.setRefreshTime(CommonUtils.getTime());
		View content = LayoutInflater.from(getActivity()).inflate(R.layout.home_content_layout, null);
		if (null != content) {
			initView(content);
		}

		mScrollView.setView(content);
	}

	private void initView(View view) {
		imageLoader =ImageLoader.getInstance();
		homview = (HomeItemView)view.findViewById(R.id.home_view);//最新揭晓
		LinearLayout ll_anounce = (LinearLayout) view.findViewById(R.id.ll_anounce);//最新揭晓
		LinearLayout ll_hot = (LinearLayout) view.findViewById(R.id.ll_hot);//人气
		LinearLayout ll_shaidan = (LinearLayout) view.findViewById(R.id.ll_shaidan);//晒单
		MyImageView btn_new_goods = (MyImageView) view.findViewById(R.id.btn_new_goods);//新品
		MyImageView btn_shaidan = (MyImageView) view.findViewById(R.id.btn_shaidan);//晒单
		MyImageView btn_record = (MyImageView) view.findViewById(R.id.btn_record);//云购记录
		MyImageView btn_recharge = (MyImageView) view.findViewById(R.id.btn_recharge);//充值
		btn_shaidan.setOnClickListener(this);
		btn_new_goods.setOnClickListener(this);
		btn_record.setOnClickListener(this);
		btn_recharge.setOnClickListener(this);
		ll_anounce.setOnClickListener(this);
		ll_hot.setOnClickListener(this);
		ll_shaidan.setOnClickListener(this);
		slidView = (SlideShowView)view.findViewById(R.id.slideshowView);//轮播图
		goodsLayout = (GoodsLayout)view.findViewById(R.id.horezontal_goodsLayout);
		iv_limit_main = (ImageView)view.findViewById(R.id.iv_limit_main);
		iv_limit_main.setOnClickListener(this);
		iv_limitGoods = new ImageView[4];
		iv_limitGoods[0] = (ImageView)view.findViewById(R.id.iv_phone);
		iv_limitGoods[1] = (ImageView)view.findViewById(R.id.iv_gold);
		iv_limitGoods[2] = (ImageView)view.findViewById(R.id.iv_machine);
		iv_limitGoods[3] = (ImageView)view.findViewById(R.id.iv_car);
		iv_limitGoods[0].setOnClickListener(this);
		iv_limitGoods[1].setOnClickListener(this);
		iv_limitGoods[2].setOnClickListener(this);
		iv_limitGoods[3].setOnClickListener(this);
		tv_shaidan = new TextView[4];
		tv_shaidan[0] = (TextView)view.findViewById(R.id.shaidan_title);
		tv_shaidan[1] = (TextView)view.findViewById(R.id.shaidan_title2);
		tv_shaidan[2] = (TextView)view.findViewById(R.id.shaidan_title3);
		tv_shaidan[3] = (TextView)view.findViewById(R.id.shaidan_title4);
		iv_shaidan = new ImageView[4];
		iv_shaidan[0] = (ImageView)view.findViewById(R.id.iv_shaidan);
		iv_shaidan[1] = (ImageView)view.findViewById(R.id.iv_shaidan2);
		iv_shaidan[2] = (ImageView)view.findViewById(R.id.iv_shaidan3);
		iv_shaidan[3] = (ImageView)view.findViewById(R.id.iv_shaidan4);
		iv_shaidan[0].setOnClickListener(this);
		iv_shaidan[1].setOnClickListener(this);
		iv_shaidan[2].setOnClickListener(this);
		iv_shaidan[3].setOnClickListener(this);
		adapter = new HomeHotGoodsAdapter(getActivity());
		getHomeList();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden & homview!=null){
			homview.getAnnounceList(getActivity());
		}
	}

	private void getHomeList() {
		imgList=null;
		idList=null;
		imgList = new ArrayList<ImageItem>();
		idList = new ArrayList<>();
		VolleyRequest.get(getActivity(), Uriconfig.getHomeList,null,new RequestListener() {

			@Override
			public void requestSuccess(String json) {
				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)){
						JSONArray imgArray =inf.getJSONArray("imgs");//首页轮播图
						for(int i=0;i<imgArray.length();i++){
							JSONObject object = (JSONObject) imgArray.get(i);
							String img =object.optString("img");
							String goodId =object.optString("goodId");
							ImageItem item = new ImageItem();
							item.setImageId(goodId);
							item.setImagePath(Uriconfig.baseUrl+img);
							imgList.add(item);
						}
						JSONArray hotGoods = inf.optJSONArray("cloudGoods");//首页人气推荐
						adapter.clear();
						for(int i=0;i<hotGoods.length();i++){
							Map map = new HashMap();
							JSONObject object = (JSONObject) hotGoods.get(i);
							map.put("sumCount",object.optString("sumCount"));
							map.put("joinCount",object.optString("joinCount"));
							map.put("price",object.optString("price"));
							map.put("goodId",object.optString("goodId"));
							map.put("pic", Uriconfig.baseUrl+object.optString("pic"));
							map.put("leftCount",  object.optString("leftCount"));
							adapter.addObject(map);
						}
						slidView.setSlideShow(imgList,getActivity());
						goodsLayout.setAdapter(adapter);
						JSONArray limitImgs = inf.optJSONArray("restrictImgs");
						for(int i=0;i<limitImgs.length();i++){
							JSONObject object = (JSONObject) limitImgs.get(0);
							String img = Uriconfig.baseUrl+object.optString("img");
							imageLoader.displayImage(img, iv_limit_main,CommonUtils.displayImageOptions);
						}
						JSONArray centreImgs = inf.optJSONArray("centreImgs");//限购专区
						for(int i=0;i<centreImgs.length();i++){
							JSONObject object = (JSONObject) centreImgs.get(i);
							String imgs = Uriconfig.baseUrl+object.optString("img");
							String title = object.optString("title");
							imageLoader.displayImage(imgs, iv_limitGoods[i],CommonUtils.displayImageOptions);
						}
						JSONArray shareOrder = inf.optJSONArray("shareOrder");//晒单分享
						for(int i=0;i<shareOrder.length();i++){
							JSONObject object = (JSONObject) shareOrder.get(i);
							String imgs = object.optString("imgs");
							String[] imgArr = imgs.split(",");
							String title = object.optString("title");
							String shareId = object.optString("shareOrderId");
							if(i<4) {
								tv_shaidan[i].setText(title);
								iv_shaidan[i].setTag(shareId);
								imageLoader.displayImage(Uriconfig.baseUrl + imgArr[0], iv_shaidan[i], CommonUtils.displayImageOptions);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			@Override
			public void requestError(VolleyError e) {
				Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onRefresh() {//下拉刷新
		onLoad();
		getHomeList();
		homview.getAnnounceList(getActivity());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoadMore() {//加载更多
		onLoad();
	}
	private void onLoad() {
		mScrollView.stopRefresh();
		mScrollView.stopLoadMore();
		mScrollView.setRefreshTime(CommonUtils.getTime());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_shaidan:
			case R.id.iv_shaidan2:
			case R.id.iv_shaidan3:
			case R.id.iv_shaidan4:
				if(v.getTag()!=null) {
					String id = v.getTag().toString();
					startActivity(new Intent(getActivity(), ShaidanDetailActivity.class).putExtra("id", id));
				}
				break;
			case R.id.ll_shaidan://晒单
			case R.id.btn_shaidan:
				startActivity(new Intent(getActivity(),ShaidanListActivity.class));
				break;
			case R.id.btn_record:
				startActivity(new Intent(getActivity(),MyCloundHistoryActivity.class));
				break;
			case R.id.btn_recharge:
				startActivity(new Intent(getActivity(),RechargeActivity.class));
				break;
			default:
				listener.OnHomeItemClick(v);
				break;
		}
	}

	public interface OnHomeItemClickListener {
		void OnHomeItemClick(View v);
	}
     public void setOnHomeItemClickListener(OnHomeItemClickListener listener) {
		 this.listener=listener;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


}
