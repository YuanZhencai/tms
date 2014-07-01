package com.wcs.sys.ejbtimer.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wcs.base.model.IdEntity;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
@Entity
@Table(name = "SYS_JOB_INFO")
public class SysJobInfo extends IdEntity {

	private static final long serialVersionUID = 1L;
	// 定时任务标题
	@Column(name = "JOB_SUBJECT", length = 100)
	private String jobSubject;

	// 定时任务描述
	@Column(name = "JOB_DESC", length = 500)
	private String jobDesc;

	// 定时任务要调用Service类或EJB JNDI
	@Column(name = "JOB_SERVICE", length = 200)
	private String jobService;

	// 定时任务要调用Service类中的方法
	@Column(name = "JOB_METHOD", length = 50)
	private String jobMethod;

	// 触发时间
	@Column(name = "CRON_EXPRESSION", length = 200)
	private String cronExpression;

	// 是否调用EJB服务　
	@Column(name = "IS_EJB")
	private Boolean isEJB;

	// 是否启用
	@Column(name = "IS_ENABLED")
	private Boolean isEnabled;

	// 预期运行时间
	@Column(name = "NEXT_RUNTIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date nextRunTime;

	public String getJobSubject() {
		return jobSubject;
	}

	public void setJobSubject(String jobSubject) {
		this.jobSubject = jobSubject;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public String getJobService() {
		return jobService;
	}

	public void setJobService(String jobService) {
		this.jobService = jobService;
	}

	public String getJobMethod() {
		return jobMethod;
	}

	public void setJobMethod(String jobMethod) {
		this.jobMethod = jobMethod;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Boolean getIsEnabled() {
		if (isEnabled == null) {
			isEnabled = Boolean.FALSE;
		}
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Boolean getIsEJB() {
		if (isEJB == null){
			isEJB = Boolean.FALSE;
		}
		return isEJB;
	}

	public void setIsEJB(Boolean isEJB) {
		this.isEJB = isEJB;
	}

	public Date getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	@Override
	public String toString() {
		return "SysJobInfo [jobid=" + getId().toString() + ";jobSubject=" + jobSubject + ";jobDesc=" + jobDesc
				+ ";jobSubject=" + jobService + ";jobMethod=" + jobMethod + ";isEJB=" + isEJB + ";cronExpression="
				+ cronExpression + "]";
	}

}
