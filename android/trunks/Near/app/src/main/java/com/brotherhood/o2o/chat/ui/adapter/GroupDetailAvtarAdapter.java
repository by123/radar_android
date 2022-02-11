package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.GroupUserBean;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBService;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.util.ViewUtil;
import com.skynet.library.message.MessageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laimo.li on 2015/12/29.
 */
public class GroupDetailAvtarAdapter extends BaseAdapter {

    public static final String KICKUID = "-1";

    private boolean creatorTag;

    private String creatorUid;

    private String groupId;

    private List<GroupUserBean> list = new ArrayList<GroupUserBean>();

    private Context context;

    private ViewHolder mViewHolder;

    private boolean showKick;

    public GroupDetailAvtarAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_detail_avatar_item_view, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.ivGroupDetailAvater = (ImageView) convertView.findViewById(R.id.ivGroupDetailAvater);
            mViewHolder.ivKick = (ImageView) convertView.findViewById(R.id.ivKick);
            mViewHolder.ivGroupDetailName = (TextView) convertView.findViewById(R.id.ivGroupDetailName);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final GroupUserBean bean = list.get(position);
        if (bean.getId().equals(KICKUID)) {
            mViewHolder.ivGroupDetailAvater.setImageResource(R.mipmap.ic_chat_kick_normal);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showKick = showKick ? false : true;
                    notifyDataSetChanged();
                }
            });
        } else {
            ImageLoaderManager.displayCircleImageByUrl(context, mViewHolder.ivGroupDetailAvater, list.get(position).getAvatar(), R.mipmap.ic_msg_default);
        }
        if (!TextUtils.isEmpty(creatorUid) && !TextUtils.isEmpty(groupId)) {
            if (showKick) {
                if (bean.getId().equals(KICKUID) || bean.getId().equals(creatorUid)) {
                    ViewUtil.toggleView(mViewHolder.ivKick, false);
                } else {
                    ViewUtil.toggleView(mViewHolder.ivKick, true);
                    mViewHolder.ivKick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            kickOutMember(groupId, bean.getId(), position);
                        }
                    });
                }
            } else {
                ViewUtil.toggleView(mViewHolder.ivKick, false);
            }
        }
        mViewHolder.ivGroupDetailName.setText(list.get(position).getNickname());

        return convertView;
    }


    public class ViewHolder {
        ImageView ivGroupDetailAvater;
        TextView ivGroupDetailName;
        ImageView ivKick;
    }


    public void addAll(List<GroupUserBean> list) {
        this.list.clear();
        if (list != null) {
            this.list.addAll(list);
        }
        if (creatorTag) {
            GroupUserBean bean = new GroupUserBean();
            bean.setNickname(context.getString(R.string.remove_group_chat));
            bean.setId(KICKUID);
            this.list.add(bean);
            notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }


    public void creatorKick() {
        if (!creatorTag) {
            if (!list.isEmpty()) {
                GroupUserBean bean = new GroupUserBean();
                bean.setNickname(context.getString(R.string.remove_group_chat));
                bean.setId(KICKUID);
                list.add(bean);
                notifyDataSetChanged();
            }
            creatorTag = true;
        }
    }


    public void setCreatorUid(String groupId, String creatorUid) {
        this.groupId = groupId;
        this.creatorUid = creatorUid;
    }


    private void kickOutMember(final String groupId, final String uid, final int position) {
        IDSIMManager.getInstance().kickOutMember(groupId, uid, new MessageManager.HttpCallBack() {

            @Override
            public void onSuc(Object o) {
                list.remove(position);
                notifyDataSetChanged();

                IMDBService.addDelByGroupCreator(Long.valueOf(groupId), Long.valueOf(uid));
            }

            @Override
            public void onFail(Object o) {

            }
        });

    }

}
