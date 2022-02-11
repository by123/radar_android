package com.brotherhood.o2o.lib.annotation;

import android.view.View;
import android.widget.AdapterView;

import com.brotherhood.o2o.manager.LogManager;

import java.lang.reflect.Method;

public class OnEventListener implements View.OnClickListener, AdapterView.OnItemClickListener {
	private Object obj;
	private Method clickMethod;
	private Method onItemClickMethod;

	public OnEventListener(Object obj) {
		this.obj = obj;
	}

	@Override
	public void onClick(View v) {
		try {
			clickMethod.invoke(obj, v);
		} catch (Exception e) {

			LogManager.d("", e);

		}
	}

	public void click(String methodName) {
		try {
			clickMethod = obj.getClass().getDeclaredMethod(methodName, View.class);
			clickMethod.setAccessible(true);
		} catch (Exception e) {
			LogManager.d("", e);
		}
	}

	public void click(String methodName, String className) {

		for (Class<?> clazz = obj.getClass(); clazz != null && !className.equals(clazz.getSimpleName()); clazz = clazz
				.getSuperclass()) {
			try {
				clickMethod = clazz.getDeclaredMethod(methodName, View.class);
				clickMethod.setAccessible(true);
			} catch (Exception e) {
				LogManager.d("", e);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> view, View v, int pos, long value) {
		try {
			onItemClickMethod.invoke(obj, view, v, pos, value);
		} catch (Exception e) {
			LogManager.d("", e);
		}
	}

	public void onItemClick(String methodName) {
		try {
			onItemClickMethod = obj.getClass().getDeclaredMethod(methodName, AdapterView.class, View.class, int.class,
					long.class);
			onItemClickMethod.setAccessible(true);
		} catch (Exception e) {
			LogManager.d("", e);
		}
	}

}
