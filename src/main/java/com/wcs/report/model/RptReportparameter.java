package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the RPT_REPORTPARAMETER database table.
 * 
 */
@Entity
@Table(name="RPT_REPORTPARAMETER")
public class RptReportparameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME")
	private Timestamp createdDatetime;

	@Column(name="DEFAULT_VALUE", length=200)
	private String defaultValue;

	@Column(name="DEFAULT_VALUE_TYPE", length=8)
	private String defaultValueType;

	@Column(name="DEFUNCT_IND", nullable=false, length=1)
	private String defunctInd;

	@Column(name="DISPLAY_IND", nullable=false, length=1)
	private String displayInd;

	@Column(name="EDIT_IND", nullable=false, length=1)
	private String editInd;

	@Column(name="JAVA_DATA_TYPE", nullable=false, length=20)
	private String javaDataType;

	@Column(name="MANDATORY_IND", nullable=false, length=1)
	private String mandatoryInd;

	@Column(length=600)
	private String remarks;

	@Column(name="REPORT_PARAMETER_CODE", nullable=false, length=30)
	private String reportParameterCode;

	@Column(name="SEQ_NO", nullable=false)
	private short seqNo;

	@Column(name="SQL_STATEMENT", length=2000)
	private String sqlStatement;

	@Column(name="UI_LABEL", length=50)
	private String uiLabel;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME")
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptExecutionplanparameter
	@OneToMany(mappedBy="rptReportparameter", fetch=FetchType.EAGER)
	private List<RptExecutionplanparameter> rptExecutionplanparameters;

	//bi-directional many-to-one association to RptReportmstr
    @ManyToOne
	@JoinColumn(name="REPORTMSTR_ID")
	private RptReportmstr rptReportmstr;

	//bi-directional many-to-one association to RptSnapshotparameter
	@OneToMany(mappedBy="rptReportparameter", fetch=FetchType.EAGER)
	private List<RptSnapshotparameter> rptSnapshotparameters;

    public RptReportparameter() {
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

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValueType() {
		return this.defaultValueType;
	}

	public void setDefaultValueType(String defaultValueType) {
		this.defaultValueType = defaultValueType;
	}

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public String getDisplayInd() {
		return this.displayInd;
	}

	public void setDisplayInd(String displayInd) {
		this.displayInd = displayInd;
	}

	public String getEditInd() {
		return this.editInd;
	}

	public void setEditInd(String editInd) {
		this.editInd = editInd;
	}

	public String getJavaDataType() {
		return this.javaDataType;
	}

	public void setJavaDataType(String javaDataType) {
		this.javaDataType = javaDataType;
	}

	public String getMandatoryInd() {
		return this.mandatoryInd;
	}

	public void setMandatoryInd(String mandatoryInd) {
		this.mandatoryInd = mandatoryInd;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReportParameterCode() {
		return this.reportParameterCode;
	}

	public void setReportParameterCode(String reportParameterCode) {
		this.reportParameterCode = reportParameterCode;
	}

	public short getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(short seqNo) {
		this.seqNo = seqNo;
	}

	public String getSqlStatement() {
		return this.sqlStatement;
	}

	public void setSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
	}

	public String getUiLabel() {
		return this.uiLabel;
	}

	public void setUiLabel(String uiLabel) {
		this.uiLabel = uiLabel;
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

	public List<RptExecutionplanparameter> getRptExecutionplanparameters() {
		return this.rptExecutionplanparameters;
	}

	public void setRptExecutionplanparameters(List<RptExecutionplanparameter> rptExecutionplanparameters) {
		this.rptExecutionplanparameters = rptExecutionplanparameters;
	}
	
	public RptReportmstr getRptReportmstr() {
		return this.rptReportmstr;
	}

	public void setRptReportmstr(RptReportmstr rptReportmstr) {
		this.rptReportmstr = rptReportmstr;
	}
	
	public List<RptSnapshotparameter> getRptSnapshotparameters() {
		return this.rptSnapshotparameters;
	}

	public void setRptSnapshotparameters(List<RptSnapshotparameter> rptSnapshotparameters) {
		this.rptSnapshotparameters = rptSnapshotparameters;
	}
	
}