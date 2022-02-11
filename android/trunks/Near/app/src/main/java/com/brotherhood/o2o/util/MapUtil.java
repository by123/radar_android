package com.brotherhood.o2o.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.activity.WebViewActivity;
import com.brotherhood.o2o.ui.widget.ColorfulToast;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by jl.zhang on 2015/12/31.
 */
public class MapUtil {


    /**
     * 打开谷歌地图，并规划路线
     *
     * @param context
     * @param startPoint
     * @param destPoint
     */
    public static void buildGoogleWay(Context context, LatLng startPoint, LatLng destPoint) {
        if (startPoint == null) {
            ColorfulToast.orange(context, context.getString(R.string.food_detail_location_empty), Toast.LENGTH_SHORT);
            return;
        }
        if (destPoint == null) {
            ColorfulToast.orange(context, context.getString(R.string.food_detail_destlocation_empty), Toast.LENGTH_SHORT);
            return;
        }
        /**
         * 标记一个点
         * http://www.google.cn/maps/dir/22.5548791,113.9499281/22.281452,114.155653/@22.3969018,114.0739518,11z?hl=zh
         */
        boolean isGMapInstall = DeviceUtil.isAppInstall(context, "com.google.android.apps.maps");

        if (isGMapInstall) {//使用googlemap地图客户端打开
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr="
                    + startPoint.latitude
                    + ","
                    + startPoint.longitude
                    + "&daddr="
                    + destPoint.latitude
                    + ","
                    + destPoint.longitude + "&hl=zh"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            context.startActivity(intent);
        } else {
            String url = "http://ditu.google.cn/maps?f=d&source=s_d&saddr="
                    + startPoint.latitude
                    + ","
                    + startPoint.longitude
                    + "&daddr="
                    + destPoint.latitude
                    + ","
                    + destPoint.longitude + "&hl=zh";
            WebViewActivity.show(context, url);
        }
    }

    /**
     * 打开Uber，并规划路线
     *
     * @param context
     * @param startPoint
     * @param destPoint  uber://?client_id=YOUR_CLIENT_ID
     *                   &action=setPickup
     *                   &pickup[latitude]=37.775818
     *                   &pickup[longitude]=-122.418028
     *                   &pickup[nickname]=UberHQ
     *                   &pickup[formatted_address]=1455%20Market%20St%2C%20San%20Francisco%2C%20CA%2094103
     *                   &dropoff[latitude]=37.802374
     *                   &dropoff[longitude]=-122.405818
     *                   &dropoff[nickname]=Coit%20Tower
     *                   &dropoff[formatted_address]=1%20Telegraph%20Hill%20Blvd%2C%20San%20Francisco%2C%20CA%2094133
     *                   &product_id=a1111c8c-c720-46c3-8534-2fcdd730040d
     */
    public static void buildUberWay(Context context, LatLng startPoint, LatLng destPoint, String dropoffAddress) {
        if (startPoint == null) {
            ColorfulToast.orange(context, context.getString(R.string.food_detail_location_empty), Toast.LENGTH_SHORT);
            return;
        }
        if (destPoint == null) {
            ColorfulToast.orange(context, context.getString(R.string.food_detail_destlocation_empty), Toast.LENGTH_SHORT);
            return;
        }
        boolean isUberInstall = DeviceUtil.isAppInstall(context, "com.ubercab");
        if (!isUberInstall) {
            String url = "https://login.uber.com.cn/login";
            //String url = "http://m.uber.com";
            WebViewActivity.show(context, url);
        } else {
            String uri = "uber://?action=setPickup"
                    + "&pickup[latitude]=" + startPoint.latitude
                    + "&pickup[longitude]=" + startPoint.longitude
                    + "&dropoff[latitude]=" + destPoint.latitude
                    + "&dropoff[longitude]=" + destPoint.longitude
                    + "&client_id=" + context.getString(R.string.uber_client_id);
            StringBuffer sb = new StringBuffer();
            sb.append(uri);
            if (!TextUtils.isEmpty(dropoffAddress)) {
                sb.append("&dropoff[formatted_address]=" + dropoffAddress);
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(sb.toString()));
                context.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据经纬度获取对应地址,此处的经纬度须使用Google或者高德地图的经纬度；<br>
     * 若使用百度地图经纬度，须经过百度API接口(BMap.Convertor.transMore(points,2,callback))的转换；
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return 详细街道地址
     */
    public static String getAddressByGoogleLocation(double longitude, double latitude) {

        //定义一个HttpClient，用于向指定地址发送请求
        HttpClient client = new DefaultHttpClient();
        //向指定地址发送Get请求
        HttpGet hhtpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=false&region=cn");
        StringBuilder sb = new StringBuilder();
        try {
            //获取服务器响应
            HttpResponse response = client.execute(hhtpGet);
            HttpEntity entity = response.getEntity();
            //获取服务器响应的输入流
            InputStream stream = entity.getContent();
            int b;
            //循环读取服务器响应
            while ((b = stream.read()) != -1) {
                sb.append((char) b);
            }
            //将服务器返回的字符串转换为JSONObject  对象
            JSONObject jsonObject = new JSONObject(sb.toString());
            //从JSONObject 中取出location 属性
            JSONObject location = jsonObject.getJSONObject("results").getJSONObject("0").getJSONObject("formatted_address");
            return location.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
