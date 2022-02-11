package com.brotherhood.o2o.explore.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.helper.ExploerUrlFetcher;
import com.brotherhood.o2o.explore.model.ActivityBean;
import com.brotherhood.o2o.explore.model.OrderSuccessInfo;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.surprise.helper.SurpriseUrlFetcher;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/7/24.
 */
public class OrderSuccessActivity extends BaseActivity {


    @InjectView(R.id.img_product)
    SimpleDraweeView mProductImg;

    @InjectView(R.id.img_right)
    ImageView mRightImg;

    @InjectView(R.id.img_left)
    ImageView mLeftImg;

    @InjectView(R.id.img_title)
    ImageView mTitleImg;

    @InjectView(R.id.txt_title)
    TextView mTitleTxt;

    @InjectView(R.id.txt_product_name)
    TextView mProductNameTxt;

    @InjectView(R.id.txt_introduce)
    TextView mSummaryTxt;

    @InjectView(R.id.txt_goodname)
    TextView mGoodName;

    @InjectView(R.id.layout_activity)
    LinearLayout mActivityLayout;

    @InjectView(R.id.btn_order)
    Button mOrderBtn;

    @InjectView(R.id.layout_consume)
    View mConsumeLayout;

    @InjectView(R.id.edittext)
    EditText mConsumeEdit;

    @InjectView(R.id.btn_confirm)
    View mConfirmBtn;

    @OnClick(R.id.img_left)
    void OnLeftImgClick() {
        finish();
    }

    @OnClick(R.id.btn_order)
    void onOrderBtnClick() {
        if (mOrderInfo == null) {
            return;
        }
        if (mOrderInfo.mStatu == 2) {
            Utils.showShortToast(R.string.ordersuccess_used);
            return;
        }
        showConsumeCodeLayout(mOrderInfo.mId);
    }

    public final static String EXTRA_ID = "extra_id";
    private OrderSuccessInfo mOrderInfo = null;
    private boolean isConsume = false;

    public static void show(Context context, int id) {
        Intent intent = new Intent(context, OrderSuccessActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ordersuccess);
        ButterKnife.inject(this);
        initTitle();
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        requestOrderInfo(id);
    }

    private void initView() {
        mProductNameTxt.setText(mOrderInfo.mTitle);
        mSummaryTxt.setText(mOrderInfo.mSummary);
        mGoodName.setText(Utils.getString(R.string.product_activity_goodname, mOrderInfo.mSupplier));
        ImageLoader.getInstance().setImageUrl(mProductImg, mOrderInfo.mIconUrl);
        mOrderBtn.setText(R.string.product_activity_confirm);
        initActivityLayout();
    }

    private void initTitle() {
        mLeftImg.setImageResource(R.drawable.selector_left_arrow_white);
        mTitleImg.setVisibility(View.GONE);
        mTitleTxt.setText(R.string.product_activity_order_finish);
        mRightImg.setImageResource(R.drawable.selector_share_white);
    }

    private void initActivityLayout() {
        List<ActivityBean> datas = mOrderInfo.mActivityBeans;
        if (datas != null && datas.size() > 0) {
            for (ActivityBean data : datas) {
                View v = LayoutInflater.from(this).inflate(R.layout.product_dynamic_itemview, null);
                TextView contentTxt = (TextView) v.findViewById(R.id.txt_content);
                contentTxt.setText(data.mTitle + ":" + data.mContent);
                mActivityLayout.addView(v);
                v.setPadding(0, Utils.dip2px(15), 0, 0);
            }
        }
    }


    private void showConsumeCodeLayout(final int id) {
        isConsume = true;
        mConsumeLayout.setVisibility(View.VISIBLE);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestConsume(id);
            }
        });
    }

    private void requestConsume(int id) {
        String code = mConsumeEdit.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Utils.showShortToast(R.string.product_activity_order_codenotnull);
            return;
        }
        ExploerUrlFetcher.requestConsumeCode(id, code, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    Utils.showShortToast(R.string.product_activity_consume_success);
                    mConsumeLayout.setVisibility(View.GONE);
                    isConsume = false;
                } else {
                    Utils.showShortToast(R.string.product_activity_consume_fail);
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {
                Utils.showShortToast(R.string.product_activity_consume_fail);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isConsume) {
            mConsumeLayout.setVisibility(View.GONE);
            isConsume = false;
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    initView();
                }

            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }
}
