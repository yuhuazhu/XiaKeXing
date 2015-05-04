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

	private static final int PERIOD_TO_SCAN = 400;

	private static final float RSSI_TO_TRIGGER = -70f;

	private final int NUM_SCAN = 5;

	private int maxRssi = -1000;

	private boolean exitScan = false;

	private boolean shake = false;

	private BleBinder bleBinder = new BleBinder();

	private OnProximityBleChangedListener listener;

	private BluetoothDevice proximityBleDevice = null;

	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

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
			maxAutoLeScan(NUM_SCAN);
		}
	}

	// public BluetoothDevice maxShakeLeScan() {
	// shake = true;
	// maxRssi = -1000;
	// proximityBleDevice = null;
	// for (int i = 0; i < NUM_SCAN; i++) {
	// adapter.startLeScan(callback);
	// try {
	// Thread.sleep(PERIOD_TO_SCAN);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// adapter.stopLeScan(callback);
	// }
	// BluetoothDevice rs = proximityBleDevice;
	// maxRssi = -1000;
	// proximityBleDevice = null;
	// shake = false;
	// return rs;
	// }

	public void setShakeScan(boolean shake) {
		this.shake = shake;
	}

	private void maxAutoLeScan(int scanNumber, float rssiFilter) {
		maxRssi = -1000;
		proximityBleDevice = null;
		for (int i = 0; i < scanNumber; i++) {
			adapter.startLeScan(callback);
			try {
				Thread.sleep(PERIOD_TO_SCAN);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			adapter.stopLeScan(callback);
		}
		if (!shake) {
			if (maxRssi >= rssiFilter) {
				listener.onConditionTriggerSuccess(proximityBleDevice);
			} else {
				listener.onConditionTriggerFailed(proximityBleDevice);
			}
		}
	}

	private void maxAutoLeScan(int scanNumber) {
		maxAutoLeScan(scanNumber, RSSI_TO_TRIGGER);
	}

	public BluetoothDevice getProximityBleDevice(int rssiFilter) {
		return proximityBleDevice;
	}

	public BluetoothDevice getProximityBleDevice() {
		return proximityBleDevice;
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
