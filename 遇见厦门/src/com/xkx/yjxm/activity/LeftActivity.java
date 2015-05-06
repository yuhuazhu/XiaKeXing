package com.xkx.yjxm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xkx.yjxm.R;
import com.xkx.yjxm.adpater.FastAdpater;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

//左侧界面
public class LeftActivity extends Activity implements OnItemClickListener {

	private Integer[] imgeIDs = { R.drawable.ic_left_ticket,
			R.drawable.ic_left_photo, R.drawable.ic_left_massage,
			R.drawable.ic_left_sos, R.drawable.ic_left_advice,
			R.drawable.ic_left_about, R.drawable.ic_left_setting };
	private String[] title = { "门票购买", "照片冲印", "全身按摩", "紧急救援", "资讯投诉", "关于我们",
			"设置" };
	private List<Map<String, Object>> listItems;
	private FastAdpater FastAdpater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 移除ActionBar，在setContent之前调用下面这句，保证没有ActionBar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_left);
		ListView listView = (ListView) findViewById(R.id.left_listView1);
		listItems = getListItems();
		FastAdpater = new FastAdpater(this, listItems);
		listView.setAdapter(FastAdpater);
		listView.setOnItemClickListener(this);
	}

	public void StartMain(View v) {
		finish();
		overridePendingTransition(R.anim.anim_main_in, R.anim.anim_main_out);
		// AnimationSet animationSet = new AnimationSet(true);
		// TranslateAnimation translateAnimation = new
		// TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
		// Animation.RELATIVE_TO_SELF, 0f,Animation.RELATIVE_TO_SELF,
		// 2.0f,Animation.RELATIVE_TO_SELF, 0f);
		// translateAnimation.setDuration(2000);
		// animationSet.addAnimation(translateAnimation);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			finish();
			overridePendingTransition(R.anim.anim_main_in, R.anim.anim_main_out);
		}
		return false;
	}

	public void StartPhoto(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PhotoActivity.class);
		startActivity(intent);
	}

	public void Start(View v) {
		// 通过包名获取要跳转的app，创建intent对象

		Intent intent = getPackageManager().getLaunchIntentForPackage(
				"com.example.test");
		// Intent intent = new Intent();
		// intent.setAction("xkx");
		// intent.setComponent("com.example.test");

	}

	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", imgeIDs[i]);
			map.put("title", title[i]);
			listItems.add(map);
		}
		return listItems;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.left, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		// Toast.makeText(getApplication(), ""+position,
		// Toast.LENGTH_SHORT).show();
		if (position == 1) {
			// 跳转到照片界面
			intent.setClass(getApplication(), PhotoWashActivity.class);
			startActivity(intent);
		}

		else if (position == 2) 
		{
			// 跳转到按摩界面
			// 通过包名获取要跳转的app，创建intent对象
			intent = getPackageManager().getLaunchIntentForPackage(
					"com.ebwing.mass");
			// 这里如果intent为空，就说名没有安装要跳转的应用嘛
			if (intent != null) 
			{
				// 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
				intent.putExtra("name", "XiaKeXing");
				intent.putExtra("app", "123456");
				startActivity(intent);
			} 
			else 
			{
				// 没有安装要跳转的app应用，提醒一下
//				Toast.makeText(getApplicationContext(), "哟，赶紧下载安装这个APP吧",
//						Toast.LENGTH_LONG).show();
				intent = new Intent();      
				intent.setAction("android.intent.action.VIEW");    
	            Uri content_url = Uri.parse("http://www.ebwing.com/download/appindex.do#");   
	            intent.setData(content_url);  
	            startActivity(intent);

			}
			// intent.setAction("")
			// intent.setClass(getApplication(), MassageActivity.class);
			// startActivity(intent);
		}

		else if (position == 3) {
			// 跳转到SOS界面
			intent.setClass(getApplication(), GuideActivity.class);
			startActivity(intent);
		}

	}

}
