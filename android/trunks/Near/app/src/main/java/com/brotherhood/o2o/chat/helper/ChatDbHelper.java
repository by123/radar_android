package com.brotherhood.o2o.chat.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.brotherhood.o2o.chat.db.utils.SimpleDatabaseHelper;
import com.skynet.library.message.MessageManager;

public class ChatDbHelper extends SimpleDatabaseHelper {

	private static final int PLUGIN_DB_VERSION = 1;

	ChatDbHelper(Context cxt, String userId) {
		super(cxt.getDir(userId, Context.MODE_PRIVATE).getPath()
				+ "/message.db", "msgs", PLUGIN_DB_VERSION);
	}

	@Override
	protected void onPluginDbCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + MessageItem.TABLE + "("
				+ MessageItem.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ MessageItem.COL_SESSION_ID + " INTEGER, "
				+ MessageItem.COL_SESSION_TYPE + " INTEGER DEFAULT 0, "
				+ MessageItem.COL_SENDER + " INTEGER DEFAULT -1, "
				+ MessageItem.COL_SEND_STATUS + " INTEGER DEFAULT -1, "
				+ MessageItem.COL_DOWNLOAD_STATUS + " INTEGER DEFAULT -1, "
				+ MessageItem.COL_RECEIVER + " INTEGER DEFAULT -1, "
				+ MessageItem.COL_TYPE + " INTEGER DEFAULT -1, "
				+ MessageItem.COL_OPTIONS + " INTEGER DEFAULT 0, "
				+ MessageItem.COL_DURATION + " INTEGER DEFAULT -1, "
				+ MessageItem.COL_CONTENT + " BLOB, "
				+ MessageItem.COL_CONTENT_PATH + " TEXT, "
				+ MessageItem.COL_VOL_VIEWED + " INTEGER DEFAULT 0, "
				+ MessageItem.COL_VOL_ALIVE_SECS + " INTEGER DEFAULT 0, "
				+ MessageItem.COL_CREATE_TIME + " INTEGER, "
				+ MessageItem.COL_UPDATE_TIME + " INTEGER, "
				+ "_ext1 TEXT, _ext2 TEXT, _ext3 TEXT, _ext4 TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + SessionItem.TABLE + "("
				+ SessionItem.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ SessionItem.COL_TYPE + " INTEGER DEFAULT - 1, "
				+ SessionItem.COL_TARGET + " TEXT NOT NULL, "
				+ SessionItem.COL_UNREAD_COUNT + " INTEGER DEFAULT 0, "
				+ SessionItem.COL_MSG + " TEXT NOT NULL, "
				+ SessionItem.COL_SUB_MSG + " TEXT, " + SessionItem.COL_BLOCKED
				+ " INTEGER DEFAULT 0, " + SessionItem.COL_DO_NOT_NOTIFY
				+ " INTEGER DEFAULT 0, " + SessionItem.COL_CREATE_DATE
				+ " INTEGER, " + SessionItem.COL_MODIFIED_DATE + " INTEGER, "
				+ "_ext1 TEXT, _ext2 TEXT, _ext3 TEXT, _ext4 TEXT)");
	}

	@Override
	protected void onPluginDbUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MessageItem.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SessionItem.TABLE);
		onPluginDbCreate(db);
	}

	/**
	 * 消息表，用来存储所有的消息
	 */
	public static class MessageItem {

		/**
		 * 消息表名称
		 */
		public static final String TABLE = "chat_msgs";

		/**
		 * 消息记录ID
		 */
		public static final String COL_ID = "_id";

		/**
		 * 消息所属的会话ID
		 */
		public static final String COL_SESSION_ID = "_session_id";

		/**
		 * 消息所属的会话类型
		 */
		public static final String COL_SESSION_TYPE = "_session_type";

		/**
		 * 消息的发送者ID
		 */
		public static final String COL_SENDER = "_sender";

		/**
		 * 消息的发送状态
		 */
		public static final String COL_SEND_STATUS = "_send_status";

		/**
		 * 消息的下载状态
		 */
		public static final String COL_DOWNLOAD_STATUS = "_download_status";

		/**
		 * 消息的接收者，如果是群，则为群组ID
		 */
		public static final String COL_RECEIVER = "_receiver";

		/**
		 * 消息类型，文本、图片、语音、多媒体等等
		 */
		public static final String COL_TYPE = "_type";

		/**
		 * 消息属性，如是否加密、阅后即焚等等
		 */
		public static final String COL_OPTIONS = "_opts";

		/**
		 * 语音消息长度，单位秒
		 */
		public static final String COL_DURATION = "_duration";

		/**
		 * 消息内容
		 */
		public static final String COL_CONTENT = "_content";

		/**
		 * 消息内容的存储文件路径
		 */
		public static final String COL_CONTENT_PATH = "_content_path";

		/**
		 * 消息是否被阅读，仅针对阅后即焚消息
		 */
		public static final String COL_VOL_VIEWED = "_vol_viewed";

		/**
		 * 消息被阅读剩余时间，单位秒，仅针对阅后即焚消息
		 */
		public static final String COL_VOL_ALIVE_SECS = "_vol_left_time";

		/**
		 * 消息创建时间，单位秒
		 */
		public static final String COL_CREATE_TIME = "_create_time";

		/**
		 * 消息更新时间，单位秒
		 */
		public static final String COL_UPDATE_TIME = "_update_time";

		public static final int STATUS_SENDING = 1;
		public static final int STATUS_SEND_SUCCEEDED = 2;
		public static final int STATUS_SEND_FAILED = 3;

		public static final int DOWNLOADING = 1;
		public static final int DOWNLOAD_DONE = 2;
		public static final int DOWNLOAD_FAILED = 3;
		public static final int DOWNLOAD_OUT_OF_DATE = 4;

		public long id;
		public long sessionId;
		public int sessionType;
		public long sender;
		public int sendStatus;
		public int downloadStatus;
		public long receiver;
		public int msgType;
		public MessageManager.MessageOptions options;
		public long duration;
		public byte[] content;
		public String contentFilePath;
		public int volViewed;
		public int volAliveSecs;
		public long createTime;
		public long updateTime;

//		public ChatManager.MultimediaImage image;
//		public ChatManager.MultimediaAudio audio;

		@Override
		public String toString() {
			return "[id=" + id + "]";
		}
	}

	/**
	 * 会话表，存储所有的会话信息
	 */
	public static class SessionItem {

		/**
		 * 会话表名称
		 */
		public static final String TABLE = "sessions";

		/**
		 * 会话记录ID
		 */
		public static final String COL_ID = "_id";

		/**
		 * 会话类型
		 */
		public static final String COL_TYPE = "_type";

		/**
		 * 会话接收者，对群会话来讲，为群组ID，对单聊会话来讲，为对方的UID
		 */
		public static final String COL_TARGET = "_target";

		/**
		 * 会话内容
		 */
		public static final String COL_MSG = "_msg";

		/**
		 * 会话子内容
		 */
		public static final String COL_SUB_MSG = "_sub_msg";

		/**
		 * 会话未读消息数量
		 */
		public static final String COL_UNREAD_COUNT = "_unread_count";

		/**
		 * 标识是否屏蔽，即消息不插入数据库，1为true，0为false
		 */
		public static final String COL_BLOCKED = "_blocked";

		/**
		 * 标识是否不需要提醒在状态栏上，1为true，0为false
		 */
		public static final String COL_DO_NOT_NOTIFY = "_do_not_notify";

		/**
		 * 会话创建时间
		 */
		public static final String COL_CREATE_DATE = "_create_t";

		/**
		 * 会话修改时间，用于排序
		 */
		public static final String COL_MODIFIED_DATE = "_update_t";

		public long id;
		public int type;
		public long target;
		public String msg;
		public String subMsg;
		public int unreadCount;
		public boolean blocked;
		public boolean doNotNotify;
		public long createTime;
		public long updateTime;
	}
}