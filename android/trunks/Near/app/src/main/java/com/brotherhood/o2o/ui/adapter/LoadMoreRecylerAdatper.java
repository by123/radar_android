package com.brotherhood.o2o.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brotherhood.o2o.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部加载更多的Adatper
 */
public abstract class LoadMoreRecylerAdatper<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM = -995;
    public static final int FOOTER = -994;

    protected List<T> mList;
    private View.OnClickListener mFooterClickListener;

    public static final int LOAD_ING = 0x8881;
    public static final int LOAD_FINISH = 0x8882;
    public static final int LOAD_ERROR = 0x8883;

    private int mLoadStuts;

    private int initLastPosition = -1;
    private boolean isShowFootHint = true;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case FOOTER:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_list_loading_layout, null);
                holder = new ViewHolder(v, viewType);
                break;
            case ITEM:
                holder = onCreateItemViewHolder(parent);
                break;
        }
        return holder;
    }

    public LoadMoreRecylerAdatper(List<T> list) {
        mList = list;
        if (mList == null)
            mList = new ArrayList<>();

        isShowFootHint = getShowFootView();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM:
                onBindItemViewHolder((VH) holder, getItem(position), position);
                break;
            case FOOTER:
                switch (mLoadStuts) {
                    case LOAD_FINISH:
                        ((LoadMoreRecylerAdatper.ViewHolder) holder).viewLoad.setText(R.string.list_load_nomore);
                        if (isShowFootHint) {
                            ((LoadMoreRecylerAdatper.ViewHolder) holder).viewLoad.setVisibility(View.VISIBLE);
                        } else {
                            ((LoadMoreRecylerAdatper.ViewHolder) holder).viewLoad.setVisibility(View.GONE);
                        }
                        ((LoadMoreRecylerAdatper.ViewHolder) holder).mProgressBar.setVisibility(View.GONE);
                        break;
                    case LOAD_ERROR:
                        ((LoadMoreRecylerAdatper.ViewHolder) holder).viewLoad.setText(R.string.list_load_more);
                        ((LoadMoreRecylerAdatper.ViewHolder) holder).mProgressBar.setVisibility(View.GONE);
                        break;
                    case LOAD_ING:
                        ((LoadMoreRecylerAdatper.ViewHolder) holder).viewLoad.setText(R.string.list_loading);
                        ((LoadMoreRecylerAdatper.ViewHolder) holder).mProgressBar.setVisibility(View.VISIBLE);
                        break;
                }
                if (mFooterClickListener != null) {
                    ((LoadMoreRecylerAdatper.ViewHolder) holder).viewLoad.setOnClickListener(mFooterClickListener);
                }
                break;
        }
    }

    public T getItem(int position) {
        if (position >= mList.size())
            return null;
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size() - 1) {
            return FOOTER;
        } else {
            return ITEM;
        }
    }

    protected boolean getShowFootView() {
        return true;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView viewLoad;
        public ProgressBar mProgressBar;
        public int viewType;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            viewLoad = (TextView) itemView.findViewById(R.id.tvListLoadMore);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.pbList);
        }
    }

    public int getLoadStuts() {
        return this.mLoadStuts;
    }

    public void setLoadStuts(int loadStuts) {
        this.mLoadStuts = loadStuts;
        notifyItemChanged(getItemCount() - 1);
    }

    public void setListData(List<T> list) {
        this.mList = list;
        if (mList == null)
            mList = new ArrayList<>();
        else
            notifyDataSetChanged();
    }

    public void loadSuccess(List<T> content, boolean hasMore) {
        if (mList != null && !mList.isEmpty()){
            mList.addAll(mList.size() - 1, content);
        }
        mLoadStuts = hasMore ? LOAD_ING : LOAD_FINISH;
        notifyDataSetChanged();
    }

    public void loadFailure() {
        mLoadStuts = LOAD_ERROR;
        notifyItemChanged(getItemCount() - 1);
    }

    public void loadEnd() {
        mLoadStuts = LOAD_FINISH;
        notifyItemChanged(getItemCount() - 1);
    }

    public void addFooter(int loadStuts) {
        mLoadStuts = loadStuts;
        mList.add(null);
        notifyDataSetChanged();
    }

    public void isShowFootHint() {
        isShowFootHint(-1);
    }

    public void isShowFootHint(int lastPosition) {
        if (initLastPosition == -1 && lastPosition > -1) {
            initLastPosition = lastPosition;
        }
        boolean show = false;
        if (mList.size() - 1 > initLastPosition) {
            show = true;
        }
        if (isShowFootHint == show) {
            return;
        }
        isShowFootHint = show;
        notifyItemChanged(mList.size() - 1);
    }


    /**
     * Footer点击事件
     *
     * @param clickListener
     */
    public void setOnFooterClickListener(View.OnClickListener clickListener) {
        this.mFooterClickListener = clickListener;
    }

    protected abstract VH onCreateItemViewHolder(ViewGroup parent);

    protected abstract void onBindItemViewHolder(VH holder, T t, int position);
}
