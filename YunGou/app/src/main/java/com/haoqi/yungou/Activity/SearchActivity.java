package com.haoqi.yungou.Activity;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.fragment.HotSearchFragment;
import com.haoqi.yungou.fragment.RecentSearchFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends FragmentActivity implements View.OnClickListener {
    private ViewPager viewPager;// 页卡内容
    private ImageView imageView;// 动画图片
    private List<Fragment> fragments;// Tab页面列表
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private static final int pageSize = 2;
    private EditText et_search;
    private HotSearchFragment hotFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findbyId();
        initView();
    }
    private void findbyId() {
        ImageView iv_search_back =(ImageView)findViewById(R.id.iv_search_back);
        TextView tv_search =(TextView)findViewById(R.id.tv_goods_search);
        Button btn_recent =(Button)findViewById(R.id.btn_recent);
        Button btn_hot =(Button)findViewById(R.id.btn_hot);
         et_search =(EditText)findViewById(R.id.et_search_content);
        viewPager =(ViewPager)findViewById(R.id.fragment_container);
        iv_search_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        btn_hot.setOnClickListener(this);
        btn_recent.setOnClickListener(this);
    }
    private void initView() {
        fragments = new ArrayList<Fragment>();
        RecentSearchFragment recenFragment = new RecentSearchFragment();
         hotFragment = new HotSearchFragment();
        hotFragment.setUserVisibleHint(false);
        fragments.add(recenFragment);
        fragments.add(hotFragment);
        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager(),
                fragments));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        InitImageView();
    }
    private void InitImageView() {
        imageView = (ImageView) findViewById(R.id.cursor);
        bmpW = imageView.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / pageSize - bmpW) / 2;// 计算偏移量--(屏幕宽度/页卡总数-图片实际宽度)/2
        // = 偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置
    }
    /**
     * 为选项卡绑定监听器
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int index) {
        }

        public void onPageScrolled(int position, float offset, int offsetPixels) {
        }

        public void onPageSelected(int index) {
            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
            currIndex = index;
            Animation animation = new TranslateAnimation(one * currIndex, one
                    * index, 0, 0);// 显然这个比较简洁，只有一行代码。
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
        }
    }

    /**
     * 定义适配器
     */
    class myPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null
                    : fragmentList.get(arg0);
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search_back:
                finish();
                break;
            case R.id.btn_recent:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_hot:
                viewPager.setCurrentItem(1);
                hotFragment.setUserVisibleHint(false);
                break;
            case R.id.tv_goods_search:
                startActivity(new Intent(this,SearchListActivity.class).putExtra("str",et_search.getText().toString()));
                break;
        }
    }
}
