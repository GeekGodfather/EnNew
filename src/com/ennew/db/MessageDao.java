package com.ennew.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ennew.model.Conversation;
import com.ennew.model.MessageInfo;
import com.ennew.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DB中IMMessage表操作类
 *
 * @author jianglihui
 */
public class MessageDao {
    private IMDbHelper helper;

    private static String MSG_COUNT = "msgCount";

    public static final int MESSAGE_LIMIT_NO = 10000; // 本地消息存储上限
    public static final int MESSAGE_LIMIT_DELETE_NO = 5000; // 若超出存储上限，一次删除消息条数

    private static MessageDao instance;

    public static MessageDao getInstance() {
        if (instance == null) {
            synchronized (Object.class) {
                if (instance == null) {
                    instance = new MessageDao(IMEntrance.getInstance().getContext());
                }
            }
        }
        return instance;
    }

    public MessageDao(Context context) {
        helper = IMDbHelper.instance(context);
    }

    /**
     * 新增一条消息
     *
     * @param msg 消息体
     * @return msgId 消息存储的唯一ID
     */
    public void saveMsg(MessageInfo msg) {
        SQLiteDatabase dbMaster = null;
        try {
            dbMaster = helper.getWritableDatabase();
            dbMaster.beginTransaction();

            dbMaster.execSQL(
                    IMDbHelper.INSERT_MESSAGE_SQL,
                    new Object[]{msg.getMessageId(), msg.getFromuserId(),
                            msg.getTouserId(), msg.getIsRequireEncryption(),
                            msg.getIsEncryptedOnServer(), msg.getIsRead(),
                            msg.getTimesTamp(), msg.getIsReadacked(),
                            msg.getIsDeliveredacked(), msg.getContentType(),
                            msg.getContent(), msg.getConversationId(),
                            msg.getSenderName(), msg.getExt(),
                            msg.getDeliveryState(), msg.getIsAnonymous(),
                            msg.getMessageType(), msg.getDuration(),
                            msg.getPortraitImg(),
                            msg.getAudioFilePath()

                    });

            dbMaster.setTransactionSuccessful();
        } catch (SQLException e) {
            LogUtil.info(e.toString());

        } finally {
            if (null != dbMaster) {
                dbMaster.endTransaction();
            }
            // dbMaster.close();
        }
    }

    /**
     * 查询本地存储的消息条数
     *
     * @return msgCount
     */
    public int queryMsgCount() {
        String SQL_QUERY_MSG_COUNT = "SELECT COUNT(*)  AS " + MSG_COUNT
                + " FROM t_message";

        int msgCount = 0;
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            c = dbSlaver.rawQuery(SQL_QUERY_MSG_COUNT, null);
            while (c.moveToNext()) {
                msgCount = c.getInt(c.getColumnIndex(MSG_COUNT));
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
            // dbSlaver.close();
        }

        return msgCount;
    }

    /**
     * 根据conversationId查询消息记录
     *
     * @param conversationId
     * @param num
     * @param startIndex
     * @return
     */
    public List<MessageInfo> queryInfoByConversationId(String conversationId,
                                                       int num, int startIndex) {
        MessageInfo msg = null;
        List<MessageInfo> msgList = new ArrayList<MessageInfo>();
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            String querySql = "select * from t_message"
                    + " where conversationid=? order by mid desc limit " + num
                    + " offset " + startIndex;
            c = dbSlaver.rawQuery(querySql,
                    new String[]{conversationId + ""});
            while (c.moveToNext()) {
                msg = new MessageInfo();
                msg.setMid(c.getInt(c.getColumnIndex("mid")));
                msg.setMessageId(c.getString(c.getColumnIndex(IMDbHelper.MESSAGE_ID)));
                msg.setFromuserId(c.getString(c.getColumnIndex(IMDbHelper.FROM_USER_ID)));
                msg.setTouserId(c.getString(c.getColumnIndex(IMDbHelper.TO_USER_ID)));
                msg.setIsRequireEncryption(c.getInt(c
                        .getColumnIndex(IMDbHelper.IS_REQUIRE_ENCRYPTION)));
                msg.setIsEncryptedOnServer(c.getInt(c
                        .getColumnIndex(IMDbHelper.IS_ENCRYPTED_ON_SERVER)));
                msg.setIsRead(c.getInt(c.getColumnIndex(IMDbHelper.IS_READ)));
                msg.setTimesTamp(c.getString(c.getColumnIndex(IMDbHelper.TIMES_TAMP)));
                msg.setIsReadacked(c.getInt(c.getColumnIndex(IMDbHelper.IS_READACKED)));
                msg.setIsDeliveredacked(c.getInt(c
                        .getColumnIndex(IMDbHelper.IS_DELIVERED_HACKED)));
                msg.setContentType(c.getInt(c.getColumnIndex(IMDbHelper.CONTENT_TYPE)));
                msg.setContent(c.getString(c.getColumnIndex(IMDbHelper.CONTENT)));
                msg.setConversationId(c.getString(c
                        .getColumnIndex(IMDbHelper.CONVERSATION_ID)));
                msg.setSenderName(c.getString(c.getColumnIndex(IMDbHelper.SENDER_NAME)));
                msg.setExt(c.getString(c.getColumnIndex(IMDbHelper.EXT)));
                msg.setDeliveryState(c.getInt(c.getColumnIndex(IMDbHelper.DELIVERY_STATE)));
                msg.setIsAnonymous(c.getInt(c.getColumnIndex(IMDbHelper.IS_ANONYMOUS)));
                msg.setMessageType(c.getInt(c.getColumnIndex(IMDbHelper.MESSAGE_TYPE)));
                msg.setPortraitImg(c.getString(c.getColumnIndex(IMDbHelper.PORTRAIT_IMG)));
                msg.setDuration(c.getInt(c.getColumnIndex(IMDbHelper.DURATION)));
                msg.setAudioFilePath(c.getString(c.getColumnIndex(IMDbHelper.AUDIO_FILE_PATH)));
                msgList.add(msg);
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
            // dbSlaver.close();
        }
        Collections.reverse(msgList);
        return msgList;
    }

    /**
     * 根据conversationId查询消息记录
     */
    public List<MessageInfo> queryMessageInfosByFromIdAndToId(String fromId,
                                                              String toId, int num, int startIndex) {
        MessageInfo msg = null;
        List<MessageInfo> msgList = new ArrayList<MessageInfo>();
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            String querySql = "select * from t_message"
                    + " where (fromuserid like ? and touserid like ?) or (touserid like ? and fromuserid like ?) order by mid desc";
            if (num > 0 && startIndex >= 0) {
                querySql = querySql + "limit " + num + " offset " + startIndex;
            }
            c = dbSlaver.rawQuery(querySql, new String[]{fromId + "%", toId + "%", fromId + "%", toId + "%"});
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    msg = new MessageInfo();
                    msg.setMid(c.getInt(c.getColumnIndex("mid")));
                    msg.setMessageId(c.getString(c.getColumnIndex(IMDbHelper.MESSAGE_ID)));
                    msg.setFromuserId(c.getString(c.getColumnIndex(IMDbHelper.FROM_USER_ID)));
                    msg.setTouserId(c.getString(c.getColumnIndex(IMDbHelper.TO_USER_ID)));
                    msg.setIsRequireEncryption(c.getInt(c
                            .getColumnIndex(IMDbHelper.IS_REQUIRE_ENCRYPTION)));
                    msg.setIsEncryptedOnServer(c.getInt(c
                            .getColumnIndex(IMDbHelper.IS_ENCRYPTED_ON_SERVER)));
                    msg.setIsRead(c.getInt(c.getColumnIndex(IMDbHelper.IS_READ)));
                    msg.setTimesTamp(c.getString(c.getColumnIndex(IMDbHelper.TIMES_TAMP)));
                    msg.setIsReadacked(c.getInt(c.getColumnIndex(IMDbHelper.IS_READACKED)));
                    msg.setIsDeliveredacked(c.getInt(c
                            .getColumnIndex(IMDbHelper.IS_DELIVERED_HACKED)));
                    msg.setContentType(c.getInt(c.getColumnIndex(IMDbHelper.CONTENT_TYPE)));
                    msg.setContent(c.getString(c.getColumnIndex(IMDbHelper.CONTENT)));
                    msg.setConversationId(c.getString(c
                            .getColumnIndex(IMDbHelper.CONVERSATION_ID)));
                    msg.setSenderName(c.getString(c.getColumnIndex(IMDbHelper.SENDER_NAME)));
                    msg.setExt(c.getString(c.getColumnIndex(IMDbHelper.EXT)));
                    msg.setDeliveryState(c.getInt(c.getColumnIndex(IMDbHelper.DELIVERY_STATE)));
                    msg.setIsAnonymous(c.getInt(c.getColumnIndex(IMDbHelper.IS_ANONYMOUS)));
                    msg.setMessageType(c.getInt(c.getColumnIndex(IMDbHelper.MESSAGE_TYPE)));
                    msg.setPortraitImg(c.getString(c.getColumnIndex(IMDbHelper.PORTRAIT_IMG)));
                    msg.setDuration(c.getInt(c.getColumnIndex(IMDbHelper.DURATION)));
                    msg.setAudioFilePath(c.getString(c.getColumnIndex(IMDbHelper.AUDIO_FILE_PATH)));
                    msgList.add(msg);
                }
                if (null != c) {
                    c.close();
                    c = null;
                }
            }
        } catch (Exception e) {
            LogUtil.info(e.toString());
        }
        Collections.reverse(msgList);
        return msgList;
    }

    /**
     * 查询最后一条聊天记录
     */
    public MessageInfo queryMessageInfoByFromIdAndToId(String fromId,
                                                       String toId) {
        MessageInfo msg = null;
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            String querySql = "select * from t_message where (fromuserid like ? and touserid like ?) or (touserid like ? and fromuserid like ?) order by mid desc limit 1";
            c = dbSlaver.rawQuery(querySql, new String[]{fromId + "%", toId + "%", fromId + "%", toId + "%"});
            if (c != null && c.moveToFirst()) {
                msg = new MessageInfo();
                msg.setMid(c.getInt(c.getColumnIndex("mid")));
                msg.setMessageId(c.getString(c.getColumnIndex("messageid")));
                msg.setFromuserId(c.getString(c.getColumnIndex("fromuserid")));
                msg.setTouserId(c.getString(c.getColumnIndex("touserid")));
                msg.setIsRequireEncryption(c.getInt(c
                        .getColumnIndex("isrequireencryption")));
                msg.setIsEncryptedOnServer(c.getInt(c
                        .getColumnIndex("isencryptedonserver")));
                msg.setIsRead(c.getInt(c.getColumnIndex("isRead")));
                msg.setTimesTamp(c.getString(c.getColumnIndex("timestamp")));
                msg.setIsReadacked(c.getInt(c.getColumnIndex("isreadacked")));
                msg.setIsDeliveredacked(c.getInt(c
                        .getColumnIndex("isdeliveredacked")));
                msg.setContentType(c.getInt(c.getColumnIndex("contenttype")));
                msg.setContent(c.getString(c.getColumnIndex("content")));
                msg.setConversationId(c.getString(c
                        .getColumnIndex("conversationid")));
                msg.setSenderName(c.getString(c.getColumnIndex("sendername")));
                msg.setExt(c.getString(c.getColumnIndex("ext")));
                msg.setDeliveryState(c.getInt(c.getColumnIndex("deliverystate")));
                msg.setIsAnonymous(c.getInt(c.getColumnIndex("isanonymous")));
                msg.setMessageType(c.getInt(c.getColumnIndex("messagetype")));
                msg.setPortraitImg(c.getString(c.getColumnIndex("portraitimg")));
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
        }
        return msg;
    }

    /**
     * 更新消息的已读状态
     */
    public void updateMessageReadState(MessageInfo info) {
        SQLiteDatabase dbSlaver = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("isRead", info.getIsRead());
        String whereClause = "messageid=?";
        String[] whereArgs = {info.getMessageId()};
        int row = dbSlaver.update("t_message", values, whereClause, whereArgs);
    }

    /**
     * 删除一条消息
     *
     * @param msg
     * @return Boolean
     */
    public Boolean delete(MessageInfo msg) {
        Boolean boolRtn = false;
        if (null == msg) {
            return boolRtn;
        }

        SQLiteDatabase dbMaster = null;
        try {
            dbMaster = helper.getWritableDatabase();
            dbMaster.beginTransaction();
            String msgId = msg.getMessageId();
            dbMaster.delete("t_message", "" + "msgId" + " == ?",
                    new String[]{msgId});

            dbMaster.setTransactionSuccessful();
            boolRtn = true;
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != dbMaster) {
                dbMaster.endTransaction();
            }
            // dbMaster.close();
        }
        return boolRtn;
    }

    /**
     * 修改录音文件的路径
     */
    public boolean updateAudioFilePathByMessageId(MessageInfo info) {
        SQLiteDatabase dbMaster = null;
        try {
            dbMaster = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("audioFilePath", info.getAudioFilePath());
            String whereClause = "messageid=?";
            String[] whereArgs = {info.getMessageId()};
            int row = dbMaster.update("t_message", values, whereClause, whereArgs);
            if (row > 0) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * 新增会话
     *
     * @param conv 会话实体
     */
    public void saveConv(Conversation conv) {
        SQLiteDatabase dbMaster = null;
        try {
            dbMaster = helper.getWritableDatabase();
            dbMaster.beginTransaction();

            dbMaster.execSQL(
                    IMDbHelper.INSERT_CONVERSATION_SQL,
                    new Object[]{conv.getChatterId(),
                            conv.getConversationType(), conv.getExt(),
                            conv.getPortraitImg(), conv.getFromuserid(),
                            conv.getTouserid(), conv.getConversationName(),
                            conv.getLastMeaage(), conv.getLastTime(),
                            conv.getContentType()
                    });

            dbMaster.setTransactionSuccessful();
        } catch (SQLException e) {
            LogUtil.info(e.toString());

        } finally {
            if (null != dbMaster) {
                dbMaster.endTransaction();
            }
            // dbMaster.close();
        }
    }


    public int updateConversationInfoById(Conversation conversation) {
        SQLiteDatabase dbMaster = null;
        int row = -1;
        try {
            dbMaster = helper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("chatterid", conversation.getChatterId());
            values.put("conversationtype", conversation.getConversationType());
            values.put("ext", conversation.getExt());
            values.put("portraitimg", conversation.getPortraitImg());
            values.put("lastmessage", conversation.getLastMeaage());
            values.put("lasttime", conversation.getLastTime());
            values.put("contenttype", conversation.getContentType());
            String where = "cid=" + conversation.getCid();
            row = dbMaster.update("t_conversation", values, where, null);
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        }
        return row;
    }

    /**
     * 查询本地存储的消息条数
     *
     * @return msgCount
     */
    public int queryConvCount() {
        String SQL_QUERY_CONV_COUNT = "SELECT COUNT(*)  AS " + MSG_COUNT
                + " FROM t_conversation";

        int msgCount = 0;
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            c = dbSlaver.rawQuery(SQL_QUERY_CONV_COUNT, null);
            while (c.moveToNext()) {
                msgCount = c.getInt(c.getColumnIndex(MSG_COUNT));
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
            // dbSlaver.close();
        }

        return msgCount;
    }

    /**
     * 查询会话表
     *
     * @param num        查询数量
     * @param startIndex 查询开始索引
     * @return
     */
    public List<Conversation> queryConverInfo(int num, int startIndex) {
        Conversation conv = null;
        List<Conversation> convList = new ArrayList<Conversation>();
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            String querySql = "select * from t_conversation";
            c = dbSlaver.rawQuery(querySql, null);
            while (c.moveToNext()) {
                conv = new Conversation();
                conv.setCid(c.getInt(c.getColumnIndex("cid")));
                conv.setChatterId(c.getString(c.getColumnIndex("chatterid")));
                conv.setConversationType(c.getInt(c
                        .getColumnIndex("conversationtype")));
                conv.setExt(c.getString(c.getColumnIndex("ext")));
                conv.setPortraitImg(c.getString(c.getColumnIndex("portraitimg")));
                conv.setConversationName(c.getString(c
                        .getColumnIndex("conversationname")));

                convList.add(conv);
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
            // dbSlaver.close();
        }
        Collections.reverse(convList);
        return convList;
    }

    /**
     * 删除会话
     *
     * @param conv
     * @return
     */
    public Boolean deleteConv(Conversation conv) {
        Boolean boolRtn = false;
        if (null == conv) {
            return boolRtn;
        }

        SQLiteDatabase dbMaster = null;
        try {
            dbMaster = helper.getWritableDatabase();
            dbMaster.beginTransaction();
            int cid = conv.getCid();
            dbMaster.delete("t_conversation", "" + "cid" + " == ?",
                    new String[]{String.valueOf(cid)});

            dbMaster.setTransactionSuccessful();
            boolRtn = true;
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != dbMaster) {
                dbMaster.endTransaction();
            }
            // dbMaster.close();
        }

        return boolRtn;
    }

    /**
     * 获取会话列表
     */
    public List<Conversation> queryConverList() {
        Conversation conv = null;
        List<Conversation> convList = new ArrayList<Conversation>();
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            // String querySql =
            // "select * from t_conversation inner join t_message on t_conversation.chatterid = t_message.conversationid";

            String querySql = "select * from t_conversation";
            c = dbSlaver.rawQuery(querySql, null);
            while (c.moveToNext()) {
                conv = new Conversation();
                conv.setCid(c.getInt(c.getColumnIndex("cid")));
                conv.setChatterId(c.getString(c.getColumnIndex("chatterid")));
                conv.setConversationType(c.getInt(c
                        .getColumnIndex("conversationtype")));
                conv.setExt(c.getString(c.getColumnIndex("ext")));
                conv.setPortraitImg(c.getString(c.getColumnIndex("portraitimg")));
                conv.setConversationName(c.getString(c
                        .getColumnIndex("conversationname")));
                conv.setFromuserid(c.getString(c.getColumnIndex("fromuserid")));
                conv.setTouserid(c.getString(c.getColumnIndex("touserid")));
                conv.setLastMeaage(c.getString(c.getColumnIndex("lastmessage")));
                conv.setLastTime(c.getString(c.getColumnIndex("lasttime")));
                conv.setContentType(c.getInt(c.getColumnIndex("contenttype")));

                convList.add(conv);
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
            // dbSlaver.close();
        }
        Collections.reverse(convList);
        return convList;
    }

    /**
     * 根据聊天双方的ID查看时候有会话缓存
     */
    public Conversation queryConversationInfo(String fromId, String toId) {
        Conversation conv = null;
        Cursor c = null;
        SQLiteDatabase dbSlaver = null;
        try {
            dbSlaver = helper.getReadableDatabase();
            String selection = "(fromuserId=? and touserId=?) or (touserId=? and fromuserId=?)";
            String[] selectionArgs = {fromId, toId, fromId, toId};
            c = dbSlaver.query("t_conversation", null, selection,
                    selectionArgs, null, null, null);
            if (c != null && c.moveToNext()) {
                conv = new Conversation();
                conv.setCid(c.getInt(c.getColumnIndex("cid")));
                conv.setChatterId(c.getString(c.getColumnIndex("chatterid")));
                conv.setConversationType(c.getInt(c
                        .getColumnIndex("conversationtype")));
                conv.setExt(c.getString(c.getColumnIndex("ext")));
                conv.setPortraitImg(c.getString(c.getColumnIndex("portraitimg")));
                conv.setConversationName(c.getString(c
                        .getColumnIndex("conversationname")));
                conv.setFromuserid(c.getString(c.getColumnIndex("fromuserid")));
                conv.setTouserid(c.getString(c.getColumnIndex("touserid")));
                conv.setLastMeaage(c.getString(c.getColumnIndex("lastmessage")));
                conv.setLastTime(c.getString(c.getColumnIndex("lasttime")));
                conv.setContentType(c.getInt(c.getColumnIndex("contenttype")));
            }
        } catch (SQLException e) {
            LogUtil.info(e.toString());
        } finally {
            if (null != c) {
                c.close();
            }
        }
        return conv;
    }


}
