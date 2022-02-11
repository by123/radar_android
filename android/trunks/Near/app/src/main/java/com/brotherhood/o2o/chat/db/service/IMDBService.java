package com.brotherhood.o2o.chat.db.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.db.dao.IDSChatMsgDao;
import com.brotherhood.o2o.chat.db.dao.IDSGroupChatDao;
import com.brotherhood.o2o.chat.db.dao.IMSendMsgDao;
import com.brotherhood.o2o.chat.helper.IMUserDataHelper;
import com.brotherhood.o2o.chat.model.IMChatBean;
import com.brotherhood.o2o.chat.model.IMUserBean;
import com.brotherhood.o2o.manager.AccountManager;
import com.skynet.library.message.MessageManager;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/12/17 0017.
 */
public class IMDBService {

    private static Context mContext = NearApplication.mInstance.getApplicationContext();

    public interface DBListener {
        public void onResult(Object obj);
    }

    private static void postMain(Runnable run) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(run);
    }

    private static void onResult(final Object obj, final DBListener listener) {
        postMain(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onResult(obj);
                }
            }
        });
    }

    public static void addMsg(final IMChatBean bean, final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                long id = 0;
                if (isGroup(bean.subType)) {
                    id = IDSGroupChatDao.getInstance(mContext).addMsg(bean);
                } else {
                    id = IDSChatMsgDao.getInstance(mContext).addMsg(bean);
                }
                onResult(id, listener);
            }
        });
    }

    public static void addMsgs(final LinkedList<LinkedList> list, final boolean isGroup, final DBListener listener) {
        if (list.isEmpty()) {
            return;
        }
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                LinkedList idLists = new LinkedList();
                if (isGroup) {
                    for (LinkedList<IMChatBean> beans : list) {
                        LinkedList list1 = IDSGroupChatDao.getInstance(mContext).addMsgs(beans);
                        idLists.add(list1);
                    }
                } else {
                    for (LinkedList<IMChatBean> beans : list) {
                        LinkedList list1 = IDSChatMsgDao.getInstance(mContext).addMsgs(beans);
                        idLists.add(list1);
                    }
                }
                onResult(idLists, listener);
            }
        });
    }
    // =======================================================

    public static void queryLimitLastMsg(final long uid, final int pageSize, final long lastMsgRowId,
                                         final boolean isGroup, final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                long index = 0;
                LinkedList<IMChatBean> linkedList = null;
                if (lastMsgRowId == 0) {
                    if (isGroup) {
                        index = IDSGroupChatDao.getInstance(mContext).getLastRowId(uid);
                    } else {
                        index = IDSChatMsgDao.getInstance(mContext).getLastRowId(uid);
                    }
                } else if (lastMsgRowId == 1) {//sql<=1
                    onResult(linkedList, listener);
                    return;
                } else {
                    index = lastMsgRowId;
                }
                if (isGroup) {
                    linkedList = IDSGroupChatDao.getInstance(mContext).queryLimitLastMsg(uid, pageSize, index);
                } else {
                    linkedList = IDSChatMsgDao.getInstance(mContext).queryLimitLastMsg(uid, pageSize, index);
                }
                onResult(linkedList, listener);
            }
        });
    }


    // =======================================================

    public static void addSendMsg(final IMChatBean bean) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMSendMsgDao.getInstance(mContext).addMsg(bean);
            }
        });
    }

    public static void getUIDFromMsgId(final long msgId, final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                Bundle b = IMSendMsgDao.getInstance(mContext).getUIDFromMsgId(msgId);
                onResult(b, listener);
            }
        });
    }

    public static void updateMsgSendState(final boolean isGroup, final long uid,
                                          final long msgId,
                                          final IMChatBean.SendState sendState) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).updateMsgSendState(uid, msgId, sendState);
                } else {
                    Boolean s = IDSChatMsgDao.getInstance(mContext).
                            updateMsgSendState(uid, msgId, sendState);
                }
            }
        });
    }

    public static void updateMsgToRead(final IMChatBean bean, final boolean isGroup, DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).updateMsgToRead(bean);
                } else {
                    IDSChatMsgDao.getInstance(mContext).updateMsgToRead(bean);
                }
            }
        });
    }

    public static void updateAllMsgToRead(final boolean isGroup, final long uid) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).updateAllMsgToRead(uid);
                } else {
                    IDSChatMsgDao.getInstance(mContext).updateAllMsgToRead(uid);
                }
            }
        });
    }

    public static void updateMsgDownloadState(final IMChatBean bean, final boolean isGroup, DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).updateMsgDownloadState(bean);
                } else {
                    IDSChatMsgDao.getInstance(mContext).updateMsgDownloadState(bean);
                }
            }
        });
    }

    public static void updateMsgDuration(final IMChatBean bean, final boolean isGroup, DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).updateMsgDuration(bean);
                } else {
                    IDSChatMsgDao.getInstance(mContext).updateMsgDuration(bean);
                }
            }
        });
    }

    public static void deleteChatTable(final long uid, final boolean isGroup) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).deleteTable(uid);
                } else {
                    IDSChatMsgDao.getInstance(mContext).deleteTable(uid);
                }
            }
        });
    }


    public static void updateMsgContent(final IMChatBean bean, final boolean isGroup, DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (isGroup) {
                    IDSGroupChatDao.getInstance(mContext).updateMsgContent(bean);
                } else {
                    IDSChatMsgDao.getInstance(mContext).updateMsgContent(bean);
                }
            }
        });
    }

    // =======================================================

    public static void addSayHiFriendMsg(long uid, String text) {
        final IMChatBean bean = new IMChatBean();
        bean.sender = Long.valueOf(AccountManager.getInstance().getUser().mUid);
        bean.receiverId = uid;
        bean.isHello = true;
        bean.subType = MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND.getValue();
        bean.msgType = MessageManager.MessageEntity.MsgType.TEXT.getValue();
        bean.content = text;
        bean.time = System.currentTimeMillis() / 1000L;
        addMsg(bean, new DBListener() {
            @Override
            public void onResult(Object obj) {
                bean.id = (long) obj;
                IDSIMManager.getInstance().sendMsgBroadcast(bean);
            }
        });
    }

    public static void addAcceptFriendMsg(long uid) {
        final IMChatBean bean = new IMChatBean();
        bean.sender = Long.valueOf(AccountManager.getInstance().getUser().mUid);
        bean.receiverId = uid;
        bean.isHello = true;
        bean.subType = MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue();
        bean.msgType = MessageManager.MessageEntity.MsgType.TEXT.getValue();
        bean.time = System.currentTimeMillis() / 1000L;
        addMsg(bean, new DBListener() {
            @Override
            public void onResult(Object obj) {
            }
        });
    }

    public static void addJoinGroupMsg(String groupId, String[] members) {
    }

    public static void addDelByGroupCreator(long groupId, long uid) {
        final IMChatBean bean = new IMChatBean();
        bean.sender = Long.valueOf(AccountManager.getInstance().getUser().mUid);
        bean.receiverId = groupId;
        bean.groupId = groupId;
        bean.isHello = true;
        bean.subType = MessageManager.MessageEntity.ChatType.FEED_DELETED_BY_GROUP.getValue();
        bean.msgType = MessageManager.MessageEntity.MsgType.TEXT.getValue();
        bean.time = System.currentTimeMillis() / 1000L;
        IMUserDataHelper.getInstance().findUser(uid, new IMUserDataHelper.IMUserDataCallback() {
            @Override
            public void onResult(IMUserBean userBean) {
                bean.content = String.format("您已将%s移除群组", userBean.userName);
                addMsg(bean, new DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        bean.id = (long) obj;
                        IDSIMManager.getInstance().sendMsgBroadcast(bean);
                    }
                });
            }
        });
    }

    public static void addModifyGroupNameMsg(long groupId, String name) {
        final IMChatBean bean = new IMChatBean();
        bean.sender = Long.valueOf(AccountManager.getInstance().getUser().mUid);
        bean.receiverId = groupId;
        bean.groupId = groupId;
        bean.isHello = true;
        bean.subType = MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue();
        bean.msgType = MessageManager.MessageEntity.MsgType.TEXT.getValue();
        bean.time = System.currentTimeMillis() / 1000L;
        bean.content = name;
        bean.extra = name;
        addMsg(bean, new DBListener() {
            @Override
            public void onResult(Object obj) {
                bean.id = (long) obj;
                IDSIMManager.getInstance().sendMsgBroadcast(bean);
            }
        });
    }


    // =======================================================

    public static boolean isGroup(int subType) {
        if (subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()
                // 活动群
                || subType == MessageManager.MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()
                || subType == MessageManager.MessageEntity.ChatType.FEED_FROM_GROUP_ACTIVITY.getValue()
                ) {
            return true;
        }
        return false;
    }
}
