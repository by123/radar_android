package com.brotherhood.o2o.explore.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.explore.model.RadarItemBean;
import com.brotherhood.o2o.location.LocationComponent;
import com.brotherhood.o2o.test.blur.Blur;
import com.brotherhood.o2o.test.blur.ImageUtils;
import com.brotherhood.o2o.test.blur.ScrollableImageView;
import com.brotherhood.o2o.utils.ByLogout;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.utils.ContactUtils;
import com.brotherhood.o2o.utils.MD5Utils;
import com.brotherhood.o2o.utils.Utils;
import com.brotherhood.o2o.ui.widget.radar.DirectLayout;
import com.brotherhood.o2o.ui.widget.radar.HeadViewBuilder;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by by.huang on 2015/6/16.
 */
public class ExplperHelper {

    private static ExplperHelper mHelper;
    private static byte[] sync = new byte[0];
    private final static float SCALEFACTOR = 15f;
    private final static float RADIUS = 2f;
    private Drawable mLastDrawable;
    private Bitmap mOverlay;
    public double Radar_Center_W = (Constants.SCREEN_WIDTH * 15 / 32);
    public double Radar_Center_H = (Constants.SCREEN_WIDTH * 15 / 32);
    //雷达扫描动画
    public final static int SCAN_TIME = 3000;

    public static ExplperHelper getInstance() {
        if (mHelper == null) {
            synchronized (sync) {
                if (mHelper == null) {
                    mHelper = new ExplperHelper();
                }
            }
        }
        return mHelper;
    }

    /**
     * 雷达扫描动画
     *
     * @param view
     */
    public void startScanAnim(View view) {
        RotateAnimation animation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(SCAN_TIME);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                if (mLastDatas != null && mLastDatas.size() > 0) {
                    for (final RadarItemBean data : mLastDatas) {
                        long delayTimes = Constants.HEAD_DELAY
                                + (long) (data.mDegree * (SCAN_TIME / (Math.PI * 2)));
                        if (data.mHeadView != null) {
                            data.mHeadView.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    data.mHeadView.setVisibility(View.VISIBLE);
                                    final AnimatorSet animatorSet = new AnimatorSet();
                                    animatorSet.setDuration(200);
                                    animatorSet.playTogether(ObjectAnimator.ofFloat(data.mHeadView, Constants.ALPHA, 0f, 1f));
                                    animatorSet.start();

                                }
                            }, delayTimes);
                        }

                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                StartShowOther();
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }


    /**
     * 模糊背景
     */
    public void applyBlur(final ImageView imageView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        try {
                            imageView.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            imageView.buildDrawingCache();
                            final Bitmap bmp = imageView.getDrawingCache();
                            blur(bmp, imageView);
                            imageView.destroyDrawingCache();
                        } catch (Exception e) {
                        }
                        return true;
                    }
                });
    }

    private Bitmap doBlurJniArray(Bitmap sentBitmap, int radius,
                                  boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        ImageBlur.blurIntArray(pix, w, h, radius);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    private void blur(Bitmap bkg, ImageView view) {
        if (bkg == null) {
            return;
        }

        int viewHeight;
        int viewWidth;
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0) {
            viewWidth = Constants.SCREEN_WIDTH;
            viewHeight = Constants.SCREEN_HEIGHT;

        } else {
            viewWidth = view.getMeasuredWidth();
            viewHeight = view.getMeasuredHeight();
        }

        mOverlay = Bitmap.createBitmap((int) (viewWidth / SCALEFACTOR),
                (int) (viewHeight / SCALEFACTOR), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(mOverlay);
        canvas.translate(-view.getLeft() / SCALEFACTOR, -view.getTop(

        )

                / SCALEFACTOR);
        canvas.scale(1 / SCALEFACTOR, 1 / SCALEFACTOR);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        mOverlay = doBlurJniArray(mOverlay, (int) RADIUS, true);
        mLastDrawable = new BitmapDrawable(MyApplication.mApplication.getResources(), mOverlay);
        view.setImageDrawable(mLastDrawable);
    }

    /**
     * 绘制附近的人在雷达盘上的分布
     */
    private static int CENTER_WIDTH = Utils.dip2px(76);
    private static int POINT_WIDTH = Utils.dip2px(15);
    private static int SERVER_WIDTH = Utils.dip2px(53);
    private static int PEOPLE_WIDTH = Utils.dip2px(40);
    private static int OFFSET = Utils.dip2px(10);
    private int width = POINT_WIDTH;
    private ArrayList<RadarItemBean> mLastDatas;

    public void ReDrawHeadInfo(View mScanImg, DirectLayout mRadarLayout, ArrayList<RadarItemBean> requestDatas) {
        if (mLastDatas != null && mLastDatas.size() > 0) {
            for (RadarItemBean data : mLastDatas) {
                mRadarLayout.removeView(data.mHeadView);
            }
        }
        HeadViewBuilder builder = new HeadViewBuilder(MyApplication.mApplication.getApplicationContext());
        // 计算当前雷达半径对应的实际距离
        double dSpan = Constants.dLargestDistance;
        //雷达盘的半径对应的距离
        double rSpan = Radar_Center_W;
        if (requestDatas == null) {
            return;
        }
        RadarItemBean data = null;
        ByLogout.out(Constants.dLargestDistance + "最大距离");
        synchronized (requestDatas) {
            for (int i = 0; i < requestDatas.size(); i++) {
                data = requestDatas.get(i);
                View headView = null;
                if (data.mType == 0) {
                    if (data.mIsFriend == 0) {
                        headView = builder.buildHeadViewPoint(data.mGender);
                        width = POINT_WIDTH;
                    } else {
                        if (!TextUtils.isEmpty(data.mAvatarUrl)) {
                            ByLogout.out("头像地址->" + data.mAvatarUrl);
                            headView = builder.buildHeadViewPeople(data.mAvatarUrl);
                            width = PEOPLE_WIDTH;
                        } else {
                            headView = builder.buildHeadViewPoint(data.mGender);
                            width = POINT_WIDTH;
                        }
                    }
                } else if (data.mType == 1 || data.mType == 2) {
                    width = SERVER_WIDTH;
                    headView = builder.buildHeadViewServer();
                } else {
                    width = SERVER_WIDTH;
                    headView = builder.buildHeadViewServer();
                }
                GetXYValue(data);
                double bValue = data.mDistance * rSpan / dSpan;
                if (data.mType != 0) {
                    bValue = Constants.dLargestDistance * rSpan / dSpan;
                }
                double xValue = 0.0;
                double yValue = 0.0;
                if (bValue < CENTER_WIDTH / 2 + width / 2 + OFFSET) {
                    bValue = CENTER_WIDTH / 2 + width / 2 + OFFSET;
                    xValue = bValue * Math.cos(data.mDegree);
                    yValue = bValue * Math.sin(data.mDegree);

                    xValue = Radar_Center_W + xValue - width / 2;
                    yValue = Radar_Center_H + yValue - width / 2;
                } else {
                    if (bValue - Radar_Center_W <= 0 && bValue + (width / 2) >= Radar_Center_W) {

                        xValue = (Radar_Center_W - width) * Math.cos(data.mDegree);
                        yValue = (Radar_Center_H - width) * Math.sin(data.mDegree);

                        xValue = Radar_Center_W + xValue - OFFSET;
                        yValue = Radar_Center_H + yValue - OFFSET;
                    } else {
                        xValue = Radar_Center_W + bValue * Math.cos(data.mDegree);
                        yValue = Radar_Center_H + bValue * Math.sin(data.mDegree);
                    }
                }
                data.mPosX = xValue;
                data.mPosY = yValue;
                ViewHelper.setTranslationX(headView, (float) xValue);
                ViewHelper.setTranslationY(headView, (float) yValue);
                data.mHeadView = headView;
                mRadarLayout.addView(headView);
                headView.setVisibility(View.GONE);
            }
        }
        this.mLastDatas = requestDatas;
        startScanAnim(mScanImg);
    }

    /**
     * 根据 经纬度信息获取X,Y坐标;
     */
    public void GetXYValue(RadarItemBean bean) {
        double degree = getDegree(bean.mLatitude, bean.mLontitude);
        ByLogout.out("degree->" + degree);
        while (degree > 360) {
            degree = degree - 360;
            if (degree <= 360)
                break;
        }
        bean.mDegree = degree * Math.PI / 180;
    }

    private double getDegree(double lat, double lon) {
        double lat1 = Constants.toRad(LocationComponent.shareComponent().getCachedCurrentAddressOrNil().mLongitude);
        double lon1 = Constants.toRad(LocationComponent.shareComponent().getCachedCurrentAddressOrNil().mLatitude);

        double lat2 = Constants.toRad(lat);
        double lon2 = Constants.toRad(lon);
        double dLon = (lon2 - lon1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double brng = Constants.toDeg(Math.atan2(y, x));
        brng = (brng + 360);
        brng = (brng > 360) ? (brng - 360) : brng;
        return brng;
    }

    /**
     * 雷达点跳动动画
     */
    private void StartShowOther() {
        if (mLastDatas != null && mLastDatas.size() > 0) {
            for (final RadarItemBean data : mLastDatas) {
                final AnimatorSet setAmimation = new AnimatorSet();
                setAmimation.playTogether(
                        ObjectAnimator.ofFloat(
                                data.mHeadView, Constants.SCALE_X,
                                1.2f, 1.0f), ObjectAnimator.ofFloat(
                                data.mHeadView, Constants.SCALE_Y,
                                1.2f, 1.0f));

                long delayTimes = Constants.HEAD_DELAY
                        + (long) (data.mDegree * (SCAN_TIME / (Math.PI * 2)));
                if (data.mHeadView != null) {
                    data.mHeadView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            setAmimation.setDuration(200).start();
                        }
                    }, delayTimes);
                }

            }
        }
    }


    /**
     * 集码效果
     */
    private ValueAnimator valueAnimator;
    private static int CodeAnim_Time = 500;

    public interface OnCodeAnimListener {
        void OnFinish();
    }

    public void startActivityCodeAnim(final View mView, final TextView mTextView, int startX, int startY, int endX, int endY, final OnCodeAnimListener listener) {

        ByLogout.out(endY + "endY");
        valueAnimator = ValueAnimator.ofObject(new BezierEvaluator(startX, startY),
                new PointF(startX, startY), new PointF(endX, endY));
        valueAnimator.setDuration(CodeAnim_Time);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                ViewHelper.setX(mView, pointF.x);
                ViewHelper.setY(mView, pointF.y);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(
                                mView, Constants.SCALE_X,
                                1.0f, 0.3f), ObjectAnimator.ofFloat(
                                mView, Constants.SCALE_Y,
                                1.0f, 0.3f));
                animatorSet.setDuration(CodeAnim_Time);
                animatorSet.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mView.setVisibility(View.GONE);
                mTextView.setBackgroundResource(R.drawable.shape_draw_red);
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(
                                mTextView, Constants.SCALE_X,
                                0f, 1f), ObjectAnimator.ofFloat(
                                mTextView, Constants.SCALE_Y,
                                0f, 1f));
                animatorSet.setDuration(CodeAnim_Time);
                animatorSet.start();
                listener.OnFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setTarget(mView);
        valueAnimator.setupEndValues();
        valueAnimator.setRepeatCount(0);
        valueAnimator.start();
    }

    class BezierEvaluator implements TypeEvaluator<PointF> {
        int x = 0;
        int y = 0;

        public BezierEvaluator(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue,
                               PointF endValue) {
            final float t = fraction;
            float oneMinusT = 1.0f - t;
            PointF point = new PointF();

            PointF point0 = (PointF) startValue;

//            PointF point1 = new PointF();
//            if(x < Constants.SCREEN_WIDTH * 15/32 )
//            {
//                point1.set(-Constants.SCREEN_WIDTH/8, Constants.SCREEN_HEIGHT*2/3);
//            }
//            else
//            {
//                point1.set(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT*2/3);
//
//            }

            PointF point2 = (PointF) endValue;

//            point.x = oneMinusT * oneMinusT *  (point0.x)
//                    + 2 * oneMinusT * t * (point1.x)
//                    + t * t  * (point2.x);
//
//            point.y = oneMinusT * oneMinusT * (point0.y)
//                    + 2 * oneMinusT  * t * (point1.y)
//                    + t * t  * (point2.y);


            point.x = oneMinusT * (point0.x)
                    + t * (point2.x);

            point.y = oneMinusT * (point0.y)
                    + t * (point2.y);
            return point;
        }
    }

    /**
     * 底部列表出现动画
     *
     * @param view
     */
    public void showRecycleViewAnim(View view) {
        view.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, Constants.TRANSLATION_X, Constants.SCREEN_WIDTH, 0));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    /**
     * 底部布局出现动画
     *
     * @param view
     */
    public void showBottomLayout(View view, View view2, View view3, final View view4) {
        view4.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, Constants.TRANSLATION_Y, Utils.dip2px(200), 0), ObjectAnimator.ofFloat(view2, Constants.TRANSLATION_Y, 0, -Utils.dip2px(60)), ObjectAnimator.ofFloat(view3, Constants.TRANSLATION_Y, 0, -Utils.dip2px(60)));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showRecycleViewAnim(view4);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    /**
     * 底部布局隐藏动画
     *
     * @param view
     */
    public void hideBottomLayout(final View view, View view2, View view3) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, Constants.TRANSLATION_Y, 0, Utils.dip2px(200)), ObjectAnimator.ofFloat(view2, Constants.TRANSLATION_Y, -Utils.dip2px(60), 0), ObjectAnimator.ofFloat(view3, Constants.TRANSLATION_Y, -Utils.dip2px(60), 0));
        animatorSet.setDuration(500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    /**
     * 获取联系人信息
     *
     * @param radarItemBeans
     * @return
     */
    public ArrayList<RadarItemBean> getContactList(ArrayList<RadarItemBean> radarItemBeans) {
        ArrayList<RadarItemBean> datas = new ArrayList<>();
        datas.addAll(radarItemBeans);
        List<String> mPhoneTemps = ContactUtils.getInstance().getPhoneList();
        List<String> mPhones = new ArrayList<>();
        for (int i = 0; i < mPhoneTemps.size(); i++) {
            mPhones.add(MD5Utils.MD5(mPhoneTemps.get(i).replaceAll(" ", "").replaceAll("-", "")));
        }
        for (int i = 0; i < radarItemBeans.size(); i++) {
            if (mPhones.contains(radarItemBeans.get(i).mPhone)) {
                datas.get(i).mIsFriend = 1;
            }
        }
        return datas;
    }


    /**
     * 磨砂效果
     *
     * @param scrollableImageView
     * @param imagePath
     */
    public void blurInScrub(final Activity context, final ScrollableImageView scrollableImageView, final String imagePath) {

        String path = Constants.ImageDir + "/blur.jpg";
        final File blurredImage = new File(path);
        if (!blurredImage.exists()) {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeFile(imagePath);
                    Bitmap newImg = Blur.fastblur(MyApplication.mApplication.getApplicationContext(), image, 12);
                    ImageUtils.storeImage(newImg, blurredImage);
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Bitmap bmpBlurred = BitmapFactory.decodeFile(imagePath);
                            bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, Constants.SCREEN_WIDTH, (int) (bmpBlurred.getHeight() * ((float) Constants.SCREEN_WIDTH) / (float) bmpBlurred.getWidth()), false);
                            scrollableImageView.setoriginalImage(bmpBlurred);
                        }
                    });

                }
            }).start();

        } else {
            Bitmap bmpBlurred = BitmapFactory.decodeFile(path);
            bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, Constants.SCREEN_WIDTH, (int) (bmpBlurred.getHeight() * ((float) Constants.SCREEN_WIDTH) / (float) bmpBlurred.getWidth()), false);
            scrollableImageView.setoriginalImage(bmpBlurred);
        }
    }

}
