package com.brotherhood.o2o.ui.widget.account;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 选择图片界面recyclerview分割线
 * Created by jl.zhang on 2015/12/17.
 */

public class UserDetailDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;          //列数
    private boolean includeEdge;    //是否包含边缘
    private int leftSpace;          //左
    private int topSpace;           //上
    private int rightSpace;         //右
    private int bottomSpace;        //下

    public UserDetailDecoration(int spanCount, int leftSpace, int topSpace, int rightSpace, int bottomSpace, boolean includeEdge) {
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
        this.leftSpace = leftSpace;
        this.topSpace = topSpace;
        this.rightSpace = rightSpace;
        this.bottomSpace = bottomSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {//包含边缘
            outRect.left = leftSpace - column * leftSpace / spanCount;
            outRect.right = (column + 1) * rightSpace / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
        } else {//不包含边缘
            outRect.left = column * leftSpace / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = rightSpace - (column + 1) * rightSpace / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
        }
        outRect.top = topSpace;
        outRect.bottom = bottomSpace; // item bottom
    }
}
