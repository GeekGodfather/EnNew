package com.ennew.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ennew.config.MyConstant;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	/**
	 * @Description 判断是否是顶部activity
	 * @param context
	 * @param activityName
	 * @return
	 */
	public static boolean isTopActivy(Context context, String activityName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cName = am.getRunningTasks(1).size() > 0 ? am
				.getRunningTasks(1).get(0).topActivity : null;

		if (null == cName)
			return false;
		return cName.getClassName().equals(activityName);
	}

	/**
	 * 判断当前网络是否可用
	 * 
	 * @param act
	 * @return
	 */
	public static boolean isNetAvailable(Context act) {
		ConnectivityManager manager = (ConnectivityManager) act
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;
	}

	/**
	 * @Description 判断存储卡是否存在
	 * @return
	 */
	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}

		return false;
	}

	/**
	 * @Description 获取sdcard可用空间的大小
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getSDFreeSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long freeBlocks = sf.getAvailableBlocks();
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	/**
	 * @Description 获取sdcard容量
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private static long getSDAllSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long allBlocks = sf.getBlockCount();
		// 返回SD卡大小
		// return allBlocks * blockSize; //单位Byte
		// return (allBlocks * blockSize)/1024; //单位KB
		return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	public static byte[] intToBytes(int n) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (n >> (24 - i * 8));
		}
		return b;
	}

	public static byte[] float2byte(float f) {

		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}

		return dest;

	}

	/**
	 * 将byte数组转换为int数据
	 * 
	 * @param b
	 *            字节数组
	 * @return 生成的int数据
	 */
	public static int byteArray2int(byte[] b) {
		return (((int) b[0]) << 24) + (((int) b[1]) << 16)
				+ (((int) b[2]) << 8) + b[3];
	}

	/**
	 * @Description 判断是否是url
	 * @param text
	 * @return
	 */
	private static String matchUrl(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		Pattern p = Pattern.compile(
				"[http]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&]]*",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(text);
		if (matcher.find()) {
			return matcher.group();
		} else {
			return null;
		}
	}

	/**
	 * @Description 返回匹配到的URL
	 * @param text
	 * @param cmpUrl
	 * @return
	 */
	private static String getMatchUrl(String text, String cmpUrl) {
		String url = matchUrl(text);
		if (url != null && url.contains(cmpUrl)) {
			return url;
		} else {
			return null;
		}
	}

	public static String getImageSavePath(String fileName) {

		if (TextUtils.isEmpty(fileName)) {
			return null;
		}

		final File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "MFQ"
				+ File.separator
				+ "images");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		return folder.getAbsolutePath() + File.separator + fileName;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static int getDefaultPannelHeight(Context context) {
		if (context != null) {
			int size = (int) (getElementSzie(context) * 6.5);
			return size;
		} else {
			return 300;
		}
	}

	public static int getAudioBkSize(int sec, Context context) {
		int size = getElementSzie(context) * 3;
		if (sec <= 0) {
			return -1;
		} else if (sec <= 2) {
			return size;
		} else if (sec <= 8) {
			return (int) (size + ((float) ((sec - 2) / 6.0)) * size);
		} else if (sec <= 60) {
			return (int) (2 * size + ((float) ((sec - 8) / 52.0)) * size);
		} else {
			return -1;
		}
	}

	public static int getElementSzie(Context context) {
		int size = 0;
		if (context != null) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			int screenHeight = px2dip(dm.heightPixels, context);
			int screenWidth = px2dip(dm.widthPixels, context);
			if (screenWidth >= 800) {
				size = 60;
			} else if (screenWidth >= 650) {
				size = 55;
			} else if (screenWidth >= 600) {
				size = 50;
			} else if (screenHeight <= 400) {
				size = 20;
			} else if (screenHeight <= 480) {
				size = 25;
			} else if (screenHeight <= 520) {
				size = 30;
			} else if (screenHeight <= 570) {
				size = 35;
			} else if (screenHeight <= 640) {
				if (dm.heightPixels <= 960) {
					size = 35;
				} else if (dm.heightPixels <= 1000) {
					size = 45;
				} else if (dm.heightPixels <= 1280) {
					size = 60;
				} else if (dm.heightPixels > 1280) {
					size = 90;
				}
			}
		}
		return size;
	}

	private static int px2dip(float pxValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getImageMessageItemDefaultWidth(Context context) {
		return CommonUtil.getElementSzie(context) * 5;
	}

	public static int getImageMessageItemDefaultHeight(Context context) {
		return CommonUtil.getElementSzie(context) * 7;
	}

	public static int getImageMessageItemMinWidth(Context context) {
		return CommonUtil.getElementSzie(context) * 3;
	}

	public static int getImageMessageItemMinHeight(Context context) {
		return CommonUtil.getElementSzie(context) * 3;
	}

	/**
	 * @Description 隐藏软键盘
	 * @param activity
	 */
	public static void hideInput(Activity activity, EditText ed) {

		InputMethodManager inputmanger = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.hideSoftInputFromWindow(ed.getWindowToken(), 0);
	}

	/**
	 * 隐藏软键盘 时判断输入框的位置
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	public static boolean isShouldHideInput(View v, MotionEvent event,
			EditText ed) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			ed.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + ed.getHeight();
			int right = left + ed.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/** 获取屏幕的高度 */
	public final static int getWindowsHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public static String getAudioSavePath(String userId) {
		String path = getSavePath(MyConstant.FILE_SAVE_TYPE_AUDIO) + userId
				+ "_" + String.valueOf(System.currentTimeMillis())
				+ MyConstant.DEFAULT_AUDIO_SUFFIX;
		File file = new File(path);
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		return path;
	}

	public static String getSavePath(int type) {
		String path;
		String floder = (type == MyConstant.FILE_SAVE_TYPE_IMAGE) ? "images"
				: "audio";
		if (CommonUtil.checkSDCard()) {
			path = Environment.getExternalStorageDirectory().toString()
					+ File.separator + "MFQ" + File.separator + floder
					+ File.separator;

		} else {
			path = Environment.getDataDirectory().toString() + File.separator
					+ "MFQ" + File.separator + floder + File.separator;
		}
		return path;
	}

	/**
	 * 获取当前时间
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getDate(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	/**
	 * 手机无法获取设备号后，自动生成的deviceID 时间+4位随机数
	 * 
	 * @return
	 */
	public static String getDeviceId() {
		return getDate("yyyyMMddHHmmss") + getRandom(4);
	}

	/**
	 * 字母和数字的随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandom(int length) {
		StringBuffer sb = new StringBuffer();

		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
			// 字符串
			if ("char".equalsIgnoreCase(charOrNum)) {
				int choice = /* random.nextInt(2) % 2 == 0 ? */65/* : 97 */; // 取得大写字母还是小写字母
				sb.append((char) (choice + random.nextInt(26)));
			} else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
				sb.append(String.valueOf(random.nextInt(10)));
			}
		}
		return sb.toString();
	}

	/**
	 * MD5加密算法（不加盐）
	 * 
	 * @param content
	 * 
	 * @return 加密后的应用签名
	 */
	public static String encodeSig(String content) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(content.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int num = b & 0xff;
				String hexString = Integer.toHexString(num);
				if (hexString.length() == 1) {
					// 为了位数的整齐，如果转换后的16进制数只有一位，就在前面补0
					sb.append("0");
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 防止按钮短时间连续点击
	 */
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
	 * 
	 * @param mobile
	 *            移动、联通、电信运营商的号码段
	 *            <p>
	 *            移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
	 *            、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）
	 *            </p>
	 *            <p>
	 *            联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）
	 *            </p>
	 *            <p>
	 *            电信的号段：133、153、180（未启用）、189
	 *            </p>
	 *            <p>
	 *            添加支持170号码
	 *            <p>
	 * @return 验证成功返回true，验证失败返回false
	 */
	public static boolean checkCellPhone(String mobile) {
		String regex = "(\\+\\d+)?1[34578]\\d{9}$";
		return Pattern.matches(regex, mobile);
	}

	/**
	 * 打出电话
	 * 
	 * @param num
	 *            要拨打的号码
	 */
	public static void makeAcall(String num, Context ctx) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("tel://" + num));
		intent.setAction(Intent.ACTION_CALL);
		ctx.startActivity(intent);
	}
}
