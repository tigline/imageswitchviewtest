/**
 * 
 */
package com.example.imageswitchviewtest;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @Project ImageSwitchViewTest	
 * @author houxb
 * @Date 2015-11-18
 */
public class InitActivity extends Activity implements OnClickListener {
	
	Button imageh_bt, circle_bt, imagev_bt;
	Intent intent;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);
        
        imageh_bt = (Button) findViewById(R.id.imageh);
        imagev_bt = (Button) findViewById(R.id.imagev);
        circle_bt = (Button) findViewById(R.id.circleh);
        
        imageh_bt.setOnClickListener(this);
        imagev_bt.setOnClickListener(this);
        circle_bt.setOnClickListener(this);

    }

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageh:
			intent = new Intent(InitActivity.this, ImageActivity.class);
			startActivity(intent);
			break;
		case R.id.circleh:
			intent = new Intent(InitActivity.this, CircleActivity.class);
			startActivity(intent);
			break;
		case R.id.imagev:
			intent = new Intent(InitActivity.this, MainActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
