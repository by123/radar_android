//package com.brotherhood.o2o.wrapper;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.util.Log;
//
//import com.brotherhood.o2o.application.NearApplication;
//import com.brotherhood.o2o.util.UDIDUtils;
//import com.idswz.plugin.PluginAPI;
//import com.idswz.plugin.PushAPI;
//
///**
// * Created by ZhengYi on 15/6/5.
// */
//public class DGCPushServiceWrapper {
//    private static BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent != null && intent.getExtras() != null) {
//                long id = intent.getLongExtra("id", 0);
//                String extra = intent.getStringExtra("data");
//                if (extra != null) {
//                    Log.e("DGC_PUSH_SERVICE", "extra: " + extra);
//                }
//                PushAPI.readMessage(context, id);
//            }
//        }
//    };
//    private static boolean mIsRegistered = false;
//
//    private DGCPushServiceWrapper() {
//    }
//
//    public static void init() {
//        Context context = NearApplication.mInstance;
//        PluginAPI.bindUidAndUdid(context, UDIDUtils.getUdid(context));
//        registerReceiverIfNeeded(context);
//    }
//
//    private static void registerReceiverIfNeeded(Context context) {
//        if (!mIsRegistered) {
//            mIsRegistered = true;
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(PushAPI.ACTION + "." + context.getPackageName());
//            context.registerReceiver(mMessageReceiver, filter);
//        }
//    }
//}
