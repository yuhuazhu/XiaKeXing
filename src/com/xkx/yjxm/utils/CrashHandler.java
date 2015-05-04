package com.xkx.yjxm.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		logToFile(thread, ex);
	}

	private static CrashHandler handler;

	private CrashHandler() {

	}

	public static CrashHandler getInstance() {
		synchronized (CrashHandler.class) {
			if (handler == null) {
				handler = new CrashHandler();
			}
			return handler;
		}
	}

	public void logToFile(Thread thread, Throwable ex) {
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Log.e("yjxm", ex.getLocalizedMessage());
			return;
		}
		File sdcard = Environment.getExternalStorageDirectory();
		File f = null;
		String path = sdcard.getAbsolutePath() + File.separator + "yjxm";
		File dir = new File(path);
		if (!dir.exists()) {
			try {
				dir.mkdirs();
				f = new File(path + File.separator + "ex.log");
				f.createNewFile();
			} catch (IOException e) {
				Log.e("yjxm", e.getLocalizedMessage());
				return;
			}
		}

		FileWriter writer = null;
		try {
			writer = new FileWriter(f, true);
			StringBuffer sb = new StringBuffer();
			StackTraceElement[] trace = ex.getStackTrace();
			sb.append(ex.getLocalizedMessage() + "\n");
			sb.append(trace[0] + "\n");
			sb.append(trace[1] + "\n");
			sb.append("---------------------------------\n");
			writer.write(sb.toString());
		} catch (IOException e) {
			Log.e("yjxm", e.getLocalizedMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					Log.e("yjxm", e.getStackTrace().toString());
				}
			}
		}
	}
}
