package com.brotherhood.o2o.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.util.DisplayUtil;

/**
 * 通用横向recyclerview分割线
 * Created by jl.zhang on 2015/12/17.
 */

public class CommonHorizontalDecoration extends RecyclerView.ItemDecoration {

    private int edge;          //边缘
    private int left;          //左
    private int top;           //上
    private int right;         //右
    private int bottom;        //下

    public CommonHorizontalDecoration(int edge, int left, int top, int right, int bottom) {
        this.edge = edge;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position
        int childCount = parent.getAdapter().getItemCount();

        if (position == 0) {
            outRect.left = DisplayUtil.dp2px(edge);
        } else {
            outRect.left = DisplayUtil.dp2px(left);
        }
        if (position == childCount - 1) {
            outRect.right = DisplayUtil.dp2px(edge);
        } else {
            outRect.right = DisplayUtil.dp2px(right);
        }
        outRect.top = top;
        outRect.bottom = bottom;
    }
}
