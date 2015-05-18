package com.xkx.yjxm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.RangingListener;
import com.brtbeacon.sdk.ServiceReadyCallback;
import com.brtbeacon.sdk.service.RangingResult;

public class BleScanService extends Service {

	private final int SCAN_WAIT_MILLIS = 0;
	private final int SCAN_PERIOD_MILLIS = 1000;

	private BRTRegion region;
	private BRTBeacon freshBeacon;
	private BRTBeaconManager brtBeaconMgr;
	private OnBleScanListener onBleScanListener;
	private BleBinder bleBinder = new BleBinder();
	private CopyOnWriteArrayList<BRTBeacon> rangedBeacon = new CopyOnWriteArrayList<BRTBeacon>();
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

	public BleScanService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void enableBlueToothIfClosed() {
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		enableBlueToothIfClosed();
		brtBeaconMgr = new BRTBeaconManager(this);
		brtBeaconMgr.setForegroundScanPeriod(SCAN_PERIOD_MILLIS,
				SCAN_WAIT_MILLIS);

		brtBeaconMgr.setRangingListener(new RangingListener() {

			@Override
			public void onBeaconsDiscovered(RangingResult result) {
				processScanResult(result);
			}

			private void processScanResult(RangingResult result) {
				rangedBeacon.clear();
				rangedBeacon.addAll(result.beacons);
				onBleScanListener.onPeriodScan(rangedBeacon);
				if (rangedBeacon.size() == 0) {
					return;
				}
				BRTBeacon nearBeacon = rangedBeacon.get(0);// 第0位是信号强度最高的
				onBleScanListener.onNearBle(nearBeacon);
				boolean isMacEqual = (freshBeacon == null ? true
						: freshBeacon.macAddress
								.equalsIgnoreCase(nearBeacon.macAddress));
				if (!isMacEqual && nearBeacon.rssi > -73) {
					onBleScanListener.onNearBleChanged(freshBeacon, nearBeacon);
				}
				freshBeacon = nearBeacon;
			}
		});
		brtBeaconMgr.connect(new ServiceReadyCallback() {

			@Override
			public void onServiceReady() {
				if (region == null) {
					region = new BRTRegion("xkx", null, null, null, null);
				}
				try {
					brtBeaconMgr.startRanging(region);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		return bleBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		try {
			brtBeaconMgr.stopRanging(region);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		brtBeaconMgr.disconnect();
		// 关闭蓝牙
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.disable();
		return super.onUnbind(intent);
	}

	/**
	 * 扫描监听器，需要扫描的页面，绑定此服务，并实现这个接口。
	 * 
	 * @author ztb
	 * 
	 */
	public interface OnBleScanListener {
		/**
		 * 扫描的最近一个ble基站改变了，回调的方法。
		 * 
		 * @param oriBeacon
		 *            原来的最近的ble
		 * @param desBeacon
		 *            现在的最近的ble
		 */
		public void onNearBleChanged(BRTBeacon oriBeacon, BRTBeacon desBeacon);

		/**
		 * 周期性扫描回调方法
		 * 
		 * @param scanResultList
		 *            扫描到的beacon列表
		 */
		public void onPeriodScan(List<BRTBeacon> scanResultList);

		/**
		 * 周期性扫描提取最近的一个ble，回调方法
		 * 
		 * @param brtBeacon
		 */
		public void onNearBle(BRTBeacon brtBeacon);
	}

	public class BleBinder extends Binder {

		/**
		 * 设置扫描的条件
		 * 
		 * @param uuid
		 */
		public void setRegion(String uuid) {
			region = new BRTRegion("xkx", uuid, null, null, null);
		}

		public BRTBeacon getProximityBeacon() {
			if (rangedBeacon != null && rangedBeacon.size() > 0) {
				return rangedBeacon.get(0);
			} else {
				return null;
			}
		}

		public void setOnBleScanListener(OnBleScanListener listener) {
			onBleScanListener = listener;
		}

		/**
		 * 获取附近指定个数的beacons
		 * 
		 * @param num
		 *            要获取的个数
		 * @return
		 */
		public List<BRTBeacon> getBRTBeacons(Integer num) {
			if (rangedBeacon.size() == 0) {
				return null;
			}
			if (num == null || num < 0) {
				return rangedBeacon;
			} else {
				List<BRTBeacon> temp = new ArrayList<BRTBeacon>();
				int size = rangedBeacon.size();
				num = num > size ? size : num;
				for (int i = 0; i < num; i++) {
					temp.add(rangedBeacon.get(i));
				}
				return temp;
			}
		}
	}
}
