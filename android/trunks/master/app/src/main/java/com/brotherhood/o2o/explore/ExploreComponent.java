package com.brotherhood.o2o.explore;

import com.brotherhood.o2o.explore.controller.ExploreFragment;

/**
 * Created by by.huang on 2015/6/2.
 */
public class ExploreComponent {


    private static ExploreComponent mExploreComponent = null;
    private static byte[] sync = new byte[0];
    private ExploreFragment mFragment;

    public static ExploreComponent shareComponent() {
        if (mExploreComponent == null) {
            synchronized (sync) {
                if (mExploreComponent == null) {
                    mExploreComponent = new ExploreComponent();
                }
            }
        }
        return mExploreComponent;
    }

    public void setExploreComponent(ExploreFragment exploreFragment) {
        this.mFragment = exploreFragment;
    }

    public ExploreFragment getExploreFragment() {
        return mFragment;
    }
}
