package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_EXECUTIONPLANHISTORY database table.
 * 
 */
@Entity
@Table(name="RPT_EXECUTIONPLANHISTORY")
public class RptExecutionplanhistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(name="EXECUTION_END_DATETIME")
	private Timestamp executionEndDatetime;

	@Column(name="EXECUTION_ERROR_LOG", length=3000)
	private String executionErrorLog;

	@Column(name="EXECUTION_RESULT", nullable=false, length=8)
	private String executionResult;

	@Column(name="EXECUTION_START_DATETIME", nullable=false)
	private Timestamp executionStartDatetime;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptExecutionplanmstr
    @ManyToOne
	@JoinColumn(name="RPT_EXECUTIONPLANMSTR_ID", nullable=false)
	private RptExecutionplanmstr rptExecutionplanmstr;

	//bi-directional many-to-one association to RptSnapshot
    @ManyToOne
	@JoinColumn(name="RPT_SNAPSHOT_ID")
	private RptSnapshot rptSnapshot;

    public RptExecutionplanhistory() {
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

	public Timestamp getExecutionEndDatetime() {
		return this.executionEndDatetime;
	}

	public void setExecutionEndDatetime(Timestamp executionEndDatetime) {
		this.executionEndDatetime = executionEndDatetime;
	}

	public String getExecutionErrorLog() {
		return this.executionErrorLog;
	}

	public void setExecutionErrorLog(String executionErrorLog) {
		this.executionErrorLog = executionErrorLog;
	}

	public String getExecutionResult() {
		return this.executionResult;
	}

	public void setExecutionResult(String executionResult) {
		this.executionResult = executionResult;
	}

	public Timestamp getExecutionStartDatetime() {
		return this.executionStartDatetime;
	}

	public void setExecutionStartDatetime(Timestamp executionStartDatetime) {
		this.executionStartDatetime = executionStartDatetime;
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
	
	public RptSnapshot getRptSnapshot() {
		return this.rptSnapshot;
	}

	public void setRptSnapshot(RptSnapshot rptSnapshot) {
		this.rptSnapshot = rptSnapshot;
	}
	
}