package com.brotherhood.o2o.ui.widget.radar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * Created by by.huang on 2015/8/6.
 */
public class ScrollCallbackView extends ScrollView {

    public ScrollCallbackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScrollCallbackView(Context context) {
        super(context);
    }

    public ScrollCallbackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mListener!=null)
        {
            mListener.OnScroll(t);
        }
    }

    private OnScrollListener mListener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.mListener = listener;
    }

    public interface OnScrollListener {
        void OnScroll(int top);
    }
}
