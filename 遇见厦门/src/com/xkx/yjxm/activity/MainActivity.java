package com.xkx.yjxm.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.xkx.yjxm.R;

public class MainActivity extends BaseActivity implements OnTouchListener,
		OnGestureListener {
	GestureDetector mGestureDetector;
	private boolean isExit;
	private ImageView im_paopao;
	private ImageView im_paopao_x;
	private static final int FLING_MIN_DISTANCE = 300;
	private static final int FLING_MIN_VELOCITY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mGestureDetector = new GestureDetector(this);
		im_paopao = (ImageView) findViewById(R.id.img_paopao);
		FrameLayout ll = (FrameLayout) findViewById(R.id.ll);
		ll.setOnTouchListener(this);
		ll.setLongClickable(true);    
		im_paopao_x = (ImageView) findViewById(R.id.img_paopao_x);
		StartPaoPao();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getAction() == null) {
			finish();
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			// overridePendingTransition(0, 0);
		} else {
			// 其他逻辑
		}
		super.onNewIntent(intent);
	}

	// 返回
	public void Backs(View v) {
		finish();
	}

	// 跳转到左侧界面
	public void StartLeft(View v) {
		Intent intent = new Intent(this, LeftActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
	}

	// 跳转到活动资讯
	public void StartActivities(View v) {
		Intent intent = new Intent(this, ActivitiesActivity.class);
		startActivity(intent);
	}

	// 跳转到映像
	public void StartReflex(View v) {
		Intent intent = new Intent(this, ReflexActivity.class);
		startActivity(intent);
	}

	// 跳转到导购
	public void StartShoppingGuide(View v) {
		Intent intent = new Intent(this, ShoppingGuideActivity.class);
		startActivity(intent);
	}

	// 跳转到导航的搜索界面
	public void StartSearch(View v) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}

	// 跳转到导览(选择界面)
	public void StartGuide(View v) {

		Intent intent = new Intent(this, ChoiceActivity.class);
		intent.putExtra("列表", false);
		// Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}

	// 跳转到路线推荐(选择界面)
	public void StartRoute(View v) {
		Intent intent = new Intent(this, ChoiceActivity.class);
		intent.putExtra("列表", true);
		// Intent intent = new Intent(this, RouteActivity.class);
		startActivity(intent);
	}

	// 泡泡动画
	public void StartPaoPao() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth = dm.widthPixels;
		int iHeight = dm.heightPixels;
		final int runW;
		final int runH;
		runW = iWidth * 20 / 100;
		runH = iHeight * 55 / 100 - 40;
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_paopao);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 移动到屏幕外，这样就不会出现图片跳动
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.setMargins(-100, 0, 0, 0);
				im_paopao_x.setLayoutParams(param);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				im_paopao.clearAnimation();
				im_paopao.setBackgroundResource(R.drawable.ic_paopao);
				int left = im_paopao.getLeft();
				left += runW;
				int top = im_paopao.getTop();
				top -= runH;
				RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param1.setMargins(left, top, 0, 0);
				im_paopao.setLayoutParams(param1);
				param2.setMargins(left + im_paopao.getWidth() / 3 * 2, top, 0,
						0);
				im_paopao_x.setLayoutParams(param2);
				im_paopao_x.setVisibility(View.VISIBLE);
				im_paopao.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						im_paopao.setVisibility(View.INVISIBLE);
						im_paopao_x.setVisibility(View.INVISIBLE);
						StartActivities(v);
					}
				});
				im_paopao_x.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						im_paopao.setVisibility(View.INVISIBLE);
						im_paopao_x.setVisibility(View.INVISIBLE);
					}
				});
			}
		});
		im_paopao.startAnimation(anim);

		// AnimationSet
	}

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		// float y1 = e1.getY(), y2 = e2.getY();
		// if (y1 -y2 > 120) {
		// if (mDirection != SOUTH) {
		// mNextDirection = NORTH;
		// }
		// Log.d(this.getClass().getName(), "To UP" + "(" + y1
		// + "," + y2 + ")");
		// return (true);
		// } else if (y1 - y2 < -120) {
		// if (mDirection != NORTH) {
		// mNextDirection = SOUTH;
		// }
		// Log.d(this.getClass().getName(), "To Down" + "(" + y1
		// + "," + y2 + ")");
		// return (true);
		// }
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
