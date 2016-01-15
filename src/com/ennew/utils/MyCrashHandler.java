package com.ennew.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import com.ennew.app.AppManager;
import com.ennew.app.EnnewAcitivityList;
import com.ennew.config.MyConstant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
/**
 * 异常处理
 * @author jianglihui
 *
 */
public class MyCrashHandler implements UncaughtExceptionHandler {

	private static Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	private static MyCrashHandler instance;
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private StringBuilder builder;
	private String versionInfo;
	private String mobileInfo;
	private String errorInfo;
	private String SDCardRoot;

	private MyCrashHandler() {

	}

	public static MyCrashHandler getInstance() {
		if (instance == null) {
			return new MyCrashHandler();
		}
		return instance;
	}

	public void init(Context ctx, Context appContext) {
		mContext = ctx;
		String state = android.os.Environment.getExternalStorageState();

		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
			}
		} else {
			SDCardRoot = ctx.getFilesDir() + File.separator;
		}
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences("gsk_sp", Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean("gsk_crash_status", true);
		edit.commit();
		final Throwable mEx = ex;
		if (MyConstant.ENNEW_DEBUG) {
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					handleException(mEx);
					AppManager.getAppManager().finishAllActivity();
					EnnewAcitivityList.getInstance().killall();
					Looper.loop();
				}
			}.start();
		}

	}

	private String getErrorInfo(Throwable ex) {

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		printWriter.close();
		String error = writer.toString();

		return error;
	}

	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		try {
			Field[] fields = Build.class.getFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	private String getVersionInfo() {
		PackageManager packageManager = mContext.getPackageManager();
		try {
			PackageInfo info = packageManager.getPackageInfo(mContext.getPackageName(), 0);
			String versionName = info.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return true;
		}
		// 收集设备参数信息,保存日志文件
		collectDeviceInfo(mContext, ex);
		return true;
	}

	private void collectDeviceInfo(Context context, Throwable ex) {

		String dateFormat = dataFormat.format(System.currentTimeMillis());
		versionInfo = getVersionInfo();
		mobileInfo = getMobileInfo();
		errorInfo = getErrorInfo(ex);
		builder = new StringBuilder();
		builder.append("[" + dateFormat + "]").append("login_id= " + MyConstant.LOGIN_ID + "******").append("versionInfo= " + versionInfo + "******").append("mobileInfo= " + mobileInfo + "******").append("errorInfo= " + errorInfo + "******");

		InputStream string2Stream = string2Stream(builder.toString());
		write2SDFromInput("GSK/log", dateFormat + "crashlog.txt", string2Stream);

	}

	/**
	 * 字符串转换为输入流
	 *
	 * @param str
	 * @return
	 */
	public static InputStream string2Stream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {

		File file = null;
		OutputStream output = null;
		try {
			File lFile = creatSDDir(path);

			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 在SD卡上创建文件
	 *
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName, String dir) {
		File file = null;
		try {
			file = new File(SDCardRoot + dir + File.separator + fileName);
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 *
	 * @param dirName
	 */
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return dirFile;
	}

}
