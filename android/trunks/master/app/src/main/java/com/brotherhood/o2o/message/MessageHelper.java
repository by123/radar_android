package com.brotherhood.o2o.message;


import com.brotherhood.o2o.application.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装MessagePump
 * Created with Android Studio.
 */
public class MessageHelper {
	private MessageCallback mMessageCallback;
	private List<Message.Type> mMessageTypes = new ArrayList<>();

	public void setMessageCallback(MessageCallback messageCallback) {
		this.mMessageCallback = messageCallback;
	}

	public void registerMessages() {
		if (mMessageCallback == null) {
			return;
		}

		for (Message.Type messageType : mMessageTypes) {
			MyApplication.mApplication.getMessagePump().register(messageType, mMessageCallback);
		}
	}

	public void unRegisterMessages() {
		if (mMessageCallback == null)
			return;

		for (Message.Type messageType : mMessageTypes) {
			MyApplication.mApplication.getMessagePump().unregister(messageType, mMessageCallback);
		}
	}

	/**
	 * 监听消息
	 *
	 * @param mssageType
	 */
	public void attachMessage(Message.Type mssageType) {
		if (mMessageCallback == null) {
			throw new IllegalStateException("You need call setMessageCallback() at first.");
		}

		mMessageTypes.add(mssageType);
	}

	public void clearMessages(){
		mMessageTypes.clear();
		mMessageTypes = null;
		this.mMessageCallback = null;
	}

}
