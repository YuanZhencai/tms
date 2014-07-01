package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_EXECUTIONPLANPARAMETER database table.
 * 
 */
@Entity
@Table(name="RPT_EXECUTIONPLANPARAMETER")
public class RptExecutionplanparameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(name="DAY_IN_DURATION")
	private short dayInDuration;

	@Column(name="DEFUNCT_IND", length=1)
	private String defunctInd;

	@Column(name="DURATION_BEFORE")
	private short durationBefore;

	@Column(name="PARAMETER_VALUE", length=300)
	private String parameterValue;

	@Column(name="PARAMETER_VALUE_DESC", length=300)
	private String parameterValueDesc;

	@Column(name="PARAMETER_VALUE_TYPE", nullable=false, length=8)
	private String parameterValueType;

	@Column(name="TIME_IN_DAY")
	private Timestamp timeInDay;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptExecutionplanmstr
    @ManyToOne
	@JoinColumn(name="RPT_EXECUTIONPALNMSTR_ID", nullable=false)
	private RptExecutionplanmstr rptExecutionplanmstr;

	//bi-directional many-to-one association to RptReportparameter
    @ManyToOne
	@JoinColumn(name="RPT_REPORTPARAMETER_ID", nullable=false)
	private RptReportparameter rptReportparameter;

    public RptExecutionplanparameter() {
    }

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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

	public short getDayInDuration() {
		return this.dayInDuration;
	}

	public void setDayInDuration(short dayInDuration) {
		this.dayInDuration = dayInDuration;
	}

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public short getDurationBefore() {
		return this.durationBefore;
	}

	public void setDurationBefore(short durationBefore) {
		this.durationBefore = durationBefore;
	}

	public String getParameterValue() {
		return this.parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public String getParameterValueDesc() {
		return this.parameterValueDesc;
	}

	public void setParameterValueDesc(String parameterValueDesc) {
		this.parameterValueDesc = parameterValueDesc;
	}

	public String getParameterValueType() {
		return this.parameterValueType;
	}

	public void setParameterValueType(String parameterValueType) {
		this.parameterValueType = parameterValueType;
	}

	public Timestamp getTimeInDay() {
		return this.timeInDay;
	}

	public void setTimeInDay(Timestamp timeInDay) {
		this.timeInDay = timeInDay;
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

	public RptExecutionplanmstr getRptExecutionplanmstr() {
		return this.rptExecutionplanmstr;
	}

	public void setRptExecutionplanmstr(RptExecutionplanmstr rptExecutionplanmstr) {
		this.rptExecutionplanmstr = rptExecutionplanmstr;
	}
	
	public RptReportparameter getRptReportparameter() {
		return this.rptReportparameter;
	}

	public void setRptReportparameter(RptReportparameter rptReportparameter) {
		this.rptReportparameter = rptReportparameter;
	}
	
}