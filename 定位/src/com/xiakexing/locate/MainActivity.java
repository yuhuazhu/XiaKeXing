package com.xiakexing.locate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.brtbeacon.sdk.BRTBeacon;
import com.xiakexing.locate.BleScanService.BleBinder;
import com.xiakexing.locate.BleScanService.OnBleScanListener;

public class MainActivity extends Activity {

	private SurfaceView view;
	private SurfaceHolder holder;

	private Paint paint;

	private Point pointBR;
	private Point pointTL;

	private HandlerThread handlerThread;
	private Handler drawHandler;
	private TextView tv;

	int scannedCount = 0;
	private BleBinder binder;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("service", "onServiceConnected");
			binder = (BleBinder) service;
			binder.setOnBleScanListener(new OnBleScanListener() {

				@Override
				public void onPeriodScan(List<BRTBeacon> scanResultList) {
					if (scanResultList == null || scanResultList.size() == 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								tv.setText("没有");
							}
						});
						return;
					}
					list.clear();
					double x = 0, y = 0;
					float dis = 0;
					double sigma = 0;
					Beacon4Loc ble = null;
					final StringBuffer info = new StringBuffer();
					String name = "";
					// 构造方法的字符格式这里如果小数不足2位,会以0补足.
					DecimalFormat decimalFormat = new DecimalFormat("0.00");
					for (int i = 0; i < scanResultList.size(); i++) {
						BRTBeacon beacon = scanResultList.get(i);
						if (beacon.macAddress
								.equalsIgnoreCase("C6:30:73:33:B4:D1")) {// 墙角
							x = 1;
							y = 1;
							name = "墙角";
						} else if (beacon.macAddress
								.equalsIgnoreCase("F7:ED:22:A4:91:D4")) {// 饮水机
							x = 1;
							y = 15;
							name = "饮水机";
						} else if (beacon.macAddress
								.equalsIgnoreCase("DC:8B:BB:FC:F3:64")) {// 墙壁
							x = 9;
							y = 1;
							name = "墙壁";
						} else if (beacon.macAddress
								.equalsIgnoreCase("C5:48:BD:AE:1A:F5")) {// 玻璃
							x = 9;
							y = 15;
							name = "玻璃";
						} else if (beacon.macAddress
								.equalsIgnoreCase("EC:98:14:03:87:52")) {// 桌上
							x = 5.5f;
							y = 6;
							name = "桌上";
						} else {
							name = "";
						}
						if (!name.equals("")) {
							dis = (float) Utils.altCalDis(beacon.rssi,
									beacon.measuredPower);
							sigma = getSigma(beacon.rssi, beacon.measuredPower);
							ble = new Beacon4Loc(x, y, sigma, dis);
							list.add(ble);
							info.append(name + ":" + beacon.rssi + "\t");
						}
					}
					runOnUiThread(new Runnable() {
						public void run() {
							tv.setText(info.toString());
						}
					});
					Message msg = drawHandler.obtainMessage();
					Bundle data = new Bundle();
					data.putParcelableArrayList("beacons", list);
					msg.setData(data);
					drawHandler.sendMessage(msg);
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

	ArrayList<Beacon4Loc> list = new ArrayList<Beacon4Loc>();

	/**
	 * 返回可能的最大误差,单位:米
	 * 
	 * @param rssi
	 * @param txPower
	 * @return
	 */
	// TODO 待优化
	private double getSigma(int rssi, int txPower) {
		if (rssi >= txPower) {
			return 3;
		} else if (txPower - rssi <= 10) {
			return 5;
		} else {
			return 10;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv = (TextView) findViewById(R.id.textView1);

		handlerThread = new HandlerThread("handler-thread");
		handlerThread.start();

		drawHandler = new Handler(handlerThread.getLooper(),
				new Handler.Callback() {
					@Override
					public boolean handleMessage(Message msg) {
						Bundle data = msg.getData();
						ArrayList<Beacon4Loc> list = data
								.getParcelableArrayList("beacons");
						singleDraw(list);
						return true;
					}
				});

		Intent service = new Intent(this, BleScanService.class);

		bindService(service, conn, BIND_AUTO_CREATE);

		view = (SurfaceView) findViewById(R.id.surfaceView1);
		view.setZOrderOnTop(true);
		holder = view.getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.e("surface", "surfaceDestroyed()");
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.e("surface", "surfaceCreated()");
				paint = new Paint();
				paint.setColor(Color.RED);
				paint.setTextSize(40);
				setWallPosition();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.e("surface", "surfaceChanged(),width:" + width + ",height:"
						+ height);
			}
		});
		holder.setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}

	private void setWallPosition() {
		pointTL = new Point(10, 10);
		pointBR = new Point(800, 1200);
	}

	private float distance2px(double meters) {
		// TODO 算法待确定
		return (float) (80 * meters);
	}

	private void singleDraw(final ArrayList<Beacon4Loc> list) {
		Canvas canvas = holder.lockCanvas();
		try {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			// 画房间墙壁
			RectF rect = new RectF(pointTL.x, pointTL.y, pointBR.x, pointBR.y);
			paint.setColor(Color.RED);
			paint.setStyle(Style.STROKE);
			canvas.drawRoundRect(rect, 10, 10, paint);
			// 画蓝牙站点
			paint.setStyle(Style.FILL);
			paint.setColor(Color.BLUE);
			Bitmap bitmap = null;
			int dstWidth = 0;
			int dstHeight = 0;
			if (list.size() > 0) {
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.beacon_gray);
				dstWidth = bitmap.getWidth() / 4;
				dstHeight = bitmap.getHeight() / 4;
				bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,
						true);
			}
			for (int i = 0; i < list.size(); i++) {
				Beacon4Loc beacon = list.get(i);
				// canvas.drawCircle(distance2px(beacon.x),
				// distance2px(beacon.y),
				// 10,
				// paint);
				canvas.drawBitmap(bitmap, distance2px(beacon.x) - dstWidth / 2,
						distance2px(beacon.y) - dstHeight / 2, paint);
				if (i == list.size() - 1) {
					bitmap.recycle();
				}
			}
			// 画手机位置
			if (list.size() >= 3) {
				int n = list.size();
				double[] pointX = new double[n];
				double[] pointY = new double[n];
				double[] distance = new double[n];
				double[] sigma = new double[n];
				for (int i = 0; i < list.size(); i++) {
					Beacon4Loc beacon = list.get(i);
					pointX[i] = beacon.x;
					pointY[i] = beacon.y;
					distance[i] = beacon.distance;
					sigma[i] = beacon.sigma;
				}

				double[] solved = Multilaterator.multilaterate(pointX, pointY,
						distance);
				double[] delta = Multilaterator.correct(solved[0], solved[1],
						pointX, pointY, distance, sigma);
				solved[0] += delta[0];
				solved[1] += delta[1];
				paint.setColor(Color.RED);
				canvas.drawCircle(distance2px(solved[0]),
						distance2px(solved[1]), 20, paint);
			}
			holder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			holder.unlockCanvasAndPost(canvas);
		}
	}

	class ScanData {
		//
		// 信号强度总和
		int sumRssi;
		// 平均信号强度
		int averRssi;
		// 被扫描到的次数
		int scannedCount;
		// 最后一次被扫描到是第几次
		int lastScanNum;
	}

}
