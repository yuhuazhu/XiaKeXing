package com.example.rotate;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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

	private ImageView ivRadar;
	private boolean isScanning = false;
	private boolean isBack;
	private RelativeLayout rlDaoyouInfo;
	private RelativeLayout headlay;

	private BleBinder bleBinder;
	/**
	 * 最多导游数
	 */
	int maxDrawCount = 4;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected");
			bleBinder = (BleBinder) service;

			bleBinder.setRegion("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0");// 空代表扫描所有
			bleBinder.setOnBleScanListener(new OnBleScanListener() {

				@Override
				public void onPeriodScan(List<BRTBeacon> scanResultList) {
					if (scanResultList.size() != 0) {
						isScanning = false;
						ivRadar.clearAnimation();
						processScanResult(scanResultList);
					}
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

	Comparator<? super BRTBeacon> rssiComparator = new Comparator<BRTBeacon>() {

		@Override
		public int compare(BRTBeacon lhs, BRTBeacon rhs) {
			return rhs.rssi - lhs.rssi;
		}
	};

	private void processScanResult(List<BRTBeacon> scanResultList) {
		tvTip.setText("已搜索到附近" + scanResultList.size() + "位导游");
		drawBeacons(scanResultList);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
	}

	int[] heads = new int[] { R.drawable.img_lixiaohua, R.drawable.img_liuna,
			R.drawable.img_zhangyang, R.drawable.img_zhoutongtong };

	int[] rights = new int[] { 50, 150, -400, -300 };
	int[] tops = new int[] { 750, -140, 200, 50 };
	CircleImageView[] cr;
	private ImageButton imgbtnScan;
	private TextView tvDaoyouName;
	private TextView tvDaoyouPhone;
	private TextView tvCerType;
	private TextView tvCerNum;
	private CircleImageView ivHead;
	private TextView tvTip;

	private void drawBeacons(final List<BRTBeacon> list) {
		cr = new CircleImageView[list.size()];

		for (int i = 0; i < list.size() && i <= 3; i++) {
			RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
					100, 100);
			cr[i] = new CircleImageView(this);
			lp.rightMargin = rights[i];
			lp.topMargin = tops[i];
			lp.addRule(RelativeLayout.LEFT_OF, R.id.imageButton1);
			if (i % 2 == 0) {
				lp.addRule(RelativeLayout.ABOVE, R.id.imageButton1);
			} else {
				lp.addRule(RelativeLayout.BELOW, R.id.imageButton1);
			}
			cr[i].setLayoutParams(lp);
			cr[i].setBackgroundResource(heads[getID(list.get(i).macAddress)]);
			final int index = i;
			cr[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int childCount = headlay.getChildCount();
					for (int j = 0; j < childCount; j++) {
						View view = headlay.getChildAt(j);
						view.clearAnimation();
					}
					Animation scaleAnimation = new ScaleAnimation(1f, 2.0f, 1f,
							2.0f);
					scaleAnimation.setDuration(1000);
					scaleAnimation.setFillAfter(true);
					cr[index].startAnimation(scaleAnimation);
					// TODO 跳转页面
					// Intent intent = new Intent(MainActivity.this, cls);
					// startActivity(intent);
					processInfoShow(getID(list.get(index).macAddress));
				}
			});
			headlay.addView(cr[i], lp);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.enable();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 关闭蓝牙
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.disable();
	}

	private int getID(String mac) {
		mac = mac.trim();
		// TODO 蓝牙和导游匹配
		if (mac.equalsIgnoreCase("54:4A:16:2D:B0:7D")) {// 李晓华
			return 0;
		} else if (mac.equalsIgnoreCase("54:4A:16:2D:A0:DC")) {// 刘娜
			return 1;
		} else if (mac.equalsIgnoreCase("54:4A:16:2D:AD:F9")) {// 张阳
			return 2;
		} else if (mac.equalsIgnoreCase("54:4A:16:2D:AA:3C")) {// 周彤彤
			return 3;
		}
		return -1;
	}

	private void processInfoShow(int id) {
		String name = "李晓华", phone = "15805934402", type = "国导证", num = "D-3501-003469";
		int head = heads[0];

		// TODO 更改信息
		if (id == 0) {
			name = "李晓华";
			phone = "15805934402";
			type = "国导证";
			num = "D-3501-003469";
			head = heads[0];
		} else if (id == 1) {
			name = "刘娜";
			phone = "15805934402";
			type = "国导证";
			num = "D-3501-003469";
			head = heads[1];
		} else if (id == 2) {
			name = "张阳";
			phone = "15805934402";
			type = "国导证";
			num = "D-3501-003469";
			head = heads[2];
		} else if (id == 3) {
			name = "周彤彤";
			phone = "15805934402";
			type = "国导证";
			num = "D-3501-003469";
			head = heads[3];
		}
		tvDaoyouName.setText(name);
		tvDaoyouPhone.setText(phone);
		tvCerType.setText(type);
		tvCerNum.setText(num);
		ivHead.setBackgroundResource(head);
		rlDaoyouInfo.setVisibility(View.VISIBLE);

	}

	public void initUI() {
		tvDaoyouName = (TextView) findViewById(R.id.textView2);
		tvTip = (TextView) findViewById(R.id.textView1);
		tvDaoyouPhone = (TextView) findViewById(R.id.textView3);
		tvCerType = (TextView) findViewById(R.id.txtd);
		tvCerNum = (TextView) findViewById(R.id.tvCerNum);
		ivHead = (CircleImageView) findViewById(R.id.img_head);
		headlay = (RelativeLayout) findViewById(R.id.headlay);
		imgbtnScan = (ImageButton) findViewById(R.id.imageButton1);
		ivRadar = (ImageView) findViewById(R.id.imageView1);
		rlDaoyouInfo = (RelativeLayout) findViewById(R.id.daolayout);
	}

	public void process(View v) {
		// 更改状态
		isScanning = !isScanning;
		rlDaoyouInfo.setVisibility(View.GONE);
		v.setBackgroundResource(isScanning ? R.drawable.bt2 : R.drawable.bt1);

		if (isScanning) {
			// 开启蓝牙扫描
			Intent service = new Intent(MainActivity.this, BleScanService.class);
			bindService(service, conn, BIND_AUTO_CREATE);
			playAnimation();
		} else {
			// 解绑服务,取消扫描.
			unbindService(conn);
			// 清除画上去的头像
			if (cr != null) {
				for (int i = 0; i < cr.length; i++) {
					headlay.removeView(cr[i]);
				}
			}
			ivRadar.clearAnimation();
			ivRadar.setVisibility(View.INVISIBLE);
		}
	}

	private void playAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		RotateAnimation animation = new RotateAnimation(0f, 360f, 0, 0);
		animation.setRepeatCount(3);
		animation.setDuration(4000);// 设置动画持续时间
		animationSet.addAnimation(animation);
		animationSet.setInterpolator(new LinearInterpolator());
		ivRadar.startAnimation(animationSet);
		ivRadar.setVisibility(View.VISIBLE);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO 移除头像
				// 启动扫描
				Log.e("size", "size :" + headlay.getChildCount());
				for (int i = 0; i <= headlay.getChildCount() + 2; i++) {
					try {
						headlay.removeViewAt(1);
					} catch (Exception e) {
						Log.e("ex", "" + e.toString());
					}
				}
				tvTip.setText("正在搜索...");
				rlDaoyouInfo.setVisibility(View.INVISIBLE);
				imgbtnScan.setBackgroundResource(R.drawable.bt2);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				try {
					imgbtnScan.setBackgroundResource(R.drawable.bt1);
					isScanning = false;
					ivRadar.clearAnimation();
					ivRadar.setVisibility(View.INVISIBLE);
					unbindService(conn);
				} catch (Exception e) {
					Log.e("ex", e.toString());
				}
			}
		});
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
				ivRadar.postDelayed(new Runnable() {
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
