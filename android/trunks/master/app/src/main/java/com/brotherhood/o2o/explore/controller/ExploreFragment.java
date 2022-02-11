package com.brotherhood.o2o.explore.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.ChaListActivity;
import com.brotherhood.o2o.chat.utils.ChatAPI;
import com.brotherhood.o2o.component.AccountComponent;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.explore.ExploreComponent;
import com.brotherhood.o2o.explore.controller.adapter.RecyclerAdapter;
import com.brotherhood.o2o.explore.controller.additional.DividerItemDecoration;
import com.brotherhood.o2o.explore.controller.additional.WrapLinearLayoutManage;
import com.brotherhood.o2o.explore.helper.ExploerUrlFetcher;
import com.brotherhood.o2o.explore.helper.ExplperHelper;
import com.brotherhood.o2o.explore.model.RadarItemBean;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.LocationComponent;
import com.brotherhood.o2o.location.model.LocationInfo;
import com.brotherhood.o2o.ui.activity.MainActivity;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.ui.fragment.base.BaseFragment;
import com.brotherhood.o2o.ui.widget.dialog.BasicDialog;
import com.brotherhood.o2o.ui.widget.radar.CircleProgressView;
import com.brotherhood.o2o.ui.widget.radar.DirectLayout;
import com.brotherhood.o2o.ui.widget.radar.HeadViewBuilder;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by by.huang on 2015/6/2.
 */
public class ExploreFragment extends BaseFragment {

    //最下层背景图叠层（用于模糊变化）
    @InjectView(R.id.iv_radar_bg)
    SimpleDraweeView mRadarBgImg;

    //扫描雷达图片
    @InjectView(R.id.iv_scan)
    ImageView mScanImg;

    //雷达盘内布局
    @InjectView(R.id.layout_radar)
    DirectLayout mRadarLayout;

    //中间外环背景
    @InjectView(R.id.iv_three)
    ImageView mOutBgImg;

    //最大外环背景
    @InjectView(R.id.radar_content_bg)
    ImageView mRadarContentBgImg;

    @InjectView(R.id.layout_tip)
    View mTipsLayout;

    //下拉提示图片
    @InjectView(R.id.iv_tip)
    ImageView mTipsImg;

    //下拉提示文本
    @InjectView(R.id.tv_tip)
    TextView mTipsTxt;

    @InjectView(R.id.txt_title)
    TextView mAddressTxt;

    @InjectView(R.id.layout_position)
    View mCenterLayout;

    @InjectView(R.id.gestureview)
    GestureOverlayView mGestureView;

    @InjectView(R.id.circleprogressview)
    CircleProgressView mCircleProgressView;

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @InjectView(R.id.layout_bottom)
    View mBottomLayout;

    @InjectView(R.id.img_left_point)
    View mLeftPointImg;

    @InjectView(R.id.img_right_point)
    View mRightPointImg;


    private ExplperHelper mHelper;
    private LocationBroadCastReceiver mLocationBroadCastReceiver;
    private GestureLibrary mGestureLib;
    public MainActivity mActivity;
    private View rootView;

    //传感器
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private float[] mValues = {180, 0, 0};

    //记录是否雷达盘托起
    private boolean isUp = false;
    private HeadViewBuilder mBuilder = null;

    private ArrayList<RadarItemBean> mDatas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.frag_explore, container, false);
        }
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mHelper = ExplperHelper.getInstance();
        mBuilder = new HeadViewBuilder(mActivity);
        initView();
//        setSensor();
        RegisterLocationBroadCast();
        ExploreComponent.shareComponent().setExploreComponent(this);
    }

    @OnClick(R.id.img_left)
    void onMenuImageClick() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            MainActivity act = (MainActivity) getActivity();
            act.toggleSlideMenu();
        }
    }

    @OnClick(R.id.txt_title)
    void OnAddressClick() {
        if (mActivity != null) {
            LocationComponent.shareComponent().showSelectAddressPage(mActivity);
        }
    }


    @OnClick(R.id.img_right)
    void OnMsgListBtnClick() {
        if (AccountComponent.shareComponent().getLoginUserInfoOrNil() == null) {
            Utils.showShortToast(R.string.please_login);
        } else {
            ChaListActivity.show(mActivity);
        }
    }


    @OnClick(R.id.img_center)
    void OnCenterImgClick() {
        String content = null;
        if (Constants.URL_ROOT_V1.contains("86")) {

            content = "开发环境";
        } else {
            content = "正式环境";
        }
        new BasicDialog(mActivity, new BasicDialog.OnDialogListener() {
            @Override
            public void OnConfirm(BasicDialog dialog) {
                //if (Constants.URL_ROOT_V1.contains("86")) {
                //    Constants.URL_ROOT_V1 = "http://openapi.ids111.com:82/v1";
                //} else {
                //    Constants.URL_ROOT_V1 = "http://openapi.ids111.com:86/v1";
                //}
                //dialog.dismiss();
            }

            @Override
            public void OnCancel(BasicDialog dialog) {
                dialog.dismiss();
            }
        }).setMainTxt("当前环境为" + content + ",是否切换？").show();
    }

    private void initView() {

        mRadarLayout.setClipChildren(false);
        mRadarLayout.getLayoutParams().width = Constants.SCREEN_WIDTH * 15 / 16;
        mRadarLayout.getLayoutParams().height = Constants.SCREEN_WIDTH * 15 / 16;

        mOutBgImg.getLayoutParams().width = 1 + Constants.SCREEN_WIDTH * 15 / 16;
        mOutBgImg.getLayoutParams().height = 1 + Constants.SCREEN_WIDTH * 15 / 16;

        mRadarContentBgImg.getLayoutParams().height = (int) (1.5 * Constants.SCREEN_WIDTH);
        mRadarContentBgImg.getLayoutParams().width = (int) (1.5 * Constants.SCREEN_WIDTH);


        LocationInfo addressInfo = LocationComponent.shareComponent().getCachedCurrentAddressOrNil();
        if (addressInfo != null) {
            mAddressTxt.setText(addressInfo.mBuildingName);
            requestData(false);

        } else {
            mAddressTxt.setText("正在获取地址...");
            LocationComponent.shareComponent().updateAddressAsync();
        }

        mHelper.applyBlur(mRadarBgImg);
//        mHelper.startScanAnim(mScanImg,mRadarLayout);
        setRadarLayoutTouchListener();
//        initActivityCodeLayout();
//        setGestureView();
//        setCircleProgress();
//        setScrollTxt();
    }


    private RecyclerAdapter mAdapter;

    private void initRecyclerView(List<RadarItemBean> datas) {
        for (RadarItemBean data : datas) {
            ByLogout.out("图片地址111->" + data.mAvatarUrl);
        }
        if (mAdapter == null) {
            WrapLinearLayoutManage mLinearLayoutManager = new WrapLinearLayoutManage(mActivity);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new RecyclerAdapter(datas);
            mAdapter.setOnItemClickListener(new RecyclerAdapter.OnRecyclerItemListener() {
                @Override
                public void OnItemClick(View view, int postion) {
                    List<RadarItemBean> datas = mAdapter.getDatas();
                    if (datas != null && datas.size() > 0) {
                        RadarItemBean data = datas.get(postion);
                        ChatAPI.get(mActivity).openSingleChatUI(mActivity, data.mUid, data.mAvatarUrl, data.mName, data.mGender);
                    }
                }
            });
            mRecyclerView.setAdapter(mAdapter);
//            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(
                    getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        } else {
            mAdapter.UpdateDatas(datas);
            mAdapter.notifyDataSetChanged();
        }
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case Constants.SHOW_RADAR_PULL_DOWM:
                        ByLogout.out("pull_down");
                        setPullDownTips();
                        break;
                    case Constants.SHOW_RADAR_PULL_UP:
                        ByLogout.out("pull_up");
                        setPullUpTips();
                        break;
                    case Constants.RADAR_FINDING_AROUND_PEOPLE:
                        setPullCompelete();
                        break;
                    case Constants.RADAR_PULL_DOWN_DISTANCE_SHORT:
                        setPullClear();
                        break;
                    case Constants.UPDATE_ALP_VALUE:
                        if (msg != null && msg.obj instanceof Integer) {
                            int iValue = (Integer) msg.obj;
                            UpdateRadarAlpStatus(iValue);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };


    /**
     * ************************************** 拖动雷达盘*************************************************
     */
    protected void UpdateRadarAlpStatus(int alpValue) {

        try {
            if (Math.abs(alpValue) > 10) {
                double alphaDelt = ((double) Math.abs(alpValue) / (double) 300);
                if (alphaDelt > 1.0) {
                    alphaDelt = 1.0f;
                } else if (alphaDelt < 0.0) {
                    alphaDelt = 0.0f;
                }
                float value = Float.parseFloat(alphaDelt + "");
                float iValue = 1.0f - value;
                ViewHelper.setAlpha(mRadarBgImg, iValue);

            } else {
                ViewHelper.setAlpha(mRadarBgImg, 1.0f);
            }
        } catch (Exception e) {
        }
    }

    public View getRadarContentView() {
        return mRadarLayout;
    }

    public View getRadarCenterView() {
        return mCenterLayout;
    }

    private void setPullDownTips() {
        if (isAdded()) {
            mTipsLayout.setVisibility(View.VISIBLE);
            mTipsImg.setImageResource(R.drawable.ic_renovate_down);
            mTipsImg.setVisibility(View.VISIBLE);
            mTipsTxt.setText(getString(R.string.explorefragment_pulltorefresh));
            mTipsTxt.setVisibility(View.VISIBLE);
        }
    }

    private void setPullUpTips() {
        if (isAdded()) {
            mTipsLayout.setVisibility(View.VISIBLE);
            mTipsImg.setImageResource(R.drawable.ic_renovate_up);
            mTipsImg.setVisibility(View.VISIBLE);
            mTipsTxt.setText(getString(R.string.explorefragment_relesetorefresh));
            mTipsTxt.setVisibility(View.VISIBLE);
        }
    }

    private void setPullCompelete() {
        if (isAdded()) {
            mTipsImg.setVisibility(View.GONE);
            mTipsTxt.setText(getString(R.string.explorefragment_finding));
            mTipsTxt.setVisibility(View.VISIBLE);
            LocationComponent.shareComponent().updateAddressAsync();
        }
    }

    private void setPullFish() {
        if (isAdded()) {
            mTipsTxt.setText(getString(R.string.explorefragment_update));
            mActivity.newThread(new BaseActivity.OnThreadListener() {
                @Override
                public void doInThread() {
                    mTipsTxt.setVisibility(View.GONE);
                }
            }, 500);
        }
    }

    private void setPullClear() {
        if (isAdded()) {
            mTipsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRadarBgImg != null) {
            mRadarBgImg = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationBroadCastReceiver != null && isAdded() && mActivity != null) {
            mActivity.unregisterReceiver(mLocationBroadCastReceiver);
        }
    }


    /**
     * 注册位置广播
     */
    private void RegisterLocationBroadCast() {
        if (isAdded() && mActivity != null) {
            mLocationBroadCastReceiver = new LocationBroadCastReceiver();
            IntentFilter filter = new IntentFilter(LocationComponent.ACTION_ON_ADDRESS_CHANGED);
            mActivity.registerReceiver(mLocationBroadCastReceiver, filter);
        }
    }


    /**
     * 请求雷达数据
     */
    private void requestData(final boolean isPull) {

        if (LocationComponent.shareComponent().getCachedCurrentAddressOrNil() == null) {
            return;
        }
        ExploerUrlFetcher.requestRadarDatas(new HttpClient.OnHttpListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                String jsonStr = respondObject.toString();
                if (Utils.isRequestValid(jsonStr)) {
                    mDatas = RadarItemBean.getDatas(jsonStr);
                    mDatas = mHelper.getContactList(mDatas);
                    DrawHeadInfo(mDatas);
                    ByLogout.out("请求成功->" + jsonStr);
                    if (isPull) {
                        setPullFish();
                    }
//                    AnimatorSet animatorSet = new AnimatorSet();
//                    animatorSet.playTogether(ObjectAnimator.ofFloat(
//                            mRadarBgImg, "alpha", 0,1));
//
//                    animatorSet.setDuration(2000);
//                    animatorSet.start();
                }
            }

            @Override
            public void OnFail(HttpClient.RequestStatu statu, String resons) {

            }
        });
    }

    private class LocationBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isAdded()) {
                return;
            }
            if (intent.getAction().equalsIgnoreCase(LocationComponent.ACTION_ON_ADDRESS_CHANGED)) {
                requestData(true);
            }
        }

    }

    private void DrawHeadInfo(ArrayList<RadarItemBean> datas) {
        LocationInfo addressInfo = LocationComponent.shareComponent().getCachedCurrentAddressOrNil();
        mAddressTxt.setText(addressInfo.mBuildingName);
        mHelper.ReDrawHeadInfo(mScanImg,mRadarLayout, datas);
    }


    private boolean isIntervalTouch = true;

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
                            if (!ShowUpdateUserInfo(lastRadarX, lastRadarY)) {
                                mBuilder.restoreHeadViewPoint(mDatas);
                                boolean result = isSelected(lastRadarX, lastRadarY);
                                if (!result && isUp) {
                                    isUp = false;
                                    ExplperHelper.getInstance().hideBottomLayout(mBottomLayout, mRadarLayout, mRadarContentBgImg);
                                }
                                return result;
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
    public boolean ShowUpdateUserInfo(int x, int y) {
        double distance = Math.sqrt(Math.abs(x - Constants.SCREEN_WIDTH / 2)
                * Math.abs(x - Constants.SCREEN_WIDTH / 2)
                + Math.abs(y - Constants.SCREEN_WIDTH / 2)
                * Math.abs(y - Constants.SCREEN_WIDTH / 2));
        if (distance < Constants.SCREEN_WIDTH / 8) {
            return true;
        } else
            return false;
    }

//    int count = 0;


    public boolean isSelected(int x, int y) {
        ArrayList<RadarItemBean> selectDatas = new ArrayList<>();
        if (mDatas != null && mDatas.size() > 0) {
            for (RadarItemBean data : mDatas) {
                double xValue = data.mPosX;
                double yValue = data.mPosY;
                if (Math.abs(x - xValue) <= Constants.VALID_VALUE && Math.abs(y - yValue) <= Constants.VALID_VALUE && data.mHeadView.getVisibility() == View.VISIBLE) {
                    if (data.mType == 0) {
                        if (data.mIsFriend == 0) {
                            mBuilder.clickHeadViewPoint(data.mHeadView, data.mGender);
                        }
                        selectDatas.add(data);
                    } else {
                        OrderActivity.show(mActivity, data.mBeaconId, data.mType);
                        isUp = false;
                        isIntervalTouch = true;
                        return true;
                    }
//                    ActivityCodeBean codeBean = mCodeBeans.get(count);
//                    TextView view = codeBean.mCodeTxt;
//                    int endX = codeBean.mLeft;
//                    int endY = codeBean.mTop;
//                    count++;
//                    mHelper.startActivityCodeAnim(data.mHeadView, view, (int)
//                            xValue, (int) yValue, endX, endY, new ExplperHelper.OnCodeAnimListener() {
//                        @Override
//                        public void OnFinish() {
//                            isIntervalTouch = true;
//                        }
//                    });
//                    return true;
                }
            }
        }
        ByLogout.out(selectDatas.size() + "选择的人数");
        if (selectDatas != null && selectDatas.size() > 0) {
            if (isUp) {
                ExplperHelper.getInstance().showRecycleViewAnim(mRecyclerView);
            } else {
                isUp = true;
                ExplperHelper.getInstance().showBottomLayout(mBottomLayout, mRadarLayout, mRadarContentBgImg, mRecyclerView);
            }
            initRecyclerView(selectDatas);
            isIntervalTouch = true;
            return true;
        } else {
            isIntervalTouch = true;
            return false;
        }
    }

//    private ArrayList<ActivityCodeBean> mCodeBeans;
//    private int mActivityCodeLayoutMargin = Utils.dip2px(20);
//
//    private void setGestureView() {
//        mGestureLib = GestureLibraries.fromRawResource(MyApplication.mApplication.getApplicationContext(), R.raw.gestures);
//        mGestureLib.load();
//        mGestureView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
//
//            @Override
//            public void onGesturePerformed(GestureOverlayView overlay,
//                                           Gesture gesture) {
//                //从手势库中查询匹配的内容，匹配的结果可能包括多个相似的结果，匹配度高的结果放在最前面
//                ArrayList<Prediction> predictions = mGestureLib
//                        .recognize(gesture);
//                if (predictions.size() > 0) {
//                    Prediction prediction = predictions.get(0);
//                    if (prediction.name.equalsIgnoreCase("n")) {
//                        if (prediction.score > 2.0) {
//                            Utils.showShortToast("正确手势->N");
//                        } else {
//                            Utils.showShortToast("错误手势");
//                        }
//                    } else {
//                        Utils.showShortToast("错误手势");
//
//                    }
//                }
//
//            }
//        });
//
//    }

//
//    private int progress = 0;
//
//    private void setCircleProgress() {
//        if (mActivity != null) {
//            mActivity.newThread(new BaseActivity.OnThreadListener() {
//                @Override
//                public void doInThread() {
//                    if (mCircleProgressView != null) {
//                        mCircleProgressView.setProgress(progress);
//                    }
//                    progress++;
//                    if (progress > 100) {
//                        progress = 0;
//                    }
//                    setCircleProgress();
//                }
//            }, 200);
//        }
//
//    }


    @Override
    public void onStart() {
        super.onStart();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.registerListener(mSensorListener, mOrientation,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] mTempValues = event.values;
            mValues[0] = (int) mTempValues[0];
            mValues[1] = (int) mTempValues[1];
            mValues[2] = (int) mTempValues[2];
            if (getRadarContentView() == null)
                return;
            ViewHelper.setRotation(getRadarContentView(), -mValues[0]);
            ViewHelper.setRotation(getRadarCenterView(), mValues[0]);

            if (mDatas != null && mDatas.size() > 0) {
                for (RadarItemBean data : mDatas) {
                    ViewHelper.setRotation(data.mHeadView, mValues[0]);
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void setSensor() {
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(mSensorListener, mOrientation,
                SensorManager.SENSOR_DELAY_GAME);
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

