package com.brotherhood.o2o.lib.annotation.parser;


import com.brotherhood.o2o.lib.annotation.BundleInject;

import java.lang.reflect.Field;


public class AnnotationBundleParser implements AnnotationParser {

	@Override
	public void parse(Object obj) {
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(BundleInject.class)) {
					BundleInject inject = field.getAnnotation(BundleInject.class);
					@SuppressWarnings("unused")
					String name = inject.name();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

    @Override
    public void parse(Object obj,String simpleName){
        // TODO Auto-generated method stub
        
    }

}
