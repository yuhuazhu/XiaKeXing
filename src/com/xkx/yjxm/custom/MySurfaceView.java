package com.xkx.yjxm.custom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.xkx.yjxm.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.ImageView;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {

	public static final int TIME_IN_FRAME = 50;

	Paint mPaint = null;
	Paint mTextPaint = null;
	SurfaceHolder mSurfaceHolder = null;
	private int lefts;
	private int tops;
	private ImageView spaceshipImage;
	boolean mRunning = false;

	Canvas mCanvas = null;

	private Path mPath;

	private Bitmap background;
	
	private float mPosX, mPosY;

	private Bitmap bitmap;

	public MySurfaceView(Context context) {
		super(context);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		background = BitmapFactory.decodeResource(getResources(), R.drawable.img_bg);  
//		Bitmap mBitmapDisplayed = Bitmap.createScaledBitmap(copy, copy.getWidth(), copy.getHeight(), false);//这里就是用原来的bitmap生成一个mutable的Bitmap，然后用这个mutable的Bitmap去构造CANVAS
//		mCanvas = new Canvas(bitmap);
//		mCanvas = new Canvas();
		mPaint = new Paint();
		mPaint.setColor(Color.YELLOW);

		mPaint.setAntiAlias(true);

		mPaint.setStyle(Paint.Style.STROKE);

		mPaint.setStrokeCap(Paint.Cap.ROUND);

		mPaint.setStrokeWidth(6);

//		mCanvas.drawBitmap(bitmap, 200 , 500 , mPaint);
	        
		mPath = new Path();

		mTextPaint = new Paint();

		mTextPaint.setColor(Color.BLACK);

		mTextPaint.setTextSize(15);
		

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			mRunning = false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
//			mPosX = x;
//			mPosY = y;
//			mPath.quadTo(mPosX, mPosY, x, y);
//			mPath.moveTo(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
//			mPath.quadTo(mPosX, mPosY, x, y);
			break;
		case MotionEvent.ACTION_UP:
			// mPath.reset();
//			mPath.quadTo(mPosX, mPosY, x, y);
			mPosX = x;
			mPosY = y;
			break;
		}
		// 记录当前触摸点得当前得坐标
		mPosX = x;
		mPosY = y;
		return super.onTouchEvent(event);
	}

	private void onDraw() {
		mCanvas.drawColor(Color.WHITE);
		// 绘制曲线
//		mCanvas.drawPath(mPath, mPaint);
		mCanvas.drawText("当前触笔X：" + mPosX, 0, 20, mTextPaint);
		mCanvas.drawText("当前触笔Y:" + mPosY, 0, 40, mTextPaint);
		
//		mCanvas.drawBitmap(background, 0, 0, null);
		mCanvas.drawBitmap(background, 0, 0, null);
		mCanvas.drawPath(mPath, mPaint);
		mCanvas.save(Canvas.ALL_SAVE_FLAG);  
        // 存储新合成的图片  
		mCanvas.restore();
	}

	@SuppressLint("WrongCall")
	public void run() {
		// TODO Auto-generated method stub
		while (mRunning) {
			long startTime = System.currentTimeMillis();
			synchronized (mSurfaceHolder) {
				mCanvas = mSurfaceHolder.lockCanvas();
				onDraw();
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}
			long endTime = System.currentTimeMillis();
			int diffTime = (int) (endTime - startTime);
			while (diffTime <= TIME_IN_FRAME) {
				diffTime = (int) (System.currentTimeMillis() - startTime);
				Thread.yield();
			}
		}
	}
	
	public float GetX()
	{ 
		return mPosX;
	}
	
	public float GetY()
	{ 
		return mPosY;
	}
	
	public void QuadTo(float x,float y, float toX, float toY)
	{
		mPath.quadTo(x, y, toX, toY);
		mPath.moveTo(toX, toY);
	}
	
	public void MoveTo(float x,float y)
	{
		mPath.moveTo(x, y);
	}
	
	public void SetPoint(int x,int y)
	{
		mPosX = x;
		mPosY = y;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mRunning = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mRunning = false;
	}

}