package com.brotherhood.o2o.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.brotherhood.o2o.controller.SwLinController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laimo.li on 2016/1/18.
 */
public abstract class SwLinRecylerAdatper<T, VH extends SwLinViewHolder> extends RecyclerView.Adapter<VH> {

    protected SwLinController mSwLinController;

    protected Context mContext;

    protected List<T> mList;

    public SwLinRecylerAdatper(Context context) {
        this.mContext = context;
        mSwLinController = new SwLinController();
        setHasStableIds(true);
        mList = new ArrayList<T>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
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
        if(mList.isEmpty()){

        }
    }

}
