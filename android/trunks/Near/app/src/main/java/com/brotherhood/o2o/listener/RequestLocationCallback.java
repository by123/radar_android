package com.brotherhood.o2o.listener;

import com.brotherhood.o2o.bean.location.LocationInfo;

/**
 * Created by jl.zhang on 2015/12/5.
 */
public interface RequestLocationCallback {
    void onFinishRequestLocation(LocationInfo locationInfo, String errorOrNil);
}
