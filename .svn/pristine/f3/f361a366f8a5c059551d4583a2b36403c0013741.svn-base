package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the RPT_REPORTMSTR database table.
 * 
 */
@Entity
@Table(name="RPT_REPORTMSTR")
public class RptReportmstr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(name="DATASOURCE_TYPE", length=8)
	private String datasourceType;

	@Column(name="DEFUNCT_IND", nullable=false, length=1)
	private String defunctInd;

	@Column(length=300)
	private String remarks;

	@Column(name="REPORT_CATEGORY", nullable=false, length=8)
	private String reportCategory;

	@Column(name="REPORT_CODE", nullable=false, length=50)
	private String reportCode;

	@Column(name="REPORT_MODE", length=8)
	private String reportMode;

	@Column(name="REPORT_NAME", nullable=false, length=300)
	private String reportName;

	@Column(name="SNAPSHOT_TITLE_PATTERN")
	private long snapshotTitlePattern;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptExecutionplanmstr
	@OneToMany(mappedBy="rptReportmstr", fetch=FetchType.EAGER)
	private List<RptExecutionplanmstr> rptExecutionplanmstrs;

	//bi-directional many-to-one association to RptReportfilehistory
	@OneToMany(mappedBy="rptReportmstr", fetch=FetchType.EAGER)
	private List<RptReportfilehistory> rptReportfilehistories;

	//bi-directional many-to-one association to Datasourcemstr
    @ManyToOne
	@JoinColumn(name="DATASOURCEMSTR_ID")
	private Datasourcemstr datasourcemstr;

	//bi-directional many-to-one association to Filemstr
    @ManyToOne
	@JoinColumn(name="FILEMSTR_COMPILED_ID")
	private Filemstr filemstr1;

	//bi-directional many-to-one association to Filemstr
    @ManyToOne
	@JoinColumn(name="FILEMSTR_SOURCE_ID")
	private Filemstr filemstr2;

	//bi-directional many-to-one association to RptReportfilehistory
    @ManyToOne
	@JoinColumn(name="REPORTFILEHISTORY_ID")
	private RptReportfilehistory rptReportfilehistory;

	//bi-directional many-to-one association to RptSnapshot
    @ManyToOne
	@JoinColumn(name="RPT_SNAPSHOT_ID")
	private RptSnapshot rptSnapshot;

	//bi-directional many-to-one association to RptReportparameter
	@OneToMany(mappedBy="rptReportmstr", fetch=FetchType.EAGER)
	private List<RptReportparameter> rptReportparameters;

	//bi-directional many-to-one association to RptRole
	@OneToMany(mappedBy="rptReportmstr", fetch=FetchType.EAGER)
	private List<RptRole> rptRoles;

	//bi-directional many-to-one association to RptSnapshot
	@OneToMany(mappedBy="rptReportmstr", fetch=FetchType.EAGER)
	private List<RptSnapshot> rptSnapshots;

	//bi-directional many-to-one association to RptSubscription
	@OneToMany(mappedBy="rptReportmstr", fetch=FetchType.EAGER)
	private List<RptSubscription> rptSubscriptions;

    public RptReportmstr() {
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

	public String getDatasourceType() {
		return this.datasourceType;
	}

	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReportCategory() {
		return this.reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}

	public String getReportCode() {
		return this.reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getReportMode() {
		return this.reportMode;
	}

	public void setReportMode(String reportMode) {
		this.reportMode = reportMode;
	}

	public String getReportName() {
		return this.reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public long getSnapshotTitlePattern() {
		return this.snapshotTitlePattern;
	}

	public void setSnapshotTitlePattern(long snapshotTitlePattern) {
		this.snapshotTitlePattern = snapshotTitlePattern;
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

	public List<RptExecutionplanmstr> getRptExecutionplanmstrs() {
		return this.rptExecutionplanmstrs;
	}

	public void setRptExecutionplanmstrs(List<RptExecutionplanmstr> rptExecutionplanmstrs) {
		this.rptExecutionplanmstrs = rptExecutionplanmstrs;
	}
	
	public List<RptReportfilehistory> getRptReportfilehistories() {
		return this.rptReportfilehistories;
	}

	public void setRptReportfilehistories(List<RptReportfilehistory> rptReportfilehistories) {
		this.rptReportfilehistories = rptReportfilehistories;
	}
	
	public Datasourcemstr getDatasourcemstr() {
		return this.datasourcemstr;
	}

	public void setDatasourcemstr(Datasourcemstr datasourcemstr) {
		this.datasourcemstr = datasourcemstr;
	}
	
	public Filemstr getFilemstr1() {
		return this.filemstr1;
	}

	public void setFilemstr1(Filemstr filemstr1) {
		this.filemstr1 = filemstr1;
	}
	
	public Filemstr getFilemstr2() {
		return this.filemstr2;
	}

	public void setFilemstr2(Filemstr filemstr2) {
		this.filemstr2 = filemstr2;
	}
	
	public RptReportfilehistory getRptReportfilehistory() {
		return this.rptReportfilehistory;
	}

	public void setRptReportfilehistory(RptReportfilehistory rptReportfilehistory) {
		this.rptReportfilehistory = rptReportfilehistory;
	}
	
	public RptSnapshot getRptSnapshot() {
		return this.rptSnapshot;
	}

	public void setRptSnapshot(RptSnapshot rptSnapshot) {
		this.rptSnapshot = rptSnapshot;
	}
	
	public List<RptReportparameter> getRptReportparameters() {
		return this.rptReportparameters;
	}

	public void setRptReportparameters(List<RptReportparameter> rptReportparameters) {
		this.rptReportparameters = rptReportparameters;
	}
	
	public List<RptRole> getRptRoles() {
		return this.rptRoles;
	}

	public void setRptRoles(List<RptRole> rptRoles) {
		this.rptRoles = rptRoles;
	}
	
	public List<RptSnapshot> getRptSnapshots() {
		return this.rptSnapshots;
	}

	public void setRptSnapshots(List<RptSnapshot> rptSnapshots) {
		this.rptSnapshots = rptSnapshots;
	}
	
	public List<RptSubscription> getRptSubscriptions() {
		return this.rptSubscriptions;
	}

	public void setRptSubscriptions(List<RptSubscription> rptSubscriptions) {
		this.rptSubscriptions = rptSubscriptions;
	}
	
}