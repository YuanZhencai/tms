package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the RPT_REPORTFILEHISTORY database table.
 * 
 */
@Entity
@Table(name="RPT_REPORTFILEHISTORY")
public class RptReportfilehistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME", nullable=false)
	private Timestamp createdDatetime;

	@Column(length=600)
	private String remarks;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME")
	private Timestamp updatedDatetime;

	@Column(name="VERSION_NO", precision=3)
	private BigDecimal versionNo;

	//bi-directional many-to-one association to Filemstr
    @ManyToOne
	@JoinColumn(name="FILEMSTR_COMPILED_ID")
	private Filemstr filemstr1;

	//bi-directional many-to-one association to Filemstr
    @ManyToOne
	@JoinColumn(name="FILEMSTR_SOURCE_ID")
	private Filemstr filemstr2;

	//bi-directional many-to-one association to RptReportmstr
    @ManyToOne
	@JoinColumn(name="REPORTMSTR_ID", nullable=false)
	private RptReportmstr rptReportmstr;

	//bi-directional many-to-one association to RptReportmstr
	@OneToMany(mappedBy="rptReportfilehistory", fetch=FetchType.EAGER)
	private List<RptReportmstr> rptReportmstrs;

    public RptReportfilehistory() {
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

	public BigDecimal getVersionNo() {
		return this.versionNo;
	}

	public void setVersionNo(BigDecimal versionNo) {
		this.versionNo = versionNo;
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
	
	public RptReportmstr getRptReportmstr() {
		return this.rptReportmstr;
	}

	public void setRptReportmstr(RptReportmstr rptReportmstr) {
		this.rptReportmstr = rptReportmstr;
	}
	
	public List<RptReportmstr> getRptReportmstrs() {
		return this.rptReportmstrs;
	}

	public void setRptReportmstrs(List<RptReportmstr> rptReportmstrs) {
		this.rptReportmstrs = rptReportmstrs;
	}
	
}