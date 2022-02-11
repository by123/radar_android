package com.brotherhood.o2o.widget.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherhood.o2o.MyApplication;
import com.brotherhood.o2o.R;
import com.brotherhood.o2o.utils.ByLogout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by.huang
 */
@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class WebViewActivity extends Activity {

	private PullableWebView mWebView;
	private ProgressBar mProgressBar;
	private View mBackBtn;
	private View mCloseBtn;
	private View mShareBtn;
	private TextView mTitleTxt;
	private TextView mProviderTxt;
	public static String EXTRA_URL = "_url";

	private Bitmap mBitmap;
	private String mContent;
	private boolean isSignValid = false;// 是否验证签名
	private FrameLayout full_screen_content;
	private View mCustomView = null;
	private CustomViewCallback mCustomViewCallback;
	private String currentHost="";
	private ConfigBean configInfo;//鉴权的配置信息
	
	private String supportApi = "getProfile";//支持的API列表
	
	private boolean isBack=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		String url = getIntent().getStringExtra(EXTRA_URL);
		initView();
		initData(url);
	}

	public static void show(String url) {
		if (!url.contains("http://") && !url.contains("https://")) {
			url = "http://" + url;
		}
		Context context = MyApplication.mApplication
				.getApplicationContext();
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(EXTRA_URL, url);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCustomView != null) {
				// mCustomView.setVisibility(View.GONE);
				full_screen_content.removeView(mCustomView);
				mCustomView = null;
				full_screen_content.setVisibility(View.GONE);
				try {
					mCustomViewCallback.onCustomViewHidden();
				} catch (Exception e) {
				}
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				return true;
			} else if (mWebView.canGoBack()) {
				mWebView.goBack();
				isBack=true;
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		PullToRefreshLayout pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
		pullToRefreshLayout.setOnRefreshListener(new MyListener());
		mWebView = (PullableWebView) findViewById(R.id.webview);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mProviderTxt = (TextView) findViewById(R.id.txt_provider);
		full_screen_content = (FrameLayout) findViewById(R.id.full_screen_content);

		mBackBtn = findViewById(R.id.btn_back);
		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack()) {
					mWebView.goBack();
					isBack=true;
				} else {
					finish();
				}
			}
		});

		mCloseBtn = findViewById(R.id.btn_close);
		mCloseBtn.setVisibility(View.GONE);
		mCloseBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mShareBtn = findViewById(R.id.btn_share);
		mShareBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// /分享内容
			}
		});

	}

	/**
	 * 配置webview
	 */
	@SuppressWarnings("deprecation")
	private void WebViewSetting() {
		WebSettings webSettings = mWebView.getSettings();
		// 基本设置
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setSupportZoom(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setUseWideViewPort(true);
		String userAgent = webSettings.getUserAgentString();
//		userAgent += getExtHeader();
		webSettings.setUserAgentString(userAgent);
		
		// 适配屏幕
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// webSettings.setLoadWithOverviewMode(true);
		// 设置h5缓存
		if (Build.VERSION.SDK_INT < 18) {
			webSettings.setAppCacheMaxSize(1024 * 1024 * 32);
		}
		String appCacheDir = getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setAppCacheEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setGeolocationEnabled(true);
		webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
		webSettings.setLoadWithOverviewMode(true); 
		
		int screenDensity = getResources().getDisplayMetrics().densityDpi ;   
		ZoomDensity zoomDensity = ZoomDensity.FAR ;
		switch (screenDensity){   
		case DisplayMetrics.DENSITY_LOW :  
		    zoomDensity = ZoomDensity.CLOSE;
		    break;  
		case DisplayMetrics.DENSITY_MEDIUM:  
		    zoomDensity = ZoomDensity.MEDIUM;
		    break;  
		case DisplayMetrics.DENSITY_HIGH:  
		    zoomDensity = ZoomDensity.FAR;
		    break ;  
		}  
		webSettings.setDefaultZoom(zoomDensity);  
	}

	@SuppressLint("NewApi")
	private void initData(String url) {
		//url="file:///android_asset/pro.html";
		WebViewSetting();

		mWebView.addJavascriptInterface(new Handler(), "jssdk");
		//mWebView.setSaveEnabled(false);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Uri uri = Uri.parse(url);
				if (!TextUtils.isEmpty(url)) {
					ByLogout.out("访问网址->" + url + "\n类型->"
							+ HttpURLConnection.guessContentTypeFromName(url));
					String type = HttpURLConnection
							.guessContentTypeFromName(url);
					if (!TextUtils.isEmpty(type)
							&& type.contains("application")) {
						openSystemBrowser(url);
						return true;
					} else if (url.contains(JsBean.ACTION_CALL)) {
						openDial(uri.getLastPathSegment().replace(";", ""));
						return true;
					} else if (url.contains(JsBean.ACTION_CALL2)) {
						openDial(url.substring(JsBean.ACTION_CALL2.length(),
								url.length()));
						return true;
					} else if (url.contains(JsBean.GO_TO_USERINFO)) {
//						openUserInfo(url);
						return true;
					} else if (url.contains(JsBean.GO_TO_MESSAGE)) {
//						openNearMessage(url);
						return true;
					} else if (!url.contains("http://")&& !url.contains("https://")) {
						openSystemBrowser(url);
						return true;
					}
				}
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Uri uri = Uri.parse(url);
				mProviderTxt.setText(getString(R.string.web_power_by,
						uri.getHost()));
				currentHost = uri.getHost();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// 读取网页内容
				// view.loadUrl("javascript:window.handler.show(document.body.innerHTML);");
				//mWebView.loadUrl("javascript:showMsg()");
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				ByLogout.out("网页错误信息->" + description);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			private int mOriginalOrientation = 1;

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
				onShowCustomView(view, mOriginalOrientation, callback);

			}

			@Override
			public void onHideCustomView() {
				super.onHideCustomView();
				if (mCustomView == null) {
					return;
				}
				// mCustomView.setVisibility(View.GONE);
				full_screen_content.removeView(mCustomView);
				mCustomView = null;
				full_screen_content.setVisibility(View.GONE);
				try {
					mCustomViewCallback.onCustomViewHidden();
				} catch (Exception e) {
				}
				// Show the content view.
				mCustomViewCallback = null;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			}

			public void onShowCustomView(View view, int requestedOrientation,
					CustomViewCallback callback) {
				if (mCustomView != null) {
					callback.onCustomViewHidden();
					return;
				}
				if (Build.VERSION.SDK_INT >= 14) {
					full_screen_content.addView(view);
					mCustomView = view;
					mCustomViewCallback = callback;
					full_screen_content.setVisibility(View.VISIBLE);
					full_screen_content.bringToFront();
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}

			}

			@Override
			public void onGeolocationPermissionsShowPrompt(final String origin,
					final Callback callback) {
				if(!WebViewActivity.this.isFinishing()){
//					Dialog.showSelectDialog(WebViewActivity.this,
//							getString(R.string.web_lbr_permition), true,
//							new DialogClickListener() {
//
//								@Override
//								public void confirm() {
//									callback.invoke(origin, true, true);
//								}
//
//								@Override
//								public void cancel() {
//									callback.invoke(origin, false, false);
//								}
//							});
				}

				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				if(isFinishing()){
					return true;
				}
//				Dialog.showRadioDialog(WebViewActivity.this, message, true,
//						new DialogClickListener() {
//
//							@Override
//							public void confirm() {
//								result.confirm();
//							}
//
//							@Override
//							public void cancel() {
//							}
//						});
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {

				if(isFinishing()){
					return true;
				}
//				Dialog.showSelectDialog(WebViewActivity.this, message, true,
//						new DialogClickListener() {
//
//							@Override
//							public void confirm() {
//								result.confirm();
//							}
//
//							@Override
//							public void cancel() {
//								result.cancel();
//							}
//						});
				return true;
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {
				if(isFinishing()){
					return true;
				}
//				Dialog.showRadioDialog(WebViewActivity.this, message, true,
//						new DialogClickListener() {
//
//							@Override
//							public void confirm() {
//								result.confirm();
//							}
//
//							@Override
//							public void cancel() {
//							}
//						});
				return true;
			}

			@Override
			public void onReceivedTouchIconUrl(WebView view, String url,
					boolean precomposed) {
				ByLogout.out("点击图标->" + url);
				super.onReceivedTouchIconUrl(view, url, precomposed);
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				mBitmap = icon;
				mShareBtn.setBackgroundDrawable(new BitmapDrawable(mBitmap));
				super.onReceivedIcon(view, icon);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				mTitleTxt.setText(title);
				if (isBack) {
					mCloseBtn.setVisibility(View.VISIBLE);
				} else {
					mCloseBtn.setVisibility(View.GONE);
				}
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 0 || newProgress == 100) {
					mProgressBar.setVisibility(View.GONE);
				} else {
					mProgressBar.setVisibility(View.VISIBLE);
					mProgressBar.setProgress(newProgress);

				}
				super.onProgressChanged(view, newProgress);
			}
		});

		loadUrl(url);
	}

	private void checkSign(String appid, String timestamp, String nonce,
			String sign, String url, String api_list) {
//		ApiUtils.getInstance().jssdkCheckSign(new PluginResultHandler() {
//
//			@Override
//			public void onHandlePluginResult(PluginResult result) {
//				if (result.getStatus() == PluginResult.Status.OK) {
//					isSignValid = true;
//					CallBack(0,"ok");
//				} else {
//					isSignValid = false;
//					CallBack(2,"error");
//				}
//
//			}
//		}, appid, timestamp, nonce, sign, url, api_list);

	}

	
//	private boolean checkSign(){
//		if(isSignValid){
//		//	currentHost 
//		}
//	}
	
	
	class ConfigBean{
		public String appid;
		public String nonce;
		public String timestamp;
		public String uri;
		public String ticket;
		public String sign;
		public String apiListStr;
		public List<String> api_list = new ArrayList<String>();
		
		
	}
	
	
	/**
	 * 鉴权完成回调网页 
	 * @param errorcode 错误码
	 * @param msg 错误消息
	 */
	private void CallBack(int errorcode ,String msg){

		mWebView.loadUrl("javascript:getresult('"+ getJson(errorcode,msg) + "')");
	}
	
	
	private String getJson(int errorcode ,String msg){
		JSONObject out = new JSONObject();
		try {
			out.put("c", errorcode);
			out.put("msg", msg);
		} catch (JSONException e) {
			e.printStackTrace();
			return "{\"c\":2,\"msg\":\"error\"}";
		}
		return out.toString();
	}
	
	
	/**
	 * 检查是否支持API
	 * @param apiname
	 * @return
	 */
	private boolean containApi(String apiname){
		if(configInfo != null){
			for(int i =0 ;i < configInfo.api_list.size();i++ ){
				if(configInfo.api_list.get(i).equalsIgnoreCase(apiname)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	private boolean  CheckSign(){
		if(isSignValid && configInfo != null && configInfo.uri != null && configInfo.uri.toLowerCase().contains(currentHost.toLowerCase())){
			return true;
		}else{
			return false;
		}
	}
	
	
	class Handler {
		@JavascriptInterface
		public void show(String data) {
			mContent = data;
			Toast.makeText(WebViewActivity.this, data, Toast.LENGTH_SHORT)
					.show();
		}

		@JavascriptInterface
		public void config(String jsonstr) {

			try {
				JSONObject jsonObj = new JSONObject(jsonstr);
				configInfo = new ConfigBean();
				configInfo.appid = jsonObj.getString("appid");
				configInfo.nonce = jsonObj.getString("nonce");
				configInfo.timestamp = jsonObj.getString("timestamp");
				configInfo.uri = jsonObj.getString("uri");
				//configInfo.ticket = jsonObj.getString("ticket");
				configInfo.sign = jsonObj.getString("sign");
				configInfo.apiListStr = jsonObj.getString("api_list");
				JSONArray api = jsonObj.getJSONArray("api_list");
				for(int i = 0;i < api.length();i++){
					configInfo.api_list.add(api.getString(i));
					if(!supportApi.contains(api.getString(i))){
						CallBack(110006,"包含不支持的API, "+api.getString(i));
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				CallBack(2,"error");
				return;
			}
			checkSign(configInfo.appid, configInfo.timestamp, configInfo.nonce, configInfo.sign, configInfo.uri, configInfo.apiListStr);

		}

		@JavascriptInterface
		public String getProfile() {

			try {
				if (CheckSign() && containApi("getProfile")) {
					JSONObject json = new JSONObject();
					json.put("c", 0);
					json.put("msg", "ok");
//					JSONObject userInfo = new JSONObject();
//					userInfo.put("mid", CacheRef.getInstance().getmId());
//					userInfo.put("nickname", CacheRef.getInstance()
//							.getUserNick());
//					userInfo.put("gender", CacheRef.getInstance()
//							.getmUserGender());
//					userInfo.put("avatar", CacheRef.getInstance()
//							.returnHeadUrlExt());
//					userInfo.put("avatar_thumb", CacheRef.getInstance()
//							.getUserHeadUrl());
//					json.putOpt("data", userInfo);
					return json.toString();
				} else {
					return getJson(110005,"鉴权成功后 才能调用此接口");
							
				}
			} catch (JSONException e) {
				return getJson(2,"error");
			}
		}

	}
	
	

	private void loadUrl(String url) {
		// 如果为下载链接，则直接跳转到系统浏览器下载
		if (!TextUtils.isEmpty(url)) {
			ByLogout.out("访问网址->" + url + "\n类型->"
					+ HttpURLConnection.guessContentTypeFromName(url));
			String type = HttpURLConnection.guessContentTypeFromName(url);
			// if(!TextUtils.isEmpty(type) && type.contains("text/html"))
			// {
			// mWebView.loadUrl("file:///android_asset/test.html");
			// }
			if (!TextUtils.isEmpty(type) && type.contains("application")) {
				openSystemBrowser(url);
			} else if (url.contains(JsBean.ACTION_CALL)) {
				Uri uri = Uri.parse(url);
				openDial(uri.getLastPathSegment().replace(";", ""));
			} else if (url.contains(JsBean.ACTION_CALL2)) {
				openDial(url.substring(JsBean.ACTION_CALL2.length(),
						url.length()));
			} else if (url.contains(JsBean.GO_TO_USERINFO)) {
//				openUserInfo(url);
			} else if (url.contains(JsBean.GO_TO_MESSAGE)) {
//				openNearMessage(url);
			}else if(url.contains("file://")){
				mWebView.loadUrl(url);
			}else if (!url.contains("http://") && !url.contains("https://")) {
				openSystemBrowser(url);
			} else {
				mWebView.loadUrl(url);
			}

		}
	}
	
//	private  String  getExtHeader(){
//		 StringBuilder sb = new StringBuilder();
//		 sb.append(" NearBrowser/")
//	        .append(ContextUtil.getVersionName(this)).append(" NetType/")
//	        .append(ContextUtil.getNetworkType(this));//ContextUtil.getNetworkType(cxt)
//		 return  sb.toString();
//	}
//
//	private void openUserInfo(String url) {
//		try {
//			final Uri uri = Uri.parse(url);
//			int userid = Integer.parseInt(uri.getLastPathSegment());
//			Intent intent = new Intent(this, ActSpaceDetail.class);
//			intent.putExtra(Constants.USER_NEAR_BY, userid);
//			startActivity(intent);
//		} catch (Exception e) {
//		}
//	}

//	/**
//	 * 跳转到聊天
//	 *
//	 * @param url
//	 */
//	private void openNearMessage(String url) {
//		try {
//			final Uri uri = Uri.parse(url);
//			int userid = Integer.parseInt(uri.getLastPathSegment());
//			ApiUtils.getInstance().getAccountInfo(1, userid,
//					new PluginResultHandler() {
//
//						@Override
//						public void onHandlePluginResult(PluginResult result) {
//							if (result.getStatus() == Status.OK) {
//								String strMsg = result.getMessage();
//								DealUserInfo(uri.getLastPathSegment(), strMsg);
//							}
//						}
//					});
//
//		} catch (Exception e) {
//		}
//	}

	/**
	 * 打开系统浏览器
	 * 
	 * @param url
	 */
	private void openSystemBrowser(String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 跳转到拨号
	 */
	private void openDial(String phoneNumber) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ phoneNumber));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

//	/**
//	 * 获取用户信息
//	 *
//	 * @param strMsg
//	 */
//	protected void DealUserInfo(String userid, String strMsg) {
//		if (strMsg == null || strMsg.length() <= 0) {
//			LogUtils.d("MainActivity", "获取用户信息失败");
//			return;
//		}
//		try {
//			JSONObject obj = new JSONObject(strMsg);
//			String strNickName = obj.getString("nickname");
//			String strHeadUrl = obj.getString("avatar_thumb");
//			String strGender = obj.getString("gender");
//
//			Intent it = new Intent(WebViewActivity.this, ChatActivity.class);
//			it.putExtra(Constants.USER_NEAR_BY, userid);
//			it.putExtra(Constants.USER_NICK_NAMW, strNickName);
//			it.putExtra(Constants.USER_HEAD_URL, strHeadUrl);
//			it.putExtra(Constants.USER_SEX, strGender);
//			startActivity(it);
//
//		} catch (Exception e) {
//		}
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WebStorage.getInstance().deleteAllData();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}

}
