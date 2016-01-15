package com.ennew.db;

import android.content.Context;

import com.ennew.utils.LogUtil;

public class IMEntrance {
	private static IMEntrance instance = null;
	private int serviceType;
	private Context context = null;

	private IMEntrance() {
		LogUtil.info("created");
	}

	public static synchronized IMEntrance getInstance() {
		if (null == instance) {
			instance = new IMEntrance();
		}
		return instance;
	}

	public void killTask() {

	}

	public void initTask(final Context cxt, final String userName, final String userPass) {
		try {
			LogUtil.info("initTask ==>" + userName);
			this.context = cxt;

		} catch (Exception e) {
			LogUtil.info(e.getMessage());
		}
	}

	public void setServiceType(int type) {
		serviceType = type;
	}

	public int getServiceType() {
		return serviceType;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
