package com.wcs.tms.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:授信调剂明细_主数据(申请方)</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name="CREDIT_R")
public class CreditR extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREDIT_ID")
    private Credit credit;
	
	/** 用途*/
	@Column(name = "USE", length = 600)
    private String use;
	
	/** 核准增加授信额度*/
	@Column(name = "CREDIT_ADD", precision = 12, scale = 4)
    private Double creditAdd;
	
	/** 生效日期*/
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="CREDIT_START")
	private Date creditStart;
	
	/**授信调剂提供方明细_主数据(提供方)*/
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "creditR")
	private List<CreditP> creditPs = new ArrayList<CreditP>(0);
	
	/** default constructor */
	public CreditR(){
		credit = new Credit();
	}

	public Credit getCredit() {
		return credit;
	}

	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public Double getCreditAdd() {
		return creditAdd;
	}

	public void setCreditAdd(Double creditAdd) {
		this.creditAdd = creditAdd;
	}

	public Date getCreditStart() {
		return creditStart;
	}

	public void setCreditStart(Date creditStart) {
		this.creditStart = creditStart;
	}
		
	public List<CreditP> getCreditPs() {
		return creditPs;
	}

	public void setCreditPs(List<CreditP> creditPs) {
		this.creditPs = creditPs;
	}

	@Override
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}
}
