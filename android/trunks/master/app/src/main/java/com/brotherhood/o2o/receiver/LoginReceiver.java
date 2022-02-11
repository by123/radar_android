package com.brotherhood.o2o.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.chat.utils.ChatAPI;
import com.brotherhood.o2o.config.Constants;
import com.skynet.library.message.MessageManager;

/**
 * Created by by.huang on 2015/7/21.
 */
public class LoginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(AccountComponent.ACTION_USER_LOGIN)) {
            AccountComponent.shareComponent().getUserInfo();
            initIM(context);
        }
    }

    private void initIM(Context context) {
        String token = AccountComponent.shareComponent().getAccessTokenOrNil();
        String token_secret = AccountComponent.shareComponent().getAccessTokenSecretOrNil();

        MessageManager.getDefault(context).initializeSdk(Constants.DGC_APP_KEY, Constants.DGC_APP_SECRET,
                MessageManager.GameType.ONLINE,
                token,
                token_secret);
        long mSenderUid = Long.parseLong(AccountComponent.shareComponent().getLoginUserInfoOrNil().mUid);
        ChatAPI.get(context).setUserId(mSenderUid);
    }

}
