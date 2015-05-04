package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.R.layout;
import com.xkx.yjxm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

//选择界面
public class ChoiceActivity extends Activity {

	private boolean isList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choice);
		Intent intent = getIntent();
		isList = intent.getBooleanExtra("列表", true);
	}
	
	// 返回
	public void Back(View v) {
		finish();
	}
	
	// 跳转到观音山（ 导览 or 路线）
	public void StartGuanYinShan(View v) {
		Intent intent = null;
		if(isList)
		{
			intent = new Intent(this, RouteActivity.class);
		}
		else
		{
			intent = new Intent(this, GuideActivity.class);
		}
		intent.putExtra("name", "观音山");
		startActivity(intent);
	}

	// 跳转到胡里山炮台（ 导览 or 路线）
	public void StartHuLiShan(View v) {
		Intent intent = null;
		if(isList)
		{
			intent = new Intent(this, RouteActivity.class);
		}
		else
		{
			intent = new Intent(this, GuideActivity.class);
		}
		intent.putExtra("name", "胡里山");
//		startActivity(intent);
	}
	
	// 跳转到鼓浪屿（ 导览 or 路线）
	public void StartGuLangYu(View v) {
		Intent intent = null;
		if(isList)
		{
			intent = new Intent(this, RouteActivity.class);
		}
		else
		{
			intent = new Intent(this, GuideActivity.class);
		}
		intent.putExtra("name", "鼓浪屿");
//		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choice, menu);
		return true;
	}

}
