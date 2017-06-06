package com.haoqi.yungou.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.VolleyRequest;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = WXEntryActivity.class.getSimpleName();
	
	private Button  payBtn, favButton;
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private String url;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
		api.handleIntent(getIntent(), this);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		api.handleIntent(intent, this);//必须调用此句话
	}
	@Override
	public void onReq(BaseReq baseReq) {
		Log.d(TAG,baseReq+"");
	}

	@Override
	public void onResp(BaseResp baseResp) {
		Log.d(TAG,baseResp+"");
		//获取到code之后，需要调用接口获取到access_token
		if(baseResp.errCode==0){
			SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
			url ="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Constant.APP_ID+
					"&secret=6ed02bfbd8a0e67c9f5d975953346bf9&code="+newResp.code+"&grant_type=authorization_code";
			getAccessToken();
		}
	}
	//这个方法会取得accesstoken  和openID 
	private void getAccessToken() {
		VolleyRequest.get(this, url, new RequestListener() {
			@Override
			public void requestSuccess(String json) {
				Log.d(TAG,json);
				try {
					JSONObject response = new JSONObject(json);
					getUserInfo(response.optString( "access_token"),response.optString( "openid"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//refresh
                  url ="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+Constant.APP_ID+"&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
			}
			@Override
			public void requestError(VolleyError e) {
			}
		});
	}

	private void getUserInfo(String token, String openID) {
		VolleyRequest.get(this, "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + openID, new RequestListener() {
			@Override
			public void requestSuccess(String json) {
				try {
					JSONObject response = new JSONObject(json);
					Log.d(TAG,response.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void requestError(VolleyError e) {

			}
		});
	}
}