package com.brotherhood.o2o.address.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.address.AddressComponent;
import com.brotherhood.o2o.address.adapter.AddressListAdapter;
import com.brotherhood.o2o.address.adapter.HistoryAddressListAdapter;
import com.brotherhood.o2o.address.helper.LocationHelper;
import com.brotherhood.o2o.address.model.AddressInfo;
import com.brotherhood.o2o.address.model.HistoryAddressInfoStore;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.utils.DialogHelper;
import com.brotherhood.o2o.widget.SearchBar;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class SetAddressActivity extends BaseActivity implements SearchBar.SearchBarDelegate, AdapterView.OnItemClickListener, HistoryAddressListAdapter.Delegate {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.searchBar)
    SearchBar mSearchBar;

    @InjectView(R.id.container_search)
    View mSearchContainer;
    @InjectView(R.id.progress_search)
    ProgressBar mSearchProgressBar;
    @InjectView(R.id.list_search)
    ListView mSearchListView;
    @InjectView(R.id.container_search_error)
    View mSearchErrorContainer;

    @InjectView(R.id.container_near_history)
    View mNearAndHistoryContainer;
    @InjectView(R.id.container_near)
    View mNearContainer;
    @InjectView(R.id.progress_near)
    ProgressBar mNearProgressBar;
    @InjectView(R.id.list_near)
    ListView mNearListView;
    @InjectView(R.id.container_near_error)
    View mNearErrorContainer;
    @InjectView(R.id.container_history)
    View mHistoryContainer;
    @InjectView(R.id.list_history)
    ListView mHistoryListView;
    @InjectView(R.id.btn_near)
    View mNearButton;

    public static void show(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SetAddressActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_act_set_address);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mSearchBar.setDelegate(this);
        mNearListView.setOnItemClickListener(this);
        mSearchListView.setOnItemClickListener(this);
        mNearButton.setFocusable(true);
        mNearButton.setFocusableInTouchMode(true);
        mNearButton.requestFocus();

        //set default visability
        mNearAndHistoryContainer.setVisibility(View.VISIBLE);
        mNearErrorContainer.setVisibility(View.GONE);
        mNearListView.setVisibility(View.GONE);
        mNearProgressBar.setVisibility(View.GONE);
        mSearchContainer.setVisibility(View.GONE);
        mSearchErrorContainer.setVisibility(View.GONE);
        mSearchListView.setVisibility(View.GONE);
        mSearchProgressBar.setVisibility(View.GONE);

        //show history if has data
        HistoryAddressInfoStore store = HistoryAddressInfoStore.shareStore();
        if (store.isHistoryAddresssListEmpty()) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoryContainer.setVisibility(View.VISIBLE);
            HistoryAddressListAdapter adapter = new HistoryAddressListAdapter(mHistoryListView, store.getAddressList());
            adapter.addFooterView(mHistoryListView);
            adapter.setDelegate(this);
            mHistoryListView.setAdapter(adapter);
        }
    }

    @Override
    public void finish() {
        broadcastFinishSelectAddressEvent();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.btn_near)
    void onNearButtonClick() {
        mSearchBar.releaseFocus();
        mNearProgressBar.setVisibility(View.VISIBLE);
        mNearListView.setVisibility(View.GONE);
        mNearErrorContainer.setVisibility(View.GONE);
        mHistoryContainer.setVisibility(View.GONE);
        if (mHistoryListView.getAdapter() != null) {
            mHistoryListView.setAdapter(null);
        }
        LocationHelper.shareManager().searchNearByBuilding(new LocationHelper.Callback() {
            @Override
            public void onFinish(List<AddressInfo> dataSetOrNil, String errorOrNil) {
                if (!TextUtils.isEmpty(errorOrNil)) {
                    DialogHelper.showSimpleErrorDialog(SetAddressActivity.this, errorOrNil);
                } else {
                    assert dataSetOrNil != null;
                    if (dataSetOrNil.isEmpty()) {
                        mNearErrorContainer.setVisibility(View.VISIBLE);
                    } else {
                        AddressListAdapter adapter = new AddressListAdapter(dataSetOrNil);
                        mNearListView.setAdapter(adapter);
                        mNearListView.setVisibility(View.VISIBLE);
                    }
                }
                mNearProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() != null) {
            AddressInfo item = (AddressInfo) parent.getAdapter().getItem(position);
            AddressComponent.shareComponent().setCurrentAddress(item);
            HistoryAddressInfoStore.shareStore().addHistoryAddress(item);
            finish();
        }
    }

    @Override
    public void onTextChanged(SearchBar searchBar, String text) {
        if (TextUtils.isEmpty(text)) {
            mSearchListView.setAdapter(null);
            mNearAndHistoryContainer.setVisibility(View.VISIBLE);
            mSearchContainer.setVisibility(View.GONE);
        } else {
            mNearAndHistoryContainer.setVisibility(View.GONE);
            mSearchContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchButtonClick(SearchBar searchBar, String keyword) {
        mSearchProgressBar.setVisibility(View.VISIBLE);
        mSearchListView.setVisibility(View.GONE);
        mSearchErrorContainer.setVisibility(View.GONE);
        LocationHelper.shareManager().searchBuilding(keyword, new LocationHelper.Callback() {
            @Override
            public void onFinish(List<AddressInfo> dataSetOrNil, String errorOrNil) {
                if (!TextUtils.isEmpty(errorOrNil)) {
                    DialogHelper.showSimpleAlertDialog(SetAddressActivity.this, errorOrNil);
                } else {
                    assert dataSetOrNil != null;
                    if (dataSetOrNil.isEmpty()) {
                        mSearchErrorContainer.setVisibility(View.VISIBLE);
                    } else {
                        AddressListAdapter adapter = new AddressListAdapter(dataSetOrNil);
                        mSearchListView.setAdapter(adapter);
                        mSearchListView.setVisibility(View.VISIBLE);
                    }
                }
                mSearchProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCloseImageClick(HistoryAddressListAdapter adapter, int position) {
        mSearchBar.releaseFocus();
        if (adapter.getCount() == 1) {
            onFooterViewClick();
        } else {
            AddressInfo address = adapter.getItem(position);
            HistoryAddressInfoStore.shareStore().removeHistoryAddress(address);
            adapter.removeItem(position);
        }
    }

    @Override
    public void onItemViewClick(HistoryAddressListAdapter adapter, int position) {
        AddressInfo address = adapter.getItem(position);
        HistoryAddressInfoStore.shareStore().addHistoryAddress(address);
        AddressComponent.shareComponent().setCurrentAddress(address);
        finish();
    }

    @Override
    public void onFooterViewClick() {
        mSearchBar.releaseFocus();
        HistoryAddressInfoStore.shareStore().clearHistoryAddressList();
        mHistoryContainer.setVisibility(View.GONE);
        mHistoryListView.setAdapter(null);
    }

    private void goBack() {
        if (TextUtils.isEmpty(mSearchBar.getKeyword())) {
            finish();
        } else {
            mSearchBar.setKeyword("");
        }
    }

    private void broadcastFinishSelectAddressEvent() {
        Intent intent = new Intent(AddressComponent.ACTION_ON_FINISH_SELECT_ADDRESS);
        sendBroadcast(intent);
    }
}
