package com.haoqi.yungou.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.fragment.CartFragment;
import com.haoqi.yungou.fragment.GoodsFragment;
import com.haoqi.yungou.fragment.HomeFragment;
import com.haoqi.yungou.fragment.MyFragment;
import com.haoqi.yungou.fragment.NewestFragment;
import com.haoqi.yungou.util.CartUtils;
import com.haoqi.yungou.util.DBManager;

public class MainActivity extends FragmentActivity implements HomeFragment.OnHomeItemClickListener {
	public static final int GoodsDetail = 1;
	private FragmentManager fragmentManager;
	private ImageView iv_home,iv_goods,iv_newest,iv_cart,iv_my;
	private HomeFragment homeFragment;
	private GoodsFragment goodsFragment;
	private NewestFragment recentGoodsFragment;
	private CartFragment carFragment;
	private MyFragment myFragment;
	private TextView tv_home,tv_goods,tv_newest,tv_cart,tv_cart_count,tv_my;
	private RelativeLayout rl_home,rl_goods,rl_newest,rl_cart,rl_my;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		setTabSelection(0);
		registerReceiver(cartReceiver,new IntentFilter(Constant.ADD_CART));
	}
	public void setTabSelection(int index) {
		// 重置按钮
		resetBtn();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index){
			case 0:
				// 当点击了tab时，改变控件的图片和文字颜色
				iv_home.setSelected(true);
				tv_home.setTextColor(getResources().getColor(R.color.red_text));
				if (homeFragment == null) {
					// 如果Fragment为空，则创建一个并添加到界面上
					homeFragment = new HomeFragment();
					homeFragment.setOnHomeItemClickListener(this);
					transaction.add(R.id.id_content, homeFragment);
				} else {
					// 如果Fragment不为空，则直接将它显示出来
					transaction.show(homeFragment);
				}
				break;
			case 1:
				iv_goods.setSelected(true);
				tv_goods.setTextColor(getResources().getColor(R.color.red_text));
				if (goodsFragment == null) {
					goodsFragment = new GoodsFragment();
					transaction.add(R.id.id_content, goodsFragment);
				} else {
					transaction.show(goodsFragment);
				}
				break;
			case 2:
				iv_newest.setSelected(true);
				tv_newest.setTextColor(getResources().getColor(R.color.red_text));
				recentGoodsFragment = new NewestFragment();
				transaction.add(R.id.id_content, recentGoodsFragment);
				break;
			case 3:
				iv_cart.setSelected(true);
				tv_cart.setTextColor(getResources().getColor(R.color.red_text));
				if (carFragment == null) {
					carFragment = new CartFragment();
					transaction.add(R.id.id_content, carFragment);
				} else {
					transaction.show(carFragment);
					carFragment.setUserVisibleHint(true);
				}
				break;
			case 4:
				iv_my.setSelected(true);
				tv_my.setTextColor(getResources().getColor(R.color.red_text));
				if (myFragment == null) {
					myFragment = new MyFragment();
					transaction.add(R.id.id_content, myFragment);
				} else {
					transaction.show(myFragment);
				}
				break;
		}
		//transaction.addToBackStack(null);
		transaction.commit();

	}


	private void hideFragments(FragmentTransaction transaction) {
		if (homeFragment != null) {
			transaction.hide(homeFragment);
		}
		if (goodsFragment != null) {
			transaction.hide(goodsFragment);
		}
		if (recentGoodsFragment != null) {
			transaction.remove(recentGoodsFragment);
		}
		if (carFragment != null) {
			transaction.hide(carFragment);
			carFragment.setUserVisibleHint(false);
		}
		if (myFragment != null) {
			transaction.hide(myFragment);
		}

	}

	private void resetBtn() {
		iv_home.setSelected(false);
		iv_goods.setSelected(false);
		iv_newest.setSelected(false);
		iv_cart.setSelected(false);
		iv_my.setSelected(false);
		tv_home.setTextColor(getResources().getColor(R.color.black_text));
		tv_goods.setTextColor(getResources().getColor(R.color.black_text));
		tv_newest.setTextColor(getResources().getColor(R.color.black_text));
		tv_cart.setTextColor(getResources().getColor(R.color.black_text));
		tv_my.setTextColor(getResources().getColor(R.color.black_text));

	}

	private void initView() {
		fragmentManager=getFragmentManager();
		rl_home = (RelativeLayout)findViewById(R.id.rl_main);
		rl_goods = (RelativeLayout)findViewById(R.id.rl_goods);
		rl_newest = (RelativeLayout)findViewById(R.id.rl_newest);
		rl_cart = (RelativeLayout)findViewById(R.id.rl_cart);
		rl_my = (RelativeLayout)findViewById(R.id.rl_my);

		iv_home = (ImageView)findViewById(R.id.home_main);
		iv_goods = (ImageView)findViewById(R.id.home_all_goods);
		iv_newest = (ImageView)findViewById(R.id.home_newest);
		iv_cart = (ImageView)findViewById(R.id.home_cart);
		iv_my = (ImageView)findViewById(R.id.home_my);
		tv_home = (TextView)findViewById(R.id.tv_home);
		tv_goods = (TextView)findViewById(R.id.tv_goods);
		tv_newest = (TextView)findViewById(R.id.tv_newest);
		tv_cart = (TextView)findViewById(R.id.tv_cart);
		tv_cart_count = (TextView)findViewById(R.id.tv_cart_count);
		tv_my = (TextView)findViewById(R.id.tv_my);
		rl_home.setOnClickListener(new MyOnClickListener(0));
		rl_goods.setOnClickListener(new MyOnClickListener(1));
		rl_newest.setOnClickListener(new MyOnClickListener(2));
		rl_cart.setOnClickListener(new MyOnClickListener(3));
		rl_my.setOnClickListener(new MyOnClickListener(4));
		getCartCount();
	}

	public void getCartCount() {
		String count = DBManager.getInstance().getGoodsCount();
		if(!count.equals("0")){
			tv_cart_count.setText(count);
			tv_cart_count.setVisibility(View.VISIBLE);
		}else{
			tv_cart_count.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int endLoc[] = new int[2];
		iv_cart.getLocationInWindow(endLoc);
		if(CartUtils.getImg_X()==0) {
			GlobalApplication.getInstance().saveValue(Constant.CART_POSITION_X, endLoc[0]);
			GlobalApplication.getInstance().saveValue(Constant.CART_POSITION_Y, endLoc[1]);
		}
	}

	@Override
	public void OnHomeItemClick(View v) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (v.getId()){
			case R.id.btn_new_goods://新品
				NewGoods(transaction,2);
				transaction.commit();
				break;
			case R.id.btn_recharge://充值
				break;
			case R.id.ll_anounce://最新揭晓
				resetBtn();
				iv_newest.setSelected(true);
				tv_newest.setTextColor(getResources().getColor(R.color.red_text));
					recentGoodsFragment = new NewestFragment();
				transaction.add(R.id.id_content, recentGoodsFragment);
				transaction.commit();
				break;
			case R.id.ll_hot://人气
				NewGoods(transaction,1);
				transaction.commit();
				break;
			case R.id.iv_phone://手机
				NewGoods(transaction, 0);
				transaction.commit();
				break;
			case R.id.iv_gold://黄金
				NewGoods(transaction, 0);
				transaction.commit();
				break;
			case R.id.iv_machine://家电
				NewGoods(transaction, 0);
				transaction.commit();
				break;
			case R.id.iv_car://汽车
				NewGoods(transaction, 0);
				transaction.commit();
				break;
			case R.id.iv_limit_main:
				startActivityForResult(new Intent(this, LimitGoodsActivity.class),1);
				break;

		}
	}
	private void NewGoods(FragmentTransaction transaction, int type) {
		if (goodsFragment == null) {
			goodsFragment = new GoodsFragment();
			transaction.add(R.id.id_content, goodsFragment);
		} else {
			transaction.show(goodsFragment);
		}
		iv_goods.setSelected(true);
		tv_goods.setTextColor(getResources().getColor(R.color.red_text));
		iv_home.setSelected(false);
		tv_home.setTextColor(getResources().getColor(R.color.black_text));

	}

	private class MyOnClickListener implements OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}
		public void onClick(View v) {
			switch (index) {
				case 0:
					setTabSelection(0);
					break;
				case 1:
					setTabSelection(1);
					break;
				case 2:
					if(!iv_newest.isSelected())
						setTabSelection(2);
					break;
				case 3:
					if(!iv_cart.isSelected())
						setTabSelection(3);
					break;
				case 4:
					setTabSelection(4);
					break;
			}
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == GoodsDetail){
			setTabSelection(3);
		}
	}
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast toast =Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	BroadcastReceiver cartReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Constant.ADD_CART.equals(intent.getAction())){
				getCartCount();
			}
		}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(cartReceiver);
	}
}
