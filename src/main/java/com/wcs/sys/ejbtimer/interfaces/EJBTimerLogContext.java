package com.wcs.sys.ejbtimer.interfaces;

import org.apache.commons.logging.Log;

import com.wcs.sys.ejbtimer.consts.BusiLogLevel;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: EJBtimer定时任务，日志上下文</p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public interface EJBTimerLogContext {

    /**
     * 
     * <p>Description: 获取Log对象</p>
     * @return
     */
    public Log getLogger();

    /**
     * 
     * <p>Description:设置当前的Log对象 </p>
     * @param logger
     */
    public void setLogger(Log logger);

    /**
     * 
     * <p>Description:记录一般日志信息 </p>
     * @param msg
     */
    public void logMessage(String msg);

    /**
     * 
     * <p>Description: 记录异常日志信息</p>
     * @param errMsg
     */
    public void logError(String errMsg);

    /**
     * 
     * <p>Description: 记录异常日志信息</p>
     * @param e
     */
    public void logError(Throwable e);

    /**
     * 
     * <p>Description: 记录异常日志信息</p>
     * @param errMsg
     * @param e
     */
    public void logError(String errMsg, Throwable e);

    /**
     * 
     * <p>Description:记录业务日志 </p>
     * @param logLevel 业务日志级别
     * @param logInfo  业务日志内容
     * @param logDesc  业务日志说明 
     */
    public void logBusiInfo(BusiLogLevel logLevel, String logInfo, String logDesc);
}
