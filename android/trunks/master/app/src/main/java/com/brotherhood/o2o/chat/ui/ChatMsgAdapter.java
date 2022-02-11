package com.brotherhood.o2o.chat.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.chat.common.cache.ImageCache;
import com.brotherhood.o2o.chat.common.cache.ImageFetcher;
import com.brotherhood.o2o.chat.db.DatabaseHandler;
import com.brotherhood.o2o.chat.utils.BitmapHelper;
import com.brotherhood.o2o.chat.utils.ChatAPI;
import com.brotherhood.o2o.chat.utils.ChatManager;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.utils.ChatDbHelper.MessageItem;
import com.brotherhood.o2o.chat.utils.ChatUserLoaderInterface;
import com.brotherhood.o2o.chat.utils.ChatUserLoadingListener;
import com.brotherhood.o2o.chat.utils.Utils;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skynet.library.message.Logger;
import com.skynet.library.message.MessageManager;
import com.skynet.library.message.MessageManager.FileDownloadListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ChatMsgAdapter extends
        RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> implements
        OnClickListener {

    private static final String TAG = "ChatMsgAdapter";

    private final static String FRAGMENT_CHATTING = "f_chatting";

    private static final long MSG_TIME_SHOW_DELAY = 5 * 60 * 1000;

    private static final String ACTION_TICK_COUNT = "bomb_fuse_burning_tick";

    private LinkedList<MessageItemWrapper> mWrappers;

    private ChatActivity mChatActivity;
    private ChatFragment mChatFragment;
    private Handler mHandler;
    private DatabaseHandler mDbHandler;
    private long mCurrUser;

    private MediaPlayer mMediaPlayer;
    private MediaPlayer mDurationDetector;

    private ChatManager mChatManager;

    private ImageFetcher mFetcher;

    private int mImageSentDimen;

    private float mDensity = 1.0f;

    private String mAvatarUrl;

    private String mNickName;

    private final class VolatileMsgClickListener implements OnClickListener {
        private final MessageItem msgItem;
        private boolean isCountingDown = false;
        private int mCurrentCount;
        private LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager
                .getInstance(mChatActivity);

        private BroadcastReceiver mStartCountReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                long rcvMsgId = intent.getLongExtra("msg_id", 0);

                if (rcvMsgId != msgItem.id || isCountingDown) {
                    return;
                }

                startCountDown();
                isCountingDown = true;

                mLocalBroadcastManager.unregisterReceiver(this);
            }

            private void startCountDown() {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {

                        Intent broadcastIntent = new Intent(ACTION_TICK_COUNT);

                        broadcastIntent.putExtra("msg_id", msgItem.id);
                        broadcastIntent
                                .putExtra("current_count", mCurrentCount);
                        mLocalBroadcastManager.sendBroadcast(broadcastIntent);

                        if (mCurrentCount > 0) {
                            mHandler.postDelayed(this, 1000);
                        }
                        mCurrentCount--;
                    }
                };

                mHandler.post(runnable);
            }
        };

        private BroadcastReceiver mTickCountReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int currentCount = intent.getIntExtra("current_count", 0);

                long rcvMsgId = intent.getLongExtra("msg_id", 0);

                if (rcvMsgId != msgItem.id) {
                    return;
                }

                msgItem.volAliveSecs = currentCount;
                notifyCountDownChanged(msgItem.id);

                if (currentCount == 0) {
                    mLocalBroadcastManager
                            .unregisterReceiver(mTickCountReceiver);
                }
            }
        };

        private VolatileMsgClickListener(final MessageItem msgItem) {
            this.msgItem = msgItem;
        }

        @Override
        public void onClick(final View v) {
            final MessageItem mi = msgItem;

            if (!isCountingDown) {

                ContentValues values = new ContentValues();
                values.put(MessageItem.COL_VOL_VIEWED, 1);
                values.put(MessageItem.COL_VOL_ALIVE_SECS, 0);
                mDbHandler.update(MessageItem.TABLE, values, MessageItem.COL_ID
                        + " = ?", new String[]{String.valueOf(msgItem.id)});

                mi.volViewed = 1;
                mCurrentCount = mi.options.durationBeforeDeleted;

                IntentFilter intentFilter = new IntentFilter(
                        VolatileMsgViewer.ACTION_START_TICK_COUNT);
                mLocalBroadcastManager.registerReceiver(mStartCountReceiver,
                        intentFilter);

                IntentFilter intentFilter2 = new IntentFilter(ACTION_TICK_COUNT);
                mLocalBroadcastManager.registerReceiver(mTickCountReceiver,
                        intentFilter2);
            }

            Intent intent = new Intent(mChatActivity, VolatileMsgViewer.class);
            intent.putExtra(VolatileMsgViewer.EXTRAS_MSG_ID, mi.id);
            mChatActivity.startActivity(intent);
        }
    }

    static class MessageItemWrapper {
        MessageItem item;
        boolean isVoicePlaying;
        boolean existsDownloadTask;
        VolatileMsgClickListener listener;

        @Override
        public String toString() {
            return item.toString();
        }
    }

    public ChatMsgAdapter(ChatActivity activity, LinkedList<MessageItem> items,
                          long user) {
        if (items != null) {
            LinkedList<MessageItemWrapper> list = new LinkedList<MessageItemWrapper>();
            for (MessageItem item : items) {
                MessageItemWrapper wrapper = new MessageItemWrapper();
                wrapper.item = item;
                list.add(wrapper);
            }
            mWrappers = list;
        }
        mCurrUser = user;
        mChatActivity = activity;
        mChatFragment = (ChatFragment) mChatActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_CHATTING);
        mDensity = activity.getResources().getDisplayMetrics().density;
        mHandler = new Handler(Looper.getMainLooper());
        mDbHandler = ChatManager.getDefault(mChatActivity).getDbHandler();
        mChatManager = ChatManager.getDefault(activity);
        mFetcher = ImageFetcher.getInstance(activity);
        mFetcher.setImageCache(ImageCache.getInstance(activity));
        mImageSentDimen = activity.getResources().getDimensionPixelSize(
                R.dimen.chat_send_img_dimen);
        if (Logger.DEBUG) {
            Logger.i(TAG, "dimension=" + mImageSentDimen);
        }
    }

    public void postNotifyDataSetChanged() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void addItem(MessageItem item) {
        if (item == null) {
            Logger.w(TAG, "add an null item");
            return;
        }
        MessageItemWrapper wrapper = new MessageItemWrapper();
        wrapper.item = item;
        mWrappers.add(wrapper);
        notifyItemInserted(mWrappers.size() - 1);
    }

    public void addItems(LinkedList<MessageItem> items) {
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
        MessageItem mi = items.poll();
        while (mi != null) {
            MessageItemWrapper wrapper = new MessageItemWrapper();
            wrapper.item = mi;
            cacheList.add(idx, wrapper);
            idx++;
            mi = items.poll();
        }
        notifyItemRangeInserted(0, size);
    }

    public void notifySendChanged(long msgId, boolean suc) {
        if (Logger.DEBUG) {
            Logger.i(TAG, "notifySendChanged, msgId=" + msgId + ", suc=" + suc);
        }
        int size = mWrappers.size();
        for (int pos = size - 1; pos >= 0; pos--) {
            MessageItem mi = mWrappers.get(pos).item;
            if (mi.id == msgId) {
                mi.sendStatus = suc ? MessageItem.STATUS_SEND_SUCCEEDED
                        : MessageItem.STATUS_SEND_FAILED;
                notifyItemChanged(pos);
                break;
            }
        }
    }

    public void notifyCountDownChanged(long msgId) {
        if (Logger.DEBUG) {
            Logger.i(TAG, "notifyCountDownChanged, msgId=" + msgId);
        }

        int size = mWrappers.size();

        for (int pos = size - 1; pos >= 0; pos--) {
            MessageItem mi = mWrappers.get(pos).item;

            if (mi.id == msgId) {
                notifyItemChanged(pos);
                break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.chatting_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageItemWrapper wrapper = mWrappers.get(position);
        MessageItem item = wrapper.item;

        holder.msgTips.setVisibility(View.GONE);
        boolean shouldShowTips = false;
        long date = item.updateTime * 1000L;
        if (position == 0) {
            shouldShowTips = true;
        } else {
            long last = mWrappers.get(position - 1).item.updateTime * 1000L;
            if (Math.abs(last - date) > MSG_TIME_SHOW_DELAY) {
                shouldShowTips = true;
            }
        }
        if (shouldShowTips) {
            holder.msgTips.setText(DateFormat.format("MM-dd hh:mma", new Date(
                    date)));
            holder.msgTips.setVisibility(View.VISIBLE);
        }

        final long loadingUid;
        final TextView userTextView;
        final SimpleDraweeView userImageView;
        // 如果消息是对方发给我的
        boolean isComingMsg = item.sender != mCurrUser;
        if (isComingMsg) {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);

            loadingUid = item.sender;
            userTextView = holder.leftUsername;
            userImageView = holder.leftAvatar;
            userTextView.setText(mNickName);
            if (!TextUtils.isEmpty(mAvatarUrl)) {
                ImageLoader.getInstance().setImageUrl(userImageView,mAvatarUrl,1,null, com.brotherhood.o2o.utils.Utils.dip2px(72), com.brotherhood.o2o.utils.Utils.dip2px(72));
            }
            dealMsg(wrapper, holder, holder.leftMsgText, holder.leftMsgLayout,
                    holder.leftMsgImage, holder.leftMsgImageMask, null, true);
        } else {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);

            loadingUid = mCurrUser;
            userTextView = holder.rightUsername;
            userImageView = holder.rightAvatar;
            if(AccountComponent.shareComponent().getmUserInfo()!=null)
            {
                userTextView.setText(AccountComponent.shareComponent().getmUserInfo().mNickName);
//                if (AccountComponent.shareComponent().getmUserInfo().mAvatarPath == null) {
                ImageLoader.getInstance().setImageUrl(userImageView,AccountComponent.shareComponent().getmUserInfo().mAvatarURL,1,null, com.brotherhood.o2o.utils.Utils.dip2px(72), com.brotherhood.o2o.utils.Utils.dip2px(72));
//                } else {
//                    ImageLoader.getInstance().setImageLocal(userImageView, AccountComponent.shareComponent().getmUserInfo().mAvatarPath);
//                }
            }
            dealMsg(wrapper, holder, holder.rightMsgText,
                    holder.rightMsgLayout, holder.rightMsgImage,
                    holder.rightMsgImageMask, holder.rightResend, false);
        }
//        if (item.sessionType == MessageManager.MessageEntity.ChatType.ANONYMOUS_INBOUND
//                .getValue()) {
//            userTextView.setText(isComingMsg ? mChatActivity
//                    .getString(R.string.anonymous) : String
//                    .valueOf(item.sender));
//        } else if (item.sessionType == MessageManager.MessageEntity.ChatType.ANONYMOUS_OUTBOUND
//                .getValue()) {
//            userTextView.setText(isComingMsg ? String.valueOf(item.sender)
//                    : mChatActivity.getString(R.string.anonymous));
//        } else {
//            userTextView.setText(String.valueOf(item.sender));
//        }

        userTextView.setTag(item.id);
        userImageView.setTag(item.id);
        userImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ChatAPI.ChatSettings settings = mChatManager.getSettings();
                Class<? extends Activity> glass = settings
                        .getUserDetailActivityClass();
                if (glass != null) {
                    Intent intent = new Intent(mChatActivity, glass);
                    try {
                        intent.putExtra("uid", loadingUid);
                        mChatActivity.startActivity(intent);
                    } catch (Exception e) {
                    }
                }
            }
        });
        ChatUserLoaderInterface userLoader = mChatManager.getUserLoader();
        if (userLoader != null) {
            userLoader.loadUser(loadingUid, new UserLoaderImpl(userTextView,
                    userImageView));
        }

    }

    private class UserLoaderImpl implements ChatUserLoadingListener {

        private long mId;

        private TextView mOriginalNameView;
        private ImageView mOriginalAvatarView;

        public UserLoaderImpl(TextView nameView, ImageView avatarView) {
            mOriginalNameView = nameView;
            mOriginalAvatarView = avatarView;
            mId = (Long) nameView.getTag();
        }

        @Override
        public void onLoadComplete(boolean suc, long uid, String name,
                                   String avatarUrl) {
            if (Logger.DEBUG) {
                Logger.d(TAG, "USER onLoadComplete, suc=" + suc);
            }
            if (suc) {
                long newId = (Long) mOriginalNameView.getTag();
                if (Logger.DEBUG) {
                    Logger.d(TAG, "original id=" + mId + ", new id=" + newId);
                }
                if (newId == mId) {
                    mOriginalNameView.setText(name);
                }

                mFetcher.loadImage(avatarUrl, mOriginalAvatarView,
                        new ImageFetcher.ImageLoadListener() {

                            @Override
                            public void onLoadComplete(boolean suc) {
                                if (suc) {
                                    notifyDataSetChanged();
                                }
                            }
                        });
            }
        }

    }

    private void updateDownloadStatus(MessageItem item, int downloadStatus) {
        item.downloadStatus = downloadStatus;
        ContentValues values = new ContentValues();
        values.put(MessageItem.COL_DOWNLOAD_STATUS, downloadStatus);
        long rows = mChatManager.getDbHandler().updateAndWait(
                MessageItem.TABLE, values, MessageItem.COL_ID + " = ?",
                new String[]{String.valueOf(item.id)});
        if (Logger.DEBUG) {
            Logger.i(TAG, "update new download status [" + downloadStatus
                    + "] for id " + item.id + ", rows effected = " + rows);
        }
    }

    private void dealMsg(final MessageItemWrapper msgWrapper,
                         ViewHolder holder, final ChatTextView text, View msgLayout,
                         final GifImageView pic, View picMask, ImageView resend,
                         boolean isComingMsg) {
        final MessageItem msgItem = msgWrapper.item;
        Resources r = mChatActivity.getResources();
        byte[] content = msgItem.content;
        int msgType = msgItem.msgType;
        final MessageManager.MessageOptions options = msgItem.options;

        if (resend != null) {
            resend.setVisibility(View.GONE);
        }

        msgLayout.setOnClickListener(null);
        text.setTextColor(Color.BLACK);
        text.setText("");
        text.setBackgroundResource(0);

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
            if (msgItem.sendStatus == MessageItem.STATUS_SENDING) {
                // This message is sending
                holder.rightSendProgress.setVisibility(View.VISIBLE);
                if (MessageManager.MessageEntity.MsgType.IMAGE.getValue() == msgType) {
                    picMask.setVisibility(View.VISIBLE);
                }
            } else if (msgItem.sendStatus == MessageItem.STATUS_SEND_FAILED) {
                if (resend != null) {
                    resend.setVisibility(View.VISIBLE);
                }
            }
        }

        if (options.shouldBeDeletedAfterViewed) {
            // 对于阅后即焚消息
            if (msgItem.volViewed != 0 && msgItem.volAliveSecs == 0) {
                // 已被阅读

                if (msgWrapper.listener != null) {
                    msgWrapper.listener = null;
                    text.setVisibility(View.VISIBLE);
                    text.setText("");
                    text.setBackgroundResource(R.drawable.bomb_boom);
                    AnimationDrawable ad = (AnimationDrawable) text
                            .getBackground();
                    ad.start();
                } else {
                    text.setVisibility(View.VISIBLE);
                    text.setBackgroundResource(R.drawable.bg_smoke_small0_00020);
                }
            } else {
                text.setVisibility(View.VISIBLE);
                text.setTextColor(Color.WHITE);

                text.setText(String.valueOf(msgItem.volAliveSecs));
                text.setGravity(Gravity.CENTER);

                if (msgWrapper.listener == null) {
                    msgWrapper.listener = new VolatileMsgClickListener(msgItem);
                }

                msgLayout.setOnClickListener(msgWrapper.listener);

                if (msgItem.volAliveSecs != msgItem.options.durationBeforeDeleted) {

                    if (text.getBackground() == null) {
                        text.setBackgroundResource(R.drawable.bomb_fuse_burning);
                    }

                    AnimationDrawable ad = (AnimationDrawable) text
                            .getBackground();
                    ad.start();

                } else {
                    text.setBackgroundResource(R.drawable.boom_new_00000);
                }
            }
        } else if (MessageManager.MessageEntity.MsgType.TEXT.getValue() == msgType) {
            text.setVisibility(View.VISIBLE);
            text.resolveText(new String(content != null ? content : new byte[0]));
        } else if (MessageManager.MessageEntity.MsgType.IMAGE.getValue() == msgType
                && !isComingMsg) {
            String filePath = msgItem.contentFilePath;

            // 图片为GIF图片的情况
            try {
                if (Utils.isGIF(filePath)) {
                    GifDrawable gifDrawable = new GifDrawable(filePath);
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageDrawable(gifDrawable);

                    msgLayout.setTag(holder);
                    msgLayout.setOnClickListener(this);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();

                if (!Utils.isSdcardReadable(mChatActivity)) {
                    // SD Card not available
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
                    return;
                } else {
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageResource(R.drawable.chat_img_thumb_deleted);
                    return;
                }
            }

            // 下面为非GIF图片的情况
            Bitmap bitmap = mFetcher.getCachedBitmap(filePath);
            if (bitmap != null) {
                pic.setVisibility(View.VISIBLE);
                pic.setImageBitmap(bitmap);

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);
                return;
            }
            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
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
                pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
                return;
            }

            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.drawable.chat_img_thumb_deleted);

        } else if (MessageManager.MessageEntity.MsgType.IMAGE.getValue() == msgType
                && isComingMsg) {
            String tmpThumbPath = null;
            try {
                tmpThumbPath = mChatManager.getMultimediaPath() + "/"
                        + URLEncoder.encode(msgItem.image.thumbId, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
            }
            final String thumbPath = tmpThumbPath;

            int downloadStatus = msgItem.downloadStatus;
            if (downloadStatus == MessageItem.DOWNLOAD_DONE) {

                // GIF图片的情况
                try {
                    if (Utils.isGIF(thumbPath)) {
                        GifDrawable gifDrawable = new GifDrawable(thumbPath);
                        pic.setVisibility(View.VISIBLE);
                        pic.setImageDrawable(gifDrawable);

                        msgLayout.setTag(holder);
                        msgLayout.setOnClickListener(this);
                        return;
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                    if (!Utils.isSdcardReadable(mChatActivity)) {
                        // SD Card not available
                        pic.setVisibility(View.VISIBLE);
                        pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
                        return;
                    } else {
                        pic.setVisibility(View.VISIBLE);
                        pic.setImageResource(R.drawable.chat_img_thumb_deleted);
                        return;
                    }
                }

                // 非GIF图片的情况
                Bitmap bitmap = mFetcher.getCachedBitmap(thumbPath);
                if (bitmap != null) {
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageBitmap(bitmap);

                    msgLayout.setTag(holder);
                    msgLayout.setOnClickListener(this);
                    return;
                }
                if (!Utils.isSdcardReadable(mChatActivity)) {
                    // SD Card not available
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
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
                        return;
                    }
                    // REPLACEME
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
                    return;
                }

                // The done file exists means that the file was
                // downloaded
                // before, but right now was not found in SD Card
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_img_thumb_deleted);
                return;

            }

            if (downloadStatus == MessageItem.DOWNLOAD_OUT_OF_DATE) {
                // The file was removed from server
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_img_thumb_deleted);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOAD_FAILED) {
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_img_thumb_download_error);

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOADING
                    && msgWrapper.existsDownloadTask) {
                // Probably there is a downloading process right now
                // REPLACEME
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_img_thumb_default);
                return;
            }

            // Show default image resource which means downloading
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.drawable.chat_img_thumb_default);

            // Launch a request to fetch thumb image file.
            msgWrapper.existsDownloadTask = true;
            updateDownloadStatus(msgItem, MessageItem.DOWNLOADING);

            FileDownloadListener listener = new FileDownloadListener() {

                @Override
                public void onProgressChanged(int progress) {
                    super.onProgressChanged(progress);
                }

                @Override
                public void onDownloadFinished(int code, byte[] content) {
                    if (code == FileDownloadListener.CODE_FAILED) {
                        updateDownloadStatus(msgItem,
                                MessageItem.DOWNLOAD_FAILED);
                        postNotifyDataSetChanged();
                    } else if (code == FileDownloadListener.CODE_FILE_DELETED_FROM_SERVER) {
                        updateDownloadStatus(msgItem,
                                MessageItem.DOWNLOAD_OUT_OF_DATE);
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
                            updateDownloadStatus(msgItem,
                                    MessageItem.DOWNLOAD_FAILED);
                            postNotifyDataSetChanged();
                            return;
                        }
                        // Whenever the data can be decoded into a bitmap,
                        // we write the data to the target thumb file
                        updateDownloadStatus(msgItem, MessageItem.DOWNLOAD_DONE);
                        Utils.writeFile(new File(thumbPath), content);

                        opt.inJustDecodeBounds = false;
                        // Try again the decode the bitmap
                        bmp = BitmapFactory.decodeByteArray(content, 0,
                                content.length, opt);
                        if (bmp != null) {
                            mFetcher.addBitmapToCache(thumbPath, bmp);
                        }
                        updateDownloadStatus(msgItem, MessageItem.DOWNLOAD_DONE);
                        postNotifyDataSetChanged();
                    }
                }
            };
            MessageManager.getDefault(mChatActivity).downloadFile(
                    msgItem.image.thumbId, MessageManager.FILE_TYPE_IMAGE,
                    listener);
        } else if (MessageManager.MessageEntity.MsgType.VOICE.getValue() == msgType
                && !isComingMsg) {
            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                voiceView.setVisibility(View.VISIBLE);
                voiceView.setImageResource(R.drawable.chat_voice_no_sdcard);
                return;
            }
            voiceView.setVisibility(View.VISIBLE);
            if (msgWrapper.isVoicePlaying) {
                int playingRes = isComingMsg ? R.drawable.chat_left_voice_playing_indicator
                        : R.drawable.chat_right_voice_playing_indicator;
                AnimationDrawable d = (AnimationDrawable) r
                        .getDrawable(playingRes);
                voiceView.setImageDrawable(d);
                d.start();
            } else {
                voiceView
                        .setImageResource(isComingMsg ? R.drawable.chatfrom_voice_playing
                                : R.drawable.chatto_voice_playing);
            }
            duration.setVisibility(View.VISIBLE);
            duration.setText(String.format("%d\"", msgItem.duration));

            msgLayout.setTag(holder);
            msgLayout.setOnClickListener(this);
        } else if (MessageManager.MessageEntity.MsgType.VOICE.getValue() == msgType
                && isComingMsg) {
            String tmpFilePath = null;
            try {
                tmpFilePath = mChatManager.getMultimediaPath() + "/"
                        + URLEncoder.encode(msgItem.audio.fileId, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
            }
            final String filePath = tmpFilePath;
            int downloadStatus = msgItem.downloadStatus;
            if (downloadStatus == MessageItem.DOWNLOAD_DONE) {
                if (!Utils.isSdcardReadable(mChatActivity)) {
                    // SD Card not available
                    voiceView.setVisibility(View.VISIBLE);
                    voiceView.setImageResource(R.drawable.chat_voice_no_sdcard);
                    return;
                }

                final File audioFile = new File(filePath);
                if (audioFile.exists()) {
                    // The audio file exists in SD card
                    voiceView.setVisibility(View.VISIBLE);
                    if (msgWrapper.isVoicePlaying) {
                        int playingRes = isComingMsg ? R.drawable.chat_left_voice_playing_indicator
                                : R.drawable.chat_right_voice_playing_indicator;
                        AnimationDrawable d = (AnimationDrawable) r
                                .getDrawable(playingRes);
                        voiceView.setImageDrawable(d);
                        d.start();
                    } else {
                        voiceView
                                .setImageResource(isComingMsg ? R.drawable.chatfrom_voice_playing
                                        : R.drawable.chatto_voice_playing);
                    }

                    duration.setVisibility(View.VISIBLE);
                    duration.setText(String.format("%d\"", msgItem.duration));
                    msgItem.downloadStatus = MessageItem.DOWNLOAD_DONE;

                    msgLayout.setTag(holder);
                    msgLayout.setOnClickListener(this);
                    return;
                }

                // The file was downloaded before, but right now was not found
                // in SD Card
                voiceView.setVisibility(View.VISIBLE);
                voiceView.setImageResource(R.drawable.chat_voice_deleted);
                return;
            }
            if (downloadStatus == MessageItem.DOWNLOAD_OUT_OF_DATE) {
                // The file was removed from server
                // REPLACEME
                voiceView.setVisibility(View.VISIBLE);
                voiceView.setImageResource(R.drawable.chat_voice_deleted);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOAD_FAILED) {
                voiceView.setVisibility(View.VISIBLE);
                voiceView
                        .setImageResource(R.drawable.chat_voice_download_error);

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOADING
                    && msgWrapper.existsDownloadTask) {
                return;
            }

            // Show default image resource which means downloading
            voiceView.setVisibility(View.VISIBLE);
            voiceView.setImageResource(R.drawable.chat_voice_default);

            // Launch a request to fetch thumb image file.
            msgWrapper.existsDownloadTask = true;
            updateDownloadStatus(msgItem, MessageItem.DOWNLOADING);

            FileDownloadListener listener = new FileDownloadListener() {

                @Override
                public void onDownloadFinished(int code, byte[] content) {
                    if (code == FileDownloadListener.CODE_FAILED) {
                        updateDownloadStatus(msgItem,
                                MessageItem.DOWNLOAD_FAILED);
                        postNotifyDataSetChanged();
                    } else if (code == FileDownloadListener.CODE_FILE_DELETED_FROM_SERVER) {
                        updateDownloadStatus(msgItem,
                                MessageItem.DOWNLOAD_OUT_OF_DATE);
                        postNotifyDataSetChanged();
                    } else {
                        // Success
                        if (Utils.isSdcardWritable(mChatActivity)) {
                            Utils.writeFile(new File(filePath), content);
                        }
                        long duration = detectDuration(filePath);
                        if (duration > 0L) {
                            ContentValues values = new ContentValues();
                            values.put(MessageItem.COL_DURATION, duration);
                            DatabaseHandler handler = mChatManager
                                    .getDbHandler();
                            handler.update(MessageItem.TABLE, values,
                                    MessageItem.COL_ID + " = ?",
                                    new String[]{String
                                            .valueOf(msgWrapper.item.id)});
                            if (Logger.DEBUG) {
                                Logger.d(TAG, "update db, set duration = "
                                        + duration + " for id = "
                                        + msgWrapper.item.id);
                            }
                            msgWrapper.item.duration = duration;
                        }

                        updateDownloadStatus(msgItem, MessageItem.DOWNLOAD_DONE);
                        postNotifyDataSetChanged();
                    }
                }
            };
            MessageManager.getDefault(mChatActivity).downloadFile(
                    msgItem.audio.fileId, MessageManager.FILE_TYPE_VOICE,
                    listener);
        } else if (msgType == MessageManager.MessageEntity.MsgType.LOCATION
                .getValue()) {
            String location = new String(content);
            String tmpThumbPath = null;
            try {
                tmpThumbPath = mChatManager.getMultimediaPath() + "/"
                        + URLEncoder.encode(location.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e1) {
            }
            final String thumbPath = tmpThumbPath;
            int downloadStatus = msgItem.downloadStatus;
            if (downloadStatus == MessageItem.DOWNLOAD_DONE) {
                Bitmap bitmap = mFetcher.getCachedBitmap(thumbPath);
                if (bitmap != null) {
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageBitmap(bitmap);

                    msgLayout.setTag(holder);
                    msgLayout.setOnClickListener(this);
                    return;
                }
                if (!Utils.isSdcardReadable(mChatActivity)) {
                    // SD Card not available
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageResource(R.drawable.chat_location_default);
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
                        return;
                    }
                    // REPLACEME
                    pic.setVisibility(View.VISIBLE);
                    pic.setImageResource(R.drawable.chat_location_default);
                    return;
                }
                // The done file exists means that the file was downloaded
                // before, but right now was not found in SD Card
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_location_default);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOAD_OUT_OF_DATE) {
                // The file was removed from server
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_location_default);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOAD_FAILED) {
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_location_default);

                msgLayout.setTag(holder);
                msgLayout.setOnClickListener(this);
                return;
            }

            if (downloadStatus == MessageItem.DOWNLOADING
                    && msgWrapper.existsDownloadTask) {
                // Probably there is a downloading process right now
                // REPLACEME
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_location_default);
                return;
            }

            // Show default image resource which means downloading
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.drawable.chat_location_default);

            // Launch a request to fetch thumb image file.
            msgWrapper.existsDownloadTask = true;
            updateDownloadStatus(msgItem, MessageItem.DOWNLOADING);

            String zoom = "13";
            int width = (int) (200 * mDensity);
            int height = (int) (100 * mDensity);
            String size = width + "*" + height;
            String markers = "mid,,A:" + location;
            final String url = String.format(MAP_BITMAP_URL, location, zoom,
                    size, markers, MAP_KEY);
            new Thread() {

                @Override
                public void run() {
                    File f = downloadBitmapToFile(url, new File(thumbPath));
                    if (f == null) {
                        updateDownloadStatus(msgItem,
                                MessageItem.DOWNLOAD_FAILED);
                        postNotifyDataSetChanged();
                        return;
                    }

                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    Bitmap bmp = BitmapFactory.decodeFile(thumbPath, opt);
                    if (opt.outWidth <= 0 || opt.outHeight <= 0) {
                        // The data can not be decoded into a bitmap which
                        // should never happen
                        updateDownloadStatus(msgItem,
                                MessageItem.DOWNLOAD_FAILED);
                        postNotifyDataSetChanged();
                        return;
                    }
                    // Whenever the data can be decoded into a bitmap,
                    // we write the data to the target thumb file
                    updateDownloadStatus(msgItem, MessageItem.DOWNLOAD_DONE);
                    opt.inJustDecodeBounds = false;
                    // Try again the decode the bitmap
                    bmp = BitmapFactory.decodeFile(thumbPath, opt);
                    if (bmp != null) {
                        mFetcher.addBitmapToCache(thumbPath, bmp);
                    }
                    updateDownloadStatus(msgItem, MessageItem.DOWNLOAD_DONE);
                    postNotifyDataSetChanged();
                }
            }.start();
        } else if (isComingMsg
                && msgType == MessageManager.MessageEntity.MsgType.VIDEO
                .getValue()) {

        } else if (!isComingMsg
                && msgType == MessageManager.MessageEntity.MsgType.VIDEO
                .getValue()) {
            String filePath = msgItem.contentFilePath;

            if (!Utils.isSdcardReadable(mChatActivity)) {
                // SD Card not available
                pic.setVisibility(View.VISIBLE);
                pic.setImageResource(R.drawable.chat_img_thumb_no_sdcard);
                return;
            }
            File bmpFile = new File(filePath);
            if (bmpFile.isFile() && bmpFile.exists()) {
                MediaPlayer mm = new MediaPlayer();
                try {
                    mm.setDataSource(filePath);
                    mm.prepare();
                    mm.setDisplay(holder.rightVideoView.getHolder());
                    mm.prepare();
                    mm.start();
                } catch (Exception e) {
                }
                return;
            }
            pic.setVisibility(View.VISIBLE);
            pic.setImageResource(R.drawable.chat_img_thumb_deleted);
        }
    }

    private File downloadBitmapToFile(String urlString, File file) {
        HttpURLConnection urlConnection = null;
        FileOutputStream fos = null;
        InputStream is = null;
        BufferedOutputStream out = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            is = urlConnection.getInputStream();
            if (is == null) {
                return null;
            }
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead = -1;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            return file;
        } catch (final IOException ignored) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ignored) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private static final String MAP_KEY = "aa5a49d9b83877c319927f53b5205074";
    private static final String MAP_BITMAP_URL = "http://restapi.amap.com/v3/staticmap?"
            + "location=%s&zoom=%s&size=%s&markers=%s&key=%s";

    private long detectDuration(String filePath) {
        synchronized (this) {
            // With the synchronized keyword, we can avoid multi-thread occasion
            // which may cause MediaPlayer instances into wrong state
            long duration = -1L;
            try {
                if (mDurationDetector == null) {
                    mDurationDetector = new MediaPlayer();
                }
                mDurationDetector.reset();
                mDurationDetector.setDataSource(filePath);
                mDurationDetector.prepare();
                int millis = mDurationDetector.getDuration();
                if (millis > 0) {
                    int secs = millis / 1000;
                    if (secs == 0L) {
                        duration = 1L;
                    } else {
                        duration = secs;
                    }
                }
            } catch (Exception e) {
                if (Logger.DEBUG) {
                    e.printStackTrace();
                }
            }
            return duration;
        }
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
        MessageItem item = wrapper.item;
        if (item.msgType == MessageManager.MessageEntity.MsgType.VOICE
                .getValue()) {
            playVoice(holder);
        } else if (item.msgType == MessageManager.MessageEntity.MsgType.IMAGE
                .getValue()) {
            // showImage(holder);
            showMultimediaImage(holder);
        } else if (item.msgType == MessageManager.MessageEntity.MsgType.LOCATION
                .getValue()) {
            String location = null;
            location = new String(item.content);
            String[] loc_arr = location.split(",");
            double la = getDouble(loc_arr[1]);
            double lo = getDouble(loc_arr[0]);
            if ((la == 0.0 && lo == 0.0)
                    || item.sendStatus == MessageItem.STATUS_SEND_FAILED) {
                Toast.makeText(
                        mChatActivity,
                        mChatActivity.getResources().getString(
                                R.string.chat_location_error),
                        Toast.LENGTH_LONG).show();
                return;
            }
            LatLng ll = new LatLng(la, lo);
            Intent intent = new Intent(mChatActivity, LocationActivity.class);
            intent.putExtra("latlug", ll);
            mChatActivity.startActivity(intent);
        }
    }

    private double getDouble(String v) {
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    public void close() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Why we release {@link #mDurationDetector} here instead of in the
     * {@link #close()} method, because {@lin} maybe still in
     * use in a future time, remember there maybe a voice downloading process at
     * that time.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mDurationDetector != null) {
            try {
                mDurationDetector.release();
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
        boolean isComingMsg = wrapper.item.sender != mCurrUser;

        if (isComingMsg) {
            try {
                File f = new File(mChatManager.getMultimediaPath(),
                        URLEncoder.encode(wrapper.item.audio.fileId, "UTF-8"));
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
            notifyDataSetChanged();
        } catch (Exception e) {
            if (Logger.DEBUG) {
                Logger.w(TAG, "error:" + e.getMessage());
            }
            wrapper.isVoicePlaying = false;
            notifyDataSetChanged();
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
            stop();
            return false;
        }

    }

    private void showMultimediaImage(ViewHolder h) {
        int pos = h.getPosition();
        MessageItemWrapper wrapper = mWrappers.get(pos);
        if (wrapper.item.sender == mCurrUser) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.fromFile(new File(wrapper.item.contentFilePath)),
                        "image/*");
                mChatActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
            }
        } else {
            Intent intent = new Intent(mChatActivity, ImgPreviewActivity.class);
            intent.putExtra(ImgPreviewActivity.THUMB_PATH, new File(
                    mChatManager.getMultimediaPath(),
                    wrapper.item.image.thumbId).getPath());
            intent.putExtra(ImgPreviewActivity.FILE_PATH,
                    new File(mChatManager.getMultimediaPath(),
                            wrapper.item.image.fileId).getPath());
            mChatActivity.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mWrappers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView msgTips;

        View leftLayout;
        TextView leftUsername;
        ChatTextView leftMsgText;
        View leftMsgLayout;
        View leftMsgImageMask;
        GifImageView leftMsgImage;
        SimpleDraweeView leftAvatar;
        TextView leftVoiceDuration;
        ImageView leftVoicePlaying;
        SurfaceView leftVideoView;

        View rightLayout;
        SimpleDraweeView rightAvatar;
        ImageView rightResend;
        TextView rightUsername;
        ChatTextView rightMsgText;
        View rightMsgLayout;
        View rightMsgImageMask;
        GifImageView rightMsgImage;
        ProgressBar rightSendProgress;
        TextView rightVoiceDuration;
        ImageView rightVoicePlaying;
        SurfaceView rightVideoView;

        public ViewHolder(View v) {
            super(v);
            msgTips = (TextView) v.findViewById(R.id.chat_top_tips);
            View lv = v.findViewById(R.id.chat_left_layout);
            leftLayout = lv;
            leftAvatar = (SimpleDraweeView) lv.findViewById(R.id.chat_left_avatar);
            leftUsername = (TextView) lv.findViewById(R.id.chat_left_username);

            View leftLayout = lv.findViewById(R.id.chat_left_content_layout);
            leftMsgLayout = leftLayout;
            leftMsgText = (ChatTextView) leftLayout
                    .findViewById(R.id.chat_left_msg_text);
            leftMsgImage = (GifImageView) leftLayout
                    .findViewById(R.id.chat_left_msg_pic);
            leftMsgImageMask = leftLayout
                    .findViewById(R.id.chat_left_msg_pic_mask);
            leftVoiceDuration = (TextView) leftLayout
                    .findViewById(R.id.chat_left_voice_duration);
            leftVoicePlaying = (ImageView) leftLayout
                    .findViewById(R.id.chat_left_voice_playing);
            leftVideoView = (SurfaceView) leftLayout
                    .findViewById(R.id.chat_left_video_view);

            View rv = v.findViewById(R.id.chat_right_layout);
            rightLayout = rv;
            rightAvatar = (SimpleDraweeView) rv.findViewById(R.id.chat_right_avatar);
            rightUsername = (TextView) rv
                    .findViewById(R.id.chat_right_username);
            rightResend = (ImageView) rv.findViewById(R.id.chat_right_resend);
            rightSendProgress = (ProgressBar) v
                    .findViewById(R.id.chat_right_send_progress);

            View content = rv.findViewById(R.id.chat_right_content_layout);
            rightMsgLayout = content;
            rightMsgText = (ChatTextView) content
                    .findViewById(R.id.chat_right_msg_text);
            rightMsgImage = (GifImageView) content
                    .findViewById(R.id.chat_right_msg_pic);
            rightMsgImageMask = content
                    .findViewById(R.id.chat_right_msg_pic_mask);
            rightVoiceDuration = (TextView) content
                    .findViewById(R.id.chat_right_voice_duration);
            rightVoicePlaying = (ImageView) content
                    .findViewById(R.id.chat_right_voice_playing);
            rightVideoView = (SurfaceView) content
                    .findViewById(R.id.chat_right_video_view);
        }
    }
}
