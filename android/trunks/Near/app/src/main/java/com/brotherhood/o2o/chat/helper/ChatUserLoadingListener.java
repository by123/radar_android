package com.brotherhood.o2o.chat.helper;

public interface ChatUserLoadingListener {

	public void onLoadComplete(boolean suc, long uid, String name,
							   String avatarUrl);

}