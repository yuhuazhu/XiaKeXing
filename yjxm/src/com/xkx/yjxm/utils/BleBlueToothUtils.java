package com.xkx.yjxm.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
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
	static long startScan;

	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	public static BluetoothDevice scan(int intervalMillis, int count) {
		enableIfClosed();
		if (System.currentTimeMillis() - startScan < 1000) {
			return null;
		}
		return null;
	}

	private void maxLeScan() {
	}

	private static void enableIfClosed() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}
}
