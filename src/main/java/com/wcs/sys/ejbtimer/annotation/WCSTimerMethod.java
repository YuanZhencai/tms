package com.wcs.sys.ejbtimer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: 定时任务方法上加这个注解</p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Retention (RetentionPolicy.RUNTIME)   
@Target (ElementType.METHOD)
public @interface WCSTimerMethod {

    /**
     * 
     * <p>Description:必填，定时任务标题 </p>
     * @return
     */
    String subject();  
    /**
     * 
     * <p>Description: 可选，定时任务描述</p>
     * @return
     */
    String desc() default "";
    /**
     * 
     * <p>Description:可选，cron表达式，指定定时任务默认触发时间，默认值为每天晚上0点0分0秒执行 </p>
     * 
     * @return
     */
    String cronExpression() default "0 0 0 * * ?";
    
    /**
     * 
     * <p>Description: 可选，是否启用，默认不启用</p>
     * @return
     */
    boolean isEnabled() default false;
}


