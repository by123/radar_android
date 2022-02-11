package com.brotherhood.o2o.widget.webview;

public class JsBean {

	//js
	public static final String JS_FUNCTION_UPLOAD_USERINFO="near_upload_userinfo";
	
	//uri
	public static final String ACTION_CALL="wtai://wp";
	public static final String ACTION_CALL2="tel:";
	public static final String GO_TO_MESSAGE="near://message/chat/";
	public static final String GO_TO_USERINFO="near://userinfo/";


	/**
	 * 网页是否存在js方法
	 * @param function
	 * @return
	 */
	public boolean JsAPIExit(String function)
	{
		if(JS_FUNCTION_UPLOAD_USERINFO.equalsIgnoreCase(function))
		{
			return true;
		}
		return false;
	}
	
	
	
}
