package com.haoqi.yungou.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.haoqi.yungou.Constant;
import com.haoqi.yungou.GlobalApplication;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.domain.User;
import com.haoqi.yungou.domain.UserData;
import com.haoqi.yungou.volley.RequestJsonListener;
import com.haoqi.yungou.volley.RequestParams;
import com.haoqi.yungou.volley.VolleyRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Kentlee on 2016/9/22.
 */
public class UserUtils {
    //获取个人信息
    public static User saveUserInfo(final TextView userName,final ImageView user_header,final TextView my_balance,
                                    final TextView userPhone, final TextView remain_amount){
        RequestParams params = new RequestParams();
        params.put("userId",getUserId());
        final Context context =GlobalApplication.getContext();
        VolleyRequest.post(context, Uriconfig.user_info,UserData.class, params,new RequestJsonListener<UserData>()  {
            @Override
            public void requestSuccess(UserData result) {
                User user=result.getInf();
                String status =result.getRes().get("status").toString();
                if("0".equals(status)){
                    DBManager.getInstance().saveContact(user);//保存个人信息到本地
                    userName.setText(user.getUserName());
                    my_balance.setText(user.getScore());
                    userPhone.setText("("+user.getPhone()+")");
                    remain_amount.setText(user.getAmout());
                    if(user.getHeadImg()!=null)
                    ImageLoader.getInstance().displayImage(Uriconfig.baseUrl+user.getHeadImg(),user_header, CommonUtils.circleImageOptions);
                }
            }

            @Override
            public void requestError(VolleyError e) {
               GlobalApplication.getInstance().toastShortMsg(R.string.network_fail);
            }
        });
        return new User();
    }
    public static String getUserId(){
        return GlobalApplication.getInstance().GetValue(Constant.USERID,"");
    }
    public static String getUserName(){
        return GlobalApplication.getInstance().GetValue(Constant.USERNAME,"");
    }
    public static String getPhone(){
        return GlobalApplication.getInstance().GetValue(Constant.PHONE,"");
    }
    public static String getNickName(){
        User user = DBManager.getInstance().getUser();
        return user.getUserName();
    }
    public static boolean isLogined(){
        return GlobalApplication.getInstance().isLogined();
    }
    public static void logout(){
        GlobalApplication.getInstance().logout();
    }
}
