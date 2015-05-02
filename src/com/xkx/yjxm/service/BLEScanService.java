package com.xkx.yjxm.service;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.test.R;
import com.example.test.RouteMapActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEScanService extends IntentService {

	private BluetoothAdapter blueAdapter;
	private BroadcastReceiver receiver;
	private LeScanCallback callback;
	private HashMap<BluetoothDevice, Boolean> hasTipMap = new HashMap<BluetoothDevice, Boolean>();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("test", "onCreate->" + Thread.currentThread().getName());
		blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!blueAdapter.isEnabled()) {
			blueAdapter.enable();
		}
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO
				// String action = intent.getAction();
				// if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				// int bluetoothState = intent.getIntExtra(
				// BluetoothAdapter.EXTRA_STATE,
				// BluetoothAdapter.STATE_OFF);
				// if (bluetoothState == BluetoothAdapter.STATE_OFF) {
				// blueAdapter.enable();
				// }
				// }
			}
		};

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, intentFilter);

		callback = new LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				final BluetoothDevice temp = device;
				String name = verify(device);
				Log.e("blue", verify(device) + ",rssi=" + rssi);
				// if (name.contains("xBeacon")) {
				// if (rssi > -80) {// TODO 足够靠近,开始播报
				// if (!hasTipMap.containsKey(device)) {
				// hasTipMap.put(device, false);
				// }
				// if (!hasTipMap.get(device)) {
				// Log.e("blue", verify(temp) + "播报提示");
				// hasTipMap.put(device, true);
				// SharedPreferences sp = getSharedPreferences(
				// "prefs", Context.MODE_PRIVATE);
				// boolean isOnRouteMapActivity = sp.getBoolean(
				// "isOnRouteMapActivity", false);
				// if (isOnRouteMapActivity) {
				// sendBroad(device);
				// } else {
				// showNotification(device);
				// }
				// }
				// } else {// 不够靠近，重新扫描
				// Log.e("blue", "不够靠近，2秒后重新扫描");
				// resetScan();
				// }
				// }
			}

			private void sendBroad(BluetoothDevice device) {
				LocalBroadcastManager localBroadMgr = LocalBroadcastManager
						.getInstance(BLEScanService.this);
				Intent it = new Intent();
				it.setAction("com.yjxm.notify");
				it.putExtra("address", device.getAddress());
				localBroadMgr.sendBroadcast(it);
			}

			private void showNotification(BluetoothDevice device) {
				NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				NotificationCompat.Builder builder = new NotificationCompat.Builder(
						BLEScanService.this);
				Intent it = new Intent(BLEScanService.this,
						RouteMapActivity.class);
				it.putExtra("address", device.getAddress());
				it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent pi = PendingIntent.getActivity(
						BLEScanService.this, 1, it,
						PendingIntent.FLAG_UPDATE_CURRENT);
				Uri sound = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Notification notification = builder.setAutoCancel(true)
						.setTicker(verify(device) + "tickertext")
						.setContentTitle("contenttitle")
						.setContentText("content text").setContentIntent(pi)
						.setSmallIcon(R.drawable.ic_launcher)
						.setSound(sound, AudioManager.STREAM_NOTIFICATION)
						.build();
				manager.notify("test", 1, notification);
			}

			private String verify(BluetoothDevice device) {
				return device.getName()
						+ "-"
						+ device.getAddress().substring(
								device.getAddress().length() - 2) + " ";
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void resetScan() {
		// blueAdapter.stopLeScan(callback);
		blueAdapter.startLeScan(callback);
	}

	public BLEScanService() {
		super("BLEScanService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("test", "onHandleIntent->" + Thread.currentThread().getName());
		resetScan();
	}
}
