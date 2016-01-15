package com.ennew.model;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MessageExtensionProvider implements PacketExtensionProvider {

	@Override
	public PacketExtension parseExtension(XmlPullParser parser)
			throws Exception {
		String contentType = "1";
		String msgType = "0";
		String ext = "";
		String messageId = "";
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			String name = parser.getName();
			if (eventType == XmlPullParser.START_TAG) {
				// 判断是否是自己定义的元素
				if (MessageExtension.CONTENT_TYPE_ELEMENT_NAME.equals(name)) {
					contentType = parser.nextText();
				} else if (MessageExtension.MESSAGE_TYPE_ELEMENT_NAME
						.equals(name)) {
					msgType = parser.nextText();
				} else if (MessageExtension.EXT_ELEMENT_NAME.equals(name)) {
					ext = parser.nextText();
				} else if (MessageExtension.MESSAGE_ID_ELEMENT_NAME
						.equals(name)) {
					messageId = parser.nextText();
				}
			}
			if (eventType == XmlPullParser.END_TAG) {
				// 判断是否是扩展XML的结尾
				if (MessageExtension.ELEMENT_NAME.equals(name)) {
					done = true;
				}
			}
		}
		MessageExtension extension = new MessageExtension();
		extension.setContentType(contentType);
		extension.setMessageType(msgType);
		extension.setExt(ext);
		extension.setMessageId(messageId);
		return extension;
	}

}
