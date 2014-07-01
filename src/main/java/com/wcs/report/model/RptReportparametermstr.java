package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_REPORTPARAMETERMSTR database table.
 * 
 */
@Entity
@Table(name="RPT_REPORTPARAMETERMSTR")
public class RptReportparametermstr implements Serializable {
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

	@Column(name="EDIT_IND", nullable=false, length=1)
	private String editInd;

	@Column(name="JAVA_DATA_TYPE", length=20)
	private String javaDataType;

	@Column(name="PICKSHELL_FILTER_VALUE", length=200)
	private String pickshellFilterValue;

	@Column(name="PICKSHELL_SEARCHVIEW_TYPE", length=50)
	private String pickshellSearchviewType;

	@Column(name="PICKSHELL_VIEW_SHOW_FIELD", length=30)
	private String pickshellViewShowField;

	@Column(name="PICKSHELL_VIEW_TARGET_FIELD", length=30)
	private String pickshellViewTargetField;

	@Column(length=600)
	private String remarks;

	@Column(name="REPORT_PARAMETER_MSTR_CODE", length=30)
	private String reportParameterMstrCode;

	@Column(name="SQL_STATEMENT", length=500)
	private String sqlStatement;

	@Column(name="UI_LABEL", length=50)
	private String uiLabel;

	@Column(name="UI_LABEL_LANG1", length=50)
	private String uiLabelLang1;

	@Column(name="UI_TYPE", length=8)
	private String uiType;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME")
	private Timestamp updatedDatetime;

    public RptReportparametermstr() {
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

	public String getPickshellFilterValue() {
		return this.pickshellFilterValue;
	}

	public void setPickshellFilterValue(String pickshellFilterValue) {
		this.pickshellFilterValue = pickshellFilterValue;
	}

	public String getPickshellSearchviewType() {
		return this.pickshellSearchviewType;
	}

	public void setPickshellSearchviewType(String pickshellSearchviewType) {
		this.pickshellSearchviewType = pickshellSearchviewType;
	}

	public String getPickshellViewShowField() {
		return this.pickshellViewShowField;
	}

	public void setPickshellViewShowField(String pickshellViewShowField) {
		this.pickshellViewShowField = pickshellViewShowField;
	}

	public String getPickshellViewTargetField() {
		return this.pickshellViewTargetField;
	}

	public void setPickshellViewTargetField(String pickshellViewTargetField) {
		this.pickshellViewTargetField = pickshellViewTargetField;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReportParameterMstrCode() {
		return this.reportParameterMstrCode;
	}

	public void setReportParameterMstrCode(String reportParameterMstrCode) {
		this.reportParameterMstrCode = reportParameterMstrCode;
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

	public String getUiLabelLang1() {
		return this.uiLabelLang1;
	}

	public void setUiLabelLang1(String uiLabelLang1) {
		this.uiLabelLang1 = uiLabelLang1;
	}

	public String getUiType() {
		return this.uiType;
	}

	public void setUiType(String uiType) {
		this.uiType = uiType;
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

}