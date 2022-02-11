package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.util.ViewUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于加载h5页面
 */
public class WebViewActivity extends BaseActivity {


    @ViewInject(id = R.id.webView)
    private WebView mWebView;

    @ViewInject(id = R.id.progressBar)
    private ProgressBar progressBar;

    private String mHot = "http://www.sina.com/";
    private String mLastUrl = "";

    public static void show(Context context, String url) {
        Intent it = new Intent(context, WebViewActivity.class);
        it.putExtra(BundleKey.WEB_VIEW_URL_KEY, url);
        context.startActivity(it);
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController().setHeadBackgroundColor(R.color.white).setDivideColor(R.color.black)
                .setBackImage(R.mipmap.back_image_black);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_layout;
    }

    protected void initView() {
        String tempUrl = getIntent().getStringExtra(BundleKey.WEB_VIEW_URL_KEY);
        if (!TextUtils.isEmpty(tempUrl)) {
            mHot = tempUrl;
        }
        LogManager.d("=============webview===========" + mHot);
        initWebViewSetting();
        mWebView.loadUrl(mHot);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initWebViewSetting() {
        mLastUrl = mHot;

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setBuiltInZoomControls(false);
        webSetting.setSupportZoom(false);
        //屏蔽长按复制效果
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 0){
                    ViewUtil.toggleView(progressBar,true);
                }
                if (progress == 100){
                    ViewUtil.toggleView(progressBar,false);
                }
                progressBar.setProgress(progress);
            }
        });

        webSetting.setSupportZoom(false);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setBuiltInZoomControls(false);
        // mWebView.setInitialScale(initalValue);
        // 自动换行
        // <p style="word-break:break-all">test</p>
        webSetting.setUseWideViewPort(true);
        // mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Referer", mLastUrl);
                        mWebView.loadUrl(url, headers);
                        mLastUrl = url;
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
                break;
        }
    }
}
