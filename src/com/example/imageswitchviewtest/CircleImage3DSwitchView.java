
package com.example.imageswitchviewtest;


import java.util.ArrayList;
import java.util.List;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;








/**
 * 3D图片轮播器主控件。
 * 

 */
public class CircleImage3DSwitchView extends ViewGroup {


	/**
	 * 图片左右两边的空白间距 
	 */
	public static final int IMAGE_PADDING = 10;
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int LIST_COUNT_PER_PAGE = 6;
	private boolean CANSCROLLTONEXT = false;
	private boolean CANSCROLLTOPREV = false;
	private boolean isDelete = false;
	private boolean isFromService = false;
	private float [] scale = {};
	private final float [] scale0 = {0.7f, 0.8f, 1.2f, 1.4f, 1.1f, 0.8f};
	private final float [] scale1 = {0.8f, 1.1f, 1.4f, 1.2f, 0.8f, 0.7f};
	private final float [] scale2 = {0.7f, 0.8f, 1.4f, 1.2f, 1.1f, 0.8f};
	/**
	 * 滚动到下一张图片的速度
	 */
	private static final int SNAP_VELOCITY = 600;
	/**
	 * 表示滚动到下一张图片这个动作
	 */
	private static final int SCROLL_NEXT = 0;
	/**
	 * 表示滚动到上一张图片这个动作
	 */
	private static final int SCROLL_PREVIOUS = 1;
	/**
	 * 表示滚动回原图片这个动作
	 */
	private static final int SCROLL_BACK = 2;
	private static Handler handler = new Handler();
	private boolean isInit = false;
	/**
	 * 控件宽度
	 */
	public static int mWidth;
	private VelocityTracker mVelocityTracker;
	private Scroller mScroller;
	/**
	 * 图片滚动监听器，当图片发生滚动时回调这个接口
	 */
	private OnImageSwitchListener mListener;
	/**
	 * 记录当前的触摸状态
	 */
	private int mTouchState = TOUCH_STATE_REST;
	/**
	 * 记录被判定为滚动运动的最小滚动值
	 */
	private int mTouchSlop;
	/**
	 * 记录控件高度
	 */
	public ArrayList<View> circleList = new ArrayList<View>();
	
	class VideoInfo {
		int num;
	}
	
	public List<VideoInfo> videoList = new ArrayList<VideoInfo>();
	public static int mHeight;
	
	/**
	 * 记录每张图片的宽度
	 */
	public static int mImageHeight;
	/**
	 * 记录图片的总数量
	 */
	private int mRow;
	private int mCount;
	private int mLeft;
	private static int addRowCount = 0;
	private static int startCount = 0;
	public static int mCurrentRow = 0;
	private static int deleteCount = 0;
	/**
	 * 记录当前显示图片的坐标
	 */
	private int mCurrentImage;
	/**
	 * 记录上次触摸的横坐标值
	 */
	private float mLastMotionY;
	
	/**
	 * 是否强制重新布局
	 */
	private boolean forceToRelayout = true;
	
	private static int index = 0;

	
	@SuppressLint("InflateParams") 
	
	public CircleImage3DSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);

		
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
		//viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.id.image_switch_view_clone, null);
		//circleList = new ArrayList<CircleImage3DView>();
		isInit = true;
		scale = scale0;
		for (int i = 0; i < 12; i++) {
			
			CircleImage3DView circle = (CircleImage3DView) LayoutInflater.from(context).inflate(R.layout.circle_item, null);

			circle.setCircleItemText("万万没想到之大话西游篇王大锤叫兽易小星刘循子墨赵丽颖陈柏霖");
			if (i < 6) {
				circle.setImageResource(R.drawable.row1);
			}else if (i >=6 && i < 12) {
				circle.setImageResource(R.drawable.row2);
			}else if (i >=12 && i < 18) {
				circle.setImageResource(R.drawable.row3);
			}else if (i >=18 && i < 24) {
				circle.setImageResource(R.drawable.row4);
			}else {
				circle.setImageResource(R.drawable.row5);
			}
			circle.setItemScale(scale[i%6]);
			VideoInfo info = new VideoInfo();
			info.num = i;
			videoList.add(info);
			circleList.add(circle);
			addView(circle);		
			
		}

	}

	@SuppressLint("InflateParams") 
	public void addListView() {

		isFromService = true;
		CANSCROLLTONEXT = true;
		forceToRelayout = true;
		addRowCount++;
		mCurrentRow = addRowCount;
		index++;
		if (index > 2) {
			index = 0;
		}
		if (0 == index) {
			scale = scale0;
		}else if (1 == index) {
			scale = scale1;
		}else if (2 == index) {
			scale = scale2;
		}
		Log.d("CircleImage3DSwitchView", "addListView() addRowCount = " + addRowCount );
		for (int i = 0; i < 6; i++) {
			CircleImage3DView circle = (CircleImage3DView) LayoutInflater.from(getContext()).inflate(R.layout.circle_item, null);

			circle.setCircleItemText("万万没想到之大话西游篇王大锤叫兽易小星刘循子墨赵丽颖陈柏霖");

			if (i < 6) {
				circle.setImageResource(R.drawable.row1);
			}else if (i >=6 && i < 12) {
				circle.setImageResource(R.drawable.row2);
			}else if (i >=12 && i < 18) {
				circle.setImageResource(R.drawable.row3);
			}
			else if (i >=18 && i < 24) {
				circle.setImageResource(R.drawable.row4);
			}
			else {
				circle.setImageResource(R.drawable.row5);
			}
			circle.setItemScale(scale[i]);
			VideoInfo info = new VideoInfo();
			info.num = videoList.size() + i;
			videoList.add(info);
			circleList.add(circle);
			addView(circle);
			
			
			
			
		}
		
		//mCurrentRow = addRowCount;
	}
	public void decListView() {
		isFromService = true;
		forceToRelayout = true;
		int delete = 0;
		Log.d("CircleImage3DSwitchView", "decListView() ");
		if (mCount > 12 && addRowCount > 0) {
			
			if (mCount % 6 == 0) {
				delete = 6;
			}else {
				delete = mCount % 6;
			}
			CANSCROLLTOPREV = true;
//			for (int i = mCount-delete; i < mCount; i++) {
//				CircleImage3DView circle = (CircleImage3DView) circleList.get(i);
//				removeView(circle);			
//			}
			Log.d("CircleImage3DSwitchView", "delete = " + delete);
			for (int i = 0; i < delete; i++) {
				CircleImage3DView circle = (CircleImage3DView) circleList.get(circleList.size() - 1);
				removeView(circle);	
				circleList.remove(circleList.size()-1);
				videoList.remove(circleList.size()-1);
			}
			addRowCount--;
			mCurrentRow = addRowCount;
			Log.d("CircleImage3DSwitchView", "circleList.size() = " + circleList.size());
			
			//mCurrentRow = addRowCount;
		}
		Log.d("CircleImage3DSwitchView", "startCount = " + startCount );
		Log.d("CircleImage3DSwitchView", "mCurrentRow = " + mCurrentRow );
		
	}
	
	/**
	 * 1获取某个实例
	 * 2删除某个实例
	 * 只针对当前行
	 */
	
	/**
	 * 获取指定item
	 */
	public CircleImage3DView getCircleItem(int index) {
		CircleImage3DView circle = (CircleImage3DView) getChildAt(mCurrentRow * 6 + index);
		return circle;
	}
	/**
	 * 加入一个item
	 * @param circle
	 */
	public void addCircleItem(CircleImage3DView circle) {
		addView(circle);
		circleList.add(circle);
	}
	
	/**
	 * 删除一个item
	 * @param index
	 */
	public void deleteCircleItem(int index) {
		if (circleList.size() > 0) {
			deleteCount++;
			int CurVideoCount = getCurrentPageVideoCount();
			Log.d("CircleImage3DSwitchView", "deleteCount = " + deleteCount );
			if (deleteCount > CurVideoCount) {
				deleteCount = 0;
				if (mCurrentRow > 0) {
					mCurrentRow--;
					addRowCount--;
				}			
				Log.d("CircleImage3DSwitchView", "deleteCircleItem mCurrentRow = " + mCurrentRow );
			}
			forceToRelayout = true;
			isDelete = true;
			CircleImage3DView circle = (CircleImage3DView) getChildAt(mCurrentRow * 6 + index);		
			circleList.remove(mCurrentRow * 6 + index);
			videoList.remove(mCurrentRow * 6 + index);
			removeView(circle);
			
		}else {
			return;
		}				
	}
	
	public int getCurrentPageVideoCount() {
		if (getChildCount() > 0) {
			if ((mCurrentRow+1)*LIST_COUNT_PER_PAGE < getChildCount()) {
				return LIST_COUNT_PER_PAGE;
			}else{
				return getChildCount() - mCurrentRow * LIST_COUNT_PER_PAGE;
			}
		}else {
			return 0;
		}
	}
	
	private int positionSort(int top) {
		if (mCount > 6) {
			mRow = mCount / 6;
			mLeft = mCount - mRow * 6;
			if (mCount > 18 && !CANSCROLLTOPREV) {
				top = top - mImageHeight;
			}
			if (CANSCROLLTOPREV) {					
				if (mCount > 18) {
					top = top - 3*mImageHeight;
				}else if(mCount > 12) {
					top = top - 2*mImageHeight;
				}else if(mCount > 6) {
					top = top -mImageHeight;
				}
			}					
		}else {
			mRow = 0;
			mLeft = mCount;
		}
		return top;
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d("CircleImage3DSwitchView", "onLayout()" );
		if (changed || forceToRelayout) {
			mCount = getChildCount();		
			Log.d("CircleImage3DSwitchView", "mCount = " + mCount );
			mWidth = getMeasuredWidth() / 6; //每个控件宽度
			mHeight = getMeasuredHeight();
			// 每张图片的高度设定为控件高度的百分之
			mImageHeight = (int) (mHeight * 0.42);
			if (mCurrentRow >= 0 && mCurrentRow <= mCount/6) {			
				mScroller.abortAnimation();
				setScrollY(0);
				int top = (mHeight - mImageHeight) / 2;
				// 通过循环为每张图片设定位置				
				if (!isDelete) {
					top = positionSort(top);
				}else{

					if (mCount > 6) {
						mRow = mCount / 6;
						mLeft = mCount - mRow * 6;
						if (mCount > 18) {
							top -= mImageHeight*2;
						}else if (mCount > 12) {
							top -= mImageHeight;
						}
					}else{
						mRow = 0;
						mLeft = mCount;
					}
				}				
				Log.d("CircleImage3DSwitchView", "addRowCount = " + addRowCount );
				Log.d("CircleImage3DSwitchView", "mCurrentRow = " + mCurrentRow );
				if (mCount > 18) {					
					startCount = addRowCount-2;
				}else {
					startCount = 0;
				}
				
				int[] scaleIndex = new int[6];
				for (int i = startCount; i < mRow; i++) {
					scaleIndex = setScaleItems();
					int left = 0;
					int right = 0;
					for (int j = 0; j < LIST_COUNT_PER_PAGE; j++) {
						right = (int)(left + scale[scaleIndex[j]] * mWidth);
						//CircleImage3DView circle = (CircleImage3DView) getChildAt(j+i*6);
						CircleImage3DView circle = (CircleImage3DView) circleList.get(j+i*6);
						circle.layout(mWidth*j , top, mWidth*(j+1), top
								+ mImageHeight );
//						circle.layout(left , top, right, top
//						+ mImageHeight );
						//circle.setItemScale(scale[scaleIndex[j]]);
						//circle.setItemScale(scale[j]);
						circle.initImageViewBitmap();
						left += scale[scaleIndex[j]] * mWidth;
						refreshImageShowing();							
					}
					top = top + mImageHeight;			
				}
				int left = 0;
				int right = 0;
				int[] scaleIndexL = setScaleItems();
				for (int i = 0; i < mLeft; i++) {										
					//CircleImage3DView circle = (CircleImage3DView) getChildAt(i+mRow*6);
					right = (int)(left + scale[scaleIndexL[i]] * mWidth);
					CircleImage3DView circle = (CircleImage3DView) circleList.get(i+mRow*6);
					circle.layout(mWidth*i , top, mWidth*(i+1), top
							+ mImageHeight );
					//circle.setItemScale(scale[i]);
					circle.initImageViewBitmap();
					left += scale[scaleIndexL[i]] * mWidth;
					refreshImageShowing();
				}				
			}
			forceToRelayout = false;		
		}
		
		if (isDelete || isInit) {
			isDelete = false;
			isInit = false;
			setItemsAlign();
		}
		if (mCount > 6 && CANSCROLLTONEXT) {
			
			CANSCROLLTONEXT = false;
			scrollToNext();
		}
		if (mCount > 6 && CANSCROLLTOPREV) {
			CANSCROLLTOPREV = false;
			scrollToPrevious();
		}
	}
	
	public int getIndex() {
		return mCurrentRow;
	}
	
	private int [] setScaleItems() {
		
		int[] startArray = {0, 1, 2, 3, 4, 5};
		int N = 6;
		int[] resultArray = new int [6];
		for (int i = 0; i < N; i++) {
			int seed = (int)(Math.random()*(startArray.length - i - 0));
			resultArray[i] = startArray[seed];
			startArray[seed] = startArray[startArray.length - i - 1];
		}
		
		return resultArray;
		
	}
	
	private void setItemsAlign() {
		
		int disY = -10 ;
		int duration = (int) (1000f / mImageHeight * Math.abs(disY));
		if (mScroller.isFinished()) {
			mScroller.startScroll(0, getScrollY(), 0, disY, duration);
			invalidate();
			refreshImageShowing();
		}
		if (mScroller.isFinished()) {
			mScroller.startScroll(0, getScrollY(), 0, disY, duration);
			invalidate();
			refreshImageShowing();
		}
	
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mScroller.isFinished()) {
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();//或得实例 跟踪触摸事件速率
			}
			mVelocityTracker.addMovement(event); //将事件加入实例
			int action = event.getAction();
			//float x = event.getX();
			float y = event.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				// 记录按下时的纵坐标
				mLastMotionY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				int disY = (int) (mLastMotionY - y);
				mLastMotionY = y;
				scrollBy(0, disY);  //Y轴移动
				// 当发生移动时刷新图片的显示状态
				refreshImageShowing();
				break;
			case MotionEvent.ACTION_UP:
//				Log.d("MoveUp", "ACTION_UP");
//				mVelocityTracker.computeCurrentVelocity(1000);//每秒移动像素
//				int velocityY = (int) mVelocityTracker.getYVelocity();
//				if (shouldScrollToNext(velocityY)) {
//					// 滚动到下一张图
//					scrollToNext();
//					Log.d("MoveUp", "scrollToNext()");
//				} else if (shouldScrollToPrevious(velocityY)) {
//					// 滚动到上一张图
//					scrollToPrevious();
//					Log.d("MoveUp", "scrollToPrevious()");
//				} else {
//					// 滚动回当前图片
//					scrollBack();
//					Log.d("MoveUp", "scrollBack()");
//				}
//				if (mVelocityTracker != null) {
//					mVelocityTracker.recycle();
//					mVelocityTracker = null;
//				}
//				break;
				//scrollBy(0, mImageHeight);
				//scrollToNext();
				//refreshImageShowing();
			}
		}
		return true;
	}

	/**
	 * 根据当前的触摸状态来决定是否屏蔽子控件的交互能力。
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		float y = ev.getY();///////
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_MOVE:
			int yDiff = (int) Math.abs(mLastMotionY - y);
			if (yDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_UP:
		default:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			refreshImageShowing();
			postInvalidate();  //非UI线程中刷新view
		}
	}

	/**
	 * 设置图片滚动的监听器，每当有图片滚动时会回调此接口。
	 * 
	 * @param listener
	 *            图片滚动监听器
	 */
	public void setOnImageSwitchListener(OnImageSwitchListener listener) {
		mListener = listener;
	}

	/**
	 * 设置当前显示图片的下标，注意如果该值小于零或大于等于图片的总数量，图片则无法正常显示。
	 * 
	 * @param currentImage
	 *            图片的下标
	 */
	public void setCurrentImage(int currentImage) {
		mCurrentImage = currentImage;
		requestLayout();
	}

	/**
	 * 滚动到下一张图片。
	 */
	public void scrollToNext() {
		if (mScroller.isFinished()) {
			int disY = mImageHeight ;///////////////
			//checkImageSwitchBorder(SCROLL_NEXT);
			if (mListener != null) {
				mListener.onImageSwitch(mCurrentImage);
			}
			beginScroll(0, getScrollY(), 0, disY,SCROLL_NEXT);
			refreshImageShowing();
			if (isFromService) {
				isFromService = false;
			}else {
				mCurrentRow ++;
				addRowCount = mCurrentRow;
			}
			
		}
	}

	/**
	 * 滚动到上一张图片。
	 */
	public void scrollToPrevious() {
		if (mScroller.isFinished()) {
			int disY = -mImageHeight;
			//checkImageSwitchBorder(SCROLL_PREVIOUS);
			if (mListener != null) {
				mListener.onImageSwitch(mCurrentImage);
			}
			beginScroll(0, getScrollY(), 0, disY, SCROLL_PREVIOUS);
			refreshImageShowing();
		}
		if (isFromService) {
			isFromService = false;
		}else {
			mCurrentRow --;
			addRowCount = mCurrentRow;
		}
//		if (mCurrentRow > 0) {
//			mCurrentRow --;
//		}			
//		addRowCount = mCurrentRow;
	}

	/**
	 * 滚动回原图片。
	 */
	public void scrollBack() {
		if (mScroller.isFinished()) {
			beginScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_BACK);
		}
	}

	/**
	 * 回收所有图片对象，释放内存。
	 */
	public void clear() {
		for (int i = 0; i < mCount; i++) {
			//CircleImage3DView childView = (CircleImage3DView) getChildAt(i);
			CircleImage3DView circle = (CircleImage3DView) getChildAt(i);
			circle.recycleBitmap();
		}
	}

	/**
	 * 让控件中的所有图片开始滚动。
	 */
	private void beginScroll(int startX, int startY, int dx, int dy,
			final int action) {
		int duration = (int) (500f / mImageHeight * Math.abs(dy));
		mScroller.startScroll(startX, startY, dx, dy, duration);
		invalidate();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (action == SCROLL_NEXT || action == SCROLL_PREVIOUS) {
					forceToRelayout = true;
					//requestLayout();//当一个View需要parent view重新调用他的onMeasure onLayout来对重新设置自己位置
				}
			}
		}, duration);
	}

	/**
	 * 根据当前图片的下标和传入的item参数，来判断item位置上应该显示哪张图片。
	 * 
	 * @param item
	 *            取值范围是1-5
	 * @return 对应item位置上应该显示哪张图片。
	 */
	private int getIndexForItem(int item) {
		int index = -1;
		index = mCurrentImage + item - 3;
		while (index < 0) {
			index = index + mCount;
		}
		while (index > mCount - 1) {
			index = index - mCount;
		}
		return index;
	}


	/**
	 * 刷新所有图片的显示状态，包括当前的旋转角度。
	 * @param scale 
	 */
	private void refreshImageShowing() {
		for (int i = startCount*6; i < mCount; i++) {
			//CircleImage3DView childView = (CircleImage3DView) getChildAt(mItems[i]);

			CircleImage3DView circle = (CircleImage3DView) circleList.get(i);

			circle.setRotateData(i, scale);
			
			circle.invalidate();  //UI线程中刷新view	

		}
	}

	/**
	 * 检查图片的边界，防止图片的下标超出规定范围。 用于循环显示
	 */
	private void checkImageSwitchBorder(int action) {
		if (action == SCROLL_NEXT && ++mCurrentImage >= mCount) {
			//mCurrentImage = 0;
		} else if (action == SCROLL_PREVIOUS && --mCurrentImage < 0) {
			//mCurrentImage = mCount - 1;
		}
	}

	/**
	 * 判断是否应该滚动到下一张图片。 当速度大于600 或者距离大于两个图片宽度时 
	 */
	private boolean shouldScrollToNext(int velocityY) {
		return velocityY < -SNAP_VELOCITY || getScrollY() > mImageHeight / 2;
	}

	/**
	 * 判断是否应该滚动到上一张图片。shouldScrollToNext
	 */
	private boolean shouldScrollToPrevious(int velocityY) {
		return velocityY > SNAP_VELOCITY || getScrollY() < -mImageHeight / 2;
	}

	/**
	 * 图片滚动的监听器
	 */
	public interface OnImageSwitchListener {

		/**
		 * 当图片滚动时会回调此方法
		 * 
		 * @param currentImage
		 *            当前图片的坐标
		 */
		void onImageSwitch(int currentImage);

	}
	

}