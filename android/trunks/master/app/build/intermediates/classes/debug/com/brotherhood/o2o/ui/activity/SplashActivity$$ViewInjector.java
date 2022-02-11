// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SplashActivity$$ViewInjector<T extends com.brotherhood.o2o.ui.activity.SplashActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624047, "field 'mBgImg'");
    target.mBgImg = finder.castView(view, 2131624047, "field 'mBgImg'");
    view = finder.findRequiredView(source, 2131624092, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131624092, "field 'mViewPager'");
    view = finder.findRequiredView(source, 2131624093, "field 'mSpringIndicator'");
    target.mSpringIndicator = finder.castView(view, 2131624093, "field 'mSpringIndicator'");
  }

  @Override public void reset(T target) {
    target.mBgImg = null;
    target.mViewPager = null;
    target.mSpringIndicator = null;
  }
}
