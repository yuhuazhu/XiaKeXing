package com.xkx.goldenox;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.xkx.goldenox.utils.Utils;

public class HomeActivity extends FragmentActivity {

	private final String TAG_FRAG_MENU = "com.xkx.goldenox.menu";
    
    private RelativeLayout toplay;
    private FrameLayout suface_frame;
    private FrameLayout menu_frame;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        initUI();
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction fragTrans = fragMgr.beginTransaction();
		// TODO Ìæ»»frament
		fragTrans.replace(R.id.suface_frame, new VideoFragment(), TAG_FRAG_MENU);
		

		fragTrans.replace(R.id.menu_frame, new BottomFragment(), TAG_FRAG_MENU);
		fragTrans.commit();
		
		
		
	}
	private void initUI()
	{
		toplay = (RelativeLayout) findViewById(R.id.toplay);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(Utils.px2dip(1080),Utils.px2dip(100));
		
		toplay.setLayoutParams(param);
		
		suface_frame = (FrameLayout) findViewById(R.id.suface_frame);
		FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(Utils.px2dip(1080),Utils.px2dip(507));
	
		suface_frame.setLayoutParams(param2);
		
		menu_frame = (FrameLayout) findViewById(R.id.menu_frame);
		FrameLayout.LayoutParams param3 = new FrameLayout.LayoutParams(Utils.px2dip(1080),Utils.px2dip(1313));
		menu_frame.setLayoutParams(param3);
		
		
		
	}
}
