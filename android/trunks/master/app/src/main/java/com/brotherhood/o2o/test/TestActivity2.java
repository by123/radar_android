package com.brotherhood.o2o.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.test.blur.Blur;
import com.brotherhood.o2o.test.blur.ImageUtils;
import com.brotherhood.o2o.test.blur.ScrollableImageView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by by.huang on 2015/7/6.
 */
public class TestActivity2 extends Activity {

    @InjectView(R.id.scrollable_imageview)
    ScrollableImageView mScrollableImg;



    private static final String BLURRED_IMG_PATH = "blurred_image.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);
        ButterKnife.inject(this);
        initView();
    }

    private void initView() {
        final int screenWidth = ImageUtils.getScreenWidth(this);
        mScrollableImg.setScreenWidth(screenWidth);
        blur(screenWidth);
    }

    private void blur(final int screenWidth) {
        final File blurredImage = new File(getFilesDir() + BLURRED_IMG_PATH);
        if (!blurredImage.exists()) {

            setProgressBarIndeterminateVisibility(true);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeResource(getResources(),
                            R.drawable.test, options);
                    Bitmap newImg = Blur.fastblur(TestActivity2.this, image, 12);
                    ImageUtils.storeImage(newImg, blurredImage);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            updateView(screenWidth);
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });

                }
            }).start();

        } else {
            updateView(screenWidth);
        }


//        dotest();
    }

//    private void dotest() {
//        handler.postDelayed(runnable, 500);
//    }
//
//    private int y = 0;
//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            y = y - 10;
//            mScrollableImg.handleScroll(y);
//            dotest();
//        }
//    };

    private void updateView(final int screenWidth) {
        Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir()
                + BLURRED_IMG_PATH);
        bmpBlurred = Bitmap
                .createScaledBitmap(
                        bmpBlurred,
                        screenWidth,
                        (int) (bmpBlurred.getHeight() * ((float) screenWidth) / (float) bmpBlurred
                                .getWidth()), false);

        mScrollableImg.setoriginalImage(bmpBlurred);
    }
}
