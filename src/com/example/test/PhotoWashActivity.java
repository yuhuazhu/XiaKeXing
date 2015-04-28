package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhotoWashActivity extends Activity  implements OnClickListener{
	private Button btnback;
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
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnback:
			
			finish();
			break;

		default:
			break;
		}
	}
}
