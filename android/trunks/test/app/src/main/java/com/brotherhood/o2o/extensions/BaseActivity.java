package com.brotherhood.o2o.extensions;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by ZhengYi on 15/6/4.
 */
public class BaseActivity extends AppCompatActivity {

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

    protected boolean containFragment() {
        return false;
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
