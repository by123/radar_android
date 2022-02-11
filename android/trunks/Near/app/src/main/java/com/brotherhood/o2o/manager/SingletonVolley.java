package com.brotherhood.o2o.manager;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.brotherhood.o2o.application.NearApplication;


/**
 * Volley请求队列
 */
public class SingletonVolley {
	private static SingletonVolley mInstance;
	private Context mContext;
	private RequestQueue mRequestQueue;

	private SingletonVolley() {
		mContext = NearApplication.mInstance;
		mRequestQueue = getRequestQueue();
	}

	public static SingletonVolley getInstance() {
		if (mInstance == null) {
			synchronized (SingletonVolley.class) {
				if (mInstance == null)
					mInstance = new SingletonVolley();
			}
		}
		return mInstance;
	}

	public void destroy(){
		mRequestQueue.stop();
		mInstance = null;
	}

	/**
	 * 获取RequestQueue
	 *
	 * @return
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext);
		}
		return mRequestQueue;
	}

	/**
	 * 将Request加入RequestQueue
	 *
	 * @param req
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		if (req == null)
			return;
//		if (!TextUtils.isEmpty(mUA))
//			req.setUserAgent(mUA);
		req.setRetryPolicy(new DefaultRetryPolicy(30 * 1000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//		req.setShouldCache(true);
//		HttpsTrustManager.allowAllSSL();
		getRequestQueue().add(req);
	}


	/**
	 * 根据key删除缓存
	 *
	 * @param key
	 */
	public void removeCache(String key) {
		getRequestQueue().getCache().remove(key);
	}
}
