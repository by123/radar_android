package com.brotherhood.o2o.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.manager.NetworkStateManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.network.NetworkState;
import com.brotherhood.o2o.ui.widget.ColorfulToast;

/**
 *网络广播接收器
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			NetworkState state = NetworkStateManager.getNetworkState();
			NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.NETWORK_CHANGE, state);
			if (state == NetworkState.UNAVAILABLE){//没有网络
				ColorfulToast.orange(context, context.getString(R.string.connect_network_timeout), Toast.LENGTH_LONG);
			}
		}
	}
}
