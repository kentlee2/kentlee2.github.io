package com.haoqi.yungou.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.PayConfig;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.CustomDialog;
import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.iapppay.sdk.main.IAppPayOrderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class PayActivity extends FragmentActivity implements View.OnClickListener {


    private String cartIds;
    private LinearLayout ll_cart;
    private ImageLoader imageLoader;
    private Double score;
    private Double amount;
    private TextView tv_score,tv_score1,tv_balance,tv_balance1;
    private CheckBox cb_score,cb_balance;
    private Double totalPrice;
    private int count;
    private RelativeLayout layout_pay;
    private RelativeLayout layout_submit;
    private LinearLayout layout_result;
    private TextView tv_pay_result;
    private TextView tv_pay_result_num;
    private LinearLayout layout_success_goods;
    private ListView success_list;
    private LinearLayout layout_fail_goods;
    private Button btn_pay;
    private TextView total_amount;
    private JSONArray jsonArrays;
    private ImageView iv_view_cart;
    private FrameLayout fl_check_more;
    private CheckBox checkBox_zfb;
    private CheckBox cb_aippay;
    private View layout_pay_style;
    private TextView eback_payment;
    private String nonce_str;
    private String sign;
    private String prepay_id;
    private String timeStamp;
    private String payTotalPrice;
    private String transid;
    private static final String TAG = PayActivity.class.getSimpleName();
    private String transdata;
    private String Sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        IAppPay.init(this, IAppPay.PORTRAIT, PayConfig.appid);
        ArrayList<String> cartIdList = (ArrayList<String>) getIntent().getSerializableExtra("cartId");
        String[] cartarr = cartIdList.toArray(new String[cartIdList.size()]);
        cartIds = Arrays.toString(cartarr).replaceAll("[\\[\\]]","");
        imageLoader = ImageLoader.getInstance();
        initView();
        makeOrder();
    }

    private void initView() {
        ll_cart = (LinearLayout)findViewById(R.id.ll_cart);
        layout_pay = (RelativeLayout)findViewById(R.id.layout_pay);//支付layout
        layout_submit = (RelativeLayout)findViewById(R.id.layout_submit);//提交成功,获取支付结果
        layout_result = (LinearLayout)findViewById(R.id.layout_result);//提交成功视图
        layout_success_goods = (LinearLayout)findViewById(R.id.layout_success_goods);//支付成功商品列表
        layout_fail_goods = (LinearLayout)findViewById(R.id.layout_fail_goods);//支付失败商品列表
        RelativeLayout layout_score = (RelativeLayout) findViewById(R.id.layout_score);
        RelativeLayout layout_balance = (RelativeLayout) findViewById(R.id.layout_balance);
         layout_pay_style = findViewById(R.id.layout_pay_style);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_alipay);
        RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.rl_aipay);
        checkBox_zfb = (CheckBox)findViewById(R.id.cb_zfb);
        cb_aippay = (CheckBox)findViewById(R.id.cb_wx);
        rl.setOnClickListener(this);
        rl2.setOnClickListener(this);
         fl_check_more = (FrameLayout) findViewById(R.id.fl_check_more);

        iv_view_cart = (ImageView)findViewById(R.id.iv_view_cart);
        cb_score =(CheckBox)findViewById(R.id.cb_score);
        cb_balance =(CheckBox)findViewById(R.id.cb_balance);
        tv_score =(TextView)findViewById(R.id.tv_score);
        tv_score1 =(TextView)findViewById(R.id.tv_score1);
        tv_balance =(TextView)findViewById(R.id.tv_balance);
        tv_balance1 =(TextView)findViewById(R.id.tv_balance1);
        tv_pay_result =(TextView)findViewById(R.id.tv_pay_result);
        tv_pay_result_num =(TextView)findViewById(R.id.tv_pay_result_num);
        total_amount =(TextView)findViewById(R.id.total_amount);
        eback_payment =(TextView)findViewById(R.id.tv_ebank_payment);
        success_list =(ListView)findViewById(R.id.lv_success_goods);
         btn_pay = (Button) findViewById(R.id.btn_confirm_to_pay);
        Button btn_check_shopping_record = (Button) findViewById(R.id.btn_check_shopping_record);
        Button btn_go_shopping = (Button) findViewById(R.id.btn_go_shopping);
        btn_check_shopping_record.setOnClickListener(this);
        btn_go_shopping.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        layout_score.setOnClickListener(this);
        layout_balance.setOnClickListener(this);
        fl_check_more.setOnClickListener(this);
    }

    private void makeOrder() {

        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("cardIds",cartIds);
        VolleyRequest.post(this, Uriconfig.makeOrder, params, null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        score = inf.optDouble("score");//积分
                        amount = inf.optDouble("amount");//用户余额
                        totalPrice = inf.optDouble("sumAmount");//需支付总金额
                        payTotalPrice = inf.optString("sumAmount");
                        tv_score1.setText("(您的积分:"+score+")");
                        tv_balance1.setText("(您的余额:"+amount+")");

                        String price = "总需支付金额:￥"+totalPrice;
                        String eBackprice = "需网银支付:￥"+totalPrice;
                        CommonUtils.setSpanableText(total_amount,price,getResources().getColor(R.color.red_text),7,price.length());
                        CommonUtils.setSpanableText(eback_payment,eBackprice, ContextCompat.getColor(PayActivity.this,R.color.red_text),6,eBackprice.length());
                        if(score>100){
                            tv_score.setText(score+"");
                        }else{
                            tv_score.setText("您的积分不足");

                        }
                        if(amount>1){
                            tv_balance.setText("￥"+amount);
                        }else{
                            tv_balance.setText("可使用余额不足");
                        }
                        JSONArray arrays = inf.optJSONArray("goodsList");
                        count =arrays.length();
                        jsonArrays = arrays;
                        if(count>3){
                            fl_check_more.setVisibility(View.VISIBLE);
                        }else{
                            fl_check_more.setVisibility(View.GONE);
                        }
                        set3List();
                    }else{
                        ToastUtils.showShort(PayActivity.this,res.optString("errMsg"));
                        finish();
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
        private String goodName;
    private void setDataView(JSONObject object) {
        int width = CommonUtils.getScreenW(this);
        String isLimit = object.optString("limitBuy");
         goodName = object.optString("goodName");
        String goodCloudNo = object.optString("goodCloudNo");
        String count = object.optString("count");//购买数量
        String shoppingCarId = object.optString("shoppingCarId");//购物车id
        String goodImg = Uriconfig.baseUrl+object.optString("goodPic");
        String goodPrice = object.optString("goodPrice");
        LinearLayout ll_layout = new LinearLayout(PayActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10,2,10,5);
        ll_layout.setLayoutParams(lp);//设置布局参数
        ll_layout.setBackgroundColor(getResources().getColor(R.color.main_background));
        ll_layout.setOrientation(LinearLayout.HORIZONTAL);
        ll_layout.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv_name = new TextView(PayActivity.this);
        TextView tv_count = new TextView(PayActivity.this);
        TextView tv_price = new TextView(PayActivity.this);
        tv_name.setEllipsize(TextUtils.TruncateAt.END);
        tv_name.setMaxLines(1);
        tv_name.setWidth(width-400);
        tv_price.setTextColor(getResources().getColor(R.color.red_text));
        tv_count.setTextColor(getResources().getColor(R.color.gray_text));
        tv_count.setTextSize(12);
        tv_price.setTextSize(12);
        tv_price.setSingleLine();
        ImageView iv = new ImageView(PayActivity.this);
        iv.setMaxHeight(150);
        iv.setMaxWidth(150);
        imageLoader.displayImage(goodImg,iv, CommonUtils.displayImageOptions);
        tv_name.setText("(第"+goodCloudNo+"云)"+goodName);
        tv_count.setText(count+"人次/");
        tv_price.setText("￥"+goodPrice);
        ll_layout.addView(iv);
        ll_layout.addView(tv_name);
        ll_layout.addView(tv_count);
        ll_layout.addView(tv_price);
        ll_cart.addView(ll_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((NavigationView)findViewById(R.id.titlebar)).setClickCallback(new NavigationView.ClickCallback() {
            @Override
            public void onBackClick() {
                finish();
            }
            @Override
            public void onRightClick() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_score:
                if(score>100){
                    if(cb_score.isChecked()){
                        cb_score.toggle();
                        btn_pay.setEnabled(false);
                    }else {
                        cb_aippay.setVisibility(View.GONE);
                        checkBox_zfb.setVisibility(View.GONE);
                        checkBox_zfb.setChecked(false);
                        cb_aippay.setChecked(false);
                        cb_score.setChecked(true);
                        cb_balance.setChecked(false);
                        btn_pay.setEnabled(true);
                    }
                }else {
                    showScoreNotice();
                }
                break;
            case R.id.layout_balance:
                if(amount>1){
                    if(cb_balance.isChecked()){
                        cb_balance.toggle();
                        btn_pay.setEnabled(false);
                    }else {
                        cb_aippay.setVisibility(View.GONE);
                        checkBox_zfb.setVisibility(View.GONE);
                        checkBox_zfb.setChecked(false);
                        cb_aippay.setChecked(false);
                        cb_balance.setChecked(true);
                        cb_score.setChecked(false);
                        btn_pay.setEnabled(true);
                    }
                }else{
                    showBalanceNotice();
                }
                break;
            case  R.id.btn_confirm_to_pay:
//                if(cb_aippay.isChecked()){
//
//                }else if(checkBox_zfb.isChecked()){
////                    zfbPay();
//                }else {
//                }
                goto_pay();
                break;
            case R.id.btn_check_shopping_record:
                startActivity(new Intent(this,MyCloundHistoryActivity.class));
                finish();
                break;
            case R.id.btn_go_shopping:
                finish();
                break;
            case R.id.fl_check_more:
                if(isExtend) {
                    isExtend=false;
                    setAllList();
                }else{
                    isExtend=true;
                    set3List();
                }
                break;
            case R.id.rl_alipay:
                if(checkBox_zfb.isChecked()) {
                    checkBox_zfb.setChecked(false);
                    checkBox_zfb.setVisibility(View.GONE);
                    cb_aippay.setVisibility(View.GONE);
                    btn_pay.setEnabled(false);
                }else{
                    cb_score.setChecked(false);
                    cb_balance.setChecked(false);
                    checkBox_zfb.setChecked(true);
                    cb_aippay.setChecked(false);
                    checkBox_zfb.setVisibility(View.VISIBLE);
                    cb_aippay.setVisibility(View.GONE);
                    btn_pay.setEnabled(true);
                }
                break;
            case R.id.rl_aipay:
                if(cb_aippay.isChecked()) {
                    cb_aippay.setChecked(false);
                    checkBox_zfb.setVisibility(View.GONE);
                    cb_aippay.setVisibility(View.GONE);
                    btn_pay.setEnabled(false);
                }else{
                    cb_score.setChecked(false);
                    cb_balance.setChecked(false);
                    cb_aippay.setChecked(true);
                    checkBox_zfb.setChecked(false);
                    cb_aippay.setVisibility(View.VISIBLE);
                    checkBox_zfb.setVisibility(View.GONE);
                    btn_pay.setEnabled(true);
                }
                break;
        }
    }


            private void setAllList(){
        ll_cart.removeAllViews();
        iv_view_cart.setImageResource(R.drawable.cart_arrow_up);
        for (int i = 0; i < jsonArrays.length(); i++) {
            try {
                JSONObject object = (JSONObject) jsonArrays.get(i);
                setDataView(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void set3List(){
        ll_cart.removeAllViews();
        iv_view_cart.setImageResource(R.drawable.cart_more);
        for (int i = 0; i < jsonArrays.length(); i++) {
            if (i < 3) {
                try {
                    JSONObject object = (JSONObject) jsonArrays.get(i);
                    setDataView(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
   private  boolean isExtend =true;
    private void goto_pay() {//去支付
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("cardIds",cartIds);
        params.put("amount",totalPrice+"");
        //使用积分支付
        if(cb_score.isChecked()){
            params.put("scoreAmount",totalPrice*1000+"");//使用积分  （如果不使用积分支付，传值为0）
            params.put("payType","score");
        }else{
            params.put("scoreAmount","0");
        }
        //使用余额支付
        if(cb_balance.isChecked()){
            params.put("walletAmount",totalPrice+"");//使用钱包  （如果不使用钱包支付，传值为0）
            params.put("payType","wallet");
        }else{
            params.put("walletAmount","0");
        }
        if(cb_aippay.isChecked()) {
            params.put("payType", "iapppay");//支付类型  score（积分支付）,wallet（钱包支付）,iapppay（爱贝云计费）
            params.put("appId", PayConfig.appid);
        }
        VolleyRequest.post(this, Uriconfig.createOrder, params, null, new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        transid =  inf.optString("transid");
                        String orderNo = inf.optString("orderNo");
//                        transdata = inf.optString("transdata");
                        Sign = inf.optString("Sign");
                        transdata = getTransdata(UserUtils.getUserId(),"pay",1,Float.parseFloat(payTotalPrice),orderNo);//第二个参数判断是否为支付和充值
//                        String payData = URLDecoder.decode(transdata);
//                        String payDataSign = URLDecoder.decode(Sign);
                        if(cb_aippay.isChecked()){
                            IAppPay.startPay(PayActivity.this, transdata, iPayResultCallback);
                        }else{
                            if(cb_score.isChecked()) {//如果使用积分支付，更新个人信息
                                String left = String.valueOf(score - totalPrice * 1000);
                                DBManager.getInstance().updateUserInfo(Constant.SCORE,left);
                            }
                            if(cb_balance.isChecked()) {//如果使用余额支付，更新个人信息
                                String left = String.valueOf(Double.valueOf(amount) - Double.valueOf(totalPrice));
                                DBManager.getInstance().updateUserInfo(Constant.AMOUNT,left);
                            }
                            paySuccess();
                        }
                    }else{
                        ToastUtils.showShort(PayActivity.this,res.optString("errMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showShort(PayActivity.this,e.toString());
                }
            }
            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(PayActivity.this,e.toString());
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
        orderUtils.setWaresname("购买充值卡");//开放价格名称(用户可自定义，如果不传以后台配置为准)
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
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_LONG).show();
                        paySuccess();
                    }
                    break;
                case IAppPay.PAY_ING:
                    Toast.makeText(PayActivity.this, "成功下单", Toast.LENGTH_LONG).show();
                    break ;
                default:
                    Toast.makeText(PayActivity.this, resultInfo, Toast.LENGTH_LONG).show();
                    break;
            }
            Log.d("MainDemoActivity", "requestCode:" + resultCode + ",signvalue:" + signvalue + ",resultInfo:" + resultInfo);
        }
    };
   private void paySuccess(){
       layout_pay.setVisibility(View.GONE);
       layout_pay_style.setVisibility(View.GONE);
       layout_submit.setVisibility(View.VISIBLE);
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               layout_result.setVisibility(View.VISIBLE);
               layout_submit.setVisibility(View.GONE);
               btn_pay.setVisibility(View.GONE);
               layout_success_goods.setVisibility(View.GONE);
               layout_fail_goods.setVisibility(View.GONE);
               tv_pay_result.setText("支付成功，请等待揭晓！");
               tv_pay_result_num.setText("成功支付"+count+"个商品");
               sendBroadcast(new Intent(Constant.CART_CHANGED));
           }
       },1000);
   }
    private void showBalanceNotice() {
        CustomDialog.Builder dialog = new CustomDialog.Builder(this);
        dialog.setMessage(R.string.balance_notice)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showScoreNotice() {
        CustomDialog.Builder dialog = new CustomDialog.Builder(this);
        dialog.setMessage(R.string.score_notice)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }
}
