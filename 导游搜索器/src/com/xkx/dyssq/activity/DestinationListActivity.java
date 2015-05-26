package com.xkx.dyssq.activity;

import com.xkx.dyssq.R;
import com.xkx.dyssq.R.layout;
import com.xkx.dyssq.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DestinationListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_destinationlist);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.destination_list, menu);
		return true;
	}

}
