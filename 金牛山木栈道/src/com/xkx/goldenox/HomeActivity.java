package com.xkx.goldenox;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.SurfaceView;
import android.widget.EditText;

public class HomeActivity extends FragmentActivity {

	private final String TAG_FRAG_MENU = "com.xkx.goldenox.menu";
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction fragTrans = fragMgr.beginTransaction();
		// TODO Ìæ»»frament
		fragTrans.replace(R.id.suface_frame, new VideoFragment(), TAG_FRAG_MENU);
		

		fragTrans.replace(R.id.menu_frame, new BottomFragment(), TAG_FRAG_MENU);
		fragTrans.commit();
		
		
		
	}
}
