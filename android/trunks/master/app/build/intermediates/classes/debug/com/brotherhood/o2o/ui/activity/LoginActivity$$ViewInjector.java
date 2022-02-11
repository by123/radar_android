// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class LoginActivity$$ViewInjector<T extends com.brotherhood.o2o.ui.activity.LoginActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624022, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131624022, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131624031, "field 'mAccountField'");
    target.mAccountField = finder.castView(view, 2131624031, "field 'mAccountField'");
    view = finder.findRequiredView(source, 2131624024, "field 'mCodeField'");
    target.mCodeField = finder.castView(view, 2131624024, "field 'mCodeField'");
    view = finder.findRequiredView(source, 2131624029, "field 'mLoginButton' and method 'onLoginButtonClick'");
    target.mLoginButton = finder.castView(view, 2131624029, "field 'mLoginButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLoginButtonClick();
        }
      });
    view = finder.findRequiredView(source, 2131624033, "field 'mSendButton' and method 'onSendButtonClick'");
    target.mSendButton = finder.castView(view, 2131624033, "field 'mSendButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSendButtonClick();
        }
      });
    view = finder.findRequiredView(source, 2131624038, "field 'mWebView'");
    target.mWebView = finder.castView(view, 2131624038, "field 'mWebView'");
    view = finder.findRequiredView(source, 2131624036, "method 'onQQLoginButtonClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onQQLoginButtonClick();
        }
      });
    view = finder.findRequiredView(source, 2131624037, "method 'onWechatButtonClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onWechatButtonClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.mToolbar = null;
    target.mAccountField = null;
    target.mCodeField = null;
    target.mLoginButton = null;
    target.mSendButton = null;
    target.mWebView = null;
  }
}
