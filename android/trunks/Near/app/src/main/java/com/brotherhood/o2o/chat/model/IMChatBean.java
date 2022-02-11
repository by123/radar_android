package com.brotherhood.o2o.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.skynet.library.message.MessageManager;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class IMChatBean implements Parcelable {

    public IMChatBean() {
    }

    protected IMChatBean(Parcel in) {
        id = in.readLong();
        msgId = in.readLong();
        msgIdOther = in.readLong();
        msgType = in.readInt();
        subType = in.readInt();
        content = in.readString();
        groupId = in.readLong();
        receiverId = in.readLong();
        sender = in.readLong();
        contentFilePath = in.readString();
        extra = in.readString();
        isHello = in.readByte() != 0;
        time = in.readLong();
        sendStatus = in.readInt();
        hasRead = in.readByte() != 0;
        duration = in.readLong();
        downloadStatus = in.readInt();
    }

    public static final Creator<IMChatBean> CREATOR = new Creator<IMChatBean>() {
        @Override
        public IMChatBean createFromParcel(Parcel in) {
            return new IMChatBean(in);
        }

        @Override
        public IMChatBean[] newArray(int size) {
            return new IMChatBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(msgId);
        dest.writeLong(msgIdOther);
        dest.writeInt(msgType);
        dest.writeInt(subType);
        dest.writeString(content);
        dest.writeLong(groupId);
        dest.writeLong(receiverId);
        dest.writeLong(sender);
        dest.writeString(contentFilePath);
        dest.writeString(extra);
        dest.writeByte((byte) (isHello ? 1 : 0));
        dest.writeLong(time);
        dest.writeInt(sendStatus);
        dest.writeByte((byte) (hasRead ? 1 : 0));
        dest.writeLong(duration);
        dest.writeInt(downloadStatus);
    }


    public static enum SendState {
        STATUS_SENDING(1), STATUS_SEND_FAILED(2), STATUS_SEND_SUCCESS(3);

        private final int value;

        private SendState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static enum DownloadState {
        DOWNLOADING(1), DOWNLOAD_DONE(2), DOWNLOAD_FAILED(3), DOWNLOAD_OUT_OF_DATE(4);

        private final int value;

        private DownloadState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Image {
        public String fileId;
        public String thumbId;
    }

    public static class Voice {
        public String fileId;
    }

    public long id;//rowid
    public long msgId;//自己的msgid
    public long msgIdOther;//对方的msgid

    public int msgType;
    public int subType;
    public MessageManager.MessageEntity.MsgParam msgParam;
    public String content;
    public long groupId = 0L;

    // 接受者id
    public long receiverId;
    public long sender;

    public String contentFilePath;
    public String extra;
    public boolean isHello;
    public long time;
    public int sendStatus;
    public boolean hasRead;

    public long duration = 1;
    public int downloadStatus = -1;

}
