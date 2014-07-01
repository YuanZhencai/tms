package com.wcs.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.wcs.common.model.Companymstr;

/**
 * The persistent class for the TAXAUTHORITY database table.
 * 
 */
@Entity
public class Taxauthority extends com.wcs.base.model.IdEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private String address;

	@Column(name = "CONTACTER_NAME")
	private String contacterName;

	@Column(name = "CONTACTER_POSITION")
	private String contacterPosition;

	@Column(name = "CONTACTER_TELPHONE")
	private String contacterTelphone;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name = "DEFUNCT_IND")
	private String defunctInd;

	@Column(name = "LEADER_NAME")
	private String leaderName;

	@Column(name = "LEADER_POSITION")
	private String leaderPosition;

	@Column(name = "LEADER_TELPHONE")
	private String leaderTelphone;

	private String name;

	private String telphone;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_DATETIME")
	private Date updatedDatetime;

	private String zipcode;

	// bi-directional many-to-one association to Companymstr
	@OneToMany(mappedBy = "taxauthority", fetch = FetchType.EAGER)
	private List<Companymstr> companymstrs;

	public Taxauthority() {
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContacterName() {
		return this.contacterName;
	}

	public void setContacterName(String contacterName) {
		this.contacterName = contacterName;
	}

	public String getContacterPosition() {
		return this.contacterPosition;
	}

	public void setContacterPosition(String contacterPosition) {
		this.contacterPosition = contacterPosition;
	}

	public String getContacterTelphone() {
		return this.contacterTelphone;
	}

	public void setContacterTelphone(String contacterTelphone) {
		this.contacterTelphone = contacterTelphone;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public String getLeaderName() {
		return this.leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public String getLeaderPosition() {
		return this.leaderPosition;
	}

	public void setLeaderPosition(String leaderPosition) {
		this.leaderPosition = leaderPosition;
	}

	public String getLeaderTelphone() {
		return this.leaderTelphone;
	}

	public void setLeaderTelphone(String leaderTelphone) {
		this.leaderTelphone = leaderTelphone;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelphone() {
		return this.telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDatetime() {
		return this.updatedDatetime;
	}

	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public String getZipcode() {
		return this.zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public List<Companymstr> getCompanymstrs() {
		return this.companymstrs;
	}

	public void setCompanymstrs(List<Companymstr> companymstrs) {
		this.companymstrs = companymstrs;
	}

}