package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.db.service.IMDBSystemMsgService;
import com.brotherhood.o2o.chat.model.IMSystemMsgBean;
import com.brotherhood.o2o.chat.ui.SystemMsgDetailActivity;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.SwLinViewHolderClickListener;
import com.brotherhood.o2o.ui.adapter.SwLinRecylerAdatper;
import com.brotherhood.o2o.ui.adapter.SwLinViewHolder;
import com.brotherhood.o2o.ui.widget.SwLin;
import com.brotherhood.o2o.util.DateUtil;
import com.brotherhood.o2o.util.ViewUtil;

import java.util.LinkedList;

/**
 * Created by laimo.li on 2015/12/30.
 */
public class SystemMsgListAdapter extends SwLinRecylerAdatper<IMSystemMsgBean, SystemMsgListAdapter.ViewHolder> implements SwLinViewHolderClickListener {

    private MultiStateView stateView;

    public SystemMsgListAdapter(Context context,MultiStateView stateView) {
        super(context);
        this.stateView = stateView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.system_msg_list_item_view, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final IMSystemMsgBean bean = mList.get(position);
        holder.tvMsgTitle.setText(bean.title);
        holder.tvMsgTime.setText(DateUtil.parseUnixTimeToString(bean.time, "yyyy-MM-dd"));
        holder.tvMsgContent.setText(bean.content);
        if (bean.hasRead) {
            ViewUtil.toggleView(holder.ivRightTip, false);
        } else {
            ViewUtil.toggleView(holder.ivRightTip, true);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onItemClick(int position) {
        IMSystemMsgBean bean = mList.get(position);
        SystemMsgDetailActivity.show(mContext, bean._id, bean.title, bean.content);
    }

    public class ViewHolder extends SwLinViewHolder {

        private ImageView ivRightTip;
        private TextView tvMsgTitle;
        private TextView tvMsgTime;
        private TextView tvMsgContent;
        private SwLin sw;

        public ViewHolder(View itemView, SwLinViewHolderClickListener listener) {
            super(itemView, listener);
            sw = (SwLin) itemView.findViewById(R.id.swLinLayout);
            ivRightTip = (ImageView) itemView.findViewById(R.id.ivRightTip);
            tvMsgTitle = (TextView) itemView.findViewById(R.id.tvMsgTitle);
            tvMsgTime = (TextView) itemView.findViewById(R.id.tvMsgTime);
            tvMsgContent = (TextView) itemView.findViewById(R.id.tvMsgContent);
            itemView.findViewById(R.id.btnMessageDelete).setOnClickListener(new DelteMsgClick());
        }

        public class DelteMsgClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                int position = getPosition();
                remove(position);
                IMSystemMsgBean bean = mList.get(position);
                IMDBSystemMsgService.deleteMsg(bean._id, bean.hasRead);
                if (mList.isEmpty()) {
                    stateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                }
            }
        }
    }

    public void addAll(LinkedList<IMSystemMsgBean> list) {
        if (list == null) {
            return;
        }
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public void deleteAllMsg() {
        this.mList.clear();
        notifyDataSetChanged();
    }

}
