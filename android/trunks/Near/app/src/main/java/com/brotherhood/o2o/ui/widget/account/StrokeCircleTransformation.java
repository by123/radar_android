package com.brotherhood.o2o.ui.widget.account;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * 带边框圆形图
 * Created by jl.zhang on 2015/12/21.
 */
public class StrokeCircleTransformation implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;
    private int mBorderSize;

    public StrokeCircleTransformation(BitmapPool pool, int borderSize) {
        this.mBitmapPool = pool;
        mBorderSize = borderSize;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        int size = Math.min(source.getWidth(), source.getHeight());

        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

        Bitmap bitmap = mBitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.parseColor("#ffffff"));
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(mBorderSize);

        Paint paint = new Paint();
        //平铺  BitmapShader.TileMode.CLAMP 边缘拉伸
        BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/ 2f;
        canvas.drawCircle(r, r, r, paint);
        canvas.drawCircle(r, r, r - mBorderSize/2, borderPaint);
        return BitmapResource.obtain(bitmap, mBitmapPool);
    }
    @Override
    public String getId() {
        return "StrokeCircleTransformation()";
    }
}
