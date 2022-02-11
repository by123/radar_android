package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.MyFriendBean;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.ui.widget.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.brotherhood.o2o.util.CharacterParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by laimo.li on 2016/1/18.
 */
public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    List<MyFriendBean> friends = new ArrayList<MyFriendBean>();

    private CharacterParser characterParser = new CharacterParser();

    private Context context;

    private MultiStateView stateView;

    public MyFriendsAdapter(Context context, MultiStateView stateView) {
        this.context = context;
        this.stateView = stateView;
    }

    @Override
    public MyFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == -1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_friends_footer_view, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_friends_item_view, parent, false);
        }

        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(MyFriendsAdapter.ViewHolder holder, int position) {
        if (position == friends.size()) {
            holder.tvMyFriendsCount.setText(context.getString(R.string.my_friends_count, friends.size()));
        } else {
            MyFriendBean friend = friends.get(position);
            ImageLoaderManager.displayCircleImageByUrl(context, holder.ivFriendavatar, friend.getAvatar(), R.mipmap.ic_msg_default);
            holder.tvFriendName.setText(friend.getNickname());


        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == friends.size()) {
            return -1;
        } else {
            return friends.get(position).getSortLetters().charAt(0);
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
        textView.setText(friends.get(position).getSortLetters());
    }

    @Override
    public int getItemCount() {
        return friends.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == friends.size()) {
            return -1;
        }
        return super.getItemViewType(position);
    }

    public void remove(String uid) {
        List<MyFriendBean> list = new ArrayList<MyFriendBean>();
        list.addAll(friends);
        for(MyFriendBean bean: list){
            if(bean.getId().equals(uid)){
                list.remove(bean);
                break;
            }
        }
        addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvCatalog;
        private ImageView ivFriendavatar;
        private TextView tvFriendName;
        private TextView tvMyFriendsCount;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case -1:
                    tvMyFriendsCount = (TextView) itemView.findViewById(R.id.tvMyFriendsCount);
                    break;
                default:
                    tvCatalog = (TextView) itemView.findViewById(R.id.tvCatalog);
                    ivFriendavatar = (ImageView) itemView.findViewById(R.id.ivFriendavatar);
                    tvFriendName = (TextView) itemView.findViewById(R.id.tvFriendName);
                    itemView.setOnClickListener(this);
                    break;

            }

        }


        @Override
        public void onClick(View v) {
            MyFriendBean friendBean = friends.get(getPosition());
            if (friendBean != null) {
                OtherUserDetailActivity.show(context, friendBean.getId(), true);
            }
        }
    }


    public void addAll(List<MyFriendBean> list) {
        if (list == null) {
            return;
        }
        int count = list.size();
        for (int i = 0; i < count; i++) {

            MyFriendBean friendBean = list.get(i);
            String nickName = friendBean.getNickname();
            if (!TextUtils.isEmpty(nickName)) {
                String pinyin = characterParser.getSelling(friendBean.getNickname());
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    friendBean.setSortLetters(sortString.toUpperCase());
                } else {
                    friendBean.setSortLetters("#");
                }
            } else {
                friendBean.setSortLetters("#");
            }
        }
        Collections.sort(list, new PinyinComparator());
        this.friends.clear();
        this.friends.addAll(list);
        if(friends.size() == 0){
            stateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        }
        notifyDataSetChanged();
    }


    public class PinyinComparator implements Comparator<MyFriendBean> {

        public int compare(MyFriendBean o1, MyFriendBean o2) {
            if (o1.getSortLetters().equals("@")
                    || o2.getSortLetters().equals("#")) {
                return -1;
            } else if (o1.getSortLetters().equals("#")
                    || o2.getSortLetters().equals("@")) {
                return 1;
            } else {
                return o1.getSortLetters().compareTo(o2.getSortLetters());
            }
        }

    }

    public int getPositionForSection(int sectionIndex) {
        int count = friends.size();
        for (int i = 0; i < count; i++) {
            String sortStr = friends.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

}
