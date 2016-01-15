package com.ennew.model;

import org.jivesoftware.smack.packet.PacketExtension;

public class MessageExtension implements PacketExtension {

	// 扩展xml的根基节点
	public static final String ELEMENT_NAME = "extension";

	// 标识文本内容的节点
	public static final String CONTENT_TYPE_ELEMENT_NAME = "msgContentType";

	public static final String EXT_ELEMENT_NAME = "ext";

	public static final String MESSAGE_TYPE_ELEMENT_NAME = "msgType";

	public static final String MESSAGE_ID_ELEMENT_NAME = "messageId";

	public static final String NAME_SPACE = "com:roger:content:type";

	private String contentType;

	private String ext;

	private String messageType;

	private String messageId;

	public MessageExtension() {
		super();
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public String getNamespace() {
		return NAME_SPACE;
	}

	@Override
	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<").append(ELEMENT_NAME).append(" xmlns=\"")
				.append(NAME_SPACE).append("\">");

		sb.append("<").append(MESSAGE_ID_ELEMENT_NAME).append(">");
		sb.append(messageId);
		sb.append("</").append(MESSAGE_ID_ELEMENT_NAME).append(">");

		sb.append("<").append(MESSAGE_TYPE_ELEMENT_NAME).append(">");
		sb.append(messageType);
		sb.append("</").append(MESSAGE_TYPE_ELEMENT_NAME).append(">");

		sb.append("<").append(CONTENT_TYPE_ELEMENT_NAME).append(">");
		sb.append(contentType);
		sb.append("</").append(CONTENT_TYPE_ELEMENT_NAME).append(">");

		sb.append("<").append(EXT_ELEMENT_NAME).append(">");
		sb.append(ext);
		sb.append("</").append(EXT_ELEMENT_NAME).append(">");

		sb.append("</").append(ELEMENT_NAME).append(">");
		return sb.toString();
	}
}
