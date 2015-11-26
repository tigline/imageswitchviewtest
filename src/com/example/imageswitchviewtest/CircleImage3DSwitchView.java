
package com.example.imageswitchviewtest;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;








/**
 * 3D图片轮播器主控件。
 * 

 */
public class CircleImage3DSwitchView extends ViewGroup {

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<BitmapWorkerTask> taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * 图片左右两边的空白间距 
	 */
	public static final int IMAGE_PADDING = 10;
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
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
	/**
	 * 记录当前显示图片的坐标
	 */
	private int mCurrentImage;
	/**
	 * 记录上次触摸的横坐标值
	 */
	private float mLastMotionY;
	
	private static final int DEFAULT_VIEW_WIDTH = 320;
	private static final int DEFAULT_VIEW_HEIGHT = 453;
	/**
	 * 是否强制重新布局
	 */
	private boolean forceToRelayout;
	private int[] mItems;
	private LayoutInflater mInflater;
	

	
	@SuppressLint("InflateParams") 
	
	public CircleImage3DSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setFocusable(true);
		
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
		//viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.id.image_switch_view_clone, null);
		//circleList = new ArrayList<CircleImage3DView>();
		for (int i = 0; i < 30; i++) {

			View view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.circle_view, null);
			//CircleImage3DView circle = (CircleImage3DView) LayoutInflater.from(context).inflate(R.layout.circle_item, null);
			
			CircleImage3DView circle = (CircleImage3DView)view.findViewById(R.id.circle);
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
			circleList.add(view);
			addView(view);
		}
	}





	@SuppressLint("InflateParams") 
	public void initLayout() {
		Log.d("CircleImage3DSwitchView", "initLayout()" );
		for (int i = 0; i < 3; i++) {
			
			View view = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.circle_view, null);
			//CircleImage3DView circle = (CircleImage3DView) LayoutInflater.from(context).inflate(R.layout.circle_item, null);
			
			CircleImage3DView circle = (CircleImage3DView)view.findViewById(R.id.circle);
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
			circleList.add(view);
			addView(view);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
		        View child=getChildAt(i);
		        child.measure(widthMeasureSpec, heightMeasureSpec);
		        setMeasuredDimension(widthSpecSize, heightSpecSize);
			}
	}
	@SuppressLint("InflateParams") 
	public void addCircleItemView() {
		CircleImage3DView view = (CircleImage3DView) LayoutInflater.from(getContext()).inflate(R.layout.circle_view, null);
		
		addView(view);
		refreshImageShowing();
		
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d("CircleImage3DSwitchView", "onLayout()" );
		if (changed || forceToRelayout) {
			mCount = getChildCount();
			Log.d("CircleImage3DSwitchView", "mCount = " + mCount);
			mWidth = getMeasuredWidth()/6; //每个控件宽度
			mHeight = getMeasuredHeight();
			Log.d("CircleImage3DSwitchView", "mHeight = " + mHeight );
			Log.d("CircleImage3DSwitchView", "mWidth = " + mWidth );
			// 每张图片的高度设定为控件高度的百分之
			mImageHeight = (int) (mHeight * 0.42);
			Log.d("CircleImage3DSwitchView", "mImageHeight = " + mImageHeight );
			if (mCurrentImage >= 0 && mCurrentImage < mCount) {
				mScroller.abortAnimation();
				setScrollY(0);

				int top = (mHeight - mImageHeight) / 2;
				//int top = (mHeight - mImageHeight) / 2;
				Log.d("CircleImage3DSwitchView", "top = " + top );

				// 分别获取每个位置上应该显示的图片下标
				int[] items = { getIndexForItem(1), getIndexForItem(2),
						getIndexForItem(3), getIndexForItem(4),
						getIndexForItem(5),
						getIndexForItem(6), getIndexForItem(7),
						getIndexForItem(8), getIndexForItem(9),
						getIndexForItem(10)};
				mItems = items; 
				// 通过循环为每张图片设定位置
				if (mCount > 6) {
					mRow = mCount / 6;
					mLeft = mCount - mRow * 6;
				}else {
					mRow = 0;
					mLeft = mCount;

				}
				for (int i = 0; i < mRow; i++) {
						for (int j = 0; j < 6; j++) {
							View childView = circleList.get(j+i*6);
							CircleImage3DView circle = (CircleImage3DView)childView.findViewById(R.id.circle);
							childView.layout(mWidth*j , top + IMAGE_PADDING, mWidth*(j+1), top
									+ mImageHeight - IMAGE_PADDING);
							circle.initImageViewBitmap();
							refreshImageShowing();
							
						}
					top = top + mImageHeight;
					Log.d("CircleImage3DSwitchView", "top = " + top );				
				}
				for (int i = 0; i < mLeft; i++) {										
					View childView = circleList.get(i + mRow*6);
					CircleImage3DView circle = (CircleImage3DView)childView.findViewById(R.id.circle);
					//CircleImage3DView circle = (CircleImage3DView) circleList.get(i+mRow*6).getChildAt(i+mRow*6);
					childView.layout(mWidth*i , top + IMAGE_PADDING, mWidth*(i+1), top
							+ mImageHeight - IMAGE_PADDING);
					circle.initImageViewBitmap();
					refreshImageShowing();
				}
				
			}
			forceToRelayout = false;
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
//			RelativeLayout childView = circleList.get(i);
//			CircleImage3DView circle = (CircleImage3DView)childView.getChildAt(i);
//			circle.recycleBitmap();
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
	 */
	private void refreshImageShowing() {
		for (int i = 0; i < mCount; i++) {
			//CircleImage3DView childView = (CircleImage3DView) getChildAt(mItems[i]);
			//CircleImage3DView childView = (CircleImage3DView) getChildAt(i);
			View childView = circleList.get(i);
			CircleImage3DView circle = (CircleImage3DView)childView.findViewById(R.id.circle);
			circle.setRotateData(i, getScrollY());
//			((View)circle.getParent()).invalidate();
			
			childView.invalidate();  //UI线程中刷新view
			circle.invalidate();
			
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
	private void setImageView(String imageUrl, CircleImageView imageView) {
		Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.empty_photo);
		}
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}
	

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @param firstVisibleItem
	 *            第一个可见的ImageView的下标
	 * @param visibleItemCount
	 *            屏幕中总共可见的元素数
	 * @param context 
	 */
	private void loadBitmaps(int firstVisibleItem, int visibleItemCount, Context context) {
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
	
				String imageUrl = Images.imageThumbUrls[i];
				Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
				if (bitmap == null) {

					BitmapWorkerTask task = new BitmapWorkerTask();
					taskCollection.add(task);
					task.execute(imageUrl);
				} else {

					//ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
					CircleImage3DView imageView = (CircleImage3DView) LayoutInflater.from(context).inflate(R.layout.circle_view, null);
					imageView.setImageBitmap(bitmap);
					addView(imageView);
					postInvalidate();
					if (imageView != null && bitmap != null) {

						imageView.setImageBitmap(bitmap);
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}
	
	/**
	 * 异步下载图片的任务。
	 * 
	 * @author guolin
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {

			imageUrl = params[0];
			// 在后台开始下载图片
			Bitmap bitmap = downloadBitmap(params[0]);
			if (bitmap != null) {

				// 图片下载完成后缓存到LrcCache中
				addBitmapToMemoryCache(params[0], bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);

			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
			//CircleImage3DView imageView = (CircleImage3DView) this.findViewWithTag(imageUrl);
			CircleImage3DView imageView = (CircleImage3DView) LayoutInflater.from(getContext()).inflate(R.layout.circle_view, null);
			imageView.setImageBitmap(bitmap);
			addView(imageView);
			postInvalidate();
			if (imageView != null && bitmap != null) {

				imageView.setImageBitmap(bitmap);
			}
			taskCollection.remove(this);
		}

		/**
		 * 建立HTTP请求，并获取Bitmap对象。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 解析后的Bitmap对象
		 */
		private Bitmap downloadBitmap(String imageUrl) {

			Bitmap bitmap = null;
			HttpURLConnection con = null;
			try {

				URL url = new URL(imageUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(5 * 1000);
				con.setReadTimeout(10 * 1000);
				con.setDoInput(true);
				con.setDoOutput(true);
				bitmap = BitmapFactory.decodeStream(con.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (con != null) {
					con.disconnect();
				}
			}
			return bitmap;
		}
	}

}