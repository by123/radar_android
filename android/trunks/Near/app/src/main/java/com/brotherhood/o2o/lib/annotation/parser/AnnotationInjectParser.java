package com.brotherhood.o2o.lib.annotation.parser;

import com.brotherhood.o2o.lib.annotation.AnnotationManager;
import com.brotherhood.o2o.lib.annotation.Inject;

public class AnnotationInjectParser implements AnnotationParser {

	@Override
	public void parse(Object obj) {
		try {
			Class<?> clazz = obj.getClass();
			if (clazz.isAnnotationPresent(Inject.class)) {
				Inject inject = (Inject) clazz.getAnnotation(Inject.class);
				int[] types = inject.types();
				if (types != null) {
					for (int type : types) {
						AnnotationParser parser = AnnotationManager.getParser(type);
						if (parser != null) {
							parser.parse(obj);
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

    @Override
    public void parse(Object obj,String simpleName){
        
    }

}
