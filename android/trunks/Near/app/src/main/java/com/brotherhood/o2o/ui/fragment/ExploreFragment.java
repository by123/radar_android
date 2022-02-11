package com.brotherhood.o2o.ui.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.account.UserInfo;
import com.brotherhood.o2o.bean.location.LocationInfo;
import com.brotherhood.o2o.bean.radar.RadarEvent;
import com.brotherhood.o2o.bean.radar.RadarItem;
import com.brotherhood.o2o.bean.radar.RadarPeople;
import com.brotherhood.o2o.bean.radar.RadarPoi;
import com.brotherhood.o2o.chat.IDSIMManager;
import com.brotherhood.o2o.chat.db.service.IMDBLatestMsgService;
import com.brotherhood.o2o.chat.ui.MyMessageActivity;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.controller.ExploreComponent;
import com.brotherhood.o2o.controller.ExploreHelper;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.BaseAdapterHelper;
import com.brotherhood.o2o.lib.baseRecyclerAdapterHelper.QuickAdapter;
import com.brotherhood.o2o.listener.OnResponseListener;
import com.brotherhood.o2o.manager.AccountManager;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.manager.LocationManager;
import com.brotherhood.o2o.manager.NetworkStateManager;
import com.brotherhood.o2o.network.NetworkState;
import com.brotherhood.o2o.request.GetMyEventRequest;
import com.brotherhood.o2o.request.RadarDataRequest;
import com.brotherhood.o2o.task.TaskExecutor;
import com.brotherhood.o2o.ui.activity.MainActivity;
import com.brotherhood.o2o.ui.activity.NearbyServiceActivity;
import com.brotherhood.o2o.ui.activity.OtherUserDetailActivity;
import com.brotherhood.o2o.ui.activity.WebViewActivity;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.brotherhood.o2o.ui.widget.CommonHorizontalDecoration;
import com.brotherhood.o2o.ui.widget.radar.CoverView;
import com.brotherhood.o2o.ui.widget.radar.DirectLayout;
import com.brotherhood.o2o.ui.widget.radar.HeadViewBuilder;
import com.brotherhood.o2o.ui.widget.radar.WrapLinearLayoutManage;
import com.brotherhood.o2o.util.DialogUtil;
import com.brotherhood.o2o.util.DisplayUtil;
import com.brotherhood.o2o.util.DistanceFormatUtil;
import com.brotherhood.o2o.util.FastBlur;
import com.brotherhood.o2o.util.LanguageUtil;
import com.google.android.gms.maps.model.LatLng;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by by.huang on 2015/6/2.
 */

public class ExploreFragment extends BaseFragment implements View.OnClickListener {

    //扫描雷达图片
    private ImageView mScanImg;
    //雷达盘内布局
    private DirectLayout mRadarLayout;
    //最大外环背景
    //private ImageView mRadarWaveImg;
    private View mTipsLayout;
    //下拉提示图片
    private ImageView mTipsImg;
    //下拉提示文本
    private TextView mTipsTxt;
    private TextView mAddressTxt;
    private RecyclerView mRecyclerView;
    private View mBottomLayout;
    private View mLeftPointImg;
    private View mRightPointImg;
    private ImageView mIvHeadSlide;
    private TextView mTvHeadAddress;
    private ImageView mIvHeadChat;
    private ImageView mIvIcon;
    private ImageView mIvRadarBg;
    private ImageView mIvBlurBg;
    private RelativeLayout mRlDistance;
    private ImageView mIvMinus;
    private ImageView mIvPlus;
    private ImageView mIvCenterPlus;
    private ProgressBar mPbDistance;
    private ImageView mIvDragBtn;
    private CoverView mRadarCoverView;
    private TextView mTvRadarResult;
    private ExploreHelper mHelper;
    private RelativeLayout.LayoutParams mParams;
    private int mBgArray[] = new int[]{R.mipmap.radar_main_bg, R.mipmap.radar_main_bg_2};
    private int mBgImageIdx = 0;
    //记录是否雷达盘托起
    private boolean isUp = false;
    private HeadViewBuilder mBuilder = null;
    private ArrayList<RadarPeople> mPeopleList = new ArrayList<>();//人
    private ArrayList<RadarEvent> mEventList = new ArrayList<>();//活动
    private ArrayList<RadarEvent> mCouponsList = new ArrayList<>();//优惠券

    //保存拖拽控件的leftmargin、bottommargin
    private int mLeft = 0;
    private int mBottom = 0;
    private long mLastRefreshMills;//上一次刷新时间
    private double minDistance;
    private double maxDistance;
    private double mLength;
    private int mScreenWidth;
    private int mScreenHeight;
    private CommonHorizontalDecoration mDecoration;
    private RadarDataRequest mRadarDataRequest;//雷达数据接口
    private GetMyEventRequest mCouponsRequest;//优惠券接口
    private QuickAdapter<RadarPeople> mAdapter;
    private QuickAdapter<RadarEvent> mEventAdapter;
    private UserInfo mUserInfo;
    private boolean isPull;
    private double mCoverDistance = 10;//判定为爹加信标的距离(人物信标)
    private MsgReceiver mMsgReceiver;
    private Dialog dialog;

    private BitmapDrawable mBgDrawableOne;
    private BitmapDrawable mBgDrawableTwo;
    private BitmapDrawable mBlurDrable;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_radar_explore;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void loadData() {
        dialog = DialogUtil.createLoadingDialog(getContext());
        dialog.show();
        mHelper = ExploreHelper.getInstance();
        mBuilder = new HeadViewBuilder(getActivity());
        mUserInfo = AccountManager.getInstance().getUser();
        initView();
        ExploreComponent.shareComponent().setExploreComponent(this);
        setSensor();
        initEvent();
    }

    @Override
    protected void attachAllMessage() {
        super.attachAllMessage();
        NearApplication.mInstance.getMessagePump().register(com.brotherhood.o2o.message.Message.Type.ADDRESS_CHANGED, this);
        NearApplication.mInstance.getMessagePump().register(com.brotherhood.o2o.message.Message.Type.RADAR_REFRESH_FINISH, this);
        NearApplication.mInstance.getMessagePump().register(com.brotherhood.o2o.message.Message.Type.USER_DATA_CHANGE, this);
        NearApplication.mInstance.getMessagePump().register(com.brotherhood.o2o.message.Message.Type.NETWORK_CHANGE, this);
        NearApplication.mInstance.getMessagePump().register(com.brotherhood.o2o.message.Message.Type.UPDATE_ADDRESS_FAILED, this);
        NearApplication.mInstance.getMessagePump().register(com.brotherhood.o2o.message.Message.Type.USER_LOGIN_SUCCESS, this);
    }

    @Override
    public void onReceiveMessage(com.brotherhood.o2o.message.Message message) {
        super.onReceiveMessage(message);
        switch (message.type) {
            case UPDATE_ADDRESS_FAILED:
                if (!isAdded()) {
                    return;
                }
                mTipsTxt.setVisibility(View.GONE);
                break;
            case NETWORK_CHANGE://网络状态改变
                if (!isAdded()) {
                    return;
                }
                NetworkState state = (NetworkState) message.data;
                if (state == null) {
                    return;
                }
                if (state == NetworkState.UNAVAILABLE) {
                    mTipsTxt.setVisibility(View.GONE);
                    mTipsImg.setVisibility(View.GONE);
                }
                break;
            case ADDRESS_CHANGED://定位地址发生变化
                if (!isAdded()) {
                    return;
                }
                mTipsTxt.setText(getString(R.string.explorefragment_update));
                if (isPull) {
                    changeBackground();
                }
                requestData();
                TaskExecutor.scheduleTaskOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTipsTxt.setVisibility(View.GONE);
                    }
                }, 500);
                break;
            case RADAR_REFRESH_FINISH://刷新成功
                break;
            case USER_DATA_CHANGE://更新用户数据
            case USER_LOGIN_SUCCESS://登录成功
                UserInfo user = AccountManager.getInstance().getUser();
                if (user != null && !TextUtils.isEmpty(user.mIcon)) {
                    ImageLoaderManager.displayCircleImageByUrl(getActivity(), mIvIcon, user.mIcon, R.mipmap.ic_msg_default);
                }
                if (getActivity() != null && dialog != null) {
                    dialog.cancel();
                }
                break;
        }
    }

    private void initView() {
        mRadarLayout = (DirectLayout) findView(R.id.rlRadarDirectLayout);
        LayoutInflater.from(getActivity()).inflate(R.layout.radar_center_view, mRadarLayout, true);
        mIvCenterPlus = (ImageView) findView(R.id.ivRadarPlus);
        mScanImg = (ImageView) findView(R.id.ivRadarScan);
        //mRadarWaveImg = (ImageView) findView(R.id.ivRadarOutWave);
        mTipsLayout = findView(R.id.llRadarRenewTip);
        mTipsImg = (ImageView) findView(R.id.ivRadarRenewTip);
        mTipsTxt = (TextView) findView(R.id.tvRadarRenewTip);
        mAddressTxt = (TextView) findView(R.id.tvRadarHeadTitle);
        mRecyclerView = (RecyclerView) findView(R.id.rvRadarBottom);
        mBottomLayout = findView(R.id.llRadarBottom);
        mLeftPointImg = findView(R.id.ivRadarHeadLeftTip);
        mRightPointImg = findView(R.id.ivRadarHeadRightTip);
        mIvHeadSlide = (ImageView) findView(R.id.ivRadarHeadLeft);
        mTvHeadAddress = (TextView) findView(R.id.tvRadarHeadTitle);
        mIvHeadChat = (ImageView) findView(R.id.ivRadarHeadRight);
        mIvIcon = (ImageView) findView(R.id.ivRadarIcon);
        mIvRadarBg = (ImageView) findView(R.id.ivRadarMainBg);
        mIvBlurBg = (ImageView) findView(R.id.ivRadarBlurBg);
        mRlDistance = (RelativeLayout) findView(R.id.rlRadarDistance);
        mIvMinus = (ImageView) findView(R.id.ivRadarDistanceMinus);
        mIvPlus = (ImageView) findView(R.id.ivRadarDistancePlus);
        mPbDistance = (ProgressBar) findView(R.id.pbRadarDistance);
        mIvDragBtn = (ImageView) findView(R.id.ivRadarDragBtn);
        mRadarCoverView = (CoverView) findView(R.id.cvRadarCoverView);
        mTvRadarResult = (TextView) findView(R.id.tvRadarScanResult);
        mIvHeadSlide.setOnClickListener(this);
        mTvHeadAddress.setOnClickListener(this);
        mIvHeadChat.setOnClickListener(this);
        mIvIcon.setOnClickListener(this);
        mIvMinus.setOnClickListener(this);
        mIvPlus.setOnClickListener(this);

        mBgDrawableOne = (BitmapDrawable) getResources().getDrawable(R.mipmap.radar_main_bg);
        mBgDrawableTwo = (BitmapDrawable) getResources().getDrawable(R.mipmap.radar_main_bg_2);
        //初始化背景图片
        Random num = new Random();
        mBgImageIdx = num.nextInt(mBgArray.length);
        mIvBlurBg.setDrawingCacheEnabled(true);
        applyBlur();
        //初始化滑块
        mPbDistance.setMax(100);
        mPbDistance.setProgress(50);

        mDecoration = new CommonHorizontalDecoration(10, 5, 0, 5, 0);
        mScreenWidth = DisplayUtil.getScreenWidth(getActivity());
        mScreenHeight = DisplayUtil.getScreenHeight(getActivity());

        mRadarLayout.setClipChildren(false);
        mRadarLayout.getLayoutParams().width = Constants.SCREEN_WIDTH * 15 / 16;
        mRadarLayout.getLayoutParams().height = Constants.SCREEN_WIDTH * 15 / 16;

        //mRadarWaveImg.getLayoutParams().height = (int) (1.5 * Constants.SCREEN_WIDTH);
        //mRadarWaveImg.getLayoutParams().width = (int) (1.5 * Constants.SCREEN_WIDTH);

        LocationInfo addressInfo = LocationManager.getInstance().getCachedCurrentAddressOrNil();
        if (addressInfo != null) {
            mAddressTxt.setText(addressInfo.mBuildingName);
            requestData();
        } else {
            mAddressTxt.setText(R.string.radar_getting_address);
            LocationManager.getInstance().updateCurrentAddress();
        }
        setRadarLayoutTouchListener();

        //设置雷达盘中间头像
        if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.mIcon)) {
            ImageLoaderManager.displayCircleImageByUrl(getActivity(), mIvIcon, mUserInfo.mIcon, R.mipmap.ic_msg_default);
        }

        //注册消息广播接收器
        registerReceiver();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRadarHeadLeft:
                onMenuImageClick();
                break;
            case R.id.tvRadarHeadTitle:
                OnAddressClick();
                break;
            case R.id.ivRadarHeadRight:
                OnMsgListBtnClick();
                break;
            case R.id.ivRadarIcon:
                onFoodListClick();
                break;
            case R.id.ivRadarDistanceMinus:
                minusDistance();
                break;
            case R.id.ivRadarDistancePlus:
                plusDistance();
                break;
        }
    }

    /**
     * 人物
     *
     * @param datas
     */
    private void initPeopleRecyclerView(List<RadarPeople> datas) {

        if (mAdapter == null) {
            WrapLinearLayoutManage mLinearLayoutManager = new WrapLinearLayoutManage(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setHasFixedSize(true);

            mAdapter = new QuickAdapter<RadarPeople>(getActivity(), R.layout.radar_people_recylerview_item) {
                @Override
                protected void onBindViewHolder(final BaseAdapterHelper helper, final RadarPeople radarPeople, int position) {
                    if (TextUtils.isEmpty(radarPeople.mAvatar)) {
                        helper.displayImageByResource(R.id.ivPeopleItemHead, R.mipmap.img_default);
                    } else {
                        helper.displayImageByUrl(R.id.ivPeopleItemHead, radarPeople.mAvatar, R.mipmap.img_default);
                    }
                    helper.setText(R.id.tvPeopleName, radarPeople.mNickname);
                    if (radarPeople.mDistance < 50) {
                        helper.setText(R.id.tvPeopleDistance, "<50m");
                    } else {
                        helper.setText(R.id.tvPeopleDistance, DistanceFormatUtil.format(getActivity(), radarPeople.mDistance));
                    }
                    if (radarPeople.mGender == 0) {//男
                        helper.displayImageByResource(R.id.ivPeopleSex, R.mipmap.sex_male_white);
                        helper.setBackgroundRes(R.id.ivPeopleSex, R.drawable.sex_male_rect_bg);
                    } else {
                        helper.displayImageByResource(R.id.ivPeopleSex, R.mipmap.sex_female_white);
                        helper.setBackgroundRes(R.id.ivPeopleSex, R.drawable.sex_female_rect_bg);
                    }
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OtherUserDetailActivity.show(getActivity(), radarPeople.mUid + "");
                        }
                    });
                }
            };
            mRecyclerView.setAdapter(mAdapter);
            if (mDecoration != null) {
                mRecyclerView.removeItemDecoration(mDecoration);
                mRecyclerView.addItemDecoration(mDecoration);
            }

            mAdapter.addAll(datas);
        } else {
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.set(datas);
            mAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 活动
     *
     * @param datas
     */
    private void initEventRecyclerView(List<RadarEvent> datas) {
        if (mEventAdapter == null) {
            WrapLinearLayoutManage mLinearLayoutManager = new WrapLinearLayoutManage(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mEventAdapter = new QuickAdapter<RadarEvent>(getActivity(), R.layout.radar_event_recylerview_item) {
                @Override
                protected void onBindViewHolder(BaseAdapterHelper helper, RadarEvent radarEvent, int position) {
                    helper.displayImageByUrl(R.id.ivPeopleItemHead, radarEvent.mLogo, R.mipmap.img_default);
                    if (radarEvent.mPrice == 0) {
                        helper.displayImageByResource(R.id.ivEventCorner, R.mipmap.radar_event_free);
                    } else {
                        helper.displayImageByResource(R.id.ivEventCorner, R.mipmap.radar_event_charge);
                    }
                    helper.setText(R.id.tvPeopleName, radarEvent.mTitle);
                    helper.setText(R.id.tvEventLineMiddle, getResources().getString(R.string.radar_event_item_supplier, radarEvent.mSupplier));
                    helper.setText(R.id.tvEventLineBottom, getResources().getString(R.string.radar_event_item_place, radarEvent.mPlace));
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WebViewActivity.show(getActivity(), "http://www.sina.com/");
                        }
                    });
                }
            };
            mRecyclerView.setAdapter(mEventAdapter);
            if (mDecoration != null) {
                mRecyclerView.removeItemDecoration(mDecoration);
                mRecyclerView.addItemDecoration(mDecoration);
            }
            mEventAdapter.addAll(datas);
        } else {
            mRecyclerView.setAdapter(mEventAdapter);
            mEventAdapter.set(datas);
            mEventAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 处理雷达上拉、下拉事件
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case Constants.SHOW_RADAR_PULL_DOWM://下拉雷达盘
                        if (mIvRadarBg.getVisibility() != View.VISIBLE) {
                            mIvRadarBg.setVisibility(View.VISIBLE);
                        }
                        setPullDown();
                        break;
                    case Constants.SHOW_RADAR_BACK_UP://雷达盘从下拉中回到原本位置
                        setBackUpTips();
                        break;
                    case Constants.RADAR_FINDING_AROUND_PEOPLE:
                        setPullCompelete();
                        break;
                    case Constants.RADAR_PULL_DOWN_DISTANCE_SHORT:
                        setPullClear();
                        break;
                    case Constants.SHOW_RADAR_PULL_UP:
                        if (mIvRadarBg.getVisibility() != View.VISIBLE) {
                            mIvRadarBg.setVisibility(View.VISIBLE);
                        }
                        break;
                    case Constants.HIDE_RESULT_VIEW:
                        if (mHelper == null || !isAdded() || !isUp || mBuilder == null) {
                            return;
                        }
                        mHelper.hideResultAnimation(mBottomLayout, mRadarLayout, new ExploreHelper.OnResultHiddenListener() {
                            @Override
                            public void onHidden() {
                                mBuilder.restoreHeadViewPoint(mPeopleList);
                                mBottomLayout.setVisibility(View.GONE);
                                mTvRadarResult.setVisibility(View.VISIBLE);
                                mIvDragBtn.setVisibility(View.VISIBLE);
                                mRlDistance.setVisibility(View.VISIBLE);
                                isUp = false;
                                //mHelper.showDragBtnAnimation(mIvDragBtn, mTvRadarResult, mRlDistance);
                            }
                        });
                        break;
                    case Constants.UPDATE_ALPHA_VALUE://透明度变化
                        if (msg != null && msg.obj instanceof Integer) {
                            int distance = (Integer) msg.obj;
                            changeAlpha(distance);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void changeAlpha(int scrollDistance) {
        ViewHelper.setAlpha(mIvBlurBg, 0.0f);
        if (Math.abs(scrollDistance) > 10) {
            double alphaDelt = (double) Math.abs(scrollDistance) / 400;
            if (alphaDelt > 1.0) {
                alphaDelt = 1.0f;
            } else if (alphaDelt < 0.0) {
                alphaDelt = 0.0f;
            }
            float value = Float.parseFloat(alphaDelt + "");
            float iValue = 1.0f - value;
            ViewHelper.setAlpha(mIvBlurBg, iValue);
            ViewHelper.setAlpha(mIvDragBtn, iValue);
            ViewHelper.setAlpha(mPbDistance, iValue);
            ViewHelper.setAlpha(mIvPlus, iValue);
            ViewHelper.setAlpha(mIvMinus, iValue);
            ViewHelper.setAlpha(mTvRadarResult, iValue);
        } else {
            ViewHelper.setAlpha(mIvBlurBg, 1f);
            ViewHelper.setAlpha(mIvDragBtn, 1f);
            ViewHelper.setAlpha(mPbDistance, 1f);
            ViewHelper.setAlpha(mIvPlus, 1f);
            ViewHelper.setAlpha(mIvMinus, 1f);
            ViewHelper.setAlpha(mTvRadarResult, 1f);
        }
    }

    public View getRadarContentView() {
        return mRadarLayout;
    }

    /**
     * 更换背景
     */
    private void changeBackground() {
        if (System.currentTimeMillis() - mLastRefreshMills > 5 * 1000) {
            mLastRefreshMills = System.currentTimeMillis();
            mBgImageIdx++;
            mBgImageIdx %= mBgArray.length;
            int idx = mBgImageIdx % 2;
            if (idx == 0) {
                mIvRadarBg.setImageDrawable(mBgDrawableOne);
                mIvBlurBg.setImageDrawable(mBgDrawableOne);
            } else if (idx == 1) {
                mIvRadarBg.setImageDrawable(mBgDrawableTwo);
                mIvBlurBg.setImageDrawable(mBgDrawableTwo);
            }
            applyBlur();
        }
    }

    /**
     * 模糊效果
     */
    private void applyBlur() {
        mIvBlurBg.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        try {
                            /**
                             * 1、getDrawingCache()方法会自动先调用 buildDrawableCache(),再返回生成的图，所以不需要主动调用build..以免生成两次图片
                             * 2、buildDrawingCache建立drawingCache的同时，会将上次的DrawingCache回收掉，
                             *    在源码中buildDrawingCache 会调用destroyDrawingCache方法对之前的DrawingCache回收,所以不需要主动回收cacheBitmap，以免try to use recycled bitmap
                             */
                            Bitmap cacheBitmap = mIvBlurBg.getDrawingCache();
                            mBlurDrable = new BitmapDrawable(getResources(), FastBlur.blur(cacheBitmap, mIvBlurBg));
                            if (mBlurDrable != null) {
                                mIvBlurBg.setImageDrawable(mBlurDrable);
                            }

                            mIvBlurBg.getViewTreeObserver().removeOnPreDrawListener(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return true;
                        }
                        return true;
                    }
                });
    }


    /**
     * 下拉、上拉
     */
    private void setPullDown() {
        if (isAdded()) {
            mTipsLayout.setVisibility(View.VISIBLE);
            mTipsImg.setImageResource(R.mipmap.radar_renovate_down);
            mTipsImg.setVisibility(View.VISIBLE);
            mTipsTxt.setText(getString(R.string.explorefragment_pulltorefresh));
            mTipsTxt.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 从下拉状态回到原本位置
     */
    private void setBackUpTips() {
        if (isAdded()) {
            if (!NetworkStateManager.isNetworkAvailable()) {
                mTipsTxt.setVisibility(View.GONE);
                mTipsImg.setVisibility(View.GONE);
                return;
            }
            mTipsLayout.setVisibility(View.VISIBLE);
            mTipsImg.setImageResource(R.mipmap.radar_renovate_up);
            mTipsImg.setVisibility(View.VISIBLE);
            mTipsTxt.setText(getString(R.string.explorefragment_relesetorefresh));
            mTipsTxt.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 下拉完成，更新当前地理位置信息
     */
    private void setPullCompelete() {
        if (isAdded()) {
            if (!NetworkStateManager.isNetworkAvailable()) {
                ColorfulToast.orange(getActivity(), getString(R.string.connect_network_timeout), Toast.LENGTH_SHORT);
                return;
            }
            mTipsImg.setVisibility(View.GONE);
            mTipsTxt.setText(getString(R.string.explorefragment_finding));
            mTipsTxt.setVisibility(View.VISIBLE);
            LocationManager.getInstance().updateCurrentAddress();
            isPull = true;
        }
    }


    private void setPullClear() {
        if (isAdded()) {
            mTipsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 过滤叠加的人物信标
     *
     * @param peoples        数据源
     * @param filterDistance 过滤条件 距离小于filterDistance
     * @return 过滤后的数据
     */
    private List<RadarPeople> filterPeople(List<RadarPeople> peoples, double filterDistance) {
        List<RadarPeople> filterList = new ArrayList<>();
        for (int i = peoples.size() - 1; i >= 0; i--) {
            RadarPoi people = peoples.get(i);
            if (!(people instanceof RadarPeople)) {
                break;
            }
            if (filterList.contains(people)) {
                continue;
            }

            //List<RadarPeople> peopleList = new ArrayList<>();
            for (int j = peoples.size() - 1; j >= 0; j--) {
                RadarPoi otherPeople = peoples.get(j);
                if (people.equals(otherPeople) || filterList.contains(otherPeople)) {
                    continue;
                }
                LocationInfo peopleLocation = people.mLocation;
                LocationInfo otherLocation = otherPeople.mLocation;
                double distance = DistanceFormatUtil.getGoogleMapDistance(new LatLng(peopleLocation.mLatitude, peopleLocation.mLongitude), new LatLng(otherLocation
                        .mLatitude, otherLocation.mLongitude));
                if (distance <= filterDistance) {
                    ((RadarPeople) otherPeople).isCovered = true;
                    //peopleList.add((RadarPeople) otherPeople);
                    filterList.add((RadarPeople) otherPeople);
                }
            }
            //if (!peopleList.isEmpty()) {
            //    ((RadarPeople) people).mCoverPeopleList = peopleList;
            //}
        }
        //if (!filterList.isEmpty()) {
        //    for (RadarPeople filtePeople : filterList) {
        //        if (peoples.contains(filtePeople)) {
        //            peoples.remove(filtePeople);
        //        }
        //    }
        //}
        return peoples;
    }

    private boolean isBusy = false;

    /**
     * 请求雷达数据
     */
    private void requestData() {
        if (LocationManager.getInstance().getCachedCurrentAddressOrNil() == null) {
            return;
        }
        if (mRadarDataRequest == null) {
            mRadarDataRequest = RadarDataRequest.createRadarDataRequest(new OnResponseListener<RadarItem>() {
                @Override
                public void onSuccess(int code, String msg, final RadarItem radarItem, boolean cache) {
                    mHelper.removeAllPois(mRadarLayout);
                    TaskExecutor.executeTask(new Runnable() {
                        @Override
                        public void run() {
                            mPeopleList.clear();
                            mEventList.clear();
                            mCouponsList.clear();
                            if (isPull) {
                                NearApplication.mInstance.getMessagePump().broadcastMessage(com.brotherhood.o2o.message.Message.Type.RADAR_REFRESH_FINISH);
                                isPull = false;
                            }
                            //计算人物信标直接的距离，重叠的信标用单个点代替
                            if (radarItem != null && radarItem.mPeopleList != null && !radarItem.mPeopleList.isEmpty()) {
                                Collections.sort(radarItem.mPeopleList, new Comparator<RadarPeople>() {
                                    @Override
                                    public int compare(RadarPeople lhs, RadarPeople rhs) {
                                        if (lhs.mDistance > rhs.mDistance) {
                                            return 1;
                                        }
                                        if (lhs.mDistance < rhs.mDistance) {
                                            return -1;
                                        }
                                        return 0;
                                    }
                                });
                                double minDistance = radarItem.mPeopleList.get(0).mDistance;
                                double maxDistance = radarItem.mPeopleList.get(radarItem.mPeopleList.size() - 1).mDistance;
                                /**
                                 *  判定为重叠的距离 / (最大距离 - 最小距离)  = 人物信标宽度 / 雷达盘半径大小
                                 */
                                mCoverDistance = Math.abs(maxDistance - minDistance) * DisplayUtil.dp2px(24) / (Constants.SCREEN_WIDTH * 15 / 32);

                                List<RadarPeople> filterPeopleList = filterPeople(radarItem.mPeopleList, mCoverDistance);
                                //mPeopleList.addAll(filterPeopleList);
                                mPeopleList.addAll(radarItem.mPeopleList);
                                Collections.sort(mPeopleList, new Comparator<RadarPeople>() {
                                    @Override
                                    public int compare(RadarPeople lhs, RadarPeople rhs) {
                                        if (lhs.mDistance > rhs.mDistance) {
                                            return 1;
                                        }
                                        if (lhs.mDistance < rhs.mDistance) {
                                            return -1;
                                        }
                                        return 0;
                                    }
                                });
                            }
                            if (radarItem != null && radarItem.mEventList != null) {
                                mEventList.addAll(radarItem.mEventList);
                                Collections.sort(mEventList, new Comparator<RadarEvent>() {
                                    @Override
                                    public int compare(RadarEvent lhs, RadarEvent rhs) {
                                        if (lhs.mDistance < rhs.mDistance) {
                                            return -1;
                                        } else if (lhs.mDistance == rhs.mDistance) {
                                            return 0;
                                        }
                                        return 1;
                                    }
                                });
                            }
                            if (radarItem != null && radarItem.mWebEventList != null) {
                                mCouponsList.addAll(radarItem.mWebEventList);
                            }

                            int tempMinDistance = 0;
                            int tempMaxDistance = 0;
                            if (!mPeopleList.isEmpty()) {
                                tempMinDistance = (int) mPeopleList.get(0).mDistance;
                                tempMaxDistance = (int) mPeopleList.get(mPeopleList.size() - 1).mDistance;
                            }

                            //todo 屏蔽活动信标的最大最小距离
                            //if (!mEventList.isEmpty()) {
                            //    if (tempMinDistance > mEventList.get(0).mDistance) {
                            //        tempMinDistance = (int) mEventList.get(0).mDistance;
                            //    }
                            //    if (tempMaxDistance < mEventList.get(mEventList.size() - 1).mDistance) {
                            //        tempMaxDistance = (int) mEventList.get(mEventList.size() - 1).mDistance;
                            //    }
                            //}

                            minDistance = tempMinDistance;
                            Constants.dSmallestDistance = minDistance;
                            maxDistance = tempMaxDistance;

                            mLength = maxDistance - minDistance;
                            TaskExecutor.runTaskOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    double distance = mPbDistance.getProgress() * (maxDistance - minDistance) / 100;
                                    showDefaultInfo(distance);
                                    LocationInfo addressInfo = LocationManager.getInstance().getCachedCurrentAddressOrNil();
                                    mAddressTxt.setText(addressInfo.mBuildingName);

                                    mHelper.reDrawHeadInfo(mScanImg, mRadarLayout, mPeopleList);
                                    //todo 屏蔽活动信标
                                    //mHelper.reDrawEventInfo(mScanImg, mRadarLayout, mEventList);
                                    mHelper.reDrawWebEventInfo(mScanImg, mRadarLayout, mCouponsList);
                                    Animation scanAnim = mScanImg.getAnimation();
                                    if (scanAnim == null || !scanAnim.hasStarted()) {
                                        mHelper.startScanAnim(mScanImg);
                                    }
                                    isBusy = false;
                                }
                            });
                        }
                    });

                }

                @Override
                public void onFailure(int code, String msg) {
                    isBusy = false;
                    isPull = false;
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            });
        }
        if (!isBusy) {
            mRadarDataRequest.sendRequest();
            isBusy = true;
        }
    }

    private boolean isIntervalTouch = true;

    /**
     * 雷达盘触摸监听，实现点击信标功能
     */
    private void setRadarLayoutTouchListener() {
        mRadarLayout.setOnTouchListener(new View.OnTouchListener() {
            int lastRadarX;
            int lastRadarY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (isIntervalTouch) {
                            isIntervalTouch = false;
                            lastRadarX = (int) event.getX();
                            lastRadarY = (int) event.getY();
                            if (!showUpdateUserInfo(lastRadarX, lastRadarY)) {
                                mBuilder.restoreHeadViewPoint(mPeopleList);
                                isCouponSelected(lastRadarX, lastRadarY);
                                // TODO: 2016/1/15 暂时屏蔽活动数据
                                //boolean isSelected = isEventSelected(lastRadarX, lastRadarY);
                                //if (!isSelected) {
                                boolean isSelected = isPeopleSelected(lastRadarX, lastRadarY);
                                //}
                                if (!isSelected && isUp) {//无选中图层，且recyclerview为展开状态，则隐藏recyclerview
                                    //显示拖拽
                                    mHelper.hideResultAnimation(mBottomLayout, mRadarLayout, new ExploreHelper.OnResultHiddenListener() {
                                        @Override
                                        public void onHidden() {
                                            mBottomLayout.setVisibility(View.GONE);
                                            mTvRadarResult.setVisibility(View.VISIBLE);
                                            mIvDragBtn.setVisibility(View.VISIBLE);
                                            mRlDistance.setVisibility(View.VISIBLE);
                                            isUp = false;
                                            //mHelper.showDragBtnAnimation(mIvDragBtn, mTvRadarResult, mRlDistance);
                                        }
                                    });
                                }
                                return isSelected;
                            } else {
                                isIntervalTouch = true;
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    // 判断是否滑动到需要更新的距离
    public boolean showUpdateUserInfo(int x, int y) {
        double distance = Math.sqrt(Math.abs(x - Constants.SCREEN_WIDTH / 2)
                * Math.abs(x - Constants.SCREEN_WIDTH / 2)
                + Math.abs(y - Constants.SCREEN_WIDTH / 2)
                * Math.abs(y - Constants.SCREEN_WIDTH / 2));
        if (distance < Constants.SCREEN_WIDTH / 8) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 选中活动信标，选中活动与选中人物不重叠
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isEventSelected(int x, int y) {
        ArrayList<RadarEvent> selectEventDatas = new ArrayList<>();
        if (mEventList != null && !mEventList.isEmpty()) {
            RadarEvent data = mEventList.get(0);
            double xValue = data.mPosX;
            double yValue = data.mPosY;
            float poiWidth = getResources().getDimension(R.dimen.radar_server_width);
            float poiHeight = getResources().getDimension(R.dimen.radar_server_height);

            double minXValue = xValue;
            double maxXValue = xValue + poiWidth;
            double minYValue = yValue;
            double maxYValue = yValue + poiHeight;

            if (x >= minXValue && x <= maxXValue && y >= minYValue && y <= maxYValue && data.mHeadView.getVisibility() == View.VISIBLE) {
                int count = mHelper.getEventCount(mEventList);
                for (int i = 0; i < count; i++) {
                    selectEventDatas.add(mEventList.get(i));
                }
            }
        }
        if (selectEventDatas != null && !selectEventDatas.isEmpty()) {
            if (isUp) {
                mHelper.showRecycleViewAnim(mRecyclerView);
            } else {
                isUp = true;
                mHelper.showBottomLayout(mBottomLayout, mRadarLayout, mRecyclerView);
                //隐藏拖拽
                mTvRadarResult.setVisibility(View.GONE);
                mIvDragBtn.setVisibility(View.GONE);
                mRlDistance.setVisibility(View.GONE);
            }
            initEventRecyclerView(selectEventDatas);
            isIntervalTouch = true;
            return true;
        } else {
            isIntervalTouch = true;
            return false;
        }
    }

    /**
     * 选中人物信标
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isPeopleSelected(int x, int y) {
        ArrayList<RadarPeople> selectPeopleDatas = new ArrayList<>();
        if (mPeopleList != null && !mPeopleList.isEmpty()) {
            for (RadarPeople data : mPeopleList) {
                double xValue = data.mPosX;
                double yValue = data.mPosY;
                if (Math.abs(x - xValue) <= Constants.VALID_VALUE && Math.abs(y - yValue) <= Constants.VALID_VALUE && data.mHeadView.getVisibility() == View.VISIBLE) {
                    mBuilder.clickHeadViewPoint(data.mHeadView, data.mGender);
                    selectPeopleDatas.add(data);
                    //if (data.mCoverPeopleList != null && !data.mCoverPeopleList.isEmpty()) {
                    //    selectPeopleDatas.addAll(data.mCoverPeopleList);
                    //}
                }
            }
        }
        if (selectPeopleDatas != null && !selectPeopleDatas.isEmpty()) {
            if (isUp) {
                mHelper.showRecycleViewAnim(mRecyclerView);
            } else {
                mHelper.showBottomLayout(mBottomLayout, mRadarLayout, mRecyclerView);
                isUp = true;
                //隐藏拖拽
                mTvRadarResult.setVisibility(View.GONE);
                mIvDragBtn.setVisibility(View.GONE);
                mRlDistance.setVisibility(View.GONE);
            }
            initPeopleRecyclerView(selectPeopleDatas);
            isIntervalTouch = true;
            return true;
        } else {
            isIntervalTouch = true;
            return false;
        }
    }

    /**
     * 点击优惠券
     *
     * @param x
     * @param y
     * @return
     */
    public void isCouponSelected(int x, int y) {
        if (mPeopleList != null && !mCouponsList.isEmpty()) {
            for (RadarEvent data : mCouponsList) {
                double xValue = data.mPosX;
                double yValue = data.mPosY;
                float poiWidth = getResources().getDimension(R.dimen.radar_server_width);
                float poiHeight = getResources().getDimension(R.dimen.radar_server_height);

                double minXValue = xValue;
                double maxXValue = xValue + poiWidth;
                double minYValue = yValue;
                double maxYValue = yValue + poiHeight;

                if (x >= minXValue && x <= maxXValue && y >= minYValue && y <= maxYValue && data.mHeadView.getVisibility() == View.VISIBLE) {
                    WebViewActivity.show(getActivity(), data.mWebEventUrl);
                }
            }
        }
    }

    /**
     * 保持信标不随雷达盘转动
     */
    private void rotateRadarPois(float rotation) {
        if (isBusy) {//获取雷达数据的过程，不
            return;
        }
        if (mEventList != null && !mEventList.isEmpty()) {
            for (RadarPoi item : mEventList) {
                if (item.mHeadView != null) {
                    ViewHelper.setRotation(item.mHeadView, rotation);
                }
            }
        }
        if (mCouponsList != null && !mCouponsList.isEmpty()) {
            for (RadarPoi item : mCouponsList) {
                if (item.mHeadView != null) {
                    ViewHelper.setRotation(item.mHeadView, rotation);
                }
            }
        }
    }

    //传感器
    private SensorManager mSensorManager;//传感器管理器
    private Sensor mSensor;//方向
    private float mLastValue;
    //private Sensor aSensor = null;//加速度感应器
    //private Sensor mSensor = null;//磁场感应器
    //private float[] accelerometerValues = new float[3];//从加速度感应器获取来的数据
    //private float[] magneticFieldValues = new float[3];//从磁场感应器获取来的数据
    //private float[] mValues = new float[3];//输出数据[方向角、倾斜角、旋转角]
    //private float[] mFilterValues = new float[3];//滤波器过滤后输出数据[方向角、倾斜角、旋转角]
    //private float[] mRotationMatrix = new float[9];//旋转矩阵
    /**
     * 传感器监听
     */
    private SensorEventListener mSensorListener = new SensorEventListener() {
        AngleLowpassFilter filter = new AngleLowpassFilter();

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!isAdded()) {
                return;
            }
            //if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //    accelerometerValues = event.values;
            //}
            //if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //    magneticFieldValues = event.values;
            //}
            //SensorManager.getRotationMatrix(mRotationMatrix, null, accelerometerValues, magneticFieldValues);//调用getRotaionMatrix获得变换矩阵r[]
            //SensorManager.getOrientation(mRotationMatrix, mValues);//经过SensorManager.getOrientation(r, values);得到的values值为弧度，转换为角度
            //lowPass(mValues, mFilterValues);
            //final float degree = (float) Math.toDegrees(mFilterValues[0]);

            final float degree;
            long animTime = 500;
            if (Build.BRAND.equalsIgnoreCase("samsung") && Build.VERSION.SDK_INT == 19) {
                filter.add((float) Math.toRadians(event.values[0]));
                if (filter.isReady()) {
                    degree = (float) Math.toDegrees(filter.average());
                } else {
                    degree = event.values[0];
                }
                animTime = 500;
            } else {
                degree = event.values[0];
            }
            TaskExecutor.scheduleTaskOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewHelper.setRotation(getRadarContentView(), -degree);
                    ViewHelper.setRotation(mIvIcon, degree);
                    ViewHelper.setRotation(mIvCenterPlus, degree);
                    rotateRadarPois(degree);
                    mLastValue = degree;
                }
            }, animTime);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private void setSensor() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
        Animation scanAnim = mScanImg.getAnimation();
        if (scanAnim != null && scanAnim.hasStarted()) {
            mScanImg.clearAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        hasUnReadLatestMsg();
        //重新绘点
        if (mHelper == null || mScanImg == null || mRadarLayout == null || mPeopleList == null || mEventList == null) {
            return;
        }
        Animation scanAnim = mScanImg.getAnimation();
        if (scanAnim == null || !scanAnim.hasStarted()) {
            mHelper.startScanAnim(mScanImg);
        }
    }


    private float ALPHA = 0.08f;

    /**
     * 一阶滞后滤波法
     * 低通滤波器  让低频通过，阻住高频，避免雷达盘抖动
     * 本次滤波结果=（1-a）*本次采样值+a*上次滤波结果 = 本次采样值 + a * (上次滤波结果 - 本次采样值)
     * 缺点： 相位滞后，灵敏度低 滞后程度取决于a值大小 不能消除滤波频率高于采样频率的1/2的干扰信号
     */
    private float lowPass(float input, float output) {
        output = output + ALPHA * (input - output);
        return output;
    }

    /**
     * 递推平均滤波法
     * 返回最近LENGTH个值的平均数，低通滤波算法之一
     */
    public static class AngleLowpassFilter {
        public final int LENGTH = 4;
        private float sumSin, sumCos;
        private ArrayDeque<Float> queue = new ArrayDeque<>();

        public void add(float radians) {
            sumSin += (float) Math.sin(radians);
            sumCos += (float) Math.cos(radians);
            queue.add(radians);
            if (queue.size() > LENGTH) {
                float old = queue.poll();
                sumSin -= Math.sin(old);
                sumCos -= Math.cos(old);
            }
        }

        public boolean isReady() {
            if (getSize() >= LENGTH) {
                return true;
            }
            return false;
        }

        public int getSize() {
            return queue.size();
        }

        public float average() {
            int size = queue.size();
            return (float) Math.atan2(sumSin / size, sumCos / size);
        }
    }


    private void hasUnReadLatestMsg() {
        IMDBLatestMsgService.hasUnReadLatestMsg(new IMDBLatestMsgService.DBListener() {
            @Override
            public void onResult(Object obj) {
                boolean has = (boolean) obj;
                if (has) {
                    visibleRightPoint();
                } else {
                    hideRightPoint();
                }
            }
        });
    }

    private void registerReceiver() {
        mMsgReceiver = new MsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IDSIMManager.ACTION_IM_ON_REC_MSG);
        filter.addAction(IDSIMManager.ACTION_IM_ON_REC_MSG_MULTI);
        getActivity().registerReceiver(mMsgReceiver, filter);
    }

    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hasUnReadLatestMsg();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NearApplication.mInstance.getMessagePump().unregister(com.brotherhood.o2o.message.Message.Type.ADDRESS_CHANGED, this);
        NearApplication.mInstance.getMessagePump().unregister(com.brotherhood.o2o.message.Message.Type.RADAR_REFRESH_FINISH, this);
        NearApplication.mInstance.getMessagePump().unregister(com.brotherhood.o2o.message.Message.Type.USER_DATA_CHANGE, this);
        NearApplication.mInstance.getMessagePump().unregister(com.brotherhood.o2o.message.Message.Type.NETWORK_CHANGE, this);
        NearApplication.mInstance.getMessagePump().unregister(com.brotherhood.o2o.message.Message.Type.UPDATE_ADDRESS_FAILED, this);
        NearApplication.mInstance.getMessagePump().unregister(com.brotherhood.o2o.message.Message.Type.USER_LOGIN_SUCCESS, this);
        if (mMsgReceiver != null) {
            getActivity().unregisterReceiver(mMsgReceiver);
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (mIvBlurBg != null) {
            mIvBlurBg.setImageDrawable(null);
            mIvBlurBg = null;
        }
        if (mIvRadarBg != null) {
            mIvRadarBg.setImageDrawable(null);
        }
        if (mBgDrawableOne != null) {
            mBgDrawableOne = null;
        }
        if (mBgDrawableTwo != null) {
            mBgDrawableTwo = null;
        }

        if (mBlurDrable != null) {
            mBlurDrable = null;
        }
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    /**
     * SlideMenu的展示于关闭
     */
    public void onMenuImageClick() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity act = (MainActivity) getActivity();
            act.toggleSlideMenu();
        }
    }

    public void onFoodListClick() {
        int idx = mBgImageIdx % 2;
        int drawableId = 0;
        if (idx == 0) {
            drawableId = R.mipmap.radar_main_bg;
        } else {
            drawableId = R.mipmap.radar_main_bg_2;
        }
        NearbyServiceActivity.show(getActivity(), drawableId);
    }

    /**
     * 显示选择地址界面
     */
    public void OnAddressClick() {
        if (isAdded()) {
            //SetLocationActivity.show(getActivity());//显示选择地址的页面
        }
    }

    /**
     * 跳转到消息界面
     */
    public void OnMsgListBtnClick() {
        Intent intent = new Intent(getActivity(), MyMessageActivity.class);
        startActivity(intent);
    }


    private void initEvent() {
        mParams = (RelativeLayout.LayoutParams) mIvDragBtn.getLayoutParams();
        mParams.leftMargin = mScreenWidth / 2 - (int) (getResources().getDimension(R.dimen.drag_btn_size) / 2);//位于中间，屏幕宽度的一半 - 自身宽度的一半
        mLeft = mParams.leftMargin;
        mBottom = mParams.bottomMargin;

        mIvDragBtn.setOnTouchListener(new View.OnTouchListener() {
            int startFingerX = 0;
            int startFingerY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startFingerX = (int) event.getRawX();
                        startFingerY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int dx = (int) (event.getRawX() - startFingerX);
                        int dy = (int) (event.getRawY() - startFingerY);
                        mParams.leftMargin = mLeft + dx;
                        mParams.bottomMargin = mBottom - dy;
                        if (mParams.leftMargin < getResources().getDimension(R.dimen.drag_btn_left_margin)) {//左边界
                            mParams.leftMargin = (int) (getResources().getDimension(R.dimen.drag_btn_left_margin));
                        }
                        if (mParams.leftMargin > mScreenWidth - (int) (getResources().getDimension(R.dimen.drag_btn_left_margin) + getResources()
                                .getDimension(R.dimen.drag_btn_size))) {//右边界
                            mParams.leftMargin = mScreenWidth - (int) (getResources().getDimension(R.dimen.drag_btn_left_margin) + getResources()
                                    .getDimension(R.dimen.drag_btn_size));
                        }

                        if (mParams.bottomMargin < getResources().getDimension(R.dimen.drag_btn_bottom_margin)) {//下边界
                            mParams.bottomMargin = (int) (getResources().getDimension(R.dimen.drag_btn_bottom_margin));
                        }

                        if (mParams.bottomMargin > mScreenHeight - (int) (getResources().getDimension(R.dimen.drag_btn_top_margin))) {//上边界
                            mParams.bottomMargin = mScreenHeight - (int) (getResources().getDimension(R.dimen.drag_btn_top_margin));
                        }
                        int progress = (int) ((v.getLeft() + (int) (getResources().getDimension(R.dimen.drag_btn_size) / 2) - mPbDistance.getLeft()) * 100.0
                                / ((mScreenWidth - (int) (2 * (getResources().getDimension(R.dimen.minus_btn_margin) + getResources().getDimension
                                (R.dimen.minus_btn_size) + getResources().getDimension(R.dimen.drag_progress_left_margin)))) * 1.0));
                        mPbDistance.setProgress(progress);
                        double distance = (maxDistance - minDistance) * progress / 100;
                        mRadarCoverView.setVisibility(View.VISIBLE);
                        showDefaultInfo(distance);
                        mHelper.moveHeadInfo();
                        //mHelper.moveEventInfo();
                        break;

                    case MotionEvent.ACTION_UP:
                        int progressUp = (int) ((v.getLeft() + (int) (getResources().getDimension(R.dimen.drag_btn_size) / 2) - mPbDistance.getLeft()) * 100.0 /
                                ((mScreenWidth - (int) (2 * (getResources().getDimension(R.dimen.minus_btn_margin) + getResources().getDimension
                                        (R.dimen.minus_btn_size) + getResources().getDimension(R.dimen.drag_progress_left_margin)))) * 1.0));
                        mPbDistance.setProgress(progressUp);
                        mRadarCoverView.setVisibility(View.GONE);
                        mParams.leftMargin = v.getLeft();
                        mParams.bottomMargin = DisplayUtil.dp2px(28);
                        v.setLayoutParams(mParams);
                        mLeft = mParams.leftMargin;
                        mBottom = mParams.bottomMargin;

                        double upDistance = (maxDistance - minDistance) * progressUp / 100;
                        showDefaultInfo(upDistance);
                        //重新绘点
                        mHelper.moveHeadInfo();
                        break;
                    default:
                        break;
                }
                //v.requestLayout();
                return true;
            }
        });
    }

    /**
     * 初始显示底部进度、及对应的扫描结果
     */
    private void showDefaultInfo(double distance) {
        if (!isAdded() || getActivity() == null) {
            return;
        }
        if (distance < 0) {
            return;
        }
        if (distance < minDistance) {
            distance = minDistance;
        }
        Constants.dLargestDistance = distance;
        int personCount = 0;
        int eventCount = 0;
        String distanceStr = "";
        for (int i = 0; i < mPeopleList.size(); i++) {
            RadarPeople people = mPeopleList.get(i);
            if ((people.mDistance - minDistance) <= distance) {
                personCount++;
                //if (people.mCoverPeopleList != null && !people.mCoverPeopleList.isEmpty()) {
                //    personCount += people.mCoverPeopleList.size();
                //}
            }
        }
        //todo 屏蔽活动数量
        //for (int j = 0; j < mEventList.size(); j++) {
        //    if (mEventList.get(j).mDistance < distance) {
        //        eventCount++;
        //    }
        //}
        String languageEnv = LanguageUtil.getEnv();
        if (distance < 50) {
            distanceStr = "< 50m";
            if (languageEnv.equals(LanguageUtil.ENV_CN) || languageEnv.equals(LanguageUtil.ENV_ZH)) {
                mTvRadarResult.setText(getResources().getString(R.string.radar_scan_result, "50m", personCount + ""/*, eventCount*/));
            } else {
                mTvRadarResult.setText(getResources().getString(R.string.radar_scan_result, personCount, "50m" + ""/*, eventCount*/));
            }
        } else {
            distanceStr = DistanceFormatUtil.format(getActivity(), distance);
            if (languageEnv.equals(LanguageUtil.ENV_CN) || languageEnv.equals(LanguageUtil.ENV_ZH)) {
                mTvRadarResult.setText(getResources().getString(R.string.radar_scan_result, distanceStr, personCount + ""/*, eventCount*/));
            } else {
                mTvRadarResult.setText(getResources().getString(R.string.radar_scan_result, personCount, distanceStr + ""/*, eventCount*/));
            }
        }
        mRadarCoverView.setCurrentPoint(
                mParams.leftMargin + (int) getResources().getDimension(R.dimen.drag_btn_size) / 2
                , DisplayUtil.getScreenHeight(getActivity()) - mParams.bottomMargin - DisplayUtil.dp2px(10)
                , distanceStr
                , getActivity().getResources().getString(R.string.radar_people_count, personCount)
                , getActivity().getResources().getString(R.string.radar_event_count, eventCount)
        );
    }

    /**
     * 点击减少距离按钮
     * 每次减少5%
     */
    public void minusDistance() {
        if (mLength <= 0) {
            return;
        }
        double decreaseDistance = mLength * 5 * 1.0 / 100;
        if (Constants.dLargestDistance - decreaseDistance < minDistance) {
            Constants.dLargestDistance = minDistance;
        } else {
            Constants.dLargestDistance -= decreaseDistance;
        }
        //滑块移动
        if (mParams != null) {
            int decreaseMargin = mPbDistance.getWidth() * 5 / 100;
            if (mParams.leftMargin - decreaseMargin < getResources().getDimension(R.dimen.drag_btn_left_margin)) {
                mParams.leftMargin = (int) getResources().getDimension(R.dimen.drag_btn_left_margin);
            } else {
                mParams.leftMargin -= decreaseMargin;
            }
            mIvDragBtn.setLayoutParams(mParams);
            mLeft = mParams.leftMargin;
            mBottom = mParams.bottomMargin;
        }
        //progress
        if (mPbDistance.getProgress() - 5 < 0) {
            mPbDistance.setProgress(0);
        } else {
            mPbDistance.setProgress(mPbDistance.getProgress() - 5);
        }
        mRadarCoverView.setVisibility(View.VISIBLE);
        showDefaultInfo(Constants.dLargestDistance);
        TaskExecutor.scheduleTaskOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRadarCoverView.setVisibility(View.GONE);
            }
        }, 200);
        //重新绘点
        mHelper.moveHeadInfo();
    }

    /**
     * 点击增加距离按钮
     * 每次增加5%
     * <p/>
     * 1、Constants.dLargestDistance 增加5%
     * 2、mIvDragBtn marginLeft增加pbDistance长度的5%
     * 3、progress 增加5
     * 4、滑块上面文字改变
     * 5、CoverView展示、隐藏
     * 6、reDrawPeopleHead、reDrawEventHead
     */
    public void plusDistance() {
        if (mLength <= 0) {
            return;
        }
        double increaseDistance = mLength * 5 * 1.0 / 100;
        if (Constants.dLargestDistance + increaseDistance > mLength) {
            Constants.dLargestDistance = mLength;
        } else {
            Constants.dLargestDistance += increaseDistance;
        }
        //滑块移动
        if (mParams != null) {
            int increaseMargin = mPbDistance.getWidth() * 5 / 100;
            if (mParams.leftMargin + increaseMargin > mScreenWidth - (int) (getResources().getDimension(R.dimen.drag_btn_left_margin) + getResources()
                    .getDimension(R.dimen.drag_btn_size))) {
                mParams.leftMargin = mScreenWidth - (int) (getResources().getDimension(R.dimen.drag_btn_left_margin) + getResources()
                        .getDimension(R.dimen.drag_btn_size));
            } else {
                mParams.leftMargin += increaseMargin;
            }
            mIvDragBtn.setLayoutParams(mParams);
            mLeft = mParams.leftMargin;
            mBottom = mParams.bottomMargin;
        }
        //progress
        if (mPbDistance.getProgress() + 5 > 100) {
            mPbDistance.setProgress(100);
        } else {
            mPbDistance.setProgress(mPbDistance.getProgress() + 5);
        }
        mRadarCoverView.setVisibility(View.VISIBLE);
        showDefaultInfo(Constants.dLargestDistance);
        TaskExecutor.scheduleTaskOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRadarCoverView.setVisibility(View.GONE);
            }
        }, 200);
        //重新绘点
        mHelper.moveHeadInfo();
    }


    /**
     * 提示小圆点
     */
    private void visibleLeftPoint() {
        mLeftPointImg.setVisibility(View.VISIBLE);
    }

    private void visibleRightPoint() {
        mRightPointImg.setVisibility(View.VISIBLE);
    }

    private void hideLeftPoint() {
        mLeftPointImg.setVisibility(View.GONE);
    }

    private void hideRightPoint() {
        mRightPointImg.setVisibility(View.GONE);
    }
}

