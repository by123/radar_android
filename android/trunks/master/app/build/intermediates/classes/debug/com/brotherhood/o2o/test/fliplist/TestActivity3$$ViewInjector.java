// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.test.fliplist;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class TestActivity3$$ViewInjector<T extends com.brotherhood.o2o.test.fliplist.TestActivity3> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624063, "field 'mListView'");
    target.mListView = finder.castView(view, 2131624063, "field 'mListView'");
  }

  @Override public void reset(T target) {
    target.mListView = null;
  }
}
