package com.wcs.common.model;



import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the USERMSTR database table.
 * 
 */
@Entity
@Table(name="USERMSTR")
public class Usermstr implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USERMASTER")
	@SequenceGenerator(name = "SEQ_USERMASTER", sequenceName = "SEQ_USERMASTER", allocationSize = 20)
	private Long id;

	@Column(name="AD_ACCOUNT", length=50)
	private String adAccount;

	@Column(name="BACKGROUND_INFO", length=200)
	private String backgroundInfo;

	@Column(name="CREATED_BY", nullable=false, length=50)
	private String createdBy;
	
    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME", nullable=false)
	private Date createdDatetime;

	@Column(name="DEFUNCT_IND", nullable=false, length=1)
	private String defunctInd;

	@Column(length=20)
	private String pernr;

	@Column(name="UPDATED_BY", nullable=false, length=50)
	private String updatedBy;

    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATETIME", nullable=false)
	private Date updatedDatetime;

	//bi-directional many-to-one association to Userrole
	@OneToMany(mappedBy="usermstr", fetch=FetchType.EAGER)
	private List<Userrole> userroles;

	
    public Usermstr() {
    }

	public String getAdAccount() {
		return this.adAccount;
	}

	public void setAdAccount(String adAccount) {
		this.adAccount = adAccount;
	}

	public String getBackgroundInfo() {
		return this.backgroundInfo;
	}

	public void setBackgroundInfo(String backgroundInfo) {
		this.backgroundInfo = backgroundInfo;
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

	public String getPernr() {
		return this.pernr;
	}

	public void setPernr(String pernr) {
		this.pernr = pernr;
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

	public List<Userrole> getUserroles() {
		return this.userroles;
	}

	public void setUserroles(List<Userrole> userroles) {
		this.userroles = userroles;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}