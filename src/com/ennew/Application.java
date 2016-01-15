package com.ennew;

import com.ennew.db.IMEntrance;
import com.ennew.utils.SharedPrefUtil;

public class Application extends android.app.Application {
	private static Application instance = null;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		IMEntrance.getInstance().setContext(getApplicationContext());
		SharedPrefUtil.init(this);

	}

	public static Application getInstance() {
		if (instance == null) {
			instance = new Application();
		}
		return instance;
	}

}
