package com.brotherhood.o2o.personal.controller;

import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.SplashActivity;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.extensions.BaseFragment;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.utils.DeviceInfoUtils;
import com.brotherhood.o2o.widget.ProgressButton;
import com.brotherhood.o2o.widget.webview.WebViewActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

/**
 * Created by by.huang on 2015/6/9.
 */
public class AdviceFragment extends BaseFragment {


    @InjectView(R.id.btn_commit)
    ProgressButton mCommitBtn;

    @InjectView(R.id.edittext)
    EditText mEditText;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_advice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getMainActivity().isNeedBottomBar(false);
        initView();
    }

    private void initView() {

        mCommitBtn.setEnabled(false);
        mCommitBtn.setBackgroundColor(getResources().getColor(R.color.gray));

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mCommitBtn.setEnabled(true);
                    mCommitBtn.setBackgroundColor(getResources().getColor(R.color.main_red));

                } else {
                    mCommitBtn.setEnabled(false);
                    mCommitBtn.setBackgroundColor(getResources().getColor(R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getMainActivity().setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getMainActivity()!=null)
                {
                    getMainActivity().swichToFragment(new PersonalFragment(),false);
                }
            }
        });
    }

    @OnClick(R.id.btn_commit)
    void OnCommitClick()
    {
        String content =mEditText.getText().toString();
        if(!TextUtils.isEmpty(content))
        {
            mCommitBtn.setIsProcessing(true);
            getMainActivity().newThread(new BaseActivity.OnThreadListener() {
                @Override
                public void doInThread() {
                    mCommitBtn.setIsProcessing(false);
                    Toast.makeText(getMainActivity(), "感谢您的支持!", Toast.LENGTH_SHORT).show();
                }
            },3000);
        }
    }



}
