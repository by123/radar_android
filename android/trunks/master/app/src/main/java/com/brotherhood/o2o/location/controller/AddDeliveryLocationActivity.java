package com.brotherhood.o2o.location.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.model.OrderSuccessInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.LocationComponent;
import com.brotherhood.o2o.location.adapter.AddLocationListAdapter;
import com.brotherhood.o2o.location.helper.LocationUrlFetcher;
import com.brotherhood.o2o.location.model.LocationInfo;
import com.brotherhood.o2o.location.model.MyLocationInfo;
import com.brotherhood.o2o.surprise.helper.SurpriseUrlFetcher;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.deletelistview.DeleteListView;
import com.brotherhood.o2o.ui.widget.dialog.BasicDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/6/30.
 */
public class AddDeliveryLocationActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.btn_add)
    Button mAddBtn;

    @InjectView(R.id.listview)
    DeleteListView mListView;

    @InjectView(R.id.txt_tips)
    TextView mTipsTxt;

    @InjectView(R.id.layout_select)
    View mSelectLayout;

    @InjectView(R.id.layout_add)
    View mAddLayout;

    @InjectView(R.id.edit_name)
    EditText mNameEdit;

    @InjectView(R.id.edit_phone)
    EditText mPhoneEdit;

    @InjectView(R.id.edit_address)
    TextView mAddressTxt;

    @OnClick(R.id.edit_address)
    void onAddressEditClick() {
        LocationComponent.shareComponent().showSelectAddressPage(this);
    }

    @OnClick(R.id.btn_add)
    void onAddBtnClick() {
        if (mStatu == ButtonStatu.Select) {
            showAddLayout();
        } else {
            MyLocationInfo info = new MyLocationInfo();
            info.mName = mNameEdit.getText().toString();
            info.mPhone = mPhoneEdit.getText().toString();
            info.mAddress = mAddressTxt.getText().toString();
            requestAddLocation(info);
            AddLocationListAdapter adapter = (AddLocationListAdapter) mListView.getAdapter();
            if (adapter != null) {
                ArrayList<MyLocationInfo> infos=adapter.getDatas();
                infos.add(info);
                adapter.updateInfos(infos);
            }
            showSelectLayout();
        }
    }

    private enum ButtonStatu {Select, Add}

    private ButtonStatu mStatu = ButtonStatu.Select;

    private long seconds = -1;

    private LocationBroadCastReceiver mLocationBroadCastReceiver;

    private final static String EXTRA_ID="extra_id";

    private OrderSuccessInfo mOrderInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addlocation);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        int id=getIntent().getIntExtra(EXTRA_ID,-1);
        requestOrderInfo(id);
        requestAddress();
        RegisterLocationBroadCast();
        initView();
    }

    public static void show(Context context,int id) {
        Intent intent = new Intent(context, AddDeliveryLocationActivity.class);
        intent.putExtra(EXTRA_ID,id);
        context.startActivity(intent);
    }

    private void showAddLayout() {
        mStatu = ButtonStatu.Add;
        mSelectLayout.setVisibility(View.GONE);
        mAddLayout.setVisibility(View.VISIBLE);
        mAddBtn.setText(R.string.addlocation_btn_save);
    }

    private void showSelectLayout() {
        Utils.hideKeyboard(mNameEdit, mPhoneEdit);
        mStatu = ButtonStatu.Select;
        mSelectLayout.setVisibility(View.VISIBLE);
        mAddLayout.setVisibility(View.GONE);
        mAddBtn.setText(R.string.addlocation_btn_add);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        goBack();
        return true;
    }

    private void goBack() {
        if (mStatu == ButtonStatu.Select) {
            finish();
        } else {
            showSelectLayout();
        }
    }

    private void initView() {
        showSelectLayout();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTipsTxt.setText(Utils.getString(R.string.addlicaiton_txt_tips, formatTime()));
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            Message msg = new Message();
            handler.sendMessage(msg);
        }
    };

    private String formatTime() {
        seconds--;
        String time = null;
        String minite = "00";
        String second = "00";
        if (seconds <= 0) {
            timer.cancel();
            time = minite + ":" + second;
            finish();
        } else {
            if (seconds >= 60) {

                if (seconds / 60 > 9) {
                    minite = "" + seconds / 60;
                } else {
                    minite = "0" + seconds / 60;
                }
            }
            long temp = seconds - Long.parseLong(minite) * 60;
            if (temp < 10) {
                second = "0" + temp;
            } else {
                second = "" + temp;
            }
            time = minite + ":" + second;
        }
        return time;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationBroadCastReceiver != null) {
            unregisterReceiver(mLocationBroadCastReceiver);
        }
    }


    private void RegisterLocationBroadCast() {
        mLocationBroadCastReceiver = new LocationBroadCastReceiver();
        IntentFilter filter = new IntentFilter(LocationComponent.ACTION_ON_ADDRESS_CHANGED);
        registerReceiver(mLocationBroadCastReceiver, filter);

    }

    private class LocationBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LocationComponent.ACTION_ON_ADDRESS_CHANGED)) {
                LocationInfo addressInfo = LocationComponent.shareComponent().getCachedCurrentAddressOrNil();
                mAddressTxt.setText(addressInfo.mBuildingName);
            }
        }
    }

    private void requestAddLocation(MyLocationInfo info)
    {
        LocationUrlFetcher.requestAddLocation(info, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    Utils.showShortToast("添加成功");
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }
    private void requestAddress() {
        LocationUrlFetcher.requestAddress(new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    ArrayList<MyLocationInfo> infos = MyLocationInfo.getDatas(jsonStr);
                    initData(infos);
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }

    private void initData(final ArrayList<MyLocationInfo> infos) {
        final AddLocationListAdapter mAdapter = new AddLocationListAdapter(this, infos);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mAdapter != null && infos != null) {
                    for (MyLocationInfo info : infos) {
                        info.mStatu = false;
                    }
                    infos.get(i).mStatu = true;
                    mAdapter.updateInfos(infos);
                }
                final MyLocationInfo temp=infos.get(i);
                new BasicDialog(AddDeliveryLocationActivity.this, new BasicDialog.OnDialogListener() {
                    @Override
                    public void OnConfirm(final BasicDialog dialog) {
                        LocationUrlFetcher.requestConfirmLocation(mOrderInfo.mId, temp.mId, new HttpClient.OnHttpListener() {
                            @Override
                            public void OnStart() {

                            }

                            @Override
                            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                                DeliverySuccessActivity.show(AddDeliveryLocationActivity.this, mOrderInfo.mId);
                                dialog.dismiss();
                            }

                            @Override
                            public void OnFail(HttpClient.RequestStatu statu, String resons) {

                            }
                        });
                    }

                    @Override
                    public void OnCancel(BasicDialog dialog) {

                        dialog.dismiss();
                    }
                }).setMainTxt("确定配送到这个地址?").show();
            }
        });
    }

    private void requestOrderInfo(int id) {
        SurpriseUrlFetcher.requestSurpriseDetail(id, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                     mOrderInfo = OrderSuccessInfo.getData(jsonStr);
                    seconds = mOrderInfo.mBufferTime;
                    timer.schedule(task, 1000, 1000);
                }

            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }
}
