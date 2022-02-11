/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.brotherhood.o2o.chat.common.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * A subclass of {@link ImageWorker} that fetches images from a URL.
 */
public class ImageFetcher extends ImageWorker {

	public static final int IO_BUFFER_SIZE_BYTES = 1024;

	private static final int DEFAULT_MAX_IMAGE_HEIGHT = 1024;

	private static final int DEFAULT_MAX_IMAGE_WIDTH = 1024;

	private static final String DEFAULT_HTTP_CACHE_DIR = "http";

	private static ImageFetcher sInstance;

	/**
	 * Used to create a singleton of the image fetcher
	 * 
	 * @param context
	 *            The {@link Context} to use
	 * @return A new instance of this class.
	 */
	public static ImageFetcher getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ImageFetcher(context.getApplicationContext());
		}
		return sInstance;
	}

	/**
	 * Creates a new instance of {@link ImageFetcher}.
	 * 
	 * @param context
	 *            The {@link Context} to use.
	 */
	private ImageFetcher(Context context) {
		super(context);
	}

	public abstract static class ImageLoadListener implements TaskListener {

		@Override
		public final void onTaskComplete(boolean suc) {
			onLoadComplete(suc);
		}

		public abstract void onLoadComplete(boolean suc);

	}

	public void loadImage(String key, ImageView iv, ImageLoadListener l) {
		do {
			if (Thread.currentThread() != mContext.getMainLooper().getThread()) {
				throw new IllegalThreadStateException(
						"should be invoked in UI thread");
			}
			if (key == null || iv == null) {
				Log.w(TAG, "loadImage(), key or image view may be null, skip");
				break;
			}
			if (mImageCache == null) {
				Log.w(TAG, "loadImage(), setImageCache() not called");
				break;
			}

			if (mImageCache.getDiskCacheFile() == null) {
				if (ImageCache.DEBUG) {
					Log.w(TAG, "disk cache not ready, wait it done...");
				}
				while (true) {
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
					}
					if (mImageCache.getDiskCacheFile() != null) {
						if (ImageCache.DEBUG) {
							Log.i(TAG, "done, load image begins...");
						}
						break;
					}
				}
			}

			if (ImageCache.DEBUG) {
				Log.i(TAG, "loadImage(), key=" + key);
			}

			Bitmap cache = mImageCache.getCachedBitmap(key);
			if (cache != null) {
				// Bitmap found in memory cache
				if (ImageCache.DEBUG) {
					Log.i(TAG, "found in cache");
				}
				iv.setImageBitmap(cache);
				break;
			}

			boolean paused = mImageCache.isDiskCachePaused();
			if (paused) {
				if (ImageCache.DEBUG) {
					Log.i(TAG, "disk cache paused, break");
				}
				break;
			}

			// Otherwise run the worker task
			BitmapWorkerTask taskWithKey = sTasks.get(key);
			if (taskWithKey != null) {
				// Well there is a task associated with this key, so append
				// the image view to the task
				if (ImageCache.DEBUG) {
					Log.i(TAG,
							"task already there, just append this image view");
				}
				taskWithKey.appendImageView(iv);
				break;
			}

			// There is no task associated with the key, go forward to
			// check if the current image view is already associated
			// with a task.
			BitmapWorkerTask taskWithView = null;
			Drawable d = iv.getDrawable();
			if (d instanceof AsyncDrawable) {
				AsyncDrawable ad = (AsyncDrawable) d;
				taskWithView = ad.getBitmapWorkerTask();
			}
			boolean shouldCreateTask = false;
			if (taskWithView == null) {
				shouldCreateTask = true;
			} else {
				String oldKey = taskWithView.mKey;
				if (!key.equals(oldKey)) {
					// task.cancel(true);
					// Do not cancel, just remove this image view from
					// the task
					taskWithView.removeImageView(iv);
					shouldCreateTask = true;
				}
			}

			if (!shouldCreateTask) {
				if (ImageCache.DEBUG) {
					Log.w(TAG, "should not create a new task");
				}
				break;
			}

			BitmapWorkerTask newTask = new BitmapWorkerTask(key, l);
			newTask.appendImageView(iv);
			AsyncDrawable ad = new AsyncDrawable(newTask);
			iv.setImageDrawable(ad);
			sTasks.put(key, newTask);
			CacheUtils.execute(newTask);
		} while (false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Bitmap processBitmap(String key) {
		if (key == null) {
			return null;
		}
		File file = downloadBitmapToFile(mContext, key, DEFAULT_HTTP_CACHE_DIR);
		if (file != null) {
			// Return a sampled down version
			Bitmap bitmap = decodeSampledBitmapFromFile(file.toString());
			file.delete();
			if (bitmap != null) {
				return bitmap;
			}
		}
		return null;
	}

	/**
	 * Download a {@link Bitmap} from a URL, write it to a disk and return the
	 * File pointer. This implementation uses a simple disk cache.
	 * 
	 * @param context
	 *            The context to use
	 * @param urlString
	 *            The URL to fetch
	 * @return A {@link File} pointing to the fetched bitmap
	 */
	private File downloadBitmapToFile(Context context, String urlString,
			String uniqueName) {
		if (mImageCache == null) {
			return null;
		}
		File cacheDir = mImageCache.getDiskCacheFile();
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}

		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;

		try {
			File tempFile = File.createTempFile("bitmap", null, cacheDir); //$NON-NLS-1$
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			final InputStream in = new BufferedInputStream(
					urlConnection.getInputStream(), IO_BUFFER_SIZE_BYTES);
			out = new BufferedOutputStream(new FileOutputStream(tempFile),
					IO_BUFFER_SIZE_BYTES);

			int oneByte;
			while ((oneByte = in.read()) != -1) {
				out.write(oneByte);
			}
			return tempFile;
		} catch (final IOException ignored) {
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (out != null) {
				try {
					out.close();
				} catch (final IOException ignored) {
				}
			}
		}
		return null;
	}

	/**
	 * Decode and sample down a {@link Bitmap} from a file to the requested
	 * width and height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A {@link Bitmap} sampled down from the original with the same
	 *         aspect ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options,
				DEFAULT_MAX_IMAGE_WIDTH, DEFAULT_MAX_IMAGE_HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	/**
	 * Calculate an inSampleSize for use in a
	 * {@link BitmapFactory.Options} object when decoding
	 * bitmaps using the decode* methods from {@link BitmapFactory}. This
	 * implementation calculates the closest inSampleSize that will result in
	 * the final decoded bitmap having a width and height equal to or larger
	 * than the requested width and height. This implementation does not ensure
	 * a power of 2 is returned for inSampleSize which can be faster when
	 * decoding but results in a larger bitmap which isn't as useful for caching
	 * purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(
			final BitmapFactory.Options options, final int reqWidth,
			final int reqHeight) {
		/* Raw height and width of image */
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).
			float totalPixels = width * height;

			/* More than 2x the requested pixels we'll sample down further */
			float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

}
