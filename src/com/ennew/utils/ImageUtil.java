package com.ennew.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageUtil {

	/**
	 * 把bitmap转换成String
	 */
	public static String filePathToString(String filePath) {
		Bitmap bm = getSmallBitmapFromFilePath(filePath, 90, 90);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	/**
	 * 把字符串转换成图片
	 */
	public static Bitmap StringToBitmap(String bitmapStr) {
		Bitmap smallBitmap = null;
		byte[] bitmapByte = ImageUtil.decodeBase64String(bitmapStr);
		if (bitmapByte != null && bitmapByte.length > 0) {
			Bitmap bitmap = ImageUtil.getBitmapFromBytes(bitmapByte);
			if (bitmap != null) {
				smallBitmap = ImageUtil.getSmallBtimap(bitmap, 100, 100);
				bitmap.recycle();
				bitmap = null;
			}
		}
		return smallBitmap;
	}

	/**
	 * 根据图片路径获得指定大小的bitmap
	 */
	public static Bitmap getSmallBitmapFromFilePath(String srcPath,
			int reqHeight, int reqWidth) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = reqHeight;// 设置需要的图片高度
		float ww = reqWidth;// 设置需要的图片宽度
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inPreferredConfig = Config.RGB_565;
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap getSmallBtimap(Bitmap image, int reqWidth,
			int reqHeight) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = reqHeight;// 设置需要的高度
		float ww = reqWidth;// 设置需要的宽度
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inPreferredConfig = Config.RGB_565;
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 对字符串进行解码
	 */
	public static byte[] decodeBase64String(String encodeStr) {
		byte[] decode = Base64.decode(encodeStr, Base64.DEFAULT);
		return decode;
	}

	/**
	 * 将字节数组转化成图片
	 */
	public static Bitmap getBitmapFromBytes(byte[] datas) {
		Bitmap bitmap = null;
		if (datas != null && datas.length > 0) {
			bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
		}
		return bitmap;
	}

	public static class ImageSize {
		int width;
		int height;

		public ImageSize() {
		}

		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return "ImageSize{" + "width=" + width + ", height=" + height + '}';
		}
	}

	/**
	 * 根据ImageView获适当的压缩的宽和高
	 * 
	 * @param view
	 * @return
	 */
	public static ImageSize getImageViewSize(View view) {

		ImageSize imageSize = new ImageSize();

		imageSize.width = getExpectWidth(view);
		imageSize.height = getExpectHeight(view);

		return imageSize;
	}

	/**
	 * 根据view获得期望的高度
	 * 
	 * @param view
	 * @return
	 */
	private static int getExpectHeight(View view) {

		int height = 0;
		if (view == null)
			return 0;

		final ViewGroup.LayoutParams params = view.getLayoutParams();
		// 如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
		if (params != null
				&& params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
			height = view.getWidth(); // 获得实际的宽度
		}
		if (height <= 0 && params != null) {
			height = params.height; // 获得布局文件中的声明的宽度
		}

		if (height <= 0) {
			height = getImageViewFieldValue(view, "mMaxHeight");// 获得设置的最大的宽度
		}

		// 如果宽度还是没有获取到，憋大招，使用屏幕的宽度
		if (height <= 0) {
			DisplayMetrics displayMetrics = view.getContext().getResources()
					.getDisplayMetrics();
			height = displayMetrics.heightPixels;
		}

		return height;
	}

	/**
	 * 根据view获得期望的宽度
	 * 
	 * @param view
	 * @return
	 */
	private static int getExpectWidth(View view) {
		int width = 0;
		if (view == null)
			return 0;

		final ViewGroup.LayoutParams params = view.getLayoutParams();
		// 如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
		if (params != null
				&& params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
			width = view.getWidth(); // 获得实际的宽度
		}
		if (width <= 0 && params != null) {
			width = params.width; // 获得布局文件中的声明的宽度
		}

		if (width <= 0)

		{
			width = getImageViewFieldValue(view, "mMaxWidth");// 获得设置的最大的宽度
		}
		// 如果宽度还是没有获取到，憋大招，使用屏幕的宽度
		if (width <= 0)

		{
			DisplayMetrics displayMetrics = view.getContext().getResources()
					.getDisplayMetrics();
			width = displayMetrics.widthPixels;
		}

		return width;
	}

	/**
	 * 通过反射获取imageview的某个属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;

	}

	/**
	 * 根据指定的角度顺时针旋转图片。
	 * 
	 * @param src
	 * @param degrees
	 *            顺时针旋转的角度
	 * @return 若产生异常返回 null
	 */
	public static Bitmap rotateBitmap(Bitmap src, float degrees) {
		if (src == null) {
			return null;
		}
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(degrees);
			Bitmap bm = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
					src.getHeight(), matrix, true);
			recycleBitmap(src);
			return bm;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 回收 Bitmap 资源。<br>
	 * <p/>
	 * <b>该方法调用了 {@link System#gc()}，当同时大量执行时会影响性能。</b>
	 * 
	 * @param bm
	 */
	public static void recycleBitmap(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
			bm = null;
			System.gc();
		}
	}

	/**
	 * 获取圆角 Bitmap。
	 * 
	 * @param bm
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bm, float roundPx) {
		return convertRoundedCornerBitmap(bm, roundPx);
	}

	/**
	 * 获取圆角 Bitmap。
	 * 
	 * @param bm
	 * @param roundPx
	 *            圆角的半径
	 * @return
	 */
	public static Bitmap convertRoundedCornerBitmap(Bitmap bm, float roundPx) {
		if (bm == null) {
			return bm;
		}
		Bitmap output = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = Color.TRANSPARENT;
		final Paint paint = new Paint();

		final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bm, rect, rect, paint);

		return output;
	}

	/**
	 * 把图片转换成圆形图片。
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap convertRoundedBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dstLeft, dstTop, dstRight, dstBottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dstLeft = 0;
			dstTop = 0;
			dstRight = width;
			dstBottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dstLeft = 0;
			dstTop = 0;
			dstRight = height;
			dstBottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dstLeft, (int) dstTop, (int) dstRight,
				(int) dstBottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 根据资源名称获得图片
	 */
	public static Drawable getDrawableByResName(Context context, String resName) {
		Resources resources = context.getResources();
		int resId = resources.getIdentifier(resName, "drawable",
				context.getPackageName());
		if (resId != 0) {
			return context.getResources().getDrawable(resId);
		}
		return null;
	}

}
