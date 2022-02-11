package com.brotherhood.o2o.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.skynet.library.common.http.ServerError;
import com.skynet.library.message.MessageManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class AddFriendVerifyActivity extends BaseActivity {

    public static final String FRIEND_ID = "friend_id";

    @ViewInject(id = R.id.etVerifyMsg)
    private EditText etVerifyMsg;

    private String friendId;
    private String userName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_friend_verify_layout;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        getActionBarController().setBaseTitle(R.string.frien_validate, R.color.slide_menu_holo_black).setHeadBackgroundColor(R.color.white).addTextItem(R.id.abRight, R.string.send);

        friendId = getIntent().getStringExtra(FRIEND_ID);
        userName = AccountManager.getInstance().getUser().mNickName;

        etVerifyMsg.setText(getResources().getString(R.string.add_friend_send_msg_temp, userName));

    }


    public static void show(Context context, long friendId) {
        Intent intent = new Intent(context, AddFriendVerifyActivity.class);
        intent.putExtra(FRIEND_ID, String.valueOf(friendId));
        context.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRight:
                String verifyMsg = etVerifyMsg.getText().toString();
                if (TextUtils.isEmpty(verifyMsg)) {
                    ColorfulToast.orange(AddFriendVerifyActivity.this, getString(R.string.put_add_friend_send_verify), 0);
                    return;
                }

                IDSIMManager.getInstance().addfriend(String.valueOf(friendId), etVerifyMsg.getText().toString(), "Laimo", new MessageManager.HttpCallBack() {

                    @Override
                    public void onSuc(Object o) {

                        JSONObject j = (JSONObject) o;
                        try {
                            int code = j.getInt("error_code");
                            if (code == 0) {
                                ColorfulToast.green(AddFriendVerifyActivity.this, getString(R.string.add_friend_send_verify_suc), 0);
                                finish();
                            } else {
                                String msg = j.getString("msg");
                                ColorfulToast.orange(AddFriendVerifyActivity.this, msg, 0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFail(Object o) {
                        ServerError error = (ServerError) o;
                        ColorfulToast.orange(AddFriendVerifyActivity.this, error.err_detail, 0);
                    }
                });

                // 添加数据库
                IMDBService.addSayHiFriendMsg(Long.valueOf(friendId),
                        getResources().getString(R.string.add_friend_send_msg_temp, userName));
                break;
        }
    }

}
