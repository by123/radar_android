package com.brotherhood.o2o.chat.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.model.EmojiBean;
import com.brotherhood.o2o.chat.ui.adapter.EmojiAdpter;

import java.util.ArrayList;
import java.util.List;

public class EmotionManager {

    public static final String DEL_DESC = "[DEL]";
    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mPagerIndexPanel;
    private NativePagerAdapter mAdapter;
    private NativeOnPageChangeListener mPageChangeListener;

    private static ArrayList<EmojiBean> mEmojiArray;
    private ArrayList<ImageView> mIndexViews;
    private int mCurrPageIndex;
    private Resources mResources;

    public static interface EmotionListener {

        public void onEmotionSelected(int resId, String desc);

        public void onBackspacePressed();
    }

    public EmotionManager(Context context, ViewPager viewPager,
                          LinearLayout indexPanel) {
        mContext = context;
        mResources = context.getResources();
        mViewPager = viewPager;
        mPagerIndexPanel = indexPanel;

        initializeEmoji(context);
        initializeEmoji();

        mPageChangeListener = new NativeOnPageChangeListener();
        mAdapter = new NativePagerAdapter();
    }

    private EmotionListener mListener;

    public void setEmotionListener(EmotionListener listener) {
        mListener = listener;
    }

    private static final int COLUMN_COUNT = 6;
    private static final int ROW_COUNT = 3;

    private void initializeEmoji(Context context) {
        Resources res = mResources;
        String pkg = context.getPackageName();
        String[] emojiNames = res.getStringArray(R.array.chat_emoji_names);
        String[] desc = res.getStringArray(R.array.chat_emoji_desc);
        int len = emojiNames.length;
        mEmojiArray = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            EmojiBean bean = new EmojiBean();
            bean.resId = res.getIdentifier(emojiNames[i], "mipmap", pkg);
            bean.des = desc[i];
            mEmojiArray.add(bean);
        }
    }

    private void initializeEmoji() {
        mIndexViews = new ArrayList<ImageView>();
        Context cxt = mContext;
        Resources res = mResources;
        int margin = res
                .getDimensionPixelSize(R.dimen.chat_emo_page_index_padding);
        ArrayList<ImageView> indexViews = mIndexViews;

        int totalPages = (int) Math.ceil(mEmojiArray.size()
                / (double) (ROW_COUNT * COLUMN_COUNT));
        for (int i = 0; i < totalPages; i++) {
            ImageView index = new ImageView(cxt);
            LayoutParams params = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = margin;
            index.setLayoutParams(params);
            indexViews.add(index);
        }
    }

    private void setPagerIndex(int pos) {
        ImageView nowIndex = null;
        ImageView prevIndex = null;
        ImageView nextIndex = null;
        if (pos < 0 || pos >= mIndexViews.size()) {
            return;
        }
        mCurrPageIndex = pos;
        nowIndex = mIndexViews.get(pos);
        prevIndex = pos > 0 ? mIndexViews.get(pos - 1) : null;
        nextIndex = pos < (mIndexViews.size() - 1) ? mIndexViews.get(pos + 1)
                : null;

        nowIndex.setImageResource(R.mipmap.page_now);

        if (prevIndex != null) {
            prevIndex.setImageResource(R.mipmap.page);
        }
        if (nextIndex != null) {
            nextIndex.setImageResource(R.mipmap.page);
        }
    }

    public void initialize() {
        mViewPager.setAdapter(mAdapter);
        for (ImageView imageView : mIndexViews) {
            imageView.setImageResource(R.mipmap.page);
            mPagerIndexPanel.addView(imageView);
        }
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(mCurrPageIndex);
        ImageView index = mIndexViews.get(mCurrPageIndex);
        index.setImageResource(R.mipmap.page_now);
    }

    public void destroy() {
        mViewPager.setAdapter(null);
        mViewPager.removeAllViews();
        mPagerIndexPanel.removeAllViews();
        mViewPager.setOnPageChangeListener(null);
    }

    private class NativePagerAdapter extends PagerAdapter {

        private SparseArray<ViewHolder> mPages;

        private class ViewHolder {
            View rootView;
        }

        public NativePagerAdapter() {
            mPages = new SparseArray<ViewHolder>();
        }

        @Override
        public int getCount() {
            int len = mEmojiArray.size();
            if (len % (ROW_COUNT * COLUMN_COUNT) > 0) {
                return len / (ROW_COUNT * COLUMN_COUNT) + 1;
            } else {
                return len / (ROW_COUNT * COLUMN_COUNT);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ViewHolder viewHolder = mPages.get(position);
            if (viewHolder != null) {
                ((ViewPager) container).removeView(viewHolder.rootView);
            }
        }

        private ViewHolder instanceView(int position, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            int countPerPage = (ROW_COUNT * COLUMN_COUNT);
            int baseIndex = position * countPerPage;

            GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(
                    R.layout.chat_emotion_pager_item, parent, false);

            int total = mEmojiArray.size();
            int start = baseIndex;
            int end = (start + countPerPage) > total ? total
                    : (start + countPerPage);
            final List<EmojiBean> list = mEmojiArray.subList(start, end);
            EmojiAdpter emojiAdpter = new EmojiAdpter(mContext, list);
            gridView.setNumColumns(COLUMN_COUNT);
            gridView.setAdapter(emojiAdpter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EmojiBean bean = list.get(position);
                    if (bean.des.equals(EmotionManager.DEL_DESC)) {
                        mListener.onBackspacePressed();
                    } else {
                        mListener.onEmotionSelected(bean.resId, bean.des);
                    }
                }
            });
            viewHolder.rootView = gridView;
            return viewHolder;
        }


        @Override
        public Object instantiateItem(View container, int position) {
            ViewHolder viewHolder = mPages.get(position);
            if (viewHolder == null) {
                viewHolder = instanceView(position, ((ViewGroup) container));
                mPages.put(position, viewHolder);
            }
            ((ViewGroup) container).addView(viewHolder.rootView);
            return viewHolder.rootView;
        }

    }

    private class NativeOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int pos) {
            setPagerIndex(pos);
        }
    }

    public static int getEmotionResourceId(String message) {
        for (EmojiBean bean : mEmojiArray) {
            if (bean.des.equals(message)) {
                return bean.resId;
            }
        }
        return -1;
    }
}
