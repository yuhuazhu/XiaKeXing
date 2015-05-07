package com.xkx.goldenox;

import java.io.File;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class VideoFragment extends Fragment {
	private View rootView;// 缓存Fragment view
	private GifView GifView1;
	
	private MediaPlayer mediaPlayer;
	private String filename;
	private SurfaceView surfaceView;
	private final static String TAG = "VodeoPlayActivity";
	private int prosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_video, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		initUI(rootView);

		return rootView;
	}

	@SuppressWarnings("deprecation")
	private void initUI(View rootView) {
		
		surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceview);
		surfaceView.getHolder().setFixedSize(176, 144);// 设置分辨率
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 设置surfaceview不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前
		surfaceView.getHolder().addCallback(new SurceCallBack());// 对surface对象的状态进行监听
		mediaPlayer = new MediaPlayer();

		ButtonOnClikListiner buttonOnClikListinero = new ButtonOnClikListiner();
		ImageButton start = (ImageButton) rootView.findViewById(R.id.play);
		ImageButton pause = (ImageButton) rootView.findViewById(R.id.pause);
		start.setOnClickListener(buttonOnClikListinero);
		pause.setOnClickListener(buttonOnClikListinero);

	}

//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		if (mediaPlayer.isPlaying()) {
//			mediaPlayer.stop();
//		}
//		mediaPlayer.release();
//		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
//	}

	private final class ButtonOnClikListiner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
				Toast.makeText(getActivity(), "sd卡不存在",
						Toast.LENGTH_SHORT).show();
				return;
			}
			filename = "2793299.mp4";
			switch (v.getId()) {
			case R.id.play:
				play();
				break;
			case R.id.pause:
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
				} else {
					mediaPlayer.start();
				}
				break;

			}
		}
	}

	private void play() {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			mediaPlayer.reset();// 重置为初始状态
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置音乐流的类型
			mediaPlayer.setDisplay(surfaceView.getHolder());// 设置video影片以surfaceviewholder播放
			mediaPlayer.setDataSource(file.getAbsolutePath());// 设置路径
			mediaPlayer.prepare();// 缓冲
			mediaPlayer.start();// 播放
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	private final class SurceCallBack implements SurfaceHolder.Callback {
		/**
		 * 画面修改
		 */
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		/**
		 * 画面创建
		 */
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (prosition > 0 && filename != null) {
				play();
				mediaPlayer.seekTo(prosition);
				prosition = 0;
			}

		}

		/**
		 * 画面销毁
		 */
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mediaPlayer.isPlaying()) {
				prosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}
	}
}
