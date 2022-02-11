package com.brotherhood.o2o.request.base;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.brotherhood.o2o.cache.DataCache;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.manager.SingletonVolley;

/**
 *
 */
public abstract class BaseRequestWrapper<T> extends Request<T> {

	private final OnBaseResponseListener<T> mBaseResponseCallbackListener;

	private final Response.Listener<T> mListener;

	private DataCache mDataCache;

	/**
	 * 是否缓存泛型T的序列化对象
	 */
	private boolean mNeedDataCache = false;

	/**
	 * 缓存泛型T的序列化对象到文件的KEY
	 */
	private String mDataCacheKey;

	private SingletonVolley mSingletonVolley;


	/**
	 * @param method
	 * @param url
	 * @param requestCallbackListener
	 */
	public BaseRequestWrapper(int method, String url, boolean hasHead,OnBaseResponseListener<T> requestCallbackListener) {
		super(method, url, hasHead);
		mSingletonVolley = SingletonVolley.getInstance();
		setTag(getRequestTag());
		mBaseResponseCallbackListener = requestCallbackListener;
		mListener = getResponseListener();
		setErrorListener(getResponseErrorListener());
		mDataCache = new DataCache();
	}

	/**
	 * 返回标识请求的tag
	 *
	 * @return
	 */
	public abstract Object getRequestTag();

	/**
	 * 设置Response对象缓存文件的key
	 *
	 * @return
	 */
	public void setDataCacheKey(String key) {
		mDataCacheKey = key;
	}

	/**
	 * 返回Response对象缓存文件的key
	 *
	 * @return
	 */
	public String getDataCacheKey() {
		return mDataCacheKey;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	/**
	 * 添加请求到队�?
	 */
	public void sendRequest() {
		LogManager.d("sendRequest: %s", getUrl());
		addRequestToQueue(this);
	}

	public void stopRequest(){

	}

	/**
	 * 添加请求到队
	 *
	 * @param dataCache 是否读取缓存对象文件，并将请求到的对象覆盖缓
	 * @deprecated 统一使用HTTP缓存，不建议使用缓存对象到文
	 */
	public void sendRequest(boolean dataCache) {
		if (dataCache) {
			mNeedDataCache = true;

			Object object = mDataCache.getDataFromDiskCache(getDataCacheKey());
			if (object != null && mBaseResponseCallbackListener != null) {
				mBaseResponseCallbackListener.onResponseSuccess((T)object, true);
			}
		}
		sendRequest();
	}

	/**
	 * @return
	 */
	protected Response.Listener<T> getResponseListener() {
		return new Response.Listener<T>() {
			@Override
			public void onResponse(T response) {
				if (mBaseResponseCallbackListener != null) {
					if (mNeedDataCache && response != null) {
						mDataCache.addDataToDiskCache(getDataCacheKey(), response);
					}
					mBaseResponseCallbackListener.onResponseSuccess(response, false);
				}
			}
		};
	}

	/**
	 * @return
	 */
	protected Response.ErrorListener getResponseErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (mBaseResponseCallbackListener != null) {
					mBaseResponseCallbackListener.onResponseFailure(error);
				}
			}
		};
	}

	private void addRequestToQueue(Request<T> req) {
		if (req == null)
			return;
//		if (!TextUtils.isEmpty(mUA))
//			req.setUserAgent(mUA);
		req.setRetryPolicy(new DefaultRetryPolicy(30 * 1000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//		req.setShouldCache(true);
		mSingletonVolley.addToRequestQueue(this);
	}

}
