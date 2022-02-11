package com.brotherhood.o2o.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.brotherhood.o2o.chat.db.DatabaseHandler;


public abstract class ChatAPI {

	/**
	 * 客户端发送一条消息
	 */
	public static final String EVENT_CLIENT_SEND_MSG = "event_client_send";

	/**
	 * 能注册的最大优先级
	 */
	public static final int MAX_PRIORITY = 1000;

	/**
	 * 能注册的最小优先级
	 */
	public static final int MIN_PRIORITY = -1000;

	public static ChatAPI get(Context cxt) {
		return ChatManager.getDefault(cxt);
	}

	public abstract void registerUiWatcher(String event, UiWatcher w);

	public abstract void registerUiWatcherWithPriority(String event,
			int priority, UiWatcher w);

	public abstract void unregisterUiWatcher(String event, UiWatcher w);

	public abstract void setUserId(long uid);

	public abstract void setUserLoader(ChatUserLoaderInterface i);

	public abstract ChatSettings getSettings();

	public abstract DatabaseHandler getDbHandler();

	public abstract void openSingleChatUI(Activity activity, long targetUid,String avatarUrl,String nickname,int gender);

	public abstract void openGroupChatUI(Activity activity, long groupId);

	public class ChatSettings {

		private static final String PREFS_KEY_NOTIFY_ENABLED = "chat_notify_enabled";
		private static final String PREFS_KEY_SOUND = "chat_notify_sound";
		private static final String PREFS_KEY_VIBRATE = "chat_notify_vibrate";
		private static final String PREFS_KEY_LIGHT = "chat_notify_light";

		private Boolean mNotificationEnabled;
		private Boolean mSoundEnabled;
		private Boolean mVibrateEnabled;
		private Boolean mLightEnabled;

		private Class<? extends Activity> mUserDetailActivityClass;

		private SharedPreferences mPreferences;


		private long mSystemSenderId = -1L;

		ChatSettings(Context cxt) {
			mPreferences = cxt.getSharedPreferences("chat_settings",
					Context.MODE_PRIVATE);
		}

		public void setUserDetailActivityClass(Class<? extends Activity> cls) {
			mUserDetailActivityClass = cls;
		}

		public Class<? extends Activity> getUserDetailActivityClass() {
			return mUserDetailActivityClass;
		}

		public void setSystemMsgSenderId(long systemSender) {
			mSystemSenderId = systemSender;
		}

		public long getSystemMsgSenderId() {
			return mSystemSenderId;
		}

		public void setNotificationEnabled(boolean enabled) {
			mNotificationEnabled = enabled;
			mPreferences.edit().putBoolean(PREFS_KEY_NOTIFY_ENABLED, enabled)
					.commit();
		}

		public boolean isNotificationEnabled() {
			if (mNotificationEnabled != null) {
				return mNotificationEnabled.booleanValue();
			}
			mNotificationEnabled = mPreferences.getBoolean(
					PREFS_KEY_NOTIFY_ENABLED, true);
			return mNotificationEnabled;
		}

		public void setNotifySoundEnabled(boolean enabled) {
			mSoundEnabled = enabled;
			mPreferences.edit().putBoolean(PREFS_KEY_SOUND, enabled).commit();
		}

		public boolean isSoundEnabled() {
			if (mSoundEnabled != null) {
				return mSoundEnabled.booleanValue();
			}
			mSoundEnabled = mPreferences.getBoolean(PREFS_KEY_SOUND, true);
			return mSoundEnabled;
		}

		public void setNotifyVibrateEnabled(boolean enabled) {
			mVibrateEnabled = enabled;
			mPreferences.edit().putBoolean(PREFS_KEY_VIBRATE, enabled).commit();
		}

		public boolean isVibrateEnabled() {
			if (mVibrateEnabled != null) {
				return mVibrateEnabled.booleanValue();
			}
			mVibrateEnabled = mPreferences.getBoolean(PREFS_KEY_VIBRATE, true);
			return mVibrateEnabled;
		}

		public void setNotifyLightEnabled(boolean enabled) {
			mLightEnabled = enabled;
			mPreferences.edit().putBoolean(PREFS_KEY_LIGHT, enabled).commit();
		}

		public boolean isLightEnabled() {
			if (mLightEnabled != null) {
				return mLightEnabled.booleanValue();
			}
			mLightEnabled = mPreferences.getBoolean(PREFS_KEY_LIGHT, true);
			return mLightEnabled;
		}

	}

}
