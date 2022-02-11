package com.brotherhood.o2o.chat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.ChatCompent;
import com.brotherhood.o2o.chat.db.DatabaseHandler;
import com.brotherhood.o2o.chat.utils.ChatDbHelper.MessageItem;
import com.brotherhood.o2o.chat.utils.ChatDbHelper.SessionItem;
import com.brotherhood.o2o.chat.utils.ChatManager;
import com.brotherhood.o2o.chat.utils.ChatManager.*;
import com.brotherhood.o2o.chat.utils.UiWatcher;
import com.brotherhood.o2o.chat.utils.Utils;
import com.brotherhood.o2o.utils.ByLogout;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;
import com.skynet.library.message.MessageManager.MessageEntity.ChatType;
import com.skynet.library.message.MessageManager.MessageOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ChatFragment extends Fragment implements TextWatcher,
        View.OnClickListener, View.OnTouchListener, View.OnFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener,
        InterceptLayout.InterceptTouchListener,
        ChatEditText.ChatEditTextListener, EmotionManager.EmotionListener {

    /**
     * 会话id，如果此activity由会话界面启动，传入此参数
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
     * 若为非群组消息，此项必填
     *
     * @see Intent#getLongExtra(String, long)
     */
    public static final String EXTRAS_TARGET_ID = "target_id";
    private static final String TAG = "ChatFragment";
    private static final int REQUEST_CHOICE_PHOTO = 100;
    private static final int REQUEST_TAKE_PHOTO = 101;
    private static final int REQUEST_CAPTURE_VIDEO = 102;
    private static final long SESSION_ID_INVALID = -1L;
    private static final String KEY_SOFT_INPUT_HEIGHT = "si_height";
    private static final int MODE_LEFT_INPUT_TEXT = 1;
    private static final int MODE_LEFT_INPUT_VOICE = 2;
    private static final int MODE_RIGHT_SEND_TEXT = 1;
    private static final int MODE_RIGHT_CHOOSE_OTHER_MESSAGE = 2;
    private static final int MODE_FOOTER_GONE = 1;
    private static final int MODE_FOOTER_FEATURES = 2;
    /*----------------toolbar end-------------*/
    private static final int MODE_FOOTER_EMOTIONS = 3;
    private static final int MODE_FOOTER_VOLATILE = 4;
    private static final int PAGE_SIZE = 20;
    private static final long START_ID_INVALID = -1L;
    private static final int DEFAULT_VOLATILE_SECS = 5;
    private static final String KEY_VOLATILE_SECS = "volatile_secs";
    private static long sMessageId = Long.MIN_VALUE;
    private View mRootView;
    /*----------------footer view end-------------*/
    private InterceptLayout mInterceptLayout;
    private View mFetchMore;
    private RecyclerView mMessagesView;
    /*------------voice recording frame end-----------*/
    private LinearLayoutManager mLayoutManager;
    /*----------------toolbar-------------*/
    private ImageView mToolbarLeft;
    private TextView mToolbarVolatile;
    private View mFocusController;
    private TextView mVoiceButton;
    private ChatEditText mText;
    private ImageView mBtnEmotion;
    private ImageView mToolbarRight;
    private ChatMsgAdapter mAdapter;
    /*----------------footer view-------------*/
    private View mFooterView;
    private View mFeaturesLayout;
    private View mEmotionsLayout;
    private ViewPager mEmotionsPager;
    private LinearLayout mEmotionsPagerIndexPanel;
    private EmotionManager mEmotionManager;
    private ViewStub mVolatileStub;
    /*--------------voice recording frame-------------*/
    private View mFrameVoice;
    private ImageView mVoiceIndicator;
    private TextView mVoiceTipsText;
    private InputMethodManager mInputManager;
    private MediaRecorder mVoiceRecorder;
    private ChatManager mChatApi;
    private DatabaseHandler mDbHandler;
    private SharedPreferences mPreferences;
    private int mPrefsInputHeight;
    private long mSessionId;
    private int mSessionType = ChatType.SINGLE_CHAT
            .getValue();
    private long mCurrUserId;
    private long mGroupId;
    private long mDestUserId;
    /**
     * 可能会有更多的聊天记录
     */
    private boolean mMayHaveMoreRecords;
    private ChatScrollListener mChatScrollListener;
    private ChatActivity mChatActivity;
    private Window mWindow;
    private boolean mSoftInputVisible;
    private boolean mShouldFooterViewGoneAfterInputHide;
    private long mStartRecordingTime;
    private String mVoiceFileName;
    private int mToolbarLeftMode = MODE_LEFT_INPUT_TEXT;
    private int mToolbarRightMode = MODE_RIGHT_CHOOSE_OTHER_MESSAGE;
    private int mToolbarFooterMode = MODE_FOOTER_GONE;
    /**
     * 上次查询的第一条记录的id
     */
    private long mLastStartId = START_ID_INVALID;
    private MyWatcher mWatcher = new MyWatcher();
    private boolean mStopped;
    private LocationManagerProxy mProxy;
    private AMapLocationListener mLocationListener;
    private View mVolatileLayout;
    private TextView mVolatileTimeText;
    private SeekBar mVolatileSeekBar;
    private int mPrefsVolatileSeconds;
    private String mAvatarUrl;
    private String mNickName;
    private int mGender;
    private SeekBar.OnSeekBarChangeListener mTimeSeekChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            int secs = determineSecs(progress);
            if (secs != mPrefsVolatileSeconds) {
                String secsStr = String.valueOf(secs);
                mToolbarVolatile.setText(secsStr);
                mVolatileTimeText.setText(secsStr);
                if (Logger.DEBUG) {
                    Logger.i(TAG, "persist volatile seconds");
                }
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(KEY_VOLATILE_SECS, secs);
                editor.commit();
                mPrefsVolatileSeconds = secs;
            }
        }
    };
    private Uri mLastCameraPhotoUri;
    private ImagePickListener mPickListener;

    public ChatFragment() {
        if (Logger.DEBUG) {
            Logger.i(TAG, "new ChatFragment, hashCode=" + hashCode());
        }
    }

    private Toolbar mToolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Logger.DEBUG) {
            Logger.i(TAG, "onAttach, activity=" + activity.hashCode());
        }
        mChatActivity = (ChatActivity) activity;
        mWindow = activity.getWindow();
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (Logger.DEBUG) {
            Logger.d(TAG, "onCreate, savedInstanceState="
                    + (state != null ? state.hashCode() : "null"));
        }
        Bundle intent = getArguments();
        mCurrUserId = intent.getLong(ChatActivity.EXTRAS_CURRENT_USER_ID, -1L);
        mSessionId = intent.getLong(ChatActivity.EXTRAS_SESSION_ID,
                SESSION_ID_INVALID);
        mSessionType = intent.getInt(EXTRAS_SESSION_TYPE,
                ChatType.SINGLE_CHAT.getValue());
        mAvatarUrl = intent.getString(ChatActivity.EXTRAS_AVATAR_URL, null);
        mNickName = intent.getString(ChatActivity.EXTRAS_NICKNAME, null);
        mGender=intent.getInt(ChatActivity.EXTARS_GENDER);
        if (mSessionType == ChatType.GROUP_CHAT
                .getValue()) {
            mGroupId = intent.getLong(EXTRAS_TARGET_ID, -1L);
        } else {
            mDestUserId = intent.getLong(EXTRAS_TARGET_ID, -1L);
        }
        mChatApi = ChatManager.getDefault(mChatActivity);
        mChatApi.setUserId(mCurrUserId);
        mDbHandler = mChatApi.getDbHandler();
        mChatApi.clearNotification(mSessionId);
        mInputManager = (InputMethodManager) mChatActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mPreferences = mChatActivity.getSharedPreferences("chat_settings",
                Context.MODE_PRIVATE);
        mPrefsVolatileSeconds = mPreferences.getInt(KEY_VOLATILE_SECS,
                DEFAULT_VOLATILE_SECS);
        mPrefsInputHeight = mPreferences.getInt(KEY_SOFT_INPUT_HEIGHT, -1);

        mChatApi.registerUiWatcher(MessageManager.EVENT_NEW_MSG_ARRIVED,
                mWatcher);
        mChatApi.registerUiWatcher(MessageManager.EVENT_SEND_MSG, mWatcher);
        mChatApi.registerUiWatcher(MessageManager.EVENT_SEND_MULTIMEDIA_MSG,
                mWatcher);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle state) {
        if (Logger.DEBUG) {
            Logger.d(TAG, "onCreateView, savedInstanceState="
                    + (state != null ? state.hashCode() : "null"));
        }
        View v = inflater.inflate(R.layout.chat_activity_layout, container,
                false);
        initViews(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        if (Logger.DEBUG) {
            Logger.d(TAG, "onViewCreated, savedInstanceState="
                    + (state != null ? state.hashCode() : "null"));
        }
    }

    private void initViews(View root) {
        mRootView = root;
        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        TextView mTitleTxt = (TextView) mRootView.findViewById(R.id.txt_toolbar);
        mTitleTxt.setText(mNickName);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatActivity.finish();
            }
        });

        root.getViewTreeObserver().addOnGlobalLayoutListener(this);

        // Chat frame views
        View frameMain = root.findViewById(R.id.chat_frame_main);

        // Footer views
        mFooterView = frameMain.findViewById(R.id.chat_footer);
        if (mPrefsInputHeight > 0) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "si height=" + mPrefsInputHeight);
            }
            LinearLayout.LayoutParams footParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, com.brotherhood.o2o.utils.Utils.dip2px(130));
            mFooterView.setLayoutParams(footParams);
        }
        mVolatileStub = (ViewStub) mFooterView
                .findViewById(R.id.chat_stub_volatile_control_panel);
        mFeaturesLayout = mFooterView.findViewById(R.id.chat_features_layout);
        mFeaturesLayout.findViewById(R.id.chat_choose_photo)
                .setOnClickListener(this);
        mFeaturesLayout.findViewById(R.id.chat_choose_camera_photo)
                .setOnClickListener(this);
        mEmotionsLayout = mFooterView.findViewById(R.id.chat_emotions_layout);
        mEmotionsPager = (ViewPager) mEmotionsLayout
                .findViewById(R.id.chat_emotions_pager);
        mEmotionsPagerIndexPanel = (LinearLayout) mEmotionsLayout
                .findViewById(R.id.chat_emotions_pager_index_panel);
        mEmotionManager = new EmotionManager(mChatActivity, mEmotionsPager,
                mEmotionsPagerIndexPanel);
        mEmotionManager.setEmotionListener(this);

        // The middle chat records showing panel
        mInterceptLayout = (InterceptLayout) frameMain
                .findViewById(R.id.chat_msgs_parent);

        if (mSessionType == ChatType.ANONYMOUS_INBOUND
                .getValue()
                || mSessionType == ChatType.ANONYMOUS_OUTBOUND
                .getValue()) {
            mInterceptLayout
                    .setBackgroundResource(R.color.chat_ui_background_anonym);
        }
        mInterceptLayout.setInterceptListener(this);

        mFetchMore = mInterceptLayout.findViewById(R.id.chat_fetch_more_pb);
        mMessagesView = (RecyclerView) mInterceptLayout
                .findViewById(R.id.chat_listview);
        ViewCompat.setOverScrollMode(mMessagesView,
                ViewCompat.OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(mChatActivity);
        mMessagesView.setLayoutManager(mLayoutManager);
        LinkedList<MessageItem> items = loadMessages(mSessionId);
        mAdapter = new ChatMsgAdapter(mChatActivity, items, mCurrUserId);
        mMessagesView.setAdapter(mAdapter);
        mChatScrollListener = new ChatScrollListener();
        mMessagesView.setOnScrollListener(mChatScrollListener);
        scrollListToBottom();

        // Toolbar
        View toolbar = frameMain.findViewById(R.id.chat_toolbar);
        mToolbarLeft = (ImageView) toolbar.findViewById(R.id.chat_toolbar_left);
        mToolbarLeft.setOnClickListener(this);
        mToolbarVolatile = (TextView) toolbar
                .findViewById(R.id.chat_toolbar_volatile);
        mToolbarVolatile.setText(String.valueOf(mPrefsVolatileSeconds));
        mToolbarVolatile.setOnClickListener(this);
        View middle = toolbar.findViewById(R.id.chat_toolbar_middle_frame);
        mFocusController = middle
                .findViewById(R.id.chat_toolbar_focus_controller);
        mFocusController.requestFocus();
        mText = (ChatEditText) middle.findViewById(R.id.chat_toolbar_text);
        mText.addTextChangedListener(this);
        mText.setOnFocusChangeListener(this);
        mText.setChatEditTextListener(this);
        mBtnEmotion = (ImageView) middle
                .findViewById(R.id.chat_toolbar_emotion);
        mBtnEmotion.setOnClickListener(this);
        mVoiceButton = (TextView) middle.findViewById(R.id.chat_toolbar_voice);
        mVoiceButton.setOnTouchListener(this);
        mToolbarRight = (ImageView) toolbar
                .findViewById(R.id.chat_toolbar_right);
        mToolbarRight.setOnClickListener(this);

        // Voice frame views
        mFrameVoice = root.findViewById(R.id.chat_frame_voice);
        mVoiceIndicator = (ImageView) mFrameVoice
                .findViewById(R.id.chat_voice_frame_indicator);
        mVoiceTipsText = (TextView) mFrameVoice
                .findViewById(R.id.chat_voice_frame_text);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        View root = mRootView.getRootView();
        root.getWindowVisibleDisplayFrame(r);
        Display display = mWindow.getWindowManager().getDefaultDisplay();
        int h = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point p = new Point();
            display.getSize(p);
            h = p.y;
        } else {
            h = display.getHeight();
        }
        int inputHeight = h - r.bottom;
        boolean visible = inputHeight > 100;
        if (visible) {
            if (Logger.DEBUG) {
                Logger.w(TAG, "visible");
            }
            if (Logger.DEBUG) {
                Logger.i(TAG, "last si height=" + mPrefsInputHeight);
                Logger.i(TAG, "new si height=" + inputHeight);
            }

            if (inputHeight != mPrefsInputHeight) {
                mPrefsInputHeight = inputHeight;
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(KEY_SOFT_INPUT_HEIGHT, inputHeight);
                editor.commit();

                LinearLayout.LayoutParams footerP = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, com.brotherhood.o2o.utils.Utils.dip2px(130));
                mFooterView.setLayoutParams(footerP);
            }
        } else {
            if (Logger.DEBUG) {
                Logger.w(TAG, "invisible");
            }
        }
        if (visible == mSoftInputVisible) {
            return;
        }
        if (visible) {
            scrollListToBottom();
        }
        mSoftInputVisible = visible;
        onSoftInputVisibilityChanged(visible);
    }

    public void onSoftInputVisibilityChanged(boolean visible) {
        if (Logger.DEBUG) {
            Logger.i(TAG, "onSoftInputVisibilityChanged, visible=" + visible);
        }
        if (visible) {
            mShouldFooterViewGoneAfterInputHide = false;
            mFooterView.setVisibility(View.GONE);
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mToolbarFooterMode = MODE_FOOTER_GONE;
        } else {
            if (mShouldFooterViewGoneAfterInputHide) {
                mFooterView.setVisibility(View.GONE);
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mToolbarFooterMode = MODE_FOOTER_GONE;
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        checkHasText();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mText) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "mText, onFocusChange, hasFocus=" + hasFocus);
            }
            if (hasFocus) {
                checkHasText();
            }
            mBtnEmotion.setSelected(hasFocus);
        }
    }

    @Override
    public boolean onImeBack() {
        if (Logger.DEBUG) {
            Logger.i(TAG, "onImeBack");
        }
        return hideFooterViewOrSoftInput();
    }

    private void checkHasText() {
        String text = mText.getEditableText().toString().trim();
        if (text.length() > 0) {
            // Has some text in the edit box
            if (mToolbarRightMode == MODE_RIGHT_CHOOSE_OTHER_MESSAGE) {
                mToolbarRightMode = MODE_RIGHT_SEND_TEXT;
                mToolbarRight.setImageResource(R.drawable.chat_send_selector);
            }
        } else {
            // No text
            if (mToolbarRightMode == MODE_RIGHT_SEND_TEXT) {
                mToolbarRightMode = MODE_RIGHT_CHOOSE_OTHER_MESSAGE;
                mToolbarRight
                        .setImageResource(R.drawable.selector_chat_add_btn);
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.chat_toolbar_right) {
            onToolbarRightClick();
        } else if (id == R.id.chat_toolbar_left) {
            onToolbarLeftClick();
        } else if (id == R.id.chat_toolbar_emotion) {
            onToobarEmotionClick();
        } else if (id == R.id.chat_choose_photo) {
            onChoosePhoto();
        } else if (id == R.id.chat_choose_camera_photo) {
            onTakePicture();
        } else if (id == R.id.chat_toolbar_volatile) {
            onToolbarVolatileMsg();
        }
    }

    @Override
    public boolean onIntercept(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            hideFooterViewOrSoftInput();
        }
        return false;
    }

    @Override
    public void onEmotionSelected(int resId, String desc) {
        mText.addEmotion(resId, desc);
    }

    @Override
    public void onBackspacePressed() {
        mText.backwardDelete();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mVoiceButton) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mVoiceButton.setText(R.string.tips_untouch_to_finish);
                mVoiceButton
                        .setBackgroundResource(R.drawable.chat_voice_button_pressed);
                mFrameVoice.setVisibility(View.VISIBLE);

                startRecordingVoice();
            } else if (action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_UP) {
                mVoiceButton.setText(R.string.tips_press_to_speak);
                mVoiceButton
                        .setBackgroundResource(R.drawable.chat_voice_button);
                mFrameVoice.setVisibility(View.GONE);
                stopRecordingVoice();
                float y = event.getY();
                if (y >= -200) {
                    sendRecordedVoice();
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                float y = event.getY();
                if (y < -200) {
                    mVoiceTipsText.setText(R.string.tips_untouch_to_cancel_voice);
                    mVoiceIndicator.setImageResource(R.drawable.ic_chat_voice_cancel);
                } else {
                    mVoiceTipsText.setText(R.string.tips_move_up_to_cancel_voice);
                    mVoiceIndicator.setImageResource(R.drawable.ic_chat_voice_process);
                }
            }
            return true;
        }
        return false;
    }

    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    private void startRecordingVoice() {
        MediaRecorder cache = mVoiceRecorder;
        if (cache == null) {
            cache = new MediaRecorder();
            mVoiceRecorder = cache;
        }
        mStartRecordingTime = SystemClock.uptimeMillis();
        try {
            cache.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1) {
                cache.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            } else {
                cache.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            }
            cache.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            cache.setAudioEncodingBitRate(5600);
            mVoiceFileName = "out_" + System.currentTimeMillis() + ".amr";
            cache.setOutputFile(mChatApi.getVoicePath() + "/" + mVoiceFileName);
            cache.prepare();
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingVoice() {
        if (mVoiceRecorder == null) {
            return;
        }
        try {
            mVoiceRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVoiceRecorder.reset();
        mVoiceRecorder.release();
        mVoiceRecorder = null;
    }

    private void sendRecordedVoice() {
        long interval = SystemClock.uptimeMillis() - mStartRecordingTime;
        if (interval < 1000) {
            Toast.makeText(mChatActivity,
                    R.string.tips_recorded_voice_too_short, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        long seconds = interval / 1000L;
        if (Logger.DEBUG) {
            Logger.d(TAG, "record interval=" + interval + ", seconds="
                    + seconds);
        }
        File f = new File(mChatApi.getVoicePath(), mVoiceFileName);
        byte[] content = Utils.readFileToBytes(f);
        if (content == null) {
            Toast.makeText(mChatActivity,
                    R.string.chat_record_audio_error_no_sdcard,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.length <= 6) {
            Toast.makeText(mChatActivity,
                    R.string.chat_record_audio_error_no_permission,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        sendVoiceMsg(content, f.getPath(), seconds);
    }

    private void onToolbarLeftClick() {
        if (mToolbarLeftMode == MODE_LEFT_INPUT_TEXT) {
            mToolbarLeftMode = MODE_LEFT_INPUT_VOICE;
            mText.setVisibility(View.GONE);
            mBtnEmotion.setVisibility(View.GONE);
            mFocusController.requestFocus();

            if (mSoftInputVisible) {
                mInputManager
                        .hideSoftInputFromWindow(mText.getWindowToken(), 0);
                mShouldFooterViewGoneAfterInputHide = true;
            } else {
                mFooterView.setVisibility(View.GONE);
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }

            mVoiceButton.setVisibility(View.VISIBLE);
            mToolbarLeft.setImageResource(R.drawable.selector_chat_text_btn);
            if (mToolbarRightMode == MODE_RIGHT_SEND_TEXT) {
                mToolbarRightMode = MODE_RIGHT_CHOOSE_OTHER_MESSAGE;
                mToolbarRight
                        .setImageResource(R.drawable.selector_chat_add_btn);
            }
        } else {
            mToolbarLeftMode = MODE_LEFT_INPUT_TEXT;
            mText.setVisibility(View.VISIBLE);
            mText.requestFocus();
            mBtnEmotion.setVisibility(View.VISIBLE);
            mInputManager.showSoftInput(mText, 0);
            mVoiceButton.setVisibility(View.GONE);
            mToolbarLeft.setImageResource(R.drawable.selector_chat_voice_btn);
        }
    }

    private void onToolbarRightClick() {
        if (mToolbarRightMode == MODE_RIGHT_SEND_TEXT) {
            String content = mText.getText().toString();
            if (content.length() > 0) {
                mText.setText("");
                sendTextMsg(content);
            }
        } else {
            // Choose feature
            if (mToolbarLeftMode == MODE_LEFT_INPUT_VOICE) {
                mToolbarLeftMode = MODE_LEFT_INPUT_TEXT;
                mText.setVisibility(View.VISIBLE);
                mBtnEmotion.setVisibility(View.VISIBLE);
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                mFooterView.setVisibility(View.VISIBLE);
                mFocusController.requestFocus();
                mVoiceButton.setVisibility(View.GONE);
                mToolbarLeft
                        .setImageResource(R.drawable.selector_chat_voice_btn);
                scrollListToBottom();
            } else {
                if (mToolbarFooterMode == MODE_FOOTER_EMOTIONS) {
                    // If right now it is showing emotions panel...
                    mFocusController.requestFocus();
                    mFeaturesLayout.setVisibility(View.VISIBLE);
                    mEmotionsLayout.setVisibility(View.GONE);
                    mToolbarFooterMode = MODE_FOOTER_FEATURES;
                } else if (mToolbarFooterMode == MODE_FOOTER_VOLATILE) {
                    // If right now it is showing emotions panel...
                    mFocusController.requestFocus();
                    mFeaturesLayout.setVisibility(View.VISIBLE);
                    mVolatileLayout.setVisibility(View.GONE);
                    mToolbarFooterMode = MODE_FOOTER_FEATURES;
                } else if (mToolbarFooterMode == MODE_FOOTER_FEATURES) {
                    // Otherwise showing features panel...
                    mText.requestFocus();
                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    mInputManager.showSoftInput(mText, 0);
                    mToolbarFooterMode = MODE_FOOTER_GONE;
                } else {
                    // if mToolbarFooterMode = MODE_FOOTER_GONE
                    scrollListToBottom();
                    mFocusController.requestFocus();
                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    mShouldFooterViewGoneAfterInputHide = false;
                    mInputManager.hideSoftInputFromWindow(
                            mText.getWindowToken(), 0);

                    mFooterView.setVisibility(View.VISIBLE);
                    mFeaturesLayout.setVisibility(View.VISIBLE);
                    mEmotionsLayout.setVisibility(View.GONE);
                    if (mVolatileLayout != null) {
                        mVolatileLayout.setVisibility(View.GONE);
                    }
                    mToolbarFooterMode = MODE_FOOTER_FEATURES;
                }
            }
        }
    }

    private void onToobarEmotionClick() {
        mEmotionManager.destroy();
        mEmotionManager.initialize();
        do {
            if (mToolbarFooterMode == MODE_FOOTER_EMOTIONS) {
                break;
            }
            if (mToolbarFooterMode == MODE_FOOTER_GONE) {
                // No footer view, so show the footer view and switch to
                // emotions panel
                mFooterView.setVisibility(View.VISIBLE);
                mEmotionsLayout.setVisibility(View.VISIBLE);
                mFeaturesLayout.setVisibility(View.GONE);
                if (mVolatileLayout != null) {
                    mVolatileLayout.setVisibility(View.GONE);
                }

                scrollListToBottom();

                if (mSoftInputVisible) {
                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    mShouldFooterViewGoneAfterInputHide = false;
                    mInputManager.hideSoftInputFromWindow(
                            mText.getWindowToken(), 0);
                }
                break;
            }

            if (mToolbarFooterMode == MODE_FOOTER_FEATURES) {
                mFeaturesLayout.setVisibility(View.GONE);
                mEmotionsLayout.setVisibility(View.VISIBLE);
                break;
            }
            if (mToolbarFooterMode == MODE_FOOTER_VOLATILE) {
                mVolatileLayout.setVisibility(View.GONE);
                mEmotionsLayout.setVisibility(View.VISIBLE);
            }
        } while (false);
        mText.requestFocus();
        mToolbarFooterMode = MODE_FOOTER_EMOTIONS;
    }

    private LinkedList<MessageItem> loadMessages(long sessionId) {
        if (sessionId == SESSION_ID_INVALID) {
            sessionId = querySessionIdFromHistory();
        }
        if (sessionId == SESSION_ID_INVALID) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "this is a new session");
            }
            return new LinkedList<MessageItem>();
        }
        LinkedList<MessageItem> list = new LinkedList<MessageItem>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(MessageItem.TABLE);
        sql.append(" WHERE ");
        sql.append(MessageItem.COL_SESSION_ID);
        sql.append(" = ?");
        if (mLastStartId != START_ID_INVALID) {
            sql.append(" AND ");
            sql.append(MessageItem.COL_ID);
            sql.append(" < ");
            sql.append(mLastStartId);
        }
        sql.append(" AND ");
        sql.append(MessageItem.COL_SESSION_TYPE);
        sql.append(" = ?");
        sql.append(" ORDER BY ");
        sql.append(MessageItem.COL_ID);
        sql.append(" DESC LIMIT ");
        sql.append(PAGE_SIZE);

        if (Logger.DEBUG) {
            Logger.d(TAG, "load sql=" + sql.toString());
        }
        Cursor c = mDbHandler.query(sql.toString(),
                new String[]{String.valueOf(sessionId), "" + mSessionType});
        if (c != null) {
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
            int idxFilePath = c.getColumnIndex(MessageItem.COL_CONTENT_PATH);
            int idxViewed = c.getColumnIndex(MessageItem.COL_VOL_VIEWED);
            int idxAliveSecs = c.getColumnIndex(MessageItem.COL_VOL_ALIVE_SECS);
            int idxUpdateTime = c.getColumnIndex(MessageItem.COL_UPDATE_TIME);
            int idxSessionType = c.getColumnIndex(MessageItem.COL_SESSION_TYPE);
            boolean moveToLast = c.moveToLast();
            while (moveToLast || c.moveToPrevious()) {
                moveToLast = false;

                MessageItem item = new MessageItem();
                item.id = c.getLong(idxId);
                item.sender = c.getLong(idxSender);
                item.sendStatus = c.getInt(idxStatus);
                item.downloadStatus = c.getInt(idxDownloadStatus);
                item.receiver = c.getLong(idxReceiver);
                item.msgType = c.getInt(idxMsgType);
                item.content = c.getBlob(idxContent);
                item.options = MessageOptions.fromLong(c
                        .getLong(idxOptions));
                if (item.msgType == MessageManager.MessageEntity.MsgType.IMAGE
                        .getValue() && item.content != null) {
                    try {
                        JSONObject jo = new JSONObject(new String(item.content));
                        String thumbId = jo.optString("thumb_id");
                        String fileId = jo.optString("file_id");
                        if (!TextUtils.isEmpty(thumbId)
                                && !TextUtils.isEmpty(fileId)) {
                            MultimediaImage image = new MultimediaImage();
                            image.fileId = fileId;
                            image.thumbId = thumbId;
                            item.image = image;
                        }
                    } catch (Exception e) {
                        Log.e(TAG,
                                "error occurred when resolving a image msg, abort");
                        if (Logger.DEBUG) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                } else if (item.msgType == MessageManager.MessageEntity.MsgType.VOICE
                        .getValue() && item.content != null) {
                    try {
                        JSONObject jo = new JSONObject(new String(item.content));
                        String fileId = jo.optString("file_id");
                        if (!TextUtils.isEmpty(fileId)) {
                            MultimediaAudio audio = new MultimediaAudio();
                            audio.fileId = fileId;
                            item.audio = audio;
                        }
                    } catch (Exception e) {
                        Log.e(TAG,
                                "error occurred when resolving a VOICE msg, abort");
                        if (Logger.DEBUG) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                }

                item.duration = c.getLong(idxDuration);
                item.contentFilePath = c.getString(idxFilePath);
                item.volViewed = c.getInt(idxViewed);
                item.volAliveSecs = c.getInt(idxAliveSecs);
                item.updateTime = c.getLong(idxUpdateTime);
                item.sessionType = c.getInt(idxSessionType);
                list.add(item);
            }
            try {
                c.close();
            } catch (Exception e) {
            }
        }
        int size = list.size();
        if (Logger.DEBUG) {
            Logger.i(TAG, "get " + size + " results for session " + sessionId);
        }
        if (size > 0) {
            mLastStartId = list.get(0).id;
        }
        if (size >= PAGE_SIZE) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "probably have more older records");
            }
            mMayHaveMoreRecords = true;
        } else {
            if (Logger.DEBUG) {
                Logger.i(TAG, "no more older records");
            }
            mMayHaveMoreRecords = false;
        }
        return list;
    }

    private void scrollListToBottom() {
        if (mAdapter.getItemCount() >= 1) {
            mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Logger.DEBUG) {
                Logger.d(TAG, "onKeyDown, back");
            }
            if (hideFooterViewOrSoftInput()) {
                return true;
            }
        }
        return false;
    }

    private boolean hideFooterViewOrSoftInput() {
        if (mSoftInputVisible) {
            mInputManager.hideSoftInputFromWindow(mText.getWindowToken(), 0);
            mShouldFooterViewGoneAfterInputHide = true;
            return true;
        }
        if (mFooterView.getVisibility() != View.GONE) {
            mFooterView.setVisibility(View.GONE);
            mToolbarFooterMode = MODE_FOOTER_GONE;
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        if (Logger.DEBUG) {
            Logger.d(TAG, "onActivityCreate, savedInstanceState="
                    + (state != null ? state.hashCode() : "null"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onStart");
        }
        // mChatManager.registerUiWatcher(MessageManager.EVENT_NEW_MSG_ARRIVED,
        // mWatcher);
        // mChatManager.registerUiWatcher(MessageManager.EVENT_SEND_MSG,
        // mWatcher);
        // mChatManager.registerUiWatcher(
        // MessageManager.EVENT_SEND_MULTIMEDIA_MSG, mWatcher);
        mStopped = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onResume");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (Logger.DEBUG) {
            Logger.d(TAG, "onHiddenChanged, hidden=" + hidden);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onPause");
        }
        stopLocation();
        mChatApi.clearNotification(mSessionId);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onStop");
        }
        // mChatManager.unregisterUiWatcher(MessageManager.EVENT_NEW_MSG_ARRIVED,
        // mWatcher);
        // mChatManager.unregisterUiWatcher(MessageManager.EVENT_SEND_MSG,
        // mWatcher);
        // mChatManager.unregisterUiWatcher(
        // MessageManager.EVENT_SEND_MULTIMEDIA_MSG, mWatcher);

        mStopped = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onDestroyView");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Logger.DEBUG) {
            Logger.d(TAG, "onSaveInstanceState, outState=" + outState);
        }
    }

    @Override
    public void onViewStateRestored(Bundle state) {
        super.onViewStateRestored(state);
        if (Logger.DEBUG) {
            Logger.d(TAG, "onViewStateRestored, savedInstanceState="
                    + (state != null ? state.hashCode() : "null"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onDestroy");
        }
        if (mVoiceRecorder != null) {
            try {
                mVoiceRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mAdapter != null) {
            mAdapter.close();
        }

        mChatApi.unregisterUiWatcher(MessageManager.EVENT_NEW_MSG_ARRIVED,
                mWatcher);
        mChatApi.unregisterUiWatcher(MessageManager.EVENT_SEND_MSG, mWatcher);
        mChatApi.unregisterUiWatcher(MessageManager.EVENT_SEND_MULTIMEDIA_MSG,
                mWatcher);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onDetach");
        }
    }


    private void sendLocationMsg(String loc) {
        sendMsg(MessageManager.MessageEntity.MsgType.LOCATION, loc.getBytes(),
                null, null);
    }

    private void stopLocation() {
        if (mProxy != null) {
            mProxy.removeUpdates(mLocationListener);
            mProxy.destroy();
        }
        mProxy = null;
    }

    private void checkVolatilePanel() {
        if (mVolatileTimeText != null) {
            return;
        }
        mVolatileLayout = mVolatileStub.inflate();
        mVolatileTimeText = (TextView) mVolatileLayout
                .findViewById(R.id.chat_volatile_time_text);
        mVolatileTimeText.setText(String.valueOf(mPrefsVolatileSeconds));
        mVolatileSeekBar = (SeekBar) mVolatileLayout
                .findViewById(R.id.chat_volatile_seekbar);
        mVolatileSeekBar.setProgress(determineProgress(mPrefsVolatileSeconds));
        mVolatileSeekBar.setOnSeekBarChangeListener(mTimeSeekChangeListener);
    }

    // 1 3 5 7 10 15 20 30 60 120
    private int determineSecs(int progress) {
        int secs = DEFAULT_VOLATILE_SECS;
        if (progress < 10) {
            secs = 1;
        } else if (progress >= 10 && progress < 20) {
            secs = 3;
        } else if (progress >= 20 && progress < 30) {
            secs = 5;
        } else if (progress >= 30 && progress < 40) {
            secs = 7;
        } else if (progress >= 40 && progress < 50) {
            secs = 10;
        } else if (progress >= 50 && progress < 60) {
            secs = 15;
        } else if (progress >= 60 && progress < 70) {
            secs = 20;
        } else if (progress >= 70 && progress < 80) {
            secs = 30;
        } else if (progress >= 80 && progress < 90) {
            secs = 60;
        } else if (progress >= 90) {
            secs = 120;
        }
        return secs;
    }

    // 1 3 5 7 10 15 20 30 60 120
    private int determineProgress(int secs) {
        int p = 25;
        switch (secs) {
            case 1:
                p = 5;
                break;
            case 3:
                p = 15;
                break;
            case 5:
                p = 25;
                break;
            case 7:
                p = 35;
                break;
            case 10:
                p = 45;
                break;
            case 15:
                p = 55;
                break;
            case 20:
                p = 65;
                break;
            case 30:
                p = 75;
                break;
            case 60:
                p = 85;
                break;
            case 120:
                p = 95;
                break;
        }
        return p;
    }

    private void onToolbarVolatileMsg() {
        if (mToolbarVolatile.isSelected()) {
            mToolbarVolatile.setSelected(false);
            if (mToolbarFooterMode == MODE_FOOTER_VOLATILE) {
                mText.requestFocus();
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                mInputManager.showSoftInput(mText, 0);
                mToolbarFooterMode = MODE_FOOTER_GONE;
            }
            return;
        }
        do {
            checkVolatilePanel();
            if (mToolbarFooterMode == MODE_FOOTER_EMOTIONS) {
                mEmotionsLayout.setVisibility(View.GONE);
                mVolatileLayout.setVisibility(View.VISIBLE);
                break;
            }
            if (mToolbarFooterMode == MODE_FOOTER_FEATURES) {
                mFeaturesLayout.setVisibility(View.GONE);
                mVolatileLayout.setVisibility(View.VISIBLE);
                break;
            }
            if (mToolbarFooterMode == MODE_FOOTER_GONE) {
                // No footer view, so show the footer view and switch to
                // volatile control panel
                mFooterView.setVisibility(View.VISIBLE);
                mVolatileLayout.setVisibility(View.VISIBLE);
                mFeaturesLayout.setVisibility(View.GONE);
                mEmotionsLayout.setVisibility(View.GONE);

                scrollListToBottom();

                if (mSoftInputVisible) {
                    mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    mShouldFooterViewGoneAfterInputHide = false;
                    mInputManager.hideSoftInputFromWindow(
                            mText.getWindowToken(), 0);
                }
            }

        } while (false);
        mToolbarVolatile.setSelected(true);
        mToolbarFooterMode = MODE_FOOTER_VOLATILE;
    }

    @SuppressLint("InlinedApi")
    private void onChoosePhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if (intent.resolveActivity(mChatActivity.getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CHOICE_PHOTO);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            if (intent.resolveActivity(mChatActivity.getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CHOICE_PHOTO);
            }
        }
    }

    private void onTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mChatActivity.getPackageManager()) != null) {
            File f = new File(mChatApi.getCameraPath(), "JPEG_"
                    + System.currentTimeMillis());
            mLastCameraPhotoUri = Uri.fromFile(f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mLastCameraPhotoUri);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHOICE_PHOTO:
                onResultPhoto(resultCode, data);
                break;
            case REQUEST_TAKE_PHOTO:
                onResultTakePhoto(resultCode, data);
                break;
            case REQUEST_CAPTURE_VIDEO:
                onResultCaptureVideo(resultCode, data);
                break;
        }
    }

    private void onResultPhoto(int resultCode, Intent data) {
        hideFooterViewOrSoftInput();
        do {
            if (resultCode != Activity.RESULT_OK || data == null) {
                break;
            }
            Uri uri = data.getData();
            if (uri == null) {
                break;
            }
            if (mPickListener != null) {
                mPickListener.onImagePicked(uri);
            }
        } while (false);
    }

    private void onResultTakePhoto(int resultCode, Intent data) {
        hideFooterViewOrSoftInput();
        do {
            if (resultCode != Activity.RESULT_OK) {
                break;
            }
            if (mLastCameraPhotoUri == null) {
                break;
            }
            if (mPickListener != null) {
                mPickListener.onImagePicked(mLastCameraPhotoUri);
            }
        } while (false);
    }

    private void onResultCaptureVideo(int resultCode, Intent data) {
        hideFooterViewOrSoftInput();

        do {
            if (resultCode != Activity.RESULT_OK) {
                break;
            }
            String videoUri = data.getStringExtra("video_uri");
            File f = new File(videoUri);
            byte[] content = Utils.readFileToBytes(f);
            sendVideoMsg(content, f.getPath());
        } while (false);
    }

    public void setImagePickListener(ImagePickListener l) {
        mPickListener = l;
    }

    private long querySessionIdFromHistory() {
        int sessionType = mSessionType;
        long target = mSessionType == ChatType.GROUP_CHAT
                .getValue() ? mGroupId : mDestUserId;
        String[] args = new String[]{String.valueOf(sessionType),
                String.valueOf(target)};

        long oldSessionId = SESSION_ID_INVALID;
        Cursor c = mDbHandler.query("SELECT * FROM " + SessionItem.TABLE
                + " WHERE " + SessionItem.COL_TYPE + " = ? AND "
                + SessionItem.COL_TARGET + " = ?", args);
        if (c != null) {
            if (c.moveToNext()) {
                oldSessionId = c.getLong(c.getColumnIndex(SessionItem.COL_ID));
            }
            try {
                c.close();
            } catch (Exception e) {
            }
        }
        return oldSessionId;
    }

    public void sendTextMsg(String text) {
        sendMsg(MessageManager.MessageEntity.MsgType.TEXT, text.getBytes(),
                null, null);
    }

    void sendImageMsg(byte[] content, String contentPath) {
        sendMsg(MessageManager.MessageEntity.MsgType.IMAGE, content,
                contentPath, null);
    }

    private void sendVoiceMsg(byte[] content, String contentPath, long duration) {
        sendMsg(MessageManager.MessageEntity.MsgType.VOICE, content,
                contentPath, Long.valueOf(duration));
    }

    private void sendVideoMsg(byte[] content, String contentPath) {
        sendMsg(MessageManager.MessageEntity.MsgType.VIDEO, content,
                contentPath, null);
    }

    public void sendMsg(MessageManager.MessageEntity.MsgType msgType,
                        byte[] content, String contentPath, Object extras) {
        if (Logger.DEBUG) {
            Logger.d(TAG, "sendMsg");
        }

        MessageManager.MessageEntity entity = new MessageManager.MessageEntity();
        MessageOptions options = entity.getOptions();
        // options.encrypted = true;

        if (mSessionType == ChatType.ANONYMOUS_OUTBOUND
                .getValue()) {

            options.anonymField = 1;
        } else if (mSessionType == ChatType.ANONYMOUS_INBOUND
                .getValue()) {
            options.anonymField = 2;
        }

        if (mToolbarVolatile.isSelected()
                && (msgType == MessageManager.MessageEntity.MsgType.IMAGE || msgType == MessageManager.MessageEntity.MsgType.TEXT)) {
            options.shouldBeDeletedAfterViewed = true;
            options.durationBeforeDeleted = (byte) mPrefsVolatileSeconds;

            mToolbarVolatile.setSelected(false);
        }
        if (Logger.DEBUG) {
            long t = options.serializeToLong();
            Logger.d(TAG, "send options=" + Long.toHexString(t));
        }

        String sessionMsg = "";
        if (options.shouldBeDeletedAfterViewed) {
            sessionMsg = getString(R.string.default_session_msg_volatile);
        } else if (msgType == MessageManager.MessageEntity.MsgType.TEXT) {
            sessionMsg = new String(content);
        } else if (msgType == MessageManager.MessageEntity.MsgType.IMAGE) {
            sessionMsg = getString(R.string.default_session_msg_pic);
        } else if (msgType == MessageManager.MessageEntity.MsgType.VOICE) {
            sessionMsg = getString(R.string.default_session_msg_voice);
        } else if (msgType == MessageManager.MessageEntity.MsgType.LOCATION) {
            sessionMsg = getString(R.string.default_session_msg_location);
        } else if (msgType == MessageManager.MessageEntity.MsgType.VIDEO) {
            sessionMsg = getString(R.string.default_session_msg_video);
        }
        ensureSessionCreated(sessionMsg);

        long now = System.currentTimeMillis();
        long nowInSecs = now / 1000L;

        MessageItem mi = new MessageItem();
        // We don't care its session id
        mi.sessionId = mSessionId;

        mi.sessionType = mSessionType;
        mi.sender = mCurrUserId;
        mi.receiver = mSessionType == ChatType.GROUP_CHAT
                .getValue() ? mGroupId : mDestUserId;
        mi.msgType = msgType.getValue();
        mi.options = options;
        mi.volAliveSecs = options.durationBeforeDeleted;
        if (msgType == MessageManager.MessageEntity.MsgType.TEXT
                || msgType == MessageManager.MessageEntity.MsgType.LOCATION) {
            mi.content = content;
        } else if (msgType == MessageManager.MessageEntity.MsgType.IMAGE) {
            mi.contentFilePath = contentPath;
        } else if (msgType == MessageManager.MessageEntity.MsgType.VOICE) {
            mi.contentFilePath = contentPath;
            if (extras instanceof Long) {
                mi.duration = ((Long) extras).longValue();
            }
        } else if (msgType == MessageManager.MessageEntity.MsgType.VIDEO) {
            mi.contentFilePath = contentPath;
        }
        mi.sendStatus = MessageItem.STATUS_SENDING;
        mi.createTime = nowInSecs;
        mi.updateTime = nowInSecs;

        SessionItem tmp = new SessionItem();
        tmp.createTime = nowInSecs;
        tmp.updateTime = nowInSecs;
        tmp.msg = sessionMsg;
        tmp.id = mSessionId;
        tmp.target = mi.receiver;
        tmp.type = mi.sessionType;

        long msgId = -1L;
        if (mSessionId != SESSION_ID_INVALID) {
            ContentValues values = new ContentValues();
            values.put(MessageItem.COL_SESSION_ID, mi.sessionId);
            values.put(MessageItem.COL_SESSION_TYPE, mi.sessionType);
            values.put(MessageItem.COL_SENDER, mi.sender);
            values.put(MessageItem.COL_RECEIVER, mi.receiver);
            values.put(MessageItem.COL_TYPE, mi.msgType);
            if (msgType == MessageManager.MessageEntity.MsgType.TEXT
                    || msgType == MessageManager.MessageEntity.MsgType.LOCATION) {
                values.put(MessageItem.COL_CONTENT, mi.content);
            }
            if (msgType == MessageManager.MessageEntity.MsgType.IMAGE
                    || msgType == MessageManager.MessageEntity.MsgType.VOICE) {
                values.put(MessageItem.COL_CONTENT_PATH, mi.contentFilePath);
            }
            if (msgType == MessageManager.MessageEntity.MsgType.VOICE) {
                values.put(MessageItem.COL_DURATION, mi.duration);
            }
            values.put(MessageItem.COL_VOL_ALIVE_SECS,
                    mi.options.durationBeforeDeleted);
            values.put(MessageItem.COL_OPTIONS, mi.options.serializeToLong());
            values.put(MessageItem.COL_SEND_STATUS, MessageItem.STATUS_SENDING);
            values.put(MessageItem.COL_CREATE_TIME, nowInSecs);
            values.put(MessageItem.COL_UPDATE_TIME, nowInSecs);
            msgId = mDbHandler.insertAndWait(MessageItem.TABLE, values);
            if (Logger.DEBUG) {
                Logger.i(TAG, "insert new msgId=" + msgId);
            }

            ContentValues sessionValues = new ContentValues();
            sessionValues.put(SessionItem.COL_MODIFIED_DATE, nowInSecs);
            sessionValues.put(SessionItem.COL_MSG, sessionMsg);
            sessionValues.put(SessionItem.COL_TARGET, mi.receiver);
            sessionValues.put(SessionItem.COL_TYPE, mi.sessionType);
            mDbHandler.update(SessionItem.TABLE, sessionValues,
                    SessionItem.COL_ID + " = ?",
                    new String[]{String.valueOf(mSessionId)});
        } else {
            Logger.e(TAG, "still can not create session id");
        }

        if (msgId == -1L) {
            msgId = sMessageId;
            sMessageId++;
        }

        mi.id = msgId;

        mAdapter.addItem(mi);
        scrollListToBottom();

        entity.setBody(content);

        ChatType chatType = ChatType.SINGLE_CHAT;

        if (mSessionType == ChatType.GROUP_CHAT.getValue()) {
            chatType = ChatType.GROUP_CHAT;
        } else if (mSessionType == ChatType.ANONYMOUS_INBOUND.getValue()) {
            chatType = ChatType.ANONYMOUS_INBOUND;
        } else if (mSessionType == ChatType.ANONYMOUS_OUTBOUND.getValue()) {
            chatType = ChatType.ANONYMOUS_OUTBOUND;
        }

        entity.setChatType(chatType);
        entity.setMsgType(msgType);
        entity.setReceiver(mSessionType == ChatType.GROUP_CHAT
                .getValue() ? mGroupId : mDestUserId);
        MessageManager.getDefault(mChatActivity).sendMessage(entity, msgId);

        HashMap<String, Object> ps = new HashMap<String, Object>();
        ps.put("session", tmp);
        mChatApi.onCallback(ChatManager.EVENT_CLIENT_SEND_MSG, ps);
    }

    private void ensureSessionCreated(String msg) {
        if (mSessionId != SESSION_ID_INVALID) {
            return;
        }

        int sessionType = mSessionType;
        long target = mSessionType == ChatType.GROUP_CHAT
                .getValue() ? mGroupId : mDestUserId;
        String[] args = new String[]{String.valueOf(sessionType),
                String.valueOf(target)};

        long oldSessionId = -1L;
        Cursor c = mDbHandler.query("SELECT " + SessionItem.COL_ID + " FROM "
                + SessionItem.TABLE + " WHERE " + SessionItem.COL_TYPE
                + " = ? AND " + SessionItem.COL_TARGET + " = ?", args);
        if (c != null) {
            if (c.moveToNext()) {
                oldSessionId = c.getLong(c.getColumnIndex(SessionItem.COL_ID));
            }
            try {
                c.close();
            } catch (Exception e) {
            }
        }

        if (oldSessionId != -1L) {
            mSessionId = oldSessionId;
        } else {
            long nowInSecs = System.currentTimeMillis() / 1000L;
            ContentValues values = new ContentValues();
            values.put(SessionItem.COL_CREATE_DATE, nowInSecs);
            values.put(SessionItem.COL_MODIFIED_DATE, nowInSecs);
            values.put(SessionItem.COL_MSG, msg);
            values.put(SessionItem.COL_TARGET, target);
            values.put(SessionItem.COL_TYPE, sessionType);
            values.put(SessionItem.COL_NICKNAME,mNickName);
            values.put(SessionItem.COL_AVATAR_URL,mAvatarUrl);
            values.put(SessionItem.COL_GENDER,mGender);
            mSessionId = mDbHandler.insertAndWait(SessionItem.TABLE, values);
        }
    }

//    ChatCompent.shareCompent(mChatActivity).updateSessionInfo(mDestUserId, mNickName, mAvatarUrl, mGender);


    public static interface ImagePickListener {

        /**
         * The user chose a image from image selection apps like Gallery
         *
         * @param uri 图片Uri
         */
        public void onImagePicked(Uri uri);

    }

    private class ChatScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView v, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            if (Logger.DEBUG) {
                Logger.d(TAG, "onScrollStateChanged, newState=" + newState);
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int firstCompletelyVisible = mLayoutManager
                        .findFirstCompletelyVisibleItemPosition();
                if (firstCompletelyVisible == 0 && mMayHaveMoreRecords) {
                    mFetchMore.setVisibility(View.VISIBLE);
                    new LoadMoreRecordsTask().execute();
                }
            }
        }

        private class LoadMoreRecordsTask extends
                AsyncTask<Void, Void, LinkedList<MessageItem>> {

            @Override
            protected LinkedList<MessageItem> doInBackground(Void... params) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                LinkedList<MessageItem> items = loadMessages(mSessionId);
                return items;
            }

            @Override
            protected void onPostExecute(LinkedList<MessageItem> items) {
                super.onPostExecute(items);

                int h = mFetchMore.getHeight();
                if (Logger.DEBUG) {
                    Logger.i(TAG, "pb height=" + h);
                }
                mFetchMore.setVisibility(View.GONE);
                int size = items.size();
                if (size > 0) {
                    mAdapter.addItems(items);
                    // mAdapter.notifyItemChanged(size);
                } else {
                    // mAdapter.notifyItemChanged(0);
                }
                mMessagesView.scrollBy(0, -h);

            }

        }
    }

    private class MyWatcher extends UiWatcher {

        @Override
        public void watchAndHandle(String event, int code, Intent intent,
                                   HashMap<String, Object> extras) {
            if (MessageManager.EVENT_NEW_MSG_ARRIVED.equals(event)) {
                ArrayList<MsgEntity> entities = (ArrayList<MsgEntity>) extras
                        .get("entities");
                for (MsgEntity entity : entities) {
                    if (entity.msgSubType == ChatType.SINGLE_CHAT
                            .getValue()) {
                        fromFriend(entity);
                    } else if (entity.msgSubType == ChatType.GROUP_CHAT
                            .getValue()) {
                        fromGroup(entity);
                    } else if (entity.msgSubType == ChatType.ANONYMOUS_INBOUND
                            .getValue()
                            || entity.msgSubType == ChatType.ANONYMOUS_OUTBOUND
                            .getValue()) {
                        fromAnonymous(entity);
                    }
                }
            } else if (MessageManager.EVENT_SEND_MSG.equals(event)
                    || MessageManager.EVENT_SEND_MULTIMEDIA_MSG.equals(event)) {
                Long msgId = (Long) extras.get("msg_id");
                Boolean suc = (Boolean) extras.get("success");
                if (msgId != null && suc != null) {
                    mAdapter.notifySendChanged(msgId, suc);
                }
            }
        }

        private void fromAnonymous(MsgEntity entity) {
            MessageItem item = entity.msgItem;
            mAdapter.addItem(item);
            scrollListToBottom();
        }

        private void fromFriend(MsgEntity entity) {
            if (mSessionType != ChatType.GROUP_CHAT
                    .getValue()
                    && entity.sender == mDestUserId
                    && !entity.blocked) {
                entity.doNotNotify = !mStopped;
                MessageItem item = entity.msgItem;
                mAdapter.addItem(item);
                scrollListToBottom();
            }
        }

        private void fromGroup(MsgEntity entity) {
            if (mSessionType == ChatType.GROUP_CHAT
                    .getValue() && entity.receiver == mGroupId) {
                entity.blocked = !mStopped;
                mAdapter.addItem(entity.msgItem);
                scrollListToBottom();
            }
        }

    }

}
