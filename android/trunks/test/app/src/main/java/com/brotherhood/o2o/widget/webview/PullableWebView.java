package com.brotherhood.o2o.widget.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class PullableWebView extends WebView implements Pullable
{

	

	private boolean isH5=false;
	public PullableWebView(Context context)
	{
		super(context);
	}

	public PullableWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	public void setH5(boolean isH5)
	{
		this.isH5=isH5;
	}

	@Override
	public boolean canPullDown()
	{
		if(isH5)
		{
			return false;
		}

		if(getContentHeight() * getScale() == getMeasuredHeight()){
			return false;
		}
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if(isH5)
		{
			return false;
		}
		

		
		if(getContentHeight() * getScale() == getMeasuredHeight()){
			return false;
		}
		if (getScrollY() >= getContentHeight() * getScale()- getMeasuredHeight())
			return true;
		else
			return false;
	}
}
