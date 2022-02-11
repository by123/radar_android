package com.brotherhood.o2o.chat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.model.EmojiBean;

import java.util.List;

/**
 * Created by Administrator on 2015/12/30 0030.
 */
public class EmojiAdpter extends BaseAdapter {

    private List<EmojiBean> mEmojiArray;
    private Context context;


    public EmojiAdpter(Context context, List<EmojiBean> emojiIds) {
        this.context = context;
        this.mEmojiArray = emojiIds;
    }

    @Override
    public int getCount() {
        return mEmojiArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.chat_emoji_item, parent, false);
            holder = new Holder();
            holder.iv_emoji = (ImageView) convertView.findViewById(R.id.iv_emoji);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        EmojiBean bean = mEmojiArray.get(position);
        holder.iv_emoji.setImageResource(bean.resId);
        return convertView;
    }

    private class Holder {
        ImageView iv_emoji;
    }
}
