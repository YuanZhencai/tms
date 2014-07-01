package com.wcs.common.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the ROLERESOURCE database table.
 * 
 */
@Entity
public class Roleresource extends com.wcs.base.model.IdEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="CREATED_BY")
	private String createdBy;

    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATETIME")
	private Date createdDatetime;

	@Column(name="DEFUNCT_IND")
	private String defunctInd;

	@Column(name="UPDATED_BY")
	private String updatedBy;

    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATETIME")
	private Date updatedDatetime;

	//bi-directional many-to-one association to Resourcemstr
    @ManyToOne
	private Resourcemstr resourcemstr;

	//bi-directional many-to-one association to Rolemstr
    @ManyToOne
	private Rolemstr rolemstr;

    public Roleresource() {
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

	public Resourcemstr getResourcemstr() {
		return this.resourcemstr;
	}

	public void setResourcemstr(Resourcemstr resourcemstr) {
		this.resourcemstr = resourcemstr;
	}
	
	public Rolemstr getRolemstr() {
		return this.rolemstr;
	}

	public void setRolemstr(Rolemstr rolemstr) {
		this.rolemstr = rolemstr;
	}
	
}