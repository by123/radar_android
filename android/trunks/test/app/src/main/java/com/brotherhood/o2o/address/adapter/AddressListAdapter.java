package com.brotherhood.o2o.address.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.address.helper.LocationHelper;
import com.brotherhood.o2o.address.model.AddressInfo;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ZhengYi on 15/6/8.
 */
public class AddressListAdapter extends BaseAdapter {
    private List<AddressInfo> mDataSet;
    private int mCount;

    public AddressListAdapter(List<AddressInfo> dataSet) {
        mDataSet = dataSet;
        mCount = dataSet.size();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public AddressInfo getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_cell_address, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        AddressInfo item = getItem(position);
        holder.configureCell(item);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.label_name)
        TextView mNameLabel;
        @InjectView(R.id.label_detail)
        TextView mDetailLabel;

        ViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }

        void configureCell(AddressInfo item) {
            mNameLabel.setText(item.mBuildingName);
            mDetailLabel.setText(item.mAddress);
        }
    }
}
