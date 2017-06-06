package com.haoqi.yungou.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.RequestListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.haoqi.yungou.widget.AddAndSubView;
import com.haoqi.yungou.widget.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Kentlee on 2016/9/26.
 */
public class CartAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Map> cartList;
    private final LayoutInflater inflate;
    private final ImageLoader imageLoader;
    private CartHolder holder;
    private boolean isChange;
    private int currentNum;

    public CartAdapter(Context context, ArrayList cartList) {
        this.context =context;
        this.cartList = cartList;
        inflate = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Map getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = new CartHolder();
        if(convertView==null){
            convertView = inflate.inflate(R.layout.item_cart,null);
            holder.goodsImg =(ImageView)convertView.findViewById(R.id.iv_goodspic);
            holder.goodsLable =(ImageView)convertView.findViewById(R.id.iv_goods_label_cart);//限购
            holder.goodsName =(TextView)convertView.findViewById(R.id.tv_goodsname);
            holder.tv_isupdate =(TextView)convertView.findViewById(R.id.tv_isupdate);
            holder.surplusCount =(TextView)convertView.findViewById(R.id.tv_surplus_count);//剩余人数
            holder.et_count =(TextView)convertView.findViewById(R.id.et_count);//参与人次
            holder.tv_count =(TextView)convertView.findViewById(R.id.tv_count);//价格
            holder.cart_delete =(ImageButton)convertView.findViewById(R.id.ibtn_cart_delete);//价格
            holder.goodsLable.setVisibility(View.GONE);
            holder.tv_isupdate.setVisibility(View.GONE);
            convertView.setTag(holder);
        }else{
            holder = (CartHolder)convertView.getTag();
        }
        Map map = getItem(position);
        imageLoader.displayImage(map.get("goodPic").toString(),holder.goodsImg, CommonUtils.displayImageOptions);
        holder.goodsName.setText("第("+map.get("goodCloudNo")+"云)"+map.get("goodName").toString());
        final String goodLeftCount=map.get("goodLeftCount").toString();
        final String joinCount = map.get("count").toString();
        final String cloudGoodsId = map.get("cloudGoodsId").toString();
        final String shoppingCarId = map.get("shoppingCarId").toString();
        holder.surplusCount.setText("剩余"+goodLeftCount+"人次");
        holder.et_count.setText(joinCount);
        holder.tv_count.setText(Double.valueOf(map.get("goodPrice").toString())+"元");
        holder.cart_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.Builder dialog=new CustomDialog.Builder(context);
                dialog.setMessage(R.string.delete_sure)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                deleteCart(position,shoppingCarId);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

            }
        });
        holder.et_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(goodLeftCount,joinCount,cloudGoodsId,position);
            }
        });
        return  convertView;
    }

    private void showDialog(String count, String joinCount, final String cloudGoodsId,final int position) {
        CustomDialog.Builder dialog=new CustomDialog.Builder(context);
        dialog.setTitle(R.string.join_num);
        dialog.setMessage("剩余"+count+"人次");
        int joinNum=Integer.valueOf(joinCount);
//        View view = inflate.inflate(R.layout.dialog_editcart,null);
        final AddAndSubView subView = new AddAndSubView(context,joinNum);
        subView.setMax(Integer.valueOf(count));
        subView.setNum(joinNum);
        dialog.setView(subView);
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 changeCart(cloudGoodsId,subView.getNum(),dialog,position);
            }
        });
        dialog.create().show();
    }

    private void changeCart(String shoppingCarId, final int num, final DialogInterface dialog, final int position) {
            RequestParams params = new RequestParams();
            params.put("userId", UserUtils.getUserId());
            params.put("carType","1");// 0代表普通加入购物车   1在购物车列表中改变商品数量
            params.put("carGoodsId",shoppingCarId);//商品id
            params.put("carCount",num+"");
            VolleyRequest.post(context, Uriconfig.AddToCart, params, null,new RequestListener() {
                @Override
                public void requestSuccess(String json) {

                    try {
                        JSONObject jsonobject = new JSONObject(json);
                        JSONObject inf = jsonobject.getJSONObject("inf");
                        JSONObject	res = jsonobject.getJSONObject("res");
                        String status = res.getString("status");
                        dialog.dismiss();
                        if("0".equals(status)){
                            String count = inf.getString("carSize");
                            holder.et_count.setText(count);
                            cartList.get(position).put("count",num);
                            context.sendBroadcast(new Intent(Constant.CART_CHANGED));
                            notifyDataSetChanged();
                        }else{
                            Toast.makeText(context,res.getString("errMsg"),Toast.LENGTH_SHORT).show();
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

    private void deleteCart(final int position, String shoppingCarId) {
        RequestParams params = new RequestParams();
        params.put("userId", UserUtils.getUserId());
        params.put("shoppingCarId", shoppingCarId);
        VolleyRequest.post(context, Uriconfig.cartdelete, params, new RequestListener() {
            @Override
            public void requestSuccess(String json) {

                try {
                    JSONObject jsonobject = new JSONObject(json);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject	res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        cartList.remove(position);
                        context.sendBroadcast(new Intent(Constant.CART_CHANGED));
                        notifyDataSetChanged();
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

    class CartHolder{
        public ImageView goodsImg;
        public ImageView goodsLable;
        public TextView goodsName;
        public TextView surplusCount;
        public TextView et_count;
        public TextView tv_count;
        public ImageButton cart_delete;
        public TextView tv_isupdate;
    }
}
