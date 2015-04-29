package com.xkx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class imgUtils {
	public static void onLoadImage(final URL bitmapUrl,
			final OnLoadImageListener onLoadImageListener) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				onLoadImageListener.OnLoadImage((Bitmap) msg.obj, null);
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				URL imageUrl = bitmapUrl;
				try {
					HttpURLConnection conn = (HttpURLConnection) imageUrl
							.openConnection();
					InputStream inputStream = conn.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					Message msg = new Message();
					msg.obj = bitmap;
					handler.sendMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();

	}

	public interface OnLoadImageListener {
		public void OnLoadImage(Bitmap bitmap, String bitmapPath);
	}

}
