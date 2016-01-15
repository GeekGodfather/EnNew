package com.ennew.db;

import android.content.Context;
import android.database.SQLException;

import com.ennew.model.Conversation;
import com.ennew.model.MessageInfo;
import com.ennew.utils.LogUtil;

import java.util.List;

public class DataBaseManager {

	private Context mContext = IMEntrance.getInstance().getContext();

	private static DataBaseManager instance;

	public static DataBaseManager getInstance() {
		if (null == instance) {
			instance = new DataBaseManager();
		}
		return instance;
	}

	/**
	 * 断开数据库
	 * 
	 * @return void
	 */
	public void stopDB() {
		// 断开本地数据库
		try {
			IMDbHelper.instance(mContext).close();
			IMDbHelper.inst = null;
			instance = null;
		} catch (SQLException e) {
			LogUtil.info(e.getMessage());
		}
	}

	/**
	 * 添加信息到数据库中
	 * 
	 * @param msgInfo
	 * @return void
	 */
	public void pushMsg(MessageInfo msgInfo) {
		MessageDao.getInstance().saveMsg(msgInfo);
	}

	/**
	 * 获取聊天记录
	 * 
	 * @param conversationId
	 * @param num
	 * @param startIndex
	 * @return
	 */
	public List<MessageInfo> pullMsgByConversationId(String conversationId,
			int num, int startIndex) {
		return MessageDao.getInstance().queryInfoByConversationId(
				conversationId, num, startIndex);
	}

	/**
	 * 获取聊天记录
	 * 
	 * @param fromId
	 * @param toId
	 * @param num
	 * @param startIndex
	 * @return
	 */
	public List<MessageInfo> queryMessageInfosByFromIdAndToId(String fromId,
			String toId, int num, int startIndex) {
		return MessageDao.getInstance().queryMessageInfosByFromIdAndToId(
				fromId, toId, num, startIndex);
	}

	/**
	 * 查询最后一条聊天记录
	 */
	public MessageInfo queryLastMessageInfoByFromIdAndToId(String fromId,
			String toId) {
		return MessageDao.getInstance().queryMessageInfoByFromIdAndToId(fromId,
				toId);
	}

	/**
	 * 修改当前信息已读状态
	 */
	public void updateMessageInfoReadState(MessageInfo info){
	MessageDao.getInstance().updateMessageReadState(info);
	}

	/**
	 * 修改录音文件的路径
	 */
	public boolean updateAudioFilePathByMessageId(MessageInfo info){
		return MessageDao.getInstance().updateAudioFilePathByMessageId(info);
	}

	/**
	 * 从数据库中删除某条消息
	 * 
	 * @param msgInfo
	 * @return void
	 */
	public boolean deleteMsg(MessageInfo msgInfo) {
		return MessageDao.getInstance().delete(msgInfo);
	}

	/**
	 * 添加会话
	 * 
	 * @param conv
	 * @return void
	 */
	public void pushConv(Conversation conv) {
		MessageDao.getInstance().saveConv(conv);
	}

	/**
	 * 添加会话
	 *
	 * @param conv
	 * @return void
	 */
	public void updateConversationInfo(Conversation conv) {
		MessageDao.getInstance().updateConversationInfoById(conv);
	}

	/**
	 * 查询会话记录
	 * 
	 * @param num
	 * @param startIndex
	 * @return
	 */
	public List<Conversation> pullConv(int num, int startIndex) {
		return MessageDao.getInstance().queryConverInfo(num, startIndex);
	}

	/**
	 * 从数据库中删除某条会话记录
	 * 
	 * @param conv
	 * @return void
	 */
	public boolean deleteConv(Conversation conv) {
		return MessageDao.getInstance().deleteConv(conv);
	}

	/**
	 * 获取会话列表
	 * 
	 * @return
	 */
	public List<Conversation> queryConverList() {
		return MessageDao.getInstance().queryConverList();
	}

	/**
	 * 根据聊天双方的ID查看时候有会话缓存
	 * 
	 * @return
	 */
	public Conversation queryConversationInfo(String fromId, String toId) {
		return MessageDao.getInstance().queryConversationInfo(fromId, toId);
	}

}
