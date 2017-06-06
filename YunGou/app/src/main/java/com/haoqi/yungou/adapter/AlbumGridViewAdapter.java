package com.haoqi.yungou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.haoqi.yungou.R;
import com.haoqi.yungou.util.CommonUtils;
import com.haoqi.yungou.util.ImageItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 * 
 */
public class AlbumGridViewAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	private final ArrayList<ImageItem> selectedList;
	private final Context context;
	private final LayoutInflater inflater;
	private final ImageLoader imageLoader;
	private final ArrayList<ImageItem> mDatas;
	private LayoutParams params ;
	private AlbumGridViewAdapter.AlbumHolder holder;

	public AlbumGridViewAdapter(Context context, ArrayList<ImageItem> mDatas, ArrayList<ImageItem> dataList) {
		this.context =context;
		this.selectedList =dataList;
		inflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
		imageLoader = ImageLoader.getInstance();
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300);
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int i) {
		return mDatas.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		if(convertView==null){
			holder = new AlbumHolder();
			convertView = inflater.inflate(R.layout.album_grid_item, null);
			holder.btn_toggle = (ToggleButton) convertView.findViewById(R.id.album_grid_item_btn_toggle);
			holder.tv_choosed =(TextView)convertView.findViewById(R.id.album_grid_item_tv_choosed);
			holder.img = (ImageView)convertView.findViewById(R.id.album_grid_item_iv);
			convertView.setTag(holder);
		}else{
			holder = (AlbumHolder) convertView.getTag();
		}
		holder.img.setTag(mDatas.get(position).getImagePath());
		imageLoader.displayImage("file://"+mDatas.get(position).getImagePath(), holder.img, CommonUtils.displayImageOptions);
		holder.btn_toggle.setTag(position);
		holder.tv_choosed.setTag(position);
		holder.btn_toggle.setOnClickListener(new ToggleClickListener(holder.tv_choosed));
		if(!selectedList.isEmpty()) {
			if (selectedList.contains(mDatas.get(position))) {
				holder.btn_toggle.setChecked(true);
				holder.tv_choosed.setBackgroundResource(R.drawable.plugin_camera_choosed);
			} else {
				holder.btn_toggle.setChecked(false);
				holder.tv_choosed.setBackgroundResource(R.drawable.plugin_camera_cancle_img);
			}
		}
		return convertView;
	}

    class AlbumHolder{

		public ToggleButton btn_toggle;
		public TextView tv_choosed;
		public ImageView img;
	}
	private class ToggleClickListener implements OnClickListener {
		TextView tv_choosed;
		public ToggleClickListener(TextView choosebt){
			this.tv_choosed = choosebt;
		}
		
		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (mDatas != null && mOnItemClickListener != null
						&& position < mDatas.size()) {
					mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(),tv_choosed);
				}
			}
		}
	}
	

	private OnSelectClickListener mOnItemClickListener;

	public void setOnSelectClickListener(OnSelectClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnSelectClickListener {
		public void onItemClick(ToggleButton view, int position, boolean isChecked, TextView tv_choosed);
	}
	
}
