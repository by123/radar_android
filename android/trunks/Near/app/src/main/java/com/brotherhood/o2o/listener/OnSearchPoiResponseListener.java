package com.brotherhood.o2o.listener;

import com.brotherhood.o2o.bean.location.LocationInfo;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/4.
 */
public interface OnSearchPoiResponseListener {
    void onFinish(List<LocationInfo> dataSetOrNil, String errorOrNil);
}
