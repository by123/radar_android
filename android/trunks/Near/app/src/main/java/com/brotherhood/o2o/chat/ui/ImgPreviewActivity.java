package com.brotherhood.o2o.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.ui.fragment.ImageDownloadFragment;
import com.brotherhood.o2o.chat.ui.fragment.ImgPreviewFragment;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;

import java.util.ArrayList;

/**
 * 图片预览
 */
public class ImgPreviewActivity extends BaseActivity implements ImgPreviewFragment.PreviewResultListener {

    private static final String TAG_PREVIEW = "tag_preview";
    private static final String TAG_DOWNLOAD = "tag_download";

    //    public static final String THUMB_PATH = "thumb_path";
    public static final String FILE_PATH = "file_path";
    public static final String PREVIEW_URI = "preview_uri";
    public static final String PREVIEW_TYPE = "preview_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Intent intent = getIntent();
        Uri previewUri = intent.getParcelableExtra(PREVIEW_URI);
        int fileType = intent.getIntExtra(PREVIEW_TYPE, ImageDownloadFragment.FILE_LOCAL_TYPE);//预览图片类型，默认是本地图片
        String filePath = intent.getStringExtra(FILE_PATH);
        if (previewUri != null) {
            ImgPreviewFragment ipf = new ImgPreviewFragment();
            Bundle args = new Bundle();
            args.putParcelable(ImgPreviewFragment.ARGS_IMAGE_URI, previewUri);
            ipf.setArguments(args);
            ipf.setPreviewResultListener(this);
            ft.add(android.R.id.content, ipf, TAG_PREVIEW);
            ft.commit();
        } else if (!TextUtils.isEmpty(filePath)) {
            ImageDownloadFragment idf = new ImageDownloadFragment();
            Bundle args = new Bundle();
            args.putString(ImageDownloadFragment.ARGS_FILE_PATH, filePath);
            args.putInt(ImageDownloadFragment.ARGS_FILE_TYPE, fileType);
            idf.setArguments(args);
            ft.add(android.R.id.content, idf, TAG_DOWNLOAD);
            ft.commit();
        }
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void onPhotoConfirmed(ImgPreviewFragment f, ArrayList<byte[]> images) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int size = images.size();
        intent.putExtra("size", size);
        for (int i = 0; i < size; i++) {
            intent.putExtra("pos" + i, images.get(i));
        }
        startActivity(intent);
    }

    @Override
    public void onPhotoCanceled(ImgPreviewFragment f) {
        finish();
    }

    public static void show(FragmentActivity context, String contentFilePath) {
        Intent intent = new Intent(context, ImgPreviewActivity.class);
        intent.putExtra(ImgPreviewActivity.FILE_PATH, contentFilePath);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.scale_out, 0);
    }

}
