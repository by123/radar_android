package com.brotherhood.o2o.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.TextView;

import com.brotherhood.o2o.application.NearApplication;

/**
 * View处理
 * Created with Android Studio.
 */
public class ViewUtil {

    /**
     * 设置View背景
     *
     * @param view
     * @param background
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setViewBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * 设置TextView drawableLeft
     * @param textView
     * @param drawableId
     */
    public static void setTextViewDrawableLeft(TextView textView, @DrawableRes int drawableId){
        Drawable drawable = ViewUtil.getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 这一步必须要做,否则不会显示.
        textView.setCompoundDrawables(drawable, null, null, null);
    }
    /**
     * 设置TextView drawableTop
     * @param textView
     * @param drawableId
     */
    public static void setTextViewDrawableTop(TextView textView, @DrawableRes int drawableId){
        Drawable drawable = ViewUtil.getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 这一步必须要做,否则不会显示.
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = NearApplication.mInstance.getResources().getDrawable(drawableId, null);
        } else {
            drawable = NearApplication.mInstance.getResources().getDrawable(drawableId);
        }
        return drawable;
    }

    public static void setViewBackground(View view, int drawableId) {
        Drawable drawable = null;
        if (drawableId == 0){
            drawable = null;
        }else {
            drawable = view.getContext().getResources().getDrawable(drawableId);
        }
        setViewBackground(view, drawable);
    }

    /**
     * 判断view是否显示
     *
     * @param view
     * @return
     */
    public static boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * findViewById 省略强转过程
     *
     * @param activity
     * @param resId
     * @return
     */
    public static <V> V findView(Activity activity, @IdRes int resId) {
        return (V) activity.findViewById(resId);
    }

    /**
     * findViewById 省略强转过程
     *
     * @param resId
     * @param rootView
     * @param <V>具体的View类型
     * @return
     */
    @IdRes
    public static <V> V findView(View rootView, @IdRes int resId) {
        return (V) rootView.findViewById(resId);
    }

    /**
     * findviewById 并添加点击事�?
     *
     * @param activity
     * @param resId
     * @param onClickListener
     * @param <V>具体的View类型
     * @return
     */
    public static <V> V findViewAttachOnclick(Activity activity, @IdRes int resId, View.OnClickListener onClickListener) {
        View view = activity.findViewById(resId);
        view.setOnClickListener(onClickListener);
        //noinspection unchecked
        return (V) view;
    }

    /**
     * findviewById 并添加点击事�?
     *
     * @param rootView
     * @param resId
     * @param onClickListener
     * @param <V>具体的View类型
     * @return
     */
    @IdRes
    public static <V> V findViewAttachOnclick(View rootView, @IdRes int resId, View.OnClickListener onClickListener) {
        //noinspection unchecked
        View view = rootView.findViewById(resId);
        view.setOnClickListener(onClickListener);
        //noinspection unchecked
        return (V) view;
    }

    /**
     * 控制view的visible与Gone
     *
     * @param view
     * @param show
     */
    public static void toggleView(View view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
