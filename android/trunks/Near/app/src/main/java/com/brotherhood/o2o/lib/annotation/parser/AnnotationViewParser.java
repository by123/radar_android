package com.brotherhood.o2o.lib.annotation.parser;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.brotherhood.o2o.lib.annotation.OnEventListener;
import com.brotherhood.o2o.lib.annotation.ViewInject;
import com.brotherhood.o2o.lib.annotation.ViewInjectable;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;


public class AnnotationViewParser implements AnnotationParser {

	@Override
	public void parse(Object obj) {
		try {
			ViewInjectable viewObj = null;
			if (obj instanceof ViewInjectable) {
				viewObj = (ViewInjectable) obj;
			}
			if (viewObj == null) {
				return;
			}
			Field[] fields = viewObj.getClass().getDeclaredFields();
			for (Field field : fields) {
				try {
					parseField(field, viewObj);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(Object obj, String simpleName) {
		ViewInjectable viewObj = null;
		if (obj instanceof ViewInjectable) {
			viewObj = (ViewInjectable) obj;
		}
		if (viewObj == null) {
			return;
		}

		for (Class<?> type = obj.getClass(); type != null && !simpleName.equals(type.getSimpleName()); type = type
				.getSuperclass()) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				try {
					parseField(field, viewObj, simpleName);
				} catch (Throwable e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}

	private static void parseField(Field field, ViewInjectable viewObj) throws Throwable {
		field.setAccessible(true);
		if (field.get(viewObj) == null) {
			if (field.isAnnotationPresent(ViewInject.class)) {
				ViewInject inject = field.getAnnotation(ViewInject.class);
				String clickMethod = inject.clickMethod();
				String onItemClickMethod = inject.onItemClickMethod();
				String listeners = inject.listeners();
				int id = inject.id();
				String resId = inject.resId();
				int textId = inject.textId();
				int imgId = inject.imgId();
				int visible = inject.visible();
				View view = null;
				if (id != -1) {
					view = viewObj.findView(id);
				}
				if (!TextUtils.isEmpty(resId)) {
					view = viewObj.findView(resId);
				}
				if (view == null) {
					return;
				}
				setText(textId, view);
				setImageResource(imgId, view);
				setEventListener(clickMethod, onItemClickMethod, viewObj, view, "");
				setListeners(listeners, view, viewObj);
				setVisible(visible, view);
				field.set(viewObj, view);
			}
		}
	}

	private static void parseField(Field field, ViewInjectable viewObj, String simpleName) throws Throwable {
		field.setAccessible(true);
		if (field.get(viewObj) == null) {
			if (field.isAnnotationPresent(ViewInject.class)) {
				ViewInject inject = field.getAnnotation(ViewInject.class);
				String clickMethod = inject.clickMethod();
				String onItemClickMethod = inject.onItemClickMethod();
				String listeners = inject.listeners();
				int id = inject.id();
				String resId = inject.resId();
				int textId = inject.textId();
				int imgId = inject.imgId();
				int visible = inject.visible();
				View view = null;
				if (id != -1) {
					view = viewObj.findView(id);
				}
				if (!TextUtils.isEmpty(resId)) {
					view = viewObj.findView(resId);
				}
				if (view == null) {
					return;
				}
				setText(textId, view);
				setImageResource(imgId, view);
				setEventListener(clickMethod, onItemClickMethod, viewObj, view, simpleName);
				setListeners(listeners, view, viewObj);
				setVisible(visible, view);
				field.set(viewObj, view);
			}
		}
	}

	private static void setVisible(int visible, View view) {
		if (visible != -1) {
			try {
				Method method = view.getClass().getMethod("setVisibility", int.class);
				method.invoke(view, visible);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void setText(int textId, View view) {
		if (textId != -1) {
			try {
				Method method = view.getClass().getMethod("setText", int.class);
				method.invoke(view, textId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void setImageResource(int imgId, View view) {
		if (imgId != -1) {
			try {
				Method method = view.getClass().getMethod("setImageResource", int.class);
				method.invoke(view, imgId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private static void setEventListener(String clickMethodName, String onItemClickMethod, Object obj, View view,
			String simpleName) {
		try {
			OnEventListener listener = null;
			if (!TextUtils.isEmpty(clickMethodName)) {
				if (listener == null) {
					listener = new OnEventListener(obj);
				}
				if (TextUtils.isEmpty(simpleName)) {

					listener.click(clickMethodName);
				} else {
					listener.click(clickMethodName, simpleName);
				}
				view.setOnClickListener(listener);
			}
			if (!TextUtils.isEmpty(onItemClickMethod)) {
				if (listener == null) {
					listener = new OnEventListener(obj);
				}
				listener.onItemClick(onItemClickMethod);
				((AdapterView) view).setOnItemClickListener(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setListeners(String listeners, View view, ViewInjectable obj) {
		try {
			if (!TextUtils.isEmpty(listeners)) {
				JSONObject jobj = new JSONObject(listeners);
				Iterator<String> iterator = jobj.keys();
				while (iterator.hasNext()) {
					try {
						String key = iterator.next();
						String value = jobj.optString(key);
						Method method = findMethod("setOn" + key, view);
						if (method == null) {
							continue;
						}
						if (value.equals("this")) {
							method.invoke(view, obj);
						} else {
							//							Field field = obj.getClass()
							//									.getDeclaredField(value);
							//							Object listener = field.get(obj);
						}

					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static Method findMethod(String methodName, Object obj) {
		try {
			Method[] methods = obj.getClass().getMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
