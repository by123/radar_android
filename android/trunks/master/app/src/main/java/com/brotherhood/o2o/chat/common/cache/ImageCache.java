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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

/**
 * This class holds the memory and disk bitmap caches.
 */
public final class ImageCache {

	private static final String TAG = "ImageCache";

	static final boolean DEBUG = true;

	/**
	 * Default memory cache size as a percent of device memory class
	 */
	private static final float MEM_CACHE_DIVIDER = 0.25f;

	/**
	 * Default disk cache size 10MB
	 */
	private static final int DISK_CACHE_SIZE = 10 * 1024 * 1024;

	/**
	 * Compression settings when writing images to disk cache
	 */
	private static final CompressFormat COMPRESS_FORMAT = CompressFormat.JPEG;

	/**
	 * Disk cache index to read from
	 */
	private static final int DISK_CACHE_INDEX = 0;

	/**
	 * Image compression quality
	 */
	private static final int COMPRESS_QUALITY = 98;

	/**
	 * LRU cache
	 */
	private MemoryCache mLruCache;

	/**
	 * Disk LRU cache
	 */
	private DiskLruCache mDiskCache;

	private File mDiskCacheFile;

	private static ImageCache sInstance;

	/**
	 * Used to temporarily pause the disk cache while scrolling
	 */
	public boolean mPauseDiskAccess = false;
	private Object mPauseLock = new Object();

	/**
	 * Used to create a singleton of {@link ImageCache}
	 * 
	 * @param context
	 *            The {@link Context} to use
	 * @return A new instance of this class.
	 */
	public final static ImageCache getInstance(final Context context) {
		if (sInstance == null) {
			sInstance = new ImageCache(context.getApplicationContext());
		}
		return sInstance;
	}

	/**
	 * Constructor of <code>ImageCache</code>
	 * 
	 * @param context
	 *            The {@link Context} to use
	 */
	private ImageCache(final Context context) {
		init(context);
	}

	/**
	 * Initialize the cache, providing all parameters.
	 * 
	 * @param context
	 *            The {@link Context} to use
	 * @param cacheParams
	 *            The cache parameters to initialize the cache
	 */
	private void init(final Context context) {
		CacheUtils.execute(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... unused) {
				// Initialize the disk cache in a background thread
				initDiskCache(context);
				return null;
			}
		}, (Void[]) null);
		// Set up the memory cache
		initLruCache(context);
	}

	/**
	 * Initializes the disk cache. Note that this includes disk access so this
	 * should not be executed on the main/UI thread. By default an ImageCache
	 * does not initialize the disk cache when it is created, instead you should
	 * call initDiskCache() to initialize it on a background thread.
	 * 
	 * @param context
	 *            The {@link Context} to use
	 */
	private synchronized void initDiskCache(final Context cxt) {
		// Set up disk cache
		if (mDiskCache == null || mDiskCache.isClosed()) {
			File extDir = cxt.getExternalCacheDir();
			String cachePath = extDir != null ? extDir.getPath() : cxt
					.getCacheDir().getPath();
			File diskCacheDir = new File(cachePath, TAG);
			if (diskCacheDir != null) {
				if (!diskCacheDir.exists()) {
					diskCacheDir.mkdirs();
				}
				if (CacheUtils.getExternalAvailableSpaceInBytes() > DISK_CACHE_SIZE) {
					try {
						mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1,
								DISK_CACHE_SIZE);
						mDiskCacheFile = diskCacheDir;
					} catch (final IOException e) {
						diskCacheDir = null;
					}
				}
			}
		}
	}

	public File getDiskCacheFile() {
		return mDiskCacheFile;
	}

	/**
	 * Sets up the Lru cache
	 * 
	 * @param context
	 *            The {@link Context} to use
	 */
	@SuppressLint("NewApi")
	private void initLruCache(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		int lruCacheSize = Math.round(MEM_CACHE_DIVIDER * am.getMemoryClass()
				* 1024 * 1024);
		mLruCache = new MemoryCache(lruCacheSize);

		// Release some memory as needed
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			context.registerComponentCallbacks(new ComponentCallbacks2() {

				@Override
				public void onTrimMemory(final int level) {
					if (level >= TRIM_MEMORY_MODERATE) {
						evictAll();
					} else if (level >= TRIM_MEMORY_BACKGROUND) {
						mLruCache.trimToSize(mLruCache.size() / 2);
					}
				}

				@Override
				public void onLowMemory() {
					// Nothing to do
				}

				@Override
				public void onConfigurationChanged(final Configuration newConfig) {
					// Nothing to do
				}
			});
		}
	}

	/**
	 * Adds a new image to the memory and disk caches
	 * 
	 * @param data
	 *            The key used to store the image
	 * @param bitmap
	 *            The {@link Bitmap} to cache
	 */
	public void addBitmapToCache(String data, final Bitmap bitmap) {
		if (data == null || bitmap == null) {
			return;
		}

		// Add to memory cache
		addBitmapToMemCache(data, bitmap);

		// Add to disk cache
		if (mDiskCache != null) {
			String key = hashKeyForDisk(data);
			OutputStream out = null;
			try {
				final DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
				if (snapshot == null) {
					final DiskLruCache.Editor editor = mDiskCache.edit(key);
					if (editor != null) {
						out = editor.newOutputStream(DISK_CACHE_INDEX);
						bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
						editor.commit();
						out.close();
						flush();
					}
				} else {
					snapshot.getInputStream(DISK_CACHE_INDEX).close();
				}
			} catch (final IOException e) {
				Log.e(TAG, "addBitmapToCache - " + e);
			} finally {
				try {
					if (out != null) {
						out.close();
						out = null;
					}
				} catch (final IOException e) {
					Log.e(TAG, "addBitmapToCache - " + e);
				} catch (final IllegalStateException e) {
					Log.e(TAG, "addBitmapToCache - " + e);
				}
			}
		}
	}

	/**
	 * Called to add a new image to the memory cache
	 * 
	 * @param data
	 *            The key identifier
	 * @param bitmap
	 *            The {@link Bitmap} to cache
	 */
	private void addBitmapToMemCache(final String data, final Bitmap bitmap) {
		if (data == null || bitmap == null) {
			return;
		}
		// Add to memory cache
		if (getBitmapFromMemCache(data) == null) {
			mLruCache.put(data, bitmap);
		}
	}

	/**
	 * Fetches a cached image from the memory cache
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The {@link Bitmap} if found in cache, null otherwise
	 */
	protected final Bitmap getBitmapFromMemCache(String data) {
		if (data == null) {
			return null;
		}
		Bitmap lruBitmap = mLruCache.get(data);
		if (lruBitmap != null) {
			return lruBitmap;
		}
		return null;
	}

	/**
	 * Fetches a cached image from the disk cache
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The {@link Bitmap} if found in cache, null otherwise
	 */
	private Bitmap getBitmapFromDiskCache(String data) {
		if (data == null) {
			return null;
		}

		// Check in the memory cache here to avoid going to the disk cache less
		// often
		Bitmap b = getBitmapFromMemCache(data);
		if (b != null) {
			return b;
		}

		waitUntilUnpaused();
		final String key = hashKeyForDisk(data);
		if (mDiskCache != null) {
			InputStream inputStream = null;
			try {
				final DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
				if (snapshot != null) {
					inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
					if (inputStream != null) {
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						if (bitmap != null) {
							return bitmap;
						}
					}
				}
			} catch (final IOException e) {
				Log.e(TAG, "getBitmapFromDiskCache - " + e);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (final IOException e) {
					}
				}
			}
		}
		return null;
	}

	/**
	 * Tries to return a cached image from memory cache before fetching from the
	 * disk cache
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The {@link Bitmap} if found in cache, null otherwise
	 */
	public Bitmap getCachedBitmap(String data) {
		if (data == null) {
			return null;
		}
		Bitmap cachedImage = getBitmapFromMemCache(data);
		if (cachedImage == null) {
			cachedImage = getBitmapFromDiskCache(data);
		}
		if (cachedImage != null) {
			addBitmapToMemCache(data, cachedImage);
			return cachedImage;
		}
		return null;
	}

	/**
	 * flush() is called to synchronize up other methods that are accessing the
	 * cache first
	 */
	public void flush() {
		CacheUtils.execute(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... unused) {
				if (mDiskCache != null) {
					try {
						if (!mDiskCache.isClosed()) {
							mDiskCache.flush();
						}
					} catch (final IOException e) {
						Log.e(TAG, "flush - " + e);
					}
				}
				return null;
			}
		}, (Void[]) null);
	}

	/**
	 * Clears the disk and memory caches
	 */
	public void clearCaches() {
		CacheUtils.execute(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... unused) {
				// Clear the disk cache
				try {
					if (mDiskCache != null) {
						mDiskCache.delete();
						mDiskCache = null;
					}
				} catch (final IOException e) {
					Log.e(TAG, "clearCaches - " + e);
				}
				// Clear the memory cache
				evictAll();
				return null;
			}
		}, (Void[]) null);
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
		CacheUtils.execute(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(final Void... unused) {
				if (mDiskCache != null) {
					try {
						if (!mDiskCache.isClosed()) {
							mDiskCache.close();
							mDiskCache = null;
						}
					} catch (final IOException e) {
						Log.e(TAG, "finish - " + e);
					}
				}
				return null;
			}
		}, (Void[]) null);
	}

	/**
	 * Evicts all of the items from the memory cache and lets the system know
	 * now would be a good time to garbage collect
	 */
	public void evictAll() {
		mLruCache.evictAll();
		System.gc();
	}

	/**
	 * @param key
	 *            The key used to identify which cache entries to delete.
	 */
	public void removeFromCache(String key) {
		if (key == null) {
			return;
		}
		// Remove the Lru entry
		mLruCache.remove(key);

		try {
			// Remove the disk entry
			if (mDiskCache != null) {
				mDiskCache.remove(hashKeyForDisk(key));
			}
		} catch (final IOException e) {
			Log.e(TAG, "remove - " + e);
		}
		flush();
	}

	/**
	 * Used to temporarily pause the disk cache while the user is scrolling to
	 * improve scrolling.
	 * 
	 * @param pause
	 *            True to temporarily pause the disk cache, false otherwise.
	 */
	public void setPauseDiskCache(final boolean pause) {
		synchronized (mPauseLock) {
			if (mPauseDiskAccess != pause) {
				mPauseDiskAccess = pause;
				if (!pause) {
					mPauseLock.notify();
				}
			}
		}
	}

	private void waitUntilUnpaused() {
		synchronized (mPauseLock) {
			if (Looper.myLooper() != Looper.getMainLooper()) {
				while (mPauseDiskAccess) {
					try {
						mPauseLock.wait();
					} catch (InterruptedException e) {
						// ignored, we'll start waiting again
					}
				}
			}
		}
	}

	/**
	 * @return True if the user is scrolling, false otherwise.
	 */
	public boolean isDiskCachePaused() {
		return mPauseDiskAccess;
	}

	/**
	 * A hashing method that changes a string (like a URL) into a hash suitable
	 * for using as a disk filename.
	 * 
	 * @param key
	 *            The key used to store the file
	 */
	public static final String hashKeyForDisk(final String key) {
		String cacheKey;
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(key.getBytes());
			cacheKey = bytesToHexString(digest.digest());
		} catch (final NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	/**
	 * http://stackoverflow.com/questions/332079
	 * 
	 * @param bytes
	 *            The bytes to convert.
	 * @return A {@link String} converted from the bytes of a hashable key used
	 *         to store a filename on the disk, to hex digits.
	 */
	private static final String bytesToHexString(final byte[] bytes) {
		final StringBuilder builder = new StringBuilder();
		for (final byte b : bytes) {
			final String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				builder.append('0');
			}
			builder.append(hex);
		}
		return builder.toString();
	}

	/**
	 * Used to cache images via {@link LruCache}.
	 */
	public static final class MemoryCache extends LruCache<String, Bitmap> {

		/**
		 * Constructor of <code>MemoryCache</code>
		 * 
		 * @param maxSize
		 *            The allowed size of the {@link LruCache}
		 */
		public MemoryCache(final int maxSize) {
			super(maxSize);
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				Bitmap oldValue, Bitmap newValue) {
			if (oldValue != null) {
				oldValue.recycle();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressLint("NewApi")
		@Override
		protected int sizeOf(final String paramString, final Bitmap bmp) {
			if (DEBUG) {
				Log.i(TAG, "original size=" + bmp.getWidth() * bmp.getHeight()
						* getBytesPerPixel(bmp.getConfig()));
			}

			int size = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				size = bmp.getAllocationByteCount();
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
				size = bmp.getByteCount();
			} else {
				int w = bmp.getWidth();
				int h = bmp.getHeight();
				if (w > 0 && h > 0) {
					size = w * h * getBytesPerPixel(bmp.getConfig());
				}
			}

			if (DEBUG) {
				Log.i(TAG, "adjust size=" + size);
			}
			return size;
		}

		static int getBytesPerPixel(Config config) {
			if (config == Config.ARGB_8888) {
				return 4;
			} else if (config == Config.RGB_565) {
				return 2;
			} else if (config == Config.ARGB_4444) {
				return 2;
			} else if (config == Config.ALPHA_8) {
				return 1;
			}
			return 1;
		}

	}

}
