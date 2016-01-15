package com.ennew.model;

/**
 * 消息
 */
public class MessageInfo {
    private int mid;
    private String messageId; // 消息ID
    private String fromuserId; // 别人发送过来
    private String touserId; // 发给别人
    private int isRequireEncryption; // 消息是否加密
    private int isEncryptedOnServer; // 服务器端对消息是否有加密
    private int isRead; // 是否已读 0-未读 1-已读
    private String timesTamp; // 发送或接收消息的时间
    private int isReadacked; // 是否接收到已读回执
    private int isDeliveredacked; // 是否接收到已读回执
    private int contentType; // 消息类型 文字 语音 图片
    private String content; // 消息内容
    private String conversationId; // 会话chatID
    private String senderName; // 发送者名称
    private String ext; // 扩展字段
    private int deliveryState; // 是否发送成功 0-待发送 1-正在发送 2-发送成功 3-发送失败
    private int isAnonymous; // 是否匿名消息
    private int messageType; // 个人消息 群组消息
    private String portraitImg; // 好友头像
    private int duration; // 录音时长

    private String audioFilePath;// 录音文件的路径

    public int getIsReadacked() {
        return isReadacked;
    }

    public void setIsReadacked(int isReadacked) {
        this.isReadacked = isReadacked;
    }

    public int getIsDeliveredacked() {
        return isDeliveredacked;
    }

    public void setIsDeliveredacked(int isDeliveredacked) {
        this.isDeliveredacked = isDeliveredacked;
    }

    public int getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(int isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFromuserId() {
        return fromuserId;
    }

    public void setFromuserId(String fromuserId) {
        this.fromuserId = fromuserId;
    }

    public String getTouserId() {
        return touserId;
    }

    public void setTouserId(String touserId) {
        this.touserId = touserId;
    }

    public int getIsRequireEncryption() {
        return isRequireEncryption;
    }

    public void setIsRequireEncryption(int isRequireEncryption) {
        this.isRequireEncryption = isRequireEncryption;
    }

    public int getIsEncryptedOnServer() {
        return isEncryptedOnServer;
    }

    public void setIsEncryptedOnServer(int isEncryptedOnServer) {
        this.isEncryptedOnServer = isEncryptedOnServer;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(String timesTamp) {
        this.timesTamp = timesTamp;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(int deliveryState) {
        this.deliveryState = deliveryState;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getPortraitImg() {
        return portraitImg;
    }

    public void setPortraitImg(String portraitImg) {
        this.portraitImg = portraitImg;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }
}
