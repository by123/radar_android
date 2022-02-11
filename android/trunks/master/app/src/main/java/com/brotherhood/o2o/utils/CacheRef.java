package com.brotherhood.o2o.utils;

/**
 * Created by by.huang on 2015/6/2.
 */
public class CacheRef {

    public static CacheRef mCacheRef;

    public static CacheRef getInstance() {
        if (mCacheRef == null) {
            mCacheRef = new CacheRef();
        }
        return mCacheRef;
    }

    public int getmUserId() {
        return 1000;
    }
}
