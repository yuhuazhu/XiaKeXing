package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.R.layout;
import com.xkx.yjxm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_search);
		ImageButton button = (ImageButton) findViewById(R.id.imageButton1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}
	public void Back(View v) {
		finish();
	}
	// 跳转到活动资讯
	public void StartNavigation(View v) {
		Intent intent = new Intent(this, NavigationActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

}
