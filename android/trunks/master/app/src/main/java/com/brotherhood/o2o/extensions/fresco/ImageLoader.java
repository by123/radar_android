package com.brotherhood.o2o.extensions.fresco;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.brotherhood.o2o.application.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.Constants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by by.huang on 2015/5/29.
 * 图片处理
 */

public class ImageLoader {

    public static ImageLoader mInstance;
    private static Byte[] syncByte = new Byte[0];

    public interface ImageLoaderListener {
        void OnLoadStart();

        void OnLoadFinish(Bitmap bitmap);

        void OnLoadFail();
    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (syncByte) {
                if (mInstance == null) {
                    mInstance = new ImageLoader();
                }
            }
        }
        return mInstance;
    }

    /**
     * 网络读取图片
     *
     * @param mSimpleDraweeView
     * @param url
     */
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url) {
        setImageUrl(mSimpleDraweeView, url, 1.0f);
    }

    /**
     * 网络读取图片
     *
     * @param mSimpleDraweeView
     * @param url
     * @param proportion        图片宽高比
     */
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url, float proportion) {
        setImageUrl(mSimpleDraweeView, url, proportion, null, 0, 0);
    }

    /**
     * 网络读取图片
     *
     * @param mSimpleDraweeView
     * @param url
     * @param listener          下载监听
     */
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url, ImageLoaderListener listener) {
        setImageUrl(mSimpleDraweeView, url, 1.0f, listener, 0, 0);
    }

    /**
     * 网络读取图片(自定义宽高)
     *
     * @param mSimpleDraweeView
     * @param url
     * @param proportion
     * @param listener
     * @param width
     * @param height
     */


    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, final String url, float proportion, final ImageLoaderListener listener, final int width, final int height) {
        if (listener != null) {
            listener.OnLoadStart();
        }
        mSimpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.default_img);
        mSimpleDraweeView.setAspectRatio(proportion);
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                if (listener != null) {
                    listener.OnLoadFail();
                }
            }
        };

        Uri uri = Uri.parse(url);
        Postprocessor postprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return super.getName();
            }

            @Override
            public void process(Bitmap bitmap) {
                if (listener != null) {
                    listener.OnLoadFinish(bitmap);
                }
            }
        };

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (listener != null) {
            imageRequestBuilder.setPostprocessor(postprocessor);
        }
        imageRequestBuilder.setImageDecodeOptions(ImageDecodeOptions.defaults());
        imageRequestBuilder.setProgressiveRenderingEnabled(false);
        if (width != 0 && height != 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, height));
        }
        ImageRequest request = imageRequestBuilder.build();

        DraweeController controller = Fresco.newDraweeControllerBuilder().
                setControllerListener(controllerListener)
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .build();

        mSimpleDraweeView.setController(controller);
    }

    /**
     * 读取资源图片
     *
     * @param mSimpleDraweeView
     * @param resId
     */
    public void setImageResource(SimpleDraweeView mSimpleDraweeView, int resId) {
        mSimpleDraweeView.setAspectRatio(1.0f);
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithResourceId(resId);
        ImageRequest request = imageRequestBuilder.build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mSimpleDraweeView.getController())
                .build();

        mSimpleDraweeView.setController(controller);
    }

    /**
     * 读取资源图片
     *
     * @param mSimpleDraweeView
     * @param resId
     * @param width
     * @param height
     */
    public void setImageResource(SimpleDraweeView mSimpleDraweeView, int resId, int width, int height) {
        mSimpleDraweeView.setAspectRatio(1.0f);
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithResourceId(resId);
        if (width != 0 && height != 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, height));
        }
        ImageRequest request = imageRequestBuilder.build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mSimpleDraweeView.getController())
                .build();

        mSimpleDraweeView.setController(controller);
    }

    /**
     * 读取本地图片
     *
     * @param mSimpleDraweeView
     * @param path
     */
    public void setImageLocal(SimpleDraweeView mSimpleDraweeView, String path) {
        mSimpleDraweeView.setAspectRatio(1.0f);

        ImageRequest request = ImageRequest.fromUri(Uri.fromFile(new File(path)));
        ImageRequest[] requests = {request};

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setFirstAvailableImageRequests(requests)
                .setOldController(mSimpleDraweeView.getController())
                .build();
        mSimpleDraweeView.setController(controller);
    }

    /**
     * 先加载低质量图片，再加载高质量图片
     *
     * @param mSimpleDraweeView
     * @param lowUrl
     * @param highUrl
     */
    public void setMultiUrl(SimpleDraweeView mSimpleDraweeView, String lowUrl, String highUrl) {
        mSimpleDraweeView.setAspectRatio(1.0f);
        Uri lowResUri = Uri.parse(lowUrl);
        Uri highResUri = Uri.parse(highUrl);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(lowResUri))
                .setImageRequest(ImageRequest.fromUri(highResUri))
                .setOldController(mSimpleDraweeView.getController())
                .build();
        mSimpleDraweeView.setController(controller);
    }

    /**
     * 读取GIF图片格式
     *
     * @param mSimpleDraweeView
     * @param url
     */
    public void setImageGif(SimpleDraweeView mSimpleDraweeView, String url) {
        mSimpleDraweeView.setAspectRatio(1.0f);
        Uri uri = Uri.parse(url);
        if (null != uri) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
            mSimpleDraweeView.setController(controller);
        }
    }

    /**
     * 控制GIF动画启动
     *
     * @param mSimpleDraweeView
     */
    public void startGifAnim(SimpleDraweeView mSimpleDraweeView) {
        Animatable animation = mSimpleDraweeView.getController().getAnimatable();
        if (animation != null) {
            animation.start();
        }
    }

    /**
     * 控制GIF动画结束
     *
     * @param mSimpleDraweeView
     */
    public void stopGifAnim(SimpleDraweeView mSimpleDraweeView) {
        Animatable animation = mSimpleDraweeView.getController().getAnimatable();
        if (animation != null) {
            animation.stop();
        }
    }

    /**
     * 获取图片基础参数
     *
     * @return
     */
    public GenericDraweeHierarchyBuilder getImageProperty() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(MyApplication.mApplication.getResources());
        return builder;
    }

    /**
     * 设置图片基础参数
     *
     * @param mSimpleDraweeView
     * @param builder
     */
    public void setImageProperty(SimpleDraweeView mSimpleDraweeView, GenericDraweeHierarchyBuilder builder) {
        GenericDraweeHierarchy hierarchy = builder.build();
        mSimpleDraweeView.setHierarchy(hierarchy);
    }

    /**
     * 释放fresco
     */
    public void release() {
        Fresco.shutDown();
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     */
    public String saveBitmap(Bitmap bitmap) {
        String path = Constants.ImageDir + File.separator + System.currentTimeMillis();
        File dir = new File(Constants.ImageDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


}


