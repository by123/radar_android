package com.brotherhood.o2o.address.model;

import android.text.TextUtils;

import com.brotherhood.o2o.utils.ContextUtils;
import com.brotherhood.o2o.utils.Utils;
import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by ZhengYi on 15/6/11.
 */
public class HistoryAddressInfoStore {
    private static HistoryAddressInfoStore sInstance;
    private LinkedList<AddressInfo> mAddressList = new LinkedList<>();

    private HistoryAddressInfoStore() {
    }

    public static HistoryAddressInfoStore shareStore() {
        if (sInstance == null) {
            sInstance = new HistoryAddressInfoStore();
            sInstance.unarchive();
        }
        return sInstance;
    }

    public LinkedList<AddressInfo> getAddressList() {
        return mAddressList;
    }

    public boolean isHistoryAddresssListEmpty() {
        return mAddressList.isEmpty();
    }

    public void addHistoryAddress(AddressInfo address) {
        if (!mAddressList.contains(address)) {
            mAddressList.add(0, address);
        } else {
            mAddressList.remove(address);
            mAddressList.add(0, address);
        }
        archive();
    }

    public void removeHistoryAddress(AddressInfo address) {
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
            AddressInfo[] array = new AddressInfo[mAddressList.size()];
            mAddressList.toArray(array);
            String data = new Gson().toJson(array, AddressInfo[].class);
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
            AddressInfo[] array = new Gson().fromJson(dataOrNil, AddressInfo[].class);
            if (array != null) {
                mAddressList.addAll(Arrays.asList(array));
            }
        }
    }

    private String getArchivePath() {
        return ContextUtils.context().getCacheDir().getAbsolutePath() + File.separator + "history_address.dat";
    }
}
