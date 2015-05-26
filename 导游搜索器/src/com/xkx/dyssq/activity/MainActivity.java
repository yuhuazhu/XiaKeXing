package com.xkx.dyssq.activity;

import com.xkx.dyssq.R;
import com.xkx.dyssq.R.layout;
import com.xkx.dyssq.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	//Ìø×ªÒ³Ãæ
	public void startIn(View v) 
	{
		Intent intent = null;
		switch (v.getId()) 
		{
		case R.id.imageButton1:
			intent = new Intent(this, SearchActivity.class);
			break;
		case R.id.imageButton2:
			intent = new Intent(this, DestinationListActivity.class);
			break;
		case R.id.imageButton3:
			intent = new Intent(this, MoreDestinationActivity.class);
			break;
		case R.id.imageButton4:
			intent = new Intent(this, RouteDetailsActivity.class);
			break;
		case R.id.imageButton5:
			intent = new Intent(this, ShakeActivity.class);
			break;
		case R.id.imageButton6:
			intent = new Intent(this, PersonalCenterActivity.class);
			break;

		default:
			break;
		}
		startActivity(intent);
	}
}
