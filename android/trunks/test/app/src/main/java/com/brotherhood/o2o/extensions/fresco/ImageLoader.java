package com.brotherhood.o2o.extensions.fresco;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.brotherhood.o2o.MyApplication;
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


/**
 * Created by by.huang on 2015/5/29.
 * ע�⣺ʹ��ǰ���������ø߶Ȼ��߿�ȵ�һ��̶���С,����ͼ���޷���ʾ
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

    //չʾͼƬ
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url) {
        setImageUrl(mSimpleDraweeView, url, 1.0f);
    }

    //����ͼƬ�ı���չʾͼƬ
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url, float proportion) {
        setImageUrl(mSimpleDraweeView, url, proportion, null, 0, 0);
    }

    //����ͼƬ����չʾͼƬ
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url, ImageLoaderListener listener) {
        setImageUrl(mSimpleDraweeView, url, 1.0f, listener, 0, 0);
    }

    //����ͼƬ�ı���������ؽ��չʾͼƬ
    public void setImageUrl(SimpleDraweeView mSimpleDraweeView, String url, float proportion, final ImageLoaderListener listener, final int width, final int height) {
       if(listener!=null)
       {
           listener.OnLoadStart();
       }
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

                if(listener!=null)
                {
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
                if(listener!=null)
                {
                    listener.OnLoadFinish(bitmap);
                }
            }
        };

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        imageRequestBuilder.setPostprocessor(postprocessor);
        imageRequestBuilder.setImageDecodeOptions(ImageDecodeOptions.defaults());
        imageRequestBuilder.setProgressiveRenderingEnabled(true);
        if (width != 0 && height != 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, height));
        }
        ImageRequest request = imageRequestBuilder.build();

        DraweeController controller = Fresco.newDraweeControllerBuilder().
                setControllerListener(controllerListener)
                .setImageRequest(request)
                .setOldController(mSimpleDraweeView.getController())
                .build();

        mSimpleDraweeView.setController(controller);
    }


    //������ԴͼƬ
    public void setImageResource(SimpleDraweeView mSimpleDraweeView, int resId) {
        mSimpleDraweeView.setAspectRatio(1.0f);
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(resId).build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mSimpleDraweeView.getController())
                .build();

        mSimpleDraweeView.setController(controller);
    }

    //���ñ���ͼƬ
    public void setImageLocal(SimpleDraweeView mSimpleDraweeView, String path) {
        mSimpleDraweeView.setAspectRatio(1.0f);

        ImageRequest request=ImageRequest.fromUri(Uri.fromFile(new File(path)));
        ImageRequest[] requests = {request};

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setFirstAvailableImageRequests(requests)
                .setOldController(mSimpleDraweeView.getController())
                .build();
        mSimpleDraweeView.setController(controller);
    }

    //����һ�ŵͷֱ�����չʾ����չʾһ�Ÿ߷ֱ���
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

    //չʾgifͼƬ,�����Զ�����
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

    //��ʼ����gif
    public void startGifAnim(SimpleDraweeView mSimpleDraweeView) {
        Animatable animation = mSimpleDraweeView.getController().getAnimatable();
        if (animation != null) {
            animation.start();
        }
    }

    //ֹͣ����gif
    public void stopGifAnim(SimpleDraweeView mSimpleDraweeView) {
        Animatable animation = mSimpleDraweeView.getController().getAnimatable();
        if (animation != null) {
            animation.stop();
        }
    }

    //��ȡͼƬ������
    public GenericDraweeHierarchyBuilder getImageProperty() {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(MyApplication.mApplication.getResources());
        return builder;
    }

    //����ͼƬ������
    public void setImageProperty(SimpleDraweeView mSimpleDraweeView, GenericDraweeHierarchyBuilder builder) {
        GenericDraweeHierarchy hierarchy = builder.build();
        mSimpleDraweeView.setHierarchy(hierarchy);
    }

    //�ͷ�
    public void release() {
        Fresco.shutDown();
    }


    public void setImageResource1(SimpleDraweeView mSimpleDraweeView, int resId) {
        mSimpleDraweeView.setAspectRatio(1.0f);
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(resId).build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setOldController(mSimpleDraweeView.getController())
                .build();

        mSimpleDraweeView.setController(controller);
    }
}

