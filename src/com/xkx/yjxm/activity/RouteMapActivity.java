package com.xkx.yjxm.activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.BaseListAdapter;
import com.xkx.yjxm.service.BLEService;
import com.xkx.yjxm.service.BLEService.BleBinder;
import com.xkx.yjxm.utils.CommonUtils;

@SuppressLint("HandlerLeak")
public class RouteMapActivity extends Activity implements OnClickListener {
	// private Bitmap bitmap;
	int mBitmapWidth = 0;
	int mBitmapHeight = 0;
	int mArrayColor[] = null;
	int mArrayColorLengh = 0;
	private String TAG = "RouteMapActivity";
	private ImageView imageView;
	private TextView textView1;
	private TextView txtdetail;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private ImageButton imgmouth;
	private ImageButton imgplay;
	private ImageButton imgdownmouth;
	private static final int SENSOR_SHAKE = 10;
	private boolean down = false;
	/**
	 * 自动讲解是否开着
	 */
	private boolean openstate = false;
	private ImageButton imgswitch;
	private int soundID;
	private int currentStreamId;
	private MyAdapter myAdapter;
	// private boolean isFinishedLoad = false;
	private boolean isPausePlay = false;
	private BLEService bleService;
	private int mapID;
    private ImageButton btnback;
	/**
	 * 是否已经处理过
	 */
	private HashMap<Integer, Boolean> hasProcessedMap = new HashMap<Integer, Boolean>();

	private final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "appli cation/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

	private MediaPlayer mediaPlayer;

	private boolean isOnRouteActivity = false;

	private Map<Integer, String> soundMap;
	private Map<Integer, String> listmap = new HashMap<Integer, String>();
	private Map<Integer, Integer> mapBgMap = new HashMap<Integer, Integer>();
	private Map<Integer, String> textMap;
	private ListView listView1;
	private TextView txt_ti;
	private RelativeLayout soundlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routemap);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		initUI();
		bindBleScanService();
	}

	private void bindBleScanService() {
		Intent service = new Intent(RouteMapActivity.this, BLEService.class);
		bindService(service, conn, BIND_AUTO_CREATE);
	}

	private void initUI() {

		imgplay = (ImageButton) findViewById(R.id.imgplay);
		imgplay.setOnClickListener(this);
		imgmouth = (ImageButton) findViewById(R.id.imgmouth);
		imgmouth.setOnClickListener(this);
		imgdownmouth = (ImageButton) findViewById(R.id.imgdown);
		imgswitch = (ImageButton) findViewById(R.id.imgswitch);
		imgswitch.setOnClickListener(this);
		txtdetail = (TextView) findViewById(R.id.txtdetail);
		listView1 = (ListView) findViewById(R.id.listView1);
		txt_ti = (TextView) findViewById(R.id.txt_ti);
		ivMap = (ImageView) findViewById(R.id.iv_map);
		textView1= (TextView) findViewById(R.id.textView1);
		ivMap.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				
				
				
				int x = (int) event.getX();
				int topleftx =  x-10;
				
				//left, top, right, bottom
				int y = (int) event.getY();
				int toplefty =  y-10;
				textView1.setText("x=" + x + ",y=" + y);
				//右上角
				int topRightx =  x+10;
				int topRighty =  y-10;
				
				// 左下角
				int bottomleftx =  x-10;
				int bottomlefty =  y+10;
				
				//右上角
				int bottomRightx =  x+10;
				int bottomRighty =  y+10;
				
				
//				LayoutParams lp = new lay
//				textView1.setLayoutParams())
				if (event.getAction() == MotionEvent.ACTION_UP) {
					
					String title = "";
					if(event.getX()>=367 && event.getY()<815)
					{
						
						mapID = 1;
						title = "引导台";
						process(mapID, title);
					}
					if(event.getX()>=417 && event.getY()<792)
					{
						
						mapID = 3;
						title = "感互动3D景区推介区";
						process(mapID, title);
					}	
					
					
				}
				return true;
			}
		});
		
		soundlay = (RelativeLayout) findViewById(R.id.soundlay);
		
		myAdapter = new MyAdapter();
		listView1.setAdapter(myAdapter);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				/*
				 * 解除资源与MediaPlayer的赋值关系 103. * 让资源可以为其它程序利用
				 */
				// mp.release();
				imgplay.setBackgroundResource(R.drawable.ic_play);
				isPausePlay = true;
			}
		});

		soundMap = new HashMap<Integer, String>();
		textMap = new HashMap<Integer, String>();

		new Thread(new Runnable() {

			@Override
			public void run() {
				soundMap.put(1, "yindao.m4a");
				textMap.put(1, getResources().getString(R.string.txt_yin_dao));
				mapBgMap.put(1, R.drawable.img_map_yin_dao_tai);
				soundMap.put(2, "zi_zhu_fu_wu.m4a");
				textMap.put(2, getResources()
						.getString(R.string.txt_bo_fang_qu));
				// mapBgMap.put(2, R.drawable.img_map)
				soundMap.put(3, "tiyan3d.m4a");
				textMap.put(3, getResources().getString(R.string.txt_tiyan_3d));
				mapBgMap.put(3, R.drawable.img_map_ti_yan_3d);
				soundMap.put(4, "ying_yong_zhan_shi.m4a");
				mapBgMap.put(4, R.drawable.img_map_ying_yong_zhan_shi);
				textMap.put(4,
						getResources().getString(R.string.txt_lv_you_zhan_shi));
				soundMap.put(5, "you_ke_jie_dai.m4a");
				mapBgMap.put(5, R.drawable.img_map_you_ke_jie_dai);
				textMap.put(5,
						getResources().getString(R.string.txt_jie_dai_fu_wu));
				soundMap.put(6, "anmo.m4a");
				textMap.put(6, getResources().getString(R.string.txt_anmo));
				mapBgMap.put(6, R.drawable.img_map_an_mo_qu);
				soundMap.put(7, "bo_fang_ping_mu.m4a");
				textMap.put(7,
						getResources().getString(R.string.txt_lv_you_shi_ping));
				// mapBgMap.put(7, R.drawable.img_map);
				soundMap.put(8, "xinglijicun.m4a");
				textMap.put(8,
						getResources().getString(R.string.txt_xing_li_ji_cun));
				mapBgMap.put(8, R.drawable.img_map_xing_li_ji_cun_gui);
				soundMap.put(9, "yi_wu_shi.m4a");
				textMap.put(9, getResources().getString(R.string.txt_yiwu_shi));
				mapBgMap.put(9, R.drawable.img_map_yi_wu_shi);
				soundMap.put(10, "banshouli.m4a");
				textMap.put(10,
						getResources().getString(R.string.txt_ban_shou_li));
				mapBgMap.put(10, R.drawable.img_map_ban_shou_li_chao_shi);
				soundMap.put(11, "duo_gong_neng.m4a");
				textMap.put(11,
						getResources().getString(R.string.txt_duo_gong_neng));
				mapBgMap.put(11, R.drawable.img_map_duo_gong_neng_ting);
				soundMap.put(12, "ji_fang.m4a");
				textMap.put(12, getResources().getString(R.string.txt_hu_jiao));
				mapBgMap.put(12, R.drawable.img_map_hu_jiao_zhong_xin);
				soundMap.put(13, "yu_jing_zhi_hui.m4a");
				textMap.put(13, getResources().getString(R.string.txt_yu_jin));
				mapBgMap.put(13, R.drawable.img_map_yu_jing_zhi_hui);
				soundMap.put(14, "bangongqu.m4a");
				textMap.put(14,
						getResources().getString(R.string.txt_ban_gong_qu));
				mapBgMap.put(14, R.drawable.img_map_ban_gong_qu);
			}
		}).start();

	}

	private void trigger(BluetoothDevice device) {
		final String address = device.getAddress();
		final String name = device.getName();
		String title = "";
		boolean noAudio = false;
		if (address.equalsIgnoreCase("CF:01:01:00:02:F0")) {
			// 智慧导览
			noAudio = true;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F1")) {
			mapID = 8;
			title = "自助行李寄存柜";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F2")) {
			mapID = 3;
			title = "感互动3D景区推介区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F3")) {
			mapID = 4;
			title = "智慧旅游应用展示区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F4")) {
			mapID = 1;
			title = "引导台";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F5")) {
			// 旅客上车处
			noAudio = true;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F6")) {
			// 智慧旅游视屏
			noAudio = true;
			mapID = 2;
			title = "自助服务区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F7")) {
			// 单车租赁 no
			noAudio = true;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:F8")) {
			mapID = 6;
			title = "按摩免费体验区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:FC")) {
			mapID = 10;
			title = "伴手礼超市";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E1")) {
			mapID = 11;
			title = "多功能会议厅";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E2")) {
			mapID = 5;
			title = "综合服务区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E3")) {
			// 呼叫中心
			noAudio = true;
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E4")) {
			mapID = 13;
			title = "预警指挥中心";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E5")) {
			mapID = 14;
			title = "办公区";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E6")) {
			mapID = 9;
			title = "医务室";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E7")) {
			mapID = 7;
			title = "产品信息播放屏幕";
		} else if (address.equalsIgnoreCase("CF:01:01:00:02:E8")) {
			mapID = 12;
			title = "机房";
		} else {
			noAudio = true;
		}
		if (noAudio) {
			// process(1, "测试");
			// runOnUiThread(new Runnable() {
			// public void run() {
			// Toast.makeText(RouteMapActivity.this,
			// "无声音设备" + name + "  " + address,
			// Toast.LENGTH_LONG).show();
			// }
			// });
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(RouteMapActivity.this, "该基站没有介绍哦",
							Toast.LENGTH_LONG).show();
				}
			});
		} else {
			if (hasProcessedMap.get(mapID) == null
					|| !hasProcessedMap.get(mapID)) {
				process(mapID, title);
				hasProcessedMap.put(mapID, true);
			}
		}
		final String t = title;
		runOnUiThread(new Runnable() {
			public void run() {
				txt_ti.setText(t);
			}
		});
	}

	private class MyAdapter extends BaseListAdapter {
		/**
		 * 适配器
		 */
		private class ViewHolder {

			private ImageButton img_btnplay;
			private ImageButton img_btndel;
			private TextView txtname;

		}

		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {
			return listmap.size();
		}

		@SuppressLint("NewApi")
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

				// 设置交错颜色
				// int[] arrayOfInt = mColors;
				// int colorLength = mColors.length;
				// int selected = arrayOfInt[position % colorLength];
				//
				// convertView.setBackgroundResource(selected);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtname.setText(listmap.get(position));
			// if (position == selectItem) { // 选中状态 高亮
			// convertView.setBackgroundResource(R.drawable.img_sounditem);
			//
			// } else { // 正常状态
			// convertView.setBackgroundResource(R.drawable.tabli);
			// }

			holder.img_btnplay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					process2(position, listmap.get(position));
				}
			});
			holder.img_btndel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listmap.remove(position);
					myAdapter.notifyDataSetChanged();
				}
			});
			// holder.imageView1.setBackgroundResource((Integer)
			// list.get(position).get("img"));
			// // Bitmap image =
			// Bitmap.createBitmap(((BitmapDrawable)holder.imageView1.getDrawable()).getBitmap());
			// // imgUtils.getRoundedCornerBitmap(image, 90);
			// // imageLoader.displayImage(
			// // "drawable://" + (Integer) list.get(position).get("img"),
			// // holder.imageView1, options);
			//
			// holder.txttitle.setText((String)
			// list.get(position).get("title"));
			// holder.txttime.setText((String) list.get(position).get("time"));
			// Map<String, Object> map = list.get(position); // distance
			// String object = (String) list.get(position).get("distance");
			// holder.txtdistance.setText(object);

			return convertView;
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}

		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindBleScanService();
		//清空音频列表
		listmap.clear();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	private void unbindBleScanService() {
		unbindService(conn);
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

	private void play() throws IOException {
		// File audioFile = new
		// File(Environment.getExternalStorageDirectory(),"");
		// mediaPlayer.reset();
		// mediaPlayer.setDataSource("drawable://" + (Integer) R.raw.yindao);
		mediaPlayer.prepare();
		mediaPlayer.start();// 播放
	}

	// sound hm中的第几个歌曲
	// loop 是否循环 0不循环 -1循环
	public void playSound(final int sound) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				AssetManager assetMg = getApplicationContext().getAssets();
				AssetFileDescriptor fileDescriptor = null;
				try {
					mediaPlayer.reset();
					fileDescriptor = assetMg.openFd(soundMap.get(sound));
					mediaPlayer.setDataSource(
							fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());

					play();// 开始或恢复播放

				} catch (IOException e) {
					e.printStackTrace();
				}

				// try {
				// Thread.sleep(10000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// Log.e("sound", "before:" + System.currentTimeMillis());
				// pool.play(id, 1, 1, 1, 0, 1f);
				// Log.e("sound", "after:" + System.currentTimeMillis());
			}
		}).start();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.imgswitch:
			if (openstate) {
				// 取消摇一摇
				imgswitch.setBackgroundResource(R.drawable.img_autoexplain);
				if (sensorManager != null) {// 取消监听器
					sensorManager.unregisterListener(sensorEventListener);
				}
				bleService.setShakeScan(false);
				openstate = false;
			} else {
				// 注册摇一摇
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
				bleService.setShakeScan(true);
				openstate = true;
			}
			break;
		case R.id.imgmouth:

			if (down) {
				// 不显示文本
				imgmouth.setBackgroundResource(R.drawable.ic_mouth);
				imgdownmouth.setVisibility(View.GONE);
				txtdetail.setVisibility(View.GONE);
				down = false;
			} else {
				// 显示文本
				imgmouth.setBackgroundResource(R.drawable.img_moudown);
				imgdownmouth.setVisibility(View.VISIBLE);
				txtdetail.setVisibility(View.VISIBLE);
				down = true;
			}
			break;
		case R.id.imgplay:
			// disableViewForSeconds(imgplay);
			// 如果是在播放态
			if (!CommonUtils.isFastDoubleClick()) {
				playprocess();
			}
			break;
		case R.id.btnback:
			finish();
			break;
		default:
			break;
		}
	}

	// 暂停播放
	private void playprocess() {
		// tupian
		if (mediaPlayer.isPlaying()) {
			// 暂停语音
			mediaPlayer.pause(); // 调用暂停方法
			imgplay.setBackgroundResource(R.drawable.ic_play);
			isPausePlay = true;
		} else {
			if (isPausePlay) {
				mediaPlayer.start(); // 播放
				imgplay.setBackgroundResource(R.drawable.ic_pause);
				isPausePlay = false;
			}
			// else {
			//
			// process(mapID);
			// }
		}
	}

	private void addToList(int mapId, String title) {
		listmap.put(mapId, title);
		runOnUiThread(new Runnable() {
			public void run() {
				myAdapter.notifyDataSetChanged();
			}
		});
	}

	// 播放
	private void process2(final int mapID, String title) {
		runOnUiThread(new Runnable() {
			public void run() {

				txtdetail.setText(textMap.get(mapID));
			}
		});

		playSound(mapID);// 播放dudu，dudu文件被解码为16位的PCM数据后超过了SoundPool的1M缓冲区了，循环不了，而且不能播完整个歌曲
		runOnUiThread(new Runnable() {
			public void run() {
				imgplay.setBackgroundResource(R.drawable.ic_pause);
			}
		});
	}

	// 播放
	private void process(final int mapID, String title) {
		runOnUiThread(new Runnable() {
			public void run() {
				int resId = mapBgMap.get(mapID);
				ivMap.setBackgroundResource(resId);
				soundlay.setVisibility(View.VISIBLE);
				txtdetail.setText(textMap.get(mapID));
			}
		});
		if (mediaPlayer.isPlaying()) {
			Log.e("加入列表","加入列表");
			if (listmap.size() < 3) {
				Log.e("插入列表","插入列表");
				addToList(mapID, title);
			}
			return;
		}
		playSound(mapID);// 播放dudu，dudu文件被解码为16位的PCM数据后超过了SoundPool的1M缓冲区了，循环不了，而且不能播完整个歌曲
		runOnUiThread(new Runnable() {
			public void run() {
				imgplay.setBackgroundResource(R.drawable.ic_pause);
			}
		});
	}

	public void disableViewForSeconds(final View v) {

		v.setClickable(false);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				v.setClickable(true);

			}

		}, 2000);

	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	private String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
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
			Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = SENSOR_SHAKE;
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
			case SENSOR_SHAKE:
				processAfterShake();
				break;
			}
		}

		private void processAfterShake() {
			BluetoothDevice device = bleService.getProximityBleDevice();
			if (device != null) {
				if (openstate) {
					trigger(device);
					Log.e("toast", "yaoyiyao");
				}
			} else {
				Toast.makeText(RouteMapActivity.this, "刚才没有检测到基站噢~~~,请尝试...",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("scan", "onServiceDisconnected()");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("scan", "onServiceConnected()");
			BleBinder binder = (BleBinder) service;
			bleService = binder.getService();
			bleService
					.setOnProximityBleChangedListener(new BLEService.OnProximityBleChangedListener() {
						public void onProximityBleChanged(
								BluetoothDevice original,
								BluetoothDevice current) {
						}

						public void onConditionTriggerFailed(
								BluetoothDevice device) {
						}

						public void onConditionTriggerSuccess(
								BluetoothDevice device) {
							if (!openstate) {
								trigger(device);
							}
						}
					});
			bleService.startScanBLE();
		}
	};
	private ImageView ivMap;

}
