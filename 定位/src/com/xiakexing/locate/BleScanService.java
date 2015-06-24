package com.xiakexing.locate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Fragment;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.RangingListener;
import com.brtbeacon.sdk.ServiceReadyCallback;
import com.brtbeacon.sdk.service.RangingResult;

public class BleScanService extends Service {

	/** the number indicates the max count of rssi value in the stored list */
	private final int SAMPLE_SIZE = 8;
	private final int SCAN_WAIT_MILLIS = 0;
	private final int SCAN_PERIOD_MILLIS = 250;
	/** the number indicates how often to do actions for UI logical. */
	private static final int TIMES_FOR_LOOP = 4;
	/**
	 * the number indicates over which times the iBeacon will be removed from
	 * scanned list.
	 */
	private static final int TIMES_NO_SCANNED = 10;

	/** 提取蓝牙是否使用优化过的算法 */
	private boolean fetchComplex = false;

	/**
	 * fetchMultiBeacon is to use multi-iBeacon at one loop scan,true for
	 * location etc,otherwise for fetching the nearest ibeacon.such as to find
	 * the nearest interest thing.
	 */
	private Boolean fetchMultiBeacon;

	/** 处理轮次,外层循环 */
	private int processCount;
	/** 扫描轮次,内层循环 */
	private int loopCount = 0;

	private BRTRegion region;
	/** 当前信号最大的iBeacon */
	private BRTBeacon freshBeacon;
	private BRTBeaconManager brtBeaconMgr;
	private OnBleScanListener onBleScanListener;
	private BleBinder bleBinder = new BleBinder();
	/**
	 * to stored temporary scanned data at frequency of {@link TIMES_FOR_LOOP}
	 * times
	 */
	private ArrayList<BRTBeacon> tempList;
	private ArrayList<BRTBeacon> rangedList;
	private ArrayList<BRTBeacon> optimizedList;
	private ArrayList<BeaconData> allScannedList;

	/** 保存卡尔曼数据量,key:mac地址,value:卡尔曼滤波过程数据 */
	private HashMap<String, Kalman> kalmanMap;
	/** 保存上一次扫描到的信号强度 ,key:mac地址,value:上一次的信号强度 */
	private HashMap<String, Integer> lastRssiMap;
	/** 保存最近几次的信号数据 ,key:mac地址,value:此iBeacon最近几次的扫描信号 */
	private HashMap<String, ArrayList<Integer>> everyRssimap;

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
					// noop.
				}
			}
		});
		return bleBinder;
	}

	private void enableBlueToothIfClosed() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	private int getRssiWithFilter(ArrayList<Integer> list, FilterMode mode) {
		int rssi = 0;
		if (mode == FilterMode.MODE_AVER) {// 平均值
			int sum = 0;
			for (int i = 0; i < list.size(); i++) {
				sum += list.get(i);
			}
			rssi = sum / list.size();
		} else if (mode == FilterMode.MODE_MEDIUM) {// 中位数
			Object[] arr = (Object[]) list.toArray();
			Collections.sort(list);// 从小到大排列
			int size = list.size();
			if (size % 2 == 0) {
				rssi = Math
						.round((list.get(size / 2 - 1) + list.get(size / 2)) / 2);
			} else {
				rssi = list.get(size / 2);
			}
			list.clear();
			for (int i = 0; i < arr.length; i++) {
				list.add((Integer) arr[i]);
			}
		} else if (mode == FilterMode.MODE_MAX) {
			rssi = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				rssi = list.get(i) > rssi ? list.get(i) : rssi;
			}
		}
		return rssi;
	}

	private void setBeaconManager() {
		brtBeaconMgr = new BRTBeaconManager(this);
		// brtBeaconMgr.setBackgroundScanPeriod(SCAN_PERIOD_MILLIS,
		// SCAN_WAIT_MILLIS);
		brtBeaconMgr.setForegroundScanPeriod(SCAN_PERIOD_MILLIS,
				SCAN_WAIT_MILLIS);

		brtBeaconMgr.setRangingListener(new RangingListener() {
			@Override
			public void onBeaconsDiscovered(RangingResult result) {
				if (fetchMultiBeacon == null) {
					throw new IllegalStateException(
							"setTriggerMode() must be called after service connection established.");
				}
				if (onBleScanListener == null) {
					throw new IllegalStateException(
							"BleScanListener must be set after service connection established.");
				}
				if (fetchMultiBeacon) {
					// filterProcess(result);
					kalmanFilter(result.beacons);
				} else {
					fetchNearOneiBeacon(result);
				}
			}
		});
	}

	/**
	 * 使用滤波处理扫描数据
	 * 
	 * @param result
	 */
	private void filterProcess(RangingResult result) {
		loopCount++;
		if (everyRssimap == null) {
			everyRssimap = new HashMap<String, ArrayList<Integer>>();
		}
		if (lastRssiMap == null) {
			lastRssiMap = new HashMap<String, Integer>();
		}
		// 信号数据队列递推
		for (int i = 0; i < result.beacons.size(); i++) {
			BRTBeacon beacon = result.beacons.get(i);
			if (everyRssimap.containsKey(beacon.macAddress)) {
				// 限幅
				if (Math.abs(lastRssiMap.get(beacon.macAddress) - beacon.rssi) <= 5) {
					ArrayList<Integer> list = everyRssimap
							.get(beacon.macAddress);
					if (list.size() >= SAMPLE_SIZE) {
						list.remove(0);
					}
					list.add(beacon.rssi);
					lastRssiMap.put(beacon.macAddress, beacon.rssi);
				}
			} else {
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(beacon.rssi);
				everyRssimap.put(beacon.macAddress, list);
				lastRssiMap.put(beacon.macAddress, beacon.rssi);
			}
		}
		if (loopCount % TIMES_FOR_LOOP == 0) {
			// 滤波
			List<BRTBeacon> beacons = result.beacons;
			for (int i = 0; i < beacons.size(); i++) {
				BRTBeacon brtBeacon = beacons.get(i);
				brtBeacon.rssi = getRssiWithFilter(
						everyRssimap.get(brtBeacon.macAddress),
						FilterMode.MODE_AVER);
			}
			// 响应事件
			if (beacons.size() >= 1) {
				onBleScanListener.onPeriodScan(beacons);
				BRTBeacon brtBeacon = beacons.get(0);
				onBleScanListener.onNearBeacon(brtBeacon);
				if (freshBeacon == null || !brtBeacon.equals(freshBeacon)) {
					onBleScanListener.onNearBleChanged(freshBeacon, brtBeacon);
				}
				freshBeacon = brtBeacon;
			}
		}
	}

	private void kalmanFilter(List<BRTBeacon> beacons) {
		loopCount++;
		if (everyRssimap == null) {
			everyRssimap = new HashMap<String, ArrayList<Integer>>();
		}
		if (kalmanMap == null) {
			kalmanMap = new HashMap<String, Kalman>();
		}
		for (int i = 0; i < beacons.size(); i++) {
			BRTBeacon brtBeacon = beacons.get(i);
			if (kalmanMap.containsKey(brtBeacon.macAddress)) {
				ArrayList<Integer> list = everyRssimap
						.get(brtBeacon.macAddress);
				if (list.size() >= SAMPLE_SIZE) {
					list.remove(0);
				}
				list.add(brtBeacon.rssi);
				Kalman kalman = kalmanMap.get(brtBeacon.macAddress);
				kalman.valMeas = brtBeacon.rssi;// 测量值
				kalman.valEsti = getRssiWithFilter(list, FilterMode.MODE_MEDIUM);// 估计值,取中位值
				kalman.uncerMeas = Math.abs(kalman.valOpti - brtBeacon.rssi);// 测量值不确定度,最优值和测量值的绝对值
				kalman.uncerEsti = Math.abs(kalman.valOpti - kalman.valEsti);// 估计值不确定度,最优值和估计值的绝对值
				double uncerEsti = kalman.uncerEsti;
				double uncerMeas = kalman.uncerMeas;
				double devOpti = kalman.devOpti;
				// 估计值偏差
				kalman.devEsti = Math.pow(uncerEsti, 2) + Math.pow(devOpti, 2);
				// 测量值偏差
				kalman.devMeas = Math.pow(uncerMeas, 2) + Math.pow(devOpti, 2);
				// 卡尔曼增益
				double kalmanGain = Math.sqrt(kalman.devEsti
						/ (kalman.devEsti + kalman.devMeas));
				// 最优值
				kalman.valOpti = kalman.valEsti + kalmanGain
						* (kalman.valMeas - kalman.valEsti);
				brtBeacon.rssi = Math.round((float) kalman.valOpti);
				if (brtBeacon.macAddress.equals("EC:98:14:03:87:52")) {
					Log.e("kalman", "卡尔曼增益:" + kalmanGain);
				}
				// 最优值偏差
				kalman.devOpti = Math.sqrt((1 - kalmanGain) * kalman.devEsti);
			} else {
				Kalman kalman = new Kalman();
				// 放入最优偏差
				kalman.devOpti = 0;
				kalmanMap.put(brtBeacon.macAddress, kalman);
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(brtBeacon.rssi);
				everyRssimap.put(brtBeacon.macAddress, list);
			}
			if (brtBeacon.macAddress.equals("EC:98:14:03:87:52")) {
				Kalman kalman = kalmanMap.get(brtBeacon.macAddress);
				Log.e("kalman", "测量:" + kalman.valMeas + ",估计:"
						+ kalman.valEsti + ",最优:" + kalman.valOpti
						// + "估值偏差:" + kalman.devEsti
						+ ",最优偏差:" + kalman.devOpti);
			}
		}
		if (loopCount % TIMES_FOR_LOOP == 0) {
			if (beacons.size() >= 1) {
				BRTBeacon firstBeacon = beacons.get(0);
				onBleScanListener.onPeriodScan(beacons);
				onBleScanListener.onNearBeacon(beacons.get(0));
				if (freshBeacon == null || !firstBeacon.equals(freshBeacon)) {
					onBleScanListener
							.onNearBleChanged(freshBeacon, firstBeacon);
				}
				freshBeacon = firstBeacon;
			}
		}
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
			// noop
		}
		brtBeaconMgr.disconnect();
		return super.onUnbind(intent);
	}

	/**
	 * 用于精确提取最近一个蓝牙
	 * 
	 * @param result
	 */
	private void fetchNearOneiBeacon(RangingResult result) {
		loopCount++;
		if (tempList == null) {
			tempList = new ArrayList<BRTBeacon>();
		}
		if (rangedList == null) {
			rangedList = new ArrayList<BRTBeacon>();
		}
		List<BRTBeacon> beacons = result.beacons;
		for (int i = 0; i < beacons.size(); i++) {
			BRTBeacon beacon = beacons.get(i);
			int index = tempList.indexOf(beacon);
			if (index == -1) {
				tempList.add(beacon);
			} else {
				BRTBeacon savedBeacon = tempList.get(index);
				boolean rssiLower = savedBeacon.rssi < beacon.rssi;
				savedBeacon.rssi = rssiLower ? beacon.rssi : savedBeacon.rssi;
			}
		}
		if (loopCount % TIMES_FOR_LOOP == 0) {
			processCount++;
			Collections.sort(tempList, rssiComparator);
			rangedList.clear();
			rangedList.addAll(tempList);
			if (fetchComplex) {
				processOptimized();
			} else {
				processRaw();
			}
			tempList.clear();
		}
	}

	/** 对这一轮扫描到的iBeacon直接触发,不做数据处理 */
	private void processRaw() {
		if (rangedList.size() >= 1) {
			onBleScanListener.onPeriodScan(rangedList);
			BRTBeacon currNearBeacon = rangedList.get(0);
			onBleScanListener.onNearBeacon(currNearBeacon);
			if (currNearBeacon != freshBeacon) {
				onBleScanListener.onNearBleChanged(freshBeacon, currNearBeacon);
			}
			freshBeacon = currNearBeacon;
		}
	}

	/** 调整修改扫描到的信号数据,弃用 */
	@Deprecated
	private void processOptimized() {
		optimizedList.clear();
		if (optimizedList == null) {
			optimizedList = new ArrayList<BRTBeacon>();
		}
		if (allScannedList == null) {
			allScannedList = new ArrayList<BeaconData>();
		}
		for (int i = 0; i < rangedList.size(); i++) {
			BRTBeacon rangedBeacon = rangedList.get(i);
			BeaconData data = new BeaconData(rangedBeacon);
			int index = allScannedList.indexOf(data);
			if (index == -1) {
				data.lastScanNum = processCount;
				data.selfScanCount = data.selfScanCount + 1;
				data.sumRssi += rangedBeacon.rssi;
				allScannedList.add(data);
			} else {
				BeaconData temp = allScannedList.get(index);
				temp.lastScanNum = processCount;
				temp.selfScanCount = temp.selfScanCount + 1;
				temp.sumRssi += rangedBeacon.rssi;
			}
		}

		Iterator<BeaconData> iterator = allScannedList.iterator();
		while (iterator.hasNext()) {
			BeaconData next = iterator.next();
			if (processCount - next.lastScanNum > TIMES_NO_SCANNED) {
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
		boolean isLastScanned = processCount == allFirst.lastScanNum;

		final StringBuffer sb = new StringBuffer();

		if (!isEqual && isRssiHigerThanRanged && isLastScanned) {
			int index = rangedList.indexOf(allFirst.beacon);
			BRTBeacon beacon = rangedList.get(index);
			if (Math.abs(beacon.rssi - rangedFirst.rssi) <= 5) {
				optimizedList.add(beacon);
			}
		}

		for (int i = 0; i < rangedList.size(); i++) {
			BRTBeacon rangedBeacon = rangedList.get(i);
			BeaconData data = new BeaconData(rangedBeacon);
			int index = allScannedList.indexOf(data);
			data = allScannedList.get(index);
			int averRssi = data.sumRssi / data.selfScanCount;
			boolean rssiLower = rangedBeacon.rssi < averRssi;
			rangedBeacon.rssi = rssiLower ? averRssi : rangedBeacon.rssi;
			if (!optimizedList.contains(rangedBeacon)) {
				optimizedList.add(rangedBeacon);
			}
		}

		if (optimizedList.size() >= 2) {
			BRTBeacon brtBeacon = optimizedList.get(0);
			int index = allScannedList.indexOf(new BeaconData(brtBeacon));
			BeaconData data = allScannedList.get(index);
			if (!brtBeacon.equals(freshBeacon)) {
				if (processCount - data.previousFirstCount > 1) {
					optimizedList.remove(0);
					optimizedList.add(1, brtBeacon);
				}
				data.previousFirstCount = processCount;
			} else {
				data.previousFirstCount = processCount;
			}
		}

		Collections.sort(optimizedList, rssiComparator);
		if (optimizedList.size() >= 0) {
			if (optimizedList.size() >= 1) {
				onBleScanListener.onPeriodScan(optimizedList);
				BRTBeacon brtBeacon = optimizedList.get(0);
				// onBleScanListener.onNearBeacon(brtBeacon);
				if (freshBeacon == null || !brtBeacon.equals(freshBeacon)) {
					onBleScanListener.onNearBleChanged(freshBeacon, brtBeacon);
					// 蓝牙易主(信号强度最高的),清空掉被易的蓝牙信号总和,扫描次数.
					if (!allFirst.beacon.equals(brtBeacon)) {
						BeaconData data = new BeaconData(brtBeacon);
						allScannedList.remove(data);
					}
				}
				freshBeacon = brtBeacon;
			}
		}
	}

	/**
	 * 扫描监听器，需要扫描的页面，绑定此服务，并实现这个接口。
	 * 
	 * @author ztb
	 * 
	 */
	public interface OnBleScanListener {
		/**
		 * 扫描的最近一个ibeacon基站改变了，回调的方法。
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
		 * 周期性扫描提取最近的一个iBeacon,回调方法
		 * 
		 * @param brtBeacon
		 */
		public void onNearBeacon(BRTBeacon brtBeacon);
	}

	enum FilterMode {
		// 这些滤波方式均针对于1个或多个数据样本进行滤波,样本容量不宜过大.否则滤波出来的数据不具有代表性.
		// 在提取附近多个iBeacon时,适合使用下面滤波之一.提取最近一个iBeacon不要使用滤波.
		/***************************************************
		 * <pre>
		 * 递推平均滤波法（又称滑动平均滤波法）
		 * 说明：把连续N个采样值看成一个队列，队列长度固定为N。 每次采样到一个新数据放入队尾，并扔掉队首的一
		 * 次数据。把队列中的N各数据进行平均运算，既获得 新的滤波结果。
		 * 优点：对周期性干扰有良好的抑制作用，平滑度高；试用于高频振荡的系统
		 * 缺点：灵敏度低；对偶然出现的脉冲性干扰的抑制作用较差，不适于脉冲干 扰较严重的场合
		 * </pre>
		 ****************************************************/
		MODE_AVER,
		/************************************************
		 * <pre>
		 * 中位值滤波
		 * 优点：对于偶然出现的脉冲性干扰，可消除由其引起的采样值偏差。 对周期性干扰有良好的抑制作用，平滑度高；试用于高频振荡 的系统。
		 * 缺点：测量速度慢
		 * </pre>
		 *************************************************/
		MODE_MEDIUM,
		/** 提取样本量中最大值 */
		MODE_MAX
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

		/**
		 * <pre>
		 * set to false if to trigger one ibeacon ,true to locate with multi-ibeacon. 
		 * must be called to indicate the intent of scan.
		 * </pre>
		 * 
		 * @param isFetchMultiOneTime
		 */
		public void setFetchMultiBeacon(boolean isFetchMultiOneTime) {
			fetchMultiBeacon = isFetchMultiOneTime;
		}

		public void setOnBleScanListener(OnBleScanListener listener) {
			onBleScanListener = listener;
		}
	}
}

/**
 * <pre>
 * 根据卡尔曼滤波算法写的类，用于保存每个时刻的数据信息。
 * see at http://blog.chinaunix.net/uid-26694208-id-3184442.html
 * </pre>
 * 
 * @author zhengtianbao
 * 
 */
class Kalman {
	/** 测量值 */
	int valMeas;
	/** 估计值 */
	int valEsti;
	/** 最优值 */
	double valOpti;
	/** 估计值不确定度 */
	double uncerEsti;
	/** 测量值不确定度 */
	double uncerMeas;
	/** 估计值偏差 */
	double devEsti;
	/** 测量值偏差 */
	double devMeas;
	/** 最优值偏差 */
	double devOpti;
}
