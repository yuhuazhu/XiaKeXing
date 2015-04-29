package com.example.test;

import com.example.test.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class PhotoWashActivity extends Activity  implements OnClickListener{
	private Button btnback;
	private LinearLayout laybtn01;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photowash);
		
		initData();
		initUI();
	}
	
	private void initData() {

		
	}

	private void initUI() {
		btnback = (Button)findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		laybtn01 =(LinearLayout)findViewById(R.id.laybtn01);
		laybtn01.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnback:
			
			finish();
			break;
		case R.id.laybtn01:
			Intent intent = new Intent();
			intent.setClass(this, PhotoUploadActivity.class);

			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
