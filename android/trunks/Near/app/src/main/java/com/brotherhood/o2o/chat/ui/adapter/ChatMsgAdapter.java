package com.brotherhood.o2o.chat.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.UserInfoBean;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.cache.ImageCache;
import com.brotherhood.o2o.chat.cache.ImageFetcher;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.chat.helper.BitmapHelper;
import com.brotherhood.o2o.chat.helper.ChatSenderHelper;
import com.brotherhood.o2o.chat.helper.IMUserDataHelper;
import com.brotherhood.o2o.chat.helper.Utils;
import com.brotherhood.o2o.chat.helper.VoiceDetector;
import com.brotherhood.o2o.chat.model.IMChatBean;
import com.brotherhood.o2o.chat.model.IMUserBean;
import com.brotherhood.o2o.chat.model.IMUserExtraBean;
import com.brotherhood.o2o.chat.ui.ChatActivity;
import com.brotherhood.o2o.chat.ui.ImgPreviewActivity;
import com.brotherhood.o2o.chat.ui.view.ChatTextView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.request.GetAvatarRequest;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.Res;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//import pl.droidsonroids.gif.GifImageView;

public class ChatMsgAdapter extends
        RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> implements
        OnClickListener {

    private static final String TAG = "ChatMsgAdapter";

    private static final long MSG_TIME_SHOW_DELAY = 5 * 60 * 1000;
    private final VoiceDetector mDetector;
    private final ChatSenderHelper mSender;
    private LinkedList<MessageItemWrapper> mWrappers;
    private ChatActivity mChatActivity;
    private Handler mHandler;
    private long mCurrUser;
    private MediaPlayer mMediaPlayer;
    private ImageFetcher mFetcher;
    private int mImageSentDimen;
    private float mDensity = 1.0f;
    private ChatSenderHelper.ChatMode mChatMode;
    private boolean mShowName = false;
    private String mUserName;
    private long mMyUid = Long.valueOf(AccountManager.getInstance().getUser().mUid);
    private int mMaxItemWidth;
    private int mMinItemWidth;
    private String mSingleChatAvatar;
    private ChatAdpterListener mChatAdpterListener;
    private boolean mIsKickOutGroup = false;

    public interface ChatAdpterListener {
        public void onLoadPicEnd(int pos);
    }

    private static class MessageItemWrapper {
        IMChatBean item;
        boolean isVoicePlaying;
        boolean existsDownloadTask;

        @Override
        public String toString() {
            return item.toString();
        }
    }

    public void setChatAdpterListener(ChatAdpterListener chatAdpterListener) {
        this.mChatAdpterListener = chatAdpterListener;
    }

    public void setSingleChatAvatar(String icon) {
        mSingleChatAvatar = icon;
    }

    public void setIsKickOutGroup(boolean kick) {
        this.mIsKickOutGroup = kick;
    }

    public ChatMsgAdapter(ChatActivity activity, ChatSenderHelper.ChatMode mode,
                          LinkedList<IMChatBean> items, long user, ChatSenderHelper senderHelper) {
        if (items != null) {
            LinkedList<MessageItemWrapper> list = new LinkedList<MessageItemWrapper>();
            for (IMChatBean item : items) {
                MessageItemWrapper wrapper = new MessageItemWrapper();
                wrapper.item = item;
                list.add(wrapper);
            }
            mWrappers = list;
        }
        mChatMode = mode;
        mCurrUser = user;
        mDetector = new VoiceDetector();
        mChatActivity = activity;
        mDensity = activity.getResources().getDisplayMetrics().density;
        mHandler = new Handler(Looper.getMainLooper());
        mFetcher = ImageFetcher.getInstance(activity);
        mFetcher.setImageCache(ImageCache.getInstance(activity));
        mImageSentDimen = activity.getResources().getDimensionPixelSize(
                R.dimen.chat_send_img_dimen);
        mSender = senderHelper;
        initWidth();
        if (Logger.DEBUG) {
            Logger.i(TAG, "dimension=" + mImageSentDimen);
        }
    }

    public void setFriendName(String name) {
        mUserName = name;
    }

    public void setShowName(boolean show) {
        this.mShowName = show;
        postNotifyDataSetChanged();
    }

    public void clear() {
        mWrappers.clear();
        postNotifyDataSetChanged();
    }

    public void postNotifyDataSetChanged() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void addItem(IMChatBean item) {
        if (item == null) {
            Logger.w(TAG, "add an null item");
            return;
        }
        MessageItemWrapper wrapper = new MessageItemWrapper();
        wrapper.item = item;
        mWrappers.add(wrapper);
        notifyItemInserted(mWrappers.size() - 1);
    }

    public void addItems(LinkedList<IMChatBean> items) {
        if (items == null) {
            Logger.w(TAG, "add an null list");
            return;
        }
        int size = items.size();
        if (size == 0) {
            Logger.w(TAG, "add an empty list");
            return;
        }
        LinkedList<MessageItemWrapper> cacheList = mWrappers;
        int idx = 0;
        IMChatBean mi = items.poll();
        while (mi != null) {
            MessageItemWrapper wrapper = new MessageItemWrapper();
            wrapper.item = mi;
            cacheList.add(idx, wrapper);
            idx++;
            mi = items.poll();
        }
        notifyItemRangeInserted(0, size);
    }

//    public void notifySendChanged(long msgId, boolean suc) {
//        if (Logger.DEBUG) {
//            Logger.i(TAG, "notifySendChanged, msgId=" + msgId + ", suc=" + suc);
//        }
//        int size = mWrappers.size();
//        for (int pos = size - 1; pos >= 0; pos--) {
//            IMChatBean mi = mWrappers.get(pos).item;
//            if (mi.id == msgId) {
//                mi.sendStatus = suc ? IMChatBean.SendState.STATUS_SEND_SUCCESS.getValue()
//                        : IMChatBean.SendState.STATUS_SEND_FAILED.getValue();
//                notifyItemChanged(pos);
//                break;
//            }
//        }
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.chatting_item, parent, false);
        return new ViewHolder(v);
    }

    private boolean showTime(long date, int position) {
        boolean showTimeTip = false;
        if (position == 0) {
            showTimeTip = true;
        } else {
            long last = mWrappers.get(position - 1).item.time * 1000L;
            if (Math.abs(last - date) > MSG_TIME_SHOW_DELAY) {
                showTimeTip = true;
            }
        }
        return showTimeTip;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MessageItemWrapper wrapper = mWrappers.get(position);
        IMChatBean item = wrapper.item;

        holder.timeTips.setVisibility(View.GONE);
        holder.msgNotifyTips.setVisibility(View.GONE);
        long date = item.time * 1000L;
        if (showTime(date, position)) {
            holder.timeTips.setText(DateFormat.format("MM-dd hh:mma", new Date(
                    date)));
            holder.timeTips.setVisibility(View.VISIBLE);
        }

        final long loadingUid;
        final TextView userTextView;
        final ImageView userImageView;
        boolean isComingMsg = false;
        isComingMsg = item.sender != mMyUid;
        if (isComingMsg) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            loadingUid = item.sender;
            userTextView = holder.leftUsername;
            userImageView = holder.leftAvatar;
            userTextView.setTag(item.id);

            if (isGroup()) {
                final IMUserExtraBean extraBean = IMUserExtraBean.getBean(item.extra);
                if (extraBean != null) {
                    ImageLoaderManager.displayCircleImageByUrl(mChatActivity, userImageView, extraBean.avatar, R.mipmap.ic_msg_default);

                    if (mShowName) {
                        userTextView.setVisibility(View.VISIBLE);
                        userTextView.setText(extraBean.name);
                    } else {
                        userTextView.setVisibility(View.GONE);
                    }
                    userImageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 群聊点进去
                            OtherUserDetailActivity.show(mChatActivity, extraBean.userId, true);
                        }
                    });
                } else {
                    userTextView.setVisibility(View.GONE);
                }

            } else {
                ImageLoaderManager.displayCircleImageByUrl(mChatActivity, userImageView, mSingleChatAvatar, R.mipmap.ic_msg_default);
                userImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 单聊点进去
                        OtherUserDetailActivity.show(mChatActivity, mCurrUser + "", false);
                    }
                });
            }
            if (item.isHello) {
                dealTipMsg(holder, item);
            } else {
                dealMsg(position, wrapper, holder, holder.leftMsgText, holder.leftMsgLayout,
                        holder.leftMsgImage, holder.leftMsgImageMask, null, true);
            }

        } else {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            loadingUid = mCurrUser;
            userImageView = holder.rightAvatar;
            ImageLoaderManager.displayCircleImageByUrl(mChatActivity, userImageView,
                    AccountManager.getInstance().getUser().mIcon, R.mipmap.ic_msg_default);
            if (item.isHello) {
                dealTipMsg(holder, item);
            } else {
                dealMsg(position, wrapper, holder, holder.rightMsgText,
                        holder.rightMsgLayout, holder.rightMsgImage,
                        holder.rightMsgImageMask, holder.rightResend, false);
            }
        }
    }

    private void dealTipMsg(ViewHolder holder, IMChatBean bean) {
        holder.leftLayout.setVisibility(View.GONE);
        holder.rightLayout.setVisibility(View.GONE);
        holder.msgNotifyTips.setVisibility(View.VISIBLE);
        boolean isComingMsg = bean.sender != mMyUid;
        int subType = bean.subType;
        String content = Res.getString(R.string.loading);
        if (!TextUtils.isEmpty(bean.content) && bean.content.startsWith(IDSIMManager.TIP_MSG_START)) {
            dealTipMsgOffline(holder, bean, isComingMsg);
        } else {
            if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()) {
                content = bean.content;
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND_ACCEPTED.getValue()) {
                if (isComingMsg) {
                    content = String.format(Res.getString(R.string.chat_tip_accepted_friend_other), mUserName);
                } else {
                    content = String.format(Res.getString(R.string.chat_tip_accepted_friend_self), mUserName);
                }
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()) {
                content = bean.content;
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()) {
                content = bean.content;
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()) {
                if (isComingMsg) {
                    content = bean.content;
                } else {
                    content = Res.getString(R.string.chat_tip_quit_group);
                }
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()) {
                if (isComingMsg) {
                    content = bean.content;
                } else {
                    content = String.format(Res.getString(R.string.chat_tip_change_group_name), bean.extra);
                }
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()) {
                content = bean.content;
            } else {//nothing
            }
            bean.content = content;
        }
        if (subType == MessageManager.MessageEntity.ChatType.FEED_ADD_FRIEND.getValue()) {
            holder.msgNotifyTips.setVisibility(View.GONE);
            if (isComingMsg) {
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftMsgText.setText(bean.content);
                holder.leftUsername.setVisibility(View.GONE);
                holder.leftVoiceDuration.setVisibility(View.GONE);
                holder.leftVoicePlaying.setVisibility(View.GONE);

            } else {
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.rightMsgText.setText(bean.content);
                holder.rightVoiceDuration.setVisibility(View.GONE);
                holder.rightVoicePlaying.setVisibility(View.GONE);
            }
        } else {
            holder.msgNotifyTips.setText(content);
        }
    }

    private void dealTipMsgOffline(final ViewHolder holder, final IMChatBean bean, boolean isComing) {
        int subType = bean.subType;
        String start = IDSIMManager.TIP_MSG_START;
        String content = bean.content.substring(start.length(), bean.content.length());
        try {
            JSONObject json = new JSONObject(content);
            if (subType == MessageManager.MessageEntity.ChatType.FEED_ENTER_GROUP.getValue()) {
                final long gid = json.getLong("chnid");
                final long time = json.getLong("time");
                final long inviter = json.getLong("inviter");
                final String groupName = json.getString("chnname");
                JSONArray members = json.getJSONArray("members");

                ArrayList list = new ArrayList();
                for (int i = 0; i < members.length(); i++) {
                    JSONObject j = members.getJSONObject(i);
                    final long uid = j.getLong("uid");
                    list.add(uid);
                    long myUid = Long.valueOf(AccountManager.getInstance().getUser().mUid);
                    // 我就是被加入的人
                    if (uid == myUid) {
                        bean.extra = String.valueOf(uid);
                    }
                }
                list.add(inviter);

                // 网络请求数据
                final ArrayList avatarList = new ArrayList();
                final long rowId = bean.id;
                GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(listToString(list), new OnResponseListener<List<UserInfoBean>>() {
                    @Override
                    public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {
                        if (avatarBeans.isEmpty()) {
                            return;
                        }
                        String inviterName = "";
                        ArrayList namesArray = new ArrayList();
                        for (UserInfoBean bean1 : avatarBeans) {
                            avatarList.add(bean1);
                            long uid = Long.valueOf(bean1.getUid());
                            if (inviter == uid) {
                                // 群主
                                inviterName = bean1.getNickname();
                            } else if (uid == mMyUid) {//我

                            } else {//其他人
                                namesArray.add(bean1.getNickname());
                            }
                        }

                        //除我外其他人的名字
                        String s = listToString(namesArray);

                        if (bean.extra == null) {//我是被加入的人
                            if (namesArray.isEmpty()) {//只有自己被加入
                                bean.content = String.format(Res.getString(R.string.chat_tip_invited_by_sb), inviterName);
                            } else {
                                bean.content = String.format(Res.getString(R.string.chat_tip_invited_by_sb_and_others), inviterName, s);
                            }
                        } else {
                            bean.content = String.format(Res.getString(R.string.chat_tip_sb_invite_sb), inviterName, s);
                        }
                        updateMsgContent(bean);
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
                request.sendRequest();

            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_KICK_BY_CRREATOR.getValue()) {
                final long gid = json.getLong("chnid");
                long time = json.getLong("time");
                final long uid = json.getLong("uid");
                final long badUid = json.getLong("badUid");
                ArrayList uidArray = new ArrayList();
                uidArray.add(uid);//群主
                uidArray.add(badUid);//被移除的人
                // 网络请求数据
                final ArrayList avatarList = new ArrayList();
                GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(listToString(uidArray), new OnResponseListener<List<UserInfoBean>>() {
                    @Override
                    public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {
                        if (avatarBeans.isEmpty()) {
                            return;
                        }
                        String creatorName = "";
                        String delName = "";
                        ArrayList namesArray = new ArrayList();
                        for (UserInfoBean user : avatarBeans) {
                            avatarList.add(user);
                            long userId = Long.valueOf(user.getUid());
                            if (uid == userId) {
                                creatorName = user.getNickname();
                            } else {
                                delName = user.getNickname();
                            }
                        }
                        if (badUid == mMyUid) {
                            bean.content = String.format(Res.getString(R.string.chat_tip_kickout_group_by_sb), creatorName);
                        } else {
                            bean.content = String.format(Res.getString(R.string.chat_tip_sb_kickout_sb), creatorName, delName);
                        }
                        updateMsgContent(bean);
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
                request.sendRequest();

            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_QUIT_GROUP.getValue()) {
                long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                IMUserDataHelper.getInstance().findUser(uid, new IMUserDataHelper.IMUserDataCallback() {
                    @Override
                    public void onResult(IMUserBean userBean) {
                        bean.content = String.format(Res.getString(R.string.chat_tip_somebody_quit_group), userBean.userName);
//                        holder.msgNotifyTips.setText(bean.content);
//                        postNotifyDataSetChanged();
                        updateMsgContent(bean);
                    }
                });

            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_MODIFY_CHANNEL_NAME.getValue()) {
                final long gid = json.getLong("chnid");
                long time = json.getLong("time");
                long uid = json.getLong("uid");
                final String name = json.getString("newname");
                IMUserDataHelper.getInstance().findUser(uid, new IMUserDataHelper.IMUserDataCallback() {
                    @Override
                    public void onResult(IMUserBean userBean) {
                        bean.content = String.format(Res.getString(R.string.chat_tip_other_change_group_name), userBean.userName, name);
//                        holder.msgNotifyTips.setText(bean.content);
//                        postNotifyDataSetChanged();
                        updateMsgContent(bean);
                    }
                });
            } else if (subType == MessageManager.MessageEntity.ChatType.FEED_GREEY_GROUP_ACTIVITY.getValue()) {

            } else {//nothing
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateMsgContent(IMChatBean bean) {
        int size = mWrappers.size();
        for (int pos = size - 1; pos >= 0; pos--) {
            IMChatBean mi = mWrappers.get(pos).item;
            if (mi.id == bean.id) {
                notifyItemChanged(pos);
                IMDBService.updateMsgContent(bean, isGroup(), null);
                break;
            }
        }
    }

    private void dealMsg(int pos, final MessageItemWrapper msgWrapper,
                         ViewHolder holder, final ChatTextView text, View msgLayout,
                         final ImageView pic, View picMask, ImageView resend,
                         boolean isComingMsg) {
        final IMChatBean msgItem = msgWrapper.item;
//        byte[] content = msgItem.content.getBytes();
        String content = msgItem.content;
        int msgType = msgItem.msgType;

        if (resend != null) {
            resend.setVisibility(View.GONE);
            resend.setTag(msgItem);
            resend.setOnClickListener(new OnResendClickListener(msgItem, holder));
        }

        msgLayout.setOnClickListener(null);
        text.setText("");
        text.setVisibility(View.GONE);
        text.setBackgroundResource(0);
        holder.voiceViewLengthRight.setVisibility(View.GONE);
        holder.voiceViewLengthLeft.setVisibility(View.GONE);

        TextView duration = isComingMsg ? holder.leftVoiceDuration
                : holder.rightVoiceDuration;
        ImageView voiceView = isComingMsg ? holder.leftVoicePlaying
                : holder.rightVoicePlaying;

        duration.setVisibility(View.GONE);
        voiceView.setVisibility(View.GONE);

        holder.rightSendProgress.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        pic.setVisibility(View.GONE);
        picMask.setVisibility(View.GONE);
        msgLayout.setOnClickListener(null);

        if (!isComingMsg && msgItem.sendStatus != -1) {
            if (msgItem.sendStatus == IMChatBean.SendState.STATUS_SENDING.getValue()) {
                // This message is sending
                holder.rightSendProgress.setVisibility(View.VISIBLE);
                if (MessageManager.MessageEntity.MsgType.IMAGE.getValue() == msgType) {
                    picMask.setVisibility(View.VISIBLE);
                }
            } else if (msgItem.sendStatus == IMChatBean.SendState.STATUS_SEND_FAILED.getValue()) {
                if (resend != null) {
                    resend.setVisibility(View.VISIBLE);
                }
            }
        }

        if (MessageManager.MessageEntity.MsgType.TEXT.getValue() == msgType) {
            text.setVisibility(View.VISIBLE);
            text.resolveText(content);
        } else if (MessageManager.MessageEntity.MsgType.IMAGE.getValue() == msgType
                && !isComingMsg) {
            String filePath = msgItem.contentFilePath;

            // 下面为非GIF图片的情况
            Bitmap bitmap = mFetcher.getCachedBitmap(filePath);
            if (bitmap != null) {
                pic.setVisibility(View.VISIBLE);
                pic.setImageBitmap(bitmap);

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);

                onAdpterListener(pos);
                return;
            }
            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.mipmap.chat_img_thumb_no_sdcard);
                return;
            }
            File bmpFile = new File(filePath);
            if (bmpFile.isFile() && bmpFile.exists()) {
                try {
                    bitmap = BitmapHelper.decodeSampledBitmapFromFile(filePath,
                            mImageSentDimen, mImageSentDimen);
                } catch (Throwable t) {
                    if (Logger.DEBUG) {
                        t.printStackTrace();
                    }
                }
                if (bitmap != null) {
                    mFetcher.addBitmapToCache(filePath, bitmap);
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageBitmap(bitmap);

                    msgLayout.setTag(holder);
                    msgLayout.setOnClickListener(this);
                    return;
                }
                // REPLACEME
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.mipmap.chat_img_thumb_no_sdcard);
                return;
            }

            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.mipmap.chat_img_thumb_deleted);

        } else if (MessageManager.MessageEntity.MsgType.IMAGE.getValue() == msgType
                && isComingMsg) {
            dealWithImageMsgLeft(pos, msgWrapper, msgItem, pic, msgLayout, holder);
        } else if (MessageManager.MessageEntity.MsgType.VOICE.getValue() == msgType
                && !isComingMsg) {
            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                voiceView.setVisibility(View.VISIBLE);
                voiceView.setImageResource(R.mipmap.chat_voice_no_sdcard);
                return;
            }
            holder.voiceViewLengthRight.setVisibility(View.VISIBLE);
            voiceView.setVisibility(View.VISIBLE);
            if (msgWrapper.isVoicePlaying) {
                int playingRes = isComingMsg ? R.drawable.chat_left_voice_playing_indicator
                        : R.drawable.chat_right_voice_playing_indicator;
                AnimationDrawable d = (AnimationDrawable) Res
                        .getDrawable(playingRes);
                voiceView.setImageDrawable(d);
                d.start();
            } else {
                voiceView
                        .setImageResource(isComingMsg ? R.mipmap.ic_msg_left_speech3
                                : R.mipmap.ic_msg_speech_right_sound3);
            }
            duration.setVisibility(View.VISIBLE);
//            duration.setText(String.format("%d\"",
//                    mDetector.detectDuration(msgItem.contentFilePath)));
            duration.setText(msgItem.duration + "''");
            setVoiceMsgWidth(holder.voiceViewLengthRight, msgItem);

            msgLayout.setTag(holder);
            msgLayout.setOnClickListener(this);
        } else if (MessageManager.MessageEntity.MsgType.VOICE.getValue() == msgType
                && isComingMsg) {
            dealWithVoiceMsgLeft(msgWrapper, msgItem, voiceView, duration, msgLayout, holder, isComingMsg);
        }
    }

    private void onAdpterListener(int pos) {
        if (mChatAdpterListener != null) {
            mChatAdpterListener.onLoadPicEnd(pos);
        }
    }

    private String getImageThumbPath(String filePath) {
        String tmpThumbPath = null;
        String file_id = null;
        try {
            JSONObject jsonObject = new JSONObject(filePath);
            file_id = jsonObject.optString("file_id");
            tmpThumbPath = IDSIMManager.getInstance().getImagePath() + "/"
                    + URLEncoder.encode(file_id, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmpThumbPath;
    }

    private void dealWithImageMsgLeft(int pos, final MessageItemWrapper msgWrapper, IMChatBean msgItem,
                                      ImageView pic, View msgLayout, ViewHolder holder) {
        String tmpThumbPath = null;
        String file_id = null;
        try {
            JSONObject jsonObject = new JSONObject(msgItem.contentFilePath);
            file_id = jsonObject.optString("file_id");
            tmpThumbPath = IDSIMManager.getInstance().getImagePath() + "/"
                    + URLEncoder.encode(file_id, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String thumbPath = tmpThumbPath;

        int downloadStatus = msgWrapper.item.downloadStatus;
        if (downloadStatus == IMChatBean.DownloadState.DOWNLOAD_DONE.getValue()) {

            // 非GIF图片的情况
            Bitmap bitmap = mFetcher.getCachedBitmap(thumbPath);
            if (bitmap != null) {
                pic.setVisibility(View.VISIBLE);
                pic.setImageBitmap(bitmap);

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);

                onAdpterListener(pos);
                return;
            }
            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.mipmap.chat_img_thumb_no_sdcard);
                return;
            }

            File thumbFile = new File(thumbPath);
            if (thumbFile.exists()) {
                // The thumb file exists in SD card
                bitmap = BitmapFactory.decodeFile(thumbPath);
                if (bitmap != null) {
                    mFetcher.addBitmapToCache(thumbPath, bitmap);
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageBitmap(bitmap);

                    msgLayout.setTag(holder);
                    msgLayout.setOnClickListener(this);
                    onAdpterListener(pos);
                    return;
                }
                // REPLACEME
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.mipmap.chat_img_thumb_no_sdcard);
                return;
            }

            // The done file exists means that the file was
            // downloaded
            // before, but right now was not found in SD Card
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.mipmap.chat_img_thumb_deleted);
            return;
        }

        if (downloadStatus == IMChatBean.DownloadState.DOWNLOAD_OUT_OF_DATE.getValue()) {
            // The file was removed from server
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.mipmap.chat_img_thumb_deleted);
            return;
        }

        if (downloadStatus == IMChatBean.DownloadState.DOWNLOAD_FAILED.getValue()) {
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.mipmap.chat_img_thumb_download_error);

            msgLayout.setTag(holder);
            msgLayout.setOnClickListener(this);
            return;
        }

        if (downloadStatus == IMChatBean.DownloadState.DOWNLOADING.getValue()
                && msgWrapper.existsDownloadTask) {
            // Probably there is a downloading process right now
            // REPLACEME
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.mipmap.chat_img_thumb_default);
            return;
        }

        // Show default image resource which means downloading
        pic.setVisibility(View.VISIBLE);
        pic.setImageResource(R.mipmap.chat_img_thumb_default);

        // Launch a request to fetch thumb image file.
        msgWrapper.existsDownloadTask = true;
        updateDownloadStatus(msgWrapper, IMChatBean.DownloadState.DOWNLOADING.getValue());

        MessageManager.FileDownloadListener listener = new MessageManager.FileDownloadListener() {

            @Override
            public void onProgressChanged(int progress) {
                super.onProgressChanged(progress);
            }

            @Override
            public void onDownloadFinished(int code, byte[] content) {
                if (code == MessageManager.FileDownloadListener.CODE_FAILED) {
                    updateDownloadStatus(msgWrapper,
                            IMChatBean.DownloadState.DOWNLOAD_FAILED.getValue());
                    postNotifyDataSetChanged();
                } else if (code == MessageManager.FileDownloadListener.CODE_FILE_DELETED_FROM_SERVER) {
                    updateDownloadStatus(msgWrapper,
                            IMChatBean.DownloadState.DOWNLOAD_OUT_OF_DATE.getValue());
                    postNotifyDataSetChanged();
                } else {
                    // Success
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    Bitmap bmp = BitmapFactory.decodeByteArray(content, 0,
                            content.length, opt);
                    if (opt.outWidth <= 0 || opt.outHeight <= 0) {
                        // The data can not be decoded into a bitmap which
                        // should never happen
                        updateDownloadStatus(msgWrapper,
                                IMChatBean.DownloadState.DOWNLOAD_FAILED.getValue());
                        postNotifyDataSetChanged();
                        return;
                    }
                    // Whenever the data can be decoded into a bitmap,
                    // we write the data to the target thumb file
                    updateDownloadStatus(msgWrapper, IMChatBean.DownloadState.DOWNLOAD_DONE.getValue());
                    Utils.writeFile(new File(thumbPath), content);

                    opt.inJustDecodeBounds = false;
                    // Try again the decode the bitmap
                    bmp = BitmapFactory.decodeByteArray(content, 0,
                            content.length, opt);
                    if (bmp != null) {
                        mFetcher.addBitmapToCache(thumbPath, bmp);
                    }
                    updateDownloadStatus(msgWrapper, IMChatBean.DownloadState.DOWNLOAD_DONE.getValue());
                    postNotifyDataSetChanged();
                }
            }
        };
        MessageManager.getDefault(mChatActivity).downloadFile(
                file_id, MessageManager.FILE_TYPE_IMAGE,
                listener);
    }

    private void setVoiceMsgWidth(View voiceLength, IMChatBean msgItem) {
        ViewGroup.LayoutParams lp = voiceLength.getLayoutParams();
        lp.width = getDurationSize(msgItem.duration > 60 ? 60 : msgItem.duration);
    }

    private void dealWithVoiceMsgLeft(final MessageItemWrapper msgWrapper, IMChatBean msgItem,
                                      ImageView voiceView, TextView duration, View msgLayout,
                                      ViewHolder holder, boolean isComingMsg) {
        View voiceLength = null;
        if (isComingMsg) {
            voiceLength = holder.voiceViewLengthLeft;
        } else {
            voiceLength = holder.voiceViewLengthRight;
        }
        holder.voiceViewLengthLeft.setVisibility(View.VISIBLE);
        String tmpFilePath = null;
        String file_id = null;
        try {
            JSONObject jsonObject = new JSONObject(msgItem.contentFilePath);
            file_id = jsonObject.optString("file_id");
            tmpFilePath = IDSIMManager.getInstance().getVoicePath() + "/"
                    + file_id;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String filePath = tmpFilePath;
        int downloadStatus = msgWrapper.item.downloadStatus;
        if (downloadStatus == IMChatBean.DownloadState.DOWNLOAD_DONE.getValue()) {
            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                voiceView.setVisibility(View.VISIBLE);
                voiceView.setImageResource(R.mipmap.chat_voice_no_sdcard);
                return;
            }

            final File audioFile = new File(filePath);
            if (audioFile.exists()) {
                // The audio file exists in SD card
                voiceView.setVisibility(View.VISIBLE);
                if (msgWrapper.isVoicePlaying) {
                    int playingRes = isComingMsg ? R.drawable.chat_left_voice_playing_indicator
                            : R.drawable.chat_right_voice_playing_indicator;
                    AnimationDrawable d = (AnimationDrawable) Res
                            .getDrawable(playingRes);
                    voiceView.setImageDrawable(d);
                    d.start();
                } else {
                    voiceView
                            .setImageResource(isComingMsg ? R.mipmap.ic_msg_left_speech3
                                    : R.mipmap.ic_msg_speech_right_sound3);
                }

                duration.setVisibility(View.VISIBLE);
                duration.setText(msgItem.duration > 60 ? 60 + "''" : msgItem.duration + "''");
//                ViewGroup.LayoutParams lp = voiceLength.getLayoutParams();
//                lp.width = getDurationSize(msgItem.duration);
                setVoiceMsgWidth(voiceLength, msgItem);

                msgWrapper.item.downloadStatus = IMChatBean.DownloadState.DOWNLOAD_DONE.getValue();

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);
                return;
            }

            // The file was downloaded before, but right now was not found
            // in SD Card
            voiceView.setVisibility(View.VISIBLE);
            voiceView.setImageResource(R.mipmap.chat_voice_deleted);
            return;
        }
        if (downloadStatus == IMChatBean.DownloadState.DOWNLOAD_OUT_OF_DATE.getValue()) {
            // The file was removed from server
            // REPLACEME
            voiceView.setVisibility(View.VISIBLE);
            voiceView.setImageResource(R.mipmap.chat_voice_deleted);
            return;
        }

        if (downloadStatus == IMChatBean.DownloadState.DOWNLOAD_FAILED.getValue()) {
            voiceView.setVisibility(View.VISIBLE);
            voiceView
                    .setImageResource(R.mipmap.chat_voice_download_error);

            msgLayout.setTag(holder);
            msgLayout.setOnClickListener(this);
            return;
        }

        if (downloadStatus == IMChatBean.DownloadState.DOWNLOADING.getValue()
                && msgWrapper.existsDownloadTask) {
            return;
        }

        // Show default image resource which means downloading
        voiceView.setVisibility(View.VISIBLE);
        voiceView.setImageResource(R.mipmap.chat_voice_default);

        // Launch a request to fetch thumb image file.
        msgWrapper.existsDownloadTask = true;
        updateDownloadStatus(msgWrapper, IMChatBean.DownloadState.DOWNLOADING.getValue());

        MessageManager.FileDownloadListener listener = new MessageManager.FileDownloadListener() {

            @Override
            public void onDownloadFinished(int code, byte[] content) {
                if (code == MessageManager.FileDownloadListener.CODE_FAILED) {
                    updateDownloadStatus(msgWrapper,
                            IMChatBean.DownloadState.DOWNLOAD_FAILED.getValue());
                    postNotifyDataSetChanged();
                } else if (code == MessageManager.FileDownloadListener.CODE_FILE_DELETED_FROM_SERVER) {
                    updateDownloadStatus(msgWrapper,
                            IMChatBean.DownloadState.DOWNLOAD_OUT_OF_DATE.getValue());
                    postNotifyDataSetChanged();
                } else {
                    // Success
                    if (Utils.isSdcardWritable(mChatActivity)) {
                        Utils.writeFile(new File(filePath), content);
                    }
                    long duration = mDetector.detectDuration(filePath);
                    if (duration > 0L) {
                        msgWrapper.item.duration = duration;
                        updateDuration(msgWrapper);
                    }
                    updateDownloadStatus(msgWrapper, IMChatBean.DownloadState.DOWNLOAD_DONE.getValue());
                    postNotifyDataSetChanged();
                }
            }
        };
        MessageManager.getDefault(mChatActivity).downloadFile(
                file_id, MessageManager.FILE_TYPE_VOICE,
                listener);
    }

    private void updateDuration(MessageItemWrapper msgWrapper) {
        IMDBService.updateMsgDuration(msgWrapper.item, false, null);
    }

    private void updateDownloadStatus(MessageItemWrapper wrapper, int downloadStatus) {
        wrapper.item.downloadStatus = downloadStatus;
        IMDBService.updateMsgDownloadState(wrapper.item, false, null);
    }

    private int getDurationSize(long duration) {
        int size = (int) (mMinItemWidth + (mMaxItemWidth / 60f) * duration) / 2;
        return size;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }
        ViewHolder holder = (ViewHolder) tag;
        int pos = holder.getPosition();
        MessageItemWrapper wrapper = mWrappers.get(pos);
        IMChatBean item = wrapper.item;
        if (item.msgType == MessageManager.MessageEntity.MsgType.VOICE.getValue()
                ) {
            playVoice(holder);
        } else if (item.msgType == MessageManager.MessageEntity.MsgType.IMAGE.getValue()
                ) {
            // showImage(holder);
            showMultimediaImage(holder);
        }
    }

    public void close() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
            }
        }
    }

    private MessageItemWrapper mLastVoiceWrapper;

    private void playVoice(ViewHolder h) {
        int pos = h.getPosition();
        final MessageItemWrapper wrapper = mWrappers.get(pos);
        if (Logger.DEBUG) {
            Logger.d(TAG, "position=" + pos);
        }

        String voiceSource = null;
        File f = null;
        boolean isComingMsg = wrapper.item.sender != mMyUid;

        if (isComingMsg) {
            try {
                JSONObject jsonObject = new JSONObject(wrapper.item.contentFilePath);
                String file_id = jsonObject.getString("file_id");
                f = new File(IDSIMManager.getInstance().getVoicePath(), file_id);
                voiceSource = f.getPath();
            } catch (Exception e) {
            }
        } else {
            voiceSource = wrapper.item.contentFilePath;
        }
        if (Logger.DEBUG) {
            Logger.d(TAG, "play voice file " + voiceSource);
        }

        // cache it
        MediaPlayer p = mMediaPlayer;
        if (p == null) {
            p = new MediaPlayer();
            mMediaPlayer = p;
        }
        try {
            if (mLastVoiceWrapper != null) {
                try {
                    p.stop();
                } catch (Exception e) {
                }
                mLastVoiceWrapper.isVoicePlaying = false;
            }
            p.reset();
            VoiceCallback cb = new VoiceCallback(wrapper);
            p.setOnErrorListener(cb);
            p.setOnCompletionListener(cb);
            p.setDataSource(voiceSource);
            p.setOnInfoListener(cb);
            p.prepare();
            p.start();
            mLastVoiceWrapper = wrapper;
            wrapper.isVoicePlaying = true;
            notifyItemChanged(pos);
        } catch (Exception e) {
            e.printStackTrace();
//            if (Logger.DEBUG) {
//                Logger.w(TAG, "error:" + e.getMessage());
//            }
            wrapper.isVoicePlaying = false;
            notifyItemChanged(pos);
        }
    }

    private class VoiceCallback implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {

        private MessageItemWrapper mWrapper;

        public VoiceCallback(MessageItemWrapper wrapper) {
            mWrapper = wrapper;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (Logger.DEBUG) {
                Logger.d(TAG, "onCompletion");
            }
            stop();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (Logger.DEBUG) {
                Logger.d(TAG, "onError, what=" + what + ", extra=" + extra);
            }
            stop();
            return true;
        }

        private void stop() {
            mWrapper.isVoicePlaying = false;
            mChatActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (Logger.DEBUG) {
                Logger.d(TAG, "onInfo, what=" + what + ", extra=" + extra);
            }
            if (what != 973) {//针对三星 不造973是什么来着
                stop();
            }
            return false;
        }

    }

    private void showMultimediaImage(ViewHolder h) {
        int pos = h.getPosition();
        MessageItemWrapper wrapper = mWrappers.get(pos);
        if (wrapper.item.sender == mMyUid) {// self
//            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(
//                        Uri.fromFile(new File(wrapper.item.contentFilePath)),
//                        "image/*");
//                mChatActivity.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//            }
//            Intent intent = new Intent(mChatActivity, ImgPreviewActivity.class);
//            intent.putExtra(ImgPreviewActivity.FILE_PATH, wrapper.item.contentFilePath);
//            mChatActivity.startActivity(intent);
//            mChatActivity.overridePendingTransition(R.anim.scale_out, 0);
            ImgPreviewActivity.show(mChatActivity, wrapper.item.contentFilePath);

        } else {
//            int[] location = new int[2];
//            h.leftMsgImage.getLocationOnScreen(location);
//            Intent intent = new Intent(mChatActivity, ImgPreviewActivity.class);
//            intent.putExtra(ImgPreviewActivity.FILE_PATH, new File(
//                    getImageThumbPath(wrapper.item.contentFilePath)));
//            intent.putExtra(ImgPreviewActivity.FILE_PATH, getImageThumbPath(wrapper.item.contentFilePath));
//            mChatActivity.startActivity(intent);
//            mChatActivity.overridePendingTransition(R.anim.scale_out, 0);
            ImgPreviewActivity.show(mChatActivity, getImageThumbPath(wrapper.item.contentFilePath));
        }
    }


    @Override
    public int getItemCount() {
        return mWrappers.size();
    }

    public IMChatBean getFirstsBean() {
        return mWrappers.getFirst().item;
    }

    public void updateSendState(long msgId, boolean suc) {
        int size = mWrappers.size();
        for (int pos = size - 1; pos >= 0; pos--) {
            IMChatBean bean = mWrappers.get(pos).item;
            if (bean.msgId == msgId) {
                bean.sendStatus = suc ? IMChatBean.SendState.STATUS_SEND_SUCCESS.getValue()
                        : IMChatBean.SendState.STATUS_SEND_FAILED.getValue();
                notifyItemChanged(pos);
                break;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mDetector.finalize();
    }


    private class OnResendClickListener implements OnClickListener {

        private IMChatBean mBean;
        private ViewHolder mHolder;

        public OnResendClickListener(IMChatBean bean, ViewHolder viewHolder) {
            this.mBean = bean;
            this.mHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            if (isGroup() &&mIsKickOutGroup){

            }
            reSendMsg(mBean);
        }
    }

    private void reSendMsg(IMChatBean bean) {
        mSender.reSendMsg(bean);
        long id = bean.id;
        int size = mWrappers.size();
        for (int pos = size - 1; pos >= 0; pos--) {
            IMChatBean mi = mWrappers.get(pos).item;
            if (mi.id == bean.id) {
                mi.sendStatus = IMChatBean.SendState.STATUS_SENDING.getValue();
                notifyItemChanged(pos);
                break;
            }
        }
    }

    private String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    private void initWidth() {
        //获取屏幕的宽度
        int widthPixels = DisplayUtil.getScreenWidth(mChatActivity);
        mMaxItemWidth = (int) (widthPixels * 0.7f);
        mMinItemWidth = (int) (widthPixels * 0.15f);
    }

    private boolean isGroup() {
        return mChatMode != ChatSenderHelper.ChatMode.MODE_PRIVATE;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView msgNotifyTips;
        TextView timeTips;

        View leftLayout;
        TextView leftUsername;
        ChatTextView leftMsgText;
        View leftMsgLayout;
        View leftMsgImageMask;
        ImageView leftMsgImage;
        ImageView leftAvatar;
        TextView leftVoiceDuration;
        ImageView leftVoicePlaying;
//        SurfaceView leftVideoView;

        View rightLayout;
        ImageView rightAvatar;
        ImageView rightResend;
        //        TextView rightUsername;
        ChatTextView rightMsgText;
        View rightMsgLayout;
        View rightMsgImageMask;
        ImageView rightMsgImage;
        ProgressBar rightSendProgress;
        TextView rightVoiceDuration;
        ImageView rightVoicePlaying;
//        SurfaceView rightVideoView;

        View voiceViewLengthRight;
        View voiceViewLengthLeft;

        public ViewHolder(View v) {
            super(v);
            timeTips = (TextView) v.findViewById(R.id.chat_top_tips);
            View lv = v.findViewById(R.id.chat_left_layout);
            leftLayout = lv;
            leftAvatar = (ImageView) lv.findViewById(R.id.chat_left_avatar);
            leftUsername = (TextView) lv.findViewById(R.id.chat_left_username);

            View leftLayout = lv.findViewById(R.id.chat_left_content_layout);
            leftMsgLayout = leftLayout;
            leftMsgText = (ChatTextView) leftLayout
                    .findViewById(R.id.chat_left_msg_text);
            leftMsgImage = (ImageView) leftLayout
                    .findViewById(R.id.chat_left_msg_pic);
            leftMsgImageMask = leftLayout
                    .findViewById(R.id.chat_left_msg_pic_mask);
            leftVoiceDuration = (TextView) leftLayout
                    .findViewById(R.id.chat_left_voice_duration);
            leftVoicePlaying = (ImageView) leftLayout
                    .findViewById(R.id.chat_left_voice_playing);
//            leftVideoView = (SurfaceView) leftLayout
//                    .findViewById(R.id.chat_left_video_view);

            voiceViewLengthLeft = leftLayout.findViewById(R.id.voice_length_left);

            View rv = v.findViewById(R.id.chat_right_layout);
            rightLayout = rv;
            rightAvatar = (ImageView) rv.findViewById(R.id.chat_right_avatar);
//            rightUsername = (TextView) rv
//                    .findViewById(R.id.chat_right_username);
            rightResend = (ImageView) rv.findViewById(R.id.chat_right_resend);
            rightSendProgress = (ProgressBar) v
                    .findViewById(R.id.chat_right_send_progress);

            View content = rv.findViewById(R.id.chat_right_content_layout);

            rightMsgLayout = content;
            rightMsgText = (ChatTextView) content
                    .findViewById(R.id.chat_right_msg_text);
            rightMsgImage = (ImageView) content
                    .findViewById(R.id.chat_right_msg_pic);
            rightMsgImageMask = content
                    .findViewById(R.id.chat_right_msg_pic_mask);
            rightVoiceDuration = (TextView) content
                    .findViewById(R.id.chat_right_voice_duration);

            rightVoicePlaying = (ImageView) content
                    .findViewById(R.id.chat_right_voice_playing);
            voiceViewLengthRight = content.findViewById(R.id.voice_length_right);

//            rightVideoView = (SurfaceView) content
//                    .findViewById(R.id.chat_right_video_view);

            msgNotifyTips = (TextView) v.findViewById(R.id.tv_top_tips_notify);
        }
    }
}
