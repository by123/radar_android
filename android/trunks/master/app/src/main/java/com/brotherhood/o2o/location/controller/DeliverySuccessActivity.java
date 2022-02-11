package com.brotherhood.o2o.location.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.model.ActivityBean;
import com.brotherhood.o2o.explore.model.OrderSuccessInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.surprise.helper.SurpriseUrlFetcher;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/7/28.
 */
public class DeliverySuccessActivity extends BaseActivity {


    @InjectView(R.id.txt_name)
    TextView mNameTxt;

    @InjectView(R.id.txt_address)
    TextView mAddressTxt;

    @InjectView(R.id.txt_phone)
    TextView mPhoneTxt;

    @InjectView(R.id.txt_product)
    TextView mProductTxt;

    @InjectView(R.id.img_product)
    SimpleDraweeView mProductImg;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.layout_activity)
    LinearLayout mActivityLayout;

    public final static String EXTRA_ID = "id";


    public static void show(Context context, int id) {
        Intent intent = new Intent(context, DeliverySuccessActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_confirm_location);
        ButterKnife.inject(this);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id == -1) {
            Utils.showShortToast(R.string.server_error);
            finish();
        }
        requestOrderInfo(id);
    }

    private void initView( OrderSuccessInfo mOrderInfo ) {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (mOrderInfo == null ) {
            return;
        }
        mNameTxt.setText(mOrderInfo.mUserName);
        mAddressTxt.setText(mOrderInfo.mUserAddress);
        mPhoneTxt.setText(mOrderInfo.mUserPhone);

        mProductTxt.setText(mOrderInfo.mTitle);
        ImageLoader.getInstance().setImageUrl(mProductImg, mOrderInfo.mIconUrl);


        List<ActivityBean> activityBeans = mOrderInfo.mActivityBeans;
        if (activityBeans != null && activityBeans.size() > 0) {
            for (ActivityBean activityBean : activityBeans) {

                View view = LayoutInflater.from(this).inflate(R.layout.confirmlocation_item, null);
                TextView mTitleTxt = (TextView) view.findViewById(R.id.txt_title);
                TextView mContentTxt = (TextView) view.findViewById(R.id.txt_content);
                mTitleTxt.setText(activityBean.mTitle);
                mContentTxt.setText(activityBean.mContent);
                mActivityLayout.addView(view);
            }
        }

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
                    ByLogout.out(jsonStr);
                    OrderSuccessInfo mOrderInfo = OrderSuccessInfo.getData(jsonStr);
                    initView(mOrderInfo);
                }

            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }

}
