package com.brotherhood.o2o.location.model;

import android.text.TextUtils;

import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.utils.ContextUtils;
import com.brotherhood.o2o.utils.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by ZhengYi on 15/6/11.
 */
public class HistoryLocationInfoStore {
    private static HistoryLocationInfoStore sInstance;
    private LinkedList<LocationInfo> mAddressList = new LinkedList<>();

    private HistoryLocationInfoStore() {
    }

    public static HistoryLocationInfoStore shareStore() {
        if (sInstance == null) {
            sInstance = new HistoryLocationInfoStore();
            sInstance.unarchive();
        }
        return sInstance;
    }

    public LinkedList<LocationInfo> getAddressList() {
        return mAddressList;
    }

    public boolean isHistoryAddresssListEmpty() {
        return mAddressList.isEmpty();
    }

    public void addHistoryAddress(LocationInfo address) {
        if (!mAddressList.contains(address)) {
            mAddressList.add(0, address);
        } else {
            mAddressList.remove(address);
            mAddressList.add(0, address);
        }
        archive();
    }

    public void removeHistoryAddress(LocationInfo address) {
        if (mAddressList.contains(address)) {
            mAddressList.remove(address);
            archive();
        }
    }

    public void clearHistoryAddressList() {
        if (!mAddressList.isEmpty()) {
            mAddressList.clear();
            archive();
        }
    }

    private void archive() {
        if (!mAddressList.isEmpty()) {
            File file = new File(getArchivePath());
            LocationInfo[] array = new LocationInfo[mAddressList.size()];
            mAddressList.toArray(array);
            String data = new Gson().toJson(mAddressList);
            Utils.writeStringToFile(data, file, true);
        } else {
            new File(getArchivePath()).delete();
        }
    }

    private void unarchive() {
        mAddressList = new LinkedList<>();

        File file = new File(getArchivePath());
        String dataOrNil = Utils.readStringOrNilFromFile(file);
        if (!TextUtils.isEmpty(dataOrNil)) {
            LocationInfo[] array = new Gson().fromJson(dataOrNil, LocationInfo[].class);
            if (array != null) {
                mAddressList.addAll(Arrays.asList(array));
            }
        }
    }

    private String getArchivePath() {
        return ContextUtils.context().getCacheDir().getAbsolutePath() + File.separator + "history_address.dat";
    }
}
