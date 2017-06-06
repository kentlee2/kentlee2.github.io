package com.haoqi.yungou.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.user.LoginActivity;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CartUtils {
	public static int getImg_X(){
		return GlobalApplication.getInstance().GetValue(Constant.CART_POSITION_X,0);
	}
	public static int getImg_Y(){
		return  GlobalApplication.getInstance().GetValue(Constant.CART_POSITION_Y,0);
	}
	/**
	 * 添加到购物车动画
	 * @param v 动画的图片
	 * @param start_location 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
	 * @param end_location 用来存储动画结束位置的X、Y坐标
	 * @param cloudGoodId
     */
	public static void setAnim(final Activity ac, final ImageView v, int[] start_location, int[] end_location, final String cloudGoodId) {
		ViewGroup anim_mask_layout = null;
		anim_mask_layout = createAnimLayout(ac);
		anim_mask_layout.addView(v);//把动画小球添加到动画层
		final View view = addViewToAnimLayout(anim_mask_layout, v,
				start_location);

		// 计算位移
		int endX = end_location[0]-start_location[0] ;// 动画位移的X坐标
		int endY = end_location[1] - start_location[1];// 动画位移的y坐标
		TranslateAnimation translateAnimationX = new TranslateAnimation(0,
				endX, 0, 0);
		translateAnimationX.setInterpolator(new LinearInterpolator());
		translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
				0, endY);
		translateAnimationY.setInterpolator(new AccelerateInterpolator());
		translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
		translateAnimationX.setFillAfter(true);

		AnimationSet set = new AnimationSet(false);
		set.setFillAfter(false);
		set.addAnimation(translateAnimationY);
		set.addAnimation(translateAnimationX);
		set.setDuration(800);// 动画的执行时间
		view.startAnimation(set);
		// 动画监听事件
		set.setAnimationListener(new Animation.AnimationListener() {
			// 动画的开始
			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			// 动画的结束
			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				AddtoCart(ac,1,cloudGoodId);
			}
		});
	}

	/**
	 * 添加到购物车
	 * @param count
	 * @param goodId
	 */
	public static void AddtoCart(final Activity context, final int count, String goodId) {
		RequestParams params = new RequestParams();
		params.put("userId", UserUtils.getUserId());
		params.put("carType","0");// 0代表普通加入购物车   1在购物车列表中改变商品数量
		params.put("carGoodsId",goodId);//商品id
		params.put("carCount",count+"");
		VolleyRequest.post(context, Uriconfig.AddToCart, params, new RequestListener() {
			@Override
			public void requestSuccess(String json) {

				try {
					JSONObject jsonobject = new JSONObject(json);
					JSONObject inf = jsonobject.getJSONObject("inf");
					JSONObject	res = jsonobject.getJSONObject("res");
					String status = res.getString("status");
					if("0".equals(status)){
//						Toast.makeText(context,"加入购物车成功",Toast.LENGTH_SHORT).show();
						String count = inf.getString("carSize");
						Intent intent = new Intent(Constant.ADD_CART);
						intent.putExtra(Constant.CART_COUNT,count);
						context.sendBroadcast(intent);
						DBManager.getInstance().updateUserInfo(Constant.CART,count);
					}else{
						if(UserUtils.isLogined()){
							Toast.makeText(context,res.getString("errMsg"),Toast.LENGTH_SHORT).show();
						}else{
							context.startActivity(new Intent(context, LoginActivity.class));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestError(VolleyError e) {
				Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * @Description: 创建动画层
	 * @param
	 * @return void
	 * @throws
	 */
	private static ViewGroup createAnimLayout(Activity context) {
		ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
		LinearLayout animLayout = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		//noinspection ResourceType
		animLayout.setId(Integer.MAX_VALUE);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		return animLayout;
	}

	private static View addViewToAnimLayout(final ViewGroup vg, final View view,
											int[] location) {
		int x = location[0];
		int y = location[1];
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}
	public static void getCartCount(TextView et) {
		String count = DBManager.getInstance().getGoodsCount();
		if(!count.equals("0")){
			et.setText(count);
			et.setVisibility(View.VISIBLE);
		}else{
			et.setVisibility(View.INVISIBLE);
		}
	}

}
