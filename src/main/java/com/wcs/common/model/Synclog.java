package com.wcs.common.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The persistent class for the SYNCLOG database table.
 * 
 */
@Entity
public class Synclog extends com.wcs.base.model.IdEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private String remarks;

	@Column(name = "SYNC_DATETIME")
	private Timestamp syncDatetime;

	@Column(name = "SYNC_TYPE")
	private String syncType;

	private Timestamp version;

	public Synclog() {
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Timestamp getSyncDatetime() {
		return this.syncDatetime;
	}

	public void setSyncDatetime(Timestamp syncDatetime) {
		this.syncDatetime = syncDatetime;
	}

	public String getSyncType() {
		return this.syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	public Timestamp getVersion() {
		return this.version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

}