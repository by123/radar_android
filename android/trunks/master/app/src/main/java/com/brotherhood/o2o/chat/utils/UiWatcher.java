package com.brotherhood.o2o.chat.utils;

import java.util.HashMap;

import android.content.Intent;

public abstract class UiWatcher {

	boolean mAborted;

	public abstract void watchAndHandle(String callbackEvent, int callbackCode,
			Intent data, HashMap<String, Object> map);

	public void abort() {
		mAborted = true;
	}
}
