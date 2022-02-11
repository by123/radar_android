package com.brotherhood.o2o.chat.ui;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

import com.brotherhood.o2o.chat.ChatCompent;
import com.brotherhood.o2o.chat.utils.ChatManager;
import com.brotherhood.o2o.chat.utils.SkipProguardInterface;
import com.brotherhood.o2o.chat.utils.Utils;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.utils.ByLogout;
import com.github.snowdream.android.app.DownloadManager;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;

public class ChatActivity extends ActionBarActivity implements
		ChatFragment.ImagePickListener, SkipProguardInterface {

	private static final String TAG = "ChatActivity";

	private final static String FRAGMENT_CHATTING = "f_chatting";

	/**
	 * 会话id，如果由会话界面启动，传入此参数
	 */
	public static final String EXTRAS_SESSION_ID = "session_id";

	/**
	 * 会话类型, 如果由会话界面启动，传入此参数
	 */
	public static final String EXTRAS_SESSION_TYPE = "session_type";

	/**
	 * 当前用户id
	 * 
	 * @see {@link Intent#getLongExtra(String, long)}
	 */
	public static final String EXTRAS_CURRENT_USER_ID = "curr_user_id";

	/**
	 * 若为单聊消息，此处填用户id，如果为群聊消息，此处填群id
	 * 
	 * @see Intent#getLongExtra(String, long)
	 */
	public static final String EXTRAS_TARGET_ID = "target_id";

	public static final String EXTRAS_AVATAR_URL= "avatar_url";
	public static final String EXTRAS_NICKNAME= "nickname";
	public static final String EXTARS_GENDER="gender";

	private long mGroupId;
	private long mDestUserId;

	ChatManager mChatManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Logger.DEBUG) {
			Logger.i(TAG, "onCreate");
		}
		Intent intent = getIntent();
		if (!intent.hasExtra(EXTRAS_CURRENT_USER_ID)) {
			throw new RuntimeException(
					"EXTRAS_CURRENT_USER_ID expected to be set");
		}

		if (!intent.hasExtra(EXTRAS_SESSION_TYPE)) {
			throw new RuntimeException("EXTRAS_SESSION_TYPE expected to be set");
		}
		int sessionType = intent.getIntExtra(EXTRAS_SESSION_TYPE,
				MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue());
		if (sessionType == MessageManager.MessageEntity.ChatType.GROUP_CHAT
				.getValue()) {
			mGroupId = intent.getLongExtra(EXTRAS_TARGET_ID, -1L);
			if (mGroupId == -1L) {
				throw new RuntimeException(
						"EXTRAS_TARGET_ID expected to be set");
			}
			setTitle("Group:" + mGroupId);
		} else if (sessionType == MessageManager.MessageEntity.ChatType.ANONYMOUS_INBOUND
				.getValue()) {
			setTitle("匿名好友");
		} else {
			if (mDestUserId == -1L) {
				throw new RuntimeException(
						"EXTRAS_TARGET_ID expected to be set");
			}
			mDestUserId = intent.getLongExtra(EXTRAS_TARGET_ID, -1L);
			setTitle("Friend:" + mDestUserId);
		}
		mChatManager = ChatManager.getDefault(this);

		Bundle bundle = intent.getExtras();
		ChatFragment cf = new ChatFragment();
		cf.setArguments(bundle);
		cf.setImagePickListener(this);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(android.R.id.content, cf, FRAGMENT_CHATTING);
		ft.commit();

	}

	@Override
	public void onImagePicked(Uri uri) {
		if (Logger.DEBUG) {
			Logger.i(TAG, "onImagePicked");
		}

		Intent intent = new Intent(this, ImgPreviewActivity.class);
		intent.putExtra(ImgPreviewActivity.PREVIEW_URI, uri);
		startActivity(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (Logger.DEBUG) {
			Logger.i(TAG, "onNewIntent");
		}

		int size = intent.getIntExtra("size", -1);
		if (size == -1) {
			return;
		}
		ArrayList<byte[]> results = new ArrayList<byte[]>(size);
		for (int pos = 0; pos < size; pos++) {
			results.add(intent.getByteArrayExtra("pos" + pos));
		}
		onPhotoConfirmed(results);
	}

	private void onPhotoConfirmed(ArrayList<byte[]> images) {
		if (Logger.DEBUG) {
			Logger.d(TAG, "onPhotoConfirmed");
		}

		FragmentManager fm = getSupportFragmentManager();
		ChatFragment cf = (ChatFragment) fm
				.findFragmentByTag(FRAGMENT_CHATTING);
		if (cf != null) {
			for (byte[] bs : images) {
				File file = new File(mChatManager.getImagePath(), "OUT_JPEG_"
						+ System.currentTimeMillis());
				Utils.writeFile(file, bs);
				cf.sendImageMsg(bs, file.getPath());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (Logger.DEBUG) {
				Logger.d(TAG, "onKeyDown, back");
			}
			FragmentManager fm = getSupportFragmentManager();
			if (fm.getBackStackEntryCount() == 1) {
				ChatFragment cf = (ChatFragment) fm
						.findFragmentByTag(FRAGMENT_CHATTING);
				if (cf != null && cf.onKeyDown(keyCode, event)) {
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
