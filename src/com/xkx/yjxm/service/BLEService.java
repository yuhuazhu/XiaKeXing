package com.xkx.yjxm.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	private static final int COUNT_SCANNED_TO_RSP = 5;

	private static final long PERIOD_TO_SCAN = 400;

	private static final int COUNT_NOT_SCANNED = 10;

	private static final float RSSI_TO_TRIGGER = -70f;

	private BleBinder bleBinder = new BleBinder();

	private OnProximityBleChangedListener listener;

	private BleFilter bleFilter;

	private boolean exitScan = false;

	private BluetoothDevice proximityBleDevice = null;

	private final HashMap<BluetoothDevice, Integer> scannedCountMap = new HashMap<BluetoothDevice, Integer>();
	private final HashMap<BluetoothDevice, Long> sumRssiMap = new HashMap<BluetoothDevice, Long>();
	private final HashMap<BluetoothDevice, Float> averRssiMap = new HashMap<BluetoothDevice, Float>();
	private final HashMap<BluetoothDevice, Integer> recentScannedMap = new HashMap<BluetoothDevice, Integer>();

	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	private int scannedCount;

	public BLEService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("scan", "onCreate()");
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
		enableBlueToothIfClosed();
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
			singleLeScan();
		}
	};

	private void singleLeScan() {
		adapter.startLeScan(callback);
		scannedCount++;
		try {
			Thread.sleep(PERIOD_TO_SCAN);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		adapter.stopLeScan(callback);
		processScannedResult();
	}

	/**
	 * 处理扫描结果
	 */
	private void processScannedResult() {
		try {
			Set<BluetoothDevice> keySet = recentScannedMap.keySet();
			Iterator<BluetoothDevice> iterator = keySet.iterator();
			BluetoothDevice device;
			// 超过10次没有被扫描到的设备则移除之
			while (iterator.hasNext()) {
				device = iterator.next();
				int recentScanned = recentScannedMap.get(device);
				if (scannedCount - recentScanned >= COUNT_NOT_SCANNED) {
					Log.e("process", device.getAddress() + "is removed");
					iterator.remove();
					scannedCountMap.remove(device);
					sumRssiMap.remove(device);
					averRssiMap.remove(device);
				}
			}
			averRssi();
			List<Map.Entry<BluetoothDevice, Float>> sortedDeviceRssiList = sortAverRssiMapByRssi();
			Entry<BluetoothDevice, Float> entry = sortedDeviceRssiList.get(0);
			if (scannedCount >= COUNT_SCANNED_TO_RSP) {
				if (proximityBleDevice != entry.getKey()) {
					proximityBleDevice = entry.getKey();
					Float averRssi = averRssiMap.get(proximityBleDevice);
					if (bleFilter != null) {
						boolean nameIncluded = proximityBleDevice.getName()
								.contains(bleFilter.nameFilter);
						boolean rssiStronger = averRssi >= bleFilter.rssiFilter;
						if (!nameIncluded && rssiStronger) {
							listener.onProximityBleChanged(proximityBleDevice);
						}
					} else {
						listener.onProximityBleChanged(proximityBleDevice);
					}
				}
			}
		} catch (Exception e) {
			Log.e("process", e.toString());
		}
	}

	/**
	 * 各ble设备信号强度求平均值
	 */
	private void averRssi() {
		Set<BluetoothDevice> keySet;
		Iterator<BluetoothDevice> iterator;
		BluetoothDevice device;
		keySet = scannedCountMap.keySet();
		iterator = keySet.iterator();
		while (iterator.hasNext()) {
			device = iterator.next();
			Long sum = sumRssiMap.get(device);
			int count = scannedCountMap.get(device);
			averRssiMap.put(device, (float) ((float) sum / count));
		}
	}

	private List<Map.Entry<BluetoothDevice, Float>> sortAverRssiMapByRssi() {
		List<Map.Entry<BluetoothDevice, Float>> list = new LinkedList<Map.Entry<BluetoothDevice, Float>>();
		list.addAll(averRssiMap.entrySet());
		Collections.sort(list,
				new Comparator<Map.Entry<BluetoothDevice, Float>>() {
					public int compare(Map.Entry<BluetoothDevice, Float> obj1,
							Map.Entry<BluetoothDevice, Float> obj2) {
						// 从高往低排序
						float f1 = (Float) obj1.getValue();
						float f2 = (Float) obj2.getValue();
						if (f1 < f2) {
							return 1;
						} else if (f1 == f2) {
							return 0;
						} else {
							return -1;
						}
					}
				});
		return list;
	}

	public BluetoothDevice getProximityBleDevice(int rssiFilter) {
		// TODO 过滤待实现
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
	}

	public void setOnProximityBleChangedListener(
			OnProximityBleChangedListener listener) {
		this.listener = listener;
	}

	private LeScanCallback callback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.e("scan", device.getAddress() + "rssi:" + rssi);
			if (scannedCountMap.get(device) == null) {
				scannedCountMap.put(device, 1);
				sumRssiMap.put(device, (long) rssi);
			} else {
				int count = scannedCountMap.get(device);
				long sum = sumRssiMap.get(device);
				scannedCountMap.put(device, ++count);
				sumRssiMap.put(device, sum + rssi);
			}
			recentScannedMap.put(device, scannedCount);
		}
	};
	
	public void onDestroy() {
		exitScan = true;
	};

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

		void onProximityBleChanged(BluetoothDevice device);
	}
}
