// Generated code from Butter Knife. Do not modify!
package com.brotherhood.o2o.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class PerfectUserInfoActivity$$ViewInjector<T extends com.brotherhood.o2o.ui.activity.PerfectUserInfoActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624380, "field 'mLeftImg' and method 'onCloseImgClick'");
    target.mLeftImg = finder.castView(view, 2131624380, "field 'mLeftImg'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCloseImgClick();
        }
      });
    view = finder.findRequiredView(source, 2131624383, "field 'mRightImg' and method 'onConfirmImgClick'");
    target.mRightImg = finder.castView(view, 2131624383, "field 'mRightImg'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onConfirmImgClick();
        }
      });
    view = finder.findRequiredView(source, 2131624097, "field 'mTitleTxt'");
    target.mTitleTxt = finder.castView(view, 2131624097, "field 'mTitleTxt'");
    view = finder.findRequiredView(source, 2131624382, "field 'mTitleImg'");
    target.mTitleImg = finder.castView(view, 2131624382, "field 'mTitleImg'");
    view = finder.findRequiredView(source, 2131624043, "field 'mNicknameEdit'");
    target.mNicknameEdit = finder.castView(view, 2131624043, "field 'mNicknameEdit'");
    view = finder.findRequiredView(source, 2131624042, "field 'mTakePhoto' and method 'onTakePhotoClick'");
    target.mTakePhoto = finder.castView(view, 2131624042, "field 'mTakePhoto'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onTakePhotoClick();
        }
      });
    view = finder.findRequiredView(source, 2131624044, "field 'mMaleImg' and method 'onMaleClick'");
    target.mMaleImg = finder.castView(view, 2131624044, "field 'mMaleImg'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onMaleClick();
        }
      });
    view = finder.findRequiredView(source, 2131624045, "field 'mFemaleImg' and method 'onFemaleClick'");
    target.mFemaleImg = finder.castView(view, 2131624045, "field 'mFemaleImg'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onFemaleClick();
        }
      });
    view = finder.findRequiredView(source, 2131624040, "field 'mAnimCircleView1'");
    target.mAnimCircleView1 = finder.castView(view, 2131624040, "field 'mAnimCircleView1'");
    view = finder.findRequiredView(source, 2131624041, "field 'mAnimCircleView2'");
    target.mAnimCircleView2 = finder.castView(view, 2131624041, "field 'mAnimCircleView2'");
  }

  @Override public void reset(T target) {
    target.mLeftImg = null;
    target.mRightImg = null;
    target.mTitleTxt = null;
    target.mTitleImg = null;
    target.mNicknameEdit = null;
    target.mTakePhoto = null;
    target.mMaleImg = null;
    target.mFemaleImg = null;
    target.mAnimCircleView1 = null;
    target.mAnimCircleView2 = null;
  }
}
