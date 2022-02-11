package com.brotherhood.o2o.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.ui.widget.ColorfulToast;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by by.huang on 2015/5/29.
 */
public class Utils {

    public static void showShortToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        ColorfulToast.orange(MyApplication.mApplication.getApplicationContext(), content, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(int stringId) {
        if (TextUtils.isEmpty(getString(stringId))) {
            return;
        }
        ColorfulToast.orange(MyApplication.mApplication.getApplicationContext(), getString(stringId), Toast.LENGTH_SHORT);
    }

    public static void showLongToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
//        ColorfulToast.orange(MyApplication.mApplication.getApplicationContext(),content,Toast.LENGTH_LONG);
    }
    public static String getString(int resId) {
        return MyApplication.mApplication.getString(resId);
    }

    public static String getString(int resId, String text) {
        return MyApplication.mApplication.getString(resId, text);
    }

    /**
     * Determine the current network is available ȷ����ǰ�������
     *
     * @return
     */
    public static boolean isNetWorkOk() {

        boolean result = false;
        try {
            NetworkInfo info = getNetworkInfo(MyApplication.mApplication);
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static NetworkInfo getNetworkInfo(Context context) {

        NetworkInfo info = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoArray[] = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo aItem : infoArray) {
            if (aItem != null && aItem.isConnected()) {
                if (aItem.getState() == NetworkInfo.State.CONNECTED) {
                    info = aItem;
                    break;
                }
            }
        }
        if (info == null) {
            info = connectivityManager.getActiveNetworkInfo();
        }
        return info;
    }

    /**
     * dp转px
     */
    public static int dip2px(float dpValue) {
        final float scale = MyApplication.mApplication.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dip(float pxValue) {
        final float scale = MyApplication.mApplication.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = MyApplication.mApplication
                .getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = MyApplication.mApplication
                .getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreentHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static void hideKeyboard(EditText... controls) {
        for (EditText control : controls)
            hideKeyboard(control);
    }

    public static void hideKeyboard(EditText editText) {
        if (editText == null)
            return;

        Context context = editText.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void writeStringToFile(String data, File file, boolean overrideIfExisted) {
        if (file.isDirectory() || (file.exists() && !overrideIfExisted))
            return;

        file.delete();

        byte[] buffer = data.getBytes();
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(buffer);
        } catch (Exception ignored) {
        } finally {
            closeStream(output);
        }
    }

    public static String readStringOrNilFromFile(File file) {
        if (!file.exists() || file.isDirectory())
            return null;

        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            return new String(buffer);
        } catch (Exception ignored) {
        } finally {
            closeStream(input);
        }

        return null;
    }

    public static void closeStream(Closeable streamOrNil) {
        if (streamOrNil != null) {
            try {
                streamOrNil.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 旋转图片
     *
     * @param imagePath
     * @return
     */
    public static String RoateImage(String imagePath) {
        String strPath = "";
        int iDegree = readPicDegree(imagePath);
        if (iDegree == 0)
            return imagePath;

        Bitmap bmpTmp = Utils.decodeFile(imagePath, -1, -1);
        Bitmap picBitmap = rotateBitmap(iDegree, bmpTmp);
        if (picBitmap != null) {
            bmpTmp.recycle();
            bmpTmp = null;

            strPath = saveBitmap(imagePath, picBitmap);
            picBitmap.recycle();
            picBitmap = null;
        }
        return strPath;
    }

    /**
     * 通过ExifInterface类读取图片文件的被旋转角度
     *
     * @param path ： 图片文件的路径
     * @return 图片文件的被旋转角度
     */
    public static int readPicDegree(String path) {
        int degree = 0;

        // 读取图片文件信息的类ExifInterface
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }

        return degree;
    }

    /**
     * 将图片纠正到正确方向
     *
     * @param degree ： 图片被系统旋转的角度
     * @param bitmap ： 需纠正方向的图片
     * @return 纠向后的图片
     */
    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bm;
    }

    public static String saveBitmap(String bitName, Bitmap mBitmap) {
        File dir = new File(Constants.ImageDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        int iLast = bitName.lastIndexOf('/');
        int iLength = bitName.length();
        String strFileName = bitName.substring(iLast, iLength);
        File f = new File(Constants.ImageDir + strFileName);
        try {
            f.createNewFile();
        } catch (IOException e) {

            return "";
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {

            return "";
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();

            fOut.close();
        } catch (IOException e) {

            return "";
        }
        return Constants.ImageDir + strFileName;
    }

    public static Bitmap decodeFile(String path, int reqWidth, int reqHeight
    ) {
        return decodeFile(path, reqWidth, reqHeight,
                Bitmap.Config.RGB_565);

    }

    public static Bitmap decodeFile(String path, int reqWidth, int reqHeight, Bitmap.Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        if (reqHeight != -1 && reqWidth != -1) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSize(options, reqWidth, reqHeight);
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static int calculateInSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            if (inSampleSize == 0) {
                inSampleSize = 1;
            }
        }
        return inSampleSize;
    }

    /**
     * 请求手否有效
     *
     * @param jsonStr
     * @return
     */
    public static boolean isRequestValid(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            int code = jsonObject.optInt("c");
            CodeUtil.checkCode(code);
            String msg = jsonObject.optString("msg");
            if (code == 0 && msg.equalsIgnoreCase("ok")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 改变文字颜色
     *
     * @param text
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static SpannableStringBuilder formatTextColor(String text, int start, int end, int color) {
        if (text.length() < end) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return builder;
    }


    /**
     * 格式化消息时间
     *
     * @param time
     * @return
     */
    public static String formatTime(long time, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = new Date(time * 1000);
        return format.format(date);
    }

    /**
     * 将彩色图转换为纯黑白二色
     *
     * @return 返回转换好的位图
     */
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                //分离三原色
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                //转化成灰度像素
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        //新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }


    /**
     * 文本复制功能
     *
     * @param content
     */
    public static void copy(String content) {
        ClipboardManager cmb = (ClipboardManager) MyApplication.mApplication.getApplicationContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }




    //移植过来的


    private static ThreadLocal<byte[]> threadSafeByteBuf = null;

    public static byte[] getThreadSafeByteBuffer() {
        if (threadSafeByteBuf == null) {
            threadSafeByteBuf = new ThreadLocal<byte[]>();
        }

        byte[] buf = threadSafeByteBuf.get();

        if (buf == null) {
            buf = new byte[1024 * 4]; // 4kb
            threadSafeByteBuf.set(buf);
        }

        return buf;
    }


    /**
     * 关闭IO流
     * @param obj
     */
    public static void closeCloseable(Closeable obj) {
        try {
            // 修复小米MI2的JarFile没有实现Closeable导致崩溃问题
            if (obj != null && obj instanceof Closeable)
                obj.close();

        } catch (IOException e) {
            LogManager.e(e);
        }
    }

    // 产生userAgent
    public static String gennerateUserAgent(Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append("Mozilla/5.0 (Linux; U; Android");
        sb.append(Build.VERSION.RELEASE);
        sb.append("; ");
        sb.append(Locale.getDefault().toString());

        String model = Build.MODEL;
        if (!TextUtils.isEmpty(model)) {
            sb.append("; ");
            sb.append(model);
        }

        String buildId = Build.ID;
        if (!TextUtils.isEmpty(buildId)) {
            sb.append("; Build/");
            sb.append(buildId);
        }

        sb.append(") ");

        int versionCode = 0;
        String packageName = context.getPackageName();
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Keep the versionCode 0 as default.
        }

        sb.append(packageName);
        sb.append("/");
        sb.append(versionCode);

        sb.append("; ");
        return sb.toString();
    }

    public static void closeHttpEntity(HttpEntity en) {
        if (en != null) {
            try {
                en.consumeContent();
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }
}
