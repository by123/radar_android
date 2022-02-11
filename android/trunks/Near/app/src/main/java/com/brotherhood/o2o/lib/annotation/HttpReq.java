package com.brotherhood.o2o.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HttpReq
{
    public enum HttpType{Get,Post,Upload};
    
    public HttpType httpType() default HttpType.Get ;
    
    public String httpParams() default "";
    
    public String httpDefaultValue() default "";
    
    /** 是否必填 */
    public boolean needAddEmptyValue() default true;
    
}
