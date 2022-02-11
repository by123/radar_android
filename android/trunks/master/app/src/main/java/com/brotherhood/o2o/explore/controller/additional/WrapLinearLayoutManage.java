package com.brotherhood.o2o.explore.controller.additional;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by by.huang on 2015/7/16.
 */
public class WrapLinearLayoutManage extends LinearLayoutManager {

    public WrapLinearLayoutManage(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        View view = null;
        try {
            view = recycler.getViewForPosition(0);
        } catch (Exception e) {

        }
        if (view != null) {
            measureChild(view, widthSpec, heightSpec);
            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
            int measuredHeight = view.getMeasuredHeight();
            setMeasuredDimension(measuredWidth, measuredHeight);
        }
    }
}