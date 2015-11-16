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
	private static final float BASE_DEEP = 150f;
	
	private static final ScaleType SCALE_TYPE = ScaleType.FIT_XY ;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private Matrix mShaderMatrix;
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
	
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
	private Camera mCamera;
	//private Matrix mMaxtrix;
	private Bitmap mBitmap;
	
	private float mDrawableRadius;
    private float mBorderRadius;

    private boolean mReady;
    private boolean mSetupPending;
	/**
	 * 当前图片对应的下标
	 */
	private int mIndex;
	/**
	 * 在前图片在X轴方向滚动的距离
	 */
	private int mScrollX;
	/**
	 * CircleImage3DSwitchView控件的宽度
	 */
	private int mLayoutWidth;
	/**
	 * 当前图片的宽度
	 */
	private int mWidth;
	/**
	 * 当前旋转的角度
	 */
	private float mRotateDegree;
	/**
	 * 旋转的中心点
	 */
	private float mDx;
	/**
	 * 旋转的深度
	 */
	private float mDeep;

	public CircleImage3DView(Context context) {
        super(context);
    }

    public CircleImage3DView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
	
	public CircleImage3DView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);
		mCamera = new Camera();		
		mShaderMatrix = new Matrix();
		
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);

        a.recycle();

        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
		
		
		//mMaxtrix = new Matrix();
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
		mLayoutWidth = CircleImage3DSwitchView.mWidth;
		mWidth = getWidth() + CircleImage3DSwitchView.IMAGE_PADDING * 2;
		//setup();
	}

	/**
	 * 设置旋转角度。
	 * 
	 * @param index
	 *            当前图片的下标
	 * @param scrollX
	 *            当前图片在X轴方向滚动的距离
	 */
	public void setRotateData(int index, int scrollX) {
		mIndex = index;
		mScrollX = scrollX;
		Log.d("CircleImage3DView", "mScrollX = " + scrollX);
	}

	/**
	 * 回收当前的Bitmap对象，以释放内存。
	 */
	public void recycleBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
	}
	/*
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = null;
		initImageViewBitmap();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = null;
		initImageViewBitmap();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = null;
		initImageViewBitmap();
	}
	
	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		mBitmap = null;
		initImageViewBitmap();
	}
	*/
	 @Override
	    public void setImageBitmap(Bitmap bm) {
	        super.setImageBitmap(bm);
	        mBitmap = bm;
//	        mLayoutWidth = CircleImage3DSwitchView.mWidth;
//	        Log.d("CircleImage3DView", "mLayoutWidth = " + mLayoutWidth);
//			mWidth = getWidth() + CircleImage3DSwitchView.IMAGE_PADDING * 2;
//			Log.d("CircleImage3DView", "mWidth = " + mWidth);
	        setup();
	    }
	
	    @Override
	    public void setImageDrawable(Drawable drawable) {
	        super.setImageDrawable(drawable);
	        mBitmap = getBitmapFromDrawable(drawable);
//	        mLayoutWidth = CircleImage3DSwitchView.mWidth;
//	        Log.d("CircleImage3DView", "mLayoutWidth = " + mLayoutWidth);
//			mWidth = getWidth() + CircleImage3DSwitchView.IMAGE_PADDING * 2;
//			Log.d("CircleImage3DView", "mWidth = " + mWidth);
	        setup();
	    }
	
	    @Override
	    public void setImageResource(int resId) {
	        super.setImageResource(resId);
	        mBitmap = getBitmapFromDrawable(getDrawable());
//	        mLayoutWidth = CircleImage3DSwitchView.mWidth;
//	        Log.d("CircleImage3DView", "mLayoutWidth = " + mLayoutWidth);
//			mWidth = getWidth() + CircleImage3DSwitchView.IMAGE_PADDING * 2;
//			Log.d("CircleImage3DView", "mWidth = " + mWidth);
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
		
		if (getDrawable() == null) {
            return;
			//super.onDraw(canvas);
        }else if(isImageVisible()) {
            	
            	
            	computeRotateData();
    			mCamera.save(); //保存状态 不影响其他元素
    			mCamera.translate(0.0f, 0.0f, mDeep);
    			mCamera.rotateY(mRotateDegree);
    			mCamera.getMatrix(mShaderMatrix);
    			mCamera.restore(); //取出状态
    			mShaderMatrix.preTranslate(-mDx, -getHeight() / 2);
    			mShaderMatrix.postTranslate(mDx, getHeight() / 2);
//              canvas.drawBitmap(mBitmap, mShaderMatrix, null);
    			canvas.setMatrix(mShaderMatrix);
            	canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
                
                
    		
        }
            

        
//		if (mBitmap == null) {
//			// 如果Bitmap对象还不存在，先使用父类的onDraw方法进行绘制
//			super.onDraw(canvas);
//		} else {
//			if (isImageVisible()) {
//				// 绘图时需要注意，只有当图片可见的时候才进行绘制，这样可以节省运算效率
//				computeRotateData();
//				mCamera.save();
//				mCamera.translate(0.0f, 0.0f, mDeep);
//				mCamera.rotateY(mRotateDegree);
//				mCamera.getMatrix(mMaxtrix);
//				mCamera.restore();
//				mMaxtrix.preTranslate(-mDx, -getHeight() / 2);
//				mMaxtrix.postTranslate(mDx, getHeight() / 2);
//				canvas.drawBitmap(mBitmap, mMaxtrix, null);
//			}11-13 10:43:22.478: E/ActivityThread(13536): android.view.InflateException: Binary XML file line #12: Error inflating class com.example.imageswitchviewtest.Image3DView

//		}
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }
	/**
	 * 在这里计算所有旋转所需要的数据。
	 */
	private void computeRotateData() {
		float degreePerPix = BASE_DEGREE / mWidth;
		float deepPerPix = BASE_DEEP / ((mLayoutWidth - mWidth) / 2);
		switch (mIndex) {
		case 0:
			mDx = mWidth;
			mRotateDegree = 360f - (2 * mWidth + mScrollX) * degreePerPix;
			if (mScrollX < -mWidth) {
				mDeep = 0;
			} else {
				mDeep = (mWidth + mScrollX) * deepPerPix;
			}
			break;
		case 1:
			if (mScrollX > 0) {
				mDx = mWidth;
				mRotateDegree = (360f - BASE_DEGREE) - mScrollX * degreePerPix;
				mDeep = mScrollX * deepPerPix;
			} else {
				if (mScrollX < -mWidth) {
					mDx = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
					mRotateDegree = (-mScrollX - mWidth) * degreePerPix;
				} else {
					mDx = mWidth;
					mRotateDegree = 360f - (mWidth + mScrollX) * degreePerPix;
				}
				mDeep = 0;
			}
			break;
		case 2:
			if (mScrollX > 0) {
				mDx = mWidth;
				mRotateDegree = 360f - mScrollX * degreePerPix;
				mDeep = 0;
				if (mScrollX > mWidth) {
					mDeep = (mScrollX - mWidth) * deepPerPix;
				}
			} else {
				mDx = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
				mRotateDegree = -mScrollX * degreePerPix;
				mDeep = 0;
				if (mScrollX < -mWidth) {
					mDeep = -(mWidth + mScrollX) * deepPerPix;
				}
			}
			break;
		case 3:
			if (mScrollX < 0) {
				mDx = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
				mRotateDegree = BASE_DEGREE - mScrollX * degreePerPix;
				mDeep = -mScrollX * deepPerPix;
			} else {
				if (mScrollX > mWidth) {
					mDx = mWidth;
					mRotateDegree = 360f - (mScrollX - mWidth) * degreePerPix;
				} else {
					mDx = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
					mRotateDegree = BASE_DEGREE - mScrollX * degreePerPix;
				}
				mDeep = 0;
			}
			break;
		case 4:
			mDx = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
			mRotateDegree = (2 * mWidth - mScrollX) * degreePerPix;
			if (mScrollX > mWidth) {
				mDeep = 0;
			} else {
				mDeep = (mWidth - mScrollX) * deepPerPix;
			}
			break;
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
		switch (mIndex) {
		case 0:
			if (mScrollX < (mLayoutWidth - mWidth) / 2 - mWidth) {
				isVisible = true;
			} else {
				isVisible = false;
			}
			break;
		case 1:
			if (mScrollX > (mLayoutWidth - mWidth) / 2) {
				isVisible = false;
			} else {
				isVisible = true;
			}
			break;
		case 2:
			if (mScrollX > mLayoutWidth / 2 + mWidth / 2
					|| mScrollX < -mLayoutWidth / 2 - mWidth / 2) {
				isVisible = false;
			} else {
				isVisible = true;
			}
			break;
		case 3:
			if (mScrollX < -(mLayoutWidth - mWidth) / 2) {
				isVisible = false;
			} else {
				isVisible = true;
			}
			break;
		case 4:
			if (mScrollX > mWidth - (mLayoutWidth - mWidth) / 2) {
				isVisible = true;
			} else {
				isVisible = false;
			}
			break;
		}
		return isVisible;
	}
	
	
    private void setup() {
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
        mBorderPaint.setStrokeWidth(mBorderWidth);
 
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
        mLayoutWidth = CircleImage3DSwitchView.mWidth;
        Log.d("CircleImage3DView", "mLayoutWidth = " + mLayoutWidth);
		mWidth = getWidth() + CircleImage3DSwitchView.IMAGE_PADDING * 2;
		Log.d("CircleImage3DView", "mWidth = " + mWidth);
        updateShaderMatrix();
        invalidate();
    }
    
    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}