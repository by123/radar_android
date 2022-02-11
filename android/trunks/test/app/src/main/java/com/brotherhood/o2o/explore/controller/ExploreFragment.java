package com.brotherhood.o2o.explore.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.MainActivity;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.address.AddressComponent;
import com.brotherhood.o2o.address.model.AddressInfo;
import com.brotherhood.o2o.explore.helper.ImageBlur;
import com.brotherhood.o2o.extensions.BaseActivity;
import com.brotherhood.o2o.extensions.BaseFragment;
import com.brotherhood.o2o.extensions.fresco.ImageLoader;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.Constants;
import com.brotherhood.o2o.widget.BasicDialog;
import com.brotherhood.o2o.widget.DirectLayout;
import com.brotherhood.o2o.widget.RadarContentUI;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by by.huang on 2015/6/2.
 */
public class ExploreFragment extends BaseFragment {
    //最下层背景图
    @InjectView(R.id.layout_content)
    ImageView mBackgroudImg;
    //最下层背景图叠层（用于模糊变化）
    @InjectView(R.id.iv_radar_bg)
    ImageView mRadarBgImg;
    //雷达盘外布局
    @InjectView(R.id.radar_content_ui)
    RadarContentUI mRadarContentUIImg;
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
    //最内层雷达背景
    @InjectView(R.id.iv_bg)
    ImageView mRaderBgImg;
    //雷达正中的圆形图
    @InjectView(R.id.img_center)
    ImageView mCenterImg;
    @InjectView(R.id.layout_tip)
    View mTipsLayout;
    //下拉提示图片
    @InjectView(R.id.iv_tip)
    ImageView mTipsImg;
    //下拉提示文本
    @InjectView(R.id.tv_tip)
    TextView mTipsTxt;

    @InjectView(R.id.img_qrcode)
    ImageView mQRCode;

    @InjectView(R.id.txt_address)
    TextView mAddressTxt;

    @InjectView(R.id.layout_address)
    View mAddressView;

    @InjectView(R.id.layout_position)
    View mCenterLayout;



    private ImageLoader mImageLoader;
    private LocationBroadCastReceiver mLocationBroadCastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_explore, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView();
        RegisterLocationBroadCast();
        AddressInfo addressInfo = AddressComponent.shareComponent().getCachedCurrentAddressOrNil();
        if (addressInfo != null) {
            mAddressTxt.setText(addressInfo.mBuildingName);
        }
        else
        {
            mAddressTxt.setText("正在获取地址...");
        }
        AddressComponent.shareComponent().updateAddressAsync();
        applyBlur();
    }

    @OnClick(R.id.layout_address)
    void OnAddressClick() {
        AddressComponent.shareComponent().showSelectAddressPage(getMainActivity());
    }


    private void initView() {
        initBasicService();

        mRadarLayout.setClipChildren(false);
        mRadarLayout.getLayoutParams().width = Constants.ScreenWidth * 15 / 16;
        mRadarLayout.getLayoutParams().height = Constants.ScreenHeight * 15 / 16;

        mOutBgImg.getLayoutParams().width = 1 + Constants.ScreenWidth * 15 / 16;
        mOutBgImg.getLayoutParams().height = 1 + Constants.ScreenHeight * 15 / 16;

        mRadarContentBgImg.getLayoutParams().height = (int) (1.5 * Constants.ScreenWidth);
        mRadarContentBgImg.getLayoutParams().width = (int) (1.5 * Constants.ScreenHeight);

        mQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByLogout.out("点击");
                new BasicDialog(getActivity(), null).show();
            }
        });

        mCenterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "开发中...", Toast.LENGTH_SHORT).show();
            }
        });

        ExploreAnim.startScanAnim(mScanImg);
    }

    private void initBasicService() {
        mImageLoader = ImageLoader.getInstance();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case Constants.SHOW_RADAR_PULL_DOWM:
                        setPullDownTips();
                        break;
                    case Constants.SHOW_RADAR_PULL_UP:
                        setPullUpTips();
                        break;
                    case Constants.RADAR_FINDING_AROUND_PEOPLE:
                        setPullCompelete();
                        break;
                    case Constants.RADAR_PULL_DOWN_DISTANCE_SHORT:
                        setPullClear();
                        break;
                    default:
                        break;
                }
            }
        }
    };

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
            getMainActivity().newThread(new BaseActivity.OnThreadListener() {
                @Override
                public void doInThread() {
                    setPullFish();
                }
            }, 3000);
        }
    }

    private void setPullFish() {
        if (isAdded()) {
            mTipsTxt.setText(getString(R.string.explorefragment_update));
            getMainActivity().newThread(new BaseActivity.OnThreadListener() {
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


    //模糊背景

    /**
     * ************************************** 更换背景
     * *************************************************
     */
    private void applyBlur() {

        mRadarBgImg.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mRadarBgImg.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        mRadarBgImg.buildDrawingCache();
                        final Bitmap bmp = mRadarBgImg.getDrawingCache();
                        blur(bmp, mRadarBgImg);
                        mRadarBgImg.destroyDrawingCache();
                        return true;
                    }
                });
    }

    public static Bitmap doBlurJniArray(Bitmap sentBitmap, int radius,
                                        boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        ImageBlur.blurIntArray(pix, w, h, radius);

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    private final static float SCALEFACTOR = 15f;
    private final static float RADIUS = 2f;
    private Drawable mLastDrawable;

    private void blur(Bitmap bkg, ImageView view) {
        if (bkg == null || !isAdded()) {
            return;
        }

        int viewHeight;
        int viewWidth;
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
            viewWidth = Constants.ScreenWidth;
            viewHeight = Constants.ScreenHeight;

        } else {
            viewWidth = view.getMeasuredWidth();
            viewHeight = view.getMeasuredHeight();
        }

        Bitmap overlay = Bitmap.createBitmap((int) (viewWidth / SCALEFACTOR),
                (int) (viewHeight / SCALEFACTOR), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / SCALEFACTOR, -view.getTop()
                / SCALEFACTOR);
        canvas.scale(1 / SCALEFACTOR, 1 / SCALEFACTOR);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = doBlurJniArray(overlay, (int) RADIUS, true);

        mLastDrawable = new BitmapDrawable(getMainActivity().getResources(), overlay);
        view.setImageDrawable(mLastDrawable);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationBroadCastReceiver != null && isAdded()) {
            getMainActivity().unregisterReceiver(mLocationBroadCastReceiver);
        }
    }


    private void RegisterLocationBroadCast() {
        if (isAdded()) {
            mLocationBroadCastReceiver = new LocationBroadCastReceiver();
            IntentFilter filter = new IntentFilter(AddressComponent.ACTION_ON_ADDRESS_CHANGED);
            getMainActivity().registerReceiver(mLocationBroadCastReceiver, filter);
        }
    }

    private class LocationBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ByLogout.out("接收到广播");
            if (intent.getAction().equalsIgnoreCase(AddressComponent.ACTION_ON_ADDRESS_CHANGED)) {
                AddressInfo addressInfo = AddressComponent.shareComponent().getCachedCurrentAddressOrNil();
                mAddressTxt.setText(addressInfo.mBuildingName);
            }
        }
    }

}

