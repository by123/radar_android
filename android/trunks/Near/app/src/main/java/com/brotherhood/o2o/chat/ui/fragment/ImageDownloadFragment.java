package com.brotherhood.o2o.chat.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.helper.BitmapHelper;
import com.brotherhood.o2o.chat.helper.Utils;
import com.brotherhood.o2o.manager.ImageLoaderManager;
import com.brotherhood.o2o.task.TaskExecutor;
import com.brotherhood.o2o.util.DisplayUtil;

import java.io.File;

public class ImageDownloadFragment extends Fragment {

    private ImageView mImageView;
    private Bitmap mBitmap;
    public static final String ARGS_FILE_PATH = "file_path";//本地图片路径
    public static final String ARGS_FILE_TYPE = "file_type";//展示的图片类型(本地/网络)
    public static final int FILE_LOCAL_TYPE = 1;//本地图片
    public static final int FILE_URL_TYPE = 2;//网络图片

    public ImageDownloadFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_img_browse, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        v.findViewById(R.id.rl_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mImageView = (ImageView) v.findViewById(R.id.chat_browse_image);
        if (!Utils.isSdcardWritable(getActivity())) {
            mImageView.setImageResource(R.mipmap.chat_img_large_no_sdcard);
            return;
        }
        Bundle bundle = getArguments();
        String largeFilePath = bundle.getString(ARGS_FILE_PATH);
        int fileType = bundle.getInt(ARGS_FILE_TYPE, FILE_LOCAL_TYPE);
        if (fileType == FILE_LOCAL_TYPE){//
            final File file = new File(largeFilePath);
            if (!file.exists()){
                ImageLoaderManager.displayImageByFile(getActivity(), mImageView, file, R.mipmap.img_default, R.mipmap.chat_img_large_deleted);
                DisplayUtil.showToast(getActivity(), R.string.chat_img_not_found_or_ot);
            }else {
                try {
                    TaskExecutor.executeTask(new Runnable() {
                        @Override
                        public void run() {
                            mBitmap = BitmapHelper.decodeSampledBitmapFromFile(file.getPath(), DisplayUtil.getScreenWidth(getActivity()), DisplayUtil
                                    .getScreenHeight(getActivity()) / 2);
                            if (mBitmap != null){
                                TaskExecutor.runTaskOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageLoaderManager.displayImageByBitmap(getActivity(), mImageView, mBitmap, R.mipmap.img_default);
                                    }
                                });
                            }
                        }
                    });
                }catch (OutOfMemoryError e){
                    DisplayUtil.showToast(getActivity(), R.string.chat_can_not_show_img);
                }
            }
        }else if (fileType == FILE_URL_TYPE){
            ImageLoaderManager.displayImageByUrl(getActivity(), mImageView, largeFilePath, R.mipmap.img_default, R.mipmap.chat_img_large_no_sdcard);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if (mBitmap != null && !mBitmap.isRecycled()){
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
