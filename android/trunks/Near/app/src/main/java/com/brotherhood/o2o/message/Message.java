package com.brotherhood.o2o.message;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: xiejm
 * Date: 3/4/13
 * Time: 11:46 AM
 */
public class Message {
    public final static int PRIORITY_NORMAL = 1;
    public final static int PRIORITY_HIGH = 2;
    public final static int PRIORITY_EXTREMELY_HIGH = 3;

    private static ConcurrentLinkedQueue<Message> mCachedMessagePool = new ConcurrentLinkedQueue<Message>();
    private final static int MAX_CACHED_MESSAGE_OBJ = 15;

    public static enum Type {
        NONE,
        // this message is used to destroy the message pump,
        // we use the "Poison Pill Shutdown" approach, see: http://stackoverflow.com/a/812362/668963
        DESTROY_MESSAGE_PUMP,
        NETWORK_CHANGE,   //网络状态改变
        USER_LOGOUT_SUCCESS,//用户注销成功
        USER_DATA_CHANGE,//用户数据发生变动
        //在获取用户信息成功后通知
        USER_LOGIN_SUCCESS, //用户登录成功
        USER_LOGIN_FAILED, //用户登录失败

        OVERSEA_FOOD_COLLECT_CHANGE,//美食收藏数据发生变化
        ADDRESS_CHANGED,//当前地址发生变化
        UPDATE_ADDRESS_FAILED,//更新地理位置失败
        REQUEST_ADDRESS_SUCCESS,//请求定位数据成功
        REQUEST_ADDRESS_UNKNOWN_ERROR,//请求定位未知错误
        REQUEST_ADDRESS_SERVER_ERROR,//请求定位服务器异常
        RADAR_REFRESH_FINISH,//雷达刷新结束

        MSG_DELETE_MY_FRIEND,//删除好友
        //MSG_ADD_MY_FRIEND,//加好友
        MSG_MY_FRIEND_UPDATA,//好友更新

        MSG_VISITOR_TOTAL,//访客数量更新
    }

    public Message(Type type, Object data, int priority, Object sender) {
        this.type = type;
        this.data = data;
        this.priority = priority;
        this.sender = sender;
    }

    public Message(Type type, Object data, int priority) {
        this(type, data, priority, null);
    }

    public Message(Type type, Object data) {
        this(type, data, PRIORITY_NORMAL, null);
    }

    public Message(Type type, int priority) {
        this(type, null, priority);
    }

    public void reset() {
        type = Type.NONE;
        data = null;
        priority = PRIORITY_NORMAL;
        sender = null;
    }

    public void recycle() {
        if (mCachedMessagePool.size() < MAX_CACHED_MESSAGE_OBJ) {
            reset();
            mCachedMessagePool.add(this);
        }
    }

    public static Message obtainMessage(Type msgType, Object data, int priority, Object sender) {
        Message message = mCachedMessagePool.poll();

        if (message != null) {
            message.type = msgType;
            message.data = data;
            message.priority = priority;
            message.sender = sender;

        } else {
            message = new Message(msgType, data, priority, sender);
        }

        return message;
    }

    public Type type;
    public Object data;
    public int priority;
    public Object sender;

    public int referenceCount;
}
