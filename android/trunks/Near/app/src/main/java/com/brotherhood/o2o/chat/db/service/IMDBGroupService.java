package com.brotherhood.o2o.chat.db.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.db.dao.IDSGroupChatDao;
import com.brotherhood.o2o.chat.db.dao.IMGroupDao;
import com.brotherhood.o2o.chat.model.IMGroupInfoBean;

/**
 * Created by laimo.li on 2016/1/4.
 */
public class IMDBGroupService {

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


    public static void add(final IMGroupInfoBean info){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).add(info);
            }
        });

    }


    public static void add(final long gid, final String groupName, final String avatar){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).add(gid,groupName,avatar);
            }
        });

    }


    public static void add(final long gid,final long creatorUid, final long createTime){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).add(gid,createTime,creatorUid);
            }
        });

    }



    public static void updateAvatar(final long gid, final String avatar){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).updateAvatar(gid, avatar);
            }
        });

    }


    public static void queryGroupAvatar(final String gid, final DBListener listener){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                String avatar = IMGroupDao.getInstance(mContext).queryAvatar(Long.valueOf(gid));
                onResult(avatar, listener);
            }
        });

    }


    public static void queryGroupInfo(final String gid, final DBListener listener){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupInfoBean info = IMGroupDao.getInstance(mContext).queryGroupInfo(Long.valueOf(gid));
                onResult(info, listener);
            }
        });

    }


    public static void showMemberName(final String gid, final boolean show){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).updateShowMemberName(Long.valueOf(gid), show);
            }
        });

    }


    public static void updateName(final long gid, final String name){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).updateName(gid, name);
            }
        });

    }


    public static void deleteGroupInfo(final String gid){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMGroupDao.getInstance(mContext).deleteBean(Long.valueOf(gid));
            }
        });

    }


    public static void deleteGroupTable(final String gid){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IDSGroupChatDao.getInstance(mContext).deleteTable(Long.valueOf(gid));
            }
        });

    }


    //public static void emptyGroup(final String gid){
    //    IDSIMQueue.getInstance().post(new Runnable() {
    //        @Override
    //        public void run() {
    //            IDSGroupChatDao.getInstance(mContext).emptyTable(Long.valueOf(gid));
    //        }
    //    });
    //
    //}


    public static void showMemberName(final long gid,final DBListener listener){
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                boolean show = IMGroupDao.getInstance(mContext).showMemberName(gid);
                onResult(show,listener);
            }
        });
    }


}
