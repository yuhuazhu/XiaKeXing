package com.xkx.yjxm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.RangingListener;
import com.brtbeacon.sdk.ServiceReadyCallback;
import com.brtbeacon.sdk.service.RangingResult;
import com.xkx.yjxm.bean.MacInfo;
import com.xkx.yjxm.utils.CrashHandler;

public class BleScanService extends Service {

	/**
	 * 这个值指示:蓝牙连续这个值没有被扫描到,将被从list中移除.
	 */
	private static final int TIMES_NO_SCANNED = 10;
	private static final int timesForLoop = 3;
	private final int SCAN_WAIT_MILLIS = 0;
	private final int SCAN_PERIOD_MILLIS = 1000;

	private int scanCount;

	private BRTRegion region;
	private BRTBeacon freshBeacon;
	private BRTBeaconManager brtBeaconMgr;
	private OnBleScanListener onBleScanListener;
	private BleBinder bleBinder = new BleBinder();
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private HashMap<String, MacInfo> macMap = new HashMap<String, MacInfo>();
	private ArrayList<BRTBeacon> rangedList = new ArrayList<BRTBeacon>();
	private ArrayList<BRTBeacon> optimizedList = new ArrayList<BRTBeacon>();
	private ArrayList<BeaconData> allScannedList = new ArrayList<BeaconData>();
	private ArrayList<BRTBeacon> saveList = new ArrayList<BRTBeacon>();
	// TODO
	private Handler handler = new Handler();

	private Toast t;

	/**
	 * 提取蓝牙是否使用优化过的算法
	 */
	private boolean fetchOptimized = true;

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

	private int singleLoopCount = 0;

	private Comparator<? super BRTBeacon> rssiComparator = new Comparator<BRTBeacon>() {

		@Override
		public int compare(BRTBeacon lhs, BRTBeacon rhs) {
			return rhs.rssi - lhs.rssi;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		enableBlueToothIfClosed();
		setBeaconManager();
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

	private long startTime = 0;

	private void setBeaconManager() {
		brtBeaconMgr = new BRTBeaconManager(this);
		// brtBeaconMgr.setBackgroundScanPeriod(SCAN_PERIOD_MILLIS,
		// SCAN_WAIT_MILLIS);
		brtBeaconMgr.setForegroundScanPeriod(SCAN_PERIOD_MILLIS,
				SCAN_WAIT_MILLIS);

		brtBeaconMgr.setRangingListener(new RangingListener() {

			@Override
			public void onBeaconsDiscovered(RangingResult result) {
				Log.e("time", "" + (System.currentTimeMillis() - startTime));
				singleLoopCount++;
				List<BRTBeacon> beacons = result.beacons;
				for (int i = 0; i < beacons.size(); i++) {
					BRTBeacon beacon = beacons.get(i);
					int index = saveList.indexOf(beacon);
					if (index == -1) {
						if (beacon.name != null
								&& !beacon.name.equalsIgnoreCase("estimote")
								&& !beacon.macAddress.equalsIgnoreCase("AB:9A")
								&& !beacon.macAddress.endsWith("29:A5")
								&& !beacon.macAddress.endsWith("02:EF")
								&& !beacon.macAddress.endsWith("9B:21")
								&& !beacon.macAddress.endsWith("8F:A8")) {
							saveList.add(beacon);
						}
					} else {
						BRTBeacon savedBeacon = saveList.get(index);
						boolean rssiLower = savedBeacon.rssi < beacon.rssi;
						savedBeacon.rssi = rssiLower ? beacon.rssi
								: savedBeacon.rssi;
					}
				}
				if (singleLoopCount % timesForLoop == 0) {
					scanCount++;
					Collections.sort(saveList, rssiComparator);
					rangedList.clear();
					rangedList.addAll(saveList);
					if (fetchOptimized) {
						processOptimized();
					} else {
						processRaw();
					}
					saveList.clear();
				}
				startTime = System.currentTimeMillis();
			}

			private void processRaw() {
				onBleScanListener.onPeriodScan(rangedList);
				if (rangedList.size() >= 1) {
					BRTBeacon currNearBeacon = rangedList.get(0);
					onBleScanListener.onNearBeacon(currNearBeacon);
					if (currNearBeacon != freshBeacon) {
						onBleScanListener.onNearBleChanged(freshBeacon,
								currNearBeacon);
						freshBeacon = currNearBeacon;
					}
				}
			}

			private void processOptimized() {
				optimizedList.clear();

				for (int i = 0; i < rangedList.size(); i++) {
					BRTBeacon rangedBeacon = rangedList.get(i);
					BeaconData data = new BeaconData(rangedBeacon);
					int index = allScannedList.indexOf(data);
					if (index == -1) {
						data.lastScanNum = scanCount;
						data.selfScanCount = data.selfScanCount + 1;
						data.sumRssi += rangedBeacon.rssi;
						allScannedList.add(data);
						Log.e("remove", "添加：" + rangedBeacon.macAddress);
					} else {
						BeaconData temp = allScannedList.get(index);
						temp.lastScanNum = scanCount;
						temp.selfScanCount = temp.selfScanCount + 1;
						temp.sumRssi += rangedBeacon.rssi;
					}
				}

				Iterator<BeaconData> iterator = allScannedList.iterator();
				while (iterator.hasNext()) {
					BeaconData next = iterator.next();
					if (scanCount - next.lastScanNum > TIMES_NO_SCANNED) {
						iterator.remove();
					}
				}

				Collections.sort(allScannedList);
				if (allScannedList.size() == 0 || rangedList.size() == 0) {
					return;
				}

				BeaconData allFirst = allScannedList.get(0);
				BRTBeacon rangedFirst = rangedList.get(0);
				// 是否一样
				boolean isEqual = allFirst.beacon.equals(rangedFirst);
				// 信号强度均值比较第一个高
				boolean isRssiHigerThanRanged = allFirst.beacon.rssi > rangedFirst.rssi;
				// 最近一次是否被扫描到
				boolean isLastScanned = scanCount == allFirst.lastScanNum;

				final StringBuffer sb = new StringBuffer();

				if (!isEqual && isRssiHigerThanRanged && isLastScanned) {
					CrashHandler.getInstance().logStringToFile(
							allFirst.beacon.macAddress + " enter\n");
					int index = rangedList.indexOf(allFirst.beacon);
					BRTBeacon beacon = rangedList.get(index);
					if (Math.abs(beacon.rssi - rangedFirst.rssi) <= 5) {
						Log.e("remove", "" + beacon.macAddress + "挤进前列");
						optimizedList.add(beacon);
						sb.append(getTitle(beacon.macAddress) + "挤进前列\n");
					}
				}

				for (int i = 0; i < rangedList.size(); i++) {
					BRTBeacon rangedBeacon = rangedList.get(i);
					BeaconData data = new BeaconData(rangedBeacon);
					int index = allScannedList.indexOf(data);
					data = allScannedList.get(index);
					int averRssi = data.sumRssi / data.selfScanCount;
					boolean rssiLower = rangedBeacon.rssi < averRssi;
					rangedBeacon.rssi = rssiLower ? averRssi
							: rangedBeacon.rssi;
					if (!optimizedList.contains(rangedBeacon)) {
						optimizedList.add(rangedBeacon);
					}
				}

				if (optimizedList.size() >= 2) {
					BRTBeacon brtBeacon = optimizedList.get(0);
					int index = allScannedList
							.indexOf(new BeaconData(brtBeacon));
					BeaconData data = allScannedList.get(index);
					if (!brtBeacon.equals(freshBeacon)) {
						// 不同,判断该值上次是否超过2,超过则移到第一位,上次归0.
						if (scanCount - data.previousFirstCount > 1) {
							optimizedList.remove(0);
							optimizedList.add(1, brtBeacon);
						}
						data.previousFirstCount = scanCount;
					} else {
						data.previousFirstCount = scanCount;
					}
				}

				// Log.e("count", "optimized:" + optimizedList.size());
				Collections.sort(optimizedList, rssiComparator);
				if (optimizedList.size() >= 0) {
					// sb.append("个数:" + optimizedList.size() + "\n");
					// for (int i = 0; i < optimizedList.size() && i <= 2; i++)
					// {
					// BRTBeacon beacon = optimizedList.get(i);
					// sb.append(getTitle(beacon.macAddress) + ","
					// + beacon.rssi + "\n");
					// }
					// sb.append("----------------\n");
					// for (int i = 0; i < allScannedList.size() && i <= 2; i++)
					// {
					// BeaconData data = allScannedList.get(i);
					// sb.append(getTitle(data.beacon.macAddress) + ","
					// + data.sumRssi / data.selfScanCount + "\n");
					// }
					// if (t == null) {
					// t = Toast.makeText(BleScanService.this, "",
					// Toast.LENGTH_LONG);
					// }
					// t.setText(sb.toString());
					// t.show();
					if (optimizedList.size() >= 1) {
						// TODO
						// onBleScanListener.onPeriodScan(optimizedList);
						BRTBeacon brtBeacon = optimizedList.get(0);
						// onBleScanListener.onNearBeacon(brtBeacon);
						if (freshBeacon == null
								|| !brtBeacon.equals(freshBeacon)) {
							// onBleScanListener.onNearBleChanged(freshBeacon,
							// brtBeacon);
							// 蓝牙易主(信号强度最高的),清空掉被易的蓝牙信号总和,扫描次数.
							if (!allFirst.beacon.equals(brtBeacon)) {
								BeaconData data = new BeaconData(brtBeacon);
								allScannedList.remove(data);
							}
							freshBeacon = brtBeacon;
						}
					}
				}
			}
		});
	}

	class BeaconData implements Comparable<BeaconData> {
		BRTBeacon beacon;
		int selfScanCount = 0;
		int sumRssi = 0;
		int lastScanNum = 0;
		int previousFirstCount = 0;

		public BeaconData(BRTBeacon beacon) {
			this.beacon = beacon;
		}

		@Override
		public int hashCode() {
			return beacon.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BeaconData other = (BeaconData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (beacon == null) {
				if (other.beacon != null)
					return false;
			} else if (!beacon.equals(other.beacon))
				return false;
			return true;
		}

		private BleScanService getOuterType() {
			return BleScanService.this;
		}

		@Override
		public int compareTo(BeaconData another) {
			return (another.sumRssi / another.selfScanCount)
					- (this.sumRssi / this.selfScanCount);
		}
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
		 * 扫描的最近一个beacon基站改变了，回调的方法。
		 * 
		 * @param oriBeacon
		 *            原来的最近的beacon
		 * @param desBeacon
		 *            现在的最近的beacon
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
		 * 周期性扫描提取最近的一个beacon，回调方法
		 * 
		 * @param brtBeacon
		 */
		public void onNearBeacon(BRTBeacon brtBeacon);
	}

	public class BleBinder extends Binder {

		/**
		 * 设置每个蓝牙触发的条件信息
		 * 
		 * @param mac
		 */
		public void setMacMap(HashMap<String, MacInfo> mac) {
			macMap = mac;
		}

		/**
		 * 设置扫描的条件
		 * 
		 * @param uuid
		 */
		public void setRegion(String uuid) {
			region = new BRTRegion("xkx", uuid, null, null, null);
		}

		public BRTBeacon getProximityBeacon() {
			if (fetchOptimized) {
				if (optimizedList.size() >= 1) {
					return optimizedList.get(0);
				}
			} else {
				if (rangedList.size() >= 1) {
					return rangedList.get(0);
				}
			}
			return null;
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
			if (rangedList.size() == 0) {
				return null;
			}

			if (fetchOptimized) {
				if (num == null || num < 0) {
					return optimizedList;
				} else {
					List<BRTBeacon> temp = new ArrayList<BRTBeacon>();
					int size = optimizedList.size();
					num = num > size ? size : num;
					for (int i = 0; i < num; i++) {
						temp.add(optimizedList.get(i));
					}
					return temp;
				}
			} else {
				if (num == null || num < 0) {
					return rangedList;
				} else {
					List<BRTBeacon> temp = new ArrayList<BRTBeacon>();
					int size = rangedList.size();
					num = num > size ? size : num;
					for (int i = 0; i < num; i++) {
						temp.add(rangedList.get(i));
					}
					return temp;
				}
			}
		}
	}

	private String getTitle(String address) {
		address = address.trim();
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			return "智慧导览";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			return "行李寄存";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			return "3D互动";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			return "应用展示";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			return "引导台";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FB")) {
			return "旅客上车处";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			return "智慧旅游视屏";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			return "自行车租赁";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			return "休闲自助";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			return "伴手礼超市";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			return "多功能厅";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			return "综合服务区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			return "呼叫中心";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			return "预警指挥中心";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			return "办公区1";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			return "医务室";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			return "信息视屏";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			return "机房";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FD")) {
			return "婚纱摄影";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			return "办公区2";
		}
		return address;
	}
}
