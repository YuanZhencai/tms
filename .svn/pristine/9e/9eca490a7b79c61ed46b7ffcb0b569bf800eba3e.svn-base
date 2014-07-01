package com.wcs.sys.ejbtimer.interfaces;


/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class EJBTimerLogContextHolder {
    private static ThreadLocal<EJBTimerLogContext> logContext = new ThreadLocal<EJBTimerLogContext>();
    
    private EJBTimerLogContextHolder(){
    	
    }

    public static EJBTimerLogContext get() {
        return logContext.get();
    }

    public static void set(EJBTimerLogContext timerLogContext) {
        logContext.set(timerLogContext);
    }

    public static void remove() {
        logContext.remove();
    }
}
