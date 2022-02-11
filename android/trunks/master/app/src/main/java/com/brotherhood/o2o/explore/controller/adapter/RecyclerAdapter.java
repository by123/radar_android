package com.brotherhood.o2o.explore.controller.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.model.RadarItemBean;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by by.huang on 2015/7/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<RadarItemBean> mDatas;
    private int width = 0;
    private OnRecyclerItemListener mListener;

    public RecyclerAdapter(List<RadarItemBean> datas) {
        this.mDatas = datas;
        width = Utils.dip2px(120);
    }

    public void UpdateDatas(List<RadarItemBean> datas) {
        this.mDatas = datas;
    }

    public List<RadarItemBean> getDatas() {
        return mDatas;
    }

    public void setOnItemClickListener(OnRecyclerItemListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radar_recylerview_item, parent, false);
        ViewHolder holder = new ViewHolder(view, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RadarItemBean data = mDatas.get(position);
        ImageLoader.getInstance().setImageUrl(holder.mHeadImg, data.mAvatarUrl, 1, null, width, width);
        if(position == 0)
        {
            ByLogout.out("aaaaa->"+data.mAvatarUrl);
        }
        holder.mNameTxt.setText(data.mName);
        holder.mDistanceTxt.setText(data.mDistance + "m");

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView mHeadImg;
        private TextView mNameTxt;
        private TextView mDistanceTxt;

        public ViewHolder(View view, OnRecyclerItemListener listener) {
            super(view);
            mHeadImg = (SimpleDraweeView) view.findViewById(R.id.img_head);
            mNameTxt = (TextView) view.findViewById(R.id.txt_name);
            mDistanceTxt = (TextView) view.findViewById(R.id.txt_distance);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.OnItemClick(view, getPosition());
            }
        }
    }

    public interface OnRecyclerItemListener {
        void OnItemClick(View view, int postion);
    }

}
