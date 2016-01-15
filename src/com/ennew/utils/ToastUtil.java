package com.ennew.utils;

import android.widget.Toast;

import com.ennew.Application;


/**
 * @author jianglh-a
 * 
 */
public class ToastUtil {
	
	public static boolean isShow=true;

	/**
	 * 弹一个Toast
	 * 
	 * @param msg
	 *            要展示的信息
	 * @param lengthType
	 *            展示的时间长度（0--短，1--长）
	 */
	public static void showToast(String msg, int lengthType) {
		if (0 == lengthType)
			Toast.makeText(Application.getInstance(), msg,Toast.LENGTH_SHORT).show();
		else if (1 == lengthType)
			Toast.makeText(Application.getInstance(), msg,Toast.LENGTH_LONG).show();
		else
			Toast.makeText(Application.getInstance(), msg,Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 弹一个Toast
	 * 
	 * @param msg
	 *            要展示的信息      
	 */
	public static void show(String msg) {
		   if(isShow)
	       showToast(msg, Toast.LENGTH_SHORT);
	}
	
	/**
	 * 弹一个Toast
	 * 
	 * @param msg
	 *            要展示的信息	     
	 */
	public static void show(int msgStringid) {
		   if (isShow) 
	       showToast(msgStringid, Toast.LENGTH_SHORT);
	}
	
	/**
	 * 设置Toast是否显示
	 * 
	 * @param isBoolean
	 *           
	 */
	public static void setShowToast(Boolean isBoolean) {
		    isShow=isBoolean;		   
	}
    
	/**
	 * 弹一个Toast
	 * 
	 * @param msg
	 *            要展示的信息
	 * @param lengthType
	 *            展示的时间长度（0--短，1--长）
	 */
	public static void showToast(int msgStringid, int lengthType) {
		if (0 == lengthType)
			Toast.makeText(Application.getInstance(), msgStringid,Toast.LENGTH_SHORT).show();
		else if (1 == lengthType)
			Toast.makeText(Application.getInstance(), msgStringid,Toast.LENGTH_LONG).show();
		else
			Toast.makeText(Application.getInstance(), msgStringid,Toast.LENGTH_SHORT).show();
	}
	
	
}
