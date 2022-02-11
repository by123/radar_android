package com.brotherhood.o2o.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import com.brotherhood.o2o.application.NearApplication;
import com.brotherhood.o2o.bean.UserInfoBean;
import com.brotherhood.o2o.chat.db.service.IMDBGroupService;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.util.BitmapUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by laimo.li on 2015/12/28.
 */
public class AvatarDownloadService {

    public static final int AVATAR_SIZE = 3;

    public static final int IO_BUFFER_SIZE = 8 * 1024;

    private ExecutorService LIMITED_TASK_EXECUTOR;

    private Object lock = new Object();

    private BitmapDrawable[] avatars = new BitmapDrawable[3];

    private DownloadStateListener listener;

    private List<UserInfoBean> userAvatarList;

    private String gid;

    private int size = 0;

    public interface DownloadStateListener {
        void onFinish(String iamgePath);

        void onFailed();
    }

    public AvatarDownloadService(String gid, List<UserInfoBean> userAvatarList,
                                 DownloadStateListener listener) {
        LIMITED_TASK_EXECUTOR = (ExecutorService) Executors
                .newFixedThreadPool(1);
        this.userAvatarList = userAvatarList;
        this.gid = gid;
        this.listener = listener;
    }

    public void startDownload() {
        File downloadDirectory = new File(Constants.HTTP_CACHE);
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }
        for (int i = 0; i < AVATAR_SIZE; i++) {
            try {
                final UserInfoBean bean = userAvatarList.get(i);
                LIMITED_TASK_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        downloadBitmap(bean.getAvatar());
                    }
                });
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
                listener.onFailed();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed();
            }
        }
    }

    private void downloadBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            final InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream(), IO_BUFFER_SIZE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            bitmap = BitmapUtil.toRoundBitmap(bitmap);

            Drawable drawable = new BitmapDrawable(bitmap);
            avatars[size] = (BitmapDrawable) drawable;

            statDownloadNum();

        } catch (final IOException e) {
            listener.onFailed();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                }
            }
        }
    }

    public void saveBitmapToFile(Bitmap bitmap)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(getAvatarPath());
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void statDownloadNum() {
        synchronized (lock) {
            size++;
            if (size == AVATAR_SIZE) {
                LIMITED_TASK_EXECUTOR.shutdownNow();
                Bitmap map = BitmapUtil.createGroupAvatar(NearApplication.mInstance.getApplicationContext(), avatars);
                try {
                    saveBitmapToFile(map);
                    IMDBGroupService.updateAvatar(Long.valueOf(gid), getAvatarPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinish(getAvatarPath());
                    }
                });
            }
        }
    }

    private String getAvatarPath() {
        return DirManager.getExternalStroageDir(Constants.HTTP_CACHE)+ "/" + gid + ".png";
    }

}
