package com.haoqi.yungou.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.Bimp;
import com.haoqi.yungou.util.CommonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/9/6.
 */
public class ImageGridAdapter extends BaseAdapter {
    private final ImageLoader imgLoader;
    private final LayoutInflater inflate;
    private final Context context;
    private final ImageLoader imageLoader;

    public ImageGridAdapter(Context context) {
        this.context = context;
        imgLoader = ImageLoader.getInstance();
        inflate = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    public void update() {
        loading();
    }
    @Override
    public int getCount() {
        if(Bimp.tempSelectBitmap.size() == 4){
            return 4;
        }
        return (Bimp.tempSelectBitmap.size() + 1);
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = inflate.inflate(R.layout.item_published_grida, viewGroup,false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position ==Bimp.tempSelectBitmap.size()) {
            holder.image.setBackgroundResource(R.drawable.add_pic_selector);
            if (position == 9) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
//            holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            imageLoader.displayImage("file://"+Bimp.tempSelectBitmap.get(position).getImagePath(),holder.image, CommonUtils.displayImageOptions);
        }
        return convertView;
    }
    class ViewHolder{

        public ImageView image;
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void loading() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        break;
                    } else {
                        Bimp.max += 1;
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }
}