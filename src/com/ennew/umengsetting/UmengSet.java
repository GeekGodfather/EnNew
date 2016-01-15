package com.ennew.umengsetting;

import java.util.List;

import android.content.Context;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

/**
 * 
 * @author lilong
 * 
 */
public class UmengSet implements IUmengRegisterCallback,IUmengUnregisterCallback{
	private static PushAgent mPushAgent;
	private static UmengSet umengset = null;

	/*
	 * 获取实例
	 */
	public static UmengSet SingleInstace() {
		if (umengset==null) {
			umengset = new UmengSet();

		}

		return umengset;
	}
	// -----------------------------------开启推送-----------------------------
	/**
	 * 应用的主Activity onCreate() 函数中开启推送服务
	 * 
	 * @param context
	 */
	public void enablePush(Context context) {
		// TODO Auto-generated method stub
		setmPushAgent(context);
		mPushAgent.enable();
	}

	public void setmPushAgent(Context context) {
		if (mPushAgent == null) {
			mPushAgent = PushAgent.getInstance(context);
		}
	}

	/**
	 * 关闭推送服务
	 * 
	 * @param context
	 */
	public void disablePush(Context context) {
		// TODO Auto-generated method stub
		setmPushAgent(context);
		mPushAgent.disable();
	}

	/**
	 * 判断是否注册推送服务
	 * 
	 * @return
	 */
	public Boolean isEnabled(Context context) {
		// TODO Auto-generated method stub
		setmPushAgent(context);
		return mPushAgent.isEnabled();
	}

	// -----------------------------------开启统计-----------------------------
	/**
	 * 开启统计服务 在所有的Activity 的onCreate 函数添加
	 * 
	 * @param context
	 */
	public void appStart(Context context) {
		// TODO Auto-generated method stub
		PushAgent.getInstance(context).onAppStart();
	}

	/**
	 * 获取设备的Device Token Device Token为友盟生成的用于标识设备的id
	 * 
	 * @param context
	 * @return
	 */
	public String getRegistrationId(Context context) {
		// TODO Auto-generated method stub
		String device_token = null;
		if (!isEnabled(context)) {
			enablePush(context);
		}
		device_token = UmengRegistrar.getRegistrationId(context);
		return device_token;
	}

	/**
	 * 
	 * 通知免打扰模式
	 */
	public void setNoDisturbMode(Context context,int startHour, int startMinute, int endHour, int endMinute) {
		// TODO Auto-generated method stub
	     setmPushAgent(context);
	     mPushAgent.setNoDisturbMode(startHour, startMinute, endHour, endMinute);
	}
//---------------------------------用户添加标签分组--------------
	
	public void addTag(Context context,String key,String vlaue) {
		// TODO Auto-generated method stub
	     try {
	    	 setmPushAgent(context);
			mPushAgent.getTagManager().add(key,vlaue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	
	public void deleteTag(Context context,String key) {
		// TODO Auto-generated method stub
	     try {
	    	 setmPushAgent(context);
			mPushAgent.getTagManager().delete(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	public void updateTag(Context context,String key,String vlaue) {
		// TODO Auto-generated method stub
	     try {
	    	 setmPushAgent(context);
			mPushAgent.getTagManager().update(key,vlaue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	public void resetTag(Context context) {
		// TODO Auto-generated method stub
	     try {
	    	 setmPushAgent(context);
			mPushAgent.getTagManager().reset();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	
	public List<String> getAllTag(Context context) {
		// TODO Auto-generated method stub
		List<String> list = null;
	     try {
	    	 setmPushAgent(context);
	    	list=mPushAgent.getTagManager().list();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;	 
	}

	/**
	 * 注销回调接口
	 *  
	 */
	@Override
	public void onUnregistered(String arg0) {
		// TODO Auto-generated method stub
		
	}
    /**
     * 
     * 注册回调接口
     */
	@Override
	public void onRegistered(String arg0) {
		// TODO Auto-generated method stub		
	}

	/**
	 *
	 * @param context
	 * @return 测试ID
     */
	public String deviceToken(Context context){
		return UmengRegistrar.getRegistrationId(context);
	}
}
