package com.wcs.report.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wcs.common.model.Rolemstr;


/**
 * The persistent class for the RPT_ROLE database table.
 * 
 */
@Entity
@Table(name="RPT_ROLE")
public class RptRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CREATED_BY", length=50)
	private String createdBy;

	@Column(name="CREATED_DATETIME")
	private Timestamp createdDatetime;

	@Column(name="DEFUNCT_IND", length=1)
	private String defunctInd;

	@Column(name="UPDATED_BY", length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME")
	private Timestamp updatedDatetime;

	//bi-directional many-to-one association to Rolemstr
    @ManyToOne
	@JoinColumn(name="ROLEMSTR_ID")
	private Rolemstr rolemstr;

	//bi-directional many-to-one association to RptReportmstr
    @ManyToOne
	@JoinColumn(name="REPORTMSTR_ID")
	private RptReportmstr rptReportmstr;

    public RptRole() {
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

	public Rolemstr getRolemstr() {
		return this.rolemstr;
	}

	public void setRolemstr(Rolemstr rolemstr) {
		this.rolemstr = rolemstr;
	}
	
	public RptReportmstr getRptReportmstr() {
		return this.rptReportmstr;
	}

	public void setRptReportmstr(RptReportmstr rptReportmstr) {
		this.rptReportmstr = rptReportmstr;
	}
	
}