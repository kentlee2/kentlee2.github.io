package com.haoqi.yungou.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.CodeGridAdapter;
import com.haoqi.yungou.customView.EmptyView;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.CartUtils;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.DBManager;
import com.haoqi.yungou.util.MyCountDownTimer;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.XScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoodsDetailActivity extends FragmentActivity implements EmptyView.RetryListener, View.OnClickListener, XScrollView.IXScrollViewListener {

    private String goodsId;
    private Button btn_check_detail,btn_go_shopping,btn_add_to_cart;
    private ProgressBar pb;
    private TextView tv_goodsname;
    private TextView join_num,total_num,remain_num;
    private ImageView goodImg;
    private TextView tv_goods_price;
    private TextView tv_calculate;
    private TextView tv_cart_count;
    private ImageView goodsLable;
    private TextView tv_listmit_good;
    private String pic;
    private ImageLoader loader ;
    private ImageView imgv_goods_cart;
    private RelativeLayout rl_countdown;
    private TextView tv_min,tv_sec,tv_msec;
    private View gainerView;
    private ImageView sriv_avatar,sriv_gainer_goods;
    private TextView tv_nickname,tv_gainer_address,tv_gainer_luck_code,tv_announced_time,tv_shopping_time,tv_gainer_goodsname,tv_gainer_goods_price;
    private View layout_goods_state;
    private View gainerCodes;
    private View previous;
    private TextView tv_buy_count,tv_buy_time;
    private LinearLayout ll_codes;
    private NavigationView nav;
    private RelativeLayout rl_gainer_goods;
    private String goodId;
    private RelativeLayout rl_calculating;
    private XScrollView mScrollView;
    private String newsCloudGoodsId;
    private boolean isAdd;
    private ImageView prev_avatar;
    private TextView tv_gainer_nickname,tv_gainer_addr,tv_announced_time2,tv_shopping_time2,tv_announced_code;
    private TextView tv_shaidan;
    private int gainerJoinCount;
    private String buyTime;
    public static String allCodes;
    private MyCountDownTimer mdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_detail_content);
        goodsId = getIntent().getStringExtra("goodsId");
        loader =ImageLoader.getInstance();
        mScrollView = (XScrollView)findViewById(R.id.scroll_view);
        mScrollView.setPullRefreshEnable(true);
        mScrollView.setPullLoadEnable(false);//是否加载更多
        mScrollView.setIXScrollViewListener(this);
        mScrollView.setRefreshTime(CommonUtils.getTime());
        View content = LayoutInflater.from(this).inflate(R.layout.activity_goods_detail, null);
        if (null != content) {
            initView(content);
        }

        mScrollView.setView(content);
//        EmptyView ep = (EmptyView)findViewById(R.id.emptyview);
//        getAnnounceInfo();
        getGoodsInfo();
        registerReceiver(cartReceiver,new IntentFilter(Constant.ADD_CART));
    }


    private void initView(View view) {
        rl_countdown = (RelativeLayout)view.findViewById(R.id.rl_countdown);
        rl_calculating = (RelativeLayout)view.findViewById(R.id.rl_calculating);
        btn_check_detail = (Button)view.findViewById(R.id.btn_check_detail);//查看详情
        btn_go_shopping = (Button)view.findViewById(R.id.btn_go_shopping);//立即1元云购
        btn_add_to_cart = (Button)view.findViewById(R.id.btn_add_to_cart);//加入购物车
        tv_goodsname = (TextView)view.findViewById(R.id.tv_goodsname);//商品名
        tv_cart_count = (TextView)view.findViewById(R.id.tv_cart_count);//购物车数量
        imgv_goods_cart = (ImageView)view.findViewById(R.id.imgv_goods_cart);//购物车数量
        RelativeLayout layoutCart = (RelativeLayout)view. findViewById(R.id.layout_cart);
        setGainerView(view);
        setGainerCodeView(view);
        layout_goods_state = view.findViewById(R.id.layout_goods_state);//商品详情
        View goodsContent= view.findViewById(R.id.layout_goods_content);//揭晓详情
        setPreViousView(view);
        tv_min = (TextView)view.findViewById(R.id.tv_min);
        tv_sec = (TextView)view.findViewById(R.id.tv_sec);
        tv_msec = (TextView)view.findViewById(R.id.tv_msec);
        pb = (ProgressBar)layout_goods_state.findViewById(R.id.participation);
        goodImg = (ImageView)layout_goods_state.findViewById(R.id.sriv_goods);//商品图片
        goodsLable = (ImageView)layout_goods_state.findViewById(R.id.iv_label_goods_state);
        tv_goods_price = (TextView)layout_goods_state.findViewById(R.id.tv_goods_price);
        join_num = (TextView)layout_goods_state.findViewById(R.id.hasJoin_num);//已参与
        total_num = (TextView)layout_goods_state.findViewById(R.id.total_num);//总需人数
        remain_num = (TextView)layout_goods_state.findViewById(R.id.remain_num);//剩余(进度条)
        tv_listmit_good = (TextView)layout_goods_state.findViewById(R.id.tv_listmit_good);//剩余(进度条)
        tv_calculate = (TextView) goodsContent.findViewById(R.id.tv_calculate_details);
        TextView tv_detail = (TextView) goodsContent.findViewById(R.id.tv_detail);
        TextView tv_shopping_record = (TextView) goodsContent.findViewById(R.id.tv_shopping_record);
         tv_shaidan = (TextView) goodsContent.findViewById(R.id.tv_shaidan);

        tv_calculate.setOnClickListener(this);
        btn_add_to_cart.setOnClickListener(this);
        btn_go_shopping.setOnClickListener(this);
        layoutCart.setOnClickListener(this);
        tv_detail.setOnClickListener(this);
        tv_shopping_record.setOnClickListener(this);
        tv_shaidan.setOnClickListener(this);
        tv_calculate.setVisibility(View.GONE);
        previous.setVisibility(View.GONE);
        rl_countdown.setVisibility(View.GONE);
    }

    private void setPreViousView(View view) {
        previous= view.findViewById(R.id.layout_previous_period_gainer);//上一云获得者
        prev_avatar =(ImageView) previous.findViewById(R.id.sriv_avatar);
        tv_gainer_nickname = (TextView)previous.findViewById(R.id.tv_gainer_nickname);
        tv_gainer_addr = (TextView)previous.findViewById(R.id.tv_gainer_addr);
        tv_announced_time2 = (TextView)previous.findViewById(R.id.tv_announced_time);
        tv_shopping_time2 = (TextView)previous.findViewById(R.id.tv_shopping_time);
        tv_announced_code = (TextView)previous.findViewById(R.id.tv_announced_code);
    }

    /**
     * 获奖码视图
     * @param view
     */
    private void setGainerCodeView(View view) {
        gainerCodes = view.findViewById(R.id.layout_announced_gainer_codes);
        tv_buy_count = (TextView)gainerCodes.findViewById(R.id.tv_buy_count);
        tv_buy_time = (TextView)gainerCodes.findViewById(R.id.tv_buy_time);
        ll_codes = (LinearLayout)gainerCodes.findViewById(R.id.ll_codes);
//        tv_codes = (TextView)gainerCodes.findViewById(R.id.tv_codes);
        TextView tv_show_more = (TextView) gainerCodes.findViewById(R.id.tv_show_more);
        gainerCodes.setVisibility(View.GONE);
        tv_show_more.setOnClickListener(this);
    }

    /**
     * 获奖者view
     * @param view
     */
    private void setGainerView(View view) {
        gainerView = view.findViewById(R.id.layout_announced_detail_gainer);//获得者的视图
        gainerView.setVisibility(View.GONE);
        rl_gainer_goods = (RelativeLayout)gainerView.findViewById(R.id.rl_gainer_goods);
        sriv_avatar = (ImageView)gainerView.findViewById(R.id.sriv_avatar);
        sriv_gainer_goods = (ImageView)gainerView.findViewById(R.id.sriv_gainer_goods);
        tv_nickname = (TextView)gainerView.findViewById(R.id.tv_nickname);
        tv_gainer_address = (TextView)gainerView.findViewById(R.id.tv_gainer_address);
        tv_gainer_luck_code = (TextView)gainerView.findViewById(R.id.tv_gainer_luck_code);
        tv_announced_time = (TextView)gainerView.findViewById(R.id.tv_announced_time);
        tv_shopping_time = (TextView)gainerView.findViewById(R.id.tv_shopping_time);
        tv_gainer_goodsname = (TextView)gainerView.findViewById(R.id.tv_gainer_goodsname);
        tv_gainer_goods_price = (TextView)gainerView.findViewById(R.id.tv_gainer_goods_price);
        rl_gainer_goods.setOnClickListener(this);
    }

    private void getAnnounceInfo() {
        RequestParams params = new RequestParams();
        params.put("goodId",goodsId);
        VolleyRequest.post(this, Uriconfig.getPublicDetail, params,new RequestListener() {

            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.optString("status");
                    if("0".equals(status)) {
                           String isAnnounce = inf.optString("status"); //0进行中   1等待揭晓   2已经揭晓
                            nav.setTitle("揭晓结果");
                            rl_countdown.setVisibility(View.GONE);
                            rl_calculating.setVisibility(View.GONE);
                            gainerView.setVisibility(View.VISIBLE);
                            layout_goods_state.setVisibility(View.GONE);
                            gainerCodes.setVisibility(View.VISIBLE);
                            //底部按钮
                            btn_check_detail.setVisibility(View.VISIBLE);
                            btn_go_shopping.setVisibility(View.GONE);
                            btn_add_to_cart.setVisibility(View.GONE);
                            btn_check_detail.setText("第"+inf.optString("newsCloudNo")+"云(正在进行中...)");
                            btn_check_detail.setOnClickListener(GoodsDetailActivity.this);
                            inf.optString("buyCount");//限购数量
                            String headImg = Uriconfig.baseUrl + inf.optString("headImg");
                            inf.optString("joinCount");
                            goodId = inf.optString("goodId");
                            String goodImg = Uriconfig.baseUrl + inf.optString("pic");
                            loader.displayImage(goodImg, sriv_gainer_goods, CommonUtils.displayImageOptions);
                            inf.optString("userId");
                            inf.optString("cloudNo");
                            inf.optString("limiBut");
                             newsCloudGoodsId = inf.optString("newsCloudGoodsId");
                            String lucyCode = "幸运云购码：" + inf.optString("winCode");//中奖云购码
                            String buyCount = "获得者本云总共参与： " + inf.optInt("joinCount") + " 人次";//中奖云购码
                             gainerJoinCount = inf.optInt("joinCount");
                            loader.displayImage(headImg, sriv_avatar, CommonUtils.circleImageOptions);
                            tv_nickname.setText(inf.optString("username"));
                            tv_gainer_address.setText("("+inf.optString("address")+")");
                            tv_shopping_time.setText("云购时间："+inf.optString("buyTime"));
                            tv_gainer_goodsname.setText(inf.optString("goodName"));
                            tv_gainer_goods_price.setText("价值:￥" + inf.optString("price"));
                            tv_announced_time.setText("揭晓时间："+inf.optString("publicTime"));
                            tv_buy_time.setText(inf.optString("buyTime"));
                            buyTime = inf.optString("buyTime");
                            allCodes = inf.optString("codes");
                            setCodeLayout(allCodes);
                            CommonUtils.setSpanableText(tv_gainer_luck_code, lucyCode, getResources().getColor(R.color.red_text), 6, lucyCode.length());
                            CommonUtils.setSpanableText(tv_buy_count, buyCount, getResources().getColor(R.color.red_text), 10, buyCount.length()-2);
                    }else{
                        ToastUtils.showShort(GoodsDetailActivity.this,res.optString("errMsg"));
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
    public void onRefresh() {
        if(mdt!=null){
            mdt.cancel();
        }
        getGoodsInfo();
        onLoad();
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

    class TimerFinish implements MyCountDownTimer.OnFinished{

        @Override
        public void onfinish() {
            rl_calculating.setVisibility(View.VISIBLE);
            previous.setVisibility(View.GONE);
            getAnnounceInfo();
        }
    }
    private void setCodeLayout(String codes) {
        ArrayList<String> list = new ArrayList();
        String[] codesArr = codes.split(",");
        for(int i=0;i<codesArr.length;i++){
            if(i<12) {
                String code = codesArr[i];
                list.add(code);
            }
        }
        CodeGridAdapter adapter = new CodeGridAdapter(this,R.layout.item_code,list);
            GridView grid = (GridView) gainerCodes.findViewById(R.id.code_grid);
        grid.setAdapter(adapter);
    }

    private void getGoodsInfo() {//商品详情
        RequestParams params = new RequestParams();
        params.put("goodId",goodsId);
        if(UserUtils.isLogined()) {
            params.put("userId", UserUtils.getUserId());
        }else{
            params.put("userId", "");
        }
        VolleyRequest.post(this, Uriconfig.getGoodsDetail, params,null, new RequestListener() {
            @Override
            public void requestSuccess(String json) {
                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.optString("status");
                    if("0".equals(status)) {
                        String total = inf.optString("sumCount");//总需人数
                        String join = inf.optString("joinCount");//已参与人数
                        String goodsName = inf.optString("goodName");//商品名
                        String cloudNo = inf.optString("cloudNo");//第几云
                        String left = inf.optString("leftCount");//剩余人数
                        pic = Uriconfig.baseUrl + inf.optString("pic");//商品图片
                        String price = inf.optString("price");//价格
                        String limitBuy = inf.optString("limiBuy");//是否限购，0，否；1是
                        String cartCount = inf.optString("userShoppingCount");
                        String goodStatus = inf.optString("status");//商品的状态( 0进行中   1等待揭晓  2已经揭晓 )
                        int shareCount = inf.optInt("shareCount");
                        if(shareCount==0){
                            tv_shaidan.setText("商品晒单");
                        }else
                        tv_shaidan.setText("商品晒单"+"("+inf.optInt("shareCount")+")");
                        setBottomBtn(goodStatus);
                        if(goodStatus.equals("1")){
                            rl_countdown.setVisibility(View.VISIBLE);
                            btn_check_detail.setText("第"+inf.optString("newsCloudNo")+"(正在进行中...)");
                            newsCloudGoodsId = inf.optString("newsCloudGoodsId");
                            btn_check_detail.setOnClickListener(GoodsDetailActivity.this);
                            String announceTime = inf.optString("publicTime");
                            tv_announced_time.setText("揭晓时间："+announceTime);
                            mdt = new MyCountDownTimer(CommonUtils.comparedata(announceTime),60, tv_min,tv_sec,tv_msec);
                            mdt.start();
                            mdt.setOnFinishListener(new TimerFinish());
                        }else if(goodStatus.equals("2")){
                            getAnnounceInfo();
                            return;
                        }
                        int max = Integer.valueOf(total);
                        int curent = Integer.valueOf(join);
                        pb.setMax(max);
                        pb.setProgress(curent);
                        tv_goodsname.setText("(第" + cloudNo + "云)" + goodsName);
                        join_num.setText(join);
                        total_num.setText(total);
                        remain_num.setText(left);
                        tv_goods_price.setText("价值:￥" + Double.valueOf(price));

                        if(!TextUtils.isEmpty(cartCount) && !cartCount.equals("0")){
                            tv_cart_count.setVisibility(View.VISIBLE);
                            tv_cart_count.setText(cartCount);//购物车数量
                        }
                        if("1".equals(limitBuy)){
                            goodsLable.setVisibility(View.VISIBLE);
                            String buyCount = inf.optString("buyCount");//限购数量
                            tv_listmit_good.setVisibility(View.VISIBLE);
                            tv_listmit_good.setText("限购"+buyCount+"人次");
                        }
                        loader.displayImage(pic,goodImg, CommonUtils.displayImageOptions);
                        String previous_id = inf.optString("userId");//上一期中奖者id
                        if(!TextUtils.isEmpty(previous_id)) {
                            previous.setVisibility(View.VISIBLE);
                            String pre_head = Uriconfig.baseUrl + inf.optString("headImg");
                            loader.displayImage(pre_head, prev_avatar, CommonUtils.circleImageOptions);
                            tv_announced_code.setText("幸运云购码:"+inf.optString("winCode"));
                            tv_gainer_nickname.setText(inf.optString("username"));
                            tv_gainer_addr.setText("(" + inf.optString("address") + ")");
                            tv_announced_time2.setText("揭晓时间:"+inf.optString("LastPublicTime"));
                            tv_shopping_time2.setText("云购时间:"+inf.optString("LastBuyTime"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void requestError(VolleyError e) {
                ToastUtils.showShort(GoodsDetailActivity.this,e.toString());
            }
        });
    }

    private void setBottomBtn(String goodStatus) {
        if ("1".equals(goodStatus)) {
            btn_check_detail.setVisibility(View.VISIBLE);
            btn_go_shopping.setVisibility(View.GONE);
            btn_add_to_cart.setVisibility(View.GONE);
        } else{
            btn_check_detail.setVisibility(View.GONE);
        }
    }

    @Override
    public void retry() {
        //TODO:重新加载数据
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_calculate_details:
                break;
            case R.id.tv_detail:
                Intent detailIntent = new Intent(this,WebGoodsInfoActivity.class);
                detailIntent.putExtra("goodsId",goodsId);
                startActivity(detailIntent);
                break;
            case R.id.tv_shopping_record:
                Intent recordIntent = new Intent(this,JoinRecordActivity.class);
                recordIntent.putExtra("goodsId",goodsId);
                startActivity(recordIntent);
                break;
            case R.id.tv_shaidan:
                startActivity(new Intent(this,ShaidanListActivity.class).putExtra("goodsId",goodsId));
                break;
            case R.id.btn_add_to_cart:
                isAdd = true;
                AddCartAnim();
                break;
            case R.id.btn_go_shopping:
                isAdd = false;
                CartUtils.AddtoCart(this,1,goodsId);
                break;
            case R.id.layout_cart:
                isAdd = false;
                CartUtils.AddtoCart(this,1,goodsId);
                break;
            case R.id.rl_gainer_goods:
                startActivity(new Intent(this,GoodsDetailActivity.class).putExtra("goodsId",newsCloudGoodsId));
                break;
            case R.id.btn_check_detail:
                startActivity(new Intent(this,GoodsDetailActivity.class).putExtra("goodsId",newsCloudGoodsId));
                break;
            case R.id.tv_show_more:
                Intent itent = new Intent(this,AllCodeActivity.class);
                itent.putExtra("join",gainerJoinCount);
                itent.putExtra("buyTime",buyTime);
//                itent.putExtra("allCodes",allCodes);
                startActivity(itent);
                break;
        }
    }
    private void AddCartAnim(){
        int[] start_location = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
        goodImg.getLocationInWindow(start_location);// 动画开始的坐标
        ImageView buyImg = new ImageView(this);// buyImg是动画的图片
        loader.displayImage(pic,buyImg, CommonUtils.displayImageOptions);
        int[] end_loc =new int[2];
        imgv_goods_cart.getLocationOnScreen(end_loc);
        CartUtils.setAnim(this,buyImg, start_location,end_loc,goodsId);// 开始执行动画
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
    BroadcastReceiver cartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.ADD_CART.equals(intent.getAction())){
                if(!isAdd) {
                    GoodsDetailActivity.this.setResult(RESULT_OK);
                    finish();
                }else{
                    getCartCount();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
         nav = (NavigationView)findViewById(R.id.tb_goods_detail);
        nav.setClickCallback(new NavigationView.ClickCallback() {
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cartReceiver);
    }
}
