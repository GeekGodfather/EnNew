package com.ennew.callback;

import com.ennew.model.MessageInfo;

public interface ChatChangeListener {

	/**
	 * 接收到新消息
	 * 
	 */
	void receivedNewMessage(MessageInfo msg);
	
	/**
	 * 发送新消息
	 * 
	 */
	void sendNewMessage(MessageInfo msg);

	/**
	 * 消息发送失败
	 * 
	 */
	void sendMessageFailed(MessageInfo msg);

	/**
	 * 连接状态
	 */
	void connectionStatusChanged(int mConnectedState, int errorCode);
}
