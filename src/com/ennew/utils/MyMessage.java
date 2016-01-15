package com.ennew.utils;

import org.jivesoftware.smack.packet.Message;

public class MyMessage extends Message {

	private String type;
	private String to;
	private String from;
	private String msgType;
	private String msgContentType;
	private String messageId;
	private String body;
	private String ext;

	public MyMessage(String type, String to, String from, String msgType,
			String msgContentType, String messageId, String body, String ext) {
		this.type = type;
		this.to = to;
		this.from = from;
		this.msgType = msgType;
		this.msgContentType = msgContentType;
		this.messageId = messageId;
		this.body = body;
		this.ext = ext;
	}

	public MyMessage(String type, String to, String msgType,
			String msgContentType, String messageId, String body, String ext) {
		this.type = type;
		this.to = to;
		this.msgType = msgType;
		this.msgContentType = msgContentType;
		this.messageId = messageId;
		this.body = body;
		this.ext = ext;
	}

	public MyMessage(String arg0, Type arg1) {
		super(arg0, arg1);
	}

	public MyMessage(String arg0) {
		super(arg0);
	}

	public String getTo() {
		return super.getTo();
	}

	public void setTo(String to) {
		this.to = to;
		super.setTo(to);
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgContentType() {
		return msgContentType;
	}

	public void setMsgContentType(String msgContentType) {
		this.msgContentType = msgContentType;
		super.setType(Type.chat);
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getBody() {
		return super.getBody();
	}

	public void setBody(String body) {
		this.body = body;
		super.setBody(body);
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getFrom() {
		return super.getFrom();
	}

	public void setFrom(String from) {
		this.from = from;
		super.setFrom(from);
	}

	@Override
	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<message messageId=\"");
		sb.append(messageId);
		sb.append("\" from=\"");
		sb.append(from);
		sb.append("\" to=\"");
		sb.append(to);
		sb.append("\" type=\"");
		sb.append(type);
		sb.append("\" msgType=\"");
		sb.append(msgType);
		sb.append("\" msgContentType=\"");
		sb.append(msgContentType);
		sb.append("\">");
		sb.append("<body>");
		sb.append(body);
		sb.append("</body>");
		sb.append("<ext>");
		sb.append(ext);
		sb.append("</ext>");
		sb.append("</message>");
		return sb.toString();
	}
	/*
	 * public String toXML() { Document document =
	 * DocumentHelper.createDocument(); Element root =
	 * document.addElement("meassage"); root.addAttribute("id", messageId);
	 * root.addAttribute("to", to); root.addAttribute("from", "jianglihui");
	 * root.addAttribute("type", type); root.addAttribute("msgType", msgType);
	 * root.addAttribute("msgContentType", msgContentType); Element bodyElement
	 * = root.addElement("body"); Element extElement = root.addElement("ext");
	 * 
	 * bodyElement.setText(body); extElement.setText(ext);
	 * 
	 * return document.asXML(); }
	 */

}
