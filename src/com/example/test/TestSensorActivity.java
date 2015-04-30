package com.example.test;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class TestSensorActivity extends Activity {
	private SensorManager sensorManager; 
    private Vibrator vibrator; 
 
    private static final String TAG = "TestSensorActivity"; 
    private static final int SENSOR_SHAKE = 10; 
 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.main); 
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); 
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); 
    } 
 
    @Override 
    protected void onResume() { 
        super.onResume(); 
        if (sensorManager != null) {// 注册监听器 
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL); 
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率 
        } 
    } 
 
    @Override 
    protected void onStop() { 
        super.onStop(); 
        if (sensorManager != null) {// 取消监听器 
            sensorManager.unregisterListener(sensorEventListener); 
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
            Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z); 
            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。 
            int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了 
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) { 
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
                Toast.makeText(TestSensorActivity.this, "检测到摇晃，执行操作！", Toast.LENGTH_SHORT).show(); 
                Log.i(TAG, "检测到摇晃，执行操作！"); 
                break; 
            } 
        } 
 
    }; 

}
