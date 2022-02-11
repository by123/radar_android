package com.brotherhood.o2o.chat.utils;

public interface ChatUserLoadingListener {

	public void onLoadComplete(boolean suc, long uid, String name,
							   String avatarUrl);

}