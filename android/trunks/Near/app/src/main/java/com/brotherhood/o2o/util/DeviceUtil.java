package com.brotherhood.o2o.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.SharePrefConstant;
import com.brotherhood.o2o.manager.DefaultSharePrefManager;
import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.ui.widget.ColorfulToast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created with Android Studio.
 */
public class DeviceUtil {

    private static String CpuType;
    private static String GPU = "";

    private static String Mac = "";

    /**
     * 启动系统相机
     *
     * @param activity
     * @param outPutUri
     * @param requestCode
     */
    public static void startCamera(Activity activity, Uri outPutUri, int requestCode) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * app是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstall(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageInfo packageInfo = queryPackageInfo(context, packageName);
        return packageInfo != null;
    }

    /**
     * 根据包名获取文件的包信息
     *
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo queryPackageInfo(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return packageInfo;
    }

    /**
     * 获取手机图片
     */
    public static List<String> getImageFromAlbum(Context context) {
        List<String> pathList = new ArrayList<>();
        if (DeviceUtil.isSDCardMounted()) {
            ContentResolver contentResolver = context.getContentResolver();
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            if (cursor == null || cursor.getCount() <= 0) {
                return pathList;
            }
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                pathList.add(path);
            }
            while (cursor.moveToNext()) {//false if is end
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    continue;
                }
                pathList.add(path);
            }
        } else {
            ColorfulToast.orange(context, context.getString(R.string.choose_check_sdcard_mounted), Toast.LENGTH_SHORT);
        }
        return pathList;
    }


    /**
     * 启动系统裁剪
     *
     * @param activity
     * @param sourceUri
     * @param outputUri
     * @param requestCode
     */
    public static void startCropPhoto(Activity activity, Uri sourceUri, Uri outputUri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(sourceUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        // aspectX aspectY 是宽高的比例
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        //裁剪后图片路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 调用系统相册
     *
     * @param activity
     * @param requestCode
     */
    public static void startSystemAlbum(Activity activity, Uri outputUri, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 判断sd卡是否已经挂载
     * *
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取cpu类型
     *
     * @return
     */
    public static String getCpuType() {

        if (!TextUtils.isEmpty(CpuType) || !TextUtils.isEmpty(CpuType = DefaultSharePrefManager.getString(SharePrefConstant.KEY_CPU_TYPE, ""))) {
            return CpuType;
        }

        BufferedReader bufferedReader = null;
        InputStream is = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.board.platform");
            is = p.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String result = bufferedReader.readLine();
            if (!TextUtils.isEmpty(result)) {
                CpuType = result;
            } else {
                CpuType = getMtkType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeCloseable(bufferedReader);
            Utils.closeCloseable(is);

        }
        if (!TextUtils.isEmpty(CpuType)) {
            DefaultSharePrefManager.putString(SharePrefConstant.KEY_CPU_TYPE, CpuType);
        }
        return CpuType;
    }

    private static String getMtkType() {
        BufferedReader bufferedReader = null;
        InputStream is = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.mediatek.platform");
            is = p.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String result = bufferedReader.readLine();
            if (!TextUtils.isEmpty(result)) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogManager.e(e);
                }
            }
        }
        return "";
    }


    private static class DemoRenderer implements GLSurfaceView.Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try {
                GPU = gl.glGetString(GL10.GL_RENDERER) + "@" + gl.glGetString(GL10.GL_VENDOR);
                DefaultSharePrefManager.putString(SharePrefConstant.KEY_GPU_INFO, GPU);
            } catch (Throwable e) {
                e.printStackTrace();
                GPU = "";
            }
        }

        @Override
        public void onDrawFrame(GL10 arg0) {
        }

        @Override
        public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
        }
    }

    private static class DemoGLSurfaceView extends GLSurfaceView {
        DemoRenderer mRenderer;

        public DemoGLSurfaceView(Context context) {
            super(context);
            try {
                setEGLConfigChooser(8, 8, 8, 8, 0, 0);
                mRenderer = new DemoRenderer();
                setRenderer(mRenderer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 初始化GPU
     *
     * @param context
     * @param viewGroup
     */
    public static void initGpuInfo(Context context, ViewGroup viewGroup) {
        if (TextUtils.isEmpty(GPU))
            GPU = DefaultSharePrefManager.getString(SharePrefConstant.KEY_GPU_INFO, "");

        if (!TextUtils.isEmpty(GPU)) {
            return;
        }

        DemoGLSurfaceView sf = new DemoGLSurfaceView(context);
        LinearLayout.LayoutParams sfParams = new LinearLayout.LayoutParams(1, 1);
        viewGroup.addView(sf, sfParams);
    }

    /**
     * 判断是否是平板
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    /**
     * 获取GPU信息
     *
     * @return
     */
    public static String getGPUInfo() {
        if (TextUtils.isEmpty(GPU))
            GPU = DefaultSharePrefManager.getString(SharePrefConstant.KEY_GPU_INFO, "");
        return GPU;
    }

    /**
     * 判断是否可能有ROOT权限
     * @return
     */
    public static boolean haveRoot() {
        try {
            File file = new File("/system/bin/su");
            File file2 = new File("/system/xbin/su");
            return file.exists() || file2.exists();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取MacAddress
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        if (!TextUtils.isEmpty(Mac)) {
            return Mac;
        }
        FileInputStream ins = null;
        FileOutputStream os = null;
        try {
            String sdcardPath = DirManager.getFilesDir("mac_address").getAbsolutePath();
            String path = sdcardPath + "/data/.systemmac";
            File file = new File(path);
            if (!file.exists()) {
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifi = wm.getConnectionInfo();
                if (wifi != null) {
                    Mac = wifi.getMacAddress();
                    if (!TextUtils.isEmpty(Mac)) {
                        File dir = file.getParentFile();
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        os = new FileOutputStream(file);
                        os.write(Mac.getBytes("UTF-8"));
                    }
                }
            } else {
                ins = new FileInputStream(file);
                byte[] buff = new byte[(int) file.length()];
                ins.read(buff);
                Mac = new String(buff, "UTF-8");
            }
            return Mac;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            Utils.closeCloseable(os);
            Utils.closeCloseable(ins);
        }
        return null;
    }

    /**
     * 获取VersionCode
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取客户端版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /**
     * 获取APP版本名
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context, String pkg) {
        if (TextUtils.isEmpty(pkg))
            return "";
        try {
            return context.getPackageManager().getPackageInfo(pkg, 0).versionName;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取国家
     *
     * @return
     */
    public static String getCountry() {
        String country = Locale.getDefault().getCountry();
        return TextUtils.isEmpty(country) ? Locale.getDefault().getISO3Country() : country;
    }

    /**
     * 获取手机号码
     *
     * @param mContext
     * @return
     */
    public static String getMobileNumber(Context mContext) {
        if (checkPermission(mContext, "android.permission.READ_PHONE_STATE")) {
            TelephonyManager phoneMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String mobileNumber = phoneMgr.getLine1Number();
            mobileNumber = TextUtils.isEmpty(mobileNumber) ? "" : mobileNumber;
            if (!TextUtils.isEmpty(mobileNumber)) {
                mobileNumber = mobileNumber.replaceAll("\\+86", "");
            }
            return mobileNumber;
        } else {
            return "";
        }
    }

    /**
     * 变更应用语言
     * @param locale 常量
     */
    public static void changeLanguage(Context context, Locale locale) {
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, null);
    }

    /**
     * 重新启动应用
     */
    public static void rebootApp(final Context context) {
        ActivityUtils.getScreenManager().popAllActivity();
        startApp(context, context.getPackageName());
    }

    /**
     * 启动APP
     * @param context
     * @param pkg
     */
    public static boolean startApp(Context context, String pkg) {
        try {
            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(pkg);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            LogManager.e(e);

        }

        return false;
    }

    public static boolean checkPermission(Context paramContext, String paramString) {
        PackageManager localPackageManager = paramContext.getPackageManager();
        if (localPackageManager.checkPermission(paramString, paramContext.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private static String getMAC(Context context) {
        String mac = "";
        try {
            if (checkPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                mac = info != null ? info.getMacAddress() : "";
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return mac;
    }

    /**
     * 获取手机设备描述（包括品牌、型号等） Xiaomi MI 4LTE
     * @param
     * @return
     */
    public static String getPhoneType() {
        StringBuffer sb = new StringBuffer();
        sb.append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append(" ");
        return sb.toString();
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取property配置文件信息
     * @param context
     * @param assertName 配置文件名
     * @param propertyKey
     * @return
     */
    private static String loadAssertChannel(Context context, String assertName, String propertyKey) {
        String channel = null;
        InputStream in = null;
        try {
            //assert目录下
            Properties props = new Properties();
            in = context.getAssets().open(assertName);
            props.load(in);
            channel = props.getProperty(propertyKey, "");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return channel;
    }
}
