package com.brotherhood.o2o.personal.controller;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brotherhood.o2o.MainActivity;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.extensions.BaseFragment;
import com.brotherhood.o2o.personal.model.MsgBean;
import com.brotherhood.o2o.utils.CacheUtils;
import com.brotherhood.o2o.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/6/9.
 */
public class SystemMsgFragment extends BaseFragment {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.layout_nomsg)
    View mNoMsgLayout;

    @InjectView(R.id.listview)
    ListView mListView;

    @InjectView(R.id.layout_list)
    View mListLayout;

    @InjectView(R.id.layout_detail)
    View mDetailLayou;

    @InjectView(R.id.txt_title)
    TextView mTitleTxt;

    @InjectView(R.id.txt_content)
    TextView mContentTxt;

    private ArrayList<MsgBean> datas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_systemmsg, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        getMainActivity().isNeedBottomBar(false);
        initData();
    }

    private void initData() {
        getMainActivity().setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getMainActivity() != null) {
                    if (getMainActivity().msg == MainActivity.SystemMsg.Detail) {
                        changeList();
                    } else {
                        getMainActivity().swichToFragment(new PersonalFragment(), false);
                    }
                }
            }
        });

        datas = getCache(CacheUtils.get(getMainActivity(), "systemmsg").getAsJSONArray(Constants.SYSTEM_MSG));
        if (datas == null) {
            datas = MsgBean.getDatas();
            saveCache(datas);
        }
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
                    changeDetail(datas.get(i));
                    MsgAdapter adapter = ((MsgAdapter) adapterView.getAdapter());
                    if (adapter != null) {
                        datas.get(i).read = true;
                        adapter.updateDatas(datas);
                        adapter.notifyDataSetChanged();
                        saveCache(datas);
                    }

                }
            });
        }

    }

    private ArrayList<MsgBean> getCache(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        ArrayList<MsgBean> datas = new ArrayList<MsgBean>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                datas.add(new MsgBean(jsonObject.optInt("id"), jsonObject.optString("title"), jsonObject.optString("content"), jsonObject.optString("time"), jsonObject.optBoolean("read")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datas;
    }

    private void saveCache(ArrayList<MsgBean> datas) {
        if (datas == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < datas.size(); i++) {
            MsgBean data = datas.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", data.id);
                jsonObject.put("read", data.read);
                jsonObject.put("time", data.time);
                jsonObject.put("title", data.title);
                jsonObject.put("content", data.content);
                jsonArray.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CacheUtils.get(getMainActivity(), "systemmsg").put(Constants.SYSTEM_MSG, jsonArray);
    }


    public void changeList() {
        getMainActivity().msg = MainActivity.SystemMsg.List;
        mDetailLayou.setVisibility(View.GONE);
        mListLayout.setVisibility(View.VISIBLE);
        for (MsgBean data : datas) {
            if (!data.read) {
                return;
            }
        }
        getMainActivity().readAll=true;

    }

    private void changeDetail(MsgBean msgBean) {
        getMainActivity().msg = MainActivity.SystemMsg.Detail;
        mDetailLayou.setVisibility(View.VISIBLE);
        mListLayout.setVisibility(View.GONE);
        mTitleTxt.setText(msgBean.title);
        mContentTxt.setText(msgBean.content);
    }

    private class MsgAdapter extends BaseAdapter {
        private ArrayList<MsgBean> datas;
        private int size = 0;

        public MsgAdapter(ArrayList<MsgBean> datas) {
            updateDatas(datas);
        }

        public void updateDatas(ArrayList<MsgBean> datas) {
            this.datas = datas;
            if (datas != null && datas.size() > 0) {
                size = datas.size();
            }
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
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();

                view = LayoutInflater.from(getMainActivity()).inflate(R.layout.system_msg_listitem, null);
                holder.mMainTxt = (TextView) view.findViewById(R.id.txt_main);
                holder.mMinorTxt = (TextView) view.findViewById(R.id.txt_minor);
                holder.mTimeTxt = (TextView) view.findViewById(R.id.txt_time);
                holder.mPointView = view.findViewById(R.id.redpoint);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            MsgBean data = datas.get(i);
            holder.mMainTxt.setText(data.title);
            holder.mMinorTxt.setText(data.content);
            holder.mTimeTxt.setText(data.time);
            if (data.read) {
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
