package com.haoqi.yungou.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.haoqi.yungou.Activity.EditInfoAcitivity;
import com.haoqi.yungou.Activity.MyCloundHistoryActivity;
import com.haoqi.yungou.Activity.MyShaidanListActivity;
import com.haoqi.yungou.Activity.ObtainGoodsActivity;
import com.haoqi.yungou.Activity.RechargeActivity;
import com.haoqi.yungou.Activity.SettingActivity;
import com.haoqi.yungou.Activity.user.AccountDetailActivity;
import com.haoqi.yungou.Activity.user.LoginActivity;
import com.haoqi.yungou.R;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
	private ListView listView;
	private View view;
    private ArrayList<Map<String,Object>> list;
	private String[] menus ;
	private int[] images ={R.drawable.me1,R.drawable.me2,R.drawable.me3,R.drawable.me4};
	private LinearLayout ll_amount;
	private RelativeLayout rl_profile;
	private LinearLayout ll_login_view;
	private ImageView user_header;
	private TextView userName;
	private TextView userPhone;
	private TextView my_balance;
	private TextView remain_amount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 super.onCreateView(inflater, container, savedInstanceState);
		 view = inflater.inflate(R.layout.fragment_my, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		NavigationView nav_bar = (NavigationView) view.findViewById(R.id.nav_bar);
		listView = (ListView)view.findViewById(R.id.my_menu_list);
		ll_amount = (LinearLayout)view.findViewById(R.id.ll_amount);
		rl_profile = (RelativeLayout)view.findViewById(R.id.rl_profile);
		user_header = (ImageView)view.findViewById(R.id.user_header);//用户头像
		userName = (TextView)view.findViewById(R.id.userName);//用户名
		userPhone = (TextView)view.findViewById(R.id.userPhone);//手机号码
		my_balance = (TextView)view.findViewById(R.id.my_balance);//可用福分
		remain_amount = (TextView)view.findViewById(R.id.remain_amount);//可用余额
		ll_login_view = (LinearLayout)view.findViewById(R.id.ll_login_view);  //未登录视图
		Button btn_login_regist = (Button) view.findViewById(R.id.btn_login_regist);
		Button goto_recharge = (Button) view.findViewById(R.id.goto_recharge);
		nav_bar.getBackView().setVisibility(View.GONE);
		btn_login_regist.setOnClickListener(this);
		goto_recharge.setOnClickListener(this);
		rl_profile.setOnClickListener(this);
		list =new ArrayList<>();
		getMenuList();
		nav_bar.setClickCallback(new NavigationView.ClickCallback() {
			@Override
			public void onBackClick() {
			}
			@Override
			public void onRightClick() {
                  getActivity().startActivity(new Intent(getActivity(),SettingActivity.class));
			}
		});
	}

	private void getUserInfo() {
		if(UserUtils.isLogined()){
			UserUtils.saveUserInfo(userName,user_header,my_balance,userPhone,remain_amount);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(UserUtils.isLogined()){
			getUserInfo();
			ll_login_view.setVisibility(View.GONE);
			ll_amount.setVisibility(View.VISIBLE);
			rl_profile.setVisibility(View.VISIBLE);
		}else{
			ll_login_view.setVisibility(View.VISIBLE);
			ll_amount.setVisibility(View.GONE);
			rl_profile.setVisibility(View.GONE);
		}
	}

	private void getMenuList() {
		menus = getActivity().getResources().getStringArray(R.array.my_menu);
		for(int i=0;i<menus.length;i++){
			Map<String,Object> map = new HashMap();
			map.put("text",menus[i]);
			map.put("iv",images[i]);
			list.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.item_my_menu, new String[]{"iv","text"}, new int[]{R.id.iv,R.id.my_menu});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(UserUtils.isLogined()) {
              switch (position){
				  case 0:
						  startActivity(new Intent(getActivity(), MyCloundHistoryActivity.class));
					  break;
				  case 1:
					  startActivity(new Intent(getActivity(), ObtainGoodsActivity.class));
					  break;
				  case 2:
					  startActivity(new Intent(getActivity(), MyShaidanListActivity.class));
					  break;
				  case 3:
					  startActivity(new Intent(getActivity(), AccountDetailActivity.class));
					  break;
			  }
		}else{
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_up,0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_login_regist:
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_up,0);
				break;
			case R.id.goto_recharge:
				Intent intent2 =new Intent(getActivity(), RechargeActivity.class);
				startActivity(intent2);
				break;
			case R.id.rl_profile:
				Intent intent3 =new Intent(getActivity(), EditInfoAcitivity.class);
				startActivity(intent3);
				break;
		}
	}
}
