package com.brotherhood.o2o.extensions;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.brotherhood.o2o.MainActivity;

/**
 * Created by by.huang on 2015/6/9.
 */
public class BaseFragment extends Fragment {

    public Activity mActivity;


    @Override
    public void onResume() {
        super.onResume();
        UmengWrapper.onPageStart(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengWrapper.onPageEnd(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public MainActivity getMainActivity() {
        return (MainActivity) mActivity;
    }

}
