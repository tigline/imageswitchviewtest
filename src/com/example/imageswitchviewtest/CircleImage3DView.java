package com.example.imageswitchviewtest;

import android.R.integer;
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
	private int mScrollY;
	/**
	 * CircleImage3DSwitchView控件的宽度
	 */
	private int mLayoutHeight;
	/**
	 * 当前图片的宽度
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
	private float mDeep;
	
	private float xOffset;

	private float yOffset;
	
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
		mHeight = getHeight() + CircleImage3DSwitchView.IMAGE_PADDING * 2; 
	}

	/**
	 * 设置旋转角度。
	 * 
	 * @param index
	 *            当前图片的下标
	 * @param scrollX
	 *            当前图片在X轴方向滚动的距离
	 */
	public void setRotateData(int index, int scrollY) {
		if (mReady) {
			
		}
		mIndex = index / 6;
		if (mIndex > 3) {
			
		}
		mScrollY = scrollY;
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
		
		if (getDrawable() == null) {
            //return;
			super.onDraw(canvas);
        }else if (true) {

//            	int[] location=new int[2];
//            	getLocationOnScreen(location);
//            	yOffset = location[1];
//            	Log.d("CircleImage3DView", location[0] + "  "+ location[1]);
            	computeRotateData();
    			mCamera.save(); //保存状态 不影响其他元素
    			mCamera.translate(0.0f, 0.0f, mDeep);
    			mCamera.rotateX(360f-mRotateDegree);
    			mCamera.getMatrix(mShaderMatrix);
    			mCamera.restore(); //取出状态
    			mShaderMatrix.preTranslate(-getHeight() / 2, -mDy);
    			mShaderMatrix.postTranslate(getHeight() / 2, mDy);
//              canvas.drawBitmap(mBitmap, mShaderMatrix, null);
//    			canvas.setMatrix(mShaderMatrix);
    			canvas.concat(mShaderMatrix);
    			
            	canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);         
//                setVisibility(INVISIBLE);
        }

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
		float degreePerPix = BASE_DEGREE / mHeight;
		float deepPerPix = BASE_DEEP / ((mLayoutHeight - mHeight) / 2);
//		if (yOffset < -260) {
//			mIndex = 0;
//		}else if (yOffset >= -260 && yOffset <= 280) {
//			mIndex = 1;
//		}else if (yOffset >= 280 && yOffset <= 820) {
//			mIndex = 2;
//		}else if (yOffset >= 820 && yOffset <= 1080) {
//			mIndex = 3;
//		}else if (yOffset > 1080) {
//			mIndex = 4;
//		}
		switch (mIndex) {
		case 0:
			xOffset = 0f;
			mDy = mHeight;
			mRotateDegree = 360f - (2 * mHeight + mScrollY) * degreePerPix;
			if (mScrollY < -mHeight) {
				mDeep = 0;
			} else {
				mDeep = (mHeight + mScrollY) * deepPerPix;
				
			}
			break;
		case 1:	

			//如果向上滑动至消失
			if (mScrollY > 0) {   
				mDy = mHeight;
				mRotateDegree = (360f - BASE_DEGREE) - mScrollY * degreePerPix;
				mDeep = mScrollY * deepPerPix;
				xOffset = mDeep;
			} else {
				//如果向下滑超过中框
				if (mScrollY < -mHeight) {
					mDy = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
					mRotateDegree = (-mScrollY - mHeight) * degreePerPix;
				} else {
					//未到中框
					mDy = mHeight;
					mRotateDegree = 360f - (mHeight + mScrollY) * degreePerPix;
				}
				mDeep = 0;
			}
			break;
		case 2:
			if (mScrollY > 0) {
				mDy = mHeight;
				mRotateDegree = 360f - mScrollY * degreePerPix;
				mDeep = 0;
				if (mScrollY > mHeight) {
					mDeep = (mScrollY - mHeight) * deepPerPix;
				}
			} else {
				mDy = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
				mRotateDegree = -mScrollY * degreePerPix;
				mDeep = 0;
				if (mScrollY < -mHeight) {
					mDeep = -(mHeight + mScrollY) * deepPerPix;
				}
			}
			break;
		case 3:
			if (mScrollY < 0) {
				mDy = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
//				mRotateDegree = BASE_DEGREE - mScrollY * degreePerPix;
				mDeep = -mScrollY * deepPerPix;
			} else {
				if (mScrollY > mHeight) {
					mDy = mHeight;
//					mRotateDegree = 360f - (mScrollY - mHeight) * degreePerPix;
				} else {
					mDy = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
//					mRotateDegree = BASE_DEGREE - mScrollY * degreePerPix;
				}
				mDeep = 0;
			}
			break;			
		case 4:
			mDy = -CircleImage3DSwitchView.IMAGE_PADDING * 2;
			mRotateDegree = (2 * mHeight - mScrollY) * degreePerPix;
			if (mScrollY > mHeight) {
				mDeep = 0;
			} else {
				mDeep = (mHeight - mScrollY) * deepPerPix;
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
			if (mScrollY < (mLayoutHeight - mHeight) / 2 - mHeight) {
				isVisible = true;
			} else {
				isVisible = false;
			}
			break;
		case 1:
			if (mScrollY > (mLayoutHeight - mHeight) / 2) {
				isVisible = false;
			} else {
				isVisible = true;
			}
			break;
		case 2:
			if (mScrollY > mLayoutHeight / 2 + mHeight / 2
					|| mScrollY < -mLayoutHeight / 2 - mHeight / 2) {
				isVisible = false;
			} else {
				isVisible = true;
			}
			break;
		case 3:
			if (mScrollY < -(mLayoutHeight - mHeight) / 2) {
				isVisible = false;
			} else {
				isVisible = true;
			}
			break;
		case 4:
			if (mScrollY > mHeight - (mLayoutHeight - mHeight) / 2) {
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
        mBorderPaint.setStrokeWidth(mBorderWidth); //设置空心线宽
 
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();  

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
        mLayoutHeight = CircleImage3DSwitchView.mHeight;
        Log.d("CircleImage3DView", "mLayoutHeight = " + mLayoutHeight);
		mHeight = getWidth() + CircleImage3DSwitchView.IMAGE_PADDING * 2;
		Log.d("CircleImage3DView", "mHeight = " + mHeight);
        updateShaderMatrix();
        invalidate();  //刷新数据调用onDraw()重绘 
    }
    
    private void updateShaderMatrix() {
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