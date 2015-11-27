/**
 * 
 */
package com.example.imageswitchviewtest;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @Project ImageSwitchViewTest	
 * @author houxb
 * @Date 2015-11-27
 */
public class Video3DListAdapter<T> extends LauncherBaseAdapter<T>{

	public static final int COLUMN_COUNT = 6;
	
	private LayoutInflater mInflater;
	private ImgLoader mImgLoader;
	private Context mContext;
	private int mIndex = 0;
	/**
	 * @param list
	 * @param context
	 */
	public Video3DListAdapter(List<T> list, Context context) {
		super(list, context);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mImgLoader = ImgLoader.getInstance(context);
	}

	/* (non-Javadoc)
	 * @see com.example.imageswitchviewtest.LauncherBaseAdapter#getAdapterView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams") @SuppressWarnings({ "unchecked", "null" })
	@Override
	public View getAdapterView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			//convertView = mInflater.inflate(R.layout.media_list_item, null);
			holder = new ViewHolder();

			holder.name = (TextView) LayoutInflater
					.from(mContext).inflate(R.layout.voice_item_text, null);
			holder.post = (CircleImage3DView) LayoutInflater
					.from(mContext).inflate(R.layout.circle_item, null);

			holder.tag = (TextView) (TextView) LayoutInflater
					.from(mContext).inflate(R.layout.voice_item_text, null);
			convertView = holder.post;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		VideoListItem video = (VideoListItem) getItem(position);
//		holder.name.setText(video.getText());
//		mImgLoader.loadImage(video.getImgUrl(), holder.post, R.drawable.empty_photo,
//				R.drawable.empty_photo);

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

	class CircleViewItem extends View{

		/**
		 * @param context
		 */
		public CircleViewItem(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	class ViewHolder {
		CircleImage3DView post;
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
