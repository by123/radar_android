package com.brotherhood.o2o.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.brotherhood.o2o.controller.SwLinController;

import java.util.List;

/**
 * Created by laimo.li on 2016/1/18.
 */
public abstract class SwLinLoadMoreRecylerAdatper<T, VH extends SwLinViewHolder> extends LoadMoreRecylerAdatper<T, VH> {

    protected SwLinController mSwLinController;

    public SwLinLoadMoreRecylerAdatper(List<T> list) {
        super(list);
        mSwLinController = new SwLinController();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof SwLinViewHolder) {
            mSwLinController.put(position, ((SwLinViewHolder) holder).swLinLayout);
        }
    }

    protected void remove(int position) {
        if (position == -1) {
            return;
        }
        mSwLinController.showMainLayout();
        mList.remove(position);
        notifyDataSetChanged();
        isShowFootHint();
    }


}
