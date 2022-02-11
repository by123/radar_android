package com.brotherhood.o2o.ui.widget.radar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by by.huang on 2015/6/26.
 */
public class ScrollText extends ListView {

    private static int TextHeight = Utils.sp2px(40) * 2 + 20;

    public ScrollText(Context context) {
        super(context);
        initView();
    }

    public ScrollText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScrollText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        ArrayList<DataBean> datas = new ArrayList<DataBean>();
        for (int i = 0; i < 10; i++) {
            datas.add(new DataBean(i + "", false));
        }
        setAdapter(new ScrollTextAdapter(datas));
        setParams();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthMeasureSize, TextHeight);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setParams() {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setDivider(null);
        setDividerHeight(0);
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    private class ScrollTextAdapter extends BaseAdapter {
        private ArrayList<DataBean> datas;
        private int size = 0;

        public ScrollTextAdapter(ArrayList<DataBean> datas) {
            this.datas = datas;
            if (datas != null && datas.size() > 0) {
                size = datas.size();
            }
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(getContext()).inflate(R.layout.scrolltext_item, null);
                holder.mTextView = (TextView) view;
                holder.mTextView.setIncludeFontPadding(false);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            DataBean data = datas.get(i);
            holder.mTextView.setText(data.data);
            return view;
        }

        class ViewHolder {
            TextView mTextView;
        }
    }


    private int current = 0;

    public void start() {
        handler.postDelayed(runnable, 1000);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            smoothScrollToPositionFromTop(current, 0);
            current++;
            start();
        }
    };


    private class DataBean {
        String data;
        boolean show;

        public DataBean(String data, boolean show) {
            this.data = data;
            this.show = show;
        }
    }
}
