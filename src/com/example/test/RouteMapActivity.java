package com.example.test;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

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
	private boolean isOnRouteActivity = false;
	private boolean down = false;
	private boolean playstate = false;
	private boolean openstate = false;
	private ImageButton imgswitch;
	private int soundID;
	private final String[][] MIME_MapTable={ 
            //{后缀名，MIME类型} 
            {".3gp",    "video/3gpp"}, 
            {".apk",    "application/vnd.android.package-archive"}, 
            {".asf",    "video/x-ms-asf"}, 
            {".avi",    "video/x-msvideo"}, 
            {".bin",    "application/octet-stream"}, 
            {".bmp",    "image/bmp"}, 
            {".c",  "text/plain"}, 
            {".class",  "application/octet-stream"}, 
            {".conf",   "text/plain"}, 
            {".cpp",    "text/plain"}, 
            {".doc",    "application/msword"}, 
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, 
            {".xls",    "application/vnd.ms-excel"},  
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, 
            {".exe",    "application/octet-stream"}, 
            {".gif",    "image/gif"}, 
            {".gtar",   "application/x-gtar"}, 
            {".gz", "application/x-gzip"}, 
            {".h",  "text/plain"}, 
            {".htm",    "text/html"}, 
            {".html",   "text/html"}, 
            {".jar",    "application/java-archive"}, 
            {".java",   "text/plain"}, 
            {".jpeg",   "image/jpeg"}, 
            {".jpg",    "image/jpeg"}, 
            {".js", "application/x-javascript"}, 
            {".log",    "text/plain"}, 
            {".m3u",    "audio/x-mpegurl"}, 
            {".m4a",    "audio/mp4a-latm"}, 
            {".m4b",    "audio/mp4a-latm"}, 
            {".m4p",    "audio/mp4a-latm"}, 
            {".m4u",    "video/vnd.mpegurl"}, 
            {".m4v",    "video/x-m4v"},  
            {".mov",    "video/quicktime"}, 
            {".mp2",    "audio/x-mpeg"}, 
            {".mp3",    "audio/x-mpeg"}, 
            {".mp4",    "video/mp4"}, 
            {".mpc",    "application/vnd.mpohun.certificate"},        
            {".mpe",    "video/mpeg"},   
            {".mpeg",   "video/mpeg"},   
            {".mpg",    "video/mpeg"},   
            {".mpg4",   "video/mp4"},    
            {".mpga",   "audio/mpeg"}, 
            {".msg",    "application/vnd.ms-outlook"}, 
            {".ogg",    "audio/ogg"}, 
            {".pdf",    "application/pdf"}, 
            {".png",    "image/png"}, 
            {".pps",    "application/vnd.ms-powerpoint"}, 
            {".ppt",    "application/vnd.ms-powerpoint"}, 
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, 
            {".prop",   "text/plain"}, 
            {".rc", "text/plain"}, 
            {".rmvb",   "audio/x-pn-realaudio"}, 
            {".rtf",    "application/rtf"}, 
            {".sh", "text/plain"}, 
            {".tar",    "application/x-tar"},    
            {".tgz",    "application/x-compressed"},  
            {".txt",    "text/plain"}, 
            {".wav",    "audio/x-wav"}, 
            {".wma",    "audio/x-ms-wma"}, 
            {".wmv",    "audio/x-ms-wmv"}, 
            {".wps",    "application/vnd.ms-works"}, 
            {".xml",    "text/plain"}, 
            {".z",  "application/x-compress"}, 
            {".zip",    "application/x-zip-compressed"}, 
            {"",        "*/*"}   
        }; 

	SoundPool mSoundPool = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routemap);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		initUI();
		mSoundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		
				soundID = mSoundPool.load(RouteMapActivity.this, R.raw.windows, 1);
				// TODO Auto-generated method stub
				
			
		//设置最多可容纳10个音频流，音频的品质为5
		

	}

	private void initUI() {

		imgplay = (ImageButton) findViewById(R.id.imgplay);
		imgplay.setOnClickListener(this);
		imgmouth = (ImageButton) findViewById(R.id.imgmouth);
		imgmouth.setOnClickListener(this);
		imgdownmouth = (ImageButton) findViewById(R.id.imgdown);
		imgswitch = (ImageButton) findViewById(R.id.imgswitch);
		imgswitch.setOnClickListener(this);
		imageView = (ImageView) findViewById(R.id.imageView1);
		textView1 = new TextView(this);
		txtdetail = (TextView) findViewById(R.id.txtdetail);

		// BitmapDrawable db = (BitmapDrawable) getResources().getDrawable(
		// R.drawable.ic_left_about);
		// bitmap = big(db.getBitmap());

		LinearLayout l = new LinearLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		l.addView(textView1);
		addContentView(l, params);

		// imageView.setImageBitmap(bitmap);
		// http://xys289187120.blog.51cto.com/3361352/657590
		// mBitmapWidth = bitmap.getWidth();
		// mBitmapHeight = bitmap.getHeight();
		mArrayColorLengh = mBitmapWidth * mBitmapHeight;
		mArrayColor = new int[mArrayColorLengh];

		// Log.i(TAG, "图片大小：mBitmapWidth=" + mBitmapWidth + ",mBitmapHeight="
		// + mBitmapHeight + ",mArrayColorLengh=" + mArrayColorLengh);
		// int count = 0;
		// for (int i = 0; i < mBitmapHeight; i++) {
		// for (int j = 0; j < mBitmapWidth; j++) {
		// // 获得Bitmap 图片中每一个点的color颜色值
		// int color = bitmap.getPixel(j, i);
		// // 将颜色值存在一个数组中 方便后面修改
		// mArrayColor[count] = color;
		//
		// count++;
		// }
		// }

		// imageView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// int x = (int) event.getX();
		// // left, top, right, bottom
		// int y = (int) event.getY();
		//
		// textView1.setPadding(x - 50, y - 50, 0, 0);
		// // LayoutParams lp = new lay
		// // textView1.setLayoutParams())
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// textView1.setText("x=" + x + ",y=" + y);
		// int color = bitmap.getPixel(x, y);
		// mArrayColor[x] = color;
		// // 如果你想做的更细致的话 可以把颜色值的R G B 拿到做响应的处理 笔者在这里就不做更多解释
		// int r = Color.red(color);
		// int g = Color.green(color);
		// int b = Color.blue(color);
		// int a = Color.alpha(color);
		// Log.i(TAG, "r=" + r + ",g=" + g + ",b=" + b);
		//
		// }
		// return true;
		// }
		// });
	}

	@Override
	protected void onStart() {
		super.onStart();
		isOnRouteActivity = true;
		// TODO 注册广播
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
		// TODO 注销广播
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (sensorManager != null) {// 注册监听器
		// sensorManager.registerListener(sensorEventListener,
		// sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		// SensorManager.SENSOR_DELAY_NORMAL);
		// // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		// }

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
				Toast.makeText(RouteMapActivity.this, "检测到摇晃，执行操作！",
						Toast.LENGTH_SHORT).show();
				Log.i(TAG, "检测到摇晃，执行操作！");
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		AudioManager mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);  
		
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);  
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);      
		float volume = streamVolumeCurrent/streamVolumeMax;  
		int streamID1 = 0;
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgswitch:
			if (openstate) {
				// 取消摇一摇
				imgswitch.setBackgroundResource(R.drawable.img_autoexplain);
				if (sensorManager != null) {// 取消监听器
					sensorManager.unregisterListener(sensorEventListener);
				}
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

			if (playstate) {
				// 暂停语音
				imgplay.setBackgroundResource(R.drawable.ic_play);
				//mSoundPool.unload(soundID);
				mSoundPool.pause(soundID);
				playstate = false;
			} else {
				// 播放语音
				imgplay.setBackgroundResource(R.drawable.ic_pause);
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// TODO Auto-generated method stub
						int streamID1 = mSoundPool.play(soundID, 1, 1, 1, 0, 1.0f);
					}
				}).start();
				
//				Intent it = new Intent(Intent.ACTION_VIEW);
//				File file = new File("file:///android_asset/1yindao.m4a");
//				//获取文件file的MIME类型 
//			    String type = getMIMEType(file); 
//				it.setDataAndType(
//						Uri.fromFile(file),
//						type);
//				startActivity(it);
				playstate = true;
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file
	 */ 
	private String getMIMEType(File file) { 
	     
	    String type="*/*"; 
	    String fName = file.getName(); 
	    //获取后缀名前的分隔符"."在fName中的位置。 
	    int dotIndex = fName.lastIndexOf("."); 
	    if(dotIndex < 0){ 
	        return type; 
	    } 
	    /* 获取文件的后缀名*/ 
	    String end=fName.substring(dotIndex,fName.length()).toLowerCase(); 
	    if(end=="")return type; 
	    //在MIME和文件类型的匹配表中找到对应的MIME类型。 
	    for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？ 
	        if(end.equals(MIME_MapTable[i][0])) 
	            type = MIME_MapTable[i][1]; 
	    }        
	    return type; 
	} 
}
