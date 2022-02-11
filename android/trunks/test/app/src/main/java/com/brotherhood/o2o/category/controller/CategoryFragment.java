package com.brotherhood.o2o.category.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.account.AccountComponent;
import com.brotherhood.o2o.address.AddressComponent;
import com.brotherhood.o2o.address.model.AddressInfo;
import com.brotherhood.o2o.category.adapter.CategoryGalleryAdapter;
import com.brotherhood.o2o.category.helper.CategoryURLFetcher;
import com.brotherhood.o2o.category.model.CategoryHomeInfo;
import com.brotherhood.o2o.category.model.SellerCategoryInfo;
import com.brotherhood.o2o.category.model.SellerInfo;
import com.brotherhood.o2o.extensions.BaseFragment;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.utils.DialogHelper;
import com.brotherhood.o2o.utils.Utils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/2.
 */
public class CategoryFragment extends BaseFragment {

    @InjectView(R.id.contentView)
    View mContentView;
    @InjectView(android.R.id.progress)
    ProgressBar mProgressBar;
    @InjectView(R.id.banner_top)
    Gallery mTopBanner;
    @InjectView(R.id.container_indicator)
    LinearLayout mIndicatorContainer;
    @InjectView(R.id.container_category)
    ViewGroup mCategoryContainer;
    @InjectView(R.id.container_seller)
    LinearLayout mSellerContainer;
    @InjectView(R.id.container_seller_parent)
    FrameLayout mSellerContainerParent;
    @InjectView(R.id.banner_bottom)
    ViewGroup mBottomBanner;
    @InjectView(R.id.label_location)
    TextView mLocationLabel;

    private BroadcastReceiver mLocationChangedEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(AddressComponent.ACTION_ON_ADDRESS_CHANGED)) {
                onLocationChanged();
            }
        }
    };

    private BaseURLFetcher.Callback<CategoryHomeInfo> mFetchDataCallbackHandler = new BaseURLFetcher.Callback<CategoryHomeInfo>() {
        @Override
        public void onCallback(CategoryHomeInfo dataOrNil, String errorOrNil) {
            if (getActivity() == null)
                return;

            if (!TextUtils.isEmpty(errorOrNil)) {
                DialogHelper.showSimpleErrorDialog(getActivity(), errorOrNil);
            } else {
                assert dataOrNil != null;
                initContentView(dataOrNil);
                mContentView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_frag_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mContentView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        CategoryURLFetcher.fetchCategoryHomeInfo(mFetchDataCallbackHandler);

        IntentFilter filter = new IntentFilter(AddressComponent.ACTION_ON_ADDRESS_CHANGED);
        getActivity().registerReceiver(mLocationChangedEventReceiver, filter);

        AddressInfo addressOrNil = AddressComponent.shareComponent().getCachedCurrentAddressOrNil();
        if (addressOrNil == null) {
            mLocationLabel.setText("获取中...");
            AddressComponent.shareComponent().updateAddressAsync();
        } else {
            mLocationLabel.setText(addressOrNil.mBuildingName);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mLocationChangedEventReceiver);
    }

    @OnClick(R.id.container_location)
    void onLocationContainerClick() {
        AddressComponent.shareComponent().showSelectAddressPage(getActivity());
    }

    @OnClick(R.id.btn_login)
    void onLoginButtonClick() {
        AccountComponent.shareComponent().showLoginPage(getActivity());
    }

    private void onLocationChanged() {
        if (getActivity() == null)
            return;

        AddressInfo addressOrNil = AddressComponent.shareComponent().getCachedCurrentAddressOrNil();
        if (addressOrNil != null) {
            mLocationLabel.setText(addressOrNil.mBuildingName);
        } else {
            mLocationLabel.setText("无法获取当前位置");
        }
    }

    private void initContentView(final CategoryHomeInfo info) {

        final int bannerCount = info.mTopBannerInfoList.size();
        resetIndicatorControl(bannerCount);
        CategoryGalleryAdapter adapter = new CategoryGalleryAdapter(info.mTopBannerInfoList);
        mTopBanner.setAdapter(adapter);
        int selectedPos = Integer.MAX_VALUE / 2 + ((Integer.MAX_VALUE / 2) % bannerCount) - 1;
        mTopBanner.setSelection(selectedPos);
        makeIndicatorSelected(0);
        mTopBanner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeIndicatorSelected(position % bannerCount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        resetCategoryContainer(info.mCategoryInfoList);
        resetSellerContainer(info.mSellerInfoList);
        resetBottomBanner(info.mBottomBannerInfoList);
    }

    private void resetIndicatorControl(int count) {
        mIndicatorContainer.removeAllViewsInLayout();
        View child;
        LinearLayout.LayoutParams parmas;
        for (int i = 0; i < count; i++) {
            child = new View(getActivity());
            child.setBackgroundResource(R.drawable.category_sel_dot);
            parmas = new LinearLayout.LayoutParams(Utils.dip2px(8), Utils.dip2px(8));
            if (i > 0) {
                parmas.leftMargin = Utils.dip2px(4);
            }
            mIndicatorContainer.addView(child, parmas);
        }
    }

    private void resetCategoryContainer(List<SellerCategoryInfo> categoryInfoList) {
        if (categoryInfoList == null || categoryInfoList.isEmpty()) {
            mCategoryContainer.setVisibility(View.GONE);
        } else {
            int currentRow = 0;
            int maxColumnInRow = 4;
            LinearLayout currentRowLayout = null;
            View itemView;
            int index = 0;
            int itemViewWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth() / maxColumnInRow;
            for (SellerCategoryInfo item : categoryInfoList) {
                if (currentRowLayout == null || currentRow < (index / maxColumnInRow)) {
                    currentRowLayout = new LinearLayout(getActivity());
                    currentRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    mCategoryContainer.addView(currentRowLayout, new LinearLayout.LayoutParams(-1, -2));
                }
                currentRow = index / maxColumnInRow;

                itemView = LayoutInflater.from(getActivity()).inflate(R.layout.category_cell_category, mCategoryContainer, false);
                itemView.getLayoutParams().width = itemViewWidth;
                itemView.getLayoutParams().height = -2;
                initCategoryItemView(itemView, item);
                currentRowLayout.addView(itemView);
                index++;
            }

            mCategoryContainer.setVisibility(View.VISIBLE);
        }
    }

    private void resetSellerContainer(List<SellerInfo> sellerInfoList) {
        if (sellerInfoList == null || sellerInfoList.isEmpty()) {
            mSellerContainer.setVisibility(View.GONE);
        } else {
            int currentRow = 0;
            int maxColumnInRow = 2;
            LinearLayout currentRowLayout = null;
            View itemView;
            int index = 0;
            int itemViewWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth() / maxColumnInRow;
            for (SellerInfo item : sellerInfoList) {
                if (currentRowLayout == null || currentRow < (index / maxColumnInRow)) {
                    if (currentRowLayout != null) {
                        View sepLine = new View(getActivity());
                        sepLine.setBackgroundColor(Color.parseColor("#e5e5e5"));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 1);
                        params.leftMargin = Utils.dip2px(16);
                        params.rightMargin = Utils.dip2px(16);
                        mSellerContainer.addView(sepLine, params);
                        currentRow++;
                    }

                    currentRowLayout = new LinearLayout(getActivity());
                    currentRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    mSellerContainer.addView(currentRowLayout, new LinearLayout.LayoutParams(-1, -2));
                }

                itemView = LayoutInflater.from(getActivity()).inflate(R.layout.category_cell_seller, mCategoryContainer, false);
                itemView.getLayoutParams().width = itemViewWidth;
                itemView.getLayoutParams().height = -2;
                currentRowLayout.addView(itemView);
                initSellerItemView(itemView, item);
                index++;
            }

            mSellerContainer.setVisibility(View.VISIBLE);
        }

        mSellerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int containerHeight = mSellerContainer.getHeight();
                int margin = Utils.dip2px(16);
                View sepLine = new View(getActivity());
                sepLine.setBackgroundColor(Color.parseColor("#e5e5e5"));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1, containerHeight - margin - margin);
                params.gravity = Gravity.CENTER;
                mSellerContainerParent.addView(sepLine, params);

                mSellerContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void resetBottomBanner(List<SellerInfo> sellerInfoList) {
        if (sellerInfoList == null || sellerInfoList.isEmpty()) {
            mBottomBanner.setVisibility(View.GONE);
        } else {
            mBottomBanner.setVisibility(View.VISIBLE);
            float ratio = 670f / 300;
            int aspectWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            int aspectHeight = (int) ((float) aspectWidth / ratio);
            int topMargin = Utils.dip2px(8);
            GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(getResources());
            builder.setPlaceholderImage(getResources().getDrawable(R.mipmap.ic_launcher));

            SimpleDraweeView coverImage;
            LinearLayout.LayoutParams itemViewParams;
            for (SellerInfo item : sellerInfoList) {
                coverImage = new SimpleDraweeView(getActivity(), builder.build());
                itemViewParams = new LinearLayout.LayoutParams(aspectWidth, aspectHeight);
                itemViewParams.topMargin = topMargin;
                mBottomBanner.addView(coverImage, itemViewParams);
                initBottomBannerItemView(coverImage, item);
            }
        }
    }


    private void initCategoryItemView(View itemView, SellerCategoryInfo item) {
        SimpleDraweeView iconImage = (SimpleDraweeView) itemView.findViewById(R.id.image_icon);
        iconImage.setImageURI(Uri.parse(item.mIconURL));

        TextView titleLabel = (TextView) itemView.findViewById(R.id.label_title);
        titleLabel.setText(item.mName);
    }

    private void initSellerItemView(View itemView, SellerInfo item) {
        SimpleDraweeView iconImage = (SimpleDraweeView) itemView.findViewById(R.id.image_icon);
        iconImage.setImageURI(Uri.parse(item.mCoverURL));
    }

    private void initBottomBannerItemView(SimpleDraweeView itemView, SellerInfo item) {
        itemView.setImageURI(Uri.parse(item.mCoverURL));
    }

    private void makeIndicatorSelected(int index) {
        int count = mIndicatorContainer.getChildCount();
        if (index < count) {
            for (int i = 0; i < count; i++) {
                mIndicatorContainer.getChildAt(i).setSelected(i == index);
            }
        }
    }
}
