package com.brotherhood.o2o.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UDIDUtils {
    private static String secureFilePath;

    // private final static String secureSecretKey;

    private static String URL = "http://stat.uu.cc/update_udid";

    static {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            secureFilePath = Environment.getExternalStorageDirectory()
                    + File.separator + ".skynet" + File.separator
                    + "securenew.bat";
        }

    }

    private static int BLOCKSIZE_RANGE = 10;
    private static int BLOCKSIZE_CHAR = '0';

    /**
     * 获取设备ID
     *
     * @param context
     * @return
     */
    public static String getUdid(final Context context) {
        String udidCache = readString(context);

        final String androidId;

        androidId = ""
                + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // final CryptUtils utils = new CryptUtils(secureSecretKey);

        String udidOrig = null;

        if (udidCache != null && !"".equals(udidCache.trim())) {
            try {
                udidOrig = decode(udidCache);
            } catch (Exception e) {
                udidOrig = null;
            }
        }
        int offset = -1;
        boolean flag_exists = false;
        if (udidOrig != null && !"".equals(udidOrig.trim())) {
            offset = udidOrig.indexOf("+");
            if (offset != -1) {
                flag_exists = true;
            }
        }
        // SD卡存在记录
        if (flag_exists) {
            // System.out.println("from SD udid=" + udid);
            // Android ID 发生了变化
            if (offset != -1
                    && !androidId.equals(udidOrig.substring(offset + 1))) {
                // TODO 提交到服务器,成功后写到SD卡

                String perfix = makeUdidPrefix(context);

                if (perfix != null && !"".equals(perfix.trim())) {
                    final String newUdid = encode(perfix + "+" + androidId);
                    writeString(context, newUdid);
                }
            } else {
                // 校验IMEI或者Mac地址
                String cachePrefix = udidOrig.substring(0, offset);
                String devicePerfix = makeUdidPrefix(context);

                if (!"".equals(cachePrefix) && !cachePrefix.equals(devicePerfix)) {
                    udidOrig = devicePerfix + "+" + androidId;
                    udidCache = encode(udidOrig);
                    writeString(context, udidCache);
                }
            }

            return udidCache;
        } else {
            String perfix = makeUdidPrefix(context);

            if (perfix != null && !"".equals(perfix.trim())) {
                udidOrig = (perfix + "+" + androidId);
                udidCache = encode(udidOrig);
                writeString(context, udidCache);
            } else {
                udidOrig = androidId;
                udidCache = encode(udidOrig);
            }
            // Log.d("udidOrig", udidOrig);
            return udidCache;
        }
    }

    private static String makeUdidPrefix(Context context) {
        String udid = null;
        String tmDevice, tmMac = null;
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            tmDevice = tm.getDeviceId();
        } else {
            tmDevice = "";
        }

        if (tmDevice != null && !"".equals(tmDevice)) {
            // System.out.println("from imei addr...");
            udid = tmDevice;
        } else {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiInfo info = (null == wifiMgr ? null : wifiMgr
                        .getConnectionInfo());
                if (null != info) {
                    String tmp = info.getMacAddress();
                    if (tmp != null) {
                        tmMac = tmp.replace(":", "");
                    }
                    // tmMac = info.getMacAddress().replace(":", "");
                }
            } else {
                tmMac = "";
            }

            if (tmMac != null && !"".equals(tmMac)) {
                // System.out.println("from mac addr...");
                udid = tmMac;
            }
        }

        return udid;
    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static String readString(Context context) {
        if (hasSdcard()
                && checkPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            File file = new File(secureFilePath);
            if (file.exists()) {
                try {
                    is = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int readBytes = is.read(buffer);
                    baos = new ByteArrayOutputStream(1024);
                    while (0 < readBytes) {
                        baos.write(buffer, 0, readBytes);
                        readBytes = is.read(buffer);
                    }
                    return baos.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != is) {
                        try {
                            is.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (null != baos) {
                        try {
                            baos.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
        return "";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void writeString(Context context, String udid) {
        if (hasSdcard()
                && checkPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File file = new File(secureFilePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = null;
            DataOutputStream dos = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                dos = new DataOutputStream(fos);
                dos.writeBytes(udid);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dos != null) {
                        dos.flush();
                        dos.close();
                    }
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean checkPermission(Context context, String permissionName) {
        PackageManager packageManager = context.getPackageManager();
        String pkgName = context.getPackageName();
        return packageManager.checkPermission(permissionName, pkgName) == PackageManager.PERMISSION_GRANTED;
    }

    public static String encode(String src) {
        char[] a = src.toCharArray();
        final int blockSize = getBlockSize(src);

        exchangeValue(a);
        exchangeBlock(a, blockSize);
        exchangePosition(a, blockSize);

        char[] b = new char[a.length + 1];
        b[0] = (char) (BLOCKSIZE_CHAR + blockSize);
        System.arraycopy(a, 0, b, 1, a.length);
        return String.valueOf(b);
    }

    public static String decode(String dst) {
        char[] b = dst.toCharArray();
        char[] a = new char[b.length - 1];
        System.arraycopy(b, 1, a, 0, b.length - 1);
        final int blockSize = b[0] - BLOCKSIZE_CHAR;

        exchangePosition(a, blockSize);
        exchangeBlock(a, blockSize);
        exchangeValue(a);

        return String.valueOf(a);
    }

    private static int getBlockSize(String src) {
        int h = src.hashCode() & 0xff;
        return h % (BLOCKSIZE_RANGE - 2) + 2;
    }

    private static void exchangeBlock(char[] data, int blockSize) {
        char[] tmp = new char[blockSize];
        boolean exchange = true;
        for (int i = 0, m = data.length / blockSize / 2; i < m; i++, exchange = !exchange) {
            if (exchange) {
                System.arraycopy(data, i, tmp, 0, blockSize);
                System.arraycopy(data, m * blockSize + i, data, i, blockSize);
                System.arraycopy(tmp, 0, data, m * blockSize + i, blockSize);
            }
        }
    }

    private static void exchangePosition(char[] a, int blockSize) {
        int i = 0;
        for (int n = a.length / blockSize; i < n; i++) {
            exchangePosition(a, i * blockSize, blockSize);
        }
        exchangePosition(a, i * blockSize, a.length - i * blockSize);
    }

    private static void exchangePosition(char[] data, int index, int len) {
        if (len <= 0)
            return;

        char tmp;
        for (int i = 0, m = len / 2; i < m; i++) {
            tmp = data[index + i];
            data[index + i] = data[index + len - i - 1];
            data[index + len - i - 1] = tmp;
        }
    }

    private static void exchangeValue(char[] a) {
        for (int i = 0, len = a.length; i < len; i++) {
            // 0~9
            if (a[i] >= '0' && a[i] <= '9') {
                if (a[i] < '5') {
                    a[i] = (char) (a[i] + 5);
                } else {
                    a[i] = (char) (a[i] - 5);
                }
            }// A~Z
            else if (a[i] >= 'A' && a[i] <= 'Z') {
                if (a[i] < 'M') {
                    a[i] = (char) (a[i] + 13);
                } else {
                    a[i] = (char) (a[i] - 13);
                }
            }// a~z
            else if (a[i] >= 'a' && a[i] <= 'z') {
                if (a[i] < 'm') {
                    a[i] = (char) (a[i] + 13);
                } else {
                    a[i] = (char) (a[i] - 13);
                }
            }// + _
            else if (a[i] == '+')
                a[i] = '_';
            else if (a[i] == '_')
                a[i] = '+';
                // : .
            else if (a[i] == ':')
                a[i] = '.';
            else if (a[i] == '.')
                a[i] = ':';
        }
    }
}
