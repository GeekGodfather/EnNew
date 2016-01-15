package com.ennew.umengsetting;

import android.content.Context;

import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * 
 * @author lilong 负责消息的点击事件
 * 需在Application的onCreate()
 */
public class MyUmengNotificationClickHandler extends
		UmengNotificationClickHandler {

	public MyUmengNotificationClickHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void launchApp(Context arg0, UMessage arg1) {
		// TODO Auto-generated method stub
		super.launchApp(arg0, arg1);
	}

	/**
	 * 该Handler是在BroadcastReceiver中被调用，故
	 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
	 */
	@Override
	public void openActivity(Context arg0, UMessage arg1) {
		// TODO Auto-generated method stub
		super.openActivity(arg0, arg1);
	}

	@Override
	public void openUrl(Context arg0, UMessage arg1) {
		// TODO Auto-generated method stub
		super.openUrl(arg0, arg1);
	}

	/**
	 * 自己处理
	 */
	@Override
	public void dealWithCustomAction(Context arg0, UMessage arg1) {
		// TODO Auto-generated method stub
		// super.dealWithCustomAction(arg0, arg1);
	}

}
