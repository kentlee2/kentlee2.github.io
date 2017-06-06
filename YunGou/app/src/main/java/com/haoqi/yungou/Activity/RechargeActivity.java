package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Activity.user.AccountDetailActivity;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.PayConfig;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.iapppay.sdk.main.IAppPayOrderUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RechargeActivity extends Activity implements View.OnClickListener{
    private ArrayList<Map<String, Object>> list = new ArrayList<>();
    private CheckBox checkBox;
    private CheckBox checkBox2;
    private Button btn_recharge;
    private MySimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        IAppPay.init(this, IAppPay.PORTRAIT, PayConfig.appid);
        setData();
        GridView gridView = (GridView) findViewById(R.id.recharge_grid);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_alipay);
        RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.rl_aipay);
        checkBox = (CheckBox)findViewById(R.id.cb_zfb);
        checkBox2 = (CheckBox)findViewById(R.id.cb_wx);
        btn_recharge = (Button)findViewById(R.id.btn_recharge);
        btn_recharge.setOnClickListener(this);
        rl.setOnClickListener(this);
        rl2.setOnClickListener(this);
        adapter = new MySimpleAdapter(this, list, R.layout.item_recharge, new String[]{"num", "isCheck"}, new int[]{R.id.tv});
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((NavigationView)findViewById(R.id.title_bar)).setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                startActivity(new Intent(RechargeActivity.this, AccountDetailActivity.class));
            }
        });
    }
    /** 获取收银台参数 */
    private String getTransdata( String appuserid, String cpprivateinfo, int waresid, float price, String cporderid) {
        //调用 IAppPayOrderUtils getTransdata() 获取支付参数
        IAppPayOrderUtils orderUtils = new IAppPayOrderUtils();
        orderUtils.setAppid(PayConfig.appid);
        orderUtils.setWaresid(waresid);//传入您商户后台创建的商品编号
        orderUtils.setCporderid(cporderid);
        orderUtils.setAppuserid(appuserid);
        orderUtils.setPrice(price);//单位 元
        orderUtils.setWaresname("充值");//开放价格名称(用户可自定义，如果不传以后台配置为准)
        orderUtils.setCpprivateinfo(cpprivateinfo);
        orderUtils.setNotifyurl(PayConfig.notifyurl);
        return orderUtils.getTransdata(PayConfig.privateKey);
    }
    /**
     * 支付结果回调
     */
    IPayResultCallback iPayResultCallback = new IPayResultCallback() {

        @Override
        public void onPayResult(int resultCode, String signvalue, String resultInfo) {
            // TODO Auto-generated method stub
            switch (resultCode) {
                case IAppPay.PAY_SUCCESS:
                    //调用 IAppPayOrderUtils 的验签方法进行支付结果验证
                    boolean payState = IAppPayOrderUtils.checkPayResult(signvalue, PayConfig.publicKey);
                    if(payState){
                        Toast.makeText(RechargeActivity.this, "充值成功", Toast.LENGTH_LONG).show();
                    }
                    break;
                case IAppPay.PAY_ING:
                    Toast.makeText(RechargeActivity.this, "成功下单", Toast.LENGTH_LONG).show();
                    break ;
                default:
                    Toast.makeText(RechargeActivity.this, resultInfo, Toast.LENGTH_LONG).show();
                    break;
            }
            Log.d("MainDemoActivity", "requestCode:" + resultCode + ",signvalue:" + signvalue + ",resultInfo:" + resultInfo);
        }
    };
    class MySimpleAdapter extends SimpleAdapter{
        private EditText et;
        private String reChargeNum;
        private String cReChargeNum;

        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return list.size()-1==position ? 0 :1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            final Map map = (Map) getItem(position);
            final TextView tv = (TextView) view.findViewById(R.id.tv);
            et = (EditText) view.findViewById(R.id.et);
            int type = getItemViewType(position);
            tv.setVisibility(View.VISIBLE);
            if(type==0){
                tv.setVisibility(View.GONE);
                et.setVisibility(View.VISIBLE);
                et.setText("");
                et.clearFocus();
                if ((Boolean) map.get("isCheck") == true) {
                    et.setBackgroundResource(R.drawable.recharge_btn);
                    et.requestFocus();
                    et.setFocusable(true);
                } else {
                    et.setBackgroundResource(R.drawable.btn_radius_all_normal);
                }
                showKeyboard(et);
                et.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction()== KeyEvent.ACTION_DOWN) {
                            setUnSelected(map);
                        }
                        return false;
                    }
                });
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        cReChargeNum = s.toString();
                    }
                });
            }else {
                et.setVisibility(View.GONE);
                hideKeyboard(et);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reChargeNum= tv.getText().toString();
                        setUnSelected(map);
                    }
                });
                if ((Boolean) map.get("isCheck") == true) {
                    tv.setSelected(true);
                } else {
                    tv.setSelected(false);
                }
            }
            return view;

        }
        public String getTextNum(){
            return reChargeNum;
        }
        public String getCTextNum(){
            return cReChargeNum;
        }
        private void hideKeyboard(EditText et) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }

        private void showKeyboard(EditText et) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et, 0);
        }

        private void setUnSelected(Map map) {
            for (Map hasMap : list) {//全部设为未选中
                hasMap.put("isCheck", false);
            }
            map.put("isCheck",true);
            notifyDataSetChanged();
        }
    }

    private void setData() {
        String[] nums ={"50","100","200","500","1000","0"};
        for (int i = 0; i < nums.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("num", nums[i]);
            map.put("isCheck", false);
            list.add(map);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_alipay:
                if(checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                    checkBox2.setVisibility(View.GONE);
                }else{
                    checkBox.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox2.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_aipay:
                if(checkBox2.isChecked()) {
                    checkBox2.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                    checkBox2.setVisibility(View.GONE);
                }else{
                    checkBox2.setChecked(true);
                    checkBox.setChecked(false);
                    checkBox2.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_recharge:
                reCharge();
                break;
        }
    }

    private void reCharge() {
        final String  reChargeNum;
        if(!TextUtils.isEmpty(adapter.getCTextNum())){
            reChargeNum =adapter.getCTextNum();
        }else
            reChargeNum =adapter.getTextNum();
        if(TextUtils.isEmpty(reChargeNum)){
            GlobalApplication.getInstance().toastShortMsg(R.string.plz_select_amount);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("userId",UserUtils.getUserId());
        params.put("type","iapppay");
        params.put("amount",reChargeNum);
        VolleyRequest.post(this, Uriconfig.recharge, params, null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        String orderNo = inf.optString("orderNo");
                        String param  = getTransdata(UserUtils.getUserId(),"charge",1,Float.parseFloat(reChargeNum),orderNo);
                        IAppPay.startPay(RechargeActivity.this, param, iPayResultCallback);

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
}