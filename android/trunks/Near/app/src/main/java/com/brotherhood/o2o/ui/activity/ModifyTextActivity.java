package com.brotherhood.o2o.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.listener.OnOKHttpResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.request.UpdateUserInfoRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.Utils;

/**
 * 修改个人资料页
 */
public class ModifyTextActivity extends BaseActivity {

    private int mModifyType;
    public static final int MODIFY_TYPE_NICKNAME = 1;
    public static final int MODIFY_TYPE_SIGNATURE = 3;

    @ViewInject(id = R.id.etModifyText)
    private EditText mEtModify;

    @ViewInject(id = R.id.ivModifyClear, clickMethod = "clearText")
    private ImageView mIvClear;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_text_layout;
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    public void clearText(View view) {
        mIvClear.setVisibility(View.GONE);
        mEtModify.setText("");
    }

    public static void show(Activity context, int type, String title, String itemText, int requestCode) {
        Intent it = new Intent(context, ModifyTextActivity.class);
        it.putExtra(BundleKey.MODIFY_TEXT_KEY, type);
        it.putExtra(BundleKey.MODIFY_TEXT_TITLE_KEY, title);
        it.putExtra(BundleKey.MODIFY_TEXT_ITEM_KEY, itemText);
        context.startActivityForResult(it, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mModifyType = intent.getIntExtra(BundleKey.MODIFY_TEXT_KEY, -1);
        if (mModifyType == MODIFY_TYPE_NICKNAME) {//保存修改昵称
            mEtModify.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        } else if (mModifyType == MODIFY_TYPE_SIGNATURE) {
            mEtModify.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        }
        String title = intent.getStringExtra(BundleKey.MODIFY_TEXT_TITLE_KEY);
        String itemText = intent.getStringExtra(BundleKey.MODIFY_TEXT_ITEM_KEY);
        getActionBarController().setBaseTitle(title, R.color.black)
                .setBackImage(R.mipmap.back_image_black)
                .setDivideColor(R.color.black)
                .addTextItem(R.id.abRightText, R.string.addlocation_btn_save)
                .setHeadBackgroundColor(R.color.white);


        mEtModify.requestFocus();
        if (!TextUtils.isEmpty(itemText)) {
            mEtModify.setText(itemText);
            mEtModify.setSelection(itemText.length());
        } else {
            mIvClear.setVisibility(View.GONE);
        }
        initEvent();
    }

    private void initEvent() {
        mEtModify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mEtModify.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    mIvClear.setVisibility(View.VISIBLE);
                } else {
                    mIvClear.setVisibility(View.GONE);
                }
            }
        });
        mEtModify.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mEtModify.getText().toString().trim();
                    mEtModify.setSelection(key.length());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRightText:
                DisplayUtil.hideKeyboard(this);
                String text = mEtModify.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ColorfulToast.orange(this, getString(R.string.modify_text_not_null), 0);
                    return;
                }
                if (mModifyType == MODIFY_TYPE_NICKNAME) {//保存修改昵称
                    if (Utils.containsEmoji(text)) {
                        ColorfulToast.orange(this, getString(R.string.nickname_not_allow_emoji), 0);
                        return;
                    }
                    modifyUserInfo(text, null, null, null, null, null);
                } else if (mModifyType == MODIFY_TYPE_SIGNATURE) {//保存修改个性签名
                    if (Utils.containsEmoji(text)) {
                        ColorfulToast.orange(this, getString(R.string.about_me_not_allow_emoji), 0);
                        return;
                    }
                    modifyUserInfo(null, null, null, null, text, null);
                }
                break;
        }
    }

    private void modifyUserInfo(final String name, String gender, String birthday, String location, final String signature, String iconPath) {
        final String text = name != null ? name : signature;
        UpdateUserInfoRequest mUserInfoRequest = UpdateUserInfoRequest.createUpdateUserInfoRequest(name, gender, birthday, location, signature, iconPath, new OnOKHttpResponseListener<UserInfo>() {
            @Override
            public void onSuccess(int code, String msg, UserInfo userInfo, boolean cache) {
                if (userInfo != null) {
                    AccountManager.getInstance().updateUser(userInfo, true);

                    ColorfulToast.green(ModifyTextActivity.this, getString(R.string.put_group_name_suc), 0);

                    Intent data = new Intent();
                    data.putExtra(BundleKey.MODIFY_RESULT_KEY, text);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                ColorfulToast.orange(ModifyTextActivity.this, msg, 0);
            }
        });
        mUserInfoRequest.postAsyn(true);
    }

}
