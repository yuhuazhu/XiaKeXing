package com.xkx.yjxm.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import android.os.Environment;
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
				
				f.createNewFile();
			} catch (IOException e) {
				Log.e("yjxm", e.getLocalizedMessage());
				return;
			}
		}

		FileWriter writer = null;
		try {
			f = new File(path + File.separator + "ex.log");
			writer = new FileWriter(f, false);
			StringBuffer sb = new StringBuffer();
			StackTraceElement[] trace = ex.getStackTrace();
			sb.append(new Date().toLocaleString()+"\n");
			sb.append(ex.getMessage() + "\n");
			for (int i = 0; i < trace.length; i++) {
				sb.append(trace[i] + "\n");
			}
			sb.append("----------------------------------------------------------------------------\n");
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
