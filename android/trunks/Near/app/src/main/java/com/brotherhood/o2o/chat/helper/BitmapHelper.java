package com.brotherhood.o2o.chat.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.skynet.library.message.Logger;

import java.io.ByteArrayOutputStream;

public class BitmapHelper {

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
