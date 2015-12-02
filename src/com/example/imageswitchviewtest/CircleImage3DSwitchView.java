
package com.example.imageswitchviewtest;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;


import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;








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
	private boolean CANSCROLLTONEXT = false;
	private boolean CANSCROLLTOPREV = false;
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
	private static int addRowCount = 0;
	private static int startCount = 0;
	private static int mCurrentRow = 0;
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
	

	
	@SuppressLint("InflateParams") 
	
	public CircleImage3DSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);

		
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);
		//viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.id.image_switch_view_clone, null);
		//circleList = new ArrayList<CircleImage3DView>();
		for (int i = 0; i < 12; i++) {
			CircleImage3DView circle = (CircleImage3DView) LayoutInflater.from(context).inflate(R.layout.circle_item, null);
			
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
			circleList.add(circle);
			addView(circle);
			
		}

	}

	@SuppressLint("InflateParams") 
	public void addListView() {
		
		addRowCount++;
		CANSCROLLTONEXT = true;
		Log.d("CircleImage3DSwitchView", "addListView() addRowCount = " + addRowCount );
		for (int i = 0; i < 6; i++) {
			CircleImage3DView circle = (CircleImage3DView) LayoutInflater.from(getContext()).inflate(R.layout.circle_item, null);

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
			circleList.add(circle);
			addView(circle);
		}
	}
	public void decListView() {
		
		Log.d("CircleImage3DSwitchView", "decListView() ");
		CANSCROLLTOPREV = true;
		int cout = mCount;
		for (int i = mCount-6; i < mCount; i++) {
//			CircleImage3DView circle = (CircleImage3DView) circleList.get(i);
//			circle.recycleBitmap();
//			//circleList.remove(i);
			this.removeViewAt(0);
		}
		for (int i = cout-6; i < cout; i++) {
			circleList.remove(i);
		}
		addRowCount--;
//		if (mCount > 12 && addRowCount > 0) {
//			addRowCount--;
//			scrollToPrevious();
//		}
		
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
			if (mCurrentRow >= 0 && mCurrentRow < mCount/6) {
				
				mScroller.abortAnimation();
				setScrollY(0);
				int top = (mHeight - mImageHeight) / 2;
				// 通过循环为每张图片设定位置
				if (mCount > 6) {
					mRow = mCount / 6;
					mLeft = mCount - mRow * 6;
					if (mCount > 18) {
						top = top - mImageHeight;
					}
				}else {
					mRow = 0;
					mLeft = mCount;
				}
				mCurrentRow = addRowCount;
				Log.d("CircleImage3DSwitchView", "mCurrentRow = " + mCurrentRow );
				if (mCount > 18) {
					
					startCount = addRowCount-2;
				}else {

					startCount = 0;
				}
				for (int i = startCount; i < mRow; i++) {
						for (int j = 0; j < 6; j++) {
							CircleImage3DView circle = (CircleImage3DView) getChildAt(j+i*6);							
							circle.layout(mWidth*j , top, mWidth*(j+1), top
									+ mImageHeight );
							circle.initImageViewBitmap();
							refreshImageShowing();							
						}
					top = top + mImageHeight;			
				}
				for (int i = 0; i < mLeft; i++) {										
					CircleImage3DView circle = (CircleImage3DView) getChildAt(i+mRow*6);
					circle.layout(mWidth*i , top , mWidth*(i+1), top
							+ mImageHeight );
					circle.initImageViewBitmap();
					refreshImageShowing();
				}				
			}
			//forceToRelayout = false;
			
		}
		if (mCount > 6 && CANSCROLLTONEXT) {
			CANSCROLLTONEXT = false;
			scrollToNext();
		}
		if (mCount > 12 && CANSCROLLTOPREV) {
			CANSCROLLTOPREV = false;
			scrollToPrevious();
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
	 */
	private void refreshImageShowing() {
		for (int i = startCount*6; i < mCount; i++) {
			//CircleImage3DView childView = (CircleImage3DView) getChildAt(mItems[i]);

			CircleImage3DView circle = (CircleImage3DView) getChildAt(i);

			circle.setRotateData(i, getScrollY());
			
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