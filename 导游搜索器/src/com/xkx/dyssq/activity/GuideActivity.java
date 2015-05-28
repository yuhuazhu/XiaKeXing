package com.xkx.dyssq.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkx.dyssq.R;

public class GuideActivity extends Activity implements OnClickListener {

	private Button button1;
	private Button button2;
	private Button button3;
	GradientDrawable drawable;
	private RelativeLayout backlay;
	private RelativeLayout daolayout;
	private RelativeLayout xclayout;
	private RelativeLayout yklayout;
	private ImageView imgzk;
	private Boolean zkstate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		initUI();
	}

	private void initUI() {
		imgzk = (ImageView) findViewById(R.id.imgzk);
		backlay = (RelativeLayout) findViewById(R.id.backlay);
		daolayout = (RelativeLayout) findViewById(R.id.daolayout);
		xclayout = (RelativeLayout) findViewById(R.id.xclayout);
		yklayout = (RelativeLayout) findViewById(R.id.yklayout);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		backlay.setOnClickListener(this);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		drawable = new GradientDrawable();
		drawable.setShape(GradientDrawable.RECTANGLE); // 画框
		drawable.setStroke(2, Color.parseColor("#ff4081")); // 边框粗细及颜色
		drawable.setColor(Color.WHITE); // 边框内部颜色
		button2.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
		button3.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
		imgzk.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int topleftx = x - 10;

				// left, top, right, bottom
				int y = (int) event.getY();
				int toplefty = y - 10;
				// 右上角
				int topRightx = x + 10;
				int topRighty = y - 10;

				// 左下角
				int bottomleftx = x - 10;
				int bottomlefty = y + 10;

				// 右上角
				int bottomRightx = x + 10;
				int bottomRighty = y + 10;
				if (event.getAction() == MotionEvent.ACTION_UP) {

					String title = "";
					// Toast.makeText(GuideActivity.this, str,
					// Toast.LENGTH_SHORT).show();

					if (x >= 611 && x <= 1010 && y >= 43 && y <= 210) {
						if (zkstate) {
							imgzk.setBackgroundResource(R.drawable.img_zk);
							LayoutParams lp = new LayoutParams(900, 5800);
							imgzk.setLayoutParams(lp);
							zkstate = false;
						} else {
							imgzk.setBackgroundResource(R.drawable.img_ss);
							LayoutParams lp = new LayoutParams(900, 900);
							imgzk.setLayoutParams(lp);
							zkstate = true;

						}
					}

				}

				return true;
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guide, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			daolayout.setVisibility(View.VISIBLE);
			xclayout.setVisibility(View.GONE);
			yklayout.setVisibility(View.GONE);
			button1.setBackgroundColor(Color.parseColor("#ff4081"));
			button1.setTextColor(Color.parseColor("#ffffff"));
			button2.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
			button2.setTextColor(Color.parseColor("#ff4081"));
			button3.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
			button3.setTextColor(Color.parseColor("#ff4081"));
			break;
		case R.id.button2:
			daolayout.setVisibility(View.GONE);
			xclayout.setVisibility(View.VISIBLE);
			yklayout.setVisibility(View.GONE);
			button2.setBackgroundColor(Color.parseColor("#ff4081"));
			button2.setTextColor(Color.parseColor("#ffffff"));
			button1.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
			button1.setTextColor(Color.parseColor("#ff4081"));
			button3.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
			button3.setTextColor(Color.parseColor("#ff4081"));
			break;
		case R.id.button3:
			daolayout.setVisibility(View.GONE);
			xclayout.setVisibility(View.GONE);
			yklayout.setVisibility(View.VISIBLE);
			button3.setBackgroundColor(Color.parseColor("#ff4081"));
			button3.setTextColor(Color.parseColor("#ffffff"));
			button1.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
			button1.setTextColor(Color.parseColor("#ff4081"));
			button2.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
			button2.setTextColor(Color.parseColor("#ff4081"));
			break;
		case R.id.backlay:
			finish();
			break;
		default:
			break;
		}
	}

}
