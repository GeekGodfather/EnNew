package com.ennew.model;

/**
 * 会话
 * 
 * @author jianglihui
 * 
 */
public class Conversation {

	private int cid; // 消息ID
	private String chatterId = ""; // 会话chatID
	private int conversationType; // 会话类型 个人或群组
	private String ext = ""; // 扩展字段
	private String portraitImg = ""; // 好友头像
	private String conversationName = ""; // 显示名称（好友或群组名称）
	private String fromuserid = "";
	private String touserid = "";

	private String lastMeaage;// 最后一条信息

	private String msgCont;

	private String lastTime; // 最后一条信息发送的时间

	private int contentType;// 最后一条消息的类型

	public String getMsgCont() {
		return msgCont;
	}

	public void setMsgCont(String msgCont) {
		this.msgCont = msgCont;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getConversationType() {
		return conversationType;
	}

	public void setConversationType(int conversationType) {
		this.conversationType = conversationType;
	}

	public String getChatterId() {
		return chatterId;
	}

	public void setChatterId(String chatterId) {
		this.chatterId = chatterId;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getPortraitImg() {
		return portraitImg;
	}

	public void setPortraitImg(String portraitImg) {
		this.portraitImg = portraitImg;
	}

	public String getConversationName() {
		return conversationName;
	}

	public void setConversationName(String conversationName) {
		this.conversationName = conversationName;
	}

	public String getFromuserid() {
		return fromuserid;
	}

	public void setFromuserid(String fromuserid) {
		this.fromuserid = fromuserid;
	}

	public String getTouserid() {
		return touserid;
	}

	public void setTouserid(String touserid) {
		this.touserid = touserid;
	}

	public String getLastMeaage() {
		return lastMeaage;
	}

	public void setLastMeaage(String lastMeaage) {
		this.lastMeaage = lastMeaage;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

}
