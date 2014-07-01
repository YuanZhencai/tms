package com.wcs.sys.ejbtimer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Project: wcsoa</p>
 * <p>Description:定时任务的类上加这个注解 </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Retention (RetentionPolicy.RUNTIME)   
@Target (ElementType.TYPE) 
public @interface WCSTimerClass {

}


