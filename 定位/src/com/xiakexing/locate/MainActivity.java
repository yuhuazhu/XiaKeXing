package com.xiakexing.locate;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
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

	float dis1 = 0;
	float dis2 = 0;
	float dis3 = 0;
	float dis4 = 0;

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
					dis1 = 0;
					dis2 = 0;
					dis3 = 0;
					dis4 = 0;
					for (int i = 0; i < scanResultList.size(); i++) {
						float x = 0, y = 0, z = 0;
						BRTBeacon beacon = scanResultList.get(i);
						if (beacon.macAddress
								.equalsIgnoreCase("C6:30:73:33:B4:D1")) {// 墙角
							x = 1;
							y = 1;
							z = 1;
							dis1 = (float) Utils.altCalDis(beacon.rssi,
									beacon.measuredPower);
							Log.e("distance", "dis1=" + dis1);
							Beacon4Loc object = new Beacon4Loc(x, y, z, dis1);
							list.add(object);
						} else if (beacon.macAddress
								.equalsIgnoreCase("F7:ED:22:A4:91:D4")) {// 饮水机
							x = 1;
							y = 15;
							z = 1.3f;
							dis2 = (float) Utils.altCalDis(beacon.rssi,
									beacon.measuredPower);
							Log.e("distance", "dis2=" + dis2);
							Beacon4Loc object = new Beacon4Loc(x, y, z, dis2);
							list.add(object);
						} else if (beacon.macAddress
								.equalsIgnoreCase("DC:8B:BB:FC:F3:64")) {// 墙壁
							x = 9;
							y = 1;
							z = 1.7f;
							dis3 = (float) Utils.altCalDis(beacon.rssi,
									beacon.measuredPower);
							Log.e("distance", "dis3=" + dis3);
							Beacon4Loc object = new Beacon4Loc(x, y, z, dis3);
							list.add(object);
						} else if (beacon.macAddress
								.equalsIgnoreCase("C5:48:BD:AE:1A:F5")) {// 玻璃
							x = 9;
							y = 15;
							z = 1.65f;
							dis4 = (float) Utils.altCalDis(beacon.rssi,
									beacon.measuredPower);
							Log.e("distance", "dis4=" + dis4);
							Beacon4Loc object = new Beacon4Loc(x, y, z, dis4);
							list.add(object);
						}
					}
					runOnUiThread(new Runnable() {
						public void run() {
							tv.setText("个数:" + list.size() + ",墙角:" + dis1
									+ ",饮水机:" + dis2 + ",\n  墙壁:" + dis3
									+ ",玻璃:" + dis4);
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

	private float distance2px(float meters) {
		// TODO 算法待确定
		return 80 * meters;
	}

	private void singleDraw(final ArrayList<Beacon4Loc> list) {
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		// 画房间墙壁
		RectF rect = new RectF(pointTL.x, pointTL.y, pointBR.x, pointBR.y);
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(rect, 10, 10, paint);
		// 画四个蓝牙站点
		paint.setStyle(Style.FILL);
		paint.setColor(Color.BLUE);
		for (int i = 0; i < list.size(); i++) {
			Beacon4Loc beacon = list.get(i);
			float xyz[] = new float[] { beacon.getX(), beacon.getY(),
					beacon.getZ() };
			try {
				Location.setPoint(xyz, i + 1);
				Location.setDistance(beacon.getDistance(), i + 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("point", "x=" + distance2px(xyz[0]) + ",y="
					+ distance2px(xyz[1]));
			canvas.drawCircle(distance2px(xyz[0]), distance2px(xyz[1]), 10,
					paint);
		}
		// 画手机位置
		float x = 0;
		float y = 0;
		try {
			float[] location = Location.location();
			x = location[0];
			y = location[1];
			Log.e("location", "x:" + x + ",y:" + y);
		} catch (Exception e1) {
			Log.e("location", e1.toString());
		}
		paint.setColor(Color.RED);
		canvas.drawCircle(distance2px(x), distance2px(y), 20, paint);
		holder.unlockCanvasAndPost(canvas);
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
