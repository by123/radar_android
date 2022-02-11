package com.brotherhood.o2o.chat.db.service;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.db.dao.IMUserDao;
import com.brotherhood.o2o.chat.model.IMUserBean;

/**
 * Created by Administrator on 2015/12/25 0025.
 */
public class IMUserService {

    public static interface IMUserCallback {
        public void onResult(Object object);
    }

    public void addUser(final IMUserBean bean, IMUserCallback callback) {
        IDSIMQueue.getInstance().post(new Runnable() {
            @Override
            public void run() {
                IMUserDao.getInstance(NearApplication.mInstance).addMsg(bean);
            }
        });
    }

}
