package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.SearchUserBean;
import com.brotherhood.o2o.chat.ui.AddFriendVerifyActivity;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laimo.li on 2015/12/24.
 */
public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.ViewHolder> {

    private List<SearchUserBean> list = new ArrayList<SearchUserBean>();

    private Context context;

    public AddFriendsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public AddFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_friend_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddFriendsAdapter.ViewHolder holder, int position) {

        final SearchUserBean userBean = list.get(position);

        ImageLoaderManager.displayCircleImageByUrl(context, holder.ivUserAvatar, userBean.getAvatar(), R.mipmap.ic_msg_default);
        holder.tvUserName.setText(userBean.getNickname());

        if (userBean.getGender() == 0) {
            holder.ivAddUserSex.setImageResource(R.mipmap.sex_male_white);
            holder.lyAddUserSex.setBackgroundResource(R.drawable.add_friend_sex_male_bg);
        } else {
            holder.ivAddUserSex.setImageResource(R.mipmap.sex_female_white);
            holder.lyAddUserSex.setBackgroundResource(R.drawable.add_friend_sex_female_bg);
        }

        if (userBean.getBirthday() == 0) {
            holder.tvAddUserSex.setText("");
        } else {
            holder.tvAddUserSex.setText(String.valueOf(DateUtil.parseAge(userBean.getBirthday())));
        }

        if (userBean.is_friend()) {
            holder.tvIsFriend.setVisibility(View.VISIBLE);
            holder.ibAddFriend.setVisibility(View.GONE);
        } else {

            holder.tvIsFriend.setVisibility(View.GONE);
            holder.ibAddFriend.setVisibility(View.VISIBLE);
            holder.ibAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddFriendVerifyActivity.show(context, userBean.getId());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivUserAvatar;
        private TextView tvUserName;

        private ImageButton ibAddFriend;
        private TextView tvIsFriend;

        private LinearLayout lyAddUserSex;
        private ImageView ivAddUserSex;
        private TextView tvAddUserSex;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUserAvatar = (ImageView) itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);

            ibAddFriend = (ImageButton) itemView.findViewById(R.id.ibAddFriend);
            tvIsFriend = (TextView) itemView.findViewById(R.id.tvIsFriend);

            lyAddUserSex = (LinearLayout) itemView.findViewById(R.id.lyAddUserSex);
            ivAddUserSex = (ImageView) itemView.findViewById(R.id.ivAddUserSex);
            tvAddUserSex = (TextView) itemView.findViewById(R.id.tvAddUserSex);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SearchUserBean bean = list.get(getPosition());
            OtherUserDetailActivity.show(context, String.valueOf(bean.getId()), true);
        }
    }


    public void addAll(List<SearchUserBean> list) {
        if (list == null) {
            return;
        }
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    public void clearAll() {
        this.list.clear();
        notifyDataSetChanged();
    }

}
