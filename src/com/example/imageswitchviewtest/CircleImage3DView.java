
package com.example.imageswitchviewtest;

import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;








/**
 * 
    此view 为 circleImageView 与 Image3DView 结合 
 */
public class CircleImage3DView extends ImageView {
	/**
	 * 旋转角度的基准值
	 */
	private static final float BASE_DEGREE = 50f;
	/**
	 * 旋转深度的基准值
	 */
	private static final float BASE_DEEP = 240f;
	
	private static final float BASE_SCALE = 160f;
	
	private static final ScaleType SCALE_TYPE = ScaleType.FIT_XY ;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.RED;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private Matrix mShaderMatrix;
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
	
    private BitmapShader mBitmapShader;
    /**
     * 图片宽度
     */
    private int mBitmapWidth;  //
    /**
     * 图片高度
     */
    private int mBitmapHeight;
	private Camera mCamera;

	private Bitmap mBitmap;
	
	private float mDrawableRadius;
    private float mBorderRadius;

    private boolean mReady;
    private boolean mSetupPending;
    
    private boolean drawFlag = true;
	/**
	 * 当前图片对应的下标
	 */
	private int mIndex;
	
	private int rowIndex;
	/**
	 * 在前图片在X轴方向滚动的距离
	 */
	private int mScrollY;
	/**
	 * CircleImage3DSwitchView控件的宽度
	 */
	private int mLayoutHeight;
	/**
	 * 当前图片的高度
	 */
	private int mHeight;
	/**
	 * 当前旋转的角度
	 */
	private float mRotateDegree;
	/**
	 * 旋转的中心点
	 */
	private float mDy;
	/**
	 * 旋转的深度
	 */
	private float mItemHeight;
	
	private float mDeep;
	
	private float xOffset;

	private float yOffset;
	
	private float scaleX;
	
	private float scaleY;
	
	public CircleImage3DView(Context context) {
        super(context);
        //setWillNotDraw(false);
    }

    public CircleImage3DView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
	
	public CircleImage3DView(Context context, AttributeSet attrs, int defStyle) {		
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);
		setWillNotDraw(false);
		Log.d("CircleImage3DView", "CircleImage3DView() construct" );
		
		mCamera = new Camera();		
		mShaderMatrix = new Matrix();
		
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0); //获取自定义属性

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);

        a.recycle();

        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
	}


	@Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }
	
	/**
	 * 初始化Image3DView所需要的信息，包括图片宽度，截取背景图等。
	 */
	public void initImageViewBitmap() {
		if (mBitmap == null) {
			setDrawingCacheEnabled(true);
			buildDrawingCache();
			mBitmap = getDrawingCache();
		}
		mLayoutHeight = CircleImage3DSwitchView.mHeight;
		mItemHeight = CircleImage3DSwitchView.mImageHeight;
		Log.d("CircleImage3DView", "mItemHeight = " + mItemHeight );
		mHeight = getHeight();
		Log.d("CircleImage3DView", "mHeight = " + mHeight + " " + getHeight() );
		
	}

	/**
	 * 设置旋转角度。
	 * 
	 * @param index
	 *            当前图片的下标
	 * @param location 
	 * @param scrollX
	 *            当前图片在X轴方向滚动的距离
	 */
	public void setRotateData(int index, int scrollY) {

		mIndex = index;
		rowIndex = index%6;
		//yOffset = location;


//		yOffset = dy;
//		Log.d("CircleImage3DView", "mScrollY = " + scrollX);

	}

	/**
	 * 回收当前的Bitmap对象，以释放内存。
	 */
	public void recycleBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
	}
	
	 @Override
	    public void setImageBitmap(Bitmap bm) {
	        super.setImageBitmap(bm);
	        mBitmap = bm;
	        setup();
	    }
	
	    @Override
	    public void setImageDrawable(Drawable drawable) {
	        super.setImageDrawable(drawable);
	        mBitmap = getBitmapFromDrawable(drawable);
	        setup();
	    }
	
	    @Override
	    public void setImageResource(int resId) {
	        super.setImageResource(resId);
	        mBitmap = getBitmapFromDrawable(getDrawable());
	        setup();
	    }
	   
	    private Bitmap getBitmapFromDrawable(Drawable drawable) {
	        if (drawable == null) {
	            return null;
	        }
	
	        if (drawable instanceof BitmapDrawable) {
	            return ((BitmapDrawable) drawable).getBitmap();
	        }
	
	        try {
	            Bitmap bitmap;
	
	            if (drawable instanceof ColorDrawable) {
	                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
	            } else {
	                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
	            }
	            
	            Canvas canvas = new Canvas(bitmap);
	            
	            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	            drawable.draw(canvas);
	            return bitmap;
	        } catch (OutOfMemoryError e) {
	            return null;
	        }
	    }

	@Override
	protected void onDraw(Canvas canvas) {
		
		//Log.d("CircleImage3DView", "onDraw()");
		if (getDrawable() == null) {
            //return;
			super.onDraw(canvas);

        }else if (isImageVisible()) {
        	//Log.d("CircleImage3DView", "onDraw() ing...");
        	//isImageVisible()
        	int[] location=new int[2];
        	getLocationOnScreen(location);
        	yOffset = location[1];
        	Log.d("CircleImage3DView", "yOffset = " + yOffset );
        	computeRotateData();			
			mCamera.save(); //保存状态 不影响其他元素
			mCamera.translate(0.0f, 0.0f, mDeep);
			mCamera.rotateX(360f-mRotateDegree);
			mCamera.getMatrix(mShaderMatrix);
			mCamera.restore(); //取出状态
			mShaderMatrix.preTranslate(-getHeight() / 2, -mDy);
			mShaderMatrix.postTranslate(getHeight() / 2, mDy);
			canvas.concat(mShaderMatrix);   			
        	canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);     
        }

	}



	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }
	
	/**
	 * @param mIndex
	 * @param offsetPerPix 
	 */
	private void computeTopOffsetData(float offsetPerPix) {
		
		// TODO Auto-generated method stub
		//Log.d("CircleImage3DView", "mIndex = " + mIndex );
		int index = mIndex % 6;
		float hoffset = 0;
		switch (index) {
		case 0:
			xOffset = ((mLayoutHeight - mHeight) / 2 - yOffset) * offsetPerPix;
			hoffset = 0f;
			break;
		case 1:
			xOffset = (((mLayoutHeight - mHeight) / 2 - yOffset)*3/5) * offsetPerPix;
			hoffset = (((mLayoutHeight - mHeight) / 2 - yOffset)/5) * offsetPerPix;
			break;
		case 2:

			xOffset = (((mLayoutHeight - mHeight) / 2 - yOffset)/5) * offsetPerPix;
			hoffset = (((mLayoutHeight - mHeight) / 2 - yOffset)*3f/5) * offsetPerPix;
			break;
		case 3:
			xOffset = -(((mLayoutHeight - mHeight) / 2 - yOffset)/5) * offsetPerPix;
			hoffset = (((mLayoutHeight - mHeight) / 2 - yOffset)*2.5f/5) * offsetPerPix;
			break;
		case 4:
			xOffset = -(((mLayoutHeight - mHeight) / 2 - yOffset)*3/5) * offsetPerPix;
			hoffset = (((mLayoutHeight - mHeight) / 2 - yOffset)*1.5f/5) * offsetPerPix;
			break;
		case 5:
			xOffset = -((mLayoutHeight - mHeight) / 2 - yOffset) * offsetPerPix;
			hoffset = 0f;
			break;
		default:
			break;
		}	
		setTranslationX(xOffset);
		setTranslationY(-hoffset);

	}
	
	private void computeBottomOffsetData() {
		// TODO Auto-generated method stub
		float setPerPix = 100f / ((mLayoutHeight - mHeight) / 2);
		//Log.d("CircleImage3DView", "mIndex = " + mIndex );
		int index = mIndex % 6;
		switch (index) {
		case 0:
			xOffset = -((mLayoutHeight - mItemHeight) / 2 - yOffset) * setPerPix;

			break;
		case 1:
			xOffset = -(((mLayoutHeight - mItemHeight) / 2 - yOffset)*3/5) * setPerPix;
			break;
		case 2:
			xOffset = -(((mLayoutHeight - mItemHeight) / 2 - yOffset)/5) * setPerPix;
			break;
		case 3:
			xOffset = (((mLayoutHeight - mItemHeight) / 2 - yOffset)/5) * setPerPix;
			break;
		case 4:
			xOffset = (((mLayoutHeight - mItemHeight) / 2 - yOffset)*3/5) * setPerPix;
			break;
		case 5:
			xOffset = ((mLayoutHeight - mItemHeight) / 2 - yOffset) * setPerPix;
			break;
		default:
			break;
		}	
		setTranslationX(xOffset);

	}
	/**
	 * 在这里计算所有旋转所需要的数据。
	 */
	private void computeRotateData() {
		float degreePerPix = BASE_DEGREE / mHeight;
		float deepPerPix = BASE_DEEP / mItemHeight;
		float offsetPerPix = BASE_SCALE / ((mLayoutHeight - mHeight) / 2);
		
		if (yOffset <= (mLayoutHeight - mItemHeight) / 2) {
			
			mDy = mHeight;
			mRotateDegree = 360f - ((mLayoutHeight - mItemHeight) / 2 - yOffset) * degreePerPix;
			mDeep = 0;
			if (yOffset < (mLayoutHeight - mItemHeight) / 2-mItemHeight) {
				mDeep = ((mLayoutHeight - mItemHeight) / 2 - yOffset) * deepPerPix;
			}
			computeTopOffsetData(offsetPerPix);
		}else if (yOffset >= (mLayoutHeight - mItemHeight) / 2 && yOffset <= (mLayoutHeight + mItemHeight) / 2 ) {
			mRotateDegree = 0;
			xOffset = 0;
			mDeep = (yOffset - (mLayoutHeight - mItemHeight) / 2) * deepPerPix;
			computeBottomOffsetData();
		}else if (yOffset >= (mLayoutHeight + mItemHeight) / 2) {
			mDeep = BASE_DEEP;
			mRotateDegree = 0;
			computeBottomOffsetData();			
		}
	}

	
	

	public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }
	/**
	 * 判断当前图片是否可见。
	 * 
	 * @return 当前图片可见返回true，不可见返回false。
	 */
	private boolean isImageVisible() {
		boolean isVisible = false;
		if (yOffset > ((mLayoutHeight - mItemHeight) / 2 - mItemHeight) 
			|| yOffset < ((mLayoutHeight + mItemHeight) / 2 + mItemHeight)) {
			isVisible = true;
		}
		return isVisible;
	}
	
	
    private void setup() {
    	//Log.d("CircleImage3DView", "setup()" );
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth); //设置空心线宽
 
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();  

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        updateShaderMatrix(); 
        invalidate();  //刷新数据调用onDraw()重绘 
    }
    
    private void updateShaderMatrix() {
    	//Log.d("CircleImage3DView", "updateShaderMatrix()" );
        float scale;
        
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        //缩放形式为填充

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        //XY 轴缩放
        mShaderMatrix.setScale(scale, scale);
        //缩放后移动实际指定距离
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
        
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}