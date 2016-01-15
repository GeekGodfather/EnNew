package com.ennew.utils;

import android.text.TextUtils;

import com.ennew.config.MyConstant;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author jianglihui
 * 
 */
public class StringUtil {

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、 换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串截取方法
	 */
	public static String subStringForName(String arg, int subLen) {
		StringBuffer sb = new StringBuffer();
		int len = 0;
		String item = null;
		for (int i = 0; i < arg.length() && len < subLen; i++) {
			item = arg.substring(i, i + 1);
			sb.append(item);
			if (java.util.regex.Pattern.matches("([/d/D]*)", item)) {
				len = len + 2;
			} else {
				len++;
			}
		}
		return sb.toString() + (arg.length() > subLen ? "..." : "");
	}

	/**
	 * 截取字符串中的url
	 */
	public static ArrayList<String> subUrlForString(String str) {
		ArrayList<String> urlList = new ArrayList<String>();
		Pattern pattern = Pattern
				.compile("(http://|ftp://|https://|www)[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*");
		// 空格结束
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			urlList.add(matcher.group(0));
		}
		return urlList;
	}

	/**
	 * 截取字符串中的电话号码
	 */
	public static ArrayList<String> subNumberForString(String str) {
		ArrayList<String> digitList = new ArrayList<String>();
		Pattern p = Pattern
				.compile("(0|86|17951|\\+86)?(13[0-9]|15[0-9]|17[0678]|18[0-9]|14[57])[0-9]{8}");
		Matcher m = p.matcher(str);
		while (m.find()) {
			String find = m.group(0).toString();
			digitList.add(find);
		}
		return digitList;
	}
	/**
	 * 判断 是不是手机号
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		return m.matches();

	}
	
	/**
	 * 从ID中获取用户名
	 */
	public static String getNameForJID(String jid) {
		String name = jid;
		if (!TextUtils.isEmpty(jid)) {
			if (jid.endsWith(MyConstant.SMACK_NAME_SUFFIX_SMACK)) {
				name = jid.replace(MyConstant.SMACK_NAME_SUFFIX_SMACK, "");
			}
			if (jid.endsWith(MyConstant.SMACK_NAME_SUFFIX_PHONE)) {
				name = jid.replace(MyConstant.SMACK_NAME_SUFFIX_PHONE, "");
			}
			if (jid.endsWith(MyConstant.SMACK_NAME_BASE)) {
				name = jid.replace(MyConstant.SMACK_NAME_BASE, "");
			}
		}
		return name;
	}

	/**
	 * 从ID中获取用户名
	 */
	public static String formatUserId(String jid) {
		if (!TextUtils.isEmpty(jid)) {
			if (jid.endsWith("/Smack")) {
				jid = jid.replace("/Smack", "");
			}
			if (jid.endsWith("/iPhone")) {
				jid = jid.replace("/iPhone", "");
			}
		}
		return jid;
	}

}
