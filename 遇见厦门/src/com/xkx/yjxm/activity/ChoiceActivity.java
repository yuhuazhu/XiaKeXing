package com.xkx.yjxm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xkx.yjxm.R;

//选择界面
public class ChoiceActivity extends BaseActivity implements OnTouchListener,
OnGestureListener {
GestureDetector mGestureDetector;

	private boolean isList;
	private ImageButton imageButton3;
	private ImageButton imageButton2;

	private static final int FLING_MIN_DISTANCE = 300;
	private static final int FLING_MIN_VELOCITY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choice);
		mGestureDetector = new GestureDetector(this);
		// initUI();
		RelativeLayout ll = (RelativeLayout) findViewById(R.id.relay01);
		ll.setOnTouchListener(this);
		ll.setLongClickable(true);  
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

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling left
			Toast.makeText(this, "向左手势 ", Toast.LENGTH_SHORT).show();
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling right
			finish();
		}
		return false;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		Log.i("touch", "touch");
		return mGestureDetector.onTouchEvent(event);
	}

}
