package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the RPT_EXECUTIONPLANMSTR database table.
 * 
 */
@Entity
@Table(name="RPT_EXECUTIONPLANMSTR")
public class RptExecutionplanmstr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="AUTO_PUBLISH_IND", nullable=false, length=1)
	private String autoPublishInd;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(name="DEFUNCT_IND", nullable=false, length=1)
	private String defunctInd;

	@Column(name="EXECUTION_DAY")
	private short executionDay;

	@Column(name="EXECUTION_TIMEPOINT", nullable=false)
	private Timestamp executionTimepoint;

	@Column(name="PAUSE_IND", nullable=false, length=1)
	private String pauseInd;

	@Column(name="PLAN_DURATION", length=8)
	private String planDuration;

	@Column(name="PLAN_END_DATE")
	private Timestamp planEndDate;

	@Column(name="PLAN_NAME", nullable=false, length=200)
	private String planName;

	@Column(name="PLAN_START_DATE")
	private Timestamp planStartDate;

	@Column(name="PLAN_TYPE", nullable=false, length=8)
	private String planType;

	@Column(length=600)
	private String remarks;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptExecutionplanhistory
	@OneToMany(mappedBy="rptExecutionplanmstr", fetch=FetchType.EAGER)
	private List<RptExecutionplanhistory> rptExecutionplanhistories;

	//bi-directional many-to-one association to RptReportmstr
    @ManyToOne
	@JoinColumn(name="RPT_REPORTMSTR_ID", nullable=false)
	private RptReportmstr rptReportmstr;

	//bi-directional many-to-one association to RptExecutionplanparameter
	@OneToMany(mappedBy="rptExecutionplanmstr", fetch=FetchType.EAGER)
	private List<RptExecutionplanparameter> rptExecutionplanparameters;

    public RptExecutionplanmstr() {
    }

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAutoPublishInd() {
		return this.autoPublishInd;
	}

	public void setAutoPublishInd(String autoPublishInd) {
		this.autoPublishInd = autoPublishInd;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public short getExecutionDay() {
		return this.executionDay;
	}

	public void setExecutionDay(short executionDay) {
		this.executionDay = executionDay;
	}

	public Timestamp getExecutionTimepoint() {
		return this.executionTimepoint;
	}

	public void setExecutionTimepoint(Timestamp executionTimepoint) {
		this.executionTimepoint = executionTimepoint;
	}

	public String getPauseInd() {
		return this.pauseInd;
	}

	public void setPauseInd(String pauseInd) {
		this.pauseInd = pauseInd;
	}

	public String getPlanDuration() {
		return this.planDuration;
	}

	public void setPlanDuration(String planDuration) {
		this.planDuration = planDuration;
	}

	public Timestamp getPlanEndDate() {
		return this.planEndDate;
	}

	public void setPlanEndDate(Timestamp planEndDate) {
		this.planEndDate = planEndDate;
	}

	public String getPlanName() {
		return this.planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public Timestamp getPlanStartDate() {
		return this.planStartDate;
	}

	public void setPlanStartDate(Timestamp planStartDate) {
		this.planStartDate = planStartDate;
	}

	public String getPlanType() {
		return this.planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public List<RptExecutionplanhistory> getRptExecutionplanhistories() {
		return this.rptExecutionplanhistories;
	}

	public void setRptExecutionplanhistories(List<RptExecutionplanhistory> rptExecutionplanhistories) {
		this.rptExecutionplanhistories = rptExecutionplanhistories;
	}
	
	public RptReportmstr getRptReportmstr() {
		return this.rptReportmstr;
	}

	public void setRptReportmstr(RptReportmstr rptReportmstr) {
		this.rptReportmstr = rptReportmstr;
	}
	
	public List<RptExecutionplanparameter> getRptExecutionplanparameters() {
		return this.rptExecutionplanparameters;
	}

	public void setRptExecutionplanparameters(List<RptExecutionplanparameter> rptExecutionplanparameters) {
		this.rptExecutionplanparameters = rptExecutionplanparameters;
	}
	
}