package com.tcl.launcher.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tcl.launcher.R;
import com.tcl.launcher.base.LauncherBaseAdapter;
import com.tcl.launcher.entity.VideoListItem;
import com.tcl.launcher.entity.json.VideoBean;
import com.tcl.launcher.util.ImgLoader;
import com.tcl.launcher.widget.CircleImageView;

public class VideoListAdapter<T> extends LauncherBaseAdapter<T> {
	public static final int COLUMN_COUNT = 6;
	
	private LayoutInflater mInflater;
	private ImgLoader mImgLoader;
	
	private int mIndex = 0;

	public VideoListAdapter(List<T> list, Context context) {
		super(list, context);
		mInflater = LayoutInflater.from(context);
		mImgLoader = ImgLoader.getInstance(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getAdapterView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.media_list_item, null);
			holder = new ViewHolder();

			holder.name = (TextView) convertView
					.findViewById(R.id.media_result_gridview_list_item_name);
			holder.post = (CircleImageView) convertView
					.findViewById(R.id.media_result_gridview_list_item_img);
			holder.tag = (TextView) convertView
					.findViewById(R.id.media_result_gridview_list_item_tag);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		VideoListItem video = (VideoListItem) getItem(position);
		holder.name.setText(video.getText());
		mImgLoader.loadImage(video.getImgUrl(), holder.post, R.drawable.video_info_default_img,
				R.drawable.video_info_default_img);

		return convertView;
	}
	
	/**
	 * 下一行
	 */
	public void toNext() {
		if ((mIndex + 1) * COLUMN_COUNT < mList.size()) {
			mIndex++;
			this.notifyDataSetChanged();
		}
		this.notifyDataSetChanged();
	}
	
	/**
	 * 上一行
	 */
	public void toPre() {
		if (mIndex > 0) {
			mIndex--;
			this.notifyDataSetChanged();
		}
		this.notifyDataSetChanged();
	}

	class ViewHolder {
		CircleImageView post;
		TextView name;
		TextView tag;
	}
	
	/**
	 * 是否还有下一行
	 * @return
	 */
	public boolean hasNext() {
		return (mIndex + 1) * COLUMN_COUNT < mList.size() ? true : false;
	}
	
	/**
	 * 是否还有上一行
	 * @return
	 */
	public boolean hasPre() {
		return mIndex > 0 ? true : false;
	}
	
	public int getPageCount(){
		int count = 0;
		if(mList != null){
			count = (mList.size() % COLUMN_COUNT == 0)? (mList.size() / COLUMN_COUNT) : (mList.size() / COLUMN_COUNT + 1);
		}
		return count;
	}
	
	@Override
	public int getCount() {
		if (mList == null)
			return 0;
		int size = mList.size();
		if ((mIndex + 2) * COLUMN_COUNT > size) {
			return size - (mIndex * COLUMN_COUNT);
		} else {
			return COLUMN_COUNT * 2;
		}
	}

	@Override
	public Object getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(mIndex * COLUMN_COUNT + position);
	}
	
	
	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int mIndex) {
		this.mIndex = mIndex;
	}
	
	public void incrementIndex() {
		mIndex++;
	}
	
	public void decrementIndex() {
		mIndex--;
	}
}
