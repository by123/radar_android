package com.brotherhood.o2o.chat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;

public class ChatReceiver extends BroadcastReceiver implements
        SkipProguardInterface {

    @Override
    public void onReceive(Context context, Intent intent) {
        long currUid = intent.getLongExtra(
                MessageManager.EXTRA_CALLBACK_CURR_UID, -1L);
        if (currUid <= 0L) {
            Logger.e("ChatReceiver", "invalid uid " + currUid);
            return;
        }
        ChatManager cm = ChatManager.getDefault(context);
        cm.setUserId(currUid);
        cm.onCallback(context, intent);
        Logger.e("David", "event=" + intent.getStringExtra(MessageManager.EXTRA_CALLBACK_EVENT));

    }


}
