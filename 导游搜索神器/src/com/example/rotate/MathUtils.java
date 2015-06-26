package com.example.rotate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class MathUtils {

	private static final int THRESHOLD_ACCURACY = 500;
	/**
	 * 像素相差此值以内的无效
	 */
	private static final int INTERVAL = 120;

	public static List<Point> genPoint(int cx, int cy, int minR, int maxR,
			int[] distance) {
		List<Point> list = new ArrayList<Point>();
		for (int i = 0; i < distance.length; i++) {
			int radius = distance[i];
			radius = radius < minR ? minR : radius;
			radius = radius > maxR ? maxR : radius;
			// 根据区间生成一个x值，保证这个值不和list里面重复。
			int tempX;
			int tempY;
			int outCount = 0;
			do {
				int innerCount = 0;
				do {
					tempX = (int) (cx - radius + 2 * radius * Math.random());
					Log.e("solve", "inner(");
					innerCount++;
				} while (existValue(list, tempX, INTERVAL) && innerCount < 1000);
				// 解y值
				int solve = (int) Math.sqrt(Math.pow(radius, 2)
						- Math.pow((tempX - cx), 2));
				tempY = cy + (Math.random() > 0.5 ? solve : -solve);
				Log.e("solve", "outer()");
			} while (!checkAccuracy(cx, cy, tempX, tempY, radius,
					THRESHOLD_ACCURACY) && outCount < 10);
			list.add(new Point(tempX, tempY));
		}
		return list;
	}

	private static boolean checkAccuracy(int cx, int cy, int x, int y,
			int radius, int accuracy) {
		int xSquare = (int) Math.pow(cx - x, 2);
		int ySquare = (int) Math.pow(cy - y, 2);
		int calDis = xSquare + ySquare;
		int rSquare = (int) Math.pow(radius, 2);
		if (calDis >= rSquare - accuracy && calDis <= rSquare + accuracy) {
			return true;
		} else {
			return false;
		}
	}

	public static int meters2px(Context ctx, int meters, int maxMeters) {
		if (maxMeters <= 0) {
			throw new IllegalArgumentException(
					"maxMeters must be a positive number");
		}
		if (meters <= 0) {
			return 0;
		}
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;
		if (meters >= maxMeters) {
			return width / 2;
		} else {
			return width / 2 / maxMeters * meters;
		}
	}

	private static boolean existValue(List<Point> list, int value, int interval) {
		for (int i = 0; i < list.size(); i++) {
			int x = list.get(i).x;
			if (x - interval < value && x + interval > value) {
				return true;
			}
		}
		return false;
	}
}

class Point {
	int x;
	int y;

	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
}
