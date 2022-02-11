package com.brotherhood.o2o.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.model.ChatListBean;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.ui.widget.deletelistview.SlideView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by by.huang on 2015/7/14.
 */
public class ChatListAdapter extends BaseAdapter implements SlideView.OnSlideListener {

    private List<ChatListBean> mDatas;
    private int size = 0;
    private LayoutInflater mInflate;
    private Context mContext;
    private SlideView mLastSlideViewWithStatusOn;

    public ChatListAdapter(Context context, List<ChatListBean> mDatas) {
        this.mContext = context;
        updateDatas(mDatas);
        mInflate = LayoutInflater.from(context);
    }

    public void updateDatas(List<ChatListBean> mDatas) {
        this.mDatas = mDatas;
        if (mDatas != null) {
            size = mDatas.size();
        }
        notifyDataSetChanged();
    }

    public List<ChatListBean> getDatas() {
        return mDatas;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        SlideView slideView = (SlideView) view;
        if (slideView == null) {
            holder = new ViewHolder();
            View itemView = mInflate.inflate(R.layout.chatlist_item, null);
            slideView = new SlideView(mContext);
            slideView.setContentView(itemView);
            holder.mDeleteHolder = (ViewGroup) slideView.findViewById(R.id.holder);

            holder.mHeadImg = (SimpleDraweeView) slideView.findViewById(R.id.img_head);
            holder.mNameTxt = (TextView) slideView.findViewById(R.id.txt_name);
            holder.mContentTxt = (TextView) slideView.findViewById(R.id.txt_content);
            holder.mCountTxt = (TextView) slideView.findViewById(R.id.txt_count);
            holder.mTimeTxt = (TextView) slideView.findViewById(R.id.txt_time);
            holder.mGenderImg = (ImageView) slideView.findViewById(R.id.img_gender);

            slideView.setOnSlideListener(this);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        ChatListBean data = mDatas.get(position);
        data.mSlideView = slideView;
        data.mSlideView.shrink();

        holder.mNameTxt.setText(data.mNickName + "");
        holder.mContentTxt.setText(data.mLastContent + "");
        int unread = data.mUnread;
        if (unread > 0) {
            holder.mCountTxt.setVisibility(View.VISIBLE);
//            holder.mCountTxt.setText(data.mUnread + "");
        } else {
            holder.mCountTxt.setVisibility(View.GONE);
        }
        if (data.mGender == 0) {
            holder.mGenderImg.setBackgroundResource(R.drawable.shape_sex_male_bg);
            holder.mGenderImg.setImageResource(R.drawable.ic_sex_male_white);
        } else {
            holder.mGenderImg.setBackgroundResource(R.drawable.shape_sex_female_bg);
            holder.mGenderImg.setImageResource(R.drawable.ic_sex_female_white);
        }
        holder.mTimeTxt.setText(formatTime(data.mLastUpdateTime, "MM-dd HH:mm"));
        ImageLoader.getInstance().setImageUrl(holder.mHeadImg, data.mAvatarUrl);
        holder.mDeleteHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ChatCompent.shareCompent(mContext).deleteSession(mDatas.get(position).mTargetId);
                mDatas.remove(position);
                updateDatas(mDatas);
            }
        });
        return slideView;
    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    class ViewHolder {
        TextView mContentTxt;
        TextView mCountTxt;
        TextView mTimeTxt;
        TextView mNameTxt;
        SimpleDraweeView mHeadImg;
        ImageView mGenderImg;
        ViewGroup mDeleteHolder;
    }


    /**
     * 格式化消息时间
     *
     * @param time
     * @return
     */
    private String formatTime(long time, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = new Date(time * 1000);
        return format.format(date);
    }

}
