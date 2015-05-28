package com.example.rotate;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brtbeacon.sdk.BRTBeacon;
import com.example.rotate.BleScanService.BleBinder;
import com.example.rotate.BleScanService.OnBleScanListener;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView m_im;
	private boolean isStart;
	private boolean isBack;
	private TextView textView1;
	private RelativeLayout daolayout;
	private ImageButton imageButton1;
	private RelativeLayout headlay;

	private BleBinder bleBinder;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected");
			bleBinder = (BleBinder) service;

			bleBinder.setRegion(null);// 空代表扫描所有
			bleBinder.setOnBleScanListener(new OnBleScanListener() {

				@Override
				public void onPeriodScan(List<BRTBeacon> scanResultList) {
					isStart = false;
					m_im.clearAnimation();
					m_im.setVisibility(View.INVISIBLE);
					imgbtnScan.setBackgroundResource(R.drawable.bt1);
					processScanResult(scanResultList);
				}

				@Override
				public void onNearBleChanged(BRTBeacon oriBeacon,
						BRTBeacon desBeacon) {

				}

				@Override
				public void onNearBeacon(BRTBeacon brtBeacon) {

				}
			});
		}
	};

	/**
	 * 最多导游数
	 */
	int maxDrawCount = 4;

	Comparator<? super BRTBeacon> rssiComparator = new Comparator<BRTBeacon>() {

		@Override
		public int compare(BRTBeacon lhs, BRTBeacon rhs) {
			return rhs.rssi - lhs.rssi;
		}
	};

	private void processScanResult(List<BRTBeacon> scanResultList) {
		drawBeacons(4);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
	}

	int[] heads = new int[] { R.drawable.img_li, R.drawable.img_liu,
			R.drawable.img_zhang, R.drawable.img_zhou };

	int[] rights = new int[] { 50, 500, -300, -500 };
	int[] tops = new int[] { 75, -140, 500, 200 };
	CircleImageView[] cr;
	private ImageButton imgbtnScan;

	private void drawBeacons(int count) {
		headlay = (RelativeLayout) findViewById(R.id.headlay);
		imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		cr = new CircleImageView[4];

		for (int i = 0; i < count; i++) {
			RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
					100, 100);
			cr[i] = new CircleImageView(this);
			lp.rightMargin = rights[i];
			lp.topMargin = tops[i];
			lp.addRule(RelativeLayout.LEFT_OF, imageButton1.getId());
			if (i % 2 == 0) {
				lp.addRule(RelativeLayout.ABOVE, imageButton1.getId());
			} else {
				lp.addRule(RelativeLayout.BELOW, imageButton1.getId());
			}
			cr[i].setLayoutParams(lp);
			cr[i].setBackgroundResource(heads[i]);
			final int index = i;
			cr[i].setOnClickListener(new OnClickListener() {
				boolean isLarge = false;

				@Override
				public void onClick(View v) {
					isLarge = !isLarge;
					if (isLarge) {
						// 放大头像
						Animation scaleAnimation = new ScaleAnimation(1f, 2.0f,
								1f, 2.0f);
						scaleAnimation.setDuration(1000);
						scaleAnimation.setFillAfter(false);
						cr[index].startAnimation(scaleAnimation);
						// TODO 显示信息
					} else {
						// 还原
						Animation scaleAnimation = new ScaleAnimation(2f, 1f,
								2f, 1f);
						scaleAnimation.setDuration(500);
						scaleAnimation.setFillAfter(true);
						cr[index].startAnimation(scaleAnimation);
						// TODO 隐藏信息
					}
				}
			});
			headlay.addView(cr[i], lp);
		}
	}

	public void initUI() {
		imgbtnScan = (ImageButton) findViewById(R.id.button1);
		m_im = (ImageView) findViewById(R.id.imageView1);
		m_im.setVisibility(View.INVISIBLE);
		textView1 = (TextView) findViewById(R.id.textView1);
		daolayout = (RelativeLayout) findViewById(R.id.daolayout);
	}

	public void process(View v) {
		// 更改状态
		isStart = !isStart;
		v.setBackgroundResource(isStart ? R.drawable.bt2 : R.drawable.bt1);
		final ImageButton button = (ImageButton) v;

		if (isStart) {
			// 开启蓝牙扫描
			Intent service = new Intent(MainActivity.this, BleScanService.class);
			bindService(service, conn, BIND_AUTO_CREATE);
			AnimationSet animationSet = new AnimationSet(true);
			RotateAnimation animation = new RotateAnimation(0f, 360f, 0, 0);
			animation.setRepeatCount(3);
			animation.setDuration(4000);// 设置动画持续时间
			animationSet.addAnimation(animation);
			animationSet.setInterpolator(new LinearInterpolator());
			m_im.startAnimation(animationSet);
			m_im.setVisibility(View.VISIBLE);
			animationSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					isStart = false;
					m_im.clearAnimation();
					m_im.setVisibility(View.INVISIBLE);
					button.setBackgroundResource(R.drawable.bt1);
				}
			});
		} else {
			// 解绑服务,取消扫描.
			unbindService(conn);
			// 清除画上去的头像
			if (cr != null) {
				for (int i = 0; i < cr.length; i++) {
					headlay.removeView(cr[i]);
				}
			}
			m_im.clearAnimation();
			m_im.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (isBack) {
				finish();
				System.exit(0);
			} else {
				isBack = true;
				Toast.makeText(MainActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				m_im.postDelayed(new Runnable() {
					public void run() {
						isBack = false;
					}
				}, 2000);
			}

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
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

}
