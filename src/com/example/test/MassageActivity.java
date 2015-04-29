package com.example.test;

import com.example.test.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

//°´Ä¦
public class MassageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_massage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.massage, menu);
		return true;
	}

}
