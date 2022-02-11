package com.brotherhood.o2o.chat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.IDSIMQueue;
import com.brotherhood.o2o.chat.db.dao.IMGroupDao;
import com.brotherhood.o2o.chat.db.dao.IMLatestMsgDao;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.chat.helper.ChatSenderHelper;
import com.brotherhood.o2o.chat.helper.Utils;
import com.brotherhood.o2o.chat.helper.VoiceRecorder;
import com.brotherhood.o2o.chat.model.IMChatBean;
import com.brotherhood.o2o.chat.model.IMGroupInfoBean;
import com.brotherhood.o2o.chat.model.IMLatestMsgBean;
import com.brotherhood.o2o.chat.ui.adapter.ChatMsgAdapter;
import com.brotherhood.o2o.chat.ui.view.ChatEditText;
import com.brotherhood.o2o.chat.ui.view.InterceptLayout;
import com.brotherhood.o2o.chat.ui.view.WaveCycleView;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.ui.activity.ChoosePhotoActivity;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.Res;
import com.brotherhood.o2o.util.SharePrefUtil;
import com.brotherhood.o2o.util.SystemVersionUtil;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";

    /*-----------------------------*/
    public static final String KEY_UID = "KEY_UID";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_ICON = "KEY_ICON";
    public static final String KEY_CHAT_MODE = "KEY_CHAT_MODE";
    /*-----------------------------*/
    public static final int REQUEST_FRIEND_DETAIL = 105;
    public static final int REQUEST_GROUP_DETAIL = 103;
    public static final int REQUEST_CHOOSE_PHOTO = 104;
    /*-----------------------------*/
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
    private static final int PAGE_SIZE = 10;
    private static final long START_ID_INVALID = -1L;
    private static final int DEFAULT_VOLATILE_SECS = 5;
    private static final String KEY_VOLATILE_SECS = "volatile_secs";
    private static long sMessageId = Long.MIN_VALUE;
    //    private View mRootView;
    /*----------------footer view end-------------*/
    private InterceptLayout mInterceptLayout;
    private View mFetchMore;
    private RecyclerView mMessagesView;
    /*------------voice recording frame end-----------*/
    private LinearLayoutManager mLayoutManager;
    /*----------------toolbar-------------*/
    private ImageView mToolbarLeft;
    //	private TextView mToolbarVolatile;
    private View mFocusController;
    private TextView mVoiceButton;
    private ChatEditText mText;
    private ImageView mBtnEmotion;
    private ImageView mToolbarMore;
    private Button mToolBarSend;
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
    private WaveCycleView mWaveView;
    private ImageView mVoiceIndicator;
    private TextView mVoiceTipsText;
    private InputMethodManager mInputManager;
    private int mPrefsInputHeight;
    private long mMyUid = Long.valueOf(AccountManager.getInstance().getUser().mUid);
//    private long mCurrUserId;
//    private long mGroupId;
//    private long mDestUserId;
    /**
     * 可能会有更多的聊天记录
     */
    private ChatScrollListener mChatScrollListener;
    private Window mWindow;
    private boolean mSoftInputVisible;
    private boolean mShouldFooterViewGoneAfterInputHide;
    private int mToolbarLeftMode = MODE_LEFT_INPUT_TEXT;
    private int mToolbarRightMode = MODE_RIGHT_CHOOSE_OTHER_MESSAGE;
    private int mToolbarFooterMode = MODE_FOOTER_GONE;
    private Uri mLastCameraPhotoUri;
    private LinearLayout ll_content;
    private ChatSenderHelper mSender;
    private long mUid = 0L;
    private MsgReceiver mMsgReceiver;
    private VoiceRecorder mVoiceRecorder;
    private Handler mMainHandler;
    private String mUserAvatar;
    private String mUserName;
    private ChatSenderHelper.ChatMode mChatMode;
    private boolean mIsKickOut = false;//被t出群

    @Override
    protected int getLayoutId() {
        return R.layout.chat_activity_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainHandler = new Handler(getMainLooper());
        Intent intent = getIntent();
        mUid = Long.valueOf(intent.getStringExtra(KEY_UID));
        mUserAvatar = intent.getStringExtra(KEY_ICON);
        mUserName = intent.getStringExtra(KEY_NAME);
        mChatMode = (ChatSenderHelper.ChatMode) intent.getSerializableExtra(KEY_CHAT_MODE);
        registerReceiver();

        mSender = new ChatSenderHelper(mUid, mChatMode, mIsKickOut, mSendDBChangeListener);
        initViews();
        initGroupInfo();
        initLoadMsg();
        mVoiceRecorder = new VoiceRecorder(this);
        if (!isGroup()) {
            mAdapter.setFriendName(mUserName);
        }
    }

    public static void show(Context context, String uid, String name,
                            String icon, ChatSenderHelper.ChatMode mode) {
        IMDBLatestMsgService.updateLatestMsgToHasRead(Long.valueOf(uid));
        Intent it = new Intent(context, ChatActivity.class);
        it.putExtra(KEY_UID, uid);
        it.putExtra(KEY_NAME, name);
        it.putExtra(KEY_ICON, icon);
        it.putExtra(KEY_CHAT_MODE, mode);
        context.startActivity(it);
    }

    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    private void initActionBar() {
        ActionBarController barController = getActionBarController();
        barController.setBaseTitle(mUserName, R.color.slide_menu_holo_black).
                setHeadBackgroundColor(R.color.white);
        if (isGroup()) {
            barController.addIconItem(R.id.abRight, R.mipmap.ic_msg_add_friend_normal);
        } else {
            barController.addIconItem(R.id.abRight, R.mipmap.ic_msg_user_profile_normal);
        }

    }

    // ====================================================

    // 发送时改变数据库回调
    private ChatSenderHelper.OnDBChangeListener mSendDBChangeListener = new ChatSenderHelper.
            OnDBChangeListener() {
        @Override
        public void onAddChange(final IMChatBean bean) {
            mAdapter.addItem(bean);
            scrollListToBottom();
            IDSIMQueue.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    IMLatestMsgBean bean1 = IMLatestMsgBean.getBean(bean);
                    IMLatestMsgDao.getInstance(ChatActivity.this).addLatestMsg(bean1);
                    IMLatestMsgDao.getInstance(ChatActivity.this).updateToHasRead(bean1.taUid);
                }
            });
        }
    };

    // ====================================================

    private void registerReceiver() {
        mMsgReceiver = new MsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IDSIMManager.ACTION_IM_ON_REC_MSG);
        filter.addAction(IDSIMManager.ACTION_IM_ON_SEND_RESULT);
        filter.addAction(IDSIMManager.ACTION_IM_ON_REC_MSG_MULTI);
        filter.addAction(IDSIMManager.ACTION_IM_UPDATE_GROUP_NAME_MSG);
        filter.addAction(GroupDetailActivity.ACTION_SHOW_NAME);
        filter.addAction(GroupDetailActivity.ACTION_DELETE_TABLE);
        registerReceiver(mMsgReceiver, filter);
    }

    // sdk推送消息
    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            postMain(new Runnable() {
                @Override
                public void run() {
                    String action = intent.getAction();
                    if (action.equals(IDSIMManager.ACTION_IM_ON_REC_MSG)) {
                        onReceiveMsg(intent);
                    } else if (action.equals(IDSIMManager.ACTION_IM_ON_SEND_RESULT)) {
                        onSendResult(intent);
                    } else if (action.equals(IDSIMManager.ACTION_IM_ON_REC_MSG_MULTI)) {
                        onReceiveMsgMulti(intent);
                    } else if (action.equals(IDSIMManager.ACTION_IM_UPDATE_GROUP_NAME_MSG)) {
                        updateGroupName(intent);
                    } else if (action.equals(GroupDetailActivity.ACTION_SHOW_NAME)) {
                        showUserName(intent);
                    } else if (action.equals(GroupDetailActivity.ACTION_DELETE_TABLE)) {
                        deleteData(intent);
                    }
                }
            });
        }
    }

    private void onSendResult(Intent intent) {
        Logger.i(TAG, "======onSendResult=======");
        long msgId = intent.getLongExtra(MessageManager.EXTRA_MSG_ID, 0L);
        boolean send_suc = intent.getBooleanExtra(IDSIMManager.KEY_SEND_SUC, false);
        long uid = intent.getLongExtra(IDSIMManager.KEY_SEND_UID, 0L);
        mAdapter.updateSendState(msgId, send_suc);
    }

    private void onReceiveMsg(Intent intent) {
        Logger.i(TAG, "======onReceiveMsgChat=======");
        final IMChatBean bean = intent.getParcelableExtra(IDSIMManager.KEY_REC_MSG_BEAN);
        long uid = 0;
        if (bean.subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue() ||
                bean.subType == MessageManager.MessageEntity.ChatType.
                        FEED_FROM_GROUP_ACTIVITY.getValue()) {
            uid = bean.groupId;
        } else {
            uid = bean.receiverId;
        }
        if (uid != mUid) {//不是当前人
            return;
        }
        if (bean.subType == MessageManager.MessageEntity.ChatType.SINGLE_CHAT.getValue() ||
                bean.subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue() ||
                bean.subType == MessageManager.MessageEntity.ChatType.
                        FEED_FROM_GROUP_ACTIVITY.getValue()) {
            IMDBLatestMsgService.updateLatestMsgToHasRead(mUid);
        }
        bean.hasRead = true;
        mAdapter.addItem(bean);
        scrollListToBottom();
        IMDBService.updateMsgToRead(bean, isGroup(), null);
        boolean isComingMsg = bean.sender != mMyUid;
        if (bean.subType == MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()
                && isComingMsg) {
            getActionBarController().setBaseTitle(bean.extra, R.color.slide_menu_holo_black);
        } else if (bean.subType == MessageManager.MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()
                && isComingMsg) {
            this.mIsKickOut = true;
            mSender.setIsKickOut(mIsKickOut);
            mAdapter.setIsKickOutGroup(mIsKickOut);
            findViewById(R.id.abRight).setVisibility(View.GONE);
        } else if (bean.subType == MessageManager.MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()
                && isComingMsg) {
            this.mIsKickOut = false;
            mSender.setIsKickOut(mIsKickOut);
            mAdapter.setIsKickOutGroup(mIsKickOut);
            findViewById(R.id.abRight).setVisibility(View.VISIBLE);
        }
    }

    private void onReceiveMsgMulti(Intent intent) {
        Logger.i(TAG, "======onReceiveMsgMulti=======");
        LinkedList<IMChatBean> beans = intent.getParcelableExtra(IDSIMManager.KEY_REC_MSG_BEAN_MULTI);
        if (beans == null || beans.isEmpty()) {
            return;
        }
        long uid = 0;
        IMChatBean bean = beans.get(0);
        if (bean.subType == MessageManager.MessageEntity.ChatType.GROUP_CHAT.getValue() ||
                bean.subType == MessageManager.MessageEntity.ChatType.
                        FEED_FROM_GROUP_ACTIVITY.getValue()) {
            uid = bean.groupId;
        } else {
            uid = bean.receiverId;
        }
        if (uid != mUid) {//不是当前人
            return;
        }
        bean.hasRead = true;
        mAdapter.addItems(beans);
        scrollListToBottom();
        IMDBService.updateAllMsgToRead(isGroup(), uid);
    }

    private void updateGroupName(Intent intent) {
        String name = intent.getStringExtra(IDSIMManager.KEY_GROUP_NAME);
        long gid = Long.valueOf(intent.getStringExtra(IDSIMManager.KEY_SEND_UID));
        if (gid != mUid) {//不是当前人
            return;
        }
        getActionBarController().setBaseTitle(name, R.color.slide_menu_holo_black);
    }


    private void showUserName(Intent intent) {
        boolean show = intent.getBooleanExtra(GroupDetailActivity.KEY_SHOW_USER_NAME, false);
        mAdapter.setShowName(show);
    }

    private void deleteData(Intent intent) {
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    // ====================================================

    private void initLoadMsg() {
        IMDBService.queryLimitLastMsg(this.mUid, PAGE_SIZE, 0, isGroup(), new IMDBService.DBListener() {
            @Override
            public void onResult(Object obj) {
                LinkedList<IMChatBean> list = (LinkedList) obj;
                mAdapter.addItems(list);
                scrollListToBottom();
            }
        });
    }

    private void loadMoreMsg() {
        if (mAdapter != null && mAdapter.getItemCount() == 0) {
            return;
        }
        long rowId = mAdapter.getFirstsBean().id;
        if (rowId > 1) {
            rowId = rowId - 1;
        }
        IMDBService.queryLimitLastMsg(this.mUid, PAGE_SIZE, rowId, isGroup(), new IMDBService.DBListener() {
            @Override
            public void onResult(Object obj) {
                final LinkedList<IMChatBean> list = (LinkedList) obj;
                final int h = mFetchMore.getHeight();
                if (Logger.DEBUG) {
                    Logger.i(TAG, "pb height=" + h);
                }
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFetchMore.setVisibility(View.GONE);
                        mAdapter.addItems(list);
                        mMessagesView.scrollBy(0, -h);
                    }
                }, 500);
            }
        });
    }

    // ====================================================

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.chat_toolbar_right:
                    onToolbarRightClick();
                    break;
                case R.id.chat_toolbar_right_btn:
                    onSendTextClick();
                    break;
                case R.id.chat_toolbar_left:
                    onToolbarLeftClick();
                    break;
                case R.id.chat_toolbar_emotion:
                    onToobarEmotionClick();
                    break;
                case R.id.chat_choose_photo:
                    onChoosePhoto();
                    break;
                case R.id.chat_choose_camera_photo:
                    onTakePicture();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBack:
                finish();
                break;
            case R.id.abRight:
                if (isGroup()) {
                    GroupDetailActivity.show(this, mUid + "", REQUEST_GROUP_DETAIL);
                } else {
                    OtherUserDetailActivity.show(this, mUid + "", false, REQUEST_FRIEND_DETAIL);
                }
                break;
        }
    }

    // ====================================================

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
        // 发图片
        ArrayList<byte[]> results = new ArrayList<byte[]>(size);
        String path = IDSIMManager.getInstance().getImagePath();
        for (int pos = 0; pos < size; pos++) {
            byte[] bs = intent.getByteArrayExtra("pos" + pos);
            File file = new File(path, "OUT_JPEG_"
                    + System.currentTimeMillis());
            Utils.writeFile(file, bs);
            mSender.sendImage(file.getPath());
        }
    }

    // ====================================================

    private void initViews() {
        initActionBar();
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_content.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        mWindow = this.getWindow();
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // Footer views
        mFooterView = findViewById(R.id.chat_footer);
        if (mPrefsInputHeight > 0) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "si height=" + mPrefsInputHeight);
            }
            LinearLayout.LayoutParams footParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mPrefsInputHeight);
            mFooterView.setLayoutParams(footParams);
        }
        mVolatileStub = (ViewStub) mFooterView
                .findViewById(R.id.chat_stub_volatile_control_panel);
        mFeaturesLayout = mFooterView.findViewById(R.id.chat_features_layout);
        mFeaturesLayout.findViewById(R.id.chat_choose_photo)
                .setOnClickListener(onClickListener);
        mFeaturesLayout.findViewById(R.id.chat_choose_camera_photo)
                .setOnClickListener(onClickListener);
        mEmotionsLayout = mFooterView.findViewById(R.id.chat_emotions_layout);
        mEmotionsPager = (ViewPager) mEmotionsLayout
                .findViewById(R.id.chat_emotions_pager);
        mEmotionsPagerIndexPanel = (LinearLayout) mEmotionsLayout
                .findViewById(R.id.chat_emotions_pager_index_panel);
        mEmotionManager = new EmotionManager(this, mEmotionsPager,
                mEmotionsPagerIndexPanel);
        mEmotionManager.setEmotionListener(emotionListener);

        // The middle chat records showing panel
        mInterceptLayout = (InterceptLayout) findViewById(R.id.chat_msgs_parent);

        mInterceptLayout.setInterceptListener(interceptTouchListener);

        mFetchMore = mInterceptLayout.findViewById(R.id.chat_fetch_more_pb);
        mMessagesView = (RecyclerView) mInterceptLayout
                .findViewById(R.id.chat_listview);
        ViewCompat.setOverScrollMode(mMessagesView,
                ViewCompat.OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mLayoutManager.setReverseLayout(true);// Scroll to bottom
//        mLayoutManager.setStackFromEnd(true);
//        mMessagesView.setHasFixedSize(true);
        mMessagesView.setLayoutManager(mLayoutManager);

        mAdapter = new ChatMsgAdapter(this, mChatMode, new LinkedList<IMChatBean>(), this.mUid, mSender);
        mAdapter.setSingleChatAvatar(mUserAvatar);
        mMessagesView.setAdapter(mAdapter);

        mChatScrollListener = new ChatScrollListener();
        mMessagesView.setOnScrollListener(mChatScrollListener);

        // Toolbar
        final View toolbar = findViewById(R.id.chat_toolbar);
        mToolbarLeft = (ImageView) toolbar.findViewById(R.id.chat_toolbar_left);
        mToolbarLeft.setOnClickListener(onClickListener);
        View middle = toolbar.findViewById(R.id.chat_toolbar_middle_frame);
        mFocusController = middle
                .findViewById(R.id.chat_toolbar_focus_controller);
        mFocusController.requestFocus();
        mText = (ChatEditText) middle.findViewById(R.id.chat_toolbar_text);
        mText.addTextChangedListener(textWatcher);
        mText.setOnFocusChangeListener(onFocusChangeListener);
        mText.setChatEditTextListener(chatEditTextListener);
        mBtnEmotion = (ImageView) findViewById(R.id.chat_toolbar_emotion);
        mBtnEmotion.setOnClickListener(onClickListener);
        mVoiceButton = (TextView) middle.findViewById(R.id.chat_toolbar_voice);
        mVoiceButton.setOnTouchListener(onTouchListener);
        mToolbarMore = (ImageView) toolbar
                .findViewById(R.id.chat_toolbar_right);
        mToolBarSend = (Button) findViewById(R.id.chat_toolbar_right_btn);
        mToolBarSend.setOnClickListener(onClickListener);
        mToolbarMore.setOnClickListener(onClickListener);

        // Voice frame views
        View voiceView = getLayoutInflater().inflate(R.layout.chat_voice_record, null);
        int h = DisplayUtil.getScreenHeight(this) - DisplayUtil.getBarHeight(this) - toolbar.getLayoutParams().height;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                h);
        addContentView(voiceView, params);
        mFrameVoice = findViewById(R.id.chat_frame_voice);
        mVoiceIndicator = (ImageView) mFrameVoice
                .findViewById(R.id.chat_voice_frame_indicator);
        mVoiceTipsText = (TextView) mFrameVoice
                .findViewById(R.id.chat_voice_frame_text);
        mWaveView = (WaveCycleView) mFrameVoice.findViewById(R.id.view_wave);
//        mWaveView.setParam(Res.getColor(R.color.near_main_orange_color), 350, 8, 10);

        mAdapter.setChatAdpterListener(new ChatMsgAdapter.ChatAdpterListener() {
            @Override
            public void onLoadPicEnd(int pos) {
                int s = mAdapter.getItemCount() - 1;
                if (s == pos) {
                    scrollListToBottom();
                }
            }
        });
//        if (SystemVersionUtil.isFlyme()) {
//            setFooterView();
//        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (SystemVersionUtil.isFlyme()) {//针对魅族的适配
                return;
            }
            Rect r = new Rect();
            View root = ll_content.getRootView();
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
                    if (inputHeight != mPrefsInputHeight) {
                        mPrefsInputHeight = inputHeight;
                        SharePrefUtil.putInt("", KEY_SOFT_INPUT_HEIGHT, inputHeight);
                        LinearLayout.LayoutParams footerP = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, inputHeight);
                        mFooterView.setLayoutParams(footerP);
                    }
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
    };

    private void setFooterView() {
        Rect r = new Rect();
        View root = ll_content.getRootView();
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

            }
            SharePrefUtil.putInt("", KEY_SOFT_INPUT_HEIGHT, inputHeight);
            LinearLayout.LayoutParams footerP = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, inputHeight);
            mFooterView.setLayoutParams(footerP);
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
        mFooterView.setVisibility(View.VISIBLE);
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

    private TextWatcher textWatcher = new TextWatcher() {
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
    };


    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
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
    };

    private ChatEditText.ChatEditTextListener chatEditTextListener = new ChatEditText.ChatEditTextListener() {
        @Override
        public boolean onImeBack() {
            if (Logger.DEBUG) {
                Logger.i(TAG, "onImeBack");
            }
            return hideFooterViewOrSoftInput();
        }
    };

    private void checkHasText() {
        String text = mText.getEditableText().toString().trim();
        if (text.length() > 0) {
            // Has some text in the edit box
            if (mToolbarRightMode == MODE_RIGHT_CHOOSE_OTHER_MESSAGE) {
                mToolbarRightMode = MODE_RIGHT_SEND_TEXT;
                mToolBarSend.setVisibility(View.VISIBLE);
                mToolbarMore.setVisibility(View.GONE);
            }
        } else {
            // No text
            if (mToolbarRightMode == MODE_RIGHT_SEND_TEXT) {
                mToolbarRightMode = MODE_RIGHT_CHOOSE_OTHER_MESSAGE;
                mToolBarSend.setVisibility(View.GONE);
                mToolbarMore.setVisibility(View.VISIBLE);
            }
        }
    }

    private InterceptLayout.InterceptTouchListener interceptTouchListener = new InterceptLayout.InterceptTouchListener() {
        @Override
        public boolean onIntercept(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                hideFooterViewOrSoftInput();
            }
            return false;
        }
    };

    private EmotionManager.EmotionListener emotionListener = new EmotionManager.EmotionListener() {
        @Override
        public void onEmotionSelected(int resId, String desc) {
            mText.addEmotion(resId, desc);
        }

        @Override
        public void onBackspacePressed() {
            mText.backwardDelete();
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == mVoiceButton) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mVoiceButton.setText(R.string.tips_untouch_to_finish);
                    mVoiceButton
                            .setBackgroundResource(R.drawable.chat_voice_button_pressed);
                    mFrameVoice.setVisibility(View.VISIBLE);
                    mVoiceIndicator.setImageResource(R.mipmap.ic_chat_voice_process);
                    mWaveView.startRippleAnimation();
//                    AnimationDrawable ad = (AnimationDrawable) mVoiceIndicator
//                            .getBackground();
//                    ad.start();

                    mVoiceRecorder.startRecordingVoice();
                    mMainHandler.postDelayed(mVoiceRun, VoiceRecorder.MAX_TIME);
                } else if (action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_UP) {
                    mVoiceButton.setText(R.string.tips_press_to_speak);
                    mVoiceButton
                            .setBackgroundResource(R.drawable.chat_voice_button);
                    mWaveView.stopRippleAnimation();
                    mFrameVoice.setVisibility(View.GONE);
                    mVoiceRecorder.stopRecordingVoice();
                    float y = event.getY();
                    if (y >= -200) {
                        mVoiceRecorder.sendRecordedVoice(mSender);
                    }
                    mMainHandler.removeCallbacks(mVoiceRun);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    float y = event.getY();
                    if (y < -200) {
                        mVoiceIndicator.setImageResource(R.mipmap.ic_chat_voice_cancel);
                        mVoiceTipsText
                                .setText(R.string.tips_untouch_to_cancel_voice);
                    } else {
                        mVoiceIndicator.setImageResource(R.mipmap.ic_chat_voice_process);
                        mVoiceTipsText
                                .setText(R.string.tips_move_up_to_cancel_voice);
                    }
                }
                return true;
            }
            return false;
        }
    };

    private Runnable mVoiceRun = new Runnable() {
        @Override
        public void run() {//60秒强制停止
            mVoiceButton.setText(R.string.tips_press_to_speak);
            mVoiceButton.setBackgroundResource(R.drawable.chat_voice_button);
            mWaveView.stopRippleAnimation();
            mFrameVoice.setVisibility(View.GONE);
            mVoiceRecorder.stopRecordingVoice();
            mVoiceRecorder.sendRecordedVoice(mSender);
            mMainHandler.removeCallbacks(mVoiceRun);
        }
    };

    private void onSendTextClick() {
        String content = mText.getText().toString();
        if (content.length() > 0) {
            mText.setText("");
            mSender.sendText(content);
        }
    }

    private void onToolbarLeftClick() {
        if (mToolbarLeftMode == MODE_LEFT_INPUT_TEXT) {
            mToolbarLeftMode = MODE_LEFT_INPUT_VOICE;
            mText.setVisibility(View.GONE);
//            mBtnEmotion.setVisibility(View.GONE);
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
//			mToolbarLeft.setImageResource(R.drawable.chat_input_text_selector);
            if (mToolbarRightMode == MODE_RIGHT_SEND_TEXT) {
                mToolbarRightMode = MODE_RIGHT_CHOOSE_OTHER_MESSAGE;
//				mToolbarMore
//						.setImageResource(R.drawable.chat_feature_controller_selector);
                mToolBarSend.setVisibility(View.GONE);
                mToolbarMore.setVisibility(View.VISIBLE);
            }
        } else {
            mToolbarLeftMode = MODE_LEFT_INPUT_TEXT;
            mText.setVisibility(View.VISIBLE);
            mText.requestFocus();
            mBtnEmotion.setVisibility(View.VISIBLE);
            mInputManager.showSoftInput(mText, 0);
            mVoiceButton.setVisibility(View.GONE);
            mToolbarLeft.setImageResource(R.mipmap.ic_msg_voice_normal);
//			mToolbarLeft.setImageResource(R.drawable.chat_input_voice_selector);
        }
    }

    private void onToolbarRightClick() {
        // Choose feature
        if (mToolbarLeftMode == MODE_LEFT_INPUT_VOICE) {
            mToolbarLeftMode = MODE_LEFT_INPUT_TEXT;
            mText.setVisibility(View.VISIBLE);
            mBtnEmotion.setVisibility(View.VISIBLE);
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mFooterView.setVisibility(View.VISIBLE);
            mFocusController.requestFocus();
            mVoiceButton.setVisibility(View.GONE);
//				mToolbarLeft
//						.setImageResource(R.drawable.chat_input_voice_selector);
            mToolbarLeft.setImageResource(R.mipmap.ic_msg_voice_normal);
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
                mToolbarFooterMode = MODE_FOOTER_FEATURES;
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
                mEmotionsLayout.setVisibility(View.VISIBLE);
            }
        } while (false);
        mText.requestFocus();
        mToolbarFooterMode = MODE_FOOTER_EMOTIONS;
    }

    private void scrollListToBottom() {
        if (mAdapter.getItemCount() >= 1) {
//            mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
            mMessagesView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
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
        return super.onKeyDown(keyCode, event);
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
    public void onStart() {
        super.onStart();
        if (Logger.DEBUG) {
            Logger.d(TAG, "onStart");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWaveView.stopRippleAnimation();
        IMDBService.updateAllMsgToRead(isGroup(), mUid);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVoiceRecorder.release();
        if (mAdapter != null) {
            mAdapter.close();
        }
        this.unregisterReceiver(mMsgReceiver);
    }

    @SuppressLint("InlinedApi")
    private void onChoosePhoto() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            if (intent.resolveActivity(this.getPackageManager()) != null) {
//                startActivityForResult(intent, REQUEST_CHOICE_PHOTO);
//            }
//        } else {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            if (intent.resolveActivity(this.getPackageManager()) != null) {
//                startActivityForResult(intent, REQUEST_CHOICE_PHOTO);
//            }
//        }
        ChoosePhotoActivity.showResult(this, REQUEST_CHOOSE_PHOTO, 1, true);
    }

    private void onTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            File f = new File(IDSIMManager.getInstance().getImagePath(), "JPEG_"
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
            case REQUEST_GROUP_DETAIL:
                if (GroupDetailActivity.RESULT_DELETE_QUIT_GROUP == resultCode) {
                    finish();
                }
                break;
            case REQUEST_FRIEND_DETAIL:
                if (OtherUserDetailActivity.RESULT_DELETE_MY_FRIEND == resultCode) {
                    finish();
                }
                break;
            case REQUEST_CHOOSE_PHOTO:
                onResultChoosePhoto(resultCode, data);
                break;
        }
    }

    private void onResultChoosePhoto(int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        String path = data.getStringExtra(BundleKey.CHOOSE_HEAD_PHOTO_KEY);
        File file = new File(path);
        if (TextUtils.isEmpty(path) || !file.exists()) {
            return;
        }
//        File file = new File(path, "OUT_JPEG_"
//                + System.currentTimeMillis());
//        Utils.writeFile(file, bs);
        mSender.sendImage(file.getPath());
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
//			if (mPickListener != null) {
//				mPickListener.onImagePicked(uri);
//			}
            Intent intent = new Intent(this, ImgPreviewActivity.class);
            intent.putExtra(ImgPreviewActivity.PREVIEW_URI, uri);
            startActivity(intent);

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
//			if (mPickListener != null) {
//				mPickListener.onImagePicked(mLastCameraPhotoUri);
//			}
            Intent intent = new Intent(this, ImgPreviewActivity.class);
            intent.putExtra(ImgPreviewActivity.PREVIEW_URI, mLastCameraPhotoUri);
            startActivity(intent);
        } while (false);
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
                if (firstCompletelyVisible == 0) {
                    mFetchMore.setVisibility(View.VISIBLE);
//                    new LoadMoreRecordsTask().execute();
                    loadMoreMsg();
                }
            }
        }

//        private class LoadMoreRecordsTask extends
//                AsyncTask<Void, Void, LinkedList<ChatDbHelper.MessageItem>> {
//
//            @Override
//            protected LinkedList<ChatDbHelper.MessageItem> doInBackground(Void... params) {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                }
//                LinkedList<ChatDbHelper.MessageItem> items = loadMessages(mSessionId);
//                return items;
//            }
//
//            @Override
//            protected void onPostExecute(LinkedList<ChatDbHelper.MessageItem> items) {
//                super.onPostExecute(items);
//
//                int h = mFetchMore.getHeight();
//                if (Logger.DEBUG) {
//                    Logger.i(TAG, "pb height=" + h);
//                }
//                mFetchMore.setVisibility(View.GONE);
//                int size = items.size();
//                if (size > 0) {
////					mAdapter.addItems(items);
//                    // mAdapter.notifyItemChanged(size);
//                } else {
//                    // mAdapter.notifyItemChanged(0);
//                }
//                mMessagesView.scrollBy(0, -h);
//            }
//        }
    }

    // =========================================================

    private void initGroupInfo() {
        if (isGroup()) {
            IMGroupInfoBean bean = IMGroupDao.getInstance(this).queryGroupInfo(mUid);
            if (bean == null) {
                return;
            }
            mAdapter.setShowName(bean.showMemberName);
            if (mChatMode == ChatSenderHelper.ChatMode.MODE_GROUP_ACTIVITY) {
                mAdapter.setShowName(true);
            }
            if (bean.isKickOut) {
                this.mIsKickOut = bean.isKickOut;
                mSender.setIsKickOut(mIsKickOut);
                mAdapter.setIsKickOutGroup(mIsKickOut);
                findViewById(R.id.abRight).setVisibility(View.GONE);
            }
            getActionBarController().setBaseTitle(bean.name, R.color.slide_menu_holo_black);
        }
    }

    private void checkIsKickOut() {
        postAysn(new Runnable() {
            @Override
            public void run() {
                IDSIMManager.getInstance().checkIsGroupMember(mUid, new MessageManager.HttpCallBack() {
                    @Override
                    public void onSuc(Object o) {
                        try {
                            JSONObject j = (JSONObject) o;
                            mIsKickOut = j.getInt("Ret") == 255;
                            mSender.setIsKickOut(mIsKickOut);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        postAysn(new Runnable() {
                            @Override
                            public void run() {
                                IMGroupDao.getInstance(ChatActivity.this).setIsKickOut(mUid, mIsKickOut);
                            }
                        });
                    }

                    @Override
                    public void onFail(Object o) {
                    }
                });
            }
        });
    }

    private void postAysn(Runnable run) {
        IDSIMQueue.getInstance().post(run);
    }

    private void postMain(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    // =========================================================
    private boolean isGroup() {
        return mChatMode != ChatSenderHelper.ChatMode.MODE_PRIVATE;
    }
}
