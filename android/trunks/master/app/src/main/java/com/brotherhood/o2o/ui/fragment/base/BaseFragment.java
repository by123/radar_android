package com.brotherhood.o2o.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.brotherhood.o2o.extensions.UmengWrapper;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.message.MessageCallback;
import com.brotherhood.o2o.message.MessageHelper;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

/**
 * Created by by.huang on 2015/6/9.
 */
public class BaseFragment extends Fragment implements MessageCallback{

    private MessageHelper mMessageHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageHelper = new MessageHelper();
        mMessageHelper.setMessageCallback(this);
        attachAllMessage();
        mMessageHelper.registerMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isTraceFragment()) {
            UmengWrapper.onPageStart(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTraceFragment()) {
            UmengWrapper.onPageEnd(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMessageHelper != null) {
            mMessageHelper.unRegisterMessages();
            mMessageHelper.clearMessages();
        }
    }

    protected boolean isTraceFragment() {
        return true;
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public void onReceiveMessage(Message message) {

    }

    /**
     * 监听所有消息
     */
    protected void attachAllMessage() {

    }
}
