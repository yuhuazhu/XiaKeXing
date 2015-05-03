package com.xkx.yjxm.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEService extends Service {

	private static final float RSSI_TO_TRIGGER = -70f;

	private BleBinder bleBinder = new BleBinder();

	private OnProximityBleChangedListener listener;

	private BleFilter bleFilter;

	private boolean exitScan = false;

	private BluetoothDevice proximityBleDevice = null;

	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	private int scannedCount;

	private int maxRssi = -1000;

	public BLEService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("scan", "onCreate()");
		enableBlueToothIfClosed();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return bleBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public void startScanBLE() {
		exitScan = false;
		new Thread() {
			public void run() {
				continualLeScan();
			}
		}.start();
	}

	private void enableBlueToothIfClosed() {
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	private void continualLeScan() {
		while (true) {
			if (exitScan) {
				break;
			}
			maxLeScan();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void maxLeScan() {
		for (int i = 0; i < 4; i++) {
			adapter.startLeScan(callback);
			scannedCount++;
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			adapter.stopLeScan(callback);
		}
		if (maxRssi >= RSSI_TO_TRIGGER) {
			listener.onConditionTriggerSuccess(proximityBleDevice);
		} else {
			listener.onConditionTriggerFailed(proximityBleDevice);
		}
		maxRssi = -1000;
		proximityBleDevice = null;
//		if (scannedCount % 4 == 0) {
//		}
	}

	public BluetoothDevice getProximityBleDevice(int rssiFilter) {
		// TODO
		return proximityBleDevice;
	}

	public BluetoothDevice getProximityBleDevice() {
		return proximityBleDevice;
	}

	public void setBleFilter(BleFilter filter) {
		this.bleFilter = filter;
	}

	public void stopScanBLE() {
		exitScan = true;
		adapter.stopLeScan(callback);
	}

	public void setOnProximityBleChangedListener(
			OnProximityBleChangedListener listener) {
		this.listener = listener;
	}

	private LeScanCallback callback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.e("scan", device.getAddress() + "rssi:" + rssi);
			if (maxRssi < rssi) {
				maxRssi = rssi;
				proximityBleDevice = device;
			}
		}
	};

	public void onDestroy() {
		exitScan = true;
		adapter.disable();
	}

	public class BleBinder extends Binder {
		public BLEService getService() {
			return BLEService.this;
		}
	}

	public class BleFilter {
		int rssiFilter;
		String nameFilter;

		public BleFilter(int rssi, String name) {
			this.nameFilter = name;
			this.rssiFilter = rssi;
		}

		public class Builder {
			int rssi = -70;
			String name = "";

			public Builder setRssi(int rssi) {
				this.rssi = rssi;
				return Builder.this;
			}

			public Builder setName(String excludeName) {
				this.name = excludeName;
				return Builder.this;
			}

			public BleFilter build() {
				return new BleFilter(rssi, name);
			}
		}
	}

	public interface OnProximityBleChangedListener {

		void onProximityBleChanged(BluetoothDevice ori, BluetoothDevice current);

		void onConditionTriggerSuccess(BluetoothDevice device);

		void onConditionTriggerFailed(BluetoothDevice device);
	}
}
