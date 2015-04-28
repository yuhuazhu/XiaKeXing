package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhotoActivity extends Activity implements OnClickListener{
	private Button btnty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		initData();
		initUI();
	}
	
	private void initData() {

		
	}

	private void initUI() {
		btnty =(Button)findViewById(R.id.btnty);
		btnty.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnty:
			
			Intent intent = new Intent();
			intent.setClass(PhotoActivity.this, PhotoWashActivity.class);
			
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}


