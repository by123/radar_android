// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.test;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class TestActivity2$$ViewInjector<T extends com.brotherhood.o2o.test.TestActivity2> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624373, "field 'mScrollableImg'");
    target.mScrollableImg = finder.castView(view, 2131624373, "field 'mScrollableImg'");
  }

  @Override public void reset(T target) {
    target.mScrollableImg = null;
  }
}
