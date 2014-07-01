package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the RPT_SNAPSHOT database table.
 * 
 */
@Entity
@Table(name="RPT_SNAPSHOT")
public class RptSnapshot implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(name="DEFUNCT_IND", nullable=false, length=1)
	private String defunctInd;

	@Column(name="PRIVATE_IND", nullable=false, length=1)
	private String privateInd;

	@Column(name="PUBLISHED_BY", length=50)
	private String publishedBy;

	@Column(name="PUBLISHED_DATETIME")
	private Timestamp publishedDatetime;

	@Column(name="PUBLISHED_IND", nullable=false, length=1)
	private String publishedInd;

	@Column(length=300)
	private String remarks;

	@Column(name="SNAPSHOT_TITLE", nullable=false, length=300)
	private String snapshotTitle;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to RptExecutionplanhistory
	@OneToMany(mappedBy="rptSnapshot", fetch=FetchType.EAGER)
	private List<RptExecutionplanhistory> rptExecutionplanhistories;

	//bi-directional many-to-one association to RptReportmstr
	@OneToMany(mappedBy="rptSnapshot", fetch=FetchType.EAGER)
	private List<RptReportmstr> rptReportmstrs;

	//bi-directional many-to-one association to RptReportmstr
    @ManyToOne
	@JoinColumn(name="REPORTMSTR_ID", nullable=false)
	private RptReportmstr rptReportmstr;

	//bi-directional many-to-one association to RptSnapshotparameter
	@OneToMany(mappedBy="rptSnapshot", fetch=FetchType.EAGER)
	private List<RptSnapshotparameter> rptSnapshotparameters;

    public RptSnapshot() {
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

	public String getPrivateInd() {
		return this.privateInd;
	}

	public void setPrivateInd(String privateInd) {
		this.privateInd = privateInd;
	}

	public String getPublishedBy() {
		return this.publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}

	public Timestamp getPublishedDatetime() {
		return this.publishedDatetime;
	}

	public void setPublishedDatetime(Timestamp publishedDatetime) {
		this.publishedDatetime = publishedDatetime;
	}

	public String getPublishedInd() {
		return this.publishedInd;
	}

	public void setPublishedInd(String publishedInd) {
		this.publishedInd = publishedInd;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSnapshotTitle() {
		return this.snapshotTitle;
	}

	public void setSnapshotTitle(String snapshotTitle) {
		this.snapshotTitle = snapshotTitle;
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
	
	public List<RptReportmstr> getRptReportmstrs() {
		return this.rptReportmstrs;
	}

	public void setRptReportmstrs(List<RptReportmstr> rptReportmstrs) {
		this.rptReportmstrs = rptReportmstrs;
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