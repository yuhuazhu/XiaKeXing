package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bean.FastAdpater;
import com.example.test.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

//左侧界面
public class LeftActivity extends Activity implements OnItemClickListener{

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
		setContentView(R.layout.activity_left); 
		ListView listView = (ListView) findViewById(R.id.left_listView1);
		listItems = getListItems();   
		FastAdpater = new FastAdpater(this,listItems);    
		listView.setAdapter(FastAdpater);
		listView.setOnItemClickListener(this);
	}
	
	public void StartMain(View v){
    	finish();
    }
	
	public void StartPhoto(View v){
    	Intent intent = new Intent();
    	intent.setClass(this, PhotoActivity.class);
    	startActivity(intent);
    }
	
	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < title.length; i++) 
		{
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
//		Toast.makeText(getApplication(), ""+position, Toast.LENGTH_SHORT).show();
		if(position == 1)
		{
			intent.setClass(getApplication(), PhotoActivity.class);
			startActivity(intent);
		}
		else if(position == 2)
		{
			intent.setClass(getApplication(), MassageActivity.class);
			startActivity(intent);
		}
		else if(position == 3)
		{
			intent.setClass(getApplication(), GuideActivity.class);
			startActivity(intent);
		}
		
	}

}
