package com.brotherhood.o2o.ui.activity.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.brotherhood.o2o.extensions.DLOGWrapper;
import com.brotherhood.o2o.extensions.UmengWrapper;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.message.MessageCallback;
import com.brotherhood.o2o.message.MessageHelper;

/**
 * Created by ZhengYi on 15/6/4.
 */
public class BaseActivity extends AppCompatActivity implements MessageCallback {

    private MessageHelper mMessageHelper;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mMessageHelper = new MessageHelper();
        mMessageHelper.setMessageCallback(this);
        attachAllMessage();
        mMessageHelper.registerMessages();

        attachAllMessage();
        mMessageHelper.registerMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DLOGWrapper.onActivityResume(this);
        if (!containFragment()) {
            UmengWrapper.onPageStart(this);
        }
        UmengWrapper.onActivityResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DLOGWrapper.onActivityPause(this);
        if (!containFragment()) {
            UmengWrapper.onPageEnd(this);
        }
        UmengWrapper.onActivityPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMessageHelper != null) {
            mMessageHelper.unRegisterMessages();
            mMessageHelper.clearMessages();
        }
    }

    protected void attachMessage(Message.Type type){
        mMessageHelper.attachMessage(type);
    }

    /**
     * 监听所有消息
     */
    protected void attachAllMessage() {

    }

    protected boolean containFragment() {
        return false;
    }

    @Override
    public void onReceiveMessage(Message message) {

    }

    public interface OnThreadListener {
        void doInThread();
    }

    public void newThread(final OnThreadListener listener, final long sleep) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(sleep);
                    doInThread(listener);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void newThread(final OnThreadListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doInThread(listener);
            }
        }).start();
    }

    private void doInThread(final OnThreadListener listener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listener.doInThread();
            }
        });
    }
}
