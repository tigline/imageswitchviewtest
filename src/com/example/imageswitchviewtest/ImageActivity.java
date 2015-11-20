/**
 * 
 */
package com.example.imageswitchviewtest;

import com.example.imageswitchviewtest.Image3DSwitchViewH.OnImageSwitchListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * @Project ImageSwitchViewTest	
 * @author houxb
 * @Date 2015-11-18
 */
public class ImageActivity extends Activity{
	
	private Image3DSwitchViewH imageSwitchViewH;
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
	              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        setContentView(R.layout.activity_imageh);
        
        imageSwitchViewH = (Image3DSwitchViewH) findViewById(R.id.image_switch_viewh);
//        imageSwitchViewH.setOnImageSwitchListener(new OnImageSwitchListener() {
//			@Override
//			public void onImageSwitch(int currentImage) {
//				// Log.d("TAG", "current image is " + currentImage);
//			}
//		});
		
		//circleImageSwitchView.setCurrentImage(1);
        imageSwitchViewH.setCurrentImage(0);
	}
 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageSwitchViewH.clear();
	}
}
