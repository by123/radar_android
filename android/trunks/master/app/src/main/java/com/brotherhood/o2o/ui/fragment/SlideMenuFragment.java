package com.brotherhood.o2o.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.model.account.UserInfo;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.personal.PersonalComponent;
import com.brotherhood.o2o.personal.model.SystemMsgBean;
import com.brotherhood.o2o.surprise.SurpriseComponent;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/25.
 */
public class SlideMenuFragment extends BaseFragment {
    private BroadcastReceiver mReceiver;

    @InjectView(R.id.txt_phone)
    TextView mPhoneTxt;

    @InjectView(R.id.txt_name)
    TextView mNameTxt;

    @InjectView(R.id.layout_gender)
    View mGenderLayout;

    @InjectView(R.id.txt_gender)
    TextView mGenderTxt;

    @InjectView(R.id.img_gender)
    ImageView mGenderImg;

    @InjectView(R.id.txt_systemmsg_count)
    TextView mSystemMsgCountTxt;

    @InjectView(R.id.txt_surprise_count)
    TextView mSurpriseCountTxt;

    @InjectView(R.id.img_point_about)
    View mAboutPointImg;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slide_menu_fragment, container, false);
    }

    @InjectView(R.id.image_avatar)
    SimpleDraweeView mAvatarImg;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        updateUserCell();
        updateSystemMsgCount();
        updateSupriseCount();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action)) {
                    if (action.equalsIgnoreCase(AccountComponent.ACTION_USER_LOGIN)) {
//                        updateUserCell();
                    } else if (action.equalsIgnoreCase(AccountComponent.ACTION_USER_LOGOUT)) {
                        updateUserCell();
                    } else if (action.equalsIgnoreCase(AccountComponent.ACTION_USERINFO_UPDATE)) {
                        updateUserCell();
                    } else if (action.equalsIgnoreCase(Constants.SYSTEM_MSG_CHANGED)) {
                        updateSystemMsgCount();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(AccountComponent.ACTION_USER_LOGIN);
        filter.addAction(AccountComponent.ACTION_USER_LOGOUT);
        filter.addAction(AccountComponent.ACTION_USERINFO_UPDATE);
        filter.addAction(Constants.SYSTEM_MSG_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    protected boolean isTraceFragment() {
        return false;
    }

    @OnClick(R.id.cell_user)
    void onUserCellClick() {
        if (AccountComponent.shareComponent().getLoginUserInfoOrNil() == null) {
            AccountComponent.shareComponent().showLoginPage(getActivity());
        } else {
//            AccountComponent.shareComponent().logout();
            PersonalComponent.shareComponent().showUserInfoPage(getActivity());
        }
    }

    @OnClick(R.id.cell_surprise)
    void onSurpriseCellClick() {
        if (AccountComponent.shareComponent().isLogin()) {
            SurpriseComponent.shareComponent().showMySurprisePage(getActivity());
        } else {
            Utils.showShortToast(R.string.please_login);
        }
    }

    @OnClick(R.id.cell_message)
    void onMessageCellClick() {
        PersonalComponent.shareComponent().showMessagePage(getActivity());
    }

    @OnClick(R.id.cell_feedback)
    void onFeedbackCellClick() {
        PersonalComponent.shareComponent().showFeedbackPage(getActivity());
    }

    @OnClick(R.id.cell_about)
    void onAboutCellClick() {
        PersonalComponent.shareComponent().showAboutPage(getActivity());
    }

    private void updateUserCell() {
        if (!AccountComponent.shareComponent().isLogin()) {
            mGenderLayout.setVisibility(View.GONE);
            mPhoneTxt.setVisibility(View.INVISIBLE);
            mNameTxt.setText(R.string.no_login);
            ImageLoader.getInstance().setImageResource(mAvatarImg, R.drawable.ic_default_avatar);

        } else {
            mGenderLayout.setVisibility(View.VISIBLE);
            mPhoneTxt.setVisibility(View.VISIBLE);
            mNameTxt.setVisibility(View.VISIBLE);
            final UserInfo userInfo = AccountComponent.shareComponent().getmUserInfo();
            if (userInfo != null) {
                mNameTxt.setText(userInfo.mNickName);
                mPhoneTxt.setText(userInfo.mPhone);
                if (!TextUtils.isEmpty(userInfo.mAvatarURL)) {
                    ImageLoader.getInstance().setImageUrl(mAvatarImg, userInfo.mAvatarURL, 1, null, Utils.dip2px(72), Utils.dip2px(72));
                }
                int gender = userInfo.mGenger;
                if (gender == 0) {
                    mGenderLayout.setBackgroundResource(R.drawable.shape_sex_male_bg);
                    mGenderImg.setImageResource(R.drawable.ic_sex_male_white);
                    mGenderTxt.setText(R.string.sex_male);
                } else {
                    mGenderLayout.setBackgroundResource(R.drawable.shape_sex_female_bg);
                    mGenderImg.setImageResource(R.drawable.ic_sex_female_white);
                    mGenderTxt.setText(R.string.sex_female);
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateSystemMsgCount();
    }

    private void updateSystemMsgCount() {
        int count = 0;
        ArrayList<SystemMsgBean> datas = SystemMsgBean.getCache();
        for (SystemMsgBean data : datas) {
            if (!data.mRead) {
                count++;
            }
        }
        if (count == 0) {
            mSystemMsgCountTxt.setVisibility(View.GONE);
        } else {
            mSystemMsgCountTxt.setVisibility(View.VISIBLE);
            mSystemMsgCountTxt.setText("" + count);
        }
    }

    private void updateSupriseCount() {
        int count = 0;
        if (count == 0) {
            mSurpriseCountTxt.setVisibility(View.GONE);
        } else {
            mSurpriseCountTxt.setVisibility(View.VISIBLE);
            mSurpriseCountTxt.setText("" + count);
        }
    }

}
