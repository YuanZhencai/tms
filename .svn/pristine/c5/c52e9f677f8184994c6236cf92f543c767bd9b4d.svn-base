package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the RPT_SUBSCRIPTION database table.
 * 
 */
@Entity
@Table(name="RPT_SUBSCRIPTION")
public class RptSubscription implements Serializable {
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

	@Column(length=300)
	private String remarks;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	@Column(name="USERMSTR_ID")
	private long usermstrId;

	//bi-directional many-to-one association to RptReportmstr
    @ManyToOne
	@JoinColumn(name="REPORTMSTR_ID", nullable=false)
	private RptReportmstr rptReportmstr;

    public RptSubscription() {
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

	public long getUsermstrId() {
		return this.usermstrId;
	}

	public void setUsermstrId(long usermstrId) {
		this.usermstrId = usermstrId;
	}

	public RptReportmstr getRptReportmstr() {
		return this.rptReportmstr;
	}

	public void setRptReportmstr(RptReportmstr rptReportmstr) {
		this.rptReportmstr = rptReportmstr;
	}
	
}