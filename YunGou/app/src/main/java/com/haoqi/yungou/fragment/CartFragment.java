package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.user.LoginActivity;
import com.haoqi.yungou.Activity.MainActivity;
import com.haoqi.yungou.Activity.PayActivity;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.CartAdapter;
import com.haoqi.yungou.customView.EmptyView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
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
 * 购物车列表
 */
public class CartFragment extends Fragment implements XListView.IXListViewListener{
	private View view;
	private XListView mListView;
	private ArrayList<Map> cartList ;
	private CartAdapter cartAdapter;
	private TextView tv_num;
	private TextView tv_total;
	private String sum="";
	private RelativeLayout rl_js;
	private EmptyView emp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Button btn_recharge = (Button) view.findViewById(R.id.go_recharge);
		mListView = (XListView)view.findViewById(R.id.cart_list);
		tv_num = (TextView)view.findViewById(R.id.goods_num);
		tv_total = (TextView)view.findViewById(R.id.total_price);
		rl_js = (RelativeLayout)view.findViewById(R.id.rl_js);
		 emp = (EmptyView)view.findViewById(R.id.emptyView);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(false);
		btn_recharge.setOnClickListener(myOnclicklistener);
		getActivity().registerReceiver(priceReceiver,new IntentFilter(Constant.CART_CHANGED));
		getCart();
	}


	BroadcastReceiver priceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
             switch (intent.getAction()){
				 case Constant.CART_CHANGED:
					 getCart();
					 break;
			 }
		}
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			getCart();
		}
	}

	View.OnClickListener myOnclicklistener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ArrayList<String>  idList = new ArrayList<>();
			for(Map map : cartList){
				idList.add( map.get("shoppingCarId").toString());
			 }
			setUserVisibleHint(false);
			startActivity(new Intent(getActivity(), PayActivity.class).putExtra("cartId",idList));
		}
	};
	private void getCart() {
		cartAdapter=null;
		cartList= new ArrayList<>();
		RequestParams params = new RequestParams("userId", UserUtils.getUserId());
		VolleyRequest.post(getActivity(), Uriconfig.cart_list, params,new RequestListener() {
			@Override
			public void requestSuccess(String json) {
				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject	res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)){
						JSONArray array = inf.getJSONArray("shoppingList");
						if(array.length()>0) {
							hideEmptyView();
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								Map map = new HashMap();
								map.put("goodName", object.getString("goodName"));
								map.put("goodCloudNo", object.getString("goodCloudNo"));//第几云
								map.put("count", object.getString("count"));//商品数量
								map.put("goodLeftCount", object.getString("goodLeftCount"));//剩余人数
								map.put("shoppingCarId", object.getString("shoppingCarId"));
								map.put("cloudGoodsId", object.getString("cloudGoodsId"));
								map.put("goodPic", Uriconfig.baseUrl + object.getString("goodPic"));//图片
								map.put("goodPrice", object.getString("goodPrice"));//商品价格
								cartList.add(map);
							}
							cartAdapter = new CartAdapter(getActivity(),cartList);
							mListView.setAdapter(cartAdapter);
							cartAdapter.notifyDataSetChanged();
							String count = inf.getString("shoppingCount");//购物车数量
							DBManager.getInstance().updateUserInfo(Constant.CART,count);
							((MainActivity)getActivity()).getCartCount();
							sum = inf.getString("sum");
							rl_js.setVisibility(View.VISIBLE);
							SpannableString spanString = new SpannableString("共" + count + "件商品，合计");
							ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.red_text));
							spanString.setSpan(span, 1, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
							tv_num.setText(spanString);
							tv_total.setText(sum);
						}else{
							DBManager.getInstance().updateUserInfo(Constant.CART,"0");
							((MainActivity)getActivity()).getCartCount();
							setEmptyView();
							rl_js.setVisibility(View.GONE);
						}
					}else{
						ToastUtils.showShort(getActivity(),res.toString());
						setEmptyView();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					setEmptyView();
				}
			}

			@Override
			public void requestError(VolleyError e) {
				setEmptyView();
			}
		});
	}
	public void setEmptyView(){
		mListView.setVisibility(View.GONE);
		emp.setVisibility(View.VISIBLE);
		emp.setCartEmptyView();
		emp.getBtn_login().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
	}
	public void hideEmptyView(){
		mListView.setVisibility(View.VISIBLE);
		emp.setVisibility(View.GONE);
	}
	@Override
	public void onRefresh() {
		getCart();
		onLoad();
	}

	@Override
	public void onLoadMore() {

	}
	private void onLoad() {
		mListView.stopRefresh();
		mListView.setRefreshTime(CommonUtils.getTime());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(priceReceiver);
	}
}
