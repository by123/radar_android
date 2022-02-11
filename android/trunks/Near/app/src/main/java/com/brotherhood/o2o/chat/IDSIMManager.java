package com.brotherhood.o2o.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.UserInfoBean;
import com.brotherhood.o2o.chat.db.dao.IMGroupDao;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.db.dao.IMNewFriendDao;
import com.brotherhood.o2o.chat.db.dao.IMSystemMsgDao;
import com.brotherhood.o2o.chat.db.helper.BaseSQLiteHelper;
import com.brotherhood.o2o.chat.db.service.IMDBGroupService;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.chat.helper.BitmapHelper;
import com.brotherhood.o2o.chat.helper.IMUserDataHelper;
import com.brotherhood.o2o.chat.helper.Utils;
import com.brotherhood.o2o.chat.model.IMApplyInfoBean;
import com.brotherhood.o2o.chat.model.IMChatBean;
import com.brotherhood.o2o.chat.model.IMLatestMsgBean;
import com.brotherhood.o2o.chat.model.IMSystemMsgBean;
import com.brotherhood.o2o.chat.model.IMUserBean;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.IDSEnvManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.GetAvatarRequest;
import com.brotherhood.o2o.ui.activity.MainActivity;
import com.brotherhood.o2o.ui.activity.SplashActivity;
import com.brotherhood.o2o.util.ActivityUtils;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.Res;
import com.skynet.library.login.net.LoginManager;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;
import com.skynet.library.message.MessageManager.MessageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.skynet.library.message.MessageManager.MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY;
import static com.skynet.library.message.MessageManager.MessageEntity.ChatType.GROUP_CHAT;
import static com.skynet.library.message.MessageManager.MessageEntity.ChatType.SINGLE_CHAT;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class IDSIMManager {

    private final static String tag = "IDSIMManager";
    private static IDSIMManager instance = new IDSIMManager();
    private final ArrayList<MessageManager.ReceivedMessage> mMultiMsgList;
    private Context mContext;
    private MessageManager mMgr;
    private static final String SDCARD_ROOT_DIR = Environment
            .getExternalStorageDirectory().getPath() + "/near/IDS_IM/";
    // =======================================================
    private static long mMsgId = System.currentTimeMillis();
    private MsgReceiver mReceiver;
    private long mMyUid;
    // =======================================================
    public final static String TIP_MSG_START = "tipStart:";

    // ==========OnSendResult=============================================
    public static final String ACTION_IM_ON_SEND_RESULT = "ACTION_IM_ON_SEND_RESULT";
    public static final String KEY_SEND_SUC = "KEY_SEND_SUC";
    public static final String KEY_SEND_UID = "KEY_SEND_UID";
    // =======================================================
    public static final String ACTION_IM_ON_REC_MSG = "ACTION_IM_ON_REC_MSG";
    public static final String KEY_REC_MSG_BEAN = "KEY_REC_MSG_BEAN";
    public static final String ACTION_IM_ON_REC_MSG_MULTI = "ACTION_IM_ON_REC_MSG_MULTI";
    public static final String KEY_REC_MSG_BEAN_MULTI = "KEY_REC_MSG_BEAN_MULTI";
    // ==========logout =============================================
    public static final String ACTION_IM_LOGOUT = "ACTION_IM_LOGOUT";
    // ==========updateGroupName=============================================
    public static final String ACTION_IM_UPDATE_GROUP_NAME_MSG = "ACTION_IM_UPDATE_GROUP_NAME_MSG";
    public static final String KEY_GROUP_NAME = "KEY_GROUP_NAME";
    public static final String ACTION_IM_QUIT_GROUP = "ACTION_IM_QUIT_GROUP";
    public static final String KEY_GROUP_ID = "KEY_GROUP_ID";

    private String TAG = "IDSIManager";


    private IDSIMManager() {
        this.mContext = NearApplication.mInstance.getApplicationContext();
        mReceiver = new MsgReceiver();
        IntentFilter filter = new IntentFilter(mContext.getPackageName()
                + ".im_callback");
        mContext.registerReceiver(mReceiver, filter);
        mMultiMsgList = new ArrayList<>();
        this.mMgr = MessageManager.getDefault(mContext);
    }

    public static IDSIMManager getInstance() {
        return instance;
    }

    public void initIP() {
        mMgr.setIpAndPort(IDSEnvManager.getInstance().getImServer(), IDSEnvManager.getInstance().getPort(),
                IDSEnvManager.getInstance().getImServerHttp());
    }

    public void init() {
        initFilePath();
        Logger.i(tag, "=======IM LOGIN==========");
        mMgr.initializeSdk(Constants.DGC_APP_KEY,
                Constants.DGC_APP_SECRET, MessageManager.GameType.ONLINE,
                LoginManager.getInstance().getToken(), LoginManager.getInstance().getTokenSecret());
        mMyUid = Long.valueOf(AccountManager.getInstance().getUser().mUid);
        Logger.DEBUG = true;
    }

    // =======================================================

    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent
                    .getStringExtra(MessageManager.EXTRA_CALLBACK_EVENT);
            if (event.equals(MessageManager.EVENT_SEND_MSG) || event
                    .equals(MessageManager.EVENT_SEND_MULTIMEDIA_MSG)) {
                // 发送消息结果
                onSendResult(intent);
            } else if (event.equals(MessageManager.EVENT_NEW_MSG_ARRIVED)) {
                // 收到新消息
                onReceiveMsg(intent);
            } else if (MessageManager.EVENT_TOKEN_EXPIRED.equals(event)) {
                //  token过期
                onTokenExpired(intent);
            } else if (MessageManager.EVENT_LOGOUT.equals(event)) {
                //  下线通知
                onLogout(intent);
            }
        }
    }


    private void onSendResult(final Intent intent) {
        Log.i(tag, "========onSendResult=========");
        String event = intent
                .getStringExtra(MessageManager.EXTRA_CALLBACK_EVENT);

        long msgId = 0L;
        boolean suc = false;
        int code = intent.getIntExtra(MessageManager.EXTRA_CALLBACK_CODE, -1);
        if (event.equals(MessageManager.EVENT_SEND_MSG)) {
            msgId = intent.getLongExtra(MessageManager.EXTRA_MSG_ID, 0L);
            suc = code == MessageManager.CODE_SEND_SUCCEEDED;
        } else if (event
                .equals(MessageManager.EVENT_SEND_MULTIMEDIA_MSG)) {
            msgId = intent.getLongExtra(
                    MessageManager.EXTRA_SEND_MULTIMEDIA_MSG_ID, 0L);
            suc = code == MessageManager.CODE_SEND_MULTIMEDIA_MSG_SUCCEEDED;
        }
        final boolean send_result = suc;
        final long send_msg_id = msgId;

        IMDBService.getUIDFromMsgId(msgId, new IMDBService.DBListener() {
            @Override
            public void onResult(Object obj) {
                Bundle b = (Bundle) obj;
                long uid = b.getLong("uid", 0);
                boolean isGroup = b.getBoolean("isGroup");

                Intent i = new Intent(ACTION_IM_ON_SEND_RESULT);
                i.putExtra(MessageManager.EXTRA_MSG_ID, send_msg_id);
                i.putExtra(KEY_SEND_SUC, send_result);
                i.putExtra(KEY_SEND_UID, uid);
                mContext.sendBroadcast(i);
                IMDBService.updateMsgSendState(isGroup, uid, send_msg_id, send_result ? IMChatBean.SendState.STATUS_SEND_SUCCESS :
                        IMChatBean.SendState.STATUS_SEND_FAILED);
            }
        });

    }

    private void onReceiveMsg(Intent intent) {
        Log.i(tag, "========onReceiveMsg=========");
        ArrayList<MessageManager.ReceivedMessage> msgs = intent
                .getParcelableArrayListExtra(MessageManager.EXTRA_NEW_MSG_MSGS);
        int size = msgs.size();
        if (size == 10) {
            mMultiMsgList.addAll(msgs);
        } else if (size >= 0 && size < 10) {
            if (size == 1 && mMultiMsgList.size() == 0) {
                dealWithGeneralMsg(msgs);

            } else if (size > 0) {
                mMultiMsgList.addAll(msgs);
                Collections.sort(mMultiMsgList, new IMComparator()); // 排序
                ArrayList<MessageManager.ReceivedMessage> list = new ArrayList<>();
                list.addAll(mMultiMsgList);
                mMultiMsgList.clear();
                dealWithMultiMsgs(list);
            }
        }
    }

    private void dealWithGeneralMsg(ArrayList<MessageManager.ReceivedMessage> msgs) {
        for (MessageManager.ReceivedMessage msg : msgs) {
            if (msg.senderId == IMContants.SYSTEM_ID &&
                    msg.msgSubType == MessageEntity.ChatType.GROUP_CHAT.getValue()) {
                //系统消息
                dealWithSystemMsg(msg);

            } else if (msg.msgSubType == SINGLE_CHAT.getValue()
                    || msg.msgSubType == MessageEntity.ChatType.GROUP_CHAT.getValue()
                    || msg.msgSubType == MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY.getValue()) {
                //普通聊天消息
                final IMChatBean bean = createBean(msg);
                IMDBService.addMsg(bean, new IMDBService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        long id = (long) obj;
                        bean.id = id;
                        sendMsgBroadcast(bean);
                        sendNotification(bean);

                        IDSIMQueue.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(bean);
                                IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);
                            }
                        });
                    }
                });
            } else {// 提示消息（加群等..)
                praseTipMsg(msg);
            }
        }
    }

    private void dealWithMultiMsgs(ArrayList<MessageManager.ReceivedMessage> msgs) {
        final LinkedList<LinkedList> singleChatLists = new LinkedList<>();
        final LinkedList<LinkedList> groupChatLists = new LinkedList<>();
        final LinkedList<IMSystemMsgBean> sysMsgList = new LinkedList<>();
        final LinkedList<IMChatBean> applyMsgList = new LinkedList<>();

        final LinkedList<Long> uidList = new LinkedList<>();
        final LinkedList<Long> gidList = new LinkedList<>();

        for (MessageManager.ReceivedMessage msg : msgs) {
            if (msg.senderId == IMContants.SYSTEM_ID &&
                    msg.msgSubType == MessageEntity.ChatType.GROUP_CHAT.getValue()) {
                // 系统消息
                String content = null;
                try {
                    content = new String(msg.content, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (content == null) {
                    continue;
                }
                IMSystemMsgBean systemMsgBean = IMSystemMsgBean.getBean(content);
                sysMsgList.add(systemMsgBean);

            } else {
                final IMChatBean bean = createBeanOffLine(msg);
                if (bean == null) {
                    continue;
                }
                if (bean.subType == MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()) {
                    applyMsgList.add(bean);
                }
                boolean isGroup = IMDBService.isGroup(bean.subType);
                if (isGroup) {
                    if (!gidList.contains(bean.groupId)) {
                        gidList.add(bean.groupId);
                        LinkedList<IMChatBean> list = new LinkedList<>();
                        list.add(bean);
                        groupChatLists.add(list);
                    } else {
                        int i = gidList.indexOf(bean.groupId);
                        groupChatLists.get(i).add(bean);
                    }
                } else {
                    if (!uidList.contains(bean.groupId)) {
                        uidList.add(bean.groupId);
                        LinkedList<IMChatBean> list = new LinkedList<>();
                        list.add(bean);
                        singleChatLists.add(list);
                    } else {
                        int i = uidList.indexOf(bean.groupId);
                        singleChatLists.get(i).add(bean);
                    }
                }
            }
        }

        if (sysMsgList.size() > 0) {
            IDSIMQueue.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    IMSystemMsgDao.getInstance(mContext).addMsgs(sysMsgList);
                    for (IMSystemMsgBean systemMsgBean : sysMsgList) {
                        IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(systemMsgBean);
                        IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);
                    }
                }
            });
        }

        if (singleChatLists.size() > 0) {
            IMDBService.addMsgs(singleChatLists, false, new IMDBService.DBListener() {
                @Override
                public void onResult(Object obj) {
                    LinkedList<LinkedList> lists = (LinkedList) obj;
                    for (LinkedList beans : lists) {
                        sendMsgsBroadcast(beans);
                    }
                }
            });
            IDSIMQueue.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    IMLatestMsgDao.getInstance(mContext).updateUnReadNumsAndInsertLastMsg(singleChatLists);
                }
            });
        }

        if (groupChatLists.size() > 0) {
            IMDBService.addMsgs(groupChatLists, true, new IMDBService.DBListener() {
                @Override
                public void onResult(Object obj) {
                    LinkedList<LinkedList> lists = (LinkedList) obj;
                    for (LinkedList beans : lists) {
                        sendMsgsBroadcast(beans);
                    }
                }
            });
            IDSIMQueue.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    IMLatestMsgDao.getInstance(mContext).updateUnReadNumsAndInsertLastMsg(groupChatLists);
                }
            });
        }

        if (!applyMsgList.isEmpty()) {
            IDSIMQueue.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    for (IMChatBean chatBean : applyMsgList) {
                        IMApplyInfoBean applyInfoBean = IMApplyInfoBean.getBean(chatBean);
                        IMNewFriendDao.getInstance(mContext).add(applyInfoBean);

                        chatBean.receiverId = IMContants.ACK_ID;
                        IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(chatBean);
                        IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);
                    }
                }
            });
        }
    }

    private void dealWithSystemMsg(MessageManager.ReceivedMessage msg) {
        String content = null;
        try {
            content = new String(msg.content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (content == null) {
            return;
        }
        final String c = content;
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMSystemMsgBean systemMsgBean = IMSystemMsgBean.getBean(c);
                IMSystemMsgDao.getInstance(mContext).addMsg(systemMsgBean);

                IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(systemMsgBean);
                IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);
            }
        });
    }


    private IMChatBean createBean(MessageManager.ReceivedMessage msg) {
        IMChatBean bean = new IMChatBean();
        bean.subType = msg.msgSubType;
        bean.sender = msg.senderId;
        String content = "";
        try {
            content = new String(msg.content, "UTF-8");
            if (msg.msgType == MessageEntity.MsgType.IMAGE.getValue()
                    || msg.msgType == MessageEntity.MsgType.VOICE.getValue()) {
                String e = new String(msg.extra.getBytes(), "UTF-8");
                byte[] extra = Base64.decode(e.getBytes(), Base64.DEFAULT);
                bean.extra = new String(extra);
            } else {
//                if (!TextUtils.isEmpty(msg.extra2)) {//从ios来
//                    byte[] eb = Base64.decode(msg.extra2, Base64.DEFAULT);
//                    String ex = new String(eb, "UTF-8");
////                    IMUserExtraBean extraBean = IMUserExtraBean.getBean(ex);
//                    bean.extra = ex;
//                } else {//从android来
//                    bean.extra = new String(msg.extra.getBytes());
//                }
                if (msg.extra2 != null && msg.extra2.length > 0) {
                    String e = new String(msg.extra2, "UTF-8");
                    bean.extra = e;
                } else if (!TextUtils.isEmpty(msg.extra)) {//理论上这个没用了
                    bean.extra = new String(msg.extra.getBytes());
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (bean.subType == MessageEntity.ChatType.SINGLE_CHAT.getValue()) {
            bean.receiverId = msg.receiver;
        } else if (bean.subType == MessageEntity.ChatType.GROUP_CHAT.getValue()) {
            bean.receiverId = msg.receiver;
            bean.groupId = msg.receiver;
        } else if (bean.subType == MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY.getValue()) {
            bean.receiverId = msg.receiver;
            bean.groupId = msg.receiver;
        }
        bean.content = content;
        if (msg.msgType == MessageEntity.MsgType.IMAGE.getValue()
                || msg.msgType == MessageEntity.MsgType.VOICE.getValue()) {
            bean.contentFilePath = content;
        }
        bean.hasRead = true;
        bean.msgType = msg.msgType;
        bean.time = msg.sendTime;
        bean.sendStatus = IMChatBean.SendState.STATUS_SEND_SUCCESS.getValue();
//        bean.msgIdOther = msg.
        return bean;
    }

    private IMChatBean createBeanOffLine(MessageManager.ReceivedMessage msg) {
        IMChatBean bean = new IMChatBean();
        int subType = msg.msgSubType;
        if (subType == MessageEntity.ChatType.SINGLE_CHAT.getValue()
                || subType == MessageEntity.ChatType.GROUP_CHAT.getValue()
                || subType == MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY.getValue()) {
            bean = createBean(msg);
        } else {
            bean = praseTipMsgOffLine(msg);
        }
        return bean;
    }

    private void praseTipMsg(MessageManager.ReceivedMessage msg) {
        final IMChatBean bean = new IMChatBean();
        bean.msgType = MessageEntity.MsgType.TEXT.getValue();
        bean.subType = msg.msgSubType;
        bean.isHello = true;
        bean.hasRead = true;
        JSONObject json = null;
        try {
            json = new JSONObject(new String(msg.content, "UTF-8"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (msg.msgSubType == MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()) {
            try {
                final long uid = json.getLong("uid");
                String msg1 = json.getString("msg");
                long time = json.getLong("time");
                bean.receiverId = uid;
                bean.sender = uid;
                bean.time = time;
                bean.content = msg1;
                IMDBService.addMsg(bean, new IMDBService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        long id = (Long) obj;
                        bean.id = id;
                        IDSIMQueue.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                // 最新消息
                                bean.receiverId = IMContants.ACK_ID;
                                IMLatestMsgBean lastBean = IMLatestMsgBean.getBean(bean);
                                IMLatestMsgDao.getInstance(mContext).addLatestMsg(lastBean);
                                // 好友申请表
                                bean.receiverId = uid;
                                IMApplyInfoBean applyInfoBean = IMApplyInfoBean.getBean(bean);
                                IMNewFriendDao.getInstance(mContext).add(applyInfoBean);
                            }
                        });
                        sendMsgBroadcast(bean);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue()) {
            try {
                long uid = json.getLong("uid");
                long time = json.getLong("time");
                bean.receiverId = uid;
                bean.sender = uid;
                bean.time = time;
                IMDBService.addMsg(bean, new IMDBService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        bean.id = (long) obj;
                        sendMsgBroadcast(bean);
                        IDSIMQueue.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                // 最新消息
                                bean.content = Res.getString(R.string.chat_tip_we_are_already_friend);
                                IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(bean);
                                IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);

                                AccountManager.getInstance().getUser().mProfile.mFriendTotal++;
                                NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_MY_FRIEND_UPDATA, null);

                            }
                        });
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()) {
            // 被人拉入群
            try {
                final long gid = json.getLong("chnid");
                final long time = json.getLong("time");
                final long inviter = json.getLong("inviter");
                final String groupName = json.getString("chnname");
                JSONArray members = json.getJSONArray("members");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;

                ArrayList list = new ArrayList();
                for (int i = 0; i < members.length(); i++) {
                    JSONObject j = members.getJSONObject(i);
                    final long uid = j.getLong("uid");
                    list.add(uid);
                    long myUid = Long.valueOf(AccountManager.getInstance().getUser().mUid);
                    // 我就是被加入的人
                    if (uid == myUid) {
                        bean.extra = String.valueOf(uid);
                        IDSIMQueue.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                IMGroupDao.getInstance(mContext).setIsKickOut(gid, false);
                            }
                        });
                    }
                }
                list.add(inviter);

                // 网络请求数据
                final ArrayList avatarList = new ArrayList();
                GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(listToString(list), new OnResponseListener<List<UserInfoBean>>() {
                    @Override
                    public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {
                        if (avatarBeans.isEmpty()) {
                            return;
                        }
                        String inviterName = "";
                        ArrayList namesArray = new ArrayList();
                        for (UserInfoBean bean1 : avatarBeans) {
                            avatarList.add(bean1);
                            long uid = Long.valueOf(bean1.getUid());
                            if (inviter == uid) {
                                // 群主
                                inviterName = bean1.getNickname();
                            } else if (uid == mMyUid) {//我

                            } else {//其他人
                                namesArray.add(bean1.getNickname());
                            }
                        }

                        //除我外其他人的名字
                        String s = listToString(namesArray);

                        if (bean.extra != null) {//我是被加入的人
                            if (namesArray.isEmpty()) {//只有自己被加入
                                bean.content = String.format(Res.getString(R.string.chat_tip_invited_by_sb), inviterName);
                            } else {
                                bean.content = String.format(Res.getString(R.string.chat_tip_invited_by_sb_and_others), inviterName, s);
                            }
                        } else {
                            bean.content = String.format(Res.getString(R.string.chat_tip_sb_invite_sb), inviterName, s);
                        }

                        // 群信息
                        //final IMGroupInfoBean groupInfoBean = new IMGroupInfoBean();
                        //groupInfoBean.gid = String.valueOf(gid);
                        //groupInfoBean.name = groupName;
                        //groupInfoBean.createTime = time;
                        IDSIMQueue.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                IMGroupDao.getInstance(mContext).add(gid, groupName, time);
                            }
                        });

                        // 插入聊天消息表
                        IMDBService.addMsg(bean, new IMDBService.DBListener() {
                            @Override
                            public void onResult(Object obj) {
                                bean.id = (long) obj;
                                sendMsgBroadcast(bean);
                                IDSIMQueue.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(bean);
                                        IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);

                                        //
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
                request.sendRequest();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()) {
            try {
                final long gid = json.getLong("chnid");
                long time = json.getLong("time");
                final long uid = json.getLong("uid");
                final long badUid = json.getLong("badUid");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.extra = String.valueOf(badUid);

                ArrayList uidArray = new ArrayList();
                uidArray.add(uid);//群主
                uidArray.add(badUid);//被移除的人

                IDSIMQueue.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        if (badUid == mMyUid) {//活动群表
                        }
                    }
                });

                // 网络请求数据
                final ArrayList avatarList = new ArrayList();
                GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(listToString(uidArray), new OnResponseListener<List<UserInfoBean>>() {
                    @Override
                    public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {
                        if (avatarBeans == null || avatarBeans.isEmpty()) {
                            return;
                        }
                        String creatorName = "";
                        String delName = "";
                        ArrayList namesArray = new ArrayList();
                        for (UserInfoBean user : avatarBeans) {
                            avatarList.add(user);
                            long userId = Long.valueOf(user.getUid());
                            if (uid == userId) {
                                creatorName = user.getNickname();
                            } else {
                                delName = user.getNickname();
                            }
                        }

                        if (badUid == mMyUid) {
                            bean.content = String.format(Res.getString(R.string.chat_tip_kickout_group_by_sb), creatorName);
                            IDSIMQueue.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    IMGroupDao.getInstance(mContext).setIsKickOut(gid, true);
                                }
                            });
                        } else {
                            bean.content = String.format(Res.getString(R.string.chat_tip_sb_kickout_sb), creatorName, delName);

                        }

                        // 插入聊天消息表
                        IMDBService.addMsg(bean, new IMDBService.DBListener() {
                            @Override
                            public void onResult(Object obj) {
                                bean.id = (long) obj;
                                sendMsgBroadcast(bean);
//                                IDSIMQueue.getInstance().post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        IMLatestMsgDao.getInstance(mContext).deleteMsg(gid);
//
//                                        if (badUid == mMyUid) {
////                                            IDSGroupChatDao.getInstance(mContext)
//                                            IMGroupDao.getInstance(mContext).deleteBean(badUid);
//                                        }
//                                    }
//                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
                request.sendRequest();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()) {
            try {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                IMUserDataHelper.getInstance().findUser(uid, new IMUserDataHelper.IMUserDataCallback() {
                    @Override
                    public void onResult(IMUserBean userBean) {
                        bean.content = String.format(Res.getString(R.string.chat_tip_somebody_quit_group), userBean.userName);
                        IMDBService.addMsg(bean, new IMDBService.DBListener() {
                            @Override
                            public void onResult(Object obj) {
                                long rowId = (long) obj;
                                bean.id = rowId;
                                sendMsgBroadcast(bean);
                            }
                        });
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()) {
            try {
                final long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                final String name = json.getString("newname");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.extra = name;

                IDSIMQueue.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        IMGroupDao.getInstance(mContext).updateName(gid, name);
                    }
                });

                IMUserDataHelper.getInstance().findUser(uid, new IMUserDataHelper.IMUserDataCallback() {
                    @Override
                    public void onResult(IMUserBean userBean) {
                        bean.content = String.format(Res.getString(R.string.chat_tip_other_change_group_name), userBean.userName, name);
                        IMDBService.addMsg(bean, new IMDBService.DBListener() {
                            @Override
                            public void onResult(Object obj) {
                                bean.id = (long) obj;
                                sendMsgBroadcast(bean);

                                IDSIMQueue.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        IMLatestMsgBean latestMsgBean = IMLatestMsgBean.getBean(bean);
                                        IMLatestMsgDao.getInstance(mContext).addLatestMsg(latestMsgBean);

                                    }
                                });
                            }
                        });

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()) {
            try {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                JSONArray members = json.getJSONArray("members");
//                if ((json.get("inviter") instanceof String) && json.get("inviter") != 0) {
//
//                }
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;

                ArrayList list = new ArrayList();
                for (int i = 0; i < members.length(); i++) {
                    JSONObject j = members.getJSONObject(i);
                    long uid = j.getLong("uid");
                    list.add(uid);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {//nothing
        }
    }

    private IMChatBean praseTipMsgOffLine(MessageManager.ReceivedMessage msg) {
        final IMChatBean bean = new IMChatBean();
        bean.msgType = MessageEntity.MsgType.TEXT.getValue();
        bean.subType = msg.msgSubType;
        bean.isHello = true;
        bean.hasRead = true;
        bean.extra = msg.extra;
        JSONObject json = null;
        try {
            json = new JSONObject(new String(msg.content, "UTF-8"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (msg.msgSubType == MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()) {
            try {
                long uid = json.getLong("uid");
                String msg1 = json.getString("msg");
                long time = json.getLong("time");
                bean.receiverId = uid;
                bean.sender = uid;
                bean.time = time;
                bean.content = msg1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue()) {
            try {
                long uid = json.getLong("uid");
                long time = json.getLong("time");
                bean.receiverId = uid;
                bean.sender = uid;
                bean.time = time;

                AccountManager.getInstance().getUser().mProfile.mFriendTotal++;
                NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_MY_FRIEND_UPDATA, null);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()) {
            try {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long inviter = json.getLong("inviter");
                String groupName = json.getString("chnname");
                JSONArray members = json.getJSONArray("members");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.content = TIP_MSG_START + json;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()) {
            try {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                long badUid = json.getLong("badUid");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.extra = String.valueOf(badUid);
                bean.content = TIP_MSG_START + json;

                //活动群

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()) {
            try {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.content = TIP_MSG_START + json;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()) {
            try {
                final long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                final String name = json.getString("newname");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.extra = name;//页面用
                bean.content = TIP_MSG_START + json;
                IDSIMQueue.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        IMGroupDao.getInstance(mContext).updateName(gid, name);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (msg.msgSubType == MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()) {
            try {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                bean.receiverId = gid;
                bean.sender = gid;
                bean.groupId = gid;
                bean.time = time;
                bean.content = TIP_MSG_START + json;
                IDSIMQueue.getInstance().post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return bean;
    }

    private class IMComparator implements Comparator<MessageManager.ReceivedMessage> {
        @Override
        public int compare(MessageManager.ReceivedMessage o1, MessageManager.ReceivedMessage o2) {
            long time1 = o1.sendTime;
            long time2 = o2.sendTime;
            int re = 0;
            if (time1 > time2) {
                re = 1;
            } else if (time1 < time2) {
                re = -1;
            } else {
                re = 0;
            }
            return re;
        }
    }

    // =======================================================

    public void sendMsg(final IMChatBean bean) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                sendMessage(bean);
            }
        });
    }

    private void sendMessage(IMChatBean bean) {
        MessageEntity e = new MessageEntity();
        if (bean.subType == SINGLE_CHAT.getValue()) {
            e.setChatType(SINGLE_CHAT);
        } else if (bean.subType == GROUP_CHAT.getValue()) {
            e.setChatType(GROUP_CHAT);
        } else if (bean.subType == FEED_FROM_GROUP_ACTIVITY.getValue()) {
            e.setChatType(FEED_FROM_GROUP_ACTIVITY);
        }
        if (bean.msgType == MessageEntity.MsgType.TEXT.getValue()) {
            e.setMsgType(MessageEntity.MsgType.TEXT);
            e.setBody(bean.content.getBytes());
            e.setExtra(bean.extra);
            e.setExtra2(bean.extra.toString().getBytes());// extra用string会乱码..

        } else if (bean.msgType == MessageEntity.MsgType.VOICE.getValue()) {
            e.setMsgType(MessageEntity.MsgType.VOICE);
            File f = new File(bean.content);
            byte[] by = Utils.readFileToBytes(f);
            e.setBody(by);
            String ex = null;
//            try {
            ex = Base64.encodeToString(bean.extra.getBytes(), Base64.DEFAULT);
            e.setExtra(ex);

//            } catch (UnsupportedEncodingException e1) {
//                e1.printStackTrace();
//            }

        } else if (bean.msgType == MessageEntity.MsgType.IMAGE.getValue()) {
            e.setMsgType(MessageEntity.MsgType.IMAGE);
            File f = new File(bean.content);
            byte[] bytes = Utils.readFileToBytes(f);
            byte[] compressedBytes = null;
            if (Utils.isGIF(bytes)) {
                compressedBytes = bytes;
            } else {
                compressedBytes = BitmapHelper.compressBytes(bytes);
            }
            e.setBody(compressedBytes);
            String ex = Base64.encodeToString(bean.extra.getBytes(), Base64.DEFAULT);
            e.setExtra(ex);

        } else if (bean.msgType == MessageEntity.MsgType.SHARE_LINK.getValue()) {
            e.setMsgType(MessageEntity.MsgType.SHARE_LINK);
            e.setExtra(bean.extra);
        }

        e.setReceiver(bean.receiverId);
        if (mMgr == null) {
            return;
        }
        mMgr.sendMessage(e, bean.msgId);
    }

    // =======================================================

    public void createGroup() {
        if (mMgr == null) {
            return;
        }
        mMgr.createGroup("", false, "", null);
    }

    public void checkIsGroupMember(long gid, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.checkIsGroupMember(gid + "", callBack);
    }

    //得到群列表
    public void getMyGroupList(MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.queryMyGroups(callBack);
    }

    //查询群组成员信息
    public void qrymem(String gid, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.queryGroupMembers(gid, callBack);
    }

    //查询群组信息
    public void queryGroupInfo(String gid, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.queryGroupInfo(gid, callBack);
    }

    //群主剔除成员接口
    public void kickOutMember(String gid, String uid, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.kickOutMember(gid, uid, callBack);
    }

    //退出群
    public void quitgroup(final String gid, final MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.quitGroup(gid, new MessageManager.HttpCallBack() {

            @Override
            public void onSuc(Object o) {

                IMDBGroupService.deleteGroupInfo(gid);
                IMDBGroupService.deleteGroupTable(gid);
                IMDBLatestMsgService.deleteMsg(Long.valueOf(gid));

                Intent i = new Intent(ACTION_IM_QUIT_GROUP);
                i.putExtra(KEY_GROUP_ID, gid);
                mContext.sendBroadcast(i);

                callBack.onSuc(o);
            }

            @Override
            public void onFail(Object o) {

            }
        });
    }


    //修改群名称
    public void updateGroupName(final String gid, final String name, final MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.updateGroupName(gid, name, new MessageManager.HttpCallBack() {

            @Override
            public void onSuc(Object o) {
                IMDBGroupService.updateName(Long.valueOf(gid), name);

                // 插表
                IMDBService.addModifyGroupNameMsg(Long.valueOf(gid), name);

                Intent i = new Intent(ACTION_IM_UPDATE_GROUP_NAME_MSG);
                i.putExtra(KEY_GROUP_NAME, name);
                i.putExtra(KEY_SEND_UID, gid);
                mContext.sendBroadcast(i);

                callBack.onSuc(o);
            }

            @Override
            public void onFail(Object o) {

            }
        });
    }


    //发送好友请求
    public void addfriend(String uid, String verifyMsg, String userName, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.addFriend(uid, verifyMsg, userName, callBack);
    }

    //接受好友请求
    public void accaddfriend(String uid, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.agreeAddFriend(uid, callBack);
    }


    //删除好友
    public void delfriend(String friendId, MessageManager.HttpCallBack callBack) {
        if (mMgr == null) {
            return;
        }
        mMgr.delFriend(friendId, callBack);
    }


    // =======================================================

    private String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }
    // =======================================================

    private void initFilePath() {
        if (!Utils.isSdcardReadable(mContext)) {
            Logger.w(tag, "===!!! NO SD_CARD !!!====");
            return;
        }
        File rootDir = new File(SDCARD_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    public String getVoicePath() {
        if (!Utils.isSdcardReadable(mContext)) {
            LogManager.w(tag, "===!!! NO SD_CARD !!!====");
            return "";
        }
        String uid = AccountManager.getInstance().getUser().mUid;
        File userDir = new File(SDCARD_ROOT_DIR, uid);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        File voiceDir = new File(userDir, "voices");
        if (!voiceDir.exists()) {
            voiceDir.mkdir();
        }
        return voiceDir.getPath();
    }

    public String getImagePath() {
        if (!Utils.isSdcardReadable(mContext)) {
            LogManager.w(tag, "===!!! NO SD_CARD !!!====");
            return "";
        }
        String uid = AccountManager.getInstance().getUser().mUid;
        File userDir = new File(SDCARD_ROOT_DIR, uid);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        File imageDir = new File(userDir, "images");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        return imageDir.getPath();
    }

    // =======================================================

    private void sendNotification(IMChatBean bean) {
        if (!DeviceUtil.isBackground(mContext)) {
            return;
        }
        String text = Res.getString(R.string.chat_notify);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification(R.mipmap.ic_launcher,
                text, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(mContext, "", text, contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notification);
    }

    //========================================================================

    public void sendMsgBroadcast(IMChatBean bean) {
        Intent i = new Intent(ACTION_IM_ON_REC_MSG);
        i.putExtra(KEY_REC_MSG_BEAN, bean);
        mContext.sendBroadcast(i);
    }

    private void sendMsgsBroadcast(LinkedList<IMChatBean> beans) {
        Intent i = new Intent(ACTION_IM_ON_REC_MSG_MULTI);
        i.putExtra(KEY_REC_MSG_BEAN_MULTI, beans);
        mContext.sendBroadcast(i);
    }

    public long getMsgId() {
        return ++mMsgId;
    }

    //========================================================================

    private void onTokenExpired(Intent intent) {
        Logger.i(tag, "=======onTokenExpired==========");
        Activity activity = ActivityUtils.getScreenManager().currentActivity();
        if (activity != null && activity.getPackageName().equals(mContext.getPackageName())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.token_expired);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //关闭dialog
                }
            });
            builder.create().show();
        }
    }

    private void onLogout(Intent intent) {
        Logger.i(tag, "=======onLogout==========");
        destroy();
        Activity activity = ActivityUtils.getScreenManager().currentActivity();
        if (activity != null && activity.getPackageName().equals(mContext.getPackageName())) {
            showLogoutDialog(activity);
        }
    }

    private void showLogoutDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.logout_tip_content);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogManager.w(TAG, "账号被T掉");
                dialog.dismiss(); //关闭dialog
                AccountManager.getInstance().logout();
                ActivityUtils.getScreenManager().popAllActivity();
//                NearApplication.mInstance.getMessagePump().destroyMessagePump();
                AccountManager accountManager = AccountManager.getInstance();
                accountManager.removeUser();
                LoginManager.getInstance().logout();
                SplashActivity.show(activity);
                activity.finish();
            }
        });
        builder.create().show();
    }

    public void destroy() {
        if (mMgr != null) {
            mMgr.destroy();
        }
        BaseSQLiteHelper.getInstance(mContext).destroy();
    }
}
