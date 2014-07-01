package com.wcs.report.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the FILEMSTR database table.
 * 
 */
@Entity
@Table(name="FILEMSTR")
public class Filemstr implements Serializable {
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

	@Column(name="FILE_DESC", length=1000)
	private String fileDesc;

	@Column(name="FILE_NAME", nullable=false, length=200)
	private String fileName;

	@Column(name="FILE_STORE_LOCATION", nullable=false, length=400)
	private String fileStoreLocation;

	@Column(name="FILE_STORE_NAME", nullable=false, length=200)
	private String fileStoreName;

	@Column(name="FILE_TYPE", nullable=false, length=8)
	private String fileType;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

	@Column(name="UPDATED_DATETIME", nullable=false)
	private Timestamp updatedDatetime;

	@Column(name="UPLOADED_BY", nullable=false, length=50)
	private String uploadedBy;

	@Column(name="UPLOADED_DATETIME", nullable=false)
	private Timestamp uploadedDatetime;

	//bi-directional many-to-one association to RptReportfilehistory
	@OneToMany(mappedBy="filemstr1", fetch=FetchType.EAGER)
	private List<RptReportfilehistory> rptReportfilehistories1;

	//bi-directional many-to-one association to RptReportfilehistory
	@OneToMany(mappedBy="filemstr2", fetch=FetchType.EAGER)
	private List<RptReportfilehistory> rptReportfilehistories2;

	//bi-directional many-to-one association to RptReportmstr
	@OneToMany(mappedBy="filemstr1", fetch=FetchType.EAGER)
	private List<RptReportmstr> rptReportmstrs1;

	//bi-directional many-to-one association to RptReportmstr
	@OneToMany(mappedBy="filemstr2", fetch=FetchType.EAGER)
	private List<RptReportmstr> rptReportmstrs2;

    public Filemstr() {
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

	public String getFileDesc() {
		return this.fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileStoreLocation() {
		return this.fileStoreLocation;
	}

	public void setFileStoreLocation(String fileStoreLocation) {
		this.fileStoreLocation = fileStoreLocation;
	}

	public String getFileStoreName() {
		return this.fileStoreName;
	}

	public void setFileStoreName(String fileStoreName) {
		this.fileStoreName = fileStoreName;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
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

	public String getUploadedBy() {
		return this.uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Timestamp getUploadedDatetime() {
		return this.uploadedDatetime;
	}

	public void setUploadedDatetime(Timestamp uploadedDatetime) {
		this.uploadedDatetime = uploadedDatetime;
	}

	public List<RptReportfilehistory> getRptReportfilehistories1() {
		return this.rptReportfilehistories1;
	}

	public void setRptReportfilehistories1(List<RptReportfilehistory> rptReportfilehistories1) {
		this.rptReportfilehistories1 = rptReportfilehistories1;
	}
	
	public List<RptReportfilehistory> getRptReportfilehistories2() {
		return this.rptReportfilehistories2;
	}

	public void setRptReportfilehistories2(List<RptReportfilehistory> rptReportfilehistories2) {
		this.rptReportfilehistories2 = rptReportfilehistories2;
	}
	
	public List<RptReportmstr> getRptReportmstrs1() {
		return this.rptReportmstrs1;
	}

	public void setRptReportmstrs1(List<RptReportmstr> rptReportmstrs1) {
		this.rptReportmstrs1 = rptReportmstrs1;
	}
	
	public List<RptReportmstr> getRptReportmstrs2() {
		return this.rptReportmstrs2;
	}

	public void setRptReportmstrs2(List<RptReportmstr> rptReportmstrs2) {
		this.rptReportmstrs2 = rptReportmstrs2;
	}
	
}