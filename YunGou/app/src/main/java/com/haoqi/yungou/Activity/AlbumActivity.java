package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.haoqi.yungou.Constant;
import com.haoqi.yungou.R;
import com.haoqi.yungou.adapter.AlbumGridViewAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.util.AlbumHelper;
import com.haoqi.yungou.util.Bimp;
import com.haoqi.yungou.util.ImageBucket;
import com.haoqi.yungou.util.ImageItem;
import com.haoqi.yungou.util.PublicWay;
import com.haoqi.yungou.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends Activity implements View.OnClickListener {

    private TextView tv_photoNum;
    private ArrayList<ImageItem> dataList;
    private GridView gridView;
    private AlbumGridViewAdapter gridImageAdapter;
    private Button okButton;
    private int snap;
    public static List<ImageBucket> contentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        PublicWay.activityList.add(this);
        snap = getIntent().getIntExtra("snap", 0);
        getImageList();
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.UPDATE_ALBUM_VIEW);
        filter.addAction(Constant.FINISH_ALBUM_VIEW);
        registerReceiver(broadcastReceiver, filter);
        gridView = (GridView) findViewById(R.id.all_bum_GridView);
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList,Bimp.tempSelectBitmap);
        initListener();
        gridView.setAdapter(gridImageAdapter);
        TextView tv_empty = (TextView) findViewById(R.id.all_bum_tv_noPhoto);
        gridView.setEmptyView(tv_empty);
        okButton = (Button) findViewById(R.id.all_bum_btn_send);
        okButton.setOnClickListener(this);
        tv_photoNum = (TextView) findViewById(R.id.all_bum_tv_photoNum);//相片数量
        int curr = Bimp.tempSelectBitmap.size();
        if (snap == 1) {
            PublicWay.num = 1;
        } else if(snap == 2) {
            PublicWay.num = 2;
        } else if(snap == 3) {
            PublicWay.num = 3;
        } else if(snap == 4) {
            PublicWay.num = 4;
        }
        if(curr>0){
            tv_photoNum.setText(curr+"");
            tv_photoNum.setVisibility(View.VISIBLE);
        }else{
            tv_photoNum.setVisibility(View.GONE);
        }
    }
    /**
     * 更新和关闭广播
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                String action = intent.getAction();
                if(action.equals(Constant.UPDATE_ALBUM_VIEW)){
                    gridImageAdapter.notifyDataSetChanged();
                }else if(action.equals(Constant.FINISH_ALBUM_VIEW)){
                    finish(); //关闭
                }
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        ((NavigationView)findViewById(R.id.albume_bar)).getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initListener() {
        gridImageAdapter.setOnSelectClickListener(
                new AlbumGridViewAdapter.OnSelectClickListener() {
                    @Override
                    public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked,TextView tv_choosed) {
                        if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
                            toggleButton.setChecked(false);
                            tv_choosed.setBackgroundResource(R.drawable.plugin_camera_cancle_img);
                            if (!removeOneData(dataList.get(position))) {
                                ToastUtils.showShort(AlbumActivity.this,"图片最多选择4张");
                            }
                            return;
                        }
                        ImageItem item = dataList.get(position);
                        if (isChecked) {
                            tv_choosed.setBackgroundResource(R.drawable.plugin_camera_choosed);
                            Bimp.tempSelectBitmap.add(item);
                        } else {
                            Bimp.tempSelectBitmap.remove(item);
                            tv_choosed.setBackgroundResource(R.drawable.plugin_camera_cancle_img);
                        }
                        isShowOkBt();
                    }
                }

        );
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(AlbumActivity.this,ShowImageActivity.class);
                intent.putExtra("image", "file://"+item.getImagePath());
                startActivity(intent);
            }
        });
    }
    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            tv_photoNum.setText(Bimp.tempSelectBitmap.size()+"");
            return true;
        }
        return false;
    }
    private void getImageList() {
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for(int i = 0; i<contentList.size(); i++){
            dataList.addAll(contentList.get(i).imageList );
        }
    }
    public void isShowOkBt() {
        tv_photoNum.setText(""+Bimp.tempSelectBitmap.size());
        if (Bimp.tempSelectBitmap.size() > 0) {
            tv_photoNum.setVisibility(View.VISIBLE);
            okButton.setClickable(true);
        } else {
            tv_photoNum.setVisibility(View.GONE);
            okButton.setClickable(false);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.all_bum_btn_send){
            if (Bimp.tempSelectBitmap.size() > 0) {
                setResult(RESULT_OK);//返回数据
                finish();
                //overridePendingTransition(0,R.anim.out_to_down);
            }else{
                ToastUtils.showShort(this,"请选择图片");
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
