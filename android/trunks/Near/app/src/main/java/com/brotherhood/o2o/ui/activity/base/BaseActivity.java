package com.brotherhood.o2o.ui.activity.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.AnnotationManager;
import com.brotherhood.o2o.lib.annotation.ViewInjectable;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.message.MessageCallback;
import com.brotherhood.o2o.message.MessageHelper;
import com.brotherhood.o2o.ui.activity.SplashActivity;
import com.brotherhood.o2o.util.ActivityUtils;
import com.brotherhood.o2o.util.UmengAnalysisUtil;
import com.brotherhood.o2o.wrapper.NearBugtagsWrapper;

//import com.brotherhood.o2o.wrapper.DLOGWrapper;

public abstract class BaseActivity extends AppCompatActivity implements MessageCallback, ViewInjectable, View.OnClickListener {

    private MessageHelper mMessageHelper;
    private ActionBarController mActionBarController;
    private LayoutInflater mInflater;

    protected MultiStateView mStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIsLogin();
        mInflater = LayoutInflater.from(this);
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        if (addActionBar()) {
            LinearLayout actionbarRootLayout = null;
            if (getActionBarStyle() == ActionBarController.CENTER_TYPE) {
                actionbarRootLayout = (LinearLayout) mInflater.inflate(R.layout.action_bar_base_center_layout, viewGroup, false);
            } else {
                actionbarRootLayout = (LinearLayout) mInflater.inflate(R.layout.action_bar_base_left_layout, viewGroup, false);
            }
            mStateView = (MultiStateView) actionbarRootLayout.findViewById(R.id.stateView);
            View contentView = mInflater.inflate(getLayoutId(), viewGroup, false);
            //actionbarRootLayout.addView(mStateView);
            mStateView.addView(contentView);
            setContentView(actionbarRootLayout);
            mActionBarController = new ActionBarController(this, getActionBarStyle(), this);
        } else if (addOverlayActionBar()) {
            FrameLayout actionbarRootLayout = null;
            if (getActionBarStyle() == ActionBarController.CENTER_TYPE) {
                actionbarRootLayout = (FrameLayout) mInflater.inflate(R.layout.action_overbar_base_center_layout, viewGroup, false);
            } else {
                actionbarRootLayout = (FrameLayout) mInflater.inflate(R.layout.action_overbar_base_left_layout, viewGroup, false);
            }
            mStateView = (MultiStateView) actionbarRootLayout.findViewById(R.id.stateView);
            View contentView = mInflater.inflate(getLayoutId(), viewGroup, false);
            //actionbarRootLayout.addView(mStateView, 0);
            mStateView.addView(contentView);
            setContentView(actionbarRootLayout);
            mActionBarController = new ActionBarController(this, getActionBarStyle(), this);
        } else {
            if (getLayoutId() != 0){
                setContentView(getLayoutId());
            }
            mStateView = (MultiStateView) findViewById(R.id.stateView);
        }
        ActivityUtils.getScreenManager().pushActivity(this);
        AnnotationManager.getParser(AnnotationManager.VIEW).parse(this);
        mStateView = (MultiStateView) findView(R.id.stateView);
        if (mStateView != null) {
            setViewForState();
            if (showLoading()) {
                showLoadingView();
            } else {
                showContentView();
            }
        }
        mMessageHelper = new MessageHelper();
        mMessageHelper.setMessageCallback(this);
        attachAllMessage();
        mMessageHelper.registerMessages();
    }

    protected void setViewForState() {
    }

    protected void showLoadingView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);
    }

    protected void showEmptyView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        View emptyView = mStateView.getView(MultiStateView.VIEW_STATE_EMPTY);
        if (emptyView != null && emptyView.findViewById(R.id.tvEmpty) != null) {
            emptyView.findViewById(R.id.tvEmpty).setOnClickListener(this);
        }
    }

    protected void showContentView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
    }

    protected void showErrorView() {
        mStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        View emptyView = mStateView.getView(MultiStateView.VIEW_STATE_ERROR);
        if (emptyView != null && emptyView.findViewById(R.id.tvRetry) != null) {
            emptyView.findViewById(R.id.tvRetry).setOnClickListener(this);
        }
    }

    protected boolean showLoading() {
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        DLOGWrapper.onActivityResume(this);
        if (!containFragment()) {
            UmengAnalysisUtil.onPageStart(this);
        }
        UmengAnalysisUtil.onActivityResume(this);

        NearBugtagsWrapper.resume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        DLOGWrapper.onActivityPause(this);
        if (!containFragment()) {
            UmengAnalysisUtil.onPageEnd(this);
        }
        UmengAnalysisUtil.onActivityPause(this);

        NearBugtagsWrapper.pause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMessageHelper != null) {
            mMessageHelper.unRegisterMessages();
            mMessageHelper.clearMessages();
        }
        ActivityUtils.getScreenManager().popActivity(this);
    }

    /**
     * 当app到background，内存占过高时，会被系统回收；
     * 这时再次进入app，在某些机器上，系统会再次启动application，并启动栈顶activity，
     * 这时一些数据没有经过初始化，就会出现问题；
     * 这里使用一个标记位，若没经过初始化进入，直接再进行一次初始化流程
     */
    private void checkIsLogin() {
        if (!TextUtils.isEmpty(getLocalClassName()) && getLocalClassName().equals(SplashActivity.class.getName())) {
            return;//如果本身就是初始化页面，就不需要再走逻辑了
        }
        if (!NearApplication.mInstance.getInitFromLogin()) {
            SplashActivity.show(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        NearBugtagsWrapper.touchDidpatcher(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    protected void attachMessage(Message.Type type) {
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

    public ActionBarController getActionBarController() {
        return mActionBarController;
    }

    /**
     * 添加ActionBar(垂直)，如果不需要头部，重写改方法并返回false
     *
     * @return
     */
    protected boolean addActionBar() {
        return false;
    }

    /**
     * 添加ActionBar (Actionbar覆盖内容区域)，如果不需要头部，重写改方法并返回false
     *
     * @return
     */
    protected boolean addOverlayActionBar() {
        return false;
    }

    /**
     * 设置头部风格
     * LEFT_TYPE：标题居左
     * CENTER_TYPE：标题居中
     *
     * @return
     */
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View findView(int id) {
        return findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public <T> T getViewById(int id) {
        return (T) this.findView(id);
    }

    @Override
    public View findView(String res) {
        return null;
    }

    protected abstract int getLayoutId();

}
