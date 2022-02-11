package com.brotherhood.o2o.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.ui.widget.account.StrokeCircleTransformation;
import com.brotherhood.o2o.util.BitmapUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.executor.FifoPriorityThreadPoolExecutor;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 用于管理图片加载
 */
public class ImageLoaderManager {

    private static GlideBuilder glideBuilder;

    public static void init(Context context) {
        glideBuilder = new GlideBuilder(context);
    }

    /**
     * 设置在内置存储路径缓存目录文件，路径为Android/data/cache/dir
     * @param dir
     * @param size
     * @param context
     */
    public static void setInternalCacheDiskCache(String dir, int size, Context context) {
        glideBuilder.setDiskCache(new InternalCacheDiskCacheFactory(context, dir, size));
    }

    /**
     * 设置在外置存储路径缓存目录文件，路径为/near/img/
     * @param dir
     * @param size
     */
    public static void setExternalCacheDiskCache(Context context,final String dir, final int size) {
        glideBuilder.setDiskCacheService(new FifoPriorityThreadPoolExecutor(4));
        if (!Glide.isSetup()) {
            GlideBuilder gb = new GlideBuilder(context);
            DiskCache dlw = DiskLruCacheWrapper.get(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir), size);
            gb.setDiskCache(dlw);
            Glide.setup(gb);
        }
    }

    /**
     * 传入uri加载图片
     *
     * @param context
     * @param imageView
     * @param uri
     * @param defaultBitmap
     */
    public static void displayImageByUri(Context context, ImageView imageView, Uri uri, Bitmap defaultBitmap) {
        Drawable defaultDrawable = BitmapUtil.bitmap2Drawable(defaultBitmap);
        displayImageByUri(context, imageView, uri, defaultDrawable);
    }

    /**
     * 传入uri加载图片
     *
     * @param context
     * @param imageView
     * @param uri
     * @param defaultDrawable
     */
    public static void displayImageByUri(Context context, ImageView imageView, Uri uri, Drawable defaultDrawable) {
        if (uri == null) {
            return;
        }
        Glide.with(context).load(uri).error(defaultDrawable).diskCacheStrategy(DiskCacheStrategy.RESULT ).fallback(defaultDrawable).into(imageView);
    }

    /**
     * 传入uri加载图片
     *
     * @param context
     * @param imageView
     * @param uri
     * @param drawableId
     */
    public static void displayImageByUri(Context context, ImageView imageView, Uri uri, int drawableId) {
        Glide.with(context).load(uri).error(drawableId).diskCacheStrategy(DiskCacheStrategy.RESULT).fallback(drawableId).into(imageView);
    }

    public static void displayImageByUrl(Context context, ImageView imageView, String url, @DrawableRes int defaultId, @DrawableRes int errorId) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT).error(errorId).placeholder(defaultId).into(imageView);
    }


    /**
     * 传入String加载图片
     *
     * @param context
     * @param imageView
     * @param url
     * @param drawableId 默认图
     */
    public static void displayImageByUrl(Context context, ImageView imageView, String url, int drawableId) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).placeholder(drawableId).into(imageView);
    }

    /**
     * 加载并缓存该图片原图尺寸及与控件相同的尺寸(同一张图在不同布局使用，默认只缓存与控件大小一致的尺寸)
     *
     * @param context
     * @param imageView
     * @param url
     * @param drawableId
     */
    public static void displayImageByUrlCacheAllSize(Context context, ImageView imageView, String url, int drawableId) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).placeholder(drawableId).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    public static void displayRoundImageByUrl(Context context, ImageView imageView, String url, int drawableId, int radius) {
        if (radius == 0) {
            radius = 10;
        }
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT).bitmapTransform(new RoundedCornersTransformation(Glide.get(context).getBitmapPool(), radius, 0)).error(drawableId).placeholder(drawableId).into(imageView);
    }

    public static void displayRoundImageByFile(Context context, ImageView imageView, File file, int radius, @DrawableRes int drawableId) {
        Glide.with(context).load(file).bitmapTransform(new RoundedCornersTransformation(Glide.get(context).getBitmapPool(), radius, 0)).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).fallback(drawableId).into(imageView);
    }

    public static void displayCircleImageByFile(Context context, ImageView imageView, File file, @DrawableRes int drawableId) {
        Glide.with(context).load(file).bitmapTransform(new CropCircleTransformation(Glide.get(context).getBitmapPool())).error(drawableId).fallback(drawableId).into(imageView);
    }

    public static void displayRoundImageByUrl(Context context, ImageView imageView, int resourceId, int drawableId, int radius) {
        if (radius == 0) {
            radius = 10;
        }
        Glide.with(context).load(resourceId).bitmapTransform(new RoundedCornersTransformation(Glide.get(context).getBitmapPool(), radius, 0)).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).placeholder(drawableId).into(imageView);
    }

    public static void displayCircleImageByUrl(Context context, ImageView imageView, String url, int drawableId) {
//        if (Utils.isOnMainThread()) {
            Glide.with(context).load(url).bitmapTransform(new CropCircleTransformation(Glide.get(context).getBitmapPool())).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).placeholder(drawableId).into(imageView);
//        }
    }

    public static void displayCircleImageByResource(Context context, ImageView imageView, int resourceId, int drawableId) {

        Glide.with(context).load(resourceId).bitmapTransform(new CropCircleTransformation(Glide.get(context).getBitmapPool())).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).placeholder
                (drawableId).into(imageView);
    }

    public static void displayCircleBorderImageByUrl(Context context, ImageView imageView, String url, int borderWidth, int drawableId) {
        Glide.with(context).load(url).bitmapTransform(new StrokeCircleTransformation(Glide.get(context).getBitmapPool(), borderWidth)).diskCacheStrategy(DiskCacheStrategy.RESULT).error
                (drawableId)
                .placeholder
                        (drawableId).into(imageView);
    }


    /**
     * 传入File加载图片
     *
     * @param context
     * @param imageView
     * @param file
     * @param drawableId
     */
    public static void displayImageByFile(Context context, ImageView imageView, File file, int drawableId) {
        Glide.with(context).load(file).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).fallback(drawableId).into(imageView);
    }

    /**
     * 根据路径加载图片
     *
     * @param context
     * @param imageView
     * @param file
     * @param defaultId
     * @param errorId
     */
    public static void displayImageByFile(Context context, ImageView imageView, File file, @DrawableRes int defaultId, @DrawableRes int errorId) {
        Glide.with(context).load(file).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).error(errorId).placeholder(defaultId).into(imageView);
    }

    /**
     * 传入File加载图片
     *
     * @param context
     * @param imageView
     * @param file
     */
    public static void displayImageByFile(Context context, ImageView imageView, File file) {
        displayImageByFile(context, imageView, file, R.mipmap.img_default);
    }

    /**
     * 传入Drawable加载图片
     *
     * @param context
     * @param imageView
     * @param drawable
     * @param drawableId
     */
    public static void displayImageByBitmapDrawable(Context context, ImageView imageView, BitmapDrawable drawable, int drawableId) {
        byte[] bytes = BitmapUtil.Drawable2Bytes(drawable);
        Glide.with(context).load(bytes).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).fallback(drawableId).into(imageView);
    }

    public static void displayImageByResource(Context context, ImageView imageView, @DrawableRes int drawableId) {
        Glide.with(context).load(drawableId).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(drawableId).into(imageView);
    }

    public static void displayRoundImageByResource(Context context, ImageView imageView, @DrawableRes int drawableId, int defaultDrawableId, int radius) {
        if (radius == 0) {
            radius = 10;
        }
        Glide.with(context).load(drawableId).bitmapTransform(new RoundedCornersTransformation(Glide.get(context).getBitmapPool(), radius, 0)).diskCacheStrategy(DiskCacheStrategy.RESULT).error(defaultDrawableId).placeholder(defaultDrawableId).into(imageView);
    }
    /**
     * 传入Bitmap加载图片
     * @param context
     * @param imageView
     * @param bitmap
     * @param drawableId
     */
    public static void displayImageByBitmap(Context context, ImageView imageView, Bitmap bitmap, int drawableId) {
        if (bitmap == null) {
            return;
        }
        byte[] bytes = BitmapUtil.Bitmap2Bytes(bitmap);
        Glide.with(context).load(bytes).diskCacheStrategy(DiskCacheStrategy.RESULT).error(drawableId).fallback(drawableId).into(imageView);
    }

    /**
     * 传入Object加载图片,并且传入监听事件，在加载图片结束后做下一步动作
     *
     * @param context
     * @param imageView
     * @param object
     * @param listener
     * @param defaultDrawable
     */
    public static void displayImageWithListener(Context context, ImageView imageView, Object object, final ImageLoaderlistener listener, Drawable defaultDrawable) {
        if (object == null) {
            return;
        }
        if (object instanceof BitmapDrawable) {
            byte[] bytes = BitmapUtil.Drawable2Bytes((BitmapDrawable) object);
            object = bytes;
        } else if (object instanceof Bitmap) {
            byte[] bytes = BitmapUtil.Bitmap2Bytes((Bitmap) object);
            object = bytes;
        }

        Glide.with(context).load(object).bitmapTransform(new RoundedCornersTransformation(Glide.get(context).getBitmapPool(), 10, 0)).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<Object, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (listener != null) {
                    listener.loadException(e);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.loadCompeted(resource);
                }
                return false;
            }
        }).placeholder(defaultDrawable).error(defaultDrawable).fallback(defaultDrawable).into(imageView);
    }

    public static void displayImageWithListener(Context context, ImageView imageView, Object object, final ImageLoaderlistener listener) {
        Glide.with(context).load(object).diskCacheStrategy(DiskCacheStrategy.RESULT).listener(new RequestListener<Object, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (listener != null) {
                    listener.loadException(e);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.loadCompeted(resource);
                }
                return false;
            }

        }).into(imageView);

    }

    /**
     * 滑动时停止请求，停止滑动恢复请求
     * @param context
     * @param isPause
     */
    public static void pauseOrResumeRequest(Context context, boolean isPause) {
        if (isPause) {
            Glide.with(context).pauseRequestsRecursive();
        } else {
            Glide.with(context).resumeRequestsRecursive();
        }
    }

    public interface ImageLoaderlistener {
        void loadCompeted(GlideDrawable resource);

        void loadException(Exception e);
    }
}
