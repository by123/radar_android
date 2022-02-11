package com.brotherhood.o2o.util;

import android.content.Context;
import android.location.Location;

import com.brotherhood.o2o.R;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by jl.zhang on 2015/12/11.
 */
public class DistanceFormatUtil {

    public static String format(Context context, long meters){
        String meterStr = "";
        DecimalFormat df = new DecimalFormat("#.#");
        if (meters <= 1000){
            String str = df.format(meters);
            meterStr = context.getResources().getString(R.string.radar_meter_text, str);
        }else {
            String str = df.format(meters*1.0/1000);
            meterStr = context.getResources().getString(R.string.radar_killometer_text, str);
        }
        return meterStr;
    }

    public static String format(Context context, double meters){
        if (context == null){
            return "";
        }
        String meterStr = "";
        DecimalFormat df = new DecimalFormat("#.#");
        if (meters <= 1000){
            String str = df.format(meters);
            meterStr = context.getResources().getString(R.string.radar_meter_text, str);
        }else {
            String str = df.format(meters*1.0/1000);
            meterStr = context.getResources().getString(R.string.radar_killometer_text, str);
        }
        return meterStr;
    }

    /**
     * 谷歌地图计算能量点直线距离
     * @param startPoint
     * @param destPoint
     * @return
     */
    public static double getGoogleMapDistance(LatLng startPoint, LatLng destPoint) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(startPoint.latitude);
        locationA.setLongitude(startPoint.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(destPoint.latitude);
        locationB.setLongitude(destPoint.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }
    /**
     * 谷歌地图计算能量点直线距离
     * @param startPoint
     * @param destPoint
     * @return
     */
    public static String getGoogleMapDistance(Context context, LatLng startPoint, LatLng destPoint) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(startPoint.latitude);
        locationA.setLongitude(startPoint.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(destPoint.latitude);
        locationB.setLongitude(destPoint.longitude);
        distance = locationA.distanceTo(locationB);
        return format(context, distance);
    }

}
