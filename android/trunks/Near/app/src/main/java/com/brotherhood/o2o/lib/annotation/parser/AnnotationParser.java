package com.brotherhood.o2o.lib.annotation.parser;


public interface AnnotationParser {
	public void parse(Object obj);
	
	public void parse(Object obj, String simpleName);
}
