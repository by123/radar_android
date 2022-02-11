package com.brotherhood.o2o.chat.db.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.IMContants;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.db.dao.IMNewFriendDao;
import com.brotherhood.o2o.chat.model.IMApplyInfoBean;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/30.
 */
public class IMDBNewFriendsService {

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


    public static void queryAllApplyInfo(final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                LinkedList<IMApplyInfoBean> infoBeans = IMNewFriendDao.getInstance(mContext).queryAllApplyInfo();
                onResult(infoBeans, listener);
            }
        });
    }


    public static void queryAllUnReadNum(final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                long num = IMNewFriendDao.getInstance(mContext).queryAllUnReadNum();
                onResult(num, listener);
            }
        });
    }


    public static void queryAllUnAckNum(final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                long num = IMNewFriendDao.getInstance(mContext).queryAllUnAckNum();
                onResult(num, listener);
            }
        });
    }


    public static void updateAllToHasRead() {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMNewFriendDao.getInstance(mContext).updateAllToHasRead();
            }
        });
    }


    public static void updateToAck(final long uid) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMNewFriendDao.getInstance(mContext).updateToAck(uid);


            }
        });
    }


    public static void deleteApplyInfo(final long uid) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMNewFriendDao.getInstance(mContext).deleteApplyInfo(uid);
                IMApplyInfoBean bean = IMNewFriendDao.getInstance(mContext).queryLatestApplyInfo();
                if (bean != null) {
                    IMLatestMsgDao.getInstance(mContext).updateRequestMsg(bean.msgContents, bean.time);
                } else {
                    IMLatestMsgDao.getInstance(mContext).deleteMsg(IMContants.ACK_ID);
                }
            }
        });
    }


    public static void deleteApplyInfo() {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMNewFriendDao.getInstance(mContext).deleteAllApplyInfo();
            }
        });
    }


}
