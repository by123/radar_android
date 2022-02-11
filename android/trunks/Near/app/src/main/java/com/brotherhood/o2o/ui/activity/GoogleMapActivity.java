package com.brotherhood.o2o.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.BundleKey;
import com.brotherhood.o2o.controller.ActionBarController;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.util.DeviceUtil;
import com.brotherhood.o2o.util.MapUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends BaseActivity {

   @ViewInject(id = R.id.llGoogleMapGMap, clickMethod = "mapClient")
    private LinearLayout mLlGoogleMap;

    @ViewInject(id = R.id.llGoogleMapUber, clickMethod = "uberClient")
    private LinearLayout mLlUber;

    private SupportMapFragment mMapFragment;

    private LatLng mDestPoint;
    private LatLng mStartPoint;
    private String mAddress;

    public static void show(Context context, LatLng startPoint, LatLng destPoint, String address){
        Intent it = new Intent(context, GoogleMapActivity.class);
        it.putExtra(BundleKey.GOOGLE_MAP_START_KEY, startPoint);
        it.putExtra(BundleKey.GOOGLE_MAP_DEST_KEY, destPoint);
        it.putExtra(BundleKey.GOOGLE_MAP_DROPOFF_ADDRESS, address);
        context.startActivity(it);
    }


    @Override
    protected boolean addActionBar() {
        return true;
    }

    @Override
    protected int getActionBarStyle() {
        return ActionBarController.LEFT_TYPE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_google_map_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBarController().setBackImage(R.mipmap.btn_top_close)
                .setDivideColor(R.color.black)
                .setBaseTitle(R.string.google_map_title, R.color.black)
                .setHeadBackgroundColor(R.color.white);
        Intent it = getIntent();
        mDestPoint = it.getParcelableExtra(BundleKey.GOOGLE_MAP_DEST_KEY);
        mStartPoint = it.getParcelableExtra(BundleKey.GOOGLE_MAP_START_KEY);
        mAddress = it.getStringExtra(BundleKey.GOOGLE_MAP_DROPOFF_ADDRESS);
        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMapview);

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (mDestPoint != null) {
                    googleMap.addMarker(new MarkerOptions().position(mDestPoint));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestPoint, 14));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.abBack:
                finish();
                break;
        }
    }

    /**
     * 跳转到地图
     * @param view
     */
    public void mapClient(View view) {
        boolean isGMapInstall = DeviceUtil.isAppInstall(GoogleMapActivity.this, "com.google.android.apps.maps");
        if (!isGMapInstall) {
            WebViewActivity.show(GoogleMapActivity.this, "http://map.google.com");
        }else {
            MapUtil.buildGoogleWay(GoogleMapActivity.this, mStartPoint, mDestPoint);
        }
    }

    /**
     * Uber详情  打开Uber客户端
     */
    public void uberClient() {
        MapUtil.buildUberWay(GoogleMapActivity.this, mStartPoint, mDestPoint, mAddress);
    }

}
