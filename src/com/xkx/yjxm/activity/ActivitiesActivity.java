package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

//活动资讯
public class ActivitiesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_activities);
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activities, menu);
		return true;
	}

}
