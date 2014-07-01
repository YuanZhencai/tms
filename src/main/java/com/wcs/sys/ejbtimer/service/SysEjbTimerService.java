package com.wcs.sys.ejbtimer.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcs.base.util.DateUtil;
import com.wcs.base.util.StringUtils;
import com.wcs.common.util.ExceptionUtil;
import com.wcs.sys.ejbtimer.model.SysJobInfo;
import com.wcs.sys.ejbtimer.util.CronExpression;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Stateless
public class SysEjbTimerService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8860934675895633345L;

	private static final Logger logger = LoggerFactory.getLogger(SysEjbTimerService.class);

	// Constants used internally; unit is milliseconds
	private static final long ONE_MINUTE = 60 * 1000;

	@PersistenceContext(unitName = "pu")
	EntityManager em;

	@Resource
	TimerService timerService;

	@EJB
	SysJobService sysJobService;

	/**
	 * 
	 * <p>Description:安排一个定时任务，如果已经安排，则重新安排 </p>
	 * @param jobModel
	 */
	public void createTimer(SysJobInfo jobModel) {
		try {
			// 先删除原有的
			cancelTimer(jobModel.getId().toString());
			if (!jobModel.getIsEnabled().booleanValue()) {
				return;
			}
			String cronExpression = jobModel.getCronExpression();
			// 根据配置表的记录，安排任务
			ScheduleExpression scheExp = getScheduleExpByCron(cronExpression);
			logger.info("### EJBTimerService.createTimer() Scheduler expr: " + scheExp.toString());

			String[] cronExpressionArr = cronExpression.split(" ");
			String[] minuteArr = cronExpressionArr[1].split("\\/");
			Timer timer = null;

			// 判断频率是否为每隔几分钟
			if (minuteArr.length > 1) {
				String minute = minuteArr[1];
				long intervalTime = ONE_MINUTE * Long.valueOf(minute);
				timer = timerService.createIntervalTimer(intervalTime, intervalTime, new TimerConfig(jobModel, true));
			} else {
				timer = timerService.createCalendarTimer(scheExp, new TimerConfig(jobModel, true));
			}

			logger.info("timer.getNextTimeout():" + DateUtil.convertDateToString(timer.getNextTimeout(), null));
		} catch (Exception e) {
			logger.error("安排定时任务：" + jobModel.getId().toString() + "时发生错误", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * <p>Description: 取消单个定时器</p>
	 * @param jobModel
	 */
	public void cancelTimer(String jobId) {
		if (null != timerService.getTimers()) {
			for (Timer timer : timerService.getTimers()) {
				SysJobInfo timerJobInfo = (SysJobInfo) timer.getInfo();
				if (jobId.equals(timerJobInfo.getId().toString())) {
					timer.cancel();
					logger.debug("cancelTimer:" + jobId);
				}
			}
		}
	}

	/**
	 * 
	 * <p>Description:取消所有定时器 </p>
	 */

	public void cancelAllTimer() {
		logger.debug("In EJBTimerService -> cancelAllTimer method");
		if (null != timerService.getTimers()) {
			for (Timer timer : timerService.getTimers()) {
				logger.debug("cancelTimer:" + timer.getInfo().toString());
				timer.cancel();
			}
		}
		logger.debug("Exit EJBTimerService -> cancelAllTimer method");
	}

	/**
	 * 
	 * <p>Description: 根据cronExpression，返回ScheduleExpression</p>
	 * @param cronExpression
	 * 
	 * ScheduleExpression 默认值
	 * <p>{ second , minute , hour } : "0"</p>                
	 * <p>{ dayOfMonth, month, dayOfWeek, year } : "*"</p> 
	 * <p>timezone : default JVM time zone</p> 
	 * <p>startDate : no start date</p>
	 * <p>endDate : no end date</p>           
	 * @return
	 * @throws ParseException 
	 */
	public ScheduleExpression getScheduleExpByCron(String cronExpression) throws ParseException {
		// CronExpression 用的开源的Quartz中的类，语法稍微有点区别
		CronExpression cron = new CronExpression(cronExpression);
		ScheduleExpression scheExp = new ScheduleExpression();
		scheExp.second(cron.getSeconds().equals("*") ? "0" : cron.getSeconds());
		scheExp.minute(cron.getMinutes().equals("*") ? "0" : cron.getMinutes());
		scheExp.hour(cron.getHours().equals("*") ? "0" : cron.getHours());
		scheExp.dayOfMonth("?".equals(cron.getDaysOfMonth()) ? "*" : cron.getDaysOfMonth());
		scheExp.month(cron.getMonths().toString());
		scheExp.dayOfWeek("?".equals(cron.getDaysOfWeek()) ? "*" : cron.getDaysOfWeek());
		scheExp.year(cron.getYears());
		scheExp.timezone(TimeZone.getDefault().getID());
		scheExp.start(DateUtil.getNowDate().toDate());

		return scheExp;
	}

	/**
	 * 
	 * <p>Description: </p>
	 * @param jobId 定时任务表中的主键值
	 * @param isEnabled 是否启用
	 * @return
	 * @throws Exception
	 */
	public String getNextRunTimeByTimerID(String jobId, Boolean isEnabled) throws Exception {

		if (!isEnabled || !StringUtils.hasLength(jobId)) {
			return "";
		}
		String nextRunTime = "";
		if (null != timerService.getTimers()) {
			for (Timer timer : timerService.getTimers()) {
				SysJobInfo timerJobInfo = (SysJobInfo) timer.getInfo();
				if (jobId.equals(timerJobInfo.getId().toString())) {
					nextRunTime = DateUtil.convertDateToString(timer.getNextTimeout(), null);
				}
			}
		}
		logger.debug("getNextRunTimeByTimerID :" + nextRunTime);
		return nextRunTime;
	}

	// EJB Timer执行的回调方法
	@Timeout
	public void timeoutHandler(Timer timer) {
		try {
			logger.debug("into timeoutHandler=================" + timer.getInfo());
			SysJobInfo jobModel = (SysJobInfo) timer.getInfo();
			sysJobService.runJob(jobModel);
			logger.info("timeoutHandler timer.getNextTimeout():" + DateUtil.convertDateToString(timer.getNextTimeout(), null));
		} catch (Exception e) {
			logger.error(ExceptionUtil.getExceptionString(e));
		}

	}

	public static void main(String[] args) throws Exception {
		CronExpression cron = new CronExpression("0/20 15 4 * * ?");
		logger.info(cron.getExpressionSummary());

	}
}
