package com.brotherhood.o2o.ui.widget.nearby;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;

import java.util.List;

public class SearchHistoryView extends LinearLayout {

    private Context mContext;
    private LinearLayout mLlKeyRoot;
    private LinearLayout mHistoryContainer;
    private RelativeLayout mRlRemove;
    private TextView mTvKeyEmpty;
    private SearchHistoryCallBack mCallBack;

    public SearchHistoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public SearchHistoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchHistoryView(Context context) {
        this(context, null);
    }

    private void init() {
        View.inflate(mContext, R.layout.search_food_keyword_view, this);
        mLlKeyRoot = (LinearLayout) findViewById(R.id.llKeywordList);
        mHistoryContainer = (LinearLayout) findViewById(R.id.llSearchKeyItemContainer);
        mRlRemove = (RelativeLayout) findViewById(R.id.rlKeyRemove);
        mTvKeyEmpty = (TextView) findViewById(R.id.tvKeywordempty);
    }


    public void showEmptyKey() {
        mLlKeyRoot.setVisibility(View.GONE);
        mTvKeyEmpty.setVisibility(View.VISIBLE);
    }

    public void showKeyList() {
        mLlKeyRoot.setVisibility(View.VISIBLE);
        mTvKeyEmpty.setVisibility(View.GONE);
    }

    public void removeKeyList() {
        mHistoryContainer.removeAllViews();
    }

    public void showHistoryView() {
        if (mHistoryContainer.getChildCount() == 0) {
            showEmptyKey();
        } else {
            showKeyList();
        }
    }

    /**
     * 填充搜索记录数据
     *
     * @param historys
     */
    public void setSearchHistoryData(List<String> historys) {
        mHistoryContainer.removeAllViews();
        if (historys == null || historys.size() == 0) {
            mLlKeyRoot.setVisibility(View.GONE);
            mTvKeyEmpty.setVisibility(View.VISIBLE);
            return;
        }

        HistoryClick click = new HistoryClick();
        mLlKeyRoot.setVisibility(View.VISIBLE);
        mTvKeyEmpty.setVisibility(View.GONE);
        for (int i = 0; i < historys.size(); i++) {
            String keyWord = historys.get(i);
            View view = createHistoryItem();
            LinearLayout historyItem = (LinearLayout) view.findViewById(R.id.llSearchKeyItem);
            TextView historyText = (TextView) view.findViewById(R.id.tvSearchKeyItem);
            View historyDivide = view.findViewById(R.id.viSearchKeyDivide);
            historyText.setText(keyWord);
            historyItem.setTag(keyWord);
            historyItem.setOnClickListener(click);
            if (i == historys.size() - 1){
                historyDivide.setVisibility(View.GONE);
            }
            mHistoryContainer.addView(view);
        }
        mRlRemove.setOnClickListener(new emptyClick());
    }

    private View createHistoryItem() {
        View view = View.inflate(mContext, R.layout.search_food_key_item, null);
        return view;
    }

    public class emptyClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (mCallBack != null)
                mCallBack.emptySearchHistoty();
        }

    }

    public class HistoryClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            mCallBack.historySearch((String) view.getTag());
        }
    }

    public void setCallBack(SearchHistoryCallBack callBack) {
        this.mCallBack = callBack;
    }

    public interface SearchHistoryCallBack {
        void historySearch(String key);

        void emptySearchHistoty();
    }
}
