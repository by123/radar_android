package com.brotherhood.o2o.chat.ui;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.db.DatabaseHandler;
import com.brotherhood.o2o.chat.utils.ChatDbHelper.MessageItem;
import com.brotherhood.o2o.chat.utils.ChatManager;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;

public class VolatileMsgViewer extends Activity {

	private static final String TAG = "VolatileMsgViewer";

	public static final String EXTRAS_MSG_ID = "_id";
	public static final String ACTION_START_TICK_COUNT = "start_tick_count";

	private long mMsgId;

	private DatabaseHandler mDbHandler;

	private TextView mTextView;
	private ChatTextView mChatTextView;
	private ImageView mImageView;
	private Button mCloseBtn;
	private ProgressBar mProgressBar;

	private long mCurrUserId;

	private Handler mHandler = new Handler();

	private BroadcastReceiver mBroadcastReceiver;

	private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager
			.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_volatile_msg_viewer);

		mTextView = (TextView) findViewById(R.id.bomb_burning_tv);
		mChatTextView = (ChatTextView) findViewById(R.id.chat_msg_text);
		mImageView = (ImageView) findViewById(R.id.chat_msg_pic);
		mCloseBtn = (Button) findViewById(R.id.close_btn);
		mProgressBar = (ProgressBar) findViewById(R.id.chat_download_pb);

		mTextView.setBackgroundResource(R.drawable.boom_new_00000);
		mTextView.setTextColor(Color.WHITE);
		mCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
				finish();
			}
		});

		mDbHandler = ChatManager.getDefault(this).getDbHandler();
		mCurrUserId = ChatManager.getDefault(this).getUserId();

		mMsgId = getIntent().getLongExtra(EXTRAS_MSG_ID, -1L);
		if (mMsgId < 0) {
			finish();
			return;
		}

		Cursor c = mDbHandler.query("SELECT * FROM " + MessageItem.TABLE
				+ " WHERE " + MessageItem.COL_ID + " = ?",
				new String[] { String.valueOf(mMsgId) });
		MessageItem item = null;
		if (c != null) {
			if (c.moveToNext()) {
				int idxId = c.getColumnIndex(MessageItem.COL_ID);
				int idxSender = c.getColumnIndex(MessageItem.COL_SENDER);
				int idxStatus = c.getColumnIndex(MessageItem.COL_SEND_STATUS);
				int idxDownloadStatus = c
						.getColumnIndex(MessageItem.COL_DOWNLOAD_STATUS);
				int idxReceiver = c.getColumnIndex(MessageItem.COL_RECEIVER);
				int idxMsgType = c.getColumnIndex(MessageItem.COL_TYPE);
				int idxOptions = c.getColumnIndex(MessageItem.COL_OPTIONS);
				int idxDuration = c.getColumnIndex(MessageItem.COL_DURATION);
				int idxContent = c.getColumnIndex(MessageItem.COL_CONTENT);
				int idxFilePath = c
						.getColumnIndex(MessageItem.COL_CONTENT_PATH);
				int idxViewed = c.getColumnIndex(MessageItem.COL_VOL_VIEWED);
				int idxAliveSecs = c
						.getColumnIndex(MessageItem.COL_VOL_ALIVE_SECS);
				int idxUpdateTime = c
						.getColumnIndex(MessageItem.COL_UPDATE_TIME);
				int idxSessionType = c
						.getColumnIndex(MessageItem.COL_SESSION_TYPE);
				item = new MessageItem();
				item.id = c.getLong(idxId);
				item.sender = c.getLong(idxSender);
				item.sendStatus = c.getInt(idxStatus);
				item.downloadStatus = c.getInt(idxDownloadStatus);
				item.receiver = c.getLong(idxReceiver);
				item.msgType = c.getInt(idxMsgType);
				item.content = c.getBlob(idxContent);
				item.options = MessageManager.MessageOptions.fromLong(c
						.getLong(idxOptions));
				item.duration = c.getLong(idxDuration);
				item.contentFilePath = c.getString(idxFilePath);
				item.volViewed = c.getInt(idxViewed);
				item.volAliveSecs = c.getInt(idxAliveSecs);
				item.updateTime = c.getLong(idxUpdateTime);
				item.sessionType = c.getInt(idxSessionType);
				if (item.msgType == MessageManager.MessageEntity.MsgType.IMAGE
						.getValue()) {
					try {
						if (item.sender != mCurrUserId) {
							JSONObject jo = new JSONObject(new String(
									item.content));
							final String fileId = jo.optString("file_id");

							final File file = new File(ChatManager.getDefault(
									this).getMultimediaPath(), fileId);

							if (file.exists()) {
								Bitmap bitmap = BitmapFactory
										.decodeFile(ChatManager
												.getDefault(this)
												.getMultimediaPath()
												+ "/" + fileId);
								mImageView.setImageBitmap(bitmap);
								Intent intent = new Intent(
										ACTION_START_TICK_COUNT);
								intent.putExtra("msg_id", mMsgId);
								mLocalBroadcastManager.sendBroadcast(intent);
							} else {
								mProgressBar.setVisibility(View.VISIBLE);
								mTextView.setVisibility(View.INVISIBLE);
								mChatTextView.setVisibility(View.INVISIBLE);
								MessageManager.FileDownloadListener l = new MessageManager.FileDownloadListener() {

									@Override
									public void onDownloadFinished(int code,
											byte[] content) {
										if (code == MessageManager.FileDownloadListener.CODE_FAILED) {
											Toast.makeText(
													VolatileMsgViewer.this,
													getString(R.string.chat_download_img_io),
													Toast.LENGTH_SHORT).show();
										} else if (code == MessageManager.FileDownloadListener.CODE_FILE_DELETED_FROM_SERVER) {
											Toast.makeText(
													VolatileMsgViewer.this,
													getString(R.string.chat_img_not_found_or_ot),
													Toast.LENGTH_SHORT).show();
										} else {
											mProgressBar
													.setVisibility(View.INVISIBLE);
											mTextView
													.setVisibility(View.VISIBLE);
											Bitmap bitmap = BitmapFactory
													.decodeFile(ChatManager
															.getDefault(
																	VolatileMsgViewer.this)
															.getMultimediaPath()
															+ "/" + fileId);
											mImageView.setImageBitmap(bitmap);
											Intent intent = new Intent(
													ACTION_START_TICK_COUNT);
											intent.putExtra("msg_id", mMsgId);
											mLocalBroadcastManager
													.sendBroadcast(intent);
										}
									}
								};

								MessageManager mm = MessageManager
										.getDefault(VolatileMsgViewer.this);
								mm.downloadFile(fileId,
										MessageManager.FILE_TYPE_IMAGE, l);
							}

						} else if (item.sender == mCurrUserId) {
							Bitmap bitmap = BitmapFactory
									.decodeFile(item.contentFilePath);
							mImageView.setImageBitmap(bitmap);
							Intent intent = new Intent(ACTION_START_TICK_COUNT);
							intent.putExtra("msg_id", mMsgId);
							mLocalBroadcastManager.sendBroadcast(intent);
						}
						LayoutParams lp = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						mTextView.setLayoutParams(lp);
					} catch (Exception e) {
						Log.e(TAG,
								"error occurred when resolving a MULTIMEDIA msg");
						if (Logger.DEBUG) {
							e.printStackTrace();
						}
						item = null;
					}
				} else {
					mChatTextView.resolveText(new String(item.content));
					mChatTextView.setMovementMethod(ScrollingMovementMethod
							.getInstance());

					ViewTreeObserver vto = getWindow().getDecorView()
							.getViewTreeObserver();
					vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);

							int windowHeight = getWindow().getDecorView()
									.getHeight();
							int statusBarHeight = frame.top;
							int chatTextViewHeight = mChatTextView.getHeight();

							if (windowHeight - statusBarHeight
									- chatTextViewHeight < 200) {
								LayoutParams lp = (LayoutParams) mChatTextView
										.getLayoutParams();

								lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
								mChatTextView.setLayoutParams(lp);
								mChatTextView.setHeight(windowHeight
										- statusBarHeight - 200);
							}
						}
					});

					Intent intent = new Intent(ACTION_START_TICK_COUNT);
					intent.putExtra("msg_id", mMsgId);
					mLocalBroadcastManager.sendBroadcast(intent);
				}
			}
			try {
				c.close();
			} catch (Exception e) {
			}
		}
		if (item == null) {
			finish();
			return;
		}

		IntentFilter intentFilter = new IntentFilter("bomb_fuse_burning_tick");
		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				int currentCount = intent.getIntExtra("current_count", 0);
				long rcvMsgId = intent.getLongExtra("msg_id", 0);

				if (rcvMsgId != mMsgId) {
					return;
				}

				mTextView.setText(String.valueOf(currentCount));

				if (currentCount == 0) {
					mTextView.setBackgroundResource(R.drawable.bomb_boom);
					mTextView.setText("");
					mChatTextView.setVisibility(View.INVISIBLE);
					mImageView.setVisibility(View.INVISIBLE);
					AnimationDrawable ad = (AnimationDrawable) mTextView
							.getBackground();
					ad.start();

					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mLocalBroadcastManager
									.unregisterReceiver(mBroadcastReceiver);
							finish();
						}
					}, 1050);
				}
			}
		};
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
				intentFilter);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		mTextView.setBackgroundResource(R.drawable.bomb_fuse_burning);
		AnimationDrawable ad = (AnimationDrawable) mTextView.getBackground();
		ad.start();
	}
}
