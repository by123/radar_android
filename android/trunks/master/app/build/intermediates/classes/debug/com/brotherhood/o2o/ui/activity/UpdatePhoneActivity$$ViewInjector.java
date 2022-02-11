// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class UpdatePhoneActivity$$ViewInjector<T extends com.brotherhood.o2o.ui.activity.UpdatePhoneActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624022, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131624022, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131624050, "field 'mPhoneEdit'");
    target.mPhoneEdit = finder.castView(view, 2131624050, "field 'mPhoneEdit'");
    view = finder.findRequiredView(source, 2131624051, "field 'mVerifyEdit'");
    target.mVerifyEdit = finder.castView(view, 2131624051, "field 'mVerifyEdit'");
    view = finder.findRequiredView(source, 2131624052, "field 'mBindBtn' and method 'onBindBtnClick'");
    target.mBindBtn = finder.castView(view, 2131624052, "field 'mBindBtn'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onBindBtnClick();
        }
      });
    view = finder.findRequiredView(source, 2131624033, "field 'mSendBtn' and method 'onSendVerifyClick'");
    target.mSendBtn = finder.castView(view, 2131624033, "field 'mSendBtn'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSendVerifyClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.mToolbar = null;
    target.mPhoneEdit = null;
    target.mVerifyEdit = null;
    target.mBindBtn = null;
    target.mSendBtn = null;
  }
}
