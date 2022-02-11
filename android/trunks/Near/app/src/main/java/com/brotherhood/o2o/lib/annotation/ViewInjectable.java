package com.brotherhood.o2o.lib.annotation;


import android.view.View;

public interface ViewInjectable {
	public View findView(int id);
	
	public View findView(String res);
}
