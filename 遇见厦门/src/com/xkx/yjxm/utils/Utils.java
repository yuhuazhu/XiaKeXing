package com.xkx.yjxm.utils;

import java.security.MessageDigest;



import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;


public class Utils {
	private static int mScreenHeight  = 0;
	private static int mScreenWidth   = 0;
	private static int mScreenDensity = 0;
	public static String[] stringToArray(String src, String split) {
        String[] strs;
        if (src == null || src.length() == 0) {
            return null;
        }
        strs = src.split(split);
        return strs;
    }
	
	
	
	
	/**
	 * 根据手机的分辨率�?dip 的单�?转成�?px(像素)
	 */
	public static int dip2px(float dpValue) {
		return (int) (dpValue * ((float) getScreenDensity() / 160) + 0.5f);
	}

	/**
	 * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		Log.e("屏幕像素", scale+",");
		return (int) (dpValue * scale + 0.5f);
	}
	/**
	 * 根据手机的分辨率�?px(像素) 的单�?转成�?dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * 根据手机的分辨率�?px 的单�?转成�?dip
	 */
	public static int px2dip(float pxValue) {
		return (int) (pxValue /((float)getScreenDensity() / 160) + 0.5f);
	}
	
	/**
	 * 获取屏幕相关参数
	 */
	public static void initScreen(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(dm);
		if (dm.widthPixels > dm.heightPixels) {
			int pixel = dm.widthPixels;
			dm.widthPixels = dm.heightPixels;
			dm.heightPixels = pixel;
		}
		mScreenWidth   = dm.widthPixels;
		mScreenHeight  = dm.heightPixels;
		mScreenDensity = dm.densityDpi;
		
	}
	
	/**
	 * 获取屏幕高度
	 * @return
	 */
	public static int getScreenHeight() {
		return mScreenHeight;
	}
	
	/**
	 * 获取屏幕宽度
	 * @return
	 */
	public static int getScreenWidth() {
		return mScreenWidth;
	}
	
	/**
	 * 获取屏幕密度
	 * @return
	 */
	public static int getScreenDensity() {
		return mScreenDensity;
	}
	
	public static class LvHeightUtil {
		public static void setListViewHeightBasedOnChildren(ListView listView) {
		  ListAdapter listAdapter = listView.getAdapter();
		  if (listAdapter == null) {
		   return;
		  }
		  int totalHeight = 0;
		  for (int i = 0; i < listAdapter.getCount(); i++) {
		   View listItem = listAdapter.getView(i, null, listView);
		   listItem.measure(0, 0);
		   totalHeight += listItem.getMeasuredHeight();
		  }
		  ViewGroup.LayoutParams params = listView.getLayoutParams();
		  params.height = totalHeight
		    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		  listView.setLayoutParams(params);
		 }
		}
}
