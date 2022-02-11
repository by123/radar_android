package com.brotherhood.o2o.personal.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;
import com.brotherhood.o2o.personal.model.SystemMsgBean;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/6/9.
 */
public class SystemMsgActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.layout_nomsg)
    View mNoMsgLayout;

    @InjectView(R.id.listview)
    ListView mListView;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Constants.SYSTEM_MSG_CHANGED)) {
                initData();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_systemmsg);
        ButterKnife.inject(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SYSTEM_MSG_CHANGED);
        registerReceiver(receiver, filter);
        initData();
    }

    private void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ArrayList<SystemMsgBean> datas = SystemMsgBean.getCache();
        if (datas == null || datas.size() == 0) {
            mNoMsgLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mNoMsgLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

            mListView.setAdapter(new MsgAdapter(datas));
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MsgAdapter adapter = ((MsgAdapter) adapterView.getAdapter());
                    if (adapter != null) {
                        datas.get(i).mRead = true;
                        adapter.updateDatas(datas);
                        SystemMsgBean.saveCache(datas);
                        SystemMsgDetailActivity.show(SystemMsgActivity.this, datas.get(i).mTitle, datas.get(i).mContent);
                    }
                }
            });
        }
    }

    private class MsgAdapter extends BaseAdapter {
        private ArrayList<SystemMsgBean> datas;
        private int size = 0;

        public MsgAdapter(ArrayList<SystemMsgBean> datas) {
            updateDatas(datas);
        }

        public void updateDatas(ArrayList<SystemMsgBean> datas) {
            this.datas = datas;
            if (datas != null && datas.size() > 0) {
                size = datas.size();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();

                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.system_msg_listitem, null);
                holder.mMainTxt = (TextView) view.findViewById(R.id.txt_main);
                holder.mMinorTxt = (TextView) view.findViewById(R.id.txt_minor);
                holder.mTimeTxt = (TextView) view.findViewById(R.id.txt_time);
                holder.mPointView = view.findViewById(R.id.redpoint);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            SystemMsgBean data = datas.get(i);
            holder.mMainTxt.setText(data.mTitle);
            holder.mMinorTxt.setText(data.mContent);
            holder.mTimeTxt.setText(Utils.formatTime(data.mCreateTime, "yyyy.MM.dd"));
            if (data.mRead) {
                holder.mPointView.setVisibility(View.GONE);
            } else {
                holder.mPointView.setVisibility(View.VISIBLE);
            }
            return view;
        }

        class ViewHolder {
            TextView mMainTxt;
            TextView mMinorTxt;
            TextView mTimeTxt;
            View mPointView;
        }
    }
}
