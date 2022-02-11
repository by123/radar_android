package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.GroupUserBean;
import com.brotherhood.o2o.bean.Member;
import com.brotherhood.o2o.bean.UserInfoBean;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBGroupService;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.db.service.IMDBNewFriendsService;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.chat.db.service.IMDBSystemMsgService;
import com.brotherhood.o2o.chat.helper.ChatSenderHelper;
import com.brotherhood.o2o.chat.model.IMGroupInfoBean;
import com.brotherhood.o2o.chat.model.IMLatestMsgBean;
import com.brotherhood.o2o.chat.ui.ChatActivity;
import com.brotherhood.o2o.chat.ui.MyFriendsActivity;
import com.brotherhood.o2o.chat.ui.MyGroupListActivity;
import com.brotherhood.o2o.chat.ui.NewFriendsActivity;
import com.brotherhood.o2o.chat.ui.SystemMsgListActivity;
import com.brotherhood.o2o.controller.SwLinController;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.GroupAvatarManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.request.GetAvatarRequest;
import com.brotherhood.o2o.request.GetGroupMembersRequest;
import com.brotherhood.o2o.ui.widget.MsgHintView;
import com.brotherhood.o2o.ui.widget.SwLin;
import com.brotherhood.o2o.ui.widget.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.brotherhood.o2o.util.DateUtil;
import com.skynet.library.message.MessageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by laimo.li on 2015/12/22.
 */
public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private SwLinController mSwLinController;

    private LinkedList<IMLatestMsgBean> list = new LinkedList<IMLatestMsgBean>();

    private Context context;

    public MyMessageAdapter(Context context) {
        mSwLinController = new SwLinController();
        setHasStableIds(true);
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_message_top_view, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_message_item_view, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (position != 0) {
            final IMLatestMsgBean latestMsg = getItem(position);
            if (latestMsg == null) {
                return;
            }
            if (latestMsg.isAckMsg()) {
                getPrivateMsgInfo(position, holder.tvMessageName, holder.ivMessageIcon);
                IMDBLatestMsgService.queryUnReadCount(latestMsg.taUid, new IMDBLatestMsgService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        holder.msgHintView.hasMsg((long) obj);
                    }
                });
            } else if (latestMsg.isGroupMsg()) {
                getGroupMsgInfo(position, holder.tvMessageName, holder.ivMessageIcon);
                IMDBLatestMsgService.queryUnReadCount(latestMsg.taUid, new IMDBLatestMsgService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        holder.msgHintView.hasMsg((long) obj);
                    }
                });
            } else if (latestMsg.isPrivateMsg()) {
                getPrivateMsgInfo(position, holder.tvMessageName, holder.ivMessageIcon);
                IMDBLatestMsgService.queryUnReadCount(latestMsg.taUid, new IMDBLatestMsgService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        holder.msgHintView.hasMsg((long) obj);
                    }
                });
            } else if (latestMsg.isRequestMsg()) {
                holder.tvMessageName.setText(R.string.new_friend);
                holder.ivMessageIcon.setImageResource(R.mipmap.ic_msg_new_friends_normal);
                IMDBNewFriendsService.queryAllUnAckNum(new IMDBNewFriendsService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        holder.msgHintView.hasMsg((long) obj);
                    }
                });
            } else if (latestMsg.isSystemMsg()) {
                holder.tvMessageName.setText(R.string.system_msg);
                holder.ivMessageIcon.setImageResource(R.mipmap.ic_msg_system_normal);
                IMDBSystemMsgService.queryAllUnReadMsgNum(new IMDBSystemMsgService.DBListener() {
                    @Override
                    public void onResult(Object obj) {
                        holder.msgHintView.hasMsg((long) obj);
                    }
                });
            }

            String content = latestMsg.content;
            int msgType = latestMsg.msgType;
            if (msgType == MessageManager.MessageEntity.MsgType.IMAGE.getValue()) {
                content = context.getString(R.string.latest_msg_image);
            } else if (msgType == MessageManager.MessageEntity.MsgType.VOICE.getValue()) {
                content = context.getString(R.string.latest_msg_voice);
            }
            holder.tvMessageContent.setText(content);
            holder.tvMessageTime.setText(DateUtil.getLatestMsgStrTime(latestMsg.time));

            mSwLinController.put(position, holder.sw);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_message_head_view, parent, false);
        return new RecyclerView.ViewHolder(view) {

        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(R.string.message);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout llMyFriends;
        private LinearLayout llMyGroupChat;


        private ImageView ivMessageIcon;
        private TextView tvMessageTime;
        private TextView tvMessageName;
        private TextView tvMessageContent;
        private MsgHintView msgHintView;

        private SwLin sw;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case 0:
                    llMyFriends = (LinearLayout) itemView.findViewById(R.id.llMyFriends);
                    llMyFriends.setOnClickListener(new MyFriendsClick());
                    llMyGroupChat = (LinearLayout) itemView.findViewById(R.id.llMyGroupChat);
                    llMyGroupChat.setOnClickListener(new MyGroupChatClick());
                    break;
                default:

                    sw = (SwLin) itemView.findViewById(R.id.swLinLayout);

                    ivMessageIcon = (ImageView) itemView.findViewById(R.id.ivMessageIcon);

                    tvMessageTime = (TextView) itemView.findViewById(R.id.tvMessageTime);
                    tvMessageName = (TextView) itemView.findViewById(R.id.tvMessageName);
                    tvMessageContent = (TextView) itemView.findViewById(R.id.tvMessageContent);

                    msgHintView = (MsgHintView) itemView.findViewById(R.id.msgHintView);

                    itemView.findViewById(R.id.ryMain).setOnClickListener(this);

                    itemView.findViewById(R.id.btnMessageDelete).setOnClickListener(new DelteMsgClick());


                    break;

            }
        }

        @Override
        public void onClick(View v) {

            int position = getPosition();
            if (mSwLinController.isShowMenu(position)) {
                mSwLinController.showMainLayout();
                return;
            }

            IMLatestMsgBean latestMsg = getItem(position);
            if (latestMsg.isAckMsg()) {
                ChatActivity.show(context, String.valueOf(latestMsg.taUid), latestMsg.nickname, latestMsg.avatar,
                        ChatSenderHelper.ChatMode.MODE_PRIVATE);
            } else if (latestMsg.isGroupMsg()) {
                ChatActivity.show(context, String.valueOf(latestMsg.taUid), latestMsg.nickname, null,
                        ChatSenderHelper.ChatMode.MODE_GROUP);
            } else if (latestMsg.isPrivateMsg()) {
                ChatActivity.show(context, String.valueOf(latestMsg.taUid), latestMsg.nickname,
                        latestMsg.avatar, ChatSenderHelper.ChatMode.MODE_PRIVATE);
            } else if (latestMsg.isRequestMsg()) {
                NewFriendsActivity.show(context);
            } else if (latestMsg.isSystemMsg()) {
                SystemMsgListActivity.show(context);
            }


        }


        public class DelteMsgClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {

                mSwLinController.showMainLayout();
                int position = getPosition();
                IMLatestMsgBean latestMsg = getItem(position);

                if (latestMsg.isAckMsg()) {
                    IMDBService.deleteChatTable(latestMsg.taUid, false);
                } else if (latestMsg.isGroupMsg()) {
                    IMDBGroupService.deleteGroupInfo(String.valueOf(latestMsg.taUid));
                    IMDBService.deleteChatTable(latestMsg.taUid, true);
                } else if (latestMsg.isPrivateMsg()) {
                    IMDBService.deleteChatTable(latestMsg.taUid, false);
                } else if (latestMsg.isRequestMsg()) {
                    IMDBNewFriendsService.deleteApplyInfo();
                } else if (latestMsg.isSystemMsg()) {
                    IMDBSystemMsgService.deleteAllMsg();
                }

                IMDBLatestMsgService.deleteMsg(latestMsg.taUid);
                remove(getPosition());
            }
        }


        public class MyFriendsClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyFriendsActivity.class);
                context.startActivity(intent);
            }
        }

        public class MyGroupChatClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyGroupListActivity.class);
                context.startActivity(intent);
            }
        }


    }


    public void addAll(LinkedList<IMLatestMsgBean> msgs) {
        mSwLinController.showMainLayout();
        list.clear();
        if (msgs == null) {
            msgs = new LinkedList<IMLatestMsgBean>();
        }
        msgs.add(0, null);
        list.addAll(msgs);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (list.size() > position) {
            list.remove(position);
            notifyDataSetChanged();
        }
    }

    public IMLatestMsgBean getItem(int position) {
        return list.get(position);
    }


    //-------------------消息数据请求--------------------------


    private void getPrivateMsgInfo(final int position, final TextView textView, final ImageView iamgeView) {

        IMLatestMsgBean bean = list.get(position);
        if (!TextUtils.isEmpty(bean.nickname) && !TextUtils.isEmpty(bean.avatar)) {
            set(textView, iamgeView, bean);
            return;
        }
        GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(String.valueOf(bean.taUid), new OnResponseListener<List<UserInfoBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {

                if (avatarBeans == null || avatarBeans.size() == 0) {
                    return;
                }

                UserInfoBean avatar = avatarBeans.get(0);

                IMLatestMsgBean bean = list.get(position);
                bean.nickname = avatar.getNickname();
                bean.avatar = avatar.getAvatar();

                set(textView, iamgeView, bean);
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
        request.sendRequest();
    }


    private void getGroupMsgInfo(final int position, final TextView textView, final ImageView iamgeView) {

        final IMLatestMsgBean bean = list.get(position);

        IMDBGroupService.queryGroupInfo(String.valueOf(bean.taUid), new IMDBGroupService.DBListener() {
            @Override
            public void onResult(Object obj) {
                IMGroupInfoBean info = (IMGroupInfoBean) obj;
                if (info != null) {
                    bean.nickname = info.name;
                    bean.avatar = info.avatar;
                    set(textView, iamgeView, bean);
                }
                getGroupInfoForNet(bean, textView, iamgeView);

            }
        });
    }


    private void getGroupInfoForNet(final IMLatestMsgBean bean, final TextView textView, final ImageView iamgeView) {

        IDSIMManager.getInstance().queryGroupInfo(String.valueOf(bean.taUid), new MessageManager.HttpCallBack() {


            @Override
            public void onSuc(Object o) {
                try {
                    JSONObject j = (JSONObject) o;
                    String name = j.getString("chnName");
                    long time = j.getLong("Time");
                    long creatorUid = j.getLong("Creator");

                    //textView.setText(name);
                    bean.nickname = name;
                    set(textView, iamgeView, bean);

                    IMDBGroupService.add(Long.valueOf(bean.taUid), time, creatorUid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(Object o) {

            }
        });

        GetGroupMembersRequest request = GetGroupMembersRequest.createGroupMembersRequest(String.valueOf(bean.taUid), new OnResponseListener<List<GroupUserBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<GroupUserBean> avatarBeans, boolean cache) {
                List<Member> members = new ArrayList<Member>();
                int size = avatarBeans.size() > 3 ? 3 : avatarBeans.size();
                for (int i = 0; i < size; i++) {
                    Member member = new Member();
                    member.setUid(avatarBeans.get(i).getId());
                    members.add(member);
                }
                GroupAvatarManager.getInstance().loadGroupAvatar(context, String.valueOf(bean.taUid), members, iamgeView, new GroupAvatarManager.CallBack() {
                    @Override
                    public void avatar(String avatar) {
                        bean.avatar = avatar;
                        set(textView, iamgeView, bean);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
        request.sendRequest();
    }


    private void set(TextView tvNickName, ImageView ivAvater, IMLatestMsgBean bean) {
        tvNickName.setText(bean.nickname);
        ImageLoaderManager.displayCircleImageByUrl(context.getApplicationContext(), ivAvater, bean.avatar, R.mipmap.ic_msg_default);

        if (bean.isGroupMsg()) {
            IMDBGroupService.add(Long.valueOf(bean.taUid), bean.nickname, bean.avatar);
        }
    }

}