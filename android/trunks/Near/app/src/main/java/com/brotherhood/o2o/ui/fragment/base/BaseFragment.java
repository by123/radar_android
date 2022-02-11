package com.brotherhood.o2o.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brotherhood.o2o.lib.annotation.AnnotationManager;
import com.brotherhood.o2o.lib.annotation.ViewInjectable;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.message.MessageCallback;
import com.brotherhood.o2o.message.MessageHelper;
import com.brotherhood.o2o.util.UmengAnalysisUtil;

/**
 * Created by by.huang on 2015/6/9.
 */
public abstract class BaseFragment extends Fragment implements ViewInjectable, MessageCallback {

    private MessageHelper mMessageHelper;

    protected View mView;

    abstract protected int getLayoutId();

    abstract public void loadData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageHelper = new MessageHelper();
        mMessageHelper.setMessageCallback(this);
        attachAllMessage();
        mMessageHelper.registerMessages();
    }

    public void DrawableListener(){

    }

    public boolean onBackPressed() {
        return false;
    }

    public void release() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = initView();

            AnnotationManager.getParser(AnnotationManager.VIEW).parse(this, BaseFragment.class.getSimpleName());
            loadData();
            DrawableListener();
        } else {
            ViewGroup root = (ViewGroup) mView.getParent();
            if (root != null) {
                root.removeView(mView);
            }
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isTraceFragment()) {
            UmengAnalysisUtil.onPageStart(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTraceFragment()) {
            UmengAnalysisUtil.onPageEnd(this);
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

    @Override
    public void onReceiveMessage(Message message) {

    }

    /**
     * 监听所有消息
     */
    protected void attachAllMessage() {

    }
    protected final void attachMessage(Message.Type type) {
        mMessageHelper.attachMessage(type);
    }

    private View initView() {
        return getActivity().getLayoutInflater().inflate(getLayoutId(), null);
    }

    @Override
    public View findView(int id) {
        return mView.findViewById(id);
    }

    @Override
    public View findView(String res) {
        return null;
    }

    public View getLayoutView() {
        return mView;
    }
}
