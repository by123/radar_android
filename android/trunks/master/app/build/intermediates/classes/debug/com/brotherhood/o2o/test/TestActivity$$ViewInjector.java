// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.test;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class TestActivity$$ViewInjector<T extends com.brotherhood.o2o.test.TestActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624370, "field 'mMoveTab'");
    target.mMoveTab = view;
    view = finder.findRequiredView(source, 2131624367, "field 'mTab1' and method 'onTab1Click'");
    target.mTab1 = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onTab1Click();
        }
      });
    view = finder.findRequiredView(source, 2131624368, "field 'mTab2' and method 'onTab2Click'");
    target.mTab2 = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onTab2Click();
        }
      });
    view = finder.findRequiredView(source, 2131624369, "field 'mTab3' and method 'onTab3Click'");
    target.mTab3 = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onTab3Click();
        }
      });
    view = finder.findRequiredView(source, 2131624092, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131624092, "field 'mViewPager'");
  }

  @Override public void reset(T target) {
    target.mMoveTab = null;
    target.mTab1 = null;
    target.mTab2 = null;
    target.mTab3 = null;
    target.mViewPager = null;
  }
}
