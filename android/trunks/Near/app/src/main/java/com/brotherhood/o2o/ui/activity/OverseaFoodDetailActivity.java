package com.brotherhood.o2o.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.GooglePoiInfo;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.bean.nearby.FoodComment;
import com.brotherhood.o2o.bean.nearby.FoodDetail;
import com.brotherhood.o2o.bean.nearby.FoodPhoto;
import com.brotherhood.o2o.bean.nearby.FoodPrice;
import com.brotherhood.o2o.bean.nearby.FoodTime;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.multiStateView.MultiStateView;
import com.brotherhood.o2o.listener.OnGooglePoiResponseListener;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.listener.observerview.ScrollCallBack;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.message.Message;
import com.brotherhood.o2o.request.CancelCollectEventRequest;
import com.brotherhood.o2o.request.CollectEventRequest;
import com.brotherhood.o2o.request.OverseaAddressRequest;
import com.brotherhood.o2o.request.OverseaFoodDetailRequest;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.adapter.FoodDetailCommentAdapter;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.CornerImageView;
import com.brotherhood.o2o.ui.widget.YelpRatingView;
import com.brotherhood.o2o.ui.widget.dialog.CallPhoneDialog;
import com.brotherhood.o2o.ui.widget.nearby.FoodPriceLevelView;
import com.brotherhood.o2o.ui.widget.nearby.FoodScoreView;
import com.brotherhood.o2o.ui.widget.nearby.ObservableListView;
import com.brotherhood.o2o.util.CoordinateUtil;
import com.brotherhood.o2o.util.DialogUtil;
import com.brotherhood.o2o.util.DistanceFormatUtil;
import com.brotherhood.o2o.util.MapUtil;
import com.brotherhood.o2o.util.ViewUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 海外版美食详情
 */
public class OverseaFoodDetailActivity extends BaseActivity implements ScrollCallBack {
    //头部
    @ViewInject(id = R.id.abAlpha)
    private View mViHeadBg;

    @ViewInject(id = R.id.abBack, clickMethod = "back")
    private ImageView mIvBack;

    @ViewInject(id = R.id.viHeadBottomLine)
    private View mViHeadBottomLine;

    @ViewInject(id = R.id.abDivide)
    private View mViHeadDivide;

    @ViewInject(id = R.id.abFoodDetailReserve, clickMethod = "reserve")
    private TextView mTvHeadReserve;

    @ViewInject(id = R.id.abFoodDetailCall, clickMethod = "callPhone")
    private TextView mTvHeadCall;

    @ViewInject(id = R.id.abFoodDetailVisit, clickMethod = "visit")
    private TextView mTvHeadVisit;

    @ViewInject(id = R.id.abFoodDetailCollect, clickMethod = "collectFood")
    private TextView mTvHeadCollect;

    @ViewInject(id = R.id.observerListView)
    private ObservableListView mListView;

    @ViewInject(id = R.id.viFoodDetailTitleBg)
    private View mViBg;

    private YelpRatingView yelpRating;
    private TextView tvPhotoCount;
    private LinearLayout llOpenTable;
    private LinearLayout llFoursquare;

    private View mHeadView;

    private ImageView mIvIcon;

    private CornerImageView mIvSmallIcon;

    private TextView mTvTitle;

    private TextView mTvType;

    private TextView mTvDistance;

    private TextView mTvVote;

    private FoodPriceLevelView mPriceLevel;

    private FoodScoreView mFoodScoreView;

    private TextView mTvWorkDate;

    private TextView mTvWorkTime;

    private TextView mTvWorkState;

    private TextView mTvCall;

    private SupportMapFragment mMapFragment;

    private TextView mTvLocation;

    private LinearLayout mLlGMap;

    private LinearLayout mLlUber;

    private TextView mTvMenu;
    private View mViMenuTextDivide;
    private View mViMenuDivide;
    private TextView mTvMenuTitle;

    private View mViHideDivide;

    private LinearLayout mLlBusyDate;
    private TextView mTvReviewText;
    private View mViReviewDivide;

    private FoodDetailCommentAdapter mCommentAdapter;
    private String mBusinessId;
    private OverseaFoodDetailRequest mFoodDetailRequest;
    private List<String> mFoodPhotoList = new ArrayList<>();//美食图片集合
    private List<FoodComment> mCommentList = new ArrayList<>();//评论集合
    private List<FoodComment> mPartList = new ArrayList<>();//评论前三
    private FoodDetail mFoodDetail;
    private LatLng mStartPoint;
    private LatLng mDestPoint;
    private CollectEventRequest mCollectEventRequest;
    private CancelCollectEventRequest mCancelCollectEventRequest;
    private OverseaAddressRequest mAddressRequest;
    private CallPhoneDialog mCallPhoneDialog;
    private String mPhoneNo;
    private boolean initIsCollected;
    private boolean isCollected;
    private float mScrollRatio;
    private boolean hasPicture = true;
    private String mAddress;


    private Dialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_food_detail_layout;
    }

    public static void show(Context context, String businessId) {
        Intent it = new Intent(context, OverseaFoodDetailActivity.class);
        it.putExtra(BundleKey.NEARBY_FOOD_DETAIL_KEY, businessId);
        context.startActivity(it);
    }


    @Override
    protected boolean showLoading() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBusinessId = getIntent().getStringExtra(BundleKey.NEARBY_FOOD_DETAIL_KEY);

        mStateView.setViewState(MultiStateView.VIEW_STATE_LOADING);

        mHeadView = LayoutInflater.from(this).inflate(R.layout.oversea_food_detail_headview, null);
        initHeadView();
        mListView.addHeaderView(mHeadView);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mvFoodDetailMap);
        mListView.setScrollCallBack(this);
        mCommentAdapter = new FoodDetailCommentAdapter(OverseaFoodDetailActivity.this, mPartList);
        mCommentAdapter.setOnMoreCommentCallBack(new FoodDetailCommentAdapter.OnMoreCommentCallBack() {
            @Override
            public void clickMore() {
                mCommentAdapter.replaceAll(mCommentList);
                mCommentAdapter.setHasMore(false);
            }
        });
        mListView.setAdapter(mCommentAdapter);

        dialog = DialogUtil.createLoadingDialog(this);

        loadData();
    }

    private void initHeadView() {
        mIvIcon = (ImageView) mHeadView.findViewById(R.id.ivFoodDetailIcon);
        mIvSmallIcon = (CornerImageView) mHeadView.findViewById(R.id.ivFoodDetailSmallIcon);
        mIvSmallIcon.setOnClickListener(this);
        mTvTitle = (TextView) mHeadView.findViewById(R.id.tvFoodDetailTitle);
        mTvType = (TextView) mHeadView.findViewById(R.id.tvFoodDetailType);
        mTvDistance = (TextView) mHeadView.findViewById(R.id.tvFoodDetailDistance);
        mTvVote = (TextView) mHeadView.findViewById(R.id.tvFoodDetailVotes);
        mPriceLevel = (FoodPriceLevelView) mHeadView.findViewById(R.id.foodDetailPriceLevel);
        mFoodScoreView = (FoodScoreView) mHeadView.findViewById(R.id.fsFoodDetailScore);
        mViHideDivide = mHeadView.findViewById(R.id.viFoodDetailHideDivide);
        yelpRating = (YelpRatingView) mHeadView.findViewById(R.id.yelpRating);
        tvPhotoCount = (TextView) mHeadView.findViewById(R.id.tvPhotoCount);
        llOpenTable = (LinearLayout) mHeadView.findViewById(R.id.llOpenTable);
        llFoursquare = (LinearLayout) mHeadView.findViewById(R.id.llFoursquare);


        //第三部分
        mTvWorkDate = (TextView) mHeadView.findViewById(R.id.tvFoodDetailDate);
        mTvWorkTime = (TextView) mHeadView.findViewById(R.id.tvFoodDetailTime);
        mTvWorkState = (TextView) mHeadView.findViewById(R.id.tvFoodDetailWorkState);
        mTvCall = (TextView) mHeadView.findViewById(R.id.tvFoodDetailCall);
        mTvCall.setOnClickListener(this);

        mTvLocation = (TextView) mHeadView.findViewById(R.id.tvFoodDetailLocation);
        mLlGMap = (LinearLayout) mHeadView.findViewById(R.id.llFoodDetailGMap);
        mLlGMap.setOnClickListener(this);
        mLlUber = (LinearLayout) mHeadView.findViewById(R.id.llFoodDetailUber);
        mLlUber.setOnClickListener(this);
        mTvReviewText = (TextView) mHeadView.findViewById(R.id.tvFoodDetailReviewText);
        mViReviewDivide = mHeadView.findViewById(R.id.viFoodDetailReviewDivide);
        mLlBusyDate = (LinearLayout) mHeadView.findViewById(R.id.llFoodDetailDate);

        mViMenuTextDivide = mHeadView.findViewById(R.id.viFoodDetailMenuTextDivide);
        mViMenuDivide = mHeadView.findViewById(R.id.viFoodDetailMenuDivide);
        mTvMenuTitle = (TextView) mHeadView.findViewById(R.id.tvFoodDetailMenuText);
        mTvMenu = (TextView) mHeadView.findViewById(R.id.tvFoodDetailViewMenu);
        mTvMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFoodDetailCall:
                callPhone(null);
                break;
            case R.id.mvFoodDetailMap:
                GoogleMapActivity.show(OverseaFoodDetailActivity.this, mStartPoint, mDestPoint, mAddress);
                break;
            case R.id.llFoodDetailGMap:
                mapDetail();
                break;
            case R.id.llFoodDetailUber:
                uberDetail();
                break;
            case R.id.tvFoodDetailViewMenu:
                menuDetail();
                break;
            case R.id.ivFoodDetailSmallIcon://预览
                // TODO: 2016/1/14 跳转到预览界面
                //ChoosePhotoActivity.show(OverseaFoodDetailActivity.this, -1, true);
                break;
        }
        int viewId = v.getId();
        if (viewId == R.id.tvEmpty || viewId == R.id.tvRetry) {
            loadData();
        }
    }

    @Override
    public void onScroll(float distance) {
        viewTrans(mListView.getCurrentScrollY());
    }

    //随着listview滚动改变title背景色
    private void viewTrans(float scrollY) {
        if (!hasPicture) {
            return;
        }
        int mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.food_detail_head_height) - getResources().getDimensionPixelSize(R.dimen.common_titlebar_height);
        scrollY = Math.min(scrollY, mMinHeaderHeight);
        float ratio = Math.abs(scrollY / mMinHeaderHeight);
        mScrollRatio = ratio;
        if (ratio > 0.5) {
            mIvBack.setImageResource(R.mipmap.back_image_black);
            mViHeadDivide.setBackgroundColor(getResources().getColor(R.color.black));
            ViewUtil.setTextViewDrawableTop(mTvHeadReserve, R.mipmap.ic_details_reserve_black);
            ViewUtil.setTextViewDrawableTop(mTvHeadCall, R.mipmap.food_detail_call_black);
            ViewUtil.setTextViewDrawableTop(mTvHeadVisit, R.mipmap.food_detail_visit_black);
            ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.food_detail_collect_black);
            mTvHeadReserve.setTextColor(getResources().getColor(R.color.eighty_percent_black));
            mTvHeadCollect.setTextColor(getResources().getColor(R.color.eighty_percent_black));
            mTvHeadCall.setTextColor(getResources().getColor(R.color.eighty_percent_black));
            mTvHeadVisit.setTextColor(getResources().getColor(R.color.eighty_percent_black));
        } else {
            mIvBack.setImageResource(R.mipmap.back_image_white);
            mViHeadDivide.setBackgroundColor(getResources().getColor(R.color.white));
            ViewUtil.setTextViewDrawableTop(mTvHeadReserve, R.mipmap.ic_details_reserve_white);
            ViewUtil.setTextViewDrawableTop(mTvHeadCall, R.mipmap.food_detail_call_white);
            ViewUtil.setTextViewDrawableTop(mTvHeadVisit, R.mipmap.food_detail_visit_white);
            ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.food_detail_collect_white);
            mTvHeadReserve.setTextColor(getResources().getColor(R.color.white));
            mTvHeadCall.setTextColor(getResources().getColor(R.color.white));
            mTvHeadVisit.setTextColor(getResources().getColor(R.color.white));
            mTvHeadCollect.setTextColor(getResources().getColor(R.color.white));
        }
        if (isCollected) {
            ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.collect_checked);
        }

        mViHeadBottomLine.setAlpha(ratio);
        mViHeadBg.setAlpha(ratio);
        mViBg.setAlpha(1 - ratio);
    }

    private void loadData() {
        if (mFoodDetailRequest == null) {
            mFoodDetailRequest = OverseaFoodDetailRequest.createFoodDetailRequest(mBusinessId, new OnResponseListener<FoodDetail>() {
                @Override
                public void onSuccess(int code, String msg, final FoodDetail foodDetail, boolean cache) {
                    //if (foodDetail == null) {
                    //    mStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                    //    return;
                    //}
                    showContentView();
                    mFoodDetail = foodDetail;
                    mPhoneNo = foodDetail.phoneNo;
                    FoodPhoto foodPhoto = foodDetail.mFoodPhoto;
                    yelpRating.rating(foodDetail.mYelpRating);
                    yelpRating.reviews(foodDetail.mYelpRatingCount);
                    final FoodPhoto photos = foodDetail.mFoodPhoto;
                    tvPhotoCount.setText(photos.mPhotoCount);
                    mIvSmallIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PhotosActivity.show(OverseaFoodDetailActivity.this, photos.mPhotoList);
                        }
                    });
                    if (!TextUtils.isEmpty(foodDetail.mOpenTable)) {
                        ViewUtil.toggleView(mTvHeadReserve, true);
                        ViewUtil.toggleView(llOpenTable, true);
                        mTvHeadReserve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WebViewActivity.show(OverseaFoodDetailActivity.this, foodDetail.mOpenTable);
                            }
                        });
                        llOpenTable.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WebViewActivity.show(OverseaFoodDetailActivity.this, foodDetail.mOpenTable);
                            }
                        });
                    }
                    if (foodPhoto != null && foodPhoto.mPhotoList != null && !foodPhoto.mPhotoList.isEmpty()) {
                        mFoodPhotoList.addAll(foodPhoto.mPhotoList);
                        if (!isFinishing()) {
                            ImageLoaderManager.displayImageByUrlCacheAllSize(OverseaFoodDetailActivity.this, mIvIcon, mFoodPhotoList.get(0), R.mipmap.img_default);
                            ImageLoaderManager.displayImageByUrlCacheAllSize(OverseaFoodDetailActivity.this, mIvSmallIcon, mFoodPhotoList.get(0), R.mipmap.img_default);
                            //ImageLoaderManager.displayRoundImageByUrl(OverseaFoodDetailActivity.this, mIvSmallIcon, mFoodPhotoList.get(0), R.mipmap.img_default, 5);
                        }
                    } else {
                        hasPicture = false;
                        mIvSmallIcon.setVisibility(View.GONE);
                        mIvIcon.setVisibility(View.GONE);
                        mViHideDivide.setVisibility(View.VISIBLE);
                        mViHeadBottomLine.setAlpha(1.0f);
                        mViHeadBg.setAlpha(1.0f);
                        mIvBack.setImageResource(R.mipmap.back_image_black);
                        mViHeadDivide.setBackgroundColor(getResources().getColor(R.color.black));
                        ViewUtil.setTextViewDrawableTop(mTvHeadReserve, R.mipmap.ic_details_reserve_black);
                        ViewUtil.setTextViewDrawableTop(mTvHeadCall, R.mipmap.food_detail_call_black);
                        ViewUtil.setTextViewDrawableTop(mTvHeadVisit, R.mipmap.food_detail_visit_black);
                        ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.food_detail_collect_black);
                        mTvHeadReserve.setTextColor(getResources().getColor(R.color.eighty_percent_black));
                        mTvHeadCollect.setTextColor(getResources().getColor(R.color.eighty_percent_black));
                        mTvHeadCall.setTextColor(getResources().getColor(R.color.eighty_percent_black));
                        mTvHeadVisit.setTextColor(getResources().getColor(R.color.eighty_percent_black));
                    }
                    if (foodDetail.mCollection == 1) {
                        isCollected = true;
                        initIsCollected = isCollected;
                        ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.collect_checked);
                    }
                    mStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);

                    mTvTitle.setText(foodDetail.mBusinessName);
                    mTvType.setText(foodDetail.mFoodType);

                    LatLng myLatlng = LocationManager.getInstance().getMyLatlng();
                    if (myLatlng != null) {
                        LatLng destPoint = new LatLng(foodDetail.mLatitude, foodDetail.mLongitude);
                        mStartPoint = myLatlng;
                        mDestPoint = destPoint;
                        mTvDistance.setText(DistanceFormatUtil.getGoogleMapDistance(OverseaFoodDetailActivity.this, mStartPoint, destPoint));
                    }

                    mMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng destLatLng = null;
                            if (mDestPoint != null) {
                                //谷歌地图在国内是gcj_02火星坐标系，在国外是标准坐标系
                                if (CoordinateUtil.outOfChina(mDestPoint.latitude, mDestPoint.longitude)){
                                    destLatLng = mDestPoint;
                                }else {
                                    LocationInfo destInfo = CoordinateUtil.gps84_To_Gcj02(mDestPoint.latitude, mDestPoint.longitude);
                                    LatLng dest = new LatLng(destInfo.mLatitude, destInfo.mLongitude);
                                    if (dest != null){
                                        destLatLng = dest;
                                    }
                                }
                                if (destLatLng != null){
                                    googleMap.addMarker(new MarkerOptions().position(destLatLng).title(foodDetail.mBusinessName));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 14));
                                }
                            }
                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    GoogleMapActivity.show(OverseaFoodDetailActivity.this, mStartPoint, mDestPoint, mAddress);
                                }
                            });
                        }
                    });
                    getDetailAddress(foodDetail.mLatitude, foodDetail.mLongitude);
                    //以下为可能为null的数据
                    FoodPrice price = foodDetail.mFoodPrice;
                    String unit = "$";
                    int level = 1;
                    if (price == null) {
                        mPriceLevel.setVisibility(View.GONE);
                    } else {
                        if (!TextUtils.isEmpty(price.mUnit)) {
                            unit = price.mUnit;
                        }
                        if (price.mLevel > 0) {
                            level = price.mLevel;
                        }
                        mPriceLevel.setLevel(level, unit);
                    }

                    if (TextUtils.isEmpty(foodDetail.mScore)) {
                        mFoodScoreView.setVisibility(View.GONE);
                    } else {
                        mFoodScoreView.setVisibility(View.VISIBLE);
                        mFoodScoreView.setScore(foodDetail.mScore);
                    }

                    if (mFoodDetail == null || TextUtils.isEmpty(mFoodDetail.mMenu)) {
                        mViMenuTextDivide.setVisibility(View.GONE);
                        mViMenuDivide.setVisibility(View.GONE);
                        mTvMenuTitle.setVisibility(View.GONE);
                        mTvMenu.setVisibility(View.GONE);
                    }

                    if (TextUtils.isEmpty(foodDetail.mVotes)) {
                        mTvVote.setVisibility(View.GONE);
                    } else {
                        mTvVote.setVisibility(View.VISIBLE);
                        mTvVote.setText(getString(R.string.foreign_food_vote, foodDetail.mVotes));
                    }
                    if ((TextUtils.isEmpty(foodDetail.mScore) || foodDetail.mScore.equals("0")) && (TextUtils.isEmpty(foodDetail.mVotes) || foodDetail.mVotes.equals("0"))) {
                        ViewUtil.toggleView(llFoursquare, false);
                    } else {
                        ViewUtil.toggleView(llFoursquare, true);
                    }
                    FoodTime foodTime = foodDetail.mFoodTime;
                    if (foodTime != null) {
                        if (!TextUtils.isEmpty(foodTime.mDate)) {
                            mTvWorkDate.setText(foodTime.mDate + ":");
                        }
                        if (!TextUtils.isEmpty(foodTime.mWorkTime)) {
                            mTvWorkTime.setText(foodTime.mWorkTime);
                        }

                        if (TextUtils.isEmpty(foodTime.isOpen)) {
                            mTvWorkState.setVisibility(View.GONE);
                        } else {
                            int open = Integer.valueOf(foodTime.isOpen);
                            if (open == 0) {
                                mTvWorkState.setText(getString(R.string.food_detail_close));
                                mTvWorkState.setTextColor(getResources().getColor(R.color.near_warn_red_color));
                            } else {
                                mTvWorkState.setText(getString(R.string.food_detail_open));
                                mTvWorkState.setTextColor(getResources().getColor(R.color.near_assist_green_color));
                            }
                        }
                    } else {
                        mLlBusyDate.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(foodDetail.phoneNo)) {
                        mTvCall.setText(foodDetail.phoneNo);
                    } else {
                        mTvCall.setVisibility(View.GONE);
                    }

                    List<FoodComment> comments = null;
                    if (foodDetail.mCommentWrapper != null) {
                        comments = foodDetail.mCommentWrapper.mCommentList;
                    }
                    if (comments != null) {
                        LogManager.d("=========================comment.size:" + comments.size());
                        if (comments.size() <= 3) {
                            mPartList.addAll(comments);
                        } else {
                            mPartList.add(comments.get(0));
                            mPartList.add(comments.get(1));
                            mPartList.add(comments.get(2));
                            if (mCommentAdapter != null) {
                                mCommentAdapter.setHasMore(true);
                            }
                        }
                        mCommentList.addAll(comments);
                    } else {
                        mTvReviewText.setVisibility(View.GONE);
                        mViReviewDivide.setVisibility(View.GONE);
                    }
                    if (mCommentAdapter != null) {
                        mCommentAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onFailure(int code, String msg) {
                    //mStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    showErrorView();
                }
            });
        }
        mFoodDetailRequest.sendRequest();
    }


    /**
     * 收藏
     *
     * @param view
     */
    public void collectFood(View view) {
        if (TextUtils.isEmpty(mBusinessId)) {
            return;
        }


        dialog.show();

        if (isCollected) {
            if (mCancelCollectEventRequest == null) {
                mCancelCollectEventRequest = CancelCollectEventRequest.createCancelCollectEventRequest(mBusinessId, 2, 3, new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(int code, String msg, String s, boolean cache) {
                        isCollected = false;
                        if (hasPicture) {
                            if (mScrollRatio > 0.5) {
                                ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.food_detail_collect_black);
                                mTvHeadCollect.setTextColor(getResources().getColor(R.color.eighty_percent_black));
                            } else {
                                ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.food_detail_collect_white);
                                mTvHeadCollect.setTextColor(getResources().getColor(R.color.white));
                            }
                        } else {
                            ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.food_detail_collect_black);
                            mTvHeadCollect.setTextColor(getResources().getColor(R.color.eighty_percent_black));
                        }
                        dialog.cancel();
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                        dialog.cancel();
                        ColorfulToast.orange(OverseaFoodDetailActivity.this, msg, Toast.LENGTH_SHORT);
                    }
                });

            }
            mCancelCollectEventRequest.sendRequest();
        } else {

            if (mCollectEventRequest == null) {
                mCollectEventRequest = CollectEventRequest.createCollectEventRequest(mBusinessId, 2, 3, new OnResponseListener<String>() {
                    @Override
                    public void onSuccess(int code, String msg, String s, boolean cache) {
                        dialog.cancel();
                        isCollected = true;
                        ViewUtil.setTextViewDrawableTop(mTvHeadCollect, R.mipmap.collect_checked);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        dialog.cancel();
                        ColorfulToast.orange(OverseaFoodDetailActivity.this, msg, Toast.LENGTH_SHORT);
                    }
                });

            }
            mCollectEventRequest.sendRequest();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (initIsCollected != isCollected) {
            NearApplication.mInstance.getMessagePump().broadcastMessage(Message.Type.OVERSEA_FOOD_COLLECT_CHANGE, mBusinessId);
        }
        if (mCollectEventRequest != null) {
            mCollectEventRequest.cancel();
        }
        if (mCancelCollectEventRequest != null) {
            mCancelCollectEventRequest.cancel();
        }
        if (mAddressRequest != null) {
            mAddressRequest.cancel();
        }
        if (mFoodDetailRequest != null) {
            mFoodDetailRequest.cancel();
        }
    }

    /**
     * 跳转到地图
     *
     * @param view
     */
    public void visit(View view) {
        MapUtil.buildGoogleWay(OverseaFoodDetailActivity.this, mStartPoint, mDestPoint);
    }

    /**
     * 打开url
     */
    public void menuDetail() {
        if (mFoodDetail == null || TextUtils.isEmpty(mFoodDetail.mMenu)) {
            LogManager.d("==============menu url is empty==============");
            return;
        }
        LogManager.d("==============menu url==============" + mFoodDetail.mMenu);
        WebViewActivity.show(OverseaFoodDetailActivity.this, mFoodDetail.mMenu);
    }


    public void reserve(View view) {

    }

    /**
     * Uber详情  打开Uber客户端
     */
    public void uberDetail() {
        MapUtil.buildUberWay(OverseaFoodDetailActivity.this, mStartPoint, mDestPoint, mAddress);
    }

    /**
     * @param view
     */
    public void callPhone(View view) {
        if (mCallPhoneDialog == null) {
            mCallPhoneDialog = new CallPhoneDialog(OverseaFoodDetailActivity.this);
            mCallPhoneDialog.setPhoneNumber(mPhoneNo);
        }
        mCallPhoneDialog.show();
    }

    /**
     * 跳转到地图详情
     */
    public void mapDetail() {
        MapUtil.buildGoogleWay(OverseaFoodDetailActivity.this, mStartPoint, mDestPoint);
    }

    public void back(View view) {
        finish();
    }

    /**
     * 根据坐标获取经纬度
     *
     * @param latitude
     * @param longitude
     */
    private void getDetailAddress(double latitude, double longitude) {
        String latlng = latitude + "," + longitude;
        if (mAddressRequest == null) {
            mAddressRequest = OverseaAddressRequest.createAddressRequest(latlng, new OnGooglePoiResponseListener<List<GooglePoiInfo>>() {
                @Override
                public void onSuccess(int code, String msg, List<GooglePoiInfo> googlePoiInfos, boolean cache) {
                    if (googlePoiInfos == null || googlePoiInfos.isEmpty()) {
                        return;
                    }
                    GooglePoiInfo info = googlePoiInfos.get(0);
                    if (info == null) {
                        return;
                    }
                    mAddress = info.formatted_address;
                    if (TextUtils.isEmpty(mAddress)) {
                        return;
                    }
                    mTvLocation.setText(mAddress);
                }

                @Override
                public void onFailure(int code, String msg) {
                }
            });
        }
        mAddressRequest.sendRequest();
    }
}