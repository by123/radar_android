package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.bean.nearby.FoodListItem;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.OverseaFoodListRequest;
import com.brotherhood.o2o.ui.activity.base.PullToRefreshActivity;
import com.brotherhood.o2o.ui.adapter.FoodListAdapter;
import com.brotherhood.o2o.ui.widget.BaseRecyclerView;
import com.brotherhood.o2o.ui.widget.account.PhotoDecoration;
import com.brotherhood.o2o.ui.widget.nearby.SearchFoodEditView;
import com.brotherhood.o2o.ui.widget.nearby.SearchHistoryView;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.FastBlur;
import com.brotherhood.o2o.util.SearchHistoryUtil;
import com.brotherhood.o2o.util.ViewUtil;

import java.util.List;

/**
 * 海外版美食搜索详情
 * <p/>
 * 进入，1、有关键字  关键字列表  2、无关键字  无关键字布局
 * 搜索结果  1、有结果   2、无结果
 */
public class OverseaSearchFoodActivity extends PullToRefreshActivity<FoodListItem, FoodListAdapter> {

    @ViewInject(id = R.id.vsSearchResultEmpty)
    private ViewStub mVsResultEmpty;

    private LinearLayout mLlSearchEmpty;

    @ViewInject(id = R.id.searchFoodHead)
    private SearchFoodEditView mSearchFoodEditView;

    @ViewInject(id = R.id.searchKeyHistory)
    private SearchHistoryView mSearchHistoryView;

    @ViewInject(id = R.id.rvBaseRecycler)
    protected BaseRecyclerView mChildRecyclerView;

    @ViewInject(id = R.id.stateView)
    protected MultiStateView mChildStateView;

    private FoodListAdapter mResultAdapter;

    private OverseaFoodListRequest mFoodListRequest;
    private int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 10;
    private String mKeyWord;

    private boolean isBusy = false;//是否正在搜索

    private int mDrawableId;
    private BitmapDrawable mBlurDrawable;
    private View mViBlur;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_food_layout;
    }

    public static void show(Context context, @DrawableRes int drawableId) {
        Intent it = new Intent(context, OverseaSearchFoodActivity.class);
        it.putExtra(BundleKey.SEARCH_FOOD_BACKGROUD_ID, drawableId);
        context.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawableId = getIntent().getIntExtra(BundleKey.SEARCH_FOOD_BACKGROUD_ID, 0);
        if (mDrawableId != 0){
            mViBlur = getWindow().getDecorView();
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(mDrawableId);
            Bitmap bitmap = drawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()){
                mBlurDrawable = new BitmapDrawable(getResources(), FastBlur.blur(bitmap, mViBlur));
            }
            if (mBlurDrawable != null){
                ViewUtil.setViewBackground(mViBlur, mBlurDrawable);
            }
            ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                view.setBackgroundColor(getResources().getColor(R.color.food_list_bg_color));
            }
        }
        dealSearchKey();
        //头部控件(返回、输入框、搜索功能)
        mSearchFoodEditView.setCallBack(new SearchFoodEditView.CallBack() {
            @Override
            public void back() {//返回
                finish();
            }

            @Override
            public void search(String keyWord) {//搜索
                SearchHistoryUtil.addSearchHistory(keyWord);
                initRequestParams();
                mListData.clear();
                mKeyWord = keyWord;
                loadData(true);
            }

            @Override
            public void editTextFocused() {//输入框获得焦点
                mSearchHistoryView.showHistoryView();
            }
        });

    }

    @Override
    protected void attachAllMessage() {
        attachMessage(Message.Type.OVERSEA_FOOD_COLLECT_CHANGE);
    }

    @Override
    public void onReceiveMessage(Message message) {
        switch (message.type) {
            case OVERSEA_FOOD_COLLECT_CHANGE://搜藏数据发生变化
                if (mListData == null || mListData.isEmpty()) {
                    return;
                }
                String changeBusinessId = (String) message.data;
                if (TextUtils.isEmpty(changeBusinessId)) {
                    return;
                }
                for (int i = 0; i < mListData.size(); i++) {
                    FoodListItem data = mListData.get(i);
                    if (data == null){
                        continue;
                    }
                    if (changeBusinessId.equals(data.mBusinessId)) {
                        if (data.mCollection == 0) {
                            data.mCollection = 1;
                        } else {
                            data.mCollection = 0;
                        }
                        if (mResultAdapter != null)
                            mResultAdapter.notifyItemChanged(i);
                    }
                }
                break;
        }
    }

    @Override
    protected boolean overideLayout() {
        return true;
    }

    @Override
    protected void overideInit() {
        mRecyclerView = mChildRecyclerView;
        mStateView = mChildStateView;
        mStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        mRecyclerView.attachOnScrollCallback(this);
        mRecyclerView.addItemDecoration(new PhotoDecoration(1, DisplayUtil.dp2px(10), true));
    }

    @Override
    protected FoodListAdapter createAdapter() {
        mResultAdapter = new FoodListAdapter(this, mListData);
        return mResultAdapter;
    }

    @Override
    protected void updateRequestParams() {
        PAGE_NUMBER++;
        mCurrentPageIndex = PAGE_NUMBER;
    }

    @Override
    protected void initRequestParams() {
        PAGE_NUMBER = 1;
        mCurrentPageIndex = 1;
    }

    private void dealSearchKey() {
        /**
         * 搜索记录
         */
        mSearchHistoryView.setCallBack(new SearchHistoryView.SearchHistoryCallBack() {
            @Override
            public void historySearch(String key) {
                mKeyWord = key;
                initRequestParams();
                mListData.clear();
                SearchHistoryUtil.addSearchHistory(mKeyWord);
                mSearchFoodEditView.setEditText(mKeyWord);
                loadData(true);
            }

            @Override
            public void emptySearchHistoty() {
                SearchHistoryUtil.emptySearchHistoty();
                mRecyclerView.setVisibility(View.GONE);

                mSearchHistoryView.showEmptyKey();
                mSearchHistoryView.removeKeyList();
            }
        });
        SearchHistoryUtil.getSearchHistory(new SearchHistoryUtil.OnHistoryResultListener() {
            @Override
            public void result(List<String> historyList) {
                mRecyclerView.setVisibility(View.GONE);
                mSearchHistoryView.setSearchHistoryData(historyList);
            }
        });
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        searchFoodData(mKeyWord);
    }

    /**
     * 获取美食搜索数据  关键字可变，所以每次请求只能重新创建请求对象
     */
    private void searchFoodData(final String keyWord) {
        if (isBusy){
            return;
        }
        isBusy = true;
        LocationInfo myLocationInfo = LocationManager.getInstance().getCachedCurrentAddressOrNil();
        if (myLocationInfo == null) {
            LocationManager.getInstance().updateCurrentAddress();
            return;
        }
        //重新搜索，隐藏无数据界面
        if (mKeyWord != null && mLlSearchEmpty != null && mLlSearchEmpty.getVisibility() == View.VISIBLE){
            mLlSearchEmpty.setVisibility(View.GONE);
        }
        //搜索开始后，隐藏其他界面
        mSearchHistoryView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mFoodListRequest = OverseaFoodListRequest.createFoodListRequest(myLocationInfo.mLongitude, myLocationInfo.mLatitude, PAGE_NUMBER, PAGE_SIZE, keyWord, new
                OnResponseListener<List<FoodListItem>>() {
                    @Override
                    public void onSuccess(int code, String msg, List<FoodListItem> foodListItems, boolean cache) {
                        isBusy = false;
                        if (foodListItems == null || foodListItems.isEmpty()) {
                            if (keyWord != null) {
                                mKeyWord = null;
                                if (mLlSearchEmpty == null){
                                    mVsResultEmpty.inflate();
                                    mLlSearchEmpty = (LinearLayout) findViewById(R.id.llSearchFoodEmpty);
                                }
                                if (mLlSearchEmpty != null && mLlSearchEmpty.getVisibility() != View.VISIBLE){
                                    mLlSearchEmpty.setVisibility(View.VISIBLE);
                                }
                                loadData(false);
                            }
                            return;
                        }
                        //重新搜索，隐藏无数据界面
                        if (mKeyWord != null && mLlSearchEmpty != null && mLlSearchEmpty.getVisibility() == View.VISIBLE){
                            mLlSearchEmpty.setVisibility(View.GONE);
                        }
                        boolean hasNext = true;
                        if (foodListItems.size() < PAGE_SIZE) {
                            hasNext = false;
                        }
                        loadSuccessWithPage(MultiStateView.VIEW_STATE_CONTENT, hasNext, foodListItems);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        isBusy = false;
                        loadSuccessWithPage(MultiStateView.VIEW_STATE_ERROR, false, null);
                    }
                });
        mFoodListRequest.sendRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFoodListRequest != null) {
            mFoodListRequest.cancel();
        }
        if (mBlurDrawable != null){
            if (mBlurDrawable != null){
                ViewUtil.setViewBackground(mViBlur, null);
            }
            mBlurDrawable = null;
        }
    }
}
