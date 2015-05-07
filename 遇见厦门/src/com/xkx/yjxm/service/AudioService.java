package com.xkx.yjxm.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class AudioService extends Service {
	private MediaPlayer mediaPlayer;
	// 是不是已经导入了资源
	private boolean isFrist;

	public AudioService() {
	}

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayer.release();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mediaPlayer.release();
		return super.onUnbind(intent);
	}

	public void play(Uri uri) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(this, uri);
			mediaPlayer.setVolume(1, 1);
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					isFrist = true;
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				listener.onPlayComplete();
			}
		});
	}

	public class AudioBinder extends Binder {
		public void setOnPlayCompleteListener(OnPlayCompleteListener lis) {
			listener = lis;
		}

		// 初始化并播放
		public void audioPlay(Uri uri) {
			play(uri);
		}

		// 开始播放
		public void audioStart() {
			mediaPlayer.start();
		}

		// 暂停播放
		public void audioPause() {
			mediaPlayer.pause();
			// 设置为true后，暂停服务后不会被kill掉
			stopForeground(true);
		}

		// 停止播放
		public void audioStop() {
			mediaPlayer.stop();
		}

		// 是否在播放
		public boolean audioIsPlaying() {
			return mediaPlayer.isPlaying();
		}

		// 是否已经导入资源
		public boolean audioIsFrist() {
			return isFrist;
		}

		// 播放下一个
		public void audioPlayNext(Uri uri) {
			isFrist = false;
			play(uri);
		}
	}

	private OnPlayCompleteListener listener;

	AudioBinder binder = new AudioBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public interface OnPlayCompleteListener {
		public void onPlayComplete();
	}
}
