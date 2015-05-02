package com.xkx.yjxm.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

/**
 * ble 蓝牙封装工具类
 * 
 * @author zhengtianbao
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleBlueToothUtils {
	public static BluetoothDevice scan(int interval, int count) {
		final HashMap<BluetoothDevice, Integer> scannedCountMap = new HashMap<BluetoothDevice, Integer>();
		final HashMap<BluetoothDevice, Integer> sumRssiMap = new HashMap<BluetoothDevice, Integer>();
		LeScanCallback callback = new LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				if (scannedCountMap.get(device) == null) {
					scannedCountMap.put(device, 1);
					sumRssiMap.put(device, rssi);
				} else {
					int count = scannedCountMap.get(device);
					int sum = sumRssiMap.get(device);
					scannedCountMap.put(device, ++count);
					sumRssiMap.put(device, sum + rssi);
				}
			}
		};
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		interval = (interval < 100 || interval > 2000) ? 1000 : interval;
		for (int i = 0; i < count; i++) {
			adapter.startLeScan(callback);
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			adapter.stopLeScan(callback);
		}
		// 处理扫描结果
		Set<BluetoothDevice> keySet = scannedCountMap.keySet();
		Iterator<BluetoothDevice> iterator = keySet.iterator();
		float max = -1000;
		BluetoothDevice result = null;
		while (iterator.hasNext()) {
			BluetoothDevice device = iterator.next();
			int cnt = scannedCountMap.get(device);
			int sum = sumRssiMap.get(device);
			float temp = sum / cnt;
			max = (max > temp) ? max : temp;
			if (max < temp) {
				max = temp;
				result = device;
			}
		}
		return result;
	}
}
