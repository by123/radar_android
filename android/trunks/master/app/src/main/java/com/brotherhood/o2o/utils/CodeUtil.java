package com.brotherhood.o2o.utils;

/**
 * Created by by.huang on 2015/7/31.
 */
public class CodeUtil {


    private static int ERROR_ORDER_USER_JOINED=5011;
    public static void checkCode(int code)
    {
        if(code == ERROR_ORDER_USER_JOINED)
        {
            Utils.showShortToast("您已经参加过此活动了!");
            return;
        }
    }
}
