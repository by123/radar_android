// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.surprise.controller.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ItemRewardAdapter$ViewHolder$$ViewInjector<T extends com.brotherhood.o2o.surprise.controller.adapter.ItemRewardAdapter.ViewHolder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624128, "field 'mIconImg'");
    target.mIconImg = finder.castView(view, 2131624128, "field 'mIconImg'");
    view = finder.findRequiredView(source, 2131624069, "field 'mNameTxt'");
    target.mNameTxt = finder.castView(view, 2131624069, "field 'mNameTxt'");
    view = finder.findRequiredView(source, 2131624255, "field 'mTimeTxt'");
    target.mTimeTxt = finder.castView(view, 2131624255, "field 'mTimeTxt'");
    view = finder.findRequiredView(source, 2131624362, "field 'mAuthenticationTxt'");
    target.mAuthenticationTxt = finder.castView(view, 2131624362, "field 'mAuthenticationTxt'");
    view = finder.findRequiredView(source, 2131624360, "field 'mStatuImg'");
    target.mStatuImg = finder.castView(view, 2131624360, "field 'mStatuImg'");
    view = finder.findRequiredView(source, 2131624359, "field 'mArrowImg'");
    target.mArrowImg = finder.castView(view, 2131624359, "field 'mArrowImg'");
    view = finder.findRequiredView(source, 2131624361, "field 'mFreeTxt'");
    target.mFreeTxt = finder.castView(view, 2131624361, "field 'mFreeTxt'");
  }

  @Override public void reset(T target) {
    target.mIconImg = null;
    target.mNameTxt = null;
    target.mTimeTxt = null;
    target.mAuthenticationTxt = null;
    target.mStatuImg = null;
    target.mArrowImg = null;
    target.mFreeTxt = null;
  }
}
