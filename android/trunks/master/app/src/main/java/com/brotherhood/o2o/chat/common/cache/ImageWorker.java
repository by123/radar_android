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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class wraps up completing some arbitrary long running work when loading
 * a {@link Bitmap} to an {@link ImageView}. It handles things like using a
 * memory and disk cache, running the work in a background thread and setting a
 * placeholder image.
 */
public abstract class ImageWorker {

	static final String TAG = "ImageWorker";

	/**
	 * Default transition drawable fade time
	 */
	private static final int FADE_IN_TIME = 200;

	/**
	 * The resources to use
	 */
	protected final Resources mResources;

	/**
	 * The Context to use
	 */
	protected Context mContext;

	/**
	 * Disk and memory caches
	 */
	protected ImageCache mImageCache;

	/**
	 * Constructor of <code>ImageWorker</code>
	 * 
	 * @param context
	 *            The {@link Context} to use
	 */
	protected ImageWorker(Context context) {
		mContext = context.getApplicationContext();
		mResources = mContext.getResources();
	}

	/**
	 * Set the {@link ImageCache} object to use with this ImageWorker.
	 * 
	 * @param cache
	 *            new {@link ImageCache} object.
	 */
	public void setImageCache(ImageCache cache) {
		mImageCache = cache;
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
		if (mImageCache != null) {
			mImageCache.close();
		}
	}

	/**
	 * flush() is called to synchronize up other methods that are accessing the
	 * cache first
	 */
	public void flush() {
		if (mImageCache != null) {
			mImageCache.flush();
		}
	}

	/**
	 * Clears the disk and memory caches
	 */
	public void clearCaches() {
		if (mImageCache != null) {
			mImageCache.clearCaches();
		}
	}

	/**
	 * @param pause
	 *            True to temporarily pause the disk cache, false otherwise.
	 */
	public void setPauseDiskCache(boolean pause) {
		if (mImageCache != null) {
			mImageCache.setPauseDiskCache(pause);
		}
	}

	/**
	 * @param key
	 *            The key used to find the image to remove
	 */
	public void removeFromCache(final String key) {
		if (mImageCache != null) {
			mImageCache.removeFromCache(key);
		}
	}

	/**
	 * @param key
	 *            The key used to find the image to return
	 */
	public Bitmap getCachedBitmap(final String key) {
		if (mImageCache != null) {
			return mImageCache.getCachedBitmap(key);
		}
		return null;
	}

	/**
	 * Adds a new image to the memory and disk caches
	 * 
	 * @param data
	 *            The key used to store the image
	 * @param bitmap
	 *            The {@link Bitmap} to cache
	 */
	public void addBitmapToCache(String key, final Bitmap bitmap) {
		if (mImageCache != null) {
			mImageCache.addBitmapToCache(key, bitmap);
		}
	}

	protected static HashMap<String, BitmapWorkerTask> sTasks = new HashMap<String, BitmapWorkerTask>();

	protected interface TaskListener {

		public void onTaskComplete(boolean suc);

	}

	/**
	 * The actual {@link AsyncTask} that will process the image.
	 */
	protected final class BitmapWorkerTask extends
			AsyncTask<String, Void, TransitionDrawable> {

		private LinkedList<WeakReference<ImageView>> mImages = new LinkedList<WeakReference<ImageView>>();

		/**
		 * The key used to store cached entries
		 */
		protected String mKey;

		private TaskListener mListener;

		/**
		 * Constructor of <code>BitmapWorkerTask</code>
		 */
		public BitmapWorkerTask(String key, TaskListener l) {
			if (key == null) {
				throw new NullPointerException();
			}
			mKey = key;
			mListener = l;
		}

		/**
		 * Attach this image view with this task.
		 * 
		 * @param iv
		 *            The image view.
		 */
		public void appendImageView(ImageView iv) {
			synchronized (this) {
				if (iv == null) {
					return;
				}
				mImages.add(new WeakReference<ImageView>(iv));
			}
		}

		/**
		 * Attach this image view with this task.
		 * 
		 * @param iv
		 *            The image view.
		 */
		public void removeImageView(ImageView iv) {
			synchronized (this) {
				if (iv == null) {
					return;
				}
				Iterator<WeakReference<ImageView>> iterator = mImages
						.iterator();
				while (iterator.hasNext()) {
					WeakReference<ImageView> ref = iterator.next();
					ImageView tmp = ref.get();
					if (iv == tmp) {
						iterator.remove();
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected TransitionDrawable doInBackground(String... params) {
			// First, check the disk cache for the image
			Bitmap bitmap = mImageCache.getCachedBitmap(mKey);

			// Second, by now we need to download the image
			if (bitmap == null) {
				if (ImageCache.DEBUG) {
					Log.i(TAG, "processBitmap, key=" + mKey);
				}
				bitmap = processBitmap(mKey);
			}

			// Third, add the new image to the cache
			if (bitmap != null) {
				addBitmapToCache(mKey, bitmap);
			}

			// Add the second layer to the transition drawable
			if (bitmap != null) {
				BitmapDrawable layerTwo = new BitmapDrawable(mResources, bitmap);
				layerTwo.setFilterBitmap(false);
				layerTwo.setDither(false);
				// A transparent image (layer 0) and the new result (layer 1)
				Drawable[] drawables = new Drawable[2];
				drawables[0] = new ColorDrawable(Color.TRANSPARENT);
				;
				drawables[1] = layerTwo;

				// Finally, return the image
				TransitionDrawable result = new TransitionDrawable(drawables);
				result.setCrossFadeEnabled(true);
				result.startTransition(FADE_IN_TIME);
				return result;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(TransitionDrawable result) {
			if (result != null) {
				for (WeakReference<ImageView> ref : mImages) {
					ImageView iv = ref.get();
					if (iv != null) {
						iv.setImageDrawable(result);
						if (ImageCache.DEBUG) {
							Log.w(TAG, "set image bitmap for " + iv.toString());
						}
					}
				}
			}
			sTasks.remove(mKey);
			mImages.clear();
			if (mListener != null) {
				mListener.onTaskComplete(result != null);
			}
		}

	}

	/**
	 * A custom {@link BitmapDrawable} that will be attached to the
	 * {@link ImageView} while the work is in progress. Contains a reference to
	 * the actual worker task, so that it can be stopped if a new binding is
	 * required, and makes sure that only the last started worker process can
	 * bind its result, independently of the finish order.
	 */
	protected final class AsyncDrawable extends ColorDrawable {

		private final WeakReference<BitmapWorkerTask> mTaskReference;

		/**
		 * Constructor of <code>AsyncDrawable</code>
		 */
		public AsyncDrawable(BitmapWorkerTask t) {
			super(Color.TRANSPARENT);
			mTaskReference = new WeakReference<BitmapWorkerTask>(t);
		}

		/**
		 * @return The {@link BitmapWorkerTask} associated with this drawable
		 */
		public BitmapWorkerTask getBitmapWorkerTask() {
			return mTaskReference.get();
		}
	}

	/**
	 * Subclasses should override this to define any processing or work that
	 * must happen to produce the final {@link Bitmap}. This will be executed in
	 * a background thread and be long running.
	 * 
	 * @param key
	 *            The key to identify which image to process.
	 * @param url
	 *            The url of the remote bitmap resource.
	 * 
	 * @return The processed {@link Bitmap}.
	 */
	protected abstract Bitmap processBitmap(String key);

}
