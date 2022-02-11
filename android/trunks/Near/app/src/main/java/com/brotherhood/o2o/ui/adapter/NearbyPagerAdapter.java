package com.brotherhood.o2o.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.brotherhood.o2o.ui.fragment.base.BaseFragment;

import java.util.List;

/**
 * Created by jl.zhang on 2015/12/28.
 */
public class NearbyPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> list;

    public NearbyPagerAdapter(FragmentManager fragmentManager,List<BaseFragment> list){
        super(fragmentManager);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}

