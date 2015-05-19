package com.xkx.yjxm.activity;

import com.xkx.yjxm.R;
import com.xkx.yjxm.R.layout;
import com.xkx.yjxm.R.menu;
import com.xkx.yjxm.utils.PreferenceUtil;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class SettingsActivity extends BaseActivity {

	private boolean b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		String string = PreferenceUtil.getString("language", "zh");
		b = PreferenceUtil.getBoolean("change", false);
		if(string.contains("zh")){
			toggleButton.setChecked(true);
		}
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtil.commitString("language", isChecked ? "zh" : "en");
				PreferenceUtil.commitBoolean("change", !b);
			}
		});
	}


}
