package com.brotherhood.o2o.chat.db.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.IMContants;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.db.dao.IMSystemMsgDao;
import com.brotherhood.o2o.chat.model.IMSystemMsgBean;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/30.
 */
public class IMDBSystemMsgService {

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

    public static void queryMsg(final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                LinkedList<IMSystemMsgBean> beans = IMSystemMsgDao.getInstance(mContext).queryMsg();
                onResult(beans, listener);
            }
        });
    }

    public static void deleteAllMsg() {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMSystemMsgDao.getInstance(mContext).deleteAllMsg();
                IMLatestMsgDao.getInstance(mContext).deleteMsg(IMContants.SYSTEM_ID);
            }
        });
    }

    public static void deleteMsg(final long _id, final boolean hasRead) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMSystemMsgDao.getInstance(mContext).deleteMsg(_id);
                IMSystemMsgBean bean = IMSystemMsgDao.getInstance(mContext).queryLatestMsg();
                if (bean != null) {
                    IMLatestMsgDao.getInstance(mContext).updateSystemMsg(bean.content, bean.time);
                    if (hasRead) {
                        IMDBSystemMsgService.queryAllUnReadMsgNum(new IMDBSystemMsgService.DBListener() {
                            @Override
                            public void onResult(Object obj) {
                                IMDBLatestMsgService.updateSystemUnReadNum((long) obj);
                            }
                        });
                    }
                } else {
                    IMLatestMsgDao.getInstance(mContext).deleteMsg(IMContants.SYSTEM_ID);
                }
            }
        });
    }


    public static void queryAllUnReadMsgNum(final DBListener listener) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                long num = IMSystemMsgDao.getInstance(mContext).queryAllUnReadMsgNum();
                onResult(num, listener);
            }
        });
    }


    public static void updateMsgToRead(final long _id) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMSystemMsgDao.getInstance(mContext).updateMsgToRead(_id);
                IMDBSystemMsgService.queryAllUnReadMsgNum(new IMDBSystemMsgService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        IMDBLatestMsgService.updateSystemUnReadNum((long) obj);
                    }
                });

            }
        });
    }


}
