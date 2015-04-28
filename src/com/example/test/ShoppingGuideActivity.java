package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

//µ¼¹º
public class ShoppingGuideActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_guide);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shopping_guide, menu);
		return true;
	}

}
