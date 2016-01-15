package com.ennew.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefUtil {
	
    public  static final String PERCHAGEVERSION="per_chang_verson";//个人修改版本
	
	public static void saveState(Context context, String username, String key,
			Object value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ysbao", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		if (username != null) {
			if (value instanceof Boolean) {
				editor.putBoolean(username + "_" + key, (Boolean) value);
			} else if (value instanceof Integer) {
				editor.putInt(username + "_" + key, (Integer) value);
			} else if (value instanceof String) {
				editor.putString(username + "_" + key, (String) value);
			} else if (value instanceof Long) {
				editor.putLong(key, (Long) value);
			}
		} else {
			if (value instanceof Boolean) {
				editor.putBoolean(key, (Boolean) value);
			} else if (value instanceof Integer) {
				editor.putInt(key, (Integer) value);
			} else if (value instanceof String) {
				editor.putString(key, (String) value);
			} else if (value instanceof Long) {
				editor.putLong(key, (Long) value);
			}
		}
		editor.commit();
	}
	
	public static void saveState(Context context, String key, Object value) {
		saveState(context, null, key, value);
	}

	public static String getStateString(Context context, String key) {
		return (String) getState(context, String.class, null, key);
	}

	public static Object getState(Context context, Class type, String username,
			String key) {
		Object value = null;
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ysbao", Context.MODE_PRIVATE);
		if (username != null) {
			if (type.getSimpleName().equals(Boolean.class.getSimpleName())) {
				value = sharedPreferences.getBoolean(username + "_" + key,
						false);
			} else if (type.getSimpleName().equals(
					Integer.class.getSimpleName())) {
				value = sharedPreferences.getInt(username + "_" + key, 0);
			} else if (type.getSimpleName()
					.equals(String.class.getSimpleName())) {
				value = sharedPreferences.getString(username + "_" + key, null);
			} else if (type.getSimpleName().equals(Long.class.getSimpleName())) {
				value = sharedPreferences.getLong(username + "_" + key, 0);
			}
		} else {
			if (type.getSimpleName().equals(Boolean.class.getSimpleName())) {
				value = sharedPreferences.getBoolean(key, false);
			} else if (type.getSimpleName().equals(
					Integer.class.getSimpleName())) {
				value = sharedPreferences.getInt(key, 0);
			} else if (type.getSimpleName()
					.equals(String.class.getSimpleName())) {
				value = sharedPreferences.getString(key, null);
			} else if (type.getSimpleName().equals(Long.class.getSimpleName())) {
				value = sharedPreferences.getLong(key, 0);
			}
		}
		return value;
	}

	public static void remove(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ysbao", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}
	
	//------------------------------- 
	private static Map<String, Integer> intMap;
	private static Map<String, Boolean> booleanMap;
	private static Map<String, String> stringMap;
	private static Map<String, Long> longMap;

	private static SharedPreferences sp;

	public static void init(Context context) {
		sp = context.getSharedPreferences("ysbao", Context.MODE_PRIVATE);
		intMap = new HashMap<String, Integer>();
		booleanMap = new HashMap<String, Boolean>();
		stringMap = new HashMap<String, String>();
		longMap = new HashMap<String, Long>();
	}

	public static boolean getBoolean(String key) {
		Boolean bool = booleanMap.get(key);
		if (bool == null) {
			bool = sp.getBoolean(key, false);
			booleanMap.put(key, bool);
		}
		return bool;
	}

	public static void putBoolean(String key, boolean value) {
		booleanMap.put(key, value);
		sp.edit().putBoolean(key, value).commit();
	}

	public static int getInt(String key) {
		Integer i = intMap.get(key);
		if (i == null) {
			i = sp.getInt(key, -1);
			intMap.put(key, i);
		}
		return i;
	}

	public static void putInt(String key, int value) {
		intMap.put(key, value);
		sp.edit().putInt(key, value).commit();
	}

	public static String getString(String key) {
		String str = stringMap.get(key);
		if (str == null) {
			str = sp.getString(key, "");
			stringMap.put(key, str);
		}
		return str;
	}

	public static String[] getStringArray(String key) {
		return getString(key).split(",");
	}

	public static void putString(String key, String value) {
		stringMap.put(key, value);
		sp.edit().putString(key, value).commit();
	}

	public static void putLong(String key, long value) {
		longMap.put(key, value);
		sp.edit().putLong(key, value).commit();
	}

	public static long getLong(String key) {
		Long l = longMap.get(key);
		if (l == null) {
			l = sp.getLong(key, 0l);
			longMap.put(key, l);
		}
		return l;
	}
	
	
	
	
}
