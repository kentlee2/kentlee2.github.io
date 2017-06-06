package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.haoqi.yungou.R;
import com.haoqi.yungou.Uriconfig;
import com.haoqi.yungou.adapter.ImageGridAdapter;
import com.haoqi.yungou.customView.NavigationView;
import com.haoqi.yungou.fragment.LoadingFragment;
import com.haoqi.yungou.util.Bimp;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.FileUtil;
import com.haoqi.yungou.util.ImageItem;
import com.haoqi.yungou.util.PublicWay;
import com.haoqi.yungou.util.ToastUtils;
import com.haoqi.yungou.util.UserUtils;
import com.haoqi.yungou.volley.MultipartRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaidanActivity extends Activity {
    private static final int TAKE_PHOTO = 0x000001;
    private static final int TAKE_PICTURE = 0x000002;
    private PopupWindow pop;
    private LinearLayout ll_popup;
    private GridView gridview;
    private ImageGridAdapter adapter;
    public static Bitmap bimap;
    private String id;
    private EditText et_title;
    private EditText et_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaidan);
        Init();
        getGoodsInfo();
    }
    private void getGoodsInfo() {
        ImageView iv = (ImageView)findViewById(R.id.iv_goodspic);
        TextView goodsname = (TextView)findViewById(R.id.tv_goodsname);
        TextView cloudNo = (TextView)findViewById(R.id.tv_cloudNo);
        TextView price = (TextView)findViewById(R.id.tv_price);
        Map<String,String> map = (Map<String, String>) getIntent().getSerializableExtra("map");
        ImageLoader.getInstance().displayImage(map.get("pic"),iv, CommonUtils.displayImageOptions);
        goodsname.setText(map.get("name"));
        cloudNo.setText(map.get("winCode"));
        price.setText(map.get("price"));
    }
    public void Init() {
        id = getIntent().getStringExtra("id");
        et_title = (EditText)findViewById(R.id.et_shaidan_title);
        et_detail = (EditText)findViewById(R.id.et_detail);
        PublicWay.activityList.add(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.add_pic_selector);
        pop = new PopupWindow(this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShaidanActivity.this,
                        AlbumActivity.class);
                startActivityForResult(intent,TAKE_PICTURE);
                pop.dismiss();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        gridview = (GridView) findViewById(R.id.noScrollgridview);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(this);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == Bimp.tempSelectBitmap.size()) {
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(ShaidanActivity.this,R.anim.activity_translate_in));
                    pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(ShaidanActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", position);
                    startActivity(intent);
                }
            }
        });
    }
    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PHOTO);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (Bimp.tempSelectBitmap.size() < 5 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtil.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                    gridview.setAdapter(adapter);
                }
            case TAKE_PICTURE:
                gridview.setAdapter(adapter);
                break;
        }
    }
    protected void onRestart() {
        gridview.setAdapter(adapter);
        adapter.update();
        super.onRestart();
    }

    public void submit(View view){
        final Map<String, String> params = new HashMap<String, String>();
        List<File> fileList = new ArrayList<>();
        params.put("id",id);
        params.put("title",et_title.getText().toString());
        params.put("detail",et_detail.getText().toString());
        params.put("userId", UserUtils.getUserId());
        final Map<String, File> filemap = new HashMap<String, File>();
        ArrayList<ImageItem> tmpList = Bimp.tempSelectBitmap;
        for(ImageItem item : tmpList){
            filemap.put("fileImg", new File(item.getImagePath()));
            fileList.add(new File(item.getImagePath()));
        }
        final LoadingFragment loading = new LoadingFragment();
        loading.show(getFragmentManager(),"loading");
        MultipartRequest request = new MultipartRequest(Uriconfig.save_shaidan,new  Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                ToastUtils.showShort(ShaidanActivity.this,error.toString());
            }
        } ,   new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try {
                    JSONObject jsonobject = new JSONObject(response);
                    JSONObject inf = jsonobject.getJSONObject("inf");
                    JSONObject res = jsonobject.getJSONObject("res");
                    String status = res.getString("status");
                    if("0".equals(status)){
                        ToastUtils.showShort(ShaidanActivity.this,R.string.submit_complete);
                    }else{
                        ToastUtils.showShort(ShaidanActivity.this,res.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, "fileImg", fileList, params);
        RequestQueue mQueue = Volley.newRequestQueue(this);
        mQueue.add(request);
    }
    @Override
    protected void onStart() {
        super.onStart();
        NavigationView nav = (NavigationView)findViewById(R.id.nav_bar);
        nav.getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for(int i=0;i<PublicWay.activityList.size();i++){
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            finish();
        }
        return true;
    }
}
