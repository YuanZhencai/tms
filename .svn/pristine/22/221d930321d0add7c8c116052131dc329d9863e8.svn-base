package com.wcs.tms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.wcs.base.model.BaseEntity;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description:授信调剂提供方明细_主数据(提供方)</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:huhan@wcs-global.com">huhan</a>
 */
@Entity
@Table(name="CREDIT_P")
public class CreditP extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREDIT_ID")
    private Credit credit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREDIT_RID")
	private CreditR creditR;
	
	
	/** 调剂金额(减少额度)*/
	@Column(name = "CREDIT_REDUCE", precision = 12, scale = 4)
    private Double creditReduce;
	
	/** 生效日期*/
	@Temporal( TemporalType.TIMESTAMP)
	@Column(name="CREDIT_START")
	private Date creditStart;
	
	/** 有效性状态*/
	@Column(name="STATUS", length=1)
	private String status;
	
	/** default constructor */
	public CreditP(){
		credit = new Credit();
		creditR = new CreditR();
	}
	
	


	public Credit getCredit() {
		return credit;
	}

	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	public CreditR getCreditR() {
		return creditR;
	}

	public void setCreditR(CreditR creditR) {
		this.creditR = creditR;
	}

	public Double getCreditReduce() {
		return creditReduce;
	}

	public void setCreditReduce(Double creditReduce) {
		this.creditReduce = creditReduce;
	}

	public Date getCreditStart() {
		return creditStart;
	}

	public void setCreditStart(Date creditStart) {
		this.creditStart = creditStart;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}




	@Override
	@Transient
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return null;
	}
}
