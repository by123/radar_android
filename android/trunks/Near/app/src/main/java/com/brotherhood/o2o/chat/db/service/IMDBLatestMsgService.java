package com.brotherhood.o2o.chat.db.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.model.IMLatestMsgBean;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/29.
 */
public class IMDBLatestMsgService {


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

    public static void queryAllLatestMsg(int limit, int page, final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                //LinkedList<IMLatestMsgBean> msgList = IMLatestMsgDao.getInstance(mContext).queryAllLatestMsg(50, 0);
                LinkedList<IMLatestMsgBean> msgList = IMLatestMsgDao.getInstance(mContext).queryAllLatestMsg();
                onResult(msgList, listener);
            }
        });
    }


    public static void hasUnReadLatestMsg(final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                boolean has = IMLatestMsgDao.getInstance(mContext).hasUnReadMsg();
                onResult(has, listener);
            }
        });
    }


    public static void update(final long uid, String nickName, String avatar) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMLatestMsgDao.getInstance(mContext).updateToHasRead(uid);
            }
        });
    }


    public static void updateLatestMsgToHasRead(final long uid) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMLatestMsgDao.getInstance(mContext).updateToHasRead(uid);
            }
        });
    }

    public static void queryUnReadCount(final long uid, final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                long count = IMLatestMsgDao.getInstance(mContext).queryUnReadCount(uid);
                onResult(count, listener);
            }
        });
    }


    public static void deleteMsg(final long uid) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMLatestMsgDao.getInstance(mContext).deleteMsg(uid);
            }
        });
    }


    //public static void updateName(final long gid, final String name){
    //    IDSIMQueue.getInstance().post(new Runnable() {
    //        @Override
    //        public void run() {
    //            IMGroupDao.getInstance(mContext).updateName(gid, name);
    //        }
    //    });
    //
    //}

    public static void updateSystemUnReadNum(final long num) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMLatestMsgDao.getInstance(mContext).updateSystemUnReadNum(num);
            }
        });

    }


}
