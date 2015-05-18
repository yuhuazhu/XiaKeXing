package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.R.layout;
import com.xkx.yjxm.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

//选择界面
public class ChoiceActivity extends BaseActivity {

	private boolean isList;
	private ImageButton imageButton3;
	private ImageButton imageButton2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choice);
		// initUI();

		Intent intent = getIntent();
		isList = intent.getBooleanExtra("列表", true);
	}
	
	// private void initUI()
	// {
	// imageButton3 =(ImageButton)findViewById(R.id.imageButton3);
	// imageButton3.setOnClickListener(this);
	// imageButton2 =(ImageButton)findViewById(R.id.imageButton2);
	// imageButton2.setOnClickListener(this);
	// }
	// 返回
	public void Back(View v) {
		finish();
	}

	public void home(View v) {
		Intent intent = null;

		intent = new Intent(this, MainActivity.class);

		startActivity(intent);
	}

	// 跳转到观音山（ 导览 or 路线）
	public void StartGuanYinShan(View v) {
		Intent intent = null;
		if (isList) {
			intent = new Intent(this, RouteActivity.class);
		} else {
			intent = new Intent(this, RouteMapActivity.class);
		}
		intent.putExtra("name", "观音山");
		startActivity(intent);
	}

	// 跳转到胡里山炮台（ 导览 or 路线）
	public void StartHuLiShan(View v) {
		// Intent intent = null;
		// if(isList)
		// {
		// intent = new Intent(this, RouteActivity.class);
		// }
		// else
		// {
		// intent = new Intent(this, RouteMapActivity.class);
		// }
		// intent.putExtra("name", "胡里山");
		Toast.makeText(this, "即将上线,敬请期待", Toast.LENGTH_SHORT).show();
		// startActivity(intent);
	}

	// 跳转到鼓浪屿（ 导览 or 路线）
	public void StartGuLangYu(View v) {
		// Intent intent = null;
		// if(isList)
		// {
		// intent = new Intent(this, RouteActivity.class);
		// }
		// else
		// {
		// intent = new Intent(this, RouteMapActivity.class);
		// }
		// intent.putExtra("name", "鼓浪屿");
		Toast.makeText(this, "即将上线,敬请期待", Toast.LENGTH_SHORT).show();
		// startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choice, menu);
		return true;
	}

}
