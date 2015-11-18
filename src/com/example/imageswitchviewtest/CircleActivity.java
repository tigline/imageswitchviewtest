/**
 * 
 */
package com.example.imageswitchviewtest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * @Project ImageSwitchViewTest	
 * @author houxb
 * @Date 2015-11-18
 */
public class CircleActivity extends Activity {
	
	private CircleImage3DSwitchView circleImageSwitchView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
	              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_circleh);
        circleImageSwitchView = (CircleImage3DSwitchView) findViewById(R.id.image_switch_view_clone);
        circleImageSwitchView.setCurrentImage(1);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		circleImageSwitchView.clear();
	}
}
