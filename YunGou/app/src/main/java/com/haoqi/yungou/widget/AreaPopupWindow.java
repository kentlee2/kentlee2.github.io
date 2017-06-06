package com.haoqi.yungou.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haoqi.yungou.R;

/**
 * 省市县选择PopupWindow
 * Created by Kentlee on 2016/9/29.
 */
public class AreaPopupWindow extends PopupWindow {
    private CityPicker cityPicker;
    private TextView Select_Ok,Select_Cancel;

    public AreaPopupWindow(Context context) {
        super(context);
      initView(context);
    }

    private void initView(Context context) {
        LayoutInflater mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cityPopView = mLayoutInflater.inflate(R.layout.select_city_pop_main_layout, null);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 必须设置background才能消失
        setBackgroundDrawable(context.getResources().getDrawable(
                R.drawable.fullscreen_share_bg));
        setOutsideTouchable(true);
        // 自定义动画
//			mCityPop.setAnimationStyle(R.style.tudou_encrypt_dialog);
        // 使用系统动画
        setAnimationStyle(R.style.mypopwindow_anim_style);

        cityPicker=(CityPicker) cityPopView.findViewById(R.id.citypicker);
        //省市县文字
        final TextView sheng_Text = (TextView) cityPopView.findViewById(R.id.get_sheng);
        final TextView shi_Text=(TextView) cityPopView.findViewById(R.id.get_shi);
        final TextView xian_Text=(TextView) cityPopView.findViewById(R.id.get_xian);

         Select_Ok=(TextView) cityPopView.findViewById(R.id.Select_City_Ok);
         Select_Cancel=(TextView) cityPopView.findViewById(R.id.Select_City_Cancel);

        sheng_Text.setText(cityPicker.getProvince());
        shi_Text.setText(cityPicker.getCity());
        xian_Text.setText(cityPicker.getCountry());
        cityPicker.setCity(new CityPicker.CityCall() {
            @Override
            public void cityAll(String sheng, String shi, String xian) {
                sheng_Text.setText(cityPicker.getProvince());
                shi_Text.setText(cityPicker.getCity());
                xian_Text.setText(cityPicker.getCountry());
            }
        });
        // popWindow消失监听方法
       setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                backgroundAlpha(1f);
            }
        });
        setContentView(cityPopView);
        update();
       setTouchable(true);
        setFocusable(true);
    }

    public TextView getSelect_Ok() {
        return Select_Ok;
    }

    public TextView getSelect_Cancel() {
        return Select_Cancel;
    }
    public String getAdress(){
       return cityPicker.getAreaText();
    }
}
