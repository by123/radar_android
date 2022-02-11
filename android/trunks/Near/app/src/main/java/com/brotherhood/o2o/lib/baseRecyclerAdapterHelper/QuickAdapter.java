package com.brotherhood.o2o.lib.baseRecyclerAdapterHelper;

import android.content.Context;
import android.view.ViewGroup;

import com.brotherhood.o2o.listener.OnRecyclerItemClickListener;

import java.util.List;

/**
 * <p/>http://www.codekk.com/open-source-project-analysis/detail/Android/hongyangAndroid/BaseAdapterHelper%20%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90
 */

public abstract class QuickAdapter<T> extends BaseQuickAdapter<T,BaseAdapterHelper> {

    public QuickAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public QuickAdapter(Context context, int layoutResId, List<T> data) {
        super(context, layoutResId, data);
    }

    public QuickAdapter(Context context, List<T> data,
                        MultiItemTypeSupport<T> multiItemSupport) {
        super(context, data, multiItemSupport);
    }

    protected BaseAdapterHelper getAdapterHelper(Context context, ViewGroup parent, int layoutResId, int viewType,OnRecyclerItemClickListener listener) {
        if (mMultiItemSupport != null) {
            return BaseAdapterHelper.getViewHolder(context,parent, mMultiItemSupport.getLayoutId(viewType),listener);
        } else {
            return BaseAdapterHelper.getViewHolder(context,parent,layoutResId,listener);
        }
    }

}
