package com.brotherhood.o2o.lib.baseRecyclerAdapterHelper;

/**
 * Created with Android Studio.
 * <p/>
 * Author:Lw
 * <p/>
 * Data:2015/8/27.
 */

public interface MultiItemTypeSupport<T> {

    /**
     * 设置ViewTyp相对应的布局文件
     * @param viewType
     * @return
     */
    int getLayoutId(int viewType);

    /**
     * 获取ViewTyp
     * @param position
     * @param t
     * @return
     */
    int getItemViewType(int position, T t);
}
