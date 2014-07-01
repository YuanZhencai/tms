package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wcs.base.model.IdEntity;

@Entity
@Table(name = "PE_LOG")
public class PELog extends IdEntity {
	
	@Column(name = "WOB_NUM", length = 200)
    private String wobNum;
	@Column(name = "STEP_NAME", length = 200)
    private String stepName;
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_STAMP")
    private Date timeStamp;
	@Column(name = "WORK_CLASSNAME", length = 200)
    private String workClassName;
	@Column(name = "EVENT_TYPE", length = 20)
    private String eventType;
	
	public String getWobNum() {
		return wobNum;
	}
	public void setWobNum(String wobNum) {
		this.wobNum = wobNum;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getWorkClassName() {
		return workClassName;
	}
	public void setWorkClassName(String workClassName) {
		this.workClassName = workClassName;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
}
