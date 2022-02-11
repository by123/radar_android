package com.brotherhood.o2o.request.base;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.brotherhood.o2o.listener.OnBaseResponseListener;
import com.brotherhood.o2o.manager.LogManager;
import com.brotherhood.o2o.utils.RequestParameterUtil;
import com.brotherhood.o2o.utils.ZipUtil;
import com.skynet.library.login.net.LoginManager;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口访问基类，因为请求服务器无论是get还是post请求，都是要带自定义的header�?
 * �?��其他�?��接口必须继承此类，其他请求接口可以继承BaseRequestWrapper、StringRequest、JsonObjectRequest�?
 */
public abstract class BaseAppRequest<T> extends BaseRequestWrapper<T> {

	/**
	 * Post请求内容的字符串
	 */
	private String mStrBody;

	/**
	 * 参数map
	 */
	protected Map<String, String> mParamMap = new HashMap<>();

	private BaseAppRequest(int method,String url,boolean hasHead, OnBaseResponseListener<T> baseResponseListener){
		super(method, url, hasHead, baseResponseListener);
	}
	/**
	 * post请求的构造方�?请求参数是bodyMap, url是全路径
	 *
	 * @param url
	 //* @param absoluteUrl
	 * @param bodyMap
	 * @param baseResponseListener
	 */
	public BaseAppRequest(String url, int method, boolean hasHead, Map<String, String> bodyMap, OnBaseResponseListener<T> baseResponseListener) {
		this(method, url, hasHead, baseResponseListener);
		if (Method.POST == method) {
			mStrBody = buildBodyString(bodyMap);
			LogManager.i("mStrBody====== %s", mStrBody);
		}
	}

	/**
	 * 接口请求默认用自身的对象作为请求的标�?
	 */
	@Override
	public Object getRequestTag() {
		return this;
	}

	/**
	 * 请求优先级，如果�?��调整请求优先级重写此方法
	 */
	@Override
	public Priority getPriority() {
		return super.getPriority();
	}

	/**
	 * 请求头部
	 */
	@Override
	public Map<String, String> getHeaders(String url, int method, boolean hasHead) throws AuthFailureError {
		Map<String, String> headers = new HashMap<>();
		if (hasHead) {
			String encodeMethod = "POST";
			if (method == Method.POST) {
				encodeMethod = "POST";
			} else if (method == Method.GET) {
				encodeMethod = "GET";
			}
			String header = LoginManager.getInstance().generateOAuthHeader(encodeMethod, url);
			headers.put("Authorization", header);
			LogManager.d("key:Authorization\t\t--->value:%s", header);
		}
		// 默认启动gzip压缩
		headers.put("Accept-Encoding", "gzip");
		return headers;
	}

	/**
	 * post的body内容
	 */
	@Override
	public byte[] getBody() throws AuthFailureError {
		byte[] content = null;
		if (mStrBody == null) {
			mStrBody = buildBodyString();
		}
		try {
			content = mStrBody.getBytes(HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return content;
	}



	/**
	 * 拼接Get请求链接
	 *
	 * @param url 请求链接的后�?��符串
	 * @param params    Get请求的参�?
	 * @return
	 */
	public static String buildParamsUrl(String url, Map<String, String> params) {
		String query = RequestParameterUtil.getParameterToString(params);
		if (!TextUtils.isEmpty(query)) {
			url = url + query;
		}
		return url;
	}

	/**
	 * 生成post传输内容
	 * @return
	 */
	@SuppressWarnings("finally")
	private String buildBodyString() {
		return buildBodyString(mParamMap);
	}

	/**
	 * 生成post传输内容
	 * @param args
	 * @return
	 */
	@SuppressWarnings("finally")
	private String buildBodyString(Map<String, String> args) {
		return RequestParameterUtil.getParameterToString(args);
	}

	/**
	 * 根据网络返回数据，进行�?用解压�?解密等操�?
	 */
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		if (response == null || response.data == null)
			return Response.error(null);
		byte[] data;// volley缓存是针对处理后的response对象，故只针对response的data进行处理
		if (response.headers.containsKey("Content-Encoding") && response.headers.get("Content-Encoding").equals("gzip")) {
			data = ZipUtil.decompressZipToByte(response.data);
			if (data == null)
				return null;
		} else {
			data = response.data;
		}
		Response<T> rsp = parseResponse(response, data);
		if (rsp != null) {
			return rsp;
		} else {
			return Response.error(new VolleyError());
		}
	}

	/**
	 * 解析数据为相应的对象，解析内容可直接使用data参数
	 *
	 * @param response
	 * @param data     解压或�?解密后的数据内容
	 * @return
	 */
	protected Response<T> parseResponse(NetworkResponse response, byte[] data){
		String json;
		try {
			json = new String(data, "UTF-8");
			//json = URLDecoder.decode(json, "UTF-8");
			LogManager.i("json====== %s",json);
			JSONObject jo = new JSONObject(json);
			if(jo != null) {
				if (!jo.isNull("error")) {
					return null;
				} else {
					Type genType = getClass().getGenericSuperclass();
					Type trueType = ((ParameterizedType) genType).getActualTypeArguments()[0];
					return Response.success((T) JSON.parseObject(json, trueType), HttpHeaderParser.parseCacheHeaders(response));

				}
			}else{
				return null;
			}
		} catch(Exception e) {
			LogManager.e(e);
			return Response.error(new ParseError((e)));
		}
	}



}
