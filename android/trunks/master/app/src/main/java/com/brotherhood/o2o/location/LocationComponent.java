package com.brotherhood.o2o.location;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.brotherhood.o2o.location.controller.SetLocationActivity;
import com.brotherhood.o2o.location.helper.LocationHelper;
import com.brotherhood.o2o.location.helper.LocationUrlFetcher;
import com.brotherhood.o2o.location.model.LocationInfo;
import com.brotherhood.o2o.extensions.BaseURLFetcher;
import com.brotherhood.o2o.utils.ContextUtils;

import java.util.List;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class LocationComponent {
    private static LocationComponent sInstance;
    private LocationInfo mCurrentAddressOrNil;

    /**
     * 当用户选择地址后触发该通知
     */
    public static final String ACTION_ON_FINISH_SELECT_ADDRESS = "ACTION.ADDRESS.ON_SELECTED_ADDRESS";

    /**
     * 当当前地址发生变化时触发该通知
     */
    public static final String ACTION_ON_ADDRESS_CHANGED = "ACTION.ADDRESS.ON_ADDRESS_CHANGED";

    private LocationComponent() {
    }

    public static LocationComponent shareComponent() {
        if (sInstance == null)
            sInstance = new LocationComponent();
        return sInstance;
    }

    /**
     * 获取当前位置
     */
    public void updateAddressAsync() {
        LocationHelper.shareManager().searchNearByBuilding(new LocationHelper.Callback() {
            @Override
            public void onFinish(List<LocationInfo> dataSetOrNil, String errorOrNil) {
                if (dataSetOrNil != null && dataSetOrNil.size() > 0) {
                    setCurrentAddress(dataSetOrNil.get(0));
                }
            }
        });
    }

    public LocationInfo getCachedCurrentAddressOrNil() {
        return mCurrentAddressOrNil;
    }

    /**
     * 设置当前位置
     */
    public void setCurrentAddress(LocationInfo addressOrNil) {
        if (addressOrNil != null) {
//            if (mCurrentAddressOrNil == null || !mCurrentAddressOrNil.mBuildingName.equalsIgnoreCase(addressOrNil.mBuildingName)) {
                Context context = ContextUtils.context();
                Intent intent = new Intent(ACTION_ON_ADDRESS_CHANGED);
                context.sendBroadcast(intent);
//            }
        }

        mCurrentAddressOrNil = addressOrNil;
        if (mCurrentAddressOrNil != null) {
            LocationUrlFetcher.UploadLocation(mCurrentAddressOrNil.mLongitude, mCurrentAddressOrNil.mLatitude);
        }
    }

    /**
     * 显示选择地址的页面
     *
     * @param context 程序上下文
     */
    public void showSelectAddressPage(Context context) {
        SetLocationActivity.show(context);
    }

    private <T> void postCallback(Handler handler, final BaseURLFetcher.Callback<T> callback, final T dataOrNil, final String errorOrNil) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onCallback(dataOrNil, errorOrNil);
            }
        });
    }
}
