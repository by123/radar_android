// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SplashLoginActivity$$ViewInjector<T extends com.brotherhood.o2o.ui.activity.SplashLoginActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624037, "field 'mWechatBtn' and method 'OnWechatBtnClick'");
    target.mWechatBtn = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.OnWechatBtnClick();
        }
      });
    view = finder.findRequiredView(source, 2131624036, "field 'mQQBtn' and method 'OnQQBtnClick'");
    target.mQQBtn = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.OnQQBtnClick();
        }
      });
    view = finder.findRequiredView(source, 2131624049, "field 'mOthersBtn' and method 'OnOtherBtnClick'");
    target.mOthersBtn = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.OnOtherBtnClick();
        }
      });
    view = finder.findRequiredView(source, 2131624038, "field 'mWebView'");
    target.mWebView = finder.castView(view, 2131624038, "field 'mWebView'");
  }

  @Override public void reset(T target) {
    target.mWechatBtn = null;
    target.mQQBtn = null;
    target.mOthersBtn = null;
    target.mWebView = null;
  }
}
