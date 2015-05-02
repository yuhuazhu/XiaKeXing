package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

//导航
public class NavigationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_navigation);
		ImageButton button = (ImageButton) findViewById(R.id.imageButton1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

}
