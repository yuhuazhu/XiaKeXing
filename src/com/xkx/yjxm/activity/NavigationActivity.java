package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.custom.MyView;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

//导航
public class NavigationActivity extends Activity {

	private MyView myView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_navigation);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth  = dm.widthPixels;
		int iHeight = dm.heightPixels;
		myView = new MyView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addContentView(myView, params);
		RelativeLayout layout = new RelativeLayout(this);
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.ic_navigation_mine);
		layout.addView(imageView);
//		layout.setBackgroundResource(R.drawable.y);
		myView.MoveTo(iWidth / 2, iHeight / 2);
		params.setMargins(iWidth / 2, iHeight / 2, 0, 0);
		imageView.setLayoutParams(params);
		addContentView(layout, params);
	}
	
	// 泡泡动画
	public void StartPaoPao() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth  = dm.widthPixels;
		int iHeight = dm.heightPixels;
		final int runW;
		final int runH;
		runW = iWidth * 20 / 100 ;
		runH = iHeight * 55 / 100 - 50;
		final ImageView spaceshipImage = (ImageView)findViewById(R.id.img_paopao);
		Animation hyperspaceJumpAnimation=AnimationUtils.loadAnimation(this, R.anim.anim_paopao);
		hyperspaceJumpAnimation.setFillAfter(true);
		hyperspaceJumpAnimation.setAnimationListener(new AnimationListener() {
			

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
				spaceshipImage.clearAnimation();
				spaceshipImage.setBackgroundResource(R.drawable.ic_paopaos);
				int left = spaceshipImage.getLeft();
				left += runW;
				int top = spaceshipImage.getTop();
				top -= runH;
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.setMargins(left, top, 0, 0);
				spaceshipImage.setLayoutParams(param);
				spaceshipImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						spaceshipImage.setVisibility(View.INVISIBLE);
						
					}
				});
			}
		});
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		
//			AnimationSet
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

}
