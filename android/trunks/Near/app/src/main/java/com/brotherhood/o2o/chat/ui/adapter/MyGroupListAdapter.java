package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.MyGroupBean;
import com.brotherhood.o2o.chat.helper.ChatSenderHelper;
import com.brotherhood.o2o.chat.ui.ChatActivity;
import com.brotherhood.o2o.manager.GroupAvatarManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class MyGroupListAdapter extends RecyclerView.Adapter<MyGroupListAdapter.ViewHolder> {

    private List<MyGroupBean> myGroupList = new ArrayList<MyGroupBean>();

    private Context context;

    public MyGroupListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_group_chat_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MyGroupBean myGroup = myGroupList.get(position);
        holder.tvMyGroupTitle.setText(myGroup.getCn());
        GroupAvatarManager.getInstance().loadGroupAvatar(context, myGroup.getCId(), myGroup.getMembers(), holder.ivMyGroupAvatar, new GroupAvatarManager.CallBack() {
            @Override
            public void avatar(String avatar) {
                ImageLoaderManager.displayCircleImageByUrl(context, holder.ivMyGroupAvatar, avatar, R.mipmap.ic_msg_default);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myGroupList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivMyGroupAvatar;
        private TextView tvMyGroupTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ivMyGroupAvatar = (ImageView) itemView.findViewById(R.id.ivMyGroupAvatar);
            tvMyGroupTitle = (TextView) itemView.findViewById(R.id.tvMyGroupTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MyGroupBean group = myGroupList.get(getPosition());
            ChatActivity.show(context, String.valueOf(group.getCId()), group.getCn(), null,
                    ChatSenderHelper.ChatMode.MODE_GROUP);
        }
    }


    public void addAll(List<MyGroupBean> myGroupList ) {
        this.myGroupList.addAll(myGroupList);
        notifyDataSetChanged();
    }


    public void remove(String gid) {
        for (MyGroupBean bean : myGroupList) {
            if (bean.getCId().equals(gid)) {
                myGroupList.remove(bean);
                notifyDataSetChanged();
                break;
            }
        }
    }


    public void changeName(String gid, String name) {
        for (MyGroupBean bean : myGroupList) {
            if (bean.getCId().equals(gid)) {
                bean.setCn(name);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
