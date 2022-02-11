package com.brotherhood.o2o.location.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.location.model.LocationInfo;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhengYi on 15/6/11.
 */
public class HistoryLocationListAdapter extends BaseAdapter {
    private List<LocationInfo> mDataSet;
    private int mCount;
    private WeakReference<Delegate> mDelegateRef;

    public HistoryLocationListAdapter(ListView listView, List<LocationInfo> dataSet) {
        mDataSet = new LinkedList<>(dataSet);
        mCount = dataSet.size();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public LocationInfo getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_cell_history_address, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        LocationInfo item = getItem(position);
        holder.configureCell(position, item);
        return convertView;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mDataSet.size()) {
            mCount--;
            mDataSet.remove(position);
            notifyDataSetChanged();
        }
    }

    public void addFooterView(ListView listView) {
        View footerView = LayoutInflater.from(listView.getContext()).inflate(R.layout.address_footer_history_address, listView, false);
        listView.addFooterView(footerView);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelegateRef != null && mDelegateRef.get() != null)
                    mDelegateRef.get().onFooterViewClick();
            }
        });
    }

    public void setDelegate(Delegate delegateOrNil) {
        if (delegateOrNil == null)
            mDelegateRef = null;
        else
            mDelegateRef = new WeakReference<>(delegateOrNil);
    }

    class ViewHolder {
        @InjectView(R.id.txt_name)
        TextView mNameLabel;
        private int mCurrentPosition;


        ViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClick();
                }
            });
        }

        void onItemViewClick() {
            if (mDelegateRef != null && mDelegateRef.get() != null) {
                mDelegateRef.get().onItemViewClick(HistoryLocationListAdapter.this, mCurrentPosition);
            }
        }

        @OnClick(R.id.image_close)
        void onCloseImageClick() {
            if (mDelegateRef != null && mDelegateRef.get() != null) {
                mDelegateRef.get().onCloseImageClick(HistoryLocationListAdapter.this, mCurrentPosition);
            }
        }

        void configureCell(int position, LocationInfo item) {
            mNameLabel.setText(item.mBuildingName);
            mCurrentPosition = position;
        }
    }

    public interface Delegate {
        void onCloseImageClick(HistoryLocationListAdapter adapter, int position);

        void onItemViewClick(HistoryLocationListAdapter adapter, int position);

        void onFooterViewClick();
    }
}
