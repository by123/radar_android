package com.brotherhood.o2o.chat.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.chat.ChatCompent;
import com.brotherhood.o2o.chat.db.DatabaseHandler;
import com.brotherhood.o2o.chat.ui.ChatActivity;
import com.brotherhood.o2o.chat.utils.ChatDbHelper.MessageItem;
import com.brotherhood.o2o.chat.utils.ChatDbHelper.SessionItem;
import com.brotherhood.o2o.personal.helper.PersonalHelper;
import com.brotherhood.o2o.personal.model.SystemMsgBean;
import com.brotherhood.o2o.config.Constants;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;
import com.skynet.library.message.MessageService;
import com.skynet.library.message.MessageUtils;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class ChatManager extends ChatAPI {

    /**
     * Probably be <b>/mnt/sdcard/mofang/</b>
     */
    public static final String SDCARD_ROOT_DIR = Environment
            .getExternalStorageDirectory().getPath() + "/mofang/";
    private static final String TAG = "ChatAPI";
    private static ChatManager sManager;

    static {
        File rootDir = new File(SDCARD_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    private Context mAppContext;
    private NewMsgFirstWatcher mFirstWatcher;
    private NewMsgLastWatcher mLastWatcher;
    private SendMsgCallbackWatcher mSendMsgWatcher;
    private long mCurrUid = -1L;
    private DatabaseHandler mDbHandler;
    private ChatUserLoaderInterface mUserLoader;
    private ChatSettings mSettings;
    private HashMap<String, ArrayList<UiWatcherWrapper>> mHandlers = new HashMap<String, ArrayList<UiWatcherWrapper>>();
    private String mImagePath;
    private String mVoicePath;

    private String mCameraPath;
    private String mMultimediaPath;
    private String mVideoPath;

    private ChatManager(Context cxt) {
        mAppContext = cxt.getApplicationContext();
        mSettings = new ChatSettings(mAppContext);
        mFirstWatcher = new NewMsgFirstWatcher();
        mLastWatcher = new NewMsgLastWatcher(mAppContext);
        mSendMsgWatcher = new SendMsgCallbackWatcher();
        registerInternal(MessageManager.EVENT_NEW_MSG_ARRIVED,
                MAX_PRIORITY + 1, mFirstWatcher, false);
        registerInternal(MessageManager.EVENT_NEW_MSG_ARRIVED,
                MIN_PRIORITY - 1, mLastWatcher, false);
        registerInternal(MessageManager.EVENT_SEND_MSG, MAX_PRIORITY + 1,
                mSendMsgWatcher, false);
        registerInternal(MessageManager.EVENT_SEND_MULTIMEDIA_MSG,
                MAX_PRIORITY + 1, mSendMsgWatcher, false);
    }

    ;

    public static ChatManager getDefault(Context cxt) {
        if (sManager == null) {
            sManager = new ChatManager(cxt);
        }
        return sManager;
    }

    public void clearNotification(long sessionId) {
        if (Logger.DEBUG) {
            Logger.v(TAG, "clearNotification, sessionId=" + sessionId);
        }
        mLastWatcher.clearSessionNotification(sessionId);
    }

    ;

    public void onCallback(String event, HashMap<String, Object> extras) {
        onCallback(event, 0, null, extras);
    }

    public void onCallback(Context cxt, Intent intent) {
        String event = intent
                .getStringExtra(MessageManager.EXTRA_CALLBACK_EVENT);
        int code = intent.getIntExtra(MessageManager.EXTRA_CALLBACK_CODE, -1);
        onCallback(event, code, intent, null);
    }

    private void onCallback(String event, int code, Intent intent,
                            HashMap<String, Object> extras) {
        synchronized (mHandlers) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "onCallback(), [event=" + event + "]");
            }
            if (event == null) {
                Logger.e(TAG, "should specify a non-null callback event");
                return;
            }
            ArrayList<UiWatcherWrapper> list = mHandlers.get(event);
            if (list == null) {
                // No watcher to handle this
                return;
            }
            Collections.sort(list);
            int size = list.size();
            for (int i = size - 1; i >= 0; i--) {
                UiWatcher h = list.get(i).watcherRef.get();
                if (h != null) {
                    if (extras == null) {
                        if (Logger.DEBUG) {
                            Logger.i(TAG, "new extras for watchers");
                        }
                        extras = new HashMap<String, Object>();
                    }
                    h.mAborted = false;
                    h.watchAndHandle(event, code, intent, extras);
                    if (h.mAborted) {
                        if (Logger.DEBUG) {
                            Logger.i(TAG, "aborted, stop notifying next");
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void registerUiWatcher(String event, UiWatcher handler) {
        registerInternal(event, 0, handler, true);
    }

    @Override
    public void registerUiWatcherWithPriority(String event, int priority,
                                              UiWatcher handler) {
        registerInternal(event, priority, handler, true);
    }

    private void registerInternal(String event, int priority,
                                  UiWatcher handler, boolean checkPriority) {
        synchronized (mHandlers) {
            if (checkPriority
                    && (priority > MAX_PRIORITY || priority < MIN_PRIORITY)) {
                throw new IllegalArgumentException("priority should between "
                        + MIN_PRIORITY + " AND " + MAX_PRIORITY);
            }
            if (event == null || handler == null) {
                return;
            }
            ArrayList<UiWatcherWrapper> list = mHandlers.get(event);
            if (list == null) {
                list = new ArrayList<UiWatcherWrapper>();
                UiWatcherWrapper wrapper = new UiWatcherWrapper();
                wrapper.priority = priority;
                wrapper.watcherRef = new WeakReference<UiWatcher>(handler);
                list.add(wrapper);
                mHandlers.put(event, list);
                return;
            }
            boolean found = false;
            for (UiWatcherWrapper wrapper : list) {
                UiWatcher h = wrapper.watcherRef.get();
                if (handler == h) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                UiWatcherWrapper wrapper = new UiWatcherWrapper();
                wrapper.watcherRef = new WeakReference<UiWatcher>(handler);
                wrapper.priority = priority;
                list.add(wrapper);
            }
        }

    }

    @Override
    public void unregisterUiWatcher(String event, UiWatcher handler) {
        synchronized (mHandlers) {
            if (event == null || handler == null) {
                return;
            }
            ArrayList<UiWatcherWrapper> list = mHandlers.get(event);
            if (list == null) {
                return;
            }
            Iterator<UiWatcherWrapper> iterator = list.iterator();
            while (iterator.hasNext()) {
                UiWatcherWrapper wrapper = iterator.next();
                if (wrapper.watcherRef.get() == handler) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public DatabaseHandler getDbHandler() {
        return mDbHandler;
    }

    @Override
    public ChatSettings getSettings() {
        return mSettings;
    }

    @Override
    public void openGroupChatUI(Activity activity, long groupId) {
        if (mCurrUid == -1L) {
            Log.e(TAG, "openGroupChatUI(), setUserId(long) not called yet");
            return;
        }
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRAS_CURRENT_USER_ID, mCurrUid);
        intent.putExtra(ChatActivity.EXTRAS_SESSION_TYPE,
                MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue());
        intent.putExtra(ChatActivity.EXTRAS_TARGET_ID, groupId);
        activity.startActivity(intent);
    }

    @Override
    public void openSingleChatUI(Activity activity, long targetUid, String avatarUrl, String nickname,int gender) {
        if (mCurrUid == -1L) {
            Log.e(TAG, "openSingleChatUI(), setUserId(long) not called yet");
            return;
        }
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRAS_CURRENT_USER_ID, mCurrUid);
        intent.putExtra(ChatActivity.EXTRAS_SESSION_TYPE,
                MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue());
        intent.putExtra(ChatActivity.EXTRAS_TARGET_ID, targetUid);
        intent.putExtra(ChatActivity.EXTRAS_AVATAR_URL, avatarUrl);
        intent.putExtra(ChatActivity.EXTRAS_NICKNAME, nickname);
        intent.putExtra(ChatActivity.EXTARS_GENDER, gender);

        activity.startActivity(intent);
    }

    public long getUserId() {
        return mCurrUid;
    }

    @Override
    public void setUserId(long uid) {
        if (uid != mCurrUid) {
            mCurrUid = uid;
            ensureUserDirs(md5(uid));
            mDbHandler = new ChatDbHelper(mAppContext, String.valueOf(uid))
                    .getHandler();
            Intent intent = new Intent(mAppContext, MessageService.class);
            intent.putExtra(MessageService.EXTRAS_CMD,
                    MessageService.CMD_SET_USER);
            intent.putExtra(MessageService.EXTRAS_USER_ID, uid);
            mAppContext.startService(intent);
        }
    }

    private String md5(long uid) {
        // TODO
        return String.valueOf(uid);
    }

    public ChatUserLoaderInterface getUserLoader() {
        return mUserLoader;
    }

    @Override
    public void setUserLoader(ChatUserLoaderInterface i) {
        mUserLoader = i;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public String getVoicePath() {
        return mVoicePath;
    }

    public String getCameraPath() {
        return mCameraPath;
    }

    public String getMultimediaPath() {
        return mMultimediaPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    private void ensureUserDirs(String uid) {
        File userDir = new File(SDCARD_ROOT_DIR, uid);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        File voiceDir = new File(userDir, "voices");
        if (!voiceDir.exists()) {
            voiceDir.mkdir();
        }
        mVoicePath = voiceDir.getPath();

        File imageDir = new File(userDir, "images");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        mImagePath = imageDir.getPath();

        File cameraDir = new File(userDir, "camera");
        if (!cameraDir.exists()) {
            cameraDir.mkdir();
        }
        mCameraPath = cameraDir.getPath();

        File mmDir = new File(userDir, "multimedia");
        if (!mmDir.exists()) {
            mmDir.mkdir();
        }
        mMultimediaPath = mmDir.getPath();

        File videoDir = new File(userDir, "video");
        if (!videoDir.exists()) {
            videoDir.mkdir();
        }
        mVideoPath = videoDir.getPath();
    }

    public static class MsgEntity {

        public int msgType;

        public int msgSubType;

        /**
         * {@link MessageManager.MessageOptions}
         */
        public MessageManager.MessageOptions options;

        public byte[] content;
        public long sender;
        public long receiver;
        public int sendTime;

        public boolean blocked;
        public boolean doNotNotify;
        public MessageItem msgItem;
        public long sessionId;

        public MultimediaImage image;
        public MultimediaAudio audio;

    }

    public static class MultimediaImage {
        public String fileId;
        public String thumbId;
    }

    public static class MultimediaAudio {
        public String fileId;
    }

    class UiWatcherWrapper implements Comparable<UiWatcherWrapper> {
        WeakReference<UiWatcher> watcherRef;
        int priority;

        @Override
        public int compareTo(UiWatcherWrapper another) {
            if (this.priority < another.priority) {
                return -1;
            } else if (this.priority > another.priority) {
                return 1;
            }
            return 0;
        }
    }

    private class SendMsgCallbackWatcher extends UiWatcher {

        @Override
        public void watchAndHandle(String callbackEvent, int callbackCode,
                                   Intent data, HashMap<String, Object> map) {
            long msgId = 0L;
            boolean suc = false;
            if (callbackEvent.equals(MessageManager.EVENT_SEND_MSG)) {
                msgId = data.getLongExtra(MessageManager.EXTRA_MSG_ID, 0L);
                suc = callbackCode == MessageManager.CODE_SEND_SUCCEEDED;
            } else if (callbackEvent
                    .equals(MessageManager.EVENT_SEND_MULTIMEDIA_MSG)) {
                msgId = data.getLongExtra(
                        MessageManager.EXTRA_SEND_MULTIMEDIA_MSG_ID, 0L);
                suc = callbackCode == MessageManager.CODE_SEND_MULTIMEDIA_MSG_SUCCEEDED;
            }
            map.put("msg_id", msgId);
            map.put("success", suc);

            int sendStatus = suc ? MessageItem.STATUS_SEND_SUCCEEDED
                    : MessageItem.STATUS_SEND_FAILED;
            ContentValues values = new ContentValues();
            values.put(MessageItem.COL_SEND_STATUS, sendStatus);
            values.put(MessageItem.COL_UPDATE_TIME,
                    System.currentTimeMillis() / 1000L);
            mDbHandler.update(MessageItem.TABLE, values, MessageItem.COL_ID
                    + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    private class NewMsgFirstWatcher extends UiWatcher {

        @Override
        public void watchAndHandle(String callbackEvent, int callbackCode,
                                   Intent intent, HashMap<String, Object> extras) {
            ArrayList<MessageManager.ReceivedMessage> msgs = intent
                    .getParcelableArrayListExtra(MessageManager.EXTRA_NEW_MSG_MSGS);
            ArrayList<MsgEntity> entities = new ArrayList<MsgEntity>(
                    msgs.size());
            extras.put("entities", entities);
            for (MessageManager.ReceivedMessage msg : msgs) {
                if (msg.msgType == MessageManager.MessageEntity.MsgType.RTC_VIDEO.getValue()
                        || msg.msgType == MessageManager.MessageEntity.MsgType.RTC_AUDIO.getValue()) {
                    return;
                }

                int subType = msg.msgSubType;
                long senderUid = msg.senderId;
                byte[] content = msg.content;
                long receiver = msg.receiver;
                if (senderUid != -1) {
                    AccountComponent.shareComponent().saveUserAvatar(senderUid);
                }
                MessageManager.MessageOptions options = msg.options;
                int msgType = msg.msgType;
                int sendTime = (int) msg.sendTime;

                if (senderUid == mSettings.getSystemMsgSenderId()) {
                    Intent sysIntent = new Intent(mAppContext.getPackageName()
                            + ".system.message");
                    sysIntent.putExtra("content", content);
                    mAppContext.sendBroadcast(sysIntent);
                    abort();
                    return;
                }

                if (options != null && options.anonymField != -1) {
                    if (senderUid == mCurrUid) {
                        subType = options.anonymField == 1 ? MessageManager.MessageEntity.ChatType.ANONYMOUS_OUTBOUND
                                .getValue()
                                : MessageManager.MessageEntity.ChatType.ANONYMOUS_INBOUND
                                .getValue();
                    } else {
                        subType = options.anonymField == 1 ? MessageManager.MessageEntity.ChatType.ANONYMOUS_INBOUND
                                .getValue()
                                : MessageManager.MessageEntity.ChatType.ANONYMOUS_OUTBOUND
                                .getValue();
                    }
                }

                MsgEntity entity = new MsgEntity();
                entity.msgSubType = subType;
                entity.msgType = msgType;
                entity.receiver = receiver;
                entity.sender = senderUid;
                entity.options = options;
                entity.sendTime = sendTime;
                if (Logger.DEBUG) {
                    long t = entity.options.serializeToLong();
                    Logger.d(TAG, "receive options=0x" + Long.toHexString(t));
                }
                if (entity.options.encrypted) {
                    entity.content = MessageUtils.decrypt(content);
                } else {
                    entity.content = content;
                }
                if (senderUid == Constants.SYSTEM_ID) {
                    SystemMsgBean.addSystemMsg(new String(entity.content));
                    PersonalHelper.sendReceiveSystemMsgBoradCast(mAppContext);
                }
                if (entity.msgType == MessageManager.MessageEntity.MsgType.IMAGE
                        .getValue()) {
                    try {
                        JSONObject jo = new JSONObject(new String(content));
                        String thumbId = jo.getString("thumb_id");
                        String fileId = jo.getString("file_id");
                        entity.image = new MultimediaImage();
                        entity.image.fileId = fileId;
                        entity.image.thumbId = thumbId;
                    } catch (Exception e) {
                        Log.e(TAG,
                                "error occurred when resolving a IMAGE msg, abort");
                        if (Logger.DEBUG) {
                            e.printStackTrace();
                        }
                        // If this message is invalid, then it would be avoid to
                        // being adding to the entities
                        continue;
                    }
                } else if (entity.msgType == MessageManager.MessageEntity.MsgType.VOICE
                        .getValue()) {
                    try {
                        JSONObject jo = new JSONObject(new String(content));
                        String fileId = jo.getString("file_id");
                        entity.audio = new MultimediaAudio();
                        entity.audio.fileId = fileId;
                    } catch (Exception e) {
                        Log.e(TAG,
                                "error occurred when resolving a VOICE msg, abort");
                        if (Logger.DEBUG) {
                            e.printStackTrace();
                        }
                        // If this message is invalid, then it would be avoid to
                        // being adding to the entities
                        continue;
                    }

                }
                entities.add(entity);
                long t = entity.sendTime != -1 ? entity.sendTime : System
                        .currentTimeMillis() / 1000L;
                updateOrCreateSession(msgType, subType, t, entity);
            }
        }

        private void updateOrCreateSession(int msgType, int subType, long t,
                                           MsgEntity entity) {
            SessionItem oldSession = null;
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            sql.append(SessionItem.TABLE);
            sql.append(" WHERE ");
            sql.append(SessionItem.COL_TYPE);
            sql.append(" = ");
            sql.append(subType);
            if (subType == MessageManager.MessageEntity.ChatType.SINGLE_CHAT
                    .getValue()
                    || subType == MessageManager.MessageEntity.ChatType.ANONYMOUS_OUTBOUND
                    .getValue()
                    || subType == MessageManager.MessageEntity.ChatType.ANONYMOUS_INBOUND
                    .getValue()) {

                sql.append(" AND ");
                sql.append(SessionItem.COL_TARGET);
                sql.append(" = ");
                sql.append(entity.sender);
            } else if (subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT
                    .getValue()) {
                sql.append(" AND ");
                sql.append(SessionItem.COL_TARGET);
                sql.append(" = ");
                sql.append(entity.receiver);
            }
            sql.append(" LIMIT 1");
            if (Logger.DEBUG) {
                Logger.d(TAG, "updateOrCreateSession, sql=" + sql.toString());
            }
            Cursor c = mDbHandler.query(sql.toString(), null);
            if (c != null) {
                if (c.moveToNext()) {
                    oldSession = new SessionItem();
                    oldSession.id = c.getLong(c
                            .getColumnIndex(SessionItem.COL_ID));
                    oldSession.unreadCount = c.getInt(c
                            .getColumnIndex(SessionItem.COL_UNREAD_COUNT));
                    oldSession.blocked = c.getInt(c
                            .getColumnIndex(SessionItem.COL_BLOCKED)) == 1;
                    oldSession.doNotNotify = c.getInt(c
                            .getColumnIndex(SessionItem.COL_DO_NOT_NOTIFY)) == 1;
                }
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
            if (oldSession != null) {
                if (oldSession.blocked) {
                    // abort();
                    entity.blocked = true;
                }
                if (oldSession.doNotNotify) {
                    // extras.put("do_not_notify", true);
                    entity.doNotNotify = true;
                }
            }

            String msg = "";
            if (subType == MessageManager.MessageEntity.ChatType.SINGLE_CHAT
                    .getValue()
                    || subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT
                    .getValue()
                    || subType == MessageManager.MessageEntity.ChatType.ANONYMOUS_INBOUND
                    .getValue()
                    || subType == MessageManager.MessageEntity.ChatType.ANONYMOUS_OUTBOUND
                    .getValue()) {
                if (entity.options.shouldBeDeletedAfterViewed) {
                    msg = mAppContext
                            .getString(R.string.default_session_msg_volatile);
                } else if (msgType == MessageManager.MessageEntity.MsgType.IMAGE
                        .getValue()) {
                    msg = mAppContext
                            .getString(R.string.default_session_msg_pic);
                } else if (msgType == MessageManager.MessageEntity.MsgType.VOICE
                        .getValue()) {
                    msg = mAppContext
                            .getString(R.string.default_session_msg_voice);
                } else if (msgType == MessageManager.MessageEntity.MsgType.TEXT
                        .getValue()) {
                    msg = new String(entity.content);
                } else if (msgType == MessageManager.MessageEntity.MsgType.LOCATION
                        .getValue()) {
                    msg = mAppContext
                            .getString(R.string.default_session_msg_location);
                }
            } else {
                msg = mAppContext
                        .getString(R.string.default_session_msg_other_sub_type);
                // extras.put("do_not_notify", true);
                entity.doNotNotify = true;
            }

            long sessionId = -1L;
            if (oldSession != null) {
                sessionId = oldSession.id;
                ContentValues values = new ContentValues();
                values.put(SessionItem.COL_UNREAD_COUNT,
                        ++oldSession.unreadCount);
                if (subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT
                        .getValue()) {
                    values.put(SessionItem.COL_MSG,
                            String.valueOf(entity.sender) + ": " + msg);
                } else {
                    values.put(SessionItem.COL_MSG, msg);
                }
                values.put(SessionItem.COL_MODIFIED_DATE, t);
                mDbHandler.update(SessionItem.TABLE, values, SessionItem.COL_ID
                                + " = ?",
                        new String[]{String.valueOf(oldSession.id)});
            } else {
                ContentValues values = new ContentValues();
                values.put(SessionItem.COL_TYPE, entity.msgSubType);
                if (subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT
                        .getValue()) {
                    values.put(SessionItem.COL_TARGET, entity.receiver);
                    values.put(SessionItem.COL_MSG,
                            String.valueOf(entity.sender) + ": " + msg);
                } else {
                    values.put(SessionItem.COL_TARGET, entity.sender);
                    values.put(SessionItem.COL_MSG, msg);
                }
                values.put(SessionItem.COL_UNREAD_COUNT, 1);
                values.put(SessionItem.COL_CREATE_DATE, t);
                values.put(SessionItem.COL_MODIFIED_DATE, t);
                sessionId = mDbHandler.insertAndWait(SessionItem.TABLE, values);
            }
            saveMessage(t, entity, sessionId);

        }

        private void saveMessage(long t, MsgEntity entity, long sessionId) {
            MessageItem item = new MessageItem();
            item.content = entity.content;
            item.createTime = t;
            item.msgType = entity.msgType;
            item.options = entity.options;
            item.image = entity.image;
            item.audio = entity.audio;
            item.receiver = entity.receiver;
            item.sender = entity.sender;
            item.sessionId = sessionId;
            item.sessionType = entity.msgSubType;
            item.updateTime = t;
            item.volAliveSecs = entity.options.durationBeforeDeleted;
            entity.msgItem = item;
            entity.sessionId = sessionId;
            // extras.put("msg_obj", item);
            // extras.put("session_id", sessionId);
            ContentValues values = new ContentValues();
            values.put(MessageItem.COL_UPDATE_TIME, t);
            values.put(MessageItem.COL_CONTENT, entity.content);
            values.put(MessageItem.COL_CREATE_TIME, t);
            values.put(MessageItem.COL_OPTIONS, item.options.serializeToLong());
            values.put(MessageItem.COL_VOL_ALIVE_SECS,
                    item.options.durationBeforeDeleted);

            values.put(MessageItem.COL_RECEIVER, entity.receiver);
            values.put(MessageItem.COL_SENDER, entity.sender);
            values.put(MessageItem.COL_SESSION_ID, sessionId);
            values.put(MessageItem.COL_SESSION_TYPE, entity.msgSubType);
            values.put(MessageItem.COL_TYPE, entity.msgType);
            item.id = mDbHandler.insertAndWait(MessageItem.TABLE, values);
            ChatCompent.shareCompent(MyApplication.mApplication).sendMsgBroadCast(item.receiver);
        }
    }

    class NewMsgLastWatcher extends UiWatcher {

        private int mStartMsgNotificationId = 100000;
        private Context mContext;
        private NotificationManager mNotificationManager;
        private BroadcastReceiver mCancelReceiver;
        private LongSparseArray<NotifyInfo> mNotifyInfoMap = new LongSparseArray<NotifyInfo>();

        NewMsgLastWatcher(Context cxt) {
            mContext = cxt;
            mNotificationManager = (NotificationManager) cxt
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void clearSessionNotification(long sessionId) {
            if (sessionId > 0L) {
                NotifyInfo info = mNotifyInfoMap.get(sessionId);
                if (info != null) {
                    info.unreadCount = 0;
                    mNotificationManager.cancel(info.notificationId);
                }
                if (mDbHandler != null) {
                    ContentValues values = new ContentValues();
                    values.put(SessionItem.COL_UNREAD_COUNT, 0);
                    mDbHandler.update(SessionItem.TABLE, values,
                            SessionItem.COL_ID + " = " + sessionId, null);
                }
            }
        }

        private void registerCancelReceiver() {
            if (mCancelReceiver == null) {
                mCancelReceiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Uri uri = intent.getData();
                        if (uri != null) {
                            String last = uri.getLastPathSegment();
                            if (Logger.DEBUG) {
                                Logger.d(TAG, "uri=" + uri.toString());
                            }
                            if (last != null) {
                                Long sessionId = Long.valueOf(last);
                                if (Logger.DEBUG) {
                                    Logger.d(TAG, "sessionId=" + uri.toString());
                                }
                                mNotifyInfoMap.remove(sessionId);
                            }
                        }
                    }
                };
                IntentFilter f = new IntentFilter(mAppContext.getPackageName()
                        + ".chat.notificationCanceledByUser");
                mAppContext.registerReceiver(mCancelReceiver, f);
            }
        }

        @Override
        public void watchAndHandle(String callbackEvent, int callbackCode,
                                   Intent data, HashMap<String, Object> map) {
            if (!mSettings.isNotificationEnabled()) {
                return;
            }
            @SuppressWarnings("unchecked")
            ArrayList<MsgEntity> entities = (ArrayList<MsgEntity>) map
                    .get("entities");
            for (MsgEntity entity : entities) {
                if (entity.doNotNotify) {
                    continue;
                }
                if (entity.msgSubType == MessageManager.MessageEntity.MsgType.RTC_AUDIO.getValue()
                        || entity.msgSubType == MessageManager.MessageEntity.MsgType.RTC_VIDEO.getValue()) {
                    continue;
                }
                long sessionId = entity.sessionId;
                registerCancelReceiver();

                Context cxt = mContext;
                Resources r = cxt.getResources();
                String pkg = cxt.getPackageName();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        cxt);
                builder.setSmallIcon(R.drawable.icon_statusbar);

                boolean isGroup = entity.msgSubType == MessageManager.MessageEntity.ChatType.GROUP_CHAT
                        .getValue();

                Intent ci = new Intent(cxt, ChatActivity.class);
                ci.setAction(pkg + "/" + System.currentTimeMillis());
                ci.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ci.putExtra(ChatActivity.EXTRAS_SESSION_ID, sessionId);
                ci.putExtra(ChatActivity.EXTRAS_CURRENT_USER_ID, getUserId());
                if (isGroup) {
                    ci.putExtra(ChatActivity.EXTRAS_SESSION_TYPE,
                            MessageManager.MessageEntity.ChatType.GROUP_CHAT
                                    .getValue());
                    ci.putExtra(ChatActivity.EXTRAS_TARGET_ID, entity.receiver);
                } else {
                    ci.putExtra(ChatActivity.EXTRAS_SESSION_TYPE,
                            MessageManager.MessageEntity.ChatType.SINGLE_CHAT
                                    .getValue());
                    ci.putExtra(ChatActivity.EXTRAS_TARGET_ID, entity.sender);
                }
                PendingIntent contentIntent = PendingIntent.getActivity(cxt,
                        1001, ci, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);

                Intent di = new Intent(pkg + ".chat.notificationCanceledByUser");
                di.setData(Uri.parse("skynet://sdk.im.chat/" + sessionId));
                PendingIntent deleteIntent = PendingIntent.getBroadcast(cxt,
                        1002, di, 0);
                builder.setDeleteIntent(deleteIntent);
                int flags = 0;
                if (mSettings.isLightEnabled()) {
                    flags |= Notification.DEFAULT_LIGHTS;
                }
                if (mSettings.isSoundEnabled()) {
                    flags |= Notification.DEFAULT_SOUND;
                }
                if (mSettings.isVibrateEnabled()) {
                    flags |= Notification.DEFAULT_VIBRATE;
                }
                builder.setDefaults(flags);
                builder.setAutoCancel(true);
                builder.setTicker(r.getString(R.string.statusbar_msg_ticker));
                NotifyInfo ni = mNotifyInfoMap.get(sessionId);
                int unreadCount = 0;
                int notiId = 0;
                if (ni != null) {
                    unreadCount = ++ni.unreadCount;
                    notiId = ni.notificationId;
                } else {
                    unreadCount = 1;
                    mStartMsgNotificationId++;
                    notiId = mStartMsgNotificationId;
                    ni = new NotifyInfo();
                    ni.unreadCount = 1;
                    ni.notificationId = notiId;
                    mNotifyInfoMap.put(sessionId, ni);
                }
                String contentText = "";
                if (entity.options.shouldBeDeletedAfterViewed) {
                    contentText = r
                            .getString(R.string.default_session_msg_volatile);
                } else if (entity.msgType == MessageManager.MessageEntity.MsgType.IMAGE
                        .getValue()) {
                    contentText = r.getString(R.string.default_session_msg_pic);
                } else if (entity.msgType == MessageManager.MessageEntity.MsgType.TEXT
                        .getValue()) {
                    if (entity.content != null) {
                        contentText = new String(entity.content);
                    }
                } else if (entity.msgType == MessageManager.MessageEntity.MsgType.VOICE
                        .getValue()) {
                    contentText = r
                            .getString(R.string.default_session_msg_voice);
                } else if (entity.msgType == MessageManager.MessageEntity.MsgType.LOCATION
                        .getValue()) {
                    contentText = r
                            .getString(R.string.default_session_msg_location);
                }
                if (isGroup) {
                    contentText = String.valueOf(entity.sender) + ": "
                            + contentText;
                }
                if (unreadCount > 1) {
                    contentText = String.format(
                            r.getString(R.string.statusbar_msg_count),
                            new Object[]{unreadCount}) + contentText;
                }
                String baseTitle = isGroup ? r
                        .getString(R.string.statusbar_msg_type_group) : r
                        .getString(R.string.statusbar_msg_type_friend);
                if (entity.sender == Constants.SYSTEM_ID) {
                    baseTitle = r.getString(R.string.statusbar_msg_type_system);
                }
                String contact = isGroup ? String.valueOf(entity.receiver)
                        : String.valueOf(entity.sender);

                builder.setContentTitle(String.format(baseTitle,
                        new Object[]{contact}));
                builder.setContentText(contentText);
                mNotificationManager.notify(notiId, builder.build());
            }
        }

        private class NotifyInfo {
            int unreadCount;
            int notificationId;
        }
    }

}
