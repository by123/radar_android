package com.brotherhood.o2o.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.task.TaskExecutor;

/**
 * 需要处理的操作，定期轮询用户位置，用户距离改变达到100米时，上报位置，更新坐标，重新拉取雷达数据
 */
public class NearService extends Service {

    private static final int LOCATION_UPDATE_INTERVAL = 5 * 60 * 1000;//五分钟获取一次定位信息

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateLocation();
    }

    private void updateLocation(){
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                //更新定位信息
                LocationManager.getInstance().updateCurrentAddress();
                TaskExecutor.scheduleTask(new Runnable() {
                    @Override
                    public void run() {
                        updateLocation();
                    }
                }, LOCATION_UPDATE_INTERVAL);
            }
        });
    }


    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    public class Binder extends android.os.Binder {
        public NearService getNearService() {
            return NearService.this;
        }
    }
}
