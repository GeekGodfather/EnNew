package com.ennew.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.alibaba.fastjson.JSONObject;
import com.ennew.Application;
import com.ennew.config.MyConstant;
import com.ennew.db.DataBaseManager;
import com.ennew.model.Conversation;
import com.ennew.model.MessageExtension;
import com.ennew.model.MessageExtensionProvider;
import com.ennew.model.MessageInfo;
import com.ennew.service.XMPPService;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class XMPPManager {

    private static final String TAG = "XMPPManager";

    private static final int PACKET_TIMEOUT = 5000;

    // 连接对象
    public XMPPConnection mXmppConnection;

    // 连接配置
    private ConnectionConfiguration mConfiguration;

    // 新消息的监听
    private PacketListener mPacketListener;

    private PacketListener mSendFailureListener;

    private XMPPService mXmppService;

    private PacketListener mPongListener;

    private String touserid = "";

    // ping-pong服务器
    private String mPingID;// ping服务器的id
    private long mPingTimestamp;// 时间戳
    private PendingIntent mPingAlarmPendIntent;// 是通过闹钟来控制ping服务器的时间间隔
    private PendingIntent mPongTimeoutAlarmPendIntent;// 判断服务器连接超时的闹钟
    private static final String PING_ALARM = "com.way.xx.PING_ALARM";// ping服务器闹钟BroadcastReceiver的Action
    private static final String PONG_TIMEOUT_ALARM = "com.way.xx.PONG_TIMEOUT_ALARM";// 判断连接超时的闹钟BroadcastReceiver的Action
    private Intent mPingAlarmIntent = new Intent(PING_ALARM);
    private Intent mPongTimeoutAlarmIntent = new Intent(PONG_TIMEOUT_ALARM);
    private PongTimeoutAlarmReceiver mPongTimeoutAlarmReceiver = new PongTimeoutAlarmReceiver();
    private BroadcastReceiver mPingAlarmReceiver = new PingAlarmReceiver();
    private PacketListener mSendPacketListener;

    public XMPPManager(XMPPService service) {

        // 创建配置
        this.mConfiguration = new ConnectionConfiguration(MyConstant.SMACK_CUSTOM_SERVER, MyConstant.SMACK_PORT);
        this.mConfiguration.setReconnectionAllowed(false);// 是否启用重连机制
        // this.mConfiguration.setSendPresence(true);// 设置状态为离线
        this.mConfiguration.setCompressionEnabled(false);// 是否使用压缩
        this.mConfiguration.setSASLAuthenticationEnabled(MyConstant.SMACK_REQUIRE);// 是否启用安全验证
        this.mConfiguration.setDebuggerEnabled(MyConstant.SMACK_DEBUG);// 是否开启调试模式
        this.mXmppConnection = new XMPPConnection(mConfiguration);
        // 配置各种Provider，如果不配置，则会无法解析数据
        configureConnection(ProviderManager.getInstance());
        this.mXmppService = service;
    }

    public void setToUserId(String touserid) {
        this.touserid = StringUtil.formatUserId(touserid);
    }

    public boolean login(String name, String pwd) throws XMPPException {
        if (mXmppConnection.isConnected()) {// 首先判断是否还连接着服务器，需要先断开
            mXmppConnection.disconnect();
        }
        SmackConfiguration.setPacketReplyTimeout(PACKET_TIMEOUT);// 设置超时时间
        SmackConfiguration.setKeepAliveInterval(-1);
        SmackConfiguration.setDefaultPingInterval(0);
        mXmppConnection.connect();
        if (!mXmppConnection.isConnected()) {
            try {
                throw new Exception("SMACK connect failed without exception!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        registerConnectionListener();
        if (!mXmppConnection.isAuthenticated()) {
            mXmppConnection.login(name, pwd);
            initServiceDiscovery();
        }
        setStatusFromConfig();// 更新在线状态
        registerAllListener();// 注册监听其他的事件，比如新消息
        return mXmppConnection.isAuthenticated();
    }

    /**
     * 设置消息回执
     */
    private void initServiceDiscovery() {
        // // register connection features
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager
                .getInstanceFor(mXmppConnection);
        if (sdm == null)
            sdm = new ServiceDiscoveryManager(mXmppConnection);
        sdm.addFeature("http://jabber.org/protocol/disco#info");
        DeliveryReceiptManager dm = DeliveryReceiptManager
                .getInstanceFor(mXmppConnection);
        dm.enableAutoReceipts();
        //添加消息回执监听
        dm.registerReceiptReceivedListener(new DeliveryReceiptManager.ReceiptReceivedListener() {
            public void onReceiptReceived(String fromJid, String toJid,
                                          String receiptId) {
                Log.e("TAG", "对方收到了" + receiptId);
                // 标记为对方已读
            }
        });

    }

    /**
     * 注册链接监听
     */
    private void registerConnectionListener() {
        mXmppConnection.addConnectionListener(new ConnectionListener() {
            public void connectionClosedOnError(Exception e) {
                // 链接异常关闭
                Log.e("TAG", "-->>连接异常关闭");
                mXmppService.postConnectionFailed(XMPPService.CONNECTION_FAILED);// 连接关闭时，动态反馈给服务
            }

            public void connectionClosed() {
                // 连接关闭
                Log.e("TAG", "-->>连接关闭");
            }

            public void reconnectingIn(int seconds) {
            }

            public void reconnectionFailed(Exception e) {

                Log.e("TAG", "-->>连接异常 ： " + e.getMessage());
            }

            public void reconnectionSuccessful() {
            }
        });
    }

    private void configureConnection(ProviderManager pm) {
        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());

        // 注册自定义的扩展xml
        pm.addExtensionProvider(MessageExtension.ELEMENT_NAME, MessageExtension.NAME_SPACE, new MessageExtensionProvider());


        // XEP-184 Message Delivery Receipts
        pm.addExtensionProvider("received", "urn:xmpp:receipts",
                new DeliveryReceipt.Provider());
        pm.addExtensionProvider("request", "urn:xmpp:receipts",
                new DeliveryReceipt.Provider());

        // add delivery receipts
        pm.addExtensionProvider(DeliveryReceipt.ELEMENT,
                DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
        pm.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,
                DeliveryReceipt.NAMESPACE,
                new DeliveryReceiptRequest.Provider());

    }

    /**
     * 添加监听
     */
    private void registerAllListener() {
        if (isAuthenticated()) {
            // 注册接受消息的监听
            registerMessageListener();
            registerSendMessageListener();
            registerMessageSendFailureListener();
            registerPongListener();// 注册服务器回应ping消息监听
        }
    }

    /**
     * 发送消息的监听
     */
    private void registerSendMessageListener() {
        if (mSendPacketListener != null) {
            mXmppConnection.removePacketListener(mSendPacketListener);
        }
        PacketTypeFilter typeFilter = new PacketTypeFilter(Message.class);
        mXmppConnection.addPacketSendingListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                parserDataFromMessageXml(packet, true);
            }
        }, typeFilter);
    }

    /**
     * 注册新消息的监听
     */
    private void registerMessageListener() {
        if (mPacketListener != null) {
            mXmppConnection.removePacketListener(mPacketListener);
        }
        PacketTypeFilter typeFilter = new PacketTypeFilter(Message.class);

        mPacketListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                parserDataFromMessageXml(packet, false);
            }
        };
        mXmppConnection.addPacketListener(mPacketListener, typeFilter);
    }

    /**
     * 解析消息XML，处理不同的操作
     *
     * @param isYouSend true代表自己发送的消息，false代表别人发送给自己的消息
     */
    private void parserDataFromMessageXml(Packet packet, boolean isYouSend) {
        if (packet != null && packet instanceof Message) {
            Message message = (Message) packet;
            String chatMsg = message.getBody();
            Log.e("TAG", "-->>" + message.getFrom() + "新消息：" + message.toXML());

            // 因为前面已经对自己发送的消息进行过处理，这过滤自己发送的消息
            if (!TextUtils.isEmpty(chatMsg)) {
                // 获取扩展XML
                MessageExtension extension = (MessageExtension) message.getExtension(MessageExtension.ELEMENT_NAME, MessageExtension.NAME_SPACE);
                MessageInfo msgInfo = new MessageInfo();
                // 解析扩展字段中的消息类型、会话类型、以及JSON数据
                parserXml(msgInfo, extension.toXML());
                // 记录聊天内容
                msgInfo.setContent(chatMsg);
                // 记录当前接收到消息的时间
                msgInfo.setTimesTamp(CommonUtil.getDate("yyyy-MM-dd HH:mm:ss"));
                // 设置消息来源
                String from = StringUtil.formatUserId(message.getFrom());
                msgInfo.setFromuserId(from);
                String to = StringUtil.formatUserId(message.getTo());
                // 如果是自己发送的没必要对录音文件进行解析
                if (isYouSend) {
                    to = touserid;
                    // 设置消息去除
                    msgInfo.setTouserId(touserid);
                    mXmppService.sendNewMessage(msgInfo);
                } else if (!TextUtils.isEmpty(touserid) && touserid.equals(from)) {
                    // 设置消息去除
                    msgInfo.setTouserId(to);
                    // 当接收的是录音文件时，对录音文件进行解析
                    if (msgInfo.getContentType() == 3) {
                        String filePath = FileUtil.createAudioCacheFilePath("IM_"
                                + System.currentTimeMillis() + ".amr");
                        try {
                            filePath = FileUtil.decoderBase64File(msgInfo.getContent(), Base64.DEFAULT, filePath);
                            msgInfo.setAudioFilePath(filePath);
                        } catch (Exception e) {
                        }
                    }
                    mXmppService.receiveMessage(msgInfo);
                }
                Log.e("TAG", "-->>from : " + from + "-------------" + "to : " + to);
                saveConverstionInfo(msgInfo, isYouSend);

                // 将接受到的消息存入到数据库中
                DataBaseManager.getInstance().pushMsg(msgInfo);
            }
        }
    }

    private void saveConverstionInfo(MessageInfo msgInfo, boolean isYouSend) {
        String account = SharedPrefUtil.getStateString(mXmppService, MyConstant.ACCOUNT);
        account = account + MyConstant.SMACK_NAME_BASE;
        // 查询会话记录是否创建，如果不存在就进行创建
        Conversation conversation = DataBaseManager.getInstance().queryConversationInfo(msgInfo.getFromuserId(), msgInfo.getTouserId());
        if (conversation == null) {
            Conversation conv = new Conversation();
            conv.setConversationType(msgInfo.getMessageType());
            conv.setFromuserid(msgInfo.getFromuserId());
            conv.setTouserid(msgInfo.getTouserId());
            conv.setLastMeaage(msgInfo.getContent());
            if (isYouSend) {
                conv.setFromuserid(msgInfo.getFromuserId());
                conv.setTouserid(msgInfo.getTouserId());
            } else if (!TextUtils.equals(msgInfo.getFromuserId(), account)) {
                conv.setFromuserid(msgInfo.getTouserId());
                conv.setTouserid(msgInfo.getFromuserId());
            }
            conv.setPortraitImg(msgInfo.getPortraitImg());
            conv.setLastTime(msgInfo.getTimesTamp());
            conv.setConversationName(msgInfo.getSenderName());
            conv.setExt(msgInfo.getExt());
            conv.setContentType(msgInfo.getContentType());
            DataBaseManager.getInstance().pushConv(conv);
        }else{
            Conversation conv = new Conversation();
            conv.setCid(conversation.getCid());
            conv.setExt(msgInfo.getExt());
            conv.setConversationType(msgInfo.getMessageType());
            conv.setLastMeaage(msgInfo.getContent());
            conv.setLastTime(msgInfo.getTimesTamp());
            conv.setConversationName(msgInfo.getSenderName());
            conv.setExt(msgInfo.getExt());
            conv.setContentType(msgInfo.getContentType());
            DataBaseManager.getInstance().updateConversationInfo(conv);
        }
    }

    private void registerMessageSendFailureListener() {
        if (mSendFailureListener != null) {
            mXmppConnection.removePacketListener(mSendFailureListener);
        }
        PacketTypeFilter typeFilter = new PacketTypeFilter(Message.class);
        mSendFailureListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                Log.e("TAG", "->>addPacketSendFailureListener发送消息失败");
                mXmppService.sendMessageFailed(null);
            }
        };
        mXmppConnection.addPacketSendFailureListener(mPacketListener, typeFilter);
    }

    /**
     * 更新自己的状态
     */
    private void setStatusFromConfig() {
        if (mXmppConnection != null) {
            Presence presence = new Presence(Presence.Type.available);
            mXmppConnection.sendPacket(presence);
        }
    }

    /**
     * 判断当前连接状态
     */
    public boolean isAuthenticated() {
        if (mXmppConnection != null) {
            if (mXmppConnection.isConnected()) {
                if (mXmppConnection.isAuthenticated()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 发送消息
     */
    public void sendMessage(String message,
                            String contentType, String messageType, int duration) {
        if (isAuthenticated()) {
            Message newMessage = getMessage(message, contentType, messageType, duration);
            // 添加消息回执
            DeliveryReceiptManager.addDeliveryReceiptRequest(newMessage);
            // 发送消息
            mXmppConnection.sendPacket(newMessage);
        } else {
            mXmppService.sendMessageFailed(null);
        }
    }

    @NonNull
    private Message getMessage(String message, String contentType, String messageType, int duration) {
        // 获取用户名
        String userName = SharedPrefUtil.getStateString(Application.getInstance(),
                MyConstant.USER_NAME);
        // 获取openfire账户
        String account = SharedPrefUtil.getStateString(Application.getInstance(),
                MyConstant.ACCOUNT);
        String ext = getExt(duration, userName);
        final Message newMessage = new Message(touserid, Message.Type.chat);// 设置接受用户ID以及发送信息类型
        newMessage.setFrom(account + MyConstant.SMACK_NAME_BASE);
        newMessage.setTo(touserid);
        newMessage.setBody(message);// 添加body体
        // 设置自定义的扩展字段
        MessageExtension messageExtension = getMessageExtension(contentType, messageType, ext, newMessage);
        // 将自定义的扩展xml加入到Message中
        newMessage.addExtension(messageExtension);
        return newMessage;
    }

    /**
     * 设置自定义的扩展字段
     */
    @NonNull
    private MessageExtension getMessageExtension(String contentType, String messageType, String ext, Message newMessage) {
        MessageExtension messageExtension = new MessageExtension();
        messageExtension.setContentType(contentType);
        messageExtension.setMessageType(messageType);
        messageExtension.setMessageId(newMessage.getPacketID());
        messageExtension.setExt(ext);
        return messageExtension;
    }

    /**
     * 设置备用JSON字符串
     */
    @NonNull
    private String getExt(int duration, String userName) {
        JSONObject jsonObject = new JSONObject();
        // 用户名
        jsonObject.put("senderName", userName);
        // 用户头像地址(目前写死)
        jsonObject.put("portraitimg",
                "http://p4.so.qhimg.com/t0175ca48d527d2f739.jpg");
        // 录音时长
        jsonObject.put("duration", duration);
        return jsonObject.toJSONString();
    }

    private void parserXml(MessageInfo msgInfo, String extension) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(new StringReader(extension));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (MessageExtension.CONTENT_TYPE_ELEMENT_NAME.equals(name)) {// 消息内容类型
                            String contentType = parser.nextText();
                            if (!TextUtils.isEmpty(contentType)) {
                                msgInfo.setContentType(Integer.parseInt(contentType));
                            }
                        } else if (MessageExtension.MESSAGE_TYPE_ELEMENT_NAME.equals(name)) {// 会话类型
                            String msgType = parser.nextText();
                            if (!TextUtils.isEmpty(msgType)) {
                                msgInfo.setMessageType(Integer.parseInt(msgType));
                            }
                        } else if (MessageExtension.EXT_ELEMENT_NAME.equals(name)) {// 扩展
                            String ext = parser.nextText();
                            JSONObject parserObject = JSONObject.parseObject(ext);
                            String senderName = parserObject.getString("senderName");
                            msgInfo.setSenderName(senderName);
                            msgInfo.setExt(ext);
                            msgInfo.setPortraitImg(parserObject.getString("portraitimg"));
                            msgInfo.setDuration(parserObject.getIntValue("duration"));
                        } else if (MessageExtension.MESSAGE_ID_ELEMENT_NAME.equals(name)) {// 消息ID，用于回执（暂时不用）
                            String messageId = parser.nextText();
                            msgInfo.setMessageId(messageId);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手动退出聊天
     */
    public boolean logout() {
        try {
            mXmppConnection.removePacketListener(mPacketListener);
            mXmppConnection.removePacketSendFailureListener(mSendFailureListener);
            mXmppConnection.removePacketListener(mPongListener);
            ((AlarmManager) mXmppService.getSystemService(Context.ALARM_SERVICE)).cancel(mPingAlarmPendIntent);
            ((AlarmManager) mXmppService.getSystemService(Context.ALARM_SERVICE)).cancel(mPongTimeoutAlarmPendIntent);
            mXmppService.unregisterReceiver(mPingAlarmReceiver);
            mXmppService.unregisterReceiver(mPongTimeoutAlarmReceiver);
        } catch (Exception e) {
            return false;
        }
        if (mXmppConnection.isConnected()) {
            new Thread() {
                public void run() {
                    Log.d(TAG, "shutDown thread started");
                    mXmppConnection.disconnect();
                    Log.d(TAG, "shutDown thread finished");
                }
            }.start();
        }
        this.mXmppService = null;
        return true;
    }

    /*****************
     * start 处理ping服务器消息
     ***********************/
    private void registerPongListener() {
        mPingID = null;// 初始化ping的id
        if (mPongListener != null) {
            mXmppConnection.removePacketListener(mPongListener);// 先移除之前监听对象
        }
        mPongListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                if (packet == null)
                    return;

                if (packet.getPacketID().equals(mPingID)) {// 如果服务器返回的消息为ping服务器时的消息，说明没有掉线
                    Log.d(TAG, String.format("Ping: server latency %1.3fs", (System.currentTimeMillis() - mPingTimestamp) / 1000.));
                    mPingID = null;
                    ((AlarmManager) mXmppService.getSystemService(Context.ALARM_SERVICE)).cancel(mPongTimeoutAlarmPendIntent);// 取消超时闹钟
                }
            }

        };

        mXmppConnection.addPacketListener(mPongListener, new PacketTypeFilter(IQ.class));// 正式开始监听
        mPingAlarmPendIntent = PendingIntent.getBroadcast(mXmppService.getApplicationContext(), 0, mPingAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);// 定时ping服务器，以此来确定是否掉线
        mPongTimeoutAlarmPendIntent = PendingIntent.getBroadcast(mXmppService.getApplicationContext(), 0, mPongTimeoutAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);// 超时闹钟
        mXmppService.registerReceiver(mPingAlarmReceiver, new IntentFilter(PING_ALARM));// 注册定时ping服务器广播接收者
        mXmppService.registerReceiver(mPongTimeoutAlarmReceiver, new IntentFilter(PONG_TIMEOUT_ALARM));// 注册连接超时广播接收者
        ((AlarmManager) mXmppService.getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, mPingAlarmPendIntent);// 15分钟ping以此服务器
    }

    /**
     * Ping服务器超时的广播
     */
    private class PongTimeoutAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent i) {
            mXmppService.postConnectionFailed(XMPPService.CONNECTION_TIMEOUT);
            logout();// 超时就断开连接
        }
    }

    /**
     * Ping服务器的广播
     */
    private class PingAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent i) {
            if (mXmppConnection.isAuthenticated()) {
                sendServerPing();// 收到ping服务器的闹钟，即ping一下服务器
            }
        }
    }

    public void sendServerPing() {
        if (mPingID != null) {// 此时说明上一次ping服务器还未回应，直接返回，直到连接超时
            return;
        }
        Ping ping = new Ping();
        ping.setType(Type.GET);
        ping.setTo(MyConstant.SMACK_CUSTOM_SERVER);
        mPingID = ping.getPacketID();// 此id其实是随机生成，但是唯一的
        mPingTimestamp = System.currentTimeMillis();
        mXmppConnection.sendPacket(ping);// 发送ping消息
        ((AlarmManager) mXmppService.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + PACKET_TIMEOUT + 3000, mPongTimeoutAlarmPendIntent);// 此时需要启动超时判断的闹钟了，时间间隔为30+3秒
    }

    /***************** end 处理ping服务器消息 ***********************/
}
