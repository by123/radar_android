// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.surprise.controller;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class CouponRewardFragment$$ViewInjector<T extends com.brotherhood.o2o.surprise.controller.CouponRewardFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624310, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131624310, "field 'mRecyclerView'");
    view = finder.findRequiredView(source, 2131624363, "field 'mCouponNullLayout'");
    target.mCouponNullLayout = view;
  }

  @Override public void reset(T target) {
    target.mRecyclerView = null;
    target.mCouponNullLayout = null;
  }
}
