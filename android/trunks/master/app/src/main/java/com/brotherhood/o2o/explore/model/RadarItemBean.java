package com.brotherhood.o2o.explore.model;

import android.view.View;

import com.brotherhood.o2o.config.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by by.huang on 2015/6/2.
 */
public class RadarItemBean {
    /**
     * 获取数据
     */
    //用户uid
    public long mUid;
    //信标id
    public String mBeaconId;
    //用户头像
    public String mAvatarUrl;
    //用户昵称/服务名称
    public String mName;
    //信标类型  0.人  1.物品信标
    public int mType;
    //经度
    public double mLontitude;
    //纬度
    public double mLatitude;
    //实际距离
    public double mDistance;
    //用户性别
    public int mGender;
    //用户电话
    public String mPhone;
    //是否在线
    public int mOnline;
    //是否是虚拟用户
    public int mVirtual;
    //开始时间
    public long mStartTime;
    //结束时间
    public long mEndTime;
    //服务范围
    public int mServerRadius;
    /**
     * 计算数据
     */
    //角度
    public double mDegree;
    //实际雷达X位置
    public double mPosX;
    //实际雷达Y位置
    public double mPosY;
    //绑定view
    public View mHeadView;
    //是否是熟人
    public int mIsFriend=0;

    public RadarItemBean(long mUid, String mAvatarUrl, String mName, int mType, double mLontitude, double mLatitude, double mDistance, int mGender, String mPhone, int mOnline, int mVirtual) {
        this.mUid = mUid;
        this.mAvatarUrl = mAvatarUrl;
        this.mName = mName;
        this.mType = mType;
        this.mLontitude = mLontitude;
        this.mLatitude = mLatitude;
        this.mDistance = mDistance;
        this.mGender = mGender;
        this.mPhone = mPhone;
        this.mOnline = mOnline;
        this.mVirtual = mVirtual;
    }

    public RadarItemBean(String mBeaconId, String mAvatarUrl, String mNickName, int mType, double mLontitude, double mLatitude, double mDistance, long mStartTime, long mEndTime, int mServerRadius) {
        this.mBeaconId = mBeaconId;
        this.mAvatarUrl = mAvatarUrl;
        this.mName = mNickName;
        this.mType = mType;
        this.mLontitude = mLontitude;
        this.mLatitude = mLatitude;
        this.mDistance = mDistance;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mServerRadius = mServerRadius;
    }

    public static ArrayList<RadarItemBean> getDatas(String jsonStr) {

        ArrayList<RadarItemBean> datas = new ArrayList<>();
        RadarItemBean data = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject dataObj=jsonObject.getJSONObject("data");
            Constants.dLargestDistance=dataObj.optInt("radius");
            if(dataObj.has("people"))
            {
                JSONArray jsonArray = dataObj.optJSONArray("people");
                if (jsonArray != null && jsonArray.length() > 0) {
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {

                        JSONObject peopleObj=jsonArray.getJSONObject(i);
                        long mUid=peopleObj.optLong("id");
                        String mName=peopleObj.optString("title");
                        int mType=peopleObj.optInt("type_id");
                        String mAvatarUrl=peopleObj.optString("icon");
                        double mDistance=peopleObj.optDouble("distance") ;
                        int mGender=peopleObj.optInt("gender");
                        String mPhone=peopleObj.optString("cell_phone");
                        int mOnline=peopleObj.optInt("online");
                        int mVirtual=peopleObj.optInt("virtual");
                        JSONObject locationObj=peopleObj.optJSONObject("location");
                        double mLontitude=locationObj.optDouble("lon");
                        double mLatitude=locationObj.optDouble("lat");
                        data=new RadarItemBean( mUid,  mAvatarUrl,  mName,  mType,  mLontitude,  mLatitude,  mDistance,mGender,mPhone,mOnline,mVirtual);
                        datas.add(data);
                    }
                }
            }
            if(dataObj.has("activity"))
            {
                JSONArray jsonArray = dataObj.optJSONArray("activity");
                if (jsonArray != null && jsonArray.length() > 0) {
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {

                        JSONObject serverObj=jsonArray.getJSONObject(i);
                        String mBeaconId=serverObj.optString("beacon_id");
                        String mName=serverObj.optString("title");
                        int mType=serverObj.optInt("type_id");
                        String mAvatarUrl=serverObj.optString("icon");
                        double mDistance=serverObj.optDouble("distance") ;
                        long mStartTime=serverObj.optLong("start_time");
                        long mEndTime=serverObj.optLong("end_time");
                        int mServerRadius=serverObj.optInt("service_radius");
                        JSONObject locationObj=serverObj.optJSONObject("location");
                        double mLontitude=locationObj.optDouble("lon");
                        double mLatitude=locationObj.optDouble("lat");

                        data=new RadarItemBean(mBeaconId,  mAvatarUrl,  mName,  mType,  mLontitude,  mLatitude,  mDistance,mStartTime,mEndTime,mServerRadius);
                        datas.add(data);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datas;
    }

}
