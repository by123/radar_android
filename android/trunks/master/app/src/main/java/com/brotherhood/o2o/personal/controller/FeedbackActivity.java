package com.brotherhood.o2o.personal.controller;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.personal.helper.PersonalUrlFetcher;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.ProgressButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/6/9.
 */
public class FeedbackActivity extends BaseActivity {


    @InjectView(R.id.btn_commit)
    ProgressButton mCommitBtn;

    @InjectView(R.id.edittext)
    EditText mEditText;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_feedback);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCommitBtn.setEnabled(false);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mCommitBtn.setEnabled(true);

                } else {
                    mCommitBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.btn_commit)
    void OnCommitClick() {
        String content = mEditText.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            mCommitBtn.setIsProcessing(true);
            PersonalUrlFetcher.getInstance().submitFeedback(content, new HttpClient.OnHttpListener() {
                @Override
                public void OnStart() {

                }

                @Override
                public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                    String jsonStr=respondObject.toString();
                    if(Utils.isRequestValid(jsonStr))
                    {
                        Utils.showShortToast("感谢您的支持！");
                        mEditText.setText("");
                        Utils.hideKeyboard(mEditText);
                    }
                    else
                    {
                        Utils.showShortToast("提交失败，请重试！");
                    }
                    mCommitBtn.setIsProcessing(false);
                }

                @Override
                public void OnFail(HttpClient.RequestStatu statu, String resons) {

                }
            });
        }
        else
        {
            Utils.showShortToast("反馈内容不能为空哦！");
        }
    }

}
