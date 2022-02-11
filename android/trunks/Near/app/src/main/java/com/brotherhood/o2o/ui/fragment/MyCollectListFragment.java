package com.brotherhood.o2o.ui.fragment;

import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.bean.MyCollectBean;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.StateViewManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.GetMyCollectListRequest;
import com.brotherhood.o2o.ui.adapter.MyCollectListAdapter;
import com.brotherhood.o2o.ui.fragment.base.LoadMoreFragment;
import com.brotherhood.o2o.ui.widget.ColorfulToast;

import java.util.List;

/**
 * Created by laimo.li on 2016/1/6.
 */
public class MyCollectListFragment extends LoadMoreFragment<MyCollectBean, MyCollectListAdapter> {

    public static final String MY_COLLECT_TYPE = "my_collect_type";

    private MyCollectListAdapter adapter;

    public static final int LIMIT = 15;

    private int OFFSET = 0;

    private int TYPE = 2; //收藏类型：1、想玩，2、想吃，3、想看（可选，默认为1）


    public static MyCollectListFragment newInstance() {
        return new MyCollectListFragment();
    }

    @Override
    protected MyCollectListAdapter createAdapter() {
        adapter = new MyCollectListAdapter(getActivity(), mListData,mStateView, TYPE);
        return adapter;
    }

    @Override
    protected void updateRequestParams() {
        mCurrentPageIndex++;
    }

    @Override
    protected void initRequestParams() {
        mCurrentPageIndex = 1;
        OFFSET = 0;
    }


    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        getMyCollectList();
    }

    @Override
    protected void init() {
        mRecyclerView.attachOnScrollCallback(this);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mStateView.setViewForState(StateViewManager.getEmpty(getContext(), R.string.no_collect, R.mipmap.ic_activity_no_favour_big_normal), MultiStateView.VIEW_STATE_EMPTY);
        if (getArguments() != null) {
            TYPE = getArguments().getInt(MY_COLLECT_TYPE);
        }


    }


    private void getMyCollectList() {
        GetMyCollectListRequest request = GetMyCollectListRequest.createMyCollectListRequest(LIMIT, OFFSET, TYPE, new OnResponseListener<List<MyCollectBean>>() {
            @Override
            public void onSuccess(int code, String msg, List<MyCollectBean> myCollectBeans, boolean cache) {
                boolean hasNext = true;
                if (myCollectBeans.size() < LIMIT) {
                    hasNext = false;
                }
                loadSuccessWithPage(MultiStateView.VIEW_STATE_CONTENT, hasNext, myCollectBeans);
                OFFSET = +myCollectBeans.size();
            }

            @Override
            public void onFailure(int code, String msg) {
                ColorfulToast.orange(getContext(), msg, Toast.LENGTH_SHORT);
                showErrorView();
            }
        });
        request.sendRequest();
    }

    @Override
    protected void attachAllMessage() {
        super.attachAllMessage();
        attachMessage(Message.Type.OVERSEA_FOOD_COLLECT_CHANGE);
    }

    @Override
    public void onReceiveMessage(Message message) {
        super.onReceiveMessage(message);
        if (message.type == Message.Type.OVERSEA_FOOD_COLLECT_CHANGE) {
            //String id = (String) message.data;
            //if (!TextUtils.isEmpty(id)) {
            //    adapter.remove(id);
            //}
            mCurrentPageIndex = 1;
            OFFSET = 0;
            loadData(true);
        }
    }


}
