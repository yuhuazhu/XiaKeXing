package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.custom.MySurfaceView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

//导航
public class NavigationActivity extends Activity {

	private MySurfaceView mySurfaceView;
	private ImageView imageView;
	private int lefts;
	private int tops;
	
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
		mySurfaceView = new MySurfaceView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addContentView(mySurfaceView, params);
		RelativeLayout layout = new RelativeLayout(this);
		imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.ic_navigation_mine);
		layout.addView(imageView);
//		layout.setBackgroundResource(R.drawable.y);
		Bitmap b = ((BitmapDrawable)imageView.getBackground()).getBitmap();
		int width = b.getWidth();
		int height = b.getHeight();
		mySurfaceView.MoveTo(iWidth / 2, iHeight / 2);
		params.setMargins(iWidth / 2 - width / 2, iHeight / 2 - height, 0, 0);
		imageView.setLayoutParams(params);
		addContentView(layout, params);
	}
	
	// 人物动画
	public void StartPaoPao(int x, int y) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int iWidth  = dm.widthPixels;
		int iHeight = dm.heightPixels;
		final int runW;
		final int runH;
		runW = iWidth * 20 / 100 ;
		runH = iHeight * 55 / 100 - 50;
		
		 // 创建一个AnimationSet对象   
        AnimationSet animationSet = new AnimationSet(true);  
        // j加速播放   
//        animationSet.setInterpolator(new AccelerateInterpolator());  
        // //创建一个AnimationSet对象淡出旋转二合一   
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);

        // 将alphaAnimation对象添加到animationSet中   
        animationSet.addAnimation(translateAnimation);  
        // 显示的时间为1s   
        translateAnimation.setDuration(1000);  
        // 开始执行动画   
        imageView.startAnimation(animationSet);  
        // 设置重复的次数   
//        animationSet.setRepeatCount(4);  

		Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_paopao);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			

			@Override
			public void onAnimationStart(Animation animation) {
				final int left = imageView.getLeft();
				final int top = imageView.getTop();
				imageView.postDelayed(new Runnable() {
					

					public void run() {
						if(lefts == 0)
						{
							mySurfaceView.MoveTo(left, top);
							lefts = left;
							tops = top;
							Log.e("0lefts="+ lefts, "0tops="+tops);
						}
						lefts += 5;
						tops -= 25;
						mySurfaceView.QuadTo(left, top, lefts, tops);
						Log.e("lefts="+ lefts, "tops="+tops);
						if(lefts < left + 200)
							imageView.postDelayed(this, 100);
						
					}
				},100);
				  
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				imageView.clearAnimation();
//				spaceshipImage.setBackgroundResource(R.drawable.ic_paopaos);
				int left = imageView.getLeft();
				left += 200;
				int top = imageView.getTop();
				top -= 1000;
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				param.setMargins(left, top, 0, 0);
				imageView.setLayoutParams(param);
				imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						imageView.setVisibility(View.INVISIBLE);
						
					}
				});
			}
		});
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}

}
