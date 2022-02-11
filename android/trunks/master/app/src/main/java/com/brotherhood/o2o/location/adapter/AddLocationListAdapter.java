package com.brotherhood.o2o.location.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.extensions.http.HttpClient;
import com.brotherhood.o2o.location.helper.LocationUrlFetcher;
import com.brotherhood.o2o.location.model.MyLocationInfo;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.deletelistview.SlideView;

import java.util.ArrayList;

/**
 * Created by by.huang on 2015/6/30.
 */
public class AddLocationListAdapter extends BaseAdapter implements SlideView.OnSlideListener {

    private ArrayList<MyLocationInfo> infos = null;
    private Context mContext;
    private int size = 0;
    private SlideView mLastSlideViewWithStatusOn;

    public AddLocationListAdapter(Context context, ArrayList<MyLocationInfo> infos) {
        this.mContext = context;
        this.infos = infos;
        if (infos != null && infos.size() > 0) {
            size = infos.size();
        }
    }

    public ArrayList<MyLocationInfo> getDatas() {
        return infos;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public MyLocationInfo getItem(int i) {
        return infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updateInfos(ArrayList<MyLocationInfo> infos) {
        this.infos = infos;
        if (infos != null && infos.size() > 0) {
            size = infos.size();
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        SlideView slideView = (SlideView) view;
        if (slideView == null) {
            holder = new ViewHolder();
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.addlocation_listitem, null);
            slideView = new SlideView(mContext);
            slideView.setContentView(itemView);
            holder.mDeleteHolder = (ViewGroup) slideView.findViewById(R.id.holder);
            holder.mNameTxt = (TextView) slideView.findViewById(R.id.txt_name);
            holder.mAddressTxt = (TextView) slideView.findViewById(R.id.txt_title);
            holder.mPhoneTxt = (TextView) slideView.findViewById(R.id.txt_phone);
            holder.mStatuView = slideView.findViewById(R.id.statuview);
//            holder.mDivideView = slideView.findViewById(R.id.divide_view);
            slideView.setOnSlideListener(this);
            slideView.setTag(holder);

        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        final MyLocationInfo info = infos.get(i);
        info.mSlideView = slideView;
        info.mSlideView.shrink();

        holder.mNameTxt.setText(info.mName);
        holder.mAddressTxt.setText(info.mAddress);
        holder.mPhoneTxt.setText(info.mPhone);
//        if (i == infos.size() - 1) {
//            holder.mDivideView.setVisibility(View.GONE);
//        } else {
//            holder.mDivideView.setVisibility(View.VISIBLE);
//        }
        if (info.mStatu) {
            holder.mStatuView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_receiving_selected_press));
        } else {
            holder.mStatuView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_receiving_selected_normal));
        }
        holder.mDeleteHolder.setTag(info);
        holder.mDeleteHolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final MyLocationInfo info= (MyLocationInfo) v.getTag();
                LocationUrlFetcher.requestDeleteLocation(info.mId, new HttpClient.OnHttpListener() {
                    @Override
                    public void OnStart() {

                    }

                    @Override
                    public void OnSuccess(HttpClient.RequestStatu statu, Object respondObject) {

                        String jsonStr=respondObject.toString();
                        if(Utils.isRequestValid(jsonStr))
                        {
                            ArrayList<MyLocationInfo> infos=getDatas();
                            infos.remove(info);
                            updateInfos(infos);
                        }
                        else
                        {
                            Utils.showShortToast(jsonStr);
                        }
                    }

                    @Override
                    public void OnFail(HttpClient.RequestStatu statu, String resons) {

                    }
                });
            }
        });
        return slideView;
    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    class ViewHolder {
        TextView mNameTxt;
        TextView mAddressTxt;
        TextView mPhoneTxt;
        View mStatuView;
        //        View mDivideView;
        ViewGroup mDeleteHolder;
    }
}
