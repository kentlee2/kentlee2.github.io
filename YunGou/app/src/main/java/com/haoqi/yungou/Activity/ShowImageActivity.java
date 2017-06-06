package com.haoqi.yungou.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.widget.zoom.PhotoView;
import com.haoqi.yungou.widget.zoom.PhotoViewAttacher;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShowImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        String url = getIntent().getStringExtra("image");
        PhotoView bigImage = (PhotoView) findViewById(R.id.bigImage);
        ImageLoader.getInstance().displayImage(url,bigImage, CommonUtils.displayImageOptions);
        bigImage.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
    }
}
