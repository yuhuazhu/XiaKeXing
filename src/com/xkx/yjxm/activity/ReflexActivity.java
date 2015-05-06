package com.xkx.yjxm.activity;

import java.util.ArrayList;

import com.xkx.yjxm.R;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

//映像
public class ReflexActivity extends Activity {
	private ArrayList<View> m_ViewList = new ArrayList<View>();
	private ArrayList<Integer> m_ImgIdX = new ArrayList<Integer>();
	private ArrayList<Integer> m_ImgIdY = new ArrayList<Integer>();
	private ViewPager pager;
	private int attractionsCount = 3;		//景点数
	private ImageView m_imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_reflex);
		m_imageView = (ImageView) findViewById(R.id.imageView_tip);
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(new ViewPagerAdapter());
		for (int i = 0; i < 3; i++) {
			m_ImgIdX.add(R.drawable.reflex_bg_1 + i);
			m_ImgIdY.add(R.drawable.reflex_text_1 + i);
			LayoutInflater layoutInflater = getLayoutInflater();
			View view = layoutInflater.inflate(R.layout.viewpager_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView_content);
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.viewpager_bg);
			layout.setBackgroundResource(m_ImgIdX.get(i));
			imageView.setBackgroundResource(m_ImgIdY.get(i));
			m_ViewList.add(view);
		}
		StartTip();
	}
	
	// 动画 
		public void StartTip() {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int iWidth  = dm.widthPixels;
			int iHeight = dm.heightPixels;
			TranslateAnimation animation = new TranslateAnimation(iWidth / 5, 0 - iWidth / 5, 0, 0);
			animation.setDuration(3000);
			m_imageView.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {
				
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
					m_imageView.setVisibility(View.INVISIBLE);
					
				}
			});
		}
	
	class ViewPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return attractionsCount;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(m_ViewList.get(position));
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(m_ViewList.get(position));
			return m_ViewList.get(position);
		}

		
	}
	
	
	//返回
    public void Back(View v){
    	finish();
    }
    //隐藏
    public void hide(View v){
    	v.setVisibility(View.GONE);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reflex, menu);
		return true;
	}

}
