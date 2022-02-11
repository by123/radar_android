// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.surprise.controller;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MySurpriseActivity$$ViewInjector<T extends com.brotherhood.o2o.surprise.controller.MySurpriseActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624351, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131624351, "field 'mViewPager'");
    view = finder.findRequiredView(source, 2131624347, "field 'mRewardTxt'");
    target.mRewardTxt = finder.castView(view, 2131624347, "field 'mRewardTxt'");
    view = finder.findRequiredView(source, 2131624348, "field 'mRewardLine'");
    target.mRewardLine = view;
    view = finder.findRequiredView(source, 2131624349, "field 'mCouponTxt'");
    target.mCouponTxt = finder.castView(view, 2131624349, "field 'mCouponTxt'");
    view = finder.findRequiredView(source, 2131624350, "field 'mCouponLine'");
    target.mCouponLine = view;
    view = finder.findRequiredView(source, 2131624345, "method 'OnLeftBtnClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.OnLeftBtnClick();
        }
      });
    view = finder.findRequiredView(source, 2131624346, "method 'OnReWardLayoutClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.OnReWardLayoutClick();
        }
      });
    view = finder.findRequiredView(source, 2131624313, "method 'OnCouponLayoutClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.OnCouponLayoutClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.mViewPager = null;
    target.mRewardTxt = null;
    target.mRewardLine = null;
    target.mCouponTxt = null;
    target.mCouponLine = null;
  }
}
