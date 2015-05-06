package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class PhotoActivity extends Activity implements OnClickListener {
	private Button btnty;
	private ImageButton btnback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo);

		initData();
		initUI();
	}
	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}
	private void initData() {

	}

	private void initUI() {
		btnty = (Button) findViewById(R.id.btnty);
		btnty.setOnClickListener(this);
		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnty:
			// Intent i = new Intent(
			// Intent.ACTION_PICK,
			// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//
			// 调用android的图库
			// startActivityForResult(i, 2);
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
