package com.brotherhood.o2o.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.config.Constants;
import com.brotherhood.o2o.manager.DirManager;
import com.skynet.library.message.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with Android Studio.
 */
public class BitmapUtil {

    /**
     * 图片长、宽超过200的，按比例把长度缩放至200，宽度按比例缩小。
     * @param context
     * @param mHeadPhotoPath
     * @return
     */
    public static String getResizedBitmapFromFile(Context context, String mHeadPhotoPath) {
        File headFile = new File(mHeadPhotoPath);
        if (!headFile.exists()) {
            return null;
        }

        Bitmap oldBitmap = BitmapFactory.decodeFile(mHeadPhotoPath);
        int newWidth = 0;
        int newHeight = 0;
        int oldWidth = oldBitmap.getWidth();
        int oldHeight = oldBitmap.getHeight();
        if (oldWidth > oldHeight) {
            if (oldWidth > 250) {
                newWidth = 250;
                newHeight = (int) (oldHeight * 250.0 / oldWidth * 1.0);
            }else if (oldWidth < 150){
                newWidth = 150;
                newHeight = (int) (oldHeight * 250.0 / oldWidth * 1.0);
            }else {
                newWidth = oldWidth;
                newHeight = oldHeight;
            }
        } else {
            if (oldHeight > 250) {
                newHeight = 250;
                newWidth = (int) (oldWidth * 250.0 / oldHeight * 1.0);
            } else if (oldHeight < 150){
                newHeight = 150;
                newWidth = (int) (oldWidth * 150.0 / oldHeight * 1.0);
            }else {
                newWidth = oldWidth;
                newHeight = oldHeight;
            }
        }
        Bitmap newBitmap = null;
        if (newWidth == oldWidth && newHeight == oldHeight) {
            newBitmap = oldBitmap;
        } else {
            newBitmap = BitmapUtil.resizeBitmap(context, oldBitmap, newWidth, newHeight);
        }
        String imageName = System.currentTimeMillis()+"_headphoto.jpg";
        BitmapUtil.saveBitmap(DirManager.getExternalStroageDir(Constants.PHOTO_CROP_DIR), imageName, newBitmap);
        if (oldBitmap != null && !oldBitmap.isRecycled()){
            oldBitmap.recycle();
        }
        if (newBitmap != null && !newBitmap.isRecycled()){
            newBitmap.recycle();
        }
        File file = new File(DirManager.getExternalStroageDir(Constants.PHOTO_CROP_DIR), imageName);
        if (!file.exists() || !file.isFile()){
            return "";
        }
        if (headFile.exists()){
            headFile.delete();
        }
        return file.getAbsolutePath();
    }

    /**
     * 压缩上传图片
     * @return
     */
  /*  public static String compressPhoto(Context context, String mHeadPhotoPath) {
        File headFile = new File(mHeadPhotoPath);
        if (!headFile.exists()) {
            return "";
        }
        Bitmap oldBitmap = BitmapFactory.decodeFile(mHeadPhotoPath);

        // 可以捕获内存缓冲区的数据，转换成字节数组。
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (oldBitmap != null) {
            //
            oldBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            int options = 100;
            while (bos.toByteArray().length / 1024 > 200) {
                bos.reset();// 置为空
                // 压缩options%
                oldBitmap.compress(Bitmap.CompressFormat.JPEG, options, bos);

                //判断如果一个图片的10%依然大于300k, 那么输出10%质量的图片后， 继续做压缩
                if(options == 10 && bos.toByteArray().length / 1024 > 300) {
                   options -= 1;
                } else {
                    // 每次都减少10
                    options -= 10;
                }
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(
                    bos.toByteArray());
            // 将数据转换成图片
            Bitmap newBitmap = BitmapFactory.decodeStream(bis);

            BitmapUtil.saveBitmap(DirManager.getExternalStroageDir(Constants.IMAGE_DIR), "headphoto.jpg",newBitmap);
            File file = new File(DirManager.getExternalStroageDir(Constants.IMAGE_DIR), "headphoto.jpg");
            if (!file.exists() || !file.isFile()){
                return "";
            }

            return  file.getAbsolutePath();
        }

        return null;
    }*/


    public static BitmapFactory.Options getBitmapOptionsFromFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }


    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth() - 2,
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap color2Bitmap(int color, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.eraseColor(color);
        return bmp;
    }

    public static Bitmap color2Bitmap(String rgb, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int color = Color.parseColor(rgb);
        bmp.eraseColor(color);
        return bmp;
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    public boolean bitmap2File(Bitmap map, File file) {
        OutputStream stream;
        try {
            stream = new FileOutputStream(file);

            return map.compress(Bitmap.CompressFormat.PNG, 100, stream);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static byte[] Drawable2Bytes(BitmapDrawable drawable) {

        Bitmap bitmap = drawable2Bitmap(drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();

    }

    public static byte[] Drawable2Bytes(Drawable drawable) {
        Bitmap bitmap = drawable2Bitmap(drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] Drawable2Bytes(NinePatchDrawable drawable) {
        Bitmap bitmap = drawable2Bitmap(drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获取圆角位图的方�?
     *
     * @param bitmap �?��转化成圆角的位图
     * @param pixels 圆角的度数，数�?越大，圆角越�?
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    /**
     * bitmap裁剪圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);
        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }


    /**
     * 多张图片拼成一张
     *
     * @param context
     * @param icons       拼图的图片数组
     * @param strokeColor 边框颜色
     * @param strokeWidth 边框宽度
     * @return 组装后的图片
     */
    public static Bitmap createShortCutIcon(Context context, BitmapDrawable[] icons, String strokeColor,
                                            int strokeWidth) {
        int margin = 5;
        // 背景框的宽高
        BitmapDrawable drawable = icons[0];
        if (drawable == null) {
            return null;
        }
        int borderWidth = drawable.getBitmap().getWidth(), borderHeight = drawable.getBitmap().getHeight();
        // 将icons里的�?��元素的内容都绘制到这个Bitmap上�?
        Bitmap bitmap = Bitmap.createBitmap(borderWidth, borderHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor(strokeColor));
        //        RectF rect = new RectF(margin,margin,borderWidth-margin,borderHeight-margin);
        RectF rect = new RectF(0, 0, borderWidth, borderHeight);
        canvas.drawRoundRect(rect, margin * 4, margin * 4, paint);//参数二：x方向上的圆角半径，参数三：y方向上的圆角半径
        // 背景框内部，每个小图标的宽高
        int itemWidth, itemHeight;
        if (icons.length == 1) {
            itemWidth = borderWidth - margin * 4;
            itemHeight = borderHeight - margin * 4;
            drawable = icons[0];
            drawable = resizeBitmap(context, drawable, itemWidth, itemHeight);
            // 设置Drawable的尺寸，否则将不会draw任何内容到bitmap中�?
            drawable.setBounds(margin * 2, margin * 2, borderWidth - margin * 2, borderHeight - margin * 2);
            drawable.draw(canvas);
            canvas.drawBitmap(drawable.getBitmap(), margin * 2 - strokeWidth, margin * 2 - strokeWidth, paint);
        } else {
            itemWidth = (borderWidth - margin * 4) / 2;
            itemHeight = (borderHeight - margin * 4) / 2;
            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;

            for (int i = 0; i < icons.length; i++) {
                drawable = icons[i];
                if (icons.length == 2) {
                    left = margin * 2 + (i % 2) * (itemWidth + margin);
                    top = (borderHeight - itemHeight) / 2;
                    right = left + itemWidth;
                    bottom = top + itemHeight;
                } else {
                    left = margin * 2 + (i % 2) * (itemWidth + margin);
                    if (i > 1) {
                        top = margin * 2 + itemHeight + margin;
                    } else {
                        top = margin * 2;
                    }
                    right = left + itemWidth;
                    bottom = top + itemHeight;
                }
                drawable = resizeBitmap(context, drawable, itemWidth, itemHeight);
                drawable.setBounds(left, top, right, bottom);
                canvas.drawBitmap(drawable.getBitmap(), left - strokeWidth, top - strokeWidth, paint);
            }
        }
        return bitmap;
    }


    /**
     * 群列表头像拼合
     *
     * @param context
     * @param avatars
     * @return
     */
    public static Bitmap createGroupAvatar(Context context, BitmapDrawable[] avatars) {
        int margin = 0;
        // 背景框的宽高
        BitmapDrawable drawable = avatars[0];
        if (drawable == null) {
            return null;
        }
        int borderWidth = 120, borderHeight = 120;
        // 将icons里的�?��元素的内容都绘制到这个Bitmap上�?
        Bitmap bitmap = Bitmap.createBitmap(borderWidth, borderHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(context.getResources().getColor(R.color.my_activities_bg));
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.STROKE);

        // 背景框内部，每个小图标的宽高
        int itemWidth, itemHeight;

        itemWidth = 60;
        itemHeight = 60;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        for (int i = 2; i >= 0; i--) {
            drawable = avatars[i];
            if (i == 0) {
                top = 0;
                left = borderWidth / 4;
            } else if (i == 1) {
                top = borderHeight / 2 - 15;
                left = 10;
            } else if (i == 2) {
                top = borderHeight / 2 - 15;
                left = borderWidth / 2 - 10;
            }
            drawable = resizeBitmap(context, drawable, itemWidth, itemHeight);
            drawable.setBounds(left, top, right, bottom);
            canvas.drawBitmap(drawable.getBitmap(), left, top, paint);
        }

        return bitmap;
    }


    public static BitmapDrawable resizeBitmap(Context context, BitmapDrawable drawable, int newWidth, int newHeight) {
        Bitmap oldBitmap = drawable.getBitmap();
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(context.getResources(), resizedBitmap);
    }

    public static Bitmap resizeBitmap(Context context, Bitmap oldBitmap, int newWidth, int newHeight) {
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static void saveBitmap(String dirPath, String fileName, Bitmap mBitmap) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File bitmap = new File(dir, fileName);
        FileOutputStream fOut = null;
        try {
            if (bitmap.exists() && bitmap.isFile()){
                bitmap.delete();
            }
            bitmap.createNewFile();
            fOut = new FileOutputStream(bitmap);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int MAX_HEIGHT = 960;
    private static final int MAX_WIDTH = 720;

    public static byte[] compressBytes(byte[] bytes) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);

        if (Logger.DEBUG) {
            Logger.e("", "w,h=" + opt.outWidth + "," + opt.outHeight);
        }

        opt.inSampleSize = calculateInSampleSize(opt, MAX_WIDTH, MAX_HEIGHT);

        if (Logger.DEBUG) {
            Logger.e("", "simplesize=" + opt.inSampleSize);
        }

        if (Logger.DEBUG) {
            Logger.e("", "new w,h=" + opt.outWidth / opt.inSampleSize + ","
                    + opt.outHeight / opt.inSampleSize);
        }
        opt.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                opt);
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        bitmap.recycle();
        return baos.toByteArray();
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath,
                                                     int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((height / inSampleSize) > reqHeight
                    || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
