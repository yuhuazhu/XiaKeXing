package com.xkx.dyssq.activity;

import com.xkx.dyssq.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private boolean isBack;
	private boolean isStart;
	private ImageView m_im1;
	private ImageView m_im2;
	private LinearLayout m_ll;
	private int animationCount = 5;
	private int animationCount2 = 3;
	private SensorManager sensorManager;
	private final int MSG_SENSOR_SHAKE = 10;
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); 
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); 
		m_im1 = (ImageView) findViewById(R.id.imageView1);
		m_im2 = (ImageView) findViewById(R.id.imageView2);
		m_ll = (LinearLayout) findViewById(R.id.linearLayout);
		m_ll.setVisibility(View.INVISIBLE);
		m_ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = null;
				intent = new Intent(MainActivity.this, GuideActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//注销
		sensorManager.unregisterListener(sensorEventListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//注册
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
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
				Log.i("123123123123", "x轴" + x + "；y轴" + y + "；z轴" + z);
			}
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				vibrator.vibrate(200);
				Toast.makeText(MainActivity.this, "yao", Toast.LENGTH_SHORT).show();
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
			// BRTBeacon beacon = bleBinder.getProximityBeacon();
			// if (beacon != null) {
			// trigger(beacon);
			// }
			// Intent intent = null;
			// intent = new Intent(MainActivity.this, GuideActivity.class);
			// startActivity(intent);
			startPeople(m_im2);
		}
	};

	public void startPeople(View v)
	{
		//更改状态
		isStart = !isStart;
		v.setClickable(false);
		m_ll.setVisibility(View.INVISIBLE);
		final ImageView im = (ImageView) v;
		
		if(isStart)
		{
			m_im1.setBackgroundResource(R.drawable.shake2);
			m_im2.setBackgroundResource(R.drawable.people2);
			
			final AnimationSet animationSet = new AnimationSet(true);
			
			TranslateAnimation animation1 = new TranslateAnimation(0, 30, 0, 0);
			TranslateAnimation animation2 = new TranslateAnimation(0, -60, 0, 0);
			TranslateAnimation animation3 = new TranslateAnimation(0, 60, 0, 0);
			TranslateAnimation animation4 = new TranslateAnimation(0, -30, 0, 0);
			
			//设置动画持续时间 
			animation1.setDuration(70);
			animation2.setDuration(140);
			animation3.setDuration(140);
			animation4.setDuration(70);
			
			//延迟播放
//			animation1.setStartOffset();  
			animation2.setStartOffset(70);  
			animation3.setStartOffset(140);  
			animation4.setStartOffset(140);  
			
			animationSet.addAnimation(animation1);
			animationSet.addAnimation(animation2);
			animationSet.addAnimation(animation3);
			animationSet.addAnimation(animation4);
			
			animationSet.setInterpolator(new LinearInterpolator());
			m_im2.startAnimation(animationSet);
			animationSet.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation)
				{
//					m_im2.clearAnimation();
					if(animationCount-- != 0)
					{
						m_im2.startAnimation(animationSet);
					}
					else
					{
						m_im2.clearAnimation();
						isStart = false;
						m_im1.setBackgroundResource(R.drawable.shake1);
						m_im2.setBackgroundResource(R.drawable.people1);
						m_im2.setClickable(true);
						animationCount = 5;
						startGuide();
					}
				}
			});
		}
	}
	
	public void startGuide()
	{
		m_ll.setVisibility(View.VISIBLE);
		final AnimationSet animationSet = new AnimationSet(true);
		
		TranslateAnimation animation1 = new TranslateAnimation(0, 60, 0, 0);
		TranslateAnimation animation2 = new TranslateAnimation(0, -90, 0, 0);
		TranslateAnimation animation3 = new TranslateAnimation(0, 90, 0, 0);
		TranslateAnimation animation4 = new TranslateAnimation(0, -60, 0, 0);
		
		//设置动画持续时间 
		animation1.setDuration(40);
		animation2.setDuration(80);
		animation3.setDuration(80);
		animation4.setDuration(40);
		
		//延迟播放
//			animation1.setStartOffset();  
		animation2.setStartOffset(40);  
		animation3.setStartOffset(80);  
		animation4.setStartOffset(80);  
		
		animationSet.addAnimation(animation1);
		animationSet.addAnimation(animation2);
		animationSet.addAnimation(animation3);
		animationSet.addAnimation(animation4);
			
		animationSet.setInterpolator(new LinearInterpolator());
		m_ll.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation)
			{
//					m_im2.clearAnimation();
				if(animationCount2-- != 0)
				{
					m_ll.startAnimation(animationSet);
				}
				else
				{
					m_ll.clearAnimation();
					isStart = false;
					animationCount2 = 3;
				}
			}
		});
	}
	
	//跳转页面
	public void startIn(View v) 
	{
		Intent intent = null;
		intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			if(isBack){
				finish();
				System.exit(0);
			}else{
				isBack = true;
				Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				m_im1.postDelayed(new Runnable() {
					public void run() 
					{
						isBack = false;
					}
				}, 2000);
			}
			
		}
		return false;
	}
}
