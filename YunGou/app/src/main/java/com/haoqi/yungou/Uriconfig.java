package com.haoqi.yungou;

/**
 * Created by Administrator on 2016/9/18.
 */
public class Uriconfig {
   public static String baseUrl ="http://www.gzleyg.com/cloudBuy/";
   public static String getHomeList = baseUrl+"index!ajaxGetIndex";
   public static String getAnounceList = baseUrl+"index!ajaxGetnewCloud";//首页最近揭晓
   public static String getGoodsList = baseUrl+"goods!goodList";//所有商品列表
   public static String getGoodsDetail = baseUrl+"goods!detailGood";//商品详情
   public static String getPublicDetail = baseUrl+"goods!publicGoodsDetail";//商品揭晓详情
   public static String getDetailImg = baseUrl+"goods!detailImg";//图文详情
   public static String NewestList = baseUrl+"index!ajaxnewCloudGoodsList";//最新揭晓列表
   public static String History_JxList = baseUrl+"index!winGoodsList";//已揭晓列表

   public static String GoodsTypeList = baseUrl+"index!goodsType";//所有商品分类列表
   public static String AddToCart = baseUrl+"order!addCar";//添加到购物车
   public static String register_code = baseUrl+"login/login!ajaxGetCode";//用户注册验证手机号码
   public static String checkCode = baseUrl+"login/login!ajaxCheckPhoneCode";//手机验证码验证接口
   public static String register_setpwd = baseUrl+"login/login!ajaxAddUser";//注册设置密码
   public static String find_pwd_getCode = baseUrl+"login/login!getPhoneCode";//找回密码获取验证码
   public static String find_pwd_resetPwd = baseUrl+"login/login!ajaxEditPw";//找回密码重新设置密码
   public static String resetPwd = baseUrl+"login/login!ajaxUserEditPw";//重新设置密码

   public static String user_login = baseUrl+"login/login!ajaxUserLogin";//用户登录
   public static String user_info = baseUrl+"user/userIndex!ajaxGetUserIndex";//用户个人信息
   public static String edit_info = baseUrl+"user/userIndex!ajaxEditUser";//编辑个人信息
   public static String cart_list = baseUrl+"shoppingCar!shoppingList";//购物车列表
   public static String search_goods = baseUrl+"goods!goodList";//商品搜索
   public static String recent_search = baseUrl+"goods!recentlySelect";//最近搜索
   public static String delete_search = baseUrl+"goods!clearSearchLog";//清除搜索
   public static String hot_search = baseUrl+"goods!hotSelect";//热门搜索
   public static String my_record = baseUrl+"user/userIndex!ajaxGetMyOrder";//我的云购记录
   public static String my_record_detail = baseUrl+"user/userIndex!ajaxGetOrderDetail";//我的云购记录详情
   public static String my_adress = baseUrl+"user/userIndex!ajaxGetMyAddress";//我的收货地址
   public static String add_adress = baseUrl+"user/userIndex!ajaxCreateAddress";//添加收货地址
   public static String edit_adress = baseUrl+"user/userIndex!ajaxEditAddress";//编辑收货地址
   public static String delete_adress = baseUrl+"user/userIndex!ajaxDelAddress";//删除收货地址
   public static String help_center = baseUrl+"user/help!ajaxGetHelpContent";//帮助中心
   public static String about_us = baseUrl+"user/safe!ajaxGetAbout";//关于我们
   public static String service_agreement = baseUrl+"user/safe!ajaxGetService";//服务协议
   public static String feedback = baseUrl+"user/help!ajaxAddSuggestion";//意见反馈
   public static String cartdelete = baseUrl+"shoppingCar!ajaxdelete";//购物车删除
   public static String makeOrder = baseUrl+"order!makeSure";//确认订单
   public static String createOrder = baseUrl+"order!createOrder";//下订单
   public static String recharge = baseUrl+"charge!createChargeOrder";//充值下单
   public static String obtain_goods = baseUrl+"user/userIndex!ajaxGetMyGoods";//获得的商品
   public static String Accnount_detail = baseUrl+"user/userIndex!ajaxGetMyAccount";//账户明细
   public static String join_record = baseUrl+"goods!joinRecord";//参与记录
   public static String my_shaidan = baseUrl+"user/userIndex!ajaxGetMyShare";//我的晒单列表
   public static String all_shaidan = baseUrl+"goods!shareOrderList";//所有晒单列表
   public static String save_shaidan = baseUrl+"user/userIndex!ajaxAddShare";//保存我的晒单
   public static String shaidan_detail = baseUrl+"user/userIndex!ajaxShareDetail";//晒单详情
   public static String praise = baseUrl+"user/userIndex!ajaxToPraise";//点赞
   public static String all_comment = baseUrl+"user/userIndex!ajaxComments";//晒单评论列表
   public static String send_comment = baseUrl+"user/userIndex!ajaxSendComment";//晒单评论
   public static String goods_address = baseUrl+"order!editAddress";//保存商品收货地址
   public static String receive_goods = baseUrl+"order!sucGoods";//确认收货
   public static String check_express = baseUrl+"goods!goodsLogistics";//物流

}
