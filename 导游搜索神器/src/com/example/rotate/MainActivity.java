package com.example.rotate;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ImageView m_im;
	private boolean isStart;
	private boolean isBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m_im = (ImageView) findViewById(R.id.imageView1);
		m_im.setVisibility(View.INVISIBLE);
	}

	public void startAnimation(View v)
	{
		//更改状态
		isStart = !isStart;
		v.setBackgroundResource(isStart ? R.drawable.bt2 : R.drawable.bt1);
		final ImageButton button = (ImageButton) v;
		
		if(isStart)
		{
			AnimationSet animationSet = new AnimationSet(true);
			int width = m_im.getWidth();
			int height = m_im.getHeight();
			RotateAnimation animation = new RotateAnimation(0f,360f * 10, 0, 0); 
			animation.setDuration(20000);//设置动画持续时间 
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
				}
			});
		}
		else
		{
			m_im.clearAnimation();
			m_im.setVisibility(View.INVISIBLE);
		}
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
				m_im.postDelayed(new Runnable() {
					public void run() 
					{
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

}
