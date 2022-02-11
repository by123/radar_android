package com.brotherhood.o2o.personal.controller;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.extensions.BaseFragment;
import com.brotherhood.o2o.widget.BasicDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/6/9.
 */
public class UserInfoFragment extends BaseFragment {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.txt_phone)
    TextView mPhoneTxt;

    @InjectView(R.id.btn_logout)
    View mLogoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_userinfo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getMainActivity().isNeedBottomBar(false);
        initData();
    }

    private void initData() {
        if(getMainActivity()!=null)
        {
            getMainActivity().setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getMainActivity() != null) {
                        getMainActivity().swichToFragment(new PersonalFragment(), false);
                    }
                }
            });
        }

    }


    @OnClick(R.id.btn_logout)
    void OnLogoutClick() {
        new BasicDialog(getMainActivity(), new BasicDialog.OnDialogListener() {
            @Override
            public void OnConfirm(BasicDialog dialog) {
                dialog.dismiss();
                Toast.makeText(getMainActivity(),"退出登录",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnCancel(BasicDialog dialog) {
                dialog.dismiss();
            }
        }).setMainTxt(getString(R.string.userinfofragment_logout_tips)).hideMinorTxt().show();
    }
}
