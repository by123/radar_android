package com.brotherhood.o2o.chat.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.brotherhood.o2o.chat.utils.SkipProguardInterface;
import com.brotherhood.o2o.ui.activity.base.BaseActivity;


public class ImgPreviewActivity extends BaseActivity implements
        ImgPreviewFragment.PreviewResultListener, SkipProguardInterface {

    private static final String TAG_PREVIEW = "tag_preview";
    private static final String TAG_DOWNLOAD = "tag_download";

    public static final String THUMB_PATH = "thumb_path";
    public static final String FILE_PATH = "file_path";
    public static final String PREVIEW_URI = "preview_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Intent intent = getIntent();
        Uri previewUri = intent.getParcelableExtra(PREVIEW_URI);
        if (previewUri != null) {
            ImgPreviewFragment ipf = new ImgPreviewFragment();
            Bundle args = new Bundle();
            args.putParcelable(ImgPreviewFragment.ARGS_IMAGE_URI, previewUri);
            ipf.setArguments(args);
            ipf.setPreviewResultListener(this);
            ft.add(android.R.id.content, ipf, TAG_PREVIEW);
            ft.commit();
        } else {
            String filePath = intent.getStringExtra(FILE_PATH);
            String thumbPath = intent.getStringExtra(THUMB_PATH);
            ImageDownloadFragment idf = new ImageDownloadFragment();
            Bundle args = new Bundle();
            args.putString(ImageDownloadFragment.ARGS_FILE_PATH, filePath);
            args.putString(ImageDownloadFragment.ARGS_THUMB_PATH, thumbPath);
            idf.setArguments(args);
            ft.add(android.R.id.content, idf, TAG_DOWNLOAD);
            ft.commit();
        }
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

}
