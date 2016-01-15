package com.ennew.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ennew.utils.LogUtil;

public class IMDbHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

	public static IMDbHelper inst;

	/**消息表*/
	public final static String MESSAGE_TABLE_NAME ="t_message";

	/**会话表*/
	public final static String CONVERSATION_TABLE_NAME ="t_conversation";


	public final static String MID = "mid";
	/**消息ID*/
	public final static String MESSAGE_ID = "messageid";
	/**别人发送过来*/
	public final static String FROM_USER_ID ="fromuserid";
	/**发给别人*/
	public final static String TO_USER_ID ="touserid";
	/**消息是否加密*/
	public final static String IS_REQUIRE_ENCRYPTION ="isrequireencryption";
	/**服务器端对消息是否有加密*/
	public final static String IS_ENCRYPTED_ON_SERVER ="isencryptedonserver";
	/**是否已读*/
	public final static String IS_READ ="isRead";
	/**发送或接收消息的时间*/
	public final static String TIMES_TAMP ="timestamp";
	/**是否接收到已读回执*/
	public final static String IS_READACKED ="isreadacked";
	/**是否接收到已读回执*/
	public final static String IS_DELIVERED_HACKED ="isdeliveredacked";
	/**消息类型*/
	public final static String CONTENT_TYPE ="contenttype";
	/**消息内容*/
	public final static String CONTENT ="content";
	/**会话chatID*/
	public final static String CONVERSATION_ID ="conversationid";
	/**发送者名称*/
	public final static String SENDER_NAME ="sendername";
	/**扩展字段*/
	public final static String EXT ="ext";
	/**是否发送成功*/
	public final static String DELIVERY_STATE ="deliverystate";
	/**是否匿名消息*/
	public final static String IS_ANONYMOUS ="isanonymous";
	/**聊天类型*/
	public final static String MESSAGE_TYPE ="messagetype";
	/***录音时长*/
	public final static String DURATION ="duration";
	/**头像*/
	public final static String PORTRAIT_IMG ="portraitimg";
	/**录音文件路径*/
	public final static String AUDIO_FILE_PATH = "audiofilepath";

	public static synchronized IMDbHelper instance(Context ctx) {
		if (inst == null) {
			inst = new IMDbHelper(ctx, "im.db", null, DB_VERSION);
		}

		return inst;
	}

	public IMDbHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		createMsgTable(db);
		createConvTable(db);

	}

	private void createMsgTable(SQLiteDatabase db) {
		LogUtil.info("db#createMsgTable");

		String sql = "CREATE TABLE IF NOT EXISTS t_message (mid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, messageid TEXT NOT NULL, fromuserid TEXT NOT NULL, touserid TEXT NOT NULL, isrequireencryption INTEGER NOT NULL DEFAULT 0, isencryptedonserver INTEGER NOT NULL DEFAULT 0,isRead INTEGER NOT NULL DEFAULT 0, timestamp TEXT NOT NULL, isreadacked  INTEGER NOT NULL DEFAULT 0, isdeliveredacked  INTEGER NOT NULL DEFAULT 0, contenttype INTEGER NOT NULL DEFAULT 0, content TEXT, conversationid TEXT, sendername TEXT NOT NULL, ext TEXT, deliverystate INTEGER NOT NULL DEFAULT 0, isanonymous  INTEGER NOT NULL DEFAULT 0, messagetype INTEGER NOT NULL DEFAULT 0, duration TEXT, portraitimg TEXT, audiofilepath TEXT)";

		Log.v("db#create session_msg table -> sql:%s", sql);

		db.execSQL(sql);
	}

	private void createConvTable(SQLiteDatabase db) {
		LogUtil.info("db#conversation");

		String sql = "CREATE TABLE IF NOT EXISTS t_conversation (cid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, chatterid TEXT, conversationtype INTEGER NOT NULL DEFAULT 0, ext TEXT, portraitimg TEXT, conversationname TEXT, fromuserid TEXT NOT NULL, touserid TEXT NOT NULL, lastmessage TEXT, lasttime TEXT, contenttype TEXT)";

		Log.v("db#create conversation table -> sql:%s", sql);

		db.execSQL(sql);
	}
	
	public final static String INSERT_MESSAGE_SQL = "INSERT INTO "
			+ "t_message" + "(`" 
			+ "messageid" + "`, `" 
			+ "fromuserid" + "`, `"
			+ "touserid" + "`, `" 
			+ "isrequireencryption" + "`, `"
			+ "isencryptedonserver" + "`, `" 
			+ "isRead" + "`, `"
			+ "timestamp" + "`, `"
			+ "isreadacked" + "`, `"
			+ "isdeliveredacked" + "`, `" 
			+ "contenttype" + "`, `" 
			+ "content" + "`, `"
			+ "conversationid" + "`, `" 
			+ "sendername" + "`, `" 
			+ "ext" + "`, `" 
			+ "deliverystate" + "`, `" 
			+ "isanonymous" + "`, `" 
			+ "messagetype" + "`, `"
			+ "duration" + "`, `"
			+ "portraitimg" + "`, `"
			+ "audiofilepath"
			+ "`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public final static String INSERT_CONVERSATION_SQL = "INSERT INTO "
			+ "t_conversation" + "(`" 
			+ "chatterid" + "`, `" 
			+ "conversationtype" + "`, `"
			+ "ext" + "`, `" 
			+ "portraitimg" + "`, `"
			+ "fromuserid" + "`, `"
			+ "touserid" + "`, `"
			+ "conversationname"+ "`, `"
			+ "lastmessage"+ "`, `"
			+ "lasttime"+ "`, `"
			+ "contenttype"
			+ "`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
