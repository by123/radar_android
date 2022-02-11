package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.account.Visitor;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.util.DateUtil;
import com.brotherhood.o2o.util.ViewUtil;

import java.util.List;

/**
 * 访客列表适配器
 * Created by jl.zhang on 2015/12/28.
 */
public class VisitListAdapter extends LoadMoreRecylerAdatper<Visitor, VisitListAdapter.VisitViewHolder> {

    private Context mContext;

    public VisitListAdapter(Context context, List<Visitor> list) {
        super(list);
        mContext = context;
    }

    @Override
    protected VisitViewHolder onCreateItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.visitor_list_item, null);
        return new VisitViewHolder(itemView);
    }

    @Override
    protected void onBindItemViewHolder(VisitViewHolder holder, final Visitor visitor, int position) {
        if (visitor == null) {
            return;
        }
        ImageLoaderManager.displayCircleImageByUrl(mContext, holder.mIvIcon, visitor.mIcon, R.mipmap.ic_msg_default);
        holder.mTvTitle.setText(visitor.mNickname);
        int age = DateUtil.parseAge(visitor.mBirthday);
        holder.mTvAge.setText(String.valueOf(age));
        int gender = visitor.mGender;
        if (gender == 0){
            holder.mTvAge.setBackgroundResource(R.drawable.sex_male_oval_bg);
            ViewUtil.setTextViewDrawableLeft(holder.mTvAge, R.mipmap.sex_male_white);
        }else if (gender == 1){
            holder.mTvAge.setBackgroundResource(R.drawable.sex_female_oval_bg);
            ViewUtil.setTextViewDrawableLeft(holder.mTvAge, R.mipmap.sex_female_white);
        }
        holder.mTvSignature.setText(visitor.mSignature);
        if (TextUtils.isEmpty(visitor.mVisitTime)) {
            return;
        }
        final String visitTime = DateUtil.parseJavaTimeToString(Long.valueOf(visitor.mVisitTime), "MM-dd HH:mm");
        if (TextUtils.isEmpty(visitTime)) {
            return;
        }
        String[] visitTimeArray = visitTime.split(" ");
        if (visitTimeArray == null) {
            return;
        }
        if (visitTimeArray[0] != null) {
            holder.mTvDate.setText(visitTimeArray[0]);
        }
        if (visitTimeArray[1] != null) {
            holder.mTvTime.setText(visitTimeArray[1]);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(visitor.mUid)){
                    return;
                }
                OtherUserDetailActivity.show(mContext, visitor.mUid);
            }
        });
    }

    protected static class VisitViewHolder extends RecyclerView.ViewHolder{

        private TextView mTvTitle;
        private ImageView mIvIcon;
        private TextView mTvSignature;
        private TextView mTvDate;
        private TextView mTvAge;
        private TextView mTvTime;

        public VisitViewHolder(View itemView) {
            super(itemView);
            mIvIcon = (ImageView) itemView.findViewById(R.id.ivVisitorIcon);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvVisitorName);
            mTvAge = (TextView) itemView.findViewById(R.id.tvVisitorAge);
            mTvSignature = (TextView) itemView.findViewById(R.id.tvVisitorSignature);
            mTvDate = (TextView) itemView.findViewById(R.id.tvVisitorDate);
            mTvTime = (TextView) itemView.findViewById(R.id.tvVisitorTime);
        }
    }
}
