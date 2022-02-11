package com.brotherhood.o2o.explore.controller;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by by.huang on 2015/6/2.
 */
public class ExploreAnim {
    public final static int SCAN_TIME = 4000;

    public static void startScanAnim(View view) {
        RotateAnimation animation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(SCAN_TIME);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }


//    //图片模糊方法
//    private final static float SCALEFACTOR = 15f;
//    private final static float RADIUS = 2f;
//    private static Drawable lastDrawable;
//
//    public static void blur(Context context, Bitmap bkg, ImageView view) {
//        if (bkg == null) {
//            return;
//        }
//        int viewHeight;
//        int viewWidth;
//        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
//            viewWidth = Constants.ScreenWidth;
//            viewHeight = Constants.ScreenHeight
//                    - (int) context.getResources().getDimension(
//                    R.dimen.titlebar_height);
//            //   R.dimen.actionbar_height
//
//        } else {
//            viewWidth = view.getMeasuredWidth();
//            viewHeight = view.getMeasuredHeight();
//        }
//        Bitmap overlay = Bitmap.createBitmap((int) (viewWidth / SCALEFACTOR),
//                (int) (viewHeight / SCALEFACTOR), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-view.getLeft() / SCALEFACTOR, -view.getTop()
//                / SCALEFACTOR);
//        canvas.scale(1 / SCALEFACTOR, 1 / SCALEFACTOR);
//        Paint paint = new Paint();
//        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//        canvas.drawBitmap(bkg, 0, 0, paint);
//        overlay = doBlurJniArray(overlay, (int) RADIUS, true);
//        lastDrawable = new BitmapDrawable(context.getResources(), overlay);
//        view.setImageDrawable(lastDrawable);
//    }
//
//    private static Bitmap doBlurJniArray(Bitmap sentBitmap, int radius,
//                                        boolean canReuseInBitmap) {
//        Bitmap bitmap;
//        if (canReuseInBitmap) {
//            bitmap = sentBitmap;
//        } else {
//            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
//        }
//        if (radius < 1) {
//            return (null);
//        }
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//        int[] pix = new int[w * h];
//        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
//        ImageBlur.blurIntArray(pix, w, h, radius);
//        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
//        return (bitmap);
//    }

}
