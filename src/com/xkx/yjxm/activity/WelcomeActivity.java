package com.xkx.yjxm.activity;

import com.example.test.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		StartMain();
	}

	private void StartMain() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}

		}, 2000);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
