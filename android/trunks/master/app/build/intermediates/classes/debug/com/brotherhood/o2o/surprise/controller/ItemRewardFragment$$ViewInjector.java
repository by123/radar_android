// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.surprise.controller;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ItemRewardFragment$$ViewInjector<T extends com.brotherhood.o2o.surprise.controller.ItemRewardFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131623985, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131623985, "field 'mRecyclerView'");
    view = finder.findRequiredView(source, 2131624364, "field 'mRewardNullLayout'");
    target.mRewardNullLayout = view;
  }

  @Override public void reset(T target) {
    target.mRecyclerView = null;
    target.mRewardNullLayout = null;
  }
}
