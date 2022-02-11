package com.brotherhood.o2o.personal.helper;

import android.content.Context;
import android.content.Intent;

import com.brotherhood.o2o.config.Constants;

/**
 * Created by by.huang on 2015/8/4.
 */
public class PersonalHelper {


    /**
     * 发送系统信息到来广播
     */
    public static void sendReceiveSystemMsgBoradCast(Context context) {
        Intent intent = new Intent(Constants.SYSTEM_MSG_CHANGED);
        context.sendBroadcast(intent);
    }
}
