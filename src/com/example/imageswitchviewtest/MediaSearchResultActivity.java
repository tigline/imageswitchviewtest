package com.tcl.launcher.ui;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tcl.launcher.R;
import com.tcl.launcher.adapter.VideoListAdapter;
import com.tcl.launcher.base.BaseListActivity;
import com.tcl.launcher.core.VoiceControl;
import com.tcl.launcher.entity.VideoListItem;
import com.tcl.launcher.entity.json.MovieListBean;
import com.tcl.launcher.entity.json.MultiDomain;
import com.tcl.launcher.entity.json.ResponseData;
import com.tcl.launcher.entity.json.VideoBean;
import com.tcl.launcher.entity.json.VoiceResponse;
import com.tcl.launcher.util.TLog;
import com.tcl.launcher.util.Utils;

/**
 * @author caomengqi/caomengqi@tcl.com
 * 2015年11月25日
 * @JDK version 1.8
 * @brief 搜索视频结果列表界面
 * @version
 */
public class MediaSearchResultActivity extends BaseListActivity {
	private static final String TAG = "MediaSearchResultActivity";

	private VoiceResponse mVoiceResponse;
	private List<VideoBean> mVideoList;

	private FrameLayout mDomainLayout;
	private LinearLayout mDomainList;
	private TextView[] mDomainListItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_search_result);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mVoiceResponse = (VoiceResponse) getIntent().getExtras().getSerializable(
					VoiceControl.CMD_PAGE);
		}
		findViews();
		setViews();
	}

	private void findViews() {
		mGridView = (GridView) findViewById(R.id.media_result_gridview);
		mGridView.setWillNotDraw(false);
		mDomainList = (LinearLayout) findViewById(R.id.media_result_domain_listview);
		mDomainLayout = (FrameLayout) findViewById(R.id.media_result_domain_layout);
	}

	private void setViews() {
		Utils.recordCurrentTime("setViews");
		if (mVoiceResponse != null) {
			Object element = mVoiceResponse.getData().getResult();
			Gson gson = new Gson();
			MovieListBean listBean = gson.fromJson(gson.toJson(element), MovieListBean.class);
			mVideoList = Arrays.asList(listBean.getList());
			List<VideoListItem> videoList = VideoListItem.getListFromVideobean(mVideoList);
			VideoListAdapter<VideoListItem> adapater = new VideoListAdapter<VideoListItem>(
					videoList, this);
			mGridView.setAdapter(adapater);
			
			MultiDomain[] domains = mVoiceResponse.getMulti_total();
			setMultiDomain(domains);
		}
	}

	private void setMultiDomain(MultiDomain[] domains) {
		if (domains != null) {
			mDomainLayout.setVisibility(View.VISIBLE);
			addDomainView(domains);
		} else {
			mDomainLayout.setVisibility(View.GONE);
		}
	}

	private void addDomainView(MultiDomain[] domains) {
		mDomainList.removeAllViews();
		mDomainListItem = new TextView[domains.length];

		LinearLayout.LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		int margin = (int) getResources().getDimension(R.dimen.video_list_multi_text_margin);
		textParams.topMargin = margin;
		textParams.bottomMargin = margin;

		for (int i = 0; i < domains.length; i++) {
			if (i != 0) {
				View v = new View(this);
				v.setBackgroundResource(R.color.gray);
				LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 4);
				mDomainList.addView(v, params);
			}
			mDomainListItem[i] = new TextView(this);
			mDomainListItem[i].setTextAppearance(this, R.style.DomainText);
			mDomainListItem[i].setGravity(Gravity.CENTER_HORIZONTAL);
			mDomainListItem[i].setText(domains[i].getKey() + "      "  + domains[i].getValue());
			mDomainList.addView(mDomainListItem[i], textParams);
		}
	}

	public void updateList(VoiceResponse response) {
		if (response != null) {
			mVoiceResponse = response;
			Object element = response.getData().getResult();
			Gson gson = new Gson();
			setMultiDomain(response.getMulti_total());
			setPageId(getPageIdFromCommand(response.getCommand(), response.getDomain()));
			MovieListBean listBean = gson.fromJson(gson.toJson(element), MovieListBean.class);
			List<VideoBean> newList = Arrays.asList(listBean.getList());
			List<VideoListItem> videoList = VideoListItem.getListFromVideobean(newList);
			VideoListAdapter<VideoListItem> adapater = (VideoListAdapter<VideoListItem>) mGridView
					.getAdapter();
			if (adapater == null) {
				// 如果原适配器为空，重新生成新的adapter加载
				adapater = new VideoListAdapter<VideoListItem>(videoList, this);
				mGridView.setAdapter(adapater);
			} else {
				int currentPageIndex = adapater.getIndex() + 1;
				// adapter不为空，数据的加减和列表的显示直接在原来的adapter上进行操作
				int pageIndex = getPageIndexFromData(response.getData());
				if (pageIndex == 1) {
					adapater.setIndex(0);
					adapater.removeAll();
					adapater.addToBottom(videoList);
					return;
				}
				TLog.i(TAG, "list refresh :   currentPageIndex" + currentPageIndex + ", pageIndex : " + pageIndex);
				if (pageIndex == currentPageIndex) {
					// 页面index与之前的相同不操作
					return;
				} else if (pageIndex > currentPageIndex) {
					// 跳转到下一批的界面
					// 如果adapter的总数已经超过目标index的总数，直接跳转到index处，否则加入新的视频，再进行跳转
					int count = adapater.getCount();
					int pageCount = (count % LIST_COUNT_PER_PAGE == 0) ? (count / LIST_COUNT_PER_PAGE)
							: (count / LIST_COUNT_PER_PAGE + 1);
					if (pageCount > pageIndex + 1) {

					} else {
						Utils.removeFromList(videoList, true, LIST_COUNT_PER_PAGE);
						if (videoList.size() != 0) {
							adapater.addToBottom(videoList);
						}
					}
					pageChanged(true, adapater);
//					adapater.incrementIndex();
				} else if (pageIndex < currentPageIndex) {
					// 回退到之前的页面
					pageChanged(false, adapater);
//					adapater.decrementIndex();
				}
			}
		}
	}

	private int getPageIndexFromData(ResponseData data) {
		int result = 0;
		String pageIndexStr = data.getPn();
		if (pageIndexStr != null) {
			result = Integer.valueOf(pageIndexStr);
		}
		return result;
	}
}
