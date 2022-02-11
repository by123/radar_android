// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MainActivity$$ViewInjector<T extends com.brotherhood.o2o.ui.activity.MainActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624077, "field 'mDrawerLayout'");
    target.mDrawerLayout = finder.castView(view, 2131624077, "field 'mDrawerLayout'");
  }

  @Override public void reset(T target) {
    target.mDrawerLayout = null;
  }
}
