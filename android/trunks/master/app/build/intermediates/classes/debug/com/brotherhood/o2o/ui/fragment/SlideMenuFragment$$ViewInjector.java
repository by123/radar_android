// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SlideMenuFragment$$ViewInjector<T extends com.brotherhood.o2o.ui.fragment.SlideMenuFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624071, "field 'mPhoneTxt'");
    target.mPhoneTxt = finder.castView(view, 2131624071, "field 'mPhoneTxt'");
    view = finder.findRequiredView(source, 2131624069, "field 'mNameTxt'");
    target.mNameTxt = finder.castView(view, 2131624069, "field 'mNameTxt'");
    view = finder.findRequiredView(source, 2131624103, "field 'mGenderLayout'");
    target.mGenderLayout = view;
    view = finder.findRequiredView(source, 2131624104, "field 'mGenderTxt'");
    target.mGenderTxt = finder.castView(view, 2131624104, "field 'mGenderTxt'");
    view = finder.findRequiredView(source, 2131624254, "field 'mGenderImg'");
    target.mGenderImg = finder.castView(view, 2131624254, "field 'mGenderImg'");
    view = finder.findRequiredView(source, 2131624340, "field 'mSystemMsgCountTxt'");
    target.mSystemMsgCountTxt = finder.castView(view, 2131624340, "field 'mSystemMsgCountTxt'");
    view = finder.findRequiredView(source, 2131624338, "field 'mSurpriseCountTxt'");
    target.mSurpriseCountTxt = finder.castView(view, 2131624338, "field 'mSurpriseCountTxt'");
    view = finder.findRequiredView(source, 2131624343, "field 'mAboutPointImg'");
    target.mAboutPointImg = view;
    view = finder.findRequiredView(source, 2131624335, "field 'mAvatarImg'");
    target.mAvatarImg = finder.castView(view, 2131624335, "field 'mAvatarImg'");
    view = finder.findRequiredView(source, 2131624334, "method 'onUserCellClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onUserCellClick();
        }
      });
    view = finder.findRequiredView(source, 2131624337, "method 'onSurpriseCellClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSurpriseCellClick();
        }
      });
    view = finder.findRequiredView(source, 2131624339, "method 'onMessageCellClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onMessageCellClick();
        }
      });
    view = finder.findRequiredView(source, 2131624341, "method 'onFeedbackCellClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onFeedbackCellClick();
        }
      });
    view = finder.findRequiredView(source, 2131624342, "method 'onAboutCellClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAboutCellClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.mPhoneTxt = null;
    target.mNameTxt = null;
    target.mGenderLayout = null;
    target.mGenderTxt = null;
    target.mGenderImg = null;
    target.mSystemMsgCountTxt = null;
    target.mSurpriseCountTxt = null;
    target.mAboutPointImg = null;
    target.mAvatarImg = null;
  }
}
