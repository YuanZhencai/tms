package com.wcs.tms.exception;

import java.io.IOException;
import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.util.JSFUtils;
import com.wcs.tms.conf.SystemConfiguration;
/**
 * @author Chris Guan
 */

@ExceptionHandler @Interceptor
public class AccessExceptionAdvice implements Serializable{
	  
	private Log log = LogFactory.getLog(AccessExceptionAdvice.class);
	
      @AroundInvoke
      public Object handleAccessException(InvocationContext joinPoint) throws Exception {
    	  try{
    		  return joinPoint.proceed();
    	  } catch(ServiceException ae){
    		  log.error("handleAccessException方法出现异常", ae);
    		  JSFUtils.flashScope().put("exceptionMessage", ae.getMessage());
    		  JSFUtils.redirect(JSFUtils.contextPath()+SystemConfiguration.EXCEPTION_HANDLE_PAGE);
    		  return null;
    	  }
      }
}