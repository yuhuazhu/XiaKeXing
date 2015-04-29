package com.example.test;

import com.example.test.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhotoActivity extends Activity implements OnClickListener {
	private Button btnty;
	private Button btnback;

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
		btnty = (Button) findViewById(R.id.btnty);
		btnty.setOnClickListener(this);
		btnback = (Button) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnty:
//			Intent i = new Intent(
//					Intent.ACTION_PICK,
//					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// µ÷ÓÃandroidµÄÍ¼¿â
//			startActivityForResult(i, 2);
			 Intent intent = new Intent();
			 intent.setClass(PhotoActivity.this, PhotoWashActivity.class);
			
			 startActivity(intent);
			break;
		case R.id.btnback:

			finish();
			break;
		default:
			break;
		}
	}
}
