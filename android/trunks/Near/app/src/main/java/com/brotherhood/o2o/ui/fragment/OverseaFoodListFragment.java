package com.brotherhood.o2o.ui.fragment;

import android.text.TextUtils;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.nearby.FoodListItem;
import com.brotherhood.o2o.cache.impl.SerializationCache;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.OverseaFoodListRequest;
import com.brotherhood.o2o.ui.adapter.FoodListAdapter;
import com.brotherhood.o2o.ui.fragment.base.PullToRefreshFragment;
import com.brotherhood.o2o.ui.widget.account.PhotoDecoration;
import com.brotherhood.o2o.util.DisplayUtil;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jl.zhang on 2015/12/28.
 * 海外版美食列表
 */
public class OverseaFoodListFragment extends PullToRefreshFragment<FoodListItem, FoodListAdapter> {

    private static final String TAG = "OverseaFoodListFragment";

    private FoodListAdapter mAdapter;

    private OverseaFoodListRequest mFoodListRequest;
    private int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 10;

    private SerializationCache mCache;
    private boolean loadCache = true;


    @Override
    public void loadData() {
        mRecyclerView.attachOnScrollCallback(this);
        mRecyclerView.addItemDecoration(new PhotoDecoration(1, DisplayUtil.dp2px(10), true));
        super.loadData();
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
                    if (data == null) {
                        continue;
                    }
                    if (changeBusinessId.equals(data.mBusinessId)) {
                        if (data.mCollection == 0) {
                            data.mCollection = 1;
                        } else {
                            data.mCollection = 0;
                        }
                        if (mAdapter != null)
                            mAdapter.notifyItemChanged(i);
                    }
                }
                break;
        }
    }

    @Override
    protected FoodListAdapter createAdapter() {
        mAdapter = new FoodListAdapter(getActivity(), mListData);
        return mAdapter;
    }

    @Override
    protected void updateRequestParams() {
        PAGE_NUMBER++;
        mCurrentPageIndex = PAGE_NUMBER;
    }

    @Override
    protected void initRequestParams() {
        if (mCache != null){
            //mCache.asyncClear();
            mCache.clear();
        }
        loadCache = false;
        PAGE_NUMBER = 1;
        mCurrentPageIndex = 1;
    }

    @Override
    protected void init() {
        mStateView.setViewForState(R.layout.nearby_food_empty_view, MultiStateView.VIEW_STATE_EMPTY);
        mStateView.setViewForState(R.layout.loading_view_dark, MultiStateView.VIEW_STATE_LOADING);
        mStateView.setViewForState(R.layout.error_view_dark, MultiStateView.VIEW_STATE_ERROR);
        File dir = DirManager.getExternalStroageDirFile(Constants.HTTP_CACHE + "/" + TAG);
        if (dir != null){
            mCache = new SerializationCache(dir);
        }
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        getFoodList();
    }

    /**
     * 获取周边美食列表
     */
    private void getFoodList() {

        getCaChe();
        if (!loadCache){
            //LocationInfo myLocationInfo = LocationManager.getInstance().getCachedCurrentAddressOrNil();
            LatLng myLocationInfo = LocationManager.getInstance().getMyLatlng();

            if (myLocationInfo == null) {
                LocationManager.getInstance().updateCurrentAddress();
                LogManager.e("================当前位置信息缓存为null===============" + getClass().getName());
                return;
            }
            LogManager.d("=================latitude:" + myLocationInfo.latitude + "===longitude:" + myLocationInfo.longitude);
            mFoodListRequest = OverseaFoodListRequest.createFoodListRequest(myLocationInfo.longitude, myLocationInfo.latitude, PAGE_NUMBER, PAGE_SIZE, null, new
                    OnResponseListener<List<FoodListItem>>() {
                        @Override
                        public void onSuccess(int code, String msg, List<FoodListItem> foodListItems, boolean cache) {
                            changeData((ArrayList<FoodListItem>)foodListItems);
                            //if (foodListItems == null || foodListItems.isEmpty()) {
                            //    loadSuccessWithPage(MultiStateView.VIEW_STATE_EMPTY, false, null);
                            //    return;
                            //}
                            //boolean hasNext = true;
                            //if (foodListItems.size() < PAGE_SIZE) {
                            //    hasNext = false;
                            //}
                            //loadSuccessWithPage(MultiStateView.VIEW_STATE_CONTENT, hasNext, foodListItems);

                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            loadSuccessWithPage(MultiStateView.VIEW_STATE_ERROR, false, null);
                        }
                    });
            mFoodListRequest.sendRequest();
        }
    }

    public void changeData(ArrayList<FoodListItem> foodListItems) {
        if (foodListItems == null || foodListItems.isEmpty()) {
            loadSuccessWithPage(MultiStateView.VIEW_STATE_EMPTY, false, null);
            return;
        }
        boolean hasNext = true;
        if (foodListItems.size() < PAGE_SIZE) {
            hasNext = false;
        }
        if (!loadCache){
            saveCaChe(foodListItems);
        }
        loadSuccessWithPage(MultiStateView.VIEW_STATE_CONTENT, hasNext, foodListItems);
    }

    /**
     * 获取缓存
     */
    private void getCaChe() {
        if (mCache == null){
            loadCache = false;
            return;
        }
        ArrayList<FoodListItem> foodListItems = (ArrayList<FoodListItem>) mCache.get(String.valueOf(PAGE_NUMBER));
        if (foodListItems == null && PAGE_NUMBER == 1){
            loadCache = false;
        }
        if (loadCache){
            changeData(foodListItems);
        }
    }

    /**
     * 写入缓存
     * @param foodListItems
     */
    private void saveCaChe(ArrayList<FoodListItem> foodListItems) {
        if (mCache == null){
            return;
        }
        mCache.asyncPut(String.valueOf(PAGE_NUMBER), foodListItems);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFoodListRequest != null) {
            mFoodListRequest.cancel();
        }
    }
}
