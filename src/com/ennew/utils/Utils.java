package com.ennew.utils;

public class Utils {
	private static long lastClickTime;
/**
 * 防止快速点击
 * @return
 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
