package com.brotherhood.o2o.chat.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.UserInfoBean;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBNewFriendsService;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.chat.model.IMApplyInfoBean;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.listener.SwLinViewHolderClickListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.GetAvatarRequest;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.ui.adapter.SwLinRecylerAdatper;
import com.brotherhood.o2o.ui.adapter.SwLinViewHolder;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.skynet.library.message.MessageManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by laimo.li on 2015/12/30.
 */
public class NewFriendsAdapter extends SwLinRecylerAdatper<IMApplyInfoBean, NewFriendsAdapter.ViewHolder> implements SwLinViewHolderClickListener {

    private MultiStateView stateView;

    public NewFriendsAdapter(Activity context, MultiStateView stateView) {
        super(context);
        this.stateView = stateView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_friends_item_view, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        IMApplyInfoBean bean = mList.get(position);

        holder.tvMessageContent.setText(bean.msgContents);

        getNikeNameAndAvatar(position, holder.tvUserName, holder.ivUserAvatar);

        if (bean.isAck) {
            holder.tvAddFriended.setVisibility(View.VISIBLE);
            holder.btnNewFriendsAcc.setVisibility(View.GONE);
        } else {
            holder.tvAddFriended.setVisibility(View.GONE);
            holder.btnNewFriendsAcc.setVisibility(View.VISIBLE);
        }

        holder.btnNewFriendsAcc.setTag(bean);
        holder.btnNewFriendsAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final IMApplyInfoBean bean = (IMApplyInfoBean) v.getTag();

                IDSIMManager.getInstance().accaddfriend(String.valueOf(bean.taUid), new MessageManager.HttpCallBack() {

                    @Override
                    public void onSuc(Object o) {
                        ColorfulToast.green(mContext, mContext.getString(R.string.add_friend_suc), 0);
                        IMDBNewFriendsService.updateToAck(bean.taUid);
                        bean.isAck = true;
                        notifyDataSetChanged();

                        AccountManager.getInstance().getUser().mProfile.mFriendTotal++;
                        NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.MSG_MY_FRIEND_UPDATA, null);

                        // 插表
                        IMDBService.addAcceptFriendMsg(bean.taUid);
                    }

                    @Override
                    public void onFail(Object o) {

                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onItemClick(int position) {
        IMApplyInfoBean bean = mList.get(position);
        OtherUserDetailActivity.show(mContext, String.valueOf(bean.taUid), true);
    }

    public class ViewHolder extends SwLinViewHolder {

        private ImageView ivUserAvatar;
        private TextView tvUserName;
        private TextView tvMessageContent;
        private TextView tvAddFriended;
        private Button btnNewFriendsAcc;

        public ViewHolder(View itemView, SwLinViewHolderClickListener listener) {
            super(itemView, listener);
            ivUserAvatar = (ImageView) itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvMessageContent = (TextView) itemView.findViewById(R.id.tvMessageContent);
            tvAddFriended = (TextView) itemView.findViewById(R.id.tvAddFriended);
            btnNewFriendsAcc = (Button) itemView.findViewById(R.id.btnNewFriendsAcc);
            itemView.findViewById(R.id.btnMessageDelete).setOnClickListener(new DelteMsgClick());

        }

        public class DelteMsgClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                int position = getPosition();
                IMDBNewFriendsService.deleteApplyInfo(mList.get(position).taUid);
                remove(position);
                if (mList.isEmpty()) {
                    stateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                }
            }
        }

    }

    public void addAll(LinkedList<IMApplyInfoBean> list) {
        if (list == null) {
            return;
        }
        this.mList.addAll(list);
        notifyDataSetChanged();
    }


    //-------------------消息数据请求--------------------------


    private void getNikeNameAndAvatar(final int position, final TextView textView, final ImageView iamgeView) {

        GetAvatarRequest request = GetAvatarRequest.createAvatarRequest(String.valueOf(mList.get(position).taUid), new OnResponseListener<List<UserInfoBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<UserInfoBean> avatarBeans, boolean cache) {
                if (avatarBeans == null || avatarBeans.size() == 0) {
                    return;
                }
                UserInfoBean avatar = avatarBeans.get(0);

                textView.setText(avatar.getNickname());
                ImageLoaderManager.displayCircleImageByUrl(mContext, iamgeView, avatar.getAvatar(), R.mipmap.ic_msg_default);

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
        request.sendRequest();
    }

}
