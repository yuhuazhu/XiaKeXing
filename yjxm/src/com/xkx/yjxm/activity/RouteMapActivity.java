package com.xkx.yjxm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.sdk.BRTBeacon;
import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.bean.MacInfo;
import com.xkx.yjxm.bean.ResInfo;
import com.xkx.yjxm.db.MySqlite;
import com.xkx.yjxm.service.AudioService;
import com.xkx.yjxm.service.AudioService.AudioBinder;
import com.xkx.yjxm.service.AudioService.OnPlayCompleteListener;
import com.xkx.yjxm.service.BleScanService;
import com.xkx.yjxm.service.BleScanService.BleBinder;
import com.xkx.yjxm.service.BleScanService.OnBleScanListener;
import com.xkx.yjxm.utils.CommonUtils;
import com.xkx.yjxm.utils.CrashHandler;

@SuppressLint({ "HandlerLeak", "DefaultLocale", "UseSparseArrays" })
public class RouteMapActivity extends Activity implements OnClickListener {

	protected static final int DIMEN = 20;
	private final int ID_ZHI_HUI_DAO_LAN = 1;
	private final int ID_XING_LI_JI_CUN = 2;
	private final int ID_3D_HU_DONG = 3;
	private final int ID_YING_YONG_ZHAN_SHI = 4;
	private final int ID_YIN_DAO_TAI = 5;
	private final int ID_LV_KE_SHANG_CHE = 6;
	private final int ID_ZHI_HUI_LV_YOU_SHI_PING = 7;
	private final int ID_DAN_CHE_ZU_LIN = 8;
	private final int ID_XIU_XIAN_ZI_ZHU = 9;
	private final int ID_BAN_SHOU_LI_CHAO_SHI = 10;
	private final int ID_DUO_GONG_NENG_TING = 11;
	private final int ID_ZONG_HE_FU_WU_QU = 12;
	private final int ID_HU_JIAO_ZHONG_XIN = 13;
	private final int ID_YU_JING_ZHI_HUI_ZHONG_XIN = 14;
	private final int ID_BAN_GONG_QU = 15;
	private final int ID_YI_WU_SHI = 16;
	private final int ID_XIN_XI_SHI_PING = 17;
	private final int ID_JI_FNAG = 18;
	private final int ID_HUN_SHA_SHE_YING = 19;

	private final int MSG_SENSOR_SHAKE = 10;

	private TextView textView1;
	/** 解说词 */
	private TextView tvContent;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private ImageButton imgmouth;
	private ImageButton imgplay;
	/** 舌头解说词 */
	private ImageButton imgdownmouth;

	private String TAG = "RouteMapActivity";
	private boolean isPlaying = false;
	private boolean down = false;
	/**
	 * 自动讲解是否开着
	 */
	private boolean isAuto = true;
	private long lastTriggerTime;

	private TextView tvTitle;
	private ImageButton imgswitch;
	private ImageButton btnback;
	private ImageView ivMap;
	private ListView listView1;

	private Map<Integer, String> xMap = new HashMap<Integer, String>();
	private Map<Integer, String> yMap = new HashMap<Integer, String>();

	
	private HashMap<Integer, ResInfo> ResMap = new HashMap<Integer, ResInfo>();//资源
	private HashMap<Integer, Boolean> hasProcessedMap = new HashMap<Integer, Boolean>();
	
	private HashMap<Integer, MacInfo> MacMap = new HashMap<Integer, MacInfo>();//mac地址
	private CopyOnWriteArrayList<ItemData> titleList = new CopyOnWriteArrayList<ItemData>();
	private HashMap<Integer, Long> idTriggerTimeMap = new HashMap<Integer, Long>();

	private BaseAdapter adapter;

	private RelativeLayout soundlay;

	private SQLiteDatabase mDB;

	// /**
	// * 存储音频地址
	// */
	// ArrayList<String> listImgPath;
	// String[] imageUriArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routemap);
		bindBleScanService();
		bindAudioService();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		initData();
		initUI();
	}

	public void hidesoundlay(View v) {
		isPlaying = false;
		audioBinder.audioStop();
		soundlay.setVisibility(View.GONE);
	}

	public void home(View v) {
		Intent intent = null;
		intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	private void bindAudioService() {
		Intent service = new Intent(RouteMapActivity.this, AudioService.class);
		bindService(service, audioConn, BIND_AUTO_CREATE);
	}

	private void bindBleScanService() {
		Intent service = new Intent(RouteMapActivity.this, BleScanService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	private void initUI() {
		soundlay = (RelativeLayout) findViewById(R.id.soundlay);
		imgplay = (ImageButton) findViewById(R.id.imgplay);
		imgplay.setOnClickListener(this);
		imgmouth = (ImageButton) findViewById(R.id.imgmouth);
		imgmouth.setOnClickListener(this);
		imgdownmouth = (ImageButton) findViewById(R.id.imgdown);
		imgswitch = (ImageButton) findViewById(R.id.imgswitch);
		imgswitch.setOnClickListener(this);
		imgplay.setEnabled(false);
		tvContent = (TextView) findViewById(R.id.txtdetail);
		listView1 = (ListView) findViewById(R.id.listView1);
		ivMap = (ImageView) findViewById(R.id.iv_map);
		textView1 = (TextView) findViewById(R.id.textView1);
		tvTitle = (TextView) findViewById(R.id.txt_ti);
		btnback = (ImageButton) findViewById(R.id.btnback);
		btnback.setOnClickListener(this);
		// setIvMap();

		adapter = new MyAdapter();
		listView1.setAdapter(adapter);

		// initXYMap();
	}

	/**
	 * 
	 * 获取图片地址列表
	 * 
	 * @return list
	 */
	private void getResource() {
		
		Cursor cursorRes = mDB
				.query("ResInfo", null, null, null, null, null, null);
		Log.e("cursor", cursorRes.getCount() + "");

		boolean toResFirst = cursorRes.moveToFirst();

		// Toast.makeText(getApplicationContext(),
		// String.valueOf(cursor.getCount()), 3000).show();
		while (toResFirst) {
			ResInfo rs = new ResInfo(cursorRes.getString(cursorRes.getColumnIndex("title")),
					cursorRes.getString(cursorRes.getColumnIndex("content")),
					cursorRes.getString(cursorRes.getColumnIndex("bgname")),
					cursorRes.getString(cursorRes.getColumnIndex("musicname")));
			ResMap.put(cursorRes.getInt(cursorRes.getColumnIndex("ID")),
					rs);// 将资源文件添加到list中
			toResFirst = cursorRes.moveToNext();
		}
		cursorRes.close();
		
		Cursor cursorMac = mDB
				.query("MacInfo", null, null, null, null, null, null);
		Log.e("cursor", cursorMac.getCount() + "");

		boolean toMacFirst = cursorMac.moveToFirst();
		while (toMacFirst) {
			
			MacInfo rs = new MacInfo(cursorRes.getString(cursorRes.getColumnIndex("macname")),
					cursorRes.getFloat(cursorRes.getColumnIndex("power")),
					cursorRes.getFloat(cursorRes.getColumnIndex("musicname")));
			MacMap.put(cursorRes.getInt(cursorRes.getColumnIndex("ID")),
					rs);// 将mac信息添加到list中
			toMacFirst = cursorMac.moveToNext();
		}
		cursorMac.close();

	}

	/**
	 * 
	 * 获取音频地址列表
	 * 
	 * @return list
	 */
	private ArrayList<String> getSoundPathList() {
		ArrayList<String> list = new ArrayList<String>();
		// 获取SD卡路径
		String path = Environment.getExternalStorageDirectory()
				+ "/resource/muisc";
		File file = new File(path);
		// 如果SD卡目录不存在创建
		if (!file.exists()) {
			file.mkdir();
		}
		File[] file2 = file.listFiles();
		for (int i = 0; i < file2.length; i++) {
			if (file2[i].isDirectory()) {
			} else {
				list.add(file2[i].getAbsolutePath());// 将图片路径添加到list中
			}
		}
		return list;
	}

	private void initData() {

		// 扫描内存中图片并存入list
		MySqlite mySqlite = new MySqlite(RouteMapActivity.this, "yjxm.db",
				null, 1);
		mDB = mySqlite.getReadableDatabase();
		getResource();
		for (int id = 1; id <= 19; id++) {

			hasProcessedMap.put(id, false);
			idTriggerTimeMap.put(id, 0l);
		}
	}
	/**
	 * 更新界面
	 * @param id  序号
	 * @param play 是否播放
	 */
	private void updateHead(final int id, final boolean play) {
		runOnUiThread(new Runnable() {
			public void run() {
				String title = ResMap.get(id).getTitle();
				String content = ResMap.get(id).getContent();
				if (play) {
					imgplay.setBackgroundResource(R.drawable.ic_pause);
				} else {
					imgplay.setBackgroundResource(R.drawable.ic_play);
				}
				imgplay.setEnabled(true);
				tvTitle.setText(title);
				tvContent.setText(content);
				tvContent.setVisibility(View.VISIBLE);

				// 获取SD卡路径
				String path = Environment.getExternalStorageDirectory()
						+ "/resource/map/";
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(path + ResMap.get(id).getBgname(), options);
				ivMap.setImageBitmap(bm);
				// ivMap.setBackgroundResource(bgMap.get(id));
				imgdownmouth.setVisibility(View.VISIBLE);
			}
		});
	}

	private void trigger(BRTBeacon beacon) {
		if (System.currentTimeMillis() - lastTriggerTime < 2000) {
			return;
		}
		final String address = beacon.macAddress.trim();
		Log.e("address", address);
		final int id = getId(address);
		if (id < 1 || id > 19) {
			return;
		}
		long idLastTriggerTime = idTriggerTimeMap.get(id);
		if (isAuto
				&& System.currentTimeMillis() - idLastTriggerTime < 60 * 1000) {// 自动讲解，一分钟内不触发
			return;
		}
		runOnUiThread(new Runnable() {
			public void run() {
				String title = ResMap.get(id).getTitle();
				ItemData data = new ItemData(title, id);
				if (isPlaying) {
					for (int i = 0; i < titleList.size(); i++) {
						ItemData listData = titleList.get(i);
						if (listData.id == id) {
							return;
						}
					}
					// boolean hasProcess = hasProcessedMap.get(id);
					if (titleList.size() < 3) {
						titleList.add(0, data);
						adapter.notifyDataSetChanged();
					}
				} else {
					lastTriggerTime = System.currentTimeMillis();
					idTriggerTimeMap.put(id, lastTriggerTime);
					processPlay(id, true);
					// boolean hasProcessd = hasProcessedMap.get(id);
					// if (!hasProcessd) {
					// lastTriggerTime = System.currentTimeMillis();
					// imgplay.setEnabled(true);
					// processPlay(id, true);
					// }
				}
			}
		});
	}

	private class MyAdapter extends BaseListAdapter {
		private class ViewHolder {
			private ImageButton img_btnplay;
			private ImageButton img_btndel;
			private TextView txtname;
		}

		@Override
		public int getCount() {
			return titleList.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.activity_sounditem, null);
				holder = new ViewHolder();
				holder.img_btnplay = (ImageButton) convertView
						.findViewById(R.id.img_btnplay);
				holder.img_btndel = (ImageButton) convertView
						.findViewById(R.id.img_btndel);
				holder.txtname = (TextView) convertView
						.findViewById(R.id.txtname);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtname.setText(titleList.get(position).title);
			holder.img_btnplay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!CommonUtils.isFastDoubleClick()) {
						ItemData data = titleList.get(position);
						processPlay(data.id, true);
					}
				}
			});
			holder.img_btndel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					titleList.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	private class ItemData {
		public String title;
		public int id;

		public ItemData(String title, int id) {
			super();
			this.title = title;
			this.id = id;
		}
	}

	private int getId(String address) {
		address = address.trim();
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			return ID_ZHI_HUI_DAO_LAN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			return ID_XING_LI_JI_CUN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			return ID_3D_HU_DONG;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			return ID_YING_YONG_ZHAN_SHI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			return ID_YIN_DAO_TAI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FB")) {
			return ID_LV_KE_SHANG_CHE;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			return ID_ZHI_HUI_LV_YOU_SHI_PING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			return ID_DAN_CHE_ZU_LIN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			return ID_XIU_XIAN_ZI_ZHU;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			return ID_BAN_SHOU_LI_CHAO_SHI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			return ID_DUO_GONG_NENG_TING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			return ID_ZONG_HE_FU_WU_QU;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			return ID_HU_JIAO_ZHONG_XIN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			return ID_YU_JING_ZHI_HUI_ZHONG_XIN;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			return ID_BAN_GONG_QU;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			return ID_YI_WU_SHI;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			return ID_XIN_XI_SHI_PING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			return ID_JI_FNAG;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FD")) {
			return ID_HUN_SHA_SHE_YING;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			return ID_BAN_GONG_QU;
		}
		return -1;
	}

	private void processPlay(int id, boolean play) {

		// 获取SD卡路径
		String path = Environment.getExternalStorageDirectory()
				+ "/resource/muisc/";
		Uri uri = Uri.parse(path + ResMap.get(id).getMusicname());
		if (uri == null) {
			CrashHandler.getInstance().logToFile(Thread.currentThread(),
					new Exception("Uri null"));
			return;
		}
		updateHead(id, play);
		hasProcessedMap.put(id, true);
		isPlaying = true;
		soundlay.setVisibility(View.VISIBLE);
		// getSoundPathList()

		audioBinder.audioPlay(uri);
	}

	private Uri getUri(int id) {
		Uri uri = null;
		String parent = "android.resource://com.xkx.yjxm/";
		if (id == ID_BAN_GONG_QU) {
			uri = Uri.parse(parent + R.raw.ban_gong_qu);
		} else if (id == ID_BAN_SHOU_LI_CHAO_SHI) {
			uri = Uri.parse(parent + R.raw.ban_shou_li_chao_shi);
		} else if (id == ID_DUO_GONG_NENG_TING) {
			uri = Uri.parse(parent + R.raw.duo_gong_neng_ting);
		} else if (id == ID_3D_HU_DONG) {
			uri = Uri.parse(parent + R.raw.hu_dong_qu);
		} else if (id == ID_HU_JIAO_ZHONG_XIN) {
			uri = Uri.parse(parent + R.raw.hu_jiao_zhong_xin);
		} else if (id == ID_HUN_SHA_SHE_YING) {
			uri = Uri.parse(parent + R.raw.hun_sha_she_ying);
		} else if (id == ID_JI_FNAG) {
			uri = Uri.parse(parent + R.raw.ji_fang);
		} else if (id == ID_ZHI_HUI_LV_YOU_SHI_PING) {
			uri = Uri.parse(parent + R.raw.lv_you_zhi_hui_ping);
		} else if (id == ID_XIU_XIAN_ZI_ZHU) {
			uri = Uri.parse(parent + R.raw.lv_you_zi_zhu_fu_wu_qu);
		} else if (id == ID_XIN_XI_SHI_PING) {
			uri = Uri.parse(parent + R.raw.xin_xi_bo_fang_ping);
		} else if (id == ID_XING_LI_JI_CUN) {
			uri = Uri.parse(parent + R.raw.xing_li_ji_cun_qu);
		} else if (id == ID_YI_WU_SHI) {
			uri = Uri.parse(parent + R.raw.yi_wu_shi);
		} else if (id == ID_YIN_DAO_TAI) {
			uri = Uri.parse(parent + R.raw.yin_dao_tai);
		} else if (id == ID_YING_YONG_ZHAN_SHI) {
			uri = Uri.parse(parent + R.raw.ying_yong_zhan_shi_qu);
		} else if (id == ID_LV_KE_SHANG_CHE) {
			uri = Uri.parse(parent + R.raw.you_ke_shang_che_qu);
		} else if (id == ID_YU_JING_ZHI_HUI_ZHONG_XIN) {
			uri = Uri.parse(parent + R.raw.yu_jing_zhong_xin);
		} else if (id == ID_DAN_CHE_ZU_LIN) {
			uri = Uri.parse(parent + R.raw.zi_xing_che_zu_lin);
		} else if (id == ID_ZONG_HE_FU_WU_QU) {
			uri = Uri.parse(parent + R.raw.zong_he_fu_wu_qu);
		} else if (id == ID_ZHI_HUI_DAO_LAN) {
			uri = Uri.parse(parent + R.raw.zhi_hui_dao_lan);
		}
		return uri;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindAllService();
	}

	private void unbindAllService() {
		unbindService(conn);
		unbindService(audioConn);
	}

	private static Bitmap big(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(2.0f, 1.85f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.8f, 0.8f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgswitch:
			isAuto = !isAuto;
			if (isAuto) {
				// 自动讲解
				imgswitch.setBackgroundResource(R.drawable.img_autoexplain);
				sensorManager.unregisterListener(sensorEventListener);
			} else {
				// 摇一摇
				imgswitch.setBackgroundResource(R.drawable.img_shake);
				if (sensorManager != null) {// 注册监听器
					sensorManager
							.registerListener(
									sensorEventListener,
									sensorManager
											.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
									SensorManager.SENSOR_DELAY_NORMAL);
					// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
				}
			}
			break;
		case R.id.imgmouth:
			if (down) {
				// 不显示文本
				imgmouth.setBackgroundResource(R.drawable.ic_mouth);
				imgdownmouth.setVisibility(View.GONE);
				tvContent.setVisibility(View.GONE);
				down = false;
			} else {
				// 显示文本
				imgmouth.setBackgroundResource(R.drawable.img_moudown);
				imgdownmouth.setVisibility(View.VISIBLE);
				tvContent.setVisibility(View.VISIBLE);
				down = true;
			}
			break;
		case R.id.imgplay:
			if (CommonUtils.isFastDoubleClick()) {
				return;
			}
			isPlaying = !isPlaying;
			if (isPlaying) {
				imgplay.setBackgroundResource(R.drawable.ic_pause);
				audioBinder.audioStart();
			} else {
				imgplay.setBackgroundResource(R.drawable.ic_play);
				audioBinder.audioPause();
			}
			break;
		case R.id.btnback:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			if (Math.abs(x) > 15 || Math.abs(y) > 15 || Math.abs(z) > 15) {
				Log.i(TAG, "x轴" + x + "；y轴" + y + "；z轴" + z);
			}
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = MSG_SENSOR_SHAKE;
				handler.sendMessage(msg);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	/**
	 * 动作执行
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SENSOR_SHAKE:
				processAfterShake();
				break;
			}
		}

		private void processAfterShake() {
			// BluetoothDevice device = bleService.getProximityBleDevice();
			BRTBeacon beacon = bleBinder.getProximityBeacon();
			if (beacon != null) {
				trigger(beacon);
			}
		}
	};

	private AudioBinder audioBinder;

	private ServiceConnection audioConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			audioBinder = (AudioBinder) service;
			audioBinder.setOnPlayCompleteListener(new OnPlayCompleteListener() {

				@Override
				public void onPlayComplete() {
					runOnUiThread(new Runnable() {
						public void run() {
							isPlaying = false;
							imgplay.setBackgroundResource(R.drawable.ic_play);
							tvContent.setVisibility(View.INVISIBLE);
							imgdownmouth.setVisibility(View.INVISIBLE);
						}
					});
				}
			});
			if (audioBinder == null) {
				CrashHandler.getInstance().logToFile(Thread.currentThread(),
						new Exception("audioBinder null"));
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	};

	private BleBinder bleBinder;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected");
			bleBinder = (BleBinder) service;
			bleBinder.setRegion(null);
			bleBinder.setOnBleScanListener(new OnBleScanListener() {

				@Override
				public void onPeriodScan(List<BRTBeacon> scanResultList) {
					Log.e("scan", "onPeriodScan,size:" + scanResultList.size());
				}

				@Override
				public void onNearBleChanged(BRTBeacon oriBeacon,
						BRTBeacon desBeacon) {
					Log.e("scan", "onPeriodScan,current:"
							+ desBeacon.macAddress);
				}

				@Override
				public void onNearBle(BRTBeacon brtBeacon) {
					Log.e("scan", "onNearBle");
					if (isAuto) {
						trigger(brtBeacon);
					}
				}
			});
		}
	};

	private void initXYMap() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				xMap.put(1, getResources().getString(R.string.point1x));
				yMap.put(1, getResources().getString(R.string.point1y));
				xMap.put(2, getResources().getString(R.string.point2x));
				yMap.put(2, getResources().getString(R.string.point2y));
				xMap.put(3, getResources().getString(R.string.point3x));
				yMap.put(3, getResources().getString(R.string.point3y));
				xMap.put(4, getResources().getString(R.string.point4x));
				yMap.put(4, getResources().getString(R.string.point4y));
				xMap.put(5, getResources().getString(R.string.point5x));
				yMap.put(5, getResources().getString(R.string.point5y));
				xMap.put(6, getResources().getString(R.string.point6x));
				yMap.put(6, getResources().getString(R.string.point6y));
				xMap.put(7, getResources().getString(R.string.point7x));
				yMap.put(7, getResources().getString(R.string.point7y));
				xMap.put(8, getResources().getString(R.string.point8x));
				yMap.put(8, getResources().getString(R.string.point8y));
				xMap.put(9, getResources().getString(R.string.point9x));
				yMap.put(9, getResources().getString(R.string.point9y));
				xMap.put(10, getResources().getString(R.string.point10x));
				yMap.put(10, getResources().getString(R.string.point10y));
				xMap.put(11, getResources().getString(R.string.point11x));
				yMap.put(11, getResources().getString(R.string.point11y));
			}
		}).start();
	}

	private void setIvMap() {

		ivMap.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int topleftx = x - 10;

				// left, top, right, bottom
				int y = (int) event.getY();
				int toplefty = y - 10;
				textView1.setText("x=" + x + ",y=" + y);
				// 右上角
				int topRightx = x + 10;
				int topRighty = y - 10;

				// 左下角
				int bottomleftx = x - 10;
				int bottomlefty = y + 10;

				// 右上角
				int bottomRightx = x + 10;
				int bottomRighty = y + 10;

				// LayoutParams lp = new lay
				// textView1.setLayoutParams())
				if (event.getAction() == MotionEvent.ACTION_UP) {

					String title = "";

					if (x >= Integer.parseInt(xMap.get(2)) - 20
							&& x <= Integer.parseInt(xMap.get(2)) + 20
							&& y >= Integer.parseInt(yMap.get(2)) - 20
							&& y <= Integer.parseInt(yMap.get(2)) + 20) {

						title = "引导台";
					}
					if (x >= Integer.parseInt(xMap.get(3)) - 20
							&& x <= Integer.parseInt(xMap.get(3)) + 20
							&& y >= Integer.parseInt(yMap.get(3)) - 20
							&& y <= Integer.parseInt(yMap.get(3)) + 20) {

						title = "智慧旅游应用展示区";
					}
					if (x >= Integer.parseInt(xMap.get(4)) - 20
							&& x <= Integer.parseInt(xMap.get(4)) + 20
							&& y >= Integer.parseInt(yMap.get(4)) - 20
							&& y <= Integer.parseInt(yMap.get(4)) + 20) {

						title = "产品信息播放屏幕";
					}

					if (x >= Integer.parseInt(xMap.get(5)) - 20
							&& x <= Integer.parseInt(xMap.get(5)) + 20
							&& y >= Integer.parseInt(yMap.get(5)) - 20
							&& y <= Integer.parseInt(yMap.get(5)) + 20) {

						title = "综合服务区";
					}

					if (x >= Integer.parseInt(xMap.get(6)) - 20
							&& x <= Integer.parseInt(xMap.get(6)) + 20
							&& y >= Integer.parseInt(yMap.get(6)) - 20
							&& y <= Integer.parseInt(yMap.get(6)) + 20) {

						title = "感互动3D景区推介区";
					}

					if (x >= Integer.parseInt(xMap.get(8)) - 20
							&& x <= Integer.parseInt(xMap.get(8)) + 20
							&& y >= Integer.parseInt(yMap.get(8)) - 20
							&& y <= Integer.parseInt(yMap.get(8)) + 20) {

						title = "自助行李寄存柜";
					}

					if (x >= Integer.parseInt(xMap.get(9)) - 20
							&& x <= Integer.parseInt(xMap.get(9)) + 20
							&& y >= Integer.parseInt(yMap.get(9)) - 20
							&& y <= Integer.parseInt(yMap.get(9)) + 20) {

						title = "按摩免费体验区";
					}

					if (x >= Integer.parseInt(xMap.get(10)) - 20
							&& x <= Integer.parseInt(xMap.get(10)) + 20
							&& y >= Integer.parseInt(yMap.get(10)) - 20
							&& y <= Integer.parseInt(yMap.get(10)) + 20) {

						title = "医务室";
					}
					if (x >= Integer.parseInt(xMap.get(11)) - 20
							&& x <= Integer.parseInt(xMap.get(11)) + 20
							&& y >= Integer.parseInt(yMap.get(11)) - 20
							&& y <= Integer.parseInt(yMap.get(11)) + 20) {

						title = "伴手礼超市";
					}
					// if (event.getX() >= Integer.parseInt(xMap.get(2))
					// && event.getY() < Integer.parseInt(xMap.get(2))) {
					//
					// mapID = 1;
					// title = "引导台";
					// process(mapID, title);
					// }

				}
				return true;
			}
		});
	}
}
