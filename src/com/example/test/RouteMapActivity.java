package com.example.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class RouteMapActivity extends Activity {
	private Bitmap bitmap;
	int mBitmapWidth = 0;
	int mBitmapHeight = 0;
	int mArrayColor[] = null;
	int mArrayColorLengh = 0;
	private String TAG = "RouteMapActivity";
	private ImageView imageView;
	private TextView textView1;
	private TextView TextView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routemap);
		imageView = (ImageView) findViewById(R.id.imageView1);
		textView1 = new TextView(this);
		TextView2 = (TextView) findViewById(R.id.TextView2);
		BitmapDrawable db = (BitmapDrawable) getResources().getDrawable(
				R.drawable.img_routemap);
		bitmap = big(db.getBitmap());

		LinearLayout l = new LinearLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		l.addView(textView1);
		addContentView(l, params);

		imageView.setImageBitmap(bitmap);
		// http://xys289187120.blog.51cto.com/3361352/657590
		mBitmapWidth = bitmap.getWidth();
		mBitmapHeight = bitmap.getHeight();
		mArrayColorLengh = mBitmapWidth * mBitmapHeight;
		mArrayColor = new int[mArrayColorLengh];

		Log.i(TAG, "图片大小：mBitmapWidth=" + mBitmapWidth + ",mBitmapHeight="
				+ mBitmapHeight + ",mArrayColorLengh=" + mArrayColorLengh);
		int count = 0;
		for (int i = 0; i < mBitmapHeight; i++) {
			for (int j = 0; j < mBitmapWidth; j++) {
				// 获得Bitmap 图片中每一个点的color颜色值
				int color = bitmap.getPixel(j, i);
				// 将颜色值存在一个数组中 方便后面修改
				mArrayColor[count] = color;

				count++;
			}
		}

		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				// left, top, right, bottom
				int y = (int) event.getY();

				textView1.setPadding(x-50, y-50, 0, 0);
				// LayoutParams lp = new lay
				// textView1.setLayoutParams())
				if (event.getAction() == MotionEvent.ACTION_UP) {
					textView1.setText("x=" + x + ",y=" + y);
					int color = bitmap.getPixel(x, y);
					mArrayColor[x] = color;
					// 如果你想做的更细致的话 可以把颜色值的R G B 拿到做响应的处理 笔者在这里就不做更多解释
					int r = Color.red(color);
					int g = Color.green(color);
					int b = Color.blue(color);
					int a = Color.alpha(color);
					Log.i(TAG, "r=" + r + ",g=" + g + ",b=" + b);
					TextView2.setText("我是1点");
				}
				return true;
			}
		});
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
}
