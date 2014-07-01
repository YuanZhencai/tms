package com.wcs.sys.ejbtimer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.common.util.ExceptionUtil;
import com.wcs.sys.ejbtimer.consts.BusiLogLevel;
import com.wcs.sys.ejbtimer.interfaces.EJBTimerLogContext;
import com.wcs.sys.ejbtimer.model.SysBusinessLog;
import com.wcs.sys.ejbtimer.model.SysJobLog;

/**
 * <p>Project: wcsoa</p>
 * <p>Description:EJBTimer定时任务日志记录上下文实现 </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class EJBTimerLogContextImp implements EJBTimerLogContext {

	private final static Log DEFAULT_LOGGER = LogFactory.getLog(EJBTimerLogContextImp.class);

	private Log logger = DEFAULT_LOGGER;

	private SysJobLog logModel;

	private List<SysBusinessLog> busiLogs;

	protected EJBTimerLogContextImp(SysJobLog logModel) {
		this.logModel = logModel;
		busiLogs = new ArrayList<SysBusinessLog>();
	}

	@Override
	public Log getLogger() {
		return logger;
	}

	@Override
	public void setLogger(Log logger) {
		this.logger = logger;

	}

	@Override
	public void logMessage(String msg) {
		logger.debug(msg);
		addMessage(msg);
	}

	@Override
	public void logError(String errMsg) {
		logger.error(errMsg);
		logModel.setIsSuccess(Boolean.FALSE);
		addMessage(errMsg);

	}

	@Override
	public void logError(Throwable e) {
		logger.error(logModel.getJobSubject() + "发生错误", e);
		logModel.setIsSuccess(Boolean.FALSE);
		addMessage(ExceptionUtil.getExceptionString(e));

	}

	@Override
	public void logBusiInfo(BusiLogLevel logLevel, String logInfo, String logDesc) {
		SysBusinessLog busiLog = new SysBusinessLog();
		busiLog.setBusiLogLevel(logLevel.value);
		busiLog.setCreatedDatetime(new Date());
		busiLog.setBusiLogInfo(logInfo);
		busiLog.setBusiLogDesc(logDesc);
		busiLog.setSysJobLog(logModel);
		busiLogs.add(busiLog);
		logModel.setSysBusinessLogs(busiLogs);
	}

	@Override
	public void logError(String errMsg, Throwable e) {
		logger.error(errMsg, e);
		logModel.setIsSuccess(Boolean.FALSE);
		addMessage(errMsg);
		addMessage(ExceptionUtil.getExceptionString(e));

	}

	private void addMessage(String msg) {
		String newMsg = logModel.getLogDetail();
		if (newMsg == null) {
			newMsg = "";
		}
		newMsg += msg + "\r\n";
		logModel.setLogDetail(newMsg);
	}

}
