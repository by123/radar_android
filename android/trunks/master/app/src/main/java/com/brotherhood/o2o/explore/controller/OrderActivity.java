package com.brotherhood.o2o.explore.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.helper.ExploerUrlFetcher;
import com.brotherhood.o2o.explore.model.ActivityBean;
import com.brotherhood.o2o.explore.model.ContentBean;
import com.brotherhood.o2o.explore.model.ProductBean;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.controller.AddDeliveryLocationActivity;
import com.brotherhood.o2o.surprise.controller.MySurpriseActivity;
import com.brotherhood.o2o.test.blur.ScrollableImageView;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.dialog.BasicDialog;
import com.brotherhood.o2o.ui.widget.radar.ScrollCallbackView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by by.huang on 2015/7/24.
 */
public class OrderActivity extends BaseActivity {


    @InjectView(R.id.img_product)
    SimpleDraweeView mProductImg;

    @InjectView(R.id.img_left)
    ImageView mLeftImg;

    @InjectView(R.id.img_right)
    ImageView mRightImg;

    @InjectView(R.id.img_title)
    ImageView mTitleImg;

    @InjectView(R.id.txt_title)
    TextView mTitleTxt;

    @InjectView(R.id.txt_product_name)
    TextView mProductNameTxt;

    @InjectView(R.id.txt_original_price)
    TextView mOriginalPriceTxt;

    @InjectView(R.id.txt_discount_price)
    TextView mDiscountPriceTxt;

    @InjectView(R.id.txt_introduce)
    TextView mSummaryTxt;

    @InjectView(R.id.txt_places_total)
    TextView mPlacesTotalTxt;

    @InjectView(R.id.txt_places)
    TextView mPlacesTxt;

    @InjectView(R.id.txt_goodname)
    TextView mGoodName;

    @InjectView(R.id.layout_activity)
    LinearLayout mActivityLayout;

    @InjectView(R.id.img_map)
    SimpleDraweeView mMapImg;

    @InjectView(R.id.layout_content)
    LinearLayout mContentLayout;

    @InjectView(R.id.btn_order)
    Button mOrderBtn;

    @InjectView(R.id.img_scrollable)
    ScrollableImageView mScollableImg;

    @InjectView(R.id.scrollview)
    ScrollCallbackView mScrollView;

    @OnClick(R.id.img_left)
    void OnLeftBtnClick() {
        finish();
    }

    @OnClick(R.id.btn_order)
    void onOrderBtnClick() {
        if (mProductBean == null) {
            return;
        }
        makeOrder();
    }

    public final static String EXTRA_ID = "extra_id";
    public final static String EXTAR_TYPEID = "extra_typeid";
    private String mBeaconId;
    private int mTypeid = -1;
    private ProductBean mProductBean = null;

    public static void show(Context context, String beaconId, int typeid) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(EXTRA_ID, beaconId);
        intent.putExtra(EXTAR_TYPEID, typeid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_product);
        ButterKnife.inject(this);
        mBeaconId = getIntent().getStringExtra(EXTRA_ID);
        mTypeid = getIntent().getIntExtra(EXTAR_TYPEID, -1);
        initTitle();
        requestDatas();
    }

    private void initView(ProductBean productBean) {
        mProductNameTxt.setText(productBean.mTitle);
        mSummaryTxt.setText(productBean.mSummary);
        mDiscountPriceTxt.setText(Utils.getString(R.string.product_activity_rmb, productBean.mDiscountPrice));
        mOriginalPriceTxt.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mOriginalPriceTxt.setText(Utils.getString(R.string.product_activity_rmb, productBean.mPrice));
        String placeTotalStr = Utils.getString(R.string.product_activity_quota_total, productBean.mQuota + "");
        mPlacesTotalTxt.setText(Utils.formatTextColor(placeTotalStr, 6, String.valueOf(productBean.mQuota).length() + 6, getResources().getColor(R.color.main_red)));
        String placeRemainStr = Utils.getString(R.string.product_activity_quota_remain, productBean.mRemainQuota + "");
        mPlacesTxt.setText(Utils.formatTextColor(placeRemainStr, 6, String.valueOf(productBean.mQuota).length() + 6, getResources().getColor(R.color.main_red)));
        mGoodName.setText(Utils.getString(R.string.product_activity_goodname, productBean.mSupplier));
        ImageLoader.getInstance().setImageUrl(mProductImg, productBean.mIcon);
//        String path=ImageLoader.getInstance().setImageUrl(mProductImg, productBean.mIcon, 1, new ImageLoader.ImageLoaderListener() {
//            @Override
//            public void OnLoadStart() {
//
//            }
//
//            @Override
//            public void OnLoadFinish(String path, Bitmap bitmap) {
//                initScrollableImg(path);
//            }
//
//            @Override
//            public void OnLoadFail() {
//
//            }
//        }, Constants.SCREEN_WIDTH, Utils.dip2px(200));
//
//        if(!TextUtils.isEmpty(path))
//        {
//            initScrollableImg(path);
//        }
        initActivityLayout(productBean);
        initContentLayout(productBean);
        if (mTypeid == 1) {
            mOrderBtn.setText(Utils.getString(R.string.product_activity_order, productBean.mDiscountPrice));
        } else if (mTypeid == 2) {
            mOrderBtn.setText(R.string.product_activity_delivery);
        } else {
            mOrderBtn.setText(R.string.product_activity_get);
        }
    }

    private void initTitle() {
        mLeftImg.setImageResource(R.drawable.selector_left_arrow_white);
        mTitleImg.setVisibility(View.GONE);
        mTitleTxt.setText(R.string.product_activity_introduce_title);
        mRightImg.setImageResource(R.drawable.selector_share_white);
    }

    private void initActivityLayout(ProductBean productBean) {
        List<ActivityBean> datas = productBean.mActivityBeans;
        if (datas != null && datas.size() > 0) {
            for (ActivityBean data : datas) {
                View v = LayoutInflater.from(this).inflate(R.layout.product_dynamic_itemview, null);
                TextView contentTxt = (TextView) v.findViewById(R.id.txt_content);
                contentTxt.setText(data.mTitle + ":" + data.mContent);
                mActivityLayout.addView(v);
                v.setPadding(0, Utils.dip2px(15), 0, 0);
            }
        }
        ImageLoader.getInstance().setImageUrl(mMapImg, productBean.mMapUrl);
    }

//    private void initScrollableImg(String imgPath) {
//        mScollableImg.setScreenWidth(Constants.SCREEN_WIDTH);
//        ExplperHelper.getInstance().blurInScrub(OrderActivity.this,mScollableImg, imgPath);
//        mScollableImg.setVisibility(View.VISIBLE);
//        mScrollView.setOnScrollListener(new ScrollCallbackView.OnScrollListener() {
//            @Override
//            public void OnScroll(int top) {
//                if(top < Utils.dip2px(200) && top>0)
//                {
//                    mScollableImg.handleScroll(-top);
//                }
//            }
//        });
//    }

    private void initContentLayout(ProductBean productBean) {
        mContentLayout.setGravity(Gravity.CENTER);
        List<ContentBean> datas = productBean.mContentBeans;
        if (datas != null && datas.size() > 0) {
            for (ContentBean data : datas) {
                if (data.mType.equalsIgnoreCase("text")) {
                    TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.product_dynamic_textview, null);
                    textView.setText(data.mContent);
                    mContentLayout.addView(textView);
                } else if (data.mType.equalsIgnoreCase("image")) {
                    SimpleDraweeView simpleDraweeView = new SimpleDraweeView(this);
                    ImageLoader.getInstance().setImageUrl(simpleDraweeView, data.mContent);
                    mContentLayout.addView(simpleDraweeView);
                }
            }
        }
    }

    /**
     * 请求活动数据
     */
    private void requestDatas() {
        if (TextUtils.isEmpty(mBeaconId)) {
            return;
        }
        ExploerUrlFetcher.requsetProductDatas(mBeaconId, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    mProductBean = ProductBean.getDatas(jsonStr);
                    initView(mProductBean);
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }

    /**
     * 下订单
     */
    private void makeOrder() {
        final String beaconID = getIntent().getStringExtra(EXTRA_ID);
        ExploerUrlFetcher.requestCreateOrder(beaconID, new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {
                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        int id = jsonObject.getJSONObject("data").getInt("id");
                        if (mTypeid == 1) {
                            OrderSuccessActivity.show(OrderActivity.this, id);
                        } else if (mTypeid == 2) {
                            AddDeliveryLocationActivity.show(OrderActivity.this, id);
                        } else {
                            new BasicDialog(OrderActivity.this, new BasicDialog.OnDialogListener() {
                                @Override
                                public void OnConfirm(BasicDialog dialog) {

                                    MySurpriseActivity.show(OrderActivity.this, MySurpriseActivity.TYPE_COUPON);
                                    dialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void OnCancel(BasicDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).setMainTxt("恭喜获得xxx优惠码").hideOneButton().setConfirmTxt("去查看吧").show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {
            }
        });
    }
}
