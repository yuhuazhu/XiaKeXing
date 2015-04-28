package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

//活动资讯
public class ActivitiesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activities);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activities, menu);
		return true;
	}

}
