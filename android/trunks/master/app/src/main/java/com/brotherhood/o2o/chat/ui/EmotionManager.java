package com.brotherhood.o2o.chat.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.brotherhood.o2o.R;


public class EmotionManager implements View.OnClickListener {

	private Context mContext;
	private ViewPager mViewPager;
	private LinearLayout mPagerIndexPanel;
	private NativePagerAdapter mAdapter;
	private NativeOnPageChangeListener mPageChangeListener;

	private static int[] mEmotionResIds;
	private static String[] mEmotionDescs;
	private ArrayList<ImageView> mIndexViews;
	private int mCurrPageIndex;
	private Resources mResources;

	/**
	 * Callback interface to observe events when user interacts with the emotion
	 * panel.
	 */
	public static interface EmotionListener {

		/**
		 * Invoked when a specified emotion is selected by the user.
		 * 
		 * @param resId
		 *            The resource id of the emotion.
		 * @param desc
		 *            The description of the emotion.
		 */
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

	private static final int DEFAULT_COLUMN_COUNT = 7;
	private static final int DEFAULT_ROW_COUNT = 3;

	private int mColumnCount = DEFAULT_COLUMN_COUNT;
	private int mRowCount = DEFAULT_ROW_COUNT;

	public void setColumnCountPerPage(int columnCount) {
		mColumnCount = columnCount;
	}

	public void setRowCountPerPage(int rowCount) {
		mRowCount = rowCount;
	}

	private void initializeEmoji(Context context) {
		if (mEmotionDescs != null && mEmotionResIds != null) {
			return;
		}
		Resources res = mResources;
		String pkg = context.getPackageName();

		String[] emojiNames = res.getStringArray(R.array.chat_emoji_names);
		int len = emojiNames.length;
		int[] array = new int[len];
		for (int i = 0; i < len; i++) {
			array[i] = res.getIdentifier(emojiNames[i], "drawable", pkg);
		}
		mEmotionResIds = array;
		mEmotionDescs = res.getStringArray(R.array.chat_emoji_desc);
	}

	private void initializeEmoji() {
		mIndexViews = new ArrayList<ImageView>();
		Context cxt = mContext;
		Resources res = mResources;
		int margin = res
				.getDimensionPixelSize(R.dimen.chat_emo_page_index_padding);
		ArrayList<ImageView> indexViews = mIndexViews;

		int totalPages = (int) Math.ceil(mEmotionResIds.length
				/ (double) (mRowCount * mColumnCount));
		for (int i = 0; i < totalPages; i++) {
			ImageView index = new ImageView(cxt);
			LayoutParams params = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = margin;
			index.setLayoutParams(params);
			indexViews.add(index);
		}
	}

	public void setPagerIndex(int pos) {
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

		nowIndex.setImageResource(R.drawable.page_now);

		if (prevIndex != null) {
			prevIndex.setImageResource(R.drawable.page);
		}
		if (nextIndex != null) {
			nextIndex.setImageResource(R.drawable.page);
		}
	}

	public void initialize() {
		mViewPager.setAdapter(mAdapter);
		for (ImageView imageView : mIndexViews) {
			imageView.setImageResource(R.drawable.page);
			mPagerIndexPanel.addView(imageView);
		}
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(mCurrPageIndex);
		ImageView index = mIndexViews.get(mCurrPageIndex);
		index.setImageResource(R.drawable.page_now);
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
			ArrayList<ImageView> emotionViews;
		}

		public NativePagerAdapter() {
			mPages = new SparseArray<ViewHolder>();
		}

		@Override
		public int getCount() {
			return mEmotionResIds.length / (mRowCount * mColumnCount);
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

		private ViewHolder instanceView(int position) {
			ViewHolder viewHolder = new ViewHolder();
			int countPerPage = (mRowCount * mColumnCount);
			int baseIndex = position * countPerPage;

			View rootView = LayoutInflater.from(mContext).inflate(
					R.layout.chat_emotion_pager_item, (ViewGroup) null);

			ArrayList<ImageView> emotions = new ArrayList<ImageView>(
					countPerPage);
			String pkg = mContext.getPackageName();
			Resources res = mResources;
			EmotionManager em = EmotionManager.this;
			for (int i = 0; i < countPerPage; i++) {
				ImageView emotion = (ImageView) rootView.findViewById(res
						.getIdentifier("emo_" + (i + 1), "id", pkg));
				emotion.setOnClickListener(em);
				emotion.setTag(Integer.valueOf(baseIndex + i));
				emotions.add(emotion);
			}

			viewHolder.emotionViews = emotions;
			viewHolder.rootView = rootView;
			return viewHolder;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ViewHolder viewHolder = mPages.get(position);
			if (viewHolder == null) {
				viewHolder = instanceView(position);
				mPages.put(position, viewHolder);
			}
			int emoCount = mEmotionResIds.length;
			int countPerPage = mRowCount * mColumnCount;

			ArrayList<ImageView> emotions = viewHolder.emotionViews;
			int offset = position * countPerPage;
			for (int i = offset; i < emoCount
					&& i < (position + 1) * countPerPage; i++) {
				ImageView emotion = emotions.get(i - offset);
				emotion.setImageResource(mEmotionResIds[i]);
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
		String[] descs = mEmotionDescs;
		int len = descs.length;
		for (int i = 0; i < len; i++) {
			if (descs[i].equals(message)) {
				return mEmotionResIds[i];
			}
		}
		return -1;
	}

	@Override
	public void onClick(View v) {
		Integer tag = (Integer) v.getTag();
		if (tag != null) {
			int index = tag.intValue();
			if (mListener != null) {
				String desc = mEmotionDescs[index];
				if (desc.equals("[DEL]")) {
					mListener.onBackspacePressed();
				} else {
					mListener.onEmotionSelected(mEmotionResIds[index],
							mEmotionDescs[index]);
				}
			}
		}
	}
}
