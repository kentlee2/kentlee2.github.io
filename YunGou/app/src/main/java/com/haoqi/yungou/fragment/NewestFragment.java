package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.GoodsDetailActivity;
import com.haoqi.yungou.Activity.MainActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.NewestAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ToastUtils;
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

/**
 * 最新揭晓
 */
public class NewestFragment extends Fragment implements XListView.IXListViewListener {
	private String TAG ="NewestFragment";
	private View view;
	private NavigationView navigationView;
	private PopupWindow pop;
	private CheckBox[] cb;
	private ImageView[] iv;
	private XListView mListView;
    private ArrayList<Map<String,Object>> list = new ArrayList<>();;
	private NewestAdapter adapter;
	private ImageLoader imagLoader;
	private List<View> viewList  = new ArrayList<>();
	private boolean isHeaderRefresh =true;//是否显示最新揭晓
	private int firstIndex=1;
	private LayoutInflater inflate;
	private String selectId="";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_recent, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		imagLoader = ImageLoader.getInstance();
		initView();
        getData(selectId);
	}



	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
//			getNewestList();
		}
	}

	private void initView() {
		mListView = (XListView) view.findViewById(R.id.newest_list);
		setPopupWindow();
		mListView.setPullRefreshEnable(false);
		mListView.setAutoLoadEnable(true);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		adapter = new NewestAdapter(getActivity(),list);
		mListView.setAdapter(adapter);
		inflate = getActivity().getLayoutInflater();
		navigationView = (NavigationView)view.findViewById(R.id.nav_main) ;
		navigationView.setTitle("最新揭晓");
		navigationView.getRightView().setVisibility(View.GONE);
		navigationView.setClickCallback(new NavigationView.ClickCallback() {
			@Override
			public void onBackClick() {

			}

			@Override
			public void onRightClick() {
				pop.showAsDropDown(navigationView);
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
				Map map = (Map) parent.getItemAtPosition(position);
				intent.putExtra("goodsId",map.get("cloudGoodsId").toString());
				startActivityForResult(intent, MainActivity.GoodsDetail);
			}
		});
	}
	private void getData(String id) {//揭晓详情
		RequestParams params = new RequestParams();
		params.put("goodsTypeId",id);
		params.put("firstIndex",firstIndex+"");
		VolleyRequest.post(getActivity(), Uriconfig.History_JxList,params,null,new RequestListener() {
			@Override
			public void requestSuccess(String json) {

				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject	res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)) {
						if(inf.optString("isPageEnd").equals("1")){
							mListView.setPullLoadEnable(false);
						}
						JSONArray arrays = inf.getJSONArray("cloudGoodsList");
						for(int i=0;i<arrays.length();i++){
							JSONObject object = arrays.getJSONObject(i);
							object.getString("goodsId");
							String isAnnounce = object.optString("status");
							String goodsImg = Uriconfig.baseUrl+object.optString("pic");
							String headImg = Uriconfig.baseUrl+object.optString("headImg");
							String userId = object.optString("userId");
							String joinCount = object.optString("joinCount");//参加本云次数
							String username =object.optString("username");//获奖用户名
							String cloudGoodsId = object.optString("cloudGoodsId");
							object.optString("cloudNo");
							String time = object.optString("publicTime");//揭晓时间
							String price =object.optString("price");//价值
							String goodsName =object.optString("goodsName");//商品名
							Map map = new HashMap();
							map.put("goodsImg",goodsImg);
							map.put("cloudGoodsId",cloudGoodsId);
							map.put("time",time);
							map.put("price",price);
							map.put("goodsName",goodsName);
							map.put("joinCount",joinCount);
							map.put("username",username);
							map.put("headImg",headImg);
							map.put("userId",userId);
							list.add(map);
						}
						adapter.notifyDataSetChanged();
						if(isHeaderRefresh)
						getNewestList();
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
	private void getNewestList() {//头部正在揭晓信息
		VolleyRequest.get(getActivity(), Uriconfig.NewestList,new RequestListener() {
			@Override
			public void requestSuccess(String json) {

				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject	res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)) {
//						String isPageEnd = inf.getString("isPageEnd");
						JSONArray arrays = inf.getJSONArray("cloudGoodsList");
						for(int i=0;i<arrays.length();i++) {
							JSONObject object = arrays.getJSONObject(i);
							String isAnounce = object.optString("status");
							if (isAnounce.equals("1")) {
								String count = object.getString("sumCount");//需要总人数
								String time = object.getString("publicTime");//揭晓事件
								String goodsId = object.getString("goodsId");
								String price = object.getString("price");//价值
								String pic = object.getString("pic");//图片路径
								String goodsName = object.getString("goodsName");
								final String cloudGoodsId = object.getString("cloudGoodsId");
								String cloudsNo = object.getString("cloudNo");
								 View view = inflate.inflate(R.layout.item_newest_header, null);
								final ImageView goodsPic = (ImageView) view.findViewById(R.id.iv_goodspic);
								TextView tv_goodsName = (TextView) view.findViewById(R.id.tv_goodsName);
								TextView tv_price = (TextView) view.findViewById(R.id.tv_price);
								if (!TextUtils.isEmpty(pic))
									imagLoader.displayImage(Uriconfig.baseUrl + pic, goodsPic, CommonUtils.displayImageOptions);
								tv_goodsName.setText("(第" + cloudsNo + "云)" + goodsName);
								tv_price.setText("价值:￥" + price);
								AnnounceButton buyView = (AnnounceButton) view.findViewById(R.id.jx_btn);
								buyView.setTime(time);
								buyView.start();
								viewList.add(view);
								mListView.addHeaderView(view);
								buyView.setOnFinishListener(new AnnounceButton.OnFinished() {
									@Override
									public void onfinish(View v) {
										removeHeaders((View)v.getParent());
										getAnnounceInfo(cloudGoodsId);//获得该商品的揭晓信息
									}
								});
								view.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
										intent.putExtra("goodsId",cloudGoodsId);
										startActivityForResult(intent, MainActivity.GoodsDetail);
									}
								});
							}
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

	private void removeHeaders(View v) {
		for(int i=0;i<mListView.getHeaderViewsCount();i++) {
			mListView.removeHeaderView(v);
		}
	}
	private void getAnnounceInfo(final String cloudGoodsId) {
		RequestParams params = new RequestParams();
		params.put("goodId",cloudGoodsId);
		VolleyRequest.post(getActivity(), Uriconfig.getPublicDetail, params,new RequestListener() {

			@Override
			public void requestSuccess(String json) {

				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject res = jsonobject.getJSONObject("res");
					String status = res.optString("status");
					if("0".equals(status)) {
						String isAnnounce = inf.optString("status"); //0进行中   1等待揭晓   2已经揭晓
						String headImg = Uriconfig.baseUrl + inf.optString("headImg");
						String goodsImg = Uriconfig.baseUrl + inf.optString("pic");
						Map map = new HashMap();
						map.put("goodsImg",goodsImg);
						map.put("cloudGoodsId",cloudGoodsId);
						map.put("time",inf.optString("publicTime"));
						map.put("price",inf.optString("price"));
						map.put("goodsName",inf.optString("goodName"));
						map.put("joinCount",inf.optString("allJoinCount"));
						map.put("username",inf.optString("username"));
						map.put("headImg",headImg);
						map.put("userId",inf.optString("userId"));
						list.add(0,map);
						adapter.notifyDataSetChanged();
					}else{
						ToastUtils.showShort(getActivity(),res.optString("errMsg"));
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
	private void setPopupWindow() {
		final ArrayList<Map<String,Object>> dataList = new ArrayList();
		View contentView =getActivity().getLayoutInflater().inflate(R.layout.anounce_type_list,null);
		pop = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_stroke_normal));
		ListView listView = (ListView) contentView.findViewById(R.id.anounce_list);
		final MySimpleAdapter simAdapter = new MySimpleAdapter(getActivity(),dataList,R.layout.item_announce_type,new String[]{"img","typeName"},
				new int[]{R.id.sort0,R.id.all});
		listView.setAdapter(simAdapter);
		final int[] imageArr = {R.drawable._sort0_state_bg,R.drawable._sort104_state_bg,R.drawable._sort100_state_bg,R.drawable._sort106_state_bg,
				R.drawable._sort222_state_bg, R.drawable._sort2_state_bg,R.drawable._sort312_state_bg,R.drawable._sort312_state_bg,R.drawable._sort312_state_bg};
		pop.setFocusable(true);
		pop.update();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map map = (Map) parent.getItemAtPosition(position);
				String selectId = map.get("goodsId").toString();
				for(Map hashMap : dataList){//全部设为未选中
					hashMap.put("check",false);
				}
				map.put("check",true);
				dataList.set(position,map);
				simAdapter.notifyDataSetChanged();
				navigationView.getRightText().setText(map.get("typeName").toString());
				list.clear();
				getData(selectId);
				pop.dismiss();
			}
		});
//		simAdapter.setPop(pop);
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
							String pic = jsobject.optString("pic");
							map.put("img",imageArr[i]);
							map.put("typeName",typeName);
							map.put("goodsId",goodsId);
							if(i==0){
								map.put("check",true);
							}else{
								map.put("check",false);
							}
							dataList.add(map);
						}
						simAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void requestError(VolleyError e) {
			}
		});
	}
	class MySimpleAdapter extends SimpleAdapter{
		public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view =super.getView(position, convertView, parent);
			CheckBox cb = (CheckBox) view.findViewById(R.id.chebox);
			ImageView iv = (ImageView) view.findViewById(R.id.sort0);
             Map map = (Map) getItem(position);
			if((boolean)map.get("check")){
				cb.setChecked(true);
				iv.setSelected(true);
			}else{
				cb.setChecked(false);
				iv.setSelected(false);
			}
			return view;
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		firstIndex++;
		isHeaderRefresh=false;
		getData(selectId);
		onLoad();
	}
	private void onLoad() {
		mListView.stopRefresh();
		mListView.setRefreshTime(CommonUtils.getTime());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG,"onDestroy");
	}
}
