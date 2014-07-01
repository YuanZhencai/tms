package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_SNAPSHOTPARAMETER database table.
 * 
 */
@Entity
@Table(name="RPT_SNAPSHOTPARAMETER")
public class RptSnapshotparameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(name="DEFUNCT_IND", length=1)
	private String defunctInd;

	@Column(name="PARAMETER_CODE", length=30)
	private String parameterCode;

	@Column(name="PARAMETER_DESC", length=50)
	private String parameterDesc;

	@Column(name="PARAMETER_VALUE", length=300)
	private String parameterValue;

	@Column(name="PARAMETER_VALUE_DESC", length=300)
	private String parameterValueDesc;

	@Column(name="SEQ_NO", nullable=false)
	private short seqNo;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME")
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptReportparameter
    @ManyToOne
	@JoinColumn(name="REPORTPARAMETER_ID")
	private RptReportparameter rptReportparameter;

	//bi-directional many-to-one association to RptSnapshot
    @ManyToOne
	@JoinColumn(name="RPT_SNAPSHOT_ID", nullable=false)
	private RptSnapshot rptSnapshot;

    public RptSnapshotparameter() {
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

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public String getParameterCode() {
		return this.parameterCode;
	}

	public void setParameterCode(String parameterCode) {
		this.parameterCode = parameterCode;
	}

	public String getParameterDesc() {
		return this.parameterDesc;
	}

	public void setParameterDesc(String parameterDesc) {
		this.parameterDesc = parameterDesc;
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

	public short getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(short seqNo) {
		this.seqNo = seqNo;
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

	public RptReportparameter getRptReportparameter() {
		return this.rptReportparameter;
	}

	public void setRptReportparameter(RptReportparameter rptReportparameter) {
		this.rptReportparameter = rptReportparameter;
	}
	
	public RptSnapshot getRptSnapshot() {
		return this.rptSnapshot;
	}

	public void setRptSnapshot(RptSnapshot rptSnapshot) {
		this.rptSnapshot = rptSnapshot;
	}
	
}