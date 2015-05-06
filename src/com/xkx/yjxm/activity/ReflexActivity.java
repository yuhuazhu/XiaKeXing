package com.xkx.yjxm.activity;

import java.util.ArrayList;

import com.xkx.yjxm.R;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

//映像
public class ReflexActivity extends Activity {
	private ArrayList<View> m_ViewList = new ArrayList<View>();
	private ArrayList<Integer> m_ImgIdX = new ArrayList<Integer>();
	private ArrayList<Integer> m_ImgIdY = new ArrayList<Integer>();
	private ViewPager pager;
	private int attractionsCount = 3;		//景点数
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_reflex);
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(new ViewPagerAdapter());
		for (int i = 0; i < 3; i++) {
			m_ImgIdX.add(R.drawable.x1 + i);
			m_ImgIdY.add(R.drawable.y1 + i);
			LayoutInflater layoutInflater = getLayoutInflater();
			View view = layoutInflater.inflate(R.layout.viewpager_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView_content);
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.viewpager_bg);
			layout.setBackgroundResource(m_ImgIdX.get(i));
			imageView.setBackgroundResource(m_ImgIdY.get(i));
			m_ViewList.add(view);
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reflex, menu);
		return true;
	}

}
