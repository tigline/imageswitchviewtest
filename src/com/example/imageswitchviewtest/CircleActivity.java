/**
 * 
 */
package com.example.imageswitchviewtest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @Project ImageSwitchViewTest	
 * @author houxb
 * @Date 2015-11-18
 */
public class CircleActivity extends Activity implements OnClickListener{
	
	Button prev_bt, next_bt, add_bt, dec_bt;
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
 
        //circleImageSwitchView.initLayout();
        
        prev_bt = (Button) findViewById(R.id.previous);
        next_bt = (Button) findViewById(R.id.next);
        add_bt = (Button) findViewById(R.id.add);
        dec_bt = (Button) findViewById(R.id.dec);
        prev_bt.setOnClickListener(this);
        next_bt.setOnClickListener(this);
        add_bt.setOnClickListener(this);
        dec_bt.setOnClickListener(this);
        
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		circleImageSwitchView.clear();
//		circleImage3DSwitchView_2.clear();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.previous:
			circleImageSwitchView.decListView();

			break;
		case R.id.next:
			circleImageSwitchView.addListView();

			break;
		case R.id.add:
			circleImageSwitchView.deleteCircleItem(0);

			break;
		case R.id.dec:
			circleImageSwitchView.deleteCircleItem(0);

			break;
		default:
			break;
		}
		
	}
}
