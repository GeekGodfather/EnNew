package com.ennew.umengsetting;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;

import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

/**
 * 
 * @author lilong
 * 自定义通知栏样式
 */
public class MyUmengMessageHandler extends UmengMessageHandler{
	Context context;
	public MyUmengMessageHandler(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	@Override
	public Notification getNotification(Context arg0, UMessage arg1) {
		// TODO Auto-generated method stub
		switch (arg1.builder_id) {
		case 1:
			 //自定义 TODO
			return null;
			 
		default:
			return super.getNotification(arg0, arg1); 
		}
		
		
	} 
	
	/**
	 * 需要处理自定义消息，则需要将方法dealWithCustomMessage()重写，自定义消息的内容则存放在UMessage.custom里
	 */
	
	@Override
	public void dealWithCustomMessage(Context arg0, final UMessage arg1) {
		 new Handler(context.getMainLooper()).post(new Runnable() {

	            @Override
	            public void run() { 
	                  // TODO Auto-generated method stub
	               
	                 // 对自定义消息的处理方式，点击或者忽略
	               boolean isClickOrDismissed = true;
	                if(isClickOrDismissed) {
	                    //自定义消息的点击统计
	                    UTrack.getInstance(context).trackMsgClick(arg1);
	                } else {
	                    //自定义消息的忽略统计
	                    UTrack.getInstance(context).trackMsgDismissed(arg1);
	                }
	                //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
	            }
	        });
	    }	
	//mPushAgent.setMessageHandler(messageHandler);
	/**
	 * dealWithNotificationMessage()方法负责处理通知消息，该方法已经由消息推送SDK 完成
	 * 
	 */
	@Override
	public void dealWithNotificationMessage(Context arg0, UMessage arg1) {
		// TODO Auto-generated method stub
		super.dealWithNotificationMessage(arg0, arg1);
	}
	
	
	
	
	
	

}
