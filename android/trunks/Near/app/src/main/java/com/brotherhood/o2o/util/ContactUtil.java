package com.brotherhood.o2o.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.brotherhood.o2o.application.NearApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/7/30.
 */
public class ContactUtil {

    private static byte[] sync = new byte[0];
    private static ContactUtil mInstance = null;

    public static ContactUtil getInstance() {
        if (mInstance == null) {
            synchronized (sync) {
                if (mInstance == null) {
                    mInstance = new ContactUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取联系人及手机号码
     */
    public List<ContactInfo> getContactList() {
        List<ContactInfo> infos = new ArrayList<>();
        ContactInfo contactInfo = null;
        ContentResolver cr = NearApplication.mInstance.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            contactInfo = new ContactInfo();
            List<String> mPhones = new ArrayList<>();
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFieldColumnIndex);
            contactInfo.mName = contact;
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            while (phone.moveToNext()) {
                String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mPhones.add(PhoneNumber);
            }
            contactInfo.mPhones = mPhones;
            infos.add(contactInfo);
        }
        cursor.close();
        return infos;
    }


    /**
     * 获取手机号码
     */
    public List<String> getPhoneList() {
        List<String> mPhones = new ArrayList<>();
        ContentResolver cr = NearApplication.mInstance.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            while (phone.moveToNext()) {
                String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mPhones.add(PhoneNumber);
            }
        }
        cursor.close();
        return mPhones;
    }

    public class ContactInfo {
        public String mName;
        public List<String> mPhones;
    }

}

