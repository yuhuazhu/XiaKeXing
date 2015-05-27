package com.example.rotate;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView m_im;
	private boolean isStart;
	private boolean isBack;
	private TextView textView1;
	private CircleImageView img_zhang;
	private CircleImageView img_li;
	private CircleImageView img_liu;
	private CircleImageView img_zhou;
	private RelativeLayout daolayout;
	private CircleImageView img_head;
	private ImageButton imageButton1;
	private RelativeLayout headlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
	}

	public void initUI() {
		headlay = (RelativeLayout) findViewById(R.id.headlay);
		imageButton1= (ImageButton) findViewById(R.id.imageButton1);
		CircleImageView cr = new CircleImageView(this);
		android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
				80, 80);
		lp.rightMargin = 40;
		lp.topMargin = 400;
		lp.addRule(RelativeLayout.LEFT_OF,imageButton1.getId());
		cr.setLayoutParams(lp);
		cr.setBackgroundResource(R.drawable.img_zhang);
		headlay.addView(cr);
		// <com.example.rotate.CircleImageView
		// android:id="@+id/img_zhang"
		// android:layout_width="40dp"
		// android:layout_height="40dp"
		// android:layout_marginRight="40dp"
		// android:layout_toLeftOf="@+id/imageButton1"
		// android:layout_marginTop="130dp"
		//
		// android:background="@drawable/img_zhang" />

		m_im = (ImageView) findViewById(R.id.imageView1);
		m_im.setVisibility(View.INVISIBLE);
		textView1 = (TextView) findViewById(R.id.textView1);
		daolayout = (RelativeLayout) findViewById(R.id.daolayout);
		img_head = (CircleImageView) findViewById(R.id.img_head);
		img_zhang = (CircleImageView) findViewById(R.id.img_zhang);
		img_li = (CircleImageView) findViewById(R.id.img_li);
		img_liu = (CircleImageView) findViewById(R.id.img_liu);
		img_zhou = (CircleImageView) findViewById(R.id.img_zhou);
		img_zhang.setOnClickListener(this);
		img_li.setOnClickListener(this);
		img_liu.setOnClickListener(this);
		img_zhou.setOnClickListener(this);
	}

	public void startAnimation(View v) {
		// 更改状态
		isStart = !isStart;
		v.setBackgroundResource(isStart ? R.drawable.bt2 : R.drawable.bt1);
		final ImageButton button = (ImageButton) v;

		if (isStart) {
			AnimationSet animationSet = new AnimationSet(true);
			int width = m_im.getWidth();
			int height = m_im.getHeight();
			RotateAnimation animation = new RotateAnimation(0f, 360f * 10, 0, 0);
			animation.setDuration(5000);// 设置动画持续时间
			animationSet.addAnimation(animation);
			animationSet.setInterpolator(new LinearInterpolator());
			m_im.startAnimation(animationSet);
			m_im.setVisibility(View.VISIBLE);
			animationSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					isStart = false;
					m_im.clearAnimation();
					m_im.setVisibility(View.INVISIBLE);
					button.setBackgroundResource(R.drawable.bt1);
					textView1.setText("已搜索到附近4位导游,点击头像查看信息");
					img_zhang.setVisibility(View.VISIBLE);
					img_li.setVisibility(View.VISIBLE);
					img_liu.setVisibility(View.VISIBLE);
					img_zhou.setVisibility(View.VISIBLE);
				}
			});
		} else {
			m_im.clearAnimation();
			m_im.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (isBack) {
				finish();
				System.exit(0);
			} else {
				isBack = true;
				Toast.makeText(MainActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				m_im.postDelayed(new Runnable() {
					public void run() {
						isBack = false;
					}
				}, 2000);
			}

		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		LayoutParams para;

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_zhang:
			daolayout.setVisibility(View.VISIBLE);
			img_head.setBackgroundResource(R.drawable.img_zhang);
			para = img_zhang.getLayoutParams();
			para.height = 70;
			para.width = 70;
			img_zhang.setLayoutParams(para);
			break;
		case R.id.img_li:
			daolayout.setVisibility(View.VISIBLE);
			img_head.setBackgroundResource(R.drawable.img_li);
			para = img_li.getLayoutParams();
			para.height = 70;
			para.width = 70;
			img_li.setLayoutParams(para);
			break;
		case R.id.img_liu:
			daolayout.setVisibility(View.VISIBLE);
			img_head.setBackgroundResource(R.drawable.img_liu);
			para = img_liu.getLayoutParams();
			para.height = 70;
			para.width = 70;
			img_liu.setLayoutParams(para);
			break;
		case R.id.img_zhou:
			daolayout.setVisibility(View.VISIBLE);
			img_head.setBackgroundResource(R.drawable.img_zhou);
			para = img_zhou.getLayoutParams();
			para.height = 70;
			para.width = 70;
			img_zhou.setLayoutParams(para);
			break;
		default:
			break;
		}
	}

}
