package com.ennew.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.ennew.Application;

/**
 * 输出log的总工具类
 * 
 * @author jianglihui
 * 
 */
public class LogUtil {
	/** 开发阶段 */
	private static final int DEVELOP = 0;
	/** 内部测试阶段 */
	private static final int DEBUG = 1;
	/** 公开测试 */
	private static final int BATA = 2;
	/** 正式版 */
	private static final int RELEASE = 3;

	/** 当前阶段标示 */
	private static int currentStage = DEVELOP;

	private static String path;
	private static File file;
	private static FileOutputStream outputStream;
	private static String pattern = "yyyy-MM-dd HH:mm:ss";

	static {
		switch (currentStage) {
		case BATA:
			initLogFilePath();
			break;
		case DEBUG:
			File file = new File(Application.getInstance().getFilesDir(),
					"YSB_App.txt");
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 初始化Log文件的路径与相关输出流
	 */
	private static void initLogFilePath() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File logFile = Environment.getExternalStorageDirectory();
			path = logFile.getAbsolutePath() + "/ysb/";
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			file = new File(new File(path), "log" + ".txt");
			android.util.Log.i("LogUtil", "log在SD卡上的路径：" + path);
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// 如果SD卡不在位
			File file = new File(Application.getInstance().getFilesDir(),
					"YSB_App.txt");
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 此方法默认标签是LogUtil
	 * 
	 * @param msg
	 */
	public static void info(String msg) {
		info(LogUtil.class, msg);
	}

	/**
	 * 向外输出log的工具类
	 * 
	 * @param clazz
	 *            当前所在类
	 * @param msg
	 *            输出的log正文
	 */
	public static void info(@SuppressWarnings("rawtypes") Class clazz,
			String msg) {
		String className = "";
		if (clazz != null) {
			className = clazz.getSimpleName();
		}
		switch (currentStage) {
		case DEVELOP:
			// 控制台输出
			Log.i(clazz.getSimpleName(), msg);
			break;
		case DEBUG:
			// 在应用下面创建目录存放日志
			String t = CommonUtil.getDate(pattern);
			if (outputStream != null) {
				try {
					outputStream.write(t.getBytes());
					outputStream.write(("  " + className + "\r\n").getBytes());
					outputStream.write(("    " + msg).getBytes());
					outputStream.write("\r\n".getBytes());
					outputStream.flush();

				} catch (Exception e1) {
					e1.printStackTrace();
					if (outputStream != null)
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			} else {
				initLogFilePath();
			}
			break;
		case BATA:
			// 写日志到sdcard
			String time = CommonUtil.getDate(pattern);

			if (outputStream != null) {
				try {
					outputStream.write(time.getBytes());
					outputStream
							.write(("    " + className + "\r\n").getBytes());
					outputStream.write(msg.getBytes());
					outputStream.write("\r\n".getBytes());
					outputStream.flush();
				} catch (IOException e) {

				}
			} else {
				initLogFilePath();
			}

			break;
		case RELEASE:
			// 一般不做日志记录
			break;
		}
	}
}
