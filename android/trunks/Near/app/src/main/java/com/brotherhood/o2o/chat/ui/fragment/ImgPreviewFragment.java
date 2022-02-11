package com.brotherhood.o2o.chat.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.chat.helper.BitmapHelper;
import com.brotherhood.o2o.chat.helper.Utils;
import com.brotherhood.o2o.util.Res;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ImgPreviewFragment extends Fragment implements
        View.OnClickListener, View.OnTouchListener {

    public static interface PreviewResultListener {

        public void onPhotoConfirmed(ImgPreviewFragment f,
                                     ArrayList<byte[]> images);

        public void onPhotoCanceled(ImgPreviewFragment f);

    }

    public static final String ARGS_IMAGE_URI = "image_uri";

    private ImageView mImageView;
    private View mCompressBar;
    private View mBtnDone;

    private Uri mImageUri;
    private View mTop;
    private View mBottom;

    private PreviewResultListener mResultListener;

    public ImgPreviewFragment() {
    }

    public void setPreviewResultListener(PreviewResultListener l) {
        mResultListener = l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mImageUri = bundle.getParcelable(ARGS_IMAGE_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chat_photo_preview, container,
                false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mTop = root.findViewById(R.id.chat_pp_top);
        mTop.findViewById(R.id.chat_pp_back).setOnClickListener(this);

        mImageView = (ImageView) root.findViewById(R.id.chat_pp_image);
        mImageView.setOnTouchListener(this);
        mCompressBar = root.findViewById(R.id.chat_pp_compress_bar);

        mBottom = root.findViewById(R.id.chat_pp_bottom);
        mBottom.findViewById(R.id.chat_pp_cancel).setOnClickListener(this);
        mBtnDone = mBottom.findViewById(R.id.chat_pp_done);
        mBtnDone.setEnabled(false);
        mBtnDone.setOnClickListener(this);

        root.findViewById(R.id.rl_content).setOnClickListener(this);
        new CompressTask().execute(mImageUri);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.chat_pp_back) {
            if (mResultListener != null) {
                mResultListener.onPhotoCanceled(this);
            }
        } else if (id == R.id.chat_pp_done) {
            if (mResultListener != null) {
                mResultListener.onPhotoConfirmed(this, mResults);
            }
        } else if (id == R.id.chat_pp_cancel) {
            if (mResultListener != null) {
                mResultListener.onPhotoCanceled(this);
            }
        } else if (id == R.id.rl_content) {
            getActivity().finish();
        }

    }

    private boolean mVisible = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mResults == null) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mVisible) {
                mVisible = false;
//                mTop.startAnimation(AnimationUtils.loadAnimation(getActivity(),
//                        R.anim.push_up_out));
//                mBottom.startAnimation(AnimationUtils.loadAnimation(
//                        getActivity(), R.anim.push_bottom_out));
            } else {
                mVisible = true;
//                mTop.startAnimation(AnimationUtils.loadAnimation(getActivity(),
//                        R.anim.push_up_in));
//                mBottom.startAnimation(AnimationUtils.loadAnimation(
//                        getActivity(), R.anim.push_bottom_in));
            }
        }
        return true;
    }

    private ArrayList<byte[]> mResults;

    private class CompressTask extends AsyncTask<Uri, Void, ArrayList<byte[]>> {

        @Override
        protected ArrayList<byte[]> doInBackground(Uri... params) {
            ContentResolver cr = getActivity().getContentResolver();
            int count = params.length;
            ArrayList<byte[]> list = new ArrayList<byte[]>();
            for (int i = 0; i < count; i++) {
                try {
                    InputStream is = cr.openInputStream(params[i]);
                    byte[] bytes = Utils.getInputStreamAsBytes(is);

                    byte[] compressedBytes = null;
                    if (Utils.isGIF(bytes)) {
                        compressedBytes = bytes;
                    } else {
                        compressedBytes = BitmapHelper.compressBytes(bytes);
                    }

                    if (compressedBytes != null) {
                        list.add(compressedBytes);
                    }
                } catch (FileNotFoundException e) {
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<byte[]> result) {
            super.onPostExecute(result);
            mCompressBar.setVisibility(View.GONE);
            // Here we only have one image
            if (result.size() > 0) {
                byte[] first = result.get(0);

                if (Utils.isGIF(first)) {
//                    try {
//                        GifDrawable gifDrawable = new GifDrawable(first);
//                        mImageView.setImageDrawable(gifDrawable);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    Toast.makeText(getActivity(), Res.getString(R.string.not_support_gif_yet), Toast.LENGTH_LONG).show();
                } else {
                    Bitmap bmp = BitmapFactory.decodeByteArray(first, 0,
                            first.length);
                    mImageView.setImageBitmap(bmp);
                }

                mBtnDone.setEnabled(true);
                mResults = result;
            }
        }
    }

}
