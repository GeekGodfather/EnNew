package com.ennew.app;

import java.util.ArrayList;

import com.ennew.config.MyConstant;
import com.ennew.service.XMPPService;
import com.ennew.utils.SharedPrefUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
	public static final String BOOT_COMPLETED_ACTION = "com.ennew.app.action.BOOT_COMPLETED";
	public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (mListeners.size() > 0)// 通知接口完成加载
				for (EventHandler handler : mListeners) {
					handler.onNetChange();
				}
		} else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			Intent xmppServiceIntent = new Intent(context, XMPPService.class);
			context.stopService(xmppServiceIntent);
		} else {
			String account = SharedPrefUtil.getStateString(context,
					MyConstant.ACCOUNT);
			String password = SharedPrefUtil.getStateString(context,
					MyConstant.PASSWORD);
			if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
				Intent i = new Intent(context, XMPPService.class);
				i.setAction(BOOT_COMPLETED_ACTION);
				context.startService(i);
			}
		}
	}

	public static abstract interface EventHandler {

		public abstract void onNetChange();
	}
}
