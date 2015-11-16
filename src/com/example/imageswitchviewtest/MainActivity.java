package com.example.imageswitchviewtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.imageswitchviewtest.CircleImage3DSwitchView.OnImageSwitchListener;

/**
 
 */
public class MainActivity extends Activity {

	private CircleImage3DSwitchView circleImageSwitchView;
	private Image3DSwitchView imageSwitchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
	              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		circleImageSwitchView = (CircleImage3DSwitchView) findViewById(R.id.image_switch_view_clone);
		imageSwitchView = (Image3DSwitchView) findViewById(R.id.image_switch_view);
		circleImageSwitchView.setOnImageSwitchListener(new OnImageSwitchListener() {
			@Override
			public void onImageSwitch(int currentImage) {
				// Log.d("TAG", "current image is " + currentImage);
			}
		});
		
		circleImageSwitchView.setCurrentImage(1);
		imageSwitchView.setCurrentImage(1);
	}
 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		circleImageSwitchView.clear();
	}

}
