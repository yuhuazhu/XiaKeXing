package com.xkx.yjxm.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
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
		String log = exceptionToString(ex);
		logStringToFile(log);
	}

	private String exceptionToString(Throwable ex) {
		StackTraceElement[] trace = ex.getStackTrace();
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append(sdf.format(new Date()) + "\n");
		sb.append(ex.getMessage() + "\n");
		for (int i = 0; i < trace.length; i++) {
			sb.append(trace[i] + "\n");
		}
		sb.append("----------------------------------------------------------------------------\n");
		return sb.toString();
	}

	public void logStringToFile(String str) {
		File file = createFileIfNotExist();
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.write(str);
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

	private File createFileIfNotExist() {
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Log.e("yjxm", "sdcard not mounted");
			return null;
		}
		File sdcard = Environment.getExternalStorageDirectory();
		String path = sdcard.getAbsolutePath() + File.separator + "yjxm";
		File f = new File(path + File.separator + "ex.log");
		File dir = new File(path);
		if (!dir.exists()) {
			try {
				dir.mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				Log.e("yjxm", e.getLocalizedMessage());
				return null;
			}
		}
		return f;
	}
}
