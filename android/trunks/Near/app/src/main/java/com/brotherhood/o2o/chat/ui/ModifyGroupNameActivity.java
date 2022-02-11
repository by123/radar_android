package com.brotherhood.o2o.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.Utils;
import com.skynet.library.message.MessageManager;

/**
 * Created by laimo.li on 2016/1/4.
 */
public class ModifyGroupNameActivity extends BaseActivity implements View.OnClickListener {

    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_ID = "group_id";

    @ViewInject(id = R.id.etGroupName)
    private EditText etGroupName;

    @ViewInject(id = R.id.ivEtEmpty, clickMethod = "onClick")
    private ImageView ivEtEmpty;

    private String name;
    private String gid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_group_name_layout;
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRight:
                save();
                break;
            case R.id.ivEtEmpty:
                ivEtEmpty.setVisibility(View.GONE);
                etGroupName.setText("");
                break;
        }
    }

    private void save() {
        DisplayUtil.hideKeyboard(this);
        final String name = etGroupName.getText().toString();
        if (TextUtils.isEmpty(name.replace(" ", ""))) {
            ColorfulToast.orange(this, getString(R.string.put_group_name), 0);
            return;
        }
        if (Utils.containsEmoji(name)) {
            ColorfulToast.orange(this, getString(R.string.group_name_not_allow_emoji), 0);
            return;
        }
        IDSIMManager.getInstance().updateGroupName(gid, name, new MessageManager.HttpCallBack() {

            @Override
            public void onSuc(Object o) {
                ColorfulToast.green(ModifyGroupNameActivity.this, getString(R.string.put_group_name_suc), 0);
                finish();
            }

            @Override
            public void onFail(Object o) {
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        getActionBarController().setBaseTitle(R.string.group_name, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white).addTextItem(R.id.abRight, R.string.group_name_save);

        name = getIntent().getStringExtra(GROUP_NAME);
        gid = getIntent().getStringExtra(GROUP_ID);

        etGroupName.setText(name);

        etGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etGroupName.getText().toString().replace(" ", "");
                if (!TextUtils.isEmpty(text)) {
                    ivEtEmpty.setVisibility(View.VISIBLE);
                } else {
                    ivEtEmpty.setVisibility(View.GONE);
                }
            }
        });


    }

    public static void show(Activity activity, String name, String gid) {
        Intent intent = new Intent(activity, ModifyGroupNameActivity.class);
        intent.putExtra(GROUP_NAME, name);
        intent.putExtra(GROUP_ID, gid);
        activity.startActivityForResult(intent, 0);
    }

}
